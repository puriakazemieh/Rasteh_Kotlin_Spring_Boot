package com.kazemieh.rasteh.marketplace.onboarding

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.util.UUID

data class NearbyCluster(val id:UUID,val name:String,val type:String,val distanceMeters:Double)
/** Portable Haversine query; the `(lat,lng)` partial index narrows populated points. PostGIS may replace this query operationally without changing the API. */
@Service class GeoSearchService(private val jdbc:NamedParameterJdbcTemplate){
 fun nearby(lat:Double,lng:Double,radiusMeters:Double=5_000.0):List<NearbyCluster>{require(lat in -90.0..90.0&&lng in -180.0..180.0&&radiusMeters in 1.0..50_000.0);val sql="""select id,name,type, 6371000 * acos(least(1, greatest(-1, cos(radians(:lat))*cos(radians(lat))*cos(radians(lng)-radians(:lng))+sin(radians(:lat))*sin(radians(lat))))) as distance from geo_clusters where lat is not null and lng is not null order by distance limit 100""";return jdbc.query(sql,mapOf("lat" to lat,"lng" to lng)){rs,_->NearbyCluster(UUID.fromString(rs.getString("id")),rs.getString("name"),rs.getString("type"),rs.getDouble("distance"))}.filter{it.distanceMeters<=radiusMeters}}
}
