package com.kjipo.font

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import kotlin.streams.toList

object GlyphFactory {

    val nameGlyphMap = mapOf(*loadGlyphs())

    val scale = 0.1
    val glyphResource = "/glyphs.json"
    val blankGlyph = GlyphData("blank", emptyList(), 0, BoundingBox(0.0, 0.0, 0.0, 0.0))


    fun getGlyph(noteType: NoteType): GlyphData {
        return when (noteType) {
            NoteType.QUARTER_NOTE -> getGlyph("noteheads.s2")
            NoteType.HALF_NOTE -> getGlyph("noteheads.s1")
            NoteType.WHOLE_NOTE -> getGlyph("noteheads.s0")
        }
    }

    fun getGlyph(name: String): GlyphData {
        return nameGlyphMap.getOrDefault(name, blankGlyph)
    }

    private fun loadGlyphs(): Array<Pair<String, GlyphData>> {
        val gson = Gson()
        val typeToken = object : TypeToken<Collection<GlyphData>>() {

        }.type

        return javaClass.getResourceAsStream(glyphResource).use {
            val reader = InputStreamReader(it, StandardCharsets.UTF_8)
            gson.fromJson<Collection<GlyphData>>(reader, typeToken).stream()
                    .map { glyphData -> scaleGlyph(glyphData, scale) }
                    .map { invertYCoordinates(it) }
                    .map { Pair(it.name, it) }
                    .toList()
                    .toTypedArray()

        }

    }

}