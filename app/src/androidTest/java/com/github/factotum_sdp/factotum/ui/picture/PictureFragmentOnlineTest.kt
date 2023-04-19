package com.github.factotum_sdp.factotum.ui.picture

import android.Manifest
import android.os.Environment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.ui.picture.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PictureFragmentOnlineTest {

    // Those tests need to run with a firebase storage emulator
    private lateinit var storage: FirebaseStorage
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
        storage = Firebase.storage
        storage.useEmulator("10.0.2.2", 9199)
        emptyLocalFiles(picturesDir)

        // Open the drawer
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.roadBookFragment))
            .perform(ViewActions.click())

        // Click on one of the roadbook
        val destID = DestinationRecords.RECORDS[2].destID
        onView(ViewMatchers.withText(destID)).perform(ViewActions.click())

        // Go to the picture fragment
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())

        // Wait for the camera to open
        Thread.sleep(TIME_WAIT_SHUTTER)
    }

    @After
    fun tearDown() {
        runBlocking { emptyFirebaseStorage(storage.reference) }
        emptyLocalFiles(picturesDir)
    }


    @Test
    fun testUploadFileCorrectly() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Click on the shutter button
        device.executeShellCommand("input keyevent 27")

        // Wait for the photo to be taken
        Thread.sleep(TIME_WAIT_DONE_OR_CANCEL)

        // Click the button to validate the photo
        device.executeShellCommand("input keyevent 61")
        device.executeShellCommand("input keyevent 61")
        device.executeShellCommand("input keyevent 62")

        // Wait for the photo to be uploaded
        Thread.sleep(TIME_WAIT_UPLOAD)

        // Check that the storage contains at least one file
        storage.reference.listAll().addOnSuccessListener { listResult ->
            // Check that there is one folder that is not empty
            assertTrue(listResult.prefixes.isNotEmpty())

            // Check that the local picture directory contains one folder
            // and check that the folder is empty
            assertTrue(picturesDir.listFiles()?.isNotEmpty() ?: false)
        }.addOnFailureListener { except ->
            fail(except.message)
        }
    }

    @Test
    fun testCancelPhoto() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        val takePictureButton = device.findObject(UiSelector().description("Shutter"))
        takePictureButton.click()

        // Wait for the photo to be taken
        Thread.sleep(TIME_WAIT_DONE_OR_CANCEL)

        // Click the button to cancel the photo
        device.executeShellCommand("input keyevent 61")
        device.executeShellCommand("input keyevent 61")
        device.executeShellCommand("input keyevent 61")
        device.executeShellCommand("input keyevent 62")

        // Wait for the photo to be uploaded
        Thread.sleep(TIME_WAIT_UPLOAD)

        // Check that the storage contains no files
        storage.reference.listAll().addOnSuccessListener { listResult ->
            // Check that the folder in the storage is empty
            assertTrue(listResult.items.isEmpty())

            // Check that the local picture directory contains no files (folders are not counted
            // but should be empty)
            val directories = picturesDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
            assertTrue(directories.all { it.listFiles()?.isEmpty() == true})
        }.addOnFailureListener{ except ->
            fail(except.message)
        }
    }
}
