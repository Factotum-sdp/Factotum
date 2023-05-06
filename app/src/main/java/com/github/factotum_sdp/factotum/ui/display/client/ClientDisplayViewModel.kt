package com.github.factotum_sdp.factotum.ui.display.client

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.factotum_sdp.factotum.ui.display.AppDatabase
import com.github.factotum_sdp.factotum.ui.display.data.client.CachedPhoto
import com.github.factotum_sdp.factotum.ui.display.data.client.CachedPhotoDao
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ClientDisplayViewModel(
    private val _folderName: MutableLiveData<String>,
    context : Context
    ) : ViewModel() {

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault())

    private val _photoReferences = MutableLiveData<List<StorageReference>>()
    val photoReferences: LiveData<List<StorageReference>> = _photoReferences
    val folderName: LiveData<String> = _folderName

    private val database = AppDatabase.getInstance(context)
    private val storage = FirebaseStorage.getInstance()
    private val cachedPhotoDao: CachedPhotoDao = database.cachedPhotoDao()

    init { loadImages() }

    fun refreshImages() { loadImages() }

    fun setFolderName(folderName: String) {
        _folderName.value = folderName
    }

    private fun loadImages() {
        viewModelScope.launch {
            val folderName = _folderName.value ?: return@launch
            val cachedPhotos = withContext(Dispatchers.IO) {
                cachedPhotoDao.getAllByFolderName(folderName)
            }
            val storageReferences = cachedPhotos.map {
                storage.getReference(it.path)
            }.sortedByDescending { getDateFromRef(it) }

            _photoReferences.value = storageReferences
            updateImages(folderName)
        }
    }


    private fun updateImages(folderName: String) {
        viewModelScope.launch {
            val remotePhotos = fetchRemotePhotos(folderName)

            if (remotePhotos != null) {
                withContext(Dispatchers.IO) {
                    val remotePhotoPaths = remotePhotos.map { it.path }
                    val cachedPhotos = cachedPhotoDao.getAllByFolderName(folderName)

                    val photosToDelete = cachedPhotos.filter { it.path !in remotePhotoPaths }
                    cachedPhotoDao.deleteAll(photosToDelete)

                    cachedPhotoDao.insertAll(*remotePhotos.map { photo ->
                        CachedPhoto(photo.path, folderName)
                    }.toTypedArray())
                }

                val updatedCachedPhotos = withContext(Dispatchers.IO) {
                    cachedPhotoDao.getAllByFolderName(folderName)
                }
                val updatedStorageReferences = updatedCachedPhotos.map {
                    storage.getReference(it.path)
                }.sortedByDescending { getDateFromRef(it) }

                _photoReferences.postValue(updatedStorageReferences)
            }
        }
    }


    private suspend fun fetchRemotePhotos(folderName: String): List<StorageReference>? {
        return try {
            val folderReference = storage.reference.child(folderName)
            val photosListResult = folderReference.listAll().await()

            photosListResult.items
        } catch (e: StorageException) {
            null
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun getDateFromRef(ref: StorageReference): Date {
        val dateString = ref.name.substringAfterLast("_").substringBeforeLast(".")
        return try {
            dateFormat.parse(dateString) as Date
        } catch (e: ParseException) {
            Date(0)
        }
    }
}
