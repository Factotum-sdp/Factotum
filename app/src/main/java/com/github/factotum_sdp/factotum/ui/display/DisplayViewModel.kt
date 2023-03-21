package com.github.factotum_sdp.factotum.ui.display
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DisplayViewModel : ViewModel() {
    private val _photoUrls = MutableLiveData<List<String>>()
    val photoUrls: LiveData<List<String>>
        get() = _photoUrls

    private val storage = FirebaseStorage.getInstance()

    init { fetchPhotoUrls() }

    private fun fetchPhotoUrls() {
        CoroutineScope(Dispatchers.IO).launch {
            val photoList = mutableListOf<String>()
            val storageRef = storage.reference

            storageRef.listAll().addOnSuccessListener { listResult ->
                val photoRefs = listResult.items.sortedBy { it.name }

                photoRefs.forEach { photoRef ->
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        photoList.add(uri.toString())

                        if (photoList.size == photoRefs.size) {
                            // All items have been processed, update the LiveData
                            _photoUrls.postValue(photoList)
                        }
                    }
                }
            }
        }
    }


    fun refreshImages() {
        CoroutineScope(Dispatchers.IO).launch {
            val newPhotoList = mutableListOf<String>()
            val storageRef = storage.reference

            storageRef.listAll().addOnSuccessListener { listResult ->
                val photoRefs = listResult.items.sortedBy { it.name }
                val itemsCount = photoRefs.size
                var itemsProcessed = 0

                photoRefs.forEach { photoRef ->
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        newPhotoList.add(uri.toString())
                        itemsProcessed++

                        if (itemsProcessed == itemsCount) {
                            // All items have been processed, update the LiveData
                            _photoUrls.postValue(newPhotoList)
                        }
                    }
                }
            }
        }
    }

}
