package com.github.factotum_sdp.factotum.ui.picture

import android.Manifest
import android.os.Environment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


@RunWith(AndroidJUnit4::class)
class PictureFragmentOfflineTest {
    private lateinit var storage: FirebaseStorage
    private lateinit var device: UiDevice
    private val externalDir = Environment.getExternalStorageDirectory()
    private val picturesDir = File(externalDir, "/Android/data/com.github.factotum_sdp.factotum/files/Pictures")

    @get:Rule
    val permissionsRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Before
    fun setUp() {
        // Initialize Firebase Storage
        storage = Firebase.storage
        storage.useEmulator("10.0.2.2", 9198)
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        emptyLocalFiles(picturesDir)

        // Open the drawer
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.roadBookFragment))
            .perform(click())

        // Click on one of the roadbook
        val destID = DestinationRecords.RECORDS[2].destID
        onView(withText(destID)).perform(click())

        // Go to the picture fragment
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())

        // Wait for the camera to open
        Thread.sleep(TIME_WAIT_SHUTTER)
    }

    @After
    fun tearDown() {
        emptyLocalFiles(picturesDir)
    }

    @Test
    fun testDoesNotDeleteFileIfUploadFails() {
        // Take a photo
        triggerShutter(device)

        // Wait for the photo to be taken
        Thread.sleep(TIME_WAIT_DONE_OR_CANCEL)

        // Click the button to validate the photo
        triggerDone(device)

        // Wait for the photo to be uploaded
        Thread.sleep(TIME_WAIT_UPLOAD)

        storage.reference.listAll().addOnSuccessListener { listResult ->
            fail("Should not succeed")
        }

        //Check if there is a folder with a file in it
        val directories = picturesDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
        assertTrue(directories.isNotEmpty())

    }
}