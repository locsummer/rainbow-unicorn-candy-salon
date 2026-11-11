import Foundation

public struct AwardResult: Equatable {
    public let newBestStars: Int
    public let candyEarned: Int
    public let newCandyBalance: Int

    public init(newBestStars: Int, candyEarned: Int, newCandyBalance: Int) {
        self.newBestStars = newBestStars
        self.candyEarned = candyEarned
        self.newCandyBalance = newCandyBalance
    }
}

public struct PurchaseResult: Equatable {
    public let isSuccess: Bool
    public let remainingCandy: Int
    public let unlockedItemId: String?
    public let errorMessage: String?

    public init(
        isSuccess: Bool,
        remainingCandy: Int,
        unlockedItemId: String? = nil,
        errorMessage: String? = nil
    ) {
        self.isSuccess = isSuccess
        self.remainingCandy = remainingCandy
        self.unlockedItemId = unlockedItemId
        self.errorMessage = errorMessage
    }
}

public enum EconomyService {

    public static let candyPerStar = 10

    public static func calculateStarAward(
        initialBestStars: Int,
        levelEarnedStars: Int,
        currentCandyBalance: Int
    ) -> AwardResult {
        let newBestStars = max(initialBestStars, levelEarnedStars)
        let deltaStars = max(0, levelEarnedStars - initialBestStars)
        let candyEarned = deltaStars * candyPerStar
        let newCandyBalance = currentCandyBalance + candyEarned

        return AwardResult(
            newBestStars: newBestStars,
            candyEarned: candyEarned,
            newCandyBalance: newCandyBalance
        )
    }

    public static func purchaseItem(
        itemId: String,
        itemPrice: Int,
        currentCandyBalance: Int,
        alreadyOwnedItemIds: Set<String>
    ) -> PurchaseResult {
        if alreadyOwnedItemIds.contains(itemId) {
            return PurchaseResult(
                isSuccess: false,
                remainingCandy: currentCandyBalance,
                errorMessage: "Item already unlocked"
            )
        }

        if currentCandyBalance < itemPrice {
            return PurchaseResult(
                isSuccess: false,
                remainingCandy: currentCandyBalance,
                errorMessage: "Insufficient candy balance"
            )
        }

        let remaining = currentCandyBalance - itemPrice
        return PurchaseResult(
            isSuccess: true,
            remainingCandy: remaining,
            unlockedItemId: itemId
        )
    }
}
