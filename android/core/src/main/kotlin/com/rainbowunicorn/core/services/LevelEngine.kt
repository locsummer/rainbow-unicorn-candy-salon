package com.rainbowunicorn.core.services

import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.Level
import com.rainbowunicorn.core.models.SaveData
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
    initialCharacter: CharacterState = CharacterState(characterType = level.characterType)
) {
    private var currentStationIndex = 0
    private val stationStatuses = mutableMapOf<StationType, StationStatus>()
    private val stationOutputs = mutableMapOf<StationType, StationOutput>()
    var characterState: CharacterState = initialCharacter
        private set
    private val undoStack = UndoStack(20)

    init {
        level.stations.forEachIndexed { idx, st ->
            stationStatuses[st] = if (idx == 0) StationStatus.IN_PROGRESS else StationStatus.NOT_STARTED
        }
        undoStack.initialize(characterState)
    }

    val currentStation: StationType?
        get() = level.stations.getOrNull(currentStationIndex)

    val isAllStationsCompleted: Boolean
        get() = level.stations.all { stationStatuses[it] == StationStatus.COMPLETED }

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
        // Also reset station output
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
        val eval = ScoringEngine.evaluateLevel(level, characterState, stationOutputs)
        return eval
    }

    fun applyResultsToSaveData(saveData: SaveData, evaluation: ScoreEvaluation): SaveData {
        if (!evaluation.isWin) {
            return saveData // Do not award stars or unlock next level on failure
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
            unlockedLevels = updatedUnlocked
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
