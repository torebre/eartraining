package graph

interface PitchGraphModelInterface {

    fun getPointsInRange(startRangeTimeStamp: Long, endRangeTimeStamp: Long): List<PitchDataWithTime>

    fun getTargetSequence(): List<PitchDataWithTime>

    fun toggleTargetSequenceShowing()

    fun getTargetSequencePointsInRange(startRangeTimeStamp: Long, endRangeTimeStamp: Long): List<PitchDataWithTime>
}