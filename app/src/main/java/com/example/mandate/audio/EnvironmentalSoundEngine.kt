package com.example.mandate.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import com.example.mandate.model.*
import kotlinx.coroutines.*
import kotlin.math.*
import kotlin.random.Random

/**
 * Procedural Environmental Sound Engine for "Mandate: The Long Game".
 * Synthesizes dynamic, rich ambient soundscapes in real-time based on:
 * - Time of Day (Morning rush, afternoon heat, evening go-slow, nighttime crickets)
 * - Location (Markets, Danfo parks, Presidential boulevards, Owambe strips, Niger Delta creeks)
 * - Regional State (Lagos, Abuja Villa, Rivers creeks, Kano markets)
 * - Weather conditions (Harmattan winds, tropical downpour)
 *
 * Runs 100% offline with zero external audio assets, using algorithmic PCM synthesis.
 */
class EnvironmentalSoundEngine {

    enum class SoundscapeProfile(
        val title: String,
        val subtitle: String,
        val iconEmoji: String
    ) {
        NIGHT_CRICKETS_AND_GEN(
            title = "Night Crickets & Generator Hum",
            subtitle = "Nocturnal cicadas chorus & distant 'I better pass my neighbor' hum",
            iconEmoji = "🦗"
        ),
        DAY_MARKET_MURMUR(
            title = "Bustling Open Market & Hawkers",
            subtitle = "Merchant banter, street hawker calls & generator drone",
            iconEmoji = "🏪"
        ),
        DANFO_TRANSIT_PARK(
            title = "Danfo Motor Park & Transit Hub",
            subtitle = "Diesel engine idle rumble, pneumatic air brakes & conductor whistles",
            iconEmoji = "🚌"
        ),
        EXECUTIVE_BOULEVARD(
            title = "Aso Rock & Executive Boulevard",
            subtitle = "Air-conditioned glass hum, asphalt breeze & distant convoy whoosh",
            iconEmoji = "🏛️"
        ),
        COASTAL_CREEK_WATERS(
            title = "Niger Delta Mangrove Creeks",
            subtitle = "Lapping river currents, boat thrum & distant oil flare drone",
            iconEmoji = "🛢️"
        ),
        OWAMBE_PARTY_GROOVE(
            title = "Owambe Canopy Groove",
            subtitle = "Live talking drum rhythms, brass warmups & party chatter",
            iconEmoji = "🎉"
        ),
        SAVANNAH_HARMATTAN_WIND(
            title = "Harmattan Dust & Savannah Wind",
            subtitle = "Dry desert winds, distant livestock bells & open frontier breeze",
            iconEmoji = "🌾"
        ),
        TROPICAL_DOWNPOUR(
            title = "Heavy Lagos Downpour",
            subtitle = "Relentless rain on corrugated zinc roofs & roaring street gutters",
            iconEmoji = "🌧️"
        )
    }

    private val engineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var streamingJob: Job? = null

    @Volatile
    private var isPlaying = false

    @Volatile
    var isMuted: Boolean = false
        private set

    @Volatile
    var masterVolume: Float = 0.55f // 0.0f to 1.0f

    @Volatile
    var currentProfile: SoundscapeProfile = SoundscapeProfile.DAY_MARKET_MURMUR
        private set

    private var audioTrack: AudioTrack? = null
    private val sampleRate = 22050 // Optimized sample rate for light CPU & rich environmental texture
    private val chunkSize = (sampleRate * 1.2).toInt() // ~1.2s audio chunks

    // Random generator with fixed seed state for smooth atmospheric noise
    private val random = Random(1337)
    private var continuousPhase = 0.0
    private var noiseFilterState = 0.0

    init {
        startStreaming()
    }

    /**
     * Determines the optimal environmental soundscape based on the player's current location,
     * state of origin, time of day, and weather.
     */
    fun evaluateSoundscape(
        timeOfDay: LagosTimeOfDay,
        district: LagosDistrict,
        state: NigeriaState,
        weather: LagosWeather
    ): SoundscapeProfile {
        // 1. Extreme Weather Override
        if (weather.isRaining && weather.rainIntensity > 0.5f) {
            return SoundscapeProfile.TROPICAL_DOWNPOUR
        }

        // 2. Nighttime Override (After 9 PM: Owambe night or Midnight breeze)
        if (timeOfDay.isNightLighting && timeOfDay == LagosTimeOfDay.MIDNIGHT_BREEZE) {
            return SoundscapeProfile.NIGHT_CRICKETS_AND_GEN
        }

        // 3. Owambe Nightlife
        if (timeOfDay == LagosTimeOfDay.OWAMBE_NIGHT || district == LagosDistrict.LEKKI_PHASE_ONE) {
            return SoundscapeProfile.OWAMBE_PARTY_GROOVE
        }

        // 4. Regional State Specifics
        if (state.id == NigeriaFederation.FCT_ABUJA.id || district == LagosDistrict.VICTORIA_ISLAND_MARINA) {
            return if (timeOfDay.isNightLighting) SoundscapeProfile.NIGHT_CRICKETS_AND_GEN
            else SoundscapeProfile.EXECUTIVE_BOULEVARD
        }

        if (state.id == NigeriaFederation.RIVERS_STATE.id) {
            return SoundscapeProfile.COASTAL_CREEK_WATERS
        }

        if (state.id == NigeriaFederation.BORNO_STATE.id || weather == LagosWeather.SUNNY_HARMATTAN) {
            return SoundscapeProfile.SAVANNAH_HARMATTAN_WIND
        }

        // 5. Transit & Market District Specifics
        return when (district) {
            LagosDistrict.OSHODI_OJUELEGBA -> SoundscapeProfile.DANFO_TRANSIT_PARK
            LagosDistrict.LAGOS_ISLAND_BALOGUN,
            LagosDistrict.IKEJA_COMPUTER_VILLAGE -> SoundscapeProfile.DAY_MARKET_MURMUR
            LagosDistrict.SURULERE_STREETS -> {
                if (timeOfDay == LagosTimeOfDay.EVENING_HOLDUP) SoundscapeProfile.OWAMBE_PARTY_GROOVE
                else SoundscapeProfile.DAY_MARKET_MURMUR
            }
            else -> {
                if (timeOfDay.isNightLighting) SoundscapeProfile.NIGHT_CRICKETS_AND_GEN
                else SoundscapeProfile.DAY_MARKET_MURMUR
            }
        }
    }

    /**
     * Updates the soundscape based on live game state parameters.
     */
    fun updateEnvironment(
        timeOfDay: LagosTimeOfDay,
        district: LagosDistrict,
        state: NigeriaState,
        weather: LagosWeather
    ) {
        val targetProfile = evaluateSoundscape(timeOfDay, district, state, weather)
        if (targetProfile != currentProfile) {
            currentProfile = targetProfile
            Log.d("EnvironmentalSoundEngine", "Transitioned to soundscape: ${targetProfile.title}")
        }
    }

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        return !isMuted
    }

    fun setMuted(muted: Boolean) {
        isMuted = muted
    }

    fun pause() {
        isPlaying = false
        try {
            audioTrack?.pause()
        } catch (_: Exception) {}
    }

    fun resume() {
        if (!isPlaying) {
            startStreaming()
        }
    }

    private fun startStreaming() {
        if (isPlaying) return
        isPlaying = true

        streamingJob?.cancel()
        streamingJob = engineScope.launch {
            initAudioTrack()
            val buffer = ShortArray(chunkSize)

            while (isActive && isPlaying) {
                if (isMuted || masterVolume <= 0.01f) {
                    // Sleep to conserve CPU when muted
                    delay(300)
                    continue
                }

                try {
                    // Synthesize chunk for current soundscape profile
                    synthesizeSoundscapeChunk(buffer, currentProfile, masterVolume)

                    // Write PCM buffer to AudioTrack
                    audioTrack?.write(buffer, 0, buffer.size)
                } catch (e: Exception) {
                    Log.w("EnvironmentalSoundEngine", "Streaming error: ${e.message}")
                    delay(500)
                }
            }
        }
    }

    private fun initAudioTrack() {
        try {
            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = max(minBufSize * 2, chunkSize * 2)

            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            val format = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            audioTrack?.release()
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(attributes)
                .setAudioFormat(format)
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
        } catch (e: Exception) {
            Log.e("EnvironmentalSoundEngine", "Failed to init AudioTrack: ${e.message}")
        }
    }

    /**
     * Synthesizes audio samples procedurally into the target buffer according to the profile.
     */
    private fun synthesizeSoundscapeChunk(
        buffer: ShortArray,
        profile: SoundscapeProfile,
        volume: Float
    ) {
        val count = buffer.size
        val effectiveVol = volume.coerceIn(0.0f, 1.0f) * 22000.0f

        when (profile) {
            SoundscapeProfile.NIGHT_CRICKETS_AND_GEN -> {
                // High-frequency cricket chirps (pulsed 4.8kHz & 5.2kHz) + Generator hum (50Hz + harmonics) + night breeze
                for (i in 0 until count) {
                    val t = (continuousPhase + i).toDouble() / sampleRate
                    
                    // Cricket pulse: 12 Hz modulation
                    val cricketMod = (sin(2.0 * PI * 13.0 * t) + 1.0) * 0.5
                    val cricketTone = if (cricketMod > 0.4) {
                        (sin(2.0 * PI * 4850.0 * t) * 0.6 + sin(2.0 * PI * 5220.0 * t) * 0.4) * (cricketMod - 0.4) * 1.6
                    } else 0.0

                    // Small random cricket variation
                    val cricketNoise = (random.nextDouble() - 0.5) * 0.08 * cricketMod

                    // Generator drone ("I better pass my neighbor" 55Hz fundamental + odd harmonics)
                    val genBuzz = 0.22 * sin(2.0 * PI * 55.0 * t) +
                            0.14 * sin(2.0 * PI * 110.0 * t) +
                            0.09 * sin(2.0 * PI * 165.0 * t)

                    // Night breeze soft pink noise
                    val rawNoise = (random.nextDouble() - 0.5)
                    noiseFilterState = noiseFilterState * 0.92 + rawNoise * 0.08
                    val breeze = noiseFilterState * 0.18

                    val sampleVal = (cricketTone * 0.45 + cricketNoise + genBuzz * 0.35 + breeze)
                    buffer[i] = (sampleVal * effectiveVol).toInt().coerceIn(-32767, 32767).toShort()
                }
            }

            SoundscapeProfile.DAY_MARKET_MURMUR -> {
                // Dense crowd chatter murmur + generator hum + subtle metallic coin/clink accents
                for (i in 0 until count) {
                    val t = (continuousPhase + i).toDouble() / sampleRate

                    // Crowd formant hum layers
                    val murmur = (sin(2.0 * PI * 210.0 * t) * sin(2.0 * PI * 3.2 * t) * 0.25 +
                            sin(2.0 * PI * 380.0 * t) * sin(2.0 * PI * 2.1 * t) * 0.20 +
                            sin(2.0 * PI * 680.0 * t) * sin(2.0 * PI * 4.5 * t) * 0.15)

                    // Filtered street noise
                    val rawNoise = (random.nextDouble() - 0.5)
                    noiseFilterState = noiseFilterState * 0.85 + rawNoise * 0.15

                    // Generator hum in the background (50Hz)
                    val gen = 0.16 * sin(2.0 * PI * 50.0 * t) + 0.10 * sin(2.0 * PI * 150.0 * t)

                    // Subtle distant horn beep every ~3 seconds
                    val hornMod = sin(2.0 * PI * 0.35 * t)
                    val horn = if (hornMod > 0.94) {
                        (sin(2.0 * PI * 520.0 * t) + sin(2.0 * PI * 650.0 * t)) * 0.18
                    } else 0.0

                    val sampleVal = (murmur * 0.40 + noiseFilterState * 0.22 + gen + horn)
                    buffer[i] = (sampleVal * effectiveVol).toInt().coerceIn(-32767, 32767).toShort()
                }
            }

            SoundscapeProfile.DANFO_TRANSIT_PARK -> {
                // Diesel engine idle rumble (42Hz) + pneumatic air brake bursts + conductor whistles
                for (i in 0 until count) {
                    val t = (continuousPhase + i).toDouble() / sampleRate

                    // Heavy diesel rumble with cylinder chug
                    val dieselChug = sin(2.0 * PI * 42.0 * t) * (1.0 + 0.35 * sin(2.0 * PI * 14.0 * t)) * 0.35

                    // Air brake hiss burst periodic
                    val brakeMod = sin(2.0 * PI * 0.28 * t)
                    val brakeHiss = if (brakeMod > 0.88) {
                        val raw = (random.nextDouble() - 0.5)
                        raw * (brakeMod - 0.88) * 2.5 * 0.30
                    } else 0.0

                    // Danfo conductor whistle blip
                    val whistleMod = sin(2.0 * PI * 0.45 * t + 1.2)
                    val whistle = if (whistleMod > 0.95) {
                        sin(2.0 * PI * 1850.0 * t) * 0.22
                    } else 0.0

                    // Background street roar
                    val rawNoise = (random.nextDouble() - 0.5)
                    noiseFilterState = noiseFilterState * 0.88 + rawNoise * 0.12

                    val sampleVal = (dieselChug + brakeHiss + whistle + noiseFilterState * 0.25)
                    buffer[i] = (sampleVal * effectiveVol).toInt().coerceIn(-32767, 32767).toShort()
                }
            }

            SoundscapeProfile.EXECUTIVE_BOULEVARD -> {
                // Smooth wide boulevard breeze + car passing whoosh + quiet air-conditioned hum
                for (i in 0 until count) {
                    val t = (continuousPhase + i).toDouble() / sampleRate

                    // Soft pink noise breeze
                    val rawNoise = (random.nextDouble() - 0.5)
                    noiseFilterState = noiseFilterState * 0.95 + rawNoise * 0.05
                    val breeze = noiseFilterState * 0.28

                    // High speed SUV passing whoosh (doppler sweep)
                    val whooshCycle = (t * 0.4) % 1.0
                    val whooshEnv = if (whooshCycle in 0.4..0.6) {
                        sin((whooshCycle - 0.4) / 0.2 * PI) * 0.26
                    } else 0.0
                    val whooshFreq = 700.0 - (whooshCycle * 350.0)
                    val carTone = sin(2.0 * PI * whooshFreq * t) * whooshEnv

                    // Clean low A/C glass hum
                    val acHum = 0.12 * sin(2.0 * PI * 60.0 * t)

                    val sampleVal = (breeze + carTone + acHum)
                    buffer[i] = (sampleVal * effectiveVol).toInt().coerceIn(-32767, 32767).toShort()
                }
            }

            SoundscapeProfile.COASTAL_CREEK_WATERS -> {
                // Niger Delta water lap waves (0.25Hz swell) + distant gas flare roar
                for (i in 0 until count) {
                    val t = (continuousPhase + i).toDouble() / sampleRate

                    // Water wave swell modulation
                    val waveCycle = (sin(2.0 * PI * 0.25 * t) + 1.0) * 0.5
                    val rawNoise = (random.nextDouble() - 0.5)
                    noiseFilterState = noiseFilterState * 0.90 + rawNoise * 0.10
                    val waterSwell = noiseFilterState * (0.15 + 0.25 * waveCycle)

                    // Distant gas flare combustion roar (38Hz - 70Hz low rumble)
                    val flareRoar = (sin(2.0 * PI * 46.0 * t) * 0.20 + sin(2.0 * PI * 68.0 * t) * 0.15) *
                            (0.85 + 0.15 * (random.nextDouble() - 0.5))

                    val sampleVal = (waterSwell + flareRoar)
                    buffer[i] = (sampleVal * effectiveVol).toInt().coerceIn(-32767, 32767).toShort()
                }
            }

            SoundscapeProfile.OWAMBE_PARTY_GROOVE -> {
                // Live talking drum & sakara kick rhythm + crowd cheering atmosphere
                for (i in 0 until count) {
                    val t = (continuousPhase + i).toDouble() / sampleRate

                    // Sakara bass kick beat on 120 BPM (every 0.5s)
                    val beatTime = t % 0.5
                    val kick = if (beatTime < 0.12) {
                        sin(2.0 * PI * (90.0 * exp(-beatTime * 15.0) + 40.0) * t) * exp(-beatTime * 12.0) * 0.35
                    } else 0.0

                    // Talking drum rhythmic tap on off-beat (every 0.25s)
                    val drumTime = (t + 0.15) % 0.25
                    val drum = if (drumTime < 0.08) {
                        sin(2.0 * PI * 220.0 * t) * exp(-drumTime * 18.0) * 0.25
                    } else 0.0

                    // Background party laughter & crowd buzz
                    val rawNoise = (random.nextDouble() - 0.5)
                    noiseFilterState = noiseFilterState * 0.88 + rawNoise * 0.12

                    val sampleVal = (kick + drum + noiseFilterState * 0.20)
                    buffer[i] = (sampleVal * effectiveVol).toInt().coerceIn(-32767, 32767).toShort()
                }
            }

            SoundscapeProfile.SAVANNAH_HARMATTAN_WIND -> {
                // Dry Sahelian wind whistling + sand grains
                for (i in 0 until count) {
                    val t = (continuousPhase + i).toDouble() / sampleRate

                    // Wind gust modulation
                    val gust = sin(2.0 * PI * 0.18 * t) * 0.4 + sin(2.0 * PI * 0.07 * t) * 0.6
                    val rawNoise = (random.nextDouble() - 0.5)
                    noiseFilterState = noiseFilterState * 0.94 + rawNoise * 0.06
                    val wind = noiseFilterState * (0.20 + 0.20 * gust)

                    // Whistling wind pitch
                    val whistle = sin(2.0 * PI * (420.0 + 80.0 * gust) * t) * 0.08 * (gust.coerceAtLeast(0.0))

                    val sampleVal = (wind + whistle)
                    buffer[i] = (sampleVal * effectiveVol).toInt().coerceIn(-32767, 32767).toShort()
                }
            }

            SoundscapeProfile.TROPICAL_DOWNPOUR -> {
                // Rain roaring on zinc roofs + storm gutter whoosh
                for (i in 0 until count) {
                    val t = (continuousPhase + i).toDouble() / sampleRate

                    // Dense noise for rain hiss
                    val rawNoise = (random.nextDouble() - 0.5)
                    noiseFilterState = noiseFilterState * 0.75 + rawNoise * 0.25
                    val rainHiss = noiseFilterState * 0.42

                    // Gutter water rush (low frequency churn)
                    val gutterRush = sin(2.0 * PI * 65.0 * t) * 0.15

                    // Distant thunder rumble occasionally
                    val thunderMod = sin(2.0 * PI * 0.08 * t)
                    val thunder = if (thunderMod > 0.92) {
                        sin(2.0 * PI * 35.0 * t) * 0.32 * (thunderMod - 0.92) * 12.0
                    } else 0.0

                    val sampleVal = (rainHiss + gutterRush + thunder)
                    buffer[i] = (sampleVal * effectiveVol).toInt().coerceIn(-32767, 32767).toShort()
                }
            }
        }

        continuousPhase += count
        if (continuousPhase > sampleRate * 1000) {
            continuousPhase %= (sampleRate * 100)
        }
    }

    fun release() {
        isPlaying = false
        streamingJob?.cancel()
        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (_: Exception) {}
        engineScope.cancel()
    }
}
