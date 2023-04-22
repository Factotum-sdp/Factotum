package com.github.factotum_sdp.factotum.utils

import com.github.factotum_sdp.factotum.MainActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GeneralUtils {
    companion object {
        private var emulatorSet: Boolean = false
        private lateinit var database: FirebaseDatabase

        fun setEmulatorGet(): FirebaseDatabase {
            if (!emulatorSet) {
                database = Firebase.database
                database.useEmulator("10.0.2.2", 9000)
                MainActivity.setDatabase(database)
                emulatorSet = true
            }
            return database
        }

        fun getDatabase(): FirebaseDatabase {
            return database
        }
    }
}