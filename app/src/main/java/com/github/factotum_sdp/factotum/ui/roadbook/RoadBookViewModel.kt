package com.github.factotum_sdp.factotum.ui.roadbook

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import com.github.factotum_sdp.factotum.models.DestinationRecord
import com.github.factotum_sdp.factotum.models.RoadBookPreferences
import com.github.factotum_sdp.factotum.models.Shift
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.repositories.RoadBookPreferencesRepository
import com.github.factotum_sdp.factotum.repositories.RoadBookRepository
import com.github.factotum_sdp.factotum.repositories.ShiftRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * The RoadBook ViewModel
 * holds an observable list of DestinationRecord which can evolve dynamically
 *
 * @param roadBookRepository: RoadBookRepository The data source for the RoadBook
 * @param shiftRepository: ShiftRepository The data source for registering the shift feed-back
 */
class RoadBookViewModel(private val roadBookRepository: RoadBookRepository,
                        private val shiftRepository: ShiftRepository) : ViewModel() {

    private val _recordsList: MutableLiveData<DRecordList> = MutableLiveData(DRecordList())
    val recordsListState: LiveData<DRecordList> = _recordsList
    val timestampedRecords = recordsListState.map { recordsList ->
        recordsList.timestampedSet().associate { it.destID to it.timeStamp!! }
    }.distinctUntilChanged()

    private val clientOccurrences = HashMap<String, Int>()
    private lateinit var preferencesRepository: RoadBookPreferencesRepository

    init {
        addDemoRecords(DestinationRecords.RECORDS)
    }

    /**
     * Set the preferencesRepository field of this ViewModel
     *
     * To be called before any call to a function dealing with the RoadBookPreferences i.e :
     * initialPreferences()
     * updateRoadBookPreferences()
     *
     * @param preferences: RoadBookPreferencesRepository
     */
    fun setPreferencesRepository(preferences: RoadBookPreferencesRepository) {
        preferencesRepository = preferences
    }

    /**
     * Fetch the initial observable RoadBookPreferences state
     *
     * @return LiveData<RoadBookPreferences> The initial preferences state
     */
    fun initialPreferences(): LiveData<RoadBookPreferences> {
        return liveData {
            emit(preferencesRepository.fetchInitialPreferences())
        }
    }

    /**
     * Update the DataStore RoadBookPreferences state
     *
     * @param preferences: RoadBookPreferences
     */
    fun updateRoadBookPreferences(preferences: RoadBookPreferences) {
        if (::preferencesRepository.isInitialized) {
            viewModelScope.launch {
                preferences.apply {
                    preferencesRepository.updateReordering(enableReordering)
                    preferencesRepository.updateDeletionOrArchiving(enableArchivingAndDeletion)
                    preferencesRepository.updateEdition(enableEdition)
                    preferencesRepository.updateDetailsAccess(enableDetailsAccess)
                    preferencesRepository.updateShowArchived(showArchived)
                }
            }
        }
    }


    /**
     * Log the current deliveries to the database if
     *
     */
    fun logShift(shift: Shift){
        val createdShift = Shift(Date(), shift.user, currentDRecList())
        shiftRepository.logShift(createdShift)
    }

    /**
     * Send the current recordsList data to the Database referenced at construction time
     */
    fun backUp() {
        roadBookRepository.setBackUp(currentDRecList())
    }

    /**
     * Replace the current displayed list by the last available back up of the RoadBookRepository
     *
     * Note that the the back up don't take into account the archiving state, all fetched from
     * back up records are no more archived.
     */
    fun fetchBackBackUps(){
        runBlocking {
            val lastBackUp = roadBookRepository.getLastBackUp()
            val timestamped = buildSet {
                lastBackUp.forEach { record ->
                    record.timeStamp?.let {
                        add(record)
                    }
                }
            }
            _recordsList.value =
                DRecordList(
                    allRecords = lastBackUp,
                    showArchived = currentDRecList().showArchived,
                    timestamped = timestamped
                )
        }
    }

    /**
     * Get the next destination to deliver
     *
     * The result is null if there is no nextDestination for the actual records state
     * @return DestinationRecord?
     */
    fun nextDestination(): DestinationRecord? {
       return currentDRecList().getNextDestinationRecord()
    }

    /**
     * Time stamp the given DestinationRecord
     *
     * @param timeStamp: Date
     * @param record: DestinationRecord
     */
    fun timeStampARecord(timeStamp: Date, record: DestinationRecord) {
        val newRec = DestinationRecord(
            record.destID,
            record.clientID,
            timeStamp,
            record.waitingTime,
            record.rate,
            record.actions,
            record.notes
        )
        val pos = currentDRecList().getIndexOf(record.destID)

        _recordsList.postValue(currentDRecList().editRecordAt(pos, newRec))
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
        val destID = computeDestID(clientID)
        val rec = DestinationRecord(destID, clientID, timeStamp, waitingTime, rate, actions, notes)
        _recordsList.value = currentDRecList().addRecord(rec)
    }

    /**
     * Delete the DestinationRecord at index "pos" in the recordsList
     *
     * @param pos: Int Index of the target record to delete
     */
    fun deleteRecordAt(pos: Int) {
        _recordsList.value = currentDRecList().removeRecordAt(pos)
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
        if (currentRec != newRec) {
            _recordsList.value = currentDRecList().editRecordAt(pos, newRec)
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
        _recordsList.value =
            DRecordList(
                allRecords = newList,
                showArchived = currentDRecList().showArchived,
                timestamped = setOf(ls.first())
            )
    }

    /**
     * Clear all records
     */
    fun clearAllRecords() {
        _recordsList.value =
            DRecordList(
                allRecords = emptyList(),
                showArchived = currentDRecList().showArchived
            )
    }

    private fun currentDRecList(): DRecordList {
        return _recordsList.value!!
    }

    private fun computeDestID(clientID: String): String {
        val occ = clientOccurrences.compute(clientID) { _, oldOcc ->
            var occ = oldOcc ?: 0
            ++occ
        }
        return "$clientID#$occ"
    }

    // Factory needed to assign a value at construction time to the class attribute
    @Suppress("UNCHECKED_CAST")
    class RoadBookViewModelFactory(private val repository: RoadBookRepository,
                                   private val shiftRepository: ShiftRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RoadBookViewModel::class.java))
                return RoadBookViewModel(repository, shiftRepository) as T
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}