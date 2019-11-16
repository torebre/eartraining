package com.kjipo.svg


import com.kjipo.score.Accidental
import com.kjipo.score.Duration


const val SCALE = 0.1
val blankGlyph = GlyphData("blank", emptyList(), 0, BoundingBox(0.0, 0.0, 0.0, 0.0))


fun getNoteHeadGlyph(noteType: Duration): GlyphData {
    return when (noteType) {
        Duration.ZERO -> blankGlyph
        // The note head is the same for the eight as for the quarter note
        Duration.EIGHT -> getGlyph("noteheads.s2")
        Duration.QUARTER -> getGlyph("noteheads.s2")
        Duration.HALF -> getGlyph("noteheads.s1")
        Duration.WHOLE -> getGlyph("noteheads.s0")
    }
}

fun getAccidentalGlyph(accidental: Accidental): GlyphData {
    return when (accidental) {
        Accidental.FLAT -> getGlyph("accidentals.flat")
        Accidental.SHARP -> getGlyph("accidentals.sharp")
    }
}

fun getRestGlyph(restDuration: Duration): GlyphData {
    return when (restDuration) {
        Duration.ZERO -> blankGlyph
        Duration.EIGHT -> getGlyph("rests.3")
        Duration.QUARTER -> getGlyph("rests.2")
        Duration.HALF -> getGlyph("rests.1")
        Duration.WHOLE -> getGlyph("rests.0")
    }
}

fun getFlagGlyph(duration: Duration, up: Boolean): GlyphData {
    return when (duration) {
        Duration.EIGHT -> if(up) {
            getGlyph("flags.u3")
        }
        else {
            getGlyph("flags.d3")
        }
        else -> blankGlyph
    }
}

fun getGlyph(name: String): GlyphData {
    return GlyphList.instance.nameGlyphMap.getOrElse(name, { blankGlyph })
}

fun getNumberGlyph(number: Int): GlyphData {
    return when (number) {
        0 -> GlyphList.instance.alphabetGlyphMap.getValue("zero")
        1 -> GlyphList.instance.alphabetGlyphMap.getValue("one")
        2 -> GlyphList.instance.alphabetGlyphMap.getValue("two")
        3 -> GlyphList.instance.alphabetGlyphMap.getValue("three")
        4 -> GlyphList.instance.alphabetGlyphMap.getValue("four")
        5 -> GlyphList.instance.alphabetGlyphMap.getValue("five")
        6 -> GlyphList.instance.alphabetGlyphMap.getValue("six")
        7 -> GlyphList.instance.alphabetGlyphMap.getValue("seven")
        8 -> GlyphList.instance.alphabetGlyphMap.getValue("eight")
        9 -> GlyphList.instance.alphabetGlyphMap.getValue("nine")
        else -> GlyphList.instance.nameGlyphMap.getValue(number.toString())
    }
}

fun getCharacter(name: String): GlyphData {
    return GlyphList.instance.alphabetGlyphMap.getOrElse(name, { blankGlyph })
}
