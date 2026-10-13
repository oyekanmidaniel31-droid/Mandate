package com.example.mandate.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.*
import kotlin.random.Random

/**
 * 3D Representation of a flying ₦1,000 banknote in physical space.
 */
data class SprayedNote3D(
    val id: Long,
    var x: Float, // -100 to 100
    var y: Float, // -100 to 100
    var z: Float, // height off floor (0 to 150)
    var vx: Float,
    var vy: Float,
    var vz: Float,
    var pitch: Float,
    var roll: Float,
    var yaw: Float,
    var rotationSpeed: Float,
    val noteType: String = "N1000", // "N1000", "USD100"
    var isLanded: Boolean = false,
    var alpha: Float = 1.0f
)

/**
 * Visual particle for celebratory sparkles and confetti.
 */
data class OwambeParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var size: Float,
    val color: Color,
    var alpha: Float = 1.0f,
    val shape: String = "STAR" // "STAR", "CIRCLE", "NAIRA"
)

/**
 * Floating hype text reaction.
 */
data class PraisePopup(
    val id: Long,
    val text: String,
    var x: Float,
    var y: Float,
    var alpha: Float = 1.0f,
    val color: Color = NaijaGold
)

enum class OwambeCameraAngle(val label: String) {
    SIMS_ISOMETRIC("3D Isometric"),
    STAGE_FRONT("Stage View"),
    DANCE_FLOOR("Floor Cam")
}

@Composable
fun LagosOwambe3DCanvas(
    playerName: String,
    playerOutfit: String,
    danceStyle: String,
    partyHype: Int, // 0 - 100
    sprayedNotes: List<SprayedNote3D>,
    particles: List<OwambeParticle>,
    praisePopups: List<PraisePopup>,
    livePraiseLyric: String,
    cameraAngle: OwambeCameraAngle,
    onCameraAngleChange: (OwambeCameraAngle) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    // Continuous beat animation loop (110 BPM Fuji tempo ~ 545ms per beat)
    val infiniteTransition = rememberInfiniteTransition(label = "OwambeLoop")
    val beatPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(545, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "beatPulse"
    )

    // Stage spotlight sweeps
    val spotlightAngle by infiniteTransition.animateFloat(
        initialValue = -35f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spotlightAngle"
    )

    // Crowd dance sway
    val dancerBounce by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(272, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dancerBounce"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(310.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF07120D))
            .testTag("lagos_owambe_3d_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val centerX = canvasW / 2f
            val centerY = canvasH * 0.58f

            // 1. Draw Marquee Tent Roof & Ambient Stage Lighting
            drawMarqueeAtmosphere(canvasW, canvasH, spotlightAngle, partyHype)

            // 2. Draw 3D Isometric Dance Floor & Stage
            drawIsometricStageFloor(centerX, centerY, canvasW, canvasH, cameraAngle, beatPulse)

            // 3. Draw Stacks of Bass Amplifiers Pulsing
            drawAmplifierStacks(centerX, centerY, beatPulse)

            // 4. Draw 3D Live Fuji Band (Lead Singer & Talking Drummer)
            drawLiveFujiBand(centerX, centerY, beatPulse, dancerBounce, playerName, livePraiseLyric, textMeasurer)

            // 5. Draw 3D VIP Party Guests & Dancers in Aso-Ebi
            drawVIPGuestsAndDancers(centerX, centerY, dancerBounce, partyHype)

            // 6. Draw 3D Player Avatar
            drawPlayerAvatar(centerX, centerY, dancerBounce, playerOutfit, danceStyle, partyHype)

            // 7. Draw 3D Sprayed Currency Notes (₦1,000 & USD)
            drawSprayedNotes3D(sprayedNotes, centerX, centerY, canvasW, canvasH)

            // 8. Draw Sparkling Particle Confetti
            drawOwambeParticles(particles, canvasW, canvasH)

            // 9. Draw Floating Praise Reaction Popups
            drawPraisePopups(praisePopups, textMeasurer)

            // 10. Draw Equalizer & Stage Banner Overlays
            drawStageEqualizerHUD(canvasW, canvasH, beatPulse, partyHype)
        }

        // Camera Angle Selector Overlay Chip at top right
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xCC0E2318),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Camera",
                    tint = NaijaGold,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = cameraAngle.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaijaGoldLight
                )
            }
        }

        // Live Hype Thermometer Tag at top left
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xCC160F05),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = SolidColor(if (partyHype > 75) AlertRed else NaijaGold)
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (partyHype > 80) "🔥 DORIME MADNESS" else if (partyHype > 50) "⚡ VIBEZ ACTIVE" else "🍾 CHILLED OWAMBE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = if (partyHype > 80) AlertRed else NaijaGoldLight
                )
                Text(
                    text = "$partyHype%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Canvas Drawing Routines for 3D Lagos Owambe Simulation
// -------------------------------------------------------------

private fun DrawScope.drawMarqueeAtmosphere(
    width: Float,
    height: Float,
    spotlightAngle: Float,
    hype: Int
) {
    // Background gradient: Rich emerald luxury marquee hall
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF041009),
                Color(0xFF0A2215),
                Color(0xFF07140D)
            )
        ),
        size = Size(width, height)
    )

    // Flowing Marquee Canopy Swags (Gold & Deep Emerald draped fabrics)
    val drapePath = Path().apply {
        moveTo(0f, 0f)
        cubicTo(width * 0.25f, 50f, width * 0.75f, 50f, width, 0f)
        lineTo(width, 25f)
        cubicTo(width * 0.75f, 65f, width * 0.25f, 65f, 0f, 25f)
        close()
    }
    drawPath(drapePath, brush = Brush.horizontalGradient(listOf(NaijaGold, Color(0xFFC7982E), NaijaGold)))

    // Hanging Festoon Fairy Lights
    val numLights = 14
    for (i in 0..numLights) {
        val lx = (width / numLights) * i
        val dropY = sin((i.toFloat() / numLights) * PI.toFloat()) * 35f + 18f
        // Wire
        drawLine(
            color = Color(0x55E5C158),
            start = Offset(lx, dropY - 8f),
            end = Offset(lx, dropY),
            strokeWidth = 1.5f
        )
        // Glowing bulb
        drawCircle(
            color = Color(0xFFFFE082),
            radius = 3.5f,
            center = Offset(lx, dropY)
        )
        drawCircle(
            color = Color(0x44FFD54F),
            radius = 7f,
            center = Offset(lx, dropY)
        )
    }

    // Dynamic Colored 3D Stage Spotlights
    val spotColors = listOf(
        Color(0x2800E5FF), // Cyan beam
        Color(0x33FFD700), // Gold beam
        Color(0x30E040FB)  // Magenta beam
    )

    val beamStartX = width / 2f
    val beamStartY = 10f

    spotColors.forEachIndexed { idx, color ->
        val offsetAngle = (spotlightAngle + (idx - 1) * 22f) * (PI.toFloat() / 180f)
        val beamTargetX = width / 2f + tan(offsetAngle) * (height * 0.9f)

        val beamPath = Path().apply {
            moveTo(beamStartX, beamStartY)
            lineTo(beamTargetX - 55f, height)
            lineTo(beamTargetX + 55f, height)
            close()
        }
        drawPath(
            beamPath,
            brush = Brush.verticalGradient(
                colors = listOf(color, Color.Transparent),
                startY = beamStartY,
                endY = height
            )
        )
    }
}

private fun DrawScope.drawIsometricStageFloor(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    cameraAngle: OwambeCameraAngle,
    beatPulse: Float
) {
    // Isometric Projection Math
    val angleScale = when (cameraAngle) {
        OwambeCameraAngle.SIMS_ISOMETRIC -> 0.55f
        OwambeCameraAngle.STAGE_FRONT -> 0.40f
        OwambeCameraAngle.DANCE_FLOOR -> 0.70f
    }

    // Stage Platform (Raised Wooden Dais at the back)
    val stagePath = Path().apply {
        moveTo(centerX - 160f, centerY - 45f)
        lineTo(centerX + 160f, centerY - 45f)
        lineTo(centerX + 190f, centerY - 10f)
        lineTo(centerX - 190f, centerY - 10f)
        close()
    }
    drawPath(
        stagePath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF1E3A28), Color(0xFF132B1C)),
            start = Offset(centerX - 160f, centerY - 45f),
            end = Offset(centerX + 190f, centerY - 10f)
        )
    )
    drawPath(stagePath, color = Color(0xFF3B684A), style = Stroke(width = 1.5f))

    // Main Dance Floor Isometric Tiles
    val gridSize = 6
    val tileW = 52f
    val tileH = 28f * angleScale

    for (row in 0 until gridSize) {
        for (col in 0 until gridSize) {
            val isoX = centerX + (col - row) * (tileW * 0.5f)
            val isoY = centerY + 10f + (col + row) * (tileH * 0.5f)

            val isAlternate = (row + col) % 2 == 0
            val tileColor = if (isAlternate) Color(0xFF0F2618) else Color(0xFF163723)

            val tilePath = Path().apply {
                moveTo(isoX, isoY - tileH * 0.5f)
                lineTo(isoX + tileW * 0.5f, isoY)
                lineTo(isoX, isoY + tileH * 0.5f)
                lineTo(isoX - tileW * 0.5f, isoY)
                close()
            }
            drawPath(tilePath, color = tileColor)
            drawPath(tilePath, color = Color(0x332E5E3D), style = Stroke(width = 0.8f))
        }
    }

    // Royal VIP Red Carpet Center Runway
    val carpetPath = Path().apply {
        moveTo(centerX - 24f, centerY - 10f)
        lineTo(centerX + 24f, centerY - 10f)
        lineTo(centerX + 52f, centerY + 110f)
        lineTo(centerX - 52f, centerY + 110f)
        close()
    }
    drawPath(
        carpetPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF881B1B), Color(0xFF570D0D))
        )
    )
    drawPath(carpetPath, color = NaijaGold, style = Stroke(width = 1.5f))
}

private fun DrawScope.drawAmplifierStacks(
    centerX: Float,
    centerY: Float,
    beatPulse: Float
) {
    // Bass amp bounce scale
    val pulseExpand = if (beatPulse < 0.25f) 3f * (1f - beatPulse / 0.25f) else 0f

    val ampPositions = listOf(
        Offset(centerX - 170f, centerY - 35f),
        Offset(centerX + 170f, centerY - 35f)
    )

    ampPositions.forEach { pos ->
        // Speaker cabinet body
        drawRoundRect(
            color = Color(0xFF141715),
            topLeft = Offset(pos.x - 16f, pos.y - 30f),
            size = Size(32f, 42f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
        )
        drawRoundRect(
            color = Color(0xFF2B332E),
            topLeft = Offset(pos.x - 16f, pos.y - 30f),
            size = Size(32f, 42f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f),
            style = Stroke(width = 1.2f)
        )

        // Twin subwoofer speaker cones (pumping with beat)
        val upperConeY = pos.y - 18f
        val lowerConeY = pos.y - 2f

        drawCircle(color = Color(0xFF262E29), radius = 6.5f + pulseExpand, center = Offset(pos.x, upperConeY))
        drawCircle(color = NaijaGold, radius = 2.5f, center = Offset(pos.x, upperConeY))

        drawCircle(color = Color(0xFF262E29), radius = 6.5f + pulseExpand, center = Offset(pos.x, lowerConeY))
        drawCircle(color = NaijaGold, radius = 2.5f, center = Offset(pos.x, lowerConeY))
    }
}

private fun DrawScope.drawLiveFujiBand(
    centerX: Float,
    centerY: Float,
    beatPulse: Float,
    bounce: Float,
    playerName: String,
    liveLyric: String,
    textMeasurer: TextMeasurer
) {
    val stageY = centerY - 28f

    // 1. Lead Fuji Praise Singer (Center stage)
    val singerX = centerX
    val singerY = stageY + bounce * 0.7f

    // Agbada Body (Rich Gold & White)
    val singerAgbada = Path().apply {
        moveTo(singerX, singerY - 22f)
        lineTo(singerX + 18f, singerY)
        lineTo(singerX + 12f, singerY + 18f)
        lineTo(singerX - 12f, singerY + 18f)
        lineTo(singerX - 18f, singerY)
        close()
    }
    drawPath(
        singerAgbada,
        brush = Brush.verticalGradient(listOf(NaijaGoldLight, Color(0xFFB8860B)))
    )
    drawPath(singerAgbada, color = Color(0xFF5D4037), style = Stroke(width = 1f))

    // Head, Fila Cap & Sunglasses
    drawCircle(color = Color(0xFF6D4C41), radius = 6f, center = Offset(singerX, singerY - 26f))
    // Gold Fila Cap
    drawRoundRect(
        color = NaijaGold,
        topLeft = Offset(singerX - 5.5f, singerY - 33f),
        size = Size(11f, 7f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f)
    )
    // Dark shades
    drawLine(
        color = Color.Black,
        start = Offset(singerX - 4f, singerY - 26f),
        end = Offset(singerX + 4f, singerY - 26f),
        strokeWidth = 2.5f
    )

    // Golden Microphone in hand
    drawLine(
        color = Color(0xFFCFD8DC),
        start = Offset(singerX + 6f, singerY - 14f),
        end = Offset(singerX + 8f, singerY - 24f),
        strokeWidth = 2f
    )
    drawCircle(color = NaijaGold, radius = 3.5f, center = Offset(singerX + 8f, singerY - 24f))

    // 2. Animated Talking Drummer (Alagbe) on the left
    val drummerX = centerX - 55f
    val drummerY = stageY - 2f - bounce * 0.5f

    // Body in Royal Purple Lace
    drawRoundRect(
        color = RoyalPurple,
        topLeft = Offset(drummerX - 8f, drummerY - 14f),
        size = Size(16f, 24f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
    )
    drawCircle(color = Color(0xFF5D4037), radius = 5.5f, center = Offset(drummerX, drummerY - 20f))

    // Hourglass Talking Drum (Gangan) under arm
    val drumHourglass = Path().apply {
        moveTo(drummerX + 6f, drummerY - 8f)
        lineTo(drummerX + 16f, drummerY - 8f)
        lineTo(drummerX + 9f, drummerY + 2f)
        lineTo(drummerX + 16f, drummerY + 12f)
        lineTo(drummerX + 6f, drummerY + 12f)
        lineTo(drummerX + 13f, drummerY + 2f)
        close()
    }
    drawPath(drumHourglass, color = Color(0xFFD7CCC8))
    drawPath(drumHourglass, color = Color(0xFF8D6E63), style = Stroke(width = 1f))
    // Curved drum stick (Opa Gangan)
    val stickAngle = (beatPulse * 2f * PI.toFloat()).coerceIn(0f, PI.toFloat())
    val stickEnd = Offset(drummerX + 12f + sin(stickAngle) * 6f, drummerY - 4f - cos(stickAngle) * 4f)
    drawLine(color = NaijaGold, start = Offset(drummerX + 22f, drummerY - 6f), end = stickEnd, strokeWidth = 1.8f)

    // 3. Sekere / Backing Vocalist on the right
    val sekereX = centerX + 55f
    val sekereY = stageY - 2f + bounce * 0.5f
    drawRoundRect(
        color = Color(0xFF1E5128),
        topLeft = Offset(sekereX - 8f, sekereY - 14f),
        size = Size(16f, 24f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
    )
    drawCircle(color = Color(0xFF4E342E), radius = 5.5f, center = Offset(sekereX, sekereY - 20f))
    // Sekere gourd with bead net
    drawCircle(color = Color(0xFFFFB74D), radius = 6f, center = Offset(sekereX + 8f, sekereY - 4f))
    drawCircle(color = Color(0xFF3E2723), radius = 6.2f, center = Offset(sekereX + 8f, sekereY - 4f), style = Stroke(width = 0.8f))

    // Live Praise Singer Speech Bubble
    val bubbleW = 210f
    val bubbleH = 34f
    val bubbleX = centerX - bubbleW / 2f
    val bubbleY = stageY - 72f

    drawRoundRect(
        color = Color(0xE60A1F14),
        topLeft = Offset(bubbleX, bubbleY),
        size = Size(bubbleW, bubbleH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
    )
    drawRoundRect(
        color = NaijaGold,
        topLeft = Offset(bubbleX, bubbleY),
        size = Size(bubbleW, bubbleH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f),
        style = Stroke(width = 1.2f)
    )
    // Little triangle pointer
    val pointerPath = Path().apply {
        moveTo(centerX - 5f, bubbleY + bubbleH)
        lineTo(centerX + 5f, bubbleY + bubbleH)
        lineTo(centerX, bubbleY + bubbleH + 6f)
        close()
    }
    drawPath(pointerPath, color = Color(0xE60A1F14))

    // Praise lyrics text inside bubble
    val measuredText = textMeasurer.measure(
        text = if (liveLyric.isNotEmpty()) liveLyric else "Olowo ti de! Chief $playerName!",
        style = TextStyle(
            color = NaijaGoldLight,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    )
    drawText(
        textLayoutResult = measuredText,
        topLeft = Offset(bubbleX + 8f, bubbleY + 8f)
    )
}

private fun DrawScope.drawVIPGuestsAndDancers(
    centerX: Float,
    centerY: Float,
    bounce: Float,
    partyHype: Int
) {
    // 1. VIP Madam in towering Gele (Left VIP Table)
    val madamX = centerX - 95f
    val madamY = centerY + 30f + bounce * 0.8f

    // Towering Royal Purple Gele (Head-tie)
    val gelePath = Path().apply {
        moveTo(madamX - 11f, madamY - 26f)
        lineTo(madamX + 11f, madamY - 26f)
        lineTo(madamX + 18f, madamY - 38f)
        lineTo(madamX - 18f, madamY - 38f)
        close()
    }
    drawPath(gelePath, brush = Brush.verticalGradient(listOf(NaijaGoldLight, RoyalPurple)))
    drawCircle(color = Color(0xFF6D4C41), radius = 5.5f, center = Offset(madamX, madamY - 22f))
    // Lace Dress
    drawRoundRect(
        color = RoyalPurple,
        topLeft = Offset(madamX - 10f, madamY - 14f),
        size = Size(20f, 32f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f)
    )
    // Gold necklace beads
    drawArc(
        color = NaijaGold,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(madamX - 5f, madamY - 16f),
        size = Size(10f, 6f),
        style = Stroke(width = 1.5f)
    )

    // 2. VIP Chief in White Agbada & Red Fila (Right VIP Table)
    val chiefX = centerX + 95f
    val chiefY = centerY + 30f - bounce * 0.8f

    // Red Chieftaincy Cap (Fila)
    drawRoundRect(
        color = AlertRed,
        topLeft = Offset(chiefX - 5.5f, chiefY - 32f),
        size = Size(11f, 8f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f)
    )
    drawCircle(color = Color(0xFF5D4037), radius = 6f, center = Offset(chiefX, chiefY - 22f))
    // White Agbada
    val chiefAgbada = Path().apply {
        moveTo(chiefX, chiefY - 16f)
        lineTo(chiefX + 16f, chiefY - 2f)
        lineTo(chiefX + 12f, chiefY + 28f)
        lineTo(chiefX - 12f, chiefY + 28f)
        lineTo(chiefX - 16f, chiefY - 2f)
        close()
    }
    drawPath(chiefAgbada, color = Color(0xFFE0E0E0))
    drawPath(chiefAgbada, color = NaijaGold, style = Stroke(width = 1.2f))
    // Beaded Wristbands
    drawCircle(color = AlertRed, radius = 2.5f, center = Offset(chiefX - 14f, chiefY + 2f))
    drawCircle(color = AlertRed, radius = 2.5f, center = Offset(chiefX + 14f, chiefY + 2f))

    // VIP Banquet Tables with Champagne & Jollof Rice
    listOf(Offset(centerX - 130f, centerY + 50f), Offset(centerX + 130f, centerY + 50f)).forEach { tablePos ->
        // Table Cloth
        drawOval(
            color = Color(0xFF1E3326),
            topLeft = Offset(tablePos.x - 22f, tablePos.y - 10f),
            size = Size(44f, 20f)
        )
        drawOval(
            color = CardBorderGold,
            topLeft = Offset(tablePos.x - 22f, tablePos.y - 10f),
            size = Size(44f, 20f),
            style = Stroke(width = 1f)
        )
        // Moët Champagne Bucket with gold foil top
        drawRect(
            color = Color(0xFFCFD8DC),
            topLeft = Offset(tablePos.x - 5f, tablePos.y - 16f),
            size = Size(10f, 10f)
        )
        drawCircle(color = NaijaGold, radius = 2f, center = Offset(tablePos.x, tablePos.y - 18f))
    }
}

private fun DrawScope.drawPlayerAvatar(
    centerX: Float,
    centerY: Float,
    bounce: Float,
    outfit: String,
    danceStyle: String,
    partyHype: Int
) {
    // Player is positioned center stage in the VIP spotlight
    val playerX = centerX
    val playerY = centerY + 55f + bounce

    // Spotlight pool under player's feet
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x77FFD700), Color.Transparent),
            center = Offset(playerX, playerY + 28f),
            radius = 35f
        ),
        topLeft = Offset(playerX - 35f, playerY + 16f),
        size = Size(70f, 24f)
    )

    // Shadow on dance floor
    drawOval(
        color = Color(0x99050C08),
        topLeft = Offset(playerX - 16f, playerY + 26f),
        size = Size(32f, 10f)
    )

    val (robeColorPrimary, robeColorAccent) = when (outfit) {
        "ROYAL_LACE" -> Pair(RoyalPurple, NaijaGold)
        "SENATOR_SUIT" -> Pair(Color(0xFF212121), NaijaGold)
        "STREET_ICE" -> Pair(Color(0xFF1565C0), Color(0xFF00E5FF))
        else -> Pair(NaijaGold, EmeraldPrimary) // "EMERALD_AGBADA" default
    }

    // Outfit Body / Agbada
    val playerBody = Path().apply {
        moveTo(playerX, playerY - 26f)
        lineTo(playerX + 22f, playerY - 8f)
        lineTo(playerX + 16f, playerY + 24f)
        lineTo(playerX - 16f, playerY + 24f)
        lineTo(playerX - 22f, playerY - 8f)
        close()
    }
    drawPath(
        playerBody,
        brush = Brush.verticalGradient(listOf(robeColorPrimary, robeColorAccent))
    )
    drawPath(playerBody, color = NaijaGold, style = Stroke(width = 1.5f))

    // Head, Skin & Cap
    drawCircle(color = Color(0xFF6D4C41), radius = 7f, center = Offset(playerX, playerY - 32f))
    // Fila Cap
    drawRoundRect(
        color = robeColorAccent,
        topLeft = Offset(playerX - 6.5f, playerY - 40f),
        size = Size(13f, 9f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f)
    )
    // Gold Chain Necklace
    drawArc(
        color = NaijaGold,
        startAngle = 10f,
        sweepAngle = 160f,
        useCenter = false,
        topLeft = Offset(playerX - 7f, playerY - 28f),
        size = Size(14f, 10f),
        style = Stroke(width = 2f)
    )

    // Hand holding crispy banknote bundle, reaching out to spray!
    val armGesture = if (danceStyle == "GBESE") -18f else -8f
    drawLine(
        color = Color(0xFF6D4C41),
        start = Offset(playerX + 12f, playerY - 14f),
        end = Offset(playerX + 26f, playerY + armGesture),
        strokeWidth = 4f
    )
    // Bundle of ₦1,000 cash in hand
    drawRoundRect(
        color = Color(0xFF8E24AA), // ₦1,000 Purple
        topLeft = Offset(playerX + 22f, playerY + armGesture - 6f),
        size = Size(14f, 8f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f)
    )
    drawRoundRect(
        color = Color(0xFF00E5FF), // Cyan band
        topLeft = Offset(playerX + 26f, playerY + armGesture - 6f),
        size = Size(6f, 8f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1f)
    )
}

private fun DrawScope.drawSprayedNotes3D(
    notes: List<SprayedNote3D>,
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float
) {
    notes.forEach { note ->
        if (note.alpha <= 0.01f) return@forEach

        // 3D Isometric Projection
        // World coordinates: x (-100 to 100), y (-100 to 100), z (height off floor 0 to 150)
        val isoX = centerX + (note.x - note.y) * 1.4f
        val floorY = centerY + 50f + (note.x + note.y) * 0.7f
        val projectedY = floorY - note.z * 1.1f

        // Draw drop shadow on floor if note is airborne
        if (note.z > 5f) {
            val shadowAlpha = (1f - (note.z / 150f).coerceIn(0f, 0.8f)) * 0.45f * note.alpha
            drawOval(
                color = Color.Black.copy(alpha = shadowAlpha),
                topLeft = Offset(isoX - 10f, floorY - 3f),
                size = Size(20f, 6f)
            )
        }

        // Note Dimensions in 3D perspective
        val perspectiveScale = (1f - (note.y / 250f)).coerceIn(0.6f, 1.4f)
        val noteW = 28f * perspectiveScale
        val noteH = 14f * perspectiveScale

        // 3D rotation transform approximation
        val cosRoll = cos(note.roll)
        val sinPitch = sin(note.pitch)

        val noteColor = if (note.noteType == "USD100") Color(0xFF2E7D32) else Color(0xFF7B1FA2)
        val accentColor = if (note.noteType == "USD100") Color(0xFFA5D6A7) else Color(0xFF00E5FF)

        val matrix = Matrix().apply {
            translate(isoX, projectedY)
            rotateZ(note.yaw * (180f / PI.toFloat()))
        }

        // Draw crisp banknote polygon
        val notePath = Path().apply {
            moveTo(-noteW / 2f, (-noteH / 2f) * cosRoll)
            lineTo(noteW / 2f, (-noteH / 2f) * cosRoll + sinPitch * 4f)
            lineTo(noteW / 2f, (noteH / 2f) * cosRoll + sinPitch * 4f)
            lineTo(-noteW / 2f, (noteH / 2f) * cosRoll)
            close()
        }

        drawPath(
            path = notePath.apply { transform(matrix) },
            color = noteColor.copy(alpha = note.alpha)
        )

        // Banknote Inner Border & Emblem
        val innerPath = Path().apply {
            moveTo((-noteW / 2f) + 2f, (-noteH / 2f) * cosRoll + 1.5f)
            lineTo((noteW / 2f) - 2f, (-noteH / 2f) * cosRoll + sinPitch * 4f + 1.5f)
            lineTo((noteW / 2f) - 2f, (noteH / 2f) * cosRoll + sinPitch * 4f - 1.5f)
            lineTo((-noteW / 2f) + 2f, (noteH / 2f) * cosRoll - 1.5f)
            close()
        }
        drawPath(
            path = innerPath.apply { transform(matrix) },
            color = accentColor.copy(alpha = note.alpha),
            style = Stroke(width = 0.8f)
        )
    }
}

private fun DrawScope.drawOwambeParticles(
    particles: List<OwambeParticle>,
    width: Float,
    height: Float
) {
    particles.forEach { p ->
        if (p.alpha <= 0.01f) return@forEach

        when (p.shape) {
            "STAR" -> {
                // 4-pointed golden sparkle
                val r = p.size
                val starPath = Path().apply {
                    moveTo(p.x, p.y - r)
                    lineTo(p.x + r * 0.3f, p.y - r * 0.3f)
                    lineTo(p.x + r, p.y)
                    lineTo(p.x + r * 0.3f, p.y + r * 0.3f)
                    lineTo(p.x, p.y + r)
                    lineTo(p.x - r * 0.3f, p.y + r * 0.3f)
                    lineTo(p.x - r, p.y)
                    lineTo(p.x - r * 0.3f, p.y - r * 0.3f)
                    close()
                }
                drawPath(starPath, color = p.color.copy(alpha = p.alpha))
            }
            "NAIRA" -> {
                // Glowing Currency Orb
                drawCircle(color = p.color.copy(alpha = p.alpha), radius = p.size, center = Offset(p.x, p.y))
                drawCircle(color = Color.White.copy(alpha = p.alpha), radius = p.size * 0.4f, center = Offset(p.x, p.y))
            }
            else -> {
                drawCircle(color = p.color.copy(alpha = p.alpha), radius = p.size, center = Offset(p.x, p.y))
            }
        }
    }
}

private fun DrawScope.drawPraisePopups(
    popups: List<PraisePopup>,
    textMeasurer: TextMeasurer
) {
    popups.forEach { popup ->
        if (popup.alpha <= 0.01f) return@forEach

        val textLayout = textMeasurer.measure(
            text = popup.text,
            style = TextStyle(
                color = popup.color.copy(alpha = popup.alpha),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
        )

        // Drop shadow for readability
        drawText(
            textLayoutResult = textLayout,
            topLeft = Offset(popup.x + 1f, popup.y + 1f)
        )
        drawText(
            textLayoutResult = textLayout,
            topLeft = Offset(popup.x, popup.y)
        )
    }
}

private fun DrawScope.drawStageEqualizerHUD(
    width: Float,
    height: Float,
    beatPulse: Float,
    partyHype: Int
) {
    // Stage Floor Fuji Beat Waveform Indicator
    val numBars = 16
    val barW = 6f
    val barSpacing = 4f
    val startX = width / 2f - (numBars * (barW + barSpacing)) / 2f
    val bottomY = height - 10f

    for (i in 0 until numBars) {
        val barX = startX + i * (barW + barSpacing)
        val phase = (i.toFloat() / numBars) * PI.toFloat() * 2f
        val pulseHeight = (sin(phase + beatPulse * 2f * PI.toFloat()).absoluteValue * 22f + 4f) * (partyHype / 100f).coerceAtLeast(0.4f)

        val barColor = if (partyHype > 75) {
            if (i % 2 == 0) AlertRed else NaijaGold
        } else {
            if (i % 2 == 0) NaijaGold else SuccessGreen
        }

        drawRoundRect(
            color = barColor,
            topLeft = Offset(barX, bottomY - pulseHeight),
            size = Size(barW, pulseHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f)
        )
    }
}
