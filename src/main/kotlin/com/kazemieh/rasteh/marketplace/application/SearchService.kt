package com.kazemieh.rasteh.marketplace.application

import com.kazemieh.rasteh.marketplace.api.dto.ProductResponse
import com.kazemieh.rasteh.marketplace.api.dto.ShopResponse
import com.kazemieh.rasteh.marketplace.api.mapper.ProductMapper
import com.kazemieh.rasteh.marketplace.api.mapper.ShopMapper
import com.kazemieh.rasteh.marketplace.domain.ProductCondition
import com.kazemieh.rasteh.marketplace.domain.ShopStatus
import com.kazemieh.rasteh.marketplace.domain.ShopType
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopEntity
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

/**
 * جست‌وجویِ فروشگاه/کالا با فیلترهایِ v2 (راسته، محل، دسته، وضعیت، قیمت، مرتب‌سازی).
 * صفحهٔ search. همیشه فقط موجودیت‌هایِ در دسترس (فروشگاهِ APPROVED، کالایِ active).
 */
@Service
class SearchService(
    private val shopRepository: ShopRepository,
    private val productRepository: ShopProductRepository,
) {

    @Transactional(readOnly = true)
    fun searchShops(
        query: String?,
        rastehId: Long?,
        locationId: Long?,
        type: String?,
        sort: String?,
    ): List<ShopResponse> {
        val spec = Specification<ShopEntity> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            predicates += cb.equal(root.get<ShopStatus>("status"), ShopStatus.APPROVED)
            if (!query.isNullOrBlank()) {
                predicates += cb.like(cb.lower(root.get("name")), "%${query.trim().lowercase()}%")
            }
            rastehId?.let { predicates += cb.equal(root.get<ShopEntity>("rasteh").get<Long>("id"), it) }
            locationId?.let { predicates += cb.equal(root.get<ShopEntity>("location").get<Long>("id"), it) }
            type?.let { runCatching { ShopType.valueOf(it) }.getOrNull()?.let { t -> predicates += cb.equal(root.get<ShopType>("type"), t) } }
            cb.and(*predicates.toTypedArray())
        }
        val sortSpec = when (sort) {
            "sales" -> Sort.by(Sort.Direction.DESC, "salesCount")
            "newest" -> Sort.by(Sort.Direction.DESC, "id")
            else -> Sort.by(Sort.Direction.DESC, "rating")
        }
        return shopRepository.findAll(spec, sortSpec).map(ShopMapper::toResponse)
    }

    @Transactional(readOnly = true)
    fun searchProducts(
        query: String?,
        shopId: Long?,
        locationId: Long?,
        condition: String?,
        minPrice: Long?,
        maxPrice: Long?,
        sort: String?,
    ): List<ProductResponse> {
        val spec = Specification<ShopProductEntity> { root, _, cb ->
            val predicates = mutableListOf<Predicate>()
            predicates += cb.isTrue(root.get("active"))
            predicates += cb.equal(root.get<ShopEntity>("shop").get<ShopStatus>("status"), ShopStatus.APPROVED)
            if (!query.isNullOrBlank()) {
                predicates += cb.like(cb.lower(root.get("name")), "%${query.trim().lowercase()}%")
            }
            shopId?.let { predicates += cb.equal(root.get<ShopEntity>("shop").get<Long>("id"), it) }
            locationId?.let { predicates += cb.equal(root.get<ShopEntity>("shop").get<Any>("location").get<Long>("id"), it) }
            condition?.let {
                runCatching { ProductCondition.valueOf(it) }.getOrNull()?.let { c ->
                    predicates += cb.equal(root.get<ProductCondition>("condition"), c)
                }
            }
            minPrice?.let { predicates += cb.greaterThanOrEqualTo(root.get("price"), BigDecimal.valueOf(it)) }
            maxPrice?.let { predicates += cb.lessThanOrEqualTo(root.get("price"), BigDecimal.valueOf(it)) }
            cb.and(*predicates.toTypedArray())
        }
        val sortSpec = when (sort) {
            "cheapest" -> Sort.by(Sort.Direction.ASC, "price")
            "expensive" -> Sort.by(Sort.Direction.DESC, "price")
            else -> Sort.by(Sort.Direction.DESC, "id")
        }
        return productRepository.findAll(spec, sortSpec).map(ProductMapper::toResponse)
    }
}
