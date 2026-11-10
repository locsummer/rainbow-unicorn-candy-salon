package com.rainbowunicorn.core

import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.SaveData
import com.rainbowunicorn.core.services.ParentalGateService
import com.rainbowunicorn.core.services.SaveSerializer
import com.rainbowunicorn.core.services.UndoStack
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ParentalGateAndServicesTest {

    @Test
    fun testParentalGateHoldTimeAndArithmetic() {
        val gate = ParentalGateService()

        // Test hold requirement (3000 ms)
        assertFalse(gate.verifyHold(2999L), "2999ms should fail hold requirement")
        assertTrue(gate.verifyHold(3000L), "3000ms should pass hold requirement")
        assertTrue(gate.verifyHold(4500L), "4500ms should pass hold requirement")

        // Test question generation
        for (i in 1..20) {
            val q = gate.generateQuestion()
            assertTrue(q.numA in 1..9, "numA should be 1..9")
            assertTrue(q.numB in 1..9, "numB should be 1..9")
            assertEquals(q.numA + q.numB, q.correctAnswer, "correctAnswer must equal numA + numB")
            assertEquals(3, q.options.size, "Must have exactly 3 options")
            assertTrue(q.options.contains(q.correctAnswer), "Options must contain correct answer")

            // Verify correct answer
            assertTrue(gate.verifyAnswer(q, q.correctAnswer), "Correct answer must be accepted")

            // Verify wrong answer
            val wrong = q.options.firstOrNull { it != q.correctAnswer }
            if (wrong != null) {
                assertFalse(gate.verifyAnswer(q, wrong), "Wrong answer must be rejected")
            }
        }
    }

    @Test
    fun testSaveDataSerializationAndCorruptFallback() {
        val original = SaveData(
            schemaVersion = 1,
            candyBalance = 240,
            bestStars = mapOf(1 to 3, 2 to 2, 3 to 1),
            unlockedLevels = setOf(1, 2, 3, 4),
            unlockedItems = setOf("hair_1", "dress_2"),
            purchasedEntitlements = setOf("premium_levels"),
            galleryCount = 12,
            bgmEnabled = false,
            sfxEnabled = true
        )

        val json = SaveSerializer.toJson(original)
        val deserialized = SaveSerializer.fromJson(json)

        assertEquals(original.candyBalance, deserialized.candyBalance)
        assertEquals(original.bestStars, deserialized.bestStars)
        assertEquals(original.unlockedLevels, deserialized.unlockedLevels)
        assertEquals(original.unlockedItems, deserialized.unlockedItems)
        assertEquals(original.purchasedEntitlements, deserialized.purchasedEntitlements)
        assertEquals(original.galleryCount, deserialized.galleryCount)
        assertEquals(original.bgmEnabled, deserialized.bgmEnabled)
        assertEquals(original.sfxEnabled, deserialized.sfxEnabled)

        // Test corrupt JSON fallback
        val corruptJson = "{ malformed json :: [ 123 "
        val fallback = SaveSerializer.fromJson(corruptJson)
        assertNotNull(fallback)
        assertEquals(50, fallback.candyBalance)
        assertEquals(setOf(1), fallback.unlockedLevels)
    }

    @Test
    fun testUndoStackFullLifecycle() {
        val stack = UndoStack(maxCapacity = 20)
        val s0 = CharacterState(skinTone = "tone_0")
        stack.initialize(s0)

        assertFalse(stack.canUndo)
        assertFalse(stack.canRedo)
        assertEquals(1, stack.size)

        // Push 5 states
        for (i in 1..5) {
            stack.push(CharacterState(skinTone = "tone_$i"))
        }
        assertEquals(6, stack.size)
        assertTrue(stack.canUndo)

        // Undo 2 times
        val u1 = stack.undo()
        assertEquals("tone_4", u1?.skinTone)
        val u2 = stack.undo()
        assertEquals("tone_3", u2?.skinTone)
        assertTrue(stack.canRedo)

        // Redo 1 time
        val r1 = stack.redo()
        assertEquals("tone_4", r1?.skinTone)

        // New action clears redo
        stack.push(CharacterState(skinTone = "tone_branched"))
        assertFalse(stack.canRedo)

        // Fill past 20 steps
        for (i in 10..35) {
            stack.push(CharacterState(skinTone = "tone_$i"))
        }
        assertEquals(20, stack.size)
    }
}
