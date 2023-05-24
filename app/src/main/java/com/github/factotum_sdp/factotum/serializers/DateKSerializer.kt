package com.github.factotum_sdp.factotum.serializers

import android.util.Log
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.DateFormat.DEFAULT
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale.ENGLISH

/**
 * This class is used to serialize and deserialize java.util.Date
 */
object DateKSerializer: KSerializer<Date> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("java.util.Date", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Date {
        val format = decoder.decodeString()
        return try {
            SimpleDateFormat.getDateTimeInstance(
                DEFAULT,
                DEFAULT,
                ENGLISH
            ).parse(format) as Date
        } catch (e: ParseException) {
            Log.e("DateKSerializer", "Failed to parse date: $format")
            Date()
        }
    }

    override fun serialize(encoder: Encoder, value: Date) {
            val format = SimpleDateFormat.getDateTimeInstance(
                DEFAULT,
                DEFAULT,
                ENGLISH
            ).format(value)
            encoder.encodeString(format)
    }
}