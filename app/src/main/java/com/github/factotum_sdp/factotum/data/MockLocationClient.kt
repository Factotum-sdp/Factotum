package com.github.factotum_sdp.factotum.data

import android.annotation.SuppressLint
import android.location.Location
import com.github.factotum_sdp.factotum.placeholder.LocationsPlaceHolder.PELICAN
import com.github.factotum_sdp.factotum.placeholder.LocationsPlaceHolder.ROLEX_CENTER
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val TIMES_OUT_OF_PLACE = 4
class MockLocationClient: LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        val longerInterval = interval * 10
        return flow {
            for(i in 0 until TIMES_OUT_OF_PLACE) {
                emit(PELICAN)
                delay(longerInterval)
            }

            while (true) {
                emit(ROLEX_CENTER)
                delay(longerInterval)
            }
        }
    }
}