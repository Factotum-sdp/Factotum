package com.github.factotum_sdp.factotum.ui.bag

import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.User
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder.USER_COURIER
import com.github.factotum_sdp.factotum.ui.roadbook.RoadBookViewAdapter
import com.github.factotum_sdp.factotum.ui.roadbook.TouchCustomMoves.swipeRightTheRecordAt
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.github.factotum_sdp.factotum.utils.PreferencesSetting

import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


@RunWith(AndroidJUnit4::class)
class BagFragmentTest {

    @get:Rule
    val coarseLocationRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    @get:Rule
    val fineLocationRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val foreGroundService = GrantPermissionRule.grant(Manifest.permission.FOREGROUND_SERVICE)


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

    private val courier = User(USER_COURIER.name, USER_COURIER.email, USER_COURIER.role)

    @Before
    fun toRoadBookFragment() {

        // Ensure "use RoadBook preferences" is disabled
        PreferencesSetting.setRoadBookPrefs(testRule)

        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.roadBookFragment))
            .perform(click())

        GeneralUtils.injectLoggedInUser(testRule, courier)
    }

    @Test
    fun addAPickRecordWithoutTimestampDoesNotShowCreatePackDialog() {
        val record = DestinationRecords.RECORD_TO_ADD
        val clientID = record.clientID
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.autoCompleteClientID))
            .perform(typeText("$clientID "))

        onView(withId(R.id.multiAutoCompleteActions))
            .perform(
                click(),
                clearText(),
                typeText("pick, contact"),
                closeSoftKeyboard()
            )

        onView(withText(R.string.edit_dialog_update_b)).perform(click())
        onView(withText(PackageCreationDialogBuilder.DIALOG_TITLE_PREFIX + clientID)).check(
            doesNotExist()
        )
    }
 /*
    @Test
    fun editARecordWithPickAndNoTimestampDoesNotShowCreatePackDialog() {
        val clientID = DestinationRecords.RECORDS[1].clientID
        swipeRightTheRecordAt(1)

        onView(withId(R.id.multiAutoCompleteActions))
            .perform(
                click(),
                clearText(),
                typeText("pick, contact"),
                closeSoftKeyboard()
            )

        onView(withText(R.string.edit_dialog_update_b)).perform(click())
        onView(withText(PackageCreationDialogBuilder.DIALOG_TITLE_PREFIX + clientID)).check(
            doesNotExist()
        )
    }
*/
    @Test
    fun addAPickRecordWithTimestampShowsCreatePackDialog() {
        val record = DestinationRecords.RECORD_TO_ADD
        val clientID = record.clientID
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.autoCompleteClientID))
            .perform(typeText("$clientID "))

        onView(withId(R.id.multiAutoCompleteActions))
            .perform(
                click(),
                clearText(),
                typeText("pick, contact"),
                closeSoftKeyboard()
            )

        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click())

        onView(withText(R.string.edit_dialog_update_b)).perform(click())
        onView(withText(PackageCreationDialogBuilder.DIALOG_TITLE_PREFIX + clientID)).check(
            matches(isDisplayed())
        )
    }

    /*
    @Test
    fun editARecordWithPickAnTimestampShowsCreatePackDialog() {
        val clientID = DestinationRecords.RECORDS[1].clientID
        swipeRightTheRecordAt(1)

        onView(withId(R.id.multiAutoCompleteActions))
            .perform(
                click(),
                clearText(),
                typeText("pick, contact"),
                closeSoftKeyboard()
            )

        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click()) // edited through TimePicker

        onView(withText(R.string.edit_dialog_update_b)).perform(click())
        onView(withText(PackageCreationDialogBuilder.DIALOG_TITLE_PREFIX + clientID)).check(
            matches(isDisplayed())
        )
    }*/

    @Test
    fun addAPickRecordWithTimestampAndCreateAPack() {
        val record = DestinationRecords.RECORD_TO_ADD
        val clientID = record.clientID
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.autoCompleteClientID))
            .perform(typeText("$clientID "))

        onView(withId(R.id.multiAutoCompleteActions))
            .perform(
                click(),
                clearText(),
                typeText("pick, contact"),
                closeSoftKeyboard()
            )

        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click())

        onView(withText(R.string.edit_dialog_update_b)).perform(click())

        val packageName = "Gold bottle"
        val recipientID = "X17"
        onView(withId(R.id.editTextPackageName)).perform(click(),  typeText(packageName), closeSoftKeyboard())
        onView(withId(R.id.autoCompleteRecipientClientID)).perform(click(), typeText("$recipientID "), closeSoftKeyboard())
        onView(withId(R.id.editTextPackageNotes)).perform(click(), typeText("It is soon the end of SDP"), closeSoftKeyboard())

        onView(withText(R.string.confirm_label_pack_creation_dialog)).perform(click())

        onView(withId(R.id.bag_button)).perform(click())

        onView(withText(startsWith(packageName))).check(matches(isDisplayed()))
        onView(withText(containsString(clientID))).check(matches(isDisplayed()))
        onView(withText(containsString(recipientID))).check(matches(isDisplayed()))
        onView(withText(containsString(PackagesAdapter.DELIVERED_TIMESTAMP_PREFIX))).check(
            doesNotExist()
        )
    }

/*
    @Test
    fun editAPickRecordWithTimestampAndCreateAPack() {
        val clientID = DestinationRecords.RECORDS[1].clientID
        swipeRightTheRecordAt(1)

        onView(withId(R.id.multiAutoCompleteActions))
            .perform(
                click(),
                clearText(),
                typeText("pick, contact"),
                closeSoftKeyboard()
            )

        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click()) // edited through TimePicker

        onView(withText(R.string.edit_dialog_update_b)).perform(click())

        val packageName = "Gold bottle"
        val recipientID = "X17"
        onView(withId(R.id.editTextPackageName)).perform(click(),  typeText(packageName), closeSoftKeyboard())
        onView(withId(R.id.autoCompleteRecipientClientID)).perform(click(), typeText("$recipientID "), closeSoftKeyboard())
        onView(withId(R.id.editTextPackageNotes)).perform(click(), typeText("It is soon the end of SDP"), closeSoftKeyboard())

        onView(withText(R.string.confirm_label_pack_creation_dialog)).perform(click())

        onView(withId(R.id.bag_button)).perform(click())

        onView(withText(startsWith(packageName))).check(matches(isDisplayed()))
        onView(withText(containsString(clientID))).check(matches(isDisplayed()))
        onView(withText(containsString(recipientID))).check(matches(isDisplayed()))
        onView(withText(containsString(PackagesAdapter.DELIVERED_TIMESTAMP_PREFIX))).check(
            doesNotExist()
        )
    }
*/
    /*
    @Test
    fun pickARecordAndDeliverRecipientUpdateTheBag() {
        val clientID = DestinationRecords.RECORDS[1].clientID
        swipeRightTheRecordAt(1)

        onView(withId(R.id.multiAutoCompleteActions))
            .perform(
                click(),
                clearText(),
                typeText("pick, contact"),
                closeSoftKeyboard()
            )

        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click()) // edited through TimePicker

        onView(withText(R.string.edit_dialog_update_b)).perform(click())

        val packageName = "Gold bottle"
        val recipientID = "X17"
        onView(withId(R.id.editTextPackageName)).perform(click(),  typeText(packageName), closeSoftKeyboard())
        onView(withId(R.id.autoCompleteRecipientClientID)).perform(click(), typeText("$recipientID "), closeSoftKeyboard())
        onView(withId(R.id.editTextPackageNotes)).perform(click(), typeText("It is soon the end of SDP"), closeSoftKeyboard())

        onView(withText(R.string.confirm_label_pack_creation_dialog)).perform(click())

        swipeRightTheRecordAt(2)

        onView(withId(R.id.multiAutoCompleteActions))
            .perform(
                click(),
                clearText(),
                typeText("deliver, contact"),
                closeSoftKeyboard()
            )
        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click()) // edited through TimePicker

        onView(withText(R.string.edit_dialog_update_b)).perform(click())

        onView(withId(R.id.bag_button)).perform(click())

        onView(withText(startsWith(packageName))).check(matches(isDisplayed()))
        onView(withText(containsString(clientID))).check(matches(isDisplayed()))
        onView(withText(containsString(recipientID))).check(matches(isDisplayed()))
        onView(withText(containsString(PackagesAdapter.DELIVERED_TIMESTAMP_PREFIX))).check(
            matches(isDisplayed())
        )
    }
*/
    /*
    @Test
    fun pickARecordAndArriveAtRecipientButWithNoDeliverActionDoesNotUpdateTheBag() {
        val clientID = DestinationRecords.RECORDS[1].clientID
        swipeRightTheRecordAt(1)

        onView(withId(R.id.multiAutoCompleteActions))
            .perform(
                click(),
                clearText(),
                typeText("pick, contact"),
                closeSoftKeyboard()
            )

        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click()) // edited through TimePicker

        onView(withText(R.string.edit_dialog_update_b)).perform(click())

        val packageName = "Gold bottle"
        val recipientID = "X17"
        onView(withId(R.id.editTextPackageName)).perform(click(),  typeText(packageName), closeSoftKeyboard())
        onView(withId(R.id.autoCompleteRecipientClientID)).perform(click(), typeText("$recipientID "), closeSoftKeyboard())
        onView(withId(R.id.editTextPackageNotes)).perform(click(), typeText("It is soon the end of SDP"), closeSoftKeyboard())

        onView(withText(R.string.confirm_label_pack_creation_dialog)).perform(click())

        swipeRightTheRecordAt(2)

        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click()) // edited through TimePicker

        onView(withText(R.string.edit_dialog_update_b)).perform(click())

        onView(withId(R.id.bag_button)).perform(click())

        onView(withText(startsWith(packageName))).check(matches(isDisplayed()))
        onView(withText(containsString(clientID))).check(matches(isDisplayed()))
        onView(withText(containsString(recipientID))).check(matches(isDisplayed()))
        onView(withText(containsString(PackagesAdapter.DELIVERED_TIMESTAMP_PREFIX))).check(
            doesNotExist()
        )
    }

    @Test
    fun getOutTimestampOfADeliveredPlaceRemoveTimestampInBag() {
        val clientID = DestinationRecords.RECORDS[1].clientID
        swipeRightTheRecordAt(1)

        onView(withId(R.id.multiAutoCompleteActions))
            .perform(
                click(),
                clearText(),
                typeText("pick, contact"),
                closeSoftKeyboard()
            )

        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click()) // edited through TimePicker

        onView(withText(R.string.edit_dialog_update_b)).perform(click())

        val packageName = "Gold bottle"
        val recipientID = "X17"
        onView(withId(R.id.editTextPackageName)).perform(click(),  typeText(packageName), closeSoftKeyboard())
        onView(withId(R.id.autoCompleteRecipientClientID)).perform(click(), typeText("$recipientID "), closeSoftKeyboard())
        onView(withId(R.id.editTextPackageNotes)).perform(click(), typeText("It is soon the end of SDP"), closeSoftKeyboard())

        onView(withText(R.string.confirm_label_pack_creation_dialog)).perform(click())

        swipeRightTheRecordAt(2)

        onView(withId(R.id.multiAutoCompleteActions))
            .perform(
                click(),
                clearText(),
                typeText("deliver, contact"),
                closeSoftKeyboard()
            )
        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click()) // edited through TimePicker

        onView(withText(R.string.edit_dialog_update_b)).perform(click())



        swipeRightTheRecordAt(2)
        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerEraseBLabel)).perform(click())
        onView(withText(R.string.edit_dialog_update_b)).perform(click())

        onView(withId(R.id.bag_button)).perform(click())

        onView(withText(startsWith(packageName))).check(matches(isDisplayed()))
        onView(withText(containsString(clientID))).check(matches(isDisplayed()))
        onView(withText(containsString(recipientID))).check(matches(isDisplayed()))
        onView(withText(containsString(PackagesAdapter.DELIVERED_TIMESTAMP_PREFIX))).check(
            doesNotExist()
        )
    }

    @Test
    fun getOutTimestampOfAPickPlaceRemovePacketFromThereInBag() {
        val clientID = DestinationRecords.RECORDS[1].clientID
        swipeRightTheRecordAt(1)

        onView(withId(R.id.multiAutoCompleteActions))
            .perform(
                click(),
                clearText(),
                typeText("pick, contact"),
                closeSoftKeyboard()
            )

        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click()) // edited through TimePicker

        onView(withText(R.string.edit_dialog_update_b)).perform(click())

        val packageName = "Gold bottle"
        val recipientID = "X17"
        onView(withId(R.id.editTextPackageName)).perform(click(),  typeText(packageName), closeSoftKeyboard())
        onView(withId(R.id.autoCompleteRecipientClientID)).perform(click(), typeText("$recipientID "), closeSoftKeyboard())
        onView(withId(R.id.editTextPackageNotes)).perform(click(), typeText("It is soon the end of SDP"), closeSoftKeyboard())

        onView(withText(R.string.confirm_label_pack_creation_dialog)).perform(click())

        swipeRightTheRecordAt(2)

        onView(withId(R.id.multiAutoCompleteActions))
            .perform(
                click(),
                clearText(),
                typeText("deliver, contact"),
                closeSoftKeyboard()
            )
        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerUpdateBLabel)).perform(click()) // edited through TimePicker

        onView(withText(R.string.edit_dialog_update_b)).perform(click())



        swipeRightTheRecordAt(1)
        onView(withId(R.id.editTextTimestamp)).perform(click())
        onView(withText(timePickerEraseBLabel)).perform(click())
        onView(withText(R.string.edit_dialog_update_b)).perform(click())

        onView(withId(R.id.bag_button)).perform(click())

        onView(withText(startsWith(packageName))).check(doesNotExist())
        onView(withText(containsString(clientID))).check(doesNotExist())
        onView(withText(containsString(recipientID))).check(doesNotExist())
        onView(withText(containsString(PackagesAdapter.DELIVERED_TIMESTAMP_PREFIX))).check(
            doesNotExist()
        )
    }
*/
    private val timePickerUpdateBLabel = "OK"
    private val timePickerEraseBLabel = "Erase"
}