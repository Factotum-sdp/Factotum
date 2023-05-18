package com.github.factotum_sdp.factotum.ui.display.client

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.factotum_sdp.factotum.ui.display.data.AppDatabase
import com.github.factotum_sdp.factotum.ui.display.data.client.CachedPhoto
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ClientDisplayViewModel(
    private val _folderName: MutableLiveData<String>,
    context: Context
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault())

    private val _photoReferences = MutableLiveData<List<StorageReference>>()
    val photoReferences: LiveData<List<StorageReference>> = _photoReferences
    val folderName: LiveData<String> = _folderName

    private val database by lazy { AppDatabase.getInstance(context) }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val cachedPhotoDao by lazy { database.cachedPhotoDao() }

    init { updateImages() }

    fun refreshImages() { updateImages() }

    fun setFolderName(folderName: String) {
        _folderName.value = folderName
    }

    fun getUrlForPhoto(photoPath: String): LiveData<String?> {
        val photoLiveData = MutableLiveData<String?>()

        viewModelScope.launch(Dispatchers.IO) {
            val cachedPhoto = cachedPhotoDao.getPhotoByPath(photoPath)
            photoLiveData.postValue(cachedPhoto?.url)
        }

        return photoLiveData
    }

    private fun updateImages() {
        viewModelScope.launch {
            val folderName = _folderName.value ?: return@launch

            displayCachedPhotos(folderName)

            val remotePhotos = fetchRemotePhotos(folderName)
            if (remotePhotos != null) {
                updateCachedPhotos(folderName, remotePhotos)
            }

        }
    }

    private suspend fun updateCachedPhotos(folderName: String, remotePhotos: List<StorageReference>) {
        withContext(Dispatchers.IO) {
            val remotePhotoPaths = remotePhotos.map { it.path }
            val cachedPhotos = cachedPhotoDao.getAllByFolderName(folderName)

            val photosToDelete = cachedPhotos.filter { it.path !in remotePhotoPaths }
            cachedPhotoDao.deleteAll(photosToDelete)

            cachedPhotoDao.insertAll(*remotePhotos.map { photo ->
                val url = photo.downloadUrl.await().toString()
                CachedPhoto(photo.path, folderName, url)
            }.toTypedArray())
        }

        val updatedCachedPhotos = withContext(Dispatchers.IO) {
            cachedPhotoDao.getAllByFolderName(folderName)
        }
        val updatedStorageReferences = updatedCachedPhotos.map {
            storage.getReference(it.path)
        }.sortedWith { date1, date2 -> getDateFromRef(date2).compareTo(getDateFromRef(date1)) }

        _photoReferences.postValue(updatedStorageReferences)
    }

    private suspend fun displayCachedPhotos(folderName: String) {
        val cachedPhotos = withContext(Dispatchers.IO) {
            cachedPhotoDao.getAllByFolderName(folderName)
        }
        val storageReferences = cachedPhotos.map {
            storage.getReference(it.path)
        }.sortedWith { date1, date2 -> getDateFromRef(date2).compareTo(getDateFromRef(date1)) }

        _photoReferences.postValue(storageReferences)
    }

    private suspend fun fetchRemotePhotos(folderName: String): List<StorageReference>? {
        return try {
            val folderReference = storage.reference.child(folderName)
            val photosListResult = folderReference.listAll().await()
            photosListResult.items
        } catch (e: Exception) {
            null
        }
    }

    fun filterImagesByDate(date: Date) {
        viewModelScope.launch {
            val folderName = _folderName.value ?: return@launch
            val cachedPhotos = withContext(Dispatchers.IO) {
                cachedPhotoDao.getAllByFolderName(folderName)
            }
            val storageReferences = cachedPhotos.map {
                storage.getReference(it.path)
            }.filter { isSameDay(getDateFromRef(it), date) }
                .sortedWith {  date1, date2 -> getDateFromRef(date2).compareTo(getDateFromRef(date1)) }

            _photoReferences.postValue(storageReferences)
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        cal1.time = date1

        val cal2 = Calendar.getInstance()
        cal2.time = date2

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun getDateFromRef(ref: StorageReference): Date {
        val dateString = ref.name.substringAfter("_").substringBeforeLast(".")
        return try {
            dateFormat.parse(dateString) as Date
        } catch (e: ParseException) {
            Date(0)
        }
    }
}