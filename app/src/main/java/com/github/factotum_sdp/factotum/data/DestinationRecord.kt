package com.github.factotum_sdp.factotum.data

import java.util.Date

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