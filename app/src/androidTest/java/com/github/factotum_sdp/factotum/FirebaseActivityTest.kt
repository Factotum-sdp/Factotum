package com.github.factotum_sdp.factotum

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.ActivityTestRule
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.After
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class FirebaseActivityTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        FirebaseActivity::class.java
    )

    //Execute one time before the test is launched
    companion object {

        private val database = Firebase.database

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            // With a DatabaseReference, write null to clear the database.
            database.useEmulator("10.0.2.2", 9000)
        }

        @After
        fun cleanUp() {
            // With a DatabaseReference, write null to clear the database.
            database.reference.setValue(null)
        }

    }

    @Test
    fun setAndGetTest() {
        // Set the email and phone number
        Espresso.onView(withId(R.id.editTextEmailAddress)).perform(ViewActions.typeText("jules.perrin@epfl.ch"))
            .perform(ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.editTextPhone)).perform(ViewActions.typeText("123-45-6789"))
            .perform(ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.buttonSet)).perform(ViewActions.click())

        // Get the email using the phone number
        // Clean the email field
        Espresso.onView(withId(R.id.editTextEmailAddress)).perform(ViewActions.clearText())
        Espresso.onView(withId(R.id.buttonGet)).perform(ViewActions.click())

        // Check that the email in the email field is the one we set
        Espresso.onView(withId(R.id.editTextEmailAddress))
            .check(ViewAssertions.matches(ViewMatchers.withText("jules.perrin@epfl.ch")))
    }

}