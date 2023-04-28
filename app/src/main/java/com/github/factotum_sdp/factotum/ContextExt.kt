package com.github.factotum_sdp.factotum

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.preferencesDataStore

const val FACTOTUM_PREFERENCES_NAME = "factotum_preferences"
val Context.dataStore by preferencesDataStore(
    name = FACTOTUM_PREFERENCES_NAME
)
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