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
import androidx.compose.ui.graphics.Path
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
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPurple
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
    onRemoveTopping: (String) -> Unit = {},
    onOpenAddModal: () -> Unit = {},
    onOpenFlavorModal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val safeLayers = layers.coerceIn(1, 4)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Interactive Cake Action Buttons Bar (CRUD Operations: Add, Edit, Delete)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Button(
                onClick = onOpenAddModal,
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = CandyActionPink,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1.1f)
                    .heightIn(min = 48.dp)
            ) {
                Text(
                    text = "🍬 " + stringResource(R.string.btn_add_topping),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            androidx.compose.material3.Button(
                onClick = onOpenFlavorModal,
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = CandyActionPurple,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
            ) {
                Text(
                    text = "🍰 " + stringResource(R.string.btn_change_flavor),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            if (toppings.isNotEmpty()) {
                androidx.compose.material3.Button(
                    onClick = onClearToppings,
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = Color(0xFFD32F2F)
                    ),
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = stringResource(R.string.btn_clear_all_toppings),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Removable Topping Chips (Shows equipped toppings with individual DELETE [x] button)
        if (toppings.isNotEmpty()) {
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(toppings.size) { idx ->
                    val topId = toppings[idx]
                    val cleanLabel = topId.substringAfter("topping_").replace("_", " ").replaceFirstChar { it.uppercase() }
                    androidx.compose.material3.Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CandyPink),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🍬 $cleanLabel",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CandyDeepPlum
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(Color(0xFFFFCDD2))
                                    .clickable { onRemoveTopping(topId) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "✕",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tier selector & instruction card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
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
                            .heightIn(min = 44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selected) CandyActionPink else CandyPastelPink.copy(alpha = 0.35f))
                            .border(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) CandyDeepPlum else CandyPurple.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onLayersChanged(t) }
                    ) {
                        Text(
                            text = "$t",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (selected) Color.White else CandyDeepPlum
                        )
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
                // Distribute across tiers: alternating between top tier rim and lower tier ledges
                val tierIdx = (idx % safeLayers)
                val targetTierY = baseY - (safeLayers - tierIdx) * layerHeight
                val targetTierWidth = baseWidth * (1f - (safeLayers - tierIdx - 1) * 0.16f)
                val spreadX = if (toppings.size == 1) {
                    cx
                } else {
                    cx - targetTierWidth * 0.32f + ((idx % 3).toFloat() / 2f.coerceAtLeast(1f)) * (targetTierWidth * 0.64f)
                }
                val ty = targetTierY - 6f
                when {
                    toppingId.contains("cherries") -> {
                        // Glossy pair of red cherries with green stem
                        drawCircle(Color(0xFFD32F2F), radius = 15f, center = Offset(spreadX - 8f, ty))
                        drawCircle(Color.White.copy(alpha = 0.75f), radius = 5f, center = Offset(spreadX - 11f, ty - 4f))
                        drawCircle(Color(0xFFD32F2F), radius = 15f, center = Offset(spreadX + 8f, ty))
                        drawCircle(Color.White.copy(alpha = 0.75f), radius = 5f, center = Offset(spreadX + 5f, ty - 4f))
                        drawLine(Color(0xFF388E3C), Offset(spreadX - 8f, ty - 12f), Offset(spreadX, ty - 26f), strokeWidth = 3f)
                        drawLine(Color(0xFF388E3C), Offset(spreadX + 8f, ty - 12f), Offset(spreadX, ty - 26f), strokeWidth = 3f)
                    }
                    toppingId.contains("stars") || toppingId.contains("glitter") -> {
                        CharacterVectorRenderer.run {
                            drawStarShape(Offset(spreadX, ty - 4f), 18f, CandyYellow)
                        }
                        drawCircle(Color.White, radius = 4f, center = Offset(spreadX - 3f, ty - 7f))
                    }
                    toppingId.contains("chocolate") -> {
                        drawCircle(Color(0xFF3E2723), radius = 14f, center = Offset(spreadX, ty))
                        drawCircle(Color(0xFF5D4037), radius = 9f, center = Offset(spreadX - 2f, ty - 2f))
                        drawCircle(Color.White.copy(alpha = 0.5f), radius = 4f, center = Offset(spreadX - 4f, ty - 4f))
                    }
                    toppingId.contains("macaron") -> {
                        drawRoundRect(CandyPurple, topLeft = Offset(spreadX - 16f, ty - 10f), size = Size(32f, 20f), cornerRadius = CornerRadius(10f, 10f))
                        drawRoundRect(Color.White, topLeft = Offset(spreadX - 14f, ty - 2f), size = Size(28f, 4f), cornerRadius = CornerRadius(2f, 2f))
                    }
                    toppingId.contains("wafer") -> {
                        // Striped candy wafer stick
                        val wafer = Path().apply {
                            moveTo(spreadX - 6f, ty + 10f)
                            lineTo(spreadX + 2f, ty - 24f)
                            lineTo(spreadX + 8f, ty - 22f)
                            lineTo(spreadX, ty + 12f)
                            close()
                        }
                        drawPath(wafer, Color(0xFFFFB74D))
                        drawLine(Color(0xFFD32F2F), Offset(spreadX - 3f, ty + 2f), Offset(spreadX + 3f, ty - 14f), strokeWidth = 2.5f)
                    }
                    toppingId.contains("crystal") -> {
                        CharacterVectorRenderer.run {
                            drawDiamondShape(Offset(spreadX, ty - 4f), 16f, Color(0xFF00E5FF))
                        }
                        drawCircle(Color.White, radius = 4f, center = Offset(spreadX - 2f, ty - 6f))
                    }
                    else -> {
                        // Multi-color rainbow sprinkles scattered nicely
                        for (s in -2..2) {
                            val sprinkleCol = CandyPalette.RainbowColors[(idx + s + 2) % CandyPalette.RainbowColors.size]
                            drawCircle(sprinkleCol, radius = 5.5f, center = Offset(spreadX + s * 8f, ty + (s % 2) * 6f))
                        }
                    }
                }
            }
        }
    }
}
