package com.github.factotum_sdp.factotum.ui.picture

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class UploadWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    private val storage = FirebaseStorage.getInstance()


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val picturesDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return@withContext Result.failure()
        val clientDirs = picturesDir.listFiles()?.filter { it.isDirectory } ?: return@withContext Result.failure()

        for (clientDir in clientDirs) {
            if (!uploadFile(clientDir)) {
                return@withContext Result.failure()
            }
        }
        return@withContext Result.success()
    }

    private suspend fun uploadFile(directory: File): Boolean {
        val files = directory.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.isFile) {
                    val storageRef = storage.reference.child("${directory.name}/${file.name}")
                    try {
                        storageRef.putFile(file.toUri()).await()
                        file.delete()
                    } catch (e: Exception) {
                        Log.e("UploadWorker", "Upload error", e)
                        return false
                    }
                }
            }
        }
        return true
    }
}


