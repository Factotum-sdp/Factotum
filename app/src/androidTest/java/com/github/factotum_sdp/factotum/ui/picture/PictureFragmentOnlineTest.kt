package com.github.factotum_sdp.factotum.ui.picture

//import android.Manifest
//import android.os.Environment
//import androidx.test.espresso.IdlingRegistry
//import androidx.test.espresso.IdlingResource
//import androidx.test.ext.junit.rules.ActivityScenarioRule
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.platform.app.InstrumentationRegistry
//import androidx.test.rule.GrantPermissionRule
//import androidx.test.uiautomator.UiDevice
//import com.github.factotum_sdp.factotum.MainActivity
//import com.github.factotum_sdp.factotum.ui.picture.*
//import com.github.factotum_sdp.factotum.utils.GeneralUtils
//import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
//import com.github.factotum_sdp.factotum.utils.LoginMenuIdlingResource
//import junit.framework.TestCase.assertTrue
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.test.runTest
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
//    private lateinit var loginMenuIdlingResource: IdlingResource
//
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
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Before
//    fun setUp() = runTest{
//        emptyLocalFiles(picturesDir)
//        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//        GeneralUtils.fillUserEntryAndEnterTheApp("courier@gmail.com", "123456")
//        testRule.scenario.onActivity { activity ->
//            loginMenuIdlingResource = LoginMenuIdlingResource(activity)
//            IdlingRegistry.getInstance().register(loginMenuIdlingResource)
//        }
//
//        goToPictureFragment()
//        triggerShutter(device)
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @After
//    fun tearDown() = runTest {
//        IdlingRegistry.getInstance().unregister(loginMenuIdlingResource)
//        launch { emptyFirebaseStorage(GeneralUtils.getStorage().reference) }.join()
//        emptyLocalFiles(picturesDir)
//    }
//

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun testUploadFileCorrectly() = runTest {
//        triggerDone(device)
//
//        runBlocking{
//            withContext(Dispatchers.IO) {
//                Thread.sleep(TIME_WAIT_UPLOAD_PHOTO)
//            }
//        }
//        GeneralUtils.getStorage().reference.child(CLIENT_ID).listAll().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val files = task.result?.items ?: emptyList()
//                assertTrue(files.size == 1)
//            } else {
//                fail(task.exception?.message)
//            }
//        }
//
//        runBlocking { delay(TIME_WAIT_PHOTO_DELETE) }
//
//        val localFolder = File(picturesDir, CLIENT_ID)
//        assertTrue(localFolder.listFiles()?.isEmpty() == true)
//    }
//
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun testCancelPhoto() = runTest {
//        triggerCancel(device)
//
//        runBlocking { delay(TIME_WAIT_PHOTO_DELETE) }
//
//        val localFolder = File(picturesDir, CLIENT_ID)
//        assertTrue(localFolder.listFiles()?.isEmpty() == true)
//    }
//}
