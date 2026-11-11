import Foundation

public protocol AdProvider: AnyObject {
    var isInterstitialAdLoaded: Bool { get }
    var isRewardedAdLoaded: Bool { get }
    func showInterstitial(onDismissed: @escaping () -> Void)
    func showRewarded(onRewardEarned: @escaping (Bool) -> Void)
}

public final class FakeAdProvider: AdProvider {
    public var isInterstitialAdLoaded: Bool = true
    public var isRewardedAdLoaded: Bool = true
    public private(set) var interstitialShownCount: Int = 0
    public private(set) var rewardedShownCount: Int = 0

    public init() {}

    public func showInterstitial(onDismissed: @escaping () -> Void) {
        interstitialShownCount += 1
        onDismissed()
    }

    public func showRewarded(onRewardEarned: @escaping (Bool) -> Void) {
        rewardedShownCount += 1
        onRewardEarned(true)
    }
}
