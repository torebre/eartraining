import com.kjipo.score.*


val scoreHandler2 = ScoreHandlerJavaScript({
    val scoreData = ScoreSetup()
    var idCounter = 0
    val note1 = NoteElement(NoteType.C, 5, Duration.HALF)
    ++idCounter
    val note2 = NoteElement(NoteType.C, 5, Duration.HALF)
    ++idCounter

    scoreData.noteElements.add(note1)
    scoreData.noteElements.add(note2)

    val barData = BarData()
    barData.clef = Clef.G
    barData.scoreRenderingElements.add(note1)
    barData.scoreRenderingElements.add(note2)

    scoreData.bars.add(barData)

    ScoreBuilderSequence(scoreData)
}())