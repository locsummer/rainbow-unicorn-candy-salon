package com.rainbowunicorn.candysalon.ui.stations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow

@Composable
fun CakeStationView(
    layers: Int,
    flavor: String?,
    toppings: List<String>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val baseY = size.height * 0.85f

            // Cake stand
            drawOval(
                color = Color.LightGray,
                topLeft = Offset(cx - size.width * 0.35f, baseY),
                size = Size(size.width * 0.7f, 30f)
            )

            val layerHeight = 50f
            val baseWidth = size.width * 0.55f

            for (lvl in 0 until layers.coerceIn(1, 4)) {
                val currentWidth = baseWidth * (1f - lvl * 0.18f)
                val y = baseY - (lvl + 1) * layerHeight

                val layerColor = when (lvl % 3) {
                    0 -> CandyPastelPink
                    1 -> CandyYellow
                    else -> CandyPurple
                }

                drawRoundRect(
                    color = layerColor,
                    topLeft = Offset(cx - currentWidth / 2f, y),
                    size = Size(currentWidth, layerHeight),
                    cornerRadius = CornerRadius(16f, 16f)
                )

                // Cream drip
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(cx - currentWidth / 2f, y),
                    size = Size(currentWidth, 14f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }

            // Draw toppings on top
            val topLayerY = baseY - layers * layerHeight
            for ((idx, _) in toppings.withIndex()) {
                val tx = cx - (toppings.size * 12f) + (idx * 24f)
                drawCircle(CandyPink, radius = 8f, center = Offset(tx, topLayerY - 10f))
            }
        }
    }
}
