// Uncommenting the following will set up an empty webscore automatically when the Javascript in the built module is run
//val scoreHandler = ScoreHandlerJavaScript(ScoreHandler())
//val intialWebscore = WebScore(scoreHandler)


// If this is commented in it will run when the webscore-multimodule module is included in other modules

/*
fun main() {
    val elementId = "test1"
    val noteSequence = listOf(
        NoteSequenceElement.NoteElement(
            elementId, NoteType.G, 5, Duration.QUARTER, mapOf(
                Pair(
                    ELEMENT_ID, elementId
                )
            )
        )
    )
    val score = ScoreElementsTranslator.createRenderingData(noteSequence)
    val scoreHandler = ScoreHandlerWithReducedLogic(score)

//    val webScore = WebScoreView(WebscoreSvgProvider(scoreHandler), "score")

    // Do nothing as default now

//    val scoreHandler = ScoreHandler()

//    val note1 = scoreHandler.insertNote(Duration.EIGHT)
//    val note2 = scoreHandler.insertNote(Duration.EIGHT)
//    val note3 = scoreHandler.insertNote(Duration.QUARTER)
//    val note4 = scoreHandler.insertNote(Duration.QUARTER)
//    val note5 = scoreHandler.insertNote(Duration.QUARTER)

//    WebScore(ScoreHandlerJavaScript(scoreHandler))

//    addNoteGroup()
}
*/