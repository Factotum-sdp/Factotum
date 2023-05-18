package com.github.factotum_sdp.factotum.ui.bossmap

import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.LocationClientFactory
import com.github.factotum_sdp.factotum.data.MockLocationClient
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.logout
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

private const val WAIT_TIME_UPDATE_LOCATION = 20000L

@RunWith(AndroidJUnit4::class)
class BossMapFragmentTest {


    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @get:Rule
    val permission = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            initFirebase()
            logout()
        }
    }

    @After
    fun tearDown() {
        logout()
    }


    @Test
    fun testBossMapFragmentWorksProperly(): Unit = runBlocking {
        LocationClientFactory.setMockClient(MockLocationClient())
        GeneralUtils.fillUserEntryAndEnterTheApp("courier@gmail.com", "123456")

        activateLocation()

        delay(WAIT_TIME_UPDATE_LOCATION)

        logout()

        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")

        goToBossMapFragment()
        //Check that in Firebase Database in "Location" the is some value
        GeneralUtils.getDatabase().reference.child("Location").get().addOnSuccessListener {
            assert(it.value != null)
        }
    }

    @Test
    fun testDeliveryStatusAreUpdated(){
        GeneralUtils.fillUserEntryAndEnterTheApp(UsersPlaceHolder.USER_BOSS.email, UsersPlaceHolder.USER_BOSS.password)
        goToBossMapFragment()
        val cuf = CameraUpdateFactory.newLatLng(LatLng(46.517719,6.569090))
        moveMapCamera(testRule, cuf)
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val mailboxBuaghiat = device.wait(Until.findObject(By.descContains("Mailbox")), 5000L)
        assert(mailboxBuaghiat != null)
        val mailboxX17 = device.findObject(By.descContains("Mailbox"))
        assert(mailboxX17 != null)

    }

    private fun goToBossMapFragment() {
        onView(withId(R.id.drawer_layout))
            .perform(open())
        onView(withId(R.id.bossMapFragment))
            .perform(click())
    }

    private fun logout() {
        onView(withId(R.id.drawer_layout))
            .perform(open())
        onView(withId(R.id.signoutButton))
            .perform(click())
    }

    private fun activateLocation() {
        onView(withId(R.id.location_switch)).perform(click())
    }

    private fun moveMapCamera(rule: ActivityScenarioRule<MainActivity>, cuf : CameraUpdate) {
        val latch = CountDownLatch(1)
        rule.scenario.onActivity {
            it.findViewById<MapView>(R.id.mapView).getMapAsync {
                it.moveCamera(cuf)
                latch.countDown()
            }
        }
        latch.await(1L, TimeUnit.SECONDS)

    }
}