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
 * Camera perspective for 3D Isometric Home Environment.
 */
enum class HomeCameraPerspective(val label: String, val angleDeg: Float) {
    COURTYARD_MAIN("Courtyard & Parlor", 0f),
    VERANDA_HEARTH("Veranda & Hearth", 90f),
    DRONE_OVERVIEW("Compound Overview", 45f)
}

/**
 * Types of traditional or urban Nigerian headgear for NPCs.
 */
enum class NPCHeadgear {
    TOWERING_GELE,
    YORUBA_FILA,
    CHIEFTAINCY_RED_CAP,
    MODERN_HAIR,
    BASEBALL_CAP,
    COUP_BERET
}

/**
 * Interactive 3D NPC entity moving in the living space.
 */
data class HomeNPCEntity(
    val id: String,
    val name: String,
    val title: String,
    val isFamilyMember: Boolean,
    val familyMember: FamilyMember? = null,
    val padi: Padi? = null,
    var gridX: Float,
    var gridY: Float,
    var targetGridX: Float,
    var targetGridY: Float,
    var moveSpeed: Float = 0.02f,
    var headgear: NPCHeadgear = NPCHeadgear.MODERN_HAIR,
    var primaryColor: Color = NaijaGold,
    var secondaryColor: Color = EmeraldPrimary,
    var speechThought: String = "All is well in the house.",
    var thoughtEmoji: String = "🏡",
    var isSelected: Boolean = false,
    var isWalking: Boolean = false,
    var facingDirection: Float = 0f, // radians
    var animPhase: Float = 0f,
    var hasUrgentAlert: Boolean = false
)

/**
 * 3D isometric representation of the Nigerian family home living space
 * (Polygamous Compound or Monogamous Urban Duplex) with autonomous moving NPCs.
 */
@Composable
fun HomeIsometric3DCanvas(
    layoutType: FamilyStructure,
    familyMembers: List<FamilyMember>,
    padis: List<Padi>,
    blackTaxQueue: List<BlackTaxRequest>,
    selectedNpc: HomeNPCEntity?,
    onSelectNpc: (HomeNPCEntity) -> Unit,
    onTapFloor: (Float, Float) -> Unit,
    cameraPerspective: HomeCameraPerspective,
    onCameraChange: (HomeCameraPerspective) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    // Autonomous NPC simulation state
    val npcsState = remember(familyMembers, padis, blackTaxQueue, layoutType) {
        val list = mutableListOf<HomeNPCEntity>()

        // 1. Convert family members into 3D NPCs with authentic roles & positions
        familyMembers.forEachIndexed { index, fm ->
            val hasTax = blackTaxQueue.any { it.fromMemberName.contains(fm.name, ignoreCase = true) }
            val (initialX, initialY, headgear, pColor, thought, emoji) = when (fm.role) {
                KinshipRole.MOTHER -> {
                    // Mother near the kitchen hearth / parlor dining
                    Tuple6(
                        2.5f + (index * 0.3f),
                        3.0f,
                        NPCHeadgear.TOWERING_GELE,
                        RoyalPurple,
                        if (hasTax) "My child, remember school fees!" else "My son, your morning blessings are ready.",
                        "🙏"
                    )
                }
                KinshipRole.FATHER -> {
                    // Father at Patriarch's Obi / central grand armchair
                    Tuple6(
                        5.5f,
                        5.5f,
                        NPCHeadgear.CHIEFTAINCY_RED_CAP,
                        NaijaGold,
                        "Listen well to elder counsel before making decrees.",
                        "👑"
                    )
                }
                KinshipRole.CO_WIFE -> {
                    // Rival stepmother pacing between verandas
                    Tuple6(
                        6.5f,
                        2.5f,
                        NPCHeadgear.TOWERING_GELE,
                        AlertRed,
                        if (hasTax) "Village land taxes must be shared equally!" else "I am watching whose son brings more glory.",
                        "👀"
                    )
                }
                KinshipRole.HALF_SIBLING -> {
                    // Sibling studying or playing in the corridor
                    Tuple6(
                        3.0f + (index * 0.5f),
                        6.5f,
                        NPCHeadgear.MODERN_HAIR,
                        NaijaGreenLight,
                        "Senior Bro! Buy me Gala when you go to Island!",
                        "⚽"
                    )
                }
                KinshipRole.FULL_SIBLING -> {
                    Tuple6(
                        4.0f,
                        6.0f,
                        NPCHeadgear.BASEBALL_CAP,
                        NaijaGold,
                        "We stand together against any family politics.",
                        "🤝"
                    )
                }
                KinshipRole.VILLAGE_ELDER -> {
                    Tuple6(
                        7.0f,
                        4.0f,
                        NPCHeadgear.CHIEFTAINCY_RED_CAP,
                        DeepCharcoal,
                        "The masquerade never dances without ancestral wine.",
                        "📜"
                    )
                }
            }

            list.add(
                HomeNPCEntity(
                    id = fm.id,
                    name = fm.name,
                    title = fm.role.title,
                    isFamilyMember = true,
                    familyMember = fm,
                    gridX = initialX.coerceIn(1f, 7.5f),
                    gridY = initialY.coerceIn(1f, 7.5f),
                    targetGridX = initialX.coerceIn(1f, 7.5f),
                    targetGridY = initialY.coerceIn(1f, 7.5f),
                    headgear = headgear,
                    primaryColor = pColor,
                    speechThought = thought,
                    thoughtEmoji = emoji,
                    hasUrgentAlert = hasTax
                )
            )
        }

        // 2. Add visiting Padis hanging out in the living room / courtyard
        padis.take(3).forEachIndexed { idx, padi ->
            val (px, py, head, col, thought, emoji) = when (padi.archetype) {
                PadiArchetype.RIDE_OR_DIE -> Tuple6(
                    2.0f + idx * 0.8f,
                    4.5f,
                    NPCHeadgear.YORUBA_FILA,
                    EmeraldPrimary,
                    "Senior Man! Street boys dey wait your signal!",
                    "💪"
                )
                PadiArchetype.RICH_PADI -> Tuple6(
                    4.5f,
                    2.2f + idx * 0.6f,
                    NPCHeadgear.MODERN_HAIR,
                    NaijaGoldLight,
                    "Chilled malt or champagne? You name it, I pay!",
                    "🍾"
                )
                PadiArchetype.ACTIVIST -> Tuple6(
                    6.2f,
                    6.2f,
                    NPCHeadgear.COUP_BERET,
                    AlertRed,
                    "The fuel subsidy protest is gathering momentum!",
                    "✊"
                )
                PadiArchetype.GOSSIP_BLOGGER -> Tuple6(
                    3.5f,
                    5.0f,
                    NPCHeadgear.MODERN_HAIR,
                    RoyalPurple,
                    "I hear the Governor's Chief of Staff just resigned...",
                    "📱"
                )
                PadiArchetype.SNAKE_RIVAL -> Tuple6(
                    6.8f,
                    3.5f,
                    NPCHeadgear.YORUBA_FILA,
                    WarningOrange,
                    "Nice compound, brother... but elections cost billions.",
                    "🐍"
                )
            }

            list.add(
                HomeNPCEntity(
                    id = padi.id,
                    name = padi.name,
                    title = "${padi.nickname} (${padi.archetype.title})",
                    isFamilyMember = false,
                    padi = padi,
                    gridX = px.coerceIn(1.5f, 7f),
                    gridY = py.coerceIn(1.5f, 7f),
                    targetGridX = px.coerceIn(1.5f, 7f),
                    targetGridY = py.coerceIn(1.5f, 7f),
                    headgear = head,
                    primaryColor = col,
                    speechThought = thought,
                    thoughtEmoji = emoji
                )
            )
        }

        list
    }

    // Player Avatar position in the home (Starts at entrance/center)
    var playerGridX by remember { mutableFloatStateOf(4.5f) }
    var playerGridY by remember { mutableFloatStateOf(4.5f) }
    var playerTargetX by remember { mutableFloatStateOf(4.5f) }
    var playerTargetY by remember { mutableFloatStateOf(4.5f) }

    // Ambient Animation Loop (Sims clock, Plumbob spin, Generator vibration, Fan rotation)
    val infiniteTransition = rememberInfiniteTransition(label = "HomeEngineLoop")
    val animClock by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animClock"
    )

    // Generator vibration / gentle idle breathing
    val pulseBounce by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(180, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseBounce"
    )

    // Autonomous AI movement updates every tick
    LaunchedEffect(animClock) {
        // Move player towards target smoothly
        val pDx = playerTargetX - playerGridX
        val pDy = playerTargetY - playerGridY
        if (abs(pDx) > 0.05f || abs(pDy) > 0.05f) {
            playerGridX += pDx * 0.15f
            playerGridY += pDy * 0.15f
        }

        // Autonomous NPC wandering & gestures
        npcsState.forEachIndexed { i, npc ->
            val dx = npc.targetGridX - npc.gridX
            val dy = npc.targetGridY - npc.gridY
            if (abs(dx) > 0.05f || abs(dy) > 0.05f) {
                npc.gridX += dx * 0.08f
                npc.gridY += dy * 0.08f
                npc.isWalking = true
                npc.facingDirection = atan2(dy, dx)
                npc.animPhase += 0.25f
            } else {
                npc.isWalking = false
                // Occasionally pick a new destination within room boundaries
                if (Random.nextInt(100) < 5) {
                    val angle = Random.nextFloat() * 2f * PI.toFloat()
                    val dist = Random.nextFloat() * 1.5f + 0.3f
                    val newX = (npc.gridX + cos(angle) * dist).coerceIn(1.2f, 7.2f)
                    val newY = (npc.gridY + sin(angle) * dist).coerceIn(1.2f, 7.2f)
                    npc.targetGridX = newX
                    npc.targetGridY = newY
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = if (layoutType == FamilyStructure.POLYGAMOUS) {
                        listOf(Color(0xFF14241B), Color(0xFF0D1812), Color(0xFF09120D))
                    } else {
                        listOf(Color(0xFF1B2329), Color(0xFF0F171D), Color(0xFF0A0F14))
                    }
                )
            )
            .testTag("home_isometric_3d_canvas")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(layoutType, cameraPerspective) {
                    detectTapGestures { tapOffset ->
                        val w = size.width
                        val h = size.height
                        val centerX = w / 2f
                        val centerY = h / 2f + 25f

                        // Convert tap screen coordinates back to 3D grid space
                        val gridCoords = screenToIsoGrid(
                            screenX = tapOffset.x,
                            screenY = tapOffset.y,
                            centerX = centerX,
                            centerY = centerY,
                            tileScale = w / 16f,
                            perspective = cameraPerspective
                        )

                        // Check if an NPC was tapped
                        var clickedNpc: HomeNPCEntity? = null
                        var minDistance = 0.85f

                        for (npc in npcsState) {
                            val dist = hypot(npc.gridX - gridCoords.x, npc.gridY - gridCoords.y)
                            if (dist < minDistance) {
                                minDistance = dist
                                clickedNpc = npc
                            }
                        }

                        if (clickedNpc != null) {
                            onSelectNpc(clickedNpc)
                            // Player walks toward the clicked NPC to engage!
                            playerTargetX = clickedNpc.gridX
                            playerTargetY = clickedNpc.gridY
                        } else {
                            // Player walks to tapped floor tile
                            val boundedX = gridCoords.x.coerceIn(1.0f, 7.5f)
                            val boundedY = gridCoords.y.coerceIn(1.0f, 7.5f)
                            playerTargetX = boundedX
                            playerTargetY = boundedY
                            onTapFloor(boundedX, boundedY)
                        }
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val centerX = canvasW / 2f
            val centerY = canvasH / 2f + 25f
            val tileScale = canvasW / 16f

            // 1. Draw Architectural Environment & Rooms
            if (layoutType == FamilyStructure.POLYGAMOUS) {
                drawPolygamousCompound(
                    centerX, centerY, tileScale, canvasW, canvasH,
                    cameraPerspective, animClock, pulseBounce
                )
            } else {
                drawMonogamousDuplex(
                    centerX, centerY, tileScale, canvasW, canvasH,
                    cameraPerspective, animClock, pulseBounce
                )
            }

            // 2. Draw 3D Living NPCs with autonomous movement & Plumbobs
            npcsState.sortedBy { it.gridX + it.gridY }.forEach { npc ->
                val isFocused = selectedNpc?.id == npc.id
                drawIsometricNPC(
                    npc = npc,
                    centerX = centerX,
                    centerY = centerY,
                    tileScale = tileScale,
                    perspective = cameraPerspective,
                    animClock = animClock,
                    isFocused = isFocused,
                    textMeasurer = textMeasurer
                )
            }

            // 3. Draw Player 3D Avatar in the house
            drawPlayerAvatarIso(
                gridX = playerGridX,
                gridY = playerGridY,
                centerX = centerX,
                centerY = centerY,
                tileScale = tileScale,
                perspective = cameraPerspective,
                animClock = animClock,
                isMoving = abs(playerTargetX - playerGridX) > 0.05f || abs(playerTargetY - playerGridY) > 0.05f
            )

            // 4. Draw Floating Atmosphere (Generator heat-haze / ceiling fan shadow / dust motes)
            drawHomeAtmosphere(canvasW, canvasH, animClock, layoutType)
        }

        // Top Left: Living Space Style Badge
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xCC0E2318),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold)),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (layoutType == FamilyStructure.POLYGAMOUS) "🏰 POLYGAMOUS COMPOUND" else "🏡 MONOGAMOUS DUPLEX",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = NaijaGoldLight
                )
                Text(
                    text = "• ${npcsState.size} NPCs Active",
                    fontSize = 9.sp,
                    color = Color.White
                )
            }
        }

        // Top Right: 3D Camera Mode Toggle
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xCC1A1208),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = {
                        val next = when (cameraPerspective) {
                            HomeCameraPerspective.COURTYARD_MAIN -> HomeCameraPerspective.VERANDA_HEARTH
                            HomeCameraPerspective.VERANDA_HEARTH -> HomeCameraPerspective.DRONE_OVERVIEW
                            HomeCameraPerspective.DRONE_OVERVIEW -> HomeCameraPerspective.COURTYARD_MAIN
                        }
                        onCameraChange(next)
                    },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Rotate Camera",
                        tint = NaijaGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = cameraPerspective.label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaijaGoldLight
                )
            }
        }

        // Bottom Banner Hint: "Tap floor to move • Tap any family member or padi to engage in 3D"
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xBB08120B),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen)
                )
                Text(
                    text = "Tap floor to walk • Tap any NPC to interact in 3D space",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Isometric Coordinate Mathematics
// -------------------------------------------------------------

private fun isoProject(
    gridX: Float,
    gridY: Float,
    gridZ: Float = 0f,
    centerX: Float,
    centerY: Float,
    tileScale: Float,
    perspective: HomeCameraPerspective
): Offset {
    val cos30 = 0.8660254f
    val sin30 = 0.5f

    // Coordinate rotation based on perspective
    val (rx, ry) = when (perspective) {
        HomeCameraPerspective.COURTYARD_MAIN -> Pair(gridX, gridY)
        HomeCameraPerspective.VERANDA_HEARTH -> Pair(gridY, 8f - gridX)
        HomeCameraPerspective.DRONE_OVERVIEW -> Pair(gridX * 0.9f, gridY * 0.9f)
    }

    val screenX = centerX + (rx - ry) * cos30 * tileScale
    val screenY = centerY + (rx + ry) * sin30 * (tileScale * 0.58f) - (gridZ * tileScale * 0.85f)
    return Offset(screenX, screenY)
}

private fun screenToIsoGrid(
    screenX: Float,
    screenY: Float,
    centerX: Float,
    centerY: Float,
    tileScale: Float,
    perspective: HomeCameraPerspective
): Offset {
    val cos30 = 0.8660254f
    val sin30 = 0.5f
    val factorY = tileScale * 0.58f
    val factorX = cos30 * tileScale

    val dx = (screenX - centerX) / factorX
    val dy = (screenY - centerY) / factorY

    val rx = (dx + dy) / 2f
    val ry = (dy - dx) / 2f

    val (gx, gy) = when (perspective) {
        HomeCameraPerspective.COURTYARD_MAIN -> Pair(rx, ry)
        HomeCameraPerspective.VERANDA_HEARTH -> Pair(8f - ry, rx)
        HomeCameraPerspective.DRONE_OVERVIEW -> Pair(rx / 0.9f, ry / 0.9f)
    }

    return Offset(gx, gy)
}

// -------------------------------------------------------------
// Drawing Polygamous Compound Environment
// -------------------------------------------------------------

private fun DrawScope.drawPolygamousCompound(
    centerX: Float,
    centerY: Float,
    tileScale: Float,
    canvasW: Float,
    canvasH: Float,
    perspective: HomeCameraPerspective,
    animClock: Float,
    generatorVibrate: Float
) {
    val gridSize = 8

    // 1. Courtyard Flooring (Red Laterite stamped earth + stone tiles)
    for (x in 0 until gridSize) {
        for (y in 0 until gridSize) {
            val p0 = isoProject(x.toFloat(), y.toFloat(), 0f, centerX, centerY, tileScale, perspective)
            val p1 = isoProject((x + 1).toFloat(), y.toFloat(), 0f, centerX, centerY, tileScale, perspective)
            val p2 = isoProject((x + 1).toFloat(), (y + 1).toFloat(), 0f, centerX, centerY, tileScale, perspective)
            val p3 = isoProject(x.toFloat(), (y + 1).toFloat(), 0f, centerX, centerY, tileScale, perspective)

            val tilePath = Path().apply {
                moveTo(p0.x, p0.y)
                lineTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                lineTo(p3.x, p3.y)
                close()
            }

            // Courtyard interior tiles vs veranda perimeter
            val isVeranda = x == 0 || y == 0 || x == gridSize - 1 || y == gridSize - 1
            val tileColor = if (isVeranda) {
                if ((x + y) % 2 == 0) Color(0xFF332014) else Color(0xFF2B1B11)
            } else {
                if ((x + y) % 2 == 0) Color(0xFF1E2F23) else Color(0xFF17251C)
            }

            drawPath(path = tilePath, color = tileColor, style = Fill)
            drawPath(path = tilePath, color = Color(0x33000000), style = Stroke(width = 1f))
        }
    }

    // 2. Compound Walls and Corrugated Zinc Roof Eaves
    val wallHeight = 2.0f
    // Back wall along X=0
    val wallPath = Path().apply {
        val w0 = isoProject(0f, 0f, 0f, centerX, centerY, tileScale, perspective)
        val w1 = isoProject(gridSize.toFloat(), 0f, 0f, centerX, centerY, tileScale, perspective)
        val w2 = isoProject(gridSize.toFloat(), 0f, wallHeight, centerX, centerY, tileScale, perspective)
        val w3 = isoProject(0f, 0f, wallHeight, centerX, centerY, tileScale, perspective)
        moveTo(w0.x, w0.y)
        lineTo(w1.x, w1.y)
        lineTo(w2.x, w2.y)
        lineTo(w3.x, w3.y)
        close()
    }
    drawPath(path = wallPath, color = Color(0xFF263B2F))

    // Corrugated zinc roof over the veranda with ribbed stripes
    val roofPath = Path().apply {
        val r0 = isoProject(0f, 0f, wallHeight, centerX, centerY, tileScale, perspective)
        val r1 = isoProject(gridSize.toFloat(), 0f, wallHeight, centerX, centerY, tileScale, perspective)
        val r2 = isoProject(gridSize.toFloat(), 1.8f, wallHeight + 0.8f, centerX, centerY, tileScale, perspective)
        val r3 = isoProject(0f, 1.8f, wallHeight + 0.8f, centerX, centerY, tileScale, perspective)
        moveTo(r0.x, r0.y)
        lineTo(r1.x, r1.y)
        lineTo(r2.x, r2.y)
        lineTo(r3.x, r3.y)
        close()
    }
    drawPath(
        path = roofPath,
        brush = Brush.horizontalGradient(
            colors = listOf(Color(0xFF5A4839), Color(0xFF705947), Color(0xFF47372B))
        )
    )

    // 3. Baba's Obi (Throne Room / Patriarch Armchair) at (5.5, 5.5)
    val obiPos = isoProject(5.5f, 5.5f, 0f, centerX, centerY, tileScale, perspective)
    // Red royal velvet carpet
    drawOval(
        color = Color(0xFF8B1E1E),
        topLeft = Offset(obiPos.x - 22f, obiPos.y - 12f),
        size = Size(44f, 24f)
    )
    // Carved grand mahogany armchair
    drawRect(
        color = Color(0xFF4E260E),
        topLeft = Offset(obiPos.x - 10f, obiPos.y - 28f),
        size = Size(20f, 26f)
    )
    drawRect(
        color = NaijaGold,
        topLeft = Offset(obiPos.x - 8f, obiPos.y - 26f),
        size = Size(16f, 18f)
    )
    // Gold ceremonial staff / walking stick leaning
    drawLine(
        color = NaijaGoldLight,
        start = Offset(obiPos.x + 14f, obiPos.y - 25f),
        end = Offset(obiPos.x + 18f, obiPos.y),
        strokeWidth = 2.5f
    )

    // 4. Outdoor Cooking Station / Hearth (Mama's Egusi / Jollof Cauldron) at (2.2, 2.5)
    val hearthPos = isoProject(2.2f, 2.5f, 0f, centerX, centerY, tileScale, perspective)
    // Charcoal firewood embers (animated glow)
    val emberGlow = (sin(animClock * 2f * PI.toFloat()) * 0.3f + 0.7f).coerceIn(0.4f, 1f)
    drawCircle(
        color = Color(0xFFFF5722).copy(alpha = emberGlow),
        radius = 12f,
        center = hearthPos
    )
    // Black cast iron three-legged cooking pot
    drawCircle(
        color = Color(0xFF1A1A1A),
        radius = 10f,
        center = Offset(hearthPos.x, hearthPos.y - 6f)
    )
    // Simmering stew inside the pot
    drawCircle(
        color = Color(0xFFE65100),
        radius = 6f,
        center = Offset(hearthPos.x, hearthPos.y - 8f)
    )
    // Wooden pestle and mortar nearby
    drawRect(
        color = Color(0xFF5D4037),
        topLeft = Offset(hearthPos.x + 14f, hearthPos.y - 12f),
        size = Size(10f, 12f)
    )

    // 5. Classic Nigerian Plastic Chairs (Blue & Green) in courtyard
    val chairPositions = listOf(
        Pair(Offset(3.5f, 4.2f), Color(0xFF1565C0)), // Blue chair
        Pair(Offset(4.2f, 3.8f), Color(0xFF2E7D32)), // Green chair
        Pair(Offset(6.2f, 4.8f), Color(0xFFC62828))  // Red stool
    )
    for ((grid, chairCol) in chairPositions) {
        val cPos = isoProject(grid.x, grid.y, 0f, centerX, centerY, tileScale, perspective)
        // Seat
        drawRoundRect(
            color = chairCol,
            topLeft = Offset(cPos.x - 7f, cPos.y - 14f),
            size = Size(14f, 10f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
        )
        // Backrest
        drawRect(
            color = chairCol,
            topLeft = Offset(cPos.x - 6f, cPos.y - 24f),
            size = Size(12f, 10f)
        )
    }

    // 6. Tiger "I-Pass-My-Neighbor" Generator Shed at (7.2, 1.5)
    val genPos = isoProject(7.2f, 1.5f, 0f, centerX, centerY, tileScale, perspective)
    // Generator body with gentle engine rattle vibration
    drawRect(
        color = Color(0xFFD32F2F),
        topLeft = Offset(genPos.x - 12f + generatorVibrate, genPos.y - 18f),
        size = Size(24f, 16f)
    )
    // Metal roll cage frame
    drawRect(
        color = Color(0xFF212121),
        topLeft = Offset(genPos.x - 14f, genPos.y - 20f),
        size = Size(28f, 20f),
        style = Stroke(width = 2f)
    )
    // Fuel tank cap & exhaust pipe
    drawCircle(
        color = Color.Black,
        radius = 3f,
        center = Offset(genPos.x, genPos.y - 20f)
    )

    // 7. Compound Plant Pots (Tropical Crotons / Hibiscus)
    val plantPos = isoProject(1.2f, 6.5f, 0f, centerX, centerY, tileScale, perspective)
    drawOval(
        color = Color(0xFF795548),
        topLeft = Offset(plantPos.x - 8f, plantPos.y - 5f),
        size = Size(16f, 10f)
    )
    // Vibrant green & yellow croton leaves
    drawCircle(color = NaijaGreenLight, radius = 9f, center = Offset(plantPos.x - 4f, plantPos.y - 14f))
    drawCircle(color = NaijaGold, radius = 7f, center = Offset(plantPos.x + 4f, plantPos.y - 16f))
}

// -------------------------------------------------------------
// Drawing Monogamous Modern Duplex Environment
// -------------------------------------------------------------

private fun DrawScope.drawMonogamousDuplex(
    centerX: Float,
    centerY: Float,
    tileScale: Float,
    canvasW: Float,
    canvasH: Float,
    perspective: HomeCameraPerspective,
    animClock: Float,
    pulseBounce: Float
) {
    val gridSize = 8

    // 1. Polished Italian Ceramic Tiles (Cream / Marble grid)
    for (x in 0 until gridSize) {
        for (y in 0 until gridSize) {
            val p0 = isoProject(x.toFloat(), y.toFloat(), 0f, centerX, centerY, tileScale, perspective)
            val p1 = isoProject((x + 1).toFloat(), y.toFloat(), 0f, centerX, centerY, tileScale, perspective)
            val p2 = isoProject((x + 1).toFloat(), (y + 1).toFloat(), 0f, centerX, centerY, tileScale, perspective)
            val p3 = isoProject(x.toFloat(), (y + 1).toFloat(), 0f, centerX, centerY, tileScale, perspective)

            val tilePath = Path().apply {
                moveTo(p0.x, p0.y)
                lineTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                lineTo(p3.x, p3.y)
                close()
            }

            val tileColor = if ((x + y) % 2 == 0) Color(0xFF26333D) else Color(0xFF1E2830)
            drawPath(path = tilePath, color = tileColor, style = Fill)
            drawPath(path = tilePath, color = Color(0x33546E7A), style = Stroke(width = 1f))
        }
    }

    // 2. Interior Parlor Walls with Crown Molding
    val wallHeight = 2.4f
    val wallPath = Path().apply {
        val w0 = isoProject(0f, 0f, 0f, centerX, centerY, tileScale, perspective)
        val w1 = isoProject(gridSize.toFloat(), 0f, 0f, centerX, centerY, tileScale, perspective)
        val w2 = isoProject(gridSize.toFloat(), 0f, wallHeight, centerX, centerY, tileScale, perspective)
        val w3 = isoProject(0f, 0f, wallHeight, centerX, centerY, tileScale, perspective)
        moveTo(w0.x, w0.y)
        lineTo(w1.x, w1.y)
        lineTo(w2.x, w2.y)
        lineTo(w3.x, w3.y)
        close()
    }
    drawPath(path = wallPath, color = Color(0xFF2C3942))

    // 3. Wall-Mounted Big-Screen TV playing news ticker at (3.5, 0.0)
    val tvPos = isoProject(3.5f, 0f, 1.2f, centerX, centerY, tileScale, perspective)
    // TV frame
    drawRect(
        color = Color(0xFF111111),
        topLeft = Offset(tvPos.x - 30f, tvPos.y - 22f),
        size = Size(60f, 35f)
    )
    // Glowing TV Screen (Simulating Channels TV broadcast)
    val screenColor = if (animClock % 0.5f < 0.25f) Color(0xFF1565C0) else Color(0xFF00838F)
    drawRect(
        color = screenColor,
        topLeft = Offset(tvPos.x - 27f, tvPos.y - 19f),
        size = Size(54f, 29f)
    )
    // Red news ticker line on TV
    drawLine(
        color = AlertRed,
        start = Offset(tvPos.x - 25f, tvPos.y + 6f),
        end = Offset(tvPos.x + 25f, tvPos.y + 6f),
        strokeWidth = 2f
    )

    // 4. Modern L-Shaped Leather Sofa at (4.5, 4.5)
    val sofaPos = isoProject(4.5f, 4.5f, 0f, centerX, centerY, tileScale, perspective)
    // Plush rug under sofa
    drawOval(
        color = Color(0xFF37474F),
        topLeft = Offset(sofaPos.x - 35f, sofaPos.y - 18f),
        size = Size(70f, 36f)
    )
    // Main sofa couch
    drawRoundRect(
        color = Color(0xFF455A64),
        topLeft = Offset(sofaPos.x - 24f, sofaPos.y - 20f),
        size = Size(48f, 22f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
    )
    // Ankara colorful throw pillows
    drawCircle(color = NaijaGold, radius = 5f, center = Offset(sofaPos.x - 14f, sofaPos.y - 14f))
    drawCircle(color = RoyalPurple, radius = 5f, center = Offset(sofaPos.x + 14f, sofaPos.y - 14f))

    // 5. Glass Center Coffee Table with Remotes & Malt Drinks
    drawRoundRect(
        color = Color(0xCC78909C),
        topLeft = Offset(sofaPos.x - 14f, sofaPos.y + 4f),
        size = Size(28f, 14f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
    )

    // 6. Dining Table & Family Centenary Portraits at (1.8, 4.5)
    val diningPos = isoProject(1.8f, 4.5f, 0f, centerX, centerY, tileScale, perspective)
    drawRoundRect(
        color = Color(0xFF4E342E),
        topLeft = Offset(diningPos.x - 16f, diningPos.y - 14f),
        size = Size(32f, 20f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
    )
    // Center fruit bowl
    drawCircle(color = NaijaGoldLight, radius = 4f, center = Offset(diningPos.x, diningPos.y - 6f))

    // 7. Rotating Ceiling Fan overhead at center ceiling (4.0, 4.0, 2.5)
    val fanPos = isoProject(4.0f, 4.0f, 2.3f, centerX, centerY, tileScale, perspective)
    val fanAngle = animClock * 4f * PI.toFloat()
    val bladeLength = 22f
    for (i in 0..2) {
        val angle = fanAngle + (i * 2f * PI.toFloat() / 3f)
        val end = Offset(
            fanPos.x + cos(angle) * bladeLength,
            fanPos.y + sin(angle) * (bladeLength * 0.5f)
        )
        drawLine(
            color = Color(0xFFB0BEC5),
            start = fanPos,
            end = end,
            strokeWidth = 3f
        )
    }
    drawCircle(color = Color(0xFF263238), radius = 5f, center = fanPos)
}

// -------------------------------------------------------------
// Drawing Isometric 3D NPCs (Autonomous movement, Gele, Plumbob)
// -------------------------------------------------------------

private fun DrawScope.drawIsometricNPC(
    npc: HomeNPCEntity,
    centerX: Float,
    centerY: Float,
    tileScale: Float,
    perspective: HomeCameraPerspective,
    animClock: Float,
    isFocused: Boolean,
    textMeasurer: TextMeasurer
) {
    val pos = isoProject(npc.gridX, npc.gridY, 0f, centerX, centerY, tileScale, perspective)
    val walkBounce = if (npc.isWalking) sin(npc.animPhase * 2f * PI.toFloat()) * 3f else 0f
    val baseY = pos.y + walkBounce

    // 1. Soft Character Ground Shadow
    drawOval(
        color = Color(0x66000000),
        topLeft = Offset(pos.x - 12f, pos.y - 5f),
        size = Size(24f, 10f)
    )

    // 2. Sims-Style Plumbob (Glowing Diamond) over selected / alert NPC
    if (isFocused || npc.hasUrgentAlert) {
        val plumbobY = baseY - 58f + sin(animClock * 4f * PI.toFloat()) * 3f
        val plumbobColor = if (npc.hasUrgentAlert) AlertRed else SuccessGreen

        val diamondPath = Path().apply {
            moveTo(pos.x, plumbobY - 10f)
            lineTo(pos.x + 6f, plumbobY)
            lineTo(pos.x, plumbobY + 10f)
            lineTo(pos.x - 6f, plumbobY)
            close()
        }
        drawPath(path = diamondPath, color = plumbobColor)
        drawPath(path = diamondPath, color = Color.White, style = Stroke(width = 1f))
    }

    // 3. Body / Outfit (Aso-Ebi, Senator, or Casual print)
    val torsoPath = Path().apply {
        moveTo(pos.x - 7f, baseY - 12f)
        lineTo(pos.x + 7f, baseY - 12f)
        lineTo(pos.x + 9f, baseY - 28f)
        lineTo(pos.x - 9f, baseY - 28f)
        close()
    }
    drawPath(path = torsoPath, color = npc.primaryColor)

    // Legs / Wrapper skirt
    drawRect(
        color = npc.secondaryColor,
        topLeft = Offset(pos.x - 6f, baseY - 12f),
        size = Size(12f, 12f)
    )

    // 4. Head (Warm Nigerian skin tones)
    drawCircle(
        color = Color(0xFF6D4C41),
        radius = 7f,
        center = Offset(pos.x, baseY - 35f)
    )

    // 5. Distinct Headgear (Towering Gele, Fila Cap, Red Chieftaincy, Beret)
    when (npc.headgear) {
        NPCHeadgear.TOWERING_GELE -> {
            // Elegant folded royal purple/gold Gele headtie
            val gelePath = Path().apply {
                moveTo(pos.x - 14f, baseY - 40f)
                lineTo(pos.x + 14f, baseY - 40f)
                lineTo(pos.x + 10f, baseY - 50f)
                lineTo(pos.x - 10f, baseY - 50f)
                close()
            }
            drawPath(path = gelePath, color = npc.primaryColor)
            drawPath(path = gelePath, color = NaijaGold, style = Stroke(width = 1.5f))
        }
        NPCHeadgear.YORUBA_FILA -> {
            // Folded Gobi/Abeti-aja cap tilted gracefully
            drawRoundRect(
                color = npc.primaryColor,
                topLeft = Offset(pos.x - 8f, baseY - 44f),
                size = Size(16f, 8f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
            )
        }
        NPCHeadgear.CHIEFTAINCY_RED_CAP -> {
            // Royal red chieftaincy cap with white eagle feather
            drawRect(
                color = AlertRed,
                topLeft = Offset(pos.x - 7f, baseY - 45f),
                size = Size(14f, 8f)
            )
            // Eagle feather
            drawLine(
                color = Color.White,
                start = Offset(pos.x + 6f, baseY - 44f),
                end = Offset(pos.x + 12f, baseY - 53f),
                strokeWidth = 2f
            )
        }
        NPCHeadgear.COUP_BERET -> {
            drawCircle(
                color = Color(0xFF2E7D32),
                radius = 8f,
                center = Offset(pos.x - 2f, baseY - 40f)
            )
        }
        NPCHeadgear.BASEBALL_CAP -> {
            drawRoundRect(
                color = DeepCharcoal,
                topLeft = Offset(pos.x - 8f, baseY - 43f),
                size = Size(16f, 6f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
            )
        }
        NPCHeadgear.MODERN_HAIR -> {
            // Styled black hair curls
            drawCircle(color = Color(0xFF1A1A1A), radius = 7.5f, center = Offset(pos.x, baseY - 37f))
        }
    }

    // 6. Name Label & Role Tag hovering above head
    val nameTagY = baseY - 50f
    val displayName = "${npc.thoughtEmoji} ${npc.name}"
    val layoutResult = textMeasurer.measure(
        text = displayName,
        style = TextStyle(
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isFocused) NaijaGoldLight else Color.White
        )
    )

    // Pill background for text
    val textW = layoutResult.size.width.toFloat() + 10f
    val textH = layoutResult.size.height.toFloat() + 4f
    drawRoundRect(
        color = if (isFocused) Color(0xEE0A2315) else Color(0xCC111814),
        topLeft = Offset(pos.x - (textW / 2f), nameTagY - (textH / 2f)),
        size = Size(textW, textH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
    )
    if (isFocused) {
        drawRoundRect(
            color = NaijaGold,
            topLeft = Offset(pos.x - (textW / 2f), nameTagY - (textH / 2f)),
            size = Size(textW, textH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
            style = Stroke(width = 1.2f)
        )
    }

    drawText(
        textLayoutResult = layoutResult,
        topLeft = Offset(pos.x - (layoutResult.size.width / 2f), nameTagY - (layoutResult.size.height / 2f))
    )
}

// -------------------------------------------------------------
// Drawing Player 3D Avatar (Walk Cycle, Direction)
// -------------------------------------------------------------

private fun DrawScope.drawPlayerAvatarIso(
    gridX: Float,
    gridY: Float,
    centerX: Float,
    centerY: Float,
    tileScale: Float,
    perspective: HomeCameraPerspective,
    animClock: Float,
    isMoving: Boolean
) {
    val pos = isoProject(gridX, gridY, 0f, centerX, centerY, tileScale, perspective)
    val walkBounce = if (isMoving) sin(animClock * 8f * PI.toFloat()) * 3f else 0f
    val baseY = pos.y + walkBounce

    // Player Shadow
    drawOval(
        color = Color(0x77000000),
        topLeft = Offset(pos.x - 14f, pos.y - 6f),
        size = Size(28f, 12f)
    )

    // Player Selection Golden Ring on Floor
    drawOval(
        color = NaijaGold,
        topLeft = Offset(pos.x - 16f, pos.y - 7f),
        size = Size(32f, 14f),
        style = Stroke(width = 1.8f)
    )

    // Agbada / Senator Robe
    val agbadaPath = Path().apply {
        moveTo(pos.x - 12f, baseY - 12f)
        lineTo(pos.x + 12f, baseY - 12f)
        lineTo(pos.x + 14f, baseY - 30f)
        lineTo(pos.x - 14f, baseY - 30f)
        close()
    }
    drawPath(path = agbadaPath, color = EmeraldPrimary)
    // Gold embroidery chest braid
    drawLine(
        color = NaijaGold,
        start = Offset(pos.x, baseY - 30f),
        end = Offset(pos.x, baseY - 16f),
        strokeWidth = 2f
    )

    // Player Head
    drawCircle(
        color = Color(0xFF5D4037),
        radius = 7.5f,
        center = Offset(pos.x, baseY - 37f)
    )

    // Player Gold Fila Cap
    drawRoundRect(
        color = NaijaGold,
        topLeft = Offset(pos.x - 8f, baseY - 46f),
        size = Size(16f, 8f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
    )

    // Player Tag: "YOU"
    drawCircle(
        color = NaijaGold,
        radius = 4f,
        center = Offset(pos.x, baseY - 52f)
    )
}

// -------------------------------------------------------------
// Atmospheric Ambient Overlays
// -------------------------------------------------------------

private fun DrawScope.drawHomeAtmosphere(
    canvasW: Float,
    canvasH: Float,
    animClock: Float,
    layoutType: FamilyStructure
) {
    // Subtle vignette around edges for cinematic 3D realism
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0x55000000)),
            center = Offset(canvasW / 2f, canvasH / 2f),
            radius = canvasW * 0.7f
        ),
        size = Size(canvasW, canvasH)
    )
}

/**
 * Helper container for unpacking 6 values cleanly.
 */
private data class Tuple6<A, B, C, D, E, F>(
    val a: A, val b: B, val c: C, val d: D, val e: E, val f: F
)
