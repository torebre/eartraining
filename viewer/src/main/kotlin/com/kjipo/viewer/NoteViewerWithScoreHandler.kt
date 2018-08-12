package com.kjipo.viewer

import com.kjipo.handler.ScoreHandler
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker
import javafx.event.EventType
import javafx.scene.layout.Region
import javafx.scene.web.WebView
import netscape.javascript.JSObject
import tornadofx.*
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class NoteViewerWithScoreHandler : View("Note view") {
    val webView: WebViewTest = WebViewTest()

    fun load(scoreHandler: ScoreHandler) {
        webView.latch.await(10, TimeUnit.SECONDS)
        val window = webView.webEngine.executeScript("window") as JSObject
        window.setMember("scorehandler", scoreHandler)
        webView.webEngine.executeScript("var webscoreInstance = new webscore.WebScore(scorehandler);")
    }

    override val root = stackpane {
        webView.style(true, {
            minWidth = Dimension(100.0, Dimension.LinearUnits.px)
            minHeight = Dimension(100.0, Dimension.LinearUnits.px)
        })

        add(webView)
    }


    class JavaBridge {

        fun log(text: String) {
            println(text)
        }

    }

    inner class WebViewTest : Region() {
        val browser = WebView()
        val webEngine = browser.engine
        val latch = CountDownLatch(1)

        init {
            add(browser)
            styleClass.add("browser")
            webEngine.isJavaScriptEnabled = true

            webEngine.loadWorker.stateProperty().addListener { observableValue: ObservableValue<out Worker.State>, state: Worker.State, state1: Worker.State ->
                run {
                    if (state1 == Worker.State.SUCCEEDED) {
//                        val window = webEngine.executeScript("window") as JSObject
//                        val bridge = JavaBridge()
//                        window.setMember("java", bridge)
//                        webEngine.executeScript("""console.log = function(message) { java.log(message); }""")

                        loadAndExecuteResourceFromFile(Paths.get("/home/student/workspace/EarTraining/webscore/web/kotlin.js"))
                        loadAndExecuteResourceFromFile(Paths.get("/home/student/workspace/EarTraining/webscore/web/kotlinx-html-js.js"))
                        loadAndExecuteResourceFromFile(Paths.get("/home/student/workspace/EarTraining/webscore/web/kotlinx-serialization-runtime-js.js"))
                        loadAndExecuteResourceFromFile(Paths.get("/home/student/workspace/EarTraining/webscore/web/score-js.js"))
                        loadAndExecuteResourceFromFile(Paths.get("/home/student/workspace/EarTraining/webscore/web/webscore.js"))

                        latch.countDown()
                    }
                }
            }

            val content = Files.readAllLines(Paths.get("/home/student/workspace/EarTraining/webscore/index.html")).joinToString(separator = "")
            webEngine.loadContent(content)
        }


        private fun loadAndExecuteResource(resourcePath: String) {
            val jqueryCode = StringBuilder()
            javaClass.getResourceAsStream(resourcePath).use { inputStream ->
                InputStreamReader(inputStream).useLines {
                    it.forEach { jqueryCode.append(it).append("\n") }
                }
            }
            webEngine.executeScript(jqueryCode.toString())
        }

        private fun loadAndExecuteResourceFromFile(filePath: Path) {
            webEngine.executeScript(Files.readAllLines(filePath).joinToString(separator = ""))
        }


    }


}
