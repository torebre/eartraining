package com.kjipo.viewer

import com.kjipo.score.RenderingSequence
import com.kjipo.svg.createHtmlDocumentString
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker
import javafx.scene.layout.Region
import javafx.scene.web.WebView
import netscape.javascript.JSObject
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler


class NoteView : View("Note view"), ScoreControllerListener {
    val scoreController: ScoreController by inject()
    val webView: WebViewTest = WebViewTest()

    init {
        scoreController.addListener(this)
    }

    override fun load(renderingSequence: RenderingSequence) {
        webView.webEngine.loadContent(createHtmlDocumentString(renderingSequence))
    }

    override val root = stackpane {
        webView.style(true, {
            minWidth = Dimension(100.0, Dimension.LinearUnits.px)
            minHeight = Dimension(100.0, Dimension.LinearUnits.px)
        })

        add(webView)
    }

    override fun noteOn(noteId: Int) {
        webView.webEngine.executeScript("noteOn(\"note$noteId\");")
    }

    override fun noteOff(noteId: Int) {
        webView.webEngine.executeScript("noteOff(\"note$noteId\");")
    }

}


class JavaBridge {

    fun log(text: String) {
        println(text)
    }

}

class WebViewTest : Region() {
    val browser = WebView()
    val webEngine = browser.engine

    val LOGGER = LoggerFactory.getLogger(WebViewTest::class.java)


    init {

        add(browser)

        styleClass.add("browser")

        webEngine.isJavaScriptEnabled = true

        LOGGER.info("Loading class")

        webEngine.loadWorker.stateProperty().addListener({ observableValue: ObservableValue<out Worker.State>, state: Worker.State, state1: Worker.State ->
            run {
                if (state1 == Worker.State.SUCCEEDED) {
                    val window = webEngine.executeScript("window") as JSObject
                    val bridge = JavaBridge()

                    window.setMember("java", bridge)
                    webEngine.executeScript("""console.log = function(message) {
java.log(message);
}
""")
                    loadAndExecuteResource("/snap.svg-min.js")
                    loadAndExecuteResource("/js/score_functions.js")
                }
            }
        })

    }


    private fun loadAndExecuteResource(resourcePath: String) {
        val jqueryCode = StringBuilder()
        javaClass.getResourceAsStream(resourcePath).use {
            InputStreamReader(it).useLines {
                it.forEach { jqueryCode.append(it).append("\n") }
            }
        }
        webEngine.executeScript(jqueryCode.toString())
    }

}


class Handler : URLStreamHandler() {
    val classLoader: ClassLoader = javaClass.classLoader

    val LOGGER = LoggerFactory.getLogger(Handler::class.java)

    override fun openConnection(url: URL): URLConnection {
        val resource = if (url.getPath().startsWith("/")) url.getPath().substring(1) else url.getPath() ?: throw IOException("Resource not found: " + url)
        val resourceUrl = classLoader.getResource(resource)

        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("Found resource: {}. Resource URL: {}", url.getPath(), resourceUrl)
        }

        return resourceUrl.openConnection()
    }

}