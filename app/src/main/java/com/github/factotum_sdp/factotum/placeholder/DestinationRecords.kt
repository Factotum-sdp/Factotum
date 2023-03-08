package com.github.factotum_sdp.factotum.placeholder

import com.github.factotum_sdp.factotum.data.Action
import com.github.factotum_sdp.factotum.data.DestinationRecord
import java.time.Instant

object DestinationRecords {

    var RECORDS: MutableList<DestinationRecord> = ArrayList()

    init {
        RECORDS.add(DestinationRecord(Instant.now(), "QG", 1, arrayListOf()))
        RECORDS.add(DestinationRecord(null, "Buhagiat", 1, arrayListOf(Action.PICK, Action.CONTACT)))
        RECORDS.add(DestinationRecord(null, "X17", 1, arrayListOf(Action.DELIVER, Action.CONTACT)))

        //for (i in 1..40)
          //  RECORDS.add(DestinationRecord(null, "More", 1, arrayListOf(Action.PICK, Action.DELIVER)))
    }
}