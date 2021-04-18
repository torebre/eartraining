import com.kjipo.score.Duration


internal fun getDuration(keyPressed: Int): Duration =
    when (keyPressed) {
        1 -> Duration.QUARTER
        2 -> Duration.HALF
        3 -> Duration.WHOLE
        else -> Duration.QUARTER
    }
