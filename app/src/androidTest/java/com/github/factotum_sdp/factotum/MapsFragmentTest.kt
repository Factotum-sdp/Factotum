package com.github.factotum_sdp.factotum

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By.clazz
import androidx.test.uiautomator.By.descContains
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObject2
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
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.routeFragment))
            .perform(click())
        val nextButton = onView(withId(R.id.button_first))
        nextButton.perform(click())
        onView(withId(R.id.maps_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun showsStartMarker(){
        val device = UiDevice.getInstance(getInstrumentation())
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.routeFragment))
            .perform(click())
        device.findObject(UiSelector().textContains("->")).click()
        val nextButton = onView(withId(R.id.button_first))
        nextButton.perform(click())
        val startMarker = device.findObject(UiSelector().descriptionContains("Start"))
        assert(startMarker.exists())
    }

    @Test
    fun showsDestinationMarker(){
        val device = UiDevice.getInstance(getInstrumentation())
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.routeFragment))
            .perform(click())
        device.findObject(UiSelector().textContains("->")).click()
        val nextButton = onView(withId(R.id.button_first))
        nextButton.perform(click())
        val endMarker = device.findObject(UiSelector().descriptionContains("Destination"))
        assertTrue(endMarker.exists())
    }

    @Test
    fun showsStartAndFinish(){
        val device = UiDevice.getInstance(getInstrumentation())
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.routeFragment))
            .perform(click())
        device.findObject(UiSelector().textContains("->")).click()
        val nextButton = onView(withId(R.id.button_first))
        nextButton.perform(click())
        val startMarker = device.findObject(UiSelector().descriptionContains("Start"))
        val startId = startMarker.contentDescription.substringAfter("of")
        val endMarker = device.findObject(UiSelector().descriptionContains("Destination"))
        val endId = endMarker.contentDescription.substringAfter("of")
        assertEquals(startId, endId)
    }
    @Test
    fun showsAll(){
        val device = UiDevice.getInstance(getInstrumentation())
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.routeFragment))
            .perform(click())
        val showAll = onView(withId(R.id.button_all))
        showAll.perform(click())
        val startMarker = device.findObjects(descContains("Start"))
        val startIds = startMarker.map { it.contentDescription.substringAfter("of") }
        val endMarker = device.findObjects(descContains("Destination"))
        val endIds = endMarker.map { it.contentDescription.substringAfter("of") }
        assertEquals(startMarker.size, endMarker.size)
        assertEquals(startIds.toSet(), endIds.toSet())
    }


}