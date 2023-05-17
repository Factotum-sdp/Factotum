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

private val DEFAULT_JOURNEY = listOf(
    PELICAN,
    PELICAN,
    PELICAN_TO_ROLEX_1,
    PELICAN_TO_ROLEX_2,
    FRONT_OF_ROLEX
)
class MockLocationClient(private val journey: List<Location> = DEFAULT_JOURNEY): LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        val longerInterval = interval * 4
        return flow {
            for(loc in journey) {
                emit(loc)
                delay(longerInterval)
            }

            while (true) {
                emit(journey.last())
                delay(longerInterval)
            }
        }
    }
}