package com.example.passlessauthethication.domain.manager

import com.example.passlessauthethication.data.model.OtpData
import com.example.passlessauthethication.data.repository.OtpRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtpManager @Inject constructor(
    private val otpRepository: OtpRepository
) {

    fun generateOtp(email: String): OtpData {
        return otpRepository.generateOtp(email)
    }

    fun validateOtp(email: String, enteredOtp: String): OtpValidationResult {
        val otpData = otpRepository.getOtpData(email)
            ?: return OtpValidationResult.NotFound

        if (otpData.isExpired()) {
            return OtpValidationResult.Expired
        }

        if (!otpData.hasAttemptsRemaining()) {
            return OtpValidationResult.MaxAttemptsExceeded
        }

        return if (otpData.otp == enteredOtp) {
            otpRepository.clearOtp(email)
            OtpValidationResult.Success
        } else {
            val updatedData = otpRepository.decrementAttempt(email)
            OtpValidationResult.Invalid(updatedData?.attemptsRemaining ?: 0)
        }
    }

    fun getOtpData(email: String): OtpData? {
        return otpRepository.getOtpData(email)
    }
}

sealed class OtpValidationResult {
    object Success : OtpValidationResult()
    object NotFound : OtpValidationResult()
    object Expired : OtpValidationResult()
    object MaxAttemptsExceeded : OtpValidationResult()
    data class Invalid(val attemptsRemaining: Int) : OtpValidationResult()
}