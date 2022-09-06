import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import com.kjipo.scoregenerator.ELEMENT_ID
import com.kjipo.scoregenerator.ReducedScore
import com.kjipo.scoregenerator.SimpleNoteSequence
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse


class WebscoreTests {

    @Test
    fun changeSetTest() {
        val elementId = "note1"
        val noteSequenceElements =
            listOf(
                NoteSequenceElement.NoteElement(
                    elementId,
                    NoteType.A,
                    5,
                    Duration.QUARTER,
                    mapOf(Pair(ELEMENT_ID, elementId))
                )
            )

        val splitScoreHandler =
            ReducedScore().also { it.loadSimpleNoteSequence(SimpleNoteSequence(noteSequenceElements)) }
        val scoreHandlerJavaScript = ScoreHandlerJavaScript(splitScoreHandler)
        WebScore(scoreHandlerJavaScript, "scaleTest", false)

        var receivedUpdateId = -1
        scoreHandlerJavaScript.addListener(object : ScoreHandlerListener {
            override fun scoreUpdated(updateId: Int) {
                receivedUpdateId = updateId
            }
        })

        val latestId = scoreHandlerJavaScript.getLatestId()
        scoreHandlerJavaScript.moveNoteOneStep(elementId, true)
        assertEquals(receivedUpdateId, latestId + 1)

        val changeSet = scoreHandlerJavaScript.getChangeSet(latestId)!!
        assertFalse {
            changeSet.renderGroupUpdates.isEmpty()
        }
    }

}