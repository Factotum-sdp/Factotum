package com.github.factotum_sdp.factotum.data

import java.util.Date

/**
 * Data design of a Destination Record
 * @property destID The destination unique identifier
 * @property timeStamp The arrival time
 * @property waitingTime The waiting time in minutes
 * @property rate Rate as internal code notation
 * @property actions The actions to be done on a destination
 *
 * @See Action
 */
data class DestinationRecord(
    val destID: String,
    val timeStamp: Date?,
    val waitingTime: Int,
    val rate: Int,
    val actions: List<Action>
)

    /**
     * The possible actions to achieve on a destination
     */
    enum class Action {
        PICK,
        DELIVER,
        CONTACT,
        RELAY
    }