package com.github.factotum_sdp.factotum.ui.picture

import android.Manifest
import android.os.Environment
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.runner.RunWith
import java.io.File


@RunWith(AndroidJUnit4::class)
class PictureFragmentOfflineTest {
    private lateinit var device: UiDevice
    private val externalDir = Environment.getExternalStorageDirectory()
    private val picturesDir =
        File(externalDir, "/Android/data/com.github.factotum_sdp.factotum/files/Pictures")

    @get:Rule
    val permissionsRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            initFirebase(online = false)
        }
    }

    @Test
    fun testDoesNotDeleteFileIfUploadFails() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        emptyLocalFiles(picturesDir)

        goToPictureFragment()

        // Wait for the camera to open
        Thread.sleep(TIME_WAIT_SHUTTER)

        // Take a photo
        triggerShutter(device)

        // Wait for the photo to be taken
        Thread.sleep(TIME_WAIT_DONE_OR_CANCEL)

        // Click the button to validate the photo
        triggerDone(device)

        runBlocking {
            GeneralUtils.getStorage().reference.child(CLIENT_ID).listAll().addOnSuccessListener {
                fail("Should not succeed")
            }
            delay(TIME_WAIT_UPLOAD_PHOTO)
        }

        //Check if there is a folder with a file in it
        val directories = picturesDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
        assertTrue(directories.isNotEmpty())

        emptyLocalFiles(picturesDir)
    }
}
