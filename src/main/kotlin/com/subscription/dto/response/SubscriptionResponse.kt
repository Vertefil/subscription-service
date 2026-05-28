package com.subscription.dto.response

import com.subscription.domain.enums.SubscriptionStatus
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class SubscriptionResponse(
    val id: Long,
    val userId: Long,
    val serviceName: String,
    val status: SubscriptionStatus,
    val price: BigDecimal,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val cancelledAt: Instant?,
    val suspendedAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isActive: Boolean,
    val daysUntilExpiration: Long?
)