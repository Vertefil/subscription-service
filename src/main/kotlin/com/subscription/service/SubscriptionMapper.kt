package com.subscription.service

import com.subscription.domain.entity.Subscription
import com.subscription.dto.request.CreateSubscriptionRequest
import com.subscription.dto.response.SubscriptionResponse
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Component
class SubscriptionMapper {

    fun toEntity(request: CreateSubscriptionRequest): Subscription =
        Subscription(
            userId = requireNotNull(request.userId),
            serviceName = requireNotNull(request.serviceName).trim(),
            price = requireNotNull(request.price),
            startDate = requireNotNull(request.startDate),
            endDate = requireNotNull(request.endDate)
        )

    fun toResponse(entity: Subscription): SubscriptionResponse =
        SubscriptionResponse(
            id = requireNotNull(entity.id),
            userId = entity.userId,
            serviceName = entity.serviceName,
            status = entity.status,
            price = entity.price,
            startDate = entity.startDate,
            endDate = entity.endDate,
            cancelledAt = entity.cancelledAt,
            suspendedAt = entity.suspendedAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            isActive = entity.status == com.subscription.domain.enums.SubscriptionStatus.ACTIVE,
            daysUntilExpiration = calculateDaysUntilExpiration(entity)
        )

    fun toResponseList(entities: List<Subscription>): List<SubscriptionResponse> =
        entities.map { toResponse(it) }

    private fun calculateDaysUntilExpiration(entity: Subscription): Long? {
        if (entity.status.isTerminal()) return null
        val today = LocalDate.now()
        if (today.isAfter(entity.endDate)) return 0
        return ChronoUnit.DAYS.between(today, entity.endDate)
    }
}