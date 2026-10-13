package com.example.mandate.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mandate.model.*
import com.example.ui.theme.*
import kotlin.math.*
import kotlin.random.Random

/**
 * High-performance 3D Isometric explorable open-world canvas for "Lagos Life".
 * Renders multi-block Lagos districts, animated yellow Danfos, Kekes, street hawkers,
 * 3D buildings (Owambe Canopy, Mama Put Bukka, Computer Village, Motor Park, Compound),
 * dynamic day/night atmospheric lighting, and tropical rain particles.
 */
@Composable
fun LagosOpenWorld3DCanvas(
    worldState: OpenWorldState,
    pois: List<OpenWorldPOI>,
    npcs: List<OpenWorldNPC>,
    vehicles: List<OpenWorldVehicle>,
    onMoveToGrid: (Float, Float) -> Unit,
    onPOISelected: (OpenWorldPOI) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    // Smooth real-time infinite ticker for walk cycles, vehicle motions, fuji pulse, and rain
    val infiniteTransition = rememberInfiniteTransition(label = "OpenWorldAnimation")
    val ticker by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WorldTicker"
    )

    val rainOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RainOffset"
    )

    // Smooth camera lerp tracking player position
    var cameraX by remember { mutableFloatStateOf(worldState.playerX) }
    var cameraY by remember { mutableFloatStateOf(worldState.playerY) }

    LaunchedEffect(worldState.playerX, worldState.playerY) {
        cameraX = cameraX + (worldState.playerX - cameraX) * 0.4f
        cameraY = cameraY + (worldState.playerY - cameraY) * 0.4f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(DeepCharcoal)
            .testTag("lagos_open_world_canvas_container")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(worldState.currentDistrict, pois) {
                    detectTapGestures { tapOffset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val tileW = 90f
                        val tileH = 46f

                        // Invert isometric projection from screen coordinates to grid coordinates
                        val relX = tapOffset.x - centerX
                        val relY = tapOffset.y - (centerY - 50f)

                        val dX = (relX / (tileW / 2f) + relY / (tileH / 2f)) / 2f
                        val dY = (relY / (tileH / 2f) - relX / (tileW / 2f)) / 2f

                        val tappedGridX = (cameraX + dX).coerceIn(1f, 15f)
                        val tappedGridY = (cameraY + dY).coerceIn(1f, 15f)

                        // Check if tap hit any POI directly
                        val hitPOI = pois.find { poi ->
                            val dist = hypot(poi.gridX - tappedGridX, poi.gridY - tappedGridY)
                            dist < 1.6f
                        }

                        if (hitPOI != null) {
                            onPOISelected(hitPOI)
                        } else {
                            onMoveToGrid(tappedGridX, tappedGridY)
                        }
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val originX = canvasW / 2f
            val originY = canvasH / 2f - 40f
            val tileW = 90f
            val tileH = 46f

            fun toIso(gx: Float, gy: Float): Offset {
                val relGx = gx - cameraX
                val relGy = gy - cameraY
                val sx = originX + (relGx - relGy) * (tileW / 2f)
                val sy = originY + (relGx + relGy) * (tileH / 2f)
                return Offset(sx, sy)
            }

            // 1. Draw World Ground Grid: Asphalt roads, concrete sidewalks, gutters, grass/dirt verges
            drawLagosGround(
                toIso = ::toIso,
                tileW = tileW,
                tileH = tileH,
                district = worldState.currentDistrict,
                ticker = ticker
            )

            // 2. Draw 3D Isometric Buildings for all POIs in District
            pois.sortedBy { it.gridX + it.gridY }.forEach { poi ->
                draw3DPOIBuilding(
                    poi = poi,
                    toIso = ::toIso,
                    tileW = tileW,
                    tileH = tileH,
                    ticker = ticker,
                    textMeasurer = textMeasurer,
                    isPlayerNearby = (worldState.nearbyPOI?.id == poi.id)
                )
            }

            // 3. Draw Dynamic Moving Vehicles (Danfos, Kekes, Okadas, G-Wagons)
            vehicles.sortedBy { it.gridX + it.gridY }.forEach { veh ->
                drawLagosVehicle(
                    veh = veh,
                    toIso = ::toIso,
                    ticker = ticker,
                    textMeasurer = textMeasurer
                )
            }

            // 4. Draw Ambient Pedestrian NPCs with speech bubbles
            npcs.sortedBy { it.gridX + it.gridY }.forEach { npc ->
                drawLagosPedestrianNPC(
                    npc = npc,
                    toIso = ::toIso,
                    ticker = ticker,
                    textMeasurer = textMeasurer
                )
            }

            // 5. Draw Player Avatar (Walking or Riding Vehicle)
            drawPlayerAvatar(
                worldState = worldState,
                toIso = ::toIso,
                ticker = ticker,
                textMeasurer = textMeasurer
            )

            // 6. Atmospheric Day/Night Lighting Overlay
            drawAtmosphericLighting(
                timeOfDay = worldState.timeOfDay,
                canvasW = canvasW,
                canvasH = canvasH,
                playerPos = toIso(worldState.playerX, worldState.playerY)
            )

            // 7. Tropical Rain Particles
            if (worldState.weather.isRaining) {
                drawLagosRain(
                    canvasW = canvasW,
                    canvasH = canvasH,
                    rainOffset = rainOffset,
                    rainIntensity = worldState.weather.rainIntensity
                )
            }
        }

        // Overlay Proximity Action Prompt if standing near a POI
        if (worldState.nearbyPOI != null) {
            val nearby = worldState.nearbyPOI
            Card(
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .testTag("world_poi_proximity_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(nearby.type.iconEmoji, fontSize = 22.sp)
                    Column {
                        Text(
                            text = nearby.name,
                            color = NaijaGoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = nearby.actionPrompt,
                            color = TextPrimaryLight,
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = { onPOISelected(nearby) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NaijaGold,
                            contentColor = DeepCharcoal
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_enter_poi")
                    ) {
                        Text("ENTER [E]", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Draws the ground terrain: road network, pedestrian sidewalks, storm drain gutters, and verges.
 */
private fun DrawScope.drawLagosGround(
    toIso: (Float, Float) -> Offset,
    tileW: Float,
    tileH: Float,
    district: LagosDistrict,
    ticker: Float
) {
    val roadColor = Color(0xFF23272B) // Dark asphalt
    val sidewalkColor = Color(0xFF4A4E54) // Concrete curb
    val gutterColor = Color(0xFF1E2124) // Storm drain trench
    val grassDirtColor = Color(0xFF384333) // Tropical verge

    // Draw base ground tiles across 16x16 grid
    for (gx in 0..16) {
        for (gy in 0..16) {
            val p0 = toIso(gx.toFloat(), gy.toFloat())
            val p1 = toIso(gx + 1f, gy.toFloat())
            val p2 = toIso(gx + 1f, gy + 1f)
            val p3 = toIso(gx.toFloat(), gy + 1f)

            // Roads run along grid lines gx in 6..9 or gy in 6..9
            val isMainRoad = (gx in 6..9 || gy in 6..9)
            val isSidewalk = (gx in 5..10 || gy in 5..10) && !isMainRoad

            val tileColor = when {
                isMainRoad -> roadColor
                isSidewalk -> sidewalkColor
                else -> grassDirtColor
            }

            val path = Path().apply {
                moveTo(p0.x, p0.y)
                lineTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                lineTo(p3.x, p3.y)
                close()
            }
            drawPath(path, tileColor)

            // Tile edge border
            drawPath(path, Color(0x15000000), style = Stroke(width = 1f))
        }
    }

    // Draw Road Painted Lines (Iconic Yellow center stripes & white lane dividers)
    for (i in 1..15) {
        if (i % 2 == 0) {
            val roadMidH0 = toIso(i.toFloat(), 7.5f)
            val roadMidH1 = toIso(i + 0.8f, 7.5f)
            drawLine(
                color = NaijaGold,
                start = roadMidH0,
                end = roadMidH1,
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            val roadMidV0 = toIso(7.5f, i.toFloat())
            val roadMidV1 = toIso(7.5f, i + 0.8f)
            drawLine(
                color = Color.White.copy(alpha = 0.85f),
                start = roadMidV0,
                end = roadMidV1,
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
        }
    }

    // Draw Zebra Crossings at intersection
    for (step in 0..4) {
        val z0 = toIso(6f + step * 0.7f, 5.2f)
        val z1 = toIso(6f + step * 0.7f, 5.8f)
        drawLine(
            color = Color.White.copy(alpha = 0.9f),
            start = z0,
            end = z1,
            strokeWidth = 4f
        )
    }
}

/**
 * Renders stylized 3D Isometric buildings with architectural variety:
 * Canopy tents for Owambe, wooden shacks for Bukka, high-rises for Banks,
 * modern boutique for Salon, fenced compound for Home, etc.
 */
private fun DrawScope.draw3DPOIBuilding(
    poi: OpenWorldPOI,
    toIso: (Float, Float) -> Offset,
    tileW: Float,
    tileH: Float,
    ticker: Float,
    textMeasurer: TextMeasurer,
    isPlayerNearby: Boolean
) {
    val base = toIso(poi.gridX, poi.gridY)
    val heightPx = when (poi.type) {
        POIType.COMMERCIAL_BANK -> 110f
        POIType.FAMILY_COMPOUND -> 75f
        POIType.OWAMBE_CANOPY -> 65f
        POIType.COMPUTER_VILLAGE -> 80f
        else -> 60f
    }

    // Pulsing selection glow if player is near
    if (isPlayerNearby) {
        val pulseRadius = 45f + 6f * sin(ticker * 0.1f)
        drawCircle(
            color = NaijaGold.copy(alpha = 0.35f),
            radius = pulseRadius,
            center = base
        )
    }

    // 3D Prism Base Projection
    val wHalf = 32f
    val hHalf = 18f

    val bFront = Offset(base.x, base.y + hHalf)
    val bBack = Offset(base.x, base.y - hHalf)
    val bLeft = Offset(base.x - wHalf, base.y)
    val bRight = Offset(base.x + wHalf, base.y)

    val tFront = Offset(bFront.x, bFront.y - heightPx)
    val tBack = Offset(bBack.x, bBack.y - heightPx)
    val tLeft = Offset(bLeft.x, bLeft.y - heightPx)
    val tRight = Offset(bRight.x, bRight.y - heightPx)

    // Left wall shadow
    val leftWall = Path().apply {
        moveTo(bLeft.x, bLeft.y)
        lineTo(bFront.x, bFront.y)
        lineTo(tFront.x, tFront.y)
        lineTo(tLeft.x, tLeft.y)
        close()
    }
    // Right wall light
    val rightWall = Path().apply {
        moveTo(bFront.x, bFront.y)
        lineTo(bRight.x, bRight.y)
        lineTo(tRight.x, tRight.y)
        lineTo(tFront.x, tFront.y)
        close()
    }
    // Roof top
    val roof = Path().apply {
        moveTo(tBack.x, tBack.y)
        lineTo(tLeft.x, tLeft.y)
        lineTo(tFront.x, tFront.y)
        lineTo(tRight.x, tRight.y)
        close()
    }

    when (poi.type) {
        POIType.OWAMBE_CANOPY -> {
            // Festive green/gold canopy tent with festive string bulbs
            drawPath(leftWall, Color(0xFF1B5E20))
            drawPath(rightWall, Color(0xFF2E7D32))
            drawPath(roof, Color(0xFFFFD700))

            // Festoon fairy lights along canopy ridge
            for (i in 0..4) {
                val lx = tLeft.x + (tRight.x - tLeft.x) * (i / 4f)
                val ly = tLeft.y + (tRight.y - tLeft.y) * (i / 4f) + 6f * sin(i + ticker * 0.1f)
                drawCircle(
                    color = if (i % 2 == 0) Color(0xFFFFEB3B) else Color(0xFFFF5722),
                    radius = 3.5f,
                    center = Offset(lx, ly)
                )
            }
        }
        POIType.MAMA_PUT_BUKKA -> {
            // Thatched wooden bukka with smoking pot on hearth
            drawPath(leftWall, Color(0xFF4E342E))
            drawPath(rightWall, Color(0xFF6D4C41))
            drawPath(roof, Color(0xFF8D6E63))

            // Steaming pot on veranda
            val potCenter = Offset(bFront.x + 10f, bFront.y - 12f)
            drawCircle(Color(0xFFB0BEC5), radius = 7f, center = potCenter)
            // Smoke particles rising
            for (s in 0..2) {
                val sy = potCenter.y - 12f - (ticker * 1.5f + s * 14f) % 35f
                val sx = potCenter.x + sin(sy * 0.1f) * 5f
                drawCircle(Color.White.copy(alpha = 0.5f - (potCenter.y - sy) / 80f), radius = 3f, center = Offset(sx, sy))
            }
        }
        POIType.COMMERCIAL_BANK -> {
            // Glass high-rise skyscraper with blue window grid
            drawPath(leftWall, Color(0xFF0D47A1))
            drawPath(rightWall, Color(0xFF1976D2))
            drawPath(roof, Color(0xFF42A5F5))

            // Glass window panes
            for (floor in 1..4) {
                val wy = tFront.y + floor * 18f
                drawLine(Color(0xFFE3F2FD), Offset(tFront.x - 18f, wy), Offset(tFront.x + 18f, wy), strokeWidth = 2f)
            }
        }
        POIType.BARBING_SALON -> {
            // Modern boutique with rotating barber pole
            drawPath(leftWall, Color(0xFF263238))
            drawPath(rightWall, Color(0xFF37474F))
            drawPath(roof, Color(0xFFE91E63))

            // Barber pole
            val poleX = bRight.x + 4f
            val poleY = bRight.y - 25f
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(poleX, poleY),
                size = Size(6f, 22f),
                cornerRadius = CornerRadius(2f, 2f)
            )
            // Barber pole red stripe
            val stripeOffset = (ticker * 2f) % 12f
            drawLine(
                color = Color.Red,
                start = Offset(poleX, poleY + stripeOffset),
                end = Offset(poleX + 6f, poleY + stripeOffset + 4f),
                strokeWidth = 3f
            )
        }
        POIType.FAMILY_COMPOUND -> {
            // Compound wall with black iron gate & blue water GP tank on tower
            drawPath(leftWall, Color(0xFF3E2723))
            drawPath(rightWall, Color(0xFF4E342E))
            drawPath(roof, Color(0xFF795548))

            // Blue GP water tank
            val tankCenter = Offset(tBack.x - 14f, tBack.y - 16f)
            drawCircle(Color(0xFF0288D1), radius = 9f, center = tankCenter)
        }
        else -> {
            // Standard brick and zinc structure
            drawPath(leftWall, Color(0xFF37474F))
            drawPath(rightWall, Color(0xFF455A64))
            drawPath(roof, Color(0xFFFFB300))
        }
    }

    // Draw POI Category Icon & Name tag floating above roof
    val tagY = tBack.y - 18f
    drawRoundRect(
        color = DeepCharcoal.copy(alpha = 0.88f),
        topLeft = Offset(base.x - 36f, tagY - 14f),
        size = Size(72f, 22f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
        color = if (isPlayerNearby) NaijaGold else Color.White.copy(alpha = 0.4f),
        topLeft = Offset(base.x - 36f, tagY - 14f),
        size = Size(72f, 22f),
        cornerRadius = CornerRadius(6f, 6f),
        style = Stroke(width = 1.2f)
    )

    val labelText = "${poi.type.iconEmoji} ${poi.name.take(10)}"
    val textLayout = textMeasurer.measure(
        text = labelText,
        style = TextStyle(color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    )
    drawText(
        textLayoutResult = textLayout,
        topLeft = Offset(base.x - textLayout.size.width / 2f, tagY - 11f)
    )
}

/**
 * Draws dynamic vehicles traversing the roads:
 * Yellow Danfo with black stripes, Keke tricycle, Okada bike, or luxury G-Wagon.
 */
private fun DrawScope.drawLagosVehicle(
    veh: OpenWorldVehicle,
    toIso: (Float, Float) -> Offset,
    ticker: Float,
    textMeasurer: TextMeasurer
) {
    // Animate vehicle movement along its direction vector
    val step = (ticker * veh.speed) % 12f
    val animatedGx = (veh.gridX + veh.directionX * step).coerceIn(1f, 15f)
    val animatedGy = (veh.gridY + veh.directionY * step).coerceIn(1f, 15f)

    val pos = toIso(animatedGx, animatedGy)

    when (veh.type) {
        LagosVehicleType.YELLOW_DANFO -> {
            // Iconic yellow VW LT35 minibus with dual black stripes & bouncy suspension
            val bounce = sin(ticker * 0.4f) * 1.5f
            val basePos = Offset(pos.x, pos.y + bounce)

            // Chassis shadow
            drawOval(
                color = Color.Black.copy(alpha = 0.35f),
                topLeft = Offset(basePos.x - 22f, basePos.y + 4f),
                size = Size(44f, 16f)
            )

            // Yellow body block
            drawRoundRect(
                color = Color(veh.type.baseColorHex),
                topLeft = Offset(basePos.x - 20f, basePos.y - 18f),
                size = Size(40f, 22f),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Dual black stripes
            drawRect(
                color = Color(veh.type.stripeColorHex),
                topLeft = Offset(basePos.x - 20f, basePos.y - 10f),
                size = Size(40f, 4f)
            )

            // Windshield glass & Route text
            drawRect(
                color = Color(0xFF90CAF9),
                topLeft = Offset(basePos.x - 14f, basePos.y - 16f),
                size = Size(28f, 5f)
            )

            // Wheels
            drawCircle(Color.Black, radius = 4f, center = Offset(basePos.x - 12f, basePos.y + 4f))
            drawCircle(Color.Black, radius = 4f, center = Offset(basePos.x + 12f, basePos.y + 4f))

            // Conductor leaning out waving hand
            drawCircle(Color(0xFF5D4037), radius = 2.5f, center = Offset(basePos.x + 18f, basePos.y - 12f))
        }
        LagosVehicleType.KEKE_MARWA -> {
            // Yellow three-wheeled tricycle
            val basePos = Offset(pos.x, pos.y + sin(ticker * 0.5f))
            drawOval(Color.Black.copy(alpha = 0.3f), topLeft = Offset(basePos.x - 14f, basePos.y + 2f), size = Size(28f, 12f))

            // Body
            drawRoundRect(
                color = Color(veh.type.baseColorHex),
                topLeft = Offset(basePos.x - 12f, basePos.y - 12f),
                size = Size(24f, 15f),
                cornerRadius = CornerRadius(3f, 3f)
            )
            // Green roof canopy
            drawRoundRect(
                color = Color(0xFF1B5E20),
                topLeft = Offset(basePos.x - 10f, basePos.y - 16f),
                size = Size(20f, 6f),
                cornerRadius = CornerRadius(2f, 2f)
            )
            // 3 wheels
            drawCircle(Color.Black, radius = 3f, center = Offset(basePos.x, basePos.y + 3f))
            drawCircle(Color.Black, radius = 3f, center = Offset(basePos.x - 8f, basePos.y + 1f))
            drawCircle(Color.Black, radius = 3f, center = Offset(basePos.x + 8f, basePos.y + 1f))
        }
        LagosVehicleType.OKADA_BOXER -> {
            // Motorbike with helmet rider
            drawOval(Color.Black.copy(alpha = 0.3f), topLeft = Offset(pos.x - 10f, pos.y + 2f), size = Size(20f, 8f))
            // Red frame
            drawLine(Color(0xFFE53935), Offset(pos.x - 8f, pos.y - 2f), Offset(pos.x + 8f, pos.y - 2f), strokeWidth = 3f)
            // Wheels
            drawCircle(Color.Black, radius = 3.5f, center = Offset(pos.x - 8f, pos.y + 2f))
            drawCircle(Color.Black, radius = 3.5f, center = Offset(pos.x + 8f, pos.y + 2f))
            // Rider head & helmet
            drawCircle(Color(0xFFFFB300), radius = 3.5f, center = Offset(pos.x, pos.y - 12f))
        }
        LagosVehicleType.G_WAGON_LUXURY -> {
            // Boxy black Mercedes G-Wagon with tinted glass
            drawOval(Color.Black.copy(alpha = 0.4f), topLeft = Offset(pos.x - 22f, pos.y + 4f), size = Size(44f, 16f))
            drawRoundRect(
                color = Color(0xFF1A1A1A),
                topLeft = Offset(pos.x - 20f, pos.y - 18f),
                size = Size(40f, 20f),
                cornerRadius = CornerRadius(2f, 2f)
            )
            // Tinted dark glass
            drawRect(Color(0xFF424242), topLeft = Offset(pos.x - 16f, pos.y - 16f), size = Size(32f, 5f))
            // Wheels
            drawCircle(Color(0xFF616161), radius = 4.5f, center = Offset(pos.x - 12f, pos.y + 3f))
            drawCircle(Color(0xFF616161), radius = 4.5f, center = Offset(pos.x + 12f, pos.y + 3f))
        }
        else -> {
            drawRoundRect(Color(0xFF78909C), topLeft = Offset(pos.x - 16f, pos.y - 12f), size = Size(32f, 14f), cornerRadius = CornerRadius(3f, 3f))
        }
    }
}

/**
 * Draws animated street pedestrians: hawkers balancing trays, area boys, bankers, and padis.
 */
private fun DrawScope.drawLagosPedestrianNPC(
    npc: OpenWorldNPC,
    toIso: (Float, Float) -> Offset,
    ticker: Float,
    textMeasurer: TextMeasurer
) {
    // Walk cycle oscillation between current and target
    val walkPhase = sin(ticker * 0.3f)
    val curX = npc.gridX + (npc.targetGridX - npc.gridX) * ((walkPhase + 1f) / 2f)
    val curY = npc.gridY + (npc.targetGridY - npc.gridY) * ((walkPhase + 1f) / 2f)

    val pos = toIso(curX, curY)
    val headRadius = 4.5f
    val bodyH = 14f

    // Shadow
    drawOval(Color.Black.copy(alpha = 0.25f), topLeft = Offset(pos.x - 6f, pos.y + 2f), size = Size(12f, 6f))

    // Outfit body
    val bodyTop = pos.y - bodyH
    drawLine(
        color = Color(npc.outfitColorHex),
        start = Offset(pos.x, bodyTop),
        end = Offset(pos.x, pos.y),
        strokeWidth = 5f,
        cap = StrokeCap.Round
    )

    // Animated legs
    val legSwing = walkPhase * 4f
    drawLine(Color.DarkGray, Offset(pos.x, pos.y), Offset(pos.x - 3f - legSwing, pos.y + 6f), strokeWidth = 2.5f)
    drawLine(Color.DarkGray, Offset(pos.x, pos.y), Offset(pos.x + 3f + legSwing, pos.y + 6f), strokeWidth = 2.5f)

    // Head
    val headCenter = Offset(pos.x, bodyTop - headRadius)
    drawCircle(Color(0xFF5D4037), radius = headRadius, center = headCenter)

    // If Hawker: Draw large silver tray balanced on top of head with cold bottles & snacks
    if (npc.isHawker) {
        val trayY = headCenter.y - headRadius - 2f
        drawOval(Color(0xFFCFD8DC), topLeft = Offset(pos.x - 10f, trayY - 2f), size = Size(20f, 5f))
        // Bottles on tray
        drawRect(Color(0xFF00E676), topLeft = Offset(pos.x - 6f, trayY - 6f), size = Size(3f, 5f))
        drawRect(Color(0xFFFF9100), topLeft = Offset(pos.x + 2f, trayY - 6f), size = Size(4f, 5f))
    }

    // Speech bubble periodic pop-up
    val showSpeech = (abs(sin(ticker * 0.08f + npc.name.hashCode())) > 0.65f)
    if (showSpeech && npc.speechBubble.isNotEmpty()) {
        val bubbleY = headCenter.y - 20f
        val bubbleText = npc.speechBubble.take(18)
        val textLayout = textMeasurer.measure(
            text = bubbleText,
            style = TextStyle(color = DeepCharcoal, fontSize = 7.5.sp, fontWeight = FontWeight.SemiBold)
        )
        val bubbleW = textLayout.size.width + 10f
        val bubbleH = textLayout.size.height + 6f

        drawRoundRect(
            color = Color.White.copy(alpha = 0.95f),
            topLeft = Offset(pos.x - bubbleW / 2f, bubbleY - bubbleH),
            size = Size(bubbleW, bubbleH),
            cornerRadius = CornerRadius(5f, 5f)
        )
        drawText(
            textLayoutResult = textLayout,
            topLeft = Offset(pos.x - textLayout.size.width / 2f, bubbleY - bubbleH + 3f)
        )
    }
}

/**
 * Draws the Player Character in the open world, reflecting movement and travel mode.
 */
private fun DrawScope.drawPlayerAvatar(
    worldState: OpenWorldState,
    toIso: (Float, Float) -> Offset,
    ticker: Float,
    textMeasurer: TextMeasurer
) {
    val pos = toIso(worldState.playerX, worldState.playerY)

    // Golden Halo Marker over Player
    val markerY = pos.y - 36f + sin(ticker * 0.2f) * 3f
    val markerPath = Path().apply {
        moveTo(pos.x, markerY)
        lineTo(pos.x - 7f, markerY - 10f)
        lineTo(pos.x + 7f, markerY - 10f)
        close()
    }
    drawPath(markerPath, NaijaGold)

    when (worldState.travelMode) {
        TravelMode.ON_FOOT -> {
            val walkPhase = sin(ticker * 0.4f)
            val headRadius = 6f
            val bodyH = 18f

            // Shadow
            drawOval(Color.Black.copy(alpha = 0.4f), topLeft = Offset(pos.x - 9f, pos.y + 2f), size = Size(18f, 8f))

            // Senator Agbada / Outfit
            val bodyTop = pos.y - bodyH
            drawLine(
                color = NaijaGreenLight,
                start = Offset(pos.x, bodyTop),
                end = Offset(pos.x, pos.y),
                strokeWidth = 7f,
                cap = StrokeCap.Round
            )

            // Legs
            val legSwing = walkPhase * 5f
            drawLine(Color.Black, Offset(pos.x, pos.y), Offset(pos.x - 4f - legSwing, pos.y + 7f), strokeWidth = 3f)
            drawLine(Color.Black, Offset(pos.x, pos.y), Offset(pos.x + 4f + legSwing, pos.y + 7f), strokeWidth = 3f)

            // Head
            val headCenter = Offset(pos.x, bodyTop - headRadius)
            drawCircle(Color(0xFF5D4037), radius = headRadius, center = headCenter)

            // Fila Cap tilted to the side
            val capPath = Path().apply {
                moveTo(headCenter.x - 6f, headCenter.y - 2f)
                lineTo(headCenter.x + 6f, headCenter.y - 2f)
                lineTo(headCenter.x + 4f, headCenter.y - 9f)
                lineTo(headCenter.x - 7f, headCenter.y - 7f)
                close()
            }
            drawPath(capPath, NaijaGold)
        }
        TravelMode.DANFO_BUS -> {
            // Player riding yellow Danfo with prominent golden beacon
            drawRoundRect(
                color = Color(0xFFFFD700),
                topLeft = Offset(pos.x - 24f, pos.y - 20f),
                size = Size(48f, 24f),
                cornerRadius = CornerRadius(5f, 5f)
            )
            drawRect(Color.Black, topLeft = Offset(pos.x - 24f, pos.y - 12f), size = Size(48f, 4f))
            drawCircle(NaijaGold, radius = 5f, center = Offset(pos.x, pos.y - 26f))
        }
        TravelMode.KEKE_NAPEP -> {
            drawRoundRect(Color(0xFFFFC107), topLeft = Offset(pos.x - 14f, pos.y - 14f), size = Size(28f, 18f), cornerRadius = CornerRadius(3f, 3f))
            drawRoundRect(Color(0xFF1B5E20), topLeft = Offset(pos.x - 12f, pos.y - 19f), size = Size(24f, 6f), cornerRadius = CornerRadius(2f, 2f))
        }
        TravelMode.OKADA_BIKE -> {
            drawLine(Color(0xFFE53935), Offset(pos.x - 10f, pos.y - 2f), Offset(pos.x + 10f, pos.y - 2f), strokeWidth = 4f)
            drawCircle(Color.Black, radius = 4f, center = Offset(pos.x - 8f, pos.y + 3f))
            drawCircle(Color.Black, radius = 4f, center = Offset(pos.x + 8f, pos.y + 3f))
        }
        TravelMode.PRIVATE_CAR -> {
            drawRoundRect(Color(0xFF1A1A1A), topLeft = Offset(pos.x - 22f, pos.y - 18f), size = Size(44f, 22f), cornerRadius = CornerRadius(3f, 3f))
            drawRect(Color(0xFF424242), topLeft = Offset(pos.x - 18f, pos.y - 16f), size = Size(36f, 6f))
        }
    }

    // Player Name Tag & Drip Rating Badge
    val tagLayout = textMeasurer.measure(
        text = "YOU (Drip: ${worldState.dripRating}%)",
        style = TextStyle(color = NaijaGoldLight, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
    )
    val badgeW = tagLayout.size.width + 12f
    val badgeY = markerY - 18f
    drawRoundRect(
        color = DeepCharcoal.copy(alpha = 0.9f),
        topLeft = Offset(pos.x - badgeW / 2f, badgeY - 2f),
        size = Size(badgeW, 16f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawText(
        textLayoutResult = tagLayout,
        topLeft = Offset(pos.x - tagLayout.size.width / 2f, badgeY)
    )
}

/**
 * Draws dynamic day/night atmospheric lighting across the canvas.
 */
private fun DrawScope.drawAtmosphericLighting(
    timeOfDay: LagosTimeOfDay,
    canvasW: Float,
    canvasH: Float,
    playerPos: Offset
) {
    if (timeOfDay.isNightLighting) {
        val darkTint = when (timeOfDay) {
            LagosTimeOfDay.EVENING_HOLDUP -> Color(0x35E65100) // Golden orange sunset
            LagosTimeOfDay.OWAMBE_NIGHT -> Color(0x650D1B2A)   // Deep indigo night
            LagosTimeOfDay.MIDNIGHT_BREEZE -> Color(0x85050B14)// Midnight dark
            else -> Color.Transparent
        }

        drawRect(
            color = darkTint,
            topLeft = Offset.Zero,
            size = Size(canvasW, canvasH)
        )

        // Radial golden light halo around player
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x40FFD700), Color.Transparent),
                center = playerPos,
                radius = 160f
            ),
            radius = 160f,
            center = playerPos
        )
    }
}

/**
 * Draws tropical Lagos rain streaks and asphalt splashes.
 */
private fun DrawScope.drawLagosRain(
    canvasW: Float,
    canvasH: Float,
    rainOffset: Float,
    rainIntensity: Float
) {
    val numDrops = (120 * rainIntensity).toInt()
    val random = Random(42)

    for (i in 0 until numDrops) {
        val startX = (random.nextFloat() * canvasW + rainOffset * 0.5f) % canvasW
        val startY = (random.nextFloat() * canvasH + rainOffset) % canvasH
        val streakLen = 14f + random.nextFloat() * 10f

        drawLine(
            color = Color.White.copy(alpha = 0.45f),
            start = Offset(startX, startY),
            end = Offset(startX - 5f, startY + streakLen),
            strokeWidth = 1.5f
        )
    }
}
