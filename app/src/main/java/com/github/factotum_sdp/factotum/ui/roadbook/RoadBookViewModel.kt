package com.github.factotum_sdp.factotum.ui.roadbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RoadBookViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is the roadBook Fragment"
    }
    val text: LiveData<String> = _text
}