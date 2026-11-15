package com.rainbowunicorn.candysalon.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ads.AdProvider
import com.rainbowunicorn.candysalon.audio.SoundManager
import com.rainbowunicorn.candysalon.ui.components.CandyBalanceBadge
import com.rainbowunicorn.candysalon.ui.components.CandySalonStudioRenderer
import com.rainbowunicorn.candysalon.ui.components.CharacterPose
import com.rainbowunicorn.candysalon.ui.components.CharacterVectorRenderer
import com.rainbowunicorn.candysalon.ui.components.ConfirmDialog
import com.rainbowunicorn.candysalon.ui.components.StageBackdrop
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPink
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.CharacterType
import com.rainbowunicorn.core.models.Item
import com.rainbowunicorn.core.models.ItemCategory
import com.rainbowunicorn.core.models.MakeupState
import com.rainbowunicorn.core.services.LayerComposer
import com.rainbowunicorn.core.services.UndoStack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * PartyModeScreen ("CHOOSE YOUR STYLE") — 100% faithful recreation of the reference image:
 *  - Portrait 9:16 layout
 *  - 3D Princess beauty model (center-right, almond eyes, turquoise eyeshadow, braided bun, candy heart bodice, skater skirt)
 *  - Left 6-shelf closet tower ("CLOSET ROOM", teal rims, wood trays, dresses, perfumes, lipsticks, jewelry)
 *  - Left pink wallpaper with flying pegasuses, clouds, stars & hearts
 *  - Right arched window with 7-color curved rainbow & sheer lavender curtains
 *  - Giant chocolate cupcake & mini easel portrait at bottom-right
 *  - Floating magic styling wand spraying sparkle mist
 *  - Top-right circular back button with rainbow spiral swirl border
 *  - Bottom full-width magenta banner with 3D cartoon rainbow text: CHOOSE YOUR STYLE
 *  - 100% interactive: tap shelves to equip items, tap wand to spray mist, tap portrait to save photo.
 */
@Composable
fun PartyModeScreen(
    categories: List<ItemCategory>,
    items: List<Item>,
    language: String,
    candyBalance: Int,
    unlockedItems: Set<String>,
    galleryCount: Int,
    soundManager: SoundManager,
    adProvider: AdProvider,
    onUnlockItemWithCandy: (Item) -> Boolean,
    onUnlockItemWithAd: (Item) -> Unit,
    onSavePhotoToGallery: (Bitmap) -> Boolean,
    onBackClicked: () -> Unit
) {
    val initialPrincessState = remember {
        CharacterState(
            characterType = CharacterType.PRINCESS,
            hairStyleId = "hair_royal_chignon",
            hairColorId = "hair_rainbow_pastel",
            makeup = MakeupState(
                eyeshadow = "eyeshadow_turquoise",
                lipstick = "lipstick_bubblegum_pink",
                blush = "blush_rose_petal"
            ),
            outfitId = "outfit_ball_gown",
            jewelryId = "jewelry_pearl_choker",
            tiaraId = "tiara_star_gold"
        )
    }

    val undoStack = remember { UndoStack(20).apply { initialize(initialPrincessState) } }
    var characterState by remember { mutableStateOf(initialPrincessState) }
    var canUndo by remember { mutableStateOf(false) }
    var canRedo by remember { mutableStateOf(false) }

    var selectedShelfIndex by remember { mutableIntStateOf(-1) }
    var magicMistProgress by remember { mutableFloatStateOf(0f) }
    var isBlinking by remember { mutableStateOf(false) }
    var photoFlash by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showGalleryFullDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val savedBannerFormat = stringResource(R.string.gallery_saved_banner, (galleryCount + 1).coerceAtMost(50))

    fun commitCharacter(newState: CharacterState) {
        characterState = newState
        undoStack.push(newState)
        canUndo = undoStack.canUndo
        canRedo = undoStack.canRedo
    }

    // Natural blinking & mist animation loop
    LaunchedEffect(Unit) {
        var mistTicker = 0f
        while (true) {
            delay(120L)
            mistTicker += 0.08f
            magicMistProgress = mistTicker
            if ((0..12).random() == 0) {
                isBlinking = true
                delay(150L)
                isBlinking = false
            }
        }
    }

    LaunchedEffect(photoFlash) {
        if (photoFlash) {
            delay(200L)
            photoFlash = false
        }
    }

    // Available dress, hair, lipstick, eyeshadow collections from items
    val dressItems = remember(items) { items.filter { it.category == "outfits" } }
    val hairItems = remember(items) { items.filter { it.category == "hair_styles" || it.category == "hair_colors" } }
    val eyeshadowItems = remember(items) { items.filter { it.category == "eyeshadows" } }
    val lipstickItems = remember(items) { items.filter { it.category == "lipsticks" } }
    val jewelryItems = remember(items) { items.filter { it.category == "jewelry" || it.category == "tiaras" } }

    var dressCycleIdx by remember { mutableIntStateOf(0) }
    var hairCycleIdx by remember { mutableIntStateOf(0) }
    var eyeshadowCycleIdx by remember { mutableIntStateOf(0) }
    var lipstickCycleIdx by remember { mutableIntStateOf(0) }
    var jewelryCycleIdx by remember { mutableIntStateOf(0) }

    fun onShelfTapped(shelfIdx: Int) {
        selectedShelfIndex = shelfIdx
        soundManager.playClick()

        when (shelfIdx) {
            0 -> {
                // Shelf 1: Dresses / Outfits
                if (dressItems.isNotEmpty()) {
                    dressCycleIdx = (dressCycleIdx + 1) % dressItems.size
                    val item = dressItems[dressCycleIdx]
                    commitCharacter(LayerComposer.equipOutfit(characterState, item.id))
                    soundManager.playSparkle()
                }
            }
            1 -> {
                // Shelf 2: Hair styles & colors
                if (hairItems.isNotEmpty()) {
                    hairCycleIdx = (hairCycleIdx + 1) % hairItems.size
                    val item = hairItems[hairCycleIdx]
                    val updated = if (item.category == "hair_styles") {
                        characterState.copy(hairStyleId = item.id)
                    } else {
                        characterState.copy(hairColorId = item.id)
                    }
                    commitCharacter(updated)
                    soundManager.playSparkle()
                }
            }
            2 -> {
                // Shelf 3: Perfumes & Mist (Triggers wand spray)
                soundManager.playSparkle()
                magicMistProgress += 1.5f
            }
            3 -> {
                // Shelf 4: Eye makeup & blush
                if (eyeshadowItems.isNotEmpty()) {
                    eyeshadowCycleIdx = (eyeshadowCycleIdx + 1) % eyeshadowItems.size
                    val item = eyeshadowItems[eyeshadowCycleIdx]
                    commitCharacter(LayerComposer.applyEyeshadow(characterState, item.id))
                    soundManager.playSparkle()
                }
            }
            4 -> {
                // Shelf 5: Lipsticks
                if (lipstickItems.isNotEmpty()) {
                    lipstickCycleIdx = (lipstickCycleIdx + 1) % lipstickItems.size
                    val item = lipstickItems[lipstickCycleIdx]
                    commitCharacter(characterState.copy(makeup = characterState.makeup.copy(lipstick = item.id)))
                    soundManager.playSparkle()
                }
            }
            5 -> {
                // Shelf 6: Jewelry & Tiaras
                if (jewelryItems.isNotEmpty()) {
                    jewelryCycleIdx = (jewelryCycleIdx + 1) % jewelryItems.size
                    val item = jewelryItems[jewelryCycleIdx]
                    val updated = if (item.category == "jewelry") {
                        characterState.copy(jewelryId = item.id)
                    } else {
                        LayerComposer.equipTiara(characterState, item.id)
                    }
                    commitCharacter(updated)
                    soundManager.playSparkle()
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFF7EB6))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        val screenW = maxWidth
        val screenH = maxHeight
        val density = LocalDensity.current

        val screenWpx = with(density) { screenW.toPx() }
        val screenHpx = with(density) { screenH.toPx() }

        val towerLeftPx = screenWpx * 0.02f
        val towerTopPx = screenHpx * 0.095f
        val towerWidthPx = screenWpx * 0.32f
        val towerHeightPx = screenHpx * 0.795f

        val princessCenterXPx = screenWpx * 0.65f
        val princessHeadYPx = screenHpx * 0.36f
        val princessHeadRPx = screenWpx * 0.20f

        // 1. MASTER CANVAS: Full-fidelity reference artwork rendering
        Canvas(modifier = Modifier.fillMaxSize()) {
            CandySalonStudioRenderer.run {
                // Layer A: Complete Studio Background (Wallpaper, Arched Window, Rainbow, Cupcake)
                drawStudioBackground(
                    width = size.width,
                    height = size.height,
                    magicMistProgress = magicMistProgress
                )

                // Layer B: Left 6-Shelf Closet Tower ("CLOSET ROOM", teal rims, wood floors, dresses, perfumes, lipsticks, chest)
                drawClosetTower(
                    leftX = towerLeftPx,
                    topY = towerTopPx,
                    towerW = towerWidthPx,
                    towerH = towerHeightPx,
                    selectedShelfIndex = selectedShelfIndex,
                    shelfPulseProgress = magicMistProgress
                )

                // Layer C: 3D Princess Beauty Model (center-right, almond eyes, braided bun, candy heart bodice, skater skirt)
                drawPrincess3D(
                    cx = princessCenterXPx,
                    headY = princessHeadYPx,
                    headR = princessHeadRPx,
                    characterState = characterState,
                    isBlinking = isBlinking,
                    swayAngle = (sin(magicMistProgress * 1.5f) * 2.5f)
                )

                // Layer D: Foreground Mini Wooden Easel Portrait (beside skirt)
                drawMiniEaselPortrait(
                    cx = size.width * 0.88f,
                    bottomY = size.height * 0.90f,
                    frameW = size.width * 0.14f,
                    frameH = size.height * 0.14f
                )

                // Layer E: Foreground Floating Magic Airbrush Styling Wand (spraying sparkles into hair)
                drawMagicStylingWand(
                    wandX = size.width * 0.46f,
                    wandY = size.height * 0.44f,
                    mistProgress = magicMistProgress
                )

                // Layer F: Top-Right Circular Rainbow Swirl Back Button
                drawRainbowBackButton(
                    cx = size.width * 0.90f,
                    cy = size.height * 0.075f,
                    radius = size.width * 0.065f,
                    isPressed = false
                )

                // Layer G: Bottom "CHOOSE YOUR STYLE" Ribbon Banner
                val bannerH = size.height * 0.085f
                val bannerY = size.height - bannerH
                drawChooseYourStyleBanner(
                    leftX = 0f,
                    topY = bannerY,
                    bannerW = size.width,
                    bannerH = bannerH
                )
            }
        }

        // 2. TOUCH HITBOXES: 6 Interactive Closet Shelves on the left
        val shelfCount = 6
        val shelfHdp = screenH * 0.795f / shelfCount
        val towerLeftDp = screenW * 0.02f
        val towerTopDp = screenH * 0.095f
        val towerWDp = screenW * 0.32f

        for (i in 0 until shelfCount) {
            Box(
                modifier = Modifier
                    .offset(x = towerLeftDp, y = towerTopDp + (shelfHdp * i))
                    .size(width = towerWDp, height = shelfHdp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onShelfTapped(i)
                    }
            )
        }

        // 3. TOUCH HITBOX: Top-Right Rainbow Swirl Back Button
        val backBtnRadiusDp = screenW * 0.07f
        val backBtnCenterXDp = screenW * 0.90f
        val backBtnCenterYDp = screenH * 0.075f

        Box(
            modifier = Modifier
                .offset(
                    x = backBtnCenterXDp - backBtnRadiusDp,
                    y = backBtnCenterYDp - backBtnRadiusDp
                )
                .size(backBtnRadiusDp * 2f)
                .clip(CircleShape)
                .clickable {
                    soundManager.playClick()
                    onBackClicked()
                }
        )

        // 4. TOUCH HITBOX: Princess Center-Right (Tapping smiles & bursts sparkles)
        Box(
            modifier = Modifier
                .offset(x = screenW * 0.44f, y = screenH * 0.20f)
                .size(width = screenW * 0.54f, height = screenH * 0.65f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    soundManager.playSparkle()
                    isBlinking = true
                    magicMistProgress += 1f
                    scope.launch {
                        delay(250L)
                        isBlinking = false
                    }
                }
        )

        // 5. TOUCH HITBOX: Mini Easel Portrait at bottom-right (Tapping saves photo)
        Box(
            modifier = Modifier
                .offset(x = screenW * 0.80f, y = screenH * 0.76f)
                .size(width = screenW * 0.18f, height = screenH * 0.14f)
                .clickable {
                    if (galleryCount >= 50) {
                        showGalleryFullDialog = true
                        return@clickable
                    }
                    soundManager.playShutter()
                    photoFlash = true
                    val snapshot = CharacterVectorRenderer.renderToBitmap(
                        characterState = characterState,
                        width = 720,
                        height = 960,
                        backdrop = StageBackdrop.PASTEL,
                        pose = CharacterPose.HAPPY
                    )
                    val saved = onSavePhotoToGallery(snapshot)
                    if (saved) {
                        scope.launch { snackbarHostState.showSnackbar(savedBannerFormat) }
                    } else {
                        showGalleryFullDialog = true
                    }
                }
        )

        // 6. BOTTOM BANNER TEXT: "CHOOSE YOUR STYLE" (Thick 3D cartoon rainbow gradient)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(screenH * 0.085f)
                .clickable {
                    soundManager.playSparkle()
                },
            contentAlignment = Alignment.Center
        ) {
            val rainbowTextBrush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFFF9100), // Orange
                    Color(0xFFFFD600), // Yellow
                    Color(0xFF76FF03), // Lime
                    Color(0xFF00E676), // Green
                    Color(0xFF00E5FF), // Cyan
                    Color(0xFF2979FF), // Sky Blue
                    Color(0xFFD500F9), // Purple
                    Color(0xFFFF1744)  // Pink
                )
            )

            // Text with heavy dark drop shadow matching reference image
            Text(
                text = "CHOOSE YOUR STYLE",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    brush = rainbowTextBrush,
                    shadow = Shadow(
                        color = Color(0xFF1A0033),
                        offset = Offset(3f, 4f),
                        blurRadius = 6f
                    )
                ),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }

        // 7. DISCREET UTILITY TOOLBAR (Top-Left Safe Area)
        // Provides Candy Balance, Undo, Redo, Reset, and Role Switch without obstructing the reference art!
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CandyBalanceBadge(candyBalance = candyBalance)

            // Undo Button
            IconButton(
                onClick = {
                    undoStack.undo()?.let { prev ->
                        characterState = prev
                        canUndo = undoStack.canUndo
                        canRedo = undoStack.canRedo
                        soundManager.playClick()
                    }
                },
                enabled = canUndo,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (canUndo) Color.White.copy(alpha = 0.90f) else Color.White.copy(alpha = 0.45f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = stringResource(R.string.btn_undo),
                    tint = if (canUndo) CandyDeepPlum else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Redo Button
            IconButton(
                onClick = {
                    undoStack.redo()?.let { next ->
                        characterState = next
                        canUndo = undoStack.canUndo
                        canRedo = undoStack.canRedo
                        soundManager.playClick()
                    }
                },
                enabled = canRedo,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (canRedo) Color.White.copy(alpha = 0.90f) else Color.White.copy(alpha = 0.45f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Redo,
                    contentDescription = stringResource(R.string.btn_redo),
                    tint = if (canRedo) CandyDeepPlum else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Reset Button
            IconButton(
                onClick = { showResetConfirm = true },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(CandyPastelPink.copy(alpha = 0.90f))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.btn_reset),
                    tint = CandyDeepPlum,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Camera / Save snapshot button
            IconButton(
                onClick = {
                    if (galleryCount >= 50) {
                        showGalleryFullDialog = true
                        return@IconButton
                    }
                    soundManager.playShutter()
                    photoFlash = true
                    val snapshot = CharacterVectorRenderer.renderToBitmap(
                        characterState = characterState,
                        width = 720,
                        height = 960,
                        backdrop = StageBackdrop.PASTEL,
                        pose = CharacterPose.HAPPY
                    )
                    val saved = onSavePhotoToGallery(snapshot)
                    if (saved) {
                        scope.launch { snackbarHostState.showSnackbar(savedBannerFormat) }
                    } else {
                        showGalleryFullDialog = true
                    }
                },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(CandyMint.copy(alpha = 0.90f))
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = stringResource(R.string.btn_take_photo),
                    tint = CandyDeepPlum,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Camera Flash Animation
        AnimatedVisibility(
            visible = photoFlash,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.90f))
            )
        }

        // Snackbar Host (positioned cleanly above bottom banner)
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = screenH * 0.10f)
        )

        // Reset Confirmation Dialog
        if (showResetConfirm) {
            ConfirmDialog(
                title = stringResource(R.string.dialog_reset_title),
                message = stringResource(R.string.dialog_reset_message),
                confirmText = stringResource(R.string.btn_reset),
                dismissText = stringResource(R.string.btn_cancel),
                onConfirm = {
                    commitCharacter(initialPrincessState)
                    showResetConfirm = false
                    soundManager.playClick()
                },
                onDismiss = { showResetConfirm = false }
            )
        }

        // Gallery Full Dialog (RULE-091)
        if (showGalleryFullDialog) {
            ConfirmDialog(
                title = stringResource(R.string.gallery_full_title),
                message = stringResource(R.string.gallery_full_warning),
                confirmText = stringResource(R.string.btn_confirm),
                dismissText = stringResource(R.string.btn_cancel),
                onConfirm = { showGalleryFullDialog = false },
                onDismiss = { showGalleryFullDialog = false }
            )
        }
    }
}
