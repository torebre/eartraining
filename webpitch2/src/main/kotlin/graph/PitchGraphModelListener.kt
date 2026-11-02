package graph


interface PitchGraphModelListener {

    fun newPitchDataReceived(pitchData: PitchDataWithTime)

    fun uncertainPitchReceived(timestamp: Long)

    fun targetSequenceShowing(isShowing: Boolean)

    fun reset()

    fun pitchDataCleared()

    fun referencePitchChanged(referencePitch: Float?)

    fun darkModeChanged(isDarkMode: Boolean)

}