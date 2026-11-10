package com.rainbowunicorn.core.services

data class AwardResult(
    val newBestStars: Int,
    val candyEarned: Int,
    val newCandyBalance: Int
)

data class PurchaseResult(
    val isSuccess: Boolean,
    val remainingCandy: Int,
    val unlockedItemId: String? = null,
    val errorMessage: String? = null
)

object EconomyService {

    const val CANDY_PER_STAR = 10

    fun calculateStarAward(
        initialBestStars: Int,
        levelEarnedStars: Int,
        currentCandyBalance: Int
    ): AwardResult {
        val newBestStars = maxOf(initialBestStars, levelEarnedStars)
        val deltaStars = maxOf(0, levelEarnedStars - initialBestStars)
        val candyEarned = deltaStars * CANDY_PER_STAR
        val newCandyBalance = currentCandyBalance + candyEarned
        return AwardResult(
            newBestStars = newBestStars,
            candyEarned = candyEarned,
            newCandyBalance = newCandyBalance
        )
    }

    fun purchaseItem(
        itemId: String,
        itemPrice: Int,
        currentCandyBalance: Int,
        alreadyOwnedItemIds: Set<String>
    ): PurchaseResult {
        if (alreadyOwnedItemIds.contains(itemId)) {
            return PurchaseResult(
                isSuccess = false,
                remainingCandy = currentCandyBalance,
                errorMessage = "Item already unlocked"
            )
        }

        if (currentCandyBalance < itemPrice) {
            return PurchaseResult(
                isSuccess = false,
                remainingCandy = currentCandyBalance,
                errorMessage = "Insufficient candy balance"
            )
        }

        val remainingCandy = currentCandyBalance - itemPrice
        return PurchaseResult(
            isSuccess = true,
            remainingCandy = remainingCandy,
            unlockedItemId = itemId
        )
    }
}
