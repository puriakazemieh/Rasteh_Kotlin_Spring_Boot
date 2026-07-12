package com.kazemieh.rasteh.marketplace.application

import com.kazemieh.rasteh.identity.domain.UserRole
import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.identity.persistence.entity.UserEntity
import com.kazemieh.rasteh.marketplace.domain.ProductCondition
import com.kazemieh.rasteh.marketplace.domain.ShopStatus
import com.kazemieh.rasteh.marketplace.domain.ShopType
import com.kazemieh.rasteh.marketplace.persistence.LocationRepository
import com.kazemieh.rasteh.marketplace.persistence.RastehRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopProductRepository
import com.kazemieh.rasteh.marketplace.persistence.ShopRepository
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopEntity
import com.kazemieh.rasteh.marketplace.persistence.entity.ShopProductEntity
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.annotation.Order
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.OffsetDateTime

/**
 * فروشگاه‌ها و کالاهایِ نمونهٔ تأییدشده تا کلاینت (rastehSearch/کاتالوگ) داده برای نمایش داشته باشد.
 * پس از MarketplaceSeeder (Order(1)) اجرا می‌شود و فقط وقتی هیچ فروشگاهی وجود ندارد.
 * حسابِ فروشندهٔ نمونه: موبایل 09122222222 · رمز vendor1234.
 */
@Component
@Order(2)
@ConditionalOnProperty(name = ["app.seed.enabled"], havingValue = "true")
class MarketplaceShopSeeder(
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    private val rastehRepository: RastehRepository,
    private val shopRepository: ShopRepository,
    private val productRepository: ShopProductRepository,
    private val passwordEncoder: PasswordEncoder,
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(MarketplaceShopSeeder::class.java)

    @Transactional
    override fun run(vararg args: String) {
        if (shopRepository.count() > 0L) {
            log.info("[MarketplaceShopSeeder] فروشگاه‌ها از قبل وجود دارند؛ صرف‌نظر شد.")
            return
        }
        val location = locationRepository.findAll().firstOrNull() ?: run {
            log.warn("[MarketplaceShopSeeder] محلی یافت نشد؛ صرف‌نظر شد.")
            return
        }
        val rasteh = rastehRepository.findAll().firstOrNull { it.label != "همه" }

        val vendor = userRepository.findByPhone("09122222222") ?: userRepository.save(
            UserEntity(
                email = "vendor@rasteh.test",
                passwordHash = passwordEncoder.encode("vendor1234")!!,
                firstName = "کریم",
                lastName = "فروشنده",
                city = "تهران",
                phone = "09122222222",
                role = UserRole.VENDOR,
                isActive = true,
            )
        )

        data class ShopSpec(
            val name: String,
            val type: ShopType,
            val emoji: String,
            val acceptsOffers: Boolean,
            val products: List<Triple<String, Long, Int>>, // name, price, stock
        )

        val specs = listOf(
            ShopSpec(
                "گالریِ نور", ShopType.BUYABLE, "💡", true,
                listOf(
                    Triple("لوسترِ کریستال", 4_850_000L, 5),
                    Triple("آباژورِ رومیزی", 1_200_000L, 12),
                    Triple("چراغِ دیواری", 780_000L, 20),
                ),
            ),
            ShopSpec(
                "مبلمانِ آرام", ShopType.BUYABLE, "🛋️", true,
                listOf(
                    Triple("مبلِ راحتیِ سه‌نفره", 28_500_000L, 2),
                    Triple("میزِ جلومبلی", 3_400_000L, 8),
                ),
            ),
            ShopSpec(
                "نمایشگاهِ سنتی", ShopType.VISIT_ONLY, "🏺", false,
                listOf(
                    Triple("فرشِ دستبافِ نمونه", 0L, 0),
                    Triple("ظروفِ مسیِ نمونه", 0L, 0),
                ),
            ),
        )

        specs.forEachIndexed { i, spec ->
            val shop = shopRepository.save(
                ShopEntity(
                    owner = vendor,
                    location = location,
                    rasteh = rasteh,
                    name = spec.name,
                    type = spec.type,
                    verified = i == 0,
                    rating = BigDecimal.valueOf(45L - i * 3, 1), // 4.5, 4.2, 3.9
                    reviewsCount = (30 - i * 7).coerceAtLeast(0),
                    salesCount = (120 - i * 40).coerceAtLeast(0),
                    phone = "021${1000_0000 + i}",
                    hasChat = true,
                    acceptsOffers = spec.acceptsOffers,
                    about = "فروشگاهِ نمونهٔ «${spec.name}» برای تستِ کاتالوگ و جست‌وجو.",
                    emoji = spec.emoji,
                    floor = "همکف",
                    status = ShopStatus.APPROVED,
                    approvedAt = OffsetDateTime.now(),
                )
            )
            spec.products.forEach { (name, price, stock) ->
                productRepository.save(
                    ShopProductEntity(
                        shop = shop,
                        name = name,
                        price = BigDecimal.valueOf(price),
                        oldPrice = if (price > 0) BigDecimal.valueOf((price * 1.15).toLong()) else null,
                        discountPercent = if (price > 0) 13 else null,
                        condition = ProductCondition.NEW,
                        stock = stock,
                        emoji = spec.emoji,
                        active = true,
                    )
                )
            }
        }

        log.info("[MarketplaceShopSeeder] ${specs.size} فروشگاهِ نمونه با کالا ساخته شد.")
    }
}
