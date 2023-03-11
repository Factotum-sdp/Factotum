package com.github.factotum_sdp.factotum.PictureFragmentTests

import android.Manifest
import android.os.Environment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.PictureFragmentTests.*
import com.github.factotum_sdp.factotum.ui.picture.PictureFragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.*
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PictureFragmentOnlineTest {

    // Those tests need to run with a firebase storage emulator
    private lateinit var scenario: FragmentScenario<PictureFragment>
    private lateinit var storage: FirebaseStorage
    private val externalDir = Environment.getExternalStorageDirectory()
    private val picturesDir = File(externalDir, "/Android/data/com.github.factotum_sdp.factotum/files/Pictures")


    @get:Rule
    val permissionsRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @Before
    fun setUp() {
        storage = Firebase.storage
        storage.useEmulator("10.0.2.2", 9199)
        emptyFirebaseStorage(storage)

        // Empty Local Files
        emptyLocalFiles(picturesDir)

        // Launch the fragment
        scenario = launchFragmentInContainer(initialState = Lifecycle.State.INITIALIZED)

        // Wait for the fragment to reach the resumed state
        scenario.moveToState(Lifecycle.State.RESUMED)

        // Wait for the camera to open
        Thread.sleep(TIME_WAIT_SHUTTER)
    }


    @Test
    fun testUploadFileCorrectly() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        val takePictureButton = device.findObject(UiSelector().description("Shutter"))
        takePictureButton.click()

        // Wait for the photo to be taken
        Thread.sleep(TIME_WAIT_DONE_OR_CANCEL)

        // Click the button to validate the photo
        val validateButton = device.findObject(UiSelector().description("Done"))
        validateButton.click()

        // Wait for the photo to be uploaded
        Thread.sleep(TIME_WAIT_UPLOAD)

        // Check that the storage contains at least one file
        storage.reference.listAll().addOnSuccessListener { listResult ->
            assertTrue(listResult.items.isNotEmpty())
            //Check that the local picture directory is empty
            assertTrue(picturesDir.listFiles()?.isEmpty() ?: false)
        }.addOnFailureListener{ except ->
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
        val cancelButton = device.findObject(UiSelector().description("Cancel"))
        cancelButton.click()

        // Wait for the photo to be uploaded
        Thread.sleep(TIME_WAIT_UPLOAD)

        // Check that the storage contains no files
        storage.reference.listAll().addOnSuccessListener { listResult ->
            assertTrue(listResult.items.isEmpty())
            //Check that the local picture directory is empty
            assertTrue(picturesDir.listFiles()?.isEmpty() ?: false)
        }.addOnFailureListener{ except ->
            fail(except.message)
        }
    }
}
