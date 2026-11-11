package com.rainbowunicorn.candysalon.ui.stations

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ui.components.CandyPalette
import com.rainbowunicorn.candysalon.ui.components.CharacterVectorRenderer
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPink
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow

@Composable
fun CakeStationView(
    layers: Int,
    flavor: String?,
    toppings: List<String>,
    onLayersChanged: (Int) -> Unit,
    onClearToppings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safeLayers = layers.coerceIn(1, 4)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tier selector & instruction card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎂 " + stringResource(R.string.cake_layers_label) + ":",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CandyDeepPlum
                    )
                    for (t in 1..4) {
                        val selected = safeLayers == t
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (selected) CandyActionPink else CandyPastelPink.copy(alpha = 0.35f))
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) CandyDeepPlum else CandyPurple.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { onLayersChanged(t) }
                        ) {
                            Text(
                                text = "$t",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (selected) Color.White else CandyDeepPlum
                            )
                        }
                    }
                    if (toppings.isNotEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(CandyYellow)
                                .clickable { onClearToppings() }
                                .padding(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = stringResource(R.string.cake_clear_toppings),
                                tint = CandyDeepPlum,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Cake Canvas Stage
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val cx = size.width / 2f
            val baseY = size.height * 0.84f
            val (icingBase, icingDrip) = CandyPalette.cakeFlavorColors(flavor)

            // Pedestal stand
            drawRoundRect(
                color = Color(0xFFB0BEC5),
                topLeft = Offset(cx - size.width * 0.08f, baseY + 10f),
                size = Size(size.width * 0.16f, size.height * 0.09f),
                cornerRadius = CornerRadius(12f, 12f)
            )
            drawOval(
                color = Color(0xFFE0E0E0),
                topLeft = Offset(cx - size.width * 0.38f, baseY),
                size = Size(size.width * 0.76f, 28f)
            )

            val layerHeight = (size.height * 0.145f).coerceIn(48f, 92f)
            val baseWidth = size.width * 0.64f

            for (lvl in 0 until safeLayers) {
                val currentWidth = baseWidth * (1f - lvl * 0.16f)
                val y = baseY - (lvl + 1) * layerHeight

                val spongeTint = when (lvl % 3) {
                    0 -> icingBase
                    1 -> icingBase.copy(alpha = 0.92f)
                    else -> CandyPastelPink.copy(alpha = 0.7f)
                }

                // Cake Sponge Body
                drawRoundRect(
                    color = spongeTint,
                    topLeft = Offset(cx - currentWidth / 2f, y),
                    size = Size(currentWidth, layerHeight),
                    cornerRadius = CornerRadius(18f, 18f)
                )
                drawRoundRect(
                    color = CandyDeepPlum.copy(alpha = 0.25f),
                    topLeft = Offset(cx - currentWidth / 2f, y),
                    size = Size(currentWidth, layerHeight),
                    cornerRadius = CornerRadius(18f, 18f),
                    style = Stroke(width = 2.5f)
                )

                // Drip Frosting along top rim
                drawRoundRect(
                    color = icingDrip,
                    topLeft = Offset(cx - currentWidth / 2f, y),
                    size = Size(currentWidth, layerHeight * 0.32f),
                    cornerRadius = CornerRadius(14f, 14f)
                )
                // Drip scallops
                for (d in 0..4) {
                    val dx = cx - currentWidth * 0.38f + d * (currentWidth * 0.19f)
                    drawCircle(
                        color = icingDrip,
                        radius = layerHeight * 0.15f,
                        center = Offset(dx, y + layerHeight * 0.34f)
                    )
                }
            }

            // Birthday candle / rainbow topper on very top
            val topY = baseY - safeLayers * layerHeight
            val topWidth = baseWidth * (1f - (safeLayers - 1) * 0.16f)
            drawRoundRect(
                color = CandyYellow,
                topLeft = Offset(cx - 8f, topY - 36f),
                size = Size(16f, 36f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawCircle(CandyPink, radius = 9f, center = Offset(cx, topY - 44f))

            // Draw individual toppings distributed cleanly across cake tiers
            toppings.take(8).forEachIndexed { idx, toppingId ->
                val spreadX = if (toppings.size == 1) cx - topWidth * 0.2f else {
                    cx - topWidth * 0.36f + (idx.toFloat() / (toppings.size - 1).coerceAtLeast(1)) * (topWidth * 0.72f)
                }
                val ty = topY - 6f
                when {
                    toppingId.contains("cherries") -> {
                        drawCircle(Color(0xFFE53935), radius = 12f, center = Offset(spreadX, ty))
                        drawCircle(Color.White.copy(alpha = 0.7f), radius = 4f, center = Offset(spreadX - 3f, ty - 3f))
                    }
                    toppingId.contains("stars") || toppingId.contains("glitter") -> {
                        with(CharacterVectorRenderer) {
                            drawStarShape(Offset(spreadX, ty - 4f), 13f, CandyYellow)
                        }
                    }
                    toppingId.contains("chocolate") -> {
                        drawCircle(Color(0xFF4E342E), radius = 10f, center = Offset(spreadX, ty))
                    }
                    toppingId.contains("macaron") -> {
                        drawRoundRect(CandyPurple, topLeft = Offset(spreadX - 13f, ty - 8f), size = Size(26f, 16f), cornerRadius = CornerRadius(8f, 8f))
                    }
                    toppingId.contains("crystal") -> {
                        with(CharacterVectorRenderer) {
                            drawDiamondShape(Offset(spreadX, ty - 2f), 12f, CandyMint)
                        }
                    }
                    else -> {
                        val sprinkleCol = CandyPalette.RainbowColors[idx % CandyPalette.RainbowColors.size]
                        drawCircle(sprinkleCol, radius = 9f, center = Offset(spreadX, ty))
                    }
                }
            }
        }
    }
}
