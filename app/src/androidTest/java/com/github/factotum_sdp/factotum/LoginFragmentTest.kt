package com.github.factotum_sdp.factotum

import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.factotum_sdp.factotum.ui.login.LoginFragment
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    private val context: Context = getInstrumentation().targetContext
    private lateinit var scenario: FragmentScenario<LoginFragment>
    private val usernameInput = onView(withId(R.id.username))
    private val passwordInput = onView(withId(R.id.password))
    private val loginButton = onView(withId(R.id.login))

    @Before
    fun setup() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_Factotum)

    }

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

        scenario.close()
    }

    @Test
    fun shortPasswordDisplayInvalidPasswordError() {
        usernameInput.perform(typeText("user.name@gmail.com"), closeSoftKeyboard())
        passwordInput.perform(typeText("pwd"), closeSoftKeyboard())
        loginButton.perform(click())

        val invalidPasswordText = context.getString(R.string.invalid_password)

        passwordInput.check(matches(hasErrorText(invalidPasswordText)))

        scenario.close()
    }

    @Test
    fun loginFormWithValidDataAccessButtonIsClickable() {
        usernameInput.perform(typeText("user.name@gmail.com"))
        passwordInput.perform(typeText("password"), closeSoftKeyboard())
        loginButton.check(matches(isEnabled()))
    }

    @Test
    fun loginFormWithValidDataShowSnackbar() {
        usernameInput.perform(typeText("user.name@gmail.com"), closeSoftKeyboard())
        passwordInput.perform(typeText("password"), closeSoftKeyboard())
        loginButton.perform(click())

        onView(withText("Welcome! user.name@gmail.com"))
            .check(matches(isDisplayed()))

    }

    @Test
    fun loginFormWithoutPassword() {
        usernameInput.perform(typeText("user.name@gmail.com"))
        loginButton.check(matches(not(isEnabled())))
    }

    @Test
    fun signUpButtonOpenSnackbarWhenClicked() {
        onView(withId(R.id.signup)).perform(click())
        onView(withText("Sign Up"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun passwordForgotButtonOpenSnackbarWhenClicked() {
        onView(withId(R.id.pwdForgot)).perform(click())
        onView(withText("Password Forgot"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun googleSignInButtonOpenSnackbarWhenClicked() {
        onView(withId(R.id.sign_in_button)).perform(click())
        onView(withText("Google Sign In"))
            .check(matches(isDisplayed()))
    }
}