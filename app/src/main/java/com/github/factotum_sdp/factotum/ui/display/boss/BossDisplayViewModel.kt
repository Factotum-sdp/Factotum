package com.github.factotum_sdp.factotum.ui.display.boss

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.factotum_sdp.factotum.ui.display.AppDatabase
import com.github.factotum_sdp.factotum.ui.display.data.boss.CachedFolder
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class BossDisplayViewModel(context: Context) : ViewModel() {
    private val _folderReferences = MutableLiveData<List<StorageReference>>()
    val folderReferences: LiveData<List<StorageReference>>
        get() = _folderReferences

    private val database = AppDatabase.getInstance(context)
    private val storage = Firebase.storage
    private val cachedFolderDao = database.cachedFolderDao()

    init { loadFolders() }

    fun refreshFolders() { loadFolders() }

    private fun loadFolders() {
        viewModelScope.launch {
            val cachedFolders = withContext(Dispatchers.IO) {
                cachedFolderDao.getAll()
            }
            val storageReferences = cachedFolders.map { folder ->
                storage.getReference(folder.path)
            }
            _folderReferences.value = storageReferences
            updateLocalDatabaseFromFirebase()
        }
    }

    private suspend fun updateLocalDatabaseFromFirebase() {
        val remoteFolders = fetchRemoteFolders()

        if (remoteFolders != null) {
            withContext(Dispatchers.IO) {
                cachedFolderDao.insertAll(*remoteFolders.map { folder ->
                    CachedFolder(folder.path)
                }.toTypedArray())
            }

            val updatedCachedFolders = withContext(Dispatchers.IO) {
                cachedFolderDao.getAll()
            }
            val updatedStorageReferences = updatedCachedFolders.map { folder ->
                storage.getReference(folder.path)
            }
            _folderReferences.postValue(updatedStorageReferences)
        }
    }

    private suspend fun fetchRemoteFolders(): List<StorageReference>? {
        return try {
            val foldersListResult = storage.reference.listAll().await()

            foldersListResult.prefixes
        } catch (e: StorageException) {
            null
        } catch (e: Exception) {
            emptyList()
        }
    }
}