package com.subscription.exception

import com.subscription.domain.enums.SubscriptionStatus

class InvalidStatusTransitionException(
    from: SubscriptionStatus,
    to: SubscriptionStatus
) : RuntimeException("Cannot transition from $from to $to")