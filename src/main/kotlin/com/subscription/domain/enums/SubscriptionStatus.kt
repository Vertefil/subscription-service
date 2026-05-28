package com.subscription.domain.enums

enum class SubscriptionStatus {
    ACTIVE,
    SUSPENDED,
    CANCELLED,
    EXPIRED;

    fun isTerminal(): Boolean = this == CANCELLED || this == EXPIRED

    fun canTransitionTo(target: SubscriptionStatus): Boolean =
        when (this) {
            ACTIVE -> target in setOf(SUSPENDED, CANCELLED)
            SUSPENDED -> target in setOf(ACTIVE, CANCELLED)
            CANCELLED -> false
            EXPIRED -> false
        }
}