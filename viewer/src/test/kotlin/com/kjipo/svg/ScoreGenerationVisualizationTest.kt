package com.kjipo.svg

import com.kjipo.handler.ScoreHandler
import com.kjipo.viewer.NoteViewerWithScoreHandler
import com.kjipo.viewer.WebViewApplicationScoreHandlerApplication.Companion.startApplication
import org.junit.Test
import tornadofx.*


class ScoreGenerationVisualizationTest {


    @Test
    fun visualizeGeneratedScore() {
        val scoreGenerator = com.kjipo.scoregenerator.SequenceGenerator()

        startApplication()

        Thread.sleep(5000)
        println("Initialized: ${FX.initialized.value}")

        val noteViewerWithScoreHandler = FX.find(NoteViewerWithScoreHandler::class.java)

        scoreGenerator.createNewSequence()

        val scoreHandler = ScoreHandler()

        scoreHandler.scoreBuilder = scoreGenerator.scoreBuilder
        scoreHandler.updateScore()

        println("Pitch sequence:")
        scoreGenerator.pitchSequence.forEach { System.out.println(it) }

        FX.runAndWait { noteViewerWithScoreHandler.load(scoreHandler) }

        Thread.sleep(Long.MAX_VALUE)
    }


}