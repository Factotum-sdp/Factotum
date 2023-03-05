package com.github.factotum_sdp.factotum

import com.google.android.gms.maps.model.LatLng

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
class Route (srcLat: Double, srcLon: Double, dstLat: Double, dstLon: Double) {

    val src = LatLng(srcLat, srcLon)
    val dst = LatLng(dstLat, dstLon)

    override fun toString(): String {
        return String.format("The route starts at coordinates %s and finishes at coordinates %s")
    }


}