package com.github.factotum_sdp.factotum.models

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import java.io.File

/**
 * Class that models a Location that has a name and an address
 *
 * @constructor : Constructs a Location with the best result
 *
 * @param query : String. Query of the address that we want to create
 * @param context : Context. Context in which this constructor is called
 */
class Location {

    val coordinates: LatLng?
    val addressName: String?
    constructor(query: String, context: Context){
        val address = geocoderQuery(query, context)?.get(0)
        coordinates = address?.let { LatLng(it.latitude, it.longitude) }
        addressName = address?.getAddressLine(0)
    }

    constructor(){
        coordinates = null
        addressName = null
    }


    companion object {
        const val CACHE_FILE_NAME = "locations.txt"
        const val CACHE_FILE_SEPARATOR = "|"
        const val MAX_RESULT = 4
        private val locationDbRef = FirebaseDatabase.getInstance().reference.child("locations")

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
            if (location.coordinates == null) {
                return null
            }
            val cacheFile = File(context.cacheDir, CACHE_FILE_NAME)
            cacheFile.deleteOnExit()
            //creates the header if it is a new file
            if (cacheFile.length() == 0L) cacheFile.appendText("location${CACHE_FILE_SEPARATOR}latitude${CACHE_FILE_SEPARATOR}longitude\n")
            // stores
            val toStore =
                "${location.addressName}$CACHE_FILE_SEPARATOR${location.coordinates.latitude}$CACHE_FILE_SEPARATOR${location.coordinates.longitude}\n"
            //checks if already if file
            var alreadyExists = false
            cacheFile.forEachLine { line ->
                if (line.contains(location.addressName.toString())) {
                    alreadyExists = true
                }
            }
            if (!alreadyExists){
                cacheFile.appendText(toStore)
            }
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
                tiramisuResultHandler(query, geocoder)
            } else {
                resultHandler(query, geocoder)
            }
        }

        @RequiresApi(TIRAMISU)
        private fun tiramisuResultHandler(query: String, geocoder: Geocoder): List<Address>? {
            var result: List<Address>? = listOf()
            geocoder.getFromLocationName(query, MAX_RESULT) { addresses ->
                result = if (addresses.size > 0) addresses else null
            }
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

}