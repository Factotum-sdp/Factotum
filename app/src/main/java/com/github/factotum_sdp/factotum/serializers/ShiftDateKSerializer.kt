package com.github.factotum_sdp.factotum.serializers

import android.util.Log
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * This class is used to serialize and deserialize java.util.Date that are use as keys in Firebase
 */
object ShiftDateKSerializer : KSerializer<Date> {
    private val pattern = "ddMMyyyy-HH:mm:ss"
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("java.util.Date", PrimitiveKind.STRING)


    override fun deserialize(decoder: Decoder): Date {
        val format = decoder.decodeString()
        val date = SimpleDateFormat(pattern, Locale.ENGLISH).parse(format)
        if (date == null){
            Log.e("FirebaseDateKSerializer", "Failed to parse date: $format")
        }
        return date!!

    }

    override fun serialize(encoder: Encoder, value: Date) {

        val format = SimpleDateFormat(pattern, Locale.ENGLISH).format(value)
        encoder.encodeString(format)
    }

}