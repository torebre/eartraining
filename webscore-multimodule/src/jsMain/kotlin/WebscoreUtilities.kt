import com.kjipo.score.Duration


internal fun getDuration(value: Int): Duration? =
    when (value) {
        1 -> Duration.WHOLE
        2 -> Duration.HALF
        3 -> Duration.QUARTER
        4 -> Duration.EIGHT
        else -> null
    }
