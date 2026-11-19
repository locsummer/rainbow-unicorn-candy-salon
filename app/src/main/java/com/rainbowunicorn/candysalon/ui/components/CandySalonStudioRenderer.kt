package com.rainbowunicorn.candysalon.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.core.models.CharacterState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-fidelity graphics renderer for the "CHOOSE YOUR STYLE" Salon Studio.
 * Faithfully reproduces 100% of the visual elements from the reference image:
 *  A. 3D Princess beauty model (center-right, almond eyes, turquoise eyeshadow, green braided bun,
 *     rainbow locks, candy heart bodice with golden cone, flared skater skirt with peppermint swirls).
 *  B. 6-shelf vertical closet tower on the left ("CLOSET ROOM", teal rims, wood trays, dresses,
 *     perfume bottles, rainbow lipsticks, jewelry chest).
 *  C. Background: Left pink unicorn/pegasus wallpaper; Right arched window with sheer curtains,
 *     luminous 7-color curved rainbow, sparkles, giant chocolate cupcake & mini easel portrait.
 *  D. Top-right circular back button with rainbow spiral swirl border.
 *  E. Bottom full-width magenta banner with 3D cartoon rainbow text: CHOOSE YOUR STYLE.
 */
object CandySalonStudioRenderer {

    // Palette constants extracted from reference image
    val PinkWallpaperTop = Color(0xFFFF69B4)
    val PinkWallpaperBottom = Color(0xFFFF85C0)
    val WhitePure = Color(0xFFFFFFFF)
    val GoldStar = Color(0xFFFFD54F)
    val DeepPurple = Color(0xFF311B92)
    val MagentaHeart = Color(0xFFE91E63)
    val TealRim = Color(0xFF00C2CB)
    val TealDark = Color(0xFF00838F)
    val PeachWood = Color(0xFFF4A261)
    val PeachWoodDark = Color(0xFFE76F51)
    val ScallopCyan = Color(0xFF00E5FF)
    val ChocolateIcing = Color(0xFF3E2723)
    val ChocolateHighlight = Color(0xFF5D4037)
    val CupcakeLiner = Color(0xFFFFCA28)

    val RainbowBandColors = listOf(
        Color(0xFFFF1744), // Red
        Color(0xFFFF9100), // Orange
        Color(0xFFFFEA00), // Yellow
        Color(0xFF00E676), // Green
        Color(0xFF00E5FF), // Cyan
        Color(0xFF2979FF), // Blue
        Color(0xFFD500F9)  // Purple
    )

    // =========================================================================
    // 1. BACKGROUND LAYER
    // =========================================================================

    fun DrawScope.drawStudioBackground(
        width: Float,
        height: Float,
        magicMistProgress: Float = 0f
    ) {
        val splitX = width * 0.43f

        // --- LEFT REGION: Pink Unicorn Wallpaper ---
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(PinkWallpaperTop, PinkWallpaperBottom),
                startY = 0f,
                endY = height
            ),
            topLeft = Offset(0f, 0f),
            size = Size(splitX, height)
        )

        // Wallpaper Pattern (Pegasus Unicorns, Clouds, Stars, Hearts)
        drawWallpaperPattern(width = splitX, height = height)

        // --- RIGHT REGION: Wall & Arched Window ---
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFFF5F8), Color(0xFFFFEBF0)),
                startY = 0f,
                endY = height
            ),
            topLeft = Offset(splitX, 0f),
            size = Size(width - splitX, height)
        )

        // Large Arched Window
        val winLeft = splitX + width * 0.04f
        val winTop = height * 0.02f
        val winWidth = width * 0.50f
        val winHeight = height * 0.68f

        drawArchedWindow(
            winLeft = winLeft,
            winTop = winTop,
            winWidth = winWidth,
            winHeight = winHeight
        )

        // Giant Chocolate Cupcake at bottom right
        drawGiantCupcake(
            cx = width * 0.95f,
            bottomY = height * 0.88f,
            radius = width * 0.17f
        )
    }

    private fun DrawScope.drawWallpaperPattern(width: Float, height: Float) {
        val rows = 6
        val cols = 2
        val cellW = width / cols
        val cellH = height / rows

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val cx = c * cellW + cellW * 0.5f + (if (r % 2 == 1) cellW * 0.20f else -cellW * 0.20f)
                val cy = r * cellH + cellH * 0.5f

                // Draw cute flying pegasus
                drawCutePegasus(cx = cx, cy = cy - cellH * 0.1f, scale = cellW * 0.0070f)

                // Clouds
                drawCuteCloud(cx = cx - cellW * 0.35f, cy = cy + cellH * 0.25f, r = cellW * 0.11f)
                drawCuteCloud(cx = cx + cellW * 0.35f, cy = cy - cellH * 0.30f, r = cellW * 0.09f)

                // Golden stars
                drawStar5(cx = cx - cellW * 0.30f, cy = cy - cellH * 0.22f, r = cellW * 0.065f, color = GoldStar)
                drawStar5(cx = cx + cellW * 0.30f, cy = cy + cellH * 0.20f, r = cellW * 0.055f, color = GoldStar)

                // Hearts
                drawCuteHeart(cx = cx + cellW * 0.26f, cy = cy - cellH * 0.10f, size = cellW * 0.065f, color = Color(0xFFFF4081))
                drawCuteHeart(cx = cx - cellW * 0.24f, cy = cy + cellH * 0.32f, size = cellW * 0.055f, color = WhitePure.copy(alpha = 0.9f))
            }
        }
    }

    private fun DrawScope.drawCutePegasus(cx: Float, cy: Float, scale: Float) {
        withTransform({
            translate(left = cx, top = cy)
            scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
        }) {
            // Mane (cyan & purple locks)
            val mane = Path().apply {
                moveTo(-15f, -22f)
                cubicTo(-35f, -30f, -40f, -10f, -30f, 5f)
                cubicTo(-40f, 15f, -30f, 30f, -20f, 25f)
                close()
            }
            drawPath(mane, Brush.linearGradient(listOf(Color(0xFF80DEEA), Color(0xFFCE93D8))))

            // Body
            val body = Path().apply {
                moveTo(-25f, 15f)
                cubicTo(-10f, 0f, 20f, 0f, 35f, 15f)
                cubicTo(45f, 25f, 35f, 40f, 20f, 42f)
                lineTo(22f, 60f)
                lineTo(14f, 60f)
                lineTo(10f, 42f)
                lineTo(-10f, 42f)
                lineTo(-12f, 60f)
                lineTo(-20f, 60f)
                lineTo(-18f, 35f)
                close()
            }
            drawPath(body, WhitePure)
            drawPath(body, Color(0xFF90A4AE).copy(alpha = 0.4f), style = Stroke(width = 2.5f))

            // Head & Snout
            val head = Path().apply {
                moveTo(15f, 10f)
                lineTo(25f, -15f)
                cubicTo(26f, -28f, 45f, -30f, 50f, -18f)
                cubicTo(52f, -10f, 40f, -2f, 32f, 0f)
                lineTo(25f, 12f)
                close()
            }
            drawPath(head, WhitePure)
            drawPath(head, Color(0xFF90A4AE).copy(alpha = 0.4f), style = Stroke(width = 2.5f))

            // Golden Horn
            val horn = Path().apply {
                moveTo(32f, -24f)
                lineTo(42f, -45f)
                lineTo(38f, -22f)
                close()
            }
            drawPath(horn, GoldStar)

            // Wing
            val wing = Path().apply {
                moveTo(0f, 10f)
                cubicTo(10f, -20f, 30f, -25f, 35f, -15f)
                cubicTo(25f, 5f, 15f, 15f, 0f, 22f)
                close()
            }
            drawPath(wing, WhitePure)
            drawPath(wing, Color(0xFFFF80AB).copy(alpha = 0.5f), style = Stroke(width = 2f))

            // Eye
            drawCircle(Color(0xFF263238), radius = 2.5f, center = Offset(38f, -18f))
            drawCircle(WhitePure, radius = 1f, center = Offset(37f, -19f))

            // Tail
            val tail = Path().apply {
                moveTo(-25f, 18f)
                cubicTo(-45f, 10f, -50f, 35f, -38f, 42f)
                cubicTo(-45f, 50f, -30f, 55f, -22f, 38f)
                close()
            }
            drawPath(tail, Brush.linearGradient(listOf(Color(0xFF80DEEA), Color(0xFFFF80AB))))
        }
    }

    private fun DrawScope.drawCuteCloud(cx: Float, cy: Float, r: Float) {
        val cloudColor = WhitePure.copy(alpha = 0.88f)
        drawCircle(cloudColor, radius = r * 0.7f, center = Offset(cx - r * 0.6f, cy))
        drawCircle(cloudColor, radius = r * 0.95f, center = Offset(cx, cy - r * 0.3f))
        drawCircle(cloudColor, radius = r * 0.65f, center = Offset(cx + r * 0.6f, cy))
        drawRoundRect(
            color = cloudColor,
            topLeft = Offset(cx - r * 0.8f, cy - r * 0.1f),
            size = Size(r * 1.6f, r * 0.6f),
            cornerRadius = CornerRadius(r * 0.3f, r * 0.3f)
        )
    }

    private fun DrawScope.drawStar5(cx: Float, cy: Float, r: Float, color: Color) {
        val path = Path()
        val innerR = r * 0.42f
        for (i in 0 until 10) {
            val radius = if (i % 2 == 0) r else innerR
            val angle = i * PI.toFloat() / 5f - PI.toFloat() / 2f
            val x = cx + radius * cos(angle)
            val y = cy + radius * sin(angle)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color)
    }

    private fun DrawScope.drawCuteHeart(cx: Float, cy: Float, size: Float, color: Color) {
        val path = Path().apply {
            moveTo(cx, cy + size * 0.4f)
            cubicTo(cx - size * 0.6f, cy - size * 0.2f, cx - size * 0.6f, cy - size * 0.6f, cx, cy - size * 0.3f)
            cubicTo(cx + size * 0.6f, cy - size * 0.6f, cx + size * 0.6f, cy - size * 0.2f, cx, cy + size * 0.4f)
            close()
        }
        drawPath(path, color)
    }

    private fun DrawScope.drawArchedWindow(
        winLeft: Float,
        winTop: Float,
        winWidth: Float,
        winHeight: Float
    ) {
        val archRadius = winWidth * 0.5f

        val winPath = Path().apply {
            moveTo(winLeft, winTop + archRadius)
            arcTo(
                rect = Rect(winLeft, winTop, winLeft + winWidth, winTop + archRadius * 2f),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            lineTo(winLeft + winWidth, winTop + winHeight)
            lineTo(winLeft, winTop + winHeight)
            close()
        }

        // Sky outside window
        clipPath(winPath) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE1F5FE), Color(0xFFFCE4EC), Color(0xFFFFF9C4)),
                    startY = winTop,
                    endY = winTop + winHeight
                ),
                topLeft = Offset(winLeft, winTop),
                size = Size(winWidth, winHeight)
            )

            // Luminous 7-Color Curved Rainbow Arc sweeping across the window
            val rainbowCenter = Offset(winLeft + winWidth * 0.95f, winTop + winHeight * 0.75f)
            val baseRadius = winWidth * 0.92f
            val bandWidth = winWidth * 0.045f

            RainbowBandColors.forEachIndexed { index, bandColor ->
                val r = baseRadius - index * bandWidth
                drawCircle(
                    color = bandColor.copy(alpha = 0.92f),
                    radius = r,
                    center = rainbowCenter,
                    style = Stroke(width = bandWidth + 1.5f)
                )
            }

            // Twinkling Star Sparkles outside in the sky
            val sparkles = listOf(
                Offset(winLeft + winWidth * 0.30f, winTop + winHeight * 0.18f),
                Offset(winLeft + winWidth * 0.65f, winTop + winHeight * 0.15f),
                Offset(winLeft + winWidth * 0.18f, winTop + winHeight * 0.38f),
                Offset(winLeft + winWidth * 0.82f, winTop + winHeight * 0.35f)
            )
            sparkles.forEach { sp ->
                drawStar4Sparkle(sp.x, sp.y, winWidth * 0.055f, WhitePure)
            }
        }

        // White Window Frame
        drawPath(winPath, color = WhitePure, style = Stroke(width = winWidth * 0.05f))

        // Center vertical divider
        val midX = winLeft + winWidth * 0.5f
        drawLine(
            color = WhitePure,
            start = Offset(midX, winTop),
            end = Offset(midX, winTop + winHeight),
            strokeWidth = winWidth * 0.035f
        )
        // Horizontal dividers
        val row1Y = winTop + winHeight * 0.38f
        val row2Y = winTop + winHeight * 0.68f
        drawLine(
            color = WhitePure,
            start = Offset(winLeft, row1Y),
            end = Offset(winLeft + winWidth, row1Y),
            strokeWidth = winWidth * 0.03f
        )
        drawLine(
            color = WhitePure,
            start = Offset(winLeft, row2Y),
            end = Offset(winLeft + winWidth, row2Y),
            strokeWidth = winWidth * 0.03f
        )

        // Draped Lavender/White Sheer Curtains on left & right of window
        drawCurtains(winLeft, winTop, winWidth, winHeight)
    }

    private fun DrawScope.drawStar4Sparkle(cx: Float, cy: Float, r: Float, color: Color) {
        val path = Path().apply {
            moveTo(cx, cy - r)
            quadraticBezierTo(cx, cy, cx + r * 0.25f, cy)
            quadraticBezierTo(cx, cy, cx, cy + r)
            quadraticBezierTo(cx, cy, cx - r * 0.25f, cy)
            quadraticBezierTo(cx, cy, cx, cy - r)
            close()
        }
        drawPath(path, color)
        val pathH = Path().apply {
            moveTo(cx - r, cy)
            quadraticBezierTo(cx, cy, cx, cy + r * 0.25f)
            quadraticBezierTo(cx, cy, cx + r, cy)
            quadraticBezierTo(cx, cy, cx, cy - r * 0.25f)
            quadraticBezierTo(cx, cy, cx - r, cy)
            close()
        }
        drawPath(pathH, color)
        drawCircle(WhitePure, radius = r * 0.2f, center = Offset(cx, cy))
    }

    private fun DrawScope.drawCurtains(
        winLeft: Float,
        winTop: Float,
        winWidth: Float,
        winHeight: Float
    ) {
        val curtainBrush = Brush.verticalGradient(
            listOf(Color(0xFFEDE7F6).copy(alpha = 0.92f), Color(0xFFD1C4E9).copy(alpha = 0.75f))
        )

        // Left Curtain Drape
        val leftCurtain = Path().apply {
            moveTo(winLeft - winWidth * 0.04f, winTop)
            cubicTo(
                winLeft + winWidth * 0.25f, winTop + winHeight * 0.15f,
                winLeft + winWidth * 0.05f, winTop + winHeight * 0.55f,
                winLeft + winWidth * 0.18f, winTop + winHeight * 0.95f
            )
            lineTo(winLeft - winWidth * 0.06f, winTop + winHeight * 0.95f)
            close()
        }
        drawPath(leftCurtain, curtainBrush)

        // Right Curtain Drape
        val rightCurtain = Path().apply {
            moveTo(winLeft + winWidth * 1.04f, winTop)
            cubicTo(
                winLeft + winWidth * 0.75f, winTop + winHeight * 0.15f,
                winLeft + winWidth * 0.95f, winTop + winHeight * 0.55f,
                winLeft + winWidth * 0.82f, winTop + winHeight * 0.95f
            )
            lineTo(winLeft + winWidth * 1.06f, winTop + winHeight * 0.95f)
            close()
        }
        drawPath(rightCurtain, curtainBrush)
    }

    private fun DrawScope.drawGiantCupcake(cx: Float, bottomY: Float, radius: Float) {
        val linerTopW = radius * 1.25f
        val linerBottomW = radius * 0.85f
        val linerH = radius * 0.75f
        val linerTopY = bottomY - linerH

        val liner = Path().apply {
            moveTo(cx - linerTopW * 0.5f, linerTopY)
            lineTo(cx + linerTopW * 0.5f, linerTopY)
            lineTo(cx + linerBottomW * 0.5f, bottomY)
            lineTo(cx - linerBottomW * 0.5f, bottomY)
            close()
        }
        drawPath(liner, CupcakeLiner)
        for (i in -4..4) {
            val t = i / 5f
            drawLine(
                color = Color(0xFFF57F17),
                start = Offset(cx + t * linerTopW * 0.48f, linerTopY),
                end = Offset(cx + t * linerBottomW * 0.48f, bottomY),
                strokeWidth = 2.5f
            )
        }

        val frostingH = radius * 0.95f
        val frostingY = linerTopY - frostingH * 0.4f
        val dome = Path().apply {
            moveTo(cx - linerTopW * 0.65f, linerTopY + 5f)
            cubicTo(
                cx - radius * 0.85f, frostingY - frostingH * 0.6f,
                cx + radius * 0.85f, frostingY - frostingH * 0.6f,
                cx + linerTopW * 0.65f, linerTopY + 5f
            )
            close()
        }
        drawPath(dome, ChocolateIcing)

        drawArc(
            color = ChocolateHighlight,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - radius * 0.55f, frostingY - frostingH * 0.5f),
            size = Size(radius * 1.1f, frostingH * 0.75f),
            style = Stroke(width = 6f, cap = StrokeCap.Round)
        )

        val sprinkleColors = listOf(WhitePure, ScallopCyan, Color(0xFFFF4081), GoldStar)
        val sprinklePositions = listOf(
            Offset(cx - radius * 0.35f, frostingY - frostingH * 0.2f),
            Offset(cx + radius * 0.25f, frostingY - frostingH * 0.35f),
            Offset(cx - radius * 0.10f, frostingY - frostingH * 0.45f),
            Offset(cx + radius * 0.42f, frostingY - frostingH * 0.15f),
            Offset(cx - radius * 0.45f, frostingY)
        )
        sprinklePositions.forEachIndexed { i, pos ->
            drawCircle(sprinkleColors[i % sprinkleColors.size], radius = 4f, center = pos)
        }
    }

    fun DrawScope.drawMiniEaselPortrait(
        cx: Float,
        bottomY: Float,
        frameW: Float,
        frameH: Float
    ) {
        val standH = frameH * 0.35f
        val frameTopY = bottomY - frameH - standH * 0.3f

        val legBrush = Brush.linearGradient(listOf(Color(0xFF8D6E63), Color(0xFF4E342E)))
        drawLine(legBrush, Offset(cx, frameTopY - 8f), Offset(cx - frameW * 0.65f, bottomY), strokeWidth = 5f)
        drawLine(legBrush, Offset(cx, frameTopY - 8f), Offset(cx + frameW * 0.65f, bottomY), strokeWidth = 5f)
        drawLine(legBrush, Offset(cx, frameTopY - 8f), Offset(cx, bottomY - 5f), strokeWidth = 4f)

        drawCircle(GoldStar, radius = 6f, center = Offset(cx, frameTopY - 10f))

        drawRoundRect(
            color = Color(0xFF5D4037),
            topLeft = Offset(cx - frameW * 0.5f, frameTopY),
            size = Size(frameW, frameH),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = GoldStar,
            topLeft = Offset(cx - frameW * 0.46f, frameTopY + frameH * 0.05f),
            size = Size(frameW * 0.92f, frameH * 0.90f),
            cornerRadius = CornerRadius(6f, 6f),
            style = Stroke(width = 3f)
        )

        val cardRect = Rect(cx - frameW * 0.42f, frameTopY + frameH * 0.08f, cx + frameW * 0.42f, frameTopY + frameH * 0.88f)
        clipPath(Path().apply { addRect(cardRect) }) {
            drawRect(
                brush = Brush.verticalGradient(listOf(Color(0xFFE1F5FE), Color(0xFFF8BBD0))),
                topLeft = cardRect.topLeft,
                size = cardRect.size
            )
            drawCircle(Color(0xFF00E676), radius = frameW * 0.18f, center = Offset(cx, frameTopY + frameH * 0.34f))
            drawCircle(Color(0xFFFFDFC4), radius = frameW * 0.22f, center = Offset(cx, frameTopY + frameH * 0.52f))
            drawCircle(Color(0xFF7C4DFF), radius = frameW * 0.16f, center = Offset(cx - frameW * 0.22f, frameTopY + frameH * 0.58f))
            drawCircle(Color(0xFF00B0FF), radius = frameW * 0.16f, center = Offset(cx + frameW * 0.22f, frameTopY + frameH * 0.58f))
            drawCircle(DeepPurple, radius = 2f, center = Offset(cx - frameW * 0.09f, frameTopY + frameH * 0.50f))
            drawCircle(DeepPurple, radius = 2f, center = Offset(cx + frameW * 0.09f, frameTopY + frameH * 0.50f))
            drawCircle(Color(0xFFE91E63), radius = 2.5f, center = Offset(cx, frameTopY + frameH * 0.60f))
        }
    }

    fun DrawScope.drawMagicStylingWand(
        wandX: Float,
        wandY: Float,
        mistProgress: Float
    ) {
        withTransform({
            translate(left = wandX, top = wandY)
            rotate(degrees = -32f, pivot = Offset.Zero)
        }) {
            // Bottle body (white cylinder with silver border)
            drawRoundRect(
                color = WhitePure,
                topLeft = Offset(-10f, -8f),
                size = Size(20f, 48f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRoundRect(
                color = Color(0xFFCFD8DC),
                topLeft = Offset(-10f, -8f),
                size = Size(20f, 48f),
                cornerRadius = CornerRadius(6f, 6f),
                style = Stroke(width = 1.5f)
            )
            // Purple grip section
            drawRoundRect(
                color = DeepPurple,
                topLeft = Offset(-10.5f, 10f),
                size = Size(21f, 26f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Silver cap & sprayer tip pointing UPWARDS towards hair
            val nozzle = Path().apply {
                moveTo(-5f, -8f)
                lineTo(5f, -8f)
                lineTo(2.5f, -22f)
                lineTo(-2.5f, -22f)
                close()
            }
            drawPath(nozzle, Color(0xFFB0BEC5))
            drawCircle(Color(0xFF7C4DFF), radius = 3.5f, center = Offset(0f, -24f))

            // Magic Mist spraying UPWARDS into the hair
            val mistPulse = (sin(mistProgress * 2f * PI.toFloat()) * 0.5f + 0.5f)
            val mistAlpha = 0.55f + mistPulse * 0.40f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE040FB).copy(alpha = mistAlpha), Color(0xFF80D8FF).copy(alpha = 0f)),
                    center = Offset(0f, -45f),
                    radius = 35f
                ),
                radius = 35f,
                center = Offset(0f, -45f)
            )
            drawStar4Sparkle(0f, -40f, 9f, GoldStar)
            drawStar4Sparkle(-14f, -50f, 7f, WhitePure)
            drawStar4Sparkle(12f, -55f, 8f, ScallopCyan)
            drawStar4Sparkle(-6f, -65f, 6f, Color(0xFFFF4081))
        }
    }

    // =========================================================================
    // 2. LEFT VERTICAL CLOSET TOWER (6 SHELVES)
    // =========================================================================

    fun DrawScope.drawClosetTower(
        leftX: Float,
        topY: Float,
        towerW: Float,
        towerH: Float,
        selectedShelfIndex: Int = -1,
        shelfPulseProgress: Float = 0f
    ) {
        val shelfCount = 6
        val shelfSpacing = towerH / shelfCount

        // Closet Tower Backing Frame
        drawRoundRect(
            color = WhitePure,
            topLeft = Offset(leftX, topY),
            size = Size(towerW, towerH),
            cornerRadius = CornerRadius(20f, 20f)
        )
        drawRoundRect(
            color = Color(0xFFE0F7FA),
            topLeft = Offset(leftX + 4f, topY + 8f),
            size = Size(towerW - 8f, towerH - 16f),
            cornerRadius = CornerRadius(16f, 16f)
        )

        for (i in 0 until shelfCount) {
            val shelfY = topY + i * shelfSpacing + shelfSpacing * 0.65f
            val isSelected = (i == selectedShelfIndex)

            drawSingleShelfStructure(
                cx = leftX + towerW * 0.5f,
                shelfY = shelfY,
                shelfW = towerW * 0.90f,
                shelfH = shelfSpacing * 0.35f,
                isSelected = isSelected,
                pulse = if (isSelected) shelfPulseProgress else 0f
            )

            val itemCenterX = leftX + towerW * 0.5f
            val itemCenterY = shelfY - shelfSpacing * 0.18f

            when (i) {
                0 -> drawShelf1Dresses(itemCenterX, itemCenterY, towerW * 0.85f, shelfSpacing * 0.65f)
                1 -> drawShelf2HairSalon(itemCenterX, itemCenterY, towerW * 0.75f)
                2 -> drawShelf3Perfumes(itemCenterX, itemCenterY, towerW * 0.80f)
                3 -> drawShelf4Makeup(itemCenterX, itemCenterY, towerW * 0.75f)
                4 -> drawShelf5Lipsticks(itemCenterX, itemCenterY, towerW * 0.70f)
                5 -> drawShelf6JewelryChest(itemCenterX, itemCenterY, towerW * 0.80f)
            }
        }
    }

    private fun DrawScope.drawSingleShelfStructure(
        cx: Float,
        shelfY: Float,
        shelfW: Float,
        shelfH: Float,
        isSelected: Boolean,
        pulse: Float
    ) {
        val halfW = shelfW * 0.5f

        // "CLOSET ROOM" Carved Wooden Plaque
        val plaqueW = shelfW * 0.75f
        val plaqueH = shelfH * 0.70f
        val plaqueY = shelfY - shelfH * 1.20f
        drawRoundRect(
            color = Color(0xFFD7CCC8),
            topLeft = Offset(cx - plaqueW * 0.5f, plaqueY),
            size = Size(plaqueW, plaqueH),
            cornerRadius = CornerRadius(6f, 6f)
        )
        drawRoundRect(
            color = Color(0xFFBCAAA4),
            topLeft = Offset(cx - plaqueW * 0.5f, plaqueY),
            size = Size(plaqueW, plaqueH),
            cornerRadius = CornerRadius(6f, 6f),
            style = Stroke(width = 2f)
        )
        // Embossed "CLOSET ROOM" lettering on wooden plaque
        val plaquePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#8D6E63")
            textSize = plaqueH * 0.40f
            isFakeBoldText = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
        drawContext.canvas.nativeCanvas.drawText("CLOSET ROOM", cx, plaqueY + plaqueH * 0.68f, plaquePaint)

        // Shelf Floor
        val floorPath = Path().apply {
            moveTo(cx - halfW * 0.85f, shelfY - shelfH * 0.5f)
            lineTo(cx + halfW * 0.85f, shelfY - shelfH * 0.5f)
            lineTo(cx + halfW, shelfY)
            lineTo(cx - halfW, shelfY)
            close()
        }
        drawPath(
            floorPath,
            brush = Brush.verticalGradient(
                colors = listOf(PeachWood, PeachWoodDark),
                startY = shelfY - shelfH * 0.5f,
                endY = shelfY
            )
        )

        // 3D Curved Front Lip: Glossy Teal Rim
        val rimRect = Rect(cx - halfW, shelfY - shelfH * 0.25f, cx + halfW, shelfY + shelfH * 0.45f)
        drawOval(
            brush = Brush.verticalGradient(
                colors = listOf(TealRim, TealDark),
                startY = rimRect.top,
                endY = rimRect.bottom
            ),
            topLeft = rimRect.topLeft,
            size = rimRect.size
        )

        // White Specular Highlight on rim
        drawArc(
            color = WhitePure.copy(alpha = 0.85f),
            startAngle = 190f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(rimRect.left + halfW * 0.15f, rimRect.top + 2f),
            size = Size(shelfW * 0.70f, rimRect.height * 0.5f),
            style = Stroke(width = 3.5f, cap = StrokeCap.Round)
        )

        if (isSelected) {
            val glowAlpha = 0.5f + (sin(pulse * 2f * PI.toFloat()) * 0.35f)
            drawOval(
                color = GoldStar.copy(alpha = glowAlpha),
                topLeft = Offset(rimRect.left - 6f, rimRect.top - 6f),
                size = Size(rimRect.width + 12f, rimRect.height + 12f),
                style = Stroke(width = 4f)
            )
        }
    }

    private fun DrawScope.drawShelf1Dresses(cx: Float, cy: Float, rackW: Float, rackH: Float) {
        val halfW = rackW * 0.5f
        val rackTopY = cy - rackH * 0.45f

        val barColor = Color(0xFF42A5F5)
        drawLine(barColor, Offset(cx - halfW * 0.85f, rackTopY), Offset(cx + halfW * 0.85f, rackTopY), strokeWidth = 3.5f)
        drawLine(barColor, Offset(cx - halfW * 0.80f, rackTopY), Offset(cx - halfW * 0.80f, cy + rackH * 0.45f), strokeWidth = 3f)
        drawLine(barColor, Offset(cx + halfW * 0.80f, rackTopY), Offset(cx + halfW * 0.80f, cy + rackH * 0.45f), strokeWidth = 3f)

        drawMiniHangerDress(
            hangerX = cx - halfW * 0.50f,
            hangerY = rackTopY,
            dressH = rackH * 0.70f,
            topCol = Color(0xFFFF4081),
            skirtCol = Color(0xFFD81B60),
            hasRuffles = true
        )
        drawMiniHangerDress(
            hangerX = cx,
            hangerY = rackTopY,
            dressH = rackH * 0.75f,
            topCol = ScallopCyan,
            skirtCol = Color(0xFFFF80AB),
            hasRuffles = false
        )
        drawMiniHangerDress(
            hangerX = cx + halfW * 0.50f,
            hangerY = rackTopY,
            dressH = rackH * 0.78f,
            topCol = Color(0xFF7C4DFF),
            skirtCol = Color(0xFF2979FF),
            hasRuffles = false
        )
    }

    private fun DrawScope.drawMiniHangerDress(
        hangerX: Float,
        hangerY: Float,
        dressH: Float,
        topCol: Color,
        skirtCol: Color,
        hasRuffles: Boolean
    ) {
        drawArc(
            color = GoldStar,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(hangerX - 5f, hangerY - 8f),
            size = Size(10f, 8f),
            style = Stroke(width = 2f)
        )
        drawLine(GoldStar, Offset(hangerX - 12f, hangerY + 3f), Offset(hangerX + 12f, hangerY + 3f), strokeWidth = 2f)

        val waistY = hangerY + dressH * 0.35f
        val bodice = Path().apply {
            moveTo(hangerX - 8f, hangerY + 4f)
            lineTo(hangerX + 8f, hangerY + 4f)
            lineTo(hangerX + 6f, waistY)
            lineTo(hangerX - 6f, waistY)
            close()
        }
        drawPath(bodice, topCol)

        val bottomY = hangerY + dressH
        val skirtW = 16f
        val skirt = Path().apply {
            moveTo(hangerX - 6f, waistY)
            lineTo(hangerX + 6f, waistY)
            lineTo(hangerX + skirtW, bottomY)
            lineTo(hangerX - skirtW, bottomY)
            close()
        }
        drawPath(skirt, skirtCol)

        if (hasRuffles) {
            drawCircle(WhitePure, radius = 2.5f, center = Offset(hangerX - 10f, bottomY - 2f))
            drawCircle(WhitePure, radius = 2.5f, center = Offset(hangerX, bottomY - 2f))
            drawCircle(WhitePure, radius = 2.5f, center = Offset(hangerX + 10f, bottomY - 2f))
        }
    }

    private fun DrawScope.drawShelf2HairSalon(cx: Float, cy: Float, shelfW: Float) {
        drawRoundRect(
            color = Color(0xFFCE93D8),
            topLeft = Offset(cx - 18f, cy - 14f),
            size = Size(14f, 24f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawCircle(GoldStar, radius = 4f, center = Offset(cx - 11f, cy - 18f))

        val comb = Path().apply {
            moveTo(cx + 4f, cy - 8f)
            cubicTo(cx + 18f, cy - 18f, cx + 24f, cy - 2f, cx + 18f, cy + 10f)
            close()
        }
        drawPath(comb, Brush.linearGradient(listOf(Color(0xFF00E676), Color(0xFFFF4081))))
    }

    private fun DrawScope.drawShelf3Perfumes(cx: Float, cy: Float, shelfW: Float) {
        val boxW = shelfW * 0.75f
        val boxH = 26f
        drawRoundRect(
            color = DeepPurple,
            topLeft = Offset(cx - boxW * 0.5f, cy - 6f),
            size = Size(boxW, boxH),
            cornerRadius = CornerRadius(6f, 6f)
        )

        val bottleCols = listOf(ScallopCyan, Color(0xFFFF4081), Color(0xFFBA68C8))
        val offsets = listOf(-boxW * 0.28f, 0f, boxW * 0.28f)

        offsets.forEachIndexed { idx, offX ->
            val bx = cx + offX
            drawRoundRect(
                color = bottleCols[idx].copy(alpha = 0.85f),
                topLeft = Offset(bx - 7f, cy - 18f),
                size = Size(14f, 18f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawCircle(GoldStar, radius = 3.5f, center = Offset(bx, cy - 22f))
            if (idx == 1) {
                drawOval(Color(0xFFE91E63), topLeft = Offset(bx + 4f, cy - 26f), size = Size(9f, 6f))
            }
        }
    }

    private fun DrawScope.drawShelf4Makeup(cx: Float, cy: Float, shelfW: Float) {
        val palW = shelfW * 0.70f
        drawRoundRect(
            color = Color(0xFF263238),
            topLeft = Offset(cx - palW * 0.5f, cy - 10f),
            size = Size(palW, 18f),
            cornerRadius = CornerRadius(5f, 5f)
        )
        val pans = listOf(ScallopCyan, Color(0xFFFF4081), Color(0xFF7C4DFF), GoldStar)
        val panW = palW * 0.18f
        pans.forEachIndexed { i, col ->
            val px = cx - palW * 0.38f + i * (panW + 4f)
            drawCircle(col, radius = panW * 0.45f, center = Offset(px, cy - 1f))
        }
    }

    private fun DrawScope.drawShelf5Lipsticks(cx: Float, cy: Float, shelfW: Float) {
        val spacing = shelfW * 0.30f
        drawSingleLipstick(cx = cx - spacing * 0.5f, baseY = cy + 6f, bulletCol = Color(0xFFFF1744))
        drawSingleLipstick(cx = cx + spacing * 0.5f, baseY = cy + 6f, bulletCol = Color(0xFF7C4DFF))
    }

    private fun DrawScope.drawSingleLipstick(cx: Float, baseY: Float, bulletCol: Color) {
        val baseW = 14f
        val baseH = 22f
        val baseYTop = baseY - baseH

        val stripes = listOf(Color(0xFFFF1744), Color(0xFFFFD600), Color(0xFF00E676), Color(0xFF00B0FF))
        val stripeH = baseH / stripes.size
        stripes.forEachIndexed { i, col ->
            drawRoundRect(
                color = col,
                topLeft = Offset(cx - baseW * 0.5f, baseYTop + i * stripeH),
                size = Size(baseW, stripeH + 0.5f),
                cornerRadius = if (i == stripes.lastIndex) CornerRadius(3f, 3f) else CornerRadius.Zero
            )
        }
        drawRoundRect(
            color = Color(0xFF263238).copy(alpha = 0.3f),
            topLeft = Offset(cx - baseW * 0.5f, baseYTop),
            size = Size(baseW, baseH),
            cornerRadius = CornerRadius(3f, 3f),
            style = Stroke(width = 1.5f)
        )
        drawRect(GoldStar, topLeft = Offset(cx - baseW * 0.4f, baseYTop - 5f), size = Size(baseW * 0.8f, 5f))

        val bulletH = 14f
        val bullet = Path().apply {
            moveTo(cx - baseW * 0.35f, baseYTop - 5f)
            lineTo(cx - baseW * 0.35f, baseYTop - 5f - bulletH * 0.6f)
            lineTo(cx + baseW * 0.35f, baseYTop - 5f - bulletH)
            lineTo(cx + baseW * 0.35f, baseYTop - 5f)
            close()
        }
        drawPath(bullet, bulletCol)
        drawLine(WhitePure.copy(alpha = 0.7f), Offset(cx - 2f, baseYTop - 8f), Offset(cx - 2f, baseYTop - 14f), strokeWidth = 2f)
    }

    private fun DrawScope.drawShelf6JewelryChest(cx: Float, cy: Float, shelfW: Float) {
        val chestW = shelfW * 0.75f
        val chestH = 24f

        drawRoundRect(
            color = ScallopCyan,
            topLeft = Offset(cx - chestW * 0.5f, cy - 4f),
            size = Size(chestW, chestH),
            cornerRadius = CornerRadius(6f, 6f)
        )
        drawRoundRect(
            color = GoldStar,
            topLeft = Offset(cx - chestW * 0.5f, cy - 4f),
            size = Size(chestW, chestH),
            cornerRadius = CornerRadius(6f, 6f),
            style = Stroke(width = 2.5f)
        )

        for (i in -4..4) {
            val px = cx + i * 5.5f
            val py = cy - 6f + (if (i % 2 == 0) -2f else 3f)
            drawCircle(WhitePure, radius = 3.5f, center = Offset(px, py))
            drawCircle(Color(0xFFE0E0E0), radius = 3.5f, center = Offset(px, py), style = Stroke(width = 0.8f))
        }

        val tiara = Path().apply {
            moveTo(cx - 10f, cy - 8f)
            lineTo(cx - 5f, cy - 18f)
            lineTo(cx, cy - 12f)
            lineTo(cx + 5f, cy - 18f)
            lineTo(cx + 10f, cy - 8f)
            close()
        }
        drawPath(tiara, GoldStar)
        drawCircle(Color(0xFFFF4081), radius = 2f, center = Offset(cx - 5f, cy - 18f))
        drawCircle(Color(0xFF00E5FF), radius = 2.5f, center = Offset(cx, cy - 12f))
        drawCircle(Color(0xFFFF4081), radius = 2f, center = Offset(cx + 5f, cy - 18f))
    }

    // =========================================================================
    // 3. MAIN CHARACTER (3D PRINCESS BEAUTY MODEL)
    // =========================================================================

    fun DrawScope.drawPrincess3D(
        cx: Float,
        headY: Float,
        headR: Float,
        characterState: CharacterState,
        isBlinking: Boolean = false,
        swayAngle: Float = 0f
    ) {
        val neckY = headY + headR * 0.88f
        val waistY = neckY + headR * 1.35f
        val skirtBottomY = waistY + headR * 1.55f

        val skinColor = Color(0xFFFFDFC4)
        val skinShadow = Color(0xFFF5C2A0)

        // 0. Back Wings (drawn behind princess)
        characterState.wingsId?.let { wingsId ->
            CharacterVectorRenderer.run {
                drawWingsLayer(wingsId, cx, neckY + headR * 0.5f, headR * 1.25f)
            }
        }

        // 1. Back Hair (Flowing locks responding to hairStyleId and hairColorId)
        drawPrincessBackHair(
            cx = cx,
            headY = headY,
            headR = headR,
            bodyBottomY = skirtBottomY,
            sway = swayAngle,
            hairStyleId = characterState.hairStyleId,
            hairColorId = characterState.hairColorId
        )

        // 2. Arms & Shoulders
        drawPrincessShouldersAndArms(
            cx = cx,
            neckY = neckY,
            waistY = waistY,
            headR = headR,
            skinColor = skinColor,
            skinShadow = skinShadow
        )

        // 3. Bodice (Sparkly top responding to outfitId)
        drawPrincessBodice(
            cx = cx,
            neckY = neckY,
            waistY = waistY,
            headR = headR,
            outfitId = characterState.outfitId
        )

        // 4. Skirt (Flared skater / ball skirt responding to outfitId)
        drawPrincessSkaterSkirt(
            cx = cx,
            waistY = waistY,
            skirtBottomY = skirtBottomY,
            headR = headR,
            outfitId = characterState.outfitId
        )

        // 5. Neck & Face
        drawPrincessNeckAndFace(
            cx = cx,
            headY = headY,
            neckY = neckY,
            headR = headR,
            skinColor = skinColor,
            skinShadow = skinShadow
        )

        // 6. Facial Features & Makeup (Responding to eyeshadow, lipstick, blush)
        drawPrincessFacialFeatures(
            cx = cx,
            headY = headY,
            headR = headR,
            isBlinking = isBlinking,
            eyeshadowId = characterState.makeup.eyeshadow,
            lipstickId = characterState.makeup.lipstick,
            blushId = characterState.makeup.blush
        )

        // 7. Jewelry: Drop earrings or necklace responding to jewelryId
        drawPrincessJewelry(
            cx = cx,
            headY = headY,
            neckY = neckY,
            headR = headR,
            jewelryId = characterState.jewelryId
        )

        // 8. Front Hair & Bangs (Responding to hairStyleId and hairColorId)
        drawPrincessFrontHairAndBun(
            cx = cx,
            headY = headY,
            headR = headR,
            sway = swayAngle,
            hairStyleId = characterState.hairStyleId,
            hairColorId = characterState.hairColorId
        )

        // 9. Horn & Tiara (Anchored snugly to forehead hairline)
        characterState.hornId?.let { hornId ->
            CharacterVectorRenderer.run {
                drawHornLayer(hornId, cx, headY - headR * 0.50f, headR * 1.15f)
            }
        }
        characterState.tiaraId?.let { tiaraId ->
            CharacterVectorRenderer.run {
                drawTiaraLayer(tiaraId, cx, headY - headR * 0.58f, headR * 1.25f)
            }
        }

        // 10. Stickers (Anchored to body slots: cheeks, bodice, skirt)
        CharacterVectorRenderer.run {
            drawStickersLayer(characterState.stickers, cx, headY, skirtBottomY, headR)
        }
    }

    private fun DrawScope.drawPrincessBackHair(
        cx: Float,
        headY: Float,
        headR: Float,
        bodyBottomY: Float,
        sway: Float,
        hairStyleId: String? = null,
        hairColorId: String? = null
    ) {
        val hairGradient = if (hairColorId != null) {
            CandyPalette.hairBrush(hairColorId, Offset(cx - headR, headY), Offset(cx + headR, bodyBottomY))
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF7C4DFF), // Purple
                    Color(0xFF2979FF), // Deep Blue
                    Color(0xFF00E5FF)  // Turquoise
                ),
                startY = headY,
                endY = bodyBottomY + headR * 0.4f
            )
        }

        when {
            hairStyleId?.contains("buns_twin") == true -> {
                // Soft flowing side tendrils and back hair behind shoulders
                for (side in listOf(-1f, 1f)) {
                    val lock = Path().apply {
                        moveTo(cx + side * headR * 0.45f, headY)
                        cubicTo(
                            cx + side * headR * 1.25f, headY + headR * 0.6f,
                            cx + side * headR * 1.35f, bodyBottomY - headR * 0.2f,
                            cx + side * headR * 0.95f, bodyBottomY + headR * 0.15f
                        )
                        cubicTo(
                            cx + side * headR * 0.75f, bodyBottomY + headR * 0.05f,
                            cx + side * headR * 0.65f, headY + headR * 1.0f,
                            cx + side * headR * 0.35f, headY + headR * 0.7f
                        )
                        close()
                    }
                    drawPath(lock, hairGradient)
                }
            }
            hairStyleId?.contains("ponytail_high") == true -> {
                // High ponytail swept elegantly
                val pony = Path().apply {
                    moveTo(cx, headY - headR * 0.95f)
                    cubicTo(
                        cx + headR * 1.85f, headY - headR * 1.15f,
                        cx + headR * 1.95f, bodyBottomY * 0.85f,
                        cx + headR * 1.25f, bodyBottomY + headR * 0.15f
                    )
                    cubicTo(
                        cx + headR * 0.95f, bodyBottomY,
                        cx + headR * 1.25f, headY + headR * 0.3f,
                        cx + headR * 0.25f, headY - headR * 0.4f
                    )
                    close()
                }
                drawPath(pony, hairGradient)
                // Cute scrunchie at ponytail base
                drawCircle(CandyPink, radius = headR * 0.22f, center = Offset(cx + headR * 0.25f, headY - headR * 0.85f))
            }
            hairStyleId?.contains("straight") == true -> {
                // Long sleek silky straight hair cascading down past waist
                for (side in listOf(-1f, 1f)) {
                    val straightLock = Path().apply {
                        moveTo(cx + side * headR * 0.45f, headY)
                        lineTo(cx + side * headR * 1.25f, headY + headR * 0.5f)
                        lineTo(cx + side * headR * 1.25f, bodyBottomY + headR * 0.45f)
                        lineTo(cx + side * headR * 0.45f, bodyBottomY + headR * 0.45f)
                        lineTo(cx + side * headR * 0.25f, headY + headR * 0.8f)
                        close()
                    }
                    drawPath(straightLock, hairGradient)
                    // Sleek sheen line
                    drawLine(
                        color = WhitePure.copy(alpha = 0.45f),
                        start = Offset(cx + side * headR * 0.85f, headY + headR * 0.2f),
                        end = Offset(cx + side * headR * 0.85f, bodyBottomY + headR * 0.3f),
                        strokeWidth = 3f
                    )
                }
            }
            hairStyleId?.contains("waves") == true || hairStyleId?.contains("curls") == true -> {
                // Voluminous pop waves
                for (side in listOf(-1f, 1f)) {
                    withTransform({
                        rotate(degrees = sway * side, pivot = Offset(cx + side * headR * 0.65f, headY))
                    }) {
                        val waveLock = Path().apply {
                            moveTo(cx + side * headR * 0.45f, headY)
                            cubicTo(
                                cx + side * headR * 1.75f, headY + headR * 0.5f,
                                cx + side * headR * 1.35f, headY + headR * 1.2f,
                                cx + side * headR * 1.85f, headY + headR * 1.8f
                            )
                            cubicTo(
                                cx + side * headR * 1.65f, bodyBottomY + headR * 0.3f,
                                cx + side * headR * 0.85f, bodyBottomY + headR * 0.2f,
                                cx + side * headR * 0.45f, headY + headR * 0.8f
                            )
                            close()
                        }
                        drawPath(waveLock, hairGradient)
                    }
                }
            }
            hairStyleId?.contains("braid") == true -> {
                // Elegant braid draped over shoulder
                val braidX = cx - headR * 0.75f
                for (b in 0..6) {
                    val by = headY + headR * 0.2f + b * (headR * 0.26f)
                    drawCircle(hairGradient, radius = headR * 0.22f, center = Offset(braidX, by))
                    drawCircle(WhitePure.copy(alpha = 0.35f), radius = headR * 0.12f, center = Offset(braidX - headR * 0.05f, by - headR * 0.05f))
                }
                drawCircle(CandyPink, radius = headR * 0.15f, center = Offset(braidX, headY + headR * 2.0f))
            }
            else -> {
                // Default signature locks from reference image
                for (side in listOf(-1f, 1f)) {
                    withTransform({
                        rotate(degrees = sway * side, pivot = Offset(cx + side * headR * 0.65f, headY))
                    }) {
                        val lock = Path().apply {
                            moveTo(cx + side * headR * 0.45f, headY)
                            cubicTo(
                                cx + side * headR * 1.55f, headY + headR * 0.6f,
                                cx + side * headR * 1.65f, bodyBottomY - headR * 0.2f,
                                cx + side * headR * 1.25f, bodyBottomY + headR * 0.25f
                            )
                            cubicTo(
                                cx + side * headR * 0.95f, bodyBottomY + headR * 0.15f,
                                cx + side * headR * 0.70f, headY + headR * 1.2f,
                                cx + side * headR * 0.35f, headY + headR * 0.7f
                            )
                            close()
                        }
                        drawPath(lock, hairGradient)
                    }
                }
            }
        }
    }

    private fun DrawScope.drawPrincessShouldersAndArms(
        cx: Float,
        neckY: Float,
        waistY: Float,
        headR: Float,
        skinColor: Color,
        skinShadow: Color
    ) {
        val shoulderW = headR * 1.28f
        val elbowY = neckY + headR * 0.95f
        val handY = waistY + headR * 0.70f

        // Left arm (smooth curved 3D arm)
        val leftArm = Path().apply {
            moveTo(cx - shoulderW * 0.88f, neckY + headR * 0.18f)
            cubicTo(
                cx - shoulderW * 1.15f, neckY + headR * 0.55f,
                cx - shoulderW * 1.25f, elbowY,
                cx - shoulderW * 1.02f, handY
            )
            cubicTo(
                cx - shoulderW * 0.92f, handY + 14f,
                cx - shoulderW * 0.72f, handY + 10f,
                cx - shoulderW * 0.78f, handY - 8f
            )
            cubicTo(
                cx - shoulderW * 0.92f, elbowY,
                cx - shoulderW * 0.82f, neckY + headR * 0.65f,
                cx - shoulderW * 0.62f, neckY + headR * 0.35f
            )
            close()
        }
        drawPath(
            leftArm,
            Brush.linearGradient(
                colors = listOf(skinColor, skinShadow),
                start = Offset(cx - shoulderW, neckY),
                end = Offset(cx - shoulderW, handY)
            )
        )

        // Right arm (smooth curved 3D arm)
        val rightArm = Path().apply {
            moveTo(cx + shoulderW * 0.88f, neckY + headR * 0.18f)
            cubicTo(
                cx + shoulderW * 1.15f, neckY + headR * 0.55f,
                cx + shoulderW * 1.25f, elbowY,
                cx + shoulderW * 1.02f, handY
            )
            cubicTo(
                cx + shoulderW * 0.92f, handY + 14f,
                cx + shoulderW * 0.72f, handY + 10f,
                cx + shoulderW * 0.78f, handY - 8f
            )
            cubicTo(
                cx + shoulderW * 0.92f, elbowY,
                cx + shoulderW * 0.82f, neckY + headR * 0.65f,
                cx + shoulderW * 0.62f, neckY + headR * 0.35f
            )
            close()
        }
        drawPath(
            rightArm,
            Brush.linearGradient(
                colors = listOf(skinColor, skinShadow),
                start = Offset(cx + shoulderW, neckY),
                end = Offset(cx + shoulderW, handY)
            )
        )

        // Torso base skin
        val torso = Path().apply {
            moveTo(cx - shoulderW * 0.85f, neckY + headR * 0.1f)
            lineTo(cx + shoulderW * 0.85f, neckY + headR * 0.1f)
            lineTo(cx + headR * 0.52f, waistY)
            lineTo(cx - headR * 0.52f, waistY)
            close()
        }
        drawPath(torso, skinColor)

        // Sheer White Tulle Flutter Sleeve on Right Shoulder (viewer's right)
        val tulleSleeve = Path().apply {
            moveTo(cx + shoulderW * 0.55f, neckY + headR * 0.15f)
            cubicTo(
                cx + shoulderW * 1.15f, neckY - headR * 0.12f,
                cx + shoulderW * 1.40f, neckY + headR * 0.38f,
                cx + shoulderW * 1.05f, neckY + headR * 0.82f
            )
            cubicTo(
                cx + shoulderW * 0.85f, neckY + headR * 0.68f,
                cx + shoulderW * 0.75f, neckY + headR * 0.42f,
                cx + shoulderW * 0.55f, neckY + headR * 0.15f
            )
            close()
        }
        drawPath(tulleSleeve, Color.White.copy(alpha = 0.55f))
        drawPath(tulleSleeve, Color.White.copy(alpha = 0.85f), style = Stroke(width = 1.8f))
    }

    private fun DrawScope.drawPrincessBodice(
        cx: Float,
        neckY: Float,
        waistY: Float,
        headR: Float,
        outfitId: String? = null
    ) {
        val shoulderW = headR * 1.28f
        val bodiceTopY = neckY + headR * 0.16f
        val (topColor, skirtColor) = if (outfitId != null) {
            CandyPalette.outfitColors(outfitId)
        } else {
            WhitePure to MagentaHeart
        }

        // Bodice that covers bust AND shoulders with cap sleeves
        val bodice = Path().apply {
            moveTo(cx - shoulderW * 0.85f, neckY + headR * 0.20f)
            // Left cap sleeve / shoulder arch
            cubicTo(
                cx - shoulderW * 0.70f, neckY + headR * 0.06f,
                cx - headR * 0.40f, neckY + headR * 0.08f,
                cx - headR * 0.22f, neckY + headR * 0.16f
            )
            // Sweetheart neckline dip in center
            quadraticBezierTo(cx, neckY + headR * 0.26f, cx + headR * 0.22f, neckY + headR * 0.16f)
            // Right shoulder arch
            cubicTo(
                cx + headR * 0.40f, neckY + headR * 0.08f,
                cx + shoulderW * 0.70f, neckY + headR * 0.06f,
                cx + shoulderW * 0.85f, neckY + headR * 0.20f
            )
            // Down right side along ribcage to waist
            cubicTo(
                cx + shoulderW * 0.72f, neckY + headR * 0.55f,
                cx + headR * 0.58f, waistY - headR * 0.12f,
                cx + headR * 0.55f, waistY
            )
            lineTo(cx - headR * 0.55f, waistY)
            // Up left side along ribcage to shoulder
            cubicTo(
                cx - headR * 0.58f, waistY - headR * 0.12f,
                cx - shoulderW * 0.72f, neckY + headR * 0.55f,
                cx - shoulderW * 0.85f, neckY + headR * 0.20f
            )
            close()
        }
        drawPath(bodice, topColor)
        drawPath(bodice, Color(0xFFCFD8DC).copy(alpha = 0.45f), style = Stroke(width = 2.5f))

        // Delicate sweetheart lace piping along neckline
        val lacePiping = Path().apply {
            moveTo(cx - shoulderW * 0.85f, neckY + headR * 0.20f)
            cubicTo(
                cx - shoulderW * 0.70f, neckY + headR * 0.06f,
                cx - headR * 0.40f, neckY + headR * 0.08f,
                cx - headR * 0.22f, neckY + headR * 0.16f
            )
            quadraticBezierTo(cx, neckY + headR * 0.26f, cx + headR * 0.22f, neckY + headR * 0.16f)
            cubicTo(
                cx + headR * 0.40f, neckY + headR * 0.08f,
                cx + shoulderW * 0.70f, neckY + headR * 0.06f,
                cx + shoulderW * 0.85f, neckY + headR * 0.20f
            )
        }
        drawPath(lacePiping, color = Color.White.copy(alpha = 0.85f), style = Stroke(width = 2.5f, cap = StrokeCap.Round))

        // Asymmetrical Ribbon Strap across left shoulder
        val strap = Path().apply {
            moveTo(cx - shoulderW * 0.80f, neckY + headR * 0.06f)
            lineTo(cx - headR * 0.45f, neckY + headR * 0.06f)
            lineTo(cx + headR * 0.62f, bodiceTopY + headR * 0.38f)
            lineTo(cx + headR * 0.62f, bodiceTopY + headR * 0.70f)
            close()
        }
        drawPath(strap, skirtColor)

        // Cinched Ribbon Sash at waist
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(skirtColor, Color(0xFFFF80AB), skirtColor)),
            topLeft = Offset(cx - headR * 0.58f, waistY - headR * 0.05f),
            size = Size(headR * 1.16f, headR * 0.10f),
            cornerRadius = CornerRadius(6f, 6f)
        )
        drawCircle(GoldStar, radius = headR * 0.08f, center = Offset(cx, waistY))
        drawCircle(WhitePure, radius = headR * 0.03f, center = Offset(cx, waistY))

        // 3D Candy Heart Graphic on Center of Chest
        val heartCenterY = (bodiceTopY + waistY) * 0.48f
        val heartSize = headR * 0.45f
        drawCandyHeartEmblem(cx = cx, cy = heartCenterY, size = heartSize)
    }

    private fun DrawScope.drawCandyHeartEmblem(cx: Float, cy: Float, size: Float) {
        val heart = Path().apply {
            moveTo(cx, cy + size * 0.70f)
            cubicTo(cx - size * 1.15f, cy + size * 0.15f, cx - size * 1.15f, cy - size * 0.85f, cx, cy - size * 0.35f)
            cubicTo(cx + size * 1.15f, cy - size * 0.85f, cx + size * 1.15f, cy + size * 0.15f, cx, cy + size * 0.70f)
            close()
        }
        drawPath(heart, MagentaHeart)
        drawPath(heart, WhitePure.copy(alpha = 0.85f), style = Stroke(width = 2.5f))

        // Center golden sparkle star emblem
        CharacterVectorRenderer.run {
            drawStarShape(Offset(cx, cy), size * 0.42f, GoldStar)
        }
        drawCircle(WhitePure, radius = size * 0.12f, center = Offset(cx - size * 0.15f, cy - size * 0.25f))

        // Delicate glowing pastel ribbon wings around heart
        val ribbonLeft = Path().apply {
            moveTo(cx - size * 0.75f, cy)
            cubicTo(cx - size * 1.35f, cy - size * 0.25f, cx - size * 1.45f, cy + size * 0.35f, cx - size * 0.85f, cy + size * 0.45f)
            close()
        }
        val ribbonRight = Path().apply {
            moveTo(cx + size * 0.75f, cy)
            cubicTo(cx + size * 1.35f, cy - size * 0.25f, cx + size * 1.45f, cy + size * 0.35f, cx + size * 0.85f, cy + size * 0.45f)
            close()
        }
        drawPath(ribbonLeft, ScallopCyan.copy(alpha = 0.75f))
        drawPath(ribbonLeft, WhitePure.copy(alpha = 0.85f), style = Stroke(width = 1.5f))
        drawPath(ribbonRight, ScallopCyan.copy(alpha = 0.75f))
        drawPath(ribbonRight, WhitePure.copy(alpha = 0.85f), style = Stroke(width = 1.5f))
    }

    private fun DrawScope.drawPrincessSkaterSkirt(
        cx: Float,
        waistY: Float,
        skirtBottomY: Float,
        headR: Float,
        outfitId: String? = null
    ) {
        val skirtW = headR * 1.85f
        val (topColor, skirtColor) = if (outfitId != null) {
            CandyPalette.outfitColors(outfitId)
        } else {
            Color(0xFF8E24AA) to Color(0xFFC2185B)
        }

        val skirt = Path().apply {
            moveTo(cx - headR * 0.55f, waistY)
            lineTo(cx + headR * 0.55f, waistY)
            cubicTo(
                cx + skirtW * 0.7f, waistY + (skirtBottomY - waistY) * 0.4f,
                cx + skirtW * 1.1f, skirtBottomY - (skirtBottomY - waistY) * 0.2f,
                cx + skirtW, skirtBottomY
            )
            cubicTo(
                cx + skirtW * 0.5f, skirtBottomY + headR * 0.20f,
                cx - skirtW * 0.5f, skirtBottomY + headR * 0.20f,
                cx - skirtW, skirtBottomY
            )
            cubicTo(
                cx - skirtW * 1.1f, skirtBottomY - (skirtBottomY - waistY) * 0.2f,
                cx - skirtW * 0.7f, waistY + (skirtBottomY - waistY) * 0.4f,
                cx - headR * 0.55f, waistY
            )
            close()
        }
        drawPath(
            skirt,
            brush = Brush.verticalGradient(
                colors = listOf(topColor, skirtColor),
                startY = waistY,
                endY = skirtBottomY + headR * 0.20f
            )
        )

        // Scalloped Wavy Hem in Electric Cyan
        val hemWave = Path().apply {
            moveTo(cx - skirtW, skirtBottomY)
            val scallops = 7
            val step = (skirtW * 2f) / scallops
            for (s in 0 until scallops) {
                val x1 = cx - skirtW + s * step
                val x2 = x1 + step
                val mid = (x1 + x2) * 0.5f
                val dip = skirtBottomY + 10f * sin((s / scallops.toFloat()) * PI.toFloat())
                quadraticBezierTo(mid, dip + 7f, x2, dip)
            }
        }
        drawPath(hemWave, color = ScallopCyan, style = Stroke(width = 5f, cap = StrokeCap.Round))

        // Peppermint Swirl Candy Pinwheels on Skirt
        val candyPositions = listOf(
            Offset(cx - skirtW * 0.55f, waistY + (skirtBottomY - waistY) * 0.60f),
            Offset(cx - skirtW * 0.15f, waistY + (skirtBottomY - waistY) * 0.72f),
            Offset(cx + skirtW * 0.28f, waistY + (skirtBottomY - waistY) * 0.50f),
            Offset(cx + skirtW * 0.68f, waistY + (skirtBottomY - waistY) * 0.68f)
        )
        candyPositions.forEach { pos ->
            drawPeppermintSwirl(pos.x, pos.y, radius = headR * 0.16f)
        }

        // Cute Wrapped Candies with Bow Wings (cyan & pink)
        drawWrappedCandy(cx - skirtW * 0.75f, waistY + (skirtBottomY - waistY) * 0.75f, radius = headR * 0.10f, Color(0xFF00E5FF))
        drawWrappedCandy(cx + skirtW * 0.52f, waistY + (skirtBottomY - waistY) * 0.80f, radius = headR * 0.11f, Color(0xFFE040FB))
        drawWrappedCandy(cx + skirtW * 0.85f, waistY + (skirtBottomY - waistY) * 0.76f, radius = headR * 0.09f, Color(0xFFFF4081))

        val sprinkles = listOf(
            Offset(cx - skirtW * 0.35f, waistY + (skirtBottomY - waistY) * 0.35f) to ScallopCyan,
            Offset(cx + skirtW * 0.48f, waistY + (skirtBottomY - waistY) * 0.30f) to GoldStar,
            Offset(cx, waistY + (skirtBottomY - waistY) * 0.85f) to WhitePure,
            Offset(cx + skirtW * 0.08f, waistY + (skirtBottomY - waistY) * 0.42f) to Color(0xFFFF4081)
        )
        sprinkles.forEach { (sp, col) ->
            drawCircle(col, radius = 3.5f, center = sp)
        }
    }

    private fun DrawScope.drawWrappedCandy(cx: Float, cy: Float, radius: Float, bodyColor: Color) {
        val leftWing = Path().apply {
            moveTo(cx - radius * 0.5f, cy)
            lineTo(cx - radius * 1.5f, cy - radius * 0.65f)
            lineTo(cx - radius * 1.5f, cy + radius * 0.65f)
            close()
        }
        drawPath(leftWing, WhitePure)
        drawPath(leftWing, Color.LightGray, style = Stroke(width = 1.2f))

        val rightWing = Path().apply {
            moveTo(cx + radius * 0.5f, cy)
            lineTo(cx + radius * 1.5f, cy - radius * 0.65f)
            lineTo(cx + radius * 1.5f, cy + radius * 0.65f)
            close()
        }
        drawPath(rightWing, WhitePure)
        drawPath(rightWing, Color.LightGray, style = Stroke(width = 1.2f))

        drawCircle(bodyColor, radius = radius, center = Offset(cx, cy))
        drawCircle(WhitePure.copy(alpha = 0.75f), radius = radius * 0.4f, center = Offset(cx - radius * 0.28f, cy - radius * 0.28f))
    }

    private fun DrawScope.drawPeppermintSwirl(cx: Float, cy: Float, radius: Float) {
        drawCircle(WhitePure, radius = radius, center = Offset(cx, cy))
        drawCircle(Color(0xFFE91E63), radius = radius, center = Offset(cx, cy), style = Stroke(width = 1.5f))

        for (i in 0 until 6) {
            val angle = i * (PI.toFloat() / 3f)
            val blade = Path().apply {
                moveTo(cx, cy)
                cubicTo(
                    cx + radius * 0.5f * cos(angle + 0.3f), cy + radius * 0.5f * sin(angle + 0.3f),
                    cx + radius * 0.8f * cos(angle + 0.6f), cy + radius * 0.8f * sin(angle + 0.6f),
                    cx + radius * cos(angle + 0.4f), cy + radius * sin(angle + 0.4f)
                )
                lineTo(cx + radius * cos(angle), cy + radius * sin(angle))
                close()
            }
            drawPath(blade, Color(0xFFE91E63))
        }
    }

    private fun DrawScope.drawPrincessNeckAndFace(
        cx: Float,
        headY: Float,
        neckY: Float,
        headR: Float,
        skinColor: Color,
        skinShadow: Color
    ) {
        val neckW = headR * 0.28f
        val neck = Path().apply {
            moveTo(cx - neckW, headY + headR * 0.5f)
            lineTo(cx + neckW, headY + headR * 0.5f)
            lineTo(cx + neckW * 1.35f, neckY + headR * 0.15f)
            lineTo(cx - neckW * 1.35f, neckY + headR * 0.15f)
            close()
        }
        drawPath(neck, skinColor)
        drawCircle(skinShadow, radius = headR * 0.25f, center = Offset(cx, headY + headR * 0.68f))

        // Ears
        drawOval(skinColor, topLeft = Offset(cx - headR * 0.98f, headY - headR * 0.08f), size = Size(headR * 0.22f, headR * 0.38f))
        drawOval(skinColor, topLeft = Offset(cx + headR * 0.76f, headY - headR * 0.08f), size = Size(headR * 0.22f, headR * 0.38f))

        // Solid opaque full head & face base (guarantees zero background bleed-through)
        val fullHeadPath = Path().apply {
            moveTo(cx, headY - headR * 0.96f)
            cubicTo(
                cx + headR * 0.88f, headY - headR * 0.96f,
                cx + headR * 0.94f, headY - headR * 0.15f,
                cx + headR * 0.90f, headY + headR * 0.25f
            )
            cubicTo(
                cx + headR * 0.78f, headY + headR * 0.65f,
                cx + headR * 0.42f, headY + headR * 0.90f,
                cx, headY + headR * 0.95f
            )
            cubicTo(
                cx - headR * 0.42f, headY + headR * 0.90f,
                cx - headR * 0.78f, headY + headR * 0.65f,
                cx - headR * 0.90f, headY + headR * 0.25f
            )
            cubicTo(
                cx - headR * 0.94f, headY - headR * 0.15f,
                cx - headR * 0.88f, headY - headR * 0.96f,
                cx, headY - headR * 0.96f
            )
            close()
        }
        drawPath(
            fullHeadPath,
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFF3ED), skinColor, Color(0xFFF5C2A0)),
                center = Offset(cx, headY - headR * 0.1f),
                radius = headR * 1.15f
            )
        )
    }

    private fun DrawScope.drawPrincessFacialFeatures(
        cx: Float,
        headY: Float,
        headR: Float,
        isBlinking: Boolean,
        eyeshadowId: String?,
        lipstickId: String?,
        blushId: String?
    ) {
        val eyeSpacing = headR * 0.40f
        val eyeY = headY + headR * 0.06f
        val eyeR = headR * 0.22f
        val skinShadow = Color(0xFFF5C2A0)

        // Soft Shimmering Eyeshadow (Subtle, elegant pastel contour - no opaque blocks!)
        val shadowColor = if (eyeshadowId != null) {
            CandyPalette.eyeshadowColor(eyeshadowId).copy(alpha = 0.45f)
        } else {
            Color(0xFFFFB6C1).copy(alpha = 0.18f)
        }
        for (side in listOf(-1f, 1f)) {
            val eyeX = cx + side * eyeSpacing
            val lidMask = Path().apply {
                addArc(
                    oval = Rect(eyeX - eyeR * 1.05f, eyeY - eyeR * 1.15f, eyeX + eyeR * 1.05f, eyeY + eyeR * 0.05f),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 180f
                )
                close()
            }
            clipPath(lidMask, clipOp = ClipOp.Intersect) {
                drawOval(
                    brush = Brush.verticalGradient(
                        listOf(shadowColor, shadowColor.copy(alpha = 0.05f)),
                        startY = eyeY - eyeR * 1.15f,
                        endY = eyeY
                    ),
                    topLeft = Offset(eyeX - eyeR * 1.1f, eyeY - eyeR * 1.2f),
                    size = Size(eyeR * 2.2f, eyeR * 1.3f)
                )
            }
        }

        // Rosy Cheek Blush (Soft romantic glow)
        val blushCol = if (blushId != null) {
            CandyPalette.blushColor(blushId).copy(alpha = 0.48f)
        } else {
            Color(0xFFFF80AB).copy(alpha = 0.38f)
        }
        drawOval(blushCol, topLeft = Offset(cx - eyeSpacing - headR * 0.24f, eyeY + headR * 0.22f), size = Size(headR * 0.36f, headR * 0.18f))
        drawOval(blushCol, topLeft = Offset(cx + eyeSpacing - headR * 0.12f, eyeY + headR * 0.22f), size = Size(headR * 0.36f, headR * 0.18f))

        // Large Sparkling Anime Eyes
        for (side in listOf(-1f, 1f)) {
            val eyeX = cx + side * eyeSpacing
            if (isBlinking) {
                drawArc(
                    color = Color(0xFF1A1A24),
                    startAngle = 15f,
                    sweepAngle = 150f,
                    useCenter = false,
                    topLeft = Offset(eyeX - eyeR * 0.95f, eyeY - eyeR * 0.2f),
                    size = Size(eyeR * 1.9f, eyeR * 0.5f),
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
            } else {
                // Eye white (clean almond shape)
                drawOval(
                    color = WhitePure,
                    topLeft = Offset(eyeX - eyeR * 1.02f, eyeY - eyeR * 0.65f),
                    size = Size(eyeR * 2.04f, eyeR * 1.30f)
                )
                // Iris (gorgeous deep blue & cyan multi-tier gradient)
                val irisR = eyeR * 0.68f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFF1E88E5), Color(0xFF0D47A1)),
                        center = Offset(eyeX, eyeY + 2f),
                        radius = irisR
                    ),
                    radius = irisR,
                    center = Offset(eyeX, eyeY)
                )
                // Pupil
                drawCircle(Color(0xFF0A1128), radius = irisR * 0.45f, center = Offset(eyeX, eyeY))
                // Primary starlight glint
                drawCircle(WhitePure, radius = irisR * 0.32f, center = Offset(eyeX - irisR * 0.26f, eyeY - irisR * 0.26f))
                // Secondary twinkle reflection
                drawCircle(WhitePure, radius = irisR * 0.15f, center = Offset(eyeX + irisR * 0.24f, eyeY + irisR * 0.22f))

                // Delicate fluttering winged lashes
                val lashLine = Path().apply {
                    moveTo(eyeX - side * eyeR * 0.98f, eyeY - eyeR * 0.12f)
                    quadraticBezierTo(eyeX, eyeY - eyeR * 0.80f, eyeX + side * eyeR * 1.10f, eyeY - eyeR * 0.32f)
                    lineTo(eyeX + side * eyeR * 1.30f, eyeY - eyeR * 0.55f)
                }
                drawPath(lashLine, color = Color(0xFF1A1A24), style = Stroke(width = 3.5f, cap = StrokeCap.Round))

                // Extra flutter lash tip
                drawLine(
                    color = Color(0xFF1A1A24),
                    start = Offset(eyeX + side * eyeR * 1.02f, eyeY - eyeR * 0.30f),
                    end = Offset(eyeX + side * eyeR * 1.25f, eyeY - eyeR * 0.38f),
                    strokeWidth = 2.5f,
                    cap = StrokeCap.Round
                )

                // Double eyelid fold
                drawArc(
                    color = skinShadow.copy(alpha = 0.55f),
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(eyeX - eyeR * 0.85f, eyeY - eyeR * 0.95f),
                    size = Size(eyeR * 1.7f, eyeR * 0.5f),
                    style = Stroke(width = 1.8f, cap = StrokeCap.Round)
                )
            }

            // Natural arched soft-brown eyebrows
            val brow = Path().apply {
                moveTo(eyeX - side * eyeR * 0.88f, eyeY - eyeR * 1.10f)
                quadraticBezierTo(eyeX, eyeY - eyeR * 1.45f, eyeX + side * eyeR * 0.92f, eyeY - eyeR * 1.05f)
            }
            drawPath(brow, color = Color(0xFF4A3728), style = Stroke(width = 3f, cap = StrokeCap.Round))
        }

        // Dainty Anime Button Nose
        val noseY = headY + headR * 0.30f
        drawArc(
            color = skinShadow.copy(alpha = 0.65f),
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(cx - 4.5f, noseY),
            size = Size(9f, 5.5f),
            style = Stroke(width = 2.2f, cap = StrokeCap.Round)
        )

        // Sweet Glossy Pink Smiling Lips
        val mouthY = headY + headR * 0.52f
        val lipW = headR * 0.34f
        val lipH = headR * 0.20f
        val lipCol = if (lipstickId != null) CandyPalette.lipstickColor(lipstickId) else Color(0xFFF06292)

        val topLip = Path().apply {
            moveTo(cx - lipW * 0.5f, mouthY)
            cubicTo(cx - lipW * 0.2f, mouthY - lipH * 0.45f, cx - lipW * 0.05f, mouthY - lipH * 0.35f, cx, mouthY - lipH * 0.18f)
            cubicTo(cx + lipW * 0.05f, mouthY - lipH * 0.35f, cx + lipW * 0.2f, mouthY - lipH * 0.45f, cx + lipW * 0.5f, mouthY)
            close()
        }
        val botLip = Path().apply {
            moveTo(cx - lipW * 0.5f, mouthY)
            cubicTo(cx - lipW * 0.22f, mouthY + lipH * 0.65f, cx + lipW * 0.22f, mouthY + lipH * 0.65f, cx + lipW * 0.5f, mouthY)
            close()
        }
        drawPath(topLip, lipCol)
        drawPath(botLip, lipCol.copy(alpha = 0.9f))

        // Natural soft glossy shine on bottom lip (NO CYAN STRIPES!)
        drawArc(
            color = WhitePure.copy(alpha = 0.65f),
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(cx - lipW * 0.20f, mouthY + 1.5f),
            size = Size(lipW * 0.40f, lipH * 0.35f),
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )
    }

    private fun DrawScope.drawPrincessJewelry(
        cx: Float,
        headY: Float,
        neckY: Float,
        headR: Float,
        jewelryId: String? = null
    ) {
        if (jewelryId != null) {
            CharacterVectorRenderer.run {
                drawJewelryLayer(jewelryId, cx, headY, neckY, headR)
            }
            return
        }

        // Default sparkling star earrings and pearl choker necklace
        for (side in listOf(-1f, 1f)) {
            val earX = cx + side * headR * 0.86f
            val earY = headY + headR * 0.18f

            drawStar5(earX, earY, r = 5.5f, color = GoldStar)
            drawLine(GoldStar, Offset(earX, earY + 4f), Offset(earX, earY + 16f), strokeWidth = 1.8f)
            drawCircle(CandyPink, radius = 4.5f, center = Offset(earX, earY + 18f))
            drawCircle(WhitePure, radius = 2f, center = Offset(earX - 1f, earY + 17f))
        }

        val chokerY = neckY + headR * 0.10f
        for (i in -4..4) {
            val bx = cx + i * (headR * 0.08f)
            val by = chokerY + (i * i) * 0.4f
            drawCircle(color = Color(0xFFFFFDE7), radius = 3.8f, center = Offset(bx, by))
            drawCircle(color = Color(0xFFFF80AB), radius = 3.8f, center = Offset(bx, by), style = Stroke(width = 1f))
        }
        drawStar5(cx, chokerY + 8f, r = 6f, color = GoldStar)
    }

    private fun DrawScope.drawPrincessFrontHairAndBun(
        cx: Float,
        headY: Float,
        headR: Float,
        sway: Float,
        hairStyleId: String? = null,
        hairColorId: String? = null
    ) {
        val hairGradient = if (hairColorId != null) {
            CandyPalette.hairBrush(hairColorId, Offset(cx - headR, headY - headR * 1.2f), Offset(cx + headR, headY + headR))
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF7C4DFF), // Royal Purple
                    Color(0xFF2979FF), // Sapphire Blue
                    Color(0xFF00E5FF)  // Electric Cyan
                ),
                startY = headY - headR * 1.2f,
                endY = headY + headR
            )
        }

        when {
            hairStyleId?.contains("buns_twin") == true -> {
                // Two cute anime side buns with ribbon ties
                val bunR = headR * 0.46f
                for (side in listOf(-1f, 1f)) {
                    val bx = cx + side * headR * 0.88f
                    val by = headY - headR * 0.65f
                    drawCircle(hairGradient, radius = bunR, center = Offset(bx, by))
                    drawCircle(WhitePure.copy(alpha = 0.35f), radius = bunR * 0.45f, center = Offset(bx - side * 4f, by - 4f))
                    // Cute ribbon bows
                    drawCircle(CandyPink, radius = bunR * 0.32f, center = Offset(bx, by + bunR * 0.65f))
                    drawCircle(WhitePure, radius = bunR * 0.12f, center = Offset(bx, by + bunR * 0.65f))
                }
                // Soft center parted bangs
                val bangs = Path().apply {
                    moveTo(cx - headR * 0.82f, headY - headR * 0.22f)
                    cubicTo(cx - headR * 0.40f, headY - headR * 0.70f, cx + headR * 0.40f, headY - headR * 0.70f, cx + headR * 0.82f, headY - headR * 0.22f)
                    quadraticBezierTo(cx, headY - headR * 0.35f, cx - headR * 0.82f, headY - headR * 0.22f)
                    close()
                }
                drawPath(bangs, hairGradient)
            }
            hairStyleId?.contains("ponytail_high") == true -> {
                // Sleek high ponytail sweeping gracefully
                val fringe = Path().apply {
                    moveTo(cx - headR * 0.82f, headY - headR * 0.25f)
                    cubicTo(cx - headR * 0.2f, headY - headR * 0.80f, cx + headR * 0.5f, headY - headR * 0.80f, cx + headR * 0.85f, headY - headR * 0.15f)
                    quadraticBezierTo(cx + headR * 0.2f, headY - headR * 0.42f, cx - headR * 0.82f, headY - headR * 0.25f)
                    close()
                }
                drawPath(fringe, hairGradient)
                // Cute scrunchie
                drawCircle(CandyPink, radius = headR * 0.18f, center = Offset(cx + headR * 0.25f, headY - headR * 0.85f))
            }
            hairStyleId?.contains("straight") == true -> {
                // Sleek straight parted bangs
                val bangs = Path().apply {
                    moveTo(cx - headR * 0.85f, headY - headR * 0.22f)
                    lineTo(cx + headR * 0.85f, headY - headR * 0.22f)
                    lineTo(cx + headR * 0.85f, headY - headR * 0.70f)
                    lineTo(cx - headR * 0.85f, headY - headR * 0.70f)
                    close()
                }
                drawPath(bangs, hairGradient)
                for (b in -3..3) {
                    val fx = cx + b * headR * 0.20f
                    drawLine(
                        color = WhitePure.copy(alpha = 0.45f),
                        start = Offset(fx, headY - headR * 0.65f),
                        end = Offset(fx, headY - headR * 0.28f),
                        strokeWidth = 2f
                    )
                }
            }
            hairStyleId?.contains("waves") == true || hairStyleId?.contains("curls") == true -> {
                // Voluminous side-swept glamorous waves
                val waveBangs = Path().apply {
                    moveTo(cx - headR * 0.88f, headY - headR * 0.18f)
                    cubicTo(cx - headR * 0.35f, headY - headR * 0.85f, cx + headR * 0.55f, headY - headR * 0.85f, cx + headR * 0.88f, headY - headR * 0.05f)
                    quadraticBezierTo(cx + headR * 0.20f, headY - headR * 0.50f, cx - headR * 0.88f, headY - headR * 0.18f)
                    close()
                }
                drawPath(waveBangs, hairGradient)
            }
            hairStyleId?.contains("braid") == true -> {
                // Center-parted neat bangs with braided strand
                val braidBangs = Path().apply {
                    moveTo(cx - headR * 0.82f, headY - headR * 0.18f)
                    quadraticBezierTo(cx, headY - headR * 0.70f, cx + headR * 0.82f, headY - headR * 0.18f)
                    quadraticBezierTo(cx, headY - headR * 0.42f, cx - headR * 0.82f, headY - headR * 0.18f)
                    close()
                }
                drawPath(braidBangs, hairGradient)
            }
            else -> {
                // Default high glamorous bun with textured braided weave (NO UGLY RAINBOW UNIBROW!)
                val bunY = headY - headR * 0.98f
                val bunR = headR * 0.48f

                drawCircle(
                    brush = hairGradient,
                    radius = bunR,
                    center = Offset(cx, bunY)
                )
                // Hair weave sheen curves on the bun
                for (b in -2..2) {
                    val by = bunY + b * 6.5f
                    drawArc(
                        color = WhitePure.copy(alpha = 0.45f),
                        startAngle = 195f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = Offset(cx - bunR * 0.75f, by - 5f),
                        size = Size(bunR * 1.5f, 10f),
                        style = Stroke(width = 2.2f)
                    )
                }
                // Cute pastel candy hair tie ring at bun base
                drawRoundRect(
                    color = CandyPink,
                    topLeft = Offset(cx - bunR * 0.65f, bunY + bunR * 0.55f),
                    size = Size(bunR * 1.3f, 10f),
                    cornerRadius = CornerRadius(5f, 5f)
                )

                // Soft sweeping bangs that naturally frame forehead (NO harsh rainbow bar!)
                val frontBangs = Path().apply {
                    moveTo(cx - headR * 0.82f, headY - headR * 0.20f)
                    cubicTo(
                        cx - headR * 0.40f, headY - headR * 0.75f,
                        cx + headR * 0.40f, headY - headR * 0.75f,
                        cx + headR * 0.82f, headY - headR * 0.20f
                    )
                    quadraticBezierTo(cx, headY - headR * 0.38f, cx - headR * 0.82f, headY - headR * 0.20f)
                    close()
                }
                drawPath(frontBangs, hairGradient)

                // Face-Framing Gentle Ombre Locks
                for (side in listOf(-1f, 1f)) {
                    val frontLock = Path().apply {
                        moveTo(cx + side * headR * 0.78f, headY - headR * 0.20f)
                        cubicTo(
                            cx + side * headR * 1.05f, headY + headR * 0.45f,
                            cx + side * headR * 0.95f, headY + headR * 1.15f,
                            cx + side * headR * 0.65f, headY + headR * 1.35f
                        )
                        cubicTo(
                            cx + side * headR * 0.85f, headY + headR * 0.95f,
                            cx + side * headR * 0.85f, headY + headR * 0.35f,
                            cx + side * headR * 0.68f, headY
                        )
                        close()
                    }
                    drawPath(frontLock, brush = hairGradient)
                }
            }
        }
    }

    // =========================================================================
    // 4. TOP-RIGHT CIRCULAR RAINBOW SWIRL BACK BUTTON
    // =========================================================================

    fun DrawScope.drawRainbowBackButton(
        cx: Float,
        cy: Float,
        radius: Float,
        isPressed: Boolean = false
    ) {
        val effectiveR = if (isPressed) radius * 0.92f else radius

        val segments = 8
        val colors = listOf(
            Color(0xFFFF4081),
            Color(0xFFFFD600),
            Color(0xFF00E676),
            Color(0xFF00B0FF),
            Color(0xFF7C4DFF),
            Color(0xFFE040FB),
            Color(0xFFFF5252),
            Color(0xFFFFAB00)
        )
        val sweep = 360f / segments
        for (i in 0 until segments) {
            drawArc(
                color = colors[i % colors.size],
                startAngle = i * sweep,
                sweepAngle = sweep + 2f,
                useCenter = true,
                topLeft = Offset(cx - effectiveR, cy - effectiveR),
                size = Size(effectiveR * 2f, effectiveR * 2f)
            )
        }

        val innerR = effectiveR * 0.72f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF4A148C), Color(0xFF280659)),
                center = Offset(cx, cy),
                radius = innerR
            ),
            radius = innerR,
            center = Offset(cx, cy)
        )

        val arrowW = innerR * 0.85f
        val arrowH = innerR * 0.70f
        val arrow = Path().apply {
            moveTo(cx - arrowW * 0.40f, cy)
            lineTo(cx + arrowW * 0.05f, cy - arrowH * 0.45f)
            lineTo(cx + arrowW * 0.05f, cy - arrowH * 0.18f)
            lineTo(cx + arrowW * 0.45f, cy - arrowH * 0.18f)
            lineTo(cx + arrowW * 0.45f, cy + arrowH * 0.18f)
            lineTo(cx + arrowW * 0.05f, cy + arrowH * 0.18f)
            lineTo(cx + arrowW * 0.05f, cy + arrowH * 0.45f)
            close()
        }
        drawPath(arrow, WhitePure)
    }

    // =========================================================================
    // 5. BOTTOM "CHOOSE YOUR STYLE" BANNER
    // =========================================================================

    fun DrawScope.drawChooseYourStyleBanner(
        leftX: Float,
        topY: Float,
        bannerW: Float,
        bannerH: Float
    ) {
        val ribbon = Path().apply {
            moveTo(leftX, topY + 16f)
            quadraticBezierTo(leftX, topY, leftX + 16f, topY)
            lineTo(leftX + bannerW - 16f, topY)
            quadraticBezierTo(leftX + bannerW, topY, leftX + bannerW, topY + 16f)
            lineTo(leftX + bannerW, topY + bannerH)
            lineTo(leftX, topY + bannerH)
            close()
        }
        drawPath(
            ribbon,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFF1493), Color(0xFFC2185B)),
                startY = topY,
                endY = topY + bannerH
            )
        )
        drawLine(
            color = WhitePure.copy(alpha = 0.6f),
            start = Offset(leftX + 16f, topY + 1f),
            end = Offset(leftX + bannerW - 16f, topY + 1f),
            strokeWidth = 2.5f
        )
    }
}
