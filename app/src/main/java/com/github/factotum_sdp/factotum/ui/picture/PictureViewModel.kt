package com.github.factotum_sdp.factotum.ui.picture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PictureViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the picture Fragment"
    }
    val text: LiveData<String> = _text

}