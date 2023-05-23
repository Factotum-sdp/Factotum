package com.github.factotum_sdp.factotum.ui.display

import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.view.View
import android.widget.DatePicker
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.ui.display.utils.*
import com.github.factotum_sdp.factotum.ui.picture.emptyFirebaseStorage
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.logout
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.junit.*
import org.junit.runner.RunWith
import java.util.Date

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

        @BeforeClass
        @JvmStatic
        fun dismissANRSystemDialog() {
            val device = UiDevice.getInstance(getInstrumentation())
            val waitButton = device.findObject(UiSelector().textContains("wait"))
            if (waitButton.exists()) {
                waitButton.click()
            }
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
        logout()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayOnlyOnePhotoIfSame() = runTest {
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        GeneralUtils.fillUserEntryAndEnterTheApp("client@gmail.com", "123456")

        onView(withId(R.id.menu_refresh_icon)).perform(click())

        launch { uploadImageToStorageEmulator(context,"Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()

        onView(withId(R.id.menu_refresh_icon)).perform(click())

        val recyclerView = onView(withId(R.id.recyclerView))

        //Check if only one photo is displayed
        recyclerView.check(matches(hasItemCount(1)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayTwoDifferentPhotosWorks() = runTest {
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH2, TEST_IMAGE_PATH2) }.join()
        GeneralUtils.fillUserEntryAndEnterTheApp("client@gmail.com", "123456")


        onView(withId(R.id.menu_refresh_icon)).perform(click())


        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayOneBadFormatPhotosWorks() = runTest {
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH3, TEST_IMAGE_PATH3) }.join()
        GeneralUtils.fillUserEntryAndEnterTheApp("client@gmail.com", "123456")

        onView(withId(R.id.menu_refresh_icon)).perform(click())

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(1)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun displayTwoBadFormatPhotosWorks() = runTest {
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH4, TEST_IMAGE_PATH4) }.join()
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH3, TEST_IMAGE_PATH3) }.join()
        GeneralUtils.fillUserEntryAndEnterTheApp("client@gmail.com", "123456")



        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))
    }

    @Test
    fun displayNoPhotosIfEmpty() {
        GeneralUtils.fillUserEntryAndEnterTheApp("client@gmail.com", "123456")
        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(0)))

        onView(withId(R.id.menu_refresh_icon)).perform(click())

        recyclerView.check(matches(hasItemCount(0)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun clickingOnPhotosFireCorrectIntents() = runTest {
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        GeneralUtils.fillUserEntryAndEnterTheApp("client@gmail.com", "123456")

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
    fun sharingPhotoWorks() = runTest {
        launch { uploadImageToStorageEmulator(context, "Buhagiat", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")

        goToDisplayFragment()

        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        onView(withId(R.id.shareButton)).perform(click())

        Intents.intended(hasAction(Intent.ACTION_CHOOSER))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun cantShareIfNoPhoneNumber() = runTest {
        launch { uploadImageToStorageEmulator(context, "750ukPcnZS3xZKTAk6fQmj04", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")

        goToDisplayFragment()

        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        onView(withId(R.id.shareButton)).perform(click())

        assert(Intents.getIntents().isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun bossCanSeeFolders() = runTest {
        launch { uploadImageToStorageEmulator(context, "Boss", TEST_IMAGE_PATH2, TEST_IMAGE_PATH2) }.join()
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")

        goToDisplayFragment()

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun bossCanClickOnFolderAndSeePhotos() = runTest {
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH2, TEST_IMAGE_PATH2) }.join()
        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")

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
    fun bossCanStillSeePhotos() = runTest {
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")

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
    fun bossCanGoBackFromClientFolder() = runTest {
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH2, TEST_IMAGE_PATH2) }.join()
        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")

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
    fun refreshWorksOnFolders() = runTest {
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")

        goToDisplayFragment()

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(1)))

        launch { uploadImageToStorageEmulator(context, "Boss", TEST_IMAGE_PATH2, TEST_IMAGE_PATH2) }.join()

        onView(withId(R.id.menu_refresh_icon)).perform(click())

        recyclerView.check(matches(hasItemCount(2)))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun pickingDateWorks() = runTest {
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH1, TEST_IMAGE_PATH1) }.join()
        launch { uploadImageToStorageEmulator(context, "Client", TEST_IMAGE_PATH2, TEST_IMAGE_PATH2) }.join()
        GeneralUtils.fillUserEntryAndEnterTheApp("boss@gmail.com", "123456")

        goToDisplayFragment()

        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))

        onView(withId(R.id.menu_date_picker)).perform(click())
        onView(withText("OK")).perform(click())

        recyclerView.check(matches(hasItemCount(0)))
    }

}