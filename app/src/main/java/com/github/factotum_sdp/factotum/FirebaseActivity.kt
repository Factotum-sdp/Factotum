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

    fun get(view : View) {
        val emailView = findViewById<EditText>(R.id.editTextEmailAddress) as TextView
        val phoneView = findViewById<EditText>(R.id.editTextPhone) as TextView

        val phoneText = phoneView.text.toString()

        val future = CompletableFuture<String>()

        //Get in the database the values of the mail and phone
        db.child(phoneText).get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as String)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        //Set the mail and phone in the view
        future.thenAccept {
            emailView.text = it
        }
    }

    fun set(view : View) {
        val emailView = findViewById<EditText>(R.id.editTextEmailAddress) as TextView
        val phoneView = findViewById<EditText>(R.id.editTextPhone) as TextView

        val emailText = emailView.text.toString()
        val phoneText = phoneView.text.toString()

        db.child(phoneText).setValue(emailText)
    }

}