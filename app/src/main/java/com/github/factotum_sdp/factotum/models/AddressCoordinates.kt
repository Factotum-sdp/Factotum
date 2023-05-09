package com.github.factotum_sdp.factotum.models

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.util.Log
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
 * @param store : Boolean. If true, the location will be stored in the cache
 */
class AddressCoordinates {

    val coordinates: LatLng?
    val addressName: String?
    constructor(query: String, context: Context, cache : Boolean = true) {
        val locations = geocoderQuery(query, context)
        val bestResult = locations?.get(0)
        if (bestResult != null && bestResult.isComplete()) {
            coordinates = locations[0].coordinates
            addressName = locations[0].addressName
            if (cache) store(this, context)
        }
        else {
            coordinates = null
            addressName = query
        }
    }

    constructor(addressName: String, coordinates: LatLng?, context: Context, cache : Boolean = true) {
        this.addressName = addressName
        this.coordinates = coordinates
        if (this.isComplete() && cache) {
            store(this, context)
        }
    }


    companion object {
        const val CACHE_FILE_NAME = "locations.txt"
        const val CACHE_FILE_SEPARATOR = "|"
        const val MAX_RESULT = 4

        /**
         * Blocking function that returns a list of addresses dependant on the query
         *
         * @param query : String. Address that we want to search
         * @param context : Context. Context in which this function is called
         * @return : List<Location>?. Null if no result
         */
        fun geocoderQuery(query: String, context: Context): List<AddressCoordinates>? {
            // must handle differently depending on SDK.
            // getLocationFromName(String, int) deprecated in SDK 33
            if(query.length < 2) return null
            val geocoder = Geocoder(context)
            if (!isNetworkAvailable(context) || !Geocoder.isPresent()) {
                return searchInCache(query, context)
            }
            return if (Build.VERSION.SDK_INT >= TIRAMISU) {
                tiramisuResultHandler(query, geocoder, context)
            } else {
                Log.e("Location", "SDK < TIRAMISU")
                return null
                //resultHandler(query, geocoder, context)
            }
        }

        private fun searchInCache(query: String, context: Context): List<AddressCoordinates>?{
            val cacheFile = File(context.cacheDir, CACHE_FILE_NAME)
            var addressCoordinates : List<AddressCoordinates>? = emptyList()
            cacheFile.forEachLine { line ->
                if (line.contains(query)) {
                    val split = line.split(CACHE_FILE_SEPARATOR)
                    val result = AddressCoordinates(split[0], LatLng(split[1].toDouble(), split[2].toDouble()), context)
                    addressCoordinates = addressCoordinates?.plus(result)
                }
            }
            return addressCoordinates
        }

        @RequiresApi(TIRAMISU)
        private fun tiramisuResultHandler(query: String, geocoder: Geocoder, context: Context): List<AddressCoordinates>? {
            val result = CompletableFuture<List<AddressCoordinates>?>()
            geocoder.getFromLocationName(query, MAX_RESULT) { addresses ->
                val queryResults = if (addresses.size > 0) addresses.map{
                    AddressCoordinates(it.getAddressLine(0), LatLng(it.latitude, it.longitude), context, false)
                } else null
                result.complete(queryResults)
            }
            val listToReturn : List<AddressCoordinates>?
            try {
                listToReturn = result.get(2L, java.util.concurrent.TimeUnit.SECONDS)
            } catch (e: Exception) {
                Log.e("Location", "Error getting location from geocoder",e)
                return null
            }
            return listToReturn
        }
/* For now do not need backward handling anymore
        private fun resultHandler(query: String, geocoder: Geocoder, context: Context): List<AddressCoordinates>? {
            val result = CompletableFuture<List<AddressCoordinates>?>()
            try {
                val geocodeResult = geocoder.getFromLocationName(query, MAX_RESULT)
                val queryResults =
                    if (geocodeResult == null || geocodeResult.isEmpty()) null else geocodeResult.map { address ->
                        AddressCoordinates(address.getAddressLine(0), LatLng(address.latitude, address.longitude), context, false) }
                result.complete(queryResults)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result.get(1L, java.util.concurrent.TimeUnit.SECONDS)
        }
*/
        private fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
            val currentNetwork = connectivityManager?.activeNetwork ?: return false
            val caps = connectivityManager.getNetworkCapabilities(currentNetwork) ?: return false
            if (caps.hasCapability(NET_CAPABILITY_INTERNET)) return true
            return false
        }
    }

    /**
     * Creates a location and stores it in a file named CACHE_FILE_NAME in cache if an address was found.
     * Stored as : addressName CACHE_FILE_SEPARATOR latitude CACHE_FILE_SEPARATOR longitude
     *
     * @param query : String. Location to add
     * @param context : Context. Context in which this method is called
     * @return returns the location that has been added to the file. If no address was found, returns a Location with null coordinates
     */
    private fun store(addressCoordinates: AddressCoordinates, context: Context) {
        val cacheFile = File(context.cacheDir, CACHE_FILE_NAME)
        cacheFile.deleteOnExit()
        //creates the header if it is a new file
        if (cacheFile.length() == 0L) cacheFile.appendText("location${CACHE_FILE_SEPARATOR}latitude${CACHE_FILE_SEPARATOR}longitude\n")
        // stores
        val toStore =
            "${addressCoordinates.addressName}$CACHE_FILE_SEPARATOR${addressCoordinates.coordinates!!.latitude}$CACHE_FILE_SEPARATOR${addressCoordinates.coordinates.longitude}\n"
        //checks if already if file
        var alreadyExists = false
        cacheFile.forEachLine { line ->
            if (line.contains(addressCoordinates.addressName.toString())) {
                alreadyExists = true
            }
        }
        if (!alreadyExists){
            cacheFile.appendText(toStore)
        }
    }

    fun isComplete(): Boolean{
        return coordinates != null && addressName != null
    }



}