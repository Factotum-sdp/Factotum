package com.github.factotum_sdp.factotum.ui.picture

//import android.Manifest
//import android.os.Environment
//import androidx.test.ext.junit.rules.ActivityScenarioRule
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.platform.app.InstrumentationRegistry
//import androidx.test.rule.GrantPermissionRule
//import androidx.test.uiautomator.UiDevice
//import com.github.factotum_sdp.factotum.MainActivity
//import com.github.factotum_sdp.factotum.ui.picture.*
//import com.github.factotum_sdp.factotum.utils.GeneralUtils
//import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
//import junit.framework.TestCase.assertTrue
//import junit.framework.TestCase.fail
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.runBlocking
//import org.junit.*
//import org.junit.runner.RunWith
//import java.io.File
//
//@RunWith(AndroidJUnit4::class)
//class PictureFragmentOnlineTest {
//
//    // Those tests need to run with a firebase storage emulator
//    private lateinit var device: UiDevice
//    private val externalDir = Environment.getExternalStorageDirectory()
//    private val picturesDir =
//        File(externalDir, "/Android/data/com.github.factotum_sdp.factotum/files/Pictures")
//
//    @get:Rule
//    val permissionsRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)
//
//    @get:Rule
//    var testRule = ActivityScenarioRule(
//        MainActivity::class.java
//    )
//
//    companion object {
//        @BeforeClass
//        @JvmStatic
//        fun setUpDatabase() {
//            initFirebase()
//        }
//    }
//
//    @Before
//    fun setUp() {
//        emptyLocalFiles(picturesDir)
//        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//
//        goToPictureFragment()
//
//        // Wait for the camera to open
//        Thread.sleep(TIME_WAIT_SHUTTER)
//
//        // Take a photo
//        triggerShutter(device)
//
//        // Wait for the photo to be taken
//        Thread.sleep(TIME_WAIT_DONE_OR_CANCEL)
//    }
//
//    @After
//    fun tearDown() {
//        runBlocking { emptyFirebaseStorage(GeneralUtils.getStorage().reference) }
//        emptyLocalFiles(picturesDir)
//    }
//
//    @Test
//    fun testUploadFileCorrectly() {
//        triggerDone(device)
//
//        runBlocking {
//            delay(TIME_WAIT_UPLOAD_PHOTO)
//        }
//
//        GeneralUtils.getStorage().reference.child(CLIENT_ID).listAll().addOnSuccessListener { files ->
//            assertTrue(files.items.size == 1)
//        }.addOnFailureListener { except ->
//            fail(except.message)
//        }
//
//        // Check if the folder in the local storage with the same name as the firebase folder
//        // is empty
//        val localFolder = File(picturesDir, CLIENT_ID)
//        assertTrue(localFolder.listFiles()?.isEmpty() == true)
//    }
//
//
//    @Test
//    fun testCancelPhoto() {
//        // Click the button to cancel the photo
//        triggerCancel(device)
//
//        runBlocking {
//            delay(TIME_WAIT_UPLOAD_PHOTO)
//        }
//
//        // Check that the storage contains no files
//        GeneralUtils.getStorage().reference.child(CLIENT_ID).listAll().addOnSuccessListener { listResult ->
//            // Check that the folder in the storage is empty
//            assertTrue(listResult.items.isEmpty())
//
//            // Check that the local picture directory contains no files (folders are not counted
//            // but should be empty)
//            val directories = picturesDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
//            assertTrue(directories.all { it.listFiles()?.isEmpty() == true })
//        }.addOnFailureListener { except ->
//            fail(except.message)
//        }
//    }
//
//}
