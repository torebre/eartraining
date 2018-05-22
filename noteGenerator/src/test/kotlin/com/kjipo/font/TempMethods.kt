package com.kjipo.font

import com.kjipo.svg.GlyphData
import com.kjipo.svg.findBoundingBox
import com.kjipo.svg.translateFontPathElement
import java.io.OutputStreamWriter
import java.util.*


fun main(args:Array<String>) {
    val pathElements = FontProcessingUtilities.parsePathData("M95 178Q89 178 81 186T72 200T103 230T169 280T207 309Q209 311 212 311H213Q219 311 227 294T281 177Q300 134 312 108L397 -77Q398 -77 501 136T707 565T814 786Q820 800 834 800Q841 800 846 794T853 782V776L620 293L385 -193Q381 -200 366 -200Q357 -200 354 -197Q352 -195 256 15L160 225L144 214Q129 202 113 190T95 178Z")
    val translatedElements = pathElements.map { translateFontPathElement(it, 100, 200) }.toList()
    val boundingBox = findBoundingBox(translatedElements)

    val glyphData = GlyphData("Test", translatedElements, boundingBox)

    OutputStreamWriter(System.out).use {
        ReadFonts.writeGlyphsToOutputStream(Collections.singletonList(glyphData), it)
    }

}
