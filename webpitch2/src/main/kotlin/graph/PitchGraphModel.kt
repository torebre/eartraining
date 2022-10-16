package graph

import PitchData
import PitchDetectionListener
import mu.KotlinLogging
import kotlin.js.Date

class PitchGraphModel : PitchDetectionListener {

    private val uncertaintyCutoff = 0.01


    private val receivedPitchData = mutableListOf<PitchDataWithTime>()
    private val pitchDataListeners = mutableListOf<PitchGraphModelListener>()

    private val logger = KotlinLogging.logger {}


    override fun pitchData(pitchData: PitchData) {
        val pitchDataWithTime = PitchDataWithTime(pitchData.pitch, pitchData.certainty, Date.now().toLong())
        receivedPitchData.add(pitchDataWithTime)

        if (pitchData.certainty < uncertaintyCutoff) {
            pitchDataListeners.forEach { it.uncertainPitchReceived(pitchDataWithTime.timeStamp) }
        } else {
            pitchDataListeners.forEach { it.newPitchDataReceived(pitchDataWithTime) }
        }
    }





    fun addPitchDataListener(pitchGraphModelListener: PitchGraphModelListener) =
        pitchDataListeners.add(pitchGraphModelListener)

    fun removePitchDataListener(pitchGraphModelListener: PitchGraphModelListener) =
        pitchDataListeners.remove(pitchGraphModelListener)


}