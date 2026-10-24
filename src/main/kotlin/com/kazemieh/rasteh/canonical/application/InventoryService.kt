package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.domain.SaleMode
import com.kazemieh.rasteh.canonical.persistence.ShopOfferRepository
import com.kazemieh.rasteh.canonical.persistence.InventoryLedgerRepository
import com.kazemieh.rasteh.canonical.persistence.OfferReservationRepository
import com.kazemieh.rasteh.canonical.persistence.entity.InventoryLedgerEntity
import com.kazemieh.rasteh.canonical.persistence.entity.OfferReservationEntity
import com.kazemieh.rasteh.shared.error.BadRequestException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service class InventoryService(private val offers:ShopOfferRepository,private val ledger:InventoryLedgerRepository,private val reservations:OfferReservationRepository){
 @Transactional fun reserve(offerId:UUID,quantity:Int,idempotencyKey:String):OfferReservationEntity{require(quantity>0);reservations.findByIdempotencyKey(idempotencyKey)?.let{return it};val o=offers.findLockedById(offerId)?:throw BadRequestException("Offer not found","OFFER_NOT_FOUND");if(!o.active||o.stale||o.saleMode!=SaleMode.BUYABLE||o.onHand-o.reserved<quantity)throw BadRequestException("Insufficient fresh inventory","INVENTORY_UNAVAILABLE");o.reserved+=quantity;offers.save(o);ledger.save(InventoryLedgerEntity(offerId=offerId,deltaReserved=quantity,reason="RESERVE",idempotencyKey=idempotencyKey));return reservations.save(OfferReservationEntity(offerId=offerId,quantity=quantity,expiresAt=java.time.OffsetDateTime.now().plusMinutes(15),idempotencyKey=idempotencyKey))}
 @Transactional fun release(offerId:UUID,quantity:Int,idempotencyKey:String):Int{require(quantity>0);if(ledger.findByOfferIdAndIdempotencyKey(offerId,idempotencyKey)!=null)return offers.findById(offerId).orElseThrow().let{it.onHand-it.reserved};val o=offers.findLockedById(offerId)?:throw BadRequestException("Offer not found","OFFER_NOT_FOUND");if(o.reserved<quantity)throw BadRequestException("Cannot release more than reserved","INVENTORY_INVALID_RELEASE");o.reserved-=quantity;offers.save(o);ledger.save(InventoryLedgerEntity(offerId=offerId,deltaReserved=-quantity,reason="RELEASE",idempotencyKey=idempotencyKey));return o.onHand-o.reserved}
}
