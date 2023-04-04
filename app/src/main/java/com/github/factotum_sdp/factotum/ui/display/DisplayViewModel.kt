package com.github.factotum_sdp.factotum.ui.display

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator

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
                val photoRefs = listResult.items.sortedWith { ref1, ref2 -> sortByDate(ref1, ref2) }
                val photoList = _photoReferences.value?.toMutableList() ?: mutableListOf()

                if (refresh) {
                    photoList.clear()
                }

                photoList.addAll(photoRefs)
                _photoReferences.postValue(photoList)
            }
        }
    }

    // Sort the list of images by date in descending order
    private fun sortByDate(ref1: StorageReference, ref2: StorageReference): Int {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss", Locale.getDefault())
        val date1 =
            dateFormat.parse(ref1.name.substringAfter("USER_").substringBeforeLast("."))
        val date2 =
            dateFormat.parse(ref2.name.substringAfter("USER_").substringBeforeLast("."))
        return date2.compareTo(date1)
    }

}
