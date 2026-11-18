package com.rainbowunicorn.candysalon.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas as ComposeCanvas
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.rainbowunicorn.candysalon.ui.theme.CandyCream
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyOrange
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.CharacterType
import com.rainbowunicorn.core.models.Item
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object CandyPalette {
    val RainbowColors = listOf(
        Color(0xFFFF5996),
        Color(0xFFFFB74D),
        Color(0xFFFFE57F),
        Color(0xFF7CE8D5),
        Color(0xFF81D4FA),
        Color(0xFFC792EA)
    )

    fun parseColor(hex: String?, fallback: Color = CandyPink): Color {
        if (hex.isNullOrBlank()) return fallback
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (_: Exception) {
            fallback
        }
    }

    fun hairBrush(hairColorId: String?, start: Offset, end: Offset): Brush {
        return when {
            hairColorId == null -> Brush.linearGradient(listOf(CandyPurple, Color(0xFFB388FF)), start, end)
            hairColorId.contains("rainbow") -> Brush.linearGradient(RainbowColors, start, end)
            hairColorId.contains("pastel_pink") -> Brush.linearGradient(listOf(Color(0xFFFFB6C1), Color(0xFFFF80AB)), start, end)
            hairColorId.contains("mint_green") -> Brush.linearGradient(listOf(Color(0xFF98FF98), Color(0xFF69F0AE)), start, end)
            hairColorId.contains("galaxy_purple") -> Brush.linearGradient(listOf(Color(0xFF8A2BE2), Color(0xFF4A148C)), start, end)
            hairColorId.contains("lavender") -> Brush.linearGradient(listOf(Color(0xFFE6E6FA), Color(0xFFB39DDB)), start, end)
            hairColorId.contains("neon_lime") -> Brush.linearGradient(listOf(Color(0xFF76FF03), Color(0xFF00E676)), start, end)
            hairColorId.contains("plum") -> Brush.linearGradient(listOf(Color(0xFF8E4585), Color(0xFF6A1B9A)), start, end)
            hairColorId.contains("magenta") -> Brush.linearGradient(listOf(Color(0xFFFF007F), Color(0xFFE91E63)), start, end)
            else -> Brush.linearGradient(listOf(CandyPurple, CandyPink), start, end)
        }
    }

    fun eyeshadowColor(id: String?): Color = when {
        id == null -> Color.Transparent
        id.contains("berry_pink") -> Color(0xFFFF69B4)
        id.contains("turquoise") -> Color(0xFF40E0D0)
        id.contains("shimmer_blue") -> Color(0xFF87CEEB)
        id.contains("caramel") -> Color(0xFFFFC107)
        else -> CandyPink
    }

    fun lipstickColor(id: String?): Color = when {
        id == null -> Color.Transparent
        id.contains("bubblegum") -> Color(0xFFFF1493)
        id.contains("cherry") -> Color(0xFFDC143C)
        id.contains("peach") -> Color(0xFFFF7F50)
        id.contains("crimson") -> Color(0xFF990024)
        else -> CandyPink
    }

    fun blushColor(id: String?): Color = when {
        id == null -> Color.Transparent
        id.contains("rose_petal") -> Color(0xFFFF8FAB)
        id.contains("apricot") -> Color(0xFFFFAB91)
        id.contains("cotton") -> Color(0xFFFFB6C1)
        id.contains("sparkle_berry") -> Color(0xFFE75480)
        else -> CandyPastelPink
    }

    fun outfitColors(outfitId: String?): Pair<Color, Color> = when {
        outfitId == null -> Color.Transparent to Color.Transparent
        outfitId.contains("cotton_candy") -> Color(0xFFFFB6C1) to Color(0xFF81D4FA)
        outfitId.contains("pastry_chef") -> Color(0xFFFFFFFF) to Color(0xFFFF80AB)
        outfitId.contains("mermaid") -> Color(0xFF4DD0E1) to Color(0xFFCE93D8)
        outfitId.contains("ball_gown") -> Color(0xFFFF5996) to Color(0xFFFFC1E3)
        outfitId.contains("carnival") -> Color(0xFFAB47BC) to Color(0xFFFFD54F)
        outfitId.contains("crystal") -> Color(0xFFB3E5FC) to Color(0xFFE1F5FE)
        outfitId.contains("peppermint") -> Color(0xFFEF5350) to Color.White
        outfitId.contains("tutu") -> Color(0xFF8E24AA) to Color(0xFFF48FB1)
        outfitId.contains("cyber") -> Color(0xFF00E676) to Color(0xFF651FFF)
        outfitId.contains("parisian") -> Color(0xFFFFCC80) to Color(0xFFF8BBD0)
        outfitId.contains("chocolate") -> Color(0xFF6D4C41) to Color(0xFFFFD54F)
        outfitId.contains("dragon_armor") -> Color(0xFFEC407A) to Color(0xFF00BFA5)
        outfitId.contains("coronation") -> Color(0xFFFFD54F) to Color(0xFFFF5996)
        else -> CandyPastelPink to CandyPurple
    }

    fun cakeFlavorColors(flavorId: String?): Pair<Color, Color> = when {
        flavorId == null -> Color(0xFFFFF8DC) to Color(0xFFFFE57F)
        flavorId.contains("vanilla") -> Color(0xFFFFF8DC) to Color(0xFFFFB6C1)
        flavorId.contains("strawberry") -> Color(0xFFFFC0CB) to Color(0xFFFF5996)
        flavorId.contains("blue_cotton") -> Color(0xFFB2EBF2) to Color(0xFF81D4FA)
        flavorId.contains("chocolate") -> Color(0xFF6D4C41) to Color(0xFF4E342E)
        else -> Color(0xFFFFF8DC) to CandyPastelPink
    }
}

enum class StageBackdrop {
    PASTEL,
    DISCO,
    FOREST
}

enum class CharacterPose {
    IDLE,
    HAPPY,
    STAR,
    WAVE
}

/**
 * Shared 11-Layer Vector Renderer (RULE-051, RULE-052, RULE-072).
 * Used both on live Portrait screen and for offscreen gallery bitmap capture so kept photos match 1:1.
 */
object CharacterVectorRenderer {

    fun renderToBitmap(
        characterState: CharacterState,
        width: Int = 720,
        height: Int = 960,
        backdrop: StageBackdrop = StageBackdrop.PASTEL,
        pose: CharacterPose = CharacterPose.HAPPY
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val androidCanvas = AndroidCanvas(bitmap)
        val drawScope = CanvasDrawScope()
        drawScope.draw(
            density = Density(2f, 1f),
            layoutDirection = LayoutDirection.Ltr,
            canvas = ComposeCanvas(androidCanvas),
            size = Size(width.toFloat(), height.toFloat())
        ) {
            drawCharacterScene(
                characterState = characterState,
                isBlinking = false,
                tailWagAngle = 8f,
                backdrop = backdrop,
                pose = pose,
                drawBackdrop = true
            )
        }
        return bitmap
    }

    fun DrawScope.drawBackdropLayer(backdrop: StageBackdrop) {
        when (backdrop) {
            StageBackdrop.PASTEL -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFFFE6F2), Color(0xFFFFF8E1), Color(0xFFE0F7FA))
                    )
                )
                // Soft clouds
                drawCircle(Color.White.copy(alpha = 0.65f), radius = size.minDimension * 0.14f, center = Offset(size.width * 0.2f, size.height * 0.2f))
                drawCircle(Color.White.copy(alpha = 0.65f), radius = size.minDimension * 0.10f, center = Offset(size.width * 0.3f, size.height * 0.21f))
                drawCircle(Color.White.copy(alpha = 0.65f), radius = size.minDimension * 0.13f, center = Offset(size.width * 0.8f, size.height * 0.16f))
            }
            StageBackdrop.DISCO -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF2C1035), Color(0xFF512DA8), Color(0xFFE91E63).copy(alpha = 0.7f))
                    )
                )
                // Disco spotlight beams
                val beamLeft = Path().apply {
                    moveTo(size.width * 0.5f, 0f)
                    lineTo(size.width * 0.05f, size.height)
                    lineTo(size.width * 0.35f, size.height)
                    close()
                }
                val beamRight = Path().apply {
                    moveTo(size.width * 0.5f, 0f)
                    lineTo(size.width * 0.65f, size.height)
                    lineTo(size.width * 0.95f, size.height)
                    close()
                }
                drawPath(beamLeft, CandyYellow.copy(alpha = 0.18f))
                drawPath(beamRight, CandyMint.copy(alpha = 0.18f))
                // Mirror ball
                drawCircle(Color(0xFFE0E0E0), radius = size.minDimension * 0.07f, center = Offset(size.width * 0.5f, size.height * 0.08f))
            }
            StageBackdrop.FOREST -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFE1F5FE), Color(0xFFE8F5E9), Color(0xFFFFF9C4))
                    )
                )
                // Rainbow arch behind stage
                CandyPalette.RainbowColors.forEachIndexed { idx, col ->
                    val inset = idx * (size.minDimension * 0.025f)
                    drawArc(
                        color = col.copy(alpha = 0.35f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(size.width * 0.08f + inset, size.height * 0.18f + inset),
                        size = Size(size.width * 0.84f - inset * 2, size.width * 0.84f - inset * 2),
                        style = Stroke(width = size.minDimension * 0.022f)
                    )
                }
            }
        }
    }

    fun DrawScope.drawCharacterScene(
        characterState: CharacterState,
        isBlinking: Boolean = false,
        tailWagAngle: Float = 0f,
        backdrop: StageBackdrop = StageBackdrop.PASTEL,
        pose: CharacterPose = CharacterPose.IDLE,
        drawBackdrop: Boolean = false
    ) {
        // LAYER 1: Background
        if (drawBackdrop) {
            drawBackdropLayer(backdrop)
        }

        val unit = (size.minDimension * 0.42f).coerceAtMost(size.height * 0.28f)
        val cx = size.width * 0.5f
        val headY = when (pose) {
            CharacterPose.STAR -> size.height * 0.38f
            else -> size.height * 0.42f
        }
        val headR = unit * 0.56f
        val neckY = headY + headR * 0.85f
        val bodyBottomY = neckY + headR * 1.45f
        val shoulderHalfW = headR * 0.62f

        if (characterState.characterType == CharacterType.PRINCESS) {
            CandySalonStudioRenderer.run {
                drawPrincess3D(
                    cx = cx,
                    headY = headY,
                    headR = headR,
                    characterState = characterState,
                    isBlinking = isBlinking,
                    swayAngle = tailWagAngle
                )
            }
            return
        }

        val skinColor = if (characterState.characterType == CharacterType.UNICORN) {
            Color(0xFFFFF6FA)
        } else {
            Color(0xFFFFDFC4)
        }
        val outlineColor = CandyDeepPlum.copy(alpha = 0.35f)
        val hairBrush = CandyPalette.hairBrush(
            characterState.hairColorId,
            Offset(cx - headR * 1.3f, headY - headR * 1.2f),
            Offset(cx + headR * 1.3f, bodyBottomY)
        )

        // LAYER 2: Back Wings
        characterState.wingsId?.let { wingsId ->
            drawWingsLayer(wingsId, cx, neckY + headR * 0.45f, headR)
        }

        // LAYER 3: Back Hair & Unicorn Tail
        if (characterState.characterType == CharacterType.UNICORN) {
            withTransform({
                rotate(degrees = tailWagAngle, pivot = Offset(cx + shoulderHalfW * 0.8f, bodyBottomY - headR * 0.2f))
            }) {
                val tailPath = Path().apply {
                    moveTo(cx + shoulderHalfW * 0.65f, bodyBottomY - headR * 0.25f)
                    cubicTo(
                        cx + headR * 1.65f, bodyBottomY - headR * 0.85f,
                        cx + headR * 1.75f, bodyBottomY + headR * 0.35f,
                        cx + headR * 1.15f, bodyBottomY + headR * 0.3f
                    )
                    close()
                }
                drawPath(tailPath, brush = hairBrush)
            }
        }
        drawBackHair(characterState.hairStyleId, cx, headY, headR, bodyBottomY, hairBrush)

        // LAYER 4: Body, Neck, Arms & Face Skin
        drawBodyAndFaceSkin(
            characterType = characterState.characterType,
            skinColor = skinColor,
            outlineColor = outlineColor,
            cx = cx,
            headY = headY,
            headR = headR,
            neckY = neckY,
            bodyBottomY = bodyBottomY,
            shoulderHalfW = shoulderHalfW,
            pose = pose
        )

        // LAYER 5: Makeup (Strictly Clipped inside Eyelid Mask - RULE-052)
        val eyeR = headR * 0.21f
        val leftEye = Offset(cx - headR * 0.39f, headY - headR * 0.04f)
        val rightEye = Offset(cx + headR * 0.39f, headY - headR * 0.04f)

        if (characterState.makeup.eyeshadow != null) {
            val esColor = CandyPalette.eyeshadowColor(characterState.makeup.eyeshadow)
            drawClippedEyeshadow(leftEye, eyeR, esColor)
            drawClippedEyeshadow(rightEye, eyeR, esColor)
        }

        if (characterState.makeup.blush != null) {
            val blColor = CandyPalette.blushColor(characterState.makeup.blush)
            drawOval(
                color = blColor.copy(alpha = 0.65f),
                topLeft = Offset(cx - headR * 0.68f, headY + headR * 0.16f),
                size = Size(headR * 0.34f, headR * 0.18f)
            )
            drawOval(
                color = blColor.copy(alpha = 0.65f),
                topLeft = Offset(cx + headR * 0.34f, headY + headR * 0.16f),
                size = Size(headR * 0.34f, headR * 0.18f)
            )
        }

        // LAYER 6: Eyes, Lashes & Lipstick (Always OVER Eyeshadow! RULE-051)
        drawKawaiiEyes(leftEye, rightEye, eyeR, isBlinking)
        drawMouthAndLipstick(cx, headY, headR, characterState.makeup.lipstick)

        // LAYER 7: Outfit (Dress / Bodice / Skirt)
        characterState.outfitId?.let { outfitId ->
            drawOutfitLayer(
                outfitId = outfitId,
                cx = cx,
                neckY = neckY,
                headR = headR,
                bodyBottomY = bodyBottomY
            )
        }

        // LAYER 8: Front Hair / Bangs
        drawFrontHair(characterState.hairStyleId, cx, headY, headR, hairBrush)

        // LAYER 9: Accessories (Horn & Jewelry)
        characterState.hornId?.let { hornId ->
            drawHornLayer(hornId, cx, headY - headR * 0.88f, headR)
        }
        characterState.jewelryId?.let { jewelryId ->
            drawJewelryLayer(jewelryId, cx, headY, neckY, headR)
        }

        // LAYER 10: Tiara / Crown
        characterState.tiaraId?.let { tiaraId ->
            drawTiaraLayer(tiaraId, cx, headY - headR * 0.88f, headR)
        }

        // LAYER 11: Face Paints & Stickers & Sparkles
        drawFacePaintsLayer(characterState.facePaints, cx, headY, headR)
        drawStickersLayer(characterState.stickers, cx, headY, bodyBottomY, headR)
    }

    internal fun DrawScope.drawWingsLayer(wingsId: String, cx: Float, wingCy: Float, headR: Float) {
        val (primary, secondary) = when {
            wingsId.contains("rainbow") -> Color(0xFFFF80AB) to CandyMint
            wingsId.contains("starlight") -> Color(0xFF7986CB) to CandyYellow
            wingsId.contains("fairy") -> Color(0xFFCE93D8) to Color(0xFFF48FB1)
            wingsId.contains("neon") -> Color(0xFF00E5FF) to Color(0xFF76FF03)
            wingsId.contains("celestial") -> Color(0xFFFFE082) to Color(0xFFB388FF)
            else -> CandyYellow to CandyPastelPink
        }
        val wingW = if (wingsId.contains("celestial")) headR * 1.85f else headR * 1.45f
        val wingH = headR * 1.55f

        for (side in listOf(-1f, 1f)) {
            if (wingsId.contains("fairy")) {
                // Upper butterfly wing + lower rounder wing
                val upper = Path().apply {
                    moveTo(cx + side * headR * 0.25f, wingCy - headR * 0.1f)
                    cubicTo(
                        cx + side * (wingW * 1.1f), wingCy - wingH * 0.9f,
                        cx + side * (wingW * 1.15f), wingCy + headR * 0.15f,
                        cx + side * headR * 0.3f, wingCy + headR * 0.35f
                    )
                    close()
                }
                val lower = Path().apply {
                    moveTo(cx + side * headR * 0.25f, wingCy + headR * 0.2f)
                    cubicTo(
                        cx + side * (wingW * 0.9f), wingCy + headR * 0.25f,
                        cx + side * (wingW * 0.8f), wingCy + wingH * 0.8f,
                        cx + side * headR * 0.2f, wingCy + headR * 0.6f
                    )
                    close()
                }
                drawPath(upper, Brush.linearGradient(listOf(primary, secondary)))
                drawPath(lower, Brush.linearGradient(listOf(secondary, primary.copy(alpha = 0.8f))))
            } else {
                // Feather wings with scalloped tips
                val wingPath = Path().apply {
                    moveTo(cx + side * headR * 0.2f, wingCy)
                    quadraticBezierTo(
                        cx + side * wingW * 0.95f, wingCy - wingH * 0.85f,
                        cx + side * wingW, wingCy - headR * 0.15f
                    )
                    quadraticBezierTo(
                        cx + side * wingW * 0.9f, wingCy + headR * 0.5f,
                        cx + side * wingW * 0.55f, wingCy + headR * 0.72f
                    )
                    lineTo(cx + side * headR * 0.2f, wingCy + headR * 0.45f)
                    close()
                }
                val brush = if (wingsId.contains("rainbow")) {
                    Brush.verticalGradient(CandyPalette.RainbowColors)
                } else {
                    Brush.linearGradient(listOf(primary, secondary))
                }
                drawPath(wingPath, brush = brush)
                drawPath(wingPath, color = Color.White.copy(alpha = 0.65f), style = Stroke(width = 3f))
            }
        }
    }

    private fun DrawScope.drawBackHair(
        hairStyleId: String?,
        cx: Float,
        headY: Float,
        headR: Float,
        bodyBottomY: Float,
        brush: Brush
    ) {
        when {
            hairStyleId?.contains("buns_twin") == true -> {
                drawCircle(brush, radius = headR * 0.52f, center = Offset(cx - headR * 0.95f, headY - headR * 0.72f))
                drawCircle(brush, radius = headR * 0.52f, center = Offset(cx + headR * 0.95f, headY - headR * 0.72f))
                drawCircle(brush, radius = headR * 1.04f, center = Offset(cx, headY - headR * 0.05f))
            }
            hairStyleId?.contains("ponytail_high") == true -> {
                val pony = Path().apply {
                    moveTo(cx, headY - headR * 0.9f)
                    cubicTo(cx + headR * 1.5f, headY - headR * 1.2f, cx + headR * 1.4f, bodyBottomY * 0.9f, cx + headR * 0.85f, bodyBottomY)
                    quadraticBezierTo(cx + headR * 0.6f, headY + headR * 0.5f, cx, headY - headR * 0.35f)
                    close()
                }
                drawPath(pony, brush)
                drawCircle(brush, radius = headR * 1.04f, center = Offset(cx, headY - headR * 0.05f))
            }
            hairStyleId?.contains("macaron") == true -> {
                // Two cute macaron side buns + wavy curls
                drawOval(brush, topLeft = Offset(cx - headR * 1.38f, headY - headR * 0.5f), size = Size(headR * 0.7f, headR * 0.9f))
                drawOval(brush, topLeft = Offset(cx + headR * 0.68f, headY - headR * 0.5f), size = Size(headR * 0.7f, headR * 0.9f))
                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(cx - headR * 1.05f, headY - headR * 1.0f),
                    size = Size(headR * 2.1f, headR * 1.85f),
                    cornerRadius = CornerRadius(headR * 0.9f, headR * 0.9f)
                )
            }
            hairStyleId?.contains("royal_chignon") == true -> {
                // Regal high bun
                drawCircle(brush, radius = headR * 0.58f, center = Offset(cx, headY - headR * 1.15f))
                drawCircle(brush, radius = headR * 1.06f, center = Offset(cx, headY - headR * 0.04f))
            }
            else -> {
                // Long flowing mane / waves / braids down behind shoulders
                val extraH = if (hairStyleId?.contains("waves") == true || hairStyleId?.contains("curls") == true) {
                    headR * 2.45f
                } else {
                    headR * 2.25f
                }
                drawRoundRect(
                    brush = brush,
                    topLeft = Offset(cx - headR * 1.15f, headY - headR * 1.05f),
                    size = Size(headR * 2.3f, extraH),
                    cornerRadius = CornerRadius(headR * 1.0f, headR * 1.0f)
                )
            }
        }
    }

    private fun DrawScope.drawFrontHair(
        hairStyleId: String?,
        cx: Float,
        headY: Float,
        headR: Float,
        brush: Brush
    ) {
        when {
            hairStyleId?.contains("pop_waves") == true || hairStyleId?.contains("curls") == true -> {
                // Asymmetric sweeping wave bangs
                val sweep = Path().apply {
                    moveTo(cx - headR * 0.98f, headY - headR * 0.25f)
                    cubicTo(cx - headR * 0.6f, headY - headR * 1.25f, cx + headR * 0.95f, headY - headR * 1.15f, cx + headR * 0.98f, headY - headR * 0.12f)
                    quadraticBezierTo(cx + headR * 0.25f, headY - headR * 0.7f, cx - headR * 0.98f, headY - headR * 0.25f)
                    close()
                }
                drawPath(sweep, brush)
            }
            hairStyleId?.contains("braid") == true -> {
                // Center parted soft locks + braid strand on side
                val leftPart = Path().apply {
                    moveTo(cx, headY - headR * 1.02f)
                    quadraticBezierTo(cx - headR * 0.9f, headY - headR * 0.95f, cx - headR * 0.98f, headY - headR * 0.1f)
                    quadraticBezierTo(cx - headR * 0.4f, headY - headR * 0.62f, cx, headY - headR * 0.65f)
                    close()
                }
                val rightPart = Path().apply {
                    moveTo(cx, headY - headR * 1.02f)
                    quadraticBezierTo(cx + headR * 0.9f, headY - headR * 0.95f, cx + headR * 0.98f, headY - headR * 0.1f)
                    quadraticBezierTo(cx + headR * 0.4f, headY - headR * 0.62f, cx, headY - headR * 0.65f)
                    close()
                }
                drawPath(leftPart, brush)
                drawPath(rightPart, brush)
                // Braided beads down left side
                for (i in 0..4) {
                    drawCircle(brush, radius = headR * 0.18f, center = Offset(cx - headR * 0.85f, headY + i * headR * 0.24f))
                }
            }
            else -> {
                // Classic scalloped kawaii bangs
                drawArc(
                    brush = brush,
                    startAngle = 185f,
                    sweepAngle = 170f,
                    useCenter = true,
                    topLeft = Offset(cx - headR * 0.98f, headY - headR * 1.04f),
                    size = Size(headR * 1.96f, headR * 0.88f)
                )
            }
        }
    }

    private fun DrawScope.drawBodyAndFaceSkin(
        characterType: CharacterType,
        skinColor: Color,
        outlineColor: Color,
        cx: Float,
        headY: Float,
        headR: Float,
        neckY: Float,
        bodyBottomY: Float,
        shoulderHalfW: Float,
        pose: CharacterPose
    ) {
        // Torso with soft natural curvature
        val torso = Path().apply {
            moveTo(cx - headR * 0.22f, neckY - headR * 0.05f)
            lineTo(cx + headR * 0.22f, neckY - headR * 0.05f)
            cubicTo(
                cx + shoulderHalfW * 1.15f, neckY + headR * 0.25f,
                cx + headR * 0.85f, bodyBottomY - headR * 0.25f,
                cx + headR * 0.72f, bodyBottomY
            )
            lineTo(cx - headR * 0.72f, bodyBottomY)
            cubicTo(
                cx - headR * 0.85f, bodyBottomY - headR * 0.25f,
                cx - shoulderHalfW * 1.15f, neckY + headR * 0.25f,
                cx - headR * 0.22f, neckY - headR * 0.05f
            )
            close()
        }
        drawPath(torso, skinColor)
        drawPath(torso, outlineColor, style = Stroke(width = 3f))

        // Cute cartoon forelegs / arms with soft rounded hooves
        val armW = headR * 0.20f
        val leftArmStart = Offset(cx - shoulderHalfW * 0.95f, neckY + headR * 0.25f)
        val rightArmStart = Offset(cx + shoulderHalfW * 0.95f, neckY + headR * 0.25f)

        val leftArmEnd = when (pose) {
            CharacterPose.STAR -> Offset(cx - headR * 1.12f, headY + headR * 0.05f)
            CharacterPose.HAPPY -> Offset(cx - headR * 0.82f, headY + headR * 0.48f)
            else -> Offset(cx - headR * 0.75f, neckY + headR * 0.75f)
        }
        val rightArmEnd = when (pose) {
            CharacterPose.WAVE, CharacterPose.STAR -> Offset(cx + headR * 1.15f, headY + headR * 0.05f)
            CharacterPose.HAPPY -> Offset(cx + headR * 0.82f, headY + headR * 0.48f)
            else -> Offset(cx + headR * 0.75f, neckY + headR * 0.75f)
        }

        // Left curved arm
        val leftArmPath = Path().apply {
            moveTo(leftArmStart.x, leftArmStart.y)
            quadraticBezierTo(
                (leftArmStart.x + leftArmEnd.x) * 0.5f - 12f,
                (leftArmStart.y + leftArmEnd.y) * 0.5f,
                leftArmEnd.x,
                leftArmEnd.y
            )
        }
        drawPath(leftArmPath, color = outlineColor, style = Stroke(width = armW + 5f, cap = StrokeCap.Round))
        drawPath(leftArmPath, color = skinColor, style = Stroke(width = armW, cap = StrokeCap.Round))
        // Left cute hoof cap
        drawCircle(CandyPastelPink, radius = armW * 0.72f, center = leftArmEnd)
        drawCircle(CandyYellow, radius = armW * 0.42f, center = leftArmEnd)

        // Right curved arm
        val rightArmPath = Path().apply {
            moveTo(rightArmStart.x, rightArmStart.y)
            quadraticBezierTo(
                (rightArmStart.x + rightArmEnd.x) * 0.5f + 12f,
                (rightArmStart.y + rightArmEnd.y) * 0.5f,
                rightArmEnd.x,
                rightArmEnd.y
            )
        }
        drawPath(rightArmPath, color = outlineColor, style = Stroke(width = armW + 5f, cap = StrokeCap.Round))
        drawPath(rightArmPath, color = skinColor, style = Stroke(width = armW, cap = StrokeCap.Round))
        // Right cute hoof cap
        drawCircle(CandyPastelPink, radius = armW * 0.72f, center = rightArmEnd)
        drawCircle(CandyYellow, radius = armW * 0.42f, center = rightArmEnd)

        // Cute Fluffy Pony Ears
        if (characterType == CharacterType.UNICORN) {
            for (side in listOf(-1f, 1f)) {
                val earOuter = Path().apply {
                    moveTo(cx + side * headR * 0.35f, headY - headR * 0.65f)
                    cubicTo(
                        cx + side * headR * 0.65f, headY - headR * 0.95f,
                        cx + side * headR * 1.05f, headY - headR * 1.45f,
                        cx + side * headR * 0.78f, headY - headR * 1.55f
                    )
                    cubicTo(
                        cx + side * headR * 0.55f, headY - headR * 1.50f,
                        cx + side * headR * 0.28f, headY - headR * 1.15f,
                        cx + side * headR * 0.12f, headY - headR * 0.85f
                    )
                    close()
                }
                val earInner = Path().apply {
                    moveTo(cx + side * headR * 0.40f, headY - headR * 0.75f)
                    cubicTo(
                        cx + side * headR * 0.65f, headY - headR * 1.00f,
                        cx + side * headR * 0.92f, headY - headR * 1.38f,
                        cx + side * headR * 0.72f, headY - headR * 1.42f
                    )
                    cubicTo(
                        cx + side * headR * 0.55f, headY - headR * 1.38f,
                        cx + side * headR * 0.35f, headY - headR * 1.12f,
                        cx + side * headR * 0.22f, headY - headR * 0.90f
                    )
                    close()
                }
                drawPath(earOuter, skinColor)
                drawPath(earOuter, outlineColor, style = Stroke(width = 3.2f, join = StrokeJoin.Round))
                drawPath(earInner, Brush.verticalGradient(listOf(Color(0xFFFF80AB), Color(0xFFFFCDD2))))
                // Fluffy fur tuft at ear base
                drawCircle(Color.White, radius = headR * 0.10f, center = Offset(cx + side * headR * 0.28f, headY - headR * 0.75f))
            }
        } else {
            drawCircle(skinColor, radius = headR * 0.16f, center = Offset(cx - headR * 0.96f, headY + headR * 0.06f))
            drawCircle(skinColor, radius = headR * 0.16f, center = Offset(cx + headR * 0.96f, headY + headR * 0.06f))
        }

        // Head Contour with Organic Bézier Curves
        val headPath = Path().apply {
            moveTo(cx, headY - headR * 0.92f)
            cubicTo(
                cx + headR * 0.68f, headY - headR * 0.92f,
                cx + headR * 0.94f, headY - headR * 0.35f,
                cx + headR * 0.98f, headY + headR * 0.12f
            )
            cubicTo(
                cx + headR * 1.00f, headY + headR * 0.50f,
                cx + headR * 0.65f, headY + headR * 0.92f,
                cx, headY + headR * 0.94f
            )
            cubicTo(
                cx - headR * 0.65f, headY + headR * 0.94f,
                cx - headR * 1.00f, headY + headR * 0.50f,
                cx - headR * 0.98f, headY + headR * 0.12f
            )
            cubicTo(
                cx - headR * 0.94f, headY - headR * 0.35f,
                cx - headR * 0.68f, headY - headR * 0.92f,
                cx, headY - headR * 0.92f
            )
            close()
        }
        drawPath(headPath, skinColor)
        drawPath(headPath, outlineColor, style = Stroke(width = 3.5f))

        // Cute Snout / Muzzle Patch for Unicorn
        if (characterType == CharacterType.UNICORN) {
            val muzzleY = headY + headR * 0.44f
            val muzzleW = headR * 0.72f
            val muzzleH = headR * 0.45f
            drawOval(
                brush = Brush.verticalGradient(listOf(Color(0xFFFFF0F5), Color(0xFFFFDEE9))),
                topLeft = Offset(cx - muzzleW / 2f, muzzleY),
                size = Size(muzzleW, muzzleH)
            )
            drawOval(
                color = CandyDeepPlum.copy(alpha = 0.20f),
                topLeft = Offset(cx - muzzleW / 2f, muzzleY),
                size = Size(muzzleW, muzzleH),
                style = Stroke(width = 1.8f)
            )
            // Two cute tiny nostrils
            drawCircle(Color(0xFFFF8FAB), radius = 3.5f, center = Offset(cx - headR * 0.11f, muzzleY + muzzleH * 0.32f))
            drawCircle(Color(0xFFFF8FAB), radius = 3.5f, center = Offset(cx + headR * 0.11f, muzzleY + muzzleH * 0.32f))
        }
    }

    private fun DrawScope.drawClippedEyeshadow(eyeCenter: Offset, eyeR: Float, shadowColor: Color) {
        val eyelidMask = Path().apply {
            addArc(
                oval = Rect(
                    eyeCenter.x - eyeR * 1.15f,
                    eyeCenter.y - eyeR * 1.38f,
                    eyeCenter.x + eyeR * 1.15f,
                    eyeCenter.y + eyeR * 0.05f
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 180f
            )
            close()
        }
        clipPath(eyelidMask, clipOp = ClipOp.Intersect) {
            drawOval(
                brush = Brush.verticalGradient(
                    listOf(shadowColor.copy(alpha = 0.9f), shadowColor.copy(alpha = 0.45f)),
                    startY = eyeCenter.y - eyeR * 1.4f,
                    endY = eyeCenter.y
                ),
                topLeft = Offset(eyeCenter.x - eyeR * 1.3f, eyeCenter.y - eyeR * 1.5f),
                size = Size(eyeR * 2.6f, eyeR * 1.7f)
            )
        }
    }

    private fun DrawScope.drawKawaiiEyes(leftEye: Offset, rightEye: Offset, eyeR: Float, isBlinking: Boolean) {
        val lashColor = Color(0xFF2C1035)
        for (center in listOf(leftEye, rightEye)) {
            val isLeft = center.x < rightEye.x
            val sideSign = if (isLeft) -1f else 1f
            if (isBlinking) {
                drawArc(
                    color = lashColor,
                    startAngle = 15f,
                    sweepAngle = 150f,
                    useCenter = false,
                    topLeft = Offset(center.x - eyeR, center.y - eyeR * 0.35f),
                    size = Size(eyeR * 2f, eyeR * 0.85f),
                    style = Stroke(width = 5f, cap = StrokeCap.Round)
                )
            } else {
                // White sclera
                drawOval(
                    color = Color.White,
                    topLeft = Offset(center.x - eyeR * 1.05f, center.y - eyeR * 1.15f),
                    size = Size(eyeR * 2.1f, eyeR * 2.3f)
                )
                drawOval(
                    color = lashColor.copy(alpha = 0.4f),
                    topLeft = Offset(center.x - eyeR * 1.05f, center.y - eyeR * 1.15f),
                    size = Size(eyeR * 2.1f, eyeR * 2.3f),
                    style = Stroke(width = 2f)
                )
                // Glossy gradient iris
                val irisR = eyeR * 0.82f
                val irisCenter = Offset(center.x, center.y + eyeR * 0.08f)
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF5E2B97), Color(0xFFAB47BC), Color(0xFF00E5FF)),
                        startY = irisCenter.y - irisR,
                        endY = irisCenter.y + irisR
                    ),
                    radius = irisR,
                    center = irisCenter
                )
                // Dark pupil
                drawCircle(Color(0xFF1E0533), radius = irisR * 0.52f, center = irisCenter)
                // Multi-tier sparkling catchlights
                drawCircle(Color.White, radius = irisR * 0.38f, center = Offset(irisCenter.x - irisR * 0.30f, irisCenter.y - irisR * 0.30f))
                drawCircle(Color.White, radius = irisR * 0.18f, center = Offset(irisCenter.x + irisR * 0.32f, irisCenter.y + irisR * 0.32f))
                drawStarShape(Offset(irisCenter.x + irisR * 0.12f, irisCenter.y - irisR * 0.15f), irisR * 0.16f, Color.White)

                // Upper curved lush eyelashes
                val lashPath = Path().apply {
                    moveTo(center.x - sideSign * eyeR * 1.15f, center.y - eyeR * 0.25f)
                    cubicTo(
                        center.x - sideSign * eyeR * 0.6f, center.y - eyeR * 1.35f,
                        center.x + sideSign * eyeR * 0.6f, center.y - eyeR * 1.35f,
                        center.x + sideSign * eyeR * 1.25f, center.y - eyeR * 0.55f
                    )
                    lineTo(center.x + sideSign * eyeR * 1.45f, center.y - eyeR * 0.85f)
                }
                drawPath(lashPath, color = lashColor, style = Stroke(width = 5f, cap = StrokeCap.Round))
                // Soft delicate lower lash
                drawArc(
                    color = lashColor.copy(alpha = 0.5f),
                    startAngle = 20f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(center.x - eyeR * 0.75f, center.y + eyeR * 0.65f),
                    size = Size(eyeR * 1.5f, eyeR * 0.5f),
                    style = Stroke(width = 2f, cap = StrokeCap.Round)
                )
                // Cute curved eyebrow
                val browPath = Path().apply {
                    moveTo(center.x - sideSign * eyeR * 0.95f, center.y - eyeR * 1.45f)
                    quadraticBezierTo(
                        center.x, center.y - eyeR * 1.85f,
                        center.x + sideSign * eyeR * 0.95f, center.y - eyeR * 1.40f
                    )
                }
                drawPath(browPath, color = Color(0xFF6A1B9A), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
            }
        }
    }

    private fun DrawScope.drawMouthAndLipstick(cx: Float, headY: Float, headR: Float, lipstickId: String?) {
        val mouthY = headY + headR * 0.62f
        if (lipstickId != null) {
            val lipCol = CandyPalette.lipstickColor(lipstickId)
            val lipPath = Path().apply {
                moveTo(cx - headR * 0.20f, mouthY)
                quadraticBezierTo(cx - headR * 0.08f, mouthY - headR * 0.08f, cx, mouthY - headR * 0.04f)
                quadraticBezierTo(cx + headR * 0.08f, mouthY - headR * 0.08f, cx + headR * 0.20f, mouthY)
                quadraticBezierTo(cx, mouthY + headR * 0.15f, cx - headR * 0.20f, mouthY)
                close()
            }
            drawPath(lipPath, lipCol)
            // Lip gloss highlight
            drawLine(
                color = Color.White.copy(alpha = 0.75f),
                start = Offset(cx - headR * 0.06f, mouthY + headR * 0.03f),
                end = Offset(cx + headR * 0.06f, mouthY + headR * 0.03f),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
        } else {
            // Cute cheerful smile with dimples
            val smilePath = Path().apply {
                moveTo(cx - headR * 0.15f, mouthY - headR * 0.04f)
                quadraticBezierTo(cx, mouthY + headR * 0.10f, cx + headR * 0.15f, mouthY - headR * 0.04f)
            }
            drawPath(smilePath, color = CandyDeepPlum, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
            // Sweet rosy dimples
            drawCircle(Color(0xFFFF8FAB), radius = 2.5f, center = Offset(cx - headR * 0.16f, mouthY - headR * 0.03f))
            drawCircle(Color(0xFFFF8FAB), radius = 2.5f, center = Offset(cx + headR * 0.16f, mouthY - headR * 0.03f))
        }
    }

    private fun DrawScope.drawOutfitLayer(
        outfitId: String,
        cx: Float,
        neckY: Float,
        headR: Float,
        bodyBottomY: Float
    ) {
        val (topCol, skirtCol) = CandyPalette.outfitColors(outfitId)
        val waistY = neckY + headR * 0.58f
        val skirtW = when {
            outfitId.contains("ball_gown") || outfitId.contains("coronation") -> headR * 1.45f
            outfitId.contains("tutu") -> headR * 1.35f
            outfitId.contains("mermaid") -> headR * 0.92f
            else -> headR * 1.15f
        }

        // Bodice
        val bodice = Path().apply {
            moveTo(cx - headR * 0.45f, neckY + headR * 0.08f)
            lineTo(cx + headR * 0.45f, neckY + headR * 0.08f)
            lineTo(cx + headR * 0.38f, waistY)
            lineTo(cx - headR * 0.38f, waistY)
            close()
        }
        drawPath(bodice, topCol)

        // Skirt / Lower Dress
        val skirt = Path().apply {
            moveTo(cx - headR * 0.38f, waistY)
            lineTo(cx + headR * 0.38f, waistY)
            lineTo(cx + skirtW, bodyBottomY + headR * 0.15f)
            quadraticBezierTo(cx, bodyBottomY + headR * 0.28f, cx - skirtW, bodyBottomY + headR * 0.15f)
            close()
        }
        drawPath(skirt, Brush.verticalGradient(listOf(topCol, skirtCol)))
        drawPath(skirt, CandyDeepPlum.copy(alpha = 0.25f), style = Stroke(width = 2.5f))

        // Unique outfit details!
        when {
            outfitId.contains("peppermint") -> {
                for (dx in listOf(-0.4f, 0f, 0.4f)) {
                    drawLine(
                        color = Color.White,
                        start = Offset(cx + headR * dx * 0.5f, waistY),
                        end = Offset(cx + skirtW * dx * 0.95f, bodyBottomY + headR * 0.15f),
                        strokeWidth = 6f
                    )
                }
            }
            outfitId.contains("pastry_chef") -> {
                drawCircle(CandyPink, radius = 5f, center = Offset(cx, neckY + headR * 0.25f))
                drawCircle(CandyPink, radius = 5f, center = Offset(cx, neckY + headR * 0.42f))
                drawRoundRect(
                    color = CandyPastelPink,
                    topLeft = Offset(cx - headR * 0.35f, waistY + headR * 0.12f),
                    size = Size(headR * 0.7f, headR * 0.55f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
            }
            outfitId.contains("coronation") || outfitId.contains("ball_gown") -> {
                drawLine(CandyYellow, Offset(cx - headR * 0.4f, waistY), Offset(cx + headR * 0.4f, waistY), strokeWidth = 6f)
            }
            outfitId.contains("cyber") -> {
                drawLine(Color(0xFF00E5FF), Offset(cx, neckY + headR * 0.15f), Offset(cx, bodyBottomY), strokeWidth = 5f)
            }
            outfitId.contains("carnival") -> {
                // Festive cape trim
                drawCircle(CandyYellow, radius = 6f, center = Offset(cx - headR * 0.25f, waistY))
                drawCircle(CandyMint, radius = 6f, center = Offset(cx, waistY))
                drawCircle(CandyPink, radius = 6f, center = Offset(cx + headR * 0.25f, waistY))
            }
        }
    }

    internal fun DrawScope.drawHornLayer(hornId: String, cx: Float, foreheadY: Float, headR: Float) {
        val (baseCol, stripeCol) = when {
            hornId.contains("gold") -> Color(0xFFFFD700) to Color(0xFFFFA000)
            hornId.contains("crystal") -> Color(0xFF80DEEA) to Color(0xFFE0F7FA)
            hornId.contains("marshmallow") -> Color(0xFFF8BBD0) to Color.White
            hornId.contains("star_gem") -> Color(0xFFB388FF) to CandyYellow
            else -> CandyYellow to Color.White
        }
        val tipY = foreheadY - headR * 0.72f
        val baseHalf = headR * 0.15f
        val hornPath = Path().apply {
            moveTo(cx, tipY)
            lineTo(cx + baseHalf, foreheadY + headR * 0.05f)
            lineTo(cx - baseHalf, foreheadY + headR * 0.05f)
            close()
        }
        drawPath(hornPath, Brush.verticalGradient(listOf(stripeCol, baseCol)))
        drawPath(hornPath, CandyDeepPlum.copy(alpha = 0.35f), style = Stroke(width = 2.5f))

        // Spiral groove lines
        for (i in 1..3) {
            val t = i / 4f
            val y = tipY + (foreheadY - tipY) * t
            val hw = baseHalf * t * 0.9f
            drawLine(Color.White.copy(alpha = 0.85f), Offset(cx - hw, y), Offset(cx + hw, y + 4f), strokeWidth = 3f)
        }
        drawStarShape(Offset(cx, tipY - 2f), headR * 0.16f, CandyYellow)
    }

    internal fun DrawScope.drawJewelryLayer(jewelryId: String, cx: Float, headY: Float, neckY: Float, headR: Float) {
        when {
            jewelryId.contains("earrings") -> {
                for (side in listOf(-1f, 1f)) {
                    val ex = cx + side * headR * 0.92f
                    val ey = headY + headR * 0.22f
                    drawCircle(CandyPink, radius = headR * 0.12f, center = Offset(ex, ey))
                    drawCircle(CandyYellow, radius = headR * 0.06f, center = Offset(ex, ey))
                    drawLine(Color.White, Offset(ex, ey + 4f), Offset(ex, ey + 14f), strokeWidth = 2f)
                    drawCircle(CandyMint, radius = headR * 0.05f, center = Offset(ex, ey + 15f))
                }
            }
            jewelryId.contains("choker") -> {
                drawLine(Color(0xFFF06292), Offset(cx - headR * 0.26f, neckY), Offset(cx + headR * 0.26f, neckY), strokeWidth = 8f, cap = StrokeCap.Round)
                drawCircle(CandyYellow, radius = headR * 0.10f, center = Offset(cx, neckY + 3f))
            }
            jewelryId.contains("diamond") -> {
                drawArc(
                    color = Color(0xFFB2EBF2),
                    startAngle = 20f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(cx - headR * 0.32f, neckY - headR * 0.05f),
                    size = Size(headR * 0.64f, headR * 0.38f),
                    style = Stroke(width = 4f)
                )
                drawDiamondShape(Offset(cx, neckY + headR * 0.25f), headR * 0.15f, Color(0xFF00E5FF))
            }
            else -> {
                // Pearl necklace following neck curve
                for (i in -4..4) {
                    val px = cx + i * headR * 0.085f
                    val py = neckY + headR * 0.14f - (i * i) * headR * 0.008f
                    drawCircle(Color(0xFFFFFDE7), radius = headR * 0.052f, center = Offset(px, py))
                    drawCircle(CandyPastelPink, radius = headR * 0.052f, center = Offset(px, py), style = Stroke(width = 1.5f))
                }
            }
        }
    }

    internal fun DrawScope.drawTiaraLayer(tiaraId: String, cx: Float, browY: Float, headR: Float) {
        val mainCol = when {
            tiaraId.contains("strawberry") -> Color(0xFFFF69B4)
            tiaraId.contains("candy_cane") -> Color(0xFFEF5350)
            tiaraId.contains("imperial") -> Color(0xFFFFCA28)
            else -> CandyYellow
        }
        val path = Path().apply {
            moveTo(cx - headR * 0.52f, browY + headR * 0.06f)
            lineTo(cx - headR * 0.36f, browY - headR * 0.36f)
            lineTo(cx - headR * 0.16f, browY - headR * 0.12f)
            lineTo(cx, browY - headR * 0.45f)
            lineTo(cx + headR * 0.16f, browY - headR * 0.12f)
            lineTo(cx + headR * 0.36f, browY - headR * 0.36f)
            lineTo(cx + headR * 0.52f, browY + headR * 0.06f)
            close()
        }
        val brush = if (tiaraId.contains("imperial")) {
            Brush.horizontalGradient(CandyPalette.RainbowColors)
        } else {
            Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.7f), mainCol))
        }
        drawPath(path, brush)
        drawPath(path, CandyDeepPlum.copy(alpha = 0.4f), style = Stroke(width = 2.5f))
        drawStarShape(Offset(cx, browY - headR * 0.26f), headR * 0.1f, CandyPink)
    }

    internal fun DrawScope.drawFacePaintsLayer(facePaints: List<String>, cx: Float, headY: Float, headR: Float) {
        facePaints.forEachIndexed { idx, paintId ->
            val offsetX = cx + (if (idx % 2 == 0) 0.55f else -0.55f) * headR
            val offsetY = headY + (0.08f + idx * 0.12f) * headR
            when {
                paintId.contains("stars") -> drawStarShape(Offset(offsetX, offsetY), headR * 0.12f, CandyYellow)
                paintId.contains("carnival") -> {
                    val mask = Path().apply {
                        moveTo(cx - headR * 0.65f, headY - headR * 0.08f)
                        quadraticBezierTo(cx, headY - headR * 0.35f, cx + headR * 0.65f, headY - headR * 0.08f)
                    }
                    drawPath(mask, CandyPurple, style = Stroke(width = 5f, cap = StrokeCap.Round))
                }
                paintId.contains("neon") -> drawStarShape(Offset(offsetX, offsetY), headR * 0.11f, Color(0xFF00E5FF))
                else -> {
                    drawDiamondShape(Offset(offsetX, offsetY), headR * 0.1f, CandyPink)
                    drawDiamondShape(Offset(offsetX - headR * 0.09f, offsetY + headR * 0.09f), headR * 0.07f, CandyMint)
                }
            }
        }
    }

    internal fun DrawScope.drawStickersLayer(
        stickers: List<String>,
        cx: Float,
        headY: Float,
        bodyBottomY: Float,
        headR: Float
    ) {
        val waistY = headY + headR * 1.45f
        // Exact anatomical anchor spots on body/head (never floating in the void!)
        val anchorSlots = listOf(
            Offset(cx - headR * 0.55f, headY + headR * 0.28f), // 0: Left cheek
            Offset(cx + headR * 0.55f, headY + headR * 0.28f), // 1: Right cheek
            Offset(cx, headY + headR * 1.15f),                 // 2: Bodice center
            Offset(cx - headR * 0.28f, headY + headR * 1.25f), // 3: Bodice left
            Offset(cx + headR * 0.28f, headY + headR * 1.25f), // 4: Bodice right
            Offset(cx, waistY + headR * 0.35f),                 // 5: Skirt upper center
            Offset(cx - headR * 0.55f, waistY + headR * 0.45f), // 6: Skirt left
            Offset(cx + headR * 0.55f, waistY + headR * 0.45f), // 7: Skirt right
            Offset(cx, bodyBottomY - headR * 0.05f),            // 8: Skirt lower center
            Offset(cx - headR * 0.45f, headY - headR * 0.55f)  // 9: Forehead / hair pin
        )
        stickers.take(10).forEachIndexed { idx, stkId ->
            val pos = anchorSlots.getOrElse(idx) {
                Offset(cx + ((idx % 3) - 1) * headR * 0.4f, waistY + (idx / 3) * headR * 0.3f)
            }
            drawStickerVector(stkId, pos, headR * 0.16f)
        }
    }

    internal fun DrawScope.drawStickerVector(stickerId: String, center: Offset, r: Float) {
        when {
            stickerId.contains("heart") -> {
                val heart = Path().apply {
                    moveTo(center.x, center.y + r * 0.8f)
                    cubicTo(center.x - r * 1.4f, center.y, center.x - r * 0.7f, center.y - r, center.x, center.y - r * 0.25f)
                    cubicTo(center.x + r * 0.7f, center.y - r, center.x + r * 1.4f, center.y, center.x, center.y + r * 0.8f)
                    close()
                }
                drawPath(heart, CandyPink)
            }
            stickerId.contains("star") || stickerId.contains("wand") -> {
                drawStarShape(center, r, CandyYellow)
            }
            stickerId.contains("lollipop") -> {
                drawLine(Color.White, center, Offset(center.x, center.y + r * 1.3f), strokeWidth = 4f)
                drawCircle(CandyPink, radius = r * 0.75f, center = center)
                drawCircle(Color.White, radius = r * 0.42f, center = center, style = Stroke(width = 3f))
            }
            stickerId.contains("donut") -> {
                drawCircle(Color(0xFFFFCC80), radius = r * 0.85f, center = center)
                drawCircle(CandyPink, radius = r * 0.65f, center = center)
                drawCircle(CandyCream, radius = r * 0.28f, center = center)
            }
            stickerId.contains("diamond") -> drawDiamondShape(center, r, Color(0xFF4DD0E1))
            else -> drawStarShape(center, r * 0.9f, CandyMint)
        }
    }

    internal fun DrawScope.drawStarShape(center: Offset, outerRadius: Float, color: Color) {
        val innerRadius = outerRadius * 0.45f
        val path = Path()
        for (i in 0 until 10) {
            val angle = -PI / 2.0 + i * PI / 5.0
            val rad = if (i % 2 == 0) outerRadius else innerRadius
            val x = center.x + (rad * cos(angle)).toFloat()
            val y = center.y + (rad * sin(angle)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color)
    }

    internal fun DrawScope.drawDiamondShape(center: Offset, r: Float, color: Color) {
        val path = Path().apply {
            moveTo(center.x, center.y - r)
            lineTo(center.x + r * 0.8f, center.y)
            lineTo(center.x, center.y + r)
            lineTo(center.x - r * 0.8f, center.y)
            close()
        }
        drawPath(path, color)
        drawPath(path, Color.White.copy(alpha = 0.7f), style = Stroke(width = 2f))
    }

    /**
     * Exact WYSIWYG Item Thumbnail drawing shared with character renderer (RULE-072).
     */
    fun DrawScope.drawItemThumbnail(item: Item) {
        val color = CandyPalette.parseColor(item.color, CandyPink)
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension * 0.36f

        when (item.category) {
            "hair_styles" -> {
                val brush = Brush.linearGradient(listOf(CandyPurple, CandyPink))
                when {
                    item.id.contains("buns") -> {
                        // Twin buns thumbnail: two distinct round buns with bows
                        drawCircle(brush, radius = r * 0.42f, center = Offset(cx - r * 0.58f, cy - r * 0.35f))
                        drawCircle(brush, radius = r * 0.42f, center = Offset(cx + r * 0.58f, cy - r * 0.35f))
                        drawCircle(CandyPastelPink, radius = r * 0.50f, center = Offset(cx, cy + r * 0.15f))
                        drawCircle(brush, radius = r * 0.28f, center = Offset(cx, cy - r * 0.30f))
                        drawCircle(CandyPink, radius = r * 0.14f, center = Offset(cx - r * 0.58f, cy - r * 0.05f))
                        drawCircle(CandyPink, radius = r * 0.14f, center = Offset(cx + r * 0.58f, cy - r * 0.05f))
                    }
                    item.id.contains("ponytail") -> {
                        // High ponytail thumbnail: sweeping ponytail with hair tie
                        drawCircle(CandyPastelPink, radius = r * 0.48f, center = Offset(cx - r * 0.15f, cy + r * 0.15f))
                        val pony = Path().apply {
                            moveTo(cx, cy - r * 0.5f)
                            cubicTo(cx + r * 1.1f, cy - r * 0.8f, cx + r * 1.15f, cy + r * 0.4f, cx + r * 0.55f, cy + r * 0.75f)
                            cubicTo(cx + r * 0.35f, cy + r * 0.5f, cx + r * 0.55f, cy, cx, cy - r * 0.2f)
                            close()
                        }
                        drawPath(pony, brush)
                        drawCircle(CandyPink, radius = r * 0.16f, center = Offset(cx + r * 0.05f, cy - r * 0.4f))
                    }
                    item.id.contains("straight") -> {
                        // Silky straight hair thumbnail: clean straight strands
                        drawCircle(CandyPastelPink, radius = r * 0.45f, center = Offset(cx, cy))
                        for (s in -2..2) {
                            val sx = cx + s * (r * 0.32f)
                            drawLine(
                                brush = brush,
                                start = Offset(sx, cy - r * 0.75f),
                                end = Offset(sx, cy + r * 0.85f),
                                strokeWidth = 8f,
                                cap = StrokeCap.Round
                            )
                        }
                        // Parted fringe
                        drawArc(
                            brush = brush,
                            startAngle = 190f,
                            sweepAngle = 160f,
                            useCenter = true,
                            topLeft = Offset(cx - r * 0.65f, cy - r * 0.75f),
                            size = Size(r * 1.3f, r * 0.65f)
                        )
                    }
                    item.id.contains("waves") || item.id.contains("curls") -> {
                        // Pop waves thumbnail: voluminous wavy hair
                        drawCircle(CandyPastelPink, radius = r * 0.42f, center = Offset(cx, cy))
                        for (side in listOf(-1f, 1f)) {
                            val wavePath = Path().apply {
                                moveTo(cx + side * r * 0.2f, cy - r * 0.6f)
                                cubicTo(
                                    cx + side * r * 0.95f, cy - r * 0.2f,
                                    cx + side * r * 0.45f, cy + r * 0.2f,
                                    cx + side * r * 0.85f, cy + r * 0.75f
                                )
                            }
                            drawPath(wavePath, brush = brush, style = Stroke(width = 10f, cap = StrokeCap.Round))
                        }
                    }
                    item.id.contains("braid") -> {
                        // Braid thumbnail: diagonal braided strand
                        drawCircle(CandyPastelPink, radius = r * 0.42f, center = Offset(cx, cy))
                        for (b in 0..4) {
                            val by = cy - r * 0.4f + b * (r * 0.28f)
                            val bx = cx - r * 0.35f + b * (r * 0.15f)
                            drawCircle(brush, radius = r * 0.20f, center = Offset(bx, by))
                        }
                        drawCircle(CandyPink, radius = r * 0.14f, center = Offset(cx + r * 0.35f, cy + r * 0.75f))
                    }
                    else -> {
                        drawCircle(brush, radius = r * 0.75f, center = Offset(cx, cy))
                    }
                }
            }
            "hair_colors" -> {
                val brush = CandyPalette.hairBrush(item.id, Offset.Zero, Offset(size.width, size.height))
                drawCircle(brush, radius = r, center = Offset(cx, cy))
                drawCircle(Color.White.copy(alpha = 0.4f), radius = r * 0.3f, center = Offset(cx - r * 0.3f, cy - r * 0.3f))
            }
            "eyeshadows", "blushes" -> {
                drawCircle(color, radius = r * 0.9f, center = Offset(cx, cy))
                drawCircle(Color.White.copy(alpha = 0.5f), radius = r * 0.3f, center = Offset(cx - r * 0.3f, cy - r * 0.3f))
            }
            "lipsticks" -> {
                drawRoundRect(
                    color = Color.LightGray,
                    topLeft = Offset(cx - r * 0.35f, cy),
                    size = Size(r * 0.7f, r * 0.9f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                drawOval(
                    color = color,
                    topLeft = Offset(cx - r * 0.25f, cy - r * 0.8f),
                    size = Size(r * 0.5f, r * 0.9f)
                )
            }
            "face_paints" -> {
                when {
                    item.id.contains("stars") -> drawStarShape(Offset(cx, cy), r, CandyYellow)
                    item.id.contains("carnival") -> drawRoundRect(CandyPurple, topLeft = Offset(cx - r, cy - r * 0.35f), size = Size(r * 2, r * 0.7f), cornerRadius = CornerRadius(12f, 12f))
                    item.id.contains("neon") -> drawStarShape(Offset(cx, cy), r, Color(0xFF00E5FF))
                    else -> drawDiamondShape(Offset(cx, cy), r, CandyPink)
                }
            }
            "outfits" -> {
                val (top, skirt) = CandyPalette.outfitColors(item.id)
                // Elegant fairytale dress preview
                val dress = Path().apply {
                    moveTo(cx - r * 0.45f, cy - r * 0.75f)
                    cubicTo(cx - r * 0.2f, cy - r * 0.82f, cx + r * 0.2f, cy - r * 0.82f, cx + r * 0.45f, cy - r * 0.75f)
                    lineTo(cx + r * 0.30f, cy - r * 0.15f)
                    cubicTo(cx + r * 0.85f, cy + r * 0.35f, cx + r * 0.95f, cy + r * 0.85f, cx + r * 0.90f, cy + r * 0.95f)
                    lineTo(cx - r * 0.90f, cy + r * 0.95f)
                    cubicTo(cx - r * 0.95f, cy + r * 0.85f, cx - r * 0.85f, cy + r * 0.35f, cx - r * 0.30f, cy - r * 0.15f)
                    close()
                }
                drawPath(dress, Brush.verticalGradient(listOf(top, skirt)))
                drawPath(dress, CandyDeepPlum.copy(alpha = 0.3f), style = Stroke(width = 2.5f))
                // Sweetheart waist ribbon
                drawRoundRect(CandyYellow, topLeft = Offset(cx - r * 0.32f, cy - r * 0.20f), size = Size(r * 0.64f, r * 0.12f), cornerRadius = CornerRadius(4f, 4f))
            }
            "wings" -> {
                // Centered radiant wings filling thumbnail
                drawWingsLayer(item.id, cx, cy, r * 0.92f)
            }
            "horns" -> {
                // Centered sparkling unicorn horn with spiral & stars
                val hornPath = Path().apply {
                    moveTo(cx - r * 0.35f, cy + r * 0.75f)
                    lineTo(cx + r * 0.35f, cy + r * 0.75f)
                    lineTo(cx, cy - r * 0.95f)
                    close()
                }
                drawPath(hornPath, Brush.verticalGradient(CandyPalette.RainbowColors))
                drawPath(hornPath, CandyDeepPlum.copy(alpha = 0.3f), style = Stroke(width = 2f))
                // Spiral ridges
                for (s in 1..4) {
                    val sy = cy + r * 0.65f - s * (r * 0.35f)
                    val sw = r * 0.30f * (1f - s * 0.18f)
                    drawLine(Color.White.copy(alpha = 0.85f), Offset(cx - sw, sy), Offset(cx + sw, sy - 4f), strokeWidth = 2.5f)
                }
                drawStarShape(Offset(cx, cy - r * 0.95f), r * 0.25f, CandyYellow)
            }
            "tiaras" -> {
                // Gorgeous imperial princess tiara centered in card
                val tiaraPath = Path().apply {
                    moveTo(cx - r * 0.85f, cy + r * 0.45f)
                    lineTo(cx - r * 0.80f, cy - r * 0.15f)
                    lineTo(cx - r * 0.45f, cy + r * 0.10f)
                    lineTo(cx, cy - r * 0.65f)
                    lineTo(cx + r * 0.45f, cy + r * 0.10f)
                    lineTo(cx + r * 0.80f, cy - r * 0.15f)
                    lineTo(cx + r * 0.85f, cy + r * 0.45f)
                    close()
                }
                drawPath(tiaraPath, Brush.verticalGradient(listOf(CandyYellow, Color(0xFFFFA000))))
                drawPath(tiaraPath, CandyDeepPlum.copy(alpha = 0.35f), style = Stroke(width = 2.5f))
                // Jewels on crown peaks
                drawCircle(CandyPink, radius = r * 0.14f, center = Offset(cx, cy - r * 0.65f))
                drawCircle(Color(0xFF00E5FF), radius = r * 0.10f, center = Offset(cx - r * 0.80f, cy - r * 0.15f))
                drawCircle(Color(0xFF00E5FF), radius = r * 0.10f, center = Offset(cx + r * 0.80f, cy - r * 0.15f))
                drawStarShape(Offset(cx, cy), r * 0.18f, Color.White)
            }
            "jewelry" -> {
                when {
                    item.id.contains("earrings") -> {
                        // Dazzling pair of dangling drop earrings right in center
                        for (side in listOf(-1f, 1f)) {
                            val ex = cx + side * r * 0.45f
                            val ey = cy - r * 0.35f
                            drawCircle(CandyYellow, radius = r * 0.18f, center = Offset(ex, ey))
                            drawCircle(Color.White, radius = r * 0.08f, center = Offset(ex, ey))
                            drawLine(Color(0xFFFFD54F), Offset(ex, ey + r * 0.18f), Offset(ex, ey + r * 0.55f), strokeWidth = 3f)
                            drawStarShape(Offset(ex, ey + r * 0.65f), r * 0.28f, CandyPink)
                            drawCircle(Color.White, radius = r * 0.08f, center = Offset(ex, ey + r * 0.65f))
                        }
                    }
                    item.id.contains("choker") -> {
                        // Chic ribbon choker with golden heart pendant
                        val bandTop = cy - r * 0.22f
                        drawRoundRect(
                            color = Color(0xFFE91E63),
                            topLeft = Offset(cx - r * 0.88f, bandTop),
                            size = Size(r * 1.76f, r * 0.44f),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.5f),
                            topLeft = Offset(cx - r * 0.88f, bandTop),
                            size = Size(r * 1.76f, r * 0.44f),
                            cornerRadius = CornerRadius(8f, 8f),
                            style = Stroke(width = 2f)
                        )
                        drawCircle(CandyYellow, radius = r * 0.32f, center = Offset(cx, cy + r * 0.35f))
                        drawStarShape(Offset(cx, cy + r * 0.35f), r * 0.20f, Color.White)
                    }
                    item.id.contains("diamond") -> {
                        // Sparkling diamond necklace
                        drawArc(
                            color = Color(0xFF80DEEA),
                            startAngle = 10f,
                            sweepAngle = 160f,
                            useCenter = false,
                            topLeft = Offset(cx - r * 0.85f, cy - r * 0.65f),
                            size = Size(r * 1.7f, r * 1.25f),
                            style = Stroke(width = 5f, cap = StrokeCap.Round)
                        )
                        drawDiamondShape(Offset(cx, cy + r * 0.42f), r * 0.42f, Color(0xFF00E5FF))
                        drawCircle(Color.White, radius = r * 0.14f, center = Offset(cx, cy + r * 0.42f))
                    }
                    else -> {
                        // Plump, glowing pearl necklace
                        for (i in -4..4) {
                            val angle = (i * 18f) * (PI.toFloat() / 180f)
                            val px = cx + sin(angle) * r * 0.72f
                            val py = cy - cos(angle) * r * 0.35f + r * 0.32f
                            drawCircle(Color(0xFFFFFDE7), radius = r * 0.16f, center = Offset(px, py))
                            drawCircle(CandyPink.copy(alpha = 0.5f), radius = r * 0.16f, center = Offset(px, py), style = Stroke(width = 2f))
                        }
                        drawCircle(CandyYellow, radius = r * 0.22f, center = Offset(cx, cy + r * 0.62f))
                    }
                }
            }
            "stickers" -> drawStickerVector(item.id, Offset(cx, cy), r * 0.92f)
            "cake_flavors" -> {
                val (icing, drip) = CandyPalette.cakeFlavorColors(item.id)
                // Delicious cake slice with 3 sponge layers and drip
                val cakeSlice = Path().apply {
                    moveTo(cx - r * 0.9f, cy + r * 0.7f)
                    lineTo(cx + r * 0.9f, cy + r * 0.7f)
                    lineTo(cx + r * 0.6f, cy - r * 0.5f)
                    lineTo(cx - r * 0.6f, cy - r * 0.5f)
                    close()
                }
                drawPath(cakeSlice, icing)
                // Layer divider lines
                drawLine(Color(0xFF8D6E63), Offset(cx - r * 0.75f, cy + r * 0.2f), Offset(cx + r * 0.75f, cy + r * 0.2f), strokeWidth = 3f)
                drawLine(Color(0xFF8D6E63), Offset(cx - r * 0.68f, cy - r * 0.15f), Offset(cx + r * 0.68f, cy - r * 0.15f), strokeWidth = 3f)
                // Drip frosting on top
                drawRoundRect(drip, topLeft = Offset(cx - r * 0.7f, cy - r * 0.65f), size = Size(r * 1.4f, r * 0.35f), cornerRadius = CornerRadius(6f, 6f))
            }
            "cake_toppings" -> {
                when {
                    item.id.contains("cherries") -> {
                        // Pair of glossy cherries with green stem
                        drawCircle(Color(0xFFD32F2F), radius = r * 0.35f, center = Offset(cx - r * 0.28f, cy + r * 0.25f))
                        drawCircle(Color.White, radius = r * 0.10f, center = Offset(cx - r * 0.36f, cy + r * 0.16f))
                        drawCircle(Color(0xFFD32F2F), radius = r * 0.35f, center = Offset(cx + r * 0.28f, cy + r * 0.25f))
                        drawCircle(Color.White, radius = r * 0.10f, center = Offset(cx + r * 0.20f, cy + r * 0.16f))
                        // Green curved stems meeting at top
                        val stemPath = Path().apply {
                            moveTo(cx - r * 0.28f, cy)
                            quadraticBezierTo(cx - r * 0.1f, cy - r * 0.4f, cx, cy - r * 0.75f)
                            quadraticBezierTo(cx + r * 0.1f, cy - r * 0.4f, cx + r * 0.28f, cy)
                        }
                        drawPath(stemPath, color = Color(0xFF4CAF50), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
                        drawCircle(Color(0xFF4CAF50), radius = r * 0.12f, center = Offset(cx, cy - r * 0.75f))
                    }
                    item.id.contains("stars") || item.id.contains("glitter") -> {
                        drawStarShape(Offset(cx, cy), r * 0.95f, CandyYellow)
                        drawCircle(Color.White, radius = r * 0.20f, center = Offset(cx - r * 0.2f, cy - r * 0.2f))
                    }
                    item.id.contains("chocolate") -> {
                        drawCircle(Color(0xFF4E342E), radius = r * 0.65f, center = Offset(cx, cy))
                        drawCircle(Color(0xFF6D4C41), radius = r * 0.45f, center = Offset(cx, cy))
                        drawCircle(Color.White.copy(alpha = 0.5f), radius = r * 0.15f, center = Offset(cx - r * 0.2f, cy - r * 0.2f))
                    }
                    item.id.contains("macaron") -> {
                        drawRoundRect(CandyPurple, topLeft = Offset(cx - r * 0.85f, cy - r * 0.5f), size = Size(r * 1.7f, r * 1.0f), cornerRadius = CornerRadius(14f, 14f))
                        drawRoundRect(Color.White, topLeft = Offset(cx - r * 0.8f, cy - r * 0.08f), size = Size(r * 1.6f, r * 0.22f), cornerRadius = CornerRadius(4f, 4f))
                    }
                    item.id.contains("crystal") -> {
                        drawDiamondShape(Offset(cx, cy), r * 0.9f, Color(0xFF00E5FF))
                    }
                    else -> {
                        // Multi-color rainbow sprinkles
                        CandyPalette.RainbowColors.forEachIndexed { i, col ->
                            val angle = i * (2 * PI / 6).toFloat()
                            val px = cx + cos(angle) * r * 0.55f
                            val py = cy + sin(angle) * r * 0.55f
                            drawCircle(col, radius = r * 0.22f, center = Offset(px, py))
                        }
                    }
                }
            }
            else -> drawRoundRect(color = color, cornerRadius = CornerRadius(12f, 12f))
        }
    }
}

@Composable
fun CharacterCanvasRenderer(
    characterState: CharacterState,
    modifier: Modifier = Modifier,
    isBlinking: Boolean = false,
    tailWagAngle: Float = 0f,
    backdrop: StageBackdrop = StageBackdrop.PASTEL,
    pose: CharacterPose = CharacterPose.IDLE,
    drawBackdrop: Boolean = false
) {
    Canvas(modifier = modifier) {
        with(CharacterVectorRenderer) {
            drawCharacterScene(
                characterState = characterState,
                isBlinking = isBlinking,
                tailWagAngle = tailWagAngle,
                backdrop = backdrop,
                pose = pose,
                drawBackdrop = drawBackdrop
            )
        }
    }
}

@Composable
fun ItemThumbnailCanvas(item: Item, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        with(CharacterVectorRenderer) {
            drawItemThumbnail(item)
        }
    }
}
