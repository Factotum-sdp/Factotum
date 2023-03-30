package com.github.factotum_sdp.factotum.ui.roadbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.factotum_sdp.factotum.data.DestinationRecord
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat.getDateInstance
import java.util.*
import kotlin.collections.ArrayList

/**
 * The RoadBook ViewModel
 * holds an observable list of DestinationRecord which can evolve dynamically
 *
 * @param _dbRef The database root reference to register RoadBook data
 */
class RoadBookViewModel(_dbRef: DatabaseReference) : ViewModel() {

    private val _recordsList: MutableLiveData<List<DestinationRecord>> =
        MutableLiveData(DestinationRecords.RECORDS)

    val recordsListState: LiveData<List<DestinationRecord>> = _recordsList
    private val swappedRecords: ArrayList<DestinationRecord> = arrayListOf()
    private var dbRef: DatabaseReference
    init {
        val date = Calendar.getInstance().time
        dbRef = _dbRef // ref path to register all back-ups from this RoadBook
                .child(getDateInstance().format(date))
                //.child(getTimeInstance().format(date).plus(Random.nextInt().toString()))
                // Let uncommented for testing purpose. Uncomment it for back-up uniqueness in the DB
    }

    /**
     * Add a new DestinationRecord at the end of the recordsList
     * @param destinationRecord DestinationRecord to be added
     */
    fun addRecord(destinationRecord: DestinationRecord) {
        val newList = arrayListOf<DestinationRecord>()
        newList.addAll(_recordsList.value as Collection<DestinationRecord>)
        newList.add(destinationRecord)
        _recordsList.postValue(newList)
    }

    /**
     * Delete the last DestinationRecord of the recordsList
     */
    fun deleteLastRecord() {
        val newList = arrayListOf<DestinationRecord>()
        newList.addAll(_recordsList.value as Collection<DestinationRecord>)
        if (newList.isNotEmpty()) newList.removeLast()
        _recordsList.postValue(newList)
    }

    /**
     * Send the current recordsList data to the Database referenced at construction time
     */
    fun backUp() {
        dbRef.setValue(_recordsList.value)
    }

    /**
     * Swap between DestinationRecords at position : from and at position : to
     * It is a one way swap, hence the from index must be lower or equal to the to index.
     *
     * @param from Int
     * @param to Int
     */
    fun swapRecords(from: Int, to: Int) {
        assert(from <= to)
        if(swappedRecords.isEmpty())
            swappedRecords.addAll(_recordsList.value as Collection<DestinationRecord>)
        Collections.swap(swappedRecords, from, to)
    }

    /**
     * Update the Observable LiveData of this RoadBookViewModel
     * To be called after a series of swapRecords() that comes to an end,
     * in order to show the result to some possible observers.
     */
    fun pushSwapsResult() {
        if(swappedRecords.isNotEmpty()) {
            var ls = listOf<DestinationRecord>()
            ls = ls.plus(swappedRecords)
            _recordsList.postValue(ls)
            swappedRecords.clear()
        }
    }

    /**
     * Edit the DestinationRecort at indec pos in the recordsList attribute
     * @param pos: Int position Index at which the current DestRecord will be override
     * @param newRec: DestinationRecord The record containing the new data
     */
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