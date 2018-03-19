package com.kjipo.font

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object GlyphFactory {

    val nameGlyphMap = mapOf(*loadGlyphs())
    val alphabetGlyphMap = mapOf(*loadAlphabet()).withDefault { blankGlyph }

    val scale = 0.1
    val glyphResource = "/glyphs.json"
    val blankGlyph = GlyphData("blank", emptyList(), 0, BoundingBox(0.0, 0.0, 0.0, 0.0))

    val alphabetResource = "/alpha.json"


    fun getGlyph(noteType: NoteType): GlyphData {
        return when (noteType) {
            NoteType.QUARTER_NOTE -> getGlyph("noteheads.s2")
            NoteType.HALF_NOTE -> getGlyph("noteheads.s1")
            NoteType.WHOLE_NOTE -> getGlyph("noteheads.s0")
        }
    }

    fun getGlyph(name: String): GlyphData {
        return nameGlyphMap.getOrElse(name, { blankGlyph })
    }

    fun getNumberGlyph(number: Int): GlyphData {
        return when (number) {
            0 -> alphabetGlyphMap.getValue("zero")
            1 -> alphabetGlyphMap.getValue("one")
            2 -> alphabetGlyphMap.getValue("two")
            3 -> alphabetGlyphMap.getValue("three")
            4 -> alphabetGlyphMap.getValue("four")
            5 -> alphabetGlyphMap.getValue("five")
            6 -> alphabetGlyphMap.getValue("six")
            7 -> alphabetGlyphMap.getValue("seven")
            8 -> alphabetGlyphMap.getValue("eight")
            9 -> alphabetGlyphMap.getValue("nine")
            else -> nameGlyphMap.getValue(number.toString())
        }
    }

    fun getCharacter(name: String): GlyphData {
        return alphabetGlyphMap.getOrElse(name, { blankGlyph })
    }

    private fun loadGlyphs(): Array<Pair<String, GlyphData>> = loadGlyphResource(glyphResource, scale)

    private fun loadAlphabet(): Array<Pair<String, GlyphData>> = loadGlyphResource(alphabetResource, scale)

    private fun loadGlyphResource(resourceLocation: String, glyphScale: Double): Array<Pair<String, GlyphData>> {
        val gson = Gson()
        val typeToken = object : TypeToken<Collection<GlyphData>>() {

        }.type

        return javaClass.getResourceAsStream(resourceLocation).use {
            val reader = InputStreamReader(it, StandardCharsets.UTF_8)
            gson.fromJson<Collection<GlyphData>>(reader, typeToken).toList()
                    .map { glyph -> scaleGlyph(glyph, glyphScale) }
                    .map { invertYCoordinates(it) }
                    .map { Pair(it.name, it) }
                    .toList()
                    .toTypedArray()

        }
    }

}