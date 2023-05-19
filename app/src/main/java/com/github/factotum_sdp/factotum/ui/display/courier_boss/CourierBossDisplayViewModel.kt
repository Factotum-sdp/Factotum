package com.github.factotum_sdp.factotum.ui.display.courier_boss

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.factotum_sdp.factotum.ui.display.data.AppDatabase
import com.github.factotum_sdp.factotum.ui.display.data.courier_boss.CachedFolder
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

class CourierBossDisplayViewModel(context: Context) : ViewModel() {
    private val _folderReferences = MutableLiveData<List<StorageReference>>()
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    val folderReferences: LiveData<List<StorageReference>>
        get() = _folderReferences

    private val database by lazy { AppDatabase.getInstance(context) }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val cachedFolderDao by lazy { database.cachedFolderDao() }

    init { updateFolders() }

    fun refreshFolders() { updateFolders() }

    private fun updateFolders() {
        _isLoading.value = true
        viewModelScope.launch {
            displayCachedFolders()

            val remoteFolders = fetchRemoteFolders()
            if (remoteFolders != null) {
                updateCachedFolders(remoteFolders)
            }
            _isLoading.value = false
        }
    }

    private suspend fun updateCachedFolders(remoteFolders: List<StorageReference>) {
        withContext(Dispatchers.IO) {
            val cachedFolders = cachedFolderDao.getAll()
            val remoteFolderPaths = remoteFolders.map { it.path }
            val foldersToDelete = cachedFolders.filter { it.path !in remoteFolderPaths }

            cachedFolderDao.deleteAll(foldersToDelete)

            val deferreds = remoteFolders.map { folder ->
                async {
                    val name = folder.name.lowercase(Locale.getDefault())
                    CachedFolder(folder.path, name)
                }
            }

            val newCachedFolders = deferreds.awaitAll().toTypedArray()
            cachedFolderDao.insertAll(*newCachedFolders)
        }

        val updatedCachedFolders = withContext(Dispatchers.IO) {
            cachedFolderDao.getAll()
        }
        val updatedStorageReferences = updatedCachedFolders.map { folder ->
            storage.getReference(folder.path)
        }
        _folderReferences.postValue(updatedStorageReferences)
    }

    private suspend fun displayCachedFolders() {
        val cachedFolders = withContext(Dispatchers.IO) {
            cachedFolderDao.getAll()
        }
        val storageReferences = cachedFolders.map { folder ->
            storage.getReference(folder.path)
        }
        _folderReferences.postValue(storageReferences)
    }

    private suspend fun fetchRemoteFolders(): List<StorageReference>? {
        return try {
            val foldersListResult = storage.reference.listAll().await()
            foldersListResult.prefixes
        } catch (e: StorageException) {
            null
        }
    }
}