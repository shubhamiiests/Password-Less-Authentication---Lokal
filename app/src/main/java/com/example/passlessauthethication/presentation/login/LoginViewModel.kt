package com.example.passlessauthethication.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.passlessauthethication.domain.usecase.GenerateOtpUseCase
import com.example.passlessauthethication.presentation.analytics.AnalyticsEvent
import com.example.passlessauthethication.presentation.analytics.AnalyticsLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val generateOtpUseCase: GenerateOtpUseCase,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun sendOtp() {
        val email = _uiState.value.email.trim()

        if (!isValidEmail(email)) {
            _uiState.update {
                it.copy(emailError = "Please enter a valid email address")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Simulate network delay for better UX
                kotlinx.coroutines.delay(500)

                val otpData = generateOtpUseCase(email)

                // Log analytics event
                analyticsLogger.logEvent(AnalyticsEvent.OtpGenerated(email))

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginState = LoginState.Success(email)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginState = LoginState.Error("Failed to send OTP. Please try again.")
                    )
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { LoginUiState() }
    }

    private fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false

        val emailRegex = Regex(
            "[a-zA-Z0-9+._%\\-]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )

        return emailRegex.matches(email)
    }
}