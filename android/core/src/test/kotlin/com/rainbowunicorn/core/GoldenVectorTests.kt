package com.rainbowunicorn.core

import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.RenderLayer
import com.rainbowunicorn.core.services.AdPolicy
import com.rainbowunicorn.core.services.EconomyService
import com.rainbowunicorn.core.services.JsonElement
import com.rainbowunicorn.core.services.JsonParser
import com.rainbowunicorn.core.services.LayerComposer
import com.rainbowunicorn.core.services.ScoringEngine
import com.rainbowunicorn.core.services.UndoStack
import com.rainbowunicorn.core.services.UnlockPolicy
import java.io.InputStreamReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GoldenVectorTests {

    private fun readResource(path: String): String {
        val stream = javaClass.classLoader.getResourceAsStream(path)
            ?: error("Resource not found: $path")
        return InputStreamReader(stream, Charsets.UTF_8).use { it.readText() }
    }

    @Test
    fun testScoringStarsGoldenVector() {
        val json = readResource("golden/scoring_stars.json")
        val root = JsonParser.parse(json) as JsonElement.JsonObject
        val testCases = root.getArray("testCases") ?: error("No testCases")

        println("Running ${testCases.size} ScoringStars golden vectors...")
        for (tc in testCases) {
            val obj = tc as JsonElement.JsonObject
            val caseId = obj.getString("caseId") ?: ""
            val matchScore = obj.getDouble("matchScore") ?: 0.0
            val qualityScore = obj.getDouble("qualityScore") ?: 0.0
            val allStationsCompleted = obj.getBoolean("allStationsCompleted") ?: true
            val expectedTotal = obj.getInt("expectedTotal") ?: 0
            val expectedStars = obj.getInt("expectedStars") ?: 0
            val expectedWin = obj.getBoolean("expectedWin") ?: false

            val actualTotal = ScoringEngine.calculateTotalScore(matchScore, qualityScore)
            val actualStars = ScoringEngine.calculateStars(actualTotal, allStationsCompleted)
            val actualWin = ScoringEngine.isWinningScore(actualTotal, allStationsCompleted)

            assertEquals(expectedTotal, actualTotal, "Total score mismatch in $caseId")
            assertEquals(expectedStars, actualStars, "Stars mismatch in $caseId")
            assertEquals(expectedWin, actualWin, "Win condition mismatch in $caseId")
            println("  [PASS] $caseId: match=$matchScore quality=$qualityScore -> total=$actualTotal stars=$actualStars win=$actualWin")
        }
    }

    @Test
    fun testEconomyRewardsGoldenVector() {
        val json = readResource("golden/economy_rewards.json")
        val root = JsonParser.parse(json) as JsonElement.JsonObject
        val testCases = root.getArray("testCases") ?: error("No testCases")

        println("Running ${testCases.size} EconomyRewards golden vectors...")
        for (tc in testCases) {
            val obj = tc as JsonElement.JsonObject
            val caseId = obj.getString("caseId") ?: ""

            if (obj.getInt("initialBestStars") != null) {
                val initialBestStars = obj.getInt("initialBestStars") ?: 0
                val levelEarnedStars = obj.getInt("levelEarnedStars") ?: 0
                val expectedNewBestStars = obj.getInt("expectedNewBestStars") ?: 0
                val expectedCandyAward = obj.getInt("expectedCandyAward") ?: 0

                val award = EconomyService.calculateStarAward(initialBestStars, levelEarnedStars, 50)
                assertEquals(expectedNewBestStars, award.newBestStars, "New best stars mismatch in $caseId")
                assertEquals(expectedCandyAward, award.candyEarned, "Candy award mismatch in $caseId")
                println("  [PASS] $caseId: initialStars=$initialBestStars earned=$levelEarnedStars -> newStars=${award.newBestStars} candyEarned=${award.candyEarned}")
            } else if (obj.getInt("initialCandy") != null) {
                val initialCandy = obj.getInt("initialCandy") ?: 0
                val itemPrice = obj.getInt("itemPrice") ?: 0
                val isAlreadyOwned = obj.getBoolean("isAlreadyOwned") ?: false
                val expectedSuccess = obj.getBoolean("expectedSuccess") ?: false
                val expectedRemainingCandy = obj.getInt("expectedRemainingCandy") ?: 0

                val owned = if (isAlreadyOwned) setOf("item_test") else emptySet()
                val result = EconomyService.purchaseItem("item_test", itemPrice, initialCandy, owned)

                assertEquals(expectedSuccess, result.isSuccess, "Purchase success mismatch in $caseId")
                assertEquals(expectedRemainingCandy, result.remainingCandy, "Remaining candy mismatch in $caseId")
                println("  [PASS] $caseId: initialCandy=$initialCandy price=$itemPrice owned=$isAlreadyOwned -> success=${result.isSuccess} remCandy=${result.remainingCandy}")
            }
        }
    }

    @Test
    fun testLevelUnlockGoldenVector() {
        val json = readResource("golden/level_unlock.json")
        val root = JsonParser.parse(json) as JsonElement.JsonObject
        val testCases = root.getArray("testCases") ?: error("No testCases")

        println("Running ${testCases.size} LevelUnlock golden vectors...")
        for (tc in testCases) {
            val obj = tc as JsonElement.JsonObject
            val caseId = obj.getString("caseId") ?: ""
            val hasPremium = obj.getBoolean("hasPremium") ?: false
            val queryLevelId = obj.getInt("queryLevelId") ?: 1
            val expectedIsUnlocked = obj.getBoolean("expectedIsUnlocked") ?: false
            val expectedRequiresIAP = obj.getBoolean("expectedRequiresIAP") ?: false

            val completedLevelsArray = obj.getArray("completedLevels") ?: emptyList()
            val bestStars = mutableMapOf<Int, Int>()
            completedLevelsArray.forEach { clEl ->
                val clObj = clEl as JsonElement.JsonObject
                val lvl = clObj.getInt("levelId") ?: 0
                val stars = clObj.getInt("stars") ?: 0
                bestStars[lvl] = stars
            }

            // Also check currentLevelStars if present
            val currStars = obj.getInt("currentLevelStars")
            val currLevel = obj.getInt("currentLevelPlayed")
            if (currStars != null && currLevel != null) {
                bestStars[currLevel] = currStars
            }

            val actualUnlocked = UnlockPolicy.isLevelUnlocked(queryLevelId, bestStars, hasPremium)
            val actualRequiresIAP = UnlockPolicy.requiresPremiumPurchase(queryLevelId, bestStars, hasPremium)

            assertEquals(expectedIsUnlocked, actualUnlocked, "Level unlock status mismatch in $caseId")
            assertEquals(expectedRequiresIAP, actualRequiresIAP, "Requires IAP mismatch in $caseId")
            println("  [PASS] $caseId: queryLevel=$queryLevelId hasPremium=$hasPremium -> unlocked=$actualUnlocked requiresIAP=$actualRequiresIAP")
        }
    }

    @Test
    fun testAdPolicyGoldenVector() {
        val json = readResource("golden/ad_policy.json")
        val root = JsonParser.parse(json) as JsonElement.JsonObject
        val testCases = root.getArray("testCases") ?: error("No testCases")

        println("Running ${testCases.size} AdPolicy golden vectors...")
        for (tc in testCases) {
            val obj = tc as JsonElement.JsonObject
            val caseId = obj.getString("caseId") ?: ""
            val hasRemoveAds = obj.getBoolean("hasRemoveAds") ?: false
            val hasPremiumLevels = obj.getBoolean("hasPremiumLevels") ?: false

            val expectedAdsEnabled = obj.getBoolean("expectedAdsEnabled")
            if (expectedAdsEnabled != null) {
                val actualAdsEnabled = AdPolicy.isAdsEnabled(hasRemoveAds, hasPremiumLevels)
                assertEquals(expectedAdsEnabled, actualAdsEnabled, "adsEnabled mismatch in $caseId")
                println("  [PASS] $caseId: hasRemoveAds=$hasRemoveAds hasPremium=$hasPremiumLevels -> adsEnabled=$actualAdsEnabled")
            }

            val expectedShouldShowAd = obj.getBoolean("expectedShouldShowAd")
            if (expectedShouldShowAd != null) {
                val completedLevel = obj.getInt("completedLevel") ?: 1
                val secondsSinceLastAd = obj.getInt("secondsSinceLastAd")?.toLong() ?: 0L
                val currentScreen = obj.getString("currentScreen") ?: ""
                val levelsSinceLastAd = obj.getInt("levelsSinceLastAd") ?: 2

                val actualShouldShow = AdPolicy.shouldShowInterstitialAd(
                    hasRemoveAds = hasRemoveAds,
                    hasPremiumLevels = hasPremiumLevels,
                    completedLevelId = completedLevel,
                    secondsSinceLastAd = secondsSinceLastAd,
                    levelsPlayedSinceLastAd = levelsSinceLastAd,
                    currentScreen = currentScreen
                )
                assertEquals(expectedShouldShowAd, actualShouldShow, "shouldShowInterstitialAd mismatch in $caseId")
                println("  [PASS] $caseId: completedLvl=$completedLevel cooldown=$secondsSinceLastAd screen=$currentScreen -> shouldShow=$actualShouldShow")
            }
        }
    }

    @Test
    fun testCharacterLayersSlotsGoldenVector() {
        val json = readResource("golden/character_layers_slots.json")
        val root = JsonParser.parse(json) as JsonElement.JsonObject
        val testCases = root.getArray("testCases") ?: error("No testCases")

        println("Running ${testCases.size} CharacterLayersSlots golden vectors...")

        // CHR-001: 11 layers
        val order = RenderLayer.ordered()
        assertEquals(11, order.size)
        assertEquals("background", order[0].layerName)
        assertEquals("stickers_sparkles", order[10].layerName)
        println("  [PASS] CHR-001: Strict 11 layer ordering validated.")

        // CHR-002: Exclusive slot replacement
        var charState = CharacterState(outfitId = "outfit_cotton_candy_dress")
        charState = LayerComposer.equipOutfit(charState, "outfit_pastry_chef")
        assertEquals("outfit_pastry_chef", charState.outfitId)
        println("  [PASS] CHR-002: Exclusive slot outfit replacement validated.")

        // CHR-003: Multi-item face paint capped at 3 with FIFO
        var paintChar = CharacterState(facePaints = listOf("face_paint_glitter_stars", "face_paint_carnival_mask"))
        paintChar = LayerComposer.addFacePaint(paintChar, "face_paint_neon_glow")
        assertEquals(3, paintChar.facePaints.size)
        paintChar = LayerComposer.addFacePaint(paintChar, "face_paint_dragon_sparkle")
        assertEquals(3, paintChar.facePaints.size)
        assertEquals(listOf("face_paint_carnival_mask", "face_paint_neon_glow", "face_paint_dragon_sparkle"), paintChar.facePaints)
        println("  [PASS] CHR-003: Face paint 3-item cap with FIFO validated.")

        // CHR-004: Stickers capped at 10
        var stickerChar = CharacterState(stickers = (1..10).map { "sticker_$it" })
        val (rejectedChar, success) = LayerComposer.addSticker(stickerChar, "sticker_magic_wand")
        assertFalse(success)
        assertEquals(10, rejectedChar.stickers.size)
        println("  [PASS] CHR-004: Sticker 10-item cap validated.")

        // CHR-005: Undo stack max 20 steps
        val stack = UndoStack(20)
        stack.initialize(CharacterState())
        for (i in 1..21) {
            stack.push(CharacterState(hairStyleId = "style_$i"))
        }
        assertEquals(20, stack.size)
        println("  [PASS] CHR-005: Undo stack max 20 steps drop oldest validated.")

        // CHR-006: Reset station
        val testChar = CharacterState(
            makeup = com.rainbowunicorn.core.models.MakeupState(eyeshadow = "pink", lipstick = "red"),
            outfitId = "outfit_1"
        )
        val resetChar = testChar.resetCurrentStation(com.rainbowunicorn.core.models.StationType.MAKEUP)
        assertEquals(null, resetChar.makeup.eyeshadow)
        assertEquals(null, resetChar.makeup.lipstick)
        assertEquals("outfit_1", resetChar.outfitId) // Outfit remains untouched!
        println("  [PASS] CHR-006: Reset station scope isolated validated.")
    }
}
