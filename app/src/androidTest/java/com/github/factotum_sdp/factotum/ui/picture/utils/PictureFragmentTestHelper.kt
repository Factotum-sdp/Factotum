package com.github.factotum_sdp.factotum.ui.picture

import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.github.factotum_sdp.factotum.ui.login.LoginFragmentTest
import com.github.factotum_sdp.factotum.utils.PreferencesSetting
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.File

const val TIME_WAIT_SHUTTER = 1000L
const val TIME_WAIT_DONE_OR_CANCEL = 1000L
const val TIME_WAIT_UPLOAD_PHOTO = 1000L
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
    LoginFragmentTest.fillUserEntryAndGoToRBFragment("courier@gmail.com", "123456")

    PreferencesSetting.enableTouchClick()

    // Click on one of the roadbook
    val destID = DestinationRecords.RECORDS[2].destID
    onView(withText(destID)).perform(click())

    // Go to the picture fragment
    onView(withId(R.id.viewPager)).perform(swipeLeft())
    onView(withId(R.id.viewPager)).perform(swipeLeft())
    onView(withId(R.id.viewPager)).perform(swipeLeft())
}

fun addUserToDatabase(user : UsersPlaceHolder.User) {
    // DO NOT REMOVE THIS PART OR PUT IT IN A @BeforeClass
    runBlocking {
        try {
            UsersPlaceHolder.addAuthUser(user)
        } catch (e : FirebaseAuthUserCollisionException) {
            e.message?.let { Log.e("DisplayFragmentTest", it) }
        }

        UsersPlaceHolder.addUserToDb(user)
    }
}
