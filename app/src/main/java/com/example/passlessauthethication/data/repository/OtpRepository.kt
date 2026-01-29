package com.example.passlessauthethication.data.repository

import com.example.passlessauthethication.data.model.OtpData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtpRepository @Inject constructor() {

    private val otpStorage = mutableMapOf<String, OtpData>()
    private val _otpDataFlow = MutableStateFlow<Map<String, OtpData>>(emptyMap())
    val otpDataFlow: StateFlow<Map<String, OtpData>> = _otpDataFlow.asStateFlow()

    fun generateOtp(email: String): OtpData {
        val otp = generateRandomOtp()
        val expiryTime = System.currentTimeMillis() + 60_000 // 60 seconds
        val otpData = OtpData(otp = otp, expiryTime = expiryTime)

        otpStorage[email] = otpData
        _otpDataFlow.value = otpStorage.toMap()

        return otpData
    }

    fun getOtpData(email: String): OtpData? {
        return otpStorage[email]?.takeIf { !it.isExpired() }
    }

    fun decrementAttempt(email: String): OtpData? {
        val currentData = otpStorage[email] ?: return null
        val updatedData = currentData.copy(
            attemptsRemaining = (currentData.attemptsRemaining - 1).coerceAtLeast(0)
        )
        otpStorage[email] = updatedData
        _otpDataFlow.value = otpStorage.toMap()
        return updatedData
    }

    fun clearOtp(email: String) {
        otpStorage.remove(email)
        _otpDataFlow.value = otpStorage.toMap()
    }

    private fun generateRandomOtp(): String {
        return (100000..999999).random().toString()
    }
}