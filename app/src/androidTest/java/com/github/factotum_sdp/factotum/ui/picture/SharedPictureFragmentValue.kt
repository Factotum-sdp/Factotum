package com.github.factotum_sdp.factotum.ui.picture

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.File

const val TIME_WAIT_SHUTTER = 3000L
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
