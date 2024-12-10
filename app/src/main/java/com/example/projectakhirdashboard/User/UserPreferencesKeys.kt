package com.example.projectakhirdashboard.User

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object UserPreferencesKeys {
    val IS_ALARM_ENABLED = booleanPreferencesKey("is_alarm_enabled")
    val IS_SOUND_ENABLED = booleanPreferencesKey("is_sound_enabled")
    val IS_VIBRATION_ENABLED = booleanPreferencesKey("is_vibration_enabled")
    val IS_NOTIFICATION_ENABLED = booleanPreferencesKey("is_notification_enabled")
    val IS_EVERYDAY_ENABLED = booleanPreferencesKey("is_everyday_enabled")
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

suspend fun savePreferences(context: Context, key: Preferences.Key<Boolean>, value: Boolean) {
    context.dataStore.edit { preferences ->
        preferences[key] = value
    }
}

suspend fun getPreferences(context: Context, key: Preferences.Key<Boolean>): Boolean {
    val preferences = context.dataStore.data.first()
    return preferences[key] ?: false
}
