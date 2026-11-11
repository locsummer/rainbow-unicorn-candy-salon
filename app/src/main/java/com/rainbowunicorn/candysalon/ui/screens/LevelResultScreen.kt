package com.rainbowunicorn.candysalon.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.audio.SoundManager
import com.rainbowunicorn.candysalon.ui.components.CharacterCanvasRenderer
import com.rainbowunicorn.candysalon.ui.components.CharacterPose
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.components.rememberReduceMotion
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPink
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.services.ScoreEvaluation
import java.util.Random

@Composable
fun LevelResultScreen(
    characterState: CharacterState,
    evaluation: ScoreEvaluation,
    candyEarned: Int,
    soundManager: SoundManager,
    onNextLevelClicked: () -> Unit,
    onEditLookClicked: () -> Unit,
    onReplayClicked: () -> Unit,
    onBackToMapClicked: () -> Unit
) {
    val reduceMotion = rememberReduceMotion()

    LaunchedEffect(evaluation.isWin) {
        if (evaluation.isWin) {
            soundManager.playVictory()
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
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (evaluation.isWin) {
            ConfettiCanvas(
                reduceMotion = reduceMotion,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Fixed top-left Back to Story Map button (RULE-070)
        NavButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            label = stringResource(R.string.btn_back),
            onClick = onBackToMapClicked,
            containerColor = CandyActionPink,
            compact = true,
            modifier = Modifier.align(Alignment.TopStart)
        )

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 440.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (evaluation.isWin) stringResource(R.string.level_victory) else stringResource(R.string.level_try_again),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (evaluation.isWin) CandyActionPink else CandyActionPurple,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Character preview of completed makeover
                CharacterCanvasRenderer(
                    characterState = characterState,
                    pose = if (evaluation.isWin) CharacterPose.HAPPY else CharacterPose.IDLE,
                    modifier = Modifier.size(136.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.score_total, evaluation.totalScore),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CandyDeepPlum
                )
                Text(
                    text = stringResource(R.string.score_match, (evaluation.matchScore * 100).toInt()),
                    style = MaterialTheme.typography.bodyLarge,
                    color = CandyDeepPlum.copy(alpha = 0.75f)
                )
                Text(
                    text = stringResource(R.string.score_quality, (evaluation.qualityScore * 100).toInt()),
                    style = MaterialTheme.typography.bodyLarge,
                    color = CandyDeepPlum.copy(alpha = 0.75f)
                )

                if (!evaluation.isWin) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.level_try_again_hint),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = CandyPurple,
                        textAlign = TextAlign.Center
                    )
                }

                if (candyEarned > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🍬 " + stringResource(R.string.candy_earned, candyEarned),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CandyActionPink
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 2-row Portrait Button Stack (zero horizontal squeeze!)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NavButton(
                        icon = Icons.Default.Edit,
                        label = stringResource(R.string.btn_edit_look),
                        onClick = onEditLookClicked,
                        containerColor = CandyYellow,
                        contentColor = CandyDeepPlum,
                        compact = true,
                        modifier = Modifier.weight(1f)
                    )

                    NavButton(
                        icon = Icons.Default.Refresh,
                        label = stringResource(R.string.btn_replay),
                        onClick = onReplayClicked,
                        containerColor = Color(0xFFE0E0E0),
                        contentColor = CandyDeepPlum,
                        compact = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (evaluation.isWin) {
                    NavButton(
                        icon = Icons.Default.PlayArrow,
                        label = stringResource(R.string.btn_next),
                        onClick = onNextLevelClicked,
                        containerColor = CandyMint,
                        contentColor = CandyDeepPlum,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )
                } else {
                    NavButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        label = stringResource(R.string.btn_exit),
                        onClick = onBackToMapClicked,
                        containerColor = CandyPastelPink,
                        contentColor = CandyDeepPlum,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ConfettiCanvas(
    reduceMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val particles = remember {
        val random = Random(42)
        val colors = listOf(CandyPink, CandyYellow, CandyMint, CandyPurple)
        List(120) { i ->
            Triple(random.nextFloat(), random.nextFloat(), colors[i % colors.size])
        }
    }

    val phase = if (reduceMotion) {
        0f
    } else {
        val transition = rememberInfiniteTransition(label = "confetti")
        val value by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3600, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "confettiFall"
        )
        value
    }

    Canvas(modifier = modifier) {
        // Strict <= 300 particles cap (RULE-082)
        particles.forEach { (nx, ny, col) ->
            val yFrac = (ny + phase) % 1f
            drawCircle(
                color = col.copy(alpha = 0.8f),
                radius = 6f,
                center = Offset(nx * size.width, yFrac * size.height)
            )
        }
    }
}
