package com.kazemieh.rasteh.marketplace.api.dto

import com.kazemieh.rasteh.marketplace.domain.ProductCondition
import java.math.BigDecimal

/** به‌روزرسانیِ جزئی — فقط فیلدهایِ غیرِnull اعمال می‌شوند. */
data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    val price: BigDecimal? = null,
    val oldPrice: BigDecimal? = null,
    val discountPercent: Int? = null,
    val condition: ProductCondition? = null,
    val stock: Int? = null,
    val categoryName: String? = null,
    val emoji: String? = null,
    val imageUrl: String? = null,
    val active: Boolean? = null,
)
