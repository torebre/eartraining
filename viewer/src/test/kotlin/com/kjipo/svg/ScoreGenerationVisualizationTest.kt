package com.kjipo.svg

import com.kjipo.viewer.NoteView
import com.kjipo.viewer.NoteViewerWithScoreHandler
import com.kjipo.viewer.ScoreController
import com.kjipo.viewer.WebViewApplicationScoreHandlerApplication.Companion.startApplication
import org.junit.Test
import tornadofx.*
import java.nio.file.Paths


class ScoreGenerationVisualizationTest {


    @Test
    fun visualizeGeneratedScore() {
        val scoreGenerator = com.kjipo.scoregenerator.SequenceGenerator()

        startApplication()

        Thread.sleep(5000)
        println("Initialized: ${FX.initialized.value}")

        val renderingSequence = scoreGenerator.createNewSequence()

        val htmlPath = Paths.get("test_output2.html")
        writeToHtmlFile(renderingSequence, htmlPath)


        println("Pitch sequence:")
        scoreGenerator.pitchSequence.forEach { System.out.println(it) }

//        val noteViewerWithScoreHandler = FX.find(NoteViewerWithScoreHandler::class.java)
//        FX.runAndWait { noteViewerWithScoreHandler.load(scoreGenerator.scoreHandler) }

        val scoreController = FX.find(ScoreController::class.java)
        FX.runAndWait { scoreController.fireLoadScore(renderingSequence) }

        Thread.sleep(Long.MAX_VALUE)
    }


}