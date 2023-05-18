package com.github.factotum_sdp.factotum.ui.maps

import android.Manifest
import android.content.Intent
import androidx.navigation.fragment.NavHostFragment
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
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By.descContains
import androidx.test.uiautomator.By.textContains
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.injectBossAsLoggedInUser
import com.google.android.gms.maps.SupportMapFragment
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.startsWith
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
//used to force first the tests about permissions
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MapsFragmentTest {

    val device = UiDevice.getInstance(getInstrumentation())

    @get:Rule
    val permission = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    companion object {

        const val FIRST_ROUTE_NAME_PREFIX = "BC"

        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            initFirebase()
        }
    }

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Before
    fun setUp() {
        injectBossAsLoggedInUser(testRule)
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.routeFragment))
            .perform(click())
    }


    @Test
    fun permissionAllowShowLocation() {
        onView(withText(startsWith(FIRST_ROUTE_NAME_PREFIX))).perform(click())
        val nextButton = onView(withId(R.id.button_next))
        nextButton.perform(click())
        assertTrue(checkLocationEnabled(testRule))
    }

    @Test
    fun goesToSecondFragment() {
        onView(withText(startsWith(FIRST_ROUTE_NAME_PREFIX))).perform(click())
        val nextButton = onView(withId(R.id.button_next))
        nextButton.perform(click())
        onView(withId(R.id.fragment_maps_directors_parent)).check(matches(isDisplayed()))
    }


    @Test
    fun showsDestinationMarker() {
        onView(withText(startsWith(FIRST_ROUTE_NAME_PREFIX))).perform(click())
        val nextButton = onView(withId(R.id.button_next))
        nextButton.perform(click())
        val endMarker = device.findObject(UiSelector().descriptionContains("Destination"))
        assertTrue(endMarker.exists())
    }

    @Test
    fun showsAllDest() {
        val nbRoutes = device.findObjects(textContains("->")).size
        val showAll = onView(withId(R.id.button_all))
        showAll.perform(click())
        var endMarker = device.findObjects(descContains("Destination"))
        val endTime = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
        while (endMarker.size == 0 && System.nanoTime() < endTime) {
            endMarker = device.findObjects(descContains("Destination"))
        }
        assertEquals(nbRoutes, endMarker.size)
    }

    @Test
    fun runLaunchesMaps() {
        Intents.init()
        onView(withText(startsWith(FIRST_ROUTE_NAME_PREFIX))).perform(click())
        val runButton = onView(withId(R.id.button_run))
        runButton.perform(click())
        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                toPackage(RouteFragment.MAPS_PKG)
            )
        )
        Intents.release()

    }

    private fun checkLocationEnabled(rule: ActivityScenarioRule<MainActivity>): Boolean {
        var isEnabled = false
        val latch = CountDownLatch(1)
        rule.scenario.onActivity {
            val navHostFragment =
                it.supportFragmentManager.primaryNavigationFragment as NavHostFragment
            val mapsFragment = navHostFragment.childFragmentManager.fragments[0]
            val mapFragment = mapsFragment.childFragmentManager.fragments[0] as SupportMapFragment
            mapFragment.getMapAsync {
                isEnabled = it.isMyLocationEnabled
                latch.countDown()
            }
        }
        latch.await(1L, TimeUnit.SECONDS)
        return isEnabled
    }


}
