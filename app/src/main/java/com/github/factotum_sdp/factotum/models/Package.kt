package com.github.factotum_sdp.factotum.models

import java.util.Date

data class Package(
    val packageID: String,
    val senderID: String,
    val receiverID: String,
    val timeStamp: Date?,
    val notes: String)