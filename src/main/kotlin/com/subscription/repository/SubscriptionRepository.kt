package com.subscription.repository

import com.subscription.domain.entity.Subscription
import com.subscription.domain.enums.SubscriptionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface SubscriptionRepository : JpaRepository<Subscription, Long>, JpaSpecificationExecutor<Subscription> {

    fun findByUserId(userId: Long, pageable: Pageable): Page<Subscription>

    fun findByUserIdAndStatus(userId: Long, status: SubscriptionStatus): List<Subscription>

    fun existsByUserIdAndServiceNameAndStatus(
        userId: Long,
        serviceName: String,
        status: SubscriptionStatus
    ): Boolean

    @Query("""
        SELECT s FROM Subscription s
        WHERE s.status = :status
        AND s.endDate <= :date
    """)
    fun findAllByStatusAndEndDateBefore(
        @Param("status") status: SubscriptionStatus,
        @Param("date") date: LocalDate
    ): List<Subscription>
}