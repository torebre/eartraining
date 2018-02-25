package com.kjipo.font


import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths


class PathProcessorTest {

    @Test
    fun `test cubic Bezier curve generation`() {
        val lineSegments = processPath(FontProcessingUtilities.parsePathData("M359.403 1080.91c-7.621 -2.80005 -37.638 -31.6801 -54.954 -52.88c-56.862 -69.564 -87.028 -156.64 -87.028 -247.587"))

        lineSegments.forEach(System.out::println)
    }

    @Test
    fun `test line segment generation`() {
        val lineSegments = Files.newInputStream(Paths.get("/home/student/workspace/EarTraining/noteGenerator/src/main/resources/gonville-r9313/lilyfonts/svg/gonvillepart1.svg"))
                .use { ReadFonts.extractGlyphFromFile("clefs.G", it) }
        lineSegments.forEach(System.out::println)
    }

}
