package com.rainbowunicorn.core.services

object UnlockPolicy {

    const val MAX_FREE_LEVEL = 10
    const val TOTAL_LEVELS = 20

    fun isLevelUnlocked(
        levelId: Int,
        bestStarsPerLevel: Map<Int, Int>,
        hasPremium: Boolean
    ): Boolean {
        if (levelId < 1 || levelId > TOTAL_LEVELS) return false
        if (levelId == 1) return true

        // Premium check for levels 11..20
        if (levelId > MAX_FREE_LEVEL && !hasPremium) {
            return false
        }

        // Must have completed previous level with at least 1 star
        val prevLevelStars = bestStarsPerLevel[levelId - 1] ?: 0
        return prevLevelStars >= 1
    }

    fun requiresPremiumPurchase(
        levelId: Int,
        bestStarsPerLevel: Map<Int, Int>,
        hasPremium: Boolean
    ): Boolean {
        if (levelId <= MAX_FREE_LEVEL || hasPremium) return false
        val level10Stars = bestStarsPerLevel[10] ?: 0
        return level10Stars >= 1 && levelId == 11
    }

    fun computeUnlockedLevels(
        bestStarsPerLevel: Map<Int, Int>,
        hasPremium: Boolean
    ): Set<Int> {
        val result = mutableSetOf(1)
        for (lvl in 2..TOTAL_LEVELS) {
            if (isLevelUnlocked(lvl, bestStarsPerLevel, hasPremium)) {
                result.add(lvl)
            } else {
                break
            }
        }
        return result
    }
}
