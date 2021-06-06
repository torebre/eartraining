import com.kjipo.handler.*
import com.kjipo.handler.BeamGroup
import com.kjipo.score.*
import com.kjipo.scoregenerator.ELEMENT_ID
import com.kjipo.scoregenerator.ReducedScore
import com.kjipo.scoregenerator.SimpleNoteSequence
import mu.KotlinLogging
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel

// Uncommenting the following will set up an empty webscore automatically when the Javascript in the built module is run
//val scoreHandler = ScoreHandlerJavaScript(ScoreHandler())
//val intialWebscore = WebScore(scoreHandler)


private val logger = KotlinLogging.logger {}


private fun showTie() {
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

    val bar = Bar().also {
        it.clef = Clef.G
        it.timeSignature = TimeSignature(4, 4)
    }

    val note1 = Note("test1", Duration.QUARTER, 5, NoteType.A_SHARP)
    val note2 = Note("test2", Duration.QUARTER, 5, NoteType.A)
    val notes = listOf(note1, note2)

    bar.scoreHandlerElements.addAll(notes)

    val score = Score()
    score.bars.add(bar)

    score.ties.add(Pair(note1, note2))

    val scoreHandler = ScoreHandlerWithReducedLogic(score)

    val webScore = WebScoreView(WebscoreSvgProvider(scoreHandler), "score")

}

private fun showBeam() {
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

    val bar = Bar().also {
        it.clef = Clef.G
        it.timeSignature = TimeSignature(4, 4)
    }

    val note1 = Note("test1", Duration.QUARTER, 5, NoteType.A_SHARP)
    val note2 = Note("test2", Duration.QUARTER, 5, NoteType.A)
    val notes = listOf(note1, note2)

    bar.scoreHandlerElements.addAll(notes)

    val score = Score()
    score.bars.add(bar)

    val beamGroup = BeamGroup(listOf(note1, note2))
    score.beamGroups.add(beamGroup)

    val scoreHandler = ScoreHandlerWithReducedLogic(score)

    val webScore = WebScoreView(WebscoreSvgProvider(scoreHandler), "score")
}

private fun showNoteGroupWithSharp() {
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

    val bar = Bar().also {
        it.clef = Clef.G
        it.timeSignature = TimeSignature(4, 4)
    }

    val note1 = NoteSymbol("test1", Duration.QUARTER, 5, NoteType.A_SHARP)
    val note2 = NoteSymbol("test2", Duration.QUARTER, 5, NoteType.C)
    val notes = listOf(note1, note2)

    val noteGroup = NoteGroup("testGroup", notes)

    bar.scoreHandlerElements.addAll(setOf(noteGroup))

    val score = Score()
    score.bars.add(bar)

    val scoreHandler = ScoreHandlerWithReducedLogic(score)

    val webScore = WebScoreView(WebscoreSvgProvider(scoreHandler), "score")
}

private fun showNotes() {
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

    val simpleNoteSequence = NoteType.values().mapIndexed { index, noteType ->
        NoteSequenceElement.NoteElement("test$index", noteType, 5, Duration.QUARTER, emptyMap())
    }.toList().let { SimpleNoteSequence(it) }

    val reducedScore = ReducedScore().apply {
        loadSimpleNoteSequence(simpleNoteSequence)
    }

    val webScore = WebScore(ScoreHandlerJavaScript(reducedScore), "score", true)
}

fun main() {
//showTie()
//showBeam()
//showNoteGroupWithSharp()
    showNotes()
}
