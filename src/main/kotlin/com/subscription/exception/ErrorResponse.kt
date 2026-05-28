package com.subscription.exception

import java.time.Instant

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: Instant = Instant.now(),
    val violations: List<FieldViolation> = emptyList()
)

data class FieldViolation(
    val field: String,
    val message: String
)