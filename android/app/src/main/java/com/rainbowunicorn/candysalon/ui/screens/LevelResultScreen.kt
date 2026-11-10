package com.rainbowunicorn.candysalon.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.audio.SoundManager
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.services.ScoreEvaluation
import java.util.Random

@Composable
fun LevelResultScreen(
    evaluation: ScoreEvaluation,
    candyEarned: Int,
    soundManager: SoundManager,
    onNextLevelClicked: () -> Unit,
    onEditLookClicked: () -> Unit,
    onReplayClicked: () -> Unit
) {
    LaunchedEffect(evaluation.isWin) {
        if (evaluation.isWin) {
            soundManager.playVictory()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CandyPastelPink.copy(alpha = 0.35f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Particle Confetti Background (Capped <= 300 particles RULE-082)
        if (evaluation.isWin) {
            ConfettiCanvas(modifier = Modifier.fillMaxSize())
        }

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (evaluation.isWin) stringResource(R.string.level_victory) else stringResource(R.string.level_try_again),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (evaluation.isWin) CandyPink else CandyPurple
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Stars display
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (s in 1..3) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (s <= evaluation.stars) CandyYellow else Color.LightGray,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Score metrics
                Text(
                    text = stringResource(R.string.score_total, evaluation.totalScore),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CandyDeepPlum
                )
                Text(
                    text = stringResource(R.string.score_match, (evaluation.matchScore * 100).toInt()) +
                            " | " + stringResource(R.string.score_quality, (evaluation.qualityScore * 100).toInt()),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                if (candyEarned > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🍬 " + stringResource(R.string.candy_earned, candyEarned),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CandyPink
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NavButton(
                        icon = Icons.Default.Edit,
                        label = stringResource(R.string.btn_edit_look),
                        onClick = onEditLookClicked,
                        containerColor = CandyYellow,
                        contentColor = CandyDeepPlum
                    )

                    NavButton(
                        icon = Icons.Default.Refresh,
                        label = stringResource(R.string.btn_replay),
                        onClick = onReplayClicked,
                        containerColor = Color.LightGray,
                        contentColor = Color.DarkGray
                    )

                    if (evaluation.isWin) {
                        NavButton(
                            icon = Icons.Default.PlayArrow,
                            label = stringResource(R.string.btn_next),
                            onClick = onNextLevelClicked,
                            containerColor = CandyMint,
                            contentColor = CandyDeepPlum
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfettiCanvas(modifier: Modifier = Modifier) {
    val random = remember { Random(42) }
    val colors = listOf(CandyPink, CandyYellow, CandyMint, CandyPurple)

    Canvas(modifier = modifier) {
        // Render 120 confetti particles (well below 300 cap RULE-082)
        for (i in 0 until 120) {
            val x = random.nextFloat() * size.width
            val y = random.nextFloat() * size.height
            val color = colors[i % colors.size]
            drawCircle(color = color.copy(alpha = 0.8f), radius = 6f, center = Offset(x, y))
        }
    }
}
