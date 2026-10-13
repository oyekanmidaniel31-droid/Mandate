package com.example.mandate.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mandate.audio.FujiAudioEngine
import com.example.mandate.model.Character
import com.example.mandate.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun OwambeScreen(
    character: Character,
    totalSprayedTonight: Long,
    onSprayCash: (Long, Boolean) -> Unit,
    onOrderRefreshment: (OwambeRefreshment) -> Unit,
    onInteractVIP: (OwambeVIPGuest, OwambeVIPAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val vibrator = remember {
        try {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } catch (_: Exception) {
            null
        }
    }

    // Fuji Audio Engine
    val audioEngine = remember { FujiAudioEngine() }
    DisposableEffect(Unit) {
        onDispose {
            audioEngine.release()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    // 3D Canvas dynamic states
    var cameraAngle by remember { mutableStateOf(OwambeCameraAngle.SIMS_ISOMETRIC) }
    var currentOutfit by remember { mutableStateOf("EMERALD_AGBADA") }
    var currentDanceStyle by remember { mutableStateOf("ZANKU") }
    var partyHype by remember { mutableIntStateOf(55) }

    // Notes, particles & popups
    val sprayedNotes = remember { mutableStateListOf<SprayedNote3D>() }
    val particles = remember { mutableStateListOf<OwambeParticle>() }
    val praisePopups = remember { mutableStateListOf<PraisePopup>() }

    // Live Fuji praise singer lyric rotation
    val praiseLyrics = remember {
        listOf(
            "Olowo gbogbo Naijaland ti wole! Chief ${character.name}!",
            "E ba mi gbe kini yi fun Senior Man! Money na water!",
            "Omo Baba Oloye! Agba Baller! Dorime!",
            "Heavy Spender! Otunba! Awa l'oniyan!",
            "E f'owo se party o! Spray the crisp notes!",
            "Nobody badder! Lakos big boy on the dance floor!"
        )
    }
    var currentLyricIndex by remember { mutableIntStateOf(0) }

    // Periodic Fuji praise singer lyric rotation
    LaunchedEffect(Unit) {
        while (true) {
            delay(4500)
            currentLyricIndex = (currentLyricIndex + 1) % praiseLyrics.size
        }
    }

    // Physical simulation tick for 3D notes, particles, and popups
    LaunchedEffect(Unit) {
        while (true) {
            delay(33) // ~30 fps physics loop

            // Update 3D Sprayed Notes
            val noteIterator = sprayedNotes.iterator()
            while (noteIterator.hasNext()) {
                val note = noteIterator.next()
                if (!note.isLanded) {
                    note.x += note.vx
                    note.y += note.vy
                    note.z += note.vz
                    note.vz -= 0.85f // gravity
                    note.pitch += note.rotationSpeed
                    note.roll += note.rotationSpeed * 0.7f

                    // Floor collision
                    if (note.z <= 0f) {
                        note.z = 0f
                        note.vz = 0f
                        note.vx = 0f
                        note.vy = 0f
                        note.isLanded = true
                    }
                } else {
                    note.alpha -= 0.008f
                    if (note.alpha <= 0.05f) {
                        noteIterator.remove()
                    }
                }
            }

            // Update Particles
            val particleIterator = particles.iterator()
            while (particleIterator.hasNext()) {
                val p = particleIterator.next()
                p.x += p.vx
                p.y += p.vy
                p.vy += 0.2f // gentle gravity
                p.alpha -= 0.035f
                if (p.alpha <= 0.05f) {
                    particleIterator.remove()
                }
            }

            // Update Praise Popups
            val popupIterator = praisePopups.iterator()
            while (popupIterator.hasNext()) {
                val popup = popupIterator.next()
                popup.y -= 1.8f // float upwards
                popup.alpha -= 0.025f
                if (popup.alpha <= 0.05f) {
                    popupIterator.remove()
                }
            }
        }
    }

    // Trigger Haptic Feedback
    fun triggerHaptic(durationMs: Long = 40) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    // Spawn 3D Note Helper
    fun spawn3DNote(noteType: String = "N1000", speedMultiplier: Float = 1.0f) {
        val note = SprayedNote3D(
            id = System.currentTimeMillis() + Random.nextLong(1000),
            x = Random.nextFloat() * 20f - 10f,
            y = 20f + Random.nextFloat() * 15f,
            z = 75f,
            vx = (Random.nextFloat() * 4f - 2f) * speedMultiplier,
            vy = (-3.5f - Random.nextFloat() * 3.5f) * speedMultiplier,
            vz = (4.5f + Random.nextFloat() * 3.5f) * speedMultiplier,
            pitch = Random.nextFloat() * 6.28f,
            roll = Random.nextFloat() * 6.28f,
            yaw = Random.nextFloat() * 6.28f,
            rotationSpeed = 0.12f + Random.nextFloat() * 0.18f,
            noteType = noteType
        )
        sprayedNotes.add(note)
    }

    // Spawn Confetti Sparkles
    fun spawnSparkles(count: Int = 12) {
        for (i in 0 until count) {
            particles.add(
                OwambeParticle(
                    x = 400f + (Random.nextFloat() * 200f - 100f),
                    y = 350f + (Random.nextFloat() * 120f - 60f),
                    vx = (Random.nextFloat() * 8f - 4f),
                    vy = (-4f - Random.nextFloat() * 6f),
                    size = 3.5f + Random.nextFloat() * 4.5f,
                    color = if (i % 2 == 0) NaijaGold else Color(0xFF00E5FF),
                    shape = if (i % 3 == 0) "STAR" else "NAIRA"
                )
            )
        }
    }

    // Spawn Float Popup
    fun spawnPopup(text: String, color: Color = NaijaGold) {
        praisePopups.add(
            PraisePopup(
                id = System.currentTimeMillis() + Random.nextLong(1000),
                text = text,
                x = 350f + (Random.nextFloat() * 140f - 70f),
                y = 320f,
                color = color
            )
        )
    }

    // Execute Spray Action
    fun executeSpray(amount: Long, count: Int, noteType: String) {
        if (character.naraBalance < amount) return

        triggerHaptic(if (amount >= 100_000) 80 else 40)

        // Sound cue
        if (amount >= 150_000) {
            audioEngine.playCelebrationFanfare()
        } else {
            audioEngine.playTalkingDrum(pitchMultiplier = 1.0f + (Random.nextFloat() * 0.3f))
            audioEngine.playCashFlutter()
        }

        // Spawn notes in 3D
        repeat(count) {
            spawn3DNote(noteType)
        }
        spawnSparkles(count * 6)

        partyHype = (partyHype + (count * 2)).coerceAtMost(100)

        val popupLabel = when {
            amount >= 150_000 -> "🍾 DORIME! +35 CLOUT!"
            amount >= 10_000 -> "💥 BUNDLE! +15 CLOUT!"
            else -> "💸 ₦1,000! +2 CLOUT!"
        }
        spawnPopup(popupLabel, if (amount >= 150_000) SuccessGreen else NaijaGold)

        onSprayCash(amount, false)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("owambe_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // High-Fidelity Cinematic Owambe Party Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_owambe_party_1791645917306),
                    contentDescription = "Lagos Owambe Extravaganza",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x7707180F),
                                    Color(0xFA07140D)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = NaijaGold
                    ) {
                        Text(
                            text = "HIGH SOCIETY LAGOS LIFE • LIVE OWAMBE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = DeepCharcoal,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "VICTORIA ISLAND EVENT MARQUEE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Live Fuji Praise Singers • Spraying ₦1,000 Notes • High Clout",
                        fontSize = 11.sp,
                        color = NaijaGoldLight
                    )
                }
            }
        }

        // 3D Isometric Lagos Owambe Simulation Canvas
        item {
            LagosOwambe3DCanvas(
                playerName = character.name,
                playerOutfit = currentOutfit,
                danceStyle = currentDanceStyle,
                partyHype = partyHype,
                sprayedNotes = sprayedNotes,
                particles = particles,
                praisePopups = praisePopups,
                livePraiseLyric = praiseLyrics[currentLyricIndex],
                cameraAngle = cameraAngle,
                onCameraAngleChange = { cameraAngle = it }
            )
        }

        // Live Praise Singer Ticker
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF132D20),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎤", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = praiseLyrics[currentLyricIndex],
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaijaGoldLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Sims Interaction Hub: Spray Controls, VIP Mingling, Bar, and Wardrobe
        item {
            OwambeSimsPanel(
                naraBalance = character.naraBalance,
                streetClout = character.streetClout,
                socialInfluence = character.politicalCapital,
                partyHype = partyHype,
                totalSprayedTonight = totalSprayedTonight,
                currentOutfit = currentOutfit,
                currentDanceStyle = currentDanceStyle,
                onSpraySingleNote = {
                    executeSpray(amount = 1000L, count = 1, noteType = "N1000")
                },
                onSprayBundle = {
                    executeSpray(amount = 10_000L, count = 6, noteType = "N1000")
                },
                onSprayUSD = {
                    executeSpray(amount = 150_000L, count = 8, noteType = "USD100")
                },
                onSelectDanceStyle = {
                    currentDanceStyle = it
                    audioEngine.playBassKick()
                    triggerHaptic(30)
                    spawnPopup("DANCE: $it!", NaijaGoldLight)
                },
                onSelectOutfit = {
                    currentOutfit = it
                    triggerHaptic(30)
                    spawnPopup("DRIP: $it!", NaijaGoldLight)
                },
                onOrderRefreshment = { item ->
                    triggerHaptic(50)
                    audioEngine.playCelebrationFanfare()
                    spawnPopup("ENJOYING ${item.emoji}!", SuccessGreen)
                    onOrderRefreshment(item)
                },
                onInteractVIP = { guest, action ->
                    triggerHaptic(50)
                    audioEngine.playTalkingDrum(1.2f)
                    spawnPopup("${guest.name}: +${action.cloutReward} CLOUT!", NaijaGoldLight)
                    onInteractVIP(guest, action)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
