package com.github.factotum_sdp.factotum.ui.picture

import android.Manifest
import android.os.Environment
import androidx.test.espresso.IdlingResource
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PictureFragmentTest {

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
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            initFirebase()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() = runTest{
        GeneralUtils.injectBossAsLoggedInUser(testRule)
        PreferencesSetting.setRoadBookPrefs(testRule)
        PreferencesSetting.enableTouchClick()
        emptyLocalFiles(picturesDir)
        launch { emptyFirebaseStorage(GeneralUtils.getStorage().reference) }.join()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        goToPictureFragment()
        triggerShutter(device)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() = runTest {
        launch { emptyFirebaseStorage(GeneralUtils.getStorage().reference) }.join()
        emptyLocalFiles(picturesDir)
    }

    @Test
    fun takingPictureWorks() {
        triggerDone(device)
    }

    @Test
    fun cancelingPictureWorks() {
        triggerCancel(device)
    }

}
