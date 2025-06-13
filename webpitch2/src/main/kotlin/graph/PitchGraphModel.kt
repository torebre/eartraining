package graph

import com.kjipo.attemptprocessor.PitchData
import PitchDetectionListener
import mu.KotlinLogging
import kotlin.js.Date

open class PitchGraphModel : PitchGraphModelInterface, PitchDetectionListener {

    private val uncertaintyCutoff = 0.01

    private var idCounter = 0
    private var targetSequenceShowing = false

    private val receivedPitchData = mutableListOf<PitchDataWithTime>()
    private val pitchDataListeners = mutableListOf<PitchGraphModelListener>()

    private var targetSequence = listOf<PitchDataWithTime>()

    private var startTime: Long? = null

    private val logger = KotlinLogging.logger {}


    override fun pitchData(pitchData: PitchData) {
        val timeNow = Date.now().toLong()
        if(startTime == null) {
            startTime = timeNow
        }

        startTime?.let {
            addPitchDataWithTime(PitchDataWithTime(pitchData.pitch,
                pitchData.certainty,
                timeNow - it,
                idCounter++))
        }
    }

    fun reset(targetSequence: List<PitchDataWithTime>) {
        receivedPitchData.clear()
        targetSequenceShowing = false
        this.targetSequence = targetSequence
        startTime = null
        idCounter = 0

        pitchDataListeners.forEach { it.reset() }
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

    override fun getTargetSequencePointsInRange(startRangeTimeStamp: Long, endRangeTimeStamp: Long): List<PitchDataWithTime> {
        // TODO This can be made more efficient if the underlying data structure can make use a time index
        return getTargetSequence().filter { pitchDataWithTime ->
            pitchDataWithTime.timeStamp in (startRangeTimeStamp + 1) until endRangeTimeStamp
        }

    }

    override fun getTargetSequence(): List<PitchDataWithTime> {
        return targetSequence
    }

    override fun toggleTargetSequenceShowing() {
        targetSequenceShowing = !targetSequenceShowing
        pitchDataListeners.forEach { it.targetSequenceShowing(targetSequenceShowing) }
    }

    fun addPitchDataListener(pitchGraphModelListener: PitchGraphModelListener) =
        pitchDataListeners.add(pitchGraphModelListener)

    fun removePitchDataListener(pitchGraphModelListener: PitchGraphModelListener) =
        pitchDataListeners.remove(pitchGraphModelListener)


}