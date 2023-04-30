package com.github.factotum_sdp.factotum.data

import android.annotation.SuppressLint
import android.location.Location
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar

class MockLocationClient: LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        val longerInterval = interval * 10
        return flow {
            while (true) {
                val fakeLocation = Location("fake")
                fakeLocation.latitude = 37.7749
                fakeLocation.longitude = -122.4194
                fakeLocation.time = Calendar.getInstance().timeInMillis
                fakeLocation.accuracy = 10.0f
                emit(fakeLocation)
                delay(longerInterval)
            }
        }
    }
}