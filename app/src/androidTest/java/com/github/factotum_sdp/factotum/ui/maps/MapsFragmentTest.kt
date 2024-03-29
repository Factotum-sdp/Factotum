package com.github.factotum_sdp.factotum.ui.maps

import android.Manifest
import androidx.navigation.fragment.NavHostFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By.descContains
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.logout
import com.google.android.gms.maps.SupportMapFragment
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.*
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
    val permission: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            initFirebase()
            logout()
        }

        @BeforeClass
        @JvmStatic
        fun dismissANRSystemDialog() {
            val device = UiDevice.getInstance(getInstrumentation())
            val waitButton = device.findObject(UiSelector().textContains("wait"))
            if (waitButton.exists()) {
                waitButton.click()
            }
        }
    }

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Before
    fun setUp() {
        GeneralUtils.fillUserEntryAndEnterTheApp(UsersPlaceHolder.USER_COURIER.email, UsersPlaceHolder.USER_COURIER.password)
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.mapsFragment))
            .perform(click())
    }

    @After
    fun tearDown() {
        logout()
    }


    @Test
    fun permissionAllowShowLocation() {
        assertTrue(checkLocationEnabled(testRule))
    }
    /*

    @Test
    fun showsDestinationMarker() = runBlocking {
        delay(5000)
        val endMarker = device.findObject(UiSelector().descriptionContains("Destination"))
        assertTrue(endMarker.exists())
    }

    @Test
    fun showsAllDest() = runBlocking {
        delay(5000)
        var endMarker = device.findObjects(descContains("Destination"))
        val endTime = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
        while (endMarker.size == 0 && System.nanoTime() < endTime) {
            endMarker = device.findObjects(descContains("Destination"))
        }
        assertTrue(endMarker.size != 0)
    } */



    private fun checkLocationEnabled(rule: ActivityScenarioRule<MainActivity>): Boolean {
        var isEnabled = false
        val latch = CountDownLatch(1)
        rule.scenario.onActivity { it ->
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
