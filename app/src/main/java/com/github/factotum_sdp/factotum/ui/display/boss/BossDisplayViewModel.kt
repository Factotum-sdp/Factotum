package com.github.factotum_sdp.factotum.ui.display.boss

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BossDisplayViewModel : ViewModel() {

private val _folderReferences = MutableLiveData<List<StorageReference>>()
    val folderReferences: LiveData<List<StorageReference>>
        get() = _folderReferences

    init {
        loadFolders()
    }

    fun refreshFolders() {
        loadFolders()
    }

    private fun loadFolders() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val storageRef = FirebaseStorage.getInstance().reference
                val listEntries = storageRef.listAll().await()
                val folderList = listEntries.prefixes.sortedBy { it.name }
                _folderReferences.postValue(folderList)

            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}