package com.example.passlessauthethication.data.model


data class OtpData(
    val otp: String,
    val expiryTime: Long,
    val attemptsRemaining: Int = 3,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isExpired(): Boolean = System.currentTimeMillis() > expiryTime

    fun hasAttemptsRemaining(): Boolean = attemptsRemaining > 0

    fun getRemainingSeconds(): Long {
        val remainingMillis = expiryTime - System.currentTimeMillis()
        return if (remainingMillis > 0) remainingMillis / 1000 else 0
    }
}