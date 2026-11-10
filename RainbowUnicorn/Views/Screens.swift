import SwiftUI
import GameCore

public enum AppScreen {
    case boot, home, levelSelect, levelIntro, station, levelResult, partyMode, gallery, settings, shop
}

public struct HomeView: View {
    public let candyBalance: Int
    public let onStoryMode: () -> Void
    public let onPartyMode: () -> Void
    public let onGallery: () -> Void
    public let onSettings: () -> Void
    public let onShop: () -> Void

    public var body: some View {
        ZStack {
            LinearGradient(
                colors: [Color.pink.opacity(0.2), Color.purple.opacity(0.15), Color.yellow.opacity(0.2)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            ).ignoresSafeArea()

            VStack(spacing: 24) {
                HStack {
                    Spacer()
                    Text("🍬 \(candyBalance) Kẹo")
                        .font(.system(size: 16, weight: .bold, design: .rounded))
                        .padding(.horizontal, 14)
                        .padding(.vertical, 8)
                        .background(Color.white.opacity(0.9))
                        .cornerRadius(20)
                }
                .padding(.horizontal, 20)

                Spacer()

                Text("🦄 Rainbow Unicorn Candy Salon 🍭")
                    .font(.system(size: 32, weight: .heavy, design: .rounded))
                    .foregroundColor(Color(red: 0.29, green: 0.08, blue: 0.29))

                HStack(spacing: 20) {
                    NavButton(iconName: "play.fill", label: "Cốt truyện", backgroundColor: .pink, action: onStoryMode)
                    NavButton(iconName: "sparkles", label: "Tiệc tùng", backgroundColor: .purple, action: onPartyMode)
                }

                HStack(spacing: 16) {
                    NavButton(iconName: "photo.on.rectangle", label: "Thư viện", backgroundColor: Color.mint, foregroundColor: .black, action: onGallery)
                    NavButton(iconName: "cart.fill", label: "Cửa hàng", backgroundColor: Color.yellow, foregroundColor: .black, action: onShop)
                    NavButton(iconName: "gearshape.fill", label: "Cài đặt", backgroundColor: .white, foregroundColor: .black, action: onSettings)
                }

                Spacer()
            }
        }
    }
}

public struct LevelSelectView: View {
    public let levels: [Level]
    public let unlockedLevels: [Int]
    public let bestStars: [String: Int]
    public let hasPremium: Bool
    public let onSelectLevel: (Level) -> Void
    public let onBack: () -> Void
    public let onUnlockPremium: () -> Void

    @State private var showParentalGate = false

    public var body: some View {
        ZStack {
            Color.pink.opacity(0.15).ignoresSafeArea()

            VStack {
                HStack {
                    NavButton(iconName: "arrow.backward", label: "Quay lại", action: onBack)
                    Spacer()
                    Text("Chọn màn chơi (20 Level)")
                        .font(.system(size: 20, weight: .bold, design: .rounded))
                    Spacer()
                }
                .padding()

                ScrollView {
                    LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 12), count: 5), spacing: 12) {
                        ForEach(levels, id: \.id) { lvl in
                            let isUnlocked = unlockedLevels.contains(lvl.id)
                            let isPremiumLocked = lvl.requiresPremium && !hasPremium
                            let stars = bestStars[String(lvl.id)] ?? 0

                            Button(action: {
                                if isPremiumLocked {
                                    showParentalGate = true
                                } else if isUnlocked {
                                    onSelectLevel(lvl)
                                }
                            }) {
                                VStack {
                                    Text("Màn \(lvl.id)")
                                        .font(.system(size: 14, weight: .bold, design: .rounded))
                                        .foregroundColor(Color(red: 0.29, green: 0.08, blue: 0.29))

                                    if isPremiumLocked {
                                        Image(systemName: "lock.fill")
                                            .foregroundColor(.pink)
                                        Text("VIP")
                                            .font(.system(size: 11, weight: .bold))
                                            .foregroundColor(.pink)
                                    } else if !isUnlocked {
                                        Image(systemName: "lock.fill")
                                            .foregroundColor(.gray)
                                    } else {
                                        HStack(spacing: 2) {
                                            ForEach(1...3, id: \.self) { s in
                                                Image(systemName: "star.fill")
                                                    .font(.system(size: 10))
                                                    .foregroundColor(s <= stars ? .yellow : .gray.opacity(0.4))
                                            }
                                        }
                                    }
                                }
                                .frame(height: 70)
                                .frame(maxWidth: .infinity)
                                .background(Color.white)
                                .cornerRadius(16)
                                .shadow(radius: 2)
                            }
                        }
                    }
                    .padding()
                }
            }

            if showParentalGate {
                ParentalGateModal(
                    onVerified: {
                        showParentalGate = false
                        onUnlockPremium()
                    },
                    onDismiss: { showParentalGate = false }
                )
            }
        }
    }
}
