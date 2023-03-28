package com.github.factotum_sdp.factotum.ui.maps

import androidx.navigation.fragment.NavHostFragment
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class MapsPermissionTest {

    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val buttonTextAllow = when(Locale.getDefault().language){
        Locale.FRENCH.language -> "Uniquement cette fois-ci"
        else -> "Only this time"
    }
    val buttonTextDeny = when(Locale.getDefault().language){
        Locale.FRENCH.language -> "Refuser"
        else -> "Deny"
    }
    val device = UiDevice.getInstance(instrumentation)
    lateinit var map : GoogleMap

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )


    @Before
    fun setUp(){
        onView(ViewMatchers.withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(ViewMatchers.withId(R.id.routeFragment))
            .perform(ViewActions.click())
        onData(Matchers.anything())
            .inAdapterView(ViewMatchers.withId(R.id.list_view_routes)).atPosition(0).perform(
            ViewActions.click()
        )
        onView(ViewMatchers.withId(R.id.button_next)).perform(ViewActions.click())
    }

    @Test
    fun permissionAskPopUp(){
        assertTrue(device.findObject(UiSelector().textContains(buttonTextAllow)).exists())
    }

    @Test
    fun permissionAllowShowsLocation(){
        device.findObject(UiSelector().textContains(buttonTextAllow)).click()
        assertTrue(checkLocationEnabled(testRule))
    }

    @Test
    fun permissionDenyHidesLocation(){
        device.findObject(UiSelector().textContains(buttonTextDeny)).click()
        assertFalse(checkLocationEnabled(testRule))
    }
    private fun checkLocationEnabled(rule:  ActivityScenarioRule<MainActivity>) : Boolean {
        var isEnabled = false
        val latch = CountDownLatch(1)
        rule.scenario.onActivity {
            val navHostFragment = it.supportFragmentManager.primaryNavigationFragment as NavHostFragment
            val mapsFragment = navHostFragment.childFragmentManager.fragments[0]
            val mapFragment = mapsFragment.childFragmentManager.fragments[0] as SupportMapFragment
            mapFragment.getMapAsync{
                isEnabled = it.isMyLocationEnabled
                latch.countDown()
            }
        }
        latch.await()
        return isEnabled
    }
}
