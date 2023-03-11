package com.github.factotum_sdp.factotum.ui.roadbook

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoadBookFragmentTest {
    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Test
    fun fabIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.fab))
            .check(matches(isDisplayed()))
    }

    @Test
    fun delButtonIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.fab_delete))
            .check(matches(isDisplayed()))
    }

    @Test
    fun roadBookIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.list))
            .check(matches(isDisplayed()))
    }

    @Test
    fun startingRecordsAreDisplayed() {
        for (i in 0..2)
            onView(withText(DestinationRecords.RECORDS[i].destName))
                .check(matches(isDisplayed()))
    }

    @Test
    fun pressingFabCreatesNewRecord() {
        onView(withText(DestinationRecords.RECORDS[0].destName))
            .check(matches(isDisplayed()))
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.list)).perform(
            click(),
            RecyclerViewActions.scrollToLastPosition<RoadBookViewAdapter.RecordViewHolder>(),
        )
        onView((withText(DestinationRecords.RECORD_TO_ADD.destName)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun pressingDelDeleteLastRecord() {
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.list)).perform(
            click(),
            RecyclerViewActions.scrollToLastPosition<RoadBookViewAdapter.RecordViewHolder>(),
        )
        onView((withText(DestinationRecords.RECORD_TO_ADD.destName)))
            .check(matches(isDisplayed()))
        onView(withId(R.id.fab_delete)).perform(click())
        onView((withText(DestinationRecords.RECORD_TO_ADD.destName)))
            .check(doesNotExist())
    }
}