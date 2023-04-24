package com.github.factotum_sdp.factotum.ui.roadbook

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.factotum_sdp.factotum.data.DestinationRecord
import com.github.factotum_sdp.factotum.data.User
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.ui.roadbook.RoadBookFragment.Companion.DELIVERY_LOG_DB_PATH
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.text.SimpleDateFormat.getDateInstance
import java.util.*

/**
 * The RoadBook ViewModel
 * holds an observable list of DestinationRecord which can evolve dynamically
 *
 * @param _dbRef The database root reference to register RoadBook data
 */
class RoadBookViewModel(_dbRef: DatabaseReference) : ViewModel() {

    private val _recordsList: MutableLiveData<DRecordList> =
        MutableLiveData(DRecordList())
    val recordsListState: LiveData<DRecordList> = _recordsList

    private val _loggedInUser: MutableLiveData<User> = MutableLiveData()

    private var dbRef: DatabaseReference
    private val clientOccurences = HashMap<String, Int>()
    private val _dbLogRef: DatabaseReference = FirebaseDatabase.getInstance().reference
        .child(DELIVERY_LOG_DB_PATH)

    init {
        val date = Calendar.getInstance().time
        val dateRef = getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH).format(date)
        dbRef = _dbRef // ref path to register all back-ups from this RoadBook
            .child(dateRef)
        //.child(getTimeInstance().format(date).plus(Random.nextInt().toString()))
        // Let uncommented for testing purpose. Uncomment it for back-up uniqueness in the DB
        // Only for demo purpose :
        addDemoRecords(DestinationRecords.RECORDS)
    }

    /**
     * Send the current recordsList data to the Database referenced at construction time
     */
    fun backUp() {
        dbRef.setValue(_recordsList.value)
    }



    fun timestampNextDestinationRecord(timeStamp: Date) {
        try {
            val record = currentDRecList().getNextDestinationRecord()
            val newRec = DestinationRecord(
                record.destID,
                record.clientID,
                timeStamp,
                record.waitingTime,
                record.rate,
                record.actions,
                record.notes
            )
            val ls = arrayListOf<DestinationRecord>()
            ls.addAll(_recordsList.value as Collection<DestinationRecord>)
            ls[currentDRecList().getNextDestinationIndex()] = newRec

            _recordsList.postValue(currentDRecList().replaceDisplayedList(ls))
        } catch (_: NoSuchElementException) {
        }
    }

    /**
     * Change a DRecord position in the recordsList of this RoadBookViewModel
     *
     * @param from Int
     * @param to Int
     */
    fun moveRecord(from: Int, to: Int) {
        val ls = currentDRecList().toMutableList()
        val fromLocation = ls[from]
        ls.removeAt(from)
        ls.add(to, fromLocation)
        _recordsList.value = currentDRecList().replaceDisplayedList(ls)
    }

    /**
     * Add a new DestinationRecord at the end of the recordsList
     *
     * @param clientID The Customer unique identifier associated to this DestinationRecord
     * @param timeStamp The arrival time
     * @param waitingTime The waiting time in minutes
     * @param rate Rate as internal code notation
     * @param actions The actions to be done on a destination
     * @param notes The additional notes concerning a destination
     */
    fun addRecord(
        clientID: String, timeStamp: Date?, waitingTime: Int,
        rate: Int, actions: List<DestinationRecord.Action>, notes: String
    ) {
        val newList = arrayListOf<DestinationRecord>()
        newList.addAll(_recordsList.value as Collection<DestinationRecord>)
        val destID = computeDestID(clientID)
        val rec = DestinationRecord(destID, clientID, timeStamp, waitingTime, rate, actions, notes)
        newList.add(rec)
        _recordsList.value = currentDRecList().replaceDisplayedList(newList)
    }

    /**
     * Delete the DestinationRecord at index "pos" in the recordsList
     *
     * @param pos: Int Index of the target record to delete
     */
    fun deleteRecordAt(pos: Int) {
        val newList = arrayListOf<DestinationRecord>()
        newList.addAll(currentDRecList() as Collection<DestinationRecord>)
        newList.removeAt(pos)
        _recordsList.value = currentDRecList().replaceDisplayedList(newList)
    }

    /**
     * Edit the DestinationRecord at index "pos" in the recordsList attribute
     * If the new DestRecord computed is the same at the old one, no value is set and false is returned
     *
     * @param pos: Int position Index at which the current DestRecord will be override
     * @param clientID The Customer unique identifier associated to this DestinationRecord
     * @param timeStamp The arrival time
     * @param waitingTime The waiting time in minutes
     * @param rate Rate as internal code notation
     * @param actions The actions to be done on a destination
     * @param notes The additional notes concerning a destination
     * @return true if according the args, there is a change and the _recordList is updated, false otherwise
     */
    fun editRecordAt(
        pos: Int, clientID: String, timeStamp: Date?, waitingTime: Int,
        rate: Int, actions: List<DestinationRecord.Action>, notes: String
    ): Boolean {
        val currentRec = currentDRecList()[pos]
        var destID = currentRec.destID
        if (currentRec.clientID != clientID) {
            destID = computeDestID(clientID)
        }
        val newRec =
            DestinationRecord(destID, clientID, timeStamp, waitingTime, rate, actions, notes)
        val ls = arrayListOf<DestinationRecord>()
        ls.addAll(_recordsList.value as Collection<DestinationRecord>)
        ls[pos] = newRec
        if (currentRec != newRec) {
            _recordsList.value = currentDRecList().replaceDisplayedList(ls)
            return true
        }
        // Prefer to be explicit with a boolean value, for the front-end to know it has to refresh, or act accordingly.
        // ! Check the case where the destID is the same but
        return false
    }

    /**
     * Archive the DestinationRecord at index "pos" in the recordsList
     *
     * @param pos: Int Index of the target record to archive
     */
    fun archiveRecordAt(pos: Int) {
        _recordsList.value = currentDRecList().archiveRecord(pos)
    }

    /**
     * Unarchive the DestinationRecord at index "pos" in the recordsList
     *
     * @param pos: Int Index of the target record to unarchive
     */
    fun unarchiveRecordAt(pos: Int) {
        _recordsList.value = currentDRecList().unarchiveRecord(pos)
    }

    /**
     * Check if the DestinationRecord at index "pos" is archived
     *
     * @param pos: Int Index of the target record
     * @return true if the record at "pos" is archived, false otherwise
     */
    fun isRecordAtArchived(pos: Int): Boolean {
        return currentDRecList().isArchived(pos)
    }

    /**
     * Check if the DestinationRecord at index "pos" is timestamped
     *
     * @param pos: Int Index of the target record
     * @return true if the record at "pos" is timestamped, false otherwise
     */
    fun isRecordAtTimeStamped(pos: Int): Boolean {
        currentDRecList()[pos].timeStamp?.let {
            return true
        }
        return false // if timeStamp is null
    }

    /**
     * Change the current recordsList state by "adding" the archived records
     * (i.e all the records will be in the current _recordsList)
     */
    fun showArchivedRecords() {
        _recordsList.value = currentDRecList().withArchived()
    }

    /**
     * Change the current recordsList state by "removing" the archived records
     */
    fun hideArchivedRecords() {
        _recordsList.value = currentDRecList().withoutArchived()
    }

    /**
     * Sets the logged user
     */
    fun setLoggedUser(user: User) {
        _loggedInUser.value = user
    }

    /**
     * logs the delivery of the current day
     */
    fun logDeliveries() {
        _recordsList.value?.forEach { destRec ->
            if (destRec.timeStamp != null) {
                _dbLogRef
                    .child(_loggedInUser.value?.name.toString())
                    .child(dateFormatted())
                    .child(destRec.hashCode().toString()).setValue(destRec)
            }
        }
    }

    private fun dateFormatted(): String {
        return SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(Date())
    }


    //Needed to update the destIDOccurrences cache
    private fun addDemoRecords(ls: List<DestinationRecord>) {
        val newList = arrayListOf<DestinationRecord>()
        ls.forEach {
            val destID = computeDestID(it.clientID)
            newList.add(
                DestinationRecord(
                    destID,
                    it.clientID,
                    it.timeStamp,
                    it.waitingTime,
                    it.rate,
                    it.actions,
                    it.notes
                )
            )
        }
        _recordsList.value = currentDRecList().replaceDisplayedList(newList)
    }

    private fun currentDRecList(): DRecordList {
        return _recordsList.value!!
    }

    private fun computeDestID(clientID: String): String {
        val occ = clientOccurences.compute(clientID) { _, oldOcc ->
            var occ = oldOcc ?: 0
            ++occ
        }
        return "$clientID#$occ"
    }

    // Factory needed to assign a value at construction time to the class attribute
    class RoadBookViewModelFactory(private val _dbRef: DatabaseReference) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(DatabaseReference::class.java)
                .newInstance(_dbRef)
        }
    }
}