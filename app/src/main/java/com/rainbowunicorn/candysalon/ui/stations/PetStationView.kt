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
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow

@Composable
fun PetStationView(
    petFed: Boolean,
    petClean: Double,
    petGroomed: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height * 0.55f
            val r = size.minDimension * 0.25f

            // Baby Dragon / Pet Body
            drawCircle(
                color = CandyMint,
                radius = r,
                center = Offset(cx, cy)
            )

            // Pet Ears / Wings
            drawCircle(CandyPastelPink, radius = r * 0.35f, center = Offset(cx - r * 0.7f, cy - r * 0.6f))
            drawCircle(CandyPastelPink, radius = r * 0.35f, center = Offset(cx + r * 0.7f, cy - r * 0.6f))

            // Cheerful eyes
            drawCircle(CandyDeepPlum, radius = r * 0.15f, center = Offset(cx - r * 0.35f, cy - r * 0.1f))
            drawCircle(CandyDeepPlum, radius = r * 0.15f, center = Offset(cx + r * 0.35f, cy - r * 0.1f))
            drawCircle(Color.White, radius = r * 0.05f, center = Offset(cx - r * 0.38f, cy - r * 0.13f))
            drawCircle(Color.White, radius = r * 0.05f, center = Offset(cx + r * 0.32f, cy - r * 0.13f))

            // Happy smile
            drawArc(
                color = CandyDeepPlum,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - r * 0.2f, cy + r * 0.1f),
                size = Size(r * 0.4f, r * 0.25f)
            )

            // Sparkles when groomed
            if (petGroomed) {
                drawCircle(CandyYellow, radius = 10f, center = Offset(cx - r * 1.1f, cy - r * 0.8f))
                drawCircle(CandyYellow, radius = 8f, center = Offset(cx + r * 1.1f, cy - r * 0.6f))
                drawCircle(CandyPink, radius = 9f, center = Offset(cx, cy - r * 1.2f))
            }

            // Food dish
            if (petFed) {
                drawRoundRect(
                    color = CandyPink,
                    topLeft = Offset(cx - 40f, cy + r * 0.8f),
                    size = Size(80f, 25f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawCircle(CandyYellow, radius = 8f, center = Offset(cx, cy + r * 0.75f))
            }
        }
    }
}
