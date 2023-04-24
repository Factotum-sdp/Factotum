package com.github.factotum_sdp.factotum.ui.display.utils

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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
        val storageReference = Firebase.storage.reference.child(storagePath)
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

fun emptyStorageEmulator(storageRef: StorageReference) {
    // Empty Firebase Storage
    val latch = CountDownLatch(1)

    storageRef.listAll().addOnSuccessListener { listResult ->
        val itemsCount = listResult.items.size

        if (itemsCount == 0) {
            latch.countDown()
        } else {
            listResult.items.forEach { item ->
                item.delete().addOnSuccessListener {
                    if (latch.count - 1 == 0L) {
                        latch.countDown()
                    } else {
                        latch.countDown()
                    }
                }
            }
        }
    }

    // Wait for all files to be deleted before proceeding to the next test
    latch.await()
}
