package com.github.factotum_sdp.factotum.ui.roadbook

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.ui.roadbook.TouchCustomMoves.swipeRightTheRecordAt
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.github.factotum_sdp.factotum.utils.LocationUtils
import com.github.factotum_sdp.factotum.utils.LocationUtils.Companion.buttonTextAllow
import com.github.factotum_sdp.factotum.utils.PreferencesSetting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class DRecordDetailsFragmentTest {

    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            initFirebase()
        }
    }

    @Before
    fun toRoadBookFragment() {
        // Ensure "use RoadBook preferences" is disabled
        PreferencesSetting.setRoadBookPrefs(testRule)
        GeneralUtils.injectBossAsLoggedInUser(testRule)
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.roadBookFragment))
            .perform(click())
    }

    private fun toFragment() {
        PreferencesSetting.enableTouchClick()
        val destID = DestinationRecords.RECORDS[2].destID
        onView(withText(destID)).perform(click())
    }

    private fun toLastFragment() {
        onView(withId(R.id.list)).perform(
            click(),
            RecyclerViewActions.scrollToLastPosition<RoadBookViewAdapter.RecordViewHolder>(),
        )
        PreferencesSetting.enableTouchClick()
        onView(withText("01#1")).perform(click())
    }

    @Test
    fun displayedDetailsFragMatchTheClickedRecord() {
        toFragment()
        onView(withId(R.id.fragment_drecord_details_directors_parent)).check(matches(isDisplayed()))
        onView(withText(DestinationRecords.RECORDS[2].destID)).check(matches(isDisplayed()))
    }

    @Test
    fun displayedDetailsAreStillCorrectAfterEdit() {
        swipeRightTheRecordAt(2)
        val destID = "New"
        val notes = "Some notes about how SDP is fun"
        // Edit all fields :
        onView(withId(R.id.autoCompleteClientID))
            .perform(
                click(),
                clearText(),
                typeText("$destID "),
                closeSoftKeyboard()
            )

        onView(withId(R.id.editTextNotes))
            .perform(
                click(),
                typeText(notes),
                closeSoftKeyboard()
            )
        onView(withText(R.string.edit_dialog_update_b)).perform(click())
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_touch_click)).perform(click())
        onView(withText("$destID#1")).perform(click())
        onView(withId(R.id.fragment_drecord_details_directors_parent)).check(matches(isDisplayed()))
        onView(withText(destID)).check(matches(isDisplayed()))
        onView(withText(notes)).check(matches(isDisplayed()))
    }

    @Test
    fun swipeLeftOneTimeDisplaysMaps() {
        toFragment()
        onView(withId(R.id.fragment_maps_directors_parent)).check(doesNotExist())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        enableLocation()
        onView(withId(R.id.fragment_maps_directors_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun swipeLeftTwoTimesDisplaysContactDetails() {
        toFragment()
        onView(withId(R.id.contact_details_fragment)).check(doesNotExist())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        enableLocation()
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.contact_details_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun createDRecordWithCorrespondingUsernameDisplaysCorrectContactDetails() {
        RoadBookFragmentTest().newRecordWithId("01")
        toLastFragment()
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        enableLocation()
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.contact_username)).check(matches(withText("@01")))
    }

    // I think block in the CI due to the camera authorizations however it begins to be @Jules part,
    // and maybe a different Fragment will be here.
    /*private fun swipeLeftThreeTimesDisplaysPicture() {
        toFragment()
        onView(withId(R.id.fragment_picture_directors_parent)).check(doesNotExist())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.fragment_picture_directors_parent)).check(matches(isDisplayed()))
    }*/

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun swipeRightAfterSwipeLeftDisplaysInfo() = runTest {
        runBlocking {
            toFragment()
            onView(withId(R.id.viewPager)).perform(click())
            onView(withId(R.id.fragment_drecord_info_directors_parent)).check(matches(isDisplayed()))
            onView(withId(R.id.viewPager)).perform(swipeLeft())
            enableLocation()
            delay(500L)
        }
        onView(withId(R.id.fragment_drecord_info_directors_parent)).check(doesNotExist())
        onView(withId(R.id.viewPager)).perform(swipeRight())
        onView(withId(R.id.fragment_drecord_info_directors_parent)).check(matches(isDisplayed()))
    }

    private fun enableLocation() {
        if (LocationUtils.hasLocationPopUp()) {
            device.findObject(UiSelector().textContains(buttonTextAllow)).click()
        }
    }
}

