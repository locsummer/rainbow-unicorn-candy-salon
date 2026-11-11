package com.rainbowunicorn.candysalon.ui.stations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ui.components.AnimatedCharacterStage
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.models.CharacterState

data class DirtSpot(val id: Int, val offset: Offset, val isCleaned: Boolean = false)

@Composable
fun SpaStationView(
    characterState: CharacterState,
    onCleanPercentChanged: (Double) -> Unit,
    modifier: Modifier = Modifier,
    initialCleanPercent: Double = 0.0,
    resetTrigger: Int = 0
) {
    val dirtSpots = remember(resetTrigger) {
        val spots = listOf(
            DirtSpot(1, Offset(0.42f, 0.36f)),
            DirtSpot(2, Offset(0.58f, 0.36f)),
            DirtSpot(3, Offset(0.50f, 0.48f)),
            DirtSpot(4, Offset(0.40f, 0.62f)),
            DirtSpot(5, Offset(0.60f, 0.62f))
        )
        val preCleaned = (initialCleanPercent * spots.size).toInt().coerceIn(0, spots.size)
        mutableStateListOf<DirtSpot>().apply {
            spots.forEachIndexed { idx, spot ->
                add(if (resetTrigger == 0 && idx < preCleaned) spot.copy(isCleaned = true) else spot)
            }
        }
    }

    LaunchedEffect(resetTrigger) {
        val pct = dirtSpots.count { it.isCleaned }.toDouble() / dirtSpots.size.toDouble()
        onCleanPercentChanged(pct)
    }

    var spongePos by remember { mutableStateOf<Offset?>(null) }
    val density = LocalDensity.current
    val hitRadiusPx = with(density) { 46.dp.toPx() }
    val spotRadiusPx = with(density) { 18.dp.toPx() }
    val spongeRadiusPx = with(density) { 26.dp.toPx() }

    val cleanedPct = (dirtSpots.count { it.isCleaned }.toDouble() / dirtSpots.size.toDouble())
    val cleanInt = (cleanedPct * 100).toInt()

    fun scrubAt(pos: Offset, width: Int, height: Int) {
        var changed = false
        for (i in dirtSpots.indices) {
            val spot = dirtSpots[i]
            if (!spot.isCleaned) {
                val spotPos = Offset(spot.offset.x * width, spot.offset.y * height)
                if ((spotPos - pos).getDistance() <= hitRadiusPx) {
                    dirtSpots[i] = spot.copy(isCleaned = true)
                    changed = true
                }
            }
        }
        if (changed) {
            val pct = dirtSpots.count { it.isCleaned }.toDouble() / dirtSpots.size.toDouble()
            onCleanPercentChanged(pct)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Instruction + Progress Bar Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🧼 " + stringResource(R.string.spa_clean_percent, cleanInt),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (cleanInt >= 80) Color(0xFF2E7D32) else CandyDeepPlum
                    )
                    Text(
                        text = if (cleanInt >= 80) "✓ ≥ 80%" else "Min 80%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (cleanInt >= 80) Color(0xFF2E7D32) else CandyPink
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { cleanedPct.toFloat() },
                    color = if (cleanInt >= 80) CandyMint else CandyPink,
                    trackColor = CandyPastelPink.copy(alpha = 0.35f),
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }

        // Interactive Spa Character Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(resetTrigger) {
                    detectTapGestures { pos ->
                        spongePos = pos
                        scrubAt(pos, size.width, size.height)
                    }
                }
                .pointerInput(resetTrigger) {
                    detectDragGestures(
                        onDragStart = { pos ->
                            spongePos = pos
                            scrubAt(pos, size.width, size.height)
                        },
                        onDragEnd = { spongePos = null },
                        onDragCancel = { spongePos = null },
                        onDrag = { change, _ ->
                            spongePos = change.position
                            scrubAt(change.position, size.width, size.height)
                        }
                    )
                }
        ) {
            AnimatedCharacterStage(
                characterState = characterState,
                modifier = Modifier.fillMaxSize()
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                for (spot in dirtSpots) {
                    if (!spot.isCleaned) {
                        val pos = Offset(spot.offset.x * size.width, spot.offset.y * size.height)
                        drawCircle(
                            color = Color(0xFF795548).copy(alpha = 0.85f),
                            radius = spotRadiusPx,
                            center = pos
                        )
                        drawCircle(
                            color = Color(0xFF5D4037).copy(alpha = 0.65f),
                            radius = spotRadiusPx * 0.55f,
                            center = Offset(pos.x + spotRadiusPx * 0.35f, pos.y - spotRadiusPx * 0.2f)
                        )
                    }
                }

                spongePos?.let { pos ->
                    drawCircle(
                        color = CandyYellow.copy(alpha = 0.9f),
                        radius = spongeRadiusPx,
                        center = pos
                    )
                    drawCircle(Color.White.copy(alpha = 0.85f), radius = spongeRadiusPx * 0.35f, center = Offset(pos.x - spongeRadiusPx * 0.8f, pos.y - spongeRadiusPx * 0.7f))
                    drawCircle(Color.White.copy(alpha = 0.85f), radius = spongeRadiusPx * 0.25f, center = Offset(pos.x + spongeRadiusPx * 0.9f, pos.y - spongeRadiusPx * 0.5f))
                }
            }
        }
    }
}
