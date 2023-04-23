package com.github.factotum_sdp.factotum.ui.roadbook

import com.github.factotum_sdp.factotum.data.DestinationRecord



/**
 *   Immutable Decorator for a List<DestinationRecord>
 *
 * - Allows archived records management through a private field containing all the archived items
 * - Allows choice at construction time for the main List, whether it contains or not the archived records.
 */
private fun displayedRecords(allRecords: List<DestinationRecord>,
                             archived: List<DestinationRecord>,
                             showArchived: Boolean): List<DestinationRecord> {
    if (showArchived)
        return allRecords
    return allRecords.minus(archived.toSet())
}

class DRecordList(private val allRecords: List<DestinationRecord> = listOf(),
                  private val archived: List<DestinationRecord> = listOf(),
                  private val showArchived: Boolean = false)
    : List<DestinationRecord> by displayedRecords(allRecords, archived, showArchived) {

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

    fun getNextDestinationIndex(): Int {
        return this.indexOfFirst { dRec -> dRec.timeStamp == null }
    }

    fun getNextDestinationRecord(): DestinationRecord {
        return this.first { dRec -> dRec.timeStamp == null }
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
            if(showArchived)
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
