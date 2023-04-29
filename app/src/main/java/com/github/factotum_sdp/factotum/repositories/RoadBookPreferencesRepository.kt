package com.github.factotum_sdp.factotum.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.github.factotum_sdp.factotum.models.RoadBookPreferences
import kotlinx.coroutines.flow.first

/**
 * The RoadBookPreferencesRepository
 *
 * With DataStore as unique data source to save locally the RoadBook preferences state.
 */
class RoadBookPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val REORDERING = booleanPreferencesKey("roadbook_order_reordering")
        val DELETION_AND_ARCHIVING = booleanPreferencesKey("roadbook_delete_or_archive")
        val EDITION = booleanPreferencesKey("roadbook_edition")
        val DETAILS_ACCESS = booleanPreferencesKey("roadbook_details_access")
        val SHOW_ARCHIVED = booleanPreferencesKey("roadbook_show_archived")
    }

    /**
     * Suspend function to fetch the initial preferences for the RoadBook
     *
     * @return RoadBookPreferences
     */
    suspend fun fetchInitialPreferences(): RoadBookPreferences =
        mapRoadBookPreferences(dataStore.data.first().toPreferences())

    /**
     * Suspend function to update the reordering RoodBook preference
     *
     * @param isReorderingEnabled: Boolean
     */
    suspend fun updateReordering(isReorderingEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.REORDERING] = isReorderingEnabled
        }
    }

    /**
     * Suspend function to update the deletion and archiving RoodBook preference
     *
     * @param isDeletionAndArchivingEnabled: Boolean
     */
    suspend fun updateDeletionOrArchiving(isDeletionAndArchivingEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DELETION_AND_ARCHIVING] = isDeletionAndArchivingEnabled
        }
    }

    /**
     * Suspend function to update the edition RoodBook preference
     *
     * @param isEditionEnabled: Boolean
     */
    suspend fun updateEdition(isEditionEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.EDITION] = isEditionEnabled
        }
    }

    /**
     * Suspend function to update the access details RoodBook preference
     *
     * @param isAccessDetailsEnabled: Boolean
     */
    suspend fun updateDetailsAccess(isAccessDetailsEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DETAILS_ACCESS] = isAccessDetailsEnabled
        }
    }

    /**
     * Suspend function to update the show archived RoodBook preference
     *
     * @param isShowArchivedEnabled: Boolean
     */
    suspend fun updateShowArchived(isShowArchivedEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_ARCHIVED] = isShowArchivedEnabled
        }
    }

    private fun mapRoadBookPreferences(preferences: Preferences): RoadBookPreferences {
        // Get our show completed value, defaulting to false if not set:
        val enableReordering = preferences[PreferencesKeys.REORDERING] ?: true
        val enableArchivingAndDeletion = preferences[PreferencesKeys.DELETION_AND_ARCHIVING] ?: true
        val enableEdition = preferences[PreferencesKeys.EDITION] ?: true
        val enableDetailsAccess = preferences[PreferencesKeys.DETAILS_ACCESS] ?: true
        val showArchived = preferences[PreferencesKeys.SHOW_ARCHIVED] ?: true
        return RoadBookPreferences(enableReordering, enableArchivingAndDeletion,
                                    enableEdition, enableDetailsAccess, showArchived)
    }
}