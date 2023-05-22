package com.github.factotum_sdp.factotum.ui.bag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.models.Pack
import java.util.Date
import java.util.HashMap

/**
 * The "bag" viewModel which represents the current state
 * of the packages delivered or currently delivered
 */
class BagViewModel: ViewModel() {
    private val _packages = MutableLiveData<List<Pack>>(listOf())
    val packages: LiveData<List<Pack>> = _packages

    private val packageOccurrences = HashMap<Pair<String, String>, Int>()

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
        _packages.postValue(currentPackages().plus(pack))
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
        _packages.postValue(updated)
    }

    /**
     * Set the current packages state according to the event of a removed DestinationRecord
     *
     * @param destID: String The DestinationRecord's destID
     */
    fun removedDestinationRecord(destID: String) {
        val updated = currentPackages().mapNotNull {
            if(it.startingRecordID == destID) {
                null// filter out the pack with their startingDRecord deleted
            } else if (it.arrivalRecordID == destID) {
                it.copy(deliveredAt = null)
            } else {
                it
            }
        }
        _packages.postValue(updated)
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
        _packages.postValue(updated)
    }

    /**
     * Update the specified pack notes
     *
     * @param packageID: String the pack identifier
     * @param notes: String
     */
    fun updateNotesOf(packageID: String, notes: String) {
        _packages.value = currentPackages().map {
            if(it.packageID == packageID) it.copy(notes = notes) else it
        }
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
}