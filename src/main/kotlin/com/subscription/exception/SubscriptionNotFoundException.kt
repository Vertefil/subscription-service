package com.subscription.exception

class SubscriptionNotFoundException(id: Long) :
    RuntimeException("Subscription not found with id=$id")