package com.github.factotum_sdp.factotum.models

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.util.concurrent.CompletableFuture


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
        val location = createAndStore(query, context)
        coordinates = location?.coordinates
        addressName = location?.addressName ?: query
    }

    constructor(addressName: String, coordinates: LatLng?){
        this.addressName = addressName
        this.coordinates = coordinates
    }

    constructor(){
        coordinates = null
        addressName = null
    }


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
         * @return returns the location that has been added to the file. If no address was found, returns a Location with null coordinates
         */
        private fun createAndStore(query: String, context: Context): Location? {
            val locations = geocoderQuery(query, context) ?: return Location(query, null)
            val bestLocation = locations[0]
            val cacheFile = File(context.cacheDir, CACHE_FILE_NAME)
            cacheFile.deleteOnExit()
            //creates the header if it is a new file
            if (cacheFile.length() == 0L) cacheFile.appendText("location${CACHE_FILE_SEPARATOR}latitude${CACHE_FILE_SEPARATOR}longitude\n")
            // stores
            val toStore =
                "${bestLocation.addressName}$CACHE_FILE_SEPARATOR${bestLocation.coordinates!!.latitude}$CACHE_FILE_SEPARATOR${bestLocation.coordinates.longitude}\n"
            //checks if already if file
            var alreadyExists = false
            cacheFile.forEachLine { line ->
                if (line.contains(bestLocation.addressName.toString())) {
                    alreadyExists = true
                }
            }
            if (!alreadyExists){
                cacheFile.appendText(toStore)
            }
            return bestLocation
        }

        /**
         * Blocking function that returns a list of addresses dependant on the query
         *
         * @param query : String. Address that we want to search
         * @param context : Context. Context in which this function is called
         * @return : List<Address>?. Null if no result
         */
        fun geocoderQuery(query: String, context: Context): List<Location>? {
            // must handle differently depending on SDK.
            // getLocationFromName(String, int) deprecated in SDK 33
            if(query.length < 2) return null
            val geocoder = Geocoder(context)
            if (!isNetworkAvailable(context)) {
                return searchInCache(query, context)?.let { listOf(it) }
            }
            return if (Build.VERSION.SDK_INT >= TIRAMISU) {
                tiramisuResultHandler(query, geocoder)
            } else {
                resultHandler(query, geocoder)
            }
        }

        private fun searchInCache(query: String, context: Context): Location?{
            val cacheFile = File(context.cacheDir, CACHE_FILE_NAME)
            var location : Location? = null
            cacheFile.forEachLine { line ->
                if (line.contains(query)) {
                    val split = line.split(CACHE_FILE_SEPARATOR)
                    location = Location(split[0], LatLng(split[1].toDouble(), split[2].toDouble()))
                }
            }
            return location
        }

        @RequiresApi(TIRAMISU)
        private fun tiramisuResultHandler(query: String, geocoder: Geocoder): List<Location>? {
            val result = CompletableFuture<List<Location>?>()
            geocoder.getFromLocationName(query, MAX_RESULT) { addresses ->
                val queryResults = if (addresses.size > 0) addresses.map{
                    Location(it.getAddressLine(0), LatLng(it.latitude, it.longitude))
                } else null
                result.complete(queryResults)
            }
            return result.get(1L, java.util.concurrent.TimeUnit.SECONDS)
        }

        private fun resultHandler(query: String, geocoder: Geocoder): List<Location>? {
            val result = CompletableFuture<List<Location>?>()
            try {
                val geocodeResult = geocoder.getFromLocationName(query, MAX_RESULT)
                val queryResults =
                    if (geocodeResult == null || geocodeResult.isEmpty()) null else geocodeResult.map { address ->
                        Location(address.getAddressLine(0), LatLng(address.latitude, address.longitude)) }
                result.complete(queryResults)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result.get(1L, java.util.concurrent.TimeUnit.SECONDS)
        }

        private fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            if (capabilities != null) {
                if(capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR))
                    return true
                if(capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI))
                    return true
            }
            return false
        }
    }



}