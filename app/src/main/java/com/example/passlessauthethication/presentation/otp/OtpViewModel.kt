package com.example.passlessauthethication.presentation.otp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.passlessauthethication.domain.manager.OtpValidationResult
import com.example.passlessauthethication.domain.usecase.GenerateOtpUseCase
import com.example.passlessauthethication.domain.usecase.ManageSessionUseCase
import com.example.passlessauthethication.domain.usecase.ValidateOtpUseCase
import com.example.passlessauthethication.presentation.analytics.AnalyticsEvent
import com.example.passlessauthethication.presentation.analytics.AnalyticsLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val generateOtpUseCase: GenerateOtpUseCase,
    private val validateOtpUseCase: ValidateOtpUseCase,
    private val manageSessionUseCase: ManageSessionUseCase,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val email: String = checkNotNull(savedStateHandle["email"])

    private val _uiState = MutableStateFlow(OtpUiState(email = email))
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        generateInitialOtp()
    }

    private fun generateInitialOtp() {
        val otpData = generateOtpUseCase(email)
        _uiState.update {
            it.copy(
                attemptsRemaining = otpData.attemptsRemaining,
                generatedOtp = otpData.otp // For debugging
            )
        }
        startTimer()
    }

    fun onOtpChange(otp: String) {
        _uiState.update { it.copy(otp = otp) }

        if (otp.length == 6) {
            validateOtp()
        }
    }

    fun validateOtp() {
        val currentOtp = _uiState.value.otp
        if (currentOtp.length != 6) return

        viewModelScope.launch {
            _uiState.update { it.copy(isValidating = true) }

            // Simulate network delay
            delay(500)

            val result = validateOtpUseCase(email, currentOtp)

            when (result) {
                is OtpValidationResult.Success -> {
                    analyticsLogger.logEvent(
                        AnalyticsEvent.OtpValidationSuccess(
                            email = email,
                            attemptNumber = 4 - _uiState.value.attemptsRemaining
                        )
                    )

                    // Create session
                    manageSessionUseCase.createSession(email)

                    _uiState.update {
                        it.copy(
                            isValidating = false,
                            validationState = OtpValidationState.Success
                        )
                    }
                    timerJob?.cancel()
                }

                is OtpValidationResult.Invalid -> {
                    analyticsLogger.logEvent(
                        AnalyticsEvent.OtpValidationFailure(
                            email = email,
                            reason = "invalid_otp",
                            attemptsRemaining = result.attemptsRemaining
                        )
                    )

                    _uiState.update {
                        it.copy(
                            isValidating = false,
                            attemptsRemaining = result.attemptsRemaining,
                            validationState = OtpValidationState.Error(
                                "Invalid OTP. ${result.attemptsRemaining} attempts remaining."
                            ),
                            otp = ""
                        )
                    }
                }

                is OtpValidationResult.Expired -> {
                    analyticsLogger.logEvent(
                        AnalyticsEvent.OtpValidationFailure(
                            email = email,
                            reason = "expired",
                            attemptsRemaining = _uiState.value.attemptsRemaining
                        )
                    )

                    _uiState.update {
                        it.copy(
                            isValidating = false,
                            validationState = OtpValidationState.Error(
                                "OTP has expired. Please request a new one."
                            ),
                            canResend = true,
                            otp = ""
                        )
                    }
                    timerJob?.cancel()
                }

                is OtpValidationResult.MaxAttemptsExceeded -> {
                    analyticsLogger.logEvent(
                        AnalyticsEvent.OtpValidationFailure(
                            email = email,
                            reason = "max_attempts_exceeded",
                            attemptsRemaining = 0
                        )
                    )

                    _uiState.update {
                        it.copy(
                            isValidating = false,
                            validationState = OtpValidationState.Error(
                                "Maximum attempts exceeded. Please request a new OTP."
                            ),
                            canResend = true,
                            otp = ""
                        )
                    }
                    timerJob?.cancel()
                }

                is OtpValidationResult.NotFound -> {
                    analyticsLogger.logEvent(
                        AnalyticsEvent.OtpValidationFailure(
                            email = email,
                            reason = "not_found",
                            attemptsRemaining = 0
                        )
                    )

                    _uiState.update {
                        it.copy(
                            isValidating = false,
                            validationState = OtpValidationState.Error(
                                "OTP not found. Please request a new one."
                            ),
                            canResend = true,
                            otp = ""
                        )
                    }
                }
            }
        }
    }

    fun resendOtp() {
        viewModelScope.launch {
            val otpData = generateOtpUseCase(email)

            analyticsLogger.logEvent(AnalyticsEvent.OtpGenerated(email))

            _uiState.update {
                it.copy(
                    otp = "",
                    attemptsRemaining = otpData.attemptsRemaining,
                    canResend = false,
                    showResendSuccess = true,
                    validationState = OtpValidationState.Idle,
                    generatedOtp = otpData.otp // For debugging
                )
            }

            startTimer()

            // Hide resend success message after 2 seconds
            delay(2000)
            _uiState.update { it.copy(showResendSuccess = false) }
        }
    }

    fun resetValidationState() {
        _uiState.update { it.copy(validationState = OtpValidationState.Idle) }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val otpData = generateOtpUseCase.otpManager.getOtpData(email)

                if (otpData == null || otpData.isExpired()) {
                    _uiState.update {
                        it.copy(
                            remainingSeconds = 0,
                            canResend = true
                        )
                    }
                    break
                }

                val remaining = otpData.getRemainingSeconds()
                _uiState.update { it.copy(remainingSeconds = remaining) }

                if (remaining <= 0) {
                    _uiState.update { it.copy(canResend = true) }
                    break
                }

                delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

// Extension to access OtpManager from use case (for timer)
private val GenerateOtpUseCase.otpManager: com.example.passlessauthethication.domain.manager.OtpManager
    get() = this.javaClass.getDeclaredField("otpManager").apply {
        isAccessible = true
    }.get(this) as com.example.passlessauthethication.domain.manager.OtpManager