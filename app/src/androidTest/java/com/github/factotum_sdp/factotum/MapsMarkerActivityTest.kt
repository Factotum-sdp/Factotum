package com.github.factotum_sdp.factotum

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapsMarkerActivityTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MapsMarkerActivity::class.java)

    @Test
    fun testMapIsDisplayed() {
        // Verify that the map fragment is displayed
        onView(withId(R.id.map))
            .check(matches(isDisplayed()))
    }
}