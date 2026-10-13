package com.example.mandate.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance, offline sound synthesis engine for localized Fuji music,
 * Yoruba talking drum (gangan) pitch-bends, cash spray flutter sounds, and brass fanfares.
 */
class FujiAudioEngine {

    private val audioScope = CoroutineScope(Dispatchers.Default)
    private var toneGenerator: ToneGenerator? = null

    @Volatile
    var isSfxEnabled: Boolean = true

    @Volatile
    var sfxVolume: Float = 0.85f

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            Log.w("FujiAudioEngine", "ToneGenerator init fallback: ${e.message}")
        }
    }

    /**
     * Synthesizes an authentic Yoruba talking drum (Gangan) strike with pitch bending.
     * When struck, the tension strings are squeezed and relaxed, creating a distinct pitch glide.
     */
    fun playTalkingDrum(pitchMultiplier: Float = 1.0f) {
        audioScope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 180
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                val startFreq = 180.0 * pitchMultiplier
                val peakFreq = 340.0 * pitchMultiplier
                val endFreq = 220.0 * pitchMultiplier

                var currentPhase = 0.0

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / numSamples
                    // Pitch trajectory: rises quickly (squeezed cord), then releases slightly
                    val instantFreq = if (t < 0.3) {
                        startFreq + (peakFreq - startFreq) * (t / 0.3)
                    } else {
                        peakFreq - (peakFreq - endFreq) * ((t - 0.3) / 0.7)
                    }

                    currentPhase += 2.0 * PI * instantFreq / sampleRate
                    // Drum envelope: sharp attack, exponential ring
                    val envelope = exp(-t * 6.0)
                    // Add subtle second harmonic for wooden drum shell resonance
                    val sampleVal = (sin(currentPhase) + 0.35 * sin(2.0 * currentPhase)) * envelope
                    buffer[i] = (sampleVal * 28000).toInt().coerceIn(-32767, 32767).toShort()
                }

                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                // Graceful fallback to tone generator
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_B, 100)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Synthesizes a deep Fuji Sakara / Bass drum kick thump for the pulse beat.
     */
    fun playBassKick() {
        audioScope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 120
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                var phase = 0.0
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / numSamples
                    val freq = 110.0 * exp(-t * 8.0) + 45.0
                    phase += 2.0 * PI * freq / sampleRate
                    val envelope = exp(-t * 7.0)
                    val sample = sin(phase) * envelope
                    buffer[i] = (sample * 30000).toInt().coerceIn(-32767, 32767).toShort()
                }

                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_0, 80)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Synthesizes the crisp rustle / flutter of a freshly printed ₦1,000 banknote flying.
     */
    fun playCashFlutter() {
        audioScope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 90
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / numSamples
                    val envelope = (1.0 - t) * (1.0 - t)
                    // White noise burst filtered with high-frequency flutter
                    val noise = (Math.random() * 2.0 - 1.0)
                    val flutterMod = sin(2.0 * PI * 40.0 * (i.toDouble() / sampleRate))
                    val sample = noise * envelope * (0.6 + 0.4 * flutterMod)
                    buffer[i] = (sample * 16000).toInt().coerceIn(-32767, 32767).toShort()
                }

                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    /**
     * Synthesizes a celebratory brass fanfare riff ("Dorime / Heavy Spender")
     * plays a majestic rising 3-chord arpeggio (C5 -> E5 -> G5 -> C6).
     */
    fun playCelebrationFanfare() {
        audioScope.launch {
            try {
                val sampleRate = 44100
                val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
                val noteDurationMs = 110
                val totalSamples = (sampleRate * ((noteDurationMs * notes.size) / 1000.0)).toInt()
                val buffer = ShortArray(totalSamples)

                var sampleIndex = 0
                for (freq in notes) {
                    val noteSamples = (sampleRate * (noteDurationMs / 1000.0)).toInt()
                    var phase = 0.0
                    for (i in 0 until noteSamples) {
                        if (sampleIndex >= totalSamples) break
                        val t = i.toDouble() / noteSamples
                        phase += 2.0 * PI * freq / sampleRate
                        // Brass-like harmonic overtone mix
                        val wave = sin(phase) + 0.5 * sin(2.0 * phase) + 0.25 * sin(3.0 * phase)
                        val envelope = if (t < 0.1) t / 0.1 else exp(-(t - 0.1) * 3.0)
                        buffer[sampleIndex] = (wave * envelope * 12000).toInt().coerceIn(-32767, 32767).toShort()
                        sampleIndex++
                    }
                }

                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 250)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Synthesizes a warm blessing chime when receiving maternal or paternal prayers (Dobale / Ikunle).
     */
    fun playBlessingChime() {
        audioScope.launch {
            try {
                val sampleRate = 44100
                val notes = listOf(659.25, 830.61, 987.77, 1318.51) // E5, G#5, B5, E6
                val totalDurationMs = 380
                val totalSamples = (sampleRate * (totalDurationMs / 1000.0)).toInt()
                val buffer = ShortArray(totalSamples)

                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / totalSamples
                    var sum = 0.0
                    for ((idx, freq) in notes.withIndex()) {
                        val noteStart = idx * 0.18
                        if (t >= noteStart) {
                            val localT = t - noteStart
                            val env = exp(-localT * 7.0)
                            sum += sin(2.0 * PI * freq * (i.toDouble() / sampleRate)) * env
                        }
                    }
                    val sample = (sum * 7500).toInt().coerceIn(-32767, 32767).toShort()
                    buffer[i] = sample
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 180)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Synthesizes crisp clink of cold bottles/cups for sharing drinks with visiting padis.
     */
    fun playBottleClink() {
        audioScope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 120
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / numSamples
                    val env = exp(-t * 22.0)
                    val freq1 = 2100.0
                    val freq2 = 2850.0
                    val wave = (sin(2.0 * PI * freq1 * (i.toDouble() / sampleRate)) * 0.6 +
                                sin(2.0 * PI * freq2 * (i.toDouble() / sampleRate)) * 0.4) * env
                    buffer[i] = (wave * 26000).toInt().coerceIn(-32767, 32767).toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    /**
     * Synthesizes a classic Nigerian finger-snap and handshake clap ("How far! Senior Man!").
     */
    fun playLagosHandshakeSlap() {
        audioScope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 150
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / numSamples
                    val noise = (Math.random() * 2.0 - 1.0)
                    val env = if (t < 0.25) {
                        exp(-t * 30.0) // first slap
                    } else if (t < 0.5) {
                        exp(-(t - 0.25) * 35.0) * 0.85 // finger snap
                    } else {
                        exp(-(t - 0.5) * 15.0) * 0.4
                    }
                    val sample = (noise * env * 22000).toInt().coerceIn(-32767, 32767).toShort()
                    buffer[i] = sample
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    /**
     * Synthesizes the iconic high-pitched double-tap Danfo minibus horn ("PEEP PEEP!").
     */
    fun playDanfoHorn() {
        audioScope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 260
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                val freq1 = 440.0 // A4
                val freq2 = 554.37 // C#5 - classic dual car horn interval

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / numSamples
                    // Two rapid beeps with short silence in middle
                    val env = when {
                        t < 0.42 -> 1.0 - (t / 0.42) * 0.1
                        t < 0.54 -> 0.0
                        else -> 1.0 - ((t - 0.54) / 0.46) * 0.1
                    }
                    val wave = (sin(2.0 * PI * freq1 * (i.toDouble() / sampleRate)) * 0.6 +
                                sin(2.0 * PI * freq2 * (i.toDouble() / sampleRate)) * 0.4) * env
                    buffer[i] = (wave * 25000).toInt().coerceIn(-32767, 32767).toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 180)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Synthesizes the low put-put rumble of a two-stroke Keke Marwa tricycle engine accelerating.
     */
    fun playKekeEngine() {
        audioScope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 280
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / numSamples
                    val pulse = sin(2.0 * PI * 28.0 * (i.toDouble() / sampleRate))
                    val buzz = sin(2.0 * PI * 140.0 * (i.toDouble() / sampleRate))
                    val noise = (Math.random() * 2.0 - 1.0) * 0.25
                    val env = exp(-t * 3.5)
                    val sample = ((pulse * 0.5 + buzz * 0.4 + noise) * env * 22000).toInt()
                    buffer[i] = sample.coerceIn(-32767, 32767).toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    /**
     * Synthesizes an authoritative rising/falling VIP motorcade police siren wail.
     */
    fun playVIPMotorcadeSiren() {
        audioScope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 420
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                var phase = 0.0
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / numSamples
                    // Siren frequency oscillates from 700Hz to 1200Hz
                    val currentFreq = 750.0 + 450.0 * sin(2.0 * PI * 2.5 * t)
                    phase += 2.0 * PI * currentFreq / sampleRate
                    val env = if (t < 0.1) t / 0.1 else if (t > 0.85) (1.0 - t) / 0.15 else 1.0
                    val sample = (sin(phase) * env * 22000).toInt()
                    buffer[i] = sample.coerceIn(-32767, 32767).toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (_: Exception) {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 300)
                } catch (_: Exception) {}
            }
        }
    }

    fun playCashChime() {
        playCashFlutter()
    }

    fun playTensionChord() {
        playTalkingDrum(0.75f)
    }

    fun playFujiCheer() {
        playCelebrationFanfare()
    }

    /**
     * Synthesizes a bright, inspiring ascending major arpeggio fanfare for positive events,
     * successful negotiations, breakthroughs, promotions, or electoral victories.
     * C5 (523.25Hz) -> E5 (659.25Hz) -> G5 (783.99Hz) -> C6 (1046.50Hz) -> E6 (1318.51Hz)
     */
    fun playPositiveEventSound() {
        if (!isSfxEnabled) return
        audioScope.launch {
            try {
                val sampleRate = 44100
                val notes = listOf(523.25, 659.25, 783.99, 1046.50, 1318.51)
                val noteDurationMs = 70
                val totalSamples = (sampleRate * ((noteDurationMs * notes.size + 120) / 1000.0)).toInt()
                val buffer = ShortArray(totalSamples)

                for (nIdx in notes.indices) {
                    val freq = notes[nIdx]
                    val startSample = (sampleRate * (nIdx * noteDurationMs / 1000.0)).toInt()
                    val durationSamples = (sampleRate * 0.20).toInt()
                    for (i in 0 until durationSamples) {
                        val idx = startSample + i
                        if (idx >= totalSamples) break
                        val t = i.toDouble() / durationSamples
                        val phase = 2.0 * PI * freq * (i.toDouble() / sampleRate)
                        val wave = sin(phase) + 0.35 * sin(2.0 * phase) + 0.15 * sin(3.0 * phase)
                        val env = exp(-t * 6.5)
                        val sampleVal = (wave * env * 22000 * sfxVolume).toInt()
                        val combined = buffer[idx] + sampleVal
                        buffer[idx] = combined.coerceIn(-32767, 32767).toShort()
                    }
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 180)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Synthesizes a sobering, ominous descending warning buzzer for negative events,
     * scandal disclosures, penalties, arrests, severe stress, or security crackdowns.
     * Descending dissonance: Eb4 (311.13Hz) -> D4 (293.66Hz) -> C#4 (277.18Hz) -> A3 (220.00Hz)
     */
    fun playNegativeEventSound() {
        if (!isSfxEnabled) return
        audioScope.launch {
            try {
                val sampleRate = 44100
                val notes = listOf(311.13, 293.66, 277.18, 220.00)
                val noteDurationMs = 85
                val totalSamples = (sampleRate * ((noteDurationMs * notes.size + 140) / 1000.0)).toInt()
                val buffer = ShortArray(totalSamples)

                for (nIdx in notes.indices) {
                    val freq = notes[nIdx]
                    val startSample = (sampleRate * (nIdx * noteDurationMs / 1000.0)).toInt()
                    val durationSamples = (sampleRate * 0.22).toInt()
                    for (i in 0 until durationSamples) {
                        val idx = startSample + i
                        if (idx >= totalSamples) break
                        val t = i.toDouble() / durationSamples
                        val phase = 2.0 * PI * freq * (i.toDouble() / sampleRate)
                        val wave = sin(phase) + 0.45 * sin(3.0 * phase) + 0.25 * sin(5.0 * phase)
                        val env = exp(-t * 5.0)
                        val sampleVal = (wave * env * 24000 * sfxVolume).toInt()
                        val combined = buffer[idx] + sampleVal
                        buffer[idx] = combined.coerceIn(-32767, 32767).toShort()
                    }
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 220)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Synthesizes a crisp, sparkling twin-coin pickup chime when gaining cash,
     * influence, or material political capital.
     */
    fun playResourceGainSound() {
        if (!isSfxEnabled) return
        audioScope.launch {
            try {
                val sampleRate = 44100
                val notes = listOf(880.0, 1318.51) // A5 -> E6 sparkling interval
                val totalSamples = (sampleRate * 0.28).toInt()
                val buffer = ShortArray(totalSamples)

                for (nIdx in notes.indices) {
                    val freq = notes[nIdx]
                    val startSample = (sampleRate * (nIdx * 0.075)).toInt()
                    val noteSamples = (sampleRate * 0.18).toInt()
                    for (i in 0 until noteSamples) {
                        val idx = startSample + i
                        if (idx >= totalSamples) break
                        val t = i.toDouble() / noteSamples
                        val phase = 2.0 * PI * freq * (i.toDouble() / sampleRate)
                        val wave = sin(phase) + 0.3 * sin(2.0 * phase)
                        val env = exp(-t * 9.0)
                        val sampleVal = (wave * env * 22000 * sfxVolume).toInt()
                        val combined = buffer[idx] + sampleVal
                        buffer[idx] = combined.coerceIn(-32767, 32767).toShort()
                    }
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 80)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Synthesizes a downward thud slide when spending resources, paying black tax,
     * sustaining damage, or suffering a loss of loyalty/clout.
     */
    fun playResourceLossSound() {
        if (!isSfxEnabled) return
        audioScope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 150
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                val startFreq = 260.0
                val endFreq = 140.0
                var phase = 0.0

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / numSamples
                    val freq = startFreq - (startFreq - endFreq) * t
                    phase += 2.0 * PI * freq / sampleRate
                    val env = exp(-t * 6.0)
                    val noise = (Math.random() * 2.0 - 1.0) * 0.15
                    val sample = ((sin(phase) + noise) * env * 22000 * sfxVolume).toInt()
                    buffer[i] = sample.coerceIn(-32767, 32767).toShort()
                }
                playPcmBuffer(buffer, sampleRate)
            } catch (e: Exception) {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_DTMF_0, 90)
                } catch (_: Exception) {}
            }
        }
    }

    // Convenient Aliases
    fun playPositiveEvent() = playPositiveEventSound()
    fun playNegativeEvent() = playNegativeEventSound()
    fun playPositiveChime() = playResourceGainSound()
    fun playNegativeBuzz() = playNegativeEventSound()

    private fun playPcmBuffer(buffer: ShortArray, sampleRate: Int) {
        if (!isSfxEnabled || sfxVolume <= 0.01f) return
        var track: AudioTrack? = null
        try {
            val bufferSize = buffer.size * 2
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val format = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            track = AudioTrack.Builder()
                .setAudioAttributes(attributes)
                .setAudioFormat(format)
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep((buffer.size * 1000L / sampleRate) + 20)
        } catch (_: Exception) {
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Exception) {}
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
