package com.github.factotum_sdp.factotum

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)

class MapsFragmentTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )
    @Test
    fun goesToSecondFragement(){
        val nextButton = onView(withId(R.id.button_first))
        nextButton.perform(click())
        onView(withId(R.id.maps_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun showsStartMarker(){
        val nextButton = onView(withId(R.id.button_first))
        nextButton.perform(click())
        val device = UiDevice.getInstance(getInstrumentation())
        val startMarker = device.findObject(UiSelector().descriptionContains("Start"))
        assert(startMarker.exists())
    }

    @Test
    fun showsDestinationMarker(){
        val nextButton = onView(withId(R.id.button_first))
        nextButton.perform(click())
        val device = UiDevice.getInstance(getInstrumentation())
        val endMarker = device.findObject(UiSelector().descriptionContains("Destination"))
        assertTrue(endMarker.exists())
    }

    @Test
    fun showsStartAndFinish(){
        val nextButton = onView(withId(R.id.button_first))
        nextButton.perform(click())
        val device = UiDevice.getInstance(getInstrumentation())
        val startMarker = device.findObject(UiSelector().descriptionContains("Start"))
        val startId = startMarker.contentDescription.substringAfter("of")
        val endMarker = device.findObject(UiSelector().descriptionContains("Destination"))
        val endId = endMarker.contentDescription.substringAfter("of")
        assertEquals(startId, endId)
    }

}