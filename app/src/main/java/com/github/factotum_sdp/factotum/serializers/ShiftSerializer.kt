package com.github.factotum_sdp.factotum.serializers

import androidx.datastore.core.Serializer
import com.github.factotum_sdp.factotum.ui.roadbook.ShiftList
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object ShiftSerializer : Serializer<ShiftList> {
    override val defaultValue: ShiftList
        get() = ShiftList(emptyList())

    override suspend fun readFrom(input: InputStream): ShiftList {
        return try {
            Json.decodeFromString(
                deserializer = ShiftList.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: ShiftList, output: OutputStream) {
        return output.write(Json.encodeToString(serializer = ShiftList.serializer(), value = t).encodeToByteArray())
    }
}