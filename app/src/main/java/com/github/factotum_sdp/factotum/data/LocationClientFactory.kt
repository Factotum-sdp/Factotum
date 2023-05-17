package com.github.factotum_sdp.factotum.data

import android.content.Context
import com.google.android.gms.location.LocationServices

/**
 * Construct a LocationClient object for the application
 *
 */
object LocationClientFactory {

    private var mockClient: LocationClient? = null

    /**
     * Provide a LocationClient
     *
     * @param applicationContext: Context
     * @return LocationClient
     */
    fun provideLocationClient(applicationContext: Context): LocationClient {
        mockClient?.let {
            return it
        }
        return FusedLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    /**
     * Set a mock location client,
     * used in provideLocationClient() instead of the default LocationClient
     *
     * @param mockClient: LocationClient
     */
    fun setMockClient(mockClient: LocationClient) {
        this.mockClient = mockClient
    }
}