package com.github.factotum_sdp.factotum.utils

import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.ui.signup.SignUpFragmentTest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class GeneralUtils {
    companion object {
        private var firebaseSet: Boolean = false
        private val database: FirebaseDatabase = Firebase.database
        private val auth: FirebaseAuth = Firebase.auth
        private val storage : FirebaseStorage = Firebase.storage

        fun initFirebase() {
            //if (!firebaseSet) {
            database.setPersistenceEnabled(true)
                database.useEmulator("10.0.2.2", 9000)
                auth.useEmulator("10.0.2.2", 9099)
                storage.useEmulator("10.0.2.2", 9199)
                firebaseSet = true
            //}
            MainActivity.setDatabase(database)
            MainActivity.setAuth(auth)
        }
        fun getDatabase(): FirebaseDatabase {
            return database
        }

        fun getAuth(): FirebaseAuth {
            return auth
        }

        fun getStorage(): FirebaseStorage {
            return storage
        }
    }
}