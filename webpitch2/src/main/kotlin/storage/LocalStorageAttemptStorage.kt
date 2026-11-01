package storage

import com.kjipo.attemptprocessor.InputAttempt
import com.kjipo.attemptprocessor.PitchData
import com.kjipo.score.Duration
import com.kjipo.scoregenerator.Pitch
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.browser.localStorage

class LocalStorageAttemptStorage(private val storageKey: String = STORAGE_KEY) : AttemptStorage {

    private val logger = KotlinLogging.logger {}

    companion object {
        const val STORAGE_KEY = "webpitch_saved_attempts"

        fun attemptToJson(attempt: InputAttempt): dynamic {
            val targetArr = attempt.target.map { pitch ->
                val pitchObj = js("{}")
                pitchObj["id"] = pitch.id
                pitchObj["timeOn"] = pitch.timeOn
                pitchObj["timeOff"] = pitch.timeOff
                pitchObj["pitch"] = pitch.pitch
                pitchObj["duration"] = pitch.duration.name
                pitchObj
            }.toTypedArray()

            val inputArr = attempt.input.map { input ->
                val inputObj = js("{}")
                inputObj["pitch"] = input.pitch
                inputObj["certainty"] = input.certainty
                inputObj
            }.toTypedArray()

            val obj = js("{}")
            obj["target"] = targetArr
            obj["input"] = inputArr
            return obj
        }

        fun serializeAttempts(attempts: List<InputAttempt>): String {
            val attemptsArray = attempts.map { attemptToJson(it) }.toTypedArray()
            return JSON.stringify(attemptsArray)
        }

        fun deserializeAttempt(rawAttempt: dynamic): InputAttempt {
            val target = mutableListOf<Pitch>()
            val rawTarget = when {
                rawAttempt.target == null -> null
                js("Array.isArray(rawAttempt.target)") as Boolean -> rawAttempt.target as Array<dynamic>
                rawAttempt.target.array != null -> rawAttempt.target.array as Array<dynamic>
                rawAttempt.target._array != null -> rawAttempt.target._array as Array<dynamic>
                rawAttempt.target.toArray != null -> rawAttempt.target.toArray() as Array<dynamic>
                else -> null
            }
            if (rawTarget != null) {
                for (rawPitch in rawTarget) {
                    if (rawPitch == null) continue
                    val id = (rawPitch.id as? String) ?: ""
                    val timeOn = ((rawPitch.timeOn as? Number)?.toInt()) ?: 0
                    val timeOff = ((rawPitch.timeOff as? Number)?.toInt()) ?: 0
                    val pitch = ((rawPitch.pitch as? Number)?.toInt()) ?: 0

                    val rawDuration = rawPitch.duration
                    val durationName = when {
                        rawDuration == null -> "QUARTER"
                        jsTypeOf(rawDuration) == "string" -> rawDuration as String
                        rawDuration["name"] != null -> rawDuration["name"] as String
                        rawDuration["name$"] != null -> rawDuration["name$"] as String
                        rawDuration["_name"] != null -> rawDuration["_name"] as String
                        else -> "QUARTER"
                    }
                    val duration = try {
                        Duration.valueOf(durationName)
                    } catch (e: Exception) {
                        Duration.QUARTER
                    }
                    target.add(Pitch(id, timeOn, timeOff, pitch, duration))
                }
            }

            val input = mutableListOf<PitchData>()
            val rawInput = when {
                rawAttempt.input == null -> null
                js("Array.isArray(rawAttempt.input)") as Boolean -> rawAttempt.input as Array<dynamic>
                rawAttempt.input.array != null -> rawAttempt.input.array as Array<dynamic>
                rawAttempt.input._array != null -> rawAttempt.input._array as Array<dynamic>
                rawAttempt.input.toArray != null -> rawAttempt.input.toArray() as Array<dynamic>
                else -> null
            }
            if (rawInput != null) {
                for (rawPitchData in rawInput) {
                    if (rawPitchData == null) continue
                    val pitch = ((rawPitchData.pitch as? Number)?.toFloat()) ?: 0.0f
                    val certainty = ((rawPitchData.certainty as? Number)?.toFloat()) ?: 0.0f
                    input.add(PitchData(pitch, certainty))
                }
            }

            return InputAttempt(target, input)
        }

        fun deserializeAttempts(jsonString: String): List<InputAttempt> {
            val rawList = JSON.parse<Array<dynamic>>(jsonString)
            val attempts = mutableListOf<InputAttempt>()
            for (rawAttempt in rawList) {
                if (rawAttempt != null) {
                    attempts.add(deserializeAttempt(rawAttempt))
                }
            }
            return attempts
        }
    }

    override fun saveAttempt(attempt: InputAttempt) {
        val currentAttempts = getAttempts().toMutableList()
        currentAttempts.add(attempt)
        val jsonString = serializeAttempts(currentAttempts)
        localStorage.setItem(storageKey, jsonString)
        logger.info { "Saved attempt to localStorage. Total attempts: ${currentAttempts.size}" }
    }

    override fun getAttempts(): List<InputAttempt> {
        val storedJson = localStorage.getItem(storageKey)
        if (storedJson.isNullOrBlank()) {
            return emptyList()
        }
        return try {
            deserializeAttempts(storedJson)
        } catch (e: Exception) {
            logger.error(e) { "Failed to deserialize attempts from localStorage" }
            emptyList()
        }
    }

    override fun clearAttempts() {
        localStorage.removeItem(storageKey)
    }
}
