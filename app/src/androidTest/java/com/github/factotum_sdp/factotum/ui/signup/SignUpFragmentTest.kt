package com.github.factotum_sdp.factotum.ui.signup

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpFragmentTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Before
    fun setUp() {
        onView(withId(R.id.signup)).perform(click())
    }

    @Test
    fun signUpFormInitialStateIsEmpty() {
        onView(withId(R.id.username)).check(ViewAssertions.matches(ViewMatchers.withText("")))
        onView(withId(R.id.email)).check(ViewAssertions.matches(ViewMatchers.withText("")))
        onView(withId(R.id.password)).check(ViewAssertions.matches(ViewMatchers.withText("")))
        onView(withId(R.id.role)).check(ViewAssertions.matches(ViewMatchers.withText("")))
        onView(withId(R.id.signup)).check(ViewAssertions.matches(not(ViewMatchers.isEnabled())))
    }

    @Test
    fun signUpFormWithoutRole() {
        onView(withId(R.id.username)).perform(ViewActions.typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(ViewActions.typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(ViewActions.typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.signup)).check(ViewAssertions.matches(not(ViewMatchers.isEnabled())))
    }

    @Test
    fun signUpFormWithoutPassword() {
        onView(withId(R.id.username)).perform(ViewActions.typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(ViewActions.typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(RootMatchers.isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.signup)).check(ViewAssertions.matches(not(ViewMatchers.isEnabled())))
    }

    @Test
    fun signUpFormWithoutEmail() {
        onView(withId(R.id.username)).perform(ViewActions.typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(ViewActions.typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(RootMatchers.isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.signup)).check(ViewAssertions.matches(not(ViewMatchers.isEnabled())))
    }

    @Test
    fun signUpFormWithoutUsername() {
        onView(withId(R.id.email)).perform(ViewActions.typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(ViewActions.typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(RootMatchers.isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.signup)).check(ViewAssertions.matches(not(ViewMatchers.isEnabled())))
    }

    @Test
    fun signUpFormWithAllFields() {
        onView(withId(R.id.username)).perform(ViewActions.typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(ViewActions.typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(ViewActions.typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(RootMatchers.isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.signup)).check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun clickOnSignUpLeadsToLoginFragment() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressBack()
        onView(withId(R.id.fragment_login_directors_parent)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}