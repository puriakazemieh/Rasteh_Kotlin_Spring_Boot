package com.kazemieh.rasteh.marketplace.onboarding

import com.kazemieh.rasteh.shared.error.BadRequestException
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.util.UUID

/** Enforces physical parent/child links before an application can reference them. */
@Component class GeoHierarchyValidator(private val jdbc:NamedParameterJdbcTemplate) {
    fun validate(clusterId: UUID?, buildingId: UUID?, unitId: UUID?) {
        if (buildingId != null && clusterId == null) fail("Building requires cluster")
        if (unitId != null && buildingId == null) fail("Unit requires building")
        if (buildingId != null && !exists("select 1 from buildings where id=:building and cluster_id=:cluster", mapOf("building" to buildingId,"cluster" to clusterId))) fail("Building is not in cluster")
        if (unitId != null && !exists("select 1 from indoor_units u join indoor_floors f on f.id=u.floor_id where u.id=:unit and f.building_id=:building", mapOf("unit" to unitId,"building" to buildingId))) fail("Unit is not in building")
    }
    private fun exists(sql:String, params:Map<String,Any?>)=jdbc.query(sql,params){_,_->1}.isNotEmpty()
    private fun fail(message:String):Nothing=throw BadRequestException(message,"INVALID_GEO_HIERARCHY")
}
