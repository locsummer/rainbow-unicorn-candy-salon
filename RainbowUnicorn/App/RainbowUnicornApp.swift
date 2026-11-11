import SwiftUI
import GameCore

@main
struct RainbowUnicornApp: App {
    @Environment(\.scenePhase) private var scenePhase
    @StateObject private var coordinator = AppCoordinator()

    var body: some Scene {
        WindowGroup {
            ContentView(coordinator: coordinator)
                .onChange(of: scenePhase) { phase in
                    switch phase {
                    case .active:
                        coordinator.onAppActive()
                    case .background, .inactive:
                        coordinator.onAppBackground()
                    @unknown default:
                        break
                    }
                }
        }
    }
}

final class AppCoordinator: ObservableObject {
    @Published var currentScreen: AppScreen = .boot
    @Published var saveData: SaveData
    @Published var levels: [Level] = []
    @Published var categories: [ItemCategory] = []
    @Published var items: [Item] = []

    @Published var selectedLevel: Level?
    @Published var activeEngine: LevelEngine?
    @Published var activeCheckpointForSession: SavedLevelCheckpoint?
    @Published var lastResult: StationResult?
    @Published var candyAwardedThisRun: Int = 0
    @Published var galleryURLs: [URL] = []
    @Published var shopStatusMessage: String?
    @Published var levelSelectStatusMessage: String?

    let soundManager = SoundManager()
    let adProvider: AdProvider = FakeAdProvider()
    let billingProvider: BillingProvider = FakeBillingProvider()
    private let storage = LocalStorageManager.shared

    private var lastAdTimeSeconds: TimeInterval = 0
    private var levelsPlayedSinceLastAd: Int = 0

    private static var resourceBundle: Bundle {
        #if SWIFT_PACKAGE
        return Bundle.module
        #else
        return Bundle.main
        #endif
    }

    init() {
        let loaded = LocalStorageManager.shared.loadSaveData()
        self.saveData = loaded
        self.galleryURLs = LocalStorageManager.shared.getGalleryPhotoURLs()
        self.soundManager.bgmEnabled = loaded.bgmEnabled
        self.soundManager.sfxEnabled = loaded.sfxEnabled
        loadSpecs()
    }

    private func loadSpecs() {
        if let lvlURL = Self.resourceBundle.url(forResource: "levels", withExtension: "json"),
           let data = try? Data(contentsOf: lvlURL),
           let decoded = try? LevelRepository.loadLevels(from: data) {
            self.levels = decoded
        }
        if let itemURL = Self.resourceBundle.url(forResource: "items", withExtension: "json"),
           let data = try? Data(contentsOf: itemURL),
           let decoded = try? LevelRepository.loadItems(from: data) {
            self.categories = decoded.categories
            self.items = decoded.items
        }
    }

    func onAppActive() {
        soundManager.onResume()
    }

    func onAppBackground() {
        soundManager.onPause()
        var snapshot = saveData
        if currentScreen == .station, let cp = activeCheckpointForSession {
            snapshot.activeCheckpoint = cp
        }
        storage.saveSaveData(snapshot)
    }

    func unlockItemWithCandy(_ item: Item) -> Bool {
        let res = EconomyService.purchaseItem(
            itemId: item.id,
            itemPrice: item.candyPrice,
            currentCandyBalance: saveData.candyBalance,
            alreadyOwnedItemIds: Set(saveData.unlockedItems)
        )
        guard res.isSuccess else { return false }
        var next = saveData
        next.candyBalance = res.remainingCandy
        if !next.unlockedItems.contains(item.id) {
            next.unlockedItems.append(item.id)
        }
        saveData = next
        storage.saveSaveData(next)
        return true
    }

    func unlockItemWithAd(_ item: Item) {
        var next = saveData
        if !next.unlockedItems.contains(item.id) {
            next.unlockedItems.append(item.id)
        }
        saveData = next
        storage.saveSaveData(next)
    }

    func purchaseEntitlement(_ id: String, onMessage: @escaping (String) -> Void) {
        let lang = saveData.language
        billingProvider.purchase(id) { [weak self] success in
            DispatchQueue.main.async {
                guard let self = self else { return }
                if success {
                    var next = self.saveData
                    if !next.purchasedEntitlements.contains(id) {
                        next.purchasedEntitlements.append(id)
                    }
                    var intStars: [Int: Int] = [:]
                    for (k, v) in next.bestStars {
                        if let i = Int(k) { intStars[i] = v }
                    }
                    let hasPremium = next.purchasedEntitlements.contains("premium_levels")
                    next.unlockedLevels = Array(UnlockPolicy.computeUnlockedLevels(bestStarsPerLevel: intStars, hasPremium: hasPremium)).sorted()
                    self.saveData = next
                    self.storage.saveSaveData(next)
                    onMessage(L10n.string("purchase_success", language: lang))
                } else {
                    onMessage(L10n.string("purchase_failed", language: lang))
                }
            }
        }
    }

    func recordLevelFinished() {
        levelsPlayedSinceLastAd += 1
    }

    func checkAndTriggerAd(completedLevelId: Int) {
        let hasRemoveAds = saveData.purchasedEntitlements.contains("remove_ads")
        let hasPremium = saveData.purchasedEntitlements.contains("premium_levels")
        let secondsSince = Int64(max(0, Date().timeIntervalSince1970 - lastAdTimeSeconds))
        if AdPolicy.shouldShowInterstitialAd(
            hasRemoveAds: hasRemoveAds,
            hasPremiumLevels: hasPremium,
            completedLevelId: completedLevelId,
            secondsSinceLastAd: secondsSince,
            levelsPlayedSinceLastAd: levelsPlayedSinceLastAd,
            currentScreen: "level_select"
        ) {
            adProvider.showInterstitial { [weak self] in
                self?.lastAdTimeSeconds = Date().timeIntervalSince1970
                self?.levelsPlayedSinceLastAd = 0
            }
        }
    }

    func persistSaveData(_ next: SaveData) {
        saveData = next
        storage.saveSaveData(next)
    }

    func savePhoto(_ image: UIImage) -> Bool {
        guard storage.savePhotoToInternalGallery(image: image) != nil else { return false }
        galleryURLs = storage.getGalleryPhotoURLs()
        var next = saveData
        next.galleryCount = galleryURLs.count
        persistSaveData(next)
        return true
    }

    func deletePhoto(at url: URL) -> Bool {
        let ok = storage.deletePhoto(at: url)
        if ok {
            galleryURLs = storage.getGalleryPhotoURLs()
            var next = saveData
            next.galleryCount = galleryURLs.count
            persistSaveData(next)
        }
        return ok
    }

    func exportPhoto(at url: URL, completion: @escaping (Bool) -> Void) {
        guard let img = UIImage(contentsOfFile: url.path) else {
            completion(false)
            return
        }
        storage.exportPhotoToPhotosAlbum(image: img, completion: completion)
    }

    func refreshGallery() {
        galleryURLs = storage.getGalleryPhotoURLs()
    }
}

struct ContentView: View {
    @ObservedObject var coordinator: AppCoordinator

    var body: some View {
        let save = coordinator.saveData
        let lang = save.language

        switch coordinator.currentScreen {
        case .boot:
            BootView(language: lang) {
                coordinator.soundManager.startBgm()
                coordinator.currentScreen = .home
            }
        case .home:
            HomeView(
                candyBalance: save.candyBalance,
                language: lang,
                onStoryMode: { coordinator.currentScreen = .levelSelect },
                onPartyMode: { coordinator.currentScreen = .partyMode },
                onGallery: {
                    coordinator.refreshGallery()
                    coordinator.currentScreen = .gallery
                },
                onSettings: { coordinator.currentScreen = .settings },
                onShop: { coordinator.currentScreen = .shop }
            )
        case .levelSelect:
            LevelSelectView(
                levels: coordinator.levels,
                language: lang,
                candyBalance: save.candyBalance,
                unlockedLevels: Set(save.unlockedLevels),
                bestStars: save.bestStars,
                hasPremium: save.purchasedEntitlements.contains("premium_levels"),
                activeCheckpoint: save.activeCheckpoint,
                statusMessage: coordinator.levelSelectStatusMessage,
                onClearStatus: { coordinator.levelSelectStatusMessage = nil },
                onSelectLevel: { lvl, resume in
                    coordinator.selectedLevel = lvl
                    if resume, let cp = save.activeCheckpoint, cp.levelId == lvl.id {
                        coordinator.activeCheckpointForSession = cp
                        coordinator.activeEngine = LevelEngine(level: lvl, checkpoint: cp)
                        coordinator.currentScreen = .station
                    } else {
                        if save.activeCheckpoint != nil {
                            var cleared = save
                            cleared.activeCheckpoint = nil
                            coordinator.persistSaveData(cleared)
                        }
                        coordinator.activeCheckpointForSession = nil
                        coordinator.activeEngine = LevelEngine(level: lvl)
                        coordinator.currentScreen = .levelIntro
                    }
                },
                onBack: { coordinator.currentScreen = .home },
                onUnlockPremium: {
                    coordinator.purchaseEntitlement("premium_levels") { msg in
                        coordinator.levelSelectStatusMessage = msg
                    }
                }
            )
        case .levelIntro:
            if let lvl = coordinator.selectedLevel {
                LevelIntroView(
                    level: lvl,
                    language: lang,
                    onStart: {
                        if coordinator.activeEngine == nil || coordinator.activeEngine?.level.id != lvl.id {
                            coordinator.activeEngine = LevelEngine(level: lvl)
                        }
                        coordinator.currentScreen = .station
                    },
                    onBack: { coordinator.currentScreen = .levelSelect }
                )
            } else {
                Color.clear.onAppear { coordinator.currentScreen = .levelSelect }
            }
        case .station:
            if let lvl = coordinator.selectedLevel, let engine = coordinator.activeEngine {
                StationView(
                    level: lvl,
                    language: lang,
                    categories: coordinator.categories,
                    items: coordinator.items,
                    candyBalance: save.candyBalance,
                    unlockedItems: Set(save.unlockedItems),
                    soundManager: coordinator.soundManager,
                    adProvider: coordinator.adProvider,
                    levelEngine: engine,
                    initialCheckpoint: coordinator.activeCheckpointForSession,
                    onUnlockItemWithCandy: { coordinator.unlockItemWithCandy($0) },
                    onUnlockItemWithAd: { coordinator.unlockItemWithAd($0) },
                    onCheckpointChanged: { cp in
                        coordinator.activeCheckpointForSession = cp
                        var next = coordinator.saveData
                        next.activeCheckpoint = cp
                        coordinator.persistSaveData(next)
                    },
                    onLevelFinished: { result in
                        coordinator.recordLevelFinished()
                        coordinator.lastResult = result
                        let oldStars = coordinator.saveData.bestStars[String(lvl.id)] ?? 0
                        let award = EconomyService.calculateStarAward(
                            initialBestStars: oldStars,
                            levelEarnedStars: result.evaluation.stars,
                            currentCandyBalance: coordinator.saveData.candyBalance
                        )
                        coordinator.candyAwardedThisRun = result.evaluation.isWin ? award.candyEarned : 0

                        let updatedSave = result.engine.applyResultsToSaveData(coordinator.saveData, evaluation: result.evaluation)
                        if result.evaluation.isWin {
                            coordinator.activeCheckpointForSession = nil
                        }
                        coordinator.persistSaveData(updatedSave)
                        coordinator.currentScreen = .levelResult
                    },
                    onExitConfirmed: { cp in
                        coordinator.activeCheckpointForSession = cp
                        var next = coordinator.saveData
                        next.activeCheckpoint = cp
                        coordinator.persistSaveData(next)
                        coordinator.currentScreen = .levelSelect
                        coordinator.checkAndTriggerAd(completedLevelId: lvl.id)
                    }
                )
            } else {
                Color.clear.onAppear { coordinator.currentScreen = .levelSelect }
            }
        case .levelResult:
            if let res = coordinator.lastResult {
                LevelResultView(
                    characterState: res.characterState,
                    evaluation: res.evaluation,
                    candyEarned: coordinator.candyAwardedThisRun,
                    language: lang,
                    soundManager: coordinator.soundManager,
                    onNextLevel: {
                        let nextId = res.level.id + 1
                        if let nextLvl = coordinator.levels.first(where: { $0.id == nextId }),
                           coordinator.saveData.unlockedLevels.contains(nextId) {
                            coordinator.selectedLevel = nextLvl
                            coordinator.activeCheckpointForSession = nil
                            coordinator.activeEngine = LevelEngine(level: nextLvl)
                            coordinator.currentScreen = .levelIntro
                        } else {
                            coordinator.currentScreen = .levelSelect
                            coordinator.checkAndTriggerAd(completedLevelId: res.level.id)
                        }
                    },
                    onEditLook: {
                        coordinator.activeEngine = res.engine
                        res.engine.selectStation(index: max(0, res.level.stations.count - 1))
                        coordinator.currentScreen = .station
                    },
                    onReplay: {
                        coordinator.activeCheckpointForSession = nil
                        var next = coordinator.saveData
                        next.activeCheckpoint = nil
                        coordinator.persistSaveData(next)
                        coordinator.activeEngine = LevelEngine(level: res.level)
                        coordinator.currentScreen = .station
                    },
                    onBackToMap: {
                        coordinator.currentScreen = .levelSelect
                        coordinator.checkAndTriggerAd(completedLevelId: res.level.id)
                    }
                )
            } else {
                Color.clear.onAppear { coordinator.currentScreen = .levelSelect }
            }
        case .partyMode:
            PartyModeView(
                categories: coordinator.categories,
                items: coordinator.items,
                language: lang,
                candyBalance: save.candyBalance,
                unlockedItems: Set(save.unlockedItems),
                galleryCount: coordinator.galleryURLs.count,
                soundManager: coordinator.soundManager,
                adProvider: coordinator.adProvider,
                onUnlockItemWithCandy: { coordinator.unlockItemWithCandy($0) },
                onUnlockItemWithAd: { coordinator.unlockItemWithAd($0) },
                onSavePhotoToGallery: { coordinator.savePhoto($0) },
                onBack: { coordinator.currentScreen = .home }
            )
        case .gallery:
            GalleryView(
                photoURLs: coordinator.galleryURLs,
                language: lang,
                onExportPhoto: { url, done in coordinator.exportPhoto(at: url, completion: done) },
                onDeletePhoto: { coordinator.deletePhoto(at: $0) },
                onBack: { coordinator.currentScreen = .home }
            )
        case .settings:
            SettingsView(
                bgmEnabled: save.bgmEnabled,
                sfxEnabled: save.sfxEnabled,
                language: lang,
                onBgmToggle: { enabled in
                    var next = coordinator.saveData
                    next.bgmEnabled = enabled
                    coordinator.soundManager.bgmEnabled = enabled
                    coordinator.persistSaveData(next)
                },
                onSfxToggle: { enabled in
                    var next = coordinator.saveData
                    next.sfxEnabled = enabled
                    coordinator.soundManager.sfxEnabled = enabled
                    coordinator.persistSaveData(next)
                },
                onLanguageSelected: { newLang in
                    var next = coordinator.saveData
                    next.language = newLang
                    coordinator.persistSaveData(next)
                },
                onRestorePurchases: {
                    coordinator.billingProvider.restorePurchases { entitlements in
                        DispatchQueue.main.async {
                            var next = coordinator.saveData
                            for ent in entitlements where !next.purchasedEntitlements.contains(ent) {
                                next.purchasedEntitlements.append(ent)
                            }
                            var intStars: [Int: Int] = [:]
                            for (k, v) in next.bestStars {
                                if let i = Int(k) { intStars[i] = v }
                            }
                            let hasPremium = next.purchasedEntitlements.contains("premium_levels")
                            next.unlockedLevels = Array(UnlockPolicy.computeUnlockedLevels(bestStarsPerLevel: intStars, hasPremium: hasPremium)).sorted()
                            coordinator.persistSaveData(next)
                        }
                    }
                },
                onBack: { coordinator.currentScreen = .home }
            )
        case .shop:
            ShopView(
                candyBalance: save.candyBalance,
                language: lang,
                hasPremium: save.purchasedEntitlements.contains("premium_levels"),
                hasRemoveAds: save.purchasedEntitlements.contains("remove_ads"),
                statusMessage: coordinator.shopStatusMessage,
                onClearStatus: { coordinator.shopStatusMessage = nil },
                onPurchasePremium: {
                    coordinator.purchaseEntitlement("premium_levels") { msg in
                        coordinator.shopStatusMessage = msg
                    }
                },
                onPurchaseRemoveAds: {
                    coordinator.purchaseEntitlement("remove_ads") { msg in
                        coordinator.shopStatusMessage = msg
                    }
                },
                onBack: { coordinator.currentScreen = .home }
            )
        }
    }
}
