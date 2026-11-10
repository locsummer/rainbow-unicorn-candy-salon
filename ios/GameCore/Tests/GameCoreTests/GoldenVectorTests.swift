import XCTest
@testable import GameCore

final class GoldenVectorTests: XCTestCase {

    private func loadGoldenJSON(named filename: String) throws -> Data {
        guard let url = Bundle.module.url(forResource: filename, withExtension: "json", subdirectory: "Golden")
                ?? Bundle.module.url(forResource: filename, withExtension: "json") else {
            XCTFail("Missing resource \(filename).json")
            throw CocoaError(.fileNoSuchFile)
        }
        return try Data(contentsOf: url)
    }

    func testScoringStarsGoldenVector() throws {
        struct Case: Codable {
            let caseId: String
            let matchScore: Double
            let qualityScore: Double
            let allStationsCompleted: Bool
            let expectedTotal: Int
            let expectedStars: Int
            let expectedWin: Bool
        }
        struct Wrapper: Codable {
            let testCases: [Case]
        }

        let data = try loadGoldenJSON(named: "scoring_stars")
        let wrapper = try JSONDecoder().decode(Wrapper.self, from: data)

        for tc in wrapper.testCases {
            let total = ScoringEngine.calculateTotalScore(matchScore: tc.matchScore, qualityScore: tc.qualityScore)
            let stars = ScoringEngine.calculateStars(totalScore: total, allStationsCompleted: tc.allStationsCompleted)
            let win = ScoringEngine.isWinningScore(totalScore: total, allStationsCompleted: tc.allStationsCompleted)

            XCTAssertEqual(total, tc.expectedTotal, "Total mismatch in \(tc.caseId)")
            XCTAssertEqual(stars, tc.expectedStars, "Stars mismatch in \(tc.caseId)")
            XCTAssertEqual(win, tc.expectedWin, "Win mismatch in \(tc.caseId)")
        }
    }

    func testEconomyRewardsGoldenVector() throws {
        struct Case: Codable {
            let caseId: String
            let initialBestStars: Int?
            let levelEarnedStars: Int?
            let expectedNewBestStars: Int?
            let expectedCandyAward: Int?
            let initialCandy: Int?
            let itemPrice: Int?
            let isAlreadyOwned: Bool?
            let expectedSuccess: Bool?
            let expectedRemainingCandy: Int?
        }
        struct Wrapper: Codable {
            let testCases: [Case]
        }

        let data = try loadGoldenJSON(named: "economy_rewards")
        let wrapper = try JSONDecoder().decode(Wrapper.self, from: data)

        for tc in wrapper.testCases {
            if let initStars = tc.initialBestStars, let earned = tc.levelEarnedStars {
                let res = EconomyService.calculateStarAward(initialBestStars: initStars, levelEarnedStars: earned, currentCandyBalance: 50)
                XCTAssertEqual(res.newBestStars, tc.expectedNewBestStars, "Stars mismatch in \(tc.caseId)")
                XCTAssertEqual(res.candyEarned, tc.expectedCandyAward, "Award mismatch in \(tc.caseId)")
            } else if let initCandy = tc.initialCandy, let price = tc.itemPrice {
                let owned: Set<String> = (tc.isAlreadyOwned == true) ? ["item_test"] : []
                let res = EconomyService.purchaseItem(itemId: "item_test", itemPrice: price, currentCandyBalance: initCandy, alreadyOwnedItemIds: owned)
                XCTAssertEqual(res.isSuccess, tc.expectedSuccess, "Purchase success mismatch in \(tc.caseId)")
                XCTAssertEqual(res.remainingCandy, tc.expectedRemainingCandy, "Remaining candy mismatch in \(tc.caseId)")
            }
        }
    }

    func testAdPolicyGoldenVector() throws {
        struct Case: Codable {
            let caseId: String
            let hasRemoveAds: Bool
            let hasPremiumLevels: Bool
            let expectedAdsEnabled: Bool?
            let completedLevel: Int?
            let secondsSinceLastAd: Int64?
            let currentScreen: String?
            let levelsSinceLastAd: Int?
            let expectedShouldShowAd: Bool?
        }
        struct Wrapper: Codable {
            let testCases: [Case]
        }

        let data = try loadGoldenJSON(named: "ad_policy")
        let wrapper = try JSONDecoder().decode(Wrapper.self, from: data)

        for tc in wrapper.testCases {
            if let expectedEnabled = tc.expectedAdsEnabled {
                let actual = AdPolicy.isAdsEnabled(hasRemoveAds: tc.hasRemoveAds, hasPremiumLevels: tc.hasPremiumLevels)
                XCTAssertEqual(actual, expectedEnabled, "adsEnabled mismatch in \(tc.caseId)")
            }
            if let expectedShow = tc.expectedShouldShowAd {
                let actual = AdPolicy.shouldShowInterstitialAd(
                    hasRemoveAds: tc.hasRemoveAds,
                    hasPremiumLevels: tc.hasPremiumLevels,
                    completedLevelId: tc.completedLevel ?? 1,
                    secondsSinceLastAd: tc.secondsSinceLastAd ?? 0,
                    levelsPlayedSinceLastAd: tc.levelsSinceLastAd ?? 2,
                    currentScreen: tc.currentScreen ?? ""
                )
                XCTAssertEqual(actual, expectedShow, "shouldShowAd mismatch in \(tc.caseId)")
            }
        }
    }

    func testCharacterLayersSlotsGoldenVector() throws {
        // CHR-001: 11 layers
        let layers = LayerComposer.getRenderingOrder()
        XCTAssertEqual(layers.count, 11)
        XCTAssertEqual(layers.first?.layerName, "background")
        XCTAssertEqual(layers.last?.layerName, "stickers_sparkles")

        // CHR-002: Exclusive slot
        var char = CharacterState(outfitId: "outfit_cotton_candy_dress")
        char = LayerComposer.equipOutfit(character: char, outfitId: "outfit_pastry_chef")
        XCTAssertEqual(char.outfitId, "outfit_pastry_chef")

        // CHR-003: Multi-item face paint max 3 with FIFO
        var paintChar = CharacterState(facePaints: ["face_paint_glitter_stars", "face_paint_carnival_mask"])
        paintChar = LayerComposer.addFacePaint(character: paintChar, paintId: "face_paint_neon_glow")
        XCTAssertEqual(paintChar.facePaints.count, 3)
        paintChar = LayerComposer.addFacePaint(character: paintChar, paintId: "face_paint_dragon_sparkle")
        XCTAssertEqual(paintChar.facePaints.count, 3)
        XCTAssertEqual(paintChar.facePaints, ["face_paint_carnival_mask", "face_paint_neon_glow", "face_paint_dragon_sparkle"])

        // CHR-004: Stickers max 10
        var stickerChar = CharacterState(stickers: (1...10).map { "sticker_\($0)" })
        let (rejected, success) = LayerComposer.addSticker(character: stickerChar, stickerId: "sticker_magic_wand")
        XCTAssertFalse(success)
        XCTAssertEqual(rejected.stickers.count, 10)

        // CHR-005: Undo max 20 steps
        let stack = UndoStack(maxCapacity: 20)
        stack.initialize(with: CharacterState())
        for i in 1...21 {
            stack.push(CharacterState(hairStyleId: "style_\(i)"))
        }
        XCTAssertEqual(stack.size, 20)
    }
}
