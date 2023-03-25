package com.github.factotum_sdp.factotum.placeholder

import com.github.factotum_sdp.factotum.data.DestinationRecord
import com.github.factotum_sdp.factotum.data.DestinationRecord.Action
import java.util.*

/**
 * Temporary PlaceHolder for some DestinationRecord synthetic data
 *
 * @see DestinationRecord
 */
object DestinationRecords {

    val RECORDS: MutableList<DestinationRecord> = ArrayList()
    val RECORD_TO_ADD: DestinationRecord =
        DestinationRecord("new#1", "new",null, 0, 2, arrayListOf(Action.PICK))

    init {
        val cal: Calendar = Calendar.getInstance()
        RECORDS.addAll(
            listOf(
                DestinationRecord("QG#1", "QG",cal.time, 3, 1, arrayListOf()),
                DestinationRecord(
                    "Buhagiat#1",
                    "Buhagiat",
                    null,
                    0,
                    1,
                    arrayListOf(Action.PICK, Action.CONTACT, Action.RELAY)
                ),
                DestinationRecord("X17#1","X17", null, 0, 1, arrayListOf(Action.DELIVER, Action.CONTACT))
            )
        )

        for (i in 1..15)
            RECORDS.add(
                DestinationRecord(
                    "More#$i",
                    "More",
                    null,
                    0,
                    1,
                    arrayListOf(Action.PICK, Action.DELIVER)
                )
            )
    }
}