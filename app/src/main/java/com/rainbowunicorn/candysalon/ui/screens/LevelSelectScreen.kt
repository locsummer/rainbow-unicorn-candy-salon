package com.rainbowunicorn.candysalon.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
                            .height(114.dp)
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
                                .padding(horizontal = 6.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.level_short_format, lvl.id),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CandyPink
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (lvl.characterType == CharacterType.UNICORN) "🦄" else "👸",
                                    fontSize = 12.sp
                                )
                            }

                            Text(
                                text = lvl.localizedName(language),
                                fontSize = 11.sp,
                                lineHeight = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CandyDeepPlum,
                                maxLines = 2,
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
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "VIP",
                                            fontSize = 11.sp,
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
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                else -> {
                                    Row(horizontalArrangement = Arrangement.Center) {
                                        for (s in 1..3) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (s <= stars) CandyYellow else Color.LightGray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
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
