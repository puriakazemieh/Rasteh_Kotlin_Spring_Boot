package com.kazemieh.rasteh.interaction.persistence

import com.kazemieh.rasteh.interaction.persistence.entity.BookmarkEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BookmarkRepository : JpaRepository<BookmarkEntity, Long> {
    fun findAllByUserIdOrderByIdDesc(userId: Long): List<BookmarkEntity>
    fun findByUserIdAndShopId(userId: Long, shopId: Long): BookmarkEntity?
    fun findByUserIdAndProductId(userId: Long, productId: Long): BookmarkEntity?
}
