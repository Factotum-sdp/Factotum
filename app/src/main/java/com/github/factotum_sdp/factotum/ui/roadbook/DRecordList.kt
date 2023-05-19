package com.github.factotum_sdp.factotum.ui.roadbook
import com.github.factotum_sdp.factotum.models.DestinationRecord
import java.lang.IllegalArgumentException
import kotlinx.serialization.Serializable


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

@Serializable
class DRecordList(
    private val allRecords: List<DestinationRecord> = listOf(),
    private val archived: List<DestinationRecord> = listOf(),
    private val timestamped: Set<DestinationRecord> = setOf(),
    val showArchived: Boolean = false
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

    fun getDestinationRecordFromID(destID: String): DestinationRecord {
        return this[getIndexOf(destID)]
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

    fun timestampedSet(): Set<DestinationRecord> {
        return timestamped
    }

    // append the record, for sure not archived
    fun addRecord(record: DestinationRecord): DRecordList {
        return DRecordList(allRecords.plus(record), archived, computeTimestampedOnAdd(record), showArchived)
    }

    fun removeRecordAt(pos: Int): DRecordList {
        val recordToRemove = this[pos]
        val newTimestamped = computeTimeStampedOnRemove(recordToRemove)

        if (archivedSet.contains(recordToRemove)) {
            return DRecordList(allRecords.minus(recordToRemove), archived.minus(recordToRemove), newTimestamped, showArchived)
        }
        return DRecordList(allRecords.minus(recordToRemove), archived, newTimestamped, showArchived)
    }

    fun editRecordAt(pos: Int, newRecord: DestinationRecord): DRecordList {
        val oldRecord = this[pos]
        val newTimestamped = computeTimestampedOnEdit(oldRecord, newRecord)

        val posInAll = allRecords.indexOfFirst { it.destID == oldRecord.destID }
        if(archivedSet.contains(oldRecord)) {
            val posInArchived = archived.indexOfFirst { it.destID == oldRecord.destID }
            return DRecordList(
                replaceRecordAt(posInAll, newRecord, allRecords),
                replaceRecordAt(posInArchived, newRecord, archived),
                newTimestamped,
                showArchived
            )
        }
        return DRecordList(replaceRecordAt(posInAll, newRecord, allRecords), archived, newTimestamped)
    }

    /**
     * Update the current main List (this) in an immutable way,
     * Only use that method when records are not edited, or removed in "ls".
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
        return DRecordList(allRecs, archived, timestamped, showArchived)
    }

    /**
     * Archive a record
     *
     * @param index: Int index of the target DestinationRecord
     * @return a new DRecordList with the specified record added to the archived records
     */
    fun archiveRecord(index: Int): DRecordList {
        return DRecordList(allRecords, archived.plus(this[index]), timestamped, showArchived)
    }

    /**
     * Unarchive a record
     *
     * @param index: Int index of the target DestinationRecord
     * @return a new DRecordList containing with the specified record out of the archived records
     */
    fun unarchiveRecord(index: Int): DRecordList {
        return DRecordList(allRecords, archived.minus(this[index]), timestamped, showArchived)
    }

    /**
     * Instantiate a new DRecordList which displays the archived records
     *
     * @return a new DRecordList with the main (this) list displaying all the records
     */
    fun withArchived(): DRecordList {
        return DRecordList(allRecords, archived, timestamped, true)
    }

    /**
     * Instantiate a new DRecordList which displays only the non-archived records
     *
     * @return a new DRecordList with the main (this) list displaying only the non-archived records
     */
    fun withoutArchived(): DRecordList {
        return DRecordList(allRecords, archived, timestamped, false)
    }


    /** Helpers : */
    private fun computeTimestampedOnAdd(recordToAdd: DestinationRecord): Set<DestinationRecord> {
        return recordToAdd.timeStamp?.let { timestamped.plus(recordToAdd) } ?: timestamped
    }
    private fun computeTimeStampedOnRemove(recordToRemove: DestinationRecord): Set<DestinationRecord> {
        return recordToRemove.timeStamp?.let {
            timestamped.minus(recordToRemove)
        } ?: timestamped
    }

    private fun computeTimestampedOnEdit(oldRecord: DestinationRecord, newRecord: DestinationRecord)
    : Set<DestinationRecord> {
        var result: Set<DestinationRecord>
        if(oldRecord.destID != newRecord.destID) {
            result = computeTimeStampedOnRemove(oldRecord)
            result = newRecord.timeStamp?.let { result.plus(newRecord) } ?: result
        } else {
            result = if(oldRecord.timeStamp != null && newRecord.timeStamp == null) {
                timestamped.minus(oldRecord)
            } else if(oldRecord.timeStamp == null && newRecord.timeStamp != null) {
                timestamped.plus(newRecord)
            } else { // if timestamp doesn't move update the timestamped if already here
                if(timestamped.contains(oldRecord)) {
                    timestamped.minus(oldRecord).plus(newRecord)
                } else {
                    timestamped
                }
            }
        }
        return result
    }

    private fun replaceRecordAt(pos: Int, record: DestinationRecord,
                                records: List<DestinationRecord>): List<DestinationRecord> {
        val ls = arrayListOf<DestinationRecord>()
        ls.addAll(records)
        ls[pos] = record
        return ls
    }
}
