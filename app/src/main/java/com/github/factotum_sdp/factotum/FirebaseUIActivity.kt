package com.github.factotum_sdp.factotum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.Button
import android.widget.TextView

import com.firebase.ui.auth.AuthUI

import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

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
        val logout: Button = findViewById(R.id.logout)
        logout.setOnClickListener{
            signOut()
        }
        val delete: Button = findViewById(R.id.delete)
        delete.setOnClickListener{
            delete()
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

    override fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener{
                val text: TextView = findViewById(R.id.loggedInText)
                text.text = buildString { append("You are now logged out") }
            }
    }

    override fun delete() {
        AuthUI.getInstance()
            .delete(this).addOnCompleteListener{
                val text: TextView = findViewById(R.id.loggedInText)
                text.text = buildString { append("You have deleted your account") }
            }
    }

    //callback on sign in
    override fun onSignInResult(result : FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse

        if (result.resultCode == RESULT_OK){
            //successfully signed in

            val user = FirebaseAuth.getInstance().currentUser
            val logInText: TextView = findViewById(R.id.loggedInText)
            logInText.text = buildString {
                append("Hello ")
                append(user!!.displayName)
                append("\nYou are logged in with the address : ")
                append(user.email)
            }
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