package com.github.factotum_sdp.factotum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LoggedIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)
        val plaintext: TextView = findViewById(R.id.LoggedInText)
        plaintext.text =
            buildString {
                append("Hello ")
                append(intent.getStringExtra("userName"))
                append("\nLogged in with email : ")
                append(intent.getStringExtra("userEmail"))
            }

    }
}