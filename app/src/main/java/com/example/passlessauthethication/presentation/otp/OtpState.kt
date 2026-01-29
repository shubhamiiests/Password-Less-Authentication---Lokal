package com.example.passlessauthethication.presentation.otp

sealed class OtpValidationState {
    object Idle : OtpValidationState()
    object Validating : OtpValidationState()
    object Success : OtpValidationState()
    data class Error(val message: String) : OtpValidationState()
}

data class OtpUiState(
    val email: String = "",
    val otp: String = "",
    val remainingSeconds: Long = 0,
    val attemptsRemaining: Int = 3,
    val canResend: Boolean = false,
    val isValidating: Boolean = false,
    val validationState: OtpValidationState = OtpValidationState.Idle,
    val showResendSuccess: Boolean = false,
    val generatedOtp: String = "" // For debugging - remove in production
)