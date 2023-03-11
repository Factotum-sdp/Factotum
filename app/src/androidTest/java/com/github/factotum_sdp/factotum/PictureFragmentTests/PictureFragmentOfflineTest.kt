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
import com.github.factotum_sdp.factotum.ui.picture.PictureFragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


@RunWith(AndroidJUnit4::class)
class PictureFragmentOfflineTest {
    private lateinit var scenario: FragmentScenario<PictureFragment>
    private val storage = Firebase.storage
    private val externalDir = Environment.getExternalStorageDirectory()
    private val picturesDir = File(externalDir, "/Android/data/com.github.factotum_sdp.factotum/files/Pictures")

    @get:Rule
    val permissionsRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @Before
    fun setUp() {
        // Initialize Firebase Storage
        storage.useEmulator("10.0.2.2", 9198)
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
    fun testDoesNotDeleteFileIfUploadFails() {
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

        storage.reference.listAll().addOnSuccessListener { listResult ->
            fail("Should not succeed")
        }.addOnFailureListener{
            //Check if there is a file in the local directory
            picturesDir.listFiles()?.forEach { file ->
                assertTrue(file.exists())
            }
        }
    }
}