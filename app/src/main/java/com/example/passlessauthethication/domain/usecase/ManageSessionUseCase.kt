package com.example.passlessauthethication.domain.usecase

import com.example.passlessauthethication.data.model.SessionData
import com.example.passlessauthethication.data.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {

    fun getCurrentSession(): Flow<SessionData?> {
        return sessionRepository.currentSession
    }

    suspend fun createSession(email: String): SessionData {
        return sessionRepository.createSession(email)
    }

    suspend fun endSession() {
        sessionRepository.endSession()
    }
}