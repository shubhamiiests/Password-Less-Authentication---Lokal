package com.example.passlessauthethication.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.passlessauthethication.data.model.SessionData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "passless_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val SESSION_EMAIL = stringPreferencesKey("session_email")
        private val SESSION_START_TIME = longPreferencesKey("session_start_time")
        private val SESSION_ACTIVE = booleanPreferencesKey("session_active")
    }

    val sessionData: Flow<SessionData?> = dataStore.data.map { preferences ->
        val email = preferences[SESSION_EMAIL]
        val startTime = preferences[SESSION_START_TIME]
        val isActive = preferences[SESSION_ACTIVE] ?: false

        if (email != null && startTime != null && isActive) {
            SessionData(email, startTime, isActive)
        } else {
            null
        }
    }

    suspend fun saveSession(sessionData: SessionData) {
        dataStore.edit { preferences ->
            preferences[SESSION_EMAIL] = sessionData.email
            preferences[SESSION_START_TIME] = sessionData.startTime
            preferences[SESSION_ACTIVE] = sessionData.isActive
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(SESSION_EMAIL)
            preferences.remove(SESSION_START_TIME)
            preferences.remove(SESSION_ACTIVE)
        }
    }
}