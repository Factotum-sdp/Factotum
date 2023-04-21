package com.github.factotum_sdp.factotum.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.FusedLocationClient
import com.github.factotum_sdp.factotum.data.LocationClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private var onLocationUpdateEvent: ((location: Location) -> Unit)? = null
    private val binder = LocalBinder()
    private var count: Int = 0

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = FusedLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun setEventOnLocationUpdate(onLocationUpdate: ((location: Location) -> Unit)) {
        onLocationUpdateEvent = onLocationUpdate
    }

    private fun start() {
        startForegroundJob(1000L)  { service, notification ->
            val updatedNotification = notification.setContentText(
                "Location service for Factotum is working ${count++}"
            ).setChannelId("my_service")
            service.notify(101, updatedNotification.build())
        }
    }

    private fun startForegroundJob(interval: Long,
                                   onLocationChanges: (service: NotificationManager,
                                                       notification: NotificationCompat.Builder) -> Unit) {
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service", service)
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setCategory(Notification.CATEGORY_SERVICE)
            .setSmallIcon(R.drawable.location)
            .setOngoing(true)

        locationClient
            .getLocationUpdates(interval)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                onLocationChanges(service, notification)
                onLocationUpdateEvent?.let { it(location) }
            }
            .launchIn(serviceScope)

        startForeground(101, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String,
                                          service: NotificationManager): String{
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        service.createNotificationChannel(chan)
        return channelId
    }

    private fun stop() {
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): LocationService = this@LocationService
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}