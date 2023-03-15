package com.github.factotum_sdp.factotum.ui.roadbook

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture

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
            onView(withText(DestinationRecords.RECORDS[i].destID))
                .check(matches(isDisplayed()))
    }

    @Test
    fun pressingFabCreatesNewRecord() {
        onView(withText(DestinationRecords.RECORDS[0].destID))
            .check(matches(isDisplayed()))
        onView(withId(R.id.fab)).perform(click())
        // Scroll to last position to see if the new record is displayed at the end
        onView(withId(R.id.list)).perform(
            click(),
            RecyclerViewActions.scrollToLastPosition<RoadBookViewAdapter.RecordViewHolder>(),
        )
        onView((withText(DestinationRecords.RECORD_TO_ADD.destID)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun pressingDelDeleteLastRecord() {
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.list)).perform(
            click(),
            RecyclerViewActions.scrollToLastPosition<RoadBookViewAdapter.RecordViewHolder>(),
        )
        // Record just added is displayed at the end of the list
        onView((withText(DestinationRecords.RECORD_TO_ADD.destID)))
            .check(matches(isDisplayed()))
        onView(withId(R.id.fab_delete)).perform(click())

        // Record added previously is now deleted
        onView((withText(DestinationRecords.RECORD_TO_ADD.destID)))
            .check(doesNotExist())
    }

    private val db = Firebase.database.reference

    @Test
    fun roadBookIsBackedUpCorrectly() {
        //Navigate to an other Fragment
        val date = Calendar.getInstance().time
        val ref = db
            .child("Sheet-shift")
            .child(SimpleDateFormat.getDateInstance().format(date))

        onView(withId(R.id.fab)).perform(click()) // Add 1 record

        // Navigate out of the RoadBookFragment
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.directoryFragment))
            .perform(click())
        onView(withId(R.id.fragment_directory_directors_parent))
            .check(matches(isDisplayed()))

        val future = CompletableFuture<List<String>>()
        ref.get().addOnSuccessListener {
            if (it.value == null) {
                future.completeExceptionally(NoSuchFieldException())
            }
            else {
                val ls: List<String> = it.value as List<String>
                future.complete(ls)
                assert(ls.size == DestinationRecords.RECORDS.size + 1)
            }
        }.addOnFailureListener {
            assert(false)
            future.completeExceptionally(it)
        }
    }
}