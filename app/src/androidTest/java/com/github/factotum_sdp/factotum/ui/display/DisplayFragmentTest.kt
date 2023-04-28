package com.github.factotum_sdp.factotum.ui.display

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.github.factotum_sdp.factotum.ui.display.utils.*
import com.github.factotum_sdp.factotum.ui.picture.emptyFirebaseStorage
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayFragmentTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )


    companion object {
        @OptIn(ExperimentalCoroutinesApi::class)
        @JvmStatic
        @BeforeClass
        fun setUpClass()= runTest {
            initFirebase()
            UsersPlaceHolder.init(GeneralUtils.getDatabase(), GeneralUtils.getAuth())
            launch { GeneralUtils.addUserToDatabase(UsersPlaceHolder.USER_CLIENT) }.join()
            Intents.init()
        }

        @JvmStatic
        @AfterClass
        fun tearDownClass() {
            Intents.release()
        }
    }

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        GeneralUtils.fillUserEntryAndGoToRBFragment("client@gmail.com", "123456")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() = runTest {
        launch { emptyFirebaseStorage(FirebaseStorage.getInstance().reference) }.join()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayOnlyOnePhotoIfSame() = runTest {
        launch { uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed())).perform(click())

        launch { uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed())).perform(click())

        delay(WAIT_TIME_REFRESH)

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(1)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayTwoDifferentPhotosWorks() = runTest {
        launch { uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed())).perform(click())

        launch { uploadImageToStorageEmulator(context, TEST_IMAGE_PATH2, TEST_IMAGE_PATH2) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed())).perform(click())

        delay(WAIT_TIME_REFRESH)

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayMixingFormatPhotosWorks() = runTest {
        launch { uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()

        launch { uploadImageToStorageEmulator(context, TEST_IMAGE_PATH3, TEST_IMAGE_PATH3) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed())).perform(click())

        delay(WAIT_TIME_REFRESH)

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))

        launch { emptyFirebaseStorage(FirebaseStorage.getInstance().reference) }.join()

        launch { uploadImageToStorageEmulator(context, TEST_IMAGE_PATH2, TEST_IMAGE_PATH2) }.join()

        launch { uploadImageToStorageEmulator(context, TEST_IMAGE_PATH4, TEST_IMAGE_PATH4) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed())).perform(click())

        delay(WAIT_TIME_REFRESH)

        recyclerView.check(matches(hasItemCount(2)))
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayOneBadFormatPhotosWorks() = runTest {
        launch { uploadImageToStorageEmulator(context, TEST_IMAGE_PATH3, TEST_IMAGE_PATH3) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed())).perform(click())

        delay(WAIT_TIME_REFRESH)

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(1)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayTwoBadFormatPhotosWorks() = runTest {
        launch { uploadImageToStorageEmulator(context, TEST_IMAGE_PATH3, TEST_IMAGE_PATH3) }.join()

        launch { uploadImageToStorageEmulator(context, TEST_IMAGE_PATH4, TEST_IMAGE_PATH4) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed())).perform(click())

        delay(WAIT_TIME_REFRESH)

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))
    }

    @Test
    fun displayFragmentDisplayNoPhotosIfEmpty() {
        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(0)))

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed())).perform(click())

        runBlocking{ delay(WAIT_TIME_REFRESH) }

        recyclerView.check(matches(hasItemCount(0)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentClickingOnPhotosFireCorrectIntents() = runTest {
        launch { uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        //Press on the refresh button
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed())).perform(click())

        delay(WAIT_TIME_REFRESH)

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
        launch { uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed())).perform(click())

        delay(WAIT_TIME_REFRESH)

        onView(withId(R.id.shareButton)).perform(click())

        //Check if the intent of sharing has been called
        Intents.intended(hasAction(Intent.ACTION_CHOOSER))
    }

}
