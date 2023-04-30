package com.github.factotum_sdp.factotum.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

private const val TAG = "FirebaseAppInstance"

/**
 * The Firebase singleton instance of our App
 */
object FirebaseInstance {

    private var database: FirebaseDatabase = Firebase.database
    private var auth: FirebaseAuth = Firebase.auth
    private val connectedRef = database.getReference(".info/connected")

    fun getDatabase(): FirebaseDatabase {
        return database
    }

    fun getAuth(): FirebaseAuth {
        return auth
    }

    fun setDatabase(database: FirebaseDatabase) {
        this.database = database
    }

    fun setAuth(auth: FirebaseAuth) {
        this.auth = auth
    }

    fun onConnectedStatusChanged(onConnectionChange: (Boolean) -> Unit) {
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isConnected: Boolean = snapshot.getValue<Boolean>() ?: false
                onConnectionChange(isConnected)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Listener was cancelled at .info/connected")
            }
        })
    }
}

