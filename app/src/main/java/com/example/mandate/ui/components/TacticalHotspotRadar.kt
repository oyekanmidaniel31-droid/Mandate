package com.example.mandate.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mandate.model.SecurityLevel
import com.example.ui.theme.*

data class HotspotZone(
    val id: String,
    val name: String,
    val regionTag: String,
    val threatTier: String,
    val angerContribution: Int,
    val normX: Float, // 0.0 to 1.0 relative map coordinate
    val normY: Float,
    val iconEmoji: String,
    val tacticalReport: String
)

val defaultHotspots = listOf(
    HotspotZone(
        id = "lakos",
        name = "Lakos Megacity",
        regionTag = "South-West Coast",
        threatTier = "CRITICAL",
        angerContribution = 88,
        normX = 0.22f,
        normY = 0.78f,
        iconEmoji = "🚌",
        tacticalReport = "Danfo driver strikes, youth highway barricades, major port terminal slowdown."
    ),
    HotspotZone(
        id = "abuka",
        name = "Abuka Federal Capital",
        regionTag = "Central Power Core",
        threatTier = "GUARDED",
        angerContribution = 65,
        normX = 0.50f,
        normY = 0.48f,
        iconEmoji = "🏛️",
        tacticalReport = "Presidential Villa perimeter locked down; student unions picketing National Assembly."
    ),
    HotspotZone(
        id = "creeks",
        name = "Niger Delta Creeks",
        regionTag = "South-South Hydrocarbon",
        threatTier = "SEVERE",
        angerContribution = 78,
        normX = 0.44f,
        normY = 0.88f,
        iconEmoji = "⛽",
        tacticalReport = "Community sit-ins at flow stations demanding sovereign resource derivations."
    ),
    HotspotZone(
        id = "savannah",
        name = "Kano Savannah Belt",
        regionTag = "Northern Commercial Corridor",
        threatTier = "ELEVATED",
        angerContribution = 60,
        normX = 0.58f,
        normY = 0.20f,
        iconEmoji = "🌾",
        tacticalReport = "Grain market trader protests over diesel freight tariffs and border inspections."
    ),
    HotspotZone(
        id = "eastern",
        name = "Onitsha-Aba Industrial Axis",
        regionTag = "South-East Trade Corridor",
        threatTier = "TENSE",
        angerContribution = 72,
        normX = 0.68f,
        normY = 0.72f,
        iconEmoji = "🏭",
        tacticalReport = "Markets shuttered in general protest; container transport union sit-at-home."
    )
)

/**
 * Interactive Graphical Tactical Hotspot Map
 * Simulates a high-tech National Defense & Intelligence Operations radar map
 * of the Federal Republic of Naijaland.
 */
@Composable
fun TacticalHotspotRadar(
    nationalAnger: Int,
    securityLevel: SecurityLevel,
    modifier: Modifier = Modifier
) {
    var selectedHotspot by remember { mutableStateOf(defaultHotspots[0]) }

    // Pulsing radar sweep animation
    val infiniteTransition = rememberInfiniteTransition(label = "radar_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tactical_hotspot_radar"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(NaijaGold, AlertRed, NaijaGreenLight))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (nationalAnger > 70) AlertRed else SuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NAIJALAND TACTICAL CONFLICT MAP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = NaijaGold,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF2A1515)
                ) {
                    Text(
                        text = "HOTSPOTS: ${defaultHotspots.size} ACTIVE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlertRed,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // The Graphical Tactical Radar Map Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF071710))
                    .border(1.dp, Color(0xFF163E2B), RoundedCornerShape(12.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw tactical grid lines (military radar style)
                    val gridCols = 8
                    val gridRows = 5
                    for (c in 1 until gridCols) {
                        val x = w * (c.toFloat() / gridCols)
                        drawLine(
                            color = Color(0x18288B5B),
                            start = Offset(x, 0f),
                            end = Offset(x, h),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    for (r in 1 until gridRows) {
                        val y = h * (r.toFloat() / gridRows)
                        drawLine(
                            color = Color(0x18288B5B),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Stylized geopolitical coastline outline of Naijaland
                    val mapPath = Path().apply {
                        moveTo(w * 0.15f, h * 0.35f)
                        lineTo(w * 0.45f, h * 0.15f)
                        lineTo(w * 0.85f, h * 0.25f)
                        lineTo(w * 0.82f, h * 0.65f)
                        lineTo(w * 0.60f, h * 0.90f)
                        lineTo(w * 0.40f, h * 0.88f)
                        lineTo(w * 0.20f, h * 0.80f)
                        lineTo(w * 0.12f, h * 0.60f)
                        close()
                    }

                    // Draw map perimeter
                    drawPath(
                        path = mapPath,
                        color = Color(0x22288B5B),
                        style = androidx.compose.ui.graphics.drawscope.Fill
                    )
                    drawPath(
                        path = mapPath,
                        color = Color(0x44E5C158),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Draw connecting geopolitical corridors
                    defaultHotspots.forEachIndexed { i, a ->
                        defaultHotspots.forEachIndexed { j, b ->
                            if (i < j && (i + j) % 2 == 0) {
                                drawLine(
                                    color = Color(0x22E5C158),
                                    start = Offset(w * a.normX, h * a.normY),
                                    end = Offset(w * b.normX, h * b.normY),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                        }
                    }

                    // Draw glowing beacon circles for hotspots
                    defaultHotspots.forEach { spot ->
                        val spotX = w * spot.normX
                        val spotY = h * spot.normY
                        val isSelected = spot.id == selectedHotspot.id

                        val baseColor = when (spot.threatTier) {
                            "CRITICAL" -> AlertRed
                            "SEVERE" -> WarningOrange
                            "ELEVATED" -> NaijaGold
                            else -> InfoBlue
                        }

                        // Outer pulsing halo
                        drawCircle(
                            color = baseColor.copy(alpha = if (isSelected) pulseAlpha else 0.25f),
                            radius = if (isSelected) 18.dp.toPx() else 10.dp.toPx(),
                            center = Offset(spotX, spotY)
                        )

                        // Core beacon node
                        drawCircle(
                            color = baseColor,
                            radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                            center = Offset(spotX, spotY)
                        )

                        // Center white hotcore
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = Offset(spotX, spotY)
                        )
                    }
                }

                // Interactive tap buttons positioned over nodes
                defaultHotspots.forEach { spot ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = (200.dp * spot.normX * 1.5f).coerceAtLeast(0.dp),
                                top = (200.dp * spot.normY).coerceAtLeast(0.dp)
                            )
                    )
                }

                // Hotspot clickable chips along the bottom
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color(0xCC05100B))
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    defaultHotspots.forEach { spot ->
                        val isSelected = spot.id == selectedHotspot.id
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) NaijaGreenDark else Color.Transparent,
                            border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NaijaGold)) else null,
                            modifier = Modifier
                                .clickable { selectedHotspot = spot }
                                .padding(horizontal = 2.dp)
                        ) {
                            Text(
                                text = "${spot.iconEmoji} ${spot.name.split(" ").first()}",
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                color = if (isSelected) NaijaGoldLight else TextSecondaryLight,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selected Hotspot Tactical Intelligence Panel
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0D2419),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E4D35)))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = selectedHotspot.iconEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = selectedHotspot.name.uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimaryLight
                                )
                                Text(
                                    text = selectedHotspot.regionTag,
                                    fontSize = 10.sp,
                                    color = NaijaGold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (selectedHotspot.threatTier == "CRITICAL") AlertRed else Color(0xFF8C5E14)
                        ) {
                            Text(
                                text = "${selectedHotspot.threatTier} (${selectedHotspot.angerContribution}% ANGER)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = selectedHotspot.tacticalReport,
                        fontSize = 11.sp,
                        color = TextPrimaryLight,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}
