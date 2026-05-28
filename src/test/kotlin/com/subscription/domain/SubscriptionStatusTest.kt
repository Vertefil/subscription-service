package com.subscription.domain

import com.subscription.domain.enums.SubscriptionStatus
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class SubscriptionStatusTest {

    @Test
    fun `ACTIVE can transition to SUSPENDED`() =
        assertTrue(SubscriptionStatus.ACTIVE.canTransitionTo(SubscriptionStatus.SUSPENDED))

    @Test
    fun `ACTIVE can transition to CANCELLED`() =
        assertTrue(SubscriptionStatus.ACTIVE.canTransitionTo(SubscriptionStatus.CANCELLED))

    @Test
    fun `ACTIVE cannot transition to EXPIRED directly`() =
        assertFalse(SubscriptionStatus.ACTIVE.canTransitionTo(SubscriptionStatus.EXPIRED))

    @Test
    fun `SUSPENDED can transition to ACTIVE`() =
        assertTrue(SubscriptionStatus.SUSPENDED.canTransitionTo(SubscriptionStatus.ACTIVE))

    @Test
    fun `SUSPENDED can transition to CANCELLED`() =
        assertTrue(SubscriptionStatus.SUSPENDED.canTransitionTo(SubscriptionStatus.CANCELLED))

    @ParameterizedTest
    @EnumSource(SubscriptionStatus::class)
    fun `CANCELLED cannot transition to any status`(target: SubscriptionStatus) =
        assertFalse(SubscriptionStatus.CANCELLED.canTransitionTo(target))

    @ParameterizedTest
    @EnumSource(SubscriptionStatus::class)
    fun `EXPIRED cannot transition to any status`(target: SubscriptionStatus) =
        assertFalse(SubscriptionStatus.EXPIRED.canTransitionTo(target))

    @Test
    fun `isTerminal returns true for CANCELLED`() =
        assertTrue(SubscriptionStatus.CANCELLED.isTerminal())

    @Test
    fun `isTerminal returns true for EXPIRED`() =
        assertTrue(SubscriptionStatus.EXPIRED.isTerminal())

    @Test
    fun `isTerminal returns false for ACTIVE`() =
        assertFalse(SubscriptionStatus.ACTIVE.isTerminal())

    @Test
    fun `isTerminal returns false for SUSPENDED`() =
        assertFalse(SubscriptionStatus.SUSPENDED.isTerminal())
}