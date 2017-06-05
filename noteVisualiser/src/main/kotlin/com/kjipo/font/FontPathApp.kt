package com.kjipo.font

import javafx.application.Application
import tornadofx.*

/**
 * Used for debugging line segment generation
 */
class GlyphView : App() {
    override val primaryView = FontPathVisualizer::class
}


fun main(args: Array<String>) {
    Application.launch(GlyphView::class.java, *args)
}

