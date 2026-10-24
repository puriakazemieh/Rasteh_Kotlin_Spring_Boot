package com.kazemieh.rasteh.canonical.api

import com.kazemieh.rasteh.canonical.application.*
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController @RequestMapping("/api/v1/offers") class OfferManagementController(private val offers:OfferWriteService,private val inventory:InventoryService){
 @PostMapping fun create(@RequestBody r:UpsertOfferRequest)=offers.create(r)
 @PutMapping("/{id}/confirm") fun confirm(@PathVariable id:UUID,@RequestHeader("If-Match") version:Long,@RequestParam onHand:Int,@RequestParam(required=false) price:Long?)=offers.confirm(id,version,onHand,price)
 @PostMapping("/{id}/reserve") fun reserve(@PathVariable id:UUID,@RequestParam quantity:Int)=mapOf("available" to inventory.reserve(id,quantity))
 @PostMapping("/{id}/release") fun release(@PathVariable id:UUID,@RequestParam quantity:Int)=mapOf("available" to inventory.release(id,quantity))
}
