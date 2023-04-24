package com.github.factotum_sdp.factotum.ui.roadbook

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import androidx.activity.ComponentActivity
import com.github.factotum_sdp.factotum.services.LocationService

class LocationTrackingHandler {

    private lateinit var locationService: LocationService
    private var onLocationUpdate: ((location: Location) -> Unit)? = null
    private var isTrackingEnabled = false

    fun startLocationService(applicationContext: Context, componentActivity: ComponentActivity) {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            componentActivity.startService(this)
            componentActivity.bindService(this, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun stopLocationService(applicationContext: Context, componentActivity: ComponentActivity) {
        if (isTrackingEnabled) {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                unbindWrapForCI { componentActivity.unbindService(it) }
                componentActivity.stopService(this)
            }
        } // else the service is already disabled
    }

    fun isTrackingEnabled(): Boolean {
        return isTrackingEnabled
    }

    fun setOnLocationUpdate(onLocationUpdate: ((location: Location) -> Unit)) {
        this.onLocationUpdate = onLocationUpdate
    }

    /** Defines callbacks for service binding, passed to bindService().  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            onLocationUpdate?.let { f ->
                locationService.setEventOnLocationUpdate(f)
            }
            isTrackingEnabled = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isTrackingEnabled = false
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