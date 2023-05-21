package com.github.factotum_sdp.factotum.ui.bag

import android.Manifest
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.User
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder.USER_COURIER
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.github.factotum_sdp.factotum.utils.PreferencesSetting
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.isEmptyString
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

    private val courier = User(USER_COURIER.uid, USER_COURIER.name, USER_COURIER.email, USER_COURIER.role)

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
        onView(withText(PackCreationDialogBuilder.DIALOG_TITLE_PREFIX + clientID)).check(
            doesNotExist()
        )
    }

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
        onView(withText(PackCreationDialogBuilder.DIALOG_TITLE_PREFIX + clientID)).check(
            matches(isDisplayed())
        )
    }

    @Test
    fun addAPickRecordWithTimestampAndCreateAPack() {
        val record = DestinationRecords.RECORD_TO_ADD
        val clientID = record.clientID
        val packageName = "Gold bottle"
        val recipientID = "X17"
        val notes = "It is soon the end of SDP"

        createAPackWithNotes(clientID, packageName, recipientID, notes)

        onView(withId(R.id.bag_button)).perform(click())

        onView(withText(startsWith(packageName))).check(matches(isDisplayed()))
        onView(withText(containsString(clientID))).check(matches(isDisplayed()))
        onView(withText(containsString(recipientID))).check(matches(isDisplayed()))
        onView(withText(containsString(BagAdapter.DELIVERED_TIMESTAMP_PREFIX))).check(
            doesNotExist()
        )
    }

    @Test
    fun packWithNotesShowsTheNotesIndicatorAndTheTextIsDisplayed() {
        val record = DestinationRecords.RECORD_TO_ADD
        val clientID = record.clientID
        val packageName = "Gold bottle"
        val recipientID = "X17"
        val notes =  "It is soon the end of SDP"

        createAPackWithNotes(clientID, packageName, recipientID, notes)

        onView(withId(R.id.bag_button)).perform(click())

        // Check indicator
        onView(withId(R.id.isAnnotatedIcon)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        // Check text is displayed in the notes edit dialog
        onView(withId(R.id.packageCardView)).perform(click())
        onView(withText(notes)).check(matches(isDisplayed()))
    }

    @Test
    fun packWithoutNotesDoesNotShowIndicatorAndTextIsNotDisplayed() {
        val record = DestinationRecords.RECORD_TO_ADD
        val clientID = record.clientID
        val packageName = "Gold bottle"
        val recipientID = "X17"

        createAPackWithoutNotes(clientID, packageName, recipientID)

        onView(withId(R.id.bag_button)).perform(click())

        // Check indicator
        onView(withId(R.id.isAnnotatedIcon)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))

        // Check text is displayed in the notes edit dialog
        onView(withId(R.id.packageCardView)).perform(click())
        onView(withId(R.id.postEditTextPackageNotes)).check(matches(withText(isEmptyString())))
    }

    @Test
    fun editPackNotesWorks() {
        val record = DestinationRecords.RECORD_TO_ADD
        val clientID = record.clientID
        val packageName = "Gold bottle"
        val recipientID = "X17"
        val notes = "It is the end of SDP"
        val edition = " edited"

        createAPackWithNotes(clientID, packageName, recipientID, notes)

        onView(withId(R.id.bag_button)).perform(click())

        onView(withId(R.id.packageCardView)).perform(click())

        // Edit notes
        onView(withId(R.id.postEditTextPackageNotes)).perform(typeText(edition))
        onView(withText(R.string.edit_dialog_update_b)).perform(click())

        // Check notes are updated
        onView(withId(R.id.packageCardView)).perform(click())
        onView(withText(notes + edition)).check(matches(isDisplayed()))
    }

    @Test
    fun editButCancelDoesNotUpdateTheNotes() {
        val record = DestinationRecords.RECORD_TO_ADD
        val clientID = record.clientID
        val packageName = "Gold bottle"
        val recipientID = "X17"
        val notes = "It is the end of SDP"
        val edition = " edited"

        createAPackWithNotes(clientID, packageName, recipientID, notes)

        onView(withId(R.id.bag_button)).perform(click())

        onView(withId(R.id.packageCardView)).perform(click())

        // Edit notes but then cancel
        onView(withId(R.id.postEditTextPackageNotes)).perform(typeText(edition))
        onView(withText(R.string.edit_dialog_cancel_b)).perform(click())

        // Check notes are NOT updated
        onView(withId(R.id.packageCardView)).perform(click())
        onView(withText(notes + edition)).check(doesNotExist())
    }

    private fun createAPackWithNotes(clientID: String, packageName: String, recipientID: String, notes: String) {
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


        onView(withId(R.id.editTextPackageName))
            .perform(click(),  typeText(packageName), closeSoftKeyboard())
        onView(withId(R.id.autoCompleteRecipientClientID))
            .perform(click(), typeText("$recipientID "), closeSoftKeyboard())
        onView(withId(R.id.editTextPackageNotes))
            .perform(click(), typeText(notes), closeSoftKeyboard())

        onView(withText(R.string.confirm_label_pack_creation_dialog)).perform(click())
    }

    private fun createAPackWithoutNotes(clientID: String, packageName: String, recipientID: String) {
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

        onView(withId(R.id.editTextPackageName))
            .perform(click(),  typeText(packageName), closeSoftKeyboard())
        onView(withId(R.id.autoCompleteRecipientClientID))
            .perform(click(), typeText("$recipientID "), closeSoftKeyboard())

        onView(withText(R.string.confirm_label_pack_creation_dialog)).perform(click())
    }

    private val timePickerUpdateBLabel = "OK"
}