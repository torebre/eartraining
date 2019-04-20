package com.kjipo.viewer

import javafx.application.Application
import tornadofx.*
import java.net.URL


class WebViewApplicationScoreHandlerApplication : App() {
    override val primaryView = NoteViewerWithScoreHandler::class


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
                Application.launch(WebViewApplicationScoreHandlerApplication::class.java)
            }
            startThread.start()
        }

        @JvmStatic
        fun main(args: Array<String>) {
            startApplication()
        }


    }

}

