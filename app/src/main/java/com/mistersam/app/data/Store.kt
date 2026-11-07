package com.mistersam.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.ds by preferencesDataStore(name = "mister_sam")

class Store(private val ctx: Context) {

    object K {
        val onboarded = booleanPreferencesKey("onboarded")
        val name = stringPreferencesKey("name")
        val level = stringPreferencesKey("level")
        val day = intPreferencesKey("day")
        val streak = intPreferencesKey("streak")
        val lastDone = stringPreferencesKey("last_done")
        val xp = intPreferencesKey("xp")
        val minutes = intPreferencesKey("minutes")
        val apiKey = stringPreferencesKey("api_key")
        val reminderOn = booleanPreferencesKey("reminder_on")
        val reminderHour = intPreferencesKey("reminder_hour")
        val learned = stringSetPreferencesKey("learned")
    }

    val flow: Flow<Progress> = ctx.ds.data.map { p ->
        Progress(
            loaded = true,
            onboarded = p[K.onboarded] ?: false,
            name = p[K.name] ?: "",
            level = runCatching { Level.valueOf(p[K.level] ?: "A1") }.getOrDefault(Level.A1),
            dayIndex = p[K.day] ?: 0,
            streak = p[K.streak] ?: 0,
            lastDone = p[K.lastDone] ?: "",
            xp = p[K.xp] ?: 0,
            totalMinutes = p[K.minutes] ?: 0,
            apiKey = p[K.apiKey] ?: "",
            reminderOn = p[K.reminderOn] ?: false,
            reminderHour = p[K.reminderHour] ?: 20,
            learned = p[K.learned] ?: emptySet()
        )
    }

    suspend fun edit(block: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        ctx.ds.edit { block(it) }
    }
}
