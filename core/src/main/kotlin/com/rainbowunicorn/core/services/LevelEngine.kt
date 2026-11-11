package com.rainbowunicorn.core.services

import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.Level
import com.rainbowunicorn.core.models.SaveData
import com.rainbowunicorn.core.models.SavedLevelCheckpoint
import com.rainbowunicorn.core.models.StationOutput
import com.rainbowunicorn.core.models.StationStatus
import com.rainbowunicorn.core.models.StationType

data class LevelSessionState(
    val level: Level,
    val currentStationIndex: Int,
    val stationStatuses: Map<StationType, StationStatus>,
    val stationOutputs: Map<StationType, StationOutput>,
    val characterState: CharacterState,
    val isLevelFinished: Boolean = false,
    val finalEvaluation: ScoreEvaluation? = null
)

class LevelEngine(
    val level: Level,
    initialCharacter: CharacterState = CharacterState(characterType = level.characterType),
    checkpoint: SavedLevelCheckpoint? = null
) {
    var currentStationIndex: Int = 0
        private set
    private val stationStatuses = mutableMapOf<StationType, StationStatus>()
    private val stationOutputs = mutableMapOf<StationType, StationOutput>()
    var characterState: CharacterState = checkpoint?.characterState ?: initialCharacter
        private set
    private val undoStack = UndoStack(20)

    init {
        if (checkpoint != null && checkpoint.levelId == level.id) {
            currentStationIndex = checkpoint.currentStationIndex.coerceIn(0, (level.stations.size - 1).coerceAtLeast(0))
            level.stations.forEachIndexed { idx, st ->
                val isCompleted = checkpoint.completedStations.contains(st)
                stationStatuses[st] = when {
                    isCompleted -> StationStatus.COMPLETED
                    idx == currentStationIndex -> StationStatus.IN_PROGRESS
                    else -> StationStatus.NOT_STARTED
                }
                if (isCompleted) {
                    stationOutputs[st] = when (st) {
                        StationType.SPA -> StationOutput(st, true, checkpoint.spaCleanPercent)
                        StationType.CAKE -> StationOutput(
                            st,
                            true,
                            1.0,
                            checkpoint.cakeLayers,
                            checkpoint.cakeFlavor,
                            checkpoint.cakeToppings
                        )
                        StationType.PET -> StationOutput(
                            st,
                            true,
                            1.0,
                            petFed = checkpoint.petFed,
                            petClean = checkpoint.petClean,
                            petGroomed = checkpoint.petGroomed
                        )
                        StationType.PHOTO -> StationOutput(st, true, 1.0, photoTaken = checkpoint.photoTaken)
                        else -> StationOutput(st, true, 1.0)
                    }
                }
            }
        } else {
            level.stations.forEachIndexed { idx, st ->
                stationStatuses[st] = if (idx == 0) StationStatus.IN_PROGRESS else StationStatus.NOT_STARTED
            }
        }
        undoStack.initialize(characterState)
    }

    val currentStation: StationType?
        get() = level.stations.getOrNull(currentStationIndex)

    val canUndo: Boolean
        get() = undoStack.canUndo

    val canRedo: Boolean
        get() = undoStack.canRedo

    val isAllStationsCompleted: Boolean
        get() = level.stations.all { stationStatuses[it] == StationStatus.COMPLETED }

    fun getStationStatus(station: StationType): StationStatus =
        stationStatuses[station] ?: StationStatus.NOT_STARTED

    fun selectStation(index: Int) {
        if (index in level.stations.indices) {
            currentStationIndex = index
            val st = level.stations[index]
            if (stationStatuses[st] == StationStatus.NOT_STARTED) {
                stationStatuses[st] = StationStatus.IN_PROGRESS
            }
        }
    }

    fun updateCharacter(transform: (CharacterState) -> CharacterState) {
        val updated = transform(characterState)
        characterState = updated
        undoStack.push(updated)
    }

    fun undo(): Boolean {
        val prev = undoStack.undo() ?: return false
        characterState = prev
        return true
    }

    fun redo(): Boolean {
        val next = undoStack.redo() ?: return false
        characterState = next
        return true
    }

    fun resetCurrentStation(): CharacterState {
        val st = currentStation ?: return characterState
        val reset = characterState.resetCurrentStation(st)
        characterState = reset
        undoStack.push(reset)
        stationOutputs.remove(st)
        stationStatuses[st] = StationStatus.IN_PROGRESS
        return characterState
    }

    fun completeCurrentStation(output: StationOutput) {
        val st = currentStation ?: return
        stationOutputs[st] = output.copy(isCompleted = true)
        stationStatuses[st] = StationStatus.COMPLETED

        if (currentStationIndex + 1 < level.stations.size) {
            currentStationIndex++
            val nextSt = level.stations[currentStationIndex]
            if (stationStatuses[nextSt] == StationStatus.NOT_STARTED) {
                stationStatuses[nextSt] = StationStatus.IN_PROGRESS
            }
        }
    }

    fun finishLevel(): ScoreEvaluation {
        return ScoringEngine.evaluateLevel(level, characterState, stationOutputs)
    }

    fun toCheckpoint(
        spaCleanPercent: Double = stationOutputs[StationType.SPA]?.qualityScore ?: 0.0,
        cakeLayers: Int = stationOutputs[StationType.CAKE]?.cakeLayers ?: 2,
        cakeFlavor: String? = stationOutputs[StationType.CAKE]?.cakeFlavor ?: "cake_flavor_vanilla_rainbow",
        cakeToppings: List<String> = stationOutputs[StationType.CAKE]?.cakeToppings ?: emptyList(),
        petFed: Boolean = stationOutputs[StationType.PET]?.petFed ?: false,
        petClean: Double = stationOutputs[StationType.PET]?.petClean ?: 0.0,
        petGroomed: Boolean = stationOutputs[StationType.PET]?.petGroomed ?: false,
        photoTaken: Boolean = stationOutputs[StationType.PHOTO]?.photoTaken ?: false
    ): SavedLevelCheckpoint {
        val completed = stationStatuses.filterValues { it == StationStatus.COMPLETED }.keys
        return SavedLevelCheckpoint(
            levelId = level.id,
            currentStationIndex = currentStationIndex,
            completedStations = completed,
            characterState = characterState,
            spaCleanPercent = spaCleanPercent,
            cakeLayers = cakeLayers,
            cakeFlavor = cakeFlavor,
            cakeToppings = cakeToppings,
            petFed = petFed,
            petClean = petClean,
            petGroomed = petGroomed,
            photoTaken = photoTaken
        )
    }

    fun applyResultsToSaveData(saveData: SaveData, evaluation: ScoreEvaluation): SaveData {
        if (!evaluation.isWin) {
            return saveData
        }

        val oldBestStars = saveData.bestStars[level.id] ?: 0
        val award = EconomyService.calculateStarAward(oldBestStars, evaluation.stars, saveData.candyBalance)

        val updatedBestStars = saveData.bestStars.toMutableMap()
        updatedBestStars[level.id] = award.newBestStars

        val hasPremium = saveData.purchasedEntitlements.contains("premium_levels")
        val updatedUnlocked = UnlockPolicy.computeUnlockedLevels(updatedBestStars, hasPremium)

        return saveData.copy(
            candyBalance = award.newCandyBalance,
            bestStars = updatedBestStars,
            unlockedLevels = updatedUnlocked,
            activeCheckpoint = null
        )
    }

    fun getSessionState(): LevelSessionState {
        return LevelSessionState(
            level = level,
            currentStationIndex = currentStationIndex,
            stationStatuses = stationStatuses.toMap(),
            stationOutputs = stationOutputs.toMap(),
            characterState = characterState,
            isLevelFinished = isAllStationsCompleted,
            finalEvaluation = if (isAllStationsCompleted) ScoringEngine.evaluateLevel(level, characterState, stationOutputs) else null
        )
    }
}
