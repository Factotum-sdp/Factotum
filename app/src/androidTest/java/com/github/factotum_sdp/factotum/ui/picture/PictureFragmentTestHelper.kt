package com.github.factotum_sdp.factotum.ui.picture

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.utils.PreferencesSetting
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.File

const val TIME_WAIT_SHUTTER = 5000L
const val TIME_WAIT_DONE_OR_CANCEL = 3000L
const val TIME_WAIT_UPLOAD = 500L


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
    device.executeShellCommand("input keyevent 27")
}

fun triggerDone(device: UiDevice) {
    device.executeShellCommand("input keyevent 61")
    device.executeShellCommand("input keyevent 61")
    device.executeShellCommand("input keyevent 62")
}

fun triggerCancel(device: UiDevice) {
    device.executeShellCommand("input keyevent 61")
    device.executeShellCommand("input keyevent 61")
    device.executeShellCommand("input keyevent 61")
    device.executeShellCommand("input keyevent 62")
}

fun goToPictureFragment() {
    // Open the drawer
    onView(withId(R.id.drawer_layout))
        .perform(open())
    onView(withId(R.id.roadBookFragment))
        .perform(click())

    PreferencesSetting.enableTouchClick()

    // Click on one of the roadbook
    val destID = DestinationRecords.RECORDS[2].destID
    onView(withText(destID)).perform(click())

    // Go to the picture fragment
    onView(withId(R.id.viewPager)).perform(swipeLeft())
    onView(withId(R.id.viewPager)).perform(swipeLeft())
    onView(withId(R.id.viewPager)).perform(swipeLeft())
}
