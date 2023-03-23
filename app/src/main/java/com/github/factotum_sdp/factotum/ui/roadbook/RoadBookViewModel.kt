package com.github.factotum_sdp.factotum.ui.roadbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.data.DestinationRecord
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat.getDateInstance
import java.util.*
import kotlin.collections.ArrayList

/**
 * The RoadBook ViewModel
 * holds an observable list of DestinationRecord which can evolve dynamically
 */
class RoadBookViewModel(_dbRef: DatabaseReference) : ViewModel() {
    private val _recordsList: MutableLiveData<List<DestinationRecord>> =
        MutableLiveData(DestinationRecords.RECORDS)

    val recordsListState: LiveData<List<DestinationRecord>> = _recordsList

    private val recordsOnDragAndDrop: ArrayList<DestinationRecord> = arrayListOf()

    private var dbRef: DatabaseReference
    init {
        val date = Calendar.getInstance().time
        dbRef = _dbRef // ref path to register all back-ups from this RoadBook
                .child(getDateInstance().format(date))
                //.child(getTimeInstance().format(date).plus(Random.nextInt().toString()))
                // Let uncommented for testing purpose. Uncomment it for back-up uniqueness in the DB
    }

    fun addRecord(destinationRecord: DestinationRecord) {
        val newList = arrayListOf<DestinationRecord>()
        newList.addAll(_recordsList.value as Collection<DestinationRecord>)
        newList.add(destinationRecord)
        _recordsList.postValue(newList)
    }

    fun deleteLastRecord() {
        val newList = arrayListOf<DestinationRecord>()
        newList.addAll(_recordsList.value as Collection<DestinationRecord>)
        if (newList.isNotEmpty()) newList.removeLast()
        _recordsList.postValue(newList)
    }

    fun backUp() {
        dbRef.setValue(_recordsList.value)
    }

    fun swapRecords(from: Int, to: Int) {
        if(recordsOnDragAndDrop.isEmpty())
            recordsOnDragAndDrop.addAll(_recordsList.value as Collection<DestinationRecord>)
        Collections.swap(recordsOnDragAndDrop, from, to)
    }

    fun pushDragAndDropResult() {
        if(recordsOnDragAndDrop.isNotEmpty()) {
            var ls = listOf<DestinationRecord>()
            ls = ls.plus(recordsOnDragAndDrop)
            _recordsList.postValue(ls)
            recordsOnDragAndDrop.clear()
        }
    }

    fun editRecord(pos: Int, newRec: DestinationRecord) {
        val ls = arrayListOf<DestinationRecord>()
        ls.addAll(_recordsList.value as Collection<DestinationRecord>)
        ls[pos] = newRec
        _recordsList.postValue(ls)
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