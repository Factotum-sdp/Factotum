package com.github.factotum_sdp.factotum.model

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

/**
 * Class that represents a route
 *
 * @constructor constructs a route object from the source latitude/longitude
 * and destination latitude/longitude
 *
 *
 * @param srcLat: Double. Latitude of the starting point
 * @param srcLon: Double. Longitude of the starting point
 * @param dstLat: Double. Latitude of the ending point
 * @param dstLon: Double. Longitude of the ending point
 */
class Route(srcLat: Double, srcLon: Double, dstLat: Double, dstLon: Double, private val name : String? = null) {

    val src = LatLng(srcLat, srcLon)
    val dst = LatLng(dstLat, dstLon)
    val id = hashCode()

    constructor(src: LatLng, dst: LatLng) : this(
        src.latitude,
        src.longitude,
        dst.latitude,
        dst.longitude
    )

    /**
     * Returns the coordinates of the start and destination of the route
     *
     * @return string : sentence explaining where the route starts and ends
     */
    override fun toString(): String {
        return "The route starts at coordinates (${src.latitude}, ${src.longitude}) and finishes at coordinates (${dst.latitude}, ${dst.longitude})"
    }

    /**
     * Adds a src to the map
     *
     *  @param googleMap : map to which the route is added
     */
    fun addSrcToMap(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(this.src)
                .title(name?.let { "Source of $name" } ?: "Source of $id")
        )
    }

    /**
     * Adds a dst to the map
     *
     *@param googleMap : map to which the route is added
     * @param src : boolean if we want to show the route source
     * @param dst : boolean if we want to show the route destination
     */
    fun addDstToMap(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(this.dst)
                .title(name?.let { "Destination of $name" } ?: "Destination of $id")
        )
    }

    /**
     * Draws the route on the map
     *
     * @param googleMap : map to which the route is added
     */
    fun drawRoute(googleMap: GoogleMap) {
        val polyline = googleMap.addPolyline(
            PolylineOptions()
                .add(src, dst)
                .width(15f)
                .color(Color.RED)
                .clickable(true)
        )
        polyline.tag = this
    }

}