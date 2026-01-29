package com.example.passlessauthethication.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passlessauthethication.domain.usecase.ManageSessionUseCase
import com.example.passlessauthethication.presentation.analytics.AnalyticsEvent
import com.example.passlessauthethication.presentation.analytics.AnalyticsLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val manageSessionUseCase: ManageSessionUseCase,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SessionEvent>()
    val events = _events.asSharedFlow()

    private var timerJob: Job? = null

    init {
        loadSession()
    }

    private fun loadSession() {
        viewModelScope.launch {
            manageSessionUseCase.getCurrentSession().collect { session ->
                if (session != null) {
                    _uiState.update {
                        it.copy(
                            email = session.email,
                            sessionStartTime = session.startTime
                        )
                    }
                    startTimer()
                }
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val currentTime = System.currentTimeMillis()
                val duration = (currentTime - _uiState.value.sessionStartTime) / 1000
                _uiState.update { it.copy(currentDuration = duration) }
                delay(1000)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }

            // Calculate session duration
            val sessionDuration = _uiState.value.currentDuration

            // Log analytics event
            analyticsLogger.logEvent(
                AnalyticsEvent.UserLogout(
                    email = _uiState.value.email,
                    sessionDuration = sessionDuration
                )
            )

            // Simulate logout delay
            delay(500)

            // End session
            manageSessionUseCase.endSession()

            // Stop timer
            timerJob?.cancel()

            _uiState.update { it.copy(isLoggingOut = false) }
            _events.emit(SessionEvent.LogoutSuccess)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}