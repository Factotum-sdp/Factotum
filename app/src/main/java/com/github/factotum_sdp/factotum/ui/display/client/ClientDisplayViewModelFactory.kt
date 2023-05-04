package com.github.factotum_sdp.factotum.ui.display.client

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClientDisplayViewModelFactory(private val userName: MutableLiveData<String>) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientDisplayViewModel::class.java)) {
            return ClientDisplayViewModel(userName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}