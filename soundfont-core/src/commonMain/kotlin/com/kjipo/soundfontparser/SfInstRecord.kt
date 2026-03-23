package com.kjipo.soundfontparser


/**
 * @property achInstName Name of the instrument expressed in ASCII.
 * @property wInstBagNdx Index to the instrument’s zone list in the IBAG sub-chunk.
 */
data class SfInstRecord(val achInstName: String, val wInstBagNdx: Int)
