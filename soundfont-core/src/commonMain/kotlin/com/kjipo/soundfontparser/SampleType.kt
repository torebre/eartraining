package com.kjipo.soundfontparser

enum class SampleType(val value: Int) {
    MONO_SAMPLE(1),
    RIGHT_SAMPLE(2),
    LEFT_SAMPLE(4),
    LINKED_SAMPLE(8),
    ROM_MONO_SAMPLE(32769),
    ROM_RIGHT_SAMPLE(32770),
    ROM_LEFT_SAMPLE(32772),
    ROM_LINKED_SAMPLE(32776);

    companion object {
        fun fromValue(value: Int): SampleType {
            return entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid sample type value: $value")
        }
    }
}