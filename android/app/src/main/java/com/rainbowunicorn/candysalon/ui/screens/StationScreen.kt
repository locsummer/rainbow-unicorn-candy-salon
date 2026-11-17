package com.rainbowunicorn.candysalon.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.ads.AdProvider
import com.rainbowunicorn.candysalon.audio.SoundManager
import com.rainbowunicorn.candysalon.ui.components.AnimatedCharacterStage
import com.rainbowunicorn.candysalon.ui.components.BottomItemTray
import com.rainbowunicorn.candysalon.ui.components.CharacterPose
import com.rainbowunicorn.candysalon.ui.components.ConfirmDialog
import com.rainbowunicorn.candysalon.ui.components.ItemSelectionModal
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.components.StageBackdrop
import com.rainbowunicorn.candysalon.ui.components.UnlockItemDialog
import com.rainbowunicorn.candysalon.ui.stations.CakeStationView
import com.rainbowunicorn.candysalon.ui.stations.PetStationView
import com.rainbowunicorn.candysalon.ui.stations.SpaStationView
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPink
import com.rainbowunicorn.candysalon.ui.theme.CandyActionPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.Item
import com.rainbowunicorn.core.models.ItemCategory
import com.rainbowunicorn.core.models.Level
import com.rainbowunicorn.core.models.MakeupState
import com.rainbowunicorn.core.models.SavedLevelCheckpoint
import com.rainbowunicorn.core.models.StationOutput
import com.rainbowunicorn.core.models.StationStatus
import com.rainbowunicorn.core.models.StationType
import com.rainbowunicorn.core.services.LayerComposer
import com.rainbowunicorn.core.services.LevelEngine
import com.rainbowunicorn.core.services.ScoreEvaluation
import com.rainbowunicorn.core.services.ScoringEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StationScreen(
    level: Level,
    language: String,
    categories: List<ItemCategory>,
    items: List<Item>,
    candyBalance: Int,
    unlockedItems: Set<String>,
    soundManager: SoundManager,
    adProvider: AdProvider,
    levelEngine: LevelEngine,
    initialCheckpoint: SavedLevelCheckpoint? = null,
    onUnlockItemWithCandy: (Item) -> Boolean,
    onUnlockItemWithAd: (Item) -> Unit,
    onCheckpointChanged: (SavedLevelCheckpoint) -> Unit,
    onLevelFinished: (StationScreenResult) -> Unit,
    onExitConfirmed: (SavedLevelCheckpoint) -> Unit
) {
    val initialOutputs = remember(levelEngine) { levelEngine.getSessionState().stationOutputs }

    var characterState by remember(levelEngine) { mutableStateOf(levelEngine.characterState) }
    var currentStationIndex by remember(levelEngine) { mutableIntStateOf(levelEngine.currentStationIndex) }
    var canUndo by remember(levelEngine) { mutableStateOf(levelEngine.canUndo) }
    var canRedo by remember(levelEngine) { mutableStateOf(levelEngine.canRedo) }

    var showExitDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showRequestDialog by remember { mutableStateOf(false) }
    var showCakeToppingModal by remember { mutableStateOf(false) }
    var showCakeFlavorModal by remember { mutableStateOf(false) }
    var lockedItemPending by remember { mutableStateOf<Item?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Localized toast strings
    val spaNeedMoreText = stringResource(R.string.spa_need_more)
    val photoSavedText = stringResource(R.string.photo_saved_banner)
    val stickerLimitText = stringResource(R.string.sticker_limit_banner)

    // Station specific state initialized from existing outputs OR checkpoint
    var spaCleanPercent by remember(levelEngine) {
        mutableDoubleStateOf(
            initialOutputs[StationType.SPA]?.qualityScore
                ?: initialCheckpoint?.spaCleanPercent
                ?: 0.0
        )
    }
    var spaResetTrigger by remember(levelEngine) { mutableIntStateOf(0) }

    var cakeLayers by remember(levelEngine) {
        mutableIntStateOf(
            initialOutputs[StationType.CAKE]?.cakeLayers
                ?: initialCheckpoint?.cakeLayers
                ?: 2
        )
    }
    var cakeFlavor by remember(levelEngine) {
        mutableStateOf<String?>(
            initialOutputs[StationType.CAKE]?.cakeFlavor
                ?: initialCheckpoint?.cakeFlavor
                ?: "cake_flavor_vanilla_rainbow"
        )
    }
    val cakeToppings = remember(levelEngine) {
        mutableStateListOf<String>().apply {
            val savedToppings = initialOutputs[StationType.CAKE]?.cakeToppings
                ?: initialCheckpoint?.cakeToppings
                ?: emptyList()
            addAll(savedToppings)
        }
    }

    var petFed by remember(levelEngine) {
        mutableStateOf(initialOutputs[StationType.PET]?.petFed ?: initialCheckpoint?.petFed ?: false)
    }
    var petClean by remember(levelEngine) {
        mutableDoubleStateOf(initialOutputs[StationType.PET]?.petClean ?: initialCheckpoint?.petClean ?: 0.0)
    }
    var petGroomed by remember(levelEngine) {
        mutableStateOf(initialOutputs[StationType.PET]?.petGroomed ?: initialCheckpoint?.petGroomed ?: false)
    }

    var photoTaken by remember(levelEngine) {
        mutableStateOf(initialOutputs[StationType.PHOTO]?.photoTaken ?: initialCheckpoint?.photoTaken ?: false)
    }
    var photoBackdrop by remember { mutableStateOf(StageBackdrop.PASTEL) }
    var photoPose by remember { mutableStateOf(CharacterPose.HAPPY) }
    var photoFlash by remember { mutableStateOf(false) }

    val currentStation = level.stations.getOrElse(currentStationIndex) { level.stations.first() }

    fun syncUndoFlags() {
        canUndo = levelEngine.canUndo
        canRedo = levelEngine.canRedo
    }

    fun buildCheckpoint(): SavedLevelCheckpoint = levelEngine.toCheckpoint(
        spaCleanPercent = spaCleanPercent,
        cakeLayers = cakeLayers,
        cakeFlavor = cakeFlavor,
        cakeToppings = cakeToppings.toList(),
        petFed = petFed,
        petClean = petClean,
        petGroomed = petGroomed,
        photoTaken = photoTaken
    )

    LaunchedEffect(photoFlash) {
        if (photoFlash) {
            delay(180L)
            photoFlash = false
        }
    }

    fun buildOutputFor(station: StationType): StationOutput {
        return when (station) {
            StationType.SPA -> StationOutput(
                stationType = StationType.SPA,
                isCompleted = true,
                qualityScore = spaCleanPercent.coerceIn(0.0, 1.0)
            )
            StationType.CAKE -> {
                val tierScore = (cakeLayers.coerceIn(1, 4) / 4.0) * 0.4
                val flavorScore = if (cakeFlavor != null) 0.3 else 0.0
                val toppingScore = (cakeToppings.size.coerceAtMost(3) / 3.0) * 0.3
                StationOutput(
                    stationType = StationType.CAKE,
                    isCompleted = true,
                    qualityScore = (tierScore + flavorScore + toppingScore).coerceIn(0.2, 1.0),
                    cakeLayers = cakeLayers,
                    cakeFlavor = cakeFlavor,
                    cakeToppings = cakeToppings.toList()
                )
            }
            StationType.PET -> {
                val petScore = (if (petFed) 0.35 else 0.0) + (petClean * 0.35) + (if (petGroomed) 0.30 else 0.0)
                StationOutput(
                    stationType = StationType.PET,
                    isCompleted = true,
                    qualityScore = petScore.coerceIn(0.1, 1.0),
                    petFed = petFed,
                    petClean = petClean,
                    petGroomed = petGroomed
                )
            }
            StationType.PHOTO -> StationOutput(
                stationType = StationType.PHOTO,
                isCompleted = true,
                qualityScore = if (photoTaken) 1.0 else 0.6,
                photoTaken = photoTaken
            )
            else -> StationOutput(
                stationType = station,
                isCompleted = true,
                qualityScore = 1.0
            )
        }
    }

    fun equipItem(item: Item) {
        if (item.category == "cake_flavors") {
            cakeFlavor = if (cakeFlavor == item.id) null else item.id
            onCheckpointChanged(buildCheckpoint())
            return
        }
        if (item.category == "cake_toppings") {
            if (cakeToppings.contains(item.id)) {
                cakeToppings.remove(item.id)
            } else if (cakeToppings.size < 8) {
                cakeToppings.add(item.id)
            }
            onCheckpointChanged(buildCheckpoint())
            return
        }

        if (item.category == "stickers") {
            if (characterState.stickers.contains(item.id)) {
                levelEngine.updateCharacter { current ->
                    current.copy(stickers = current.stickers - item.id)
                }
                characterState = levelEngine.characterState
                syncUndoFlags()
                onCheckpointChanged(buildCheckpoint())
                return
            }
            if (characterState.stickers.size >= LayerComposer.MAX_STICKERS) {
                scope.launch { snackbarHostState.showSnackbar(stickerLimitText) }
                return
            }
        }

        levelEngine.updateCharacter { current ->
            when (item.category) {
                "hair_styles" -> current.copy(hairStyleId = if (current.hairStyleId == item.id) null else item.id)
                "hair_colors" -> current.copy(hairColorId = if (current.hairColorId == item.id) null else item.id)
                "eyeshadows" -> if (current.makeup.eyeshadow == item.id) {
                    current.copy(makeup = current.makeup.copy(eyeshadow = null))
                } else {
                    LayerComposer.applyEyeshadow(current, item.id)
                }
                "lipsticks" -> current.copy(
                    makeup = current.makeup.copy(
                        lipstick = if (current.makeup.lipstick == item.id) null else item.id
                    )
                )
                "blushes" -> current.copy(
                    makeup = current.makeup.copy(
                        blush = if (current.makeup.blush == item.id) null else item.id
                    )
                )
                "face_paints" -> LayerComposer.toggleFacePaint(current, item.id)
                "outfits" -> current.copy(outfitId = if (current.outfitId == item.id) null else item.id)
                "wings" -> current.copy(wingsId = if (current.wingsId == item.id) null else item.id)
                "horns" -> current.copy(hornId = if (current.hornId == item.id) null else item.id)
                "tiaras" -> current.copy(tiaraId = if (current.tiaraId == item.id) null else item.id)
                "jewelry" -> current.copy(jewelryId = if (current.jewelryId == item.id) null else item.id)
                "stickers" -> LayerComposer.addSticker(current, item.id).first
                else -> current
            }
        }
        characterState = levelEngine.characterState
        syncUndoFlags()
        onCheckpointChanged(buildCheckpoint())
    }

    // Live Outputs Snapshot so Customer Request Checklist reflects real-time progress across stations
    val liveOutputs = buildMap {
        putAll(levelEngine.getSessionState().stationOutputs)
        put(currentStation, buildOutputFor(currentStation))
    }
    val criteriaResults = level.criteria.associate { c ->
        c.id to ScoringEngine.evaluateCriterion(c, characterState, liveOutputs)
    }
    val metCount = criteriaResults.values.count { it }

    val currentCategories = remember(currentStation, categories) {
        categories.filter { cat ->
            when (currentStation) {
                StationType.HAIR -> cat.id in listOf("hair_styles", "hair_colors")
                StationType.MAKEUP -> cat.id in listOf("eyeshadows", "lipsticks", "blushes", "face_paints")
                StationType.DRESSUP -> cat.id in listOf("outfits", "wings", "horns", "tiaras", "jewelry", "stickers")
                StationType.CAKE -> cat.id in listOf("cake_flavors", "cake_toppings")
                else -> false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(CandyPastelPink.copy(alpha = 0.25f), Color.White)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // PORTRAIT TOP HUD ROW 1: Exit | Criteria Progress Pill | Done
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
                    onClick = { showExitDialog = true },
                    containerColor = CandyActionPink,
                    compact = true
                )

                // Interactive Customer Goals Pill (tap anytime to inspect live checklist!)
                Button(
                    onClick = { showRequestDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (metCount == level.criteria.size) CandyMint else Color.White,
                        contentColor = CandyDeepPlum
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.FactCheck,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(R.string.criteria_progress_format, metCount, level.criteria.size),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Complete Current Station Button (RULE-020)
                NavButton(
                    icon = Icons.Default.Check,
                    label = stringResource(R.string.btn_done),
                    onClick = {
                        if (currentStation == StationType.SPA && spaCleanPercent < 0.80) {
                            scope.launch { snackbarHostState.showSnackbar(spaNeedMoreText) }
                            return@NavButton
                        }

                        soundManager.playSparkle()
                        val output = buildOutputFor(currentStation)
                        levelEngine.completeCurrentStation(output)
                        onCheckpointChanged(buildCheckpoint())

                        val firstIncomplete = level.stations.indexOfFirst {
                            levelEngine.getStationStatus(it) != StationStatus.COMPLETED
                        }

                        if (firstIncomplete == -1) {
                            // All stations completed -> Evaluate level
                            val evaluation = levelEngine.finishLevel()
                            onLevelFinished(
                                StationScreenResult(
                                    level = level,
                                    characterState = characterState,
                                    evaluation = evaluation,
                                    engine = levelEngine
                                )
                            )
                        } else {
                            // Move to next incomplete station if we haven't already stepped there
                            val nextIdx = levelEngine.currentStationIndex
                            val targetIdx = if (levelEngine.getStationStatus(level.stations[nextIdx]) != StationStatus.COMPLETED) {
                                nextIdx
                            } else {
                                firstIncomplete
                            }
                            levelEngine.selectStation(targetIdx)
                            currentStationIndex = targetIdx
                        }
                    },
                    containerColor = CandyMint,
                    contentColor = CandyDeepPlum,
                    compact = true
                )
            }

            // PORTRAIT TOP HUD ROW 2: Dedicated Full-Width Scrollable Station Steps (Zero Truncation)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(level.stations) { idx, st ->
                    val status = levelEngine.getStationStatus(st)
                    val isCurrent = idx == currentStationIndex
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = when {
                            isCurrent -> CandyActionPurple
                            status == StationStatus.COMPLETED -> CandyMint.copy(alpha = 0.65f)
                            else -> Color.White.copy(alpha = 0.9f)
                        },
                        shadowElevation = if (isCurrent) 3.dp else 1.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) CandyDeepPlum else Color.LightGray.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                levelEngine.selectStation(idx)
                                currentStationIndex = idx
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            val prefix = if (status == StationStatus.COMPLETED) "✓ " else "${idx + 1}. "
                            Text(
                                text = prefix + stationLocalizedLabel(st),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) Color.White else CandyDeepPlum,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // PORTRAIT TOP HUD ROW 3: Station Prompt + Action Controls (Undo, Redo, Reset with >= 48dp touch targets)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val instructionText = when (currentStation) {
                    StationType.SPA -> stringResource(R.string.spa_instruction)
                    StationType.HAIR -> stringResource(R.string.hair_instruction)
                    StationType.MAKEUP -> stringResource(R.string.makeup_instruction)
                    StationType.DRESSUP -> stringResource(R.string.dressup_instruction)
                    StationType.CAKE -> stringResource(R.string.cake_instruction)
                    StationType.PET -> stringResource(R.string.pet_instruction)
                    StationType.PHOTO -> stringResource(R.string.photo_instruction)
                }
                Text(
                    text = instructionText,
                    fontSize = 11.sp,
                    color = CandyDeepPlum.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (currentStation == StationType.MAKEUP) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CandyPastelPink,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                soundManager.playClick()
                                levelEngine.updateCharacter { LayerComposer.clearMakeup(it) }
                                characterState = levelEngine.characterState
                                syncUndoFlags()
                                onCheckpointChanged(buildCheckpoint())
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Spa, contentDescription = null, tint = CandyDeepPlum, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = stringResource(R.string.makeup_remover),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CandyDeepPlum
                            )
                        }
                    }
                }

                // Dedicated Undo / Redo / Reset controls
                NavButton(
                    icon = Icons.AutoMirrored.Filled.Undo,
                    label = stringResource(R.string.btn_undo),
                    onClick = {
                        if (levelEngine.undo()) {
                            characterState = levelEngine.characterState
                            syncUndoFlags()
                            soundManager.playClick()
                            onCheckpointChanged(buildCheckpoint())
                        }
                    },
                    enabled = canUndo,
                    containerColor = Color.White,
                    contentColor = CandyDeepPlum,
                    compact = true
                )

                NavButton(
                    icon = Icons.AutoMirrored.Filled.Redo,
                    label = stringResource(R.string.btn_redo),
                    onClick = {
                        if (levelEngine.redo()) {
                            characterState = levelEngine.characterState
                            syncUndoFlags()
                            soundManager.playClick()
                            onCheckpointChanged(buildCheckpoint())
                        }
                    },
                    enabled = canRedo,
                    containerColor = Color.White,
                    contentColor = CandyDeepPlum,
                    compact = true
                )

                NavButton(
                    icon = Icons.Default.Refresh,
                    label = stringResource(R.string.btn_reset),
                    onClick = { showResetDialog = true },
                    containerColor = CandyYellow,
                    contentColor = CandyDeepPlum,
                    compact = true
                )
            }

            // CENTRAL PORTRAIT STAGE (Never overlapped by Top HUD or Bottom Tray!)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (currentStation) {
                    StationType.SPA -> {
                        SpaStationView(
                            characterState = characterState,
                            initialCleanPercent = spaCleanPercent,
                            resetTrigger = spaResetTrigger,
                            onCleanPercentChanged = { pct ->
                                spaCleanPercent = pct
                                soundManager.playBubble()
                            }
                        )
                    }
                    StationType.CAKE -> {
                        CakeStationView(
                            layers = cakeLayers,
                            flavor = cakeFlavor,
                            toppings = cakeToppings,
                            onLayersChanged = { newLayers ->
                                cakeLayers = newLayers
                                soundManager.playClick()
                                onCheckpointChanged(buildCheckpoint())
                            },
                            onClearToppings = {
                                cakeToppings.clear()
                                soundManager.playClick()
                                onCheckpointChanged(buildCheckpoint())
                            },
                            onRemoveTopping = { toppingId ->
                                cakeToppings.remove(toppingId)
                                soundManager.playClick()
                                onCheckpointChanged(buildCheckpoint())
                            },
                            onOpenAddModal = {
                                soundManager.playClick()
                                showCakeToppingModal = true
                            },
                            onOpenFlavorModal = {
                                soundManager.playClick()
                                showCakeFlavorModal = true
                            }
                        )
                    }
                    StationType.PET -> {
                        PetStationView(
                            petFed = petFed,
                            petClean = petClean,
                            petGroomed = petGroomed,
                            onFeedPet = {
                                petFed = true
                                soundManager.playSparkle()
                                onCheckpointChanged(buildCheckpoint())
                            },
                            onWashPetStep = {
                                petClean = (petClean + 0.34).coerceAtMost(1.0)
                                soundManager.playBubble()
                                onCheckpointChanged(buildCheckpoint())
                            },
                            onGroomPet = {
                                petGroomed = true
                                soundManager.playClick()
                                onCheckpointChanged(buildCheckpoint())
                            }
                        )
                    }
                    StationType.PHOTO -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .border(2.dp, CandyPastelPink, RoundedCornerShape(24.dp))
                            ) {
                                AnimatedCharacterStage(
                                    characterState = characterState,
                                    backdrop = photoBackdrop,
                                    pose = photoPose,
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

                            Spacer(modifier = Modifier.height(10.dp))

                            // Pose & Backdrop Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val backdropLabel = when (photoBackdrop) {
                                    StageBackdrop.PASTEL -> stringResource(R.string.backdrop_pastel)
                                    StageBackdrop.DISCO -> stringResource(R.string.backdrop_disco)
                                    StageBackdrop.FOREST -> stringResource(R.string.backdrop_forest)
                                }
                                Button(
                                    onClick = {
                                        val next = StageBackdrop.entries[(photoBackdrop.ordinal + 1) % StageBackdrop.entries.size]
                                        photoBackdrop = next
                                        soundManager.playClick()
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = CandyActionPurple,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                ) {
                                    Text(text = backdropLabel, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }

                                val poseLabel = when (photoPose) {
                                    CharacterPose.STAR -> stringResource(R.string.photo_pose_star)
                                    CharacterPose.WAVE -> stringResource(R.string.photo_pose_wave)
                                    else -> stringResource(R.string.photo_pose_happy)
                                }
                                Button(
                                    onClick = {
                                        val poses = listOf(CharacterPose.HAPPY, CharacterPose.STAR, CharacterPose.WAVE)
                                        val idx = (poses.indexOf(photoPose) + 1) % poses.size
                                        photoPose = poses[idx]
                                        soundManager.playClick()
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CandyYellow, contentColor = CandyDeepPlum),
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                ) {
                                    Text(text = poseLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            NavButton(
                                icon = Icons.Default.CameraAlt,
                                label = stringResource(R.string.btn_take_photo),
                                onClick = {
                                    photoTaken = true
                                    photoFlash = true
                                    soundManager.playShutter()
                                    onCheckpointChanged(buildCheckpoint())
                                    scope.launch { snackbarHostState.showSnackbar(photoSavedText) }
                                },
                                containerColor = CandyActionPink,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            )
                        }
                    }
                    else -> {
                        AnimatedCharacterStage(
                            characterState = characterState,
                            pose = CharacterPose.IDLE,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp)
                        )
                    }
                }
            }

            // BOTTOM TRAY (Fixed height, per-tab scroll retained, WYSIWYG previews)
            if (currentCategories.isNotEmpty()) {
                BottomItemTray(
                    categories = currentCategories,
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
                            "cake_flavors" -> setOfNotNull(cakeFlavor)
                            "cake_toppings" -> cakeToppings.toSet()
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
                        when (cat.id) {
                            "cake_toppings" -> {
                                cakeToppings.clear()
                                onCheckpointChanged(buildCheckpoint())
                            }
                            "cake_flavors" -> {
                                cakeFlavor = null
                                onCheckpointChanged(buildCheckpoint())
                            }
                            else -> {
                                levelEngine.updateCharacter { current ->
                                    when (cat.id) {
                                        "hair_styles" -> current.copy(hairStyleId = null)
                                        "hair_colors" -> current.copy(hairColorId = null)
                                        "eyeshadows" -> current.copy(makeup = current.makeup.copy(eyeshadow = null))
                                        "lipsticks" -> current.copy(makeup = current.makeup.copy(lipstick = null))
                                        "blushes" -> current.copy(makeup = current.makeup.copy(blush = null))
                                        "face_paints" -> current.copy(facePaints = emptyList())
                                        "outfits" -> current.copy(outfitId = null)
                                        "wings" -> current.copy(wingsId = null)
                                        "horns" -> current.copy(hornId = null)
                                        "tiaras" -> current.copy(tiaraId = null)
                                        "jewelry" -> current.copy(jewelryId = null)
                                        "stickers" -> current.copy(stickers = emptyList())
                                        else -> current
                                    }
                                }
                                characterState = levelEngine.characterState
                                syncUndoFlags()
                                onCheckpointChanged(buildCheckpoint())
                            }
                        }
                    }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (currentCategories.isNotEmpty()) 194.dp else 20.dp)
        )

        // Customer Request Live Checklist Dialog
        if (showRequestDialog) {
            Dialog(
                onDismissRequest = { showRequestDialog = false },
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
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "📋 " + stringResource(R.string.level_request_card),
                            style = MaterialTheme.typography.titleLarge,
                            color = CandyDeepPlum
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        level.criteria.forEach { criterion ->
                            val isMet = criteriaResults[criterion.id] == true
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isMet) CandyMint else Color.LightGray,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = criterion.localizedDescription(language),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isMet) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isMet) CandyDeepPlum else CandyDeepPlum.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        NavButton(
                            icon = Icons.Default.Close,
                            label = stringResource(R.string.btn_close),
                            onClick = { showRequestDialog = false },
                            containerColor = CandyActionPink,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Unlock Item Dialog (Buy with Candies OR Watch Rewarded Ad)
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
                    adProvider.showRewarded { earned ->
                        if (earned) {
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

        // Cake Station Item Selection Modals (Toppings & Flavors)
        if (showCakeToppingModal) {
            val toppingItems = remember(items) { items.filter { it.category == "cake_toppings" } }
            ItemSelectionModal(
                title = stringResource(R.string.title_select_topping),
                items = toppingItems,
                language = language,
                unlockedItems = unlockedItems,
                selectedIds = cakeToppings.toSet(),
                onItemSelected = { item ->
                    soundManager.playClick()
                    equipItem(item)
                },
                onLockedItemClicked = { item ->
                    soundManager.playClick()
                    lockedItemPending = item
                },
                onDismiss = { showCakeToppingModal = false }
            )
        }

        if (showCakeFlavorModal) {
            val flavorItems = remember(items) { items.filter { it.category == "cake_flavors" } }
            ItemSelectionModal(
                title = stringResource(R.string.title_select_flavor),
                items = flavorItems,
                language = language,
                unlockedItems = unlockedItems,
                selectedIds = setOfNotNull(cakeFlavor),
                onItemSelected = { item ->
                    soundManager.playClick()
                    equipItem(item)
                },
                onLockedItemClicked = { item ->
                    soundManager.playClick()
                    lockedItemPending = item
                },
                onDismiss = { showCakeFlavorModal = false }
            )
        }

        // Mid-Level Exit Dialog with Auto-Save (RULE-071)
        if (showExitDialog) {
            ConfirmDialog(
                title = stringResource(R.string.dialog_exit_title),
                message = stringResource(R.string.dialog_exit_message),
                onConfirm = {
                    showExitDialog = false
                    onExitConfirmed(buildCheckpoint())
                },
                onDismiss = { showExitDialog = false }
            )
        }

        // Reset Station Scope Dialog (RULE-055)
        if (showResetDialog) {
            ConfirmDialog(
                title = stringResource(R.string.dialog_reset_title),
                message = stringResource(R.string.dialog_reset_message),
                onConfirm = {
                    showResetDialog = false
                    characterState = levelEngine.resetCurrentStation()
                    when (currentStation) {
                        StationType.SPA -> {
                            spaCleanPercent = 0.0
                            spaResetTrigger++
                        }
                        StationType.CAKE -> {
                            cakeLayers = 2
                            cakeFlavor = "cake_flavor_vanilla_rainbow"
                            cakeToppings.clear()
                        }
                        StationType.PET -> {
                            petFed = false
                            petClean = 0.0
                            petGroomed = false
                        }
                        StationType.PHOTO -> {
                            photoTaken = false
                            photoPose = CharacterPose.HAPPY
                        }
                        else -> {}
                    }
                    syncUndoFlags()
                    soundManager.playClick()
                    onCheckpointChanged(buildCheckpoint())
                },
                onDismiss = { showResetDialog = false }
            )
        }
    }
}

data class StationScreenResult(
    val level: Level,
    val characterState: CharacterState,
    val evaluation: ScoreEvaluation,
    val engine: LevelEngine
)
