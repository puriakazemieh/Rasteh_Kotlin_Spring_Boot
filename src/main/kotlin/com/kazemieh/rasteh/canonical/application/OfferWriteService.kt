package com.kazemieh.rasteh.canonical.application

import com.kazemieh.rasteh.canonical.domain.SaleMode
import com.kazemieh.rasteh.canonical.persistence.ShopOfferRepository
import com.kazemieh.rasteh.canonical.persistence.entity.ShopOfferEntity
import com.kazemieh.rasteh.shared.error.BadRequestException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

data class UpsertOfferRequest(val productId:UUID,val shopLocationId:UUID,val variantId:UUID?=null,val mode:SaleMode,val priceMinorUnits:Long?=null,val onHand:Int=0,val fulfillment:List<String> = emptyList(),val freshnessMinutes:Long=1_440)
data class OfferMutationResponse(val id:UUID,val version:Long,val stale:Boolean,val available:Int)
@Service class OfferWriteService(private val offers:ShopOfferRepository){
 @Transactional fun create(r:UpsertOfferRequest):OfferMutationResponse { validate(r);val now=OffsetDateTime.now();val offer=ShopOfferEntity(productId=r.productId,shopLocationId=r.shopLocationId,variantId=r.variantId,saleMode=r.mode,priceMinorUnits=r.priceMinorUnits,onHand=r.onHand,lastConfirmedAt=now,expiresAt=now.plusMinutes(r.freshnessMinutes),active=true);return saved(offers.save(offer)) }
 @Transactional fun confirm(id:UUID,version:Long,onHand:Int,price:Long?):OfferMutationResponse{val o=offers.findById(id).orElseThrow{BadRequestException("Offer not found","OFFER_NOT_FOUND")};if(o.version!=version)throw BadRequestException("Offer version conflict","OFFER_ETAG_CONFLICT");val r=UpsertOfferRequest(o.productId,o.shopLocationId,o.variantId,o.saleMode,price,onHand,freshnessMinutes=1_440);validate(r);o.onHand=onHand;o.priceMinorUnits=price;o.lastConfirmedAt=OffsetDateTime.now();o.expiresAt=o.lastConfirmedAt!!.plusMinutes(1_440);o.stale=false;return saved(offers.save(o))}
 @Transactional fun expire(now:OffsetDateTime=OffsetDateTime.now()):Int { val all=offers.findAll().filter{it.active&&!it.stale&&it.expiresAt?.isBefore(now)==true};all.forEach{it.stale=true};offers.saveAll(all);return all.size }
 private fun validate(r:UpsertOfferRequest){if(r.onHand<0||r.freshnessMinutes !in 1..43_200)throw BadRequestException("Invalid inventory or freshness","OFFER_INVALID");when(r.mode){SaleMode.SHOWCASE->if(r.priceMinorUnits!=null)throw BadRequestException("Showcase has no price","OFFER_MODE_INVALID");SaleMode.RESERVABLE->if(r.fulfillment.isEmpty())throw BadRequestException("Reservable requires availability channel","OFFER_MODE_INVALID");SaleMode.BUYABLE->if(r.priceMinorUnits==null||r.priceMinorUnits<0||r.fulfillment.isEmpty())throw BadRequestException("Buyable requires exact price and fulfillment","OFFER_MODE_INVALID")}}
 private fun saved(o:ShopOfferEntity)=OfferMutationResponse(o.id,o.version,o.stale,o.onHand-o.reserved)
}
