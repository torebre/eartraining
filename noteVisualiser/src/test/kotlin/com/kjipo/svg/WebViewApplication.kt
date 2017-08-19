package com.kjipo.svg

import javafx.application.Application
import tornadofx.*
import java.net.URL
import java.net.URLStreamHandler
import java.net.URLStreamHandlerFactory


class WebViewApplication : App() {
    override val primaryView = NoteView::class

}


fun main(args: Array<String>) {
    URL.setURLStreamHandlerFactory(object : URLStreamHandlerFactory {
        override fun createURLStreamHandler(protocol: String?): URLStreamHandler? {
            if (protocol.equals("classpath")) {
                return Handler()
            } else {
                return null
            }
        }
    })

    val testThread = Thread {
        Thread.sleep(5000)
        println("Setting fill")
        NoteView.instance.setFill(1)
    }

    testThread.start()

    Application.launch(WebViewApplication::class.java, *args)



}

