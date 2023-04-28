package com.github.factotum_sdp.factotum.ui.settings

import androidx.lifecycle.*
import com.github.factotum_sdp.factotum.repositories.SettingsRepository
import kotlinx.coroutines.launch

/**
 * The SettingsViewModel
 *
 * Holds the application settings state
 * Launched at the start of the app in MainActivity
 *
 * @param repository: SettingsRepository
 */
class SettingsViewModel(private val repository: SettingsRepository): ViewModel() {

    val settingsLiveData = repository.settingsFlow.asLiveData()

    /**
     * Update the Application setting whether the RoadBookPreferences are used or not
     *
     * @param enable: Boolean
     */
    fun updateUseRoadBookPreferences(enable: Boolean) {
        viewModelScope.launch {
            repository.updateUseRoadBookPreferences(enable)
        }
    }

    // Factory needed to assign a value at construction time to the class attribute
    class SettingsViewModelFactory(private val repository: SettingsRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(SettingsRepository::class.java)
                .newInstance(repository)
        }
    }
}