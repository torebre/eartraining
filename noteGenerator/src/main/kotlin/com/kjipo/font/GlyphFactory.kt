package com.kjipo.font

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object GlyphFactory {

    val nameGlyphMap = mapOf(*loadGlyphs())
    val alphabetGlyphMap = mapOf(*loadAlphabet())

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
        return nameGlyphMap.getOrDefault(name, blankGlyph)
    }

    fun getNumberGlyph(number: Int): GlyphData {
        return when (number) {
            0 -> alphabetGlyphMap.getOrDefault("zero", blankGlyph)
            1 -> alphabetGlyphMap.getOrDefault("one", blankGlyph)
            2 -> alphabetGlyphMap.getOrDefault("two", blankGlyph)
            3 -> alphabetGlyphMap.getOrDefault("three", blankGlyph)
            4 -> alphabetGlyphMap.getOrDefault("four", blankGlyph)
            5 -> alphabetGlyphMap.getOrDefault("five", blankGlyph)
            6 -> alphabetGlyphMap.getOrDefault("six", blankGlyph)
            7 -> alphabetGlyphMap.getOrDefault("seven", blankGlyph)
            8 -> alphabetGlyphMap.getOrDefault("eight", blankGlyph)
            9 -> alphabetGlyphMap.getOrDefault("nine", blankGlyph)
            else -> nameGlyphMap.getOrDefault(number.toString(), blankGlyph)
        }
    }

    fun getCharacter(name: String): GlyphData {
        return alphabetGlyphMap.getOrDefault(name, blankGlyph)
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