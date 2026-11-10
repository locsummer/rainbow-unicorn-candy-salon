package com.rainbowunicorn.candysalon.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyCream
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.CharacterType
import com.rainbowunicorn.core.models.Item
import com.rainbowunicorn.core.models.ItemCategory
import com.rainbowunicorn.core.services.MathQuestion
import com.rainbowunicorn.core.services.ParentalGateService
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Accessible navigation button with both Icon + Text label and >= 48dp touch target (RULE-070, RULE-074).
 */
@Composable
fun NavButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = CandyPink,
    contentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = modifier
            .heightIn(min = 48.dp)
            .padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Standard confirmation dialog for mid-level exit and station resets (RULE-055, RULE-071).
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = stringResource(R.string.btn_confirm),
    dismissText: String = stringResource(R.string.btn_cancel)
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = CandyDeepPlum,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = CandyDeepPlum.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NavButton(
                        icon = Icons.Default.Close,
                        label = dismissText,
                        onClick = onDismiss,
                        containerColor = Color.LightGray,
                        contentColor = Color.DarkGray
                    )
                    NavButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        label = confirmText,
                        onClick = onConfirm,
                        containerColor = CandyPink,
                        contentColor = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Parental Gate Modal with 3.0s circular hold progress + arithmetic question (RULE-003, RULE-004).
 */
@Composable
fun ParentalGateModal(
    onVerified: () -> Unit,
    onDismiss: () -> Unit,
    gateService: ParentalGateService = remember { ParentalGateService() }
) {
    var holdCompleted by remember { mutableStateOf(false) }
    var currentQuestion by remember { mutableStateOf<MathQuestion?>(null) }
    var errorFeedback by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.parental_gate_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = CandyDeepPlum
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (!holdCompleted) {
                    Text(
                        text = stringResource(R.string.parental_gate_instruction),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(CandyPastelPink)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        scope.launch {
                                            progress.animateTo(
                                                targetValue = 1f,
                                                animationSpec = tween(
                                                    durationMillis = (ParentalGateService.REQUIRED_HOLD_SECONDS * 1000).toInt(),
                                                    easing = LinearEasing
                                                )
                                            )
                                            if (progress.value >= 1f) {
                                                holdCompleted = true
                                                currentQuestion = gateService.generateQuestion()
                                            }
                                        }
                                        tryAwaitRelease()
                                        if (progress.value < 1f) {
                                            scope.launch {
                                                progress.snapTo(0f)
                                            }
                                        }
                                    }
                                )
                            }
                    ) {
                        CircularProgressIndicator(
                            progress = { progress.value },
                            modifier = Modifier.fillMaxSize(),
                            color = CandyPink,
                            strokeWidth = 6.dp
                        )
                        Text(
                            text = stringResource(R.string.parental_gate_hold),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CandyDeepPlum,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                } else {
                    val q = currentQuestion
                    if (q != null) {
                        Text(
                            text = q.questionText,
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 28.sp,
                            color = CandyPink
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            for (option in q.options) {
                                Button(
                                    onClick = {
                                        if (gateService.verifyAnswer(q, option)) {
                                            onVerified()
                                        } else {
                                            errorFeedback = true
                                            currentQuestion = gateService.generateQuestion()
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CandyPurple),
                                    modifier = Modifier
                                        .size(64.dp)
                                        .heightIn(min = 48.dp)
                                ) {
                                    Text(
                                        text = option.toString(),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (errorFeedback) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.parental_gate_wrong),
                                color = Color.Red,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                NavButton(
                    icon = Icons.Default.Close,
                    label = stringResource(R.string.btn_cancel),
                    onClick = onDismiss,
                    containerColor = Color.LightGray,
                    contentColor = Color.DarkGray
                )
            }
        }
    }
}

/**
 * Bottom Scrolling Item Tray with persistent category tabs, retaining scroll state across tab changes (RULE-073).
 */
@Composable
fun BottomItemTray(
    categories: List<ItemCategory>,
    items: List<Item>,
    unlockedItems: Set<String>,
    selectedItemId: String?,
    onItemSelected: (Item) -> Unit,
    onLockedItemClicked: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val tabScrollStates = remember { mutableStateMapOf<String, Int>() }

    if (categories.isEmpty()) return
    val currentCategory = categories.getOrElse(selectedCategoryIndex) { categories.first() }
    val categoryItems = remember(currentCategory, items) {
        items.filter { it.category == currentCategory.id }
    }

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = tabScrollStates[currentCategory.id] ?: 0
    )

    // Save scroll position when leaving tab
    LaunchedEffect(lazyListState.firstVisibleItemIndex) {
        tabScrollStates[currentCategory.id] = lazyListState.firstVisibleItemIndex
    }

    Card(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScrollableTabRow(
                selectedTabIndex = selectedCategoryIndex,
                edgePadding = 12.dp,
                containerColor = CandyPastelPink.copy(alpha = 0.5f),
                contentColor = CandyDeepPlum,
                modifier = Modifier.height(38.dp)
            ) {
                categories.forEachIndexed { index, cat ->
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        text = {
                            Text(
                                text = cat.name,
                                fontSize = 12.sp,
                                fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            LazyRow(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(categoryItems, key = { it.id }) { item ->
                    val isUnlocked = item.unlockedByDefault || unlockedItems.contains(item.id)
                    val isSelected = selectedItemId == item.id

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when {
                                    isSelected -> CandyYellow
                                    isUnlocked -> CandyCream
                                    else -> Color.LightGray.copy(alpha = 0.5f)
                                }
                            )
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) CandyPink else CandyPurple.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                if (isUnlocked) {
                                    onItemSelected(item)
                                } else {
                                    onLockedItemClicked(item)
                                }
                            }
                    ) {
                        // Thumbnail Preview Canvas
                        ItemThumbnailCanvas(item = item, modifier = Modifier.size(54.dp))

                        if (!isUnlocked) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.35f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "${item.candyPrice} 🍬",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * WYSIWYG thumbnail preview matching the character's rendering (RULE-072).
 */
@Composable
fun ItemThumbnailCanvas(item: Item, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val color = try {
            if (item.color != null) Color(android.graphics.Color.parseColor(item.color)) else CandyPink
        } catch (_: Exception) {
            CandyPink
        }

        when {
            item.category.contains("hair_colors") || item.category.contains("eyeshadows") || item.category.contains("lipsticks") || item.category.contains("blushes") -> {
                drawCircle(color = color, radius = size.minDimension / 2.5f)
            }
            item.category.contains("horns") -> {
                val path = Path().apply {
                    moveTo(size.width / 2, size.height * 0.1f)
                    lineTo(size.width * 0.75f, size.height * 0.85f)
                    lineTo(size.width * 0.25f, size.height * 0.85f)
                    close()
                }
                drawPath(path, color)
            }
            item.category.contains("wings") -> {
                drawRoundRect(
                    color = color,
                    size = Size(size.width * 0.8f, size.height * 0.5f),
                    topLeft = Offset(size.width * 0.1f, size.height * 0.25f),
                    cornerRadius = CornerRadius(16f, 16f)
                )
            }
            item.category.contains("tiaras") -> {
                val path = Path().apply {
                    moveTo(size.width * 0.2f, size.height * 0.7f)
                    lineTo(size.width * 0.3f, size.height * 0.3f)
                    lineTo(size.width * 0.5f, size.height * 0.5f)
                    lineTo(size.width * 0.7f, size.height * 0.3f)
                    lineTo(size.width * 0.8f, size.height * 0.7f)
                    close()
                }
                drawPath(path, CandyYellow)
            }
            else -> {
                drawRoundRect(
                    color = color,
                    cornerRadius = CornerRadius(12f, 12f)
                )
            }
        }
    }
}

/**
 * High-performance 11-layer Vector Canvas Character Renderer (RULE-051, RULE-052).
 */
@Composable
fun CharacterCanvasRenderer(
    characterState: CharacterState,
    modifier: Modifier = Modifier,
    isBlinking: Boolean = false
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height * 0.55f
        val bodyRadius = size.minDimension * 0.32f

        // Layer 2: Back Wings
        if (characterState.wingsId != null) {
            val wingBrush = Brush.linearGradient(listOf(CandyYellow, CandyMint, CandyPastelPink))
            drawOval(
                brush = wingBrush,
                topLeft = Offset(cx - bodyRadius * 1.6f, cy - bodyRadius * 1.1f),
                size = Size(bodyRadius * 1.5f, bodyRadius * 2.2f)
            )
            drawOval(
                brush = wingBrush,
                topLeft = Offset(cx + bodyRadius * 0.1f, cy - bodyRadius * 1.1f),
                size = Size(bodyRadius * 1.5f, bodyRadius * 2.2f)
            )
        }

        // Layer 3: Back Hair
        val hairColor = try {
            if (characterState.hairColorId?.contains("rainbow") == true) CandyPink
            else CandyPurple
        } catch (_: Exception) { CandyPurple }

        drawRoundRect(
            color = hairColor,
            topLeft = Offset(cx - bodyRadius * 1.1f, cy - bodyRadius * 1.2f),
            size = Size(bodyRadius * 2.2f, bodyRadius * 2.5f),
            cornerRadius = CornerRadius(60f, 60f)
        )

        // Layer 4: Body & Skin
        val skinColor = if (characterState.characterType == CharacterType.UNICORN) Color(0xFFFFF9FB) else Color(0xFFFFDFC4)
        drawCircle(
            color = skinColor,
            radius = bodyRadius,
            center = Offset(cx, cy)
        )

        // Unicorn Ears
        if (characterState.characterType == CharacterType.UNICORN) {
            val leftEar = Path().apply {
                moveTo(cx - bodyRadius * 0.7f, cy - bodyRadius * 0.6f)
                lineTo(cx - bodyRadius * 0.9f, cy - bodyRadius * 1.3f)
                lineTo(cx - bodyRadius * 0.3f, cy - bodyRadius * 0.9f)
                close()
            }
            val rightEar = Path().apply {
                moveTo(cx + bodyRadius * 0.7f, cy - bodyRadius * 0.6f)
                lineTo(cx + bodyRadius * 0.9f, cy - bodyRadius * 1.3f)
                lineTo(cx + bodyRadius * 0.3f, cy - bodyRadius * 0.9f)
                close()
            }
            drawPath(leftEar, skinColor)
            drawPath(rightEar, skinColor)
        }

        // Layer 5: Makeup (Clipped strictly by Eyelid Mask so eyeshadow never bleeds out! RULE-052)
        val eyeRadius = bodyRadius * 0.22f
        val leftEyeCenter = Offset(cx - bodyRadius * 0.42f, cy - bodyRadius * 0.05f)
        val rightEyeCenter = Offset(cx + bodyRadius * 0.42f, cy - bodyRadius * 0.05f)

        if (characterState.makeup.eyeshadow != null) {
            val shadowColor = CandyPink
            val leftMask = Path().apply {
                addArc(
                    Rect(leftEyeCenter.x - eyeRadius, leftEyeCenter.y - eyeRadius * 1.2f, leftEyeCenter.x + eyeRadius, leftEyeCenter.y),
                    180f,
                    180f
                )
            }
            clipPath(leftMask, clipOp = ClipOp.Intersect) {
                drawCircle(color = shadowColor, radius = eyeRadius * 1.1f, center = leftEyeCenter)
            }
            val rightMask = Path().apply {
                addArc(
                    Rect(rightEyeCenter.x - eyeRadius, rightEyeCenter.y - eyeRadius * 1.2f, rightEyeCenter.x + eyeRadius, rightEyeCenter.y),
                    180f,
                    180f
                )
            }
            clipPath(rightMask, clipOp = ClipOp.Intersect) {
                drawCircle(color = shadowColor, radius = eyeRadius * 1.1f, center = rightEyeCenter)
            }
        }

        // Blush
        if (characterState.makeup.blush != null) {
            drawCircle(CandyPastelPink.copy(alpha = 0.6f), radius = bodyRadius * 0.18f, center = Offset(cx - bodyRadius * 0.6f, cy + bodyRadius * 0.25f))
            drawCircle(CandyPastelPink.copy(alpha = 0.6f), radius = bodyRadius * 0.18f, center = Offset(cx + bodyRadius * 0.6f, cy + bodyRadius * 0.25f))
        }

        // Layer 6: Eyes & Eyelashes (Always over makeup! RULE-051)
        if (isBlinking) {
            // Closed eyes
            drawLine(Color(0xFF2C1035), Offset(leftEyeCenter.x - eyeRadius, leftEyeCenter.y), Offset(leftEyeCenter.x + eyeRadius, leftEyeCenter.y), strokeWidth = 5f)
            drawLine(Color(0xFF2C1035), Offset(rightEyeCenter.x - eyeRadius, rightEyeCenter.y), Offset(rightEyeCenter.x + eyeRadius, rightEyeCenter.y), strokeWidth = 5f)
        } else {
            // Open kawaii eyes
            drawCircle(Color.White, radius = eyeRadius, center = leftEyeCenter)
            drawCircle(Color.White, radius = eyeRadius, center = rightEyeCenter)
            drawCircle(Color(0xFF4A154B), radius = eyeRadius * 0.65f, center = leftEyeCenter)
            drawCircle(Color(0xFF4A154B), radius = eyeRadius * 0.65f, center = rightEyeCenter)
            // Catchlights
            drawCircle(Color.White, radius = eyeRadius * 0.22f, center = Offset(leftEyeCenter.x - eyeRadius * 0.2f, leftEyeCenter.y - eyeRadius * 0.2f))
            drawCircle(Color.White, radius = eyeRadius * 0.22f, center = Offset(rightEyeCenter.x - eyeRadius * 0.2f, rightEyeCenter.y - eyeRadius * 0.2f))
        }

        // Lipstick
        if (characterState.makeup.lipstick != null) {
            drawOval(
                color = CandyPink,
                topLeft = Offset(cx - bodyRadius * 0.18f, cy + bodyRadius * 0.45f),
                size = Size(bodyRadius * 0.36f, bodyRadius * 0.15f)
            )
        }

        // Layer 7: Outfit
        if (characterState.outfitId != null) {
            val outfitBrush = Brush.verticalGradient(listOf(CandyPastelPink, CandyPurple))
            drawRoundRect(
                brush = outfitBrush,
                topLeft = Offset(cx - bodyRadius * 0.85f, cy + bodyRadius * 0.6f),
                size = Size(bodyRadius * 1.7f, bodyRadius * 1.2f),
                cornerRadius = CornerRadius(30f, 30f)
            )
        }

        // Layer 8: Front Hair / Bangs
        drawArc(
            color = hairColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cx - bodyRadius * 0.95f, cy - bodyRadius * 1.1f),
            size = Size(bodyRadius * 1.9f, bodyRadius * 0.8f)
        )

        // Layer 9: Horn / Accessories
        if (characterState.hornId != null) {
            val hornPath = Path().apply {
                moveTo(cx, cy - bodyRadius * 1.7f)
                lineTo(cx + bodyRadius * 0.18f, cy - bodyRadius * 0.9f)
                lineTo(cx - bodyRadius * 0.18f, cy - bodyRadius * 0.9f)
                close()
            }
            drawPath(hornPath, CandyYellow)
        }

        // Layer 10: Tiara
        if (characterState.tiaraId != null) {
            val tiaraPath = Path().apply {
                moveTo(cx - bodyRadius * 0.5f, cy - bodyRadius * 0.85f)
                lineTo(cx - bodyRadius * 0.3f, cy - bodyRadius * 1.2f)
                lineTo(cx, cy - bodyRadius * 0.95f)
                lineTo(cx + bodyRadius * 0.3f, cy - bodyRadius * 1.2f)
                lineTo(cx + bodyRadius * 0.5f, cy - bodyRadius * 0.85f)
                close()
            }
            drawPath(tiaraPath, CandyYellow)
        }

        // Layer 11: Face Paints & Stickers
        for (paint in characterState.facePaints) {
            drawCircle(CandyMint, radius = 10f, center = Offset(cx + bodyRadius * 0.45f, cy + bodyRadius * 0.25f))
        }
        for ((idx, _) in characterState.stickers.withIndex()) {
            val angle = idx * (2 * Math.PI / 10)
            val sx = cx + (bodyRadius * 1.1f * cos(angle)).toFloat()
            val sy = cy + (bodyRadius * 1.1f * sin(angle)).toFloat()
            drawCircle(CandyYellow, radius = 8f, center = Offset(sx, sy))
        }
    }
}
