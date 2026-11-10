import Foundation

public struct ScoreEvaluation: Equatable {
    public let matchScore: Double
    public let qualityScore: Double
    public let totalScore: Int
    public let stars: Int
    public let isWin: Bool
    public let criteriaResults: [String: Bool]

    public init(
        matchScore: Double,
        qualityScore: Double,
        totalScore: Int,
        stars: Int,
        isWin: Bool,
        criteriaResults: [String: Bool]
    ) {
        self.matchScore = matchScore
        self.qualityScore = qualityScore
        self.totalScore = totalScore
        self.stars = stars
        self.isWin = isWin
        self.criteriaResults = criteriaResults
    }
}

public enum ScoringEngine {

    public static func calculateTotalScore(matchScore: Double, qualityScore: Double) -> Int {
        let raw = 100.0 * (0.6 * matchScore + 0.4 * qualityScore)
        let rounded = Int(raw.rounded())
        return min(max(rounded, 0), 100)
    }

    public static func calculateStars(totalScore: Int, allStationsCompleted: Bool = true) -> Int {
        guard allStationsCompleted else { return 0 }
        switch totalScore {
        case 90...100:
            return 3
        case 75..<90:
            return 2
        case 50..<75:
            return 1
        default:
            return 0
        }
    }

    public static func isWinningScore(totalScore: Int, allStationsCompleted: Bool) -> Bool {
        return allStationsCompleted && totalScore >= 50
    }

    public static func evaluateCriterion(
        _ criterion: Criterion,
        character: CharacterState,
        outputs: [StationType: StationOutput]
    ) -> Bool {
        let target = criterion.targetDescription
        switch criterion.type {
        case "hair_color":
            return character.hairColorId?.localizedCaseInsensitiveContains(target) == true
        case "hair_style":
            return character.hairStyleId?.localizedCaseInsensitiveContains(target) == true
        case "eyeshadow":
            return character.makeup.eyeshadow?.localizedCaseInsensitiveContains(target) == true
        case "lipstick":
            return character.makeup.lipstick?.localizedCaseInsensitiveContains(target) == true
        case "blush":
            return character.makeup.blush?.localizedCaseInsensitiveContains(target) == true
        case "face_paint":
            return character.facePaints.contains(where: { $0.localizedCaseInsensitiveContains(target) })
        case "outfit":
            return character.outfitId?.localizedCaseInsensitiveContains(target) == true
        case "wings":
            return character.wingsId?.localizedCaseInsensitiveContains(target) == true
        case "horn":
            return character.hornId?.localizedCaseInsensitiveContains(target) == true
        case "tiara":
            return character.tiaraId?.localizedCaseInsensitiveContains(target) == true
        case "jewelry":
            return character.jewelryId?.localizedCaseInsensitiveContains(target) == true
        case "clean_percent":
            guard let spa = outputs[.spa] else { return false }
            let required = Double(target) ?? 100.0
            return (spa.qualityScore * 100.0) >= required
        case "min_cake_layers":
            guard let cake = outputs[.cake] else { return false }
            let minLayers = Int(target) ?? 1
            return cake.cakeLayers >= minLayers
        case "cake_flavor":
            guard let cake = outputs[.cake], let flav = cake.cakeFlavor else { return false }
            return flav.localizedCaseInsensitiveContains(target)
        case "cake_topping":
            guard let cake = outputs[.cake] else { return false }
            return cake.cakeToppings.contains(where: { $0.localizedCaseInsensitiveContains(target) })
        case "pet_fed":
            return outputs[.pet]?.petFed == true
        case "pet_groomed":
            return outputs[.pet]?.petGroomed == true
        case "pet_clean":
            guard let pet = outputs[.pet] else { return false }
            let req = Double(target) ?? 100.0
            return (pet.petClean * 100.0) >= req
        case "photo_taken":
            return outputs[.photo]?.photoTaken == true
        default:
            return false
        }
    }

    public static func evaluateLevel(
        level: Level,
        character: CharacterState,
        outputs: [StationType: StationOutput]
    ) -> ScoreEvaluation {
        var results: [String: Bool] = [:]
        var metCount = 0

        for c in level.criteria {
            let met = evaluateCriterion(c, character: character, outputs: outputs)
            results[c.id] = met
            if met { metCount += 1 }
        }

        let matchScore = level.criteria.isEmpty ? 1.0 : Double(metCount) / Double(level.criteria.size)
        let allCompleted = level.stations.allSatisfy { outputs[$0]?.isCompleted == true }

        let qualitySum = level.stations.reduce(0.0) { sum, st in
            sum + (outputs[st]?.qualityScore ?? 0.0)
        }
        let qualityScore = level.stations.isEmpty ? 1.0 : qualitySum / Double(level.stations.count)

        let total = calculateTotalScore(matchScore: matchScore, qualityScore: qualityScore)
        let stars = calculateStars(totalScore: total, allStationsCompleted: allCompleted)
        let isWin = isWinningScore(totalScore: total, allStationsCompleted: allCompleted)

        return ScoreEvaluation(
            matchScore: matchScore,
            qualityScore: qualityScore,
            totalScore: total,
            stars: stars,
            isWin: isWin,
            criteriaResults: results
        )
    }
}

private extension Array {
    var size: Int { count }
}
