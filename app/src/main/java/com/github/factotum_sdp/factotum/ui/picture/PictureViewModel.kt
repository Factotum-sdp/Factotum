package com.github.factotum_sdp.factotum.ui.picture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PictureViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        //temp text, let it hardcoded until true fragment implementation
        value = ""
    }
    val text: LiveData<String> = _text

}