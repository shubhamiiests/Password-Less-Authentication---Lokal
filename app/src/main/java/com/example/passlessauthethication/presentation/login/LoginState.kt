package com.example.passlessauthethication.presentation.login

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val email: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val loginState: LoginState = LoginState.Idle
)