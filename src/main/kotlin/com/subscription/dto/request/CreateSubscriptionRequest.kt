package com.subscription.dto.request

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDate

data class CreateSubscriptionRequest(

    @field:NotNull(message = "userId is required")
    @field:Positive(message = "userId must be positive")
    val userId: Long?,

    @field:NotBlank(message = "serviceName is required")
    val serviceName: String?,

    @field:NotNull(message = "price is required")
    @field:DecimalMin(value = "0.01", message = "price must be greater than 0")
    val price: BigDecimal?,

    @field:NotNull(message = "startDate is required")
    val startDate: LocalDate?,

    @field:NotNull(message = "endDate is required")
    val endDate: LocalDate?
)