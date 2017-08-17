package com.kjipo.svg

import com.kjipo.font.SvgTools
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.web.WebView
import netscape.javascript.JSObject
import org.slf4j.LoggerFactory
import tornadofx.*
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory


class NoteView : View("Note view") {
    val webView = WebViewTest()

    companion object {
        lateinit var instance: NoteView
    }

    init {
        instance = this
    }

    override val root = stackpane {
        webView.style(true, {
            minWidth = Dimension(100.0, Dimension.LinearUnits.px)
            minHeight = Dimension(100.0, Dimension.LinearUnits.px)
            borderColor += box(top = Color.RED, bottom = Color.GREEN, left = Color.YELLOW, right = Color.BLUE)
        })


        add(webView)
    }

    fun setFill(id: Int) {
        Platform.runLater {


            //            val script = """$("#${id}").attr("fill", "red");"""

//            val script = """$("path").find("*").attr("fill", "red");"""

//            val script = """$("body").css("border", "9px solid red");"""

//   val script = """$($.parseHTML("<h1>This is a test</h1>")).appendTo("BODY");
            val script =
                    """
            console.log("Test20");
$("html").find("*").each(function(index) {
console.log("Index: " +index +". This: " +this);
});
"""

            println("Setting fill: " + script)

            webView.webEngine.executeScript(script)

            val path = Paths.get("webview_contents.html")
            SvgTools.writeDocumentToFile(webView.webEngine.document, path)


        }
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

        webEngine.load("classpath:///test_output3.html")

        val jqueryCode = StringBuilder()
        javaClass.getResourceAsStream("/jquery.js").use {
            InputStreamReader(it).useLines {
                it.forEach { jqueryCode.append(it).append("\n") }
            }
        }

        webEngine.executeScript(jqueryCode.toString())

        webEngine.loadWorker.stateProperty().addListener({
            observableValue: ObservableValue<out Worker.State>, state: Worker.State, state1: Worker.State ->
            run {
                val window = webEngine.executeScript("window") as JSObject
                val bridge = JavaBridge()

                window.setMember("java", bridge)
                webEngine.executeScript("""console.log = function(message) {
java.log(message);
}
""")
            }
        })
    }

}


class Handler : URLStreamHandler() {
    val classLoader: ClassLoader = javaClass.classLoader

    val LOGGER = LoggerFactory.getLogger(Handler::class.java)

    override fun openConnection(url: URL): URLConnection {
        val resource = if (url.getPath().startsWith("/")) url.getPath().substring(1) else url.getPath() ?: throw IOException("Resource not found: " + url)
        val resourceUrl = classLoader.getResource(resource)

        LOGGER.info("Found resource: {}. Resource URL: {}", url.getPath(), resourceUrl)

        return resourceUrl.openConnection()
    }


}