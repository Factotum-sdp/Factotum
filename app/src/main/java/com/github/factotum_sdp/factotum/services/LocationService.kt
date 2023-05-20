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
import com.github.factotum_sdp.factotum.data.LocationClient
import com.github.factotum_sdp.factotum.data.LocationClientFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

private const val CHANNEL_ID = "factotum_location_service"
private const val CHANNEL_NAME = "Factotum Live Location Service"
private const val SERVICE_ID = 101
private const val UPDATE_INTERVAL = 1000L

/**
 * The LocationService class
 *
 * It extends the android.app.Service abstract class,
 * in order to provide our own Factotum Location service.
 *
 * A service is needed to keep a high location update rate whenever
 * the app is in the background.
 */
class LocationService() : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var onLocationUpdateEvent: ((location: Location) -> Unit)? = null
    private val binder = LocalBinder()
    private var locationClient: LocationClient? = null

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationClientFactory.provideLocationClient(applicationContext)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Set the event for each Location update
     *
     * @param onLocationUpdate: (Location) -> Unit
     */
    fun setEventOnLocationUpdate(onLocationUpdate: ((location: Location) -> Unit)) {
        onLocationUpdateEvent = onLocationUpdate
    }

    private fun start() {
        startForegroundJob(UPDATE_INTERVAL) { service, notification ->
            val updatedNotification =
                notification
                    .setContentText(getString(R.string.loc_service_notification_message))
                    .setChannelId(CHANNEL_ID)
            service.notify(SERVICE_ID, updatedNotification.build())
        }
    }

    private fun stop() {
        stopSelf()
    }

    private fun startForegroundJob(
        interval: Long,
        onFirstLocationChange: ( // Only called the very first time the service is launched
            service: NotificationManager,
            notification: NotificationCompat.Builder
        ) -> Unit
    ) {
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(CHANNEL_ID, CHANNEL_NAME, service)
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notification =
            NotificationCompat
                .Builder(this, channelId)
                .setContentTitle(getString(R.string.loc_service_notification_title))
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.location)
                .setOngoing(true)
                .setContentText(getString(R.string.loc_service_notification_message))

        locationClient!!
            .getLocationUpdates(interval)
            .catch { e -> e.printStackTrace() }
            .onStart { onFirstLocationChange(service, notification) }
            .onEach { location ->
                onLocationUpdateEvent?.let { it(location) }
            }
            .launchIn(serviceScope)

        startForeground(SERVICE_ID, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String, channelName: String,
        service: NotificationManager
    ): String {
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        service.createNotificationChannel(chan)
        return channelId
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