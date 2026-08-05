package com.kazemieh.rasteh.marketplace.persistence

import com.kazemieh.rasteh.marketplace.domain.ShopStatus
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Lock
import jakarta.persistence.LockModeType
import org.springframework.data.repository.query.Param

interface ShopProductRepository : JpaRepository<ShopProductEntity, Long>, JpaSpecificationExecutor<ShopProductEntity> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ShopProductEntity p where p.id = :id")
    fun findByIdForUpdate(@Param("id") id: Long): ShopProductEntity?
    fun findAllByShopIdAndActiveTrueOrderByIdDesc(shopId: Long): List<ShopProductEntity>
    fun findAllByShopIdOrderByIdDesc(shopId: Long): List<ShopProductEntity>

    /** «دیدن در فروشگاه‌های دیگر» — کالاهایِ هم‌نامِ فعال در فروشگاه‌های تأییدشدهٔ دیگر (ارزان‌ترین اول). */
    @Query(
        "select p from ShopProductEntity p " +
            "where p.active = true and lower(p.name) = lower(:name) " +
            "and p.shop.status = :status and p.shop.id <> :shopId " +
            "order by p.price asc"
    )
    fun findOtherSellers(
        @Param("name") name: String,
        @Param("shopId") shopId: Long,
        @Param("status") status: ShopStatus,
    ): List<ShopProductEntity>
}
