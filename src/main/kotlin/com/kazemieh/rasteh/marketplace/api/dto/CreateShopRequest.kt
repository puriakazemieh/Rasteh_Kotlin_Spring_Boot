package com.kazemieh.rasteh.marketplace.api.dto

import com.kazemieh.rasteh.marketplace.domain.ShopType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/** ثبتِ درخواستِ فروشگاه (becomeVendor). فروشگاه با وضعیتِ PENDING ساخته می‌شود. */
data class CreateShopRequest(
    @field:NotBlank @field:Size(max = 180)
    val name: String,

    val rastehId: Long? = null,
    val locationId: Long? = null,

    @field:Size(max = 120)
    val category: String? = null,

    @field:Size(max = 40)
    val floor: String? = null,

    val type: ShopType = ShopType.BUYABLE,

    @field:Size(max = 30)
    val phone: String? = null,

    @field:Size(max = 255)
    val address: String? = null,

    val workingHoursJson: String? = null,

    val about: String? = null,

    val hasChat: Boolean = true,
    val acceptsOffers: Boolean = false,

    @field:Size(max = 16)
    val emoji: String? = null,

    @field:Size(max = 120)
    val coverStyle: String? = null,
)
