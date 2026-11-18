package com.rainbowunicorn.candysalon.ui.components

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPink
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyCream
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.Item
import com.rainbowunicorn.core.models.ItemCategory
import com.rainbowunicorn.core.services.MathQuestion
import com.rainbowunicorn.core.services.ParentalGateService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) { isReduceMotionEnabled(context) }
}

fun isReduceMotionEnabled(context: Context): Boolean {
    return try {
        val scale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        )
        scale <= 0.01f
    } catch (_: Exception) {
        false
    }
}

/**
 * Animated Live Stage Character with periodic blinking and gentle tail wag (respecting Reduce Motion RULE-083).
 */
@Composable
fun AnimatedCharacterStage(
    characterState: CharacterState,
    modifier: Modifier = Modifier,
    backdrop: StageBackdrop = StageBackdrop.PASTEL,
    pose: CharacterPose = CharacterPose.IDLE,
    drawBackdrop: Boolean = false
) {
    val reduceMotion = rememberReduceMotion()
    var isBlinking by remember { mutableStateOf(false) }

    LaunchedEffect(reduceMotion) {
        if (!reduceMotion) {
            while (true) {
                delay(2800L)
                isBlinking = true
                delay(140L)
                isBlinking = false
            }
        } else {
            isBlinking = false
        }
    }

    val wagAngle = if (reduceMotion) {
        0f
    } else {
        val infiniteTransition = rememberInfiniteTransition(label = "tailWag")
        val angle by infiniteTransition.animateFloat(
            initialValue = -7f,
            targetValue = 7f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "tailAngle"
        )
        angle
    }

    CharacterCanvasRenderer(
        characterState = characterState,
        modifier = modifier,
        isBlinking = isBlinking,
        tailWagAngle = wagAngle,
        backdrop = backdrop,
        pose = pose,
        drawBackdrop = drawBackdrop
    )
}

@Composable
fun CandyBalanceBadge(
    candyBalance: Int,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(text = "🍬", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.candy_balance, candyBalance),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = CandyDeepPlum,
                maxLines = 1
            )
        }
    }
}

/**
 * Accessible navigation button with Icon + Label and >= 48dp touch target (RULE-070, RULE-074).
 */
@Composable
fun NavButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = CandyActionPink,
    contentColor: Color = Color.White,
    enabled: Boolean = true,
    compact: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(22.dp),
        contentPadding = if (compact) PaddingValues(horizontal = 10.dp, vertical = 6.dp) else PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.4f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .heightIn(min = 48.dp)
            .widthIn(min = 48.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(if (compact) 18.dp else 20.dp)
        )
        Spacer(modifier = Modifier.width(if (compact) 4.dp else 6.dp))
        Text(
            text = label,
            color = if (enabled) contentColor else contentColor.copy(alpha = 0.6f),
            fontSize = if (compact) 12.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Portrait-friendly confirmation dialog (RULE-055, RULE-071).
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
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .widthIn(max = 420.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = CandyDeepPlum,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = CandyDeepPlum.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(22.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NavButton(
                        icon = Icons.Default.Close,
                        label = dismissText,
                        onClick = onDismiss,
                        containerColor = Color(0xFFE0E0E0),
                        contentColor = CandyDeepPlum,
                        modifier = Modifier.weight(1f)
                    )
                    NavButton(
                        icon = Icons.Default.Check,
                        label = confirmText,
                        onClick = onConfirm,
                        containerColor = CandyActionPink,
                        contentColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Parental Gate Modal with 3.0s hold progress + arithmetic problem, sized cleanly for Portrait (RULE-003, RULE-004).
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 420.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔒 " + stringResource(R.string.parental_gate_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = CandyDeepPlum,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (!holdCompleted) {
                    Text(
                        text = stringResource(R.string.parental_gate_instruction),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = CandyDeepPlum.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(124.dp)
                            .clip(CircleShape)
                            .background(CandyPastelPink)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        val job = scope.launch {
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
                                            job.cancel()
                                            scope.launch { progress.snapTo(0f) }
                                        }
                                    }
                                )
                            }
                    ) {
                        CircularProgressIndicator(
                            progress = { progress.value },
                            modifier = Modifier.fillMaxSize(),
                            color = CandyPink,
                            strokeWidth = 8.dp
                        )
                        Text(
                            text = stringResource(R.string.parental_gate_hold),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CandyDeepPlum,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else {
                    val q = currentQuestion
                    if (q != null) {
                        Text(
                            text = q.questionText,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CandyPink
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
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
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CandyActionPurple,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                ) {
                                    Text(
                                        text = option.toString(),
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (errorFeedback) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = stringResource(R.string.parental_gate_wrong),
                                color = Color.Red,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                NavButton(
                    icon = Icons.Default.Close,
                    label = stringResource(R.string.btn_cancel),
                    onClick = onDismiss,
                    containerColor = Color(0xFFE0E0E0),
                    contentColor = CandyDeepPlum,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Unlock locked item dialog supporting Candy purchase or free Rewarded Ad (RULE-042, RULE-043, RULE-044).
 */
@Composable
fun UnlockItemDialog(
    item: Item,
    language: String,
    candyBalance: Int,
    onUnlockWithCandies: () -> Unit,
    onUnlockWithAd: () -> Unit,
    onDismiss: () -> Unit
) {
    val canAfford = candyBalance >= item.candyPrice

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .widthIn(max = 400.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✨ " + stringResource(R.string.unlock_item_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = CandyDeepPlum
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(CandyCream)
                        .border(2.dp, CandyPastelPink, RoundedCornerShape(20.dp))
                ) {
                    ItemThumbnailCanvas(item = item, modifier = Modifier.size(68.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = item.localizedName(language),
                    style = MaterialTheme.typography.titleMedium,
                    color = CandyDeepPlum,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (!canAfford) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.unlock_not_enough_candy, item.candyPrice, candyBalance),
                        fontSize = 13.sp,
                        color = CandyPink,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                NavButton(
                    icon = Icons.Default.ShoppingBag,
                    label = stringResource(R.string.unlock_with_candies, item.candyPrice),
                    onClick = onUnlockWithCandies,
                    enabled = canAfford,
                    containerColor = CandyActionPink,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                NavButton(
                    icon = Icons.Default.PlayArrow,
                    label = stringResource(R.string.btn_watch_ad),
                    onClick = onUnlockWithAd,
                    containerColor = CandyMint,
                    contentColor = CandyDeepPlum,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.btn_cancel),
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Portrait Bottom Scrolling Item Tray (RULE-072 WYSIWYG thumbnails, RULE-073 scroll retention + non-overlapping).
 */
@Composable
fun BottomItemTray(
    categories: List<ItemCategory>,
    items: List<Item>,
    language: String,
    unlockedItems: Set<String>,
    selectedIdsForCategory: (String) -> Set<String>,
    onItemSelected: (Item) -> Unit,
    onLockedItemClicked: (Item) -> Unit,
    modifier: Modifier = Modifier,
    onClearCategory: ((ItemCategory) -> Unit)? = null,
    onOpenFullModal: ((ItemCategory) -> Unit)? = null
) {
    var selectedCategoryIndex by remember(categories.map { it.id }) { mutableIntStateOf(0) }
    val tabScrollIndices = remember { mutableStateMapOf<String, Int>() }
    val tabScrollOffsets = remember { mutableStateMapOf<String, Int>() }

    if (categories.isEmpty()) return
    val currentCategory = categories.getOrElse(selectedCategoryIndex) { categories.first() }
    val categoryItems = remember(currentCategory.id, items) {
        items.filter { it.category == currentCategory.id }
    }

    val lazyListState = rememberLazyListState()

    // Restore saved scroll index cleanly when switching category tabs, then persist subsequent scroll updates (RULE-073)
    LaunchedEffect(currentCategory.id) {
        val targetIdx = (tabScrollIndices[currentCategory.id] ?: 0).coerceIn(0, (categoryItems.size - 1).coerceAtLeast(0))
        val targetOff = tabScrollOffsets[currentCategory.id] ?: 0
        lazyListState.scrollToItem(targetIdx, targetOff)
        snapshotFlow { lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset }
            .collect { (idx, off) ->
                tabScrollIndices[currentCategory.id] = idx
                tabScrollOffsets[currentCategory.id] = off
            }
    }

    val activeIds = selectedIdsForCategory(currentCategory.id)

    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScrollableTabRow(
                selectedTabIndex = selectedCategoryIndex.coerceIn(0, categories.lastIndex),
                edgePadding = 12.dp,
                containerColor = CandyPastelPink.copy(alpha = 0.35f),
                contentColor = CandyDeepPlum,
                modifier = Modifier.height(44.dp)
            ) {
                categories.forEachIndexed { index, cat ->
                    val tabEmoji = when (cat.id) {
                        "hair_styles" -> "💇 "
                        "hair_colors" -> "🎨 "
                        "eyeshadows" -> "✨ "
                        "lipsticks" -> "💄 "
                        "blushes" -> "🌸 "
                        "face_paints" -> "🎭 "
                        "outfits" -> "👗 "
                        "wings" -> "🧚 "
                        "horns" -> "🦄 "
                        "tiaras" -> "👑 "
                        "jewelry" -> "💎 "
                        "stickers" -> "⭐ "
                        "cake_flavors" -> "🍰 "
                        "cake_toppings" -> "🍬 "
                        else -> "🎀 "
                    }
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        modifier = Modifier.height(44.dp),
                        text = {
                            Text(
                                text = tabEmoji + cat.localizedName(language),
                                fontSize = 13.sp,
                                fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            // Optional slot status / clear action bar (shows equipped count for multi-slots + quick clear/unequip)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val countText = if (currentCategory.maxSelected > 1) {
                    stringResource(R.string.multi_count_format, activeIds.size, currentCategory.maxSelected)
                } else {
                    currentCategory.localizedName(language)
                }
                Text(
                    text = countText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CandyDeepPlum.copy(alpha = 0.7f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onOpenFullModal != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CandyPastelPink.copy(alpha = 0.5f))
                                .clickable { onOpenFullModal(currentCategory) }
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = stringResource(R.string.btn_grid_modal),
                                tint = CandyDeepPlum,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = stringResource(R.string.btn_grid_modal),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CandyDeepPlum
                            )
                        }
                    }

                    if (onClearCategory != null && activeIds.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onClearCategory(currentCategory) }
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = stringResource(R.string.btn_clear),
                                tint = CandyPink,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = stringResource(R.string.btn_clear),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CandyPink
                            )
                        }
                    }
                }
            }

            LazyRow(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(categoryItems, key = { it.id }) { item ->
                    val isUnlocked = item.unlockedByDefault || unlockedItems.contains(item.id)
                    val isSelected = activeIds.contains(item.id)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(92.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                when {
                                    isSelected -> CandyYellow.copy(alpha = 0.95f)
                                    isUnlocked -> CandyCream
                                    else -> Color(0xFFEEEEEE)
                                }
                            )
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) CandyPink else CandyPurple.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable {
                                if (isUnlocked) {
                                    onItemSelected(item)
                                } else {
                                    onLockedItemClicked(item)
                                }
                            }
                            .padding(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(62.dp)
                        ) {
                            ItemThumbnailCanvas(item = item, modifier = Modifier.size(56.dp))

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(CandyPink),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            if (!isUnlocked) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.Black.copy(alpha = 0.48f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Locked",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "${item.candyPrice}🍬",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = item.localizedName(language),
                            fontSize = 10.sp,
                            lineHeight = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = CandyDeepPlum,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemSelectionModal(
    title: String,
    items: List<Item>,
    language: String,
    unlockedItems: Set<String>,
    selectedIds: Set<String>,
    onItemSelected: (Item) -> Unit,
    onLockedItemClicked: (Item) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 480.dp)
                .heightIn(min = 400.dp, max = 560.dp)
                .padding(horizontal = 10.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with soft title banner & close icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = CandyDeepPlum,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CandyPastelPink.copy(alpha = 0.5f))
                            .clickable { onDismiss() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.btn_close),
                            tint = CandyDeepPlum,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Generous 3-column Grid that properly displays all accessories and fills the modal!
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        val isUnlocked = item.unlockedByDefault || unlockedItems.contains(item.id)
                        val isSelected = selectedIds.contains(item.id)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    when {
                                        isSelected -> CandyYellow.copy(alpha = 0.95f)
                                        isUnlocked -> CandyCream
                                        else -> Color(0xFFF5F5F5)
                                    }
                                )
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) CandyPink else CandyPurple.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clickable {
                                    if (isUnlocked) {
                                        onItemSelected(item)
                                    } else {
                                        onLockedItemClicked(item)
                                    }
                                }
                                .padding(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(64.dp)
                            ) {
                                ItemThumbnailCanvas(item = item, modifier = Modifier.size(56.dp))

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(CandyPink),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                if (!isUnlocked) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color.Black.copy(alpha = 0.52f)),
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
                                                text = "${item.candyPrice}🍬",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.localizedName(language),
                                fontSize = 11.sp,
                                lineHeight = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = CandyDeepPlum,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                NavButton(
                    icon = Icons.Default.Check,
                    label = stringResource(R.string.btn_done),
                    onClick = onDismiss,
                    containerColor = CandyActionPink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }
        }
    }
}
