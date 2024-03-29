package com.github.factotum_sdp.factotum.utils

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.model.User
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder.USER_BOSS
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder.USER_CLIENT
import com.github.factotum_sdp.factotum.ui.login.LoginViewModel
import com.github.factotum_sdp.factotum.ui.roadbook.RoadBookViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class GeneralUtils {
    companion object {
        private var database: FirebaseDatabase = Firebase.database
        private var auth: FirebaseAuth = Firebase.auth
        private var storage: FirebaseStorage = Firebase.storage
        private var emulatorSet = false
        private val BOSS_USER = User(
            USER_BOSS.uid,
            USER_BOSS.name,
            USER_BOSS.email,
            USER_BOSS.role
        )

        private val CLIENT_USER = User(
            USER_CLIENT.uid,
            USER_CLIENT.name,
            USER_CLIENT.email,
            USER_CLIENT.role
        )


        fun initFirebase(online: Boolean = true) {
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
            RoadBookViewModel.demo_records = DestinationRecords.RECORDS
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
            Thread.sleep(1000)
        }

        fun injectBossAsLoggedInUser(testRule: ActivityScenarioRule<MainActivity>) {
            RoadBookViewModel.demo_records = DestinationRecords.RECORDS
            injectLoggedInUser(testRule, BOSS_USER)
        }

        fun injectClientAsLoggedInUser(testRule: ActivityScenarioRule<MainActivity>) {
            injectLoggedInUser(testRule, CLIENT_USER)
        }

        fun injectLoggedInUser(testRule: ActivityScenarioRule<MainActivity>, loggedInUser: User) {
            RoadBookViewModel.demo_records = DestinationRecords.RECORDS
            testRule.scenario.onActivity {
                val user = it.applicationUserViewModel()
                user.setLoggedInUser(loggedInUser)
            }
        }

        fun logout() {
            val loginViewModel = LoginViewModel(
                getInstrumentation().targetContext)
            loginViewModel.logout()
        }

    }
}