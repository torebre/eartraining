package graph

import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToInt


internal val SEMITONE_RATIO = (2.0).pow(1.0 / 12)
internal val PITCH_CLASSES = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

private val C2 = 440.0 * SEMITONE_RATIO.pow(-33)

internal val PITCH_CLASS_FREQUENCIES = generateSequence(C2) {
    it * SEMITONE_RATIO
}.map { it.toFloat() }.take(4 * 12).toList()


internal fun getPitchClosestToFrequency(frequency: Float): String? {
    val pitchClass = (12 * log2(frequency / C2)).roundToInt().mod(12)

    if (pitchClass < 0 || pitchClass >= PITCH_CLASSES.size) {
        return null
    }

    return PITCH_CLASSES[pitchClass]
}
