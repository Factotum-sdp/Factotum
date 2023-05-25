package com.github.factotum_sdp.factotum.model

import com.github.factotum_sdp.factotum.serializers.DateKSerializer
import com.github.factotum_sdp.factotum.serializers.NullableDateKSerializer
import kotlinx.serialization.Serializable
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

@Serializable
data class Pack(
    val packageID: String = "",
    val name: String = "",
    val senderID: String = "",
    val recipientID: String = "",
    val startingRecordID: String = "",
    val arrivalRecordID: String? = "",

    @Serializable(with = DateKSerializer::class) val takenAt: Date? = Date(),
    @Serializable(with = NullableDateKSerializer::class) val deliveredAt: Date? = null,
    val notes: String = "")