package com.github.factotum_sdp.factotum

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.datastore.dataStore
import androidx.datastore.preferences.preferencesDataStore
import com.github.factotum_sdp.factotum.serializers.BagSerializer
import com.github.factotum_sdp.factotum.serializers.RoadBookSerializer
import com.github.factotum_sdp.factotum.serializers.ShiftSerializer

const val FACTOTUM_PREFERENCES_NAME = "factotum_preferences"
const val ROADBOOK_BACKUP_NAME = "roadbook-backup.json"
const val BAG_BACKUP_NAME = "bag-backup.json"
const val SHIFT_BACKUP_NAME = "shift-backup.json"

val Context.preferencesDataStore by preferencesDataStore(
    name = FACTOTUM_PREFERENCES_NAME
)
val Context.roadBookDataStore by dataStore(ROADBOOK_BACKUP_NAME, RoadBookSerializer)
val Context.bagDataStore by dataStore(BAG_BACKUP_NAME, BagSerializer)
val Context.shiftDataStore by dataStore(SHIFT_BACKUP_NAME, ShiftSerializer)
fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasNotificationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}