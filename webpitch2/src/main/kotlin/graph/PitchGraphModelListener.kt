package graph


interface PitchGraphModelListener {

    fun newPitchDataReceived(pitchData: PitchDataWithTime)

    fun uncertainPitchReceived(timestamp: Long)


}