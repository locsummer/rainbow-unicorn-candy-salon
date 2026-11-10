package com.rainbowunicorn.candysalon.ui.stations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.rainbowunicorn.candysalon.ui.components.CharacterCanvasRenderer
import com.rainbowunicorn.core.models.CharacterState

data class DirtSpot(val id: Int, val offset: Offset, var isCleaned: Boolean = false)

@Composable
fun SpaStationView(
    characterState: CharacterState,
    onCleanPercentChanged: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val dirtSpots = remember {
        mutableStateListOf(
            DirtSpot(1, Offset(0.35f, 0.45f)),
            DirtSpot(2, Offset(0.55f, 0.40f)),
            DirtSpot(3, Offset(0.45f, 0.65f)),
            DirtSpot(4, Offset(0.65f, 0.60f)),
            DirtSpot(5, Offset(0.30f, 0.60f))
        )
    }

    var spongePos by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { spongePos = it },
                    onDragEnd = { spongePos = null },
                    onDragCancel = { spongePos = null },
                    onDrag = { change, _ ->
                        spongePos = change.position
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()

                        for (i in dirtSpots.indices) {
                            val spot = dirtSpots[i]
                            if (!spot.isCleaned) {
                                val spotScreenPos = Offset(spot.offset.x * w, spot.offset.y * h)
                                val dist = (spotScreenPos - change.position).getDistance()
                                if (dist < 80f) {
                                    dirtSpots[i] = spot.copy(isCleaned = true)
                                }
                            }
                        }

                        val cleanedCount = dirtSpots.count { it.isCleaned }
                        val pct = cleanedCount.toDouble() / dirtSpots.size.toDouble()
                        onCleanPercentChanged(pct)
                    }
                )
            }
    ) {
        CharacterCanvasRenderer(
            characterState = characterState,
            modifier = Modifier.fillMaxSize()
        )

        // Draw dirt spots and sponge
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (spot in dirtSpots) {
                if (!spot.isCleaned) {
                    val pos = Offset(spot.offset.x * size.width, spot.offset.y * size.height)
                    drawCircle(
                        color = Color(0xFF6B4423).copy(alpha = 0.75f),
                        radius = 28f,
                        center = pos
                    )
                }
            }

            spongePos?.let { pos ->
                drawCircle(
                    color = Color(0xFFFFF176).copy(alpha = 0.85f),
                    radius = 35f,
                    center = pos
                )
                // Soap bubbles
                drawCircle(Color.White.copy(alpha = 0.8f), radius = 12f, center = Offset(pos.x - 20f, pos.y - 20f))
                drawCircle(Color.White.copy(alpha = 0.8f), radius = 8f, center = Offset(pos.x + 25f, pos.y - 15f))
            }
        }
    }
}
