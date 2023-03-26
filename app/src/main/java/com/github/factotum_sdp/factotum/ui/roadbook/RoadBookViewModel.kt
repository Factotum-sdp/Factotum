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
import kotlin.collections.HashMap

/**
 * The RoadBook ViewModel
 * holds an observable list of DestinationRecord which can evolve dynamically
 *
 * @param _dbRef The database root reference to register RoadBook data
 */
class RoadBookViewModel(_dbRef: DatabaseReference) : ViewModel() {

    private val _recordsList: MutableLiveData<List<DestinationRecord>> =
        MutableLiveData(emptyList())
    val recordsListState: LiveData<List<DestinationRecord>> = _recordsList

    private var dbRef: DatabaseReference
    private val swapedRecords: ArrayList<DestinationRecord> = arrayListOf()
    private val clientOccurences = HashMap<String, Int>()

    init {
        val date = Calendar.getInstance().time
        dbRef = _dbRef // ref path to register all back-ups from this RoadBook
                .child(getDateInstance().format(date))
                //.child(getTimeInstance().format(date).plus(Random.nextInt().toString()))
                // Let uncommented for testing purpose. Uncomment it for back-up uniqueness in the DB
        // Only for demo purpose :
        addDemoRecords(DestinationRecords.RECORDS)
    }

    /**
     * Add a new DestinationRecord at the end of the recordsList
     * @param clientID The Customer unique identifier associated to this DestinationRecord
     * @param timeStamp The arrival time
     * @param waitingTime The waiting time in minutes
     * @param rate Rate as internal code notation
     * @param actions The actions to be done on a destination
     */
    fun addRecord(clientID: String, timeStamp: Date?, waitingTime: Int,
                  rate: Int, actions: List<DestinationRecord.Action>) {
        val newList = arrayListOf<DestinationRecord>()
        newList.addAll(_recordsList.value as Collection<DestinationRecord>)
        val destID = computeDestID(clientID)
        val rec = DestinationRecord(destID, clientID, timeStamp, waitingTime, rate, actions)
        newList.add(rec)
        _recordsList.postValue(newList)
    }

    //Needed to update the destIDOccurences cache
    private fun addDemoRecords(ls: List<DestinationRecord>) {
        val newList = arrayListOf<DestinationRecord>()
        ls.forEach {
            val destID = computeDestID(it.clientID)
            newList.add(DestinationRecord(destID, it.clientID, it.timeStamp, it.waitingTime, it.rate, it.actions))
        }
        _recordsList.postValue(newList)
    }

    private fun computeDestID(clientID: String): String {
        val occ = clientOccurences.compute(clientID) { _, oldOcc ->
            var occ = oldOcc ?: 0
            ++occ
        }
        return "$clientID#$occ"
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
     * Delete the DestinationRecord at index "pos" in the recordsList
     * @param pos: Int Index of the target record to delete
     */
    fun deleteRecordAt(pos: Int) {
        val newList = arrayListOf<DestinationRecord>()
        newList.addAll(_recordsList.value as Collection<DestinationRecord>)
        newList.removeAt(pos)
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
        if(swapedRecords.isEmpty())
            swapedRecords.addAll(_recordsList.value as Collection<DestinationRecord>)
        Collections.swap(swapedRecords, from, to)
    }

    /**
     * Update the Observable LiveData of this RoadBookViewModel
     * To be called after a series of swapRecords() that comes to an end,
     * in order to show the result to some possible observers.
     */
    fun pushSwapsResult() {
        if(swapedRecords.isNotEmpty()) {
            var ls = listOf<DestinationRecord>()
            ls = ls.plus(swapedRecords)
            _recordsList.postValue(ls)
            swapedRecords.clear()
        }
    }

    /**
     * Edit the DestinationRecord at index pos in the recordsList attribute
     * If the new DestRecord computed is the same at the old one no value is posted and false is returned
     * @param pos: Int position Index at which the current DestRecord will be override
     * @param clientID The Customer unique identifier associated to this DestinationRecord
     * @param timeStamp The arrival time
     * @param waitingTime The waiting time in minutes
     * @param rate Rate as internal code notation
     * @param actions The actions to be done on a destination
     * @return true if according the args, there is a change and the _recordList is updated, false otherwise
     */
    fun editRecord(pos: Int, clientID: String, timeStamp: Date?, waitingTime: Int,
                   rate: Int, actions: List<DestinationRecord.Action>): Boolean {
        val currentRec = _recordsList.value!![pos]
        var destID = currentRec.destID
        if(currentRec.clientID != clientID) {
            destID = computeDestID(clientID)
        }
        val newRec = DestinationRecord(destID, clientID, timeStamp, waitingTime, rate, actions)
        val ls = arrayListOf<DestinationRecord>()
        ls.addAll(_recordsList.value as Collection<DestinationRecord>)
        ls[pos] = newRec
        if(currentRec != newRec) {
            _recordsList.postValue(ls)
            return true
        }
        // Prefer to be explicit with a boolean value, for the front-end to know it has to refresh, or act accordingly.
        // ! Check the case where the destID is the same but
        return false
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