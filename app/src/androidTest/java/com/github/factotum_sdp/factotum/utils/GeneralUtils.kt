package com.github.factotum_sdp.factotum.utils

import android.util.Log
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class GeneralUtils {
    companion object {
        private lateinit var database: FirebaseDatabase
        private lateinit var auth: FirebaseAuth
        private lateinit var storage: FirebaseStorage
        private const val WAIT_BETWEEN_DB_ADD = 500L

        fun initFirebase(online : Boolean = true) {
            database = Firebase.database
            auth = Firebase.auth
            storage = Firebase.storage

            database.useEmulator("10.0.2.2", 9000)
            auth.useEmulator("10.0.2.2", 9099)
            if (online) {
                storage.useEmulator("10.0.2.2", 9199)
            } else {
                storage.useEmulator("10.0.2.2", 9198)
            }

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

        suspend fun addUserToDatabase(user: UsersPlaceHolder.User) {
            try {
                UsersPlaceHolder.addAuthUser(user)
            } catch (e: FirebaseAuthUserCollisionException) {
                e.message?.let { Log.e("DisplayFragmentTest", it) }
            }
            UsersPlaceHolder.addUserToDb(user)

            delay(WAIT_BETWEEN_DB_ADD)
        }
    }
}
