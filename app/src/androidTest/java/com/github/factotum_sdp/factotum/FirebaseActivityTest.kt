package com.github.factotum_sdp.factotum

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
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

    companion object {
        private val database = Firebase.database

        // This method is only call once before all
        // of the tests of the class
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            // Connect to the emulator database
            database.useEmulator("10.0.2.2", 9000)
        }

        // Clean up the database after each test
        @After
        fun cleanUp() {
            // With a DatabaseReference, write null to clear the database.
            database.reference.setValue(null)
        }

    }

    @Test
    fun setAndGetTest() {
        // Tap the email and the phone number and click on the set button
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