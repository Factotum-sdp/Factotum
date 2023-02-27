package com.github.schuetzcarl.bootcamp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: Button = findViewById(R.id.validateButton)

        button.setOnClickListener {
            val intent = Intent(this, GreetingActivity::class.java)
            val text: EditText = findViewById(R.id.userNameEditText)

            intent.putExtra("userName", text.text.toString())
            startActivity(intent)
        }
    }
}