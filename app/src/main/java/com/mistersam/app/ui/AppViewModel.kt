package com.mistersam.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mistersam.app.data.Level
import com.mistersam.app.data.Progress
import com.mistersam.app.data.Store
import com.mistersam.app.util.Reminder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val store = Store(app)

    val progress: StateFlow<Progress> =
        store.flow.stateIn(viewModelScope, SharingStarted.Eagerly, Progress())

    fun onboard(name: String, level: Level) = viewModelScope.launch {
        store.edit {
            it[Store.K.name] = name.trim()
            it[Store.K.level] = level.name
            it[Store.K.onboarded] = true
        }
    }

    fun setProfile(name: String, level: Level) = viewModelScope.launch {
        val changed = level != progress.value.level
        store.edit {
            it[Store.K.name] = name.trim()
            it[Store.K.level] = level.name
            if (changed) it[Store.K.day] = 0
        }
    }

    fun setApiKey(key: String) = viewModelScope.launch {
        store.edit { it[Store.K.apiKey] = key.trim() }
    }

    fun setReminder(on: Boolean, hour: Int) = viewModelScope.launch {
        store.edit {
            it[Store.K.reminderOn] = on
            it[Store.K.reminderHour] = hour
        }
        val ctx = getApplication<Application>()
        if (on) Reminder.schedule(ctx, hour) else Reminder.cancel(ctx)
    }

    /** يُستدعى عند إنهاء الدرس: يحدّث السلسلة اليومية والنقاط والدقائق والكلمات المتعلَّمة. */
    fun finishDay(minutes: Int, xp: Int, words: List<String>) = viewModelScope.launch {
        val p = progress.value
        val today = LocalDate.now()
        val firstTimeToday = !p.doneToday
        val newStreak = when {
            !firstTimeToday -> p.streak
            p.lastDone == today.minusDays(1).toString() -> p.streak + 1
            else -> 1
        }
        store.edit {
            it[Store.K.xp] = p.xp + xp
            it[Store.K.minutes] = p.totalMinutes + minutes
            it[Store.K.learned] = p.learned + words
            if (firstTimeToday) {
                it[Store.K.streak] = newStreak
                it[Store.K.lastDone] = today.toString()
                it[Store.K.day] = p.dayIndex + 1
            }
        }
    }
}
