package com.github.factotum_sdp.factotum.ui.picture

import android.Manifest
import android.os.Environment
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
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
        @OptIn(ExperimentalCoroutinesApi::class)
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() = runTest {
            initFirebase(online = false)

            GeneralUtils.getStorage().maxUploadRetryTimeMillis = 100L
            GeneralUtils.getStorage().maxOperationRetryTimeMillis = 100L
            GeneralUtils.getStorage().maxDownloadRetryTimeMillis = 100L
        }
    }

    @Test
    fun testDoesNotDeleteFileIfUploadFails() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        emptyLocalFiles(picturesDir)

        goToPictureFragment()

        // Wait for the camera to open
        runBlocking { delay(TIME_WAIT_SHUTTER) }

        // Take a photo
        triggerShutter(device)

        // Wait for the photo to be taken
        runBlocking { delay(TIME_WAIT_DONE_OR_CANCEL) }

        device.findObject(UiSelector().description("Done")).click()

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