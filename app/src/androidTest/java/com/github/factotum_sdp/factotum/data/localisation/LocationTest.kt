package com.github.factotum_sdp.factotum.data.localisation

import android.location.Geocoder
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class LocationTest {
    @Test
    fun rightLocationCreates(){
        val addressName = "Route Cantonale 15, 1015 Lausanne"
        val location = Location(addressName, getApplicationContext())
        val geocoder =  Geocoder(getApplicationContext())
        val latch = CountDownLatch(1)
        geocoder.getFromLocationName(addressName, 1) { addresses ->
            val result = if(addresses.size != 0) addresses[0] else null
            assertEquals(location.address!!.latitude, result!!.latitude)
            assertEquals(location.address!!.longitude, result.longitude)
            latch.countDown()
        }
        latch.await()
    }

    @Test
    fun wrongLocationCreatesNull(){
        val addressName = "dfjsdk"
        val location = Location(addressName, getApplicationContext())
        assertEquals(location.address, null)
        assertEquals(location.addressName, null)
    }
}