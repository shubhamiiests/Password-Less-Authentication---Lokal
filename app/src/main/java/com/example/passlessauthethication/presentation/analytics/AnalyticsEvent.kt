package com.example.passlessauthethication.presentation.analytics

sealed class AnalyticsEvent(val eventName: String, val params: Map<String, Any> = emptyMap()) {

    data class OtpGenerated(
        val email: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : AnalyticsEvent(
        eventName = "otp_generated",
        params = mapOf(
            "email_domain" to email.substringAfter("@"),
            "timestamp" to timestamp
        )
    )

    data class OtpValidationSuccess(
        val email: String,
        val attemptNumber: Int
    ) : AnalyticsEvent(
        eventName = "otp_validation_success",
        params = mapOf(
            "email_domain" to email.substringAfter("@"),
            "attempt_number" to attemptNumber
        )
    )

    data class OtpValidationFailure(
        val email: String,
        val reason: String,
        val attemptsRemaining: Int
    ) : AnalyticsEvent(
        eventName = "otp_validation_failure",
        params = mapOf(
            "email_domain" to email.substringAfter("@"),
            "failure_reason" to reason,
            "attempts_remaining" to attemptsRemaining
        )
    )

    data class UserLogout(
        val email: String,
        val sessionDuration: Long
    ) : AnalyticsEvent(
        eventName = "user_logout",
        params = mapOf(
            "email_domain" to email.substringAfter("@"),
            "session_duration_seconds" to sessionDuration
        )
    )
}