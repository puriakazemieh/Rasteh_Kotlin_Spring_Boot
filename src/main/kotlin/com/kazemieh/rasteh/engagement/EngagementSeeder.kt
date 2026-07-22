package com.kazemieh.rasteh.engagement

import com.kazemieh.rasteh.engagement.entity.EventEntity
import com.kazemieh.rasteh.engagement.entity.LiveSessionEntity
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

/**
 * دادهٔ نمونهٔ رویدادها/لایو (events/live) — فقط وقتی `app.seed.enabled=true` و جدول خالی است.
 */
@Component
@ConditionalOnProperty(name = ["app.seed.enabled"], havingValue = "true")
class EngagementSeeder(
    private val eventRepository: EventRepository,
    private val liveSessionRepository: LiveSessionRepository,
) : CommandLineRunner {
    override fun run(vararg args: String) {
        if (eventRepository.count() == 0L) {
            val now = OffsetDateTime.now()
            eventRepository.saveAll(
                listOf(
                    EventEntity(title = "جشنوارهٔ فروشِ پاییزه", description = "تخفیف‌های ویژهٔ فروشگاه‌های راسته تا پایانِ هفته.", eventDate = now.plusDays(3)),
                    EventEntity(title = "نمایشگاهِ صنایع‌دستی", description = "معرفیِ آثارِ هنرمندانِ محلی در پاساژ.", eventDate = now.plusDays(10)),
                    EventEntity(title = "روزِ بازارچهٔ کتاب", description = "امضای کتاب و تخفیفِ ویژهٔ کتاب‌فروشی‌ها.", eventDate = now.plusDays(21)),
                )
            )
        }
        if (liveSessionRepository.count() == 0L) {
            liveSessionRepository.saveAll(
                listOf(
                    LiveSessionEntity(shopName = "موبایل‌سِرا", title = "معرفیِ گوشی‌های جدید 📱", viewerCount = 128),
                    LiveSessionEntity(shopName = "طلای درخشان", title = "حراجِ زندهٔ طلا و جواهر ✨", viewerCount = 74),
                )
            )
        }
    }
}
