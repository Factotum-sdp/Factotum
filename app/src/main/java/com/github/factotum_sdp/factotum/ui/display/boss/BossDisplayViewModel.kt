package com.github.factotum_sdp.factotum.ui.display.boss

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class BossDisplayViewModel : ViewModel() {
    private val _folderReferences = MutableLiveData<List<StorageReference>>()
    val folderReferences: LiveData<List<StorageReference>>
        get() = _folderReferences

    private var cachedFolderReferences = listOf<StorageReference>()

    init { loadFolders() }

    fun refreshFolders() {
        loadFolders()
    }

    private fun loadFolders() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val storageRef = FirebaseStorage.getInstance().reference
                val listEntries = storageRef.listAll().await()
                val folderList = listEntries.prefixes.sortedBy { it.name }

                if (folderList != cachedFolderReferences) {
                    cachedFolderReferences = folderList
                    _folderReferences.postValue(folderList)
                }

            } catch (e: Exception) {
                Log.e("BossDisplayViewModel", "Error loading folders: ${e.message}")
            }
        }
    }
}
