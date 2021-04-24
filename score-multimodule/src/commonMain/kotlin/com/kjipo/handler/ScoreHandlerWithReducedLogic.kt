package com.kjipo.handler

import com.kjipo.score.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging

/**
 * Stores a sequence of temporal elements, and can produce a score based on them.
 */
class ScoreHandlerWithReducedLogic(score: Score) : ScoreProviderInterface {

    var score: Score = score
        set(value) {
            field = value
            cachedBuild = null
            build()
        }

    private var scoreSetup = ScoreSetup(Score())

    private var cachedBuild: RenderingSequenceWithMetaData? = null

    private val logger = KotlinLogging.logger {}

    override fun getScoreAsJson() =
        truncateNumbers(
            Json.encodeToString(
                RenderingSequence.serializer(),
                cachedBuild?.renderingSequence ?: build().renderingSequence
            )
        )

    override fun getHighlightMap() = cachedBuild?.highlightElementsMap ?: build().highlightElementsMap

    fun getHighlightableElements() = filterHighlightableElements(
        if (cachedBuild != null) {
            scoreSetup
        } else {
            build()
            scoreSetup
        }
    )

    private fun filterHighlightableElements(scoreSetup: ScoreSetup) =
        scoreSetup.scoreRenderingElements.filter { it is HighlightableElement }.toList()


    private fun build(): RenderingSequenceWithMetaData {
        scoreSetup = ScoreSetup(score)

        return scoreSetup.buildWithMetaData().also { cachedBuild = it }
    }

    private fun addBeams(barData: BarData): MutableList<BeamGroup> {
        val beamGroups = mutableListOf<BeamGroup>()
        val notesInBeamGroup = mutableListOf<NoteElement>()

        for (scoreRenderingElement in barData.scoreRenderingElements) {
            if (scoreRenderingElement is NoteElement && scoreRenderingElement.duration == Duration.EIGHT) {
                notesInBeamGroup.add(scoreRenderingElement)
            } else if (scoreRenderingElement is RestElement) {
                // Some other note element or a rest
                if (notesInBeamGroup.size > 1) {
                    notesInBeamGroup.forEach { it.partOfBeamGroup = true }
                    beamGroups.add(BeamGroup(notesInBeamGroup.map { it.id }))
                }
                notesInBeamGroup.clear()
            }
        }
        return beamGroups
    }

}
