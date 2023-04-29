package com.github.factotum_sdp.factotum.ui.picture

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.PreferencesSetting
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

const val TIME_WAIT_DONE_OR_CANCEL = 1000L
const val TIME_WAIT_BETWEEN_INPUTS = 250L
const val TIME_WAIT_UPLOAD_PHOTO = 1000L
const val TIME_WAIT_DELETE_PHOTO = 1000L
const val CLIENT_ID = "X17"


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


suspend fun emptyLocalFiles(dir: File) : Unit = withContext(Dispatchers.IO) {
    dir.listFiles()?.forEach { file ->
        if (file.isDirectory) {
            emptyLocalFiles(file)
        }
        file.delete()
    }
}

fun triggerShutter(device: UiDevice) {
    device.executeShellCommand("input keyevent 27")
}

fun triggerDone(device: UiDevice) {
    runBlocking { delay(TIME_WAIT_DONE_OR_CANCEL) }
    device.executeShellCommand("input keyevent 61")
    runBlocking { delay(TIME_WAIT_BETWEEN_INPUTS) }
    device.executeShellCommand("input keyevent 61")
    runBlocking { delay(TIME_WAIT_BETWEEN_INPUTS) }
    device.executeShellCommand("input keyevent 66")
}

fun triggerCancel(device: UiDevice) {
    runBlocking { delay(TIME_WAIT_DONE_OR_CANCEL) }
    device.executeShellCommand("input keyevent 61")
    runBlocking { delay(TIME_WAIT_BETWEEN_INPUTS) }
    device.executeShellCommand("input keyevent 61")
    runBlocking { delay(TIME_WAIT_BETWEEN_INPUTS) }
    device.executeShellCommand("input keyevent 61")
    runBlocking { delay(TIME_WAIT_BETWEEN_INPUTS) }
    device.executeShellCommand("input keyevent 66")
}



fun goToPictureFragment() {
    GeneralUtils.fillUserEntryAndGoToRBFragment("courier@gmail.com", "123456")

    PreferencesSetting.enableTouchClick()

    // Click on one of the roadbook
    val destID = DestinationRecords.RECORDS[2].destID
    onView(withText(destID)).perform(click())

    // Go to the picture fragment
    onView(withId(R.id.viewPager)).perform(swipeLeft())
    onView(withId(R.id.viewPager)).perform(swipeLeft())
    onView(withId(R.id.viewPager)).perform(swipeLeft())
}