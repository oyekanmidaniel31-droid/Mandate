package com.example.mandate.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mandate.data.OriginAnswer
import com.example.mandate.data.OriginEngineData
import com.example.mandate.data.OriginQuestion
import com.example.mandate.model.Character
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

import com.example.mandate.ui.components.MoralRadarChart

@Composable
fun OriginEngineScreen(
    currentQuestionIndex: Int,
    originScores: Map<String, Int>,
    isDossierReady: Boolean,
    generatedCharacter: Character,
    onAnswerSelected: (OriginAnswer) -> Unit,
    onAcceptDossier: () -> Unit,
    onResetQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF07120C), Color(0xFF0D2418), Color(0xFF07120C))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (isDossierReady) {
            CitizenDossierView(
                character = generatedCharacter,
                scores = originScores,
                onAcceptDossier = onAcceptDossier,
                onResetQuiz = onResetQuiz
            )
        } else {
            OriginQuestionnaireView(
                currentQuestionIndex = currentQuestionIndex,
                originScores = originScores,
                onAnswerSelected = onAnswerSelected
            )
        }
    }
}

@Composable
private fun OriginQuestionnaireView(
    currentQuestionIndex: Int,
    originScores: Map<String, Int>,
    onAnswerSelected: (OriginAnswer) -> Unit
) {
    val scrollState = rememberScrollState()
    val questions = OriginEngineData.questions
    val question = questions.getOrNull(currentQuestionIndex) ?: questions.first()

    val ambition = originScores["ambition"] ?: 50
    val empathy = originScores["empathy"] ?: 50
    val cunning = originScores["cunning"] ?: 50
    val risk = originScores["risk"] ?: 50
    val loyalty = originScores["loyalty"] ?: 50

    // Contextual graphic scene illustration per dilemma
    val dilemmaSceneRes = when (currentQuestionIndex % 5) {
        0 -> R.drawable.img_street_action_1791644914933
        1 -> R.drawable.img_hero_naija_1791640719865
        2 -> R.drawable.img_power_meeting_1791644463952
        3 -> R.drawable.img_protest_street_1791644444827
        else -> R.drawable.img_villa_gate_1791640975131
    }

    val sceneActionBadge = when (currentQuestionIndex % 5) {
        0 -> "🚨 MOTOR-PARK FLASHPOINT • STREET CODE"
        1 -> "🎓 CAMPUS CONFRONTATION • HIGH STAKES"
        2 -> "💼 GODFATHER'S SECRET DRAWING ROOM"
        3 -> "🔥 STREET BARRICADES • RIOT SHIELD"
        else -> "🏛️ SENATE COMMITTEE TRIBUNAL SHOWDOWN"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(14.dp)
            .testTag("origin_engine_questionnaire"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Emblem & Subtitle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = "Republic Seal",
                tint = NaijaGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "FEDERAL REPUBLIC OF NAIJALAND",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = NaijaGold
            )
        }

        Text(
            text = "THE ORIGIN ENGINE",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimaryLight
        )

        Text(
            text = "Action dilemmas that forge your destiny. Your choices physically configure your starting wealth, state, family, and survival attributes.",
            fontSize = 12.sp,
            color = TextSecondaryLight,
            lineHeight = 16.sp
        )

        // GRAPHICAL MORAL RADAR CANVAS (PENTAGON CHART)
        MoralRadarChart(
            ambition = ambition,
            empathy = empathy,
            cunning = cunning,
            risk = risk,
            loyalty = loyalty
        )

        // Progress Bar & Question Counter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tactical Dilemma ${currentQuestionIndex + 1} of ${questions.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NaijaGoldLight
            )

            val animatedProgress by animateFloatAsState(
                targetValue = (currentQuestionIndex + 1) / questions.size.toFloat(),
                animationSpec = spring(),
                label = "quiz_progress"
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .width(150.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = NaijaGold,
                trackColor = Color(0xFF1B3827)
            )
        }

        // Action Dilemma Hero Graphic Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("origin_dilemma_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(NaijaGold, NaijaGreenLight)))
        ) {
            Column {
                // Cinematic Graphic Illustration Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Image(
                        painter = painterResource(id = dilemmaSceneRes),
                        contentDescription = "Dilemma Scene Artwork",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Dark Vignette
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xAA081A11), Color(0xFA091811))
                                )
                            )
                    )

                    // Tactical Action Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AlertRed,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = sceneActionBadge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Category Pill
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xDD000000),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = question.category.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaijaGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = question.situationTitle,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryLight
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = question.dilemmaText,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = TextPrimaryLight
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Authentic Pidgin Dialogue Callout
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0A1F14),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1B442C)))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = NaijaGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = question.pidginFlavor,
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic,
                                color = NaijaGoldLight,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "SELECT YOUR TACTICAL ACTION MANEUVER",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = NaijaGold,
            letterSpacing = 1.sp
        )

        // Action Maneuver Cards
        question.options.forEachIndexed { index, option ->
            val (actionVerb, actionColor, riskLevel) = when (index) {
                0 -> Triple("⚡ DIRECT FORCE & BRAWL", NaijaGold, "HIGH RISK 💥")
                1 -> Triple("🤝 BACKROOM SECRET PACT", InfoBlue, "MODERATE RISK ⚠️")
                2 -> Triple("🛡️ PEOPLE'S MORAL STAND", SuccessGreen, "LOW RISK 🛡️")
                else -> Triple("🏃 STRATEGIC RETREAT", AlertRed, "EXTREME RISK ⚡")
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("origin_option_${index + 1}")
                    .clickable { onAnswerSelected(option) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF285038)))
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
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NaijaGreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${('A'.code + index).toChar()}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = NaijaGoldLight
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = actionVerb,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = actionColor
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF261818)
                        ) {
                            Text(
                                text = riskLevel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlertRed,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = option.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = option.description,
                        fontSize = 12.sp,
                        color = TextSecondaryLight,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Trait impact tags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        option.traitEffects.forEach { (trait, delta) ->
                            val sign = if (delta > 0) "+$delta" else "$delta"
                            val isPositive = delta > 0
                            val tagColor = when (trait) {
                                "ambition" -> NaijaGold
                                "empathy" -> SuccessGreen
                                "cunning" -> InfoBlue
                                "risk" -> AlertRed
                                "loyalty" -> NaijaGoldLight
                                else -> TextPrimaryLight
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isPositive) Color(0xFF132B1D) else Color(0xFF2E1313)
                            ) {
                                Text(
                                    text = "$trait $sign",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPositive) tagColor else AlertRed,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun CitizenDossierView(
    character: Character,
    scores: Map<String, Int>,
    onAcceptDossier: () -> Unit,
    onResetQuiz: () -> Unit
) {
    val scrollState = rememberScrollState()
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("origin_dossier_reveal")
    ) {
        // Official Dossier Certificate Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, NaijaGold, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF6F3E7)) // Vintage document paper
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_app_icon_1791640702854),
                    contentDescription = "National Crest",
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF0F3E26), CircleShape)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "FEDERAL REPUBLIC OF NAIJALAND",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp,
                    color = Color(0xFF1A1A1A)
                )

                Text(
                    text = "NATIONAL CITIZEN ORIGIN & DESTINY DOSSIER",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 1.sp,
                    color = Color(0xFF0F3E26),
                    textAlign = TextAlign.Center
                )

                Divider(
                    color = Color(0xFF0F3E26),
                    thickness = 2.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = "SUBJECT IDENTIFIER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666)
                )

                Text(
                    text = character.name.uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF111111)
                )

                Text(
                    text = "Alias: \"${character.alias}\" • Starting Age 18 (2005)",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF444444)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Starting Attributes Radar Grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorderGold))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CALIBRATED STARTING ATTRIBUTES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaijaGold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                AttributeRow(title = "Ambition", value = character.ambition, accentColor = NaijaGold)
                Spacer(modifier = Modifier.height(6.dp))
                AttributeRow(title = "Empathy", value = character.empathy, accentColor = SuccessGreen)
                Spacer(modifier = Modifier.height(6.dp))
                AttributeRow(title = "Cunning", value = character.cunning, accentColor = InfoBlue)
                Spacer(modifier = Modifier.height(6.dp))
                AttributeRow(title = "Risk Appetite", value = character.riskAppetite, accentColor = AlertRed)
                Spacer(modifier = Modifier.height(6.dp))
                AttributeRow(title = "Loyalty", value = character.loyalty, accentColor = NaijaGoldLight)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Generated Background & Wealth Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ORIGIN & WEALTH TIER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaijaGold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = character.background.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )

                Text(
                    text = character.background.description,
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0C2417)
                ) {
                    Text(
                        text = "Initial Capital: ₦${numberFormat.format(character.naraBalance)} • Clout: ${character.streetClout}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaijaGoldLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Territory: ${character.stateOfOrigin} (${character.neighbourhood})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryLight
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Family Structure & Family Secret Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "FAMILY DYNAMICS & DARK SECRET",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaijaGold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = character.familyType.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )

                Text(
                    text = character.familyType.dynamicDescription,
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(10.dp))

                // The Family Secret
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF281313),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AlertRed))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "FAMILY SECRET: ${character.familySecret.title}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = character.familySecret.detail,
                            fontSize = 12.sp,
                            color = TextPrimaryLight,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Academic Course Card
        character.academicCourse?.let { course ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ACADEMIC ADMISSION & POLITICAL EDGE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaijaGold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Faculty of ${course.title}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )

                    Text(
                        text = "Skill Advantage: ${course.skillBonus}",
                        fontSize = 12.sp,
                        color = NaijaGoldLight
                    )

                    Text(
                        text = "Political Weapon: ${course.politicalEdge}",
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Call to action button: ACCEPT MANDATE
        Button(
            onClick = onAcceptDossier,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("accept_mandate_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NaijaGreenPrimary, contentColor = NaijaGoldLight)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(imageVector = Icons.Default.HowToReg, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ACCEPT YOUR MANDATE (ENTER NAIJALAND)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onResetQuiz,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("recalibrate_quiz_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryLight)
        ) {
            Text(text = "Recalibrate Choices (Restart Origin Engine)", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AttributeRow(
    title: String,
    value: Int,
    accentColor: Color,
    description: String? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = value / 100f,
        animationSpec = spring(),
        label = "attr_row"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryLight
            )
            Text(
                text = "$value/100",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF132B1E))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(accentColor)
            )
        }

        if (description != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 10.sp,
                color = TextSecondaryLight
            )
        }
    }
}

@Composable
private fun MiniTraitPill(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF0F261B)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 10.sp, color = TextSecondaryLight)
            Text(
                text = "$value%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
