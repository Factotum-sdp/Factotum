package com.github.factotum_sdp.factotum.serializers

import androidx.datastore.core.Serializer
import com.github.factotum_sdp.factotum.model.DRecordList
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object RoadBookSerializer : Serializer<DRecordList> {
    override val defaultValue: DRecordList
        get() = DRecordList().withArchived()

    override suspend fun readFrom(input: InputStream): DRecordList {
        return try {
            Json.decodeFromString(
                deserializer = DRecordList.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: DRecordList, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = DRecordList.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}