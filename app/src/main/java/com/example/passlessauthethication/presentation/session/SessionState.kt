package com.example.passlessauthethication.presentation.session

sealed class SessionEvent {
    object LogoutSuccess : SessionEvent()
    data class Error(val message: String) : SessionEvent()
}

data class SessionUiState(
    val email: String = "",
    val sessionStartTime: Long = 0,
    val currentDuration: Long = 0,
    val isLoggingOut: Boolean = false
)