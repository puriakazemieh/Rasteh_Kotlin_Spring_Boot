package com.kazemieh.rasteh.marketplace.application

import com.kazemieh.rasteh.marketplace.domain.LocationKind
import com.kazemieh.rasteh.marketplace.persistence.CityRepository
import com.kazemieh.rasteh.marketplace.persistence.LocationRepository
import com.kazemieh.rasteh.marketplace.persistence.RastehRepository
import com.kazemieh.rasteh.marketplace.persistence.entity.CityEntity
import com.kazemieh.rasteh.marketplace.persistence.entity.LocationEntity
import com.kazemieh.rasteh.marketplace.persistence.entity.RastehEntity
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * دادهٔ مرجعِ بازارچه (راسته‌ها/شهر/محل‌ها) از روی نگاشتِ بستهٔ طراحیِ v2.
 * فقط وقتی اجرا می‌شود که `app.seed.enabled=true` و جدولِ راسته‌ها خالی باشد.
 */
@Component
@Order(1)
@ConditionalOnProperty(name = ["app.seed.enabled"], havingValue = "true")
class MarketplaceSeeder(
    private val cityRepository: CityRepository,
    private val rastehRepository: RastehRepository,
    private val locationRepository: LocationRepository,
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(MarketplaceSeeder::class.java)

    // راسته → (رنگ oklch، کلیدِ آیکون، محل‌ها)
    private data class RastehSpec(val label: String, val color: String, val icon: String, val locations: List<String>)

    private val specs = listOf(
        RastehSpec("مبل", "oklch(0.55 0.13 45)", "sofa", listOf("یافت‌آباد", "حسن‌آباد", "مبل ایران")),
        RastehSpec("موبایل", "oklch(0.5 0.14 260)", "mobile", listOf("پاساژ علاءالدین", "چارسو", "پاساژ آسیا")),
        RastehSpec("پوشاک", "oklch(0.55 0.15 350)", "clothing", listOf("بازار بزرگ", "پالادیوم", "مجتمع کوروش")),
        RastehSpec("طلا", "oklch(0.62 0.13 85)", "gold", listOf("بازار طلای تهران", "میدان هفت‌تیر", "گلستان")),
        RastehSpec("لوازم خانگی", "oklch(0.55 0.12 200)", "appliances", listOf("امین‌حضور", "چراغ برق", "جمهوری")),
        RastehSpec("کیف و کفش", "oklch(0.5 0.13 30)", "bag", listOf("بازار کفش", "پاساژ ملت", "مجتمع پروما")),
        RastehSpec("آرایشی", "oklch(0.58 0.15 350)", "cosmetics", listOf("پاساژ الماس", "بازار ریحان", "مجتمع کوروش")),
        RastehSpec("کتاب و لوازم", "oklch(0.55 0.11 150)", "book", listOf("انقلاب", "کریم‌خان", "فلسطین")),
        RastehSpec("اسباب‌بازی", "oklch(0.6 0.14 60)", "toy", listOf("پاساژ کودک", "میدان خراسان", "بهار")),
    )

    @Transactional
    override fun run(vararg args: String) {
        if (rastehRepository.count() > 0L) {
            log.info("[MarketplaceSeeder] راسته‌ها از قبل وجود دارند؛ صرف‌نظر شد.")
            return
        }
        log.info("[MarketplaceSeeder] تولیدِ دادهٔ مرجعِ بازارچه…")

        val city = cityRepository.save(CityEntity(name = "تهران", province = "تهران", isActive = true))

        // محل‌های یکتا (dedup by name) — برخی محل‌ها بین چند راسته مشترک‌اند.
        val locByName = mutableMapOf<String, LocationEntity>()
        fun location(name: String): LocationEntity = locByName.getOrPut(name) {
            locationRepository.save(LocationEntity(city = city, name = name, kind = LocationKind.PASSAGE))
        }

        val allLocations = linkedSetOf<LocationEntity>()
        specs.forEachIndexed { i, spec ->
            val locs = spec.locations.map(::location)
            allLocations.addAll(locs)
            rastehRepository.save(
                RastehEntity(
                    label = spec.label,
                    colorOklch = spec.color,
                    iconKey = spec.icon,
                    sortOrder = i,
                    locations = locs.toMutableSet(),
                )
            )
        }

        // راستهٔ «همه» → مجموعهٔ همهٔ محل‌ها.
        rastehRepository.save(
            RastehEntity(
                label = "همه",
                colorOklch = "oklch(0.5 0.02 280)",
                iconKey = "all",
                sortOrder = specs.size,
                locations = allLocations.toMutableSet(),
            )
        )

        log.info("[MarketplaceSeeder] پایان. ${specs.size + 1} راسته و ${locByName.size} محل ساخته شد.")
    }
}
