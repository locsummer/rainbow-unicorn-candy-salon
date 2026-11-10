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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.components.ParentalGateModal
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.models.Level

@Composable
fun LevelSelectScreen(
    levels: List<Level>,
    unlockedLevels: Set<Int>,
    bestStars: Map<Int, Int>,
    hasPremium: Boolean,
    onLevelSelected: (Level) -> Unit,
    onBackClicked: () -> Unit,
    onUnlockPremiumRequested: () -> Unit
) {
    var showParentalGateForPremium by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CandyPastelPink.copy(alpha = 0.3f))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    label = stringResource(R.string.btn_back),
                    onClick = onBackClicked,
                    containerColor = CandyPink
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.btn_play) + " (20 Levels)",
                    style = MaterialTheme.typography.titleLarge,
                    color = CandyDeepPlum,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 20 Levels Grid in Landscape (2 rows of 10)
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(levels, key = { it.id }) { lvl ->
                    val isUnlocked = unlockedLevels.contains(lvl.id)
                    val stars = bestStars[lvl.id] ?: 0
                    val isPremiumLocked = lvl.requiresPremium && !hasPremium

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isPremiumLocked -> Color(0xFFE0E0E0)
                                isUnlocked -> Color.White
                                else -> Color(0xFFF5F5F5)
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 4.dp else 1.dp),
                        modifier = Modifier
                            .height(86.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (isUnlocked) 2.dp else 0.dp,
                                color = if (isUnlocked) CandyPink else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                if (isPremiumLocked) {
                                    showParentalGateForPremium = true
                                } else if (isUnlocked) {
                                    onLevelSelected(lvl)
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Lvl ${lvl.id}: ${lvl.name}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CandyDeepPlum,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )

                            if (isPremiumLocked) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Premium Locked",
                                        tint = CandyPink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "VIP",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CandyPink
                                    )
                                }
                            } else if (!isUnlocked) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                // Stars display
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

        if (showParentalGateForPremium) {
            ParentalGateModal(
                onVerified = {
                    showParentalGateForPremium = false
                    onUnlockPremiumRequested()
                },
                onDismiss = { showParentalGateForPremium = false }
            )
        }
    }
}
