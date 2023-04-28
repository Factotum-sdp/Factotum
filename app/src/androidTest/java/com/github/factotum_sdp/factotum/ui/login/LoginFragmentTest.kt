package com.github.factotum_sdp.factotum.ui.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.getAuth
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.getDatabase
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.not
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    companion object {

        private const val WAIT_TIME_LOGIN = 1000L
        private const val WAIT_TIME_DB = 500L

        @OptIn(ExperimentalCoroutinesApi::class)
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() = runTest {
            initFirebase()
            UsersPlaceHolder.init(getDatabase(), getAuth())

            launch { GeneralUtils.addUserToDatabase(UsersPlaceHolder.USER1) }.join()
            launch {
                UsersPlaceHolder.addUserToDb(UsersPlaceHolder.USER3)
                delay(WAIT_TIME_DB)
            }.join()
            launch{ try {
                    UsersPlaceHolder.addAuthUser(UsersPlaceHolder.USER2)
                } catch (e: FirebaseAuthUserCollisionException) {
                    e.printStackTrace()
                }
            }.join()
        }

        @AfterClass
        @JvmStatic
        fun stopAuthEmulator() {
            val auth = Firebase.auth
            auth.signOut()
            MainActivity.setAuth(auth)
        }

        fun fillUserEntryAndGoToRBFragment(email: String, password: String) {
            onView(withId(R.id.email)).perform(typeText(email))
            onView(withId(R.id.fragment_login_directors_parent)).perform(
                closeSoftKeyboard()
            )
            onView(withId(R.id.password)).perform(typeText(password))
            onView(withId(R.id.fragment_login_directors_parent)).perform(
                closeSoftKeyboard()
            )
            onView(withId(R.id.login)).perform(click())

            runBlocking {
                delay(WAIT_TIME_LOGIN)
            }
        }
    }

    @Test
    fun loginFormInitialStateIsEmpty() {
        onView(withId(R.id.email)).check(matches(withText("")))
        onView(withId(R.id.password)).check(matches(withText("")))
        onView(withId(R.id.login)).check(matches(not(isEnabled())))
    }

    @Test
    fun incorrectUserEntryLeadsToFailedLogin() {
        onView(withId(R.id.email)).perform(typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_login_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("12345678"))
        onView(withId(R.id.fragment_login_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.login)).perform(click())
        FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                onView(withId(R.id.fragment_login_directors_parent)).check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun correctUserEntryLeadsToRoadBook() {
        fillUserEntryAndGoToRBFragment("jane.doe@gmail.com", "123456")
        FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                onView(withId(R.id.fragment_roadbook_directors_parent)).check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun userNotInProfileDispatchUserNotFoundMessage() {
        onView(withId(R.id.email)).perform(typeText(UsersPlaceHolder.USER2.email))
        onView(withId(R.id.fragment_login_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText(UsersPlaceHolder.USER2.password))
        onView(withId(R.id.fragment_login_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.login)).perform(click())

        FirebaseAuth.AuthStateListener {
            onView(withId(R.id.fragment_login_directors_parent)).check(matches(isDisplayed()))
            onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(R.string.user_not_found)))
        }
    }

    @Test
    fun loginFormWithoutPassword() {
        onView(withId(R.id.email)).perform(typeText("user.name@gmail.com"))
        onView(withId(R.id.login)).check(matches(not(isEnabled())))
    }

    @Test
    fun clickOnSignUpLeadsToSignUpFragment() {
        onView(withId(R.id.signup)).perform(click())
        onView(withId(R.id.fragment_signup_directors_parent)).check(matches(isDisplayed()))
    }
}