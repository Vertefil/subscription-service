package com.subscription.service

import com.subscription.domain.entity.Subscription
import com.subscription.domain.enums.SubscriptionStatus
import com.subscription.dto.request.CreateSubscriptionRequest
import com.subscription.dto.request.SubscriptionFilterRequest
import com.subscription.dto.request.UpdateSubscriptionRequest
import com.subscription.dto.response.SubscriptionResponse
import com.subscription.exception.InvalidStatusTransitionException
import com.subscription.exception.SubscriptionNotFoundException
import com.subscription.repository.SubscriptionRepository
import com.subscription.repository.SubscriptionSpecification
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional(readOnly = true)
class SubscriptionService(
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionMapper: SubscriptionMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun create(request: CreateSubscriptionRequest): SubscriptionResponse {
        validateDates(request)
        val subscription = subscriptionMapper.toEntity(request)
        return subscriptionRepository.save(subscription)
            .also { log.info("Created subscription id=${it.id} for userId=${it.userId}") }
            .let { subscriptionMapper.toResponse(it) }
    }

    fun getById(id: Long): SubscriptionResponse =
        findByIdOrThrow(id).let { subscriptionMapper.toResponse(it) }

    fun getAll(filter: SubscriptionFilterRequest, pageable: Pageable): Page<SubscriptionResponse> {
        val spec = SubscriptionSpecification.byUserId(filter.userId)
            .and(SubscriptionSpecification.byServiceName(filter.serviceName))
            .and(SubscriptionSpecification.byStatus(filter.status))
            .and(SubscriptionSpecification.byStartDateAfter(filter.startDateFrom))
            .and(SubscriptionSpecification.byEndDateBefore(filter.endDateTo))

        return subscriptionRepository.findAll(spec, pageable)
            .map { subscriptionMapper.toResponse(it) }
    }

    fun getActiveByUserId(userId: Long): List<SubscriptionResponse> =
        subscriptionRepository
            .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
            .map { subscriptionMapper.toResponse(it) }

    @Transactional
    fun suspend(id: Long): SubscriptionResponse =
        transitionStatus(id, SubscriptionStatus.SUSPENDED) {
            it.suspendedAt = Instant.now()
        }

    @Transactional
    fun cancel(id: Long): SubscriptionResponse =
        transitionStatus(id, SubscriptionStatus.CANCELLED) {
            it.cancelledAt = Instant.now()
        }

    @Transactional
    fun activate(id: Long): SubscriptionResponse =
        transitionStatus(id, SubscriptionStatus.ACTIVE) {
            it.suspendedAt = null
        }

    @Transactional
    fun update(id: Long, request: UpdateSubscriptionRequest): SubscriptionResponse {
        val subscription = findByIdOrThrow(id)

        require(!subscription.status.isTerminal()) {
            "Cannot update subscription with status ${subscription.status}"
        }

        subscription.price = requireNotNull(request.price)
        subscription.endDate = requireNotNull(request.endDate)

        return subscriptionRepository.save(subscription)
            .also { log.info("Updated subscription id=$id") }
            .let { subscriptionMapper.toResponse(it) }
    }

    @Transactional
    fun expireSubscription(id: Long): SubscriptionResponse =
        transitionStatus(id, SubscriptionStatus.EXPIRED) {}

    private fun transitionStatus(
        id: Long,
        targetStatus: SubscriptionStatus,
        sideEffect: (Subscription) -> Unit
    ): SubscriptionResponse {
        val subscription = findByIdOrThrow(id)

        if (!subscription.status.canTransitionTo(targetStatus)) {
            throw InvalidStatusTransitionException(subscription.status, targetStatus)
        }

        subscription.status = targetStatus
        sideEffect(subscription)

        return subscriptionRepository.save(subscription)
            .also { log.info("Subscription id=$id transitioned to $targetStatus") }
            .let { subscriptionMapper.toResponse(it) }
    }

    private fun findByIdOrThrow(id: Long): Subscription =
        subscriptionRepository.findById(id).orElse(null)
            ?: throw SubscriptionNotFoundException(id)

    private fun validateDates(request: CreateSubscriptionRequest) {
        val startDate = requireNotNull(request.startDate)
        val endDate = requireNotNull(request.endDate)
        require(endDate.isAfter(startDate)) {
            "endDate must be after startDate"
        }
    }
}