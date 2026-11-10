package com.rainbowunicorn.core.services

object AdPolicy {

    const val AD_COOLDOWN_SECONDS = 180
    const val MIN_LEVEL_INTERVAL = 2

    fun isAdsEnabled(hasRemoveAds: Boolean, hasPremiumLevels: Boolean): Boolean {
        return !(hasRemoveAds || hasPremiumLevels)
    }

    fun shouldShowInterstitialAd(
        hasRemoveAds: Boolean,
        hasPremiumLevels: Boolean,
        completedLevelId: Int,
        secondsSinceLastAd: Long,
        levelsPlayedSinceLastAd: Int,
        currentScreen: String
    ): Boolean {
        if (!isAdsEnabled(hasRemoveAds, hasPremiumLevels)) {
            return false
        }

        // Never show after level 1 or 2
        if (completedLevelId <= 2) {
            return false
        }

        // Only show when returning to level_select screen
        if (!currentScreen.equals("level_select", ignoreCase = true)) {
            return false
        }

        // Must respect cooldown
        if (secondsSinceLastAd < AD_COOLDOWN_SECONDS) {
            return false
        }

        // At most once every 2 levels
        if (levelsPlayedSinceLastAd < MIN_LEVEL_INTERVAL) {
            return false
        }

        return true
    }
}
