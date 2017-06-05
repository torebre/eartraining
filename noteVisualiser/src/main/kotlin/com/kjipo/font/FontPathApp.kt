package com.kjipo.font

import javafx.application.Application
import javafx.stage.Stage
import tornadofx.*



class GlyphView: App() {
    override val primaryView = FontPathVisualizer::class

}




fun main(args: Array<String>) {
    Application.launch(GlyphView::class.java, *args)
}

