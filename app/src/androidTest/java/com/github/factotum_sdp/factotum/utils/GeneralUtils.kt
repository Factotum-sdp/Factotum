package com.github.factotum_sdp.factotum.utils

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.google.firebase.auth.FirebaseAuth
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
        private const val WAIT_TIME_LOGIN = 1500L

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

        fun fillUserEntryAndGoToRBFragment(email: String, password: String) {
            onView(withId(R.id.email)).perform(typeText(email))
            onView(withId(R.id.fragment_login_directors_parent)).perform(
                closeSoftKeyboard()
            )
            onView(withId(R.id.password))
                .perform(typeText(password))
            onView(withId(R.id.fragment_login_directors_parent)).perform(
                closeSoftKeyboard()
            )
            onView(withId(R.id.login)).perform(click())

            runBlocking { delay(WAIT_TIME_LOGIN) }
        }
    }
}
