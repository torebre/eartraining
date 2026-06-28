package com.kjipo.soundfontparser.serialization

import com.kjipo.soundfontparser.SoundFontData
import kotlinx.serialization.json.Json

object SoundFontDataJsonSerializer {
    private val compactJson = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val prettyJson = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun serialize(soundFontData: SoundFontData): String =
        compactJson.encodeToString(SoundFontDataDto.serializer(), soundFontData.toDto())

    fun serializePretty(soundFontData: SoundFontData): String =
        prettyJson.encodeToString(SoundFontDataDto.serializer(), soundFontData.toDto())

    fun deserialize(serialized: String): SoundFontData =
        compactJson.decodeFromString(SoundFontDataDto.serializer(), serialized).toSoundFontData()
}
