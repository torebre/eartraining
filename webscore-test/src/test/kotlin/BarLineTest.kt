import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import com.kjipo.scoregenerator.ReducedScore
import com.kjipo.scoregenerator.SimpleNoteSequence
import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration
import io.github.oshai.kotlinlogging.Level
import kotlin.test.Test


class BarLineTest {


    @Test
    fun testBarLines() {
        KotlinLoggingConfiguration.logLevel = Level.DEBUG

        val simpleNoteSequence = NoteType.values().mapIndexed { index, noteType ->
            NoteSequenceElement.NoteElement("test$index", noteType, 5, Duration.QUARTER, emptyMap())
        }.toList().let { SimpleNoteSequence(it) }

        val reducedScore = ReducedScore().apply {
            loadSimpleNoteSequence(simpleNoteSequence)
        }

        reducedScore.getScoreAsJson()
    }


}