package com.github.factotum_sdp.factotum.ui.picture

import android.Manifest
import android.os.Environment
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.ui.picture.*
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.github.factotum_sdp.factotum.utils.PreferencesSetting
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
    private lateinit var device : UiDevice
    private val externalDir = Environment.getExternalStorageDirectory()
    private val picturesDir = File(externalDir, "/Android/data/com.github.factotum_sdp.factotum/files/Pictures")

    @get:Rule
    val permissionsRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

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

    @Before
    fun setUp() {
        storage = Firebase.storage
        storage.useEmulator("10.0.2.2", 9199)
        emptyLocalFiles(picturesDir)
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        testRule.scenario.onActivity {
            PreferencesSetting.setPrefs(PreferencesSetting.TOUCH_CLICK_SHARED_KEY, it, true)
        }

        goToPictureFragment()

        // Wait for the camera to open
        Thread.sleep(TIME_WAIT_SHUTTER)

        // Take a photo
        triggerShutter(device)
    }

    @After
    fun tearDown() {
        runBlocking { emptyFirebaseStorage(storage.reference) }
        emptyLocalFiles(picturesDir)
    }


    @Test
    fun testUploadFileCorrectly() {
        // Wait for the photo to be taken
        Thread.sleep(TIME_WAIT_DONE_OR_CANCEL)

        // Click the button to validate the photo
        triggerDone(device)

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
        // Wait for the photo to be taken
        Thread.sleep(TIME_WAIT_DONE_OR_CANCEL)

        // Click the button to cancel the photo
        triggerCancel(device)

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
