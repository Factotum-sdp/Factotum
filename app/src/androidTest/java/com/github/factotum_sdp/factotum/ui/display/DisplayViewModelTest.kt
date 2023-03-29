package com.github.factotum_sdp.factotum.ui.display

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.factotum_sdp.factotum.ui.display.utils.WAIT_TIME_INIT
import com.github.factotum_sdp.factotum.ui.display.utils.WAIT_TIME_REFRESH
import com.github.factotum_sdp.factotum.ui.display.utils.emptyStorageEmulator
import com.github.factotum_sdp.factotum.ui.display.utils.uploadImageToStorageEmulator
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class DisplayViewModelTest {
    private lateinit var displayViewModel: DisplayViewModel
    private lateinit var context: Context

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        // Initialize Firebase
        Firebase.storage.useEmulator("10.0.2.2", 9199)
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @After
    fun tearDown() {
        emptyStorageEmulator(Firebase.storage.reference)
    }


    @Test
    fun testFetchPhotoReferences() {
        // Initialize the ViewModel
        displayViewModel = DisplayViewModel()

        Thread.sleep(WAIT_TIME_INIT)

        //Check that the photoReferences is empty
        Assert.assertTrue("PhotosReference should be empty.", displayViewModel.photoReferences.value?.isEmpty() ?: true)

        runBlocking {
            val imagePath = "test_image1.jpg"
            uploadImageToStorageEmulator(context, imagePath, "test_image1.jpg")
        }

        // Refresh the images
        displayViewModel.refreshImages()

        Thread.sleep(WAIT_TIME_REFRESH)

        // Check that the photoReferences is not empty
        Assert.assertFalse("PhotosReference should not be empty.", displayViewModel.photoReferences.value?.isEmpty() ?: false)

        runBlocking {
            val imagePath = "test_image2.jpg"
            uploadImageToStorageEmulator(context, imagePath, "test_image2.jpg")
        }

        // Refresh the images
        displayViewModel.refreshImages()

        Thread.sleep(WAIT_TIME_REFRESH)

        // Check that the photoReferences has two items
        Assert.assertEquals("PhotosReference should have two items.", 2, displayViewModel.photoReferences.value?.size)
    }

    @Test
    fun testFetchPhotoShouldHaveOnePhotoRefAfterInit() {
        runBlocking {
            val imagePath = "test_image1.jpg"
            uploadImageToStorageEmulator(context, imagePath, "test_image1.jpg")
        }

        // Initialize the ViewModel
        displayViewModel = DisplayViewModel()

        Thread.sleep(WAIT_TIME_INIT)

        // Check that the photoReferences is not empty
        Assert.assertFalse("PhotosReference should not be empty.", displayViewModel.photoReferences.value?.isEmpty() ?: false)
    }

}


