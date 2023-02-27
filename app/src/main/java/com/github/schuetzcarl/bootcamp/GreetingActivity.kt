package com.github.schuetzcarl.bootcamp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GreetingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_greeting)
        val bundle: Bundle? = intent.extras

        bundle?.let {
            bundle.apply {
                val str: String? = getString("userName")
                val plainText: TextView = findViewById(R.id.greetingMessage)
                plainText.text =
                    buildString {
                        append("Hello ")
                        append(str)
                    }
            }
        }



    }
}