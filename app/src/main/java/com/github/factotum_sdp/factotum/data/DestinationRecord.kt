package com.github.factotum_sdp.factotum.data

import java.text.SimpleDateFormat
import java.util.*

/**
 * Data design of a Destination Record
 * @property destID The DestinationRecord unique identifier
 * @property clientID The Customer unique identifier associated to this DestinationRecord
 * @property timeStamp The arrival time
 * @property waitingTime The waiting time in minutes
 * @property rate Rate as internal code notation
 * @property actions The actions to be done on a destination
 * @property notes Additional notes concerning a destination
 *
 * @See Action
 */
data class DestinationRecord(
    val destID: String,
    val clientID: String,
    val timeStamp: Date?,
    val waitingTime: Int,
    val rate: Int,
    val actions: List<Action>,
    val notes: String
){
    companion object {
        fun timeStampFormat(timeStamp: Date?): String {
            val result =
                timeStamp?.let {
                    SimpleDateFormat.getTimeInstance().format(it)
                } ?: "_"
            return result
        }

        fun actionsFormat(actions: List<Action>): String {
            val actionsWithOcc: EnumMap<Action, Int> = EnumMap(Action::class.java)
            actions.forEach {
                actionsWithOcc.compute(it) { _, occ ->
                    var newOcc = occ ?: 0
                    ++newOcc
                }
            }
            val actionsFormatList = actionsWithOcc.map {
                if (it.value == 1)
                    it.key.toString()
                else
                    "${it.key} x${it.value}"
            }
            return actionsFormatList.joinToString("| ", "(", ")")
        }
    }
    /**
     * The possible actions to achieve on a destination
     */
    enum class Action {
        PICK,
        DELIVER,
        CONTACT,
        RELAY,
        UNKNOWN;
        override fun toString(): String =
            when (this) {
                PICK -> "pick"
                DELIVER -> "deliver"
                CONTACT -> "contact"
                RELAY -> "relay"
                UNKNOWN -> "unknown"
            }
        companion object {
            fun fromString(str: String): Action =
                when (str) {
                    "pick" -> PICK
                    "deliver" -> DELIVER
                    "contact" -> CONTACT
                    "relay" -> RELAY
                    else -> UNKNOWN
                }

        }
    }
}