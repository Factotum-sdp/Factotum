package com.github.factotum_sdp.factotum.utils

import com.github.factotum_sdp.factotum.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class GeneralUtils {
    companion object {
        private lateinit var database: FirebaseDatabase
        private lateinit var auth: FirebaseAuth
        private lateinit var storage: FirebaseStorage

        fun initFirebase() {
            database = Firebase.database
            auth = Firebase.auth
            storage = Firebase.storage

            database.useEmulator("10.0.2.2", 9000)
            auth.useEmulator("10.0.2.2", 9099)
            storage.useEmulator("10.0.2.2", 9199)
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