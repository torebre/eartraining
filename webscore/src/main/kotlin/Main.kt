import com.kjipo.score.*

fun main(args: Array<String>) {
    val scoreData = ScoreSetup()
    var idCounter = 0

    val note1 = NoteElement(NoteType.C, 5, Duration.HALF, 0, 0, "note-$idCounter")
    ++idCounter
    val note2 = NoteElement(NoteType.D, 5, Duration.HALF, 0, 0, "note-$idCounter")
    note2.tie = note1.id
    ++idCounter

    scoreData.noteElements.add(note1)
    scoreData.noteElements.add(note2)

    val barData = BarData()
    barData.clef = Clef.G
    barData.scoreRenderingElements.add(note1)
    barData.scoreRenderingElements.add(note2)

    scoreData.bars.add(barData)

    WebScore(ScoreHandlerJavaScript(ScoreBuilderSequence(scoreData)))
}
