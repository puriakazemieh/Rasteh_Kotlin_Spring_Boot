package com.kazemieh.rasteh.canonical.api

import com.kazemieh.rasteh.canonical.application.*
import com.kazemieh.rasteh.shared.security.UserPrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

data class CsvImportRequest(val csv:String)

@RestController @RequestMapping("/api/v1/offers") class OfferManagementController(private val offers:OfferWriteService,private val inventory:InventoryService){
 @PostMapping fun create(@AuthenticationPrincipal p:UserPrincipal,@RequestBody r:UpsertOfferRequest)=offers.create(p.id,r)
 @PutMapping("/{id}/confirm") fun confirm(@AuthenticationPrincipal p:UserPrincipal,@PathVariable id:UUID,@RequestHeader("If-Match") version:Long,@RequestParam onHand:Int,@RequestParam(required=false) price:Long?)=offers.confirm(p.id,id,version,onHand,price)
 @PostMapping("/{id}/reserve") fun reserve(@PathVariable id:UUID,@RequestParam quantity:Int,@RequestHeader("Idempotency-Key") key:String)=inventory.reserve(id,quantity,key)
 @PostMapping("/{id}/release") fun release(@PathVariable id:UUID,@RequestParam quantity:Int,@RequestHeader("Idempotency-Key") key:String)=mapOf("available" to inventory.release(id,quantity,key))
 @PostMapping("/{id}/adjust") fun adjust(@PathVariable id:UUID,@RequestParam delta:Int,@RequestHeader("Idempotency-Key") key:String)=mapOf("available" to inventory.adjust(id,delta,key))
 @PostMapping("/bulk") fun bulk(@AuthenticationPrincipal p:UserPrincipal,@RequestBody requests:List<UpsertOfferRequest>)=requests.mapIndexed { index, request -> runCatching { offers.create(p.id,request) }.fold({ mapOf("row" to index,"offer" to it,"error" to null) },{ mapOf("row" to index,"offer" to null,"error" to (it.message?:"INVALID_ROW")) }) }
 @PostMapping("/import-csv") fun importCsv(@AuthenticationPrincipal p:UserPrincipal,@RequestBody request:CsvImportRequest):List<Map<String,Any?>> { val lines=request.csv.lineSequence().filter{it.isNotBlank()}.toList();if(lines.isEmpty())return emptyList();val header=lines.first().split(',').map{it.trim()};return lines.drop(1).mapIndexed { offset,line -> runCatching { val values=line.split(',').map{it.trim()}; val row=header.zip(values).toMap(); offers.create(p.id,UpsertOfferRequest(UUID.fromString(row.getValue("productId")),UUID.fromString(row.getValue("shopLocationId")),row["variantId"]?.takeIf{it.isNotBlank()}?.let(UUID::fromString),com.kazemieh.rasteh.canonical.domain.SaleMode.valueOf(row.getValue("mode")),row["priceMinorUnits"]?.takeIf{it.isNotBlank()}?.toLong(),row["onHand"]?.toIntOrNull()?:0,row["fulfillment"]?.split('|')?.filter{it.isNotBlank()}?:emptyList(),row["freshnessMinutes"]?.toLongOrNull()?:1_440)) }.fold({mapOf("row" to offset+2,"offer" to it,"error" to null)},{mapOf("row" to offset+2,"offer" to null,"error" to (it.message?:"INVALID_ROW"))}) } }
}
