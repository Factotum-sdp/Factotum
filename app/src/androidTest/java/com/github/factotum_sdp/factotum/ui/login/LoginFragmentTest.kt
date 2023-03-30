package com.github.factotum_sdp.factotum.ui.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Test
    fun loginFormInitialStateIsEmpty() {
        onView(withId(R.id.username)).check(matches(withText("")))
        onView(withId(R.id.password)).check(matches(withText("")))
        onView(withId(R.id.login)).check(matches(not(isEnabled())))
    }

    @Test
    fun loginFormWithoutPassword() {
        onView(withId(R.id.username)).perform(typeText("user.name@gmail.com"))
        onView(withId(R.id.login)).check(matches(not(isEnabled())))
    }

    @Test
    fun correctUserEntryLeadsToRoadBook() {
        onView(withId(R.id.username)).perform(typeText("jane.doe@gmail.com"))
        closeSoftKeyboard()
        onView(withId(R.id.password)).perform(typeText("123456"))
        closeSoftKeyboard()
        onView(withId(R.id.login)).perform(click())

        FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                onView(withId(R.id.fragment_roadbook_directors_parent)).check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun incorrectUserEntryLeadsToFailedLogin() {
        onView(withId(R.id.username)).perform(typeText("jane.doe@gmail.com"))
        closeSoftKeyboard()
        onView(withId(R.id.password)).perform(typeText("12345678"))
        closeSoftKeyboard()
        onView(withId(R.id.login)).perform(click())
        FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                onView(withId(R.id.fragment_login_directors_parent)).check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun clickOnSignUpLeadsToSignUpFragment() {
        onView(withId(R.id.signup)).perform(click())
        onView(withId(R.id.fragment_signup_directors_parent)).check(matches(isDisplayed()))
    }
}