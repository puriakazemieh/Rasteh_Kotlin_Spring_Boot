package com.kazemieh.rasteh.catalog.api.dto

/** مشخصه‌ی محصول (کلید/مقدار) — مثلاً جنس: نخی. */
data class ProductAttributeDto(
    val name: String,
    val value: String
)
