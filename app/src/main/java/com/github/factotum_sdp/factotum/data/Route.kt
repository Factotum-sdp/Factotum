package com.github.factotum_sdp.factotum.data

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

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

    var src = LatLng(srcLat, srcLon)
    var dst = LatLng(dstLat, dstLon)
    var id = hashCode()

    constructor(src: LatLng, dst: LatLng) : this(src.latitude, src.longitude, dst.latitude, dst.longitude)
    /**
     * Returns the coordinates of the start and destination of the route
     *
     * @return string : sentence explaining where the route starts and ends
     */
    override fun toString(): String {
        return String.format("The route starts at coordinates (${src.latitude}, ${src.longitude}) and finishes at coordinates (${dst.latitude}, ${dst.longitude})")
    }

    /**
     * Adds a route to the map
     *
     *@param googleMap : map to which the route is added
     * @param src : boolean if we want to show the route source
     * @param dst : boolean if we want to show the route destination
     */
    fun addToMap(googleMap: GoogleMap, src: Boolean = true, dst: Boolean = true) {
        if (src){
            googleMap.addMarker(
                MarkerOptions()
                    .position(this.src)
                    .title("Start of $id")
            )

        }
        if(dst) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(this.dst)
                    .title("Destination of $id")
            )
        }
    }



}