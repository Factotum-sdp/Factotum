package com.github.factotum_sdp.factotum.ui.roadbook

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
        onView(withText(R.string.rb_label_dragdrop)).check(matches(isDisplayed()))
        onView(withText(R.string.rb_label_swiper_edition)).check(matches(isDisplayed()))
        onView(withText(R.string.rb_label_swipel_deletion)).check(matches(isDisplayed()))
    }

    @Test
    fun fabIsCorrectlyDisplayedOnFirstView() {
        onView(withId(R.id.fab))
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

    // ============================================================================================
    // ===================================== Delete Tests =========================================

    @Test
    fun swipeLeftDeleteRecord() {
        // Record just added is displayed at the end of the list
        onView((withText(DestinationRecords.RECORDS[2].destID)))
            .check(matches(isDisplayed()))

        swipeLeftTheRecordAt(2)
        onView(withText(R.string.delete_dialog_title))
        onView(withText(R.string.delete_confirm_button_label)).perform(click())

        // Record added previously is now deleted
        onView((withText(DestinationRecords.RECORDS[2].destID)))
            .check(doesNotExist())
    }

    @Test
    fun swipeLeftButCancelLetTheRecord() {

        // Record just added is displayed at the end of the list
        onView((withText(DestinationRecords.RECORDS[2].destID)))
            .check(matches(isDisplayed()))

        swipeLeftTheRecordAt(2)
        onView(withText(R.string.delete_dialog_title))
        onView(withText(R.string.delete_cancel_button_label)).perform(click())

        // Record added previously is now deleted
        onView((withText(DestinationRecords.RECORDS[2].destID)))
            .check(matches(isDisplayed()))
    }

    // ============================================================================================
    // ================================== Add record Tests ========================================

    /*
    @Test
    fun newRecordIsDisplayedAtTheEnd() {
        onView(withText(DestinationRecords.RECORDS[0].destID))
            .check(matches(isDisplayed()))
        // Add a new record
        newRecord()
        // Scroll to last position to see if the new record is displayed at the end
        onView(withId(R.id.list)).perform(
            click(),
            RecyclerViewActions.scrollToLastPosition<RoadBookViewAdapter.RecordViewHolder>(),
        )
        Thread.sleep(3000)
        onView((withText("New#1")))
            .check(matches(isDisplayed()))
    }*/

    // ============================================================================================
    // ================================== Update to Database Tests ================================

    /*
    @Test
    fun roadBookIsBackedUpCorrectly() {
        val date = Calendar.getInstance().time
        val ref = Firebase.database.reference
            .child("Sheet-shift")
            .child(SimpleDateFormat.getDateInstance().format(date))

        // Add 1 record
        newRecord()

        // Navigate out of the RoadBookFragment
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.directoryFragment))
            .perform(click())
        onView(withId(R.id.fragment_directory_directors_parent))
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

     */

    // ============================================================================================
    // ===================================== Edit Tests ===========================================
    @Test
    fun swipeRightTriggersEditScreen() {
        swipeRightTheRecordAt(4)
        onView(withText(R.string.edit_dialog_update_b)).check(matches(isDisplayed()))
        onView(withText(R.string.edit_dialog_cancel_b)).check(matches(isDisplayed()))
    }

    @Test
    fun editARecordDestIDWorks() {
        swipeRightTheRecordAt(2)
        onView(withText("X17")).perform(typeText("edited"))
        onView(withText(R.string.edit_dialog_update_b)).perform(click())
        onView((withText("X17edited#1"))).check(matches(isDisplayed()))
    }

    @Test
    fun changeEveryFieldsWorks() {
        onView(withText(DestinationRecords.RECORDS[2].destID)).check(matches(isDisplayed()))
        onView(withText(DestinationRecords.RECORDS[2].destID)).check(matches(isDisplayed()))
        onView(withText(DestinationRecords.RECORDS[2].destID)).check(matches(isDisplayed()))
        onView(withText(DestinationRecords.RECORDS[2].destID)).check(matches(isDisplayed()))
        swipeRightTheRecordAt(2)
    }

    @Test
    fun cancelOnRecordEditionWorks() {
        swipeRightTheRecordAt(2)
        onView(withText("X17")).perform(typeText("edited"))
        onView(withText(R.string.edit_dialog_cancel_b)).perform(click())
        // Same record is displayed, without the edited text happened to his destRecordID
        onView((withText("X17#1"))).check(matches(isDisplayed()))
    }

    // ============================================================================================
    // ================================== Move records Tests ======================================

    @Test
    fun dragAndDropByInjectionIsWorking() {
        // Not possible for the moment in to cover the onMove() of the ItemtTouchHelper Callback,
        // However here, I simulate its behavior to triggers the ViewModel change.

        onView(withText("X17#1")).check(isCompletelyAbove(withText("More#1")))
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

        onView(withText("X17#1")).check(matches(isDisplayed()))
        Thread.sleep(4000) // This one is needed to let the Screen enough time to be updated
        onView(withText("X17#1")).check(isCompletelyBelow(withText("More#1")))
    }

    // ============================================================================================
    // ===================================== Helpers ==============================================

    private fun newRecord() {
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.autoCompleteClientID))
            .perform(click(), typeText("New"), closeSoftKeyboard())
        onView(withText(R.string.edit_dialog_update_b)).perform(click())
    }

    private fun swipeRightTheRecordAt(pos: Int) {
        swipeSlowActionOnRecyclerList(pos, 0.5f, 1f, 2f, 1f)
    }

    private fun swipeLeftTheRecordAt(pos: Int) {
        swipeSlowActionOnRecyclerList(pos, 0.5f, 1f, -1f, 1f)
    }

    private fun swipeSlowActionOnRecyclerList(pos: Int, startX: Float, startY: Float, endX: Float, endY: Float) {
        onView(withId(R.id.list)).perform(
            longClick(),
            RecyclerViewActions.actionOnItemAtPosition<RoadBookViewAdapter.RecordViewHolder>(
                pos, GeneralSwipeAction(
                    Swipe.SLOW,
                    {
                        val xy = IntArray(2).also { ar -> it.getLocationOnScreen(ar) }
                        val x = xy[0] + (it.width - 1) * startX
                        val y = xy[1] + (it.height - 1) * startY
                        floatArrayOf(x, y)
                    },
                    {
                        val xy = IntArray(2).also { ar -> it.getLocationOnScreen(ar) }
                        val x = xy[0] + (it.width - 1) * endX
                        val y = xy[1] + (it.height - 1) * endY
                        floatArrayOf(x, y)
                    },
                    Press.PINPOINT
                )
            )
        )
    }
}