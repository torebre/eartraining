package com.kjipo.font

import kotlin.streams.toList

object GlyphFactory {

    val nameGlyphMap = mapOf(*loadGlyphs())

    val scale = 0.1
    val glyphResource = "/gonville-r9313/lilyfonts/svg/emmentaler-11.svg"


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