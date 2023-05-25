package com.github.factotum_sdp.factotum.ui.roadbook

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import androidx.activity.ComponentActivity
import com.github.factotum_sdp.factotum.data.LocationClient
import com.github.factotum_sdp.factotum.services.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * The LocationTrackingHandler class
 *
 * Stateful object to manage a LocationService instance.
 * It provides a simpler API for the UI part.
 * Should be mutated from only one place in the code.
 */
class LocationTrackingHandler {

    private lateinit var locationService: LocationService
    private var onLocationUpdate: ((location: Location) -> Unit)? = null

    private val _isTrackingEnabled = MutableStateFlow(false)
    private val _currentLocation = MutableStateFlow<Location?>(null)


    /**
     * The StateFlow<Location?> giving the current location when the tracking is enabled
     */
    val currentLocation = _currentLocation.asStateFlow()

    /**
     * The StateFlow<Boolean> giving whether the locationService is running or not
     */
    val isTrackingEnabled = _isTrackingEnabled.asStateFlow()

    /**
     * Start the location service
     * @param applicationContext: Context
     * @param componentActivity: ComponentActivity
     */
    fun startLocationService(applicationContext: Context, componentActivity: ComponentActivity) {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            componentActivity.startService(this)
            componentActivity.bindService(this, connection, Context.BIND_AUTO_CREATE)
        }
    }

    /**
     * Stop the location service
     * @param applicationContext: Context
     * @param componentActivity: ComponentActivity
     */
    fun stopLocationService(applicationContext: Context, componentActivity: ComponentActivity) {
        if (isTrackingEnabled.value) {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                unbindWrapForCI { componentActivity.unbindService(it) }
                componentActivity.stopService(this)
            }
        } // else the service is already disabled
    }

    /**
     * Set the event on each location update
     * @param onLocationUpdate: (Location) -> Unit
     */
    fun setOnLocationUpdate(onLocationUpdate: ((location: Location) -> Unit)) {
        val newCB = { location: Location ->
            _currentLocation.update { _: Location? -> location }
            onLocationUpdate(location)
        }
        this.onLocationUpdate = newCB
    }


    // Defines callbacks for service binding, passed to bindService().
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            onLocationUpdate?.let { f ->
                locationService.setEventOnLocationUpdate(f)
            }
            _isTrackingEnabled.update { true }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            _isTrackingEnabled.update { false }
        }
    }

    private fun unbindWrapForCI(unbind: (connection: ServiceConnection) -> Unit) {
        try {
            unbind(connection)
        } catch (_: java.lang.IllegalArgumentException) {
        }
        // Do not unbind, on connectedCheck... However working fine on manual tests
    }
}