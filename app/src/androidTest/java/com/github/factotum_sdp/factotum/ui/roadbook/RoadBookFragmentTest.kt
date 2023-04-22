package com.github.factotum_sdp.factotum.ui.roadbook

import android.Manifest
import android.content.Context
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
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
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.ui.roadbook.TouchCustomMoves.swipeLeftTheRecordAt
import com.github.factotum_sdp.factotum.ui.roadbook.TouchCustomMoves.swipeRightTheRecordAt
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
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
    val coarseLocationrule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    @get:Rule
    val fineLocationRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val foreGroundService = GrantPermissionRule.grant(Manifest.permission.FOREGROUND_SERVICE)



    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    companion object {
        const val SWIPE_L_SHARED_KEY = "SwipeLeftButton"
        const val SWIPE_R_SHARED_KEY = "SwipeRightButton"
        const val DRAG_N_DROP_SHARED_KEY = "DragNDropButton"
        const val TOUCH_CLICK_SHARED_KEY = "TouchClickButton"
        const val SHOW_ARCHIVED_KEY = "ShowArchived"
        const val WORST_REFRESH_TIME = 2000L

        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            val database = Firebase.database
            database.useEmulator("10.0.2.2", 9000)
            MainActivity.setDatabase(database)
        }
    }
    private fun setPrefs(sharedKey: String, activity: MainActivity, value: Boolean) {
        val sp = activity.getSharedPreferences(sharedKey, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putBoolean(sharedKey, value)
        edit.apply()
    }

    @Before
    fun toRoadBookFragment() {
        testRule.scenario.onActivity {
            setPrefs(SWIPE_L_SHARED_KEY, it, true)
            setPrefs(SWIPE_R_SHARED_KEY, it, true)
            setPrefs(DRAG_N_DROP_SHARED_KEY, it, true)
            setPrefs(TOUCH_CLICK_SHARED_KEY, it, false)
            setPrefs(SHOW_ARCHIVED_KEY, it, false)
        }
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
        // Record is there
        onView((withText(DestinationRecords.RECORDS[2].destID)))
            .check(matches(isDisplayed()))

        swipeLeftTheRecordAt(2)
        onView(withText(R.string.delete_dialog_title))
        onView(withText(R.string.swipeleft_confirm_button_label)).perform(click())

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
        onView(withText(R.string.swipeleft_cancel_button_label)).perform(click())

        // Record added previously is now deleted
        onView((withText(DestinationRecords.RECORDS[2].destID)))
            .check(matches(isDisplayed()))
    }

    // ============================================================================================
    // ================================== Add record Tests ========================================
    @Test
    fun newRecordIsDisplayedAtTheEnd() {
        val clientID = DestinationRecords.RECORD_TO_ADD.clientID
        onView(withText(DestinationRecords.RECORDS[0].destID))
            .check(matches(isDisplayed()))
        // Add a new record
        newRecord()
        // Scroll to last position to see if the new record is displayed at the end
        onView(withId(R.id.list)).perform(
            click(),
            RecyclerViewActions.scrollToLastPosition<RoadBookViewAdapter.RecordViewHolder>(),
        )
        onView((withText("$clientID#1")))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addWithWrongFormatISAborted() {
        val clientID = DestinationRecords.RECORD_TO_ADD.clientID
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.autoCompleteClientID))
            .perform(click(), typeText("$clientID "), closeSoftKeyboard())
        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerCancelBLabel)).perform(click())

        onView(withId(R.id.editTextTimestamp)).perform(typeText("2222"))
        onView(withText(R.string.edit_dialog_cancel_b)).perform(click())

        onView(withId(R.id.list)).perform(
            click(),
            RecyclerViewActions.scrollToLastPosition<RoadBookViewAdapter.RecordViewHolder>(),
        )
        onView((withText("$clientID#1")))
            .check(doesNotExist())
    }

    // ============================================================================================
    // ================================== Update to Database Tests ================================

    @Test
    fun roadBookIsBackedUpCorrectly() {
        val date = Calendar.getInstance().time
        val ref = MainActivity.getDatabase().reference
            .child("Sheet-shift")
            .child(SimpleDateFormat.getDateInstance().format(date))

        // Add 1 record
        newRecord()

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
    fun editWithAnExistingClientID() { // Means clientID already used by one record in the roadbook
        val clientID = DestinationRecords.RECORDS[2].clientID
        swipeRightTheRecordAt(3) // edit one record displayed below which has another clientID
        onView(withId(R.id.autoCompleteClientID)).perform(clearText(), typeText(clientID)) // set for the same client that different record
        onView(withText(R.string.edit_dialog_update_b)).perform(click())
        Thread.sleep(WORST_REFRESH_TIME)
        onView(withText("$clientID#2")).check(matches(isDisplayed())) // unique destID is computed
    }

    @Test
    fun editWithAWrongFormatIsAborted() {
        swipeRightTheRecordAt(2)
        onView(withId(R.id.autoCompleteClientID))
            .perform(click(), typeText("edited"), closeSoftKeyboard())
        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerCancelBLabel)).perform(click())

        onView(withId(R.id.editTextTimestamp)).perform(typeText("2222"))
        onView(withText(R.string.edit_dialog_cancel_b)).perform(click())

        onView((withText("X17edited#1")))
            .check(doesNotExist())
    }

    @Test
    fun updateEditWithNoChange() {
        swipeRightTheRecordAt(2)
        onView(withText(R.string.edit_dialog_update_b)).perform(click())
        onView((withText(DestinationRecords.RECORDS[2].destID)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun eraseOnTimePickerResetTimestamp() {
        val cal: Calendar = Calendar.getInstance()
        onView(withText(startsWith("arrival : ${timestampUntilHourFormat(cal)}"))).check(matches(isDisplayed()))
        eraseFirstRecTimestamp()
        onView(withText(startsWith("arrival : ${timestampUntilHourFormat(cal)}"))).check(doesNotExist())
    }

    @Test
    fun cancelOnTimePickerWorks() {
        eraseFirstRecTimestamp()
        swipeRightTheRecordAt(2)
        onView(withId(R.id.autoCompleteClientID))
            .perform(click(), clearText(),  typeText("New "), closeSoftKeyboard())

        val cal: Calendar = Calendar.getInstance()
        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerCancelBLabel)).perform(click())
        onView(withText(R.string.edit_dialog_update_b)).perform(click())
        Thread.sleep(WORST_REFRESH_TIME)

        onView(withText(startsWith("arrival : ${timestampUntilHourFormat(cal)}"))).check(doesNotExist())
    }

    @Test
    fun editEveryFieldsWorks() {
        onView(withText(DestinationRecords.RECORDS[2].destID)).check(matches(isDisplayed()))
        swipeRightTheRecordAt(2)

        // Edit all fields :
        onView(withId(R.id.autoCompleteClientID))
            .perform(click(), clearText(),  typeText("NewEvery "), closeSoftKeyboard())

        val cal: Calendar = Calendar.getInstance()
        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click()) // edited through TimePicker
        onView(withId(R.id.editTextWaitingTime))
            .perform(click(), clearText(), typeText("5"), closeSoftKeyboard())
        onView(withId(R.id.editTextRate))
            .perform(click(), clearText(), typeText("7"), closeSoftKeyboard())
        onView(withId(R.id.multiAutoCompleteActions))
            .perform(click(), clearText(), typeText("deliver, pick, pick, contact"), closeSoftKeyboard())
        onView(withId(R.id.editTextNotes))
            .perform(click(),  typeText("Some notes about how SDP is fun"), closeSoftKeyboard())

        // Confirm edition :
        onView(withText(R.string.edit_dialog_update_b)).perform(click())

        // Check edited record is corretly displayed :
        Thread.sleep(WORST_REFRESH_TIME)
        onView(withText("NewEvery#1")).check(matches(isDisplayed()))

        eraseFirstRecTimestamp() // For having no ambiguity btw Timestamp on screen
        onView(withText(startsWith("arrival : ${timestampUntilHourFormat(cal)}"))).check(matches(isDisplayed()))
        onView(withText("wait : 5'")).check(matches(isDisplayed()))
        onView(withText("rate : 7")).check(matches(isDisplayed()))
        onView(withText("actions : (pick x2| deliver| contact)")).check(matches(isDisplayed()))

        //Check notes were edited :
        swipeRightTheRecordAt(2)
        onView(withText("Some notes about how SDP is fun")).check(matches(isDisplayed()))
    }

    private fun eraseFirstRecTimestamp() {
        swipeRightTheRecordAt(0)
        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerEraseBLabel)).perform(click())
        onView(withText(R.string.edit_dialog_update_b)).perform(click())
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
                curr.getRBViewModelForTest().moveRecord(2,2)
                recyclerView.adapter?.notifyItemMoved(3,4)
                curr.getRBViewModelForTest().moveRecord(3,3)
            }
        }

        onView(withText("X17#1")).check(matches(isDisplayed()))
        Thread.sleep(4000) // This one is needed to let the Screen enough time to be updated
        onView(withText("X17#1")).check(isCompletelyBelow(withText("More#1")))
    }

    // ============================================================================================
    // ================================= OptionMenu RB settings ===================================

    @Test
    fun rbSwipeLeftCorrectlyDisableDeletion() {
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_swipel_deletion)).perform(click())
        swipeLeftTheRecordAt(2)
        onView(withText(R.string.delete_dialog_title)).check(doesNotExist())
    }

    @Test
    fun rbSwipeRightCorrectlyDisableEdition() {
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_swiper_edition)).perform(click())
        swipeRightTheRecordAt(2)
        onView(withText(R.string.edit_dialog_update_b)).check(doesNotExist())
        onView(withText(R.string.edit_dialog_cancel_b)).check(doesNotExist())
    }

    @Test
    fun rbDragNDrop() { // Still can't test the drag & drop touch action
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_swiper_edition)).perform(click())
    }

    @Test
    fun rbTouchClickCorrectlyDisableNavigation() {
        // Disabled in sharedPref by setUp() @before routine
        onView(withText(DestinationRecords.RECORDS[2].destID)).perform(click())
        onView(withId(R.id.fragment_drecord_details_directors_parent)).check(doesNotExist())

        // Check two times to disable it during a Fragment's LifeCycle
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_touch_click)).perform(click())
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_touch_click)).perform(click())

        // Check it is still disabled
        onView(withText(DestinationRecords.RECORDS[2].destID)).perform(click())
        onView(withId(R.id.fragment_drecord_details_directors_parent)).check(doesNotExist())
    }

    @Test
    fun rbSLeftEnabledAgainWorks() {
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_swipel_deletion)).perform(click())
        swipeLeftTheRecordAt(2)
        onView(withText(R.string.delete_dialog_title)).check(doesNotExist())

        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_swipel_deletion)).perform(click())
        swipeLeftTheRecordAt(2)
        onView(withText(R.string.delete_dialog_title)).check(matches(isDisplayed()))
    }
    @Test
    fun rbSwipeREnabledAgainWorks() {
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_swiper_edition)).perform(click())
        swipeRightTheRecordAt(2)
        onView(withText(R.string.edit_dialog_update_b)).check(doesNotExist())
        onView(withText(R.string.edit_dialog_cancel_b)).check(doesNotExist())

        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_swiper_edition)).perform(click())
        swipeRightTheRecordAt(2)
        onView(withText(R.string.edit_dialog_update_b)).check(matches(isDisplayed()))
        onView(withText(R.string.edit_dialog_cancel_b)).check(matches(isDisplayed()))
    }
    @Test
    fun rbDragNDropEnabledAgainWorks() { // Still can't test the drag & drop touch action
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_swiper_edition)).perform(click())
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_swiper_edition)).perform(click())
    }

    // Check that moving somewhere else in the app keep the sharedPref alive.
    @Test
    fun movingOutsideRBFragmentKeepsButtonStates() {
        // Set some states :
        // Turn SwipeLeft disabled
        // Keep SwipeRight, DragNDrop enabled and Touch Navigation disabled
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_swipel_deletion)).perform(click())

        // Check features :
        // Swipe Left disabled
        swipeLeftTheRecordAt(2)
        onView(withText(R.string.delete_dialog_title)).check(doesNotExist())

        // SwipeRight enabled
        swipeRightTheRecordAt(2)
        onView(withText(R.string.edit_dialog_update_b)).check(matches(isDisplayed()))
        onView(withText(R.string.edit_dialog_cancel_b)).check(matches(isDisplayed()))
        onView(withText(R.string.edit_dialog_cancel_b)).perform(click())

        // Navigation on Click disabled
        Thread.sleep(WORST_REFRESH_TIME)
        onView(withText(DestinationRecords.RECORDS[2].destID)).perform(click())
        onView(withId(R.id.fragment_drecord_details_directors_parent)).check(doesNotExist())

        navigateOutsideAndComeBack()

        // Check features again :
        // Swipe Left disabled
        swipeLeftTheRecordAt(2)
        onView(withText(R.string.delete_dialog_title)).check(doesNotExist())

        // SwipeRight enabled
        swipeRightTheRecordAt(2)
        onView(withText(R.string.edit_dialog_update_b)).check(matches(isDisplayed()))
        onView(withText(R.string.edit_dialog_cancel_b)).check(matches(isDisplayed()))
        onView(withText(R.string.edit_dialog_cancel_b)).perform(click())

        // Navigation on Click disabled
        Thread.sleep(WORST_REFRESH_TIME)
        onView(withText(DestinationRecords.RECORDS[2].destID)).perform(click())
        onView(withId(R.id.fragment_drecord_details_directors_parent)).check(doesNotExist())
    }


    // ============================================================================================
    // ================================= Navigation to DRecordDetails =============================

    @Test
    fun clickingOnADestRecordLeadsToADRecordDetailsFragment() {
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_touch_click)).perform(click())
        onView(withText(DestinationRecords.RECORDS[2].destID)).perform(click())
        onView(withId(R.id.fragment_drecord_details_directors_parent)).check(matches(isDisplayed()))
    }

    // ============================================================================================
    // ================================== RB Archiving Records ====================================
    // By default archived records are not displayed, see @before setUp() routine
    @Test
    fun archiveARecordMakesItDisappear() {
        // Archive first one because it's already timestamped
        onView((withText(DestinationRecords.RECORDS[0].destID)))
            .check(matches(isDisplayed()))

        swipeLeftTheRecordAt(0)

        // Check that the record is not here
        onView((withText(DestinationRecords.RECORDS[0].destID)))
            .check(doesNotExist())
    }

    @Test
    fun archiveARecordAndCheckShowArchivedDisplayIt() {
        // Archive the first record
        onView((withText(DestinationRecords.RECORDS[0].destID)))
            .check(matches(isDisplayed()))

        swipeLeftTheRecordAt(0)

        onView((withText(DestinationRecords.RECORDS[0].destID)))
            .check(doesNotExist())

        // Enable showArchived
        clickOnShowArchivedButton()

        onView((withText(DestinationRecords.RECORDS[0].destID)))
            .check(matches(isDisplayed()))

        // Check if the archived icon is visible on it.
        onView(allOf(withId(R.id.archivedIcon), withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun archiveRecordWithShowArchivedChecked() {
        // Enable showArchived
        clickOnShowArchivedButton()

        // Check no archived icon is displayed yet :
        onView(allOf(withId(R.id.archivedIcon), withEffectiveVisibility(Visibility.VISIBLE)))
            .check(doesNotExist())

        onView((withText(DestinationRecords.RECORDS[0].destID)))
            .check(matches(isDisplayed()))

        swipeLeftTheRecordAt(0)

        onView(allOf(withId(R.id.archivedIcon), withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(isDisplayed()))

        // Disable show archived and check they are no more displayed :
        clickOnShowArchivedButton()
        onView(allOf(withId(R.id.archivedIcon), withEffectiveVisibility(Visibility.VISIBLE)))
            .check(doesNotExist())
        onView((withText(DestinationRecords.RECORDS[0].destID)))
            .check(doesNotExist())
    }

    @Test
    fun archiveANonTimestampedRecord() {
        onView((withText(DestinationRecords.RECORDS[1].destID)))
            .check(matches(isDisplayed()))
        swipeLeftTheRecordAt(1)

        // On non timestamped record swipe left should show deletion dialog
        onView(withText(R.string.delete_dialog_title)).check(matches(isDisplayed()))
        onView(withText(R.string.swipeleft_cancel_button_label)).perform(click())
        onView((withText(DestinationRecords.RECORDS[1].destID)))
            .check(matches(isDisplayed()))

        // Edit a timestamp :
        swipeRightTheRecordAt(1)
        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click())
        onView(withText(R.string.edit_dialog_update_b)).perform(click())
        Thread.sleep(WORST_REFRESH_TIME)

        swipeLeftTheRecordAt(1)
        onView((withText(DestinationRecords.RECORDS[1].destID)))
            .check(doesNotExist())
    }

    @Test
    fun unarchiveARecord() {
        // Enable showArchived
        clickOnShowArchivedButton()

        // Archive first
        swipeLeftTheRecordAt(0)

        // Check is well archived
        onView((withText(DestinationRecords.RECORDS[0].destID)))
            .check(matches(isDisplayed()))
        onView(allOf(withId(R.id.archivedIcon), withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(isDisplayed()))

        // Unarchive it
        swipeLeftTheRecordAt(0)
        onView(withText(R.string.unarchive_dialog_title)).check(matches(isDisplayed()))
        onView(withText(R.string.swipeleft_confirm_button_label)).perform(click())

        // Check the record has been unarchived
        onView((withText(DestinationRecords.RECORDS[0].destID)))
            .check(matches(isDisplayed()))
        onView(allOf(withId(R.id.archivedIcon), withEffectiveVisibility(Visibility.VISIBLE)))
            .check(doesNotExist())

        // Disable showArchived
        clickOnShowArchivedButton()

        // Ensure the record is still displayed
        onView((withText(DestinationRecords.RECORDS[0].destID)))
            .check(matches(isDisplayed()))
        onView(allOf(withId(R.id.archivedIcon), withEffectiveVisibility(Visibility.VISIBLE)))
            .check(doesNotExist())
    }

    @Test
    fun recordStayArchivedAfterNavigation() {
        onView((withText(DestinationRecords.RECORDS[0].destID)))
            .check(matches(isDisplayed()))

        // Archive the record
        swipeLeftTheRecordAt(0)

        // Check that the record is not here
        onView((withText(DestinationRecords.RECORDS[0].destID)))
            .check(doesNotExist())

        navigateOutsideAndComeBack()

        // Check that the record is still not there
        onView((withText(DestinationRecords.RECORDS[0].destID)))
            .check(doesNotExist())
    }

    @Test
    fun stayArchivedAfterNavigationWithShowArchived() {
        // Enable showArchived
        clickOnShowArchivedButton()

        // Archive the record
        swipeLeftTheRecordAt(0)

        // Check that the record is archived through the archivedIcon
        onView(allOf(withId(R.id.archivedIcon), withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(isDisplayed()))

        navigateOutsideAndComeBack()

        // Check that the record is still archived
        onView(allOf(withId(R.id.archivedIcon), withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(isDisplayed()))
    }

    // ============================================================================================
    // ================================Automatic Timestamp ========================================

    @Test
    fun automaticTimestampDoesNotWorkAfterDestroyingTheApp()  {
        // Non timestamped record, hence swipe left shows deletion dialog
        onView(withText(DestinationRecords.RECORDS[1].destID)).check(matches(isDisplayed()))
        swipeLeftTheRecordAt(1)
        Thread.sleep(4000)

        onView(withText(R.string.delete_dialog_title)).check(matches(isDisplayed()))
        onView(withText(R.string.swipeleft_cancel_button_label)).perform(click())
        Thread.sleep(4000)
        onView((withText(DestinationRecords.RECORDS[1].destID)))
            .check(matches(isDisplayed()))
        onView(withId(R.id.refresh_button)).perform(click())
    }

    @Test
    fun automaticTimestampIsWorkingWhenFragmentIsOnTheBackGround() {
        // Non timestamped record, hence swipe left shows deletion dialog
        swipeLeftTheRecordAt(2)
        onView(withText(R.string.delete_dialog_title)).check(matches(isDisplayed()))
        Thread.sleep(3000)
        onView(withText(R.string.swipeleft_cancel_button_label)).perform(click())
        onView(withText(DestinationRecords.RECORDS[2].destID)).check(matches(isDisplayed()))

        onView(withId(R.id.location_switch)).perform(click())
        val uiDevice = UiDevice.getInstance(getInstrumentation())
        uiDevice.pressHome()

        Thread.sleep(4000)
        val device = UiDevice.getInstance(getInstrumentation())
        device.pressHome()
        device.pressRecentApps()
        device.click(device.displayWidth / 2, device.displayHeight / 2)
        device.click(device.displayWidth / 2, device.displayHeight / 2)
        Thread.sleep(4000)
        // Now
        swipeLeftTheRecordAt(2)
        onView(withText(R.string.delete_dialog_title)).check(doesNotExist())
        onView(withText(DestinationRecords.RECORDS[2].destID)).check(doesNotExist())
    }

    fun withIndex(matcher: Matcher<View?>, index: Int): Matcher<View?>? {
        return object : TypeSafeMatcher<View?>() {
            var currentIndex = 0
            override fun describeTo(description: Description) {
                description.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View?): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }


    // ============================================================================================
    // ===================================== Helpers ==============================================

    // Set by defaults by the TimePicker Instance, not stored in String Ressources
    private val timePickerCancelBLabel = "Cancel"
    private val timePickerUpdateBLabel = "OK"
    private val timePickerEraseBLabel = "Erase"

    private fun clickOnShowArchivedButton() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_show_archived)).perform(click())
    }

    private fun navigateOutsideAndComeBack() {
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.routeFragment))
            .perform(click())
        onView(withId(R.id.fragment_route_directors_parent))
            .check(matches(isDisplayed()))
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.roadBookFragment))
            .perform(click())
    }
    private fun newRecord() {
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.autoCompleteClientID))
            .perform(click(), typeText("New"), closeSoftKeyboard())
        onView(withText(R.string.edit_dialog_update_b)).perform(click())
    }

    // As we can't set the seconds currently,
    // we use in our test the current time set by default in the time picker
    // It's enough to match until the hours in our tests as at most one timestamp written at a time
    // Retrieving it by text until the hours allows less false errors on the CI
    private fun timestampUntilHourFormat(cal: Calendar): String {
        return SimpleDateFormat.getTimeInstance()
            .format(cal.time)
            .substringBeforeLast(":")
            .substringBeforeLast(":")
    }

}