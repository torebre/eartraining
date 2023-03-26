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

    val bar = Bar(clef = Clef.G, timeSignature = TimeSignature(4, 4))

    val note11 = Note("test11", Duration.EIGHT, 5, NoteType.A_SHARP, stem = Stem.UP)
    val note12 = Note("test12", Duration.EIGHT, 5, NoteType.C, stem = Stem.UP)
    val note13 = Note("test13", Duration.EIGHT, 5, NoteType.G, stem = Stem.UP)
    val note14 = Note("test14", Duration.EIGHT, 5, NoteType.A, stem = Stem.UP)
    val rest1 = Rest("test3", Duration.HALF)
    val notes = listOf(note11, note12, note13, note14, rest1)

    bar.scoreHandlerElements.addAll(notes)

    val score = Score()
    score.bars.add(bar)

    val bar2 = Bar()

    val note3 = Note("test4", Duration.EIGHT, 5, NoteType.F, stem = Stem.DOWN)
    val note4 = Note("test5", Duration.EIGHT, 5, NoteType.E, stem = Stem.DOWN)
    val note5 = Note("test6", Duration.EIGHT, 5, NoteType.F, stem = Stem.DOWN)
    val note6 = Note("test7", Duration.EIGHT, 5, NoteType.G, stem = Stem.DOWN)
    val rest2 = Rest("test8", Duration.HALF)

    bar2.scoreHandlerElements.addAll(listOf(note3, note4, note5, note6, rest2))
    score.bars.add(bar2)

    val beamGroup = BeamGroup(listOf(BeamLine(1, listOf(note11, note12, note13, note14))))
    score.beamGroups.add(beamGroup)

    val beamGroup2 = BeamGroup(listOf(BeamLine(1, listOf(note3, note4, note5, note6))))
    score.beamGroups.add(beamGroup2)

    val bar3 = Bar()

    val note31 = Note("test31", Duration.EIGHT, 6, NoteType.F, stem = Stem.DOWN)
    val note32 = Note("test32", Duration.EIGHT, 5, NoteType.E, stem = Stem.UP)
    val note33 = Note("test33", Duration.EIGHT, 6, NoteType.F_SHARP, stem = Stem.UP)
    val note34 = Note("test34", Duration.EIGHT, 5, NoteType.G, stem = Stem.DOWN)
    val rest3 = Rest("test35", Duration.HALF)

    bar3.scoreHandlerElements.addAll(listOf(note31, note32, note33, note34, rest3))
    score.bars.add(bar3)

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
        val id = "test$index"
        NoteSequenceElement.NoteElement(id, noteType, 5, Duration.QUARTER, mapOf(Pair(ELEMENT_ID, id)))
    }.toList().let { SimpleNoteSequence(it) }

    val reducedScore = ReducedScore().apply {
        loadSimpleNoteSequence(simpleNoteSequence)
    }

    val webScore = WebScore(ScoreHandlerJavaScript(reducedScore), "score", true)
}


private fun showChordWithTwoSharps() {
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

    val bar = Bar().also {
        it.clef = Clef.G
        it.timeSignature = TimeSignature(4, 4)
    }

    val note1 = NoteSymbol("test1", Duration.QUARTER, 5, NoteType.A_SHARP)
    val note2 = NoteSymbol("test2", Duration.QUARTER, 6, NoteType.C_SHARP)
    val notes = listOf(note1, note2)
    val noteGroup = NoteGroup("testGroup", notes, stem = Stem.UP)

    val note3 = NoteSymbol("test3", Duration.QUARTER, 5, NoteType.F)
    val note4 = NoteSymbol("test4", Duration.QUARTER, 5, NoteType.A)
    val notes2 = listOf(note3, note4)
    val noteGroup2 = NoteGroup("testGroup", notes2, stem = Stem.DOWN)

    bar.scoreHandlerElements.addAll(setOf(noteGroup, noteGroup2))

    val score = Score()
    score.bars.add(bar)

    val scoreHandler = ScoreHandlerWithReducedLogic(score)

    val webScore = WebScoreView(WebscoreSvgProvider(scoreHandler), "score")
}


private fun showTieAcrossBars() {
    val score = Score()

    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

    val bar = Bar().apply {
        clef = Clef.G
        timeSignature = TimeSignature(4, 4)
    }

    val note1 = Note("test1", Duration.HALF, 5, NoteType.A, stem = Stem.UP)
    val rest1 = Rest("rest1", Duration.HALF)
    bar.scoreHandlerElements.addAll(listOf(rest1, note1))
    score.bars.add(bar)

    val bar2 = Bar()
    val note2 = Note("test2", Duration.WHOLE, 5, NoteType.A)
    bar2.scoreHandlerElements.addAll(listOf(note2))
    score.bars.add(bar2)

    val scoreHandler = ScoreHandlerWithReducedLogic(score)
    score.ties.add(Pair(note1, note2))

    val webScore = WebScoreView(WebscoreSvgProvider(scoreHandler), "score")
}

private fun showBeamIncludingChords() {
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

    val bar = Bar(clef = Clef.G, timeSignature = TimeSignature(4, 4))

    val note11 = NoteSymbol("test11", Duration.EIGHT, 5, NoteType.A_SHARP)
    val note12 = NoteSymbol("test12", Duration.EIGHT, 5, NoteType.C)
    val note13 = NoteSymbol("test13", Duration.EIGHT, 4, NoteType.G)
    val note14 = NoteSymbol("test14", Duration.EIGHT, 4, NoteType.A)
    val rest1 = Rest("test3", Duration.HALF)

    val noteGroup1 = NoteGroup("notegroup1", listOf(note11, note12), stem = Stem.UP)
    val noteGroup2 = NoteGroup("notegroup2", listOf(note13, note14), stem = Stem.UP)

    bar.scoreHandlerElements.addAll(setOf(noteGroup1, noteGroup2))

    val score = Score()
    score.bars.add(bar)

    val beamGroup = BeamGroup(listOf(BeamLine(1, listOf(noteGroup1, noteGroup2))))
    score.beamGroups.add(beamGroup)

    val scoreHandler = ScoreHandlerWithReducedLogic(score)

    val webScore = WebScoreView(WebscoreSvgProvider(scoreHandler), "score")
}

fun main() {
//showTie()
//    showBeam()
//showNoteGroupWithSharp()
//    showNotes()
//    showChordWithTwoSharps()
//    showTieAcrossBars()
    showBeamIncludingChords()
}
