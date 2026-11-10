import Foundation

public enum AdPolicy {

    public static let adCooldownSeconds: Int64 = 180
    public static let minLevelInterval = 2

    public static func isAdsEnabled(hasRemoveAds: Bool, hasPremiumLevels: Bool) -> Bool {
        return !(hasRemoveAds || hasPremiumLevels)
    }

    public static func shouldShowInterstitialAd(
        hasRemoveAds: Bool,
        hasPremiumLevels: Bool,
        completedLevelId: Int,
        secondsSinceLastAd: Int64,
        levelsPlayedSinceLastAd: Int,
        currentScreen: String
    ) -> Bool {
        guard isAdsEnabled(hasRemoveAds: hasRemoveAds, hasPremiumLevels: hasPremiumLevels) else {
            return false
        }

        if completedLevelId <= 2 {
            return false
        }

        if currentScreen.lowercased() != "level_select" {
            return false
        }

        if secondsSinceLastAd < adCooldownSeconds {
            return false
        }

        if levelsPlayedSinceLastAd < minLevelInterval {
            return false
        }

        return true
    }
}
