package com.github.factotum_sdp.factotum.models

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.util.concurrent.CountDownLatch

/**
 * Class that models a Location that has a name and an address
 *
 * @constructor : Constructs a Location with the best result
 *
 * @param query : String. Query of the address that we want to create
 * @param context : Context. Context in which this constructor is called
 */
class Location(query: String, context: Context) {

    val address: Address?
    val addressName: String?

    companion object {
        const val CACHE_FILE_NAME = "locations.txt"
        const val CACHE_FILE_SEPARATOR = "|"
        const val MAX_RESULT = 4

        /**
         * Creates a location and stores it in a file named CACHE_FILE_NAME in cache if an address was found.
         * Stored as : addressName CACHE_FILE_SEPARATOR latitude CACHE_FILE_SEPARATOR longitude
         *
         * @param query : String. Location to add
         * @param context : Context. Context in which this method is called
         * @return returns the location that has been added to the file, or null if no address was found
         */
        fun createAndStore(query: String, context: Context): Location? {
            val location = Location(query, context)
            if (location.address == null) {
                return null
            }

            val cacheFile = File(context.cacheDir, CACHE_FILE_NAME)
            cacheFile.deleteOnExit()
            //creates the header if it is a new file
            if (cacheFile.length() == 0L) cacheFile.appendText("location${CACHE_FILE_SEPARATOR}latitude${CACHE_FILE_SEPARATOR}longitude\n")
            // stores
            val toStore =
                "${location.addressName}$CACHE_FILE_SEPARATOR${location.address.latitude}$CACHE_FILE_SEPARATOR${location.address.longitude}\n"
            //checks if already if file
            var alreadyExists = false
            cacheFile.forEachLine { line ->
                if (line.contains(location.addressName.toString())) {
                    alreadyExists = true
                }
            }
            if (!alreadyExists) cacheFile.appendText(toStore)
            return location
        }

        /**
         * Blocking function that returns a list of addresses dependant on the query
         *
         * @param query : String. Address that we want to search
         * @param context : Context. Context in which this function is called
         * @return : List<Address>?. Null if no result
         */
        fun geocoderQuery(query: String, context: Context): List<Address>? {
            // must handle differently depending on SDK.
            // getLocationFromName(String, int) deprecated in SDK 33
            val geocoder = Geocoder(context)
            return if (Build.VERSION.SDK_INT >= TIRAMISU) {
                Log.d("test", "tiramisu")
                tiramisuResultHandler(query, geocoder)
            } else {
                Log.d("test", "not tiramisu")
                resultHandler(query, geocoder)
            }
        }

        @RequiresApi(TIRAMISU)
        private fun tiramisuResultHandler(query: String, geocoder: Geocoder): List<Address>? {
            var result: List<Address>? = listOf()
            val latch = CountDownLatch(1)
            Log.d("test", "countdown latch${latch.count}")
            // blocking
            Log.d("test", "before blocking string query $query")
            geocoder.getFromLocationName(query, MAX_RESULT) { addresses ->
                result = if (addresses.size > 0) addresses else null
                Log.d("test", "countdown latch${latch.count}")
                latch.countDown()
                Log.d("test", "countdown latch${latch.count}")
            }
            latch.await()
            return result
        }

        private fun resultHandler(query: String, geocoder: Geocoder): List<Address>? {
            var result: List<Address>? = listOf()
            try {
                val geocodeResult = geocoder.getFromLocationName(query, MAX_RESULT)
                result =
                    if (geocodeResult == null || geocodeResult.isEmpty()) null else geocodeResult
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }
    }

    init {
        address = geocoderQuery(query, context)?.get(0)
        addressName = address?.getAddressLine(0)
    }


}