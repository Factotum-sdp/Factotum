package com.github.factotum_sdp.factotum.data.localisation

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.annotation.RequiresApi
import java.io.File
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

    companion object {
        const val CACHE_FILE_NAME = "locations.txt"
        const val CACHE_FILE_SEPARATOR = "|"

        /**
         * Creates a location and stores it in a file named CACHE_FILE_NAME in cache if an address was found.
         * Stored as : addressName CACHE_FILE_SEPARATOR latitude CACHE_FILE_SEPARATOR longitude
         *
         * @param query : String. Location to add
         * @param context : Context. Context in which this method is called
         * @return returns the location that has been added to the file, or null if no address was found
         */
        fun createAndStore(query: String, context: Context): Location?{
            val location = Location(query, context)
            if(location.address == null){
                return null
            }

            val cacheFile = File(context.cacheDir, CACHE_FILE_NAME)
            cacheFile.deleteOnExit()
            //creates the header if it is a new file
            if (cacheFile.length() == 0L) cacheFile.appendText("location${CACHE_FILE_SEPARATOR}latitude${CACHE_FILE_SEPARATOR}longitude\n")
            // stores
            val toStore = "${location.addressName}$CACHE_FILE_SEPARATOR${location.address.latitude}$CACHE_FILE_SEPARATOR${location.address.longitude}\n"
            //checks if already if file
            var alreadyExists = false
            cacheFile.forEachLine { line ->
                if(line.contains(location.addressName.toString())) {
                    alreadyExists = true
                }
            }
            if(!alreadyExists) cacheFile.appendText(toStore)
            return location
        }
    }

    val address : Address?
    val addressName : String?

    init {
        val geocoder = Geocoder(context)
        // must handle differently depending on SDK.
        // getLocationFromName(String, int) deprecated in SDK 33
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