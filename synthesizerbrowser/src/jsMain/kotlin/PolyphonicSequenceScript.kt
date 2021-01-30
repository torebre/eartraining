import com.github.aakira.napier.DebugAntilog
import com.github.aakira.napier.Napier
import com.kjipo.midi.SimplePitchEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

class PolyphonicSequenceScript(
    private val pitchEvents: List<Pair<Collection<SimplePitchEvent>, Int>>,
    private val midiPlayer: MidiPlayerInterface
) {

    suspend fun play() {
        console.log("All pitch events: ${pitchEvents}") //, tag = "Midi")

        Napier.base(DebugAntilog("test"))

        pitchEvents.forEach {
            val sleepTime = it.second


            console.log("Current pitch events: ${it.first}") //, tag = "Midi")
            Napier.i("Sleeping for $sleepTime milliseconds", tag = "Midi")

            try {
                delay(sleepTime.toLong())

                for (pitchEvent in it.first) {
                    if (pitchEvent.on) {
                        Napier.d("Pitch on: ${pitchEvent.pitch}", tag = "Midi")
                        midiPlayer.noteOn(pitchEvent.pitch)
                        Napier.d("On-message sent", tag = "Midi")
                    } else {
                        Napier.d("Pitch off: ${pitchEvent.pitch}", tag = "Midi")
                        midiPlayer.noteOff(pitchEvent.pitch)
                        Napier.d("Off-message sent", tag = "Midi")
                    }
                }

            } catch (e: CancellationException) {
                for (pitchEvent in it.first) {
                    midiPlayer.noteOff(pitchEvent.pitch)
                }
                Napier.d("Off-messages sent", tag = "Midi")
                throw e
            }
        }
    }
}