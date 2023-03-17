package com.github.factotum_sdp.factotum.ui.maps

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By.descContains
import androidx.test.uiautomator.By.textContains
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class MapsFragmentTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Before fun setUp(){
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.routeFragment))
            .perform(click())
    }
    @Test
    fun goesToSecondFragement(){
        val device = UiDevice.getInstance(getInstrumentation())
        device.findObject(UiSelector().textContains("->")).click()
        val nextButton = onView(withId(R.id.button_next))
        nextButton.perform(click())
        onView(withId(R.id.fragment_maps_directors_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun showsDestinationMarker(){
        val device = UiDevice.getInstance(getInstrumentation())
        device.findObject(UiSelector().textContains("->")).click()
        val nextButton = onView(withId(R.id.button_next))
        nextButton.perform(click())
        val endMarker = device.findObject(UiSelector().descriptionContains("Destination"))
        assertTrue(endMarker.exists())
    }

    @Test
    fun showsAllDest(){
        val device = UiDevice.getInstance(getInstrumentation())
        val nbRoutes = device.findObjects(textContains("->")).size
        val showAll = onView(withId(R.id.button_all))
        showAll.perform(click())
        var endMarker = device.findObjects(descContains("Destination"))
        val endTime = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
        while(endMarker.size == 0 && System.nanoTime() < endTime) {
            endMarker = device.findObjects(descContains("Destination"))
        }
        assertEquals(nbRoutes, endMarker.size)
    }

    @Test
    fun runLaunchesMaps(){
        Intents.init()
        val device = UiDevice.getInstance(getInstrumentation())
        device.findObject(UiSelector().textContains("->")).click()
        val runButton = onView(withId(R.id.button_run))
        runButton.perform(click())
        intended(allOf(hasAction(Intent.ACTION_VIEW),
            toPackage(RouteFragment.MAPS_PKG)
        ))
        Intents.release()

    }


}