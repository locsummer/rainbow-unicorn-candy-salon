import Foundation

public enum UnlockPolicy {

    public static let maxFreeLevel = 10
    public static let totalLevels = 20

    public static func isLevelUnlocked(
        levelId: Int,
        bestStarsPerLevel: [Int: Int],
        hasPremium: Bool
    ) -> Bool {
        guard levelId >= 1 && levelId <= totalLevels else { return false }
        if levelId == 1 { return true }

        if levelId > maxFreeLevel && !hasPremium {
            return false
        }

        let prevStars = bestStarsPerLevel[levelId - 1] ?? 0
        return prevStars >= 1
    }

    public static func requiresPremiumPurchase(
        levelId: Int,
        bestStarsPerLevel: [Int: Int],
        hasPremium: Bool
    ) -> Bool {
        guard levelId > maxFreeLevel && !hasPremium else { return false }
        let level10Stars = bestStarsPerLevel[10] ?? 0
        return level10Stars >= 1 && levelId == 11
    }

    public static func computeUnlockedLevels(
        bestStarsPerLevel: [Int: Int],
        hasPremium: Bool
    ) -> Set<Int> {
        var result: Set<Int> = [1]
        for lvl in 2...totalLevels {
            if isLevelUnlocked(levelId: lvl, bestStarsPerLevel: bestStarsPerLevel, hasPremium: hasPremium) {
                result.insert(lvl)
            } else {
                break
            }
        }
        return result
    }
}
