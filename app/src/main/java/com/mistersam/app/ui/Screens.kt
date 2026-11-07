package com.mistersam.app.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mistersam.app.data.Content
import com.mistersam.app.data.Level
import com.mistersam.app.data.Progress
import com.mistersam.app.data.Seg

@Composable
fun SamApp(vm: AppViewModel) {
    val p by vm.progress.collectAsStateWithLifecycle()
    var screen by rememberSaveable { mutableStateOf("home") }

    BackHandler(enabled = screen != "home") { screen = "home" }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when {
                !p.loaded -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                !p.onboarded -> Onboarding(vm)
                screen == "session" -> SessionScreen(vm, p) { screen = "home" }
                screen == "settings" -> SettingsScreen(vm, p) { screen = "home" }
                else -> HomeScreen(p, onStart = { screen = "session" }, onSettings = { screen = "settings" })
            }
        }
    }
}

/* ───────────── الاستقبال ───────────── */

@Composable
fun Onboarding(vm: AppViewModel) {
    var name by rememberSaveable { mutableStateOf("") }
    var level by rememberSaveable { mutableStateOf(Level.A1) }
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("👋", style = MaterialTheme.typography.displayMedium)
        Text("أنا مستر سام", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(
            "معلمك الافتراضي للإنجليزية. كل يوم ساعة واحدة معي: مراجعة، مفردات، قاعدة، استماع، نطق، ثم حوار حقيقي.",
            style = MaterialTheme.typography.bodyLarge
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("ما اسمك؟") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text("اختر مستواك الحالي:", fontWeight = FontWeight.SemiBold)
        LevelPicker(level) { level = it }
        Button(
            onClick = { vm.onboard(name, level) },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("ابدأ رحلتي") }
    }
}

@Composable
private fun LevelPicker(selected: Level, onPick: (Level) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Level.entries.forEach { lv ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPick(lv) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (lv == selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = lv == selected, onClick = { onPick(lv) })
                    Column(Modifier.padding(start = 8.dp)) {
                        Text("${lv.code} · ${lv.ar}", fontWeight = FontWeight.Bold)
                        Text(lv.desc, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

/* ───────────── الرئيسية ───────────── */

@Composable
fun HomeScreen(p: Progress, onStart: () -> Unit, onSettings: () -> Unit) {
    val lesson = remember(p.level, p.dayIndex) { Content.lesson(p.level, p.dayIndex) }
    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("أهلاً ${p.name} 👋", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("المستوى ${p.level.code} · ${p.level.ar}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onSettings) { Icon(Icons.Filled.Settings, contentDescription = "الإعدادات") }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Stat("🔥", "${p.streak}", "يوم متتالٍ", Modifier.weight(1f))
            Stat("⭐", "${p.xp}", "نقطة", Modifier.weight(1f))
            Stat("⏱", "${p.totalMinutes}", "دقيقة", Modifier.weight(1f))
        }

        SamCard {
            Text(
                if (p.doneToday) "أنجزت درس اليوم ✅ — القادم:" else "درس اليوم · اليوم ${p.dayIndex + 1}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(lesson.theme, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("ساعة واحدة = ٦ فقرات × ١٠ دقائق", style = MaterialTheme.typography.bodyMedium)
            Seg.entries.forEachIndexed { i, seg ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${seg.emoji}  ${i + 1}. ${seg.title}", Modifier.weight(1f))
                    Text("١٠ د", style = MaterialTheme.typography.labelMedium)
                }
            }
            Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
                Text(if (p.doneToday) "تدريب إضافي" else "ابدأ الدرس الآن")
            }
        }

        Text(
            "نصيحة: الدرس القصير كل يوم أقوى من درس طويل مرة في الأسبوع.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Stat(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, style = MaterialTheme.typography.titleLarge)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

/* ───────────── الإعدادات ───────────── */

@Composable
fun SettingsScreen(vm: AppViewModel, p: Progress, onBack: () -> Unit) {
    var name by remember(p.name) { mutableStateOf(p.name) }
    var level by remember(p.level) { mutableStateOf(p.level) }
    var key by remember(p.apiKey) { mutableStateOf(p.apiKey) }
    var hour by remember(p.reminderHour) { mutableIntStateOf(p.reminderHour) }
    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("رجوع") }
            Spacer(Modifier.weight(1f))
            Text("الإعدادات", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        SamCard {
            Text("الملف الشخصي", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("الاسم") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            LevelPicker(level) { level = it }
            if (level != p.level) {
                Text("تغيير المستوى يعيدك إلى اليوم الأول من المستوى الجديد.", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = { vm.setProfile(name, level) }, enabled = name.isNotBlank()) { Text("حفظ") }
        }

        SamCard {
            Text("التذكير اليومي", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("ذكّرني بالدرس كل يوم", Modifier.weight(1f))
                Switch(checked = p.reminderOn, onCheckedChange = { on ->
                    if (on && Build.VERSION.SDK_INT >= 33) notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    vm.setReminder(on, hour)
                })
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    hour = (hour + 23) % 24
                    vm.setReminder(p.reminderOn, hour)
                }) { Text("−") }
                Text("الساعة %02d:00".format(hour))
                OutlinedButton(onClick = {
                    hour = (hour + 1) % 24
                    vm.setReminder(p.reminderOn, hour)
                }) { Text("+") }
            }
        }

        SamCard {
            Text("المعلم الذكي (اختياري)", fontWeight = FontWeight.Bold)
            Text(
                "بدون مفتاح: حوار بأسئلة جاهزة تعمل بدون إنترنت. مع مفتاح Claude API: محادثة حرّة مع تصحيح أخطائك. المفتاح يُحفظ على جهازك فقط.",
                style = MaterialTheme.typography.bodySmall
            )
            OutlinedTextField(
                value = key, onValueChange = { key = it },
                label = { Text("Claude API key") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { vm.setApiKey(key) }) { Text("حفظ المفتاح") }
        }
    }
}
