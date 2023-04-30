package com.github.factotum_sdp.factotum.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.github.factotum_sdp.factotum.models.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val USE_ROADBOOK_PREFERENCES = booleanPreferencesKey("use_roadbook_preferences")
    }

    private val TAG: String = "SettingsPreferencesRepo"

    /**
     * Suspend function to fetch the initial Settings values
     *
     * @return Settings
     */
    suspend fun fetchInitialPreferences(): Settings =
        mapSettingsPreferences(dataStore.data.first().toPreferences())

    /**
     * Get the user preferences flow.
     */
    val settingsFlow: Flow<Settings> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapSettingsPreferences(preferences)
        }

    /**
     * Suspend function to update the Settings values
     *
     * @param isReorderingEnabled: Boolean
     */
    suspend fun updateUseRoadBookPreferences(areRoadBookPreferencesUsed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_ROADBOOK_PREFERENCES] = areRoadBookPreferencesUsed
        }
    }

    private fun mapSettingsPreferences(preferences: Preferences): Settings {
        // Get our show completed value, defaulting to false if not set:
        val useRoadBookPreferences = preferences[PreferencesKeys.USE_ROADBOOK_PREFERENCES] ?: true
        return Settings(useRoadBookPreferences)
    }

}