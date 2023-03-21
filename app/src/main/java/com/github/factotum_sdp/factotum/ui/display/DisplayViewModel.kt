package com.github.factotum_sdp.factotum.ui.display

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ViewModel for managing the display of images from Firebase Storage
class DisplayViewModel : ViewModel() {
    // LiveData to store the list of storage references for the images
    private val _photoReferences = MutableLiveData<List<StorageReference>>()
    val photoReferences: LiveData<List<StorageReference>>
        get() = _photoReferences

    // Reference to the Firebase Storage instance
    private val storage = FirebaseStorage.getInstance()

    init {
        // Fetch the photo references when the ViewModel is initialized
        fetchPhotoReferences()
    }

    // Fetch photo references from Firebase Storage
    private fun fetchPhotoReferences() {
        CoroutineScope(Dispatchers.IO).launch {
            val photoList = mutableListOf<StorageReference>()
            val storageRef = storage.reference

            // List all items in the storage and process the results
            storageRef.listAll().addOnSuccessListener { listResult ->
                val photoRefs = listResult.items.sorted()
                for (i in photoRefs.indices) {
                    photoList.add(photoRefs[i])

                    if (i == photoRefs.lastIndex) {
                        // All items have been processed, update the LiveData
                        _photoReferences.postValue(photoList)
                    }
                }
            }
        }
    }

    // Refresh the list of images from Firebase Storage
    fun refreshImages() {
        CoroutineScope(Dispatchers.IO).launch {
            val photoList = _photoReferences.value?.toMutableList() ?: mutableListOf()
            val storageRef = storage.reference

            // Clear the existing list and list all items in the storage
            photoList.clear()
            storageRef.listAll().addOnSuccessListener { listResult ->
                val photoRefs = listResult.items.sorted()
                for (i in photoRefs.indices) {
                    photoList.add(photoRefs[i])

                    if (i == photoRefs.lastIndex) {
                        // All items have been processed, update the LiveData
                        _photoReferences.postValue(photoList)
                    }
                }
            }
        }
    }
}
