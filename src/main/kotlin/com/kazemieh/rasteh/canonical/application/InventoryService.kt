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
 @Transactional fun adjust(offerId:UUID,delta:Int,idempotencyKey:String):Int { if(delta==0)throw BadRequestException("Adjustment cannot be zero","INVENTORY_INVALID_ADJUSTMENT");ledger.findByOfferIdAndIdempotencyKey(offerId,idempotencyKey)?.let{return offers.findById(offerId).orElseThrow().let{it.onHand-it.reserved}};val o=offers.findLockedById(offerId)?:throw BadRequestException("Offer not found","OFFER_NOT_FOUND");if(o.onHand+delta<o.reserved)throw BadRequestException("Adjustment would make available inventory negative","INVENTORY_NEGATIVE");o.onHand+=delta;offers.save(o);ledger.save(InventoryLedgerEntity(offerId=offerId,deltaOnHand=delta,reason="ADJUSTMENT",idempotencyKey=idempotencyKey));return o.onHand-o.reserved }
 @Transactional fun reserve(offerId:UUID,quantity:Int,idempotencyKey:String):OfferReservationEntity{require(quantity>0);reservations.findByIdempotencyKey(idempotencyKey)?.let{return it};val o=offers.findLockedById(offerId)?:throw BadRequestException("Offer not found","OFFER_NOT_FOUND");if(!o.active||o.stale||o.saleMode!=SaleMode.BUYABLE||o.onHand-o.reserved<quantity)throw BadRequestException("Insufficient fresh inventory","INVENTORY_UNAVAILABLE");o.reserved+=quantity;offers.save(o);ledger.save(InventoryLedgerEntity(offerId=offerId,deltaReserved=quantity,reason="RESERVE",idempotencyKey=idempotencyKey));return reservations.save(OfferReservationEntity(offerId=offerId,quantity=quantity,expiresAt=java.time.OffsetDateTime.now().plusMinutes(15),idempotencyKey=idempotencyKey))}
 @Transactional fun release(offerId:UUID,quantity:Int,idempotencyKey:String):Int{require(quantity>0);if(ledger.findByOfferIdAndIdempotencyKey(offerId,idempotencyKey)!=null)return offers.findById(offerId).orElseThrow().let{it.onHand-it.reserved};val o=offers.findLockedById(offerId)?:throw BadRequestException("Offer not found","OFFER_NOT_FOUND");if(o.reserved<quantity)throw BadRequestException("Cannot release more than reserved","INVENTORY_INVALID_RELEASE");o.reserved-=quantity;offers.save(o);ledger.save(InventoryLedgerEntity(offerId=offerId,deltaReserved=-quantity,reason="RELEASE",idempotencyKey=idempotencyKey));return o.onHand-o.reserved}
 @Transactional fun expireReservations(now:java.time.OffsetDateTime=java.time.OffsetDateTime.now()):Int { val expired=reservations.findAllByStatusAndExpiresAtBefore("ACTIVE",now);expired.forEach { r -> val o=offers.findLockedById(r.offerId)?:return@forEach;o.reserved-=r.quantity;o.reserved=o.reserved.coerceAtLeast(0);offers.save(o);ledger.save(InventoryLedgerEntity(offerId=r.offerId,deltaReserved=-r.quantity,reason="EXPIRED",idempotencyKey="expiry:${r.id}"));r.status="EXPIRED" };reservations.saveAll(expired);return expired.size }
}
