import XCTest
@testable import GameCore

final class SaveAndEngineTests: XCTestCase {

    func testSaveSerializerStrictThrowsOnCorruptJSON() {
        XCTAssertThrowsError(try SaveSerializer.fromJSONStrict("{not valid json"))
        XCTAssertThrowsError(try SaveSerializer.fromJSONStrict(""))
    }

    func testSaveSerializerMigratesV1ToV2AndPreservesDefaults() throws {
        let v1Json = """
        {
          "schemaVersion": 1,
          "candyBalance": 120,
          "bestStars": {"1": 3},
          "unlockedLevels": [2],
          "unlockedItems": ["hair_pink"],
          "purchasedEntitlements": [],
          "galleryCount": 2,
          "bgmEnabled": false,
          "sfxEnabled": true
        }
        """
        let migrated = try SaveSerializer.fromJSONStrict(v1Json)
        XCTAssertEqual(migrated.schemaVersion, 2)
        XCTAssertEqual(migrated.candyBalance, 120)
        XCTAssertTrue(migrated.unlockedLevels.contains(1))
        XCTAssertTrue(migrated.unlockedLevels.contains(2))
        XCTAssertEqual(migrated.language, "en")
        XCTAssertFalse(migrated.bgmEnabled)
    }

    func testLevelRepositoryDecodesBilingualAndPolymorphicTargets() throws {
        let levelsJson = """
        {
          "version": 1,
          "levels": [
            {
              "id": 1,
              "name": "Candy Cloud",
              "nameVi": "Mây kẹo",
              "theme": "cotton_candy",
              "characterType": "unicorn",
              "requiresPremium": false,
              "stations": ["spa", "hair", "cake"],
              "criteria": [
                { "id": "c1", "station": "spa", "type": "clean_percent", "target": 100, "description": "Rửa sạch", "descriptionEn": "Wash clean" },
                { "id": "c2", "station": "hair", "type": "hair_color", "target": "pastel_pink", "description": "Tóc hồng", "descriptionEn": "Pink hair" },
                { "id": "c3", "station": "pet", "type": "pet_fed", "target": true, "description": "Cho ăn", "descriptionEn": "Feed pet" }
              ]
            }
          ]
        }
        """
        let levels = try LevelRepository.loadLevels(from: levelsJson)
        XCTAssertEqual(levels.count, 1)
        let lvl = levels[0]
        XCTAssertEqual(lvl.localizedName(language: "en"), "Candy Cloud")
        XCTAssertEqual(lvl.localizedName(language: "vi"), "Mây kẹo")
        XCTAssertEqual(lvl.criteria[0].targetDescription, "100")
        XCTAssertEqual(lvl.criteria[1].targetDescription, "pastel_pink")
        XCTAssertEqual(lvl.criteria[2].targetDescription, "true")
        XCTAssertEqual(lvl.criteria[0].localizedDescription(language: "en"), "Wash clean")
        XCTAssertEqual(lvl.criteria[0].localizedDescription(language: "vi"), "Rửa sạch")
    }

    func testLevelEngineCheckpointExportRestoreAndWinClearsCheckpoint() {
        let lvl = Level(
            id: 1,
            name: "Candy Cloud",
            theme: "candy",
            characterType: .unicorn,
            requiresPremium: false,
            stations: [.spa, .hair, .dressup],
            criteria: [
                Criterion(id: "c1", station: "spa", type: "clean_percent", targetDescription: "100", description: "Wash", descriptionEn: "Wash"),
                Criterion(id: "c2", station: "hair", type: "hair_color", targetDescription: "pastel_pink", description: "Pink", descriptionEn: "Pink"),
                Criterion(id: "c3", station: "dressup", type: "outfit", targetDescription: "cotton_candy_dress", description: "Dress", descriptionEn: "Dress")
            ]
        )

        let engine1 = LevelEngine(level: lvl)
        engine1.completeCurrentStation(StationOutput(stationType: .spa, isCompleted: true, qualityScore: 1.0))
        engine1.updateCharacter { $0.hairColorId = "pastel_pink" }

        XCTAssertTrue(engine1.canUndo)
        XCTAssertFalse(engine1.canRedo)

        let cp = engine1.toCheckpoint(spaCleanPercent: 1.0)
        XCTAssertEqual(cp.completedStations, [.spa])
        XCTAssertEqual(cp.characterState.hairColorId, "pastel_pink")

        let engine2 = LevelEngine(level: lvl, checkpoint: cp)
        XCTAssertEqual(engine2.getStationStatus(.spa), .completed)
        XCTAssertEqual(engine2.characterState.hairColorId, "pastel_pink")
        XCTAssertEqual(engine2.currentStation, .hair)

        engine2.completeCurrentStation(StationOutput(stationType: .hair, isCompleted: true, qualityScore: 1.0))
        engine2.updateCharacter { $0.outfitId = "cotton_candy_dress" }
        engine2.completeCurrentStation(StationOutput(stationType: .dressup, isCompleted: true, qualityScore: 1.0))

        let eval = engine2.finishLevel()
        XCTAssertTrue(eval.isWin)
        XCTAssertEqual(eval.stars, 3)

        var save = SaveData()
        save.activeCheckpoint = cp
        let updatedSave = engine2.applyResultsToSaveData(save, evaluation: eval)
        XCTAssertNil(updatedSave.activeCheckpoint)
        XCTAssertEqual(updatedSave.candyBalance, 80)
        XCTAssertTrue(updatedSave.unlockedLevels.contains(2))
    }

    func testLayerComposerToggleAndResetIsolation() {
        var ch = CharacterState(characterType: .unicorn)
        ch = LayerComposer.toggleFacePaint(character: ch, paintId: "face_stars")
        XCTAssertEqual(ch.facePaints, ["face_stars"])
        ch = LayerComposer.toggleFacePaint(character: ch, paintId: "face_stars")
        XCTAssertTrue(ch.facePaints.isEmpty)

        ch = LayerComposer.applyEyeshadow(character: ch, eyeshadowId: "eye_pink")
        ch = LayerComposer.toggleFacePaint(character: ch, paintId: "face_stars")
        ch = LayerComposer.clearMakeup(character: ch)
        XCTAssertNil(ch.makeup.eyeshadow)
        XCTAssertTrue(ch.facePaints.isEmpty)

        var dressed = CharacterState(hairStyleId: "bob", outfitId: "dress", stickers: ["s1", "s2"])
        dressed = LayerComposer.removeLastSticker(character: dressed)
        XCTAssertEqual(dressed.stickers, ["s1"])
        let afterReset = dressed.resetCurrentStation(.dressup)
        XCTAssertNil(afterReset.outfitId)
        XCTAssertTrue(afterReset.stickers.isEmpty)
        XCTAssertEqual(afterReset.hairStyleId, "bob")
    }
}
