package com.github.factotum_sdp.factotum.ui.picture

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.File

const val TIME_WAIT_UPLOAD_PHOTO = 2000L
const val TIME_WAIT_BETWEEN_ACTIONS = 500L

// HELPER METHODS
suspend fun emptyFirebaseStorage(storageRef: StorageReference) {
    val listResult = storageRef.listAll().await()

    listResult.items.forEach { item ->
        item.delete().await()
    }

    listResult.prefixes.forEach { prefix ->
        emptyFirebaseStorage(prefix)
    }
}


fun emptyLocalFiles(dir: File) {
    dir.listFiles()?.forEach { file ->
        if (file.isDirectory) {
            emptyLocalFiles(file) // Recursively delete files in the directory
        }
        file.delete()
    }
}

fun triggerShutter(device: UiDevice) {
    val captureButton = device.findObject(UiSelector().description("Shutter"));
    captureButton.click()
}

fun triggerDone(device: UiDevice) {
    val doneButton = device.findObject(UiSelector().description("Done"))
    doneButton.click()
}

fun triggerCancel(device: UiDevice) {
    val cancelButton = device.findObject(UiSelector().description("Cancel"))
    cancelButton.click()
}

fun goToPictureFragment() {
    // Click on one of the roadbook
    val destID = DestinationRecords.RECORDS[0].destID
    onView(withText(destID)).perform(click())

    // Go to the picture fragment
    onView(withId(R.id.viewPager)).perform(swipeLeft())
    onView(withId(R.id.viewPager)).perform(swipeLeft())
    onView(withId(R.id.viewPager)).perform(swipeLeft())
}
