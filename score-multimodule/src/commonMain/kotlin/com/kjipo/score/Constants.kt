package com.kjipo.score


const val DEFAULT_STEM_HEIGHT = 100.0
const val DEFAULT_BAR_WIDTH = 1000
const val DEFAULT_BEAM_HEIGHT = 18
const val DEFAULT_VERTICAL_NOTE_SPACING = 12
const val DEFAULT_STEM_WIDTH = 2
const val TIE_STROKE_WIDTH = 2

const val TICKS_PER_QUARTER_NOTE = 24

const val START_NOTE_ELEMENT_MARGIN = 100

const val EXTRA_BAR_LINE_LEFT_PADDING = 15
const val EXTRA_BAR_LINE_RIGHT_PADDING = 15

const val TOP_MARGIN = 100
const val BOTTOM_MARGIN = 100
const val LEFT_MARGIN = 100
const val RIGHT_MARGIN = 100

const val STROKE_COLOUR = "black"
const val FILL_COLOUR = "black"


const val STEM_UP = "stem-up"
const val STEM_DOWN = "stem-down"

const val timeSignatureXOffset = 80.0
const val timeSignatureYOffset = -25.0


const val STEM_Y_OFFSET_STEM_UP = 12
const val STEM_Y_OFFSET_STEM_DOWN = 1

const val STEM_UP_STEM_X_OFFSET = 2


const val ELEMENT_ID = "elementId"


enum class Accidental {
    SHARP,
    FLAT
}

enum class Stem {
    NONE,
    UP,
    DOWN
}