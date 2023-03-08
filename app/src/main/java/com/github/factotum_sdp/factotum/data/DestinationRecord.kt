package com.github.factotum_sdp.factotum.data

import java.time.Instant

data class DestinationRecord(
    val timeStamp: Instant?,
    val destName: String,
    val rate: Int,
    val actions: List<Action>
)
    enum class Action {
        PICK,
        DELIVER,
        CONTACT,
        RELAY
    }