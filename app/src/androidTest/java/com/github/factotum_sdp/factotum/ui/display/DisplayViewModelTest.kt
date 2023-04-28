package com.github.factotum_sdp.factotum.ui.display

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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
class DisplayViewModelTest {
    private lateinit var displayViewModel: DisplayViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    companion object {
        private lateinit var context: Context

        @BeforeClass
        @JvmStatic
        fun setUp() {
            // Initialize Firebase
            initFirebase()
            context = InstrumentationRegistry.getInstrumentation().context
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            emptyStorageEmulator(Firebase.storage.reference)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testFetchPhotoReferences()  = runTest {
        // Initialize the ViewModel
        displayViewModel = DisplayViewModel()

        runBlocking {
            delay(WAIT_TIME_INIT)
        }

        //Check that the photoReferences is empty
        Assert.assertTrue(
            "PhotosReference should be empty.",
            displayViewModel.photoReferences.value?.isEmpty() ?: true
        )

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        runBlocking {
            // Refresh the images
            displayViewModel.refreshImages()
            delay(WAIT_TIME_REFRESH)
        }

        // Check that the photoReferences is not empty
        Assert.assertFalse(
            "PhotosReference should not be empty.",
            displayViewModel.photoReferences.value?.isEmpty() ?: false
        )

        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH2, TEST_IMAGE_PATH2)
        }

        runBlocking {
            // Refresh the images
            displayViewModel.refreshImages()
            delay(WAIT_TIME_REFRESH)
        }

        // Check that the photoReferences has two items
        Assert.assertEquals(
            "PhotosReference should have two items.",
            2,
            displayViewModel.photoReferences.value?.size
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testFetchPhotoShouldHaveOnePhotoRefAfterInit() = runTest {
        runBlocking {
            uploadImageToStorageEmulator(context, TEST_IMAGE_PATH1, TEST_IMAGE_PATH1)
        }

        runBlocking {
            // Initialize the ViewModel
            displayViewModel = DisplayViewModel()
            delay(WAIT_TIME_INIT)
        }

        // Check that the photoReferences is not empty
        Assert.assertFalse(
            "PhotosReference should not be empty.",
            displayViewModel.photoReferences.value?.isEmpty() ?: false
        )
    }

}


