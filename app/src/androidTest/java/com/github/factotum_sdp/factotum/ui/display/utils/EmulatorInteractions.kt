package com.github.factotum_sdp.factotum.ui.display.utils

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.suspendCancellableCoroutine
import org.hamcrest.Description
import org.hamcrest.Matcher
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

const val WAIT_TIME_REFRESH = 1000L
const val WAIT_TIME_INIT = 1000L
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

suspend fun deleteProfileDispatch(database: DatabaseReference) = suspendCoroutine { continuation ->
    val profileDispatchRef = database.child("profile-dispatch")
    profileDispatchRef.removeValue().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(Unit)
        } else {
            continuation.resumeWithException(task.exception!!)
        }
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

