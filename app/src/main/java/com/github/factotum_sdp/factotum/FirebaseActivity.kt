package com.github.factotum_sdp.factotum

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture

class FirebaseActivity : AppCompatActivity() {
    private var db : DatabaseReference = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase)
    }


    // Retrieve the email from the database using the phone number
    fun get(view : View) {
        val emailView = findViewById<EditText>(R.id.editTextEmailAddress) as TextView
        val phoneView = findViewById<EditText>(R.id.editTextPhone) as TextView

        val phoneText = phoneView.text.toString()

        val future = CompletableFuture<String>()

        //Get in the database the email corresponding to the phone number
        // using the phone number as a key
        db.child(phoneText).get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as String)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        //Set the email we received from the database in the email field
        //as an answer to the user
        future.thenAccept {
            emailView.text = it
        }
    }

    // Set the email in the database using the phone number as a key
    fun set(view : View) {
        val emailView = findViewById<EditText>(R.id.editTextEmailAddress) as TextView
        val phoneView = findViewById<EditText>(R.id.editTextPhone) as TextView

        val emailText = emailView.text.toString()
        val phoneText = phoneView.text.toString()

        // Simply set the email in the database using the phone number as a key
        db.child(phoneText).setValue(emailText)
    }

}