package com.kjipo.viewer

import javafx.application.Application
import tornadofx.*
import java.net.URL


class WebViewApplication : App() {
    override val primaryView = NoteView::class


    companion object {

        fun startApplication() {
            URL.setURLStreamHandlerFactory { protocol ->
                if (protocol.equals("classpath")) {
                    Handler()
                } else {
                    null
                }
            }
            val startThread = Thread {
                Application.launch(WebViewApplication::class.java)
            }
            startThread.start()
        }

        @JvmStatic
        fun main(args: Array<String>) {
            startApplication()
        }


    }

}

