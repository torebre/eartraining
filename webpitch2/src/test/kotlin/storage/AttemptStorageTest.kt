package storage

import com.kjipo.attemptprocessor.InputAttempt
import com.kjipo.attemptprocessor.PitchData
import com.kjipo.score.Duration
import com.kjipo.scoregenerator.Pitch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AttemptStorageTest {

    @Test
    fun testInMemoryAttemptStorage() {
        val storage = InMemoryAttemptStorage()
        assertEquals(0, storage.getAttempts().size)

        val pitch1 = Pitch("1", 0, 1000, 60, Duration.QUARTER)
        val pitchData1 = PitchData(440.0f, 0.95f)
        val attempt1 = InputAttempt(listOf(pitch1), listOf(pitchData1))

        storage.saveAttempt(attempt1)
        val saved = storage.getAttempts()
        assertEquals(1, saved.size)
        assertEquals(1, saved[0].target.size)
        assertEquals(60, saved[0].target[0].pitch)
        assertEquals(Duration.QUARTER, saved[0].target[0].duration)
        assertEquals(1, saved[0].input.size)
        assertEquals(440.0f, saved[0].input[0].pitch)

        storage.clearAttempts()
        assertEquals(0, storage.getAttempts().size)
    }

    @Test
    fun testSerializationAndDeserialization() {
        val pitch1 = Pitch("p1", 0, 1000, 59, Duration.QUARTER)
        val pitch2 = Pitch("p2", 1000, 2000, 62, Duration.HALF)
        val input1 = PitchData(246.94f, 0.88f)
        val input2 = PitchData(293.66f, 0.92f)

        val attempt = InputAttempt(listOf(pitch1, pitch2), listOf(input1, input2))
        val json = LocalStorageAttemptStorage.serializeAttempts(listOf(attempt))
        assertTrue(json.contains("59"))
        assertTrue(json.contains("62"))

        val deserialized = LocalStorageAttemptStorage.deserializeAttempts(json)
        assertEquals(1, deserialized.size)
        assertEquals(2, deserialized[0].target.size)
        assertEquals("p1", deserialized[0].target[0].id)
        assertEquals(0, deserialized[0].target[0].timeOn)
        assertEquals(1000, deserialized[0].target[0].timeOff)
        assertEquals(59, deserialized[0].target[0].pitch)
        assertEquals(Duration.QUARTER, deserialized[0].target[0].duration)

        assertEquals("p2", deserialized[0].target[1].id)
        assertEquals(Duration.HALF, deserialized[0].target[1].duration)

        assertEquals(2, deserialized[0].input.size)
        assertEquals(246.94f, deserialized[0].input[0].pitch)
        assertEquals(0.88f, deserialized[0].input[0].certainty)
    }

    @Test
    fun testExportDataFormat() {
        val pitch = Pitch("1", 0, 1000, 60, Duration.QUARTER)
        val attempt = InputAttempt(listOf(pitch), listOf(PitchData(261.63f, 0.9f)))
        val exported = ExportedAttempts(APPLICATION_VERSION, listOf(attempt))

        assertEquals("0.4-SNAPSHOT", exported.version)
        assertEquals(1, exported.attempts.size)

        val jsonString = JSON.stringify(exported)
        assertTrue(jsonString.contains("0.4-SNAPSHOT"))
        assertTrue(jsonString.contains("version"))
        assertTrue(jsonString.contains("attempts"))
    }

    @Test
    fun testLocalStorageAttemptStorage() {
        val storage = LocalStorageAttemptStorage("test_webpitch_attempts")
        storage.clearAttempts()
        assertEquals(0, storage.getAttempts().size)

        val pitch = Pitch("p1", 0, 1000, 60, Duration.QUARTER)
        val attempt1 = InputAttempt(listOf(pitch), listOf(PitchData(261.63f, 0.9f)))
        val attempt2 = InputAttempt(listOf(pitch), listOf(PitchData(262.0f, 0.95f)))

        storage.saveAttempt(attempt1)
        assertEquals(1, storage.getAttempts().size)

        storage.saveAttempt(attempt2)
        val attempts = storage.getAttempts()
        assertEquals(2, attempts.size)
        assertEquals(261.63f, attempts[0].input[0].pitch)
        assertEquals(262.0f, attempts[1].input[0].pitch)

        storage.clearAttempts()
        assertEquals(0, storage.getAttempts().size)
    }

    @Test
    fun testLegacyFormatDeserialization() {
        val legacyJson = """
        [
          {
            "target": [
              {
                "id": "1",
                "timeOn": 0,
                "timeOff": 1000,
                "pitch": 59,
                "duration": {
                  "name$": "QUARTER",
                  "ordinal$": 2,
                  "ticks": 24
                }
              }
            ],
            "input": [
              {
                "pitch": 83.88,
                "certainty": 0.75
              }
            ]
          }
        ]
        """.trimIndent()

        val attempts = LocalStorageAttemptStorage.deserializeAttempts(legacyJson)
        assertEquals(1, attempts.size)
        assertEquals(1, attempts[0].target.size)
        assertEquals(59, attempts[0].target[0].pitch)
        assertEquals(Duration.QUARTER, attempts[0].target[0].duration)
        assertEquals(1, attempts[0].input.size)
        assertEquals(83.88f, attempts[0].input[0].pitch)
    }
}
