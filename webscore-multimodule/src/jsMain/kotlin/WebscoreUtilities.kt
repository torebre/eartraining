import com.kjipo.score.Duration


internal fun getDuration(value: Int): Duration? =
    when (value) {
        1 -> Duration.WHOLE
        2 -> Duration.HALF
        3 -> Duration.QUARTER
        4 -> Duration.EIGHT
        else -> null
    }


fun <T> Array<T>.leftShift(positionsToShift: Int): Array<T> {
    val newList = this.copyOf()
    var shift = positionsToShift
    if (shift > size) shift %= size
    forEachIndexed { index, value ->
        val newIndex = (index + (size - shift)) % size
        newList[newIndex] = value
    }
    return newList
}
