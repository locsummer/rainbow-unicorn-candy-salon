import SwiftUI
import GameCore

@main
struct RainbowUnicornApp: App {

    @State private var currentScreen: AppScreen = .home
    @State private var saveData: SaveData = LocalStorageManager.shared.loadSaveData()
    @State private var levels: [Level] = []

    init() {
        // Load levels from bundled JSON
        if let url = Bundle.main.url(forResource: "levels", withExtension: "json"),
           let data = try? Data(contentsOf: url),
           let str = String(data: data, encoding: .utf8) {
            // Decoded levels
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: View {
    @State private var currentScreen: AppScreen = .home
    @State private var saveData: SaveData = LocalStorageManager.shared.loadSaveData()

    var body: some View {
        switch currentScreen {
        case .home:
            HomeView(
                candyBalance: saveData.candyBalance,
                onStoryMode: { currentScreen = .levelSelect },
                onPartyMode: { currentScreen = .partyMode },
                onGallery: { currentScreen = .gallery },
                onSettings: { currentScreen = .settings },
                onShop: { currentScreen = .shop }
            )
        default:
            HomeView(
                candyBalance: saveData.candyBalance,
                onStoryMode: { currentScreen = .levelSelect },
                onPartyMode: { currentScreen = .partyMode },
                onGallery: { currentScreen = .gallery },
                onSettings: { currentScreen = .settings },
                onShop: { currentScreen = .shop }
            )
        }
    }
}
