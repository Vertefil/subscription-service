package com.subscription.repository

import com.subscription.domain.entity.Subscription
import com.subscription.domain.enums.SubscriptionStatus
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

object SubscriptionSpecification {

    fun byUserId(userId: Long?): Specification<Subscription> =
        Specification { root, _, cb ->
            userId?.let { cb.equal(root.get<Long>("userId"), it) }
        }

    fun byServiceName(serviceName: String?): Specification<Subscription> =
        Specification { root, _, cb ->
            serviceName?.let { cb.equal(root.get<String>("serviceName"), it) }
        }

    fun byStatus(status: SubscriptionStatus?): Specification<Subscription> =
        Specification { root, _, cb ->
            status?.let { cb.equal(root.get<SubscriptionStatus>("status"), it) }
        }

    fun byStartDateAfter(date: LocalDate?): Specification<Subscription> =
        Specification { root, _, cb ->
            date?.let { cb.greaterThanOrEqualTo(root.get("startDate"), it) }
        }

    fun byEndDateBefore(date: LocalDate?): Specification<Subscription> =
        Specification { root, _, cb ->
            date?.let { cb.lessThanOrEqualTo(root.get("endDate"), it) }
        }
}