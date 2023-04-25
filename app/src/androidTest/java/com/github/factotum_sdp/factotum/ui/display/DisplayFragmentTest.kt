package com.github.factotum_sdp.factotum.ui.display

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.factotum_sdp.factotum.LOGIN_REFRESH_TIME
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.github.factotum_sdp.factotum.ui.display.utils.*
import com.github.factotum_sdp.factotum.ui.login.LoginFragmentTest
import com.github.factotum_sdp.factotum.ui.picture.PictureFragment
import com.github.factotum_sdp.factotum.ui.picture.emptyFirebaseStorage
import com.github.factotum_sdp.factotum.utils.ContactsUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.bytebuddy.agent.VirtualMachine.ForOpenJ9.Dispatcher
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayFragmentTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )


    companion object {
        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            initFirebase()

            UsersPlaceHolder.init(GeneralUtils.getDatabase(), GeneralUtils.getAuth())

            runBlocking {
                try {
                    UsersPlaceHolder.addAuthUser(UsersPlaceHolder.USER_CLIENT)
                } catch (e : FirebaseAuthUserCollisionException) {
                    e.message?.let { Log.e("DisplayFragmentTest", it) }
                }

                UsersPlaceHolder.addUserToDb(UsersPlaceHolder.USER_CLIENT)
            }

            Intents.init()
        }

        @JvmStatic
        @AfterClass
        fun tearDownClass() {
            Intents.release()
            runBlocking{emptyFirebaseStorage(Firebase.storage.reference)}
        }
    }

    private lateinit var context: Context

    @Before
    fun setUp() {
        LoginFragmentTest.fillUserEntryAndGoToRBFragment("client@gmail.com", "123456")
        Thread.sleep(LOGIN_REFRESH_TIME)
        context = InstrumentationRegistry.getInstrumentation().context
    }


    @After
    fun tearDown() {
        runBlocking {
            emptyFirebaseStorage(FirebaseStorage.getInstance().reference)
        }
    }
    @Test
    fun displayFragmentDisplayOnlyOnePhotoIfSame() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        onView(withId(R.id.refreshButton)).perform(click())

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }


        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        val recyclerView = onView(withId(R.id.recyclerView))

        //Check if only one photo is displayed
        recyclerView.check(matches(hasItemCount(1)))
    }

    @Test
    fun displayFragmentDisplayTwoDifferentPhotosWorks() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH2, TEST_IMAGE_PATH2)
        }

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))
    }

    @Test
    fun displayFragmentDisplayOneBadFormatPhotosWorks() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH3, TEST_IMAGE_PATH3)
        }

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(1)))
    }


    @Test
    fun displayFragmentDisplayMixingFormatPhotosWorksOneWay() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH3, TEST_IMAGE_PATH3)
        }

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))
    }

    @Test
    fun displayFragmentDisplayMixingFormatPhotosWorksOtherWay() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH2, TEST_IMAGE_PATH2)
        }

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH4, TEST_IMAGE_PATH4)
        }


        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))
    }


    @Test
    fun displayFragmentDisplayTwoBadFormatPhotosWorks() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH3, TEST_IMAGE_PATH3)
        }

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH4, TEST_IMAGE_PATH4)
        }


        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(2)))
    }

    @Test
    fun displayFragmentDisplayNoPhotosIfEmpty() {
        val recyclerView = onView(withId(R.id.recyclerView))
        recyclerView.check(matches(hasItemCount(0)))

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        recyclerView.check(matches(hasItemCount(0)))
    }

    @Test
    fun displayFragmentClickingOnPhotosFireCorrectIntents() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

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

    @Test
    fun displayFragmentSharingPhotoWorks() {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        onView(withId(R.id.refreshButton)).perform(click())

        Thread.sleep(WAIT_TIME_REFRESH)

        onView(withId(R.id.shareButton)).perform(click())

        //Check if the intent of sharing has been called
        Intents.intended(hasAction(Intent.ACTION_CHOOSER))
    }

}
