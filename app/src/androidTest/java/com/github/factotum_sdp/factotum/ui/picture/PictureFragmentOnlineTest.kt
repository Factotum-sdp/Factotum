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
import com.github.factotum_sdp.factotum.ui.picture.*
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.*
import org.junit.runner.RunWith
import java.io.File


@RunWith(AndroidJUnit4::class)
class PictureFragmentOnlineTest {

    // Those tests need to run with a firebase storage emulator
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
            initFirebase()
            UsersPlaceHolder.init(GeneralUtils.getDatabase(), GeneralUtils.getAuth())
            launch { GeneralUtils.addUserToDatabase(UsersPlaceHolder.USER_COURIER) }.join()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() = runTest{
        launch { emptyLocalFiles(picturesDir) }.join()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        goToPictureFragment()
        triggerShutter(device)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() = runTest {
        launch { emptyFirebaseStorage(GeneralUtils.getStorage().reference) }.join()
        launch { emptyLocalFiles(picturesDir) }.join()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testUploadFileCorrectly() = runTest {
        triggerDone(device)

        // Really needed to wait for the upload to finish
        runBlocking { delay(TIME_WAIT_UPLOAD_PHOTO) }

        GeneralUtils.getStorage().reference.child(CLIENT_ID).listAll().addOnSuccessListener { listResult ->
            assertTrue(listResult.items.isNotEmpty())
        }.addOnFailureListener { except ->
            fail(except.message)
        }

        delay(TIME_WAIT_DELETE_PHOTO)

        val directories = picturesDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
        assertTrue(directories.all { it.listFiles()?.isEmpty() == true })
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCancelPhoto() = runTest {
        triggerCancel(device)

        // Really needed to wait for the upload to finish
        runBlocking { delay(TIME_WAIT_UPLOAD_PHOTO) }

        GeneralUtils.getStorage().reference.child(CLIENT_ID).listAll().addOnSuccessListener { listResult ->
            assertTrue(listResult.items.isEmpty())
        }.addOnFailureListener { except ->
            fail(except.message)
        }

        delay(TIME_WAIT_DELETE_PHOTO)

        val directories = picturesDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
        assertTrue(directories.all { it.listFiles()?.isEmpty() == true })
    }

}
