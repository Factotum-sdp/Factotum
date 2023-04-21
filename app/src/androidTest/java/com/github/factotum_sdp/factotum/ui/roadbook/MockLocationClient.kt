package com.github.factotum_sdp.factotum.ui.roadbook

import android.annotation.SuppressLint
import android.location.Location
import com.github.factotum_sdp.factotum.data.LocationClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

class MockLocationClient(
): LocationClient {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return flow {
            val mockLocation = mock(Location::class.java)
            given(mockLocation.getLatitude()).willReturn(70.0)
            given(mockLocation.getLongitude()).willReturn(43.0)
            emit(mockLocation)
        }
    }

}