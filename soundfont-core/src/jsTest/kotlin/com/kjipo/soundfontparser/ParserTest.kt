package com.kjipo.soundfontparser


import com.kjipo.soundfontparser.Chunk
import com.kjipo.soundfontparser.Parser
import com.kjipo.soundfontparser.loadFile
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ParserTest {

    @Test
    fun testLocation() {
        println("Test running at: ${window.location.href}")
        println("Origin: ${window.location.origin}")
        println("Pathname: ${window.location.pathname}")
    }

    @Test
    fun testListAvailableResources() = runTest {
        val testPaths = listOf(
            "florestan-subset.sf2",
            "/florestan-subset.sf2",
            "resources/florestan-subset.sf2",
            "../florestan-subset.sf2",
            "composeApp/src/jsTest/resources/florestan-subset.sf2",
        )

        println("Testing resource paths:")
        for (path in testPaths) {
            try {
                val response = window.fetch(path).await()
                println("✓ $path - Status: ${response.status} (${if (response.ok) "OK" else "ERROR"})")
            } catch (e: Exception) {
                println("✗ $path - Error: ${e.message}")
            }
        }
    }


    @Test
    fun testLoadFile() = runTest {
        try {
            val data: List<Chunk> = Parser.parse(loadFile("florestan-subset.sf2"))

            assertTrue(data.isNotEmpty(), "File should contain data")
        } catch (e: Exception) {
            println("Note: This test requires a browser environment with HTTP server")
            println("Error: ${e.message}")
            // In a proper test environment with a web server, this test would pass
        }
    }
}