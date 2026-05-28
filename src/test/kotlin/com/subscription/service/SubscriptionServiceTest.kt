package com.subscription.service

import com.subscription.domain.entity.Subscription
import com.subscription.domain.enums.SubscriptionStatus
import com.subscription.dto.request.CreateSubscriptionRequest
import com.subscription.dto.response.SubscriptionResponse
import com.subscription.exception.InvalidStatusTransitionException
import com.subscription.exception.SubscriptionNotFoundException
import com.subscription.repository.SubscriptionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class SubscriptionServiceTest {

    @Mock
    lateinit var subscriptionRepository: SubscriptionRepository

    @Mock
    lateinit var subscriptionMapper: SubscriptionMapper

    @InjectMocks
    lateinit var subscriptionService: SubscriptionService

    @Test
    fun `getById should return response when subscription exists`() {
        val subscription = buildSubscription()
        val response = buildResponse()

        whenever(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription))
        whenever(subscriptionMapper.toResponse(subscription)).thenReturn(response)

        val result = subscriptionService.getById(1L)

        assertEquals(response, result)
        verify(subscriptionRepository).findById(1L)
    }

    @Test
    fun `getById should throw SubscriptionNotFoundException when not found`() {
        whenever(subscriptionRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows<SubscriptionNotFoundException> {
            subscriptionService.getById(99L)
        }
    }

    @Test
    fun `create should throw IllegalArgumentException when endDate before startDate`() {
        val request = CreateSubscriptionRequest(
            userId = 1L,
            serviceName = "Netflix",
            price = BigDecimal("9.99"),
            startDate = LocalDate.of(2024, 12, 31),
            endDate = LocalDate.of(2024, 1, 1)
        )

        assertThrows<IllegalArgumentException> {
            subscriptionService.create(request)
        }
    }

    @Test
    fun `suspend should set suspendedAt and transition to SUSPENDED`() {
        val subscription = buildSubscription(status = SubscriptionStatus.ACTIVE)
        val response = buildResponse(status = SubscriptionStatus.SUSPENDED)

        whenever(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription))
        whenever(subscriptionRepository.save(subscription)).thenReturn(subscription)
        whenever(subscriptionMapper.toResponse(subscription)).thenReturn(response)

        val result = subscriptionService.suspend(1L)

        assertEquals(SubscriptionStatus.SUSPENDED, result.status)
        assertNotNull(subscription.suspendedAt)
        verify(subscriptionRepository).save(subscription)
    }

    @Test
    fun `cancel should set cancelledAt and transition to CANCELLED`() {
        val subscription = buildSubscription(status = SubscriptionStatus.ACTIVE)
        val response = buildResponse(status = SubscriptionStatus.CANCELLED)

        whenever(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription))
        whenever(subscriptionRepository.save(subscription)).thenReturn(subscription)
        whenever(subscriptionMapper.toResponse(subscription)).thenReturn(response)

        subscriptionService.cancel(1L)

        assertEquals(SubscriptionStatus.CANCELLED, subscription.status)
        assertNotNull(subscription.cancelledAt)
    }

    @Test
    fun `suspend should throw InvalidStatusTransitionException for CANCELLED subscription`() {
        val subscription = buildSubscription(status = SubscriptionStatus.CANCELLED)
        whenever(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription))

        assertThrows<InvalidStatusTransitionException> {
            subscriptionService.suspend(1L)
        }
    }

    @Test
    fun `activate should throw InvalidStatusTransitionException for EXPIRED subscription`() {
        val subscription = buildSubscription(status = SubscriptionStatus.EXPIRED)
        whenever(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription))

        assertThrows<InvalidStatusTransitionException> {
            subscriptionService.activate(1L)
        }
    }

    @Test
    fun `activate should clear suspendedAt when reactivating SUSPENDED subscription`() {
        val subscription = buildSubscription(status = SubscriptionStatus.SUSPENDED)
        subscription.suspendedAt = Instant.now()
        val response = buildResponse(status = SubscriptionStatus.ACTIVE)

        whenever(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription))
        whenever(subscriptionRepository.save(subscription)).thenReturn(subscription)
        whenever(subscriptionMapper.toResponse(subscription)).thenReturn(response)

        subscriptionService.activate(1L)

        assertEquals(SubscriptionStatus.ACTIVE, subscription.status)
        org.junit.jupiter.api.Assertions.assertNull(subscription.suspendedAt)
    }

    private fun buildSubscription(
        status: SubscriptionStatus = SubscriptionStatus.ACTIVE
    ) = Subscription(
        userId = 1L,
        serviceName = "Netflix",
        price = BigDecimal("9.99"),
        startDate = LocalDate.now(),
        endDate = LocalDate.now().plusMonths(1),
        status = status
    )

    private fun buildResponse(
        status: SubscriptionStatus = SubscriptionStatus.ACTIVE
    ) = SubscriptionResponse(
        id = 1L,
        userId = 1L,
        serviceName = "Netflix",
        status = status,
        price = BigDecimal("9.99"),
        startDate = LocalDate.now(),
        endDate = LocalDate.now().plusMonths(1),
        cancelledAt = null,
        suspendedAt = null,
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
        isActive = status == SubscriptionStatus.ACTIVE,
        daysUntilExpiration = 30L
    )
}