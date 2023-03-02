package com.github.factotum_sdp.factotum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.Button

import com.firebase.ui.auth.AuthUI

import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class FirebaseUIActivity : AppCompatActivity(), FirebaseUI {
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res -> this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_uiactivity)
        val login: Button = findViewById(R.id.Login)
        login.setOnClickListener {
            createSignInIntent()
        }
    }

    //create and launch the sign in intent
    override fun createSignInIntent() {
        // authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    //callback on sign in
    override fun onSignInResult(result : FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse

        if (result.resultCode == RESULT_OK){
            //successfully signed in

            val user = FirebaseAuth.getInstance().currentUser
            val intent = Intent(this, LoggedIn::class.java)
            intent.putExtra("userEmail", user!!.email)
            intent.putExtra("userName", user.displayName)
            startActivity(intent)
        } else {
            // sign in failed
            if (response == null) {
                // authentication canceled
            } else {
                val error = response.error
                Log.e("LOGIN_ERROR", error.toString())
            }
        }
    }
}