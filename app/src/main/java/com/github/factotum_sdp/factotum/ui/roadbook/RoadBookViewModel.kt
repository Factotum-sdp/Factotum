package com.github.factotum_sdp.factotum.ui.roadbook

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.factotum_sdp.factotum.data.DestinationRecord
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat.getTimeInstance
import java.text.SimpleDateFormat.getDateInstance
import java.util.Calendar
import kotlin.random.Random

/**
 * The RoadBook ViewModel
 * holds an observable list of DestinationRecord which can evolve dynamically
 */
class RoadBookViewModel(_dbRef: DatabaseReference) : ViewModel() {
    val recordsList: MutableLiveData<List<DestinationRecord>> =
        MutableLiveData(DestinationRecords.RECORDS)

    private var dbRef: DatabaseReference
    init {
        val date = Calendar.getInstance().time
        dbRef = _dbRef
                .child(getDateInstance().format(date)) // One node to classify daily RoadBook data
                .child(getTimeInstance().format(date).plus(Random.nextInt().toString()))
                // Unique ref path to register all back-ups from this RoadBook
    }

    fun addRecord(destinationRecord: DestinationRecord) {
        val newList = arrayListOf<DestinationRecord>()
        newList.addAll(recordsList.value as Collection<DestinationRecord>)
        newList.add(destinationRecord)
        recordsList.postValue(newList)
    }

    fun deleteLastRecord() {
        val newList = arrayListOf<DestinationRecord>()
        newList.addAll(recordsList.value as Collection<DestinationRecord>)
        if (newList.isNotEmpty()) newList.removeLast()
        recordsList.postValue(newList)
    }

    fun backUp() {
        dbRef.setValue(recordsList.value)
    }

    // Factory needed to assign a value at construction time to the class attribute
    class RoadBookViewModelFactory(private val _dbRef: DatabaseReference)
        : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(DatabaseReference::class.java)
                .newInstance(_dbRef)
        }
    }
}