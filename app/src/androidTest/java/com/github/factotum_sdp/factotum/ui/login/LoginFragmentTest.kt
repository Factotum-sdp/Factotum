package com.github.factotum_sdp.factotum.ui.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    private val usernameInput = onView(withId(R.id.username))
    private val passwordInput = onView(withId(R.id.password))
    private val loginButton = onView(withId(R.id.login))
    private val signUp = onView(withId(R.id.signup))

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Test
    fun loginFormInitialStateIsEmpty() {
        usernameInput.check(matches(withText("")))
        passwordInput.check(matches(withText("")))
        loginButton.check(matches(not(isEnabled())))
    }

    @Test
    fun loginFormWithoutPassword() {
        usernameInput.perform(typeText("user.name@gmail.com"))
        loginButton.check(matches(not(isEnabled())))
    }

    @Test
    fun correctUserEntryLeadsToRoadBook() {
        onView(withId(R.id.username)).perform(typeText("jane.doe@gmail.com"))
        closeSoftKeyboard()
        onView(withId(R.id.password)).perform(typeText("123456"))
        closeSoftKeyboard()
        onView(withId(R.id.login)).perform(click())
        onView(withId(R.id.fragment_roadbook_directors_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun incorrectUserEntryLeadsToErrorSnackbar() {
        onView(withId(R.id.username)).perform(typeText("jane.doe@gmail.com"))
        closeSoftKeyboard()
        onView(withId(R.id.password)).perform(typeText("azertyuiop"))
        closeSoftKeyboard()
        onView(withId(R.id.login)).perform(click())
        onView(withId(R.id.fragment_login_directors_parent)).check(matches(isDisplayed()))
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Login failed")))
    }

    @Test
    fun clickOnSignUpLeadsToSignUpFragment() {
        onView(withId(R.id.signup)).perform(click())
        onView(withId(R.id.fragment_signup_directors_parent)).check(matches(isDisplayed()))
    }
}