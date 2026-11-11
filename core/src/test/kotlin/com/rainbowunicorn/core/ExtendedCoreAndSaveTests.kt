package com.rainbowunicorn.core

import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.CharacterType
import com.rainbowunicorn.core.models.Criterion
import com.rainbowunicorn.core.models.Level
import com.rainbowunicorn.core.models.MakeupState
import com.rainbowunicorn.core.models.SaveData
import com.rainbowunicorn.core.models.SavedLevelCheckpoint
import com.rainbowunicorn.core.models.StationOutput
import com.rainbowunicorn.core.models.StationStatus
import com.rainbowunicorn.core.models.StationType
import com.rainbowunicorn.core.services.LayerComposer
import com.rainbowunicorn.core.services.LevelEngine
import com.rainbowunicorn.core.services.LevelRepository
import com.rainbowunicorn.core.services.SaveSerializer
import java.io.InputStreamReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExtendedCoreAndSaveTests {

    private fun readResource(path: String): String {
        val stream = javaClass.classLoader.getResourceAsStream(path)
            ?: error("Resource not found: $path")
        return InputStreamReader(stream, Charsets.UTF_8).use { it.readText() }
    }

    @Test
    fun testSaveSerializerStrictThrowsOnCorruptJson() {
        assertFailsWith<Exception> { SaveSerializer.fromJsonStrict("") }
        assertFailsWith<Exception> { SaveSerializer.fromJsonStrict("   ") }
        assertFailsWith<Exception> { SaveSerializer.fromJsonStrict("{corrupt::json") }
        assertFailsWith<Exception> { SaveSerializer.fromJsonStrict("[1, 2, 3]") }
    }

    @Test
    fun testSaveSerializerMigratesV1ToV2AndPreservesState() {
        val v1Json = """
            {
              "schemaVersion": 1,
              "candyBalance": 180,
              "bestStars": { "1": 3, "2": 2 },
              "unlockedLevels": [2, 3],
              "unlockedItems": ["hair_pastel_pink", "dress_crystal"],
              "purchasedEntitlements": ["remove_ads"],
              "galleryCount": 4,
              "bgmEnabled": false,
              "sfxEnabled": true
            }
        """.trimIndent()

        val migrated = SaveSerializer.fromJsonStrict(v1Json)
        assertEquals(SaveSerializer.CURRENT_SCHEMA_VERSION, migrated.schemaVersion)
        assertEquals(180, migrated.candyBalance)
        assertEquals("en", migrated.language)
        assertTrue(migrated.unlockedLevels.contains(1), "Migration ensures level 1 is always unlocked")
        assertTrue(migrated.unlockedLevels.containsAll(setOf(1, 2, 3)))
        assertFalse(migrated.bgmEnabled)
        assertTrue(migrated.purchasedEntitlements.contains("remove_ads"))
    }

    @Test
    fun testSaveSerializerRoundTripsFullCheckpoint() {
        val cp = SavedLevelCheckpoint(
            levelId = 3,
            currentStationIndex = 2,
            completedStations = setOf(StationType.HAIR, StationType.MAKEUP),
            characterState = CharacterState(
                characterType = CharacterType.PRINCESS,
                hairStyleId = "hair_buns_twin",
                hairColorId = "hair_pastel_pink",
                makeup = MakeupState(eyeshadow = "eyeshadow_berry_pink", lipstick = "lipstick_cherry"),
                outfitId = "outfit_ball_gown",
                facePaints = listOf("face_paint_glitter_stars"),
                stickers = listOf("sticker_star", "sticker_heart")
            ),
            spaCleanPercent = 0.875,
            cakeLayers = 3,
            cakeFlavor = "cake_flavor_strawberry",
            cakeToppings = listOf("topping_sprinkles", "topping_cherry"),
            petFed = true,
            petClean = 0.68,
            petGroomed = true,
            photoTaken = true
        )

        val save = SaveData(
            candyBalance = 95,
            language = "vi",
            activeCheckpoint = cp
        )

        val serialized = SaveSerializer.toJson(save)
        val decoded = SaveSerializer.fromJsonStrict(serialized)

        assertEquals("vi", decoded.language)
        val restored = decoded.activeCheckpoint
        assertNotNull(restored)
        assertEquals(3, restored.levelId)
        assertEquals(2, restored.currentStationIndex)
        assertEquals(setOf(StationType.HAIR, StationType.MAKEUP), restored.completedStations)
        assertEquals(CharacterType.PRINCESS, restored.characterState.characterType)
        assertEquals("hair_buns_twin", restored.characterState.hairStyleId)
        assertEquals("eyeshadow_berry_pink", restored.characterState.makeup.eyeshadow)
        assertEquals(3, restored.cakeLayers)
        assertEquals("cake_flavor_strawberry", restored.cakeFlavor)
        assertEquals(listOf("topping_sprinkles", "topping_cherry"), restored.cakeToppings)
        assertTrue(restored.petFed)
        assertTrue(restored.petGroomed)
        assertTrue(restored.photoTaken)
    }

    @Test
    fun testLevelEngineCheckpointResumeAndEditLookPreservation() {
        val level = Level(
            id = 1,
            name = "Candy Cloud Awakening",
            nameVi = "Đánh thức mây kẹo bông",
            theme = "cotton_candy",
            characterType = CharacterType.UNICORN,
            requiresPremium = false,
            stations = listOf(StationType.SPA, StationType.HAIR, StationType.DRESSUP),
            criteria = listOf(
                Criterion("c1", "spa", "clean_percent", 100, "Rửa sạch", "Wash clean"),
                Criterion("c2", "hair", "hair_color", "pastel_pink", "Tóc hồng", "Pink hair"),
                Criterion("c3", "dressup", "outfit", "cotton_candy_dress", "Đầm kẹo", "Candy dress")
            )
        )

        // Start, finish Spa cleanly (100%), start Hair
        val engine = LevelEngine(level)
        engine.completeCurrentStation(StationOutput(StationType.SPA, isCompleted = true, qualityScore = 1.0))
        engine.updateCharacter { it.copy(hairColorId = "hair_pastel_pink") }

        val cp = engine.toCheckpoint(spaCleanPercent = 1.0)
        assertEquals(setOf(StationType.SPA), cp.completedStations)
        assertEquals(1, cp.currentStationIndex)

        // Resume into a brand new LevelEngine from checkpoint
        val resumed = LevelEngine(level, checkpoint = cp)
        assertEquals(StationStatus.COMPLETED, resumed.getStationStatus(StationType.SPA))
        assertEquals(StationType.HAIR, resumed.currentStation)
        assertEquals("hair_pastel_pink", resumed.characterState.hairColorId)

        // Finish Hair & DressUp WITHOUT matching target criteria -> matchScore = 1/3 (0.333), qualityScore = (1 + 0 + 0)/3 = 0.333 -> total = 33 (< 50 -> fail)
        resumed.updateCharacter { it.copy(hairColorId = null) }
        resumed.completeCurrentStation(StationOutput(StationType.HAIR, isCompleted = true, qualityScore = 0.0))
        resumed.completeCurrentStation(StationOutput(StationType.DRESSUP, isCompleted = true, qualityScore = 0.0))
        val firstEval = resumed.finishLevel()
        assertFalse(firstEval.isWin, "Missing hair & outfit with low quality gives score < 50 (actual=${firstEval.totalScore})")
        assertEquals(0, firstEval.stars)

        // Edit Look: restore Hair + DressUp criteria & full quality, keeping Spa intact
        resumed.selectStation(1)
        assertEquals(StationType.HAIR, resumed.currentStation)
        resumed.updateCharacter { it.copy(hairColorId = "hair_pastel_pink") }
        resumed.completeCurrentStation(StationOutput(StationType.HAIR, isCompleted = true, qualityScore = 1.0))
        resumed.selectStation(2)
        assertEquals(StationType.DRESSUP, resumed.currentStation)
        resumed.updateCharacter { LayerComposer.equipOutfit(it, "outfit_cotton_candy_dress") }
        resumed.completeCurrentStation(StationOutput(StationType.DRESSUP, isCompleted = true, qualityScore = 1.0))

        val secondEval = resumed.finishLevel()
        assertTrue(secondEval.isWin)
        assertEquals(3, secondEval.stars)

        val saveWithCp = SaveData(candyBalance = 50, activeCheckpoint = cp)
        val afterWin = resumed.applyResultsToSaveData(saveWithCp, secondEval)
        assertNull(afterWin.activeCheckpoint, "Winning clears active checkpoint")
        assertEquals(80, afterWin.candyBalance)
        assertTrue(afterWin.unlockedLevels.contains(2))
    }

    @Test
    fun testLayerComposerToggleRemoveLastAndClearMakeup() {
        var ch = CharacterState()
        ch = LayerComposer.toggleFacePaint(ch, "fp_star")
        assertEquals(listOf("fp_star"), ch.facePaints)
        ch = LayerComposer.toggleFacePaint(ch, "fp_star")
        assertTrue(ch.facePaints.isEmpty(), "Toggling an equipped face paint removes it")

        ch = LayerComposer.applyEyeshadow(ch, "eyeshadow_berry_pink")
        ch = ch.copy(makeup = ch.makeup.copy(lipstick = "lipstick_cherry"), outfitId = "outfit_ball_gown")
        ch = LayerComposer.toggleFacePaint(ch, "fp_heart")
        ch = LayerComposer.clearMakeup(ch)
        assertNull(ch.makeup.eyeshadow)
        assertNull(ch.makeup.lipstick)
        assertTrue(ch.facePaints.isEmpty())
        assertEquals("outfit_ball_gown", ch.outfitId, "clearMakeup preserves outfit")

        ch = ch.copy(stickers = listOf("s1", "s2", "s3"))
        ch = LayerComposer.removeLastSticker(ch)
        assertEquals(listOf("s1", "s2"), ch.stickers)
    }

    @Test
    fun testAllBundledLevelsAndItemsHaveBothEnAndViLocalizations() {
        val levels = LevelRepository.loadLevels(readResource("levels.json"))
        val (cats, items) = LevelRepository.loadItems(readResource("items.json"))

        assertEquals(20, levels.size)
        levels.forEach { lvl ->
            assertTrue(lvl.name.isNotBlank(), "Level ${lvl.id} EN name missing")
            assertTrue(lvl.nameVi.isNotBlank(), "Level ${lvl.id} VI name missing")
            assertFalse(lvl.name == lvl.nameVi, "Level ${lvl.id} should have distinct VI translation")
            lvl.criteria.forEach { c ->
                assertTrue(c.description.isNotBlank(), "Criterion ${c.id} VI description missing")
                assertTrue(c.descriptionEn.isNotBlank(), "Criterion ${c.id} EN description missing")
            }
        }

        assertTrue(cats.isNotEmpty())
        cats.forEach { cat ->
            assertTrue(cat.name.isNotBlank() && cat.nameEn.isNotBlank())
        }
        assertTrue(items.isNotEmpty())
        items.forEach { item ->
            assertTrue(item.name.isNotBlank() && item.nameEn.isNotBlank())
        }
    }
}
