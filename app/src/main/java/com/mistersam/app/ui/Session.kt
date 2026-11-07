package com.mistersam.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mistersam.app.ai.Teacher
import com.mistersam.app.data.Content
import com.mistersam.app.data.Lesson
import com.mistersam.app.data.Phrase
import com.mistersam.app.data.Progress
import com.mistersam.app.data.Quiz
import com.mistersam.app.data.Seg
import com.mistersam.app.util.Speech
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun fmt(ms: Long): String {
    val s = ms / 1000
    return "%02d:%02d".format(s / 60, s % 60)
}

@Composable
fun SessionScreen(vm: AppViewModel, p: Progress, onExit: () -> Unit) {
    val ctx = LocalContext.current
    // نثبّت درس اليوم عند بدء الجلسة حتى لا يتغيّر بعد تحديث رقم اليوم
    val day = rememberSaveable { p.dayIndex }
    val lesson = remember(day) { Content.lesson(p.level, day) }
    val segs = Seg.entries

    var idx by rememberSaveable { mutableIntStateOf(0) }
    var doneIdx by rememberSaveable { mutableIntStateOf(-1) }
    var xp by rememberSaveable { mutableIntStateOf(0) }
    var finished by rememberSaveable { mutableStateOf(false) }
    var finalMinutes by rememberSaveable { mutableIntStateOf(0) }
    val startMs = rememberSaveable { System.currentTimeMillis() }
    var elapsed by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            elapsed = System.currentTimeMillis() - startMs
            delay(1000)
        }
    }

    val speech = remember { Speech(ctx.applicationContext).also { it.init() } }
    DisposableEffect(Unit) { onDispose { speech.release() } }

    val segDone = doneIdx >= idx
    val onDone: (Int) -> Unit = { gained ->
        if (doneIdx < idx) {
            doneIdx = idx
            xp += gained
        }
    }

    if (finished) {
        FinishView(p = p, earnedXp = xp + 20, minutes = finalMinutes, words = lesson.words.size, onExit = onExit)
        return
    }

    val seg = segs[idx]
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onExit) { Text("خروج") }
            Spacer(Modifier.weight(1f))
            Text("⏱ ${fmt(elapsed)} / 60:00", style = MaterialTheme.typography.labelLarge)
        }
        Text(
            "${seg.emoji} ${seg.title}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            "الفقرة ${idx + 1} من ${segs.size} · ${seg.hint}",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = { (idx + if (segDone) 1 else 0) / segs.size.toFloat() },
            modifier = Modifier.fillMaxWidth()
        )

        key(idx) {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (seg) {
                    Seg.WARMUP -> WarmupSeg(p, speech, onDone)
                    Seg.VOCAB -> VocabSeg(lesson, speech, onDone)
                    Seg.GRAMMAR -> GrammarSeg(lesson, speech, onDone)
                    Seg.LISTEN -> ListenSeg(lesson, speech, onDone)
                    Seg.SPEAK -> SpeakSeg(lesson, speech, onDone)
                    Seg.CHAT -> ChatSeg(lesson, p, speech, onDone)
                }
            }
        }

        Button(
            onClick = {
                if (idx < segs.lastIndex) {
                    idx++
                } else {
                    val mins = ((System.currentTimeMillis() - startMs) / 60000).toInt().coerceAtLeast(1)
                    finalMinutes = mins
                    vm.finishDay(mins, xp + 20, lesson.words.map { it.en })
                    finished = true
                }
            },
            enabled = segDone,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (idx < segs.lastIndex) "التالي" else "إنهاء الدرس 🎉") }
    }
}

@Composable
private fun FinishView(p: Progress, earnedXp: Int, minutes: Int, words: Int, onExit: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎉", style = MaterialTheme.typography.displayLarge)
        Text("أنهيت درس اليوم!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        SamCard {
            Text("⭐ نقاط هذا الدرس: $earnedXp")
            Text("⏱ الوقت الفعلي: $minutes دقيقة")
            Text("📚 كلمات جديدة: $words")
            Text("🔥 سلسلة الأيام المتتالية: ${p.streak}")
        }
        Text(
            "الاستمرار اليومي هو سرّ الطلاقة. أراك غدًا!",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onExit, modifier = Modifier.fillMaxWidth()) { Text("العودة للرئيسية") }
    }
}

/* ───────────── 1) إحماء ومراجعة ───────────── */

@Composable
private fun WarmupSeg(p: Progress, speech: Speech, onDone: (Int) -> Unit) {
    val pool = remember { p.learned.mapNotNull { Content.allWords[it] } }
    if (pool.isEmpty()) {
        SamCard {
            Text("أهلاً بك في أول درس! نبدأ بإحماء النطق: اضغط على السماعة ثم كرّر بصوت عالٍ.")
            listOf("Hello!", "How are you?", "I am fine, thank you.").forEach { phrase ->
                Ltr {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(phrase, Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                        SpeakButton(speech, phrase)
                    }
                }
            }
            Button(onClick = { onDone(5) }) { Text("استمعت وكرّرت ✅") }
        }
    } else {
        val qs = remember {
            val allAr = Content.allWords.values.map { it.ar }.distinct()
            pool.shuffled().take(5).map { wd ->
                val others = allAr.filter { it != wd.ar }.shuffled().take(2)
                val opts = (others + wd.ar).shuffled()
                Quiz("ما معنى كلمة \"${wd.en}\"؟", opts, opts.indexOf(wd.ar))
            }
        }
        Text("نراجع كلمات الأيام السابقة قبل أن نتقدّم:")
        QuizBlock(qs) { correct -> onDone(correct * 2) }
    }
}

/* ───────────── 2) مفردات ───────────── */

@Composable
private fun VocabSeg(lesson: Lesson, speech: Speech, onDone: (Int) -> Unit) {
    var quizOn by rememberSaveable { mutableStateOf(false) }
    val qs = remember(lesson) {
        lesson.words.map { wd ->
            val others = lesson.words.filter { it.en != wd.en }.shuffled().take(2).map { it.ar }
            val opts = (others + wd.ar).shuffled()
            Quiz("ما معنى كلمة \"${wd.en}\"؟", opts, opts.indexOf(wd.ar))
        }
    }
    Text("الموضوع: ${lesson.theme}. اضغط السماعة لتسمع الكلمة، ثم المثال.")
    lesson.words.forEach { wd ->
        SamCard {
            Ltr {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(wd.en, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(wd.ex, style = MaterialTheme.typography.bodyMedium)
                    }
                    SpeakButton(speech, wd.en)
                    SpeakButton(speech, wd.ex, slow = true)
                }
            }
            Text(wd.ar, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
    if (!quizOn) {
        Button(onClick = { quizOn = true }, modifier = Modifier.fillMaxWidth()) { Text("ابدأ اختبار الكلمات") }
    } else {
        QuizBlock(qs) { correct -> onDone(correct * 3) }
    }
}

/* ───────────── 3) قاعدة ───────────── */

@Composable
private fun GrammarSeg(lesson: Lesson, speech: Speech, onDone: (Int) -> Unit) {
    val g = lesson.grammar
    SamCard {
        Ltr { Text(g.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        Text(g.explain, style = MaterialTheme.typography.bodyLarge)
        g.examples.forEach { ex ->
            Ltr {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("• $ex", Modifier.weight(1f))
                    SpeakButton(speech, ex)
                }
            }
        }
    }
    Text("الآن طبّق القاعدة:")
    QuizBlock(g.quiz) { correct -> onDone(correct * 4) }
}

/* ───────────── 4) استماع ───────────── */

@Composable
private fun ListenSeg(lesson: Lesson, speech: Speech, onDone: (Int) -> Unit) {
    var plays by rememberSaveable { mutableIntStateOf(0) }
    var finished by rememberSaveable { mutableStateOf(false) }
    val l = lesson.listening
    SamCard {
        Text("استمع للنص (يمكنك إعادته كما تشاء) ثم أجب عن السؤال.")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { speech.speak(l.text); plays++ }) { Text("▶ استمع") }
            OutlinedButton(onClick = { speech.speak(l.text, slow = true); plays++ }) { Text("🐢 ببطء") }
        }
    }
    if (plays > 0) {
        QuizBlock(listOf(l.quiz)) { correct ->
            finished = true
            onDone(5 + correct * 3)
        }
    } else {
        Text("اضغط «استمع» أولًا.")
    }
    if (finished) {
        SamCard {
            Text("النص المكتوب:", fontWeight = FontWeight.SemiBold)
            Ltr { Text(l.text, style = MaterialTheme.typography.bodyLarge) }
        }
    }
}

/* ───────────── 5) نطق ───────────── */

@Composable
private fun SpeakSeg(lesson: Lesson, speech: Speech, onDone: (Int) -> Unit) {
    val scores = remember { mutableStateMapOf<Int, Int>() }
    val heard = remember { mutableStateMapOf<Int, String>() }
    Text("اسمع الجملة ثم اضغط «تحدّث» وكرّرها. سأخبرك كم كلمة نطقتها بشكل مفهوم.")
    lesson.speaking.forEachIndexed { i, ph ->
        PhraseRow(
            ph = ph,
            speech = speech,
            score = scores[i],
            heard = heard[i],
            onResult = { h, s ->
                heard[i] = h
                if ((scores[i] ?: -1) < s) scores[i] = s
            }
        )
    }
    LaunchedEffect(scores.size) {
        if (scores.size == lesson.speaking.size) {
            onDone(scores.values.sumOf { if (it >= 70) 4 else 2 })
        }
    }
}

@Composable
private fun PhraseRow(
    ph: Phrase,
    speech: Speech,
    score: Int?,
    heard: String?,
    onResult: (String, Int) -> Unit
) {
    var listening by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    var live by remember { mutableStateOf("") }
    val start = rememberMic(
        speech,
        onPartial = { live = it },
        onFinal = {
            listening = false
            live = ""
            onResult(it, Speech.score(ph.en, it))
        },
        onFail = {
            listening = false
            failed = true
            live = ""
        }
    )
    SamCard {
        Ltr {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(ph.en, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                SpeakButton(speech, ph.en)
                SpeakButton(speech, ph.en, slow = true)
            }
        }
        Text(ph.ar, style = MaterialTheme.typography.bodyMedium)
        if (speech.recognitionAvailable) {
            Button(
                onClick = {
                    failed = false
                    listening = true
                    start()
                },
                enabled = !listening
            ) {
                Icon(Icons.Filled.Mic, contentDescription = null)
                Text(if (listening) "  أستمع إليك…" else "  تحدّث")
            }
        } else {
            OutlinedButton(onClick = { onResult("", 70) }) { Text("قرأتها بصوتٍ عالٍ ✓") }
        }
        if (listening && live.isNotBlank()) Ltr { Text(live, style = MaterialTheme.typography.bodyMedium) }
        if (failed && !listening) Text("لم أسمعك جيدًا، حاول مرة أخرى.", color = MaterialTheme.colorScheme.error)
        if (heard != null && score != null && heard.isNotBlank()) {
            Ltr { Text("سمعتك: $heard", style = MaterialTheme.typography.bodyMedium) }
            Text(
                when {
                    score >= 80 -> "نطق ممتاز 🌟 ($score٪)"
                    score >= 50 -> "جيد، أعد المحاولة لتحسينه ($score٪)"
                    else -> "استمع مجددًا ثم كرّر ببطء ($score٪)"
                },
                color = if (score >= 50) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        } else if (score != null) {
            Text("تم ✅", color = MaterialTheme.colorScheme.primary)
        }
    }
}

/* ───────────── 6) حوار مع المعلم ───────────── */

@Composable
private fun ChatSeg(lesson: Lesson, p: Progress, speech: Speech, onDone: (Int) -> Unit) {
    val need = 4
    val msgs = remember { mutableStateListOf(Teacher.Msg(false, lesson.script[0])) }
    var input by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var turns by remember { mutableIntStateOf(0) }
    var autoSpeak by remember { mutableStateOf(true) }
    var note by remember { mutableStateOf<String?>(null) }
    var listening by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { if (autoSpeak) speech.speak(lesson.script[0]) }

    val mic = rememberMic(
        speech,
        onPartial = { input = it },
        onFinal = {
            input = it
            listening = false
        },
        onFail = { listening = false }
    )

    val send: () -> Unit = send@{
        val text = input.trim()
        if (text.isEmpty() || busy) return@send
        msgs.add(Teacher.Msg(true, text))
        input = ""
        turns++
        busy = true
        val turnNow = turns
        scope.launch {
            val ai = if (p.apiKey.isNotBlank()) {
                Teacher.reply(p.apiKey, p.level, lesson.topic, msgs.toList()).getOrElse {
                    note = "تعذّر الاتصال بالمعلم الذكي، سأكمل معك بالأسئلة الجاهزة."
                    null
                }
            } else null
            val reply = ai ?: Teacher.scripted(lesson.script, turnNow)
            msgs.add(Teacher.Msg(false, reply))
            busy = false
            if (autoSpeak) speech.speak(reply)
            if (turnNow >= need) onDone(10)
        }
    }

    Text(
        if (p.apiKey.isBlank()) "وضع الأسئلة الجاهزة. أضف مفتاح Claude من الإعدادات لحوار حرّ مع معلم ذكي."
        else "معلمك الذكي متصل 🤖 — تحدّث بحرية عن: ${lesson.topic}",
        style = MaterialTheme.typography.bodyMedium
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(checked = autoSpeak, onCheckedChange = { autoSpeak = it })
        Text("  قراءة ردود المعلم بصوت عالٍ", Modifier.weight(1f))
        Text("$turns / $need")
    }
    note?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

    Ltr {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            msgs.forEach { m ->
                Box(Modifier.fillMaxWidth(), contentAlignment = if (m.fromUser) Alignment.CenterEnd else Alignment.CenterStart) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (m.fromUser) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            if (!m.fromUser) Text("Mr. Sam", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Text(m.text, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
            if (busy) Text("Mr. Sam is typing…", style = MaterialTheme.typography.bodySmall)
        }
    }

    Ltr {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type or speak in English…") },
                maxLines = 3
            )
            if (speech.recognitionAvailable) {
                IconButton(onClick = {
                    listening = true
                    mic()
                }, enabled = !listening) {
                    Icon(Icons.Filled.Mic, contentDescription = "تحدّث", tint = MaterialTheme.colorScheme.primary)
                }
            }
            IconButton(onClick = send, enabled = !busy && input.isNotBlank()) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "إرسال", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
