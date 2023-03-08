package com.github.factotum_sdp.factotum.ui.roadbook

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.data.DestinationRecord
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords

class RoadBookViewModel() : ViewModel() {
    val recordsList: MutableLiveData<List<DestinationRecord>> =
        MutableLiveData(DestinationRecords.RECORDS)

    //need to handle null case
    fun addRecord(destinationRecord: DestinationRecord): Unit {
        val newList = arrayListOf<DestinationRecord>()
        newList.addAll(recordsList.value as Collection<DestinationRecord>)
        newList.add(destinationRecord)
        recordsList.postValue(newList)
    }
}