package com.github.factotum_sdp.factotum.ui.display

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.ui.display.utils.*
import com.github.factotum_sdp.factotum.ui.picture.emptyFirebaseStorage
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayFragmentTest {

    private lateinit var context: Context

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            initFirebase()
        }

        @JvmStatic
        @AfterClass
        fun tearDownClass() {
            runBlocking{ emptyFirebaseStorage(Firebase.storage.reference) }
        }
    }


    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        Intents.init()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() = runTest {
        launch { emptyFirebaseStorage(FirebaseStorage.getInstance().reference) }.join()
        Intents.release()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayOnlyOnePhotoIfSame() = runTest {
        GeneralUtils.fillUserEntryAndEnterTheApp("client@gmail.com", "123456")
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).perform(click())

        launch { uploadImageToStorageEmulator(context,"Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).perform(click())

        val recyclerView = onView(withId(R.id.recyclerView))

        //Check if only one photo is displayed
        recyclerView.check(matches(hasItemCount(1)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayTwoDifferentPhotosWorks() = runTest {
        GeneralUtils.fillUserEntryAndEnterTheApp("client@gmail.com", "123456")
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).perform(click())


        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH2, TEST_IMAGE_PATH2) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).perform(click())


        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayOneBadFormatPhotosWorks() = runTest {
        GeneralUtils.fillUserEntryAndEnterTheApp("client@gmail.com", "123456")
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH3, TEST_IMAGE_PATH3) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).perform(click())

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(1)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentDisplayTwoBadFormatPhotosWorks() = runTest {
        GeneralUtils.fillUserEntryAndEnterTheApp("client@gmail.com", "123456")
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH3, TEST_IMAGE_PATH3) }.join()

        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH4, TEST_IMAGE_PATH4) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).perform(click())

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))
    }

    @Test
    fun displayFragmentDisplayNoPhotosIfEmpty() {
        GeneralUtils.fillUserEntryAndEnterTheApp("client@gmail.com", "123456")
        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(0)))

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).perform(click())

        recyclerView.check(matches(hasItemCount(0)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentClickingOnPhotosFireCorrectIntents() = runTest {
        GeneralUtils.fillUserEntryAndEnterTheApp("client@gmail.com", "123456")
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).perform(click())

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
        GeneralUtils.fillUserEntryAndEnterTheApp("client@gmail.com", "123456")
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()

        //Press on the refresh button
        onView(withId(R.id.refreshButton)).perform(click())

        onView(withId(R.id.shareButton)).perform(click())

        //Check if the intent of sharing has been called
        Intents.intended(hasAction(Intent.ACTION_CHOOSER))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentBossCanSeeFolders() = runTest {
        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        launch { uploadImageToStorageEmulator(context, "Boss", TEST_IMAGE_PATH2, TEST_IMAGE_PATH2) }.join()

        goToDisplayFragment()

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentBossCanClickOnFolderAndSeePhotos() = runTest {
        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH2, TEST_IMAGE_PATH2) }.join()

        goToDisplayFragment()

        //click on the first folder
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentBossCanStillSeePhotos() = runTest {
        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()

        goToDisplayFragment()

        //click on the first folder
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        //click on the first photo
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
    fun displayFragmentBossCanGoBackFromClientFolder() = runTest {
        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH2, TEST_IMAGE_PATH2) }.join()

        goToDisplayFragment()

        //click on the first folder
        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        //Go back
        pressBack()

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(1)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayFragmentRefreshWorksOnFolders() = runTest {
        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()

        goToDisplayFragment()

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(1)))

        launch { uploadImageToStorageEmulator(context, "Boss", TEST_IMAGE_PATH2, TEST_IMAGE_PATH2) }.join()

        onView(withId(R.id.refreshButton)).perform(click())

        recyclerView.check(matches(hasItemCount(2)))
    }
}