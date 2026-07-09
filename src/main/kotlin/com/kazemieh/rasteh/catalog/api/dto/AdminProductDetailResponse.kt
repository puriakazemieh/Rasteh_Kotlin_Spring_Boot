package com.kazemieh.rasteh.catalog.api.dto

data class AdminProductDetailResponse(
    val product: AdminProductResponse,
    val images: List<AdminProductImageResponse>,
    val videos: List<AdminProductVideoResponse>,
    val variants: List<AdminVariantResponse>
)