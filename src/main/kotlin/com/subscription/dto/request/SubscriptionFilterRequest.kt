package com.subscription.dto.request

import com.subscription.domain.enums.SubscriptionStatus
import java.time.LocalDate

data class SubscriptionFilterRequest(
    val userId: Long? = null,
    val serviceName: String? = null,
    val status: SubscriptionStatus? = null,
    val startDateFrom: LocalDate? = null,
    val endDateTo: LocalDate? = null
)