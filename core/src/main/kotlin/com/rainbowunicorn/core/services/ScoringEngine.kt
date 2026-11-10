package com.rainbowunicorn.core.services

import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.Criterion
import com.rainbowunicorn.core.models.Level
import com.rainbowunicorn.core.models.StationOutput
import com.rainbowunicorn.core.models.StationType
import kotlin.math.roundToInt

data class ScoreEvaluation(
    val matchScore: Double,
    val qualityScore: Double,
    val totalScore: Int,
    val stars: Int,
    val isWin: Boolean,
    val criteriaResults: Map<String, Boolean>
)

object ScoringEngine {

    fun calculateTotalScore(matchScore: Double, qualityScore: Double): Int {
        val raw = 100.0 * (0.6 * matchScore + 0.4 * qualityScore)
        val rounded = raw.roundToInt()
        return rounded.coerceIn(0, 100)
    }

    fun calculateStars(totalScore: Int, allStationsCompleted: Boolean = true): Int {
        if (!allStationsCompleted) return 0
        return when {
            totalScore >= 90 -> 3
            totalScore >= 75 -> 2
            totalScore >= 50 -> 1
            else -> 0
        }
    }

    fun isWinningScore(totalScore: Int, allStationsCompleted: Boolean): Boolean {
        return allStationsCompleted && totalScore >= 50
    }

    fun evaluateCriterion(
        criterion: Criterion,
        character: CharacterState,
        outputs: Map<StationType, StationOutput>
    ): Boolean {
        return when (criterion.type) {
            "hair_color" -> {
                val target = criterion.target as? String ?: return false
                character.hairColorId?.contains(target, ignoreCase = true) == true
            }
            "hair_style" -> {
                val target = criterion.target as? String ?: return false
                character.hairStyleId?.contains(target, ignoreCase = true) == true
            }
            "eyeshadow" -> {
                val target = criterion.target as? String ?: return false
                character.makeup.eyeshadow?.contains(target, ignoreCase = true) == true
            }
            "lipstick" -> {
                val target = criterion.target as? String ?: return false
                character.makeup.lipstick?.contains(target, ignoreCase = true) == true
            }
            "blush" -> {
                val target = criterion.target as? String ?: return false
                character.makeup.blush?.contains(target, ignoreCase = true) == true
            }
            "face_paint" -> {
                val target = criterion.target as? String ?: return false
                character.facePaints.any { it.contains(target, ignoreCase = true) }
            }
            "outfit" -> {
                val target = criterion.target as? String ?: return false
                character.outfitId?.contains(target, ignoreCase = true) == true
            }
            "wings" -> {
                val target = criterion.target as? String ?: return false
                character.wingsId?.contains(target, ignoreCase = true) == true
            }
            "horn" -> {
                val target = criterion.target as? String ?: return false
                character.hornId?.contains(target, ignoreCase = true) == true
            }
            "tiara" -> {
                val target = criterion.target as? String ?: return false
                character.tiaraId?.contains(target, ignoreCase = true) == true
            }
            "jewelry" -> {
                val target = criterion.target as? String ?: return false
                character.jewelryId?.contains(target, ignoreCase = true) == true
            }
            "clean_percent" -> {
                val spaOutput = outputs[StationType.SPA] ?: return false
                val requiredPercent = (criterion.target as? Number)?.toDouble() ?: 100.0
                (spaOutput.qualityScore * 100.0) >= requiredPercent
            }
            "min_cake_layers" -> {
                val cakeOutput = outputs[StationType.CAKE] ?: return false
                val minLayers = (criterion.target as? Number)?.toInt() ?: 1
                cakeOutput.cakeLayers >= minLayers
            }
            "cake_flavor" -> {
                val cakeOutput = outputs[StationType.CAKE] ?: return false
                val target = criterion.target as? String ?: return false
                cakeOutput.cakeFlavor?.contains(target, ignoreCase = true) == true
            }
            "cake_topping" -> {
                val cakeOutput = outputs[StationType.CAKE] ?: return false
                val target = criterion.target as? String ?: return false
                cakeOutput.cakeToppings.any { it.contains(target, ignoreCase = true) }
            }
            "pet_fed" -> {
                val petOutput = outputs[StationType.PET] ?: return false
                petOutput.petFed
            }
            "pet_groomed" -> {
                val petOutput = outputs[StationType.PET] ?: return false
                petOutput.petGroomed
            }
            "pet_clean" -> {
                val petOutput = outputs[StationType.PET] ?: return false
                val requiredPercent = (criterion.target as? Number)?.toDouble() ?: 100.0
                (petOutput.petClean * 100.0) >= requiredPercent
            }
            "photo_taken" -> {
                val photoOutput = outputs[StationType.PHOTO] ?: return false
                photoOutput.photoTaken
            }
            else -> false
        }
    }

    fun evaluateLevel(
        level: Level,
        character: CharacterState,
        outputs: Map<StationType, StationOutput>
    ): ScoreEvaluation {
        val criteriaResults = mutableMapOf<String, Boolean>()
        var metCount = 0
        for (c in level.criteria) {
            val met = evaluateCriterion(c, character, outputs)
            criteriaResults[c.id] = met
            if (met) metCount++
        }

        val matchScore = if (level.criteria.isNotEmpty()) {
            metCount.toDouble() / level.criteria.size.toDouble()
        } else 1.0

        val requiredStations = level.stations
        val allCompleted = requiredStations.all { st ->
            outputs[st]?.isCompleted == true
        }

        val qualitySum = requiredStations.sumOf { st ->
            outputs[st]?.qualityScore ?: 0.0
        }
        val qualityScore = if (requiredStations.isNotEmpty()) {
            qualitySum / requiredStations.size.toDouble()
        } else 1.0

        val totalScore = calculateTotalScore(matchScore, qualityScore)
        val stars = calculateStars(totalScore, allCompleted)
        val isWin = isWinningScore(totalScore, allCompleted)

        return ScoreEvaluation(
            matchScore = matchScore,
            qualityScore = qualityScore,
            totalScore = totalScore,
            stars = stars,
            isWin = isWin,
            criteriaResults = criteriaResults
        )
    }
}
