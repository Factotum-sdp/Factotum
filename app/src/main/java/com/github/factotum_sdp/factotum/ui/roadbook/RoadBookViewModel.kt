package com.github.factotum_sdp.factotum.ui.roadbook

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.data.DestinationRecord
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords

/**
 * The RoadBook ViewModel
 * holds an observable list of DestinationRecord which can evolve dynamically
 */
class RoadBookViewModel() : ViewModel() {
    val recordsList: MutableLiveData<List<DestinationRecord>> =
        MutableLiveData(DestinationRecords.RECORDS)

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
}