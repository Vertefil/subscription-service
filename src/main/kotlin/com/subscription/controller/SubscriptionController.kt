package com.subscription.controller

import com.subscription.dto.request.CreateSubscriptionRequest
import com.subscription.dto.request.SubscriptionFilterRequest
import com.subscription.dto.response.SubscriptionResponse
import com.subscription.domain.enums.SubscriptionStatus
import com.subscription.dto.request.UpdateSubscriptionRequest
import com.subscription.service.SubscriptionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscriptions", description = "Subscription management API")
class SubscriptionController(
    private val subscriptionService: SubscriptionService
) {

    @PostMapping
    @Operation(summary = "Create a new subscription")
    fun create(
        @Valid @RequestBody request: CreateSubscriptionRequest
    ): ResponseEntity<SubscriptionResponse> =
        subscriptionService.create(request)
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping("/{id}")
    @Operation(summary = "Get subscription by ID")
    fun getById(@PathVariable id: Long): ResponseEntity<SubscriptionResponse> =
        subscriptionService.getById(id)
            .let { ResponseEntity.ok(it) }

    @GetMapping
    @Operation(summary = "Get all subscriptions with filters")
    fun getAll(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) serviceName: String?,
        @RequestParam(required = false) status: SubscriptionStatus?,
        @RequestParam(required = false) startDateFrom: LocalDate?,
        @RequestParam(required = false) endDateTo: LocalDate?,
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable
    ): ResponseEntity<Page<SubscriptionResponse>> {
        val filter = SubscriptionFilterRequest(
            userId = userId,
            serviceName = serviceName,
            status = status,
            startDateFrom = startDateFrom,
            endDateTo = endDateTo
        )
        return ResponseEntity.ok(subscriptionService.getAll(filter, pageable))
    }

    @PatchMapping("/{id}/suspend")
    @Operation(summary = "Suspend a subscription")
    fun suspend(@PathVariable id: Long): ResponseEntity<SubscriptionResponse> =
        subscriptionService.suspend(id)
            .let { ResponseEntity.ok(it) }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a subscription")
    fun cancel(@PathVariable id: Long): ResponseEntity<SubscriptionResponse> =
        subscriptionService.cancel(id)
            .let { ResponseEntity.ok(it) }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a suspended subscription")
    fun activate(@PathVariable id: Long): ResponseEntity<SubscriptionResponse> =
        subscriptionService.activate(id)
            .let { ResponseEntity.ok(it) }

    @PutMapping("/{id}")
    @Operation(summary = "Update subscription price and end date")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateSubscriptionRequest
    ): ResponseEntity<SubscriptionResponse> =
        subscriptionService.update(id, request)
            .let { ResponseEntity.ok(it) }

    @GetMapping("/users/{userId}/active")
    @Operation(summary = "Get active subscriptions for a user")
    fun getActiveByUserId(
        @PathVariable userId: Long
    ): ResponseEntity<List<SubscriptionResponse>> =
        subscriptionService.getActiveByUserId(userId)
            .let { ResponseEntity.ok(it) }
}