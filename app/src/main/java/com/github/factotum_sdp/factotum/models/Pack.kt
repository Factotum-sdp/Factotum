package com.github.factotum_sdp.factotum.models

import java.util.Date

data class Pack(
    val packageID: String,
    val name: String,
    val senderID: String,
    val recipientID: String,
    val startingRecordID: String,
    val arrivalRecordID: String?,
    val takenAt: Date,
    val deliveredAt: Date?,
    val notes: String)
