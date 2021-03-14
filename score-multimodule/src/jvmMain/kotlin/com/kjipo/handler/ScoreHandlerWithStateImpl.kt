package com.kjipo.handler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonDiff
import com.kjipo.score.Duration


class ScoreHandlerWithStateImpl(private val scoreHandler: ScoreHandlerSplit) : ScoreHandlerWithState {
    private val objectMapper = ObjectMapper()

    private var currentRenderingTree: JsonNode? = null
    private var currentDiff: String? = null


    override fun applyOperation(operation: ScoreOperation): String? {
        when (operation) {
            is InsertNote -> {
                handleInsertNote(operation)


            }
            else -> {
                // TODO

            }

            // TODO


        }

        updateCurrentScoreAndGetDiff()
        return currentDiff
    }

    override fun getScoreAsJson() = scoreHandler.getScoreAsJson()

    override fun getIdOfFirstSelectableElement() = scoreHandler.getIdOfFirstSelectableElement()

    override fun getNeighbouringElement(activeElement: String, lookLeft: Boolean) = scoreHandler.getNeighbouringElement(activeElement, lookLeft)

    private fun handleInsertNote(insertNote: InsertNote) {
        // Default to quarter if nothing is set
        val duration = insertNote.duration ?: Duration.QUARTER
        if (insertNote.id != null) {
            scoreHandler.insertNote(insertNote.id, duration)
        }
        scoreHandler.insertNote(duration)
    }

    private fun updateCurrentScoreAndGetDiff(): String? {
        val scoreAsJson = scoreHandler.getScoreAsJson()
        val newTree = objectMapper.readTree(scoreAsJson)

        if (currentRenderingTree == null) {
            currentRenderingTree = newTree
            currentDiff = scoreAsJson
        } else {
            currentDiff = JsonDiff.asJson(currentRenderingTree, newTree).asText()
            currentRenderingTree = newTree
        }

        return currentDiff
    }
}