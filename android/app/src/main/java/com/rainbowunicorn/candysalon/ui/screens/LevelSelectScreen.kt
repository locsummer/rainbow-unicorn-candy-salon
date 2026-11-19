package com.rainbowunicorn.candysalon.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ui.components.CandyBalanceBadge
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.components.ParentalGateModal
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPink
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.models.CharacterType
import com.rainbowunicorn.core.models.Level
import com.rainbowunicorn.core.models.SavedLevelCheckpoint
import kotlinx.coroutines.launch

@Composable
fun LevelSelectScreen(
    levels: List<Level>,
    language: String,
    candyBalance: Int,
    unlockedLevels: Set<Int>,
    bestStars: Map<Int, Int>,
    hasPremium: Boolean,
    activeCheckpoint: SavedLevelCheckpoint?,
    statusMessage: String?,
    onClearStatusMessage: () -> Unit,
    onLevelSelected: (level: Level, resumeCheckpoint: Boolean) -> Unit,
    onBackClicked: () -> Unit,
    onUnlockPremiumRequested: () -> Unit
) {
    var showParentalGateForPremium by remember { mutableStateOf(false) }
    var levelToResumePrompt by remember { mutableStateOf<Level?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lockedHint = stringResource(R.string.level_locked_hint)

    LaunchedEffect(statusMessage) {
        if (!statusMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(statusMessage)
            onClearStatusMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(CandyPastelPink.copy(alpha = 0.45f), Color.White)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Portrait Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NavButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    label = stringResource(R.string.btn_back),
                    onClick = onBackClicked,
                    containerColor = CandyActionPink,
                    compact = true
                )
                CandyBalanceBadge(candyBalance = candyBalance)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.level_select_title),
                style = MaterialTheme.typography.titleLarge,
                color = CandyDeepPlum,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Portrait 3-Column Level Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(levels, key = { it.id }) { lvl ->
                    val isUnlocked = unlockedLevels.contains(lvl.id)
                    val stars = bestStars[lvl.id] ?: 0
                    val isPremiumLocked = lvl.requiresPremium && !hasPremium
                    val hasCheckpoint = isUnlocked && activeCheckpoint?.levelId == lvl.id

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isPremiumLocked -> Color(0xFFF3E5F5)
                                isUnlocked -> Color.White
                                else -> Color(0xFFF1F1F1)
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 4.dp else 1.dp),
                        modifier = Modifier
                            .height(134.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(
                                width = if (hasCheckpoint) 2.5.dp else if (isUnlocked) 2.dp else 1.dp,
                                color = when {
                                    hasCheckpoint -> CandyPurple
                                    isUnlocked -> CandyPink
                                    isPremiumLocked -> CandyPurple.copy(alpha = 0.45f)
                                    else -> Color.LightGray
                                },
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable {
                                if (isPremiumLocked) {
                                    showParentalGateForPremium = true
                                } else if (isUnlocked) {
                                    if (hasCheckpoint) {
                                        levelToResumePrompt = lvl
                                    } else {
                                        onLevelSelected(lvl, false)
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(lockedHint)
                                    }
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.level_short_format, lvl.id),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CandyPink
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (lvl.characterType == CharacterType.UNICORN) "🦄" else "👸",
                                    fontSize = 11.sp
                                )
                            }

                            LevelThematicThumbnail(
                                theme = lvl.theme,
                                modifier = Modifier.size(36.dp)
                            )

                            Text(
                                text = lvl.localizedName(language),
                                fontSize = 10.sp,
                                lineHeight = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CandyDeepPlum,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )

                            when {
                                isPremiumLocked -> {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "VIP",
                                            tint = CandyPurple,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "VIP",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CandyPurple
                                        )
                                    }
                                }
                                !isUnlocked -> {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                else -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        for (s in 1..3) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (s <= stars) CandyYellow else Color(0xFFE0E0E0),
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "+50🍬",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CandyPink
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (showParentalGateForPremium) {
            ParentalGateModal(
                onVerified = {
                    showParentalGateForPremium = false
                    onUnlockPremiumRequested()
                },
                onDismiss = { showParentalGateForPremium = false }
            )
        }

        levelToResumePrompt?.let { lvl ->
            Dialog(
                onDismissRequest = { levelToResumePrompt = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.90f)
                        .widthIn(max = 400.dp)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.dialog_resume_title, lvl.id),
                            style = MaterialTheme.typography.titleLarge,
                            color = CandyDeepPlum,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(R.string.dialog_resume_message),
                            style = MaterialTheme.typography.bodyLarge,
                            color = CandyDeepPlum.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        NavButton(
                            icon = Icons.Default.PlayArrow,
                            label = stringResource(R.string.btn_resume),
                            onClick = {
                                levelToResumePrompt = null
                                onLevelSelected(lvl, true)
                            },
                            containerColor = CandyActionPink,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        NavButton(
                            icon = Icons.Default.Refresh,
                            label = stringResource(R.string.btn_start_fresh),
                            onClick = {
                                levelToResumePrompt = null
                                onLevelSelected(lvl, false)
                            },
                            containerColor = CandyMint,
                            contentColor = CandyDeepPlum,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(onClick = { levelToResumePrompt = null }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = stringResource(R.string.btn_cancel), color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LevelThematicThumbnail(theme: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val cy = h * 0.5f
        val r = minOf(w, h) * 0.45f

        when (theme) {
            "cotton_candy" -> {
                drawLine(
                    color = Color(0xFFFFD54F),
                    start = Offset(cx - r * 0.3f, cy + r * 0.85f),
                    end = Offset(cx + r * 0.2f, cy - r * 0.2f),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
                drawCircle(CandyPastelPink, radius = r * 0.45f, center = Offset(cx - r * 0.15f, cy - r * 0.1f))
                drawCircle(Color(0xFF80DEEA), radius = r * 0.40f, center = Offset(cx + r * 0.2f, cy - r * 0.2f))
                drawCircle(Color(0xFFE1BEE7), radius = r * 0.38f, center = Offset(cx, cy + r * 0.15f))
            }
            "rainbow" -> {
                val colors = listOf(CandyPink, CandyYellow, CandyMint, Color(0xFF00E5FF), CandyPurple)
                colors.forEachIndexed { i, c ->
                    val inset = i * (r * 0.15f)
                    drawArc(
                        color = c,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(cx - r + inset, cy - r * 0.5f + inset),
                        size = Size((r - inset) * 2f, (r - inset) * 2f),
                        style = Stroke(width = 3f, cap = StrokeCap.Round)
                    )
                }
            }
            "strawberry" -> {
                val sPath = Path().apply {
                    moveTo(cx, cy + r * 0.75f)
                    cubicTo(cx - r * 0.8f, cy + r * 0.3f, cx - r * 0.7f, cy - r * 0.4f, cx, cy - r * 0.45f)
                    cubicTo(cx + r * 0.7f, cy - r * 0.4f, cx + r * 0.8f, cy + r * 0.3f, cx, cy + r * 0.75f)
                    close()
                }
                drawPath(sPath, Brush.radialGradient(listOf(Color(0xFFFF5252), Color(0xFFD32F2F)), center = Offset(cx - r * 0.2f, cy), radius = r))
                drawCircle(Color(0xFF4CAF50), radius = r * 0.22f, center = Offset(cx - r * 0.25f, cy - r * 0.5f))
                drawCircle(Color(0xFF4CAF50), radius = r * 0.22f, center = Offset(cx + r * 0.25f, cy - r * 0.5f))
                drawCircle(Color(0xFF81C784), radius = r * 0.20f, center = Offset(cx, cy - r * 0.6f))
                drawCircle(Color(0xFFFFF9C4), radius = 2f, center = Offset(cx - r * 0.25f, cy))
                drawCircle(Color(0xFFFFF9C4), radius = 2f, center = Offset(cx + r * 0.25f, cy))
            }
            "galaxy" -> {
                drawCircle(
                    brush = Brush.radialGradient(listOf(Color(0xFFAB47BC), Color(0xFF4A148C)), center = Offset(cx - r * 0.2f, cy - r * 0.2f), radius = r * 0.6f),
                    radius = r * 0.55f,
                    center = Offset(cx, cy)
                )
                drawArc(
                    color = Color(0xFF00E5FF),
                    startAngle = -20f,
                    sweepAngle = 220f,
                    useCenter = false,
                    topLeft = Offset(cx - r * 0.95f, cy - r * 0.3f),
                    size = Size(r * 1.9f, r * 0.6f),
                    style = Stroke(width = 3f)
                )
            }
            "bakery" -> {
                drawRoundRect(Color(0xFFD7CCC8), topLeft = Offset(cx - r * 0.65f, cy), size = Size(r * 1.3f, r * 0.75f), cornerRadius = CornerRadius(4f, 4f))
                drawRoundRect(CandyPink, topLeft = Offset(cx - r * 0.75f, cy - r * 0.35f), size = Size(r * 1.5f, r * 0.45f), cornerRadius = CornerRadius(8f, 8f))
                drawCircle(Color(0xFFD32F2F), radius = r * 0.22f, center = Offset(cx, cy - r * 0.48f))
            }
            "bubblegum" -> {
                drawCircle(
                    brush = Brush.radialGradient(listOf(Color(0xFFFF80AB), Color(0xFFF50057)), center = Offset(cx - r * 0.25f, cy - r * 0.25f), radius = r * 0.75f),
                    radius = r * 0.75f,
                    center = Offset(cx, cy)
                )
                drawCircle(Color.White.copy(alpha = 0.6f), radius = r * 0.2f, center = Offset(cx - r * 0.28f, cy - r * 0.28f))
            }
            "lagoon" -> {
                drawCircle(Color(0xFF80DEEA), radius = r * 0.75f, center = Offset(cx, cy))
                drawCircle(Color.White, radius = r * 0.32f, center = Offset(cx, cy))
                drawCircle(CandyPastelPink, radius = r * 0.25f, center = Offset(cx, cy))
            }
            "carousel", "carnival" -> {
                val tent = Path().apply {
                    moveTo(cx, cy - r * 0.8f)
                    lineTo(cx + r * 0.85f, cy + r * 0.65f)
                    lineTo(cx - r * 0.85f, cy + r * 0.65f)
                    close()
                }
                drawPath(tent, CandyPurple)
                drawLine(Color(0xFFFFD54F), Offset(cx, cy - r * 0.8f), Offset(cx, cy + r * 0.65f), strokeWidth = 3f)
                drawCircle(Color(0xFFFFD54F), radius = r * 0.15f, center = Offset(cx, cy - r * 0.8f))
            }
            "royal_ball", "coronation" -> {
                val crown = Path().apply {
                    moveTo(cx - r * 0.75f, cy + r * 0.5f)
                    lineTo(cx - r * 0.7f, cy - r * 0.2f)
                    lineTo(cx - r * 0.35f, cy + r * 0.1f)
                    lineTo(cx, cy - r * 0.6f)
                    lineTo(cx + r * 0.35f, cy + r * 0.1f)
                    lineTo(cx + r * 0.7f, cy - r * 0.2f)
                    lineTo(cx + r * 0.75f, cy + r * 0.5f)
                    close()
                }
                drawPath(crown, Brush.verticalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFFA000))))
                drawCircle(CandyPink, radius = r * 0.14f, center = Offset(cx, cy - r * 0.6f))
            }
            "crystal_sugar" -> {
                val diamond = Path().apply {
                    moveTo(cx, cy - r * 0.85f)
                    lineTo(cx + r * 0.75f, cy)
                    lineTo(cx, cy + r * 0.85f)
                    lineTo(cx - r * 0.75f, cy)
                    close()
                }
                drawPath(diamond, Color(0xFF00E5FF))
                drawCircle(Color.White, radius = r * 0.18f, center = Offset(cx - r * 0.15f, cy - r * 0.15f))
            }
            "marshmallow" -> {
                drawRoundRect(Color(0xFFFFF0F5), topLeft = Offset(cx - r * 0.65f, cy - r * 0.55f), size = Size(r * 1.3f, r * 1.1f), cornerRadius = CornerRadius(10f, 10f))
                drawRoundRect(CandyPastelPink, topLeft = Offset(cx - r * 0.55f, cy - r * 0.45f), size = Size(r * 1.1f, r * 0.9f), cornerRadius = CornerRadius(8f, 8f))
            }
            "peppermint" -> {
                drawCircle(Color.White, radius = r * 0.75f, center = Offset(cx, cy))
                for (p in 0..5) {
                    val angle = p * 60f
                    drawArc(Color(0xFFE53935), startAngle = angle, sweepAngle = 30f, useCenter = true, topLeft = Offset(cx - r * 0.75f, cy - r * 0.75f), size = Size(r * 1.5f, r * 1.5f))
                }
            }
            "sugarplum" -> {
                drawCircle(Color(0xFF7B1FA2), radius = r * 0.65f, center = Offset(cx, cy))
                drawCircle(Color(0xFFCE93D8), radius = r * 0.35f, center = Offset(cx - r * 0.15f, cy - r * 0.15f))
            }
            "neon_gummy" -> {
                drawCircle(Color(0xFF00E676), radius = r * 0.65f, center = Offset(cx, cy))
                drawCircle(Color.White.copy(alpha = 0.7f), radius = r * 0.25f, center = Offset(cx - r * 0.18f, cy - r * 0.18f))
            }
            "choco_truffle" -> {
                drawCircle(Color(0xFF3E2723), radius = r * 0.75f, center = Offset(cx, cy))
                drawCircle(Color(0xFF5D4037), radius = r * 0.55f, center = Offset(cx, cy))
                drawCircle(Color(0xFFFFD54F), radius = r * 0.15f, center = Offset(cx - r * 0.2f, cy - r * 0.2f))
            }
            "starlight_safari" -> {
                drawCircle(Color(0xFFFFD54F), radius = r * 0.65f, center = Offset(cx, cy))
                drawCircle(Color.White, radius = r * 0.55f, center = Offset(cx + r * 0.25f, cy - r * 0.1f))
            }
            "macaron" -> {
                drawRoundRect(Color(0xFFCE93D8), topLeft = Offset(cx - r * 0.75f, cy - r * 0.45f), size = Size(r * 1.5f, r * 0.9f), cornerRadius = CornerRadius(12f, 12f))
                drawRoundRect(Color.White, topLeft = Offset(cx - r * 0.7f, cy - r * 0.1f), size = Size(r * 1.4f, r * 0.2f), cornerRadius = CornerRadius(4f, 4f))
            }
            "dragon_fruit" -> {
                drawCircle(Color(0xFFE91E63), radius = r * 0.75f, center = Offset(cx, cy))
                drawCircle(Color.White, radius = r * 0.55f, center = Offset(cx, cy))
                drawCircle(Color.Black, radius = 2f, center = Offset(cx - r * 0.2f, cy))
                drawCircle(Color.Black, radius = 2f, center = Offset(cx + r * 0.2f, cy))
            }
            else -> {
                drawCircle(CandyPink, radius = r * 0.7f, center = Offset(cx, cy))
            }
        }
    }
}
