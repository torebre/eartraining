import com.kjipo.scoregenerator.Pitch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import mu.KotlinLogging

class MidiScript(pitchSequence: List<Pitch>, private val midiPlayer: MidiPlayerInterface) {
    private val pitchEvents = mutableListOf<PitchEvent>()

    private val logger = KotlinLogging.logger {}

    init {
        for (note in pitchSequence) {
            pitchEvents.add(PitchEvent(note.id, note.timeOff, false, note.pitch))
            pitchEvents.add(PitchEvent(note.id, note.timeOn, true, note.pitch))
        }
        pitchEvents.sort()
    }

    suspend fun play() {
        var timeCounter = 0

        logger.debug { "Pitch events: ${pitchEvents}" }

        pitchEvents.forEach {
            val time = it.time
            val pitchEvent = it

            logger.debug { "Pitch event: $it" }
            logger.debug { "Sleeping for " + time.minus(timeCounter).toLong() + " milliseconds" }

            try {
                delay(time.minus(timeCounter).toLong())
                if (pitchEvent.on) {
                    logger.debug { "Pitch on: ${pitchEvent.pitch}" }
                    midiPlayer.noteOn(pitchEvent.pitch)
                    logger.debug { "On-message sent" }
                } else {
                    logger.debug { "Pitch off: ${pitchEvent.pitch}" }
                    midiPlayer.noteOff(pitchEvent.pitch)
                    logger.debug { "Off-message sent" }
                }
            } catch (e: CancellationException) {
                midiPlayer.noteOff(pitchEvent.pitch)
                logger.debug { "Off-message sent" }
                throw e
            }
            timeCounter = time

        }
    }

    private data class PitchEvent(val id: String, val time: Int, val on: Boolean, val pitch: Int) :
        Comparable<PitchEvent> {
        override fun compareTo(other: PitchEvent) = time.compareTo(other.time)
    }
}
