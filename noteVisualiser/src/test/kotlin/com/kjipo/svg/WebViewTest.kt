package com.kjipo.svg

import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker
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
            val script2 = """
                var s2 = Snap(1000, 1000);
// Lets create big circle in the middle:
var bigCircle = s2.circle(10, 10, 1000);
bigCircle.attr({
    fill: "#bada55",
    stroke: "#000",
    strokeWidth: 5
});

                var s = Snap.select("#note1");
//                console.log("path: " +s);
              s.attr({
                fill: "red"
                });

                """

            webView.webEngine.executeScript(script2)
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

//        loadAndExecuteResource("/snap.svg-min.js")


//        loadAndExecuteResource("/jquery.js")
//        loadAndExecuteResource("/jquery.svg.min.js")
//        loadAndExecuteResource("/jquery.svgdom.min.js")



        webEngine.loadWorker.stateProperty().addListener({
            observableValue: ObservableValue<out Worker.State>, state: Worker.State, state1: Worker.State ->
            run {

                LOGGER.info("Test23. Setting logger")

                val window = webEngine.executeScript("window") as JSObject
                val bridge = JavaBridge()

                window.setMember("java", bridge)
                webEngine.executeScript("""console.log = function(message) {
java.log(message);
}
""")
            }
        })

        webEngine.load("classpath:///test_output3.html")


//        val window = webEngine.executeScript("window") as JSObject
//        val bridge = JavaBridge()
//        window.setMember("java", bridge)
//        webEngine.executeScript("""console.log = function(message) {
//java.log(message);
//}
//""")
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

        LOGGER.info("Found resource: {}. Resource URL: {}", url.getPath(), resourceUrl)

        return resourceUrl.openConnection()
    }


}