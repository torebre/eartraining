package com.kjipo.svg

import javafx.application.Application
import tornadofx.*
import java.net.URL


class WebViewApplication : App() {
    override val primaryView = NoteView::class

}


fun startApplication() {
    URL.setURLStreamHandlerFactory { protocol ->
        if (protocol.equals("classpath")) {
            Handler()
        } else {
            null
        }
    }
    val startThread = Thread {
        Application.launch(WebViewApplication::class.java, *emptyArray())
    }
    startThread.start()
}


fun main(args: Array<String>) {
    startApplication()
}

