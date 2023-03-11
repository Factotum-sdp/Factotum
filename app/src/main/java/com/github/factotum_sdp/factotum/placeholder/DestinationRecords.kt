package com.github.factotum_sdp.factotum.placeholder

import com.github.factotum_sdp.factotum.data.Action
import com.github.factotum_sdp.factotum.data.DestinationRecord

object DestinationRecords {

    val RECORDS: MutableList<DestinationRecord> = ArrayList()
    val RECORD_TO_ADD: DestinationRecord = DestinationRecord(null, "new", 2, arrayListOf(Action.PICK))
    val RECORD_LAST: DestinationRecord = DestinationRecord(null, "last", 2, arrayListOf(Action.PICK))
    init {
        RECORDS.add(DestinationRecord(null, "QG", 1, arrayListOf()))
        RECORDS.add(DestinationRecord(null, "Buhagiat", 1, arrayListOf(Action.PICK, Action.CONTACT)))
        RECORDS.add(DestinationRecord(null, "X17", 1, arrayListOf(Action.DELIVER, Action.CONTACT)))

        for (i in 1..15)
            RECORDS.add(DestinationRecord(null, "More", 1, arrayListOf(Action.PICK, Action.DELIVER)))
    }
}