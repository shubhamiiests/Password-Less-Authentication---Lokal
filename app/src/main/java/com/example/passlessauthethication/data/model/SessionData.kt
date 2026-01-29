package com.example.passlessauthethication.data.model

data class SessionData(
    val email: String,
    val startTime: Long,
    val isActive: Boolean = true
)