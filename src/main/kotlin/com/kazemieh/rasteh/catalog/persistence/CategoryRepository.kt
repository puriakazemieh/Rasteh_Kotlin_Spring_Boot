package com.kazemieh.rasteh.catalog.persistence

import com.kazemieh.rasteh.catalog.persistence.entity.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<CategoryEntity, Long> {
    fun findBySlug(slug: String): CategoryEntity?
    fun findAllByParentIdOrderByNameAsc(parentId: Long?): List<CategoryEntity>
    fun existsBySlug(slug: String): Boolean
}