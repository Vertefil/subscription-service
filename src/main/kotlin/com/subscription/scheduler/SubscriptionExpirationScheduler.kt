package com.subscription.scheduler

import com.subscription.domain.enums.SubscriptionStatus
import com.subscription.repository.SubscriptionRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class SubscriptionExpirationScheduler(
    private val subscriptionRepository: SubscriptionRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${scheduler.expiration.cron}")
    @Transactional
    fun expireSubscriptions() {
        val today = LocalDate.now()

        val expired = subscriptionRepository.findAllByStatusAndEndDateBefore(
            status = SubscriptionStatus.ACTIVE,
            date = today
        )

        if (expired.isEmpty()) {
            log.info("No subscriptions to expire")
            return
        }

        expired.forEach { it.status = SubscriptionStatus.EXPIRED }
        subscriptionRepository.saveAll(expired)

        log.info("Expired ${expired.size} subscriptions")
    }
}