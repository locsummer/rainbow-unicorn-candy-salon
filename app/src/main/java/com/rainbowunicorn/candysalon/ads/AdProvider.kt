package com.rainbowunicorn.candysalon.ads

interface AdProvider {
    val isInterstitialAdLoaded: Boolean
    val isRewardedAdLoaded: Boolean
    fun showInterstitial(onDismissed: () -> Unit)
    fun showRewarded(onRewardEarned: (Boolean) -> Unit)
}

class FakeAdProvider : AdProvider {

    override var isInterstitialAdLoaded: Boolean = true
    override var isRewardedAdLoaded: Boolean = true
    var interstitialShownCount: Int = 0
    var rewardedShownCount: Int = 0

    override fun showInterstitial(onDismissed: () -> Unit) {
        interstitialShownCount++
        onDismissed()
    }

    override fun showRewarded(onRewardEarned: (Boolean) -> Unit) {
        rewardedShownCount++
        onRewardEarned(true)
    }
}
