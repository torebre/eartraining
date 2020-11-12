//@file:UseSerializers(ScoreHandlerWithStateImplTest.DoubleDecimalPointsSerializer::class)

import com.kjipo.handler.InsertNote
import com.kjipo.handler.ScoreHandler
import com.kjipo.handler.ScoreOperation
import com.kjipo.score.Duration
import com.kjipo.svg.processCurveToRelative
import kotlinx.html.currentTimeMillis
import kotlinx.serialization.KSerializer
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import rfc6902.Operation
import kotlin.coroutines.coroutineContext
import kotlin.math.nextDown
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

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

        if (stateDiff2 == null) {
            fail("No diff received after applying operation")
        }

        println("State diff2: $stateDiff2")
        println("Score before applying patch: $currentScore")
        println("Applying patch")

        val patchOperations = JSON.parse<Array<Operation>>(stateDiff2)
        val currentScoreParsed = JSON.parse<Any>(currentScore)
        val result = rfc6902.applyPatch(currentScoreParsed, patchOperations)
        println("Result of applying patch: $result")

        val updatedScore = JSON.stringify(currentScoreParsed)
        println("Score after patch: $updatedScore")

        val updatedScoreFromHandler = scoreHandler.getScoreAsJson()

        println("Updated score from handler: $updatedScoreFromHandler")

        assertEquals(updatedScoreFromHandler, updatedScore)
    }


    @Test
    fun patchTest() {
        val oldData = "{ \"first\": \"Chris\" }"
        val parsedObject = JSON.parse<Any>(oldData)
        val newDataObject = JSON.parse<Any>("""{"first": "Chris", "last": "Brown"}""")

        val result = rfc6902.createPatch(parsedObject, newDataObject)

        println("Operations:")
        for (operation in result) {
            println("Operation: ${operation.op}")
        }

        println("Data before applying patch: ${JSON.stringify(parsedObject)}")
        // The operations are applied to parsedObject in place
        val patchOperationResult = rfc6902.applyPatch(parsedObject, result)

        println("Data after applying patch: ${JSON.stringify(parsedObject)}")

        assertEquals(JSON.stringify(newDataObject), JSON.stringify(parsedObject))
    }

    @Test
    fun serializationTest() {
        val scoreHandler = ScoreHandler()
        val scoreHandlerState = ScoreHandlerWithStateImpl(scoreHandler)

        val stateDiff = scoreHandlerState.applyOperation(InsertNote(duration = Duration.QUARTER))

        println("State diff: $stateDiff")

        val currentScore = scoreHandlerState.getScoreAsJson()

        println("Current score: $currentScore")

//        val renderingSequence = scoreHandler.build()

//        val renderingSequenceJson = Json.encodeToString(DoubleDecimalPointsSerializer, renderingSequence)
//        println("Rendering sequence JSON: $renderingSequenceJson")

//        Json.encodeToString(Json.encodeToString(DoubleDecimalPointsSerializer, scoreHandler.))


    }

}