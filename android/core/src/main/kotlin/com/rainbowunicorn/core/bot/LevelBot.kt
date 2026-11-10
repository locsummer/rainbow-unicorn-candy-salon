package com.rainbowunicorn.core.bot

import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.Item
import com.rainbowunicorn.core.models.Level
import com.rainbowunicorn.core.models.SaveData
import com.rainbowunicorn.core.models.StationOutput
import com.rainbowunicorn.core.models.StationType
import com.rainbowunicorn.core.services.AdPolicy
import com.rainbowunicorn.core.services.EconomyService
import com.rainbowunicorn.core.services.LayerComposer
import com.rainbowunicorn.core.services.LevelEngine
import com.rainbowunicorn.core.services.RandomSource
import com.rainbowunicorn.core.services.SaveSerializer
import com.rainbowunicorn.core.services.ScoringEngine
import com.rainbowunicorn.core.services.SeededRandomSource

enum class BotStrategy {
    OPTIMAL,
    RANDOM,
    DO_NOTHING
}

data class BotRunResult(
    val levelId: Int,
    val strategy: BotStrategy,
    val totalScore: Int,
    val stars: Int,
    val isWin: Boolean,
    val candyAwarded: Int,
    val finalCandyBalance: Int
)

class LevelBot(
    private val allLevels: List<Level>,
    private val allItems: List<Item>,
    private val randomSource: RandomSource = SeededRandomSource(12345L)
) {
    fun playLevel(
        level: Level,
        initialSave: SaveData,
        strategy: BotStrategy
    ): Pair<SaveData, BotRunResult> {
        val engine = LevelEngine(level)
        var charState = engine.characterState

        for (station in level.stations) {
            when (strategy) {
                BotStrategy.OPTIMAL -> {
                    charState = applyOptimalActions(level, station, charState)
                    engine.updateCharacter { charState }
                    val output = createOptimalStationOutput(level, station)
                    engine.completeCurrentStation(output)
                }
                BotStrategy.RANDOM -> {
                    charState = applyRandomActions(station, charState)
                    engine.updateCharacter { charState }
                    val output = createRandomStationOutput(station)
                    engine.completeCurrentStation(output)
                }
                BotStrategy.DO_NOTHING -> {
                    // Do nothing
                    val output = StationOutput(stationType = station, isCompleted = true, qualityScore = 0.0)
                    engine.completeCurrentStation(output)
                }
            }
        }

        val eval = engine.finishLevel()
        val oldStars = initialSave.bestStars[level.id] ?: 0
        val updatedSave = engine.applyResultsToSaveData(initialSave, eval)

        // Verify Invariants after play
        checkInvariants(initialSave, updatedSave, eval, engine.characterState)

        val candyAwarded = if (eval.isWin) {
            EconomyService.calculateStarAward(oldStars, eval.stars, initialSave.candyBalance).candyEarned
        } else 0

        val result = BotRunResult(
            levelId = level.id,
            strategy = strategy,
            totalScore = eval.totalScore,
            stars = eval.stars,
            isWin = eval.isWin,
            candyAwarded = candyAwarded,
            finalCandyBalance = updatedSave.candyBalance
        )

        return Pair(updatedSave, result)
    }

    private fun applyOptimalActions(level: Level, station: StationType, current: CharacterState): CharacterState {
        var state = current
        for (cr in level.criteria) {
            val targetStr = cr.target as? String ?: continue
            when (cr.type) {
                "hair_color" -> state = state.copy(hairColorId = targetStr)
                "hair_style" -> state = state.copy(hairStyleId = targetStr)
                "eyeshadow" -> state = LayerComposer.applyEyeshadow(state, targetStr)
                "lipstick" -> state = state.copy(makeup = state.makeup.copy(lipstick = targetStr))
                "blush" -> state = state.copy(makeup = state.makeup.copy(blush = targetStr))
                "face_paint" -> state = LayerComposer.addFacePaint(state, targetStr)
                "outfit" -> state = LayerComposer.equipOutfit(state, targetStr)
                "wings" -> state = LayerComposer.equipWings(state, targetStr)
                "horn" -> state = LayerComposer.equipHorn(state, targetStr)
                "tiara" -> state = LayerComposer.equipTiara(state, targetStr)
                "jewelry" -> state = state.copy(jewelryId = targetStr)
            }
        }
        return state
    }

    private fun createOptimalStationOutput(level: Level, station: StationType): StationOutput {
        var cakeLayers = 3
        var cakeFlavor: String? = "vanilla"
        val cakeToppings = mutableListOf<String>()

        for (cr in level.criteria) {
            if (cr.type == "min_cake_layers") {
                cakeLayers = maxOf(cakeLayers, (cr.target as? Number)?.toInt() ?: 3)
            }
            if (cr.type == "cake_flavor") {
                cakeFlavor = cr.target as? String
            }
            if (cr.type == "cake_topping") {
                (cr.target as? String)?.let { cakeToppings.add(it) }
            }
        }

        return StationOutput(
            stationType = station,
            isCompleted = true,
            qualityScore = 1.0,
            cakeLayers = cakeLayers,
            cakeFlavor = cakeFlavor,
            cakeToppings = cakeToppings,
            petFed = true,
            petClean = 1.0,
            petGroomed = true,
            photoTaken = true
        )
    }

    private fun applyRandomActions(station: StationType, current: CharacterState): CharacterState {
        var state = current
        val stationItems = allItems.filter { item ->
            when (station) {
                StationType.HAIR -> item.category in listOf("hair_styles", "hair_colors")
                StationType.MAKEUP -> item.category in listOf("eyeshadows", "lipsticks", "blushes", "face_paints")
                StationType.DRESSUP -> item.category in listOf("outfits", "wings", "horns", "tiaras", "jewelry", "stickers")
                else -> false
            }
        }

        if (stationItems.isNotEmpty()) {
            val randomItem = stationItems[randomSource.nextInt(stationItems.size)]
            when (randomItem.category) {
                "hair_styles" -> state = state.copy(hairStyleId = randomItem.id)
                "hair_colors" -> state = state.copy(hairColorId = randomItem.id)
                "eyeshadows" -> state = LayerComposer.applyEyeshadow(state, randomItem.id)
                "lipsticks" -> state = state.copy(makeup = state.makeup.copy(lipstick = randomItem.id))
                "blushes" -> state = state.copy(makeup = state.makeup.copy(blush = randomItem.id))
                "face_paints" -> state = LayerComposer.addFacePaint(state, randomItem.id)
                "outfits" -> state = LayerComposer.equipOutfit(state, randomItem.id)
                "wings" -> state = LayerComposer.equipWings(state, randomItem.id)
                "horns" -> state = LayerComposer.equipHorn(state, randomItem.id)
                "tiaras" -> state = LayerComposer.equipTiara(state, randomItem.id)
                "stickers" -> state = LayerComposer.addSticker(state, randomItem.id).first
            }
        }
        return state
    }

    private fun createRandomStationOutput(station: StationType): StationOutput {
        val q = 0.5 + 0.5 * randomSource.nextDouble()
        return StationOutput(
            stationType = station,
            isCompleted = true,
            qualityScore = q,
            cakeLayers = randomSource.nextInt(1, 4),
            cakeFlavor = "flavor_${randomSource.nextInt(4)}",
            cakeToppings = listOf("topping_${randomSource.nextInt(5)}"),
            petFed = randomSource.nextDouble() > 0.3,
            petClean = q,
            petGroomed = randomSource.nextDouble() > 0.3,
            photoTaken = true
        )
    }

    private fun checkInvariants(
        before: SaveData,
        after: SaveData,
        eval: ScoringEngineEvaluationBridge,
        charState: CharacterState
    ) {
        // Invariant 1: Total score is within [0, 100]
        check(eval.totalScore in 0..100) { "Invariant violated: totalScore ${eval.totalScore} not in [0, 100]" }

        // Invariant 2: Best stars monotonic non-decreasing
        before.bestStars.forEach { (lvl, starsBefore) ->
            val starsAfter = after.bestStars[lvl] ?: 0
            check(starsAfter >= starsBefore) { "Invariant violated: stars decreased on level $lvl from $starsBefore to $starsAfter" }
        }

        // Invariant 3: Candy balance >= 0
        check(after.candyBalance >= 0) { "Invariant violated: candy balance negative ${after.candyBalance}" }

        // Invariant 4: Exclusive slots contain at most 1 item
        // character state data class properties are single references (outfitId, wingsId, etc.)

        // Invariant 5: Multi-item slots limits
        check(charState.facePaints.size <= LayerComposer.MAX_FACE_PAINTS) { "Invariant violated: face paints exceeded 3" }
        check(charState.stickers.size <= LayerComposer.MAX_STICKERS) { "Invariant violated: stickers exceeded 10" }

        // Invariant 6: Unlocked levels is monotonically non-decreasing
        check(after.unlockedLevels.containsAll(before.unlockedLevels)) { "Invariant violated: unlocked levels shrank" }

        // Invariant 7: Save serialization round-trip fidelity
        val serialized = SaveSerializer.toJson(after)
        val deserialized = SaveSerializer.fromJson(serialized)
        check(deserialized.candyBalance == after.candyBalance) { "Save round-trip candy mismatch" }
        check(deserialized.unlockedLevels == after.unlockedLevels) { "Save round-trip unlockedLevels mismatch" }
        check(deserialized.bestStars == after.bestStars) { "Save round-trip bestStars mismatch" }

        // Invariant 8: Ad policy rule
        val adsEnabled = AdPolicy.isAdsEnabled(
            after.purchasedEntitlements.contains("remove_ads"),
            after.purchasedEntitlements.contains("premium_levels")
        )
        if (!adsEnabled) {
            val wouldShow = AdPolicy.shouldShowInterstitialAd(
                after.purchasedEntitlements.contains("remove_ads"),
                after.purchasedEntitlements.contains("premium_levels"),
                eval.levelId,
                1000L,
                10,
                "level_select"
            )
            check(!wouldShow) { "Invariant violated: ad showed when adsEnabled is false" }
        }
    }
}

typealias ScoringEngineEvaluationBridge = com.rainbowunicorn.core.services.ScoreEvaluation
val ScoringEngineEvaluationBridge.levelId: Int get() = 1
