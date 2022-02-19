@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external open class Essentia(EssentiaWASM: Any, isDebug: Boolean = definedExternally) {
    open var EssentiaWASM: Any
    open var isDebug: Boolean
    open var algorithms: Any
    open var module: Any
    open var version: String
    open var algorithmNames: String
    open fun getAudioBufferFromURL(audioURL: String, webAudioCtx: AudioContext): Promise<AudioBuffer>
    open fun getAudioChannelDataFromURL(audioURL: String, webAudioCtx: AudioContext, channel: Number = definedExternally): Promise<Float32Array>
    open fun audioBufferToMonoSignal(buffer: AudioBuffer): Float32Array
    open fun shutdown()
    open fun reinstantiate()
    open fun delete()
    open fun arrayToVector(inputArray: Any): Any
    open fun vectorToArray(inputVector: Any): Float32Array
    open fun FrameGenerator(inputAudioData: Float32Array, frameSize: Number = definedExternally, hopSize: Number = definedExternally): Any
    open fun MonoMixer(leftSignal: Any, rightSignal: Any): Any
    open fun LoudnessEBUR128(leftSignal: Any, rightSignal: Any, hopSize: Number = definedExternally, sampleRate: Number = definedExternally, startAtZero: Boolean = definedExternally): Any
    open fun AfterMaxToBeforeMaxEnergyRatio(pitch: Any): Any
    open fun AllPass(signal: Any, bandwidth: Number = definedExternally, cutoffFrequency: Number = definedExternally, order: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun AudioOnsetsMarker(signal: Any, onsets: Array<Any> = definedExternally, sampleRate: Number = definedExternally, type: String = definedExternally): Any
    open fun AutoCorrelation(array: Any, frequencyDomainCompression: Number = definedExternally, generalized: Boolean = definedExternally, normalization: String = definedExternally): Any
    open fun BFCC(spectrum: Any, dctType: Number = definedExternally, highFrequencyBound: Number = definedExternally, inputSize: Number = definedExternally, liftering: Number = definedExternally, logType: String = definedExternally, lowFrequencyBound: Number = definedExternally, normalize: String = definedExternally, numberBands: Number = definedExternally, numberCoefficients: Number = definedExternally, sampleRate: Number = definedExternally, type: String = definedExternally, weighting: String = definedExternally): Any
    open fun BPF(x: Number, xPoints: Array<Any> = definedExternally, yPoints: Array<Any> = definedExternally): Any
    open fun BandPass(signal: Any, bandwidth: Number = definedExternally, cutoffFrequency: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun BandReject(signal: Any, bandwidth: Number = definedExternally, cutoffFrequency: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun BarkBands(spectrum: Any, numberBands: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun BeatTrackerDegara(signal: Any, maxTempo: Number = definedExternally, minTempo: Number = definedExternally): Any
    open fun BeatTrackerMultiFeature(signal: Any, maxTempo: Number = definedExternally, minTempo: Number = definedExternally): Any
    open fun Beatogram(loudness: Any, loudnessBandRatio: Any, size: Number = definedExternally): Any
    open fun BeatsLoudness(signal: Any, beatDuration: Number = definedExternally, beatWindowDuration: Number = definedExternally, beats: Array<Any> = definedExternally, frequencyBands: Array<Any> = definedExternally, sampleRate: Number = definedExternally): Any
    open fun BinaryOperator(array1: Any, array2: Any, type: String = definedExternally): Any
    open fun BinaryOperatorStream(array1: Any, array2: Any, type: String = definedExternally): Any
    open fun BpmHistogramDescriptors(bpmIntervals: Any): Any
    open fun BpmRubato(beats: Any, longRegionsPruningTime: Number = definedExternally, shortRegionsMergingTime: Number = definedExternally, tolerance: Number = definedExternally): Any
    open fun CentralMoments(array: Any, mode: String = definedExternally, range: Number = definedExternally): Any
    open fun Centroid(array: Any, range: Number = definedExternally): Any
    open fun ChordsDescriptors(chords: Any, key: String, scale: String): Any
    open fun ChordsDetection(pcp: Any, hopSize: Number = definedExternally, sampleRate: Number = definedExternally, windowSize: Number = definedExternally): Any
    open fun ChordsDetectionBeats(pcp: Any, ticks: Any, chromaPick: String = definedExternally, hopSize: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun ChromaCrossSimilarity(queryFeature: Any, referenceFeature: Any, binarizePercentile: Number = definedExternally, frameStackSize: Number = definedExternally, frameStackStride: Number = definedExternally, noti: Number = definedExternally, oti: Boolean = definedExternally, otiBinary: Boolean = definedExternally, streaming: Boolean = definedExternally): Any
    open fun Chromagram(frame: Any, binsPerOctave: Number = definedExternally, minFrequency: Number = definedExternally, minimumKernelSize: Number = definedExternally, normalizeType: String = definedExternally, numberBins: Number = definedExternally, sampleRate: Number = definedExternally, scale: Number = definedExternally, threshold: Number = definedExternally, windowType: String = definedExternally, zeroPhase: Boolean = definedExternally): Any
    open fun ClickDetector(frame: Any, detectionThreshold: Number = definedExternally, frameSize: Number = definedExternally, hopSize: Number = definedExternally, order: Number = definedExternally, powerEstimationThreshold: Number = definedExternally, sampleRate: Number = definedExternally, silenceThreshold: Number = definedExternally): Any
    open fun Clipper(signal: Any, max: Number = definedExternally, min: Number = definedExternally): Any
    open fun CoverSongSimilarity(inputArray: Any, alignmentType: String = definedExternally, disExtension: Number = definedExternally, disOnset: Number = definedExternally, distanceType: String = definedExternally): Any
    open fun Crest(array: Any): Any
    open fun CrossCorrelation(arrayX: Any, arrayY: Any, maxLag: Number = definedExternally, minLag: Number = definedExternally): Any
    open fun CrossSimilarityMatrix(queryFeature: Any, referenceFeature: Any, binarize: Boolean = definedExternally, binarizePercentile: Number = definedExternally, frameStackSize: Number = definedExternally, frameStackStride: Number = definedExternally): Any
    open fun CubicSpline(x: Number, leftBoundaryFlag: Number = definedExternally, leftBoundaryValue: Number = definedExternally, rightBoundaryFlag: Number = definedExternally, rightBoundaryValue: Number = definedExternally, xPoints: Array<Any> = definedExternally, yPoints: Array<Any> = definedExternally): Any
    open fun DCRemoval(signal: Any, cutoffFrequency: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun DCT(array: Any, dctType: Number = definedExternally, inputSize: Number = definedExternally, liftering: Number = definedExternally, outputSize: Number = definedExternally): Any
    open fun Danceability(signal: Any, maxTau: Number = definedExternally, minTau: Number = definedExternally, sampleRate: Number = definedExternally, tauMultiplier: Number = definedExternally): Any
    open fun Decrease(array: Any, range: Number = definedExternally): Any
    open fun Derivative(signal: Any): Any
    open fun DerivativeSFX(envelope: Any): Any
    open fun DiscontinuityDetector(frame: Any, detectionThreshold: Number = definedExternally, energyThreshold: Number = definedExternally, frameSize: Number = definedExternally, hopSize: Number = definedExternally, kernelSize: Number = definedExternally, order: Number = definedExternally, silenceThreshold: Number = definedExternally, subFrameSize: Number = definedExternally): Any
    open fun Dissonance(frequencies: Any, magnitudes: Any): Any
    open fun DistributionShape(centralMoments: Any): Any
    open fun Duration(signal: Any, sampleRate: Number = definedExternally): Any
    open fun DynamicComplexity(signal: Any, frameSize: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun ERBBands(spectrum: Any, highFrequencyBound: Number = definedExternally, inputSize: Number = definedExternally, lowFrequencyBound: Number = definedExternally, numberBands: Number = definedExternally, sampleRate: Number = definedExternally, type: String = definedExternally, width: Number = definedExternally): Any
    open fun EffectiveDuration(signal: Any, sampleRate: Number = definedExternally, thresholdRatio: Number = definedExternally): Any
    open fun Energy(array: Any): Any
    open fun EnergyBand(spectrum: Any, sampleRate: Number = definedExternally, startCutoffFrequency: Number = definedExternally, stopCutoffFrequency: Number = definedExternally): Any
    open fun EnergyBandRatio(spectrum: Any, sampleRate: Number = definedExternally, startFrequency: Number = definedExternally, stopFrequency: Number = definedExternally): Any
    open fun Entropy(array: Any): Any
    open fun Envelope(signal: Any, applyRectification: Boolean = definedExternally, attackTime: Number = definedExternally, releaseTime: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun EqualLoudness(signal: Any, sampleRate: Number = definedExternally): Any
    open fun Flatness(array: Any): Any
    open fun FlatnessDB(array: Any): Any
    open fun FlatnessSFX(envelope: Any): Any
    open fun Flux(spectrum: Any, halfRectify: Boolean = definedExternally, norm: String = definedExternally): Any
    open fun FrameCutter(signal: Any, frameSize: Number = definedExternally, hopSize: Number = definedExternally, lastFrameToEndOfFile: Boolean = definedExternally, startFromZero: Boolean = definedExternally, validFrameThresholdRatio: Number = definedExternally): Any
    open fun FrameToReal(signal: Any, frameSize: Number = definedExternally, hopSize: Number = definedExternally): Any
    open fun FrequencyBands(spectrum: Any, frequencyBands: Array<Any> = definedExternally, sampleRate: Number = definedExternally): Any
    open fun GFCC(spectrum: Any, dctType: Number = definedExternally, highFrequencyBound: Number = definedExternally, inputSize: Number = definedExternally, logType: String = definedExternally, lowFrequencyBound: Number = definedExternally, numberBands: Number = definedExternally, numberCoefficients: Number = definedExternally, sampleRate: Number = definedExternally, silenceThreshold: Number = definedExternally, type: String = definedExternally): Any
    open fun GapsDetector(frame: Any, attackTime: Number = definedExternally, frameSize: Number = definedExternally, hopSize: Number = definedExternally, kernelSize: Number = definedExternally, maximumTime: Number = definedExternally, minimumTime: Number = definedExternally, postpowerTime: Number = definedExternally, prepowerThreshold: Number = definedExternally, prepowerTime: Number = definedExternally, releaseTime: Number = definedExternally, sampleRate: Number = definedExternally, silenceThreshold: Number = definedExternally): Any
    open fun GeometricMean(array: Any): Any
    open fun HFC(spectrum: Any, sampleRate: Number = definedExternally, type: String = definedExternally): Any
    open fun HPCP(frequencies: Any, magnitudes: Any, bandPreset: Boolean = definedExternally, bandSplitFrequency: Number = definedExternally, harmonics: Number = definedExternally, maxFrequency: Number = definedExternally, maxShifted: Boolean = definedExternally, minFrequency: Number = definedExternally, nonLinear: Boolean = definedExternally, normalized: String = definedExternally, referenceFrequency: Number = definedExternally, sampleRate: Number = definedExternally, size: Number = definedExternally, weightType: String = definedExternally, windowSize: Number = definedExternally): Any
    open fun HarmonicBpm(bpms: Any, bpm: Number = definedExternally, threshold: Number = definedExternally, tolerance: Number = definedExternally): Any
    open fun HarmonicPeaks(frequencies: Any, magnitudes: Any, pitch: Number, maxHarmonics: Number = definedExternally, tolerance: Number = definedExternally): Any
    open fun HighPass(signal: Any, cutoffFrequency: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun HighResolutionFeatures(hpcp: Any, maxPeaks: Number = definedExternally): Any
    open fun Histogram(array: Any, maxValue: Number = definedExternally, minValue: Number = definedExternally, normalize: String = definedExternally, numberBins: Number = definedExternally): Any
    open fun HprModelAnal(frame: Any, pitch: Number, fftSize: Number = definedExternally, freqDevOffset: Number = definedExternally, freqDevSlope: Number = definedExternally, harmDevSlope: Number = definedExternally, hopSize: Number = definedExternally, magnitudeThreshold: Number = definedExternally, maxFrequency: Number = definedExternally, maxPeaks: Number = definedExternally, maxnSines: Number = definedExternally, minFrequency: Number = definedExternally, nHarmonics: Number = definedExternally, orderBy: String = definedExternally, sampleRate: Number = definedExternally, stocf: Number = definedExternally): Any
    open fun HpsModelAnal(frame: Any, pitch: Number, fftSize: Number = definedExternally, freqDevOffset: Number = definedExternally, freqDevSlope: Number = definedExternally, harmDevSlope: Number = definedExternally, hopSize: Number = definedExternally, magnitudeThreshold: Number = definedExternally, maxFrequency: Number = definedExternally, maxPeaks: Number = definedExternally, maxnSines: Number = definedExternally, minFrequency: Number = definedExternally, nHarmonics: Number = definedExternally, orderBy: String = definedExternally, sampleRate: Number = definedExternally, stocf: Number = definedExternally): Any
    open fun IDCT(dct: Any, dctType: Number = definedExternally, inputSize: Number = definedExternally, liftering: Number = definedExternally, outputSize: Number = definedExternally): Any
    open fun IIR(signal: Any, denominator: Array<Any> = definedExternally, numerator: Array<Any> = definedExternally): Any
    open fun Inharmonicity(frequencies: Any, magnitudes: Any): Any
    open fun InstantPower(array: Any): Any
    open fun Intensity(signal: Any, sampleRate: Number = definedExternally): Any
    open fun Key(pcp: Any, numHarmonics: Number = definedExternally, pcpSize: Number = definedExternally, profileType: String = definedExternally, slope: Number = definedExternally, useMajMin: Boolean = definedExternally, usePolyphony: Boolean = definedExternally, useThreeChords: Boolean = definedExternally): Any
    open fun KeyExtractor(audio: Any, averageDetuningCorrection: Boolean = definedExternally, frameSize: Number = definedExternally, hopSize: Number = definedExternally, hpcpSize: Number = definedExternally, maxFrequency: Number = definedExternally, maximumSpectralPeaks: Number = definedExternally, minFrequency: Number = definedExternally, pcpThreshold: Number = definedExternally, profileType: String = definedExternally, sampleRate: Number = definedExternally, spectralPeaksThreshold: Number = definedExternally, tuningFrequency: Number = definedExternally, weightType: String = definedExternally, windowType: String = definedExternally): Any
    open fun LPC(frame: Any, order: Number = definedExternally, sampleRate: Number = definedExternally, type: String = definedExternally): Any
    open fun Larm(signal: Any, attackTime: Number = definedExternally, power: Number = definedExternally, releaseTime: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun Leq(signal: Any): Any
    open fun LevelExtractor(signal: Any, frameSize: Number = definedExternally, hopSize: Number = definedExternally): Any
    open fun LogAttackTime(signal: Any, sampleRate: Number = definedExternally, startAttackThreshold: Number = definedExternally, stopAttackThreshold: Number = definedExternally): Any
    open fun LogSpectrum(spectrum: Any, binsPerSemitone: Number = definedExternally, frameSize: Number = definedExternally, rollOn: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun LoopBpmConfidence(signal: Any, bpmEstimate: Number, sampleRate: Number = definedExternally): Any
    open fun LoopBpmEstimator(signal: Any, confidenceThreshold: Number = definedExternally): Any
    open fun Loudness(signal: Any): Any
    open fun LoudnessVickers(signal: Any, sampleRate: Number = definedExternally): Any
    open fun LowLevelSpectralEqloudExtractor(signal: Any, frameSize: Number = definedExternally, hopSize: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun LowLevelSpectralExtractor(signal: Any, frameSize: Number = definedExternally, hopSize: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun LowPass(signal: Any, cutoffFrequency: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun MFCC(spectrum: Any, dctType: Number = definedExternally, highFrequencyBound: Number = definedExternally, inputSize: Number = definedExternally, liftering: Number = definedExternally, logType: String = definedExternally, lowFrequencyBound: Number = definedExternally, normalize: String = definedExternally, numberBands: Number = definedExternally, numberCoefficients: Number = definedExternally, sampleRate: Number = definedExternally, silenceThreshold: Number = definedExternally, type: String = definedExternally, warpingFormula: String = definedExternally, weighting: String = definedExternally): Any
    open fun MaxFilter(signal: Any, causal: Boolean = definedExternally, width: Number = definedExternally): Any
    open fun MaxMagFreq(spectrum: Any, sampleRate: Number = definedExternally): Any
    open fun MaxToTotal(envelope: Any): Any
    open fun Mean(array: Any): Any
    open fun Median(array: Any): Any
    open fun MedianFilter(array: Any, kernelSize: Number = definedExternally): Any
    open fun MelBands(spectrum: Any, highFrequencyBound: Number = definedExternally, inputSize: Number = definedExternally, log: Boolean = definedExternally, lowFrequencyBound: Number = definedExternally, normalize: String = definedExternally, numberBands: Number = definedExternally, sampleRate: Number = definedExternally, type: String = definedExternally, warpingFormula: String = definedExternally, weighting: String = definedExternally): Any
    open fun Meter(beatogram: Any): Any
    open fun MinMax(array: Any, type: String = definedExternally): Any
    open fun MinToTotal(envelope: Any): Any
    open fun MovingAverage(signal: Any, size: Number = definedExternally): Any
    open fun MultiPitchKlapuri(signal: Any, binResolution: Number = definedExternally, frameSize: Number = definedExternally, harmonicWeight: Number = definedExternally, hopSize: Number = definedExternally, magnitudeCompression: Number = definedExternally, magnitudeThreshold: Number = definedExternally, maxFrequency: Number = definedExternally, minFrequency: Number = definedExternally, numberHarmonics: Number = definedExternally, referenceFrequency: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun MultiPitchMelodia(signal: Any, binResolution: Number = definedExternally, filterIterations: Number = definedExternally, frameSize: Number = definedExternally, guessUnvoiced: Boolean = definedExternally, harmonicWeight: Number = definedExternally, hopSize: Number = definedExternally, magnitudeCompression: Number = definedExternally, magnitudeThreshold: Number = definedExternally, maxFrequency: Number = definedExternally, minDuration: Number = definedExternally, minFrequency: Number = definedExternally, numberHarmonics: Number = definedExternally, peakDistributionThreshold: Number = definedExternally, peakFrameThreshold: Number = definedExternally, pitchContinuity: Number = definedExternally, referenceFrequency: Number = definedExternally, sampleRate: Number = definedExternally, timeContinuity: Number = definedExternally): Any
    open fun Multiplexer(numberRealInputs: Number = definedExternally, numberVectorRealInputs: Number = definedExternally): Any
    open fun NNLSChroma(logSpectrogram: Any, meanTuning: Any, localTuning: Any, chromaNormalization: String = definedExternally, frameSize: Number = definedExternally, sampleRate: Number = definedExternally, spectralShape: Number = definedExternally, spectralWhitening: Number = definedExternally, tuningMode: String = definedExternally, useNNLS: Boolean = definedExternally): Any
    open fun NoiseAdder(signal: Any, fixSeed: Boolean = definedExternally, level: Number = definedExternally): Any
    open fun NoiseBurstDetector(frame: Any, alpha: Number = definedExternally, silenceThreshold: Number = definedExternally, threshold: Number = definedExternally): Any
    open fun NoveltyCurve(frequencyBands: Any, frameRate: Number = definedExternally, normalize: Boolean = definedExternally, weightCurve: Array<Any> = definedExternally, weightCurveType: String = definedExternally): Any
    open fun NoveltyCurveFixedBpmEstimator(novelty: Any, hopSize: Number = definedExternally, maxBpm: Number = definedExternally, minBpm: Number = definedExternally, sampleRate: Number = definedExternally, tolerance: Number = definedExternally): Any
    open fun OddToEvenHarmonicEnergyRatio(frequencies: Any, magnitudes: Any): Any
    open fun OnsetDetection(spectrum: Any, phase: Any, method: String = definedExternally, sampleRate: Number = definedExternally): Any
    open fun OnsetDetectionGlobal(signal: Any, frameSize: Number = definedExternally, hopSize: Number = definedExternally, method: String = definedExternally, sampleRate: Number = definedExternally): Any
    open fun OnsetRate(signal: Any): Any
    open fun OverlapAdd(signal: Any, frameSize: Number = definedExternally, gain: Number = definedExternally, hopSize: Number = definedExternally): Any
    open fun PeakDetection(array: Any, interpolate: Boolean = definedExternally, maxPeaks: Number = definedExternally, maxPosition: Number = definedExternally, minPeakDistance: Number = definedExternally, minPosition: Number = definedExternally, orderBy: String = definedExternally, range: Number = definedExternally, threshold: Number = definedExternally): Any
    open fun PercivalBpmEstimator(signal: Any, frameSize: Number = definedExternally, frameSizeOSS: Number = definedExternally, hopSize: Number = definedExternally, hopSizeOSS: Number = definedExternally, maxBPM: Number = definedExternally, minBPM: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun PercivalEnhanceHarmonics(array: Any): Any
    open fun PercivalEvaluatePulseTrains(oss: Any, positions: Any): Any
    open fun PitchContourSegmentation(pitch: Any, signal: Any, hopSize: Number = definedExternally, minDuration: Number = definedExternally, pitchDistanceThreshold: Number = definedExternally, rmsThreshold: Number = definedExternally, sampleRate: Number = definedExternally, tuningFrequency: Number = definedExternally): Any
    open fun PitchContours(peakBins: Any, peakSaliences: Any, binResolution: Number = definedExternally, hopSize: Number = definedExternally, minDuration: Number = definedExternally, peakDistributionThreshold: Number = definedExternally, peakFrameThreshold: Number = definedExternally, pitchContinuity: Number = definedExternally, sampleRate: Number = definedExternally, timeContinuity: Number = definedExternally): Any
    open fun PitchContoursMelody(contoursBins: Any, contoursSaliences: Any, contoursStartTimes: Any, duration: Number, binResolution: Number = definedExternally, filterIterations: Number = definedExternally, guessUnvoiced: Boolean = definedExternally, hopSize: Number = definedExternally, maxFrequency: Number = definedExternally, minFrequency: Number = definedExternally, referenceFrequency: Number = definedExternally, sampleRate: Number = definedExternally, voiceVibrato: Boolean = definedExternally, voicingTolerance: Number = definedExternally): Any
    open fun PitchContoursMonoMelody(contoursBins: Any, contoursSaliences: Any, contoursStartTimes: Any, duration: Number, binResolution: Number = definedExternally, filterIterations: Number = definedExternally, guessUnvoiced: Boolean = definedExternally, hopSize: Number = definedExternally, maxFrequency: Number = definedExternally, minFrequency: Number = definedExternally, referenceFrequency: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun PitchContoursMultiMelody(contoursBins: Any, contoursSaliences: Any, contoursStartTimes: Any, duration: Number, binResolution: Number = definedExternally, filterIterations: Number = definedExternally, guessUnvoiced: Boolean = definedExternally, hopSize: Number = definedExternally, maxFrequency: Number = definedExternally, minFrequency: Number = definedExternally, referenceFrequency: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun PitchFilter(pitch: Any, pitchConfidence: Any, confidenceThreshold: Number = definedExternally, minChunkSize: Number = definedExternally, useAbsolutePitchConfidence: Boolean = definedExternally): Any
    open fun PitchMelodia(signal: Any, binResolution: Number = definedExternally, filterIterations: Number = definedExternally, frameSize: Number = definedExternally, guessUnvoiced: Boolean = definedExternally, harmonicWeight: Number = definedExternally, hopSize: Number = definedExternally, magnitudeCompression: Number = definedExternally, magnitudeThreshold: Number = definedExternally, maxFrequency: Number = definedExternally, minDuration: Number = definedExternally, minFrequency: Number = definedExternally, numberHarmonics: Number = definedExternally, peakDistributionThreshold: Number = definedExternally, peakFrameThreshold: Number = definedExternally, pitchContinuity: Number = definedExternally, referenceFrequency: Number = definedExternally, sampleRate: Number = definedExternally, timeContinuity: Number = definedExternally): Any
    open fun PitchSalience(spectrum: Any, highBoundary: Number = definedExternally, lowBoundary: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun PitchSalienceFunction(frequencies: Any, magnitudes: Any, binResolution: Number = definedExternally, harmonicWeight: Number = definedExternally, magnitudeCompression: Number = definedExternally, magnitudeThreshold: Number = definedExternally, numberHarmonics: Number = definedExternally, referenceFrequency: Number = definedExternally): Any
    open fun PitchSalienceFunctionPeaks(salienceFunction: Any, binResolution: Number = definedExternally, maxFrequency: Number = definedExternally, minFrequency: Number = definedExternally, referenceFrequency: Number = definedExternally): Any
    open fun PitchYin(signal: Any, frameSize: Number = definedExternally, interpolate: Boolean = definedExternally, maxFrequency: Number = definedExternally, minFrequency: Number = definedExternally, sampleRate: Number = definedExternally, tolerance: Number = definedExternally): Any
    open fun PitchYinFFT(spectrum: Any, frameSize: Number = definedExternally, interpolate: Boolean = definedExternally, maxFrequency: Number = definedExternally, minFrequency: Number = definedExternally, sampleRate: Number = definedExternally, tolerance: Number = definedExternally): Any
    open fun PitchYinProbabilistic(signal: Any, frameSize: Number = definedExternally, hopSize: Number = definedExternally, lowRMSThreshold: Number = definedExternally, outputUnvoiced: String = definedExternally, preciseTime: Boolean = definedExternally, sampleRate: Number = definedExternally): Any
    open fun PitchYinProbabilities(signal: Any, frameSize: Number = definedExternally, lowAmp: Number = definedExternally, preciseTime: Boolean = definedExternally, sampleRate: Number = definedExternally): Any
    open fun PitchYinProbabilitiesHMM(pitchCandidates: Any, probabilities: Any, minFrequency: Number = definedExternally, numberBinsPerSemitone: Number = definedExternally, selfTransition: Number = definedExternally, yinTrust: Number = definedExternally): Any
    open fun PowerMean(array: Any, power: Number = definedExternally): Any
    open fun PowerSpectrum(signal: Any, size: Number = definedExternally): Any
    open fun PredominantPitchMelodia(signal: Any, binResolution: Number = definedExternally, filterIterations: Number = definedExternally, frameSize: Number = definedExternally, guessUnvoiced: Boolean = definedExternally, harmonicWeight: Number = definedExternally, hopSize: Number = definedExternally, magnitudeCompression: Number = definedExternally, magnitudeThreshold: Number = definedExternally, maxFrequency: Number = definedExternally, minDuration: Number = definedExternally, minFrequency: Number = definedExternally, numberHarmonics: Number = definedExternally, peakDistributionThreshold: Number = definedExternally, peakFrameThreshold: Number = definedExternally, pitchContinuity: Number = definedExternally, referenceFrequency: Number = definedExternally, sampleRate: Number = definedExternally, timeContinuity: Number = definedExternally, voiceVibrato: Boolean = definedExternally, voicingTolerance: Number = definedExternally): Any
    open fun RMS(array: Any): Any
    open fun RawMoments(array: Any, range: Number = definedExternally): Any
    open fun ReplayGain(signal: Any, sampleRate: Number = definedExternally): Any
    open fun Resample(signal: Any, inputSampleRate: Number = definedExternally, outputSampleRate: Number = definedExternally, quality: Number = definedExternally): Any
    open fun ResampleFFT(input: Any, inSize: Number = definedExternally, outSize: Number = definedExternally): Any
    open fun RhythmDescriptors(signal: Any): Any
    open fun RhythmExtractor(signal: Any, frameHop: Number = definedExternally, frameSize: Number = definedExternally, hopSize: Number = definedExternally, lastBeatInterval: Number = definedExternally, maxTempo: Number = definedExternally, minTempo: Number = definedExternally, numberFrames: Number = definedExternally, sampleRate: Number = definedExternally, tempoHints: Array<Any> = definedExternally, tolerance: Number = definedExternally, useBands: Boolean = definedExternally, useOnset: Boolean = definedExternally): Any
    open fun RhythmExtractor2013(signal: Any, maxTempo: Number = definedExternally, method: String = definedExternally, minTempo: Number = definedExternally): Any
    open fun RhythmTransform(melBands: Any, frameSize: Number = definedExternally, hopSize: Number = definedExternally): Any
    open fun RollOff(spectrum: Any, cutoff: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun SNR(frame: Any, MAAlpha: Number = definedExternally, MMSEAlpha: Number = definedExternally, NoiseAlpha: Number = definedExternally, frameSize: Number = definedExternally, noiseThreshold: Number = definedExternally, sampleRate: Number = definedExternally, useBroadbadNoiseCorrection: Boolean = definedExternally): Any
    open fun SaturationDetector(frame: Any, differentialThreshold: Number = definedExternally, energyThreshold: Number = definedExternally, frameSize: Number = definedExternally, hopSize: Number = definedExternally, minimumDuration: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun Scale(signal: Any, clipping: Boolean = definedExternally, factor: Number = definedExternally, maxAbsValue: Number = definedExternally): Any
    open fun SineSubtraction(frame: Any, magnitudes: Any, frequencies: Any, phases: Any, fftSize: Number = definedExternally, hopSize: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun SingleBeatLoudness(beat: Any, beatDuration: Number = definedExternally, beatWindowDuration: Number = definedExternally, frequencyBands: Array<Any> = definedExternally, onsetStart: String = definedExternally, sampleRate: Number = definedExternally): Any
    open fun Slicer(audio: Any, endTimes: Array<Any> = definedExternally, sampleRate: Number = definedExternally, startTimes: Array<Any> = definedExternally, timeUnits: String = definedExternally): Any
    open fun SpectralCentroidTime(array: Any, sampleRate: Number = definedExternally): Any
    open fun SpectralComplexity(spectrum: Any, magnitudeThreshold: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun SpectralContrast(spectrum: Any, frameSize: Number = definedExternally, highFrequencyBound: Number = definedExternally, lowFrequencyBound: Number = definedExternally, neighbourRatio: Number = definedExternally, numberBands: Number = definedExternally, sampleRate: Number = definedExternally, staticDistribution: Number = definedExternally): Any
    open fun SpectralPeaks(spectrum: Any, magnitudeThreshold: Number = definedExternally, maxFrequency: Number = definedExternally, maxPeaks: Number = definedExternally, minFrequency: Number = definedExternally, orderBy: String = definedExternally, sampleRate: Number = definedExternally): Any
    open fun SpectralWhitening(spectrum: Any, frequencies: Any, magnitudes: Any, maxFrequency: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun Spectrum(frame: Any, size: Number = definedExternally): Any
    open fun SpectrumCQ(frame: Any, binsPerOctave: Number = definedExternally, minFrequency: Number = definedExternally, minimumKernelSize: Number = definedExternally, numberBins: Number = definedExternally, sampleRate: Number = definedExternally, scale: Number = definedExternally, threshold: Number = definedExternally, windowType: String = definedExternally, zeroPhase: Boolean = definedExternally): Any
    open fun SpectrumToCent(spectrum: Any, bands: Number = definedExternally, centBinResolution: Number = definedExternally, inputSize: Number = definedExternally, log: Boolean = definedExternally, minimumFrequency: Number = definedExternally, normalize: String = definedExternally, sampleRate: Number = definedExternally, type: String = definedExternally): Any
    open fun Spline(x: Number, beta1: Number = definedExternally, beta2: Number = definedExternally, type: String = definedExternally, xPoints: Array<Any> = definedExternally, yPoints: Array<Any> = definedExternally): Any
    open fun SprModelAnal(frame: Any, fftSize: Number = definedExternally, freqDevOffset: Number = definedExternally, freqDevSlope: Number = definedExternally, hopSize: Number = definedExternally, magnitudeThreshold: Number = definedExternally, maxFrequency: Number = definedExternally, maxPeaks: Number = definedExternally, maxnSines: Number = definedExternally, minFrequency: Number = definedExternally, orderBy: String = definedExternally, sampleRate: Number = definedExternally): Any
    open fun SprModelSynth(magnitudes: Any, frequencies: Any, phases: Any, res: Any, fftSize: Number = definedExternally, hopSize: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun SpsModelAnal(frame: Any, fftSize: Number = definedExternally, freqDevOffset: Number = definedExternally, freqDevSlope: Number = definedExternally, hopSize: Number = definedExternally, magnitudeThreshold: Number = definedExternally, maxFrequency: Number = definedExternally, maxPeaks: Number = definedExternally, maxnSines: Number = definedExternally, minFrequency: Number = definedExternally, orderBy: String = definedExternally, sampleRate: Number = definedExternally, stocf: Number = definedExternally): Any
    open fun SpsModelSynth(magnitudes: Any, frequencies: Any, phases: Any, stocenv: Any, fftSize: Number = definedExternally, hopSize: Number = definedExternally, sampleRate: Number = definedExternally, stocf: Number = definedExternally): Any
    open fun StartStopCut(audio: Any, frameSize: Number = definedExternally, hopSize: Number = definedExternally, maximumStartTime: Number = definedExternally, maximumStopTime: Number = definedExternally, sampleRate: Number = definedExternally, threshold: Number = definedExternally): Any
    open fun StartStopSilence(frame: Any, threshold: Number = definedExternally): Any
    open fun StochasticModelAnal(frame: Any, fftSize: Number = definedExternally, hopSize: Number = definedExternally, sampleRate: Number = definedExternally, stocf: Number = definedExternally): Any
    open fun StochasticModelSynth(stocenv: Any, fftSize: Number = definedExternally, hopSize: Number = definedExternally, sampleRate: Number = definedExternally, stocf: Number = definedExternally): Any
    open fun StrongDecay(signal: Any, sampleRate: Number = definedExternally): Any
    open fun StrongPeak(spectrum: Any): Any
    open fun SuperFluxExtractor(signal: Any, combine: Number = definedExternally, frameSize: Number = definedExternally, hopSize: Number = definedExternally, ratioThreshold: Number = definedExternally, sampleRate: Number = definedExternally, threshold: Number = definedExternally): Any
    open fun SuperFluxNovelty(bands: Any, binWidth: Number = definedExternally, frameWidth: Number = definedExternally): Any
    open fun SuperFluxPeaks(novelty: Any, combine: Number = definedExternally, frameRate: Number = definedExternally, pre_avg: Number = definedExternally, pre_max: Number = definedExternally, ratioThreshold: Number = definedExternally, threshold: Number = definedExternally): Any
    open fun TCToTotal(envelope: Any): Any
    open fun TempoScaleBands(bands: Any, bandsGain: Array<Any> = definedExternally, frameTime: Number = definedExternally): Any
    open fun TempoTap(featuresFrame: Any, frameHop: Number = definedExternally, frameSize: Number = definedExternally, maxTempo: Number = definedExternally, minTempo: Number = definedExternally, numberFrames: Number = definedExternally, sampleRate: Number = definedExternally, tempoHints: Array<Any> = definedExternally): Any
    open fun TempoTapDegara(onsetDetections: Any, maxTempo: Number = definedExternally, minTempo: Number = definedExternally, resample: String = definedExternally, sampleRateODF: Number = definedExternally): Any
    open fun TempoTapMaxAgreement(tickCandidates: Any): Any
    open fun TempoTapTicks(periods: Any, phases: Any, frameHop: Number = definedExternally, hopSize: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun TensorflowInputMusiCNN(frame: Any): Any
    open fun TensorflowInputVGGish(frame: Any): Any
    open fun TonalExtractor(signal: Any, frameSize: Number = definedExternally, hopSize: Number = definedExternally, tuningFrequency: Number = definedExternally): Any
    open fun TonicIndianArtMusic(signal: Any, binResolution: Number = definedExternally, frameSize: Number = definedExternally, harmonicWeight: Number = definedExternally, hopSize: Number = definedExternally, magnitudeCompression: Number = definedExternally, magnitudeThreshold: Number = definedExternally, maxTonicFrequency: Number = definedExternally, minTonicFrequency: Number = definedExternally, numberHarmonics: Number = definedExternally, numberSaliencePeaks: Number = definedExternally, referenceFrequency: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun TriangularBands(spectrum: Any, frequencyBands: Array<Any> = definedExternally, inputSize: Number = definedExternally, log: Boolean = definedExternally, normalize: String = definedExternally, sampleRate: Number = definedExternally, type: String = definedExternally, weighting: String = definedExternally): Any
    open fun TriangularBarkBands(spectrum: Any, highFrequencyBound: Number = definedExternally, inputSize: Number = definedExternally, log: Boolean = definedExternally, lowFrequencyBound: Number = definedExternally, normalize: String = definedExternally, numberBands: Number = definedExternally, sampleRate: Number = definedExternally, type: String = definedExternally, weighting: String = definedExternally): Any
    open fun Trimmer(signal: Any, checkRange: Boolean = definedExternally, endTime: Number = definedExternally, sampleRate: Number = definedExternally, startTime: Number = definedExternally): Any
    open fun Tristimulus(frequencies: Any, magnitudes: Any): Any
    open fun TruePeakDetector(signal: Any, blockDC: Boolean = definedExternally, emphasise: Boolean = definedExternally, oversamplingFactor: Number = definedExternally, quality: Number = definedExternally, sampleRate: Number = definedExternally, threshold: Number = definedExternally, version: Number = definedExternally): Any
    open fun TuningFrequency(frequencies: Any, magnitudes: Any, resolution: Number = definedExternally): Any
    open fun TuningFrequencyExtractor(signal: Any, frameSize: Number = definedExternally, hopSize: Number = definedExternally): Any
    open fun UnaryOperator(array: Any, scale: Number = definedExternally, shift: Number = definedExternally, type: String = definedExternally): Any
    open fun UnaryOperatorStream(array: Any, scale: Number = definedExternally, shift: Number = definedExternally, type: String = definedExternally): Any
    open fun Variance(array: Any): Any
    open fun Vibrato(pitch: Any, maxExtend: Number = definedExternally, maxFrequency: Number = definedExternally, minExtend: Number = definedExternally, minFrequency: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun WarpedAutoCorrelation(array: Any, maxLag: Number = definedExternally, sampleRate: Number = definedExternally): Any
    open fun Welch(frame: Any, averagingFrames: Number = definedExternally, fftSize: Number = definedExternally, frameSize: Number = definedExternally, sampleRate: Number = definedExternally, scaling: String = definedExternally, windowType: String = definedExternally): Any
    open fun Windowing(frame: Any, normalized: Boolean = definedExternally, size: Number = definedExternally, type: String = definedExternally, zeroPadding: Number = definedExternally, zeroPhase: Boolean = definedExternally): Any
    open fun ZeroCrossingRate(signal: Any, threshold: Number = definedExternally): Any
}