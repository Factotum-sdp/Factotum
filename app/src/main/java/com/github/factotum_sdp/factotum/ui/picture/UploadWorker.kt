package com.github.factotum_sdp.factotum.ui.picture

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class UploadWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    private val storage = FirebaseStorage.getInstance()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val picturesDirectory = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return@withContext Result.failure()
        val pictureEntries = picturesDirectory.listFiles() ?: return@withContext Result.failure()
        val clientsDirectories = pictureEntries.filter { it.isDirectory }

        try {
            clientsDirectories.map { clientsDirectory ->
                async { uploadFile(clientsDirectory) }
            }.awaitAll()
        }catch (e: Exception) {
            return@withContext Result.failure()
        }

        return@withContext Result.success()
    }

    private suspend fun uploadFile(directory: File): Boolean {
        val directoryEntries = directory.listFiles() ?: return true
        val directoryFiles = directoryEntries.filter { it.isFile }

        return directoryFiles.all { file ->
            val storageRef = storage.reference.child("${directory.name}/${file.name}")
            try {
                storageRef.putFile(file.toUri()).await()
                file.delete()
                true
            } catch (e: Exception) {
                Log.e("UploadWorker", "Upload error with file ${file.nameWithoutExtension}: ", e)
                false
            }
        }
    }
}
