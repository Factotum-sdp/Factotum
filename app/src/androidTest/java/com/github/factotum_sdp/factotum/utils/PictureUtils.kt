package com.github.factotum_sdp.factotum.utils

import com.google.firebase.storage.FirebaseStorage
import java.io.File

class PictureUtils {

    companion object {
        const val TIME_WAIT_SHUTTER = 5000L
        const val TIME_WAIT_DONE_OR_CANCEL = 3000L
        const val TIME_WAIT_UPLOAD = 500L

        fun emptyFirebaseStorage(storage: FirebaseStorage) {
            storage.reference.listAll().addOnSuccessListener { listResult ->
                listResult.items.forEach { item ->
                    item.delete()
                }
            }
        }

        fun emptyLocalFiles(dir: File) {
            dir.listFiles()?.forEach { file ->
                file.delete()
            }
        }
    }
}