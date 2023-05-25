package com.github.factotum_sdp.factotum.ui.bag

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import com.github.factotum_sdp.factotum.model.Bag
import com.github.factotum_sdp.factotum.model.Pack
import com.github.factotum_sdp.factotum.repositories.BagRepository
import kotlinx.coroutines.runBlocking
import java.util.Date
import java.util.HashMap

/**
 * The "bag" viewModel which represents the current state
 * of the packages delivered or currently delivered
 */
class BagViewModel(private val repository: BagRepository): ViewModel() {

    private val _packages = MutableLiveData<List<Pack>>(listOf())
    val displayedPackages = _packages.map { packs ->
        if(!withSendPacks) {
            packs.filter { it.deliveredAt == null }
        } else {
            packs
        }
    }

    private val packageOccurrences = HashMap<Pair<String, String>, Int>()
    private var blockPackUpdate = false
    private var withSendPacks = false


    /**
     * Create a new Package in the current stored packages
     *
     * @param startingRecordID: String The destID from where the new package is taken
     * @param takenAt: Date The timestamp when the package has been taken
     * @param name: String The name of the new package
     * @param senderID: String The sender's clientID
     * @param recipientID: String The recipient's clientID
     * @param notes: String
     */
    fun newPackage(startingRecordID: String, takenAt: Date, name: String,
                   senderID: String, recipientID: String, notes: String) {
        val pack = Pack(
            packageID = computePackageID(senderID, recipientID),
            name = name,
            senderID = senderID,
            recipientID = recipientID,
            startingRecordID = startingRecordID,
            arrivalRecordID = null,
            takenAt = takenAt,
            deliveredAt = null,
            notes = notes
        )
        updatePackages(currentPackages().plus(pack))
    }

    /**
     * Set the current packages state according to an arrival on a certain DestinationRecord
     *
     * @param destID: String The destID of the arrival DestinationRecord
     * @param clientID: String The clientID of the corresponding DestinationRecord
     * @param arrivalTime: Date The arrival time at the DestinationRecord's place
     */
    fun arrivedOnDestinationRecord(destID: String, clientID: String, arrivalTime: Date) {
        val updated = currentPackages().map {
            if(it.recipientID == clientID) {
                it.copy(deliveredAt = arrivalTime, arrivalRecordID = destID)
            } else {
                it
            }
        }
        updatePackages(updated)
    }

    /**
     * Set the current packages state according to the event of a removed DestinationRecord
     *
     * @param destID: String The DestinationRecord's destID
     */
    fun removedDestinationRecords(destIDs: Set<String>) {
        val updated = currentPackages().mapNotNull {
            if(destIDs.contains(it.startingRecordID)) {
                null// filter out the pack with their startingDRecord deleted
            } else if (destIDs.contains(it.arrivalRecordID)) {
                it.copy(deliveredAt = null)
            } else {
                it
            }
        }
        updatePackages(updated)
    }

    /**
     * Set the current packages state according to a DestinationRecord's timestamp modification
     *
     * @param timestamp: Date
     * @param destID: String
     */
    fun adjustTimestampOf(timestamp: Date, destID: String) {
        val updated = currentPackages().map {
            if(it.startingRecordID == destID) {
                it.copy(takenAt = timestamp)
            } else if(it.arrivalRecordID == destID) {
                it.copy(deliveredAt = timestamp)
            } else {
                it
            }
        }
        updatePackages(updated)
    }

    /**
     * Update the specified pack notes
     *
     * @param packageID: String the pack identifier
     * @param notes: String
     */
    fun updateNotesOf(packageID: String, notes: String) {
        val updated = currentPackages().map {
            if(it.packageID == packageID) it.copy(notes = notes) else it
        }
        updatePackages(updated)
    }

    /**
     * Triggers a back-up of the current Bag state
     */
    fun backUp() {
        _packages.value?.let {
            repository.setBackUp(Bag(it))
        }
    }

    /**
     * Load the last back-up available into the packages liveData
     */
    fun fetchBackBackUp() {
        runBlocking {
            val lastBackUp = repository.getLastBackUp()
            updatePackages(lastBackUp)
        }
    }

    /**
     * Whether the delivered Packs have to be displayed
     * @param isDisplayed: Boolean
     */
    fun displayDeliveredPacks(isDisplayed: Boolean) {
        withSendPacks = isDisplayed
        _packages.value = currentPackages()
    }

    /**
     * To check the current packs update state
     * If false, no update of this BagViewModel should be triggered from outside
     * @return Boolean
     */
    fun isPackUpdateBlocked(): Boolean {
        return blockPackUpdate
    }

    /**
     * Set the packs update state to blocked
     */
    fun blockPackUpdate() {
        blockPackUpdate = true
    }

    /**
     * Allow the packs update
     */
    fun allowPackUpdate() {
        blockPackUpdate = false
    }

    private fun updatePackages(updated: List<Pack>) {
        _packages.value = updated
        repository.setBackUp(Bag(updated))
    }

    private fun currentPackages(): List<Pack> {
        return _packages.value!!
    }

    private fun computePackageID(senderID: String, recipientID: String): String {
        val occ = packageOccurrences.compute(Pair(senderID, recipientID)) { _, oldOcc ->
            var occ = oldOcc ?: 0
            ++occ
        }
        return "${senderID}To${recipientID}#$occ"
    }

    // Factory needed to assign a value at construction time to the class attribute
    class BagViewModelFactory(private val _repository: BagRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(BagRepository::class.java)
                .newInstance(_repository)
        }
    }
}