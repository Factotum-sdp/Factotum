package com.github.factotum_sdp.factotum

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.factotum_sdp.factotum.services.LocationService
import android.content.ComponentName
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import androidx.activity.ComponentActivity


class LocationTrackingHandler() {

    private lateinit var locationService: LocationService
    private val _isRunning: MutableLiveData<Boolean> = MutableLiveData(false)
    val isRunning: LiveData<Boolean> = _isRunning


    fun startLocationService(applicationContext: Context, componentActivity: ComponentActivity) {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            componentActivity.startService(this)
            componentActivity.bindService(this, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun stopLocationService(applicationContext: Context, componentActivity: ComponentActivity) {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            componentActivity.unbindService(connection)
            componentActivity.stopService(this)
        }
    }

    fun setOnLocationUpdate(interval: Long, onLocationUpdate: ((location: Location) -> Unit)) {
        locationService.setEventOnLocationUpdate(interval, onLocationUpdate)
    }

    /** Defines callbacks for service binding, passed to bindService().  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            _isRunning.value = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            _isRunning.value = false
        }
    }
}