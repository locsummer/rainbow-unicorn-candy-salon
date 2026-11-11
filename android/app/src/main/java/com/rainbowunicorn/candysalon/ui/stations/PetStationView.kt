package com.rainbowunicorn.candysalon.ui.stations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ui.components.CharacterVectorRenderer
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPink
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow

@Composable
fun PetStationView(
    petFed: Boolean,
    petClean: Double,
    petGroomed: Boolean,
    onFeedPet: () -> Unit,
    onWashPetStep: () -> Unit,
    onGroomPet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cleanInt = (petClean * 100).toInt().coerceIn(0, 100)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pet Care Status & Instruction Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = (if (petFed) "✓ " else "○ ") + stringResource(R.string.pet_feed),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (petFed) Color(0xFF2E7D32) else Color.Gray
                    )
                    Text(
                        text = (if (cleanInt >= 100) "✓ " else "○ ") + stringResource(R.string.pet_clean_format, cleanInt),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (cleanInt >= 100) Color(0xFF2E7D32) else Color.Gray
                    )
                    Text(
                        text = (if (petGroomed) "✓ " else "○ ") + stringResource(R.string.pet_groom),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (petGroomed) Color(0xFF2E7D32) else Color.Gray
                    )
                }
            }
        }

        // Cute Interactive Pet Canvas (drag or tap washes!)
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures { onWashPetStep() }
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { _, _ -> onWashPetStep() }
                    )
                }
        ) {
            val cx = size.width / 2f
            val cy = size.height * 0.50f
            val r = size.minDimension * 0.25f

            // Cozy plush cushion
            drawOval(
                color = CandyPastelPink.copy(alpha = 0.7f),
                topLeft = Offset(cx - r * 1.45f, cy + r * 0.6f),
                size = Size(r * 2.9f, r * 0.75f)
            )

            // Baby Dragon / Candy Puppy body
            drawCircle(color = CandyMint, radius = r * 0.9f, center = Offset(cx, cy + r * 0.35f))
            drawCircle(color = Color(0xFFE0F7FA), radius = r * 0.52f, center = Offset(cx, cy + r * 0.42f))

            // Fluffy ears / wings
            val leftEar = Path().apply {
                moveTo(cx - r * 0.65f, cy - r * 0.45f)
                lineTo(cx - r * 1.05f, cy - r * 1.15f)
                lineTo(cx - r * 0.25f, cy - r * 0.85f)
                close()
            }
            val rightEar = Path().apply {
                moveTo(cx + r * 0.65f, cy - r * 0.45f)
                lineTo(cx + r * 1.05f, cy - r * 1.15f)
                lineTo(cx + r * 0.25f, cy - r * 0.85f)
                close()
            }
            drawPath(leftEar, CandyPastelPink)
            drawPath(rightEar, CandyPastelPink)

            // Head
            drawCircle(color = CandyMint, radius = r, center = Offset(cx, cy - r * 0.22f))
            drawCircle(color = CandyDeepPlum.copy(alpha = 0.25f), radius = r, center = Offset(cx, cy - r * 0.22f), style = Stroke(3f))

            // Expressive Eyes
            for (side in listOf(-1f, 1f)) {
                val eyeCenter = Offset(cx + side * r * 0.38f, cy - r * 0.28f)
                drawCircle(CandyDeepPlum, radius = r * 0.16f, center = eyeCenter)
                drawCircle(Color.White, radius = r * 0.06f, center = Offset(eyeCenter.x - r * 0.05f, eyeCenter.y - r * 0.05f))
            }

            // Happy mouth
            drawArc(
                color = CandyDeepPlum,
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(cx - r * 0.22f, cy - r * 0.1f),
                size = Size(r * 0.44f, r * 0.24f),
                style = Stroke(width = 4.5f, cap = StrokeCap.Round)
            )

            // Show remaining mud spots if petClean < 1.0
            if (petClean < 0.99) {
                drawCircle(Color(0xFF795548).copy(alpha = 0.7f), radius = 16f, center = Offset(cx - r * 0.4f, cy + r * 0.1f))
                if (petClean < 0.5) {
                    drawCircle(Color(0xFF795548).copy(alpha = 0.7f), radius = 18f, center = Offset(cx + r * 0.35f, cy + r * 0.35f))
                }
            }

            // Sparkles & ribbon when groomed
            if (petGroomed) {
                with(CharacterVectorRenderer) {
                    drawStarShape(Offset(cx - r * 1.15f, cy - r * 0.7f), 16f, CandyYellow)
                    drawStarShape(Offset(cx + r * 1.15f, cy - r * 0.7f), 16f, CandyYellow)
                    drawStarShape(Offset(cx, cy - r * 1.38f), 18f, CandyPink)
                }
            }

            // Food bowl when fed
            if (petFed) {
                drawRoundRect(
                    color = CandyPink,
                    topLeft = Offset(cx + r * 0.85f, cy + r * 0.65f),
                    size = Size(r * 0.7f, r * 0.25f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawCircle(CandyYellow, radius = 9f, center = Offset(cx + r * 1.1f, cy + r * 0.62f))
                drawCircle(CandyPurple, radius = 8f, center = Offset(cx + r * 1.3f, cy + r * 0.64f))
            }
        }

        // Tactile Care Action Buttons Row (all >= 48dp touch height!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NavButton(
                icon = Icons.Default.Cookie,
                label = stringResource(R.string.pet_feed),
                onClick = onFeedPet,
                containerColor = if (petFed) CandyMint else CandyActionPink,
                contentColor = if (petFed) CandyDeepPlum else Color.White,
                compact = true,
                modifier = Modifier.weight(1f)
            )
            NavButton(
                icon = Icons.Default.WaterDrop,
                label = stringResource(R.string.pet_bath),
                onClick = onWashPetStep,
                containerColor = if (cleanInt >= 100) CandyMint else CandyActionPurple,
                contentColor = if (cleanInt >= 100) CandyDeepPlum else Color.White,
                compact = true,
                modifier = Modifier.weight(1f)
            )
            NavButton(
                icon = Icons.Default.AutoAwesome,
                label = stringResource(R.string.pet_groom),
                onClick = onGroomPet,
                containerColor = if (petGroomed) CandyMint else CandyYellow,
                contentColor = CandyDeepPlum,
                compact = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
