package com.mistersam.app.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mistersam.app.data.Quiz
import com.mistersam.app.util.Speech

/** يعرض المحتوى الإنجليزي من اليسار لليمين داخل واجهة عربية. */
@Composable
fun Ltr(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr, content = content)
}

@Composable
fun SamCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
    }
}

@Composable
fun SpeakButton(speech: Speech, text: String, slow: Boolean = false) {
    IconButton(onClick = { speech.speak(text, slow) }) {
        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "استمع", tint = MaterialTheme.colorScheme.primary)
    }
}

/**
 * يعيد دالة تبدأ الاستماع للمايك (وتطلب إذن التسجيل أول مرة).
 */
@Composable
fun rememberMic(
    speech: Speech,
    onPartial: (String) -> Unit,
    onFinal: (String) -> Unit,
    onFail: () -> Unit
): () -> Unit {
    val ctx = LocalContext.current
    val partial by rememberUpdatedState(onPartial)
    val final by rememberUpdatedState(onFinal)
    val fail by rememberUpdatedState(onFail)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) speech.listen({ partial(it) }, { final(it) }, { fail() }) else fail()
    }
    return {
        val granted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        if (granted) speech.listen({ partial(it) }, { final(it) }, { fail() })
        else launcher.launch(Manifest.permission.RECORD_AUDIO)
    }
}

/** اختبار اختيار من متعدد، سؤال بسؤال. onFinished(عدد الإجابات الصحيحة) */
@Composable
fun QuizBlock(qs: List<Quiz>, onFinished: (Int) -> Unit) {
    var i by rememberSaveable { mutableIntStateOf(0) }
    var picked by rememberSaveable { mutableIntStateOf(-1) }
    var correct by rememberSaveable { mutableIntStateOf(0) }
    val q = qs[i]
    val last = i == qs.lastIndex

    SamCard {
        Text("سؤال ${i + 1} من ${qs.size}", style = MaterialTheme.typography.labelMedium)
        Text(q.q, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        q.options.forEachIndexed { k, option ->
            val container = when {
                picked < 0 -> MaterialTheme.colorScheme.surfaceVariant
                k == q.answer -> MaterialTheme.colorScheme.primaryContainer
                k == picked -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = picked < 0) {
                        picked = k
                        if (k == q.answer) correct++
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = container),
                border = if (picked == k) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
            ) {
                Text(option, Modifier.padding(14.dp), style = MaterialTheme.typography.bodyLarge)
            }
        }
        if (picked >= 0) {
            Text(
                if (picked == q.answer) "أحسنت! إجابة صحيحة ✅"
                else "الإجابة الصحيحة: ${q.options[q.answer]}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (picked == q.answer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Button(onClick = {
                if (last) {
                    onFinished(correct)
                } else {
                    i++
                    picked = -1
                }
            }) { Text(if (last) "إنهاء التمرين" else "السؤال التالي") }
        }
    }
}
