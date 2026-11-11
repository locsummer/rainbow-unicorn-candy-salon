import Foundation

public final class LevelEngine {

    public let level: Level
    public private(set) var characterState: CharacterState
    public private(set) var currentStationIndex: Int = 0
    private var stationStatuses: [StationType: StationStatus] = [:]
    private var stationOutputs: [StationType: StationOutput] = [:]
    private let undoStack = UndoStack(maxCapacity: 20)

    public init(
        level: Level,
        initialCharacter: CharacterState? = nil,
        checkpoint: SavedLevelCheckpoint? = nil
    ) {
        self.level = level
        if let cp = checkpoint, cp.levelId == level.id {
            self.characterState = cp.characterState
            let maxIdx = max(0, level.stations.count - 1)
            self.currentStationIndex = min(max(0, cp.currentStationIndex), maxIdx)
            let completedSet = Set(cp.completedStations)
            for (idx, st) in level.stations.enumerated() {
                if completedSet.contains(st) {
                    stationStatuses[st] = .completed
                    switch st {
                    case .spa:
                        stationOutputs[st] = StationOutput(stationType: st, isCompleted: true, qualityScore: cp.spaCleanPercent)
                    case .cake:
                        stationOutputs[st] = StationOutput(
                            stationType: st,
                            isCompleted: true,
                            qualityScore: 1.0,
                            cakeLayers: cp.cakeLayers,
                            cakeFlavor: cp.cakeFlavor,
                            cakeToppings: cp.cakeToppings
                        )
                    case .pet:
                        stationOutputs[st] = StationOutput(
                            stationType: st,
                            isCompleted: true,
                            qualityScore: 1.0,
                            petFed: cp.petFed,
                            petClean: cp.petClean,
                            petGroomed: cp.petGroomed
                        )
                    case .photo:
                        stationOutputs[st] = StationOutput(stationType: st, isCompleted: true, qualityScore: 1.0, photoTaken: cp.photoTaken)
                    default:
                        stationOutputs[st] = StationOutput(stationType: st, isCompleted: true, qualityScore: 1.0)
                    }
                } else if idx == self.currentStationIndex {
                    stationStatuses[st] = .inProgress
                } else {
                    stationStatuses[st] = .notStarted
                }
            }
        } else {
            self.characterState = initialCharacter ?? CharacterState(characterType: level.characterType)
            for (idx, st) in level.stations.enumerated() {
                stationStatuses[st] = idx == 0 ? .inProgress : .notStarted
            }
        }
        undoStack.initialize(with: self.characterState)
    }

    public var currentStation: StationType? {
        guard currentStationIndex < level.stations.count else { return nil }
        return level.stations[currentStationIndex]
    }

    public var canUndo: Bool { undoStack.canUndo }
    public var canRedo: Bool { undoStack.canRedo }
    public var currentStationOutputs: [StationType: StationOutput] { stationOutputs }

    public var isAllStationsCompleted: Bool {
        return level.stations.allSatisfy { stationStatuses[$0] == .completed }
    }

    public func getStationStatus(_ station: StationType) -> StationStatus {
        return stationStatuses[station] ?? .notStarted
    }

    public func selectStation(index: Int) {
        guard index >= 0 && index < level.stations.count else { return }
        currentStationIndex = index
        let st = level.stations[index]
        if stationStatuses[st] == .notStarted {
            stationStatuses[st] = .inProgress
        }
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

    public func toCheckpoint(
        spaCleanPercent: Double? = nil,
        cakeLayers: Int? = nil,
        cakeFlavor: String? = nil,
        cakeToppings: [String]? = nil,
        petFed: Bool? = nil,
        petClean: Double? = nil,
        petGroomed: Bool? = nil,
        photoTaken: Bool? = nil
    ) -> SavedLevelCheckpoint {
        let completed = level.stations.filter { stationStatuses[$0] == .completed }
        return SavedLevelCheckpoint(
            levelId: level.id,
            currentStationIndex: currentStationIndex,
            completedStations: completed,
            characterState: characterState,
            spaCleanPercent: spaCleanPercent ?? (stationOutputs[.spa]?.qualityScore ?? 0.0),
            cakeLayers: cakeLayers ?? (stationOutputs[.cake]?.cakeLayers ?? 2),
            cakeFlavor: cakeFlavor ?? (stationOutputs[.cake]?.cakeFlavor ?? "cake_flavor_vanilla_rainbow"),
            cakeToppings: cakeToppings ?? (stationOutputs[.cake]?.cakeToppings ?? []),
            petFed: petFed ?? (stationOutputs[.pet]?.petFed ?? false),
            petClean: petClean ?? (stationOutputs[.pet]?.petClean ?? 0.0),
            petGroomed: petGroomed ?? (stationOutputs[.pet]?.petGroomed ?? false),
            photoTaken: photoTaken ?? (stationOutputs[.photo]?.photoTaken ?? false)
        )
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
        copy.activeCheckpoint = nil

        return copy
    }
}
