package com.kazemieh.rasteh.canonical.api

import com.kazemieh.rasteh.canonical.application.*
import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController @RequestMapping("/api/v1/offers") class OfferManagementController(private val offers:OfferWriteService,private val inventory:InventoryService){
 @PostMapping fun create(@AuthenticationPrincipal p:UserPrincipal,@RequestBody r:UpsertOfferRequest)=offers.create(p.id,r)
 @PutMapping("/{id}/confirm") fun confirm(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID,@RequestHeader("If-Match") version:Long,@RequestParam onHand:Int,@RequestParam(required=false) price:Long?)=offers.confirm(p.id,id,version,onHand,price)
 @PostMapping("/{id}/reserve") fun reserve(@PathVariable id:UUID,@RequestParam quantity:Int,@RequestHeader("Idempotency-Key") key:String)=inventory.reserve(id,quantity,key)
 @PostMapping("/{id}/release") fun release(@PathVariable id:UUID,@RequestParam quantity:Int,@RequestHeader("Idempotency-Key") key:String)=mapOf("available" to inventory.release(id,quantity,key))
 @PostMapping("/{id}/adjust") fun adjust(@PathVariable id:UUID,@RequestParam delta:Int,@RequestHeader("Idempotency-Key") key:String)=mapOf("available" to inventory.adjust(id,delta,key))
 @PostMapping("/bulk") fun bulk(@AuthenticationPrincipal p:UserPrincipal,@RequestBody requests:List<UpsertOfferRequest>)=requests.mapIndexed { index, request -> runCatching { offers.create(p.id,request) }.fold({ mapOf("row" to index,"offer" to it,"error" to null) },{ mapOf("row" to index,"offer" to null,"error" to (it.message?:"INVALID_ROW")) }) }
}
