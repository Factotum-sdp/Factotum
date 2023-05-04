package com.github.factotum_sdp.factotum.data

import android.annotation.SuppressLint
import android.location.Location
import com.github.factotum_sdp.factotum.placeholder.LocationsPlaceHolder.FRONT_OF_ROLEX
import com.github.factotum_sdp.factotum.placeholder.LocationsPlaceHolder.PELICAN
import com.github.factotum_sdp.factotum.placeholder.LocationsPlaceHolder.PELICAN_TO_ROLEX_1
import com.github.factotum_sdp.factotum.placeholder.LocationsPlaceHolder.PELICAN_TO_ROLEX_2
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val TIMES_WITHOUT_MOVING = 2
class MockLocationClient: LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        val longerInterval = interval * 10
        return flow {
            for(i in 0 until TIMES_WITHOUT_MOVING) {
                emit(PELICAN)
                delay(longerInterval)
            }

            emit(PELICAN_TO_ROLEX_1)
            delay(longerInterval)

            emit(PELICAN_TO_ROLEX_2)
            delay(longerInterval)

            while (true) {
                emit(FRONT_OF_ROLEX)
                delay(longerInterval)
            }
        }
    }
}