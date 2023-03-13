package com.github.factotum_sdp.factotum

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.ui.login.LoginFragment

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.core.AllOf.allOf
import org.junit.Assert.assertTrue


@RunWith(AndroidJUnit4::class)
class LoginMainActivityTest {

    @Test
    fun testLoginFragmentIsShown() {
        val scenario = launchFragmentInContainer<LoginFragment>()

        scenario.onFragment { fragment ->
            assertTrue(fragment.isVisible)
        }
    }
}