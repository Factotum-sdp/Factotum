package com.github.factotum_sdp.factotum.ui.bag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.models.Pack
import java.util.Date
import java.util.HashMap

class BagViewModel: ViewModel() {
    private val _packages = MutableLiveData<List<Pack>>(listOf())
    val packages: LiveData<List<Pack>> = _packages

    private val packageOccurrences = HashMap<Pair<String, String>, Int>()

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

    /** Remove package or unmark the destination related to a dest record */
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