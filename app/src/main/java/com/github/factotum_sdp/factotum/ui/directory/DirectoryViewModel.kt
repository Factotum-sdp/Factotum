package com.github.factotum_sdp.factotum.ui.directory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DirectoryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        //temp text, let it hardcoded until true fragment implementation
        value = "This is the directory Fragment"
    }
    val text: LiveData<String> = _text
}