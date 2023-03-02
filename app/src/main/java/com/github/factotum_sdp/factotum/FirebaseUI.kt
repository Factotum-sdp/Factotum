package com.github.factotum_sdp.factotum

import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult

interface FirebaseUI {
    fun onSignInResult(result : FirebaseAuthUIAuthenticationResult)
    fun createSignInIntent()
}