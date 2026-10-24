package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.domain.SaleMode
import com.kazemieh.rasteh.canonical.persistence.ShopOfferRepository
import com.kazemieh.rasteh.shared.error.BadRequestException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service class InventoryService(private val offers:ShopOfferRepository){
 @Transactional fun reserve(offerId:UUID,quantity:Int):Int{require(quantity>0);val o=offers.findLockedById(offerId)?:throw BadRequestException("Offer not found","OFFER_NOT_FOUND");if(!o.active||o.stale||o.saleMode!=SaleMode.BUYABLE||o.onHand-o.reserved<quantity)throw BadRequestException("Insufficient fresh inventory","INVENTORY_UNAVAILABLE");o.reserved+=quantity;offers.save(o);return o.onHand-o.reserved}
 @Transactional fun release(offerId:UUID,quantity:Int):Int{require(quantity>0);val o=offers.findLockedById(offerId)?:throw BadRequestException("Offer not found","OFFER_NOT_FOUND");if(o.reserved<quantity)throw BadRequestException("Cannot release more than reserved","INVENTORY_INVALID_RELEASE");o.reserved-=quantity;offers.save(o);return o.onHand-o.reserved}
}
