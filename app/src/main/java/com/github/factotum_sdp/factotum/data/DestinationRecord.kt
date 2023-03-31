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

        /**
         * Usual String Format to display a Date object belonging to a DestinationRecord
         *
         * Format : "HH:MM:SS AM-PM" / if timeStamp Date is null : _
         *
         * @param timeStamp: Date?
         * @return the timeStamp String format
         */
        fun timeStampFormat(timeStamp: Date?): String {
            val result =
                timeStamp?.let {
                    SimpleDateFormat.getTimeInstance().format(it)
                } ?: "_"
            return result
        }

        /**
         * Usual String Format to display a list of Action objects
         *
         * The same Action object occurrences in a List<Action>, are collected to be displayed
         * in a parenthesis format with '|' as separator.
         *
         * Example for a List(PICK, CONTACT, PICK) : (contact, pick x2)
         *
         * @param actions: List<Action>
         * @return the actions String format
         */
        fun actionsFormat(actions: List<Action>): String {

            val actionsWithOcc: EnumMap<Action, Int> = EnumMap(Action::class.java)
            actions.forEach {// Collect the occurrences
                actionsWithOcc.compute(it) { _, occ ->
                    var newOcc = occ ?: 0
                    ++newOcc
                }
            }
            val actionsFormatList = actionsWithOcc.map {
                if (it.value == 1)
                    it.key.toString()// If only one occurrences do not display the x character
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