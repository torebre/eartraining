package com.kjipo.svg


import com.kjipo.score.Duration


val nameGlyphMap = mapOf<String, GlyphData>()
val alphabetGlyphMap = mapOf<String, GlyphData>()

val scale = 0.1
val glyphResource = "/glyphs.json"
val blankGlyph = GlyphData("blank", emptyList(), 0, BoundingBox(0.0, 0.0, 0.0, 0.0))

val alphabetResource = "/alpha.json"


fun getGlyph(noteType: Duration): GlyphData {
    return when (noteType) {
        Duration.QUARTER -> getGlyph("noteheads.s2")
        Duration.HALF -> getGlyph("noteheads.s1")
        Duration.WHOLE -> getGlyph("noteheads.s0")
    }
}

fun getRest(restDuration: Duration): GlyphData {
    return when (restDuration) {
        Duration.QUARTER -> getGlyph("rests.2")
        Duration.HALF -> getGlyph("rests.1")
        Duration.WHOLE -> getGlyph("rests.0")
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
