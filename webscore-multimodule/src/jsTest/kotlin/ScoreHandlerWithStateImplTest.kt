import com.kjipo.handler.InsertNote
import com.kjipo.handler.ScoreHandler
import com.kjipo.handler.ScoreOperation
import com.kjipo.score.Duration
import kotlin.coroutines.coroutineContext
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

        val currentScore = scoreHandlerState.getScoreAsJson()
        val stateDiff2 = scoreHandlerState.applyOperation(InsertNote(duration = Duration.QUARTER))

        println("State diff2: $stateDiff2")

        println("Score before applying patch: $currentScore")
        stateDiff2?.apply {
            println("Applying patch")

//            val result = rfc6902.applyPatch(currentScore, stateDiff2)
//            println("Result of applying patch: $result")

//            println("Score after patch: $currentScore")
        }


    }

    class Person {
        var first: String? = ""

    }

    @Test
    fun patchTest() {
        var oldData = "{ \"first\": \"Chris\" }"
        var parsedObject = JSON.parse<Person>(oldData)
        var newDataObject = JSON.parse<Any>("""{"first": "Chris", "last": "Brown"}""")

        val result = rfc6902.createPatch(parsedObject, newDataObject)

        println("Operations:")
        for(operation in result) {
            println("Operation: ${operation.op}")
        }

        println("Data before applying patch: ${JSON.stringify(parsedObject)}")
        val patchOperationResult = rfc6902.applyPatch(parsedObject, result)

        println("Data after applying patch: ${JSON.stringify(parsedObject)}")

    }



}