package graph

import PitchData
import PitchDetectionListener
import mu.KotlinLogging
import kotlin.js.Date

open class PitchGraphModel : PitchGraphModelInterface, PitchDetectionListener {

    private val uncertaintyCutoff = 0.01

    private var idCounter = 0


    private val receivedPitchData = mutableListOf<PitchDataWithTime>()
    private val pitchDataListeners = mutableListOf<PitchGraphModelListener>()

    private val logger = KotlinLogging.logger {}


    override fun pitchData(pitchData: PitchData) {
        addPitchDataWithTime(PitchDataWithTime(pitchData.pitch, pitchData.certainty, Date.now().toLong(), idCounter++))
    }

    protected fun addPitchDataWithTime(pitchDataWithTime: PitchDataWithTime) {
        receivedPitchData.add(pitchDataWithTime)

        if (pitchDataWithTime.certainty < uncertaintyCutoff) {
            pitchDataListeners.forEach { it.uncertainPitchReceived(pitchDataWithTime.timeStamp) }
        } else {
            pitchDataListeners.forEach { it.newPitchDataReceived(pitchDataWithTime) }
        }
    }

    override fun getPointsInRange(startRangeTimeStamp: Long, endRangeTimeStamp: Long): List<PitchDataWithTime> {
        // TODO This can be made more efficient if the underlying data structure can make use a time index
        return receivedPitchData.filter { pitchDataWithTime ->
            pitchDataWithTime.timeStamp in (startRangeTimeStamp + 1) until endRangeTimeStamp
        }

    }


    fun addPitchDataListener(pitchGraphModelListener: PitchGraphModelListener) =
        pitchDataListeners.add(pitchGraphModelListener)

    fun removePitchDataListener(pitchGraphModelListener: PitchGraphModelListener) =
        pitchDataListeners.remove(pitchGraphModelListener)


}