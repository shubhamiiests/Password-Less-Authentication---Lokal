package com.example.passlessauthethication.data.repository

import com.example.passlessauthethication.data.local.PreferencesManager
import com.example.passlessauthethication.data.model.SessionData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val preferencesManager: PreferencesManager
) {

    val currentSession: Flow<SessionData?> = preferencesManager.sessionData

    suspend fun createSession(email: String): SessionData {
        val sessionData = SessionData(
            email = email,
            startTime = System.currentTimeMillis(),
            isActive = true
        )
        preferencesManager.saveSession(sessionData)
        return sessionData
    }

    suspend fun endSession() {
        preferencesManager.clearSession()
    }
}