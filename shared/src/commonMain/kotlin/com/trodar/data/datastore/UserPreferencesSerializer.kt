package com.trodar.data.datastore

import androidx.datastore.core.okio.OkioSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import okio.BufferedSink
import okio.BufferedSource

@OptIn(ExperimentalSerializationApi::class)
object UserPreferencesSerializer : OkioSerializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences()

    override suspend fun readFrom(source: BufferedSource): UserPreferences {
        return try {
            val bytes = source.readByteArray()
            if (bytes.isEmpty()) {
                defaultValue
            } else {
                ProtoBuf.decodeFromByteArray(UserPreferences.serializer(), bytes)
            }
        } catch (e: Exception) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserPreferences, sink: BufferedSink) {
        val bytes = ProtoBuf.encodeToByteArray(UserPreferences.serializer(), t)
        sink.write(bytes)
    }
}
