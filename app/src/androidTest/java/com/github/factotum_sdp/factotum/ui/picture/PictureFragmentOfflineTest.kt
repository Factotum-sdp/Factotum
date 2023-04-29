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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
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
            UsersPlaceHolder.init(GeneralUtils.getDatabase(), GeneralUtils.getAuth())
            launch { GeneralUtils.addUserToDatabase(UsersPlaceHolder.USER_COURIER) }.join()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testDoesNotDeleteFileIfUploadFails() = runTest {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        launch{ emptyLocalFiles(picturesDir) }.join()

        goToPictureFragment()
        triggerShutter(device)
        triggerDone(device)

        // Really needed to wait for the upload to finish
        runBlocking { delay(TIME_WAIT_UPLOAD_PHOTO) }

        GeneralUtils.getStorage().reference.child(CLIENT_ID).listAll().addOnSuccessListener {
            fail("Should not succeed")
        }

        //Check if there is a folder with a file in it
        val directories = picturesDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
        assertTrue(directories.isNotEmpty())

        launch { emptyLocalFiles(picturesDir) }.join()
    }
}