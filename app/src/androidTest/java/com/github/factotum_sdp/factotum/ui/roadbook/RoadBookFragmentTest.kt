package com.github.factotum_sdp.factotum.ui.roadbook

import android.view.InputDevice
import android.view.MotionEvent
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow
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
import org.junit.Before
import org.junit.BeforeClass
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

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            val database = Firebase.database
            //database.useEmulator("10.0.2.2", 9000)
            MainActivity.setDatabase(database)
        }
    }


    @Before
    fun toRoadBookFragment() {
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.roadBookFragment))
            .perform(click())
    }

    @Test
    fun radioButtonsAreAccessible() {
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rbLabelDragDrop)).check(matches(isDisplayed()))
        onView(withText(R.string.rbLabelSwipeEdition)).check(matches(isDisplayed()))
    }

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

    @Test
    fun roadBookIsBackedUpCorrectly() {
        //Navigate to an other Fragment
        val date = Calendar.getInstance().time
        val ref = MainActivity.getDatabase().reference
            .child("Sheet-shift")
            .child(SimpleDateFormat.getDateInstance().format(date))

        onView(withId(R.id.fab)).perform(click()) // Add 1 record

        // Navigate out of the RoadBookFragment
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.routeFragment))
            .perform(click())
        onView(withId(R.id.fragment_route_directors_parent))
            .check(matches(isDisplayed()))

        // Our target value to fetch
        // is represented as a List<String> in Firebase
        val future = CompletableFuture<List<String>>()

        ref.get().addOnSuccessListener {
            if (it.value == null) {
                // Set an exception in the future if our target value is not found in Firebase
                future.completeExceptionally(NoSuchFieldException())
            }
            else { // Necessary cast to access List methods
                val ls: List<String> = it.value as List<String>
                future.complete(ls)
                assert(ls.size == DestinationRecords.RECORDS.size + 1)
            }
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
    }

    private fun swipeRightTheRecordAt(pos: Int) {
        onView(withId(R.id.list)).perform(
            longClick(),
            RecyclerViewActions.actionOnItemAtPosition<RoadBookViewAdapter.RecordViewHolder>(
                pos, GeneralSwipeAction(
                    Swipe.SLOW,
                    {
                        val xy = IntArray(2).also { ar -> it.getLocationOnScreen(ar) }
                        val x = xy[0] + (it.width - 1) * 0.5f
                        val y = xy[1] + (it.height - 1) * 1f
                        floatArrayOf(x, y)
                    },
                    {
                        val xy = IntArray(2).also { ar -> it.getLocationOnScreen(ar) }
                        val x = xy[0] + (it.width - 1) * 2f
                        val y = xy[1] + (it.height - 1) * 1f
                        floatArrayOf(x, y)
                    },
                    Press.PINPOINT
                )
            )
        )
    }

    @Test
    fun swipeRightTriggersEditScreen() {
        swipeRightTheRecordAt(4)
        onView(withText(R.string.editDialogTitle)).check(matches(isDisplayed()))
    }

    @Test
    fun editARecordDestIDWorks() {
        swipeRightTheRecordAt(2)
        onView(withText(R.string.editDialogTitle)).check(matches(isDisplayed()))
        onView(withText("X17")).perform(typeText("edited"))
        onView(withText(R.string.editDialogUpdateB)).perform(click())
        onView((withText("X17edited"))).check(matches(isDisplayed()))
    }
    @Test
    fun cancelOnRecordEditionWorks() {
        swipeRightTheRecordAt(2)
        onView(withText(R.string.editDialogTitle)).check(matches(isDisplayed()))
        onView(withText("X17")).perform(typeText("edited"))
        onView(withText(R.string.editDialogCancelB)).perform(click())
        // Same record is displayed, without the edited text happened to his destRecordID
        onView((withText("X17"))).check(matches(isDisplayed()))

    }

    @Test
    fun dragAndDropByInjectionIsWorking() {
        // Not possible for the moment in to cover the onMove() of the ItemtTouchHelper Callback,
        // However here, I simulate its behavior to triggers the ViewModel change.

        onView(withText("X17")).check(isCompletelyAbove(withText("More1")))
        testRule.scenario.onActivity {

            val fragment = it.supportFragmentManager.fragments.first() as NavHostFragment

            fragment.let {
                val curr = it.childFragmentManager.primaryNavigationFragment as RoadBookFragment
                val recyclerView = curr.view!!.findViewById<RecyclerView>(R.id.list)
                recyclerView.adapter?.notifyItemMoved(2,3)
                curr.getRBViewModelForTest().swapRecords(2,2)
                recyclerView.adapter?.notifyItemMoved(3,4)
                curr.getRBViewModelForTest().swapRecords(3,3)
                curr.getRBViewModelForTest().pushSwapsResult()
            }
        }

        onView(withText("X17")).check(matches(isDisplayed()))
        Thread.sleep(4000) // This one is needed to let the Screen enough time to be updated
        onView(withText("X17")).check(isCompletelyBelow(withText("More1")))
    }
}