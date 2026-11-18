import SwiftUI
import UIKit
import GameCore

public enum AppScreen: Equatable {
    case boot
    case home
    case levelSelect
    case levelIntro
    case station
    case levelResult
    case partyMode
    case gallery
    case settings
    case shop
}

public struct StationResult {
    public let level: Level
    public let characterState: CharacterState
    public let evaluation: ScoreEvaluation
    public let engine: LevelEngine

    public init(
        level: Level,
        characterState: CharacterState,
        evaluation: ScoreEvaluation,
        engine: LevelEngine
    ) {
        self.level = level
        self.characterState = characterState
        self.evaluation = evaluation
        self.engine = engine
    }
}

// MARK: - 1. Boot Screen

public struct BootView: View {
    public let language: String
    public let onLoaded: () -> Void

    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @State private var progress: Double = 0.0

    private let mascotState = CharacterState(
        characterType: .unicorn,
        hairStyleId: "hair_long_waves",
        hairColorId: "hair_rainbow_pastel",
        wingsId: "wings_rainbow",
        hornId: "horn_gold",
        tiaraId: "tiara_star_gold"
    )

    public init(language: String, onLoaded: @escaping () -> Void) {
        self.language = language
        self.onLoaded = onLoaded
    }

    public var body: some View {
        ZStack {
            LinearGradient(
                colors: [
                    CandyColors.pastelPink,
                    CandyColors.mint.opacity(0.35),
                    CandyColors.yellow.opacity(0.45),
                    .white
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 24) {
                CharacterSpriteView(characterState: mascotState)
                    .frame(width: 170, height: 170)
                    .padding(10)
                    .background(Color.white.opacity(0.88))
                    .clipShape(Circle())
                    .shadow(color: .black.opacity(0.1), radius: 8, x: 0, y: 4)

                Text(L10n.string("app_name", language: language))
                    .font(.system(size: 26, weight: .heavy, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 24)

                ProgressView(value: progress, total: 1.0)
                    .tint(CandyColors.pink)
                    .frame(width: 200)
            }
        }
        .onAppear {
            if reduceMotion {
                progress = 1.0
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
                    onLoaded()
                }
            } else {
                withAnimation(.linear(duration: 0.75)) {
                    progress = 1.0
                }
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.8) {
                    onLoaded()
                }
            }
        }
    }
}

// MARK: - 2. Portrait Home Screen

public struct HomeView: View {
    public let candyBalance: Int
    public let language: String
    public let onStoryMode: () -> Void
    public let onPartyMode: () -> Void
    public let onGallery: () -> Void
    public let onSettings: () -> Void
    public let onShop: () -> Void

    private let mascotState = CharacterState(
        characterType: .unicorn,
        hairStyleId: "hair_long_waves",
        hairColorId: "hair_rainbow_pastel",
        makeup: MakeupState(
            eyeshadow: "eyeshadow_berry_pink",
            lipstick: "lipstick_bubblegum_pink",
            blush: "blush_rose_petal"
        ),
        outfitId: "outfit_ball_gown",
        wingsId: "wings_rainbow",
        hornId: "horn_gold",
        tiaraId: "tiara_star_gold"
    )

    public init(
        candyBalance: Int,
        language: String,
        onStoryMode: @escaping () -> Void,
        onPartyMode: @escaping () -> Void,
        onGallery: @escaping () -> Void,
        onSettings: @escaping () -> Void,
        onShop: @escaping () -> Void
    ) {
        self.candyBalance = candyBalance
        self.language = language
        self.onStoryMode = onStoryMode
        self.onPartyMode = onPartyMode
        self.onGallery = onGallery
        self.onSettings = onSettings
        self.onShop = onShop
    }

    public var body: some View {
        ZStack {
            LinearGradient(
                colors: [
                    CandyColors.pastelPink,
                    CandyColors.mint.opacity(0.38),
                    CandyColors.yellow.opacity(0.45),
                    .white
                ],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 14) {
                HStack {
                    Spacer()
                    CandyBalanceBadge(candyBalance: candyBalance, language: language)
                }

                Text("🦄 " + L10n.string("app_name", language: language) + " 🍭")
                    .font(.system(size: 24, weight: .heavy, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum)
                    .multilineTextAlignment(.center)
                    .fixedSize(horizontal: false, vertical: true)

                Spacer(minLength: 4)

                CharacterSpriteView(characterState: mascotState, pose: .wave)
                    .frame(width: 210, height: 210)
                    .padding(10)
                    .background(Color.white.opacity(0.78))
                    .clipShape(Circle())
                    .shadow(color: .black.opacity(0.1), radius: 8, x: 0, y: 4)

                Spacer(minLength: 4)

                VStack(spacing: 12) {
                    NavButton(
                        iconName: "play.fill",
                        label: L10n.string("btn_play", language: language),
                        backgroundColor: CandyColors.pink,
                        fullWidth: true,
                        action: onStoryMode
                    )

                    NavButton(
                        iconName: "sparkles",
                        label: L10n.string("btn_party", language: language),
                        backgroundColor: CandyColors.purple,
                        fullWidth: true,
                        action: onPartyMode
                    )

                    HStack(spacing: 8) {
                        NavButton(
                            iconName: "photo.on.rectangle",
                            label: L10n.string("btn_gallery", language: language),
                            backgroundColor: CandyColors.mint,
                            foregroundColor: CandyColors.deepPlum,
                            compact: true,
                            fullWidth: true,
                            action: onGallery
                        )
                        NavButton(
                            iconName: "cart.fill",
                            label: L10n.string("btn_shop", language: language),
                            backgroundColor: CandyColors.yellow,
                            foregroundColor: CandyColors.deepPlum,
                            compact: true,
                            fullWidth: true,
                            action: onShop
                        )
                        NavButton(
                            iconName: "gearshape.fill",
                            label: L10n.string("btn_settings", language: language),
                            backgroundColor: .white,
                            foregroundColor: CandyColors.deepPlum,
                            compact: true,
                            fullWidth: true,
                            action: onSettings
                        )
                    }
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
        }
    }
}

// MARK: - 3. Portrait Level Select Screen

public struct LevelSelectView: View {
    public let levels: [Level]
    public let language: String
    public let candyBalance: Int
    public let unlockedLevels: Set<Int>
    public let bestStars: [String: Int]
    public let hasPremium: Bool
    public let activeCheckpoint: SavedLevelCheckpoint?
    public let statusMessage: String?
    public let onClearStatus: () -> Void
    public let onSelectLevel: (Level, Bool) -> Void
    public let onBack: () -> Void
    public let onUnlockPremium: () -> Void

    @State private var showParentalGate = false
    @State private var levelToResumePrompt: Level?
    @State private var localToast: String?

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 10), count: 3)

    public init(
        levels: [Level],
        language: String,
        candyBalance: Int,
        unlockedLevels: Set<Int>,
        bestStars: [String: Int],
        hasPremium: Bool,
        activeCheckpoint: SavedLevelCheckpoint?,
        statusMessage: String?,
        onClearStatus: @escaping () -> Void,
        onSelectLevel: @escaping (Level, Bool) -> Void,
        onBack: @escaping () -> Void,
        onUnlockPremium: @escaping () -> Void
    ) {
        self.levels = levels
        self.language = language
        self.candyBalance = candyBalance
        self.unlockedLevels = unlockedLevels
        self.bestStars = bestStars
        self.hasPremium = hasPremium
        self.activeCheckpoint = activeCheckpoint
        self.statusMessage = statusMessage
        self.onClearStatus = onClearStatus
        self.onSelectLevel = onSelectLevel
        self.onBack = onBack
        self.onUnlockPremium = onUnlockPremium
    }

    public var body: some View {
        ZStack {
            LinearGradient(
                colors: [CandyColors.pastelPink.opacity(0.45), .white],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 10) {
                HStack {
                    NavButton(
                        iconName: "arrow.backward",
                        label: L10n.string("btn_back", language: language),
                        backgroundColor: CandyColors.pink,
                        compact: true,
                        action: onBack
                    )
                    Spacer()
                    CandyBalanceBadge(candyBalance: candyBalance, language: language)
                }

                Text(L10n.string("level_select_title", language: language))
                    .font(.system(size: 20, weight: .heavy, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum)

                if let banner = statusMessage ?? localToast {
                    Text(banner)
                        .font(.system(size: 13, weight: .bold, design: .rounded))
                        .foregroundColor(.white)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 8)
                        .background(CandyColors.purple)
                        .cornerRadius(14)
                        .onAppear {
                            DispatchQueue.main.asyncAfter(deadline: .now() + 2.5) {
                                localToast = nil
                                onClearStatus()
                            }
                        }
                }

                ScrollView {
                    LazyVGrid(columns: columns, spacing: 10) {
                        ForEach(levels) { lvl in
                            let isUnlocked = unlockedLevels.contains(lvl.id)
                            let isPremiumLocked = lvl.requiresPremium && !hasPremium
                            let stars = bestStars[String(lvl.id)] ?? 0
                            let hasCheckpoint = isUnlocked && activeCheckpoint?.levelId == lvl.id

                            Button(action: {
                                if isPremiumLocked {
                                    showParentalGate = true
                                } else if isUnlocked {
                                    if hasCheckpoint {
                                        levelToResumePrompt = lvl
                                    } else {
                                        onSelectLevel(lvl, false)
                                    }
                                } else {
                                    localToast = L10n.string("level_locked_hint", language: language)
                                }
                            }) {
                                VStack(spacing: 6) {
                                    HStack(spacing: 4) {
                                        Text(L10n.format("level_short_format", language: language, lvl.id))
                                            .font(.system(size: 11, weight: .heavy, design: .rounded))
                                            .foregroundColor(CandyColors.pink)
                                        Text(lvl.characterType == .unicorn ? "🦄" : "👸")
                                            .font(.system(size: 12))
                                    }

                                    Text(lvl.localizedName(language: language))
                                        .font(.system(size: 11, weight: .semibold, design: .rounded))
                                        .foregroundColor(CandyColors.deepPlum)
                                        .multilineTextAlignment(.center)
                                        .lineLimit(2)
                                        .minimumScaleFactor(0.85)

                                    Spacer(minLength: 0)

                                    if isPremiumLocked {
                                        HStack(spacing: 3) {
                                            Image(systemName: "lock.fill")
                                                .font(.system(size: 11))
                                                .foregroundColor(CandyColors.purple)
                                            Text("VIP")
                                                .font(.system(size: 11, weight: .bold, design: .rounded))
                                                .foregroundColor(CandyColors.purple)
                                        }
                                    } else if !isUnlocked {
                                        Image(systemName: "lock.fill")
                                            .font(.system(size: 14))
                                            .foregroundColor(.gray)
                                    } else {
                                        HStack(spacing: 2) {
                                            ForEach(1...3, id: \.self) { s in
                                                Image(systemName: "star.fill")
                                                    .font(.system(size: 11))
                                                    .foregroundColor(s <= stars ? CandyColors.yellow : Color.gray.opacity(0.35))
                                            }
                                        }
                                    }
                                }
                                .padding(8)
                                .frame(maxWidth: .infinity, minHeight: 110, maxHeight: 110)
                                .background(
                                    isPremiumLocked ? Color(red: 0.95, green: 0.90, blue: 0.96)
                                    : (isUnlocked ? Color.white : Color(white: 0.94))
                                )
                                .cornerRadius(18)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 18)
                                        .stroke(
                                            hasCheckpoint ? CandyColors.purple : (isUnlocked ? CandyColors.pink : Color.gray.opacity(0.35)),
                                            lineWidth: hasCheckpoint ? 2.5 : (isUnlocked ? 1.8 : 1)
                                        )
                                )
                                .shadow(color: .black.opacity(isUnlocked ? 0.08 : 0.0), radius: 3, x: 0, y: 2)
                            }
                        }
                    }
                    .padding(.vertical, 4)
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)

            if showParentalGate {
                ParentalGateModal(
                    language: language,
                    onVerified: {
                        showParentalGate = false
                        onUnlockPremium()
                    },
                    onDismiss: { showParentalGate = false }
                )
            }

            if let resumeLvl = levelToResumePrompt {
                ZStack {
                    Color.black.opacity(0.45).ignoresSafeArea()
                    VStack(spacing: 14) {
                        Text(L10n.format("dialog_resume_title", language: language, resumeLvl.id))
                            .font(.system(size: 20, weight: .bold, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum)
                            .multilineTextAlignment(.center)
                        Text(L10n.string("dialog_resume_message", language: language))
                            .font(.system(size: 14, weight: .regular, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum.opacity(0.85))
                            .multilineTextAlignment(.center)
                        NavButton(
                            iconName: "play.fill",
                            label: L10n.string("btn_resume", language: language),
                            backgroundColor: CandyColors.pink,
                            fullWidth: true,
                            action: {
                                levelToResumePrompt = nil
                                onSelectLevel(resumeLvl, true)
                            }
                        )
                        NavButton(
                            iconName: "arrow.clockwise",
                            label: L10n.string("btn_start_fresh", language: language),
                            backgroundColor: CandyColors.mint,
                            foregroundColor: CandyColors.deepPlum,
                            fullWidth: true,
                            action: {
                                levelToResumePrompt = nil
                                onSelectLevel(resumeLvl, false)
                            }
                        )
                        Button(action: { levelToResumePrompt = nil }) {
                            Text(L10n.string("btn_cancel", language: language))
                                .font(.system(size: 15, weight: .semibold, design: .rounded))
                                .foregroundColor(.gray)
                        }
                    }
                    .padding(22)
                    .background(Color.white)
                    .cornerRadius(24)
                    .padding(.horizontal, 24)
                    .frame(maxWidth: 400)
                }
            }
        }
    }
}

// MARK: - 4. Level Intro Screen

public struct LevelIntroView: View {
    public let level: Level
    public let language: String
    public let onStart: () -> Void
    public let onBack: () -> Void

    public init(
        level: Level,
        language: String,
        onStart: @escaping () -> Void,
        onBack: @escaping () -> Void
    ) {
        self.level = level
        self.language = language
        self.onStart = onStart
        self.onBack = onBack
    }

    public var body: some View {
        let preview = CharacterState(characterType: level.characterType)
        ZStack {
            LinearGradient(
                colors: [CandyColors.pastelPink, CandyColors.yellow.opacity(0.28), .white],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 12) {
                HStack {
                    NavButton(
                        iconName: "arrow.backward",
                        label: L10n.string("btn_back", language: language),
                        backgroundColor: CandyColors.pink,
                        compact: true,
                        action: onBack
                    )
                    Spacer()
                    Text(L10n.format("level_short_format", language: language, level.id))
                        .font(.system(size: 20, weight: .heavy, design: .rounded))
                        .foregroundColor(CandyColors.deepPlum)
                    Spacer()
                    Color.clear.frame(width: 72, height: 1)
                }

                ScrollView {
                    VStack(spacing: 14) {
                        Text(level.localizedName(language: language))
                            .font(.system(size: 24, weight: .heavy, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum)
                            .multilineTextAlignment(.center)

                        CharacterSpriteView(characterState: preview)
                            .frame(width: 150, height: 150)
                            .padding(8)
                            .background(Color.white.opacity(0.85))
                            .clipShape(Circle())
                            .shadow(radius: 5)

                        Text(L10n.string("level_stations_label", language: language))
                            .font(.system(size: 13, weight: .bold, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum.opacity(0.75))

                        LazyVGrid(columns: [GridItem(.adaptive(minimum: 105), spacing: 8)], spacing: 8) {
                            ForEach(level.stations, id: \.rawValue) { st in
                                Text(L10n.stationLabel(st, language: language))
                                    .font(.system(size: 12, weight: .bold, design: .rounded))
                                    .foregroundColor(CandyColors.deepPlum)
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 6)
                                    .frame(maxWidth: .infinity)
                                    .background(CandyColors.mint.opacity(0.55))
                                    .cornerRadius(14)
                            }
                        }

                        VStack(alignment: .leading, spacing: 10) {
                            Text("📋 " + L10n.string("level_request_card", language: language))
                                .font(.system(size: 17, weight: .heavy, design: .rounded))
                                .foregroundColor(CandyColors.deepPlum)

                            ForEach(level.criteria) { c in
                                HStack(alignment: .top, spacing: 8) {
                                    Image(systemName: "checkmark.circle.fill")
                                        .foregroundColor(CandyColors.pink)
                                        .font(.system(size: 18))
                                    Text(c.localizedDescription(language: language))
                                        .font(.system(size: 15, weight: .medium, design: .rounded))
                                        .foregroundColor(CandyColors.deepPlum)
                                        .fixedSize(horizontal: false, vertical: true)
                                }
                            }
                        }
                        .padding(18)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color.white.opacity(0.96))
                        .cornerRadius(22)
                        .shadow(color: .black.opacity(0.08), radius: 5, x: 0, y: 2)
                    }
                }

                NavButton(
                    iconName: "play.fill",
                    label: L10n.string("btn_start", language: language),
                    backgroundColor: CandyColors.pink,
                    fullWidth: true,
                    action: onStart
                )
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
        }
    }
}

// MARK: - 5. Interactive Portrait Station Gameplay Screen

public struct StationView: View {
    public let level: Level
    public let language: String
    public let categories: [ItemCategory]
    public let items: [Item]
    public let candyBalance: Int
    public let unlockedItems: Set<String>
    public let soundManager: SoundManager
    public let adProvider: AdProvider
    public let levelEngine: LevelEngine
    public let initialCheckpoint: SavedLevelCheckpoint?
    public let onUnlockItemWithCandy: (Item) -> Bool
    public let onUnlockItemWithAd: (Item) -> Void
    public let onCheckpointChanged: (SavedLevelCheckpoint) -> Void
    public let onLevelFinished: (StationResult) -> Void
    public let onExitConfirmed: (SavedLevelCheckpoint) -> Void

    @State private var characterState: CharacterState
    @State private var currentStationIndex: Int
    @State private var canUndo: Bool
    @State private var canRedo: Bool

    @State private var spaCleanPercent: Double
    @State private var spaResetTrigger: Int = 0
    @State private var cakeLayers: Int
    @State private var cakeFlavor: String?
    @State private var cakeToppings: [String]
    @State private var petFed: Bool
    @State private var petClean: Double
    @State private var petGroomed: Bool
    @State private var photoTaken: Bool
    @State private var photoBackdrop: StageBackdrop = .pastel
    @State private var photoPose: CharacterPose = .happy
    @State private var photoFlash: Bool = false

    @State private var showExitDialog: Bool = false
    @State private var showResetDialog: Bool = false
    @State private var showRequestDialog: Bool = false
    @State private var showCakeToppingModal: Bool = false
    @State private var showCakeFlavorModal: Bool = false
    @State private var activeModalCategory: ItemCategory?
    @State private var lockedItemPending: Item?
    @State private var bannerMessage: String?

    public init(
        level: Level,
        language: String,
        categories: [ItemCategory],
        items: [Item],
        candyBalance: Int,
        unlockedItems: Set<String>,
        soundManager: SoundManager,
        adProvider: AdProvider,
        levelEngine: LevelEngine,
        initialCheckpoint: SavedLevelCheckpoint?,
        onUnlockItemWithCandy: @escaping (Item) -> Bool,
        onUnlockItemWithAd: @escaping (Item) -> Void,
        onCheckpointChanged: @escaping (SavedLevelCheckpoint) -> Void,
        onLevelFinished: @escaping (StationResult) -> Void,
        onExitConfirmed: @escaping (SavedLevelCheckpoint) -> Void
    ) {
        self.level = level
        self.language = language
        self.categories = categories
        self.items = items
        self.candyBalance = candyBalance
        self.unlockedItems = unlockedItems
        self.soundManager = soundManager
        self.adProvider = adProvider
        self.levelEngine = levelEngine
        self.initialCheckpoint = initialCheckpoint
        self.onUnlockItemWithCandy = onUnlockItemWithCandy
        self.onUnlockItemWithAd = onUnlockItemWithAd
        self.onCheckpointChanged = onCheckpointChanged
        self.onLevelFinished = onLevelFinished
        self.onExitConfirmed = onExitConfirmed

        let outputs = levelEngine.currentStationOutputs
        _characterState = State(initialValue: levelEngine.characterState)
        _currentStationIndex = State(initialValue: levelEngine.currentStationIndex)
        _canUndo = State(initialValue: levelEngine.canUndo)
        _canRedo = State(initialValue: levelEngine.canRedo)
        _spaCleanPercent = State(initialValue: outputs[.spa]?.qualityScore ?? initialCheckpoint?.spaCleanPercent ?? 0.0)
        _cakeLayers = State(initialValue: outputs[.cake]?.cakeLayers ?? initialCheckpoint?.cakeLayers ?? 2)
        _cakeFlavor = State(initialValue: outputs[.cake]?.cakeFlavor ?? initialCheckpoint?.cakeFlavor ?? "cake_flavor_vanilla_rainbow")
        _cakeToppings = State(initialValue: outputs[.cake]?.cakeToppings ?? initialCheckpoint?.cakeToppings ?? [])
        _petFed = State(initialValue: outputs[.pet]?.petFed ?? initialCheckpoint?.petFed ?? false)
        _petClean = State(initialValue: outputs[.pet]?.petClean ?? initialCheckpoint?.petClean ?? 0.0)
        _petGroomed = State(initialValue: outputs[.pet]?.petGroomed ?? initialCheckpoint?.petGroomed ?? false)
        _photoTaken = State(initialValue: outputs[.photo]?.photoTaken ?? initialCheckpoint?.photoTaken ?? false)
    }

    private var currentStation: StationType {
        if currentStationIndex >= 0 && currentStationIndex < level.stations.count {
            return level.stations[currentStationIndex]
        }
        return level.stations.first ?? .spa
    }

    private var currentCategories: [ItemCategory] {
        categories.filter { cat in
            switch currentStation {
            case .hair: return ["hair_styles", "hair_colors"].contains(cat.id)
            case .makeup: return ["eyeshadows", "lipsticks", "blushes", "face_paints"].contains(cat.id)
            case .dressup: return ["outfits", "wings", "horns", "tiaras", "jewelry", "stickers"].contains(cat.id)
            case .cake: return ["cake_flavors", "cake_toppings"].contains(cat.id)
            default: return false
            }
        }
    }

    private func syncUndo() {
        canUndo = levelEngine.canUndo
        canRedo = levelEngine.canRedo
    }

    private func buildCheckpoint() -> SavedLevelCheckpoint {
        return levelEngine.toCheckpoint(
            spaCleanPercent: spaCleanPercent,
            cakeLayers: cakeLayers,
            cakeFlavor: cakeFlavor,
            cakeToppings: cakeToppings,
            petFed: petFed,
            petClean: petClean,
            petGroomed: petGroomed,
            photoTaken: photoTaken
        )
    }

    private func buildOutput(for st: StationType) -> StationOutput {
        switch st {
        case .spa:
            return StationOutput(stationType: .spa, isCompleted: true, qualityScore: min(max(spaCleanPercent, 0.0), 1.0))
        case .cake:
            let tierScore = (Double(min(max(cakeLayers, 1), 4)) / 4.0) * 0.4
            let flavScore = cakeFlavor != nil ? 0.3 : 0.0
            let topScore = (Double(min(cakeToppings.count, 3)) / 3.0) * 0.3
            let q = min(max(tierScore + flavScore + topScore, 0.2), 1.0)
            return StationOutput(
                stationType: .cake,
                isCompleted: true,
                qualityScore: q,
                cakeLayers: cakeLayers,
                cakeFlavor: cakeFlavor,
                cakeToppings: cakeToppings
            )
        case .pet:
            let score = (petFed ? 0.35 : 0.0) + (petClean * 0.35) + (petGroomed ? 0.30 : 0.0)
            return StationOutput(
                stationType: .pet,
                isCompleted: true,
                qualityScore: min(max(score, 0.1), 1.0),
                petFed: petFed,
                petClean: petClean,
                petGroomed: petGroomed
            )
        case .photo:
            return StationOutput(
                stationType: .photo,
                isCompleted: true,
                qualityScore: photoTaken ? 1.0 : 0.6,
                photoTaken: photoTaken
            )
        default:
            return StationOutput(stationType: st, isCompleted: true, qualityScore: 1.0)
        }
    }

    private func showToast(_ text: String) {
        bannerMessage = text
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.2) {
            if bannerMessage == text {
                bannerMessage = nil
            }
        }
    }

    private func equipItem(_ item: Item) {
        if item.category == "cake_flavors" {
            cakeFlavor = (cakeFlavor == item.id) ? nil : item.id
            onCheckpointChanged(buildCheckpoint())
            return
        }
        if item.category == "cake_toppings" {
            if let idx = cakeToppings.firstIndex(of: item.id) {
                cakeToppings.remove(at: idx)
            } else if cakeToppings.count < 8 {
                cakeToppings.append(item.id)
            }
            onCheckpointChanged(buildCheckpoint())
            return
        }
        if item.category == "stickers" {
            if let idx = characterState.stickers.firstIndex(of: item.id) {
                levelEngine.updateCharacter { c in
                    var st = c.stickers
                    st.remove(at: idx)
                    c.stickers = st
                }
                characterState = levelEngine.characterState
                syncUndo()
                onCheckpointChanged(buildCheckpoint())
                return
            }
            if characterState.stickers.count >= LayerComposer.maxStickers {
                showToast(L10n.string("sticker_limit_banner", language: language))
                return
            }
        }

        levelEngine.updateCharacter { c in
            switch item.category {
            case "hair_styles":
                c.hairStyleId = (c.hairStyleId == item.id) ? nil : item.id
            case "hair_colors":
                c.hairColorId = (c.hairColorId == item.id) ? nil : item.id
            case "eyeshadows":
                if c.makeup.eyeshadow == item.id {
                    c.makeup.eyeshadow = nil
                } else {
                    c = LayerComposer.applyEyeshadow(character: c, eyeshadowId: item.id)
                }
            case "lipsticks":
                c.makeup.lipstick = (c.makeup.lipstick == item.id) ? nil : item.id
            case "blushes":
                c.makeup.blush = (c.makeup.blush == item.id) ? nil : item.id
            case "face_paints":
                c = LayerComposer.toggleFacePaint(character: c, paintId: item.id)
            case "outfits":
                c.outfitId = (c.outfitId == item.id) ? nil : item.id
            case "wings":
                c.wingsId = (c.wingsId == item.id) ? nil : item.id
            case "horns":
                c.hornId = (c.hornId == item.id) ? nil : item.id
            case "tiaras":
                c.tiaraId = (c.tiaraId == item.id) ? nil : item.id
            case "jewelry":
                c.jewelryId = (c.jewelryId == item.id) ? nil : item.id
            case "stickers":
                c = LayerComposer.addSticker(character: c, stickerId: item.id).0
            default: break
            }
        }
        characterState = levelEngine.characterState
        syncUndo()
        onCheckpointChanged(buildCheckpoint())
    }

    private func selectedIds(for catId: String) -> Set<String> {
        switch catId {
        case "hair_styles": return characterState.hairStyleId.map { [$0] } ?? []
        case "hair_colors": return characterState.hairColorId.map { [$0] } ?? []
        case "eyeshadows": return characterState.makeup.eyeshadow.map { [$0] } ?? []
        case "lipsticks": return characterState.makeup.lipstick.map { [$0] } ?? []
        case "blushes": return characterState.makeup.blush.map { [$0] } ?? []
        case "face_paints": return Set(characterState.facePaints)
        case "outfits": return characterState.outfitId.map { [$0] } ?? []
        case "wings": return characterState.wingsId.map { [$0] } ?? []
        case "horns": return characterState.hornId.map { [$0] } ?? []
        case "tiaras": return characterState.tiaraId.map { [$0] } ?? []
        case "jewelry": return characterState.jewelryId.map { [$0] } ?? []
        case "stickers": return Set(characterState.stickers)
        case "cake_flavors": return cakeFlavor.map { [$0] } ?? []
        case "cake_toppings": return Set(cakeToppings)
        default: return []
        }
    }

    private func clearCategory(_ cat: ItemCategory) {
        switch cat.id {
        case "cake_toppings":
            cakeToppings = []
            onCheckpointChanged(buildCheckpoint())
        case "cake_flavors":
            cakeFlavor = nil
            onCheckpointChanged(buildCheckpoint())
        default:
            levelEngine.updateCharacter { c in
                switch cat.id {
                case "hair_styles": c.hairStyleId = nil
                case "hair_colors": c.hairColorId = nil
                case "eyeshadows": c.makeup.eyeshadow = nil
                case "lipsticks": c.makeup.lipstick = nil
                case "blushes": c.makeup.blush = nil
                case "face_paints": c.facePaints = []
                case "outfits": c.outfitId = nil
                case "wings": c.wingsId = nil
                case "horns": c.hornId = nil
                case "tiaras": c.tiaraId = nil
                case "jewelry": c.jewelryId = nil
                case "stickers": c.stickers = []
                default: break
                }
            }
            characterState = levelEngine.characterState
            syncUndo()
            onCheckpointChanged(buildCheckpoint())
        }
    }

    private func instructionKey(for st: StationType) -> String {
        switch st {
        case .spa: return "spa_instruction"
        case .hair: return "hair_instruction"
        case .makeup: return "makeup_instruction"
        case .dressup: return "dressup_instruction"
        case .cake: return "cake_instruction"
        case .pet: return "pet_instruction"
        case .photo: return "photo_instruction"
        }
    }

    public var body: some View {
        var liveOutputs = levelEngine.currentStationOutputs
        liveOutputs[currentStation] = buildOutput(for: currentStation)
        let criteriaResults: [String: Bool] = Dictionary(uniqueKeysWithValues: level.criteria.map { c in
            (c.id, ScoringEngine.evaluateCriterion(c, character: characterState, outputs: liveOutputs))
        })
        let metCount = criteriaResults.values.filter { $0 }.count

        return ZStack {
            LinearGradient(
                colors: [CandyColors.pastelPink.opacity(0.25), .white],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 6) {
                // PORTRAIT TOP HUD ROW 1 (Generous safe-area padding for punch-hole cameras)
                HStack(spacing: 8) {
                    NavButton(
                        iconName: "arrow.backward",
                        label: L10n.string("btn_exit", language: language),
                        backgroundColor: CandyColors.pink,
                        compact: true,
                        action: {
                            soundManager.playClick()
                            showExitDialog = true
                        }
                    )

                    Button(action: {
                        soundManager.playClick()
                        showRequestDialog = true
                    }) {
                        HStack(spacing: 5) {
                            Image(systemName: "list.bullet.rectangle")
                                .font(.system(size: 14, weight: .bold))
                            Text(L10n.format("criteria_progress_format", language: language, metCount, level.criteria.count))
                                .font(.system(size: 12, weight: .bold, design: .rounded))
                                .lineLimit(1)
                                .minimumScaleFactor(0.8)
                        }
                        .frame(maxWidth: .infinity, minHeight: 48)
                        .padding(.horizontal, 10)
                        .background(metCount == level.criteria.count ? CandyColors.mint : .white)
                        .foregroundColor(CandyColors.deepPlum)
                        .cornerRadius(20)
                        .shadow(color: .black.opacity(0.08), radius: 2, x: 0, y: 1)
                    }

                    NavButton(
                        iconName: "checkmark",
                        label: L10n.string("btn_done", language: language),
                        backgroundColor: CandyColors.mint,
                        foregroundColor: CandyColors.deepPlum,
                        compact: true,
                        action: {
                            soundManager.playClick()
                            if currentStation == .spa && spaCleanPercent < 0.80 {
                                showToast(L10n.string("spa_need_more", language: language))
                                return
                            }
                            soundManager.playSparkle()
                            let out = buildOutput(for: currentStation)
                            levelEngine.completeCurrentStation(out)
                            onCheckpointChanged(buildCheckpoint())

                            let firstIncomplete = level.stations.firstIndex {
                                levelEngine.getStationStatus($0) != .completed
                            }

                            if firstIncomplete == nil {
                                let eval = levelEngine.finishLevel()
                                onLevelFinished(StationResult(
                                    level: level,
                                    characterState: characterState,
                                    evaluation: eval,
                                    engine: levelEngine
                                ))
                            } else if let fallback = firstIncomplete {
                                let nextIdx = levelEngine.currentStationIndex
                                let targetIdx = (nextIdx < level.stations.count && levelEngine.getStationStatus(level.stations[nextIdx]) != .completed)
                                    ? nextIdx
                                    : fallback
                                levelEngine.selectStation(index: targetIdx)
                                currentStationIndex = targetIdx
                            }
                        }
                    )
                }
                .padding(.horizontal, 10)
                .padding(.top, 14)

                // PORTRAIT TOP HUD ROW 2: Dedicated Full-Width Scrollable Station Steps (Zero Truncation)
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(Array(level.stations.enumerated()), id: \.offset) { idx, st in
                            let status = levelEngine.getStationStatus(st)
                            let isCurr = idx == currentStationIndex
                            Button(action: {
                                levelEngine.selectStation(index: idx)
                                currentStationIndex = idx
                            }) {
                                let prefix = status == .completed ? "✓ " : "\(idx + 1). "
                                Text(prefix + L10n.stationLabel(st, language: language))
                                    .font(.system(size: 12, weight: .bold, design: .rounded))
                                    .foregroundColor(isCurr ? .white : CandyColors.deepPlum)
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 8)
                                    .background(
                                        isCurr ? CandyColors.purple
                                        : (status == .completed ? CandyColors.mint.opacity(0.65) : Color.white.opacity(0.9))
                                    )
                                    .cornerRadius(16)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 16)
                                            .stroke(isCurr ? CandyColors.deepPlum : Color.gray.opacity(0.3), lineWidth: isCurr ? 2 : 1)
                                    )
                            }
                        }
                    }
                    .padding(.horizontal, 10)
                }

                // PORTRAIT TOP HUD ROW 3: Station Prompt + Action Controls (Undo, Redo, Reset with >= 44pt touch targets)
                HStack(spacing: 6) {
                    Text(L10n.string(instructionKey(for: currentStation), language: language))
                        .font(.system(size: 11, weight: .medium, design: .rounded))
                        .foregroundColor(CandyColors.deepPlum.opacity(0.85))
                        .lineLimit(2)
                        .frame(maxWidth: .infinity, alignment: .leading)

                    if currentStation == .makeup {
                        Button(action: {
                            soundManager.playClick()
                            levelEngine.updateCharacter { $0 = LayerComposer.clearMakeup(character: $0) }
                            characterState = levelEngine.characterState
                            syncUndo()
                            onCheckpointChanged(buildCheckpoint())
                        }) {
                            HStack(spacing: 4) {
                                Image(systemName: "sparkles")
                                    .font(.system(size: 11))
                                Text(L10n.string("makeup_remover", language: language))
                                    .font(.system(size: 11, weight: .bold, design: .rounded))
                            }
                            .foregroundColor(CandyColors.deepPlum)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 6)
                            .background(CandyColors.pastelPink)
                            .cornerRadius(14)
                        }
                    }

                    NavButton(
                        iconName: "arrow.uturn.backward",
                        label: L10n.string("btn_undo", language: language),
                        backgroundColor: .white,
                        foregroundColor: CandyColors.deepPlum,
                        isEnabled: true,
                        compact: true,
                        action: {
                            soundManager.playClick()
                            if levelEngine.undo() {
                                characterState = levelEngine.characterState
                                syncUndo()
                                onCheckpointChanged(buildCheckpoint())
                            }
                        }
                    )

                    NavButton(
                        iconName: "arrow.uturn.forward",
                        label: L10n.string("btn_redo", language: language),
                        backgroundColor: .white,
                        foregroundColor: CandyColors.deepPlum,
                        isEnabled: true,
                        compact: true,
                        action: {
                            soundManager.playClick()
                            if levelEngine.redo() {
                                characterState = levelEngine.characterState
                                syncUndo()
                                onCheckpointChanged(buildCheckpoint())
                            }
                        }
                    )

                    NavButton(
                        iconName: "arrow.clockwise",
                        label: L10n.string("btn_reset", language: language),
                        backgroundColor: CandyColors.yellow,
                        foregroundColor: CandyColors.deepPlum,
                        compact: true,
                        action: { showResetDialog = true }
                    )
                }
                .padding(.horizontal, 10)

                // CENTRAL PORTRAIT STAGE
                Group {
                    switch currentStation {
                    case .spa:
                        SpaStationView(
                            characterState: characterState,
                            language: language,
                            resetTrigger: spaResetTrigger,
                            onCleanPercentChanged: { pct in
                                spaCleanPercent = pct
                                soundManager.playBubble()
                            }
                        )
                    case .cake:
                        CakeStationView(
                            layers: cakeLayers,
                            flavor: cakeFlavor,
                            toppings: cakeToppings,
                            language: language,
                            onLayersChanged: { newLayers in
                                cakeLayers = newLayers
                                soundManager.playClick()
                                onCheckpointChanged(buildCheckpoint())
                            },
                            onClearToppings: {
                                cakeToppings = []
                                soundManager.playClick()
                                onCheckpointChanged(buildCheckpoint())
                            },
                            onRemoveTopping: { topId in
                                if let idx = cakeToppings.firstIndex(of: topId) {
                                    cakeToppings.remove(at: idx)
                                    soundManager.playClick()
                                    onCheckpointChanged(buildCheckpoint())
                                }
                            },
                            onOpenAddModal: {
                                soundManager.playClick()
                                showCakeToppingModal = true
                            },
                            onOpenFlavorModal: {
                                soundManager.playClick()
                                showCakeFlavorModal = true
                            }
                        )
                    case .pet:
                        PetStationView(
                            petFed: petFed,
                            petClean: petClean,
                            petGroomed: petGroomed,
                            language: language,
                            onFeedPet: {
                                petFed = true
                                soundManager.playSparkle()
                                onCheckpointChanged(buildCheckpoint())
                            },
                            onWashPetStep: {
                                petClean = min(petClean + 0.34, 1.0)
                                soundManager.playBubble()
                                onCheckpointChanged(buildCheckpoint())
                            },
                            onGroomPet: {
                                petGroomed = true
                                soundManager.playClick()
                                onCheckpointChanged(buildCheckpoint())
                            }
                        )
                    case .photo:
                        VStack(spacing: 10) {
                            ZStack {
                                CharacterSpriteView(
                                    characterState: characterState,
                                    backdrop: photoBackdrop,
                                    pose: photoPose,
                                    drawBackdrop: true
                                )
                                .cornerRadius(22)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 22)
                                        .stroke(CandyColors.pastelPink, lineWidth: 2)
                                )

                                if photoFlash {
                                    Color.white.opacity(0.85)
                                        .cornerRadius(22)
                                }
                            }
                            .frame(maxWidth: .infinity, maxHeight: .infinity)

                            HStack(spacing: 8) {
                                let allBgs = StageBackdrop.allCases
                                Button(action: {
                                    let idx = (allBgs.firstIndex(of: photoBackdrop) ?? 0) + 1
                                    photoBackdrop = allBgs[idx % allBgs.count]
                                    soundManager.playClick()
                                }) {
                                    Text(L10n.string("backdrop_\(photoBackdrop.rawValue)", language: language))
                                        .font(.system(size: 13, weight: .bold, design: .rounded))
                                        .frame(maxWidth: .infinity, minHeight: 44)
                                        .background(CandyColors.purple)
                                        .foregroundColor(.white)
                                        .cornerRadius(16)
                                }

                                Button(action: {
                                    let poses: [CharacterPose] = [.happy, .star, .wave]
                                    let idx = (poses.firstIndex(of: photoPose) ?? 0) + 1
                                    photoPose = poses[idx % poses.count]
                                    soundManager.playClick()
                                }) {
                                    Text(L10n.string("photo_pose_\(photoPose.rawValue)", language: language))
                                        .font(.system(size: 13, weight: .bold, design: .rounded))
                                        .frame(maxWidth: .infinity, minHeight: 44)
                                        .background(CandyColors.yellow)
                                        .foregroundColor(CandyColors.deepPlum)
                                        .cornerRadius(16)
                                }
                            }

                            NavButton(
                                iconName: "camera.fill",
                                label: L10n.string("btn_take_photo", language: language),
                                backgroundColor: CandyColors.pink,
                                fullWidth: true,
                                action: {
                                    photoTaken = true
                                    photoFlash = true
                                    soundManager.playShutter()
                                    onCheckpointChanged(buildCheckpoint())
                                    showToast(L10n.string("photo_saved_banner", language: language))
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.18) {
                                        photoFlash = false
                                    }
                                }
                            )
                        }
                        .padding(.horizontal, 12)
                    default:
                        CharacterSpriteView(characterState: characterState, pose: .idle)
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                            .padding(.horizontal, 12)
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)

                // BOTTOM TRAY
                if !currentCategories.isEmpty {
                    BottomItemTray(
                        categories: currentCategories,
                        items: items,
                        language: language,
                        unlockedItems: unlockedItems,
                        selectedIdsForCategory: { catId in selectedIds(for: catId) },
                        onItemSelected: { item in
                            soundManager.playClick()
                            equipItem(item)
                        },
                        onLockedItemClicked: { item in
                            soundManager.playClick()
                            lockedItemPending = item
                        },
                        onClearCategory: { cat in
                            soundManager.playClick()
                            clearCategory(cat)
                        },
                        onOpenFullModal: { cat in
                            soundManager.playClick()
                            activeModalCategory = cat
                        }
                    )
                }
            }

            if let toast = bannerMessage {
                VStack {
                    Text(toast)
                        .font(.system(size: 13, weight: .bold, design: .rounded))
                        .foregroundColor(.white)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 8)
                        .background(CandyColors.deepPlum.opacity(0.9))
                        .cornerRadius(16)
                        .padding(.top, 56)
                    Spacer()
                }
            }

            if showRequestDialog {
                ZStack {
                    Color.black.opacity(0.45).ignoresSafeArea()
                    VStack(alignment: .leading, spacing: 12) {
                        Text("📋 " + L10n.string("level_request_card", language: language))
                            .font(.system(size: 20, weight: .bold, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum)

                        ForEach(level.criteria) { criterion in
                            let isMet = criteriaResults[criterion.id] == true
                            HStack(alignment: .top, spacing: 8) {
                                Image(systemName: isMet ? "checkmark.circle.fill" : "circle")
                                    .foregroundColor(isMet ? CandyColors.mint : .gray.opacity(0.5))
                                    .font(.system(size: 18))
                                Text(criterion.localizedDescription(language: language))
                                    .font(.system(size: 15, weight: isMet ? .bold : .regular, design: .rounded))
                                    .foregroundColor(isMet ? CandyColors.deepPlum : CandyColors.deepPlum.opacity(0.7))
                            }
                        }

                        NavButton(
                            iconName: "xmark",
                            label: L10n.string("btn_close", language: language),
                            backgroundColor: CandyColors.pink,
                            fullWidth: true,
                            action: { showRequestDialog = false }
                        )
                    }
                    .padding(22)
                    .background(Color.white)
                    .cornerRadius(24)
                    .padding(.horizontal, 20)
                    .frame(maxWidth: 420)
                }
            }

            if let item = lockedItemPending {
                UnlockItemDialog(
                    item: item,
                    language: language,
                    candyBalance: candyBalance,
                    onUnlockWithCandies: {
                        if onUnlockItemWithCandy(item) {
                            soundManager.playSparkle()
                            equipItem(item)
                            lockedItemPending = nil
                        }
                    },
                    onUnlockWithAd: {
                        adProvider.showRewarded { earned in
                            if earned {
                                soundManager.playSparkle()
                                onUnlockItemWithAd(item)
                                equipItem(item)
                            }
                            lockedItemPending = nil
                        }
                    },
                    onDismiss: { lockedItemPending = nil }
                )
            }

            if showCakeToppingModal {
                let toppingItems = items.filter { $0.category == "cake_toppings" }
                ItemSelectionModal(
                    title: L10n.string("title_select_topping", language: language),
                    items: toppingItems,
                    language: language,
                    unlockedItems: unlockedItems,
                    selectedIds: Set(cakeToppings),
                    onItemSelected: { item in
                        soundManager.playClick()
                        equipItem(item)
                    },
                    onLockedItemClicked: { item in
                        soundManager.playClick()
                        lockedItemPending = item
                    },
                    onDismiss: { showCakeToppingModal = false }
                )
            }

            if showCakeFlavorModal {
                let flavorItems = items.filter { $0.category == "cake_flavors" }
                ItemSelectionModal(
                    title: L10n.string("title_select_flavor", language: language),
                    items: flavorItems,
                    language: language,
                    unlockedItems: unlockedItems,
                    selectedIds: cakeFlavor.map { [$0] } ?? [],
                    onItemSelected: { item in
                        soundManager.playClick()
                        equipItem(item)
                    },
                    onLockedItemClicked: { item in
                        soundManager.playClick()
                        lockedItemPending = item
                    },
                    onDismiss: { showCakeFlavorModal = false }
                )
            }

            if let modalCat = activeModalCategory {
                let catItems = items.filter { $0.category == modalCat.id }
                ItemSelectionModal(
                    title: modalCat.localizedName(language: language),
                    items: catItems,
                    language: language,
                    unlockedItems: unlockedItems,
                    selectedIds: selectedIds(for: modalCat.id),
                    onItemSelected: { item in
                        soundManager.playClick()
                        equipItem(item)
                    },
                    onLockedItemClicked: { item in
                        soundManager.playClick()
                        lockedItemPending = item
                    },
                    onDismiss: { activeModalCategory = nil }
                )
            }

            if showExitDialog {
                ConfirmDialog(
                    title: L10n.string("dialog_exit_title", language: language),
                    message: L10n.string("dialog_exit_message", language: language),
                    confirmText: L10n.string("btn_confirm", language: language),
                    cancelText: L10n.string("btn_cancel", language: language),
                    onConfirm: {
                        showExitDialog = false
                        onExitConfirmed(buildCheckpoint())
                    },
                    onDismiss: { showExitDialog = false }
                )
            }

            if showResetDialog {
                ConfirmDialog(
                    title: L10n.string("dialog_reset_title", language: language),
                    message: L10n.string("dialog_reset_message", language: language),
                    confirmText: L10n.string("btn_confirm", language: language),
                    cancelText: L10n.string("btn_cancel", language: language),
                    onConfirm: {
                        showResetDialog = false
                        characterState = levelEngine.resetCurrentStation()
                        switch currentStation {
                        case .spa:
                            spaCleanPercent = 0.0
                            spaResetTrigger += 1
                        case .cake:
                            cakeLayers = 2
                            cakeFlavor = "cake_flavor_vanilla_rainbow"
                            cakeToppings = []
                        case .pet:
                            petFed = false
                            petClean = 0.0
                            petGroomed = false
                        case .photo:
                            photoTaken = false
                            photoPose = .happy
                        default: break
                        }
                        syncUndo()
                        soundManager.playClick()
                        onCheckpointChanged(buildCheckpoint())
                    },
                    onDismiss: { showResetDialog = false }
                )
            }
        }
    }
}

// MARK: - 6. Portrait Level Result Screen

public struct LevelResultView: View {
    public let characterState: CharacterState
    public let evaluation: ScoreEvaluation
    public let candyEarned: Int
    public let language: String
    public let soundManager: SoundManager
    public let onNextLevel: () -> Void
    public let onEditLook: () -> Void
    public let onReplay: () -> Void
    public let onBackToMap: () -> Void

    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    public init(
        characterState: CharacterState,
        evaluation: ScoreEvaluation,
        candyEarned: Int,
        language: String,
        soundManager: SoundManager,
        onNextLevel: @escaping () -> Void,
        onEditLook: @escaping () -> Void,
        onReplay: @escaping () -> Void,
        onBackToMap: @escaping () -> Void
    ) {
        self.characterState = characterState
        self.evaluation = evaluation
        self.candyEarned = candyEarned
        self.language = language
        self.soundManager = soundManager
        self.onNextLevel = onNextLevel
        self.onEditLook = onEditLook
        self.onReplay = onReplay
        self.onBackToMap = onBackToMap
    }

    public var body: some View {
        ZStack {
            LinearGradient(
                colors: [CandyColors.pastelPink.opacity(0.45), .white],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            if evaluation.isWin {
                ConfettiOverlayView(reduceMotion: reduceMotion)
                    .ignoresSafeArea()
                    .allowsHitTesting(false)
            }

            VStack {
                HStack {
                    NavButton(
                        iconName: "arrow.backward",
                        label: L10n.string("btn_back", language: language),
                        backgroundColor: CandyColors.pink,
                        compact: true,
                        action: onBackToMap
                    )
                    Spacer()
                }
                .padding(.horizontal, 16)
                .padding(.top, 12)
                Spacer()
            }

            VStack(spacing: 12) {
                Text(L10n.string(evaluation.isWin ? "level_victory" : "level_try_again", language: language))
                    .font(.system(size: 24, weight: .heavy, design: .rounded))
                    .foregroundColor(evaluation.isWin ? CandyColors.pink : CandyColors.purple)
                    .multilineTextAlignment(.center)

                CharacterSpriteView(
                    characterState: characterState,
                    pose: evaluation.isWin ? .happy : .idle
                )
                .frame(width: 132, height: 132)

                HStack(spacing: 8) {
                    ForEach(1...3, id: \.self) { s in
                        Image(systemName: "star.fill")
                            .font(.system(size: 36))
                            .foregroundColor(s <= evaluation.stars ? CandyColors.yellow : Color.gray.opacity(0.3))
                    }
                }

                Text(L10n.format("score_total", language: language, evaluation.totalScore))
                    .font(.system(size: 18, weight: .bold, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum)
                Text(L10n.format("score_match", language: language, Int((evaluation.matchScore * 100).rounded())))
                    .font(.system(size: 14, weight: .medium, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum.opacity(0.75))
                Text(L10n.format("score_quality", language: language, Int((evaluation.qualityScore * 100).rounded())))
                    .font(.system(size: 14, weight: .medium, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum.opacity(0.75))

                if !evaluation.isWin {
                    Text(L10n.string("level_try_again_hint", language: language))
                        .font(.system(size: 13, weight: .medium, design: .rounded))
                        .foregroundColor(CandyColors.purple)
                        .multilineTextAlignment(.center)
                }

                if candyEarned > 0 {
                    Text("🍬 " + L10n.format("candy_earned", language: language, candyEarned))
                        .font(.system(size: 18, weight: .heavy, design: .rounded))
                        .foregroundColor(CandyColors.pink)
                }

                HStack(spacing: 10) {
                    NavButton(
                        iconName: "pencil",
                        label: L10n.string("btn_edit_look", language: language),
                        backgroundColor: CandyColors.yellow,
                        foregroundColor: CandyColors.deepPlum,
                        compact: true,
                        fullWidth: true,
                        action: onEditLook
                    )
                    NavButton(
                        iconName: "arrow.clockwise",
                        label: L10n.string("btn_replay", language: language),
                        backgroundColor: Color(white: 0.88),
                        foregroundColor: CandyColors.deepPlum,
                        compact: true,
                        fullWidth: true,
                        action: onReplay
                    )
                }

                if evaluation.isWin {
                    NavButton(
                        iconName: "play.fill",
                        label: L10n.string("btn_next", language: language),
                        backgroundColor: CandyColors.mint,
                        foregroundColor: CandyColors.deepPlum,
                        fullWidth: true,
                        action: onNextLevel
                    )
                } else {
                    NavButton(
                        iconName: "arrow.backward",
                        label: L10n.string("btn_exit", language: language),
                        backgroundColor: CandyColors.pastelPink,
                        foregroundColor: CandyColors.deepPlum,
                        fullWidth: true,
                        action: onBackToMap
                    )
                }
            }
            .padding(22)
            .background(Color.white.opacity(0.96))
            .cornerRadius(28)
            .padding(.horizontal, 16)
            .frame(maxWidth: 440)
            .shadow(color: .black.opacity(0.12), radius: 10, x: 0, y: 4)
        }
        .onAppear {
            if evaluation.isWin {
                soundManager.playVictory()
            }
        }
    }
}

private struct ConfettiOverlayView: View {
    let reduceMotion: Bool
    private let seeds: [(Double, Double, Int)] = (0..<120).map { i in
        let x = Double((i * 73) % 100) / 100.0
        let y = Double((i * 37) % 100) / 100.0
        return (x, y, i % 4)
    }

    var body: some View {
        TimelineView(.animation(minimumInterval: 1.0 / 60.0, paused: reduceMotion)) { timeline in
            let elapsed = reduceMotion ? 0.0 : timeline.date.timeIntervalSinceReferenceDate
            let phase = (elapsed * 0.28).truncatingRemainder(dividingBy: 1.0)
            Canvas { context, size in
                let palette: [Color] = [CandyColors.pink, CandyColors.yellow, CandyColors.mint, CandyColors.purple]
                for (nx, ny, cIdx) in seeds {
                    let yFrac = (ny + phase).truncatingRemainder(dividingBy: 1.0)
                    let rect = CGRect(x: nx * size.width - 5, y: yFrac * size.height - 5, width: 10, height: 10)
                    context.fill(Path(ellipseIn: rect), with: .color(palette[cIdx].opacity(0.8)))
                }
            }
        }
    }
}

// MARK: - 7. Portrait Party Mode Screen

public struct PartyModeView: View {
    public let categories: [ItemCategory]
    public let items: [Item]
    public let language: String
    public let candyBalance: Int
    public let unlockedItems: Set<String>
    public let galleryCount: Int
    public let soundManager: SoundManager
    public let adProvider: AdProvider
    public let onUnlockItemWithCandy: (Item) -> Bool
    public let onUnlockItemWithAd: (Item) -> Void
    public let onSavePhotoToGallery: (UIImage) -> Bool
    public let onBack: () -> Void

    @State private var undoStack = UndoStack(maxCapacity: 20)
    @State private var characterState = CharacterState(characterType: .unicorn)
    @State private var canUndo = false
    @State private var canRedo = false
    @State private var backdrop: StageBackdrop = .pastel
    @State private var photoFlash = false
    @State private var showResetConfirm = false
    @State private var showGalleryFull = false
    @State private var lockedItemPending: Item?
    @State private var toastMessage: String?

    public init(
        categories: [ItemCategory],
        items: [Item],
        language: String,
        candyBalance: Int,
        unlockedItems: Set<String>,
        galleryCount: Int,
        soundManager: SoundManager,
        adProvider: AdProvider,
        onUnlockItemWithCandy: @escaping (Item) -> Bool,
        onUnlockItemWithAd: @escaping (Item) -> Void,
        onSavePhotoToGallery: @escaping (UIImage) -> Bool,
        onBack: @escaping () -> Void
    ) {
        self.categories = categories
        self.items = items
        self.language = language
        self.candyBalance = candyBalance
        self.unlockedItems = unlockedItems
        self.galleryCount = galleryCount
        self.soundManager = soundManager
        self.adProvider = adProvider
        self.onUnlockItemWithCandy = onUnlockItemWithCandy
        self.onUnlockItemWithAd = onUnlockItemWithAd
        self.onSavePhotoToGallery = onSavePhotoToGallery
        self.onBack = onBack
    }

    private var partyCategories: [ItemCategory] {
        categories.filter { !["cake_flavors", "cake_toppings"].contains($0.id) }
    }

    private func commit(_ newState: CharacterState) {
        characterState = newState
        undoStack.push(newState)
        canUndo = undoStack.canUndo
        canRedo = undoStack.canRedo
    }

    private func showToast(_ text: String) {
        toastMessage = text
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.2) {
            if toastMessage == text { toastMessage = nil }
        }
    }

    private func equip(_ item: Item) {
        if item.category == "stickers" && characterState.stickers.count >= LayerComposer.maxStickers {
            showToast(L10n.string("sticker_limit_banner", language: language))
            return
        }
        var next = characterState
        switch item.category {
        case "hair_styles": next.hairStyleId = item.id
        case "hair_colors": next.hairColorId = item.id
        case "eyeshadows": next = LayerComposer.applyEyeshadow(character: next, eyeshadowId: item.id)
        case "lipsticks": next.makeup.lipstick = item.id
        case "blushes": next.makeup.blush = item.id
        case "face_paints": next = LayerComposer.toggleFacePaint(character: next, paintId: item.id)
        case "outfits": next = LayerComposer.equipOutfit(character: next, outfitId: item.id)
        case "wings": next = LayerComposer.equipWings(character: next, wingsId: item.id)
        case "horns": next = LayerComposer.equipHorn(character: next, hornId: item.id)
        case "tiaras": next = LayerComposer.equipTiara(character: next, tiaraId: item.id)
        case "jewelry": next.jewelryId = item.id
        case "stickers": next = LayerComposer.addSticker(character: next, stickerId: item.id).0
        default: break
        }
        commit(next)
    }

    private func selectedIds(for catId: String) -> Set<String> {
        switch catId {
        case "hair_styles": return characterState.hairStyleId.map { [$0] } ?? []
        case "hair_colors": return characterState.hairColorId.map { [$0] } ?? []
        case "eyeshadows": return characterState.makeup.eyeshadow.map { [$0] } ?? []
        case "lipsticks": return characterState.makeup.lipstick.map { [$0] } ?? []
        case "blushes": return characterState.makeup.blush.map { [$0] } ?? []
        case "face_paints": return Set(characterState.facePaints)
        case "outfits": return characterState.outfitId.map { [$0] } ?? []
        case "wings": return characterState.wingsId.map { [$0] } ?? []
        case "horns": return characterState.hornId.map { [$0] } ?? []
        case "tiaras": return characterState.tiaraId.map { [$0] } ?? []
        case "jewelry": return characterState.jewelryId.map { [$0] } ?? []
        case "stickers": return Set(characterState.stickers)
        default: return []
        }
    }

    public var body: some View {
        ZStack {
            CandySalonStudioView(
                characterState: $characterState,
                candyBalance: candyBalance,
                onBackClicked: onBack,
                onSavePhoto: {
                    if galleryCount >= LocalStorageManager.maxGalleryPhotos {
                        showGalleryFull = true
                        return
                    }
                    soundManager.playShutter()
                    photoFlash = true
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.18) { photoFlash = false }
                    let snapshot = CharacterSpriteView.renderSnapshot(
                        characterState: characterState,
                        backdrop: backdrop,
                        pose: .happy
                    )
                    if onSavePhotoToGallery(snapshot) {
                        let count = min(galleryCount + 1, LocalStorageManager.maxGalleryPhotos)
                        showToast(L10n.format("gallery_saved_banner", language: language, count))
                    } else {
                        showGalleryFull = true
                    }
                }
            )

            if photoFlash {
                Color.white.opacity(0.85).ignoresSafeArea()
            }

            if let toast = toastMessage {
                VStack {
                    Spacer()
                    Text(toast)
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.white)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(Color.black.opacity(0.75))
                        .cornerRadius(20)
                        .padding(.bottom, 65)
                }
                .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
                        }
                        commit(cleared)
                    }
                )
            }

            if let toast = toastMessage {
                VStack {
                    Text(toast)
                        .font(.system(size: 13, weight: .bold, design: .rounded))
                        .foregroundColor(.white)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 8)
                        .background(CandyColors.deepPlum.opacity(0.9))
                        .cornerRadius(16)
                        .padding(.top, 56)
                    Spacer()
                }
            }

            if let item = lockedItemPending {
                UnlockItemDialog(
                    item: item,
                    language: language,
                    candyBalance: candyBalance,
                    onUnlockWithCandies: {
                        if onUnlockItemWithCandy(item) {
                            soundManager.playSparkle()
                            equip(item)
                            lockedItemPending = nil
                        }
                    },
                    onUnlockWithAd: {
                        adProvider.showRewarded { earned in
                            if earned {
                                soundManager.playSparkle()
                                onUnlockItemWithAd(item)
                                equip(item)
                            }
                            lockedItemPending = nil
                        }
                    },
                    onDismiss: { lockedItemPending = nil }
                )
            }

            if showResetConfirm {
                ConfirmDialog(
                    title: L10n.string("dialog_reset_title", language: language),
                    message: L10n.string("dialog_reset_message", language: language),
                    confirmText: L10n.string("btn_confirm", language: language),
                    cancelText: L10n.string("btn_cancel", language: language),
                    onConfirm: {
                        showResetConfirm = false
                        commit(CharacterState(characterType: characterState.characterType))
                        soundManager.playClick()
                    },
                    onDismiss: { showResetConfirm = false }
                )
            }

            if showGalleryFull {
                ZStack {
                    Color.black.opacity(0.45).ignoresSafeArea()
                    VStack(spacing: 14) {
                        Text("⚠️ " + L10n.string("gallery_full_title", language: language))
                            .font(.system(size: 20, weight: .bold, design: .rounded))
                            .foregroundColor(CandyColors.pink)
                        Text(L10n.string("gallery_full_warning", language: language))
                            .font(.system(size: 14, weight: .regular, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum)
                            .multilineTextAlignment(.center)
                        NavButton(
                            iconName: "checkmark",
                            label: L10n.string("btn_close", language: language),
                            backgroundColor: CandyColors.pink,
                            fullWidth: true,
                            action: { showGalleryFull = false }
                        )
                    }
                    .padding(22)
                    .background(Color.white)
                    .cornerRadius(24)
                    .padding(.horizontal, 24)
                    .frame(maxWidth: 400)
                }
            }
        }
        .onAppear {
            undoStack.initialize(with: characterState)
        }
    }
}

// MARK: - 8. Portrait Gallery Screen

public struct GalleryView: View {
    public let photoURLs: [URL]
    public let language: String
    public let onExportPhoto: (URL, @escaping (Bool) -> Void) -> Void
    public let onDeletePhoto: (URL) -> Bool
    public let onBack: () -> Void

    @State private var selectedPhotoURL: URL?
    @State private var photoToDelete: URL?
    @State private var toastMessage: String?

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 10), count: 3)

    public init(
        photoURLs: [URL],
        language: String,
        onExportPhoto: @escaping (URL, @escaping (Bool) -> Void) -> Void,
        onDeletePhoto: @escaping (URL) -> Bool,
        onBack: @escaping () -> Void
    ) {
        self.photoURLs = photoURLs
        self.language = language
        self.onExportPhoto = onExportPhoto
        self.onDeletePhoto = onDeletePhoto
        self.onBack = onBack
    }

    public var body: some View {
        ZStack {
            LinearGradient(
                colors: [CandyColors.pastelPink.opacity(0.3), .white],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 10) {
                HStack {
                    NavButton(
                        iconName: "arrow.backward",
                        label: L10n.string("btn_back", language: language),
                        backgroundColor: CandyColors.pink,
                        compact: true,
                        action: onBack
                    )
                    Spacer()
                    Text(L10n.format("gallery_count", language: language, photoURLs.count))
                        .font(.system(size: 16, weight: .heavy, design: .rounded))
                        .foregroundColor(CandyColors.pink)
                }

                Text(L10n.string("gallery_title", language: language))
                    .font(.system(size: 22, weight: .heavy, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum)

                if photoURLs.count >= LocalStorageManager.maxGalleryPhotos {
                    Text("⚠️ " + L10n.string("gallery_full_warning", language: language))
                        .font(.system(size: 12, weight: .bold, design: .rounded))
                        .foregroundColor(.red)
                        .padding(10)
                        .frame(maxWidth: .infinity)
                        .background(Color.red.opacity(0.1))
                        .cornerRadius(14)
                }

                if photoURLs.isEmpty {
                    Spacer()
                    Text(L10n.string("gallery_empty", language: language))
                        .font(.system(size: 15, weight: .medium, design: .rounded))
                        .foregroundColor(.gray)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 24)
                    Spacer()
                } else {
                    ScrollView {
                        LazyVGrid(columns: columns, spacing: 10) {
                            ForEach(photoURLs, id: \.absoluteString) { url in
                                Button(action: { selectedPhotoURL = url }) {
                                    if let img = UIImage(contentsOfFile: url.path) {
                                        Image(uiImage: img)
                                            .resizable()
                                            .scaledToFill()
                                            .frame(height: 130)
                                            .clipped()
                                            .cornerRadius(16)
                                    } else {
                                        RoundedRectangle(cornerRadius: 16)
                                            .fill(CandyColors.pastelPink)
                                            .frame(height: 130)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)

            if let toast = toastMessage {
                VStack {
                    Spacer()
                    Text(toast)
                        .font(.system(size: 13, weight: .bold, design: .rounded))
                        .foregroundColor(.white)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 8)
                        .background(CandyColors.deepPlum.opacity(0.9))
                        .cornerRadius(16)
                        .padding(.bottom, 24)
                }
            }

            if let photo = selectedPhotoURL {
                ZStack {
                    Color.black.opacity(0.45).ignoresSafeArea()
                    VStack(spacing: 12) {
                        if let img = UIImage(contentsOfFile: photo.path) {
                            Image(uiImage: img)
                                .resizable()
                                .scaledToFit()
                                .frame(maxHeight: 360)
                                .cornerRadius(16)
                        }
                        NavButton(
                            iconName: "square.and.arrow.down",
                            label: L10n.string("btn_save_photo", language: language),
                            backgroundColor: CandyColors.mint,
                            foregroundColor: CandyColors.deepPlum,
                            fullWidth: true,
                            action: {
                                let target = photo
                                selectedPhotoURL = nil
                                onExportPhoto(target) { ok in
                                    let msg = L10n.string(ok ? "export_success" : "export_failed", language: language)
                                    toastMessage = msg
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 2.4) {
                                        if toastMessage == msg { toastMessage = nil }
                                    }
                                }
                            }
                        )
                        HStack(spacing: 10) {
                            NavButton(
                                iconName: "trash.fill",
                                label: L10n.string("btn_delete", language: language),
                                backgroundColor: Color.red,
                                compact: true,
                                fullWidth: true,
                                action: {
                                    photoToDelete = photo
                                    selectedPhotoURL = nil
                                }
                            )
                            NavButton(
                                iconName: "xmark",
                                label: L10n.string("btn_close", language: language),
                                backgroundColor: Color(white: 0.88),
                                foregroundColor: CandyColors.deepPlum,
                                compact: true,
                                fullWidth: true,
                                action: { selectedPhotoURL = nil }
                            )
                        }
                    }
                    .padding(18)
                    .background(Color.white)
                    .cornerRadius(24)
                    .padding(.horizontal, 20)
                    .frame(maxWidth: 420)
                }
            }

            if let target = photoToDelete {
                ConfirmDialog(
                    title: L10n.string("dialog_delete_photo_title", language: language),
                    message: L10n.string("dialog_delete_photo_message", language: language),
                    confirmText: L10n.string("btn_confirm", language: language),
                    cancelText: L10n.string("btn_cancel", language: language),
                    onConfirm: {
                        _ = onDeletePhoto(target)
                        photoToDelete = nil
                    },
                    onDismiss: { photoToDelete = nil }
                )
            }
        }
    }
}

// MARK: - 9. Portrait Settings Screen

public struct SettingsView: View {
    public let bgmEnabled: Bool
    public let sfxEnabled: Bool
    public let language: String
    public let onBgmToggle: (Bool) -> Void
    public let onSfxToggle: (Bool) -> Void
    public let onLanguageSelected: (String) -> Void
    public let onRestorePurchases: () -> Void
    public let onBack: () -> Void

    @State private var showParentalGate = false
    @State private var toastMessage: String?

    public init(
        bgmEnabled: Bool,
        sfxEnabled: Bool,
        language: String,
        onBgmToggle: @escaping (Bool) -> Void,
        onSfxToggle: @escaping (Bool) -> Void,
        onLanguageSelected: @escaping (String) -> Void,
        onRestorePurchases: @escaping () -> Void,
        onBack: @escaping () -> Void
    ) {
        self.bgmEnabled = bgmEnabled
        self.sfxEnabled = sfxEnabled
        self.language = language
        self.onBgmToggle = onBgmToggle
        self.onSfxToggle = onSfxToggle
        self.onLanguageSelected = onLanguageSelected
        self.onRestorePurchases = onRestorePurchases
        self.onBack = onBack
    }

    public var body: some View {
        ZStack {
            LinearGradient(
                colors: [CandyColors.pastelPink.opacity(0.35), .white],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            ScrollView {
                VStack(spacing: 16) {
                    HStack {
                        NavButton(
                            iconName: "arrow.backward",
                            label: L10n.string("btn_back", language: language),
                            backgroundColor: CandyColors.pink,
                            compact: true,
                            action: onBack
                        )
                        Spacer()
                        Text(L10n.string("settings_title", language: language))
                            .font(.system(size: 20, weight: .heavy, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum)
                        Spacer()
                    }

                    VStack(alignment: .leading, spacing: 16) {
                        Toggle(L10n.string("settings_bgm", language: language), isOn: Binding(
                            get: { bgmEnabled },
                            set: { onBgmToggle($0) }
                        ))
                        .tint(CandyColors.pink)
                        .font(.system(size: 16, weight: .semibold, design: .rounded))
                        .foregroundColor(CandyColors.deepPlum)

                        Toggle(L10n.string("settings_sfx", language: language), isOn: Binding(
                            get: { sfxEnabled },
                            set: { onSfxToggle($0) }
                        ))
                        .tint(CandyColors.pink)
                        .font(.system(size: 16, weight: .semibold, design: .rounded))
                        .foregroundColor(CandyColors.deepPlum)

                        Text(L10n.string("settings_language", language: language))
                            .font(.system(size: 16, weight: .semibold, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum)

                        HStack(spacing: 10) {
                            ForEach([("en", "lang_english"), ("vi", "lang_vietnamese")], id: \.0) { code, key in
                                let isSelected = language.lowercased() == code
                                Button(action: { onLanguageSelected(code) }) {
                                    Text(L10n.string(key, language: language))
                                        .font(.system(size: 14, weight: .bold, design: .rounded))
                                        .frame(maxWidth: .infinity, minHeight: 48)
                                        .background(isSelected ? CandyColors.pink : CandyColors.pastelPink.opacity(0.25))
                                        .foregroundColor(isSelected ? .white : CandyColors.deepPlum)
                                        .cornerRadius(16)
                                        .overlay(
                                            RoundedRectangle(cornerRadius: 16)
                                                .stroke(isSelected ? CandyColors.deepPlum : CandyColors.purple.opacity(0.4), lineWidth: isSelected ? 2 : 1)
                                        )
                                }
                            }
                        }

                        NavButton(
                            iconName: "arrow.counterclockwise.circle.fill",
                            label: L10n.string("settings_restore", language: language),
                            backgroundColor: CandyColors.purple,
                            fullWidth: true,
                            action: { showParentalGate = true }
                        )

                        Text("🛡️ " + L10n.string("settings_privacy", language: language))
                            .font(.system(size: 12, weight: .regular, design: .rounded))
                            .foregroundColor(.gray)
                    }
                    .padding(20)
                    .background(Color.white.opacity(0.96))
                    .cornerRadius(24)
                    .shadow(color: .black.opacity(0.08), radius: 5, x: 0, y: 2)
                }
                .padding(16)
            }

            if let toast = toastMessage {
                VStack {
                    Spacer()
                    Text(toast)
                        .font(.system(size: 13, weight: .bold, design: .rounded))
                        .foregroundColor(.white)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 8)
                        .background(CandyColors.deepPlum.opacity(0.9))
                        .cornerRadius(16)
                        .padding(.bottom, 24)
                }
            }

            if showParentalGate {
                ParentalGateModal(
                    language: language,
                    onVerified: {
                        showParentalGate = false
                        onRestorePurchases()
                        let msg = L10n.string("restore_success", language: language)
                        toastMessage = msg
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2.4) {
                            if toastMessage == msg { toastMessage = nil }
                        }
                    },
                    onDismiss: { showParentalGate = false }
                )
            }
        }
    }
}

// MARK: - 10. Portrait Shop Screen

public struct ShopView: View {
    public let candyBalance: Int
    public let language: String
    public let hasPremium: Bool
    public let hasRemoveAds: Bool
    public let statusMessage: String?
    public let onClearStatus: () -> Void
    public let onPurchasePremium: () -> Void
    public let onPurchaseRemoveAds: () -> Void
    public let onBack: () -> Void

    @State private var pendingProduct: String?

    public init(
        candyBalance: Int,
        language: String,
        hasPremium: Bool,
        hasRemoveAds: Bool,
        statusMessage: String?,
        onClearStatus: @escaping () -> Void,
        onPurchasePremium: @escaping () -> Void,
        onPurchaseRemoveAds: @escaping () -> Void,
        onBack: @escaping () -> Void
    ) {
        self.candyBalance = candyBalance
        self.language = language
        self.hasPremium = hasPremium
        self.hasRemoveAds = hasRemoveAds
        self.statusMessage = statusMessage
        self.onClearStatus = onClearStatus
        self.onPurchasePremium = onPurchasePremium
        self.onPurchaseRemoveAds = onPurchaseRemoveAds
        self.onBack = onBack
    }

    public var body: some View {
        ZStack {
            LinearGradient(
                colors: [CandyColors.pastelPink.opacity(0.35), .white],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            ScrollView {
                VStack(spacing: 14) {
                    HStack {
                        NavButton(
                            iconName: "arrow.backward",
                            label: L10n.string("btn_back", language: language),
                            backgroundColor: CandyColors.pink,
                            compact: true,
                            action: onBack
                        )
                        Spacer()
                        CandyBalanceBadge(candyBalance: candyBalance, language: language)
                    }

                    Text("🍭 " + L10n.string("shop_title", language: language))
                        .font(.system(size: 22, weight: .heavy, design: .rounded))
                        .foregroundColor(CandyColors.deepPlum)

                    if let status = statusMessage {
                        Text(status)
                            .font(.system(size: 13, weight: .bold, design: .rounded))
                            .foregroundColor(.white)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 8)
                            .background(CandyColors.purple)
                            .cornerRadius(14)
                            .onAppear {
                                DispatchQueue.main.asyncAfter(deadline: .now() + 2.5) {
                                    onClearStatus()
                                }
                            }
                    }

                    VStack(spacing: 10) {
                        Text("👑").font(.system(size: 36))
                        Text(L10n.string("shop_premium_levels", language: language))
                            .font(.system(size: 17, weight: .heavy, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum)
                            .multilineTextAlignment(.center)
                        Text(L10n.string("shop_premium_desc", language: language))
                            .font(.system(size: 14, weight: .regular, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum.opacity(0.75))
                            .multilineTextAlignment(.center)
                        if hasPremium {
                            Text("✓ " + L10n.string("shop_purchased", language: language))
                                .font(.system(size: 16, weight: .heavy, design: .rounded))
                                .foregroundColor(CandyColors.mint)
                        } else {
                            NavButton(
                                iconName: "star.fill",
                                label: L10n.string("btn_buy", language: language),
                                backgroundColor: CandyColors.pink,
                                fullWidth: true,
                                action: { pendingProduct = "premium_levels" }
                            )
                        }
                    }
                    .padding(18)
                    .frame(maxWidth: .infinity)
                    .background(Color.white)
                    .cornerRadius(22)
                    .shadow(color: .black.opacity(0.08), radius: 5, x: 0, y: 2)

                    VStack(spacing: 10) {
                        Text("🚫").font(.system(size: 36))
                        Text(L10n.string("shop_remove_ads", language: language))
                            .font(.system(size: 17, weight: .heavy, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum)
                            .multilineTextAlignment(.center)
                        Text(L10n.string("shop_remove_ads_desc", language: language))
                            .font(.system(size: 14, weight: .regular, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum.opacity(0.75))
                            .multilineTextAlignment(.center)
                        if hasRemoveAds || hasPremium {
                            Text("✓ " + L10n.string("shop_purchased", language: language))
                                .font(.system(size: 16, weight: .heavy, design: .rounded))
                                .foregroundColor(CandyColors.mint)
                        } else {
                            NavButton(
                                iconName: "star.fill",
                                label: L10n.string("btn_buy", language: language),
                                backgroundColor: CandyColors.purple,
                                fullWidth: true,
                                action: { pendingProduct = "remove_ads" }
                            )
                        }
                    }
                    .padding(18)
                    .frame(maxWidth: .infinity)
                    .background(Color.white)
                    .cornerRadius(22)
                    .shadow(color: .black.opacity(0.08), radius: 5, x: 0, y: 2)
                }
                .padding(16)
            }

            if let prod = pendingProduct {
                ParentalGateModal(
                    language: language,
                    onVerified: {
                        if prod == "premium_levels" {
                            onPurchasePremium()
                        } else if prod == "remove_ads" {
                            onPurchaseRemoveAds()
                        }
                        pendingProduct = nil
                    },
                    onDismiss: { pendingProduct = nil }
                )
            }
        }
    }
}
