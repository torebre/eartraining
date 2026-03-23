package com.kjipo.soundfontparser


/**
 * Represents a preset header record from the PHDR sub-chunk of a SoundFont file.
 *
 * @property achPresetName Preset name (20 ASCII chars, null-terminated). Names are case-sensitive and should be unique.
 * @property wPreset MIDI Preset Number (0-127). Identifies the preset within the bank.
 * @property wBank MIDI Bank Number (0-127, or 128 for General MIDI percussion bank).
 * @property wPresetBagNdx Index to the preset's zone list in the PBAG sub-chunk. Must be monotonically increasing.
 * @property dwLibrary Reserved for future library management. Should be preserved as read and created as zero.
 * @property dwGenre Reserved for future library management. Should be preserved as read and created as zero.
 * @property dwMorphology Reserved for future library management. Should be preserved as read and created as zero.
 */
data class PhdrRecord(
    val achPresetName: String,
    val wPreset: Int,
    val wBank: Int,
    val wPresetBagNdx: Int,
    val dwLibrary: Int,
    val dwGenre: Int,
    val dwMorphology: Int
) {

    override fun toString(): String {
        return "PhdrRecord(achPresetName='$achPresetName', wPreset=$wPreset, wBank=$wBank, wPresetBagNdx=$wPresetBagNdx, dwLibrary=$dwLibrary, dwGenre=$dwGenre, dwMorphology=$dwMorphology)"
    }

}
