package com.github.factotum_sdp.factotum.data.localisation

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.annotation.RequiresApi
import java.util.concurrent.CountDownLatch

/**
 * Class that models a Location that has a name and an address
 *
 * @constructor : Constructs a Location
 *
 * @param query : String. Query of the address that we want to create
 * @param context : Context. Context in which this constructor is called
 */
class Location(query: String, context : Context) {

    val address : Address?
    val addressName : String?

    init {
        val geocoder = Geocoder(context)
        address = if (Build.VERSION.SDK_INT >= TIRAMISU) {
            tiramisuResultHandler(query, geocoder)
        } else{
            resultHandler(query, geocoder)
        }
        addressName = address?.getAddressLine(0)
    }

    @RequiresApi(TIRAMISU)
    private fun tiramisuResultHandler(query: String, geocoder: Geocoder): Address? {
        var result : Address? = null
        val latch = CountDownLatch(1)
        // blocking
        geocoder.getFromLocationName(query, 1) { addresses ->
            result = if(addresses.size != 0) addresses[0] else null
            latch.countDown()
        }
        latch.await()
        return result
    }

    private fun resultHandler(query: String, geocoder: Geocoder): Address? {
        var result : Address? = null
        try {
            result = geocoder.getFromLocationName(query, 1)?.get(0)
        } catch (e : Exception){
            e.printStackTrace()
        }
        return result
    }
}