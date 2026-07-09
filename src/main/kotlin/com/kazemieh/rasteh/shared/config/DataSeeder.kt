package com.kazemieh.rasteh.shared.config

import com.kazemieh.rasteh.identity.domain.UserRole
import com.kazemieh.rasteh.identity.persistence.UserRepository
import com.kazemieh.rasteh.identity.persistence.entity.UserEntity
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * داده‌ی نمونه‌ی فازِ ۰: فقط سه حسابِ نقش‌محور برای تستِ احراز هویت و RBAC.
 *
 * فقط وقتی اجرا می‌شود که `app.seed.enabled=true` باشد و جدولِ کاربران خالی باشد؛
 * بنابراین اجرای دوباره داده‌ی تکراری نمی‌سازد.
 *
 * حساب‌های ساخته‌شده (رمز برای همه: `pass1234`):
 *   ادمینِ پاساژ → موبایل 09120000000
 *   فروشنده      → موبایل 09122222222
 *   خریدار       → موبایل 09121111111
 */
@Component
@ConditionalOnProperty(name = ["app.seed.enabled"], havingValue = "true")
class DataSeeder(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(DataSeeder::class.java)

    @Transactional
    override fun run(vararg args: String) {
        if (userRepository.count() > 0L) {
            log.info("[DataSeeder] کاربران از قبل وجود دارند؛ از تولید داده‌ی نمونه صرف‌نظر شد.")
            return
        }
        log.info("[DataSeeder] شروع تولید حساب‌های نمونه‌ی راسته…")

        userRepository.save(
            UserEntity(
                email = "admin@rasteh.test",
                passwordHash = passwordEncoder.encode("pass1234")!!,
                firstName = "مدیر",
                lastName = "پاساژ",
                city = "تهران",
                phone = "09120000000",
                role = UserRole.ADMIN,
                isActive = true,
            )
        )
        userRepository.save(
            UserEntity(
                email = "vendor@rasteh.test",
                passwordHash = passwordEncoder.encode("pass1234")!!,
                firstName = "فروشندهٔ",
                lastName = "نمونه",
                city = "تهران",
                phone = "09122222222",
                role = UserRole.VENDOR,
                isActive = true,
            )
        )
        userRepository.save(
            UserEntity(
                email = "customer@rasteh.test",
                passwordHash = passwordEncoder.encode("pass1234")!!,
                firstName = "خریدارِ",
                lastName = "نمونه",
                city = "تهران",
                phone = "09121111111",
                role = UserRole.CUSTOMER,
                isActive = true,
            )
        )

        log.info("[DataSeeder] پایان. ۳ حسابِ نقش‌محور ساخته شد.")
    }
}
