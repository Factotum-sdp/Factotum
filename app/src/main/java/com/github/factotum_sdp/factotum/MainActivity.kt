package com.github.factotum_sdp.factotum

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPhoto = findViewById<Button>(R.id.buttonProofPhoto)

        btnPhoto.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, ProofPhotoFragment())
                .addToBackStack(null)
                .commit()
        }

    }
}
