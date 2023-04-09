package com.github.factotum_sdp.factotum.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.ui.login.LoginResult
import com.google.firebase.auth.AuthResult

abstract class BaseAuthViewModel: ViewModel() {

    abstract val _authResult : MutableLiveData<BaseAuthResult<*>>
    abstract val authResult: LiveData<BaseAuthResult<*>>

    abstract fun auth(email: String, password: String)
}