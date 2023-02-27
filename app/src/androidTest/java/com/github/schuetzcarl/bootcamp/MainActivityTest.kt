package com.github.schuetzcarl.bootcamp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
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

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.core.AllOf.allOf


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val userName = "Carl"

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Test
    fun userNameEditTextIsCorrectlyEdited() {
        onView(withId(R.id.userNameEditText))
            .perform(typeText(userName), closeSoftKeyboard())
            .check(matches(withText(userName))
        )
    }

    @Test
    fun intentIsCorrectlyFired() {
        Intents.init()
        onView(withId(R.id.userNameEditText)).perform(typeText(userName), closeSoftKeyboard())
        onView(withId(R.id.validateButton)).perform(click())
        intended(
            allOf(
                hasComponent(GreetingActivity::class.java.name),
                hasExtra("userName", userName)
            )
        )
        Intents.release()
    }

    @Test
    fun endToEndCheckGreetingMessage() {
        onView(withId(R.id.userNameEditText)).perform(typeText(userName), closeSoftKeyboard())
        onView(withId(R.id.validateButton)).perform(click())
        onView(withId(R.id.greetingMessage)).check(matches(withText("Hello Carl")))
    }

    @Test
    fun endToEndCheckWithoutUserName() {
        onView(withId(R.id.validateButton)).perform(click())
        onView(withId(R.id.greetingMessage)).check(matches(withText("Hello ")))
    }
}