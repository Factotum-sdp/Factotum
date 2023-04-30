package com.github.factotum_sdp.factotum.utils

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.FirebaseInstance
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
        private const val WAIT_TIME_LOGIN = 1000L

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

            FirebaseInstance.setDatabase(database)
            FirebaseInstance.setAuth(auth)
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

        fun fillUserEntryAndEnterTheApp(email: String, password: String) {
            Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText(email))
            Espresso.onView(ViewMatchers.withId(R.id.fragment_login_directors_parent)).perform(
                ViewActions.closeSoftKeyboard()
            )
            Espresso.onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText(password))
            Espresso.onView(ViewMatchers.withId(R.id.fragment_login_directors_parent)).perform(
                ViewActions.closeSoftKeyboard()
            )
            Espresso.onView(ViewMatchers.withId(R.id.login)).perform(ViewActions.click())

            runBlocking {
                delay(WAIT_TIME_LOGIN)
            }
        }
    }
}
