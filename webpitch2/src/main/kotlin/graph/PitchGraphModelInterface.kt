package graph

interface PitchGraphModelInterface {

    fun getPointsInRange(startRangeTimeStamp: Long, endRangeTimeStamp: Long): List<PitchDataWithTime>

}