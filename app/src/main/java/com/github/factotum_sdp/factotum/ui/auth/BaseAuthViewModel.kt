package com.github.factotum_sdp.factotum.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseAuthViewModel: ViewModel() {

    abstract val _authResult : MutableLiveData<BaseAuthResult<*>>
    abstract val authResult: LiveData<BaseAuthResult<*>>

    abstract fun auth(email: String, password: String)
}