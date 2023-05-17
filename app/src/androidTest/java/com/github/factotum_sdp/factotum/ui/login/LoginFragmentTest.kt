package com.github.factotum_sdp.factotum.ui.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder.USER2
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.fillUserEntryAndEnterTheApp
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matchers.not
import org.junit.After
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
        @BeforeClass
        @JvmStatic
        fun setUpDb() {
            initFirebase()
        }

        @AfterClass
        @JvmStatic
        fun stopAuthEmulator() {
            val auth = Firebase.auth
            auth.signOut()
            FirebaseInstance.setAuth(auth)
        }
    }

    @After
    fun leave() {
        GeneralUtils.logout()
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
        fillUserEntryAndEnterTheApp("jane.doe@gmail.com", "123456")
        FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                onView(withId(R.id.fragment_roadbook_directors_parent)).check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun userNotInProfileDispatchUserNotFoundMessage() {
        onView(withId(R.id.email)).perform(typeText(USER2.email))
        onView(withId(R.id.fragment_login_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText(USER2.password))
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

