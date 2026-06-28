package com.kjipo.soundfontparser.serialization

import com.kjipo.soundfontparser.*

fun SoundFontData.toDto(): SoundFontDataDto = SoundFontDataDto(
    shdrChunk = shdrChunk.toDto(),
    instSubchunk = instSubchunk.toDto(),
    phdrSubchunks = phdrSubchunks.toDto(),
    smplChunk = smplChunk?.toDto()
)

fun ShdrChunk.toDto(): ShdrChunkDto = ShdrChunkDto(
    shdrRecords = shdrRecords.map { it.toDto() }
)

fun ShdrRecord.toDto(): ShdrRecordDto = ShdrRecordDto(
    achSampleName = achSampleName,
    dwStart = dwStart,
    dwEnd = dwEnd,
    dwStartloop = dwStartloop,
    dwEndloop = dwEndloop,
    dwSampleRate = dwSampleRate,
    byOriginalPitch = byOriginalPitch.toInt(),
    chPitchCorrection = chPitchCorrection,
    wSampleLink = wSampleLink,
    sfSampleType = sfSampleType.value
)

fun InstSubchunk.toDto(): InstSubchunkDto = InstSubchunkDto(
    sfInstRecords = sfInstRecords.map { it.toDto() }
)

fun SfInstRecord.toDto(): SfInstRecordDto = SfInstRecordDto(
    achInstName = achInstName,
    wInstBagNdx = wInstBagNdx
)

fun PhdrSubchunk.toDto(): PhdrSubchunkDto = PhdrSubchunkDto(
    phdrRecords = phdrRecords.map { it.toDto() }
)

fun PhdrRecord.toDto(): PhdrRecordDto = PhdrRecordDto(
    achPresetName = achPresetName,
    wPreset = wPreset,
    wBank = wBank,
    wPresetBagNdx = wPresetBagNdx,
    dwLibrary = dwLibrary,
    dwGenre = dwGenre,
    dwMorphology = dwMorphology
)

fun SmplChunk.toDto(): SmplChunkDto = SmplChunkDto(
    smplDataBase64 = Base64Codec.encode(smplData)
)

fun SoundFontDataDto.toSoundFontData(): SoundFontData {
    require(version == 1) { "Unsupported SoundFontData serialization version: $version" }
    return SoundFontData(
        shdrChunk = shdrChunk.toShdrChunk(),
        instSubchunk = instSubchunk.toInstSubchunk(),
        phdrSubchunks = phdrSubchunks.toPhdrSubchunk(),
        smplChunk = smplChunk?.toSmplChunk()
    )
}

fun ShdrChunkDto.toShdrChunk(): ShdrChunk = ShdrChunk(
    shdrRecords = shdrRecords.map { it.toShdrRecord() }
)

fun ShdrRecordDto.toShdrRecord(): ShdrRecord = ShdrRecord(
    achSampleName = achSampleName,
    dwStart = dwStart,
    dwEnd = dwEnd,
    dwStartloop = dwStartloop,
    dwEndloop = dwEndloop,
    dwSampleRate = dwSampleRate,
    byOriginalPitch = byOriginalPitch.toByte(),
    chPitchCorrection = chPitchCorrection,
    wSampleLink = wSampleLink,
    sfSampleType = SampleType.fromValue(sfSampleType)
)

fun InstSubchunkDto.toInstSubchunk(): InstSubchunk = InstSubchunk(
    sfInstRecords = sfInstRecords.map { it.toSfInstRecord() }
)

fun SfInstRecordDto.toSfInstRecord(): SfInstRecord = SfInstRecord(
    achInstName = achInstName,
    wInstBagNdx = wInstBagNdx
)

fun PhdrSubchunkDto.toPhdrSubchunk(): PhdrSubchunk = PhdrSubchunk(
    phdrRecords = phdrRecords.map { it.toPhdrRecord() }
)

fun PhdrRecordDto.toPhdrRecord(): PhdrRecord = PhdrRecord(
    achPresetName = achPresetName,
    wPreset = wPreset,
    wBank = wBank,
    wPresetBagNdx = wPresetBagNdx,
    dwLibrary = dwLibrary,
    dwGenre = dwGenre,
    dwMorphology = dwMorphology
)

fun SmplChunkDto.toSmplChunk(): SmplChunk {
    require(sampleDataEncoding == "int16-le-base64") { "Unsupported sample data encoding: $sampleDataEncoding" }
    return SmplChunk(
        smplData = Base64Codec.decode(smplDataBase64)
    )
}
