package com.github.factotum_sdp.factotum.ui.display

import android.content.Context
import android.content.Intent
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.ui.display.utils.*
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayFragmentTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            initFirebase()
            Intents.init()
        }

        @JvmStatic
        @AfterClass
        fun tearDownClass() {
            Intents.release()
        }
    }

    private lateinit var scenario: FragmentScenario<DisplayFragment>
    private lateinit var context: Context

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_Factotum)
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @After
    fun tearDown() {
        emptyStorageEmulator(Firebase.storage.reference)
    }

    @Test
    fun displayFragmentUiElementsDisplayed() {
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed()))
    }

    @Test
    fun displayFragmentRecyclerViewHasCorrectLayoutManager() {
        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.layoutManager is LinearLayoutManager)
        }
    }

    @Test
    fun displayFragmentRefreshButtonClicked() {
        onView(withId(R.id.refreshButton)).perform(click())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayOnlyOnePhotoIfSame()  = runTest { 
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        runBlocking {
            delay(WAIT_TIME_INIT)
            onView(withId(R.id.refreshButton)).perform(click())
            delay(WAIT_TIME_REFRESH)
        }

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        runBlocking {
            delay(WAIT_TIME_INIT)
            onView(withId(R.id.refreshButton)).perform(click())
            delay(WAIT_TIME_REFRESH)
        }

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 1)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayTwoDifferentPhotosWorks() = runTest {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        runBlocking {
            delay(WAIT_TIME_INIT)
            onView(withId(R.id.refreshButton)).perform(click())
            delay(WAIT_TIME_REFRESH)
        }

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH2, TEST_IMAGE_PATH2)
        }

        runBlocking {
            delay(WAIT_TIME_INIT)
            onView(withId(R.id.refreshButton)).perform(click())
            delay(WAIT_TIME_REFRESH)
        }

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 2)
        }
    }

    @Test
    fun displayFragmentDisplayOneBadFormatPhotosWorks() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH3, TEST_IMAGE_PATH3)
        }

        runBlocking {
            delay(WAIT_TIME_INIT)
            onView(withId(R.id.refreshButton)).perform(click())
            delay(WAIT_TIME_REFRESH)
        }

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 1)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayMixingFormatPhotosWorks() = runTest {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH3, TEST_IMAGE_PATH3)
        }

        runBlocking {
            delay(WAIT_TIME_INIT)
            onView(withId(R.id.refreshButton)).perform(click())
            delay(WAIT_TIME_REFRESH)
        }

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 2)
        }

        //Empty storage
        emptyStorageEmulator(Firebase.storage.reference)


        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH4, TEST_IMAGE_PATH4)
        }

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH2, TEST_IMAGE_PATH2)
        }

        runBlocking {
            delay(WAIT_TIME_INIT)
            onView(withId(R.id.refreshButton)).perform(click())
            delay(WAIT_TIME_REFRESH)
        }

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 2)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayTwoBadFormatPhotosWorks() = runTest {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH3, TEST_IMAGE_PATH3)
        }

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH4, TEST_IMAGE_PATH4)
        }

        runBlocking {
            delay(WAIT_TIME_INIT)
            onView(withId(R.id.refreshButton)).perform(click())
            delay(WAIT_TIME_REFRESH)
        }

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 2)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayNoPhotosIfEmpty() = runTest {
        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 0)
        }

        runBlocking {
            onView(withId(R.id.refreshButton)).perform(click())
            delay(WAIT_TIME_REFRESH)
        }

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 0)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentClickingOnPhotosFireCorrectIntents() = runTest {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        runBlocking {
            onView(withId(R.id.refreshButton)).perform(click())
            delay(WAIT_TIME_REFRESH)
        }

        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        Intents.intended(hasAction(Intent.ACTION_VIEW))
        Intents.intended(hasType("image/*"))
        Intents.intended(hasFlag(Intent.FLAG_GRANT_READ_URI_PERMISSION))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentSharingPhotoWorks() = runTest {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        runBlocking {
            delay(WAIT_TIME_INIT)
            onView(withId(R.id.refreshButton)).perform(click())
            delay(WAIT_TIME_REFRESH)
        }

        onView(withId(R.id.shareButton)).perform(click())

        //Check if the intent of sharing has been called
        Intents.intended(hasAction(Intent.ACTION_CHOOSER))
    }
}
