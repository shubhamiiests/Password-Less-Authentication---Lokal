package com.example.passlessauthethication.domain.usecase

import com.example.passlessauthethication.data.model.OtpData
import com.example.passlessauthethication.domain.manager.OtpManager
import javax.inject.Inject

class GenerateOtpUseCase @Inject constructor(
    private val otpManager: OtpManager
) {
    operator fun invoke(email: String): OtpData {
        return otpManager.generateOtp(email)
    }
    fun getOtpData(email: String): OtpData? {
        return otpManager.getOtpData(email)
    }
}