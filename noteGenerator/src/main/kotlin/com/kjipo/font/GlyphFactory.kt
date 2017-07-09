package com.kjipo.font

import kotlin.streams.toList

object GlyphFactory {

    val nameGlyphMap = mapOf(*loadGlyphs())

    val scale = 0.1
    val glyphResource = "/gonville-r9313/lilyfonts/svg/emmentaler-11.svg"
    val blankGlyph = GlyphData("blank", emptyList(), 0, BoundingBox(0.0, 0.0, 0.0, 0.0))

    fun getGlyph(name:String):GlyphData {
        return nameGlyphMap.getOrDefault(name, blankGlyph)
    }

    private fun loadGlyphs(): Array<Pair<String, GlyphData>> {
        return javaClass.getResourceAsStream(glyphResource).use {
            ReadFonts.extractGlyphPaths(it).stream()
                    .map { glyphData -> scaleGlyph(glyphData, scale) }
                    .map { invertYCoordinates(it) }
                    .map { Pair(it.name, it) }
                    .toList()
                    .toTypedArray()

        }
    }

}