package com.github.factotum_sdp.factotum.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the maps Fragment"
    }
    val text: LiveData<String> = _text
}