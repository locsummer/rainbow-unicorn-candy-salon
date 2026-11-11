package com.rainbowunicorn.candysalon.ui.screens

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ads.AdProvider
import com.rainbowunicorn.candysalon.audio.SoundManager
import com.rainbowunicorn.candysalon.ui.components.AnimatedCharacterStage
import com.rainbowunicorn.candysalon.ui.components.BottomItemTray
import com.rainbowunicorn.candysalon.ui.components.CandyBalanceBadge
import com.rainbowunicorn.candysalon.ui.components.CharacterPose
import com.rainbowunicorn.candysalon.ui.components.CharacterVectorRenderer
import com.rainbowunicorn.candysalon.ui.components.ConfirmDialog
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.components.StageBackdrop
import com.rainbowunicorn.candysalon.ui.components.UnlockItemDialog
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPink
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.CharacterType
import com.rainbowunicorn.core.models.Item
import com.rainbowunicorn.core.models.ItemCategory
import com.rainbowunicorn.core.services.LayerComposer
import com.rainbowunicorn.core.services.UndoStack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val partyCategories = remember(categories) {
        categories.filter { it.id !in listOf("cake_flavors", "cake_toppings") }
    }

    val undoStack = remember { UndoStack(20).apply { initialize(CharacterState(characterType = CharacterType.UNICORN)) } }
    var characterState by remember { mutableStateOf(CharacterState(characterType = CharacterType.UNICORN)) }
    var canUndo by remember { mutableStateOf(false) }
    var canRedo by remember { mutableStateOf(false) }

    var backdrop by remember { mutableStateOf(StageBackdrop.PASTEL) }
    var photoFlash by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showGalleryFullDialog by remember { mutableStateOf(false) }
    var lockedItemPending by remember { mutableStateOf<Item?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val stickerLimitText = stringResource(R.string.sticker_limit_banner)
    val savedBannerFormat = stringResource(R.string.gallery_saved_banner, (galleryCount + 1).coerceAtMost(50))

    fun commitCharacter(newState: CharacterState) {
        characterState = newState
        undoStack.push(newState)
        canUndo = undoStack.canUndo
        canRedo = undoStack.canRedo
    }

    fun equipItem(item: Item) {
        if (item.category == "stickers" && characterState.stickers.size >= LayerComposer.MAX_STICKERS) {
            scope.launch { snackbarHostState.showSnackbar(stickerLimitText) }
            return
        }
        val updated = when (item.category) {
            "hair_styles" -> characterState.copy(hairStyleId = item.id)
            "hair_colors" -> characterState.copy(hairColorId = item.id)
            "eyeshadows" -> LayerComposer.applyEyeshadow(characterState, item.id)
            "lipsticks" -> characterState.copy(makeup = characterState.makeup.copy(lipstick = item.id))
            "blushes" -> characterState.copy(makeup = characterState.makeup.copy(blush = item.id))
            "face_paints" -> LayerComposer.toggleFacePaint(characterState, item.id)
            "outfits" -> LayerComposer.equipOutfit(characterState, item.id)
            "wings" -> LayerComposer.equipWings(characterState, item.id)
            "horns" -> LayerComposer.equipHorn(characterState, item.id)
            "tiaras" -> LayerComposer.equipTiara(characterState, item.id)
            "jewelry" -> characterState.copy(jewelryId = item.id)
            "stickers" -> LayerComposer.addSticker(characterState, item.id).first
            else -> characterState
        }
        commitCharacter(updated)
    }

    LaunchedEffect(photoFlash) {
        if (photoFlash) {
            delay(180L)
            photoFlash = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CandyPastelPink.copy(alpha = 0.25f))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Portrait Top HUD Row 1: Exit | Candy Badge | Snap Photo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NavButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    label = stringResource(R.string.btn_exit),
                    onClick = onBackClicked,
                    containerColor = CandyActionPink,
                    compact = true
                )

                CandyBalanceBadge(candyBalance = candyBalance)

                Spacer(modifier = Modifier.weight(1f))

                NavButton(
                    icon = Icons.Default.CameraAlt,
                    label = stringResource(R.string.btn_take_photo),
                    onClick = {
                        if (galleryCount >= 50) {
                            showGalleryFullDialog = true
                            return@NavButton
                        }
                        soundManager.playShutter()
                        photoFlash = true
                        val snapshot = CharacterVectorRenderer.renderToBitmap(
                            characterState = characterState,
                            width = 720,
                            height = 960,
                            backdrop = backdrop,
                            pose = CharacterPose.HAPPY
                        )
                        val saved = onSavePhotoToGallery(snapshot)
                        if (saved) {
                            scope.launch { snackbarHostState.showSnackbar(savedBannerFormat) }
                        } else {
                            showGalleryFullDialog = true
                        }
                    },
                    containerColor = CandyMint,
                    contentColor = CandyDeepPlum,
                    compact = true
                )
            }

            // Portrait Top HUD Row 2: Role Switch | Backdrop
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val roleLabel = if (characterState.characterType == CharacterType.UNICORN) {
                    stringResource(R.string.switch_to_princess)
                } else {
                    stringResource(R.string.switch_to_unicorn)
                }
                NavButton(
                    icon = Icons.Default.SwapHoriz,
                    label = roleLabel,
                    onClick = {
                        val nextType = if (characterState.characterType == CharacterType.UNICORN) {
                            CharacterType.PRINCESS
                        } else {
                            CharacterType.UNICORN
                        }
                        commitCharacter(characterState.copy(characterType = nextType))
                        soundManager.playClick()
                    },
                    containerColor = CandyActionPurple,
                    compact = true,
                    modifier = Modifier.weight(1f)
                )

                NavButton(
                    icon = Icons.Default.Palette,
                    label = stringResource(R.string.btn_backdrop),
                    onClick = {
                        backdrop = StageBackdrop.entries[(backdrop.ordinal + 1) % StageBackdrop.entries.size]
                        soundManager.playClick()
                    },
                    containerColor = CandyYellow,
                    contentColor = CandyDeepPlum,
                    compact = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Portrait Top HUD Row 3: Undo | Redo | Reset
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NavButton(
                    icon = Icons.AutoMirrored.Filled.Undo,
                    label = stringResource(R.string.btn_undo),
                    onClick = {
                        undoStack.undo()?.let { prev ->
                            characterState = prev
                            canUndo = undoStack.canUndo
                            canRedo = undoStack.canRedo
                            soundManager.playClick()
                        }
                    },
                    enabled = canUndo,
                    containerColor = Color.White,
                    contentColor = CandyDeepPlum,
                    compact = true,
                    modifier = Modifier.weight(1f)
                )

                NavButton(
                    icon = Icons.AutoMirrored.Filled.Redo,
                    label = stringResource(R.string.btn_redo),
                    onClick = {
                        undoStack.redo()?.let { next ->
                            characterState = next
                            canUndo = undoStack.canUndo
                            canRedo = undoStack.canRedo
                            soundManager.playClick()
                        }
                    },
                    enabled = canRedo,
                    containerColor = Color.White,
                    contentColor = CandyDeepPlum,
                    compact = true,
                    modifier = Modifier.weight(1f)
                )

                NavButton(
                    icon = Icons.Default.Refresh,
                    label = stringResource(R.string.btn_reset),
                    onClick = { showResetConfirm = true },
                    containerColor = CandyPastelPink,
                    contentColor = CandyDeepPlum,
                    compact = true,
                    modifier = Modifier.weight(1f)
                )
            }

            // Central Portrait Stage
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedCharacterStage(
                    characterState = characterState,
                    backdrop = backdrop,
                    pose = CharacterPose.IDLE,
                    drawBackdrop = true,
                    modifier = Modifier.fillMaxSize()
                )

                androidx.compose.animation.AnimatedVisibility(
                    visible = photoFlash,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.85f))
                    )
                }
            }

            // Bottom Tray (Free customization across all dressup/hair/makeup categories)
            BottomItemTray(
                categories = partyCategories,
                items = items,
                language = language,
                unlockedItems = unlockedItems,
                selectedIdsForCategory = { catId ->
                    when (catId) {
                        "hair_styles" -> setOfNotNull(characterState.hairStyleId)
                        "hair_colors" -> setOfNotNull(characterState.hairColorId)
                        "eyeshadows" -> setOfNotNull(characterState.makeup.eyeshadow)
                        "lipsticks" -> setOfNotNull(characterState.makeup.lipstick)
                        "blushes" -> setOfNotNull(characterState.makeup.blush)
                        "face_paints" -> characterState.facePaints.toSet()
                        "outfits" -> setOfNotNull(characterState.outfitId)
                        "wings" -> setOfNotNull(characterState.wingsId)
                        "horns" -> setOfNotNull(characterState.hornId)
                        "tiaras" -> setOfNotNull(characterState.tiaraId)
                        "jewelry" -> setOfNotNull(characterState.jewelryId)
                        "stickers" -> characterState.stickers.toSet()
                        else -> emptySet()
                    }
                },
                onItemSelected = { item ->
                    soundManager.playClick()
                    equipItem(item)
                },
                onLockedItemClicked = { item ->
                    soundManager.playClick()
                    lockedItemPending = item
                },
                onClearCategory = { cat ->
                    soundManager.playClick()
                    val cleared = when (cat.id) {
                        "hair_styles" -> characterState.copy(hairStyleId = null)
                        "hair_colors" -> characterState.copy(hairColorId = null)
                        "eyeshadows" -> characterState.copy(makeup = characterState.makeup.copy(eyeshadow = null))
                        "lipsticks" -> characterState.copy(makeup = characterState.makeup.copy(lipstick = null))
                        "blushes" -> characterState.copy(makeup = characterState.makeup.copy(blush = null))
                        "face_paints" -> characterState.copy(facePaints = emptyList())
                        "outfits" -> characterState.copy(outfitId = null)
                        "wings" -> characterState.copy(wingsId = null)
                        "horns" -> characterState.copy(hornId = null)
                        "tiaras" -> characterState.copy(tiaraId = null)
                        "jewelry" -> characterState.copy(jewelryId = null)
                        "stickers" -> characterState.copy(stickers = emptyList())
                        else -> characterState
                    }
                    commitCharacter(cleared)
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 190.dp)
        )

        lockedItemPending?.let { item ->
            UnlockItemDialog(
                item = item,
                language = language,
                candyBalance = candyBalance,
                onUnlockWithCandies = {
                    if (onUnlockItemWithCandy(item)) {
                        soundManager.playSparkle()
                        equipItem(item)
                        lockedItemPending = null
                    }
                },
                onUnlockWithAd = {
                    adProvider.showRewarded { rewarded ->
                        if (rewarded) {
                            soundManager.playSparkle()
                            onUnlockItemWithAd(item)
                            equipItem(item)
                        }
                        lockedItemPending = null
                    }
                },
                onDismiss = { lockedItemPending = null }
            )
        }

        if (showResetConfirm) {
            ConfirmDialog(
                title = stringResource(R.string.dialog_reset_title),
                message = stringResource(R.string.dialog_reset_message),
                onConfirm = {
                    showResetConfirm = false
                    commitCharacter(CharacterState(characterType = characterState.characterType))
                    soundManager.playClick()
                },
                onDismiss = { showResetConfirm = false }
            )
        }

        if (showGalleryFullDialog) {
            Dialog(
                onDismissRequest = { showGalleryFullDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
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
                            text = "⚠️ " + stringResource(R.string.gallery_full_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = CandyPink,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(R.string.gallery_full_warning),
                            style = MaterialTheme.typography.bodyLarge,
                            color = CandyDeepPlum,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        NavButton(
                            icon = Icons.Default.Check,
                            label = stringResource(R.string.btn_close),
                            onClick = { showGalleryFullDialog = false },
                            containerColor = CandyActionPink,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
