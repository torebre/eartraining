package com.kjipo.soundfontparser.serialization

import kotlinx.serialization.Serializable

@Serializable
data class SoundFontDataDto(
    val format: String = "soundfont-data-json",
    val version: Int = 1,
    val metadata: SoundFontMetadataDto = SoundFontMetadataDto(),
    val shdrChunk: ShdrChunkDto,
    val instSubchunk: InstSubchunkDto,
    val phdrSubchunks: PhdrSubchunkDto,
    val smplChunk: SmplChunkDto?
)

@Serializable
data class SoundFontMetadataDto(
    val name: String? = null,
    val source: String? = null,
    val createdBy: String? = null
)

@Serializable
data class ShdrChunkDto(
    val shdrRecords: List<ShdrRecordDto>
)

@Serializable
data class ShdrRecordDto(
    val achSampleName: String,
    val dwStart: Int,
    val dwEnd: Int,
    val dwStartloop: Int,
    val dwEndloop: Int,
    val dwSampleRate: Int,
    val byOriginalPitch: Int,
    val chPitchCorrection: Int,
    val wSampleLink: Int,
    val sfSampleType: Int
)

@Serializable
data class InstSubchunkDto(
    val sfInstRecords: List<SfInstRecordDto>
)

@Serializable
data class SfInstRecordDto(
    val achInstName: String,
    val wInstBagNdx: Int
)

@Serializable
data class PhdrSubchunkDto(
    val phdrRecords: List<PhdrRecordDto>
)

@Serializable
data class PhdrRecordDto(
    val achPresetName: String,
    val wPreset: Int,
    val wBank: Int,
    val wPresetBagNdx: Int,
    val dwLibrary: Int,
    val dwGenre: Int,
    val dwMorphology: Int
)

@Serializable
data class SmplChunkDto(
    val sampleDataEncoding: String = "int16-le-base64",
    val smplDataBase64: String
)
