package com.github.factotum_sdp.factotum.serializers

import com.github.factotum_sdp.factotum.models.DestinationRecord
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Date

object DateKSerializer: KSerializer<Date?> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("java.util.Date", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Date?) {
        val format = DestinationRecord.timeStampFormat(value)
        encoder.encodeString(format)
    }

    override fun deserialize(decoder: Decoder): Date? {
        val format = decoder.decodeString()
        return DestinationRecord.parseTimestamp(format)
    }

}