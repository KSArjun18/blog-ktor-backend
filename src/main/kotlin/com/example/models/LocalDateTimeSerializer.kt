package com.example.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val instantString = value.toInstant(kotlinx.datetime.TimeZone.UTC).toString()
        encoder.encodeString(instantString)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val instantString = decoder.decodeString()
        return Instant.parse(instantString).toLocalDateTime(kotlinx.datetime.TimeZone.UTC)
    }
}
