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
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.ui.display.utils.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class DisplayFragmentTest {

    private lateinit var scenario: FragmentScenario<DisplayFragment>
    private lateinit var context: Context

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_Factotum)
        Firebase.storage.useEmulator("10.0.2.2", 9199)
        context = InstrumentationRegistry.getInstrumentation().context
        Intents.init()
    }

    @After
    fun tearDown() {
        emptyStorageEmulator(Firebase.storage.reference)
        Intents.release()
    }

    @Test
    fun displayFragment_uiElementsDisplayed() {
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed()))
    }

    @Test
    fun displayFragment_recyclerViewHasCorrectLayoutManager() {
        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.layoutManager is LinearLayoutManager)
        }
    }

    @Test
    fun displayFragment_refreshButtonClicked() {
        onView(withId(R.id.refreshButton)).perform(click())
    }

    @Test
    fun displayFragment_displayOnlyOnePhotoIfSame() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        Thread.sleep(WAIT_TIME_INIT)

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        Thread.sleep(WAIT_TIME_INIT)

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 1)
        }
    }

    @Test
    fun displayFragment_displayTwoDifferentPhotosWorks() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        Thread.sleep(WAIT_TIME_INIT)

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH2, TEST_IMAGE_PATH2)
        }

        Thread.sleep(WAIT_TIME_INIT)

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 2)
        }
    }

    @Test
    fun displayFragment_displayOneBadFormatPhotosWorks() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH3, TEST_IMAGE_PATH3)
        }

        Thread.sleep(WAIT_TIME_INIT)

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 1)
        }
    }


    @Test
    fun displayFragment_displayMixingFormatPhotosWorks() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH3, TEST_IMAGE_PATH3)
        }

        Thread.sleep(WAIT_TIME_INIT)

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

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

        Thread.sleep(WAIT_TIME_INIT)

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 2)
        }
    }


    @Test
    fun displayFragment_displayTwoBadFormatPhotosWorks() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH3, TEST_IMAGE_PATH3)
        }

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH4, TEST_IMAGE_PATH4)
        }

        Thread.sleep(WAIT_TIME_INIT)

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 2)
        }
    }

    @Test
    fun displayFragment_displayNoPhotosIfEmpty() {
        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 0)
        }

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.adapter?.itemCount == 0)
        }
    }

    @Test
    fun displayFragment_sharingPhotoWorks() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        Thread.sleep(WAIT_TIME_INIT)

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        onView(withId(R.id.shareButton)).perform(click())

        //Check if the intent of sharing has been called
        Intents.intended(hasAction(Intent.ACTION_CHOOSER))
    }
}
