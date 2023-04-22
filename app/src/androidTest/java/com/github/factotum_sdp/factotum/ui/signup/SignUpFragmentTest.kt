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
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.setEmulatorGet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpFragmentTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    companion object {
        private var auth: FirebaseAuth = Firebase.auth

        @BeforeClass
        @JvmStatic
        fun setUpAuth() {
            setEmulatorGet()
            auth.useEmulator("10.0.2.2", 9099)

            MainActivity.setAuth(auth)
        }
    }

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
    fun signUpFormWithAllFieldsAndClick() {
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
        onView(withId(R.id.signup)).perform(click())
        FirebaseAuth.AuthStateListener {
            onView(withId(R.id.fragment_login_directors_parent)).check(
                ViewAssertions.matches(
                    ViewMatchers.isDisplayed()
                )
            )
        }
    }

    @Test
    fun clickOnSignUpLeadsToLoginFragment() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressBack()
        onView(withId(R.id.fragment_login_directors_parent)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }
}