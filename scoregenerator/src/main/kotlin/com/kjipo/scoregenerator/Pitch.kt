package com.kjipo.scoregenerator

import com.kjipo.score.Duration


data class Pitch(val id: String, var timeOn: Int, var timeOff: Int, var pitch: Int, var duration: Duration)