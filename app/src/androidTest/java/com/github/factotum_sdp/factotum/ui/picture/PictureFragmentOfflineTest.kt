package com.github.factotum_sdp.factotum.ui.picture
//
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
//import com.github.factotum_sdp.factotum.utils.GeneralUtils
//import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
//import com.github.factotum_sdp.factotum.utils.LoginMenuIdlingResource
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.test.runTest
//import org.junit.*
//import org.junit.Assert.assertTrue
//import org.junit.Assert.fail
//import org.junit.runner.RunWith
//import java.io.File
//
//
//@RunWith(AndroidJUnit4::class)
//class PictureFragmentOfflineTest {
//    private lateinit var device: UiDevice
//    private val externalDir = Environment.getExternalStorageDirectory()
//    private val picturesDir =
//        File(externalDir, "/Android/data/com.github.factotum_sdp.factotum/files/Pictures")
//    private lateinit var loginMenuIdlingResource: IdlingResource
//
//    @get:Rule
//    val permissionsRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)
//
//    @get:Rule
//    var testRule = ActivityScenarioRule(MainActivity::class.java)
//
//    companion object {
//        @BeforeClass
//        @JvmStatic
//        fun setUpDatabase() {
//            initFirebase(online = false)
//        }
//    }
//
//    @Before
//    fun setUp() {
//        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//        GeneralUtils.fillUserEntryAndEnterTheApp("courier@gmail.com", "123456")
//        testRule.scenario.onActivity { activity ->
//            loginMenuIdlingResource = LoginMenuIdlingResource(activity)
//            IdlingRegistry.getInstance().register(loginMenuIdlingResource)
//        }
//
//        emptyLocalFiles(picturesDir)
//        goToPictureFragment()
//    }
//
//    @After
//    fun tearDown() {
//        IdlingRegistry.getInstance().unregister(loginMenuIdlingResource)
//        emptyLocalFiles(picturesDir)
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun testDoesNotDeleteFileIfUploadFails() = runTest {
//
//        triggerShutter(device)
//        triggerDone(device)
//
//        runBlocking { delay(TIME_WAIT_UPLOAD_PHOTO) }
//
//        GeneralUtils.getStorage().reference.child(CLIENT_ID).listAll().addOnSuccessListener {
//                fail("Should not succeed")
//        }
//
//        //Check if there is a folder with a file in it
//        val directories = picturesDir.listFiles()?.filter { it.isDirectory } ?: emptyList()
//        assertTrue(directories.isNotEmpty())
//    }
//}
