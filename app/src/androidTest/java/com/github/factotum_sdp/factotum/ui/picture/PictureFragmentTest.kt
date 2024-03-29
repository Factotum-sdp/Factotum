package com.github.factotum_sdp.factotum.ui.picture

import android.Manifest
import android.os.Environment
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
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
        Intents.init()
        GeneralUtils.injectBossAsLoggedInUser(testRule)
        PreferencesSetting.setRoadBookPrefs(testRule)
        goToPictureFragment()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() = runTest {
        Intents.release()
    }

    @Test
    fun goesIntoFragment() = runBlocking {
        //Check that the intent for the camera is launched
        delay(TIME_WAIT_BETWEEN_ACTIONS)
        Intents.intended(hasAction("android.media.action.IMAGE_CAPTURE"))
    }

}
