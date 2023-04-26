package com.github.factotum_sdp.factotum.ui.display.utils

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.hamcrest.Description
import org.hamcrest.Matcher
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

const val WAIT_TIME_REFRESH = 500L
const val TEST_IMAGE_PATH1 = "USER_25-03-2023_17-57-11.jpg"
const val TEST_IMAGE_PATH2 = "USER_26-03-2023_17-57-11.jpg"
const val TEST_IMAGE_PATH3 = "test_image3.jpg"
const val TEST_IMAGE_PATH4 = "test_image4.jpg"

suspend fun uploadImageToStorageEmulator(
    context: Context,
    imagePath: String,
    storagePath: String
): UploadTask.TaskSnapshot = suspendCancellableCoroutine { continuation ->
    try {
        val storageReference = Firebase.storage.reference.child("Client/$storagePath")
        val inputStream = context.assets.open(imagePath)

        val uploadTask = storageReference.putStream(inputStream)
        uploadTask.addOnSuccessListener { snapshot ->
            continuation.resume(snapshot)
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }

        continuation.invokeOnCancellation {
            uploadTask.cancel()
        }
    } catch (exception: Exception) {
        continuation.resumeWithException(exception)
    }
}

fun hasItemCount(count: Int): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description?) {
            description?.appendText("Expected item count: $count")
        }

        override fun matchesSafely(recyclerView: RecyclerView?): Boolean {
            return recyclerView?.adapter?.itemCount == count
        }
    }
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


