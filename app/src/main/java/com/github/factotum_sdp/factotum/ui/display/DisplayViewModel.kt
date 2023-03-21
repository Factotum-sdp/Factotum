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
        updateImages(false)
    }

    // Refresh the list of images from Firebase Storage
    fun refreshImages() {
        updateImages(true)
    }

    // Update the list of images from Firebase Storage
    private fun updateImages(refresh: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val storageRef = storage.reference
            storageRef.listAll().addOnSuccessListener { listResult ->
                val photoRefs = listResult.items.sorted()
                val photoList = if (refresh) _photoReferences.value?.toMutableList() ?: mutableListOf() else mutableListOf()

                if (refresh) {
                    photoList.clear()
                }

                for (photoRef in photoRefs) {
                    photoList.add(photoRef)
                }

                _photoReferences.postValue(photoList)
            }
        }
    }
}
