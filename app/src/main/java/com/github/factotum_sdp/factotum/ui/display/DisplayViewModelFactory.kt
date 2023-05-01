package com.github.factotum_sdp.factotum.ui.display

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DisplayViewModelFactory(private val userName: MutableLiveData<String>) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DisplayViewModel::class.java)) {
            return DisplayViewModel(userName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}