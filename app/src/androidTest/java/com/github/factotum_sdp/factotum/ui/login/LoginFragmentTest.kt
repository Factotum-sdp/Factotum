package com.github.factotum_sdp.factotum

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    private val context: Context = getInstrumentation().targetContext
    private val usernameInput = onView(withId(R.id.username))
    private val passwordInput = onView(withId(R.id.password))
    private val loginButton = onView(withId(R.id.login))

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
    fun emptyUsernameDisplayInvalidUsernameError() {
        usernameInput.perform(typeText(""), closeSoftKeyboard())
        passwordInput.perform(typeText("pwd"), closeSoftKeyboard())
        loginButton.perform(click())

        val invalidUsernameText = context.getString(R.string.invalid_username)

        usernameInput.check(matches(hasErrorText(invalidUsernameText)))
    }

    @Test
    fun shortPasswordDisplayInvalidPasswordError() {
        usernameInput.perform(typeText("user.name@gmail.com"), closeSoftKeyboard())
        passwordInput.perform(typeText("pwd"), closeSoftKeyboard())
        loginButton.perform(click())

        val invalidPasswordText = context.getString(R.string.invalid_password)

        passwordInput.check(matches(hasErrorText(invalidPasswordText)))
    }

    @Test
    fun loginFormWithValidDataAccessButtonIsEnabled() {
        usernameInput.perform(typeText("user.name@gmail.com"))
        passwordInput.perform(typeText("password"))
        passwordInput.perform(closeSoftKeyboard())
        loginButton.check(matches(isEnabled()))
    }

    @Test
    fun loginFormWithValidDataAccessOpenRoadBookFragment() {
        usernameInput.perform(typeText("user.name@gmail.com"))
        passwordInput.perform(typeText("password"))
        passwordInput.perform(closeSoftKeyboard())
        loginButton.perform(click())
        onView(withId(R.id.fragment_roadbook_directors_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun loginFormWithoutPassword() {
        usernameInput.perform(typeText("user.name@gmail.com"))
        loginButton.check(matches(not(isEnabled())))
    }

    @Test
    fun signUpButtonOpenSignUpFragmentWhenClicked() {
        onView(withId(R.id.signup)).perform(click())
        onView(withId(R.id.fragment_signup_directors_parent)).check(matches(isDisplayed()))
    }
}