import com.github.aakira.napier.Napier
import com.kjipo.scoregenerator.Pitch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlin.jvm.Volatile

class MidiScript(pitchSequence: List<Pitch>, private val midiPlayer: MidiPlayerInterface) {
    private val pitchEvents = mutableListOf<PitchEvent>()

    init {
        for (note in pitchSequence) {
            pitchEvents.add(PitchEvent(note.id, note.timeOff, false, note.pitch))
            pitchEvents.add(PitchEvent(note.id, note.timeOn, true, note.pitch))
        }
        pitchEvents.sort()
    }

    suspend fun play() {
        var timeCounter = 0

        Napier.d("Pitch events: ${pitchEvents}", tag = "Midi")

        pitchEvents.forEach {
            val time = it.time
            val pitchEvent = it

            Napier.d("Pitch event: $it", tag = "Midi")
            Napier.d("Sleeping for " + time.minus(timeCounter).toLong() + " milliseconds", tag = "Midi")

            try {
                delay(time.minus(timeCounter).toLong())
                if (pitchEvent.on) {
                    Napier.d("Pitch on: ${pitchEvent.pitch}", tag = "Midi")
                    midiPlayer.noteOn(pitchEvent.pitch)
                    Napier.d("On-message sent", tag = "Midi")
                } else {
                    Napier.d("Pitch off: ${pitchEvent.pitch}", tag = "Midi")
                    midiPlayer.noteOff(pitchEvent.pitch)
                    Napier.d("Off-message sent", tag = "Midi")
                }
            } catch (e: CancellationException) {
                midiPlayer.noteOff(pitchEvent.pitch)
                Napier.d("Off-message sent", tag = "Midi")
                throw e
            }
            timeCounter = time

        }
    }

    private data class PitchEvent(val id: String, val time: Int, val on: Boolean, val pitch: Int) : Comparable<PitchEvent> {
        override fun compareTo(other: PitchEvent) = time.compareTo(other.time)
    }
}
