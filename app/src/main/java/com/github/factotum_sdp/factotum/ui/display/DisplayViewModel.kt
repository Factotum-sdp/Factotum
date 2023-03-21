package com.github.factotum_sdp.factotum.ui.display

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                val photoRefs = listResult.items.sortedBy { it.name }
                photoRefs.forEach { photoRef ->
                    photoList.add(photoRef)

                    if (photoList.size == photoRefs.size) {
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
            val newPhotoList = mutableListOf<StorageReference>()
            val storageRef = storage.reference

            // List all items in the storage and process the results
            storageRef.listAll().addOnSuccessListener { listResult ->
                val photoRefs = listResult.items.sortedBy { it.name }
                val itemsCount = photoRefs.size
                var itemsProcessed = 0

                photoRefs.forEach { photoRef ->
                    newPhotoList.add(photoRef)
                    itemsProcessed++

                    if (itemsProcessed == itemsCount) {
                        // All items have been processed, update the LiveData
                        _photoReferences.postValue(newPhotoList)
                    }
                }
            }
        }
    }
}
