package com.github.factotum_sdp.factotum.ui.roadbook

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.ui.roadbook.TouchCustomMoves.swipeRightTheRecordAt
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class DRecordDetailsFragmentTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Before
    fun toRoadBookFragment() {
        testRule.scenario.onActivity {
            setPrefs(RoadBookFragmentTest.SWIPE_L_SHARED_KEY, it, true)
            setPrefs(RoadBookFragmentTest.SWIPE_R_SHARED_KEY, it, true)
            setPrefs(RoadBookFragmentTest.DRAG_N_DROP_SHARED_KEY, it, true)
            setPrefs(RoadBookFragmentTest.TOUCH_CLICK_SHARED_KEY, it, false)
        }
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.roadBookFragment))
            .perform(click())
        //wait for the previous snackbar to disappear
        Thread.sleep(5000)
    }

    private fun setPrefs(sharedKey: String, activity: MainActivity, value: Boolean) {
        val sp = activity.getSharedPreferences(sharedKey, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putBoolean(sharedKey, value)
        edit.apply()
    }

    @Test
    fun displayedDetailsFragMatchTheClickedRecord() {
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_touch_click)).perform(click())
        val destID = DestinationRecords.RECORDS[2].destID
        onView(withText(destID)).perform(click())
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
}