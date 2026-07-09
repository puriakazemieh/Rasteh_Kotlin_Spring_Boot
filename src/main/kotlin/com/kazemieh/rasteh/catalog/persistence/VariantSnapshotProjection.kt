package com.kazemieh.rasteh.catalog.persistence

import java.math.BigDecimal

interface VariantSnapshotProjection {
    fun getVariantId(): Long
    fun getPrice(): BigDecimal
    fun getTitle(): String
    fun getIsActive(): Boolean
}
