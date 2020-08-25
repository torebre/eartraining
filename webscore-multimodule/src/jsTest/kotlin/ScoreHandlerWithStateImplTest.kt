import com.kjipo.handler.InsertNote
import com.kjipo.handler.ScoreHandler
import com.kjipo.handler.ScoreOperation
import com.kjipo.score.Duration
import kotlin.test.Test

class ScoreHandlerWithStateImplTest {


    @Test
    fun scoreHandlerDiffTest() {
        val scoreHandler = ScoreHandler()

        scoreHandler.insertNote(Duration.QUARTER)
        val renderingSequence1 = scoreHandler.build()
        val renderingSequence1AsString = scoreHandler.getScoreAsJson()

        scoreHandler.insertNote(Duration.QUARTER)
        val renderingSequence2 = scoreHandler.build()
        val renderingSequence2AsString = scoreHandler.getScoreAsJson()

//        val patch = createPatch(renderingSequence1AsString, renderingSequence2AsString)
//        println("Patch: $patch")

//        println("Test23: ${js("""semver.inc('1.2.3', 'prerelease', 'beta');""")}")
//        inc("1.2.3", "prerelease", "beta")

        console.log("Hello, Kotlin/JS!")
        console.log(sorted(arrayOf(1,2,3)))
        console.log(sorted(arrayOf(3,1,2)))


//        val objectMapper = ObjectMapper()
//        val renderingDiff = renderingDiff(renderingSequence2, renderingSequence1)

//        val originalRenderingSequence = objectMapper.valueToTree<JsonNode>(renderingSequence1)
//        val updatedSequenceAsJson = objectMapper.writeValueAsString(renderingSequence2)

//        val updateJson = JsonPatch.apply(renderingDiff, originalRenderingSequence)
//        val updatedJsonAsString = updateJson.toString()

//        Assert.assertEquals(updatedSequenceAsJson, updatedJsonAsString)


    }

    @Test
    fun scoreHandlerStateDiffTest() {
        val scoreHandler = ScoreHandler()
        val scoreHandlerState = ScoreHandlerWithStateImpl(scoreHandler)

        val stateDiff = scoreHandlerState.applyOperation(InsertNote(duration = Duration.QUARTER))

        println("State diff: $stateDiff")

        val stateDiff2 = scoreHandlerState.applyOperation(InsertNote(duration = Duration.QUARTER))

        println("State diff2: $stateDiff2")

    }



}