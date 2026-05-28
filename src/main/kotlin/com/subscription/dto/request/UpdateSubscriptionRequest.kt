package com.subscription.dto.request

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class UpdateSubscriptionRequest(

    @field:NotNull(message = "price is required")
    @field:DecimalMin(value = "0.01", message = "price must be greater than 0")
    val price: BigDecimal?,

    @field:NotNull(message = "endDate is required")
    @field:Future(message = "endDate must be in the future")
    val endDate: LocalDate?
)