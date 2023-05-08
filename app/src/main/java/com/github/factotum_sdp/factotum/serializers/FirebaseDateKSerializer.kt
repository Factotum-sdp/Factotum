package com.github.factotum_sdp.factotum.serializers

import android.util.Log
import com.github.factotum_sdp.factotum.firebase.FirebaseStringFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Date

/**
 * This class is used to serialize and deserialize java.util.Date that are use as keys in Firebase
 */
object FirebaseDateKSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("java.util.Date", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Date {
        val format = decoder.decodeString()
        val date = FirebaseStringFormat.firebaseParseDate(format)
        if (date == null){
            Log.e("FirebaseDateKSerializer", "Failed to parse date: $format")
        }
        return date!!

    }

    override fun serialize(encoder: Encoder, value: Date) {
        val format = FirebaseStringFormat.firebaseDateFormatted(value)
        encoder.encodeString(format)
    }

}