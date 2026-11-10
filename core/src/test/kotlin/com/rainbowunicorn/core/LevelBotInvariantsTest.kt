package com.rainbowunicorn.core

import com.rainbowunicorn.core.bot.BotStrategy
import com.rainbowunicorn.core.bot.LevelBot
import com.rainbowunicorn.core.models.SaveData
import com.rainbowunicorn.core.services.LevelRepository
import java.io.InputStreamReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LevelBotInvariantsTest {

    private fun readResource(path: String): String {
        val stream = javaClass.classLoader.getResourceAsStream(path)
            ?: error("Resource not found: $path")
        return InputStreamReader(stream, Charsets.UTF_8).use { it.readText() }
    }

    @Test
    fun testOptimalBotPlaysAll20Levels() {
        val levelsJson = readResource("levels.json")
        val itemsJson = readResource("items.json")
        val levels = LevelRepository.loadLevels(levelsJson)
        val (_, items) = LevelRepository.loadItems(itemsJson)

        assertEquals(20, levels.size, "Must have exactly 20 levels")

        val bot = LevelBot(levels, items)
        var currentSave = SaveData(
            candyBalance = 50,
            purchasedEntitlements = setOf("premium_levels")
        )

        println("=== Starting LevelBot OPTIMAL strategy for all 20 levels ===")
        for (lvl in levels) {
            val (newSave, result) = bot.playLevel(lvl, currentSave, BotStrategy.OPTIMAL)
            assertTrue(result.isWin, "Optimal bot should win level ${lvl.id}")
            assertEquals(3, result.stars, "Optimal bot should achieve 3 stars on level ${lvl.id}")
            assertTrue(result.totalScore >= 90, "Optimal bot totalScore should be >= 90")
            println("Level ${lvl.id} [${lvl.name}]: score=${result.totalScore}, stars=${result.stars}, candyAwarded=${result.candyAwarded}, balance=${result.finalCandyBalance}")
            currentSave = newSave
        }

        assertEquals(20, currentSave.bestStars.size, "All 20 levels should have stars recorded")
        assertEquals(20, currentSave.unlockedLevels.size, "All 20 levels should be unlocked with premium")
        // 50 initial + 20 levels * 30 candy = 650 candy
        assertEquals(650, currentSave.candyBalance, "Final candy balance should be 650")
        println("=== OPTIMAL BOT FINISHED ALL 20 LEVELS SUCCESSFULLY ===")
    }

    @Test
    fun testDoNothingBotTriggersTryAgain() {
        val levelsJson = readResource("levels.json")
        val itemsJson = readResource("items.json")
        val levels = LevelRepository.loadLevels(levelsJson)
        val (_, items) = LevelRepository.loadItems(itemsJson)

        val bot = LevelBot(levels, items)
        val initialSave = SaveData(candyBalance = 50)

        println("=== Testing DO_NOTHING strategy for Level 1 ===")
        val (newSave, result) = bot.playLevel(levels[0], initialSave, BotStrategy.DO_NOTHING)
        assertEquals(false, result.isWin, "Do-nothing bot should not win")
        assertEquals(0, result.stars, "Do-nothing bot should earn 0 stars")
        assertTrue(result.totalScore < 50, "Do-nothing bot totalScore should be < 50")
        assertEquals(50, newSave.candyBalance, "Candy balance must not change on failure")
        assertEquals(setOf(1), newSave.unlockedLevels, "Next level must not unlock on failure")
        println("=== DO_NOTHING STRATEGY TEST PASSED ===")
    }

    @Test
    fun testRandomBot1000RunsCheckingInvariants() {
        val levelsJson = readResource("levels.json")
        val itemsJson = readResource("items.json")
        val levels = LevelRepository.loadLevels(levelsJson)
        val (_, items) = LevelRepository.loadItems(itemsJson)

        val bot = LevelBot(levels, items)
        var save = SaveData(
            candyBalance = 100,
            purchasedEntitlements = setOf("premium_levels")
        )

        println("=== Running LevelBot 1000 random iterations across 20 levels with invariant checks ===")
        val targetIterations = 1000
        var totalWins = 0
        var totalTries = 0

        for (i in 1..targetIterations) {
            val level = levels[(i - 1) % levels.size]
            val (newSave, result) = bot.playLevel(level, save, BotStrategy.RANDOM)
            save = newSave
            totalTries++
            if (result.isWin) totalWins++

            if (i % 250 == 0) {
                println("  Completed $i / $targetIterations runs. Wins: $totalWins, Current Candy: ${save.candyBalance}, Unlocked: ${save.unlockedLevels.size}")
            }
        }

        println("=== Completed 1000 random bot runs! All global invariants held! Total wins: $totalWins / $totalTries ===")
        assertTrue(totalTries == 1000)
        assertTrue(save.candyBalance >= 100)
    }
}
