import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import com.kjipo.scoregenerator.ReducedScore
import com.kjipo.scoregenerator.SimpleNoteSequence
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel
import kotlin.test.Test


class BarLineTest {


    @Test
    fun testBarLines() {
        KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

        val simpleNoteSequence = NoteType.values().mapIndexed { index, noteType ->
            NoteSequenceElement.NoteElement("test$index", noteType, 5, Duration.QUARTER, emptyMap())
        }.toList().let { SimpleNoteSequence(it) }

        val reducedScore = ReducedScore().apply {
            loadSimpleNoteSequence(simpleNoteSequence)
        }

        reducedScore.getScoreAsJson()
    }


}