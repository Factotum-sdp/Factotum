package com.github.factotum_sdp.factotum.models

import java.util.Date

/**
 * The delivered package data model
 *
 * @property packageID: String The pack ID
 * @property name: String The pack name
 * @property senderID: String The sender's clientID
 * @property recipientID: String The recipient's clientID
 * @property startingRecordID: String The DestinationRecord ID where this pack has been taken
 * @property arrivalRecordID: String The DestinationRecord ID where this pack has been delivered
 * @property takenAt: Date The time when this package has been taken
 * @property deliveredAt: Date The time when this package has been delivered
 * @property notes: String Some additional notes about this package
 */
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