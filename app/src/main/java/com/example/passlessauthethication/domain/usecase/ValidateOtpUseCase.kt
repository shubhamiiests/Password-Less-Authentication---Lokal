package com.example.passlessauthethication.domain.usecase

import com.example.passlessauthethication.domain.manager.OtpManager
import com.example.passlessauthethication.domain.manager.OtpValidationResult
import javax.inject.Inject

class ValidateOtpUseCase @Inject constructor(
    private val otpManager: OtpManager
) {
    operator fun invoke(email: String, otp: String): OtpValidationResult {
        return otpManager.validateOtp(email, otp)
    }
}