package com.github.factotum_sdp.factotum

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.core.AllOf
import org.junit.Rule
import org.junit.Test

class FirebaseUIActivityTest {
    @get:Rule
    var testRule = ActivityScenarioRule(
        FirebaseUIActivity::class.java
    )

    @Test
    fun correctLogIn(){
        val firebaseUI :FirebaseUIActivity  = FirebaseUIActivity()
        firebaseUI.createSignInIntent()


    }
}