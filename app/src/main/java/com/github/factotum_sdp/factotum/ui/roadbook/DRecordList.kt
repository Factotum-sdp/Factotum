package com.github.factotum_sdp.factotum.ui.roadbook

import com.github.factotum_sdp.factotum.models.DestinationRecord
import java.lang.IllegalArgumentException


/**
 *   Immutable Decorator for a List<DestinationRecord>
 *
 * - Allows archived records management through a private field containing all the archived items
 * - Allows choice at construction time for the main List, whether it contains or not the archived records.
 */
private fun displayedRecords(
    allRecords: List<DestinationRecord>,
    archived: List<DestinationRecord>,
    showArchived: Boolean
): List<DestinationRecord> {
    if (showArchived)
        return allRecords
    return allRecords.minus(archived.toSet())
}

class DRecordList(
    private val allRecords: List<DestinationRecord> = listOf(),
    private val archived: List<DestinationRecord> = listOf(),
    private val showArchived: Boolean = false
) : List<DestinationRecord> by displayedRecords(allRecords, archived, showArchived) {

    private val archivedSet = archived.toSet() // For performance

    /**
     * Check if the record at position "index" is archived
     *
     * @param index: Int index of the target DestinationRecord
     * @return Boolean whether the specified record is archived or not
     */
    fun isArchived(index: Int): Boolean {
        return archivedSet.contains(this[index])
    }

    /**
     * Retrieve the index of the DestinationRecord with id "destID"
     *
     * @param destID: String
     * @return The destination index
     * @throws IllegalArgumentException when "destID" is not contained in the current main List
     */
    fun getIndexOf(destID: String): Int {
        try {
            return this.indexOfFirst { dRec -> dRec.destID == destID }
        } catch (e: NoSuchElementException) {
            throw IllegalArgumentException()
        }
    }

    /**
     * Get the next destination to deliver
     *
     * @return DestinationRecord or null when all Destination are already visited
     */
    fun getNextDestinationRecord(): DestinationRecord? {
        return try {
            this.first { dRec -> dRec.timeStamp == null }
        } catch (e: NoSuchElementException) {
            null
        }
    }

    /**
     * Update the current main List (this) in an immutable way,
     * keeping all the others current settings.
     *
     * @param ls: List<DestinationRecord> The new list to replace the current (this) main List
     * @return a new DRecordList with the main List updated
     */
    fun replaceDisplayedList(ls: List<DestinationRecord>): DRecordList {
        val allRecs =
            if (showArchived)
                ls
            else
                archived.plus(ls)
        return DRecordList(allRecs, archived, showArchived)
    }

    /**
     * Archive a record
     *
     * @param index: Int index of the target DestinationRecord
     * @return a new DRecordList with the specified record added to the archived records
     */
    fun archiveRecord(index: Int): DRecordList {
        return DRecordList(allRecords, archived.plus(this[index]), showArchived)
    }

    /**
     * Unarchive a record
     *
     * @param index: Int index of the target DestinationRecord
     * @return a new DRecordList containing with the specified record out of the archived records
     */
    fun unarchiveRecord(index: Int): DRecordList {
        return DRecordList(allRecords, archived.minus(this[index]), showArchived)
    }

    /**
     * Instantiate a new DRecordList which displays the archived records
     *
     * @return a new DRecordList with the main (this) list displaying all the records
     */
    fun withArchived(): DRecordList {
        return DRecordList(allRecords, archived, true)
    }

    /**
     * Instantiate a new DRecordList which displays only the non-archived records
     *
     * @return a new DRecordList with the main (this) list displaying only the non-archived records
     */
    fun withoutArchived(): DRecordList {
        return DRecordList(allRecords, archived, false)
    }
}
