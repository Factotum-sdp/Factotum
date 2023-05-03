package com.github.factotum_sdp.factotum.utils

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.FirebaseInstance
import com.github.factotum_sdp.factotum.models.User
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
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
        private var database: FirebaseDatabase = Firebase.database
        private var auth: FirebaseAuth = Firebase.auth
        private var storage: FirebaseStorage = Firebase.storage
        private var emulatorSet = false
        private const val WAIT_TIME_LOGIN = 1500L
        private val BOSS_USER = User(UsersPlaceHolder.USER_BOSS.name,
                                     UsersPlaceHolder.USER_BOSS.email,
                                     UsersPlaceHolder.USER_BOSS.role)

        fun initFirebase(online : Boolean = true) {

            if (!emulatorSet) {
                database.useEmulator("10.0.2.2", 9000)
                auth.useEmulator("10.0.2.2", 9099)
                emulatorSet = true
            }
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

        fun injectBossAsLoggedInUser(testRule: ActivityScenarioRule<MainActivity>) {
           injectLoggedInUser(testRule, BOSS_USER)
        }

        fun injectLoggedInUser(testRule: ActivityScenarioRule<MainActivity>, loggedInUser: User) {
            testRule.scenario.onActivity {
                val user = it.applicationUser()
                user.setLoggedInUser(loggedInUser)
            }
        }
    }
}