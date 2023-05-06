package com.github.factotum_sdp.factotum.data.localisation

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.models.AddressCoordinates
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class AddressCoordinatesTest {
    @Test
    fun rightLocationCreates() {
        val addressName = "Route Cantonale 15, 1015 Lausanne"
        val addressCoordinates = AddressCoordinates(addressName, getApplicationContext())
        val geocoder = Geocoder(getApplicationContext())
        var result: Address? = null
        val latch = CountDownLatch(1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(addressName, 1) { addresses ->
                result = if (addresses.size != 0) addresses[0] else null
                latch.countDown()
            }
            latch.await()
        } else {
            result = geocoder.getFromLocationName(addressName, 1)?.get(0)
        }
        assertEquals(addressCoordinates.coordinates!!.latitude, result!!.latitude)
        assertEquals(addressCoordinates.coordinates!!.longitude, result!!.longitude)

    }

    @Test
    fun wrongLocationCreatesNull() {
        val addressName = "dfjsdk"
        val addressCoordinates = AddressCoordinates(addressName, getApplicationContext())
        assertEquals(addressCoordinates.coordinates, null)
        assertEquals(addressCoordinates.addressName, addressName)
    }

    @Test
    fun searchQueryAddsToCache() {
        val query = "Lausanne"
        val context: Context = getApplicationContext()
        val addressCoordinatesCache = AddressCoordinates(query, context)
        val addressCoordinates = AddressCoordinates(query, context)
        val cacheFile = File(context.cacheDir, AddressCoordinates.CACHE_FILE_NAME)
        assertTrue(cacheFile.exists())
        var containsAddress = false
        var containsLat = false
        var containsLng = false
        cacheFile.forEachLine { line ->
            if (line.contains(addressCoordinates.addressName.toString())) {
                containsAddress = true
            }
            if (line.contains(addressCoordinates.coordinates!!.latitude.toString())) {
                containsLat = true
            }
            if (line.contains(addressCoordinates.coordinates!!.longitude.toString())) {
                containsLng = true
            }
        }
        assertTrue(containsAddress)
        assertTrue(containsLat)
        assertTrue(containsLng)
    }

    @Test
    fun searchQueryDoNotAddIfNull() {
        val query = "wrong_query"
        val context: Context = getApplicationContext()
        val cacheFile = File(context.cacheDir, AddressCoordinates.CACHE_FILE_NAME)
        val cacheSizeBefore = cacheFile.length()
        val addressCoordinatesCache = AddressCoordinates(query, context)
        val cacheSizeAfter = cacheFile.length()
        assertEquals(cacheSizeBefore, cacheSizeAfter)
    }

    @Test
    fun searchQueryDoNotDuplicate() {
        val query = "Lausanne"
        val context: Context = getApplicationContext()
        val cacheFile = File(context.cacheDir, AddressCoordinates.CACHE_FILE_NAME)
        AddressCoordinates(query, context)
        val cacheSizeBefore = cacheFile.length()
        AddressCoordinates(query, context)
        val cacheSizeAfter = cacheFile.length()
        assertEquals(cacheSizeBefore, cacheSizeAfter)
    }

    @Test
    fun rightQueryReturnsMultiplesResults() {
        val query = "rue de Genève"
        val result = AddressCoordinates.geocoderQuery(query, getApplicationContext())
        assertTrue(result!!.size > 1)
    }
}