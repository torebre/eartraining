import com.kjipo.score.*

fun main(args: Array<String>) {
    val scoreData = ScoreSetup()
    var idCounter = 0

    val note1 = NoteElement(NoteType.C, 5, Duration.QUARTER, "note-$idCounter")
    ++idCounter
    val note2 = NoteElement(NoteType.D, 5, Duration.QUARTER, "note-$idCounter")
    note2.tie = note1.id
    note2.accidental = Accidental.SHARP
    ++idCounter
    val rest1 = RestElement(Duration.QUARTER, "rest-$idCounter")
    ++idCounter
    val rest2 = RestElement(Duration.QUARTER, "rest-$idCounter")

    scoreData.noteElements.add(note1)
    scoreData.noteElements.add(note2)
    scoreData.noteElements.add(rest1)
    scoreData.noteElements.add(rest2)
    scoreData.beams.add(BeamGroup(listOf(note1, note2)))

    val barData = BarData()
    barData.clef = Clef.G
    barData.scoreRenderingElements.add(note1)
    barData.scoreRenderingElements.add(note2)
    barData.scoreRenderingElements.add(rest1)
    barData.scoreRenderingElements.add(rest2)

    scoreData.bars.add(barData)

    WebScore(ScoreHandlerJavaScript(ScoreBuilderSequence(scoreData)))
}
