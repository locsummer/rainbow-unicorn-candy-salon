import Foundation

public final class LevelEngine {

    public let level: Level
    public private(set) var characterState: CharacterState
    public private(set) var currentStationIndex: Int = 0
    private var stationStatuses: [StationType: StationStatus] = [:]
    private var stationOutputs: [StationType: StationOutput] = [:]
    private let undoStack = UndoStack(maxCapacity: 20)

    public init(level: Level, initialCharacter: CharacterState? = nil) {
        self.level = level
        self.characterState = initialCharacter ?? CharacterState(characterType: level.characterType)

        for (idx, st) in level.stations.enumerated() {
            stationStatuses[st] = idx == 0 ? .inProgress : .notStarted
        }
        undoStack.initialize(with: self.characterState)
    }

    public var currentStation: StationType? {
        guard currentStationIndex < level.stations.count else { return nil }
        return level.stations[currentStationIndex]
    }

    public var isAllStationsCompleted: Bool {
        return level.stations.allSatisfy { stationStatuses[$0] == .completed }
    }

    public func updateCharacter(_ transform: (inout CharacterState) -> Void) {
        transform(&characterState)
        undoStack.push(characterState)
    }

    public func undo() -> Bool {
        guard let prev = undoStack.undo() else { return false }
        characterState = prev
        return true
    }

    public func redo() -> Bool {
        guard let next = undoStack.redo() else { return false }
        characterState = next
        return true
    }

    public func resetCurrentStation() -> CharacterState {
        guard let st = currentStation else { return characterState }
        characterState = characterState.resetCurrentStation(st)
        undoStack.push(characterState)
        stationOutputs.removeValue(forKey: st)
        stationStatuses[st] = .inProgress
        return characterState
    }

    public func completeCurrentStation(_ output: StationOutput) {
        guard let st = currentStation else { return }
        var out = output
        out.isCompleted = true
        stationOutputs[st] = out
        stationStatuses[st] = .completed

        if currentStationIndex + 1 < level.stations.count {
            currentStationIndex += 1
            let nextSt = level.stations[currentStationIndex]
            if stationStatuses[nextSt] == .notStarted {
                stationStatuses[nextSt] = .inProgress
            }
        }
    }

    public func finishLevel() -> ScoreEvaluation {
        return ScoringEngine.evaluateLevel(level: level, character: characterState, outputs: stationOutputs)
    }

    public func applyResultsToSaveData(_ saveData: SaveData, evaluation: ScoreEvaluation) -> SaveData {
        guard evaluation.isWin else { return saveData }

        let oldStars = saveData.bestStars[String(level.id)] ?? 0
        let award = EconomyService.calculateStarAward(
            initialBestStars: oldStars,
            levelEarnedStars: evaluation.stars,
            currentCandyBalance: saveData.candyBalance
        )

        var updatedStars = saveData.bestStars
        updatedStars[String(level.id)] = award.newBestStars

        var intStars: [Int: Int] = [:]
        for (k, v) in updatedStars {
            if let intKey = Int(k) { intStars[intKey] = v }
        }

        let hasPremium = saveData.purchasedEntitlements.contains("premium_levels")
        let unlockedSet = UnlockPolicy.computeUnlockedLevels(bestStarsPerLevel: intStars, hasPremium: hasPremium)

        var copy = saveData
        copy.candyBalance = award.newCandyBalance
        copy.bestStars = updatedStars
        copy.unlockedLevels = Array(unlockedSet).sorted()

        return copy
    }
}
