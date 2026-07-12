package com.kazemieh.rasteh.marketplace.api.dto

import com.kazemieh.rasteh.marketplace.domain.ProductCondition
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

data class CreateProductRequest(
    @field:NotNull
    val shopId: Long,

    @field:NotBlank
    val name: String,

    val description: String? = null,

    @field:NotNull
    @field:PositiveOrZero
    val price: BigDecimal,

    val oldPrice: BigDecimal? = null,
    val discountPercent: Int? = null,
    val condition: ProductCondition = ProductCondition.NEW,
    val stock: Int = 0,
    val categoryName: String? = null,
    val emoji: String? = null,
    val imageUrl: String? = null,
)
