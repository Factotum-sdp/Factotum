package com.github.factotum_sdp.factotum.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.github.factotum_sdp.factotum.models.RoadBookPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class RoadBookPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val REORDERING = booleanPreferencesKey("roadbook_order_reordering")
        val DELETION_AND_ARCHIVING = booleanPreferencesKey("roadbook_delete_or_archive")
        val EDITION = booleanPreferencesKey("roadbook_edition")
        val DETAILS_ACCESS = booleanPreferencesKey("roadbook_details_access")
        val SHOW_ARCHIVED = booleanPreferencesKey("roadbook_show_archived")
    }

    private val TAG: String = "RoadBookPreferencesRepo"


    /**
     * Get the user preferences flow.
     */
    val userPreferencesFlow: Flow<RoadBookPreferences> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    suspend fun fetchInitialPreferences(): RoadBookPreferences =
        mapUserPreferences(dataStore.data.first().toPreferences())


    suspend fun updateReordering(isReorderingEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.REORDERING] = isReorderingEnabled
        }
    }

    suspend fun updateDeletionOrArchiving(isDeletionAndArchivingEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DELETION_AND_ARCHIVING] = isDeletionAndArchivingEnabled
        }
    }

    suspend fun updateEdition(isEditionEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EDITION] = isEditionEnabled
        }
    }

    suspend fun updateDetailsAccess(isUpdateDetailsEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DETAILS_ACCESS] = isUpdateDetailsEnabled
        }
    }

    suspend fun updateShowArchived(isShowArchivedEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_ARCHIVED] = isShowArchivedEnabled
        }
    }

    private fun mapUserPreferences(preferences: Preferences): RoadBookPreferences {
        // Get our show completed value, defaulting to false if not set:
        val enableReordering = preferences[PreferencesKeys.REORDERING] ?: false
        val enableArchivingAndDeletion = preferences[PreferencesKeys.DELETION_AND_ARCHIVING] ?: false
        val enableEdition = preferences[PreferencesKeys.EDITION] ?: false
        val enableDetailsAccess = preferences[PreferencesKeys.DETAILS_ACCESS] ?: false
        val showArchived = preferences[PreferencesKeys.SHOW_ARCHIVED] ?: false
        return RoadBookPreferences(enableReordering, enableArchivingAndDeletion,
                                    enableEdition, enableDetailsAccess, showArchived)
    }
}