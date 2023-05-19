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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
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
        _isLoading.value = true
        viewModelScope.launch {
            val folderName = _folderName.value ?: return@launch

            displayCachedPhotos(folderName)

            val remotePhotos = fetchRemotePhotos(folderName)
            if (remotePhotos != null) {
                updateCachedPhotos(folderName, remotePhotos)
            }
            _isLoading.value = false
        }
    }

    private suspend fun updateCachedPhotos(folderName: String, remotePhotos: List<StorageReference>) {
        withContext(Dispatchers.IO) {
            val remotePhotoPaths = remotePhotos.map { it.path }
            val cachedPhotos = cachedPhotoDao.getAllByFolderName(folderName)

            val photosToDelete = cachedPhotos.filter { it.path !in remotePhotoPaths }
            cachedPhotoDao.deleteAll(photosToDelete)

            withContext(Dispatchers.Default) {
                val deferreds = remotePhotos.map { photo ->
                    async {
                        val url = photo.downloadUrl.await().toString()
                        val dateSortKey = getDateFromRef(photo).time
                        CachedPhoto(photo.path, folderName, url, dateSortKey)
                    }
                }

                val newCachedPhotos = deferreds.awaitAll().toTypedArray()
                cachedPhotoDao.insertAll(*newCachedPhotos)
            }
        }

        displayCachedPhotos(folderName)
    }

    private suspend fun displayCachedPhotos(folderName: String) {
        val cachedPhotos = withContext(Dispatchers.IO) {
            cachedPhotoDao.getAllByFolderNameSortedByDate(folderName)
        }
        val storageReferences = cachedPhotos.map {
            storage.getReference(it.path)
        }

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
        viewModelScope.launch(Dispatchers.IO) {
            val folderName = _folderName.value ?: return@launch
            val cal = Calendar.getInstance().apply { time = date }
            val startOfDay = cal.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
            val endOfDay = cal.apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999) }.timeInMillis
            val cachedPhotos = cachedPhotoDao.getAllByFolderNameAndDay(folderName, startOfDay, endOfDay)
            val storageReferences = cachedPhotos.map {
                storage.getReference(it.path)
            }

            withContext(Dispatchers.Main) {
                _photoReferences.postValue(storageReferences)
            }
        }
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
