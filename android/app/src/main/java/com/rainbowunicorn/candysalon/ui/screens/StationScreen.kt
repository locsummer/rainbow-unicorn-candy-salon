package com.rainbowunicorn.candysalon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rainbowunicorn.candysalon.R
import com.rainbowunicorn.candysalon.audio.SoundManager
import com.rainbowunicorn.candysalon.ui.components.BottomItemTray
import com.rainbowunicorn.candysalon.ui.components.CharacterCanvasRenderer
import com.rainbowunicorn.candysalon.ui.components.ConfirmDialog
import com.rainbowunicorn.candysalon.ui.components.NavButton
import com.rainbowunicorn.candysalon.ui.stations.CakeStationView
import com.rainbowunicorn.candysalon.ui.stations.PetStationView
import com.rainbowunicorn.candysalon.ui.stations.SpaStationView
import com.rainbowunicorn.candysalon.ui.theme.CandyDeepPlum
import com.rainbowunicorn.candysalon.ui.theme.CandyMint
import com.rainbowunicorn.candysalon.ui.theme.CandyPastelPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPink
import com.rainbowunicorn.candysalon.ui.theme.CandyPurple
import com.rainbowunicorn.candysalon.ui.theme.CandyYellow
import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.Item
import com.rainbowunicorn.core.models.ItemCategory
import com.rainbowunicorn.core.models.Level
import com.rainbowunicorn.core.models.StationOutput
import com.rainbowunicorn.core.models.StationType
import com.rainbowunicorn.core.services.LayerComposer
import com.rainbowunicorn.core.services.LevelEngine

@Composable
fun StationScreen(
    level: Level,
    categories: List<ItemCategory>,
    items: List<Item>,
    unlockedItems: Set<String>,
    soundManager: SoundManager,
    onLevelFinished: (StationScreenResult) -> Unit,
    onExitConfirmed: () -> Unit
) {
    val levelEngine = remember(level) { LevelEngine(level) }
    var characterState by remember { mutableStateOf(levelEngine.characterState) }
    var currentStationIndex by remember { mutableIntStateOf(0) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Station specific state
    var spaCleanPercent by remember { mutableDoubleStateOf(0.0) }
    var cakeLayers by remember { mutableIntStateOf(2) }
    var cakeFlavor by remember { mutableStateOf<String?>("vanilla") }
    val cakeToppings = remember { mutableStateListOf<String>() }
    var petFed by remember { mutableStateOf(false) }
    var petGroomed by remember { mutableStateOf(false) }
    var photoTaken by remember { mutableStateOf(false) }

    val currentStation = level.stations.getOrElse(currentStationIndex) { level.stations.first() }

    // Filter categories relevant to current station
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
            .background(CandyPastelPink.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP HUD (Fixed position, icon + text)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    label = stringResource(R.string.btn_exit),
                    onClick = { showExitDialog = true },
                    containerColor = CandyPink
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "${level.name} - Trạm ${currentStationIndex + 1}/${level.stations.size}: ${currentStation.value.uppercase()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CandyDeepPlum
                )

                Spacer(modifier = Modifier.weight(1f))

                // Undo / Redo
                NavButton(
                    icon = Icons.AutoMirrored.Filled.Undo,
                    label = stringResource(R.string.btn_undo),
                    onClick = {
                        if (levelEngine.undo()) {
                            characterState = levelEngine.characterState
                            soundManager.playClick()
                        }
                    },
                    containerColor = Color.White,
                    contentColor = CandyDeepPlum
                )

                NavButton(
                    icon = Icons.AutoMirrored.Filled.Redo,
                    label = stringResource(R.string.btn_redo),
                    onClick = {
                        if (levelEngine.redo()) {
                            characterState = levelEngine.characterState
                            soundManager.playClick()
                        }
                    },
                    containerColor = Color.White,
                    contentColor = CandyDeepPlum
                )

                // Reset Station (RULE-055)
                NavButton(
                    icon = Icons.Default.Refresh,
                    label = stringResource(R.string.btn_reset),
                    onClick = { showResetDialog = true },
                    containerColor = CandyYellow,
                    contentColor = CandyDeepPlum
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Complete Station Button (RULE-020)
                NavButton(
                    icon = Icons.Default.Check,
                    label = stringResource(R.string.btn_done),
                    onClick = {
                        soundManager.playSparkle()
                        val output = when (currentStation) {
                            StationType.SPA -> StationOutput(
                                stationType = StationType.SPA,
                                isCompleted = true,
                                qualityScore = spaCleanPercent
                            )
                            StationType.CAKE -> StationOutput(
                                stationType = StationType.CAKE,
                                isCompleted = true,
                                qualityScore = 1.0,
                                cakeLayers = cakeLayers,
                                cakeFlavor = cakeFlavor,
                                cakeToppings = cakeToppings.toList()
                            )
                            StationType.PET -> StationOutput(
                                stationType = StationType.PET,
                                isCompleted = true,
                                qualityScore = 1.0,
                                petFed = petFed,
                                petClean = 1.0,
                                petGroomed = petGroomed
                            )
                            StationType.PHOTO -> StationOutput(
                                stationType = StationType.PHOTO,
                                isCompleted = true,
                                qualityScore = 1.0,
                                photoTaken = photoTaken
                            )
                            else -> StationOutput(
                                stationType = currentStation,
                                isCompleted = true,
                                qualityScore = 1.0
                            )
                        }

                        levelEngine.completeCurrentStation(output)

                        if (currentStationIndex + 1 < level.stations.size) {
                            currentStationIndex++
                        } else {
                            // Finish level
                            val evaluation = levelEngine.finishLevel()
                            onLevelFinished(
                                StationScreenResult(
                                    level = level,
                                    characterState = characterState,
                                    evaluation = evaluation,
                                    engine = levelEngine
                                )
                            )
                        }
                    },
                    containerColor = CandyMint,
                    contentColor = CandyDeepPlum
                )
            }

            // CENTRAL STAGE
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
                            onCleanPercentChanged = {
                                spaCleanPercent = it
                                soundManager.playBubble()
                            }
                        )
                    }
                    StationType.CAKE -> {
                        CakeStationView(
                            layers = cakeLayers,
                            flavor = cakeFlavor,
                            toppings = cakeToppings
                        )
                    }
                    StationType.PET -> {
                        PetStationView(
                            petFed = petFed,
                            petClean = 1.0,
                            petGroomed = petGroomed
                        )
                    }
                    StationType.PHOTO -> {
                        Box(contentAlignment = Alignment.Center) {
                            CharacterCanvasRenderer(
                                characterState = characterState,
                                modifier = Modifier.fillMaxSize()
                            )
                            NavButton(
                                icon = Icons.Default.CameraAlt,
                                label = stringResource(R.string.btn_take_photo),
                                onClick = {
                                    photoTaken = true
                                    soundManager.playShutter()
                                },
                                containerColor = CandyPink,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                    else -> {
                        CharacterCanvasRenderer(
                            characterState = characterState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // BOTTOM TRAY (Non-overlapping, retaining scroll position)
            if (currentCategories.isNotEmpty()) {
                BottomItemTray(
                    categories = currentCategories,
                    items = items,
                    unlockedItems = unlockedItems,
                    selectedItemId = null,
                    onItemSelected = { item ->
                        soundManager.playClick()
                        levelEngine.updateCharacter { current ->
                            when (item.category) {
                                "hair_styles" -> current.copy(hairStyleId = item.id)
                                "hair_colors" -> current.copy(hairColorId = item.id)
                                "eyeshadows" -> LayerComposer.applyEyeshadow(current, item.id)
                                "lipsticks" -> current.copy(makeup = current.makeup.copy(lipstick = item.id))
                                "blushes" -> current.copy(makeup = current.makeup.copy(blush = item.id))
                                "face_paints" -> LayerComposer.addFacePaint(current, item.id)
                                "outfits" -> LayerComposer.equipOutfit(current, item.id)
                                "wings" -> LayerComposer.equipWings(current, item.id)
                                "horns" -> LayerComposer.equipHorn(current, item.id)
                                "tiaras" -> LayerComposer.equipTiara(current, item.id)
                                "jewelry" -> current.copy(jewelryId = item.id)
                                "stickers" -> LayerComposer.addSticker(current, item.id).first
                                "cake_flavors" -> {
                                    cakeFlavor = item.id
                                    current
                                }
                                "cake_toppings" -> {
                                    if (cakeToppings.size < 8) cakeToppings.add(item.id)
                                    current
                                }
                                else -> current
                            }
                        }
                        characterState = levelEngine.characterState
                    },
                    onLockedItemClicked = { _ ->
                        soundManager.playClick()
                    }
                )
            }
        }

        // Mid-Level Exit Dialog with Auto-Save (RULE-071)
        if (showExitDialog) {
            ConfirmDialog(
                title = stringResource(R.string.dialog_exit_title),
                message = stringResource(R.string.dialog_exit_message),
                onConfirm = {
                    showExitDialog = false
                    onExitConfirmed()
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
                    soundManager.playClick()
                },
                onDismiss = { showResetDialog = false }
            )
        }
    }
}

data class StationScreenResult(
    val level: Level,
    val characterState: CharacterState,
    val evaluation: com.rainbowunicorn.core.services.ScoreEvaluation,
    val engine: LevelEngine
)
