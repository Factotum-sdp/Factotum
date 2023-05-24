package com.github.factotum_sdp.factotum.serializers

import kotlinx.serialization.SerializationException
import androidx.datastore.core.Serializer
import com.github.factotum_sdp.factotum.models.Bag
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object BagSerializer : Serializer<Bag> {

    override val defaultValue: Bag
        get() = Bag(listOf())

    override suspend fun readFrom(input: InputStream): Bag {
        return try {
            Json.decodeFromString(
                deserializer = Bag.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Bag, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = Bag.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}