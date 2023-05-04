package com.github.factotum_sdp.factotum.ui.display.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ClientDisplayViewModel(private val _folderName: MutableLiveData<String>) : ViewModel() {
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault())
    private val _photoReferences = MutableLiveData<List<StorageReference>>()
    private val storage = FirebaseStorage.getInstance()
    val photoReferences: LiveData<List<StorageReference>> = _photoReferences

    private val folderNameObserver = { folderName: String -> updateImages(folderName) }

    private var cachedPhotoReferences = listOf<StorageReference>()

    init { _folderName.observeForever(folderNameObserver) }

    override fun onCleared() {
        super.onCleared()
        _folderName.removeObserver(folderNameObserver)
    }

    fun refreshImages(folderName: String) {
        updateImages(folderName)
    }

    private fun updateImages(folderName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val storageRef = storage.reference.child(folderName)
            storageRef.listAll().addOnSuccessListener { listResult ->
                val photoRefs = listResult.items.sortedByDescending { getDateFromRef(it) }
                if (photoRefs != cachedPhotoReferences) {
                    cachedPhotoReferences = photoRefs
                    _photoReferences.postValue(photoRefs)
                }
            }
        }
    }

    private fun getDateFromRef(ref: StorageReference): Date {
        val dateString = ref.name.substringAfterLast("_").substringBeforeLast(".")
        return try {
            dateFormat.parse(dateString) as Date
        } catch (e: ParseException) {
            Date(0)
        }
    }
}
