package com.github.factotum_sdp.factotum.ui.bossmap

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.logout
import com.github.factotum_sdp.factotum.utils.LocationUtils
import com.github.factotum_sdp.factotum.utils.LocationUtils.Companion.buttonTextAllow
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val WAIT_TIME_UPDATE_LOCATION = 20000L

@RunWith(AndroidJUnit4::class)
class BossMapFragmentTest {


    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            initFirebase()
            GeneralUtils.logout()
        }
    }


    @Test
    fun testBossMapFragmentWorksProperly(): Unit = runBlocking {
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
}