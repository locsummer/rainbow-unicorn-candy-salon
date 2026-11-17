import SwiftUI
import GameCore

public struct BottomItemTray: View {
    public let categories: [ItemCategory]
    public let items: [Item]
    public let language: String
    public let unlockedItems: Set<String>
    public let selectedIdsForCategory: (String) -> Set<String>
    public let onItemSelected: (Item) -> Void
    public let onLockedItemClicked: (Item) -> Void
    public let onClearCategory: ((ItemCategory) -> Void)?

    @State private var selectedCategoryIndex = 0
    @State private var savedVisibleIdByTab: [String: String] = [:]

    public init(
        categories: [ItemCategory],
        items: [Item],
        language: String,
        unlockedItems: Set<String>,
        selectedIdsForCategory: @escaping (String) -> Set<String>,
        onItemSelected: @escaping (Item) -> Void,
        onLockedItemClicked: @escaping (Item) -> Void,
        onClearCategory: ((ItemCategory) -> Void)? = nil
    ) {
        self.categories = categories
        self.items = items
        self.language = language
        self.unlockedItems = unlockedItems
        self.selectedIdsForCategory = selectedIdsForCategory
        self.onItemSelected = onItemSelected
        self.onLockedItemClicked = onLockedItemClicked
        self.onClearCategory = onClearCategory
    }

    public var body: some View {
        if categories.isEmpty {
            EmptyView()
        } else {
            let safeIndex = min(max(0, selectedCategoryIndex), categories.count - 1)
            let currentCategory = categories[safeIndex]
            let catItems = items.filter { $0.category == currentCategory.id }
            let activeIds = selectedIdsForCategory(currentCategory.id)

            VStack(spacing: 6) {
                // Category Tabs
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(Array(categories.enumerated()), id: \.element.id) { index, cat in
                            let isSelected = index == safeIndex
                            Button(action: { selectedCategoryIndex = index }) {
                                Text(cat.localizedName(language: language))
                                    .font(.system(size: 13, weight: isSelected ? .bold : .medium, design: .rounded))
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 8)
                                    .background(isSelected ? CandyColors.pink : CandyColors.pastelPink.opacity(0.35))
                                    .foregroundColor(isSelected ? .white : CandyColors.deepPlum)
                                    .cornerRadius(16)
                            }
                        }
                    }
                    .padding(.horizontal, 10)
                }

                // Multi-count & Clear row
                HStack {
                    if currentCategory.maxSelected > 1 {
                        Text(L10n.format("multi_count_format", language: language, activeIds.count, currentCategory.maxSelected))
                            .font(.system(size: 11, weight: .semibold, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum.opacity(0.75))
                    } else {
                        Text(currentCategory.localizedName(language: language))
                            .font(.system(size: 11, weight: .semibold, design: .rounded))
                            .foregroundColor(CandyColors.deepPlum.opacity(0.75))
                    }
                    Spacer()
                    if let clearAction = onClearCategory, !activeIds.isEmpty {
                        Button(action: { clearAction(currentCategory) }) {
                            HStack(spacing: 3) {
                                Image(systemName: "trash")
                                    .font(.system(size: 11, weight: .bold))
                                Text(L10n.string("btn_clear", language: language))
                                    .font(.system(size: 11, weight: .bold, design: .rounded))
                            }
                            .foregroundColor(CandyColors.pink)
                        }
                    }
                }
                .padding(.horizontal, 12)

                // Items Scroll Strip with per-tab scroll position retention (RULE-073)
                ScrollViewReader { proxy in
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 10) {
                            ForEach(catItems) { item in
                                let isUnlocked = item.unlockedByDefault || unlockedItems.contains(item.id)
                                let isSelected = activeIds.contains(item.id)

                                Button(action: {
                                    savedVisibleIdByTab[currentCategory.id] = item.id
                                    if isUnlocked {
                                        onItemSelected(item)
                                    } else {
                                        onLockedItemClicked(item)
                                    }
                                }) {
                                    VStack(spacing: 4) {
                                        ZStack {
                                            ItemThumbnailView(item: item)
                                            if !isUnlocked {
                                                RoundedRectangle(cornerRadius: 10)
                                                    .fill(Color.black.opacity(0.5))
                                                    .frame(width: 52, height: 52)
                                                VStack(spacing: 1) {
                                                    Image(systemName: "lock.fill")
                                                        .font(.system(size: 14))
                                                        .foregroundColor(.white)
                                                    Text("\(item.candyPrice)🍬")
                                                        .font(.system(size: 10, weight: .bold))
                                                        .foregroundColor(.white)
                                                }
                                            }
                                        }
                                        Text(item.localizedName(language: language))
                                            .font(.system(size: 10, weight: isSelected ? .bold : .medium, design: .rounded))
                                            .foregroundColor(CandyColors.deepPlum)
                                            .lineLimit(1)
                                            .frame(width: 66)
                                    }
                                    .padding(6)
                                    .background(isSelected ? CandyColors.yellow : (isUnlocked ? CandyColors.cream : Color(white: 0.93)))
                                    .cornerRadius(14)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 14)
                                            .stroke(isSelected ? CandyColors.pink : CandyColors.purple.opacity(0.3), lineWidth: isSelected ? 2.5 : 1)
                                    )
                                }
                                .id(item.id)
                            }
                        }
                        .padding(.horizontal, 10)
                    }
                    .onChange(of: currentCategory.id) { newCatId in
                        if let savedId = savedVisibleIdByTab[newCatId] {
                            proxy.scrollTo(savedId, anchor: .center)
                        }
                    }
                }
            }
            .padding(.vertical, 8)
            .frame(maxWidth: .infinity)
            .background(Color.white.opacity(0.96))
            .cornerRadius(20)
            .shadow(color: Color.black.opacity(0.1), radius: 6, x: 0, y: -2)
        }
    }
}

public struct UnlockItemDialog: View {
    public let item: Item
    public let language: String
    public let candyBalance: Int
    public let onUnlockWithCandies: () -> Void
    public let onUnlockWithAd: () -> Void
    public let onDismiss: () -> Void

    public init(
        item: Item,
        language: String,
        candyBalance: Int,
        onUnlockWithCandies: @escaping () -> Void,
        onUnlockWithAd: @escaping () -> Void,
        onDismiss: @escaping () -> Void
    ) {
        self.item = item
        self.language = language
        self.candyBalance = candyBalance
        self.onUnlockWithCandies = onUnlockWithCandies
        self.onUnlockWithAd = onUnlockWithAd
        self.onDismiss = onDismiss
    }

    public var body: some View {
        let canAfford = candyBalance >= item.candyPrice
        ZStack {
            Color.black.opacity(0.45).ignoresSafeArea()
            VStack(spacing: 14) {
                Text("✨ " + L10n.string("unlock_item_title", language: language))
                    .font(.system(size: 20, weight: .bold, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum)

                ItemThumbnailView(item: item)
                    .padding(14)
                    .background(CandyColors.cream)
                    .cornerRadius(16)

                Text(item.localizedName(language: language))
                    .font(.system(size: 16, weight: .bold, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum)
                    .multilineTextAlignment(.center)

                if !canAfford {
                    Text(L10n.format("unlock_not_enough_candy", language: language, item.candyPrice, candyBalance))
                        .font(.system(size: 12, weight: .medium, design: .rounded))
                        .foregroundColor(CandyColors.pink)
                        .multilineTextAlignment(.center)
                }

                NavButton(
                    iconName: "bag.fill",
                    label: L10n.format("unlock_with_candies", language: language, item.candyPrice),
                    backgroundColor: CandyColors.pink,
                    isEnabled: canAfford,
                    fullWidth: true,
                    action: onUnlockWithCandies
                )

                NavButton(
                    iconName: "play.fill",
                    label: L10n.string("btn_watch_ad", language: language),
                    backgroundColor: CandyColors.mint,
                    foregroundColor: CandyColors.deepPlum,
                    fullWidth: true,
                    action: onUnlockWithAd
                )

                Button(action: onDismiss) {
                    Text(L10n.string("btn_cancel", language: language))
                        .font(.system(size: 15, weight: .semibold, design: .rounded))
                        .foregroundColor(.gray)
                }
                .padding(.top, 4)
            }
            .padding(22)
            .background(Color.white)
            .cornerRadius(24)
            .padding(.horizontal, 24)
            .frame(maxWidth: 400)
            .shadow(radius: 12)
        }
    }
}

public struct ItemSelectionModal: View {
    public let title: String
    public let items: [Item]
    public let language: String
    public let unlockedItems: Set<String>
    public let selectedIds: Set<String>
    public let onItemSelected: (Item) -> Void
    public let onLockedItemClicked: (Item) -> Void
    public let onDismiss: () -> Void

    public init(
        title: String,
        items: [Item],
        language: String,
        unlockedItems: Set<String>,
        selectedIds: Set<String>,
        onItemSelected: @escaping (Item) -> Void,
        onLockedItemClicked: @escaping (Item) -> Void,
        onDismiss: @escaping () -> Void
    ) {
        self.title = title
        self.items = items
        self.language = language
        self.unlockedItems = unlockedItems
        self.selectedIds = selectedIds
        self.onItemSelected = onItemSelected
        self.onLockedItemClicked = onLockedItemClicked
        self.onDismiss = onDismiss
    }

    public var body: some View {
        ZStack {
            Color.black.opacity(0.45).ignoresSafeArea()

            VStack(spacing: 14) {
                Text(title)
                    .font(.system(size: 18, weight: .bold, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum)

                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 10) {
                        ForEach(items) { item in
                            let isUnlocked = item.unlockedByDefault || unlockedItems.contains(item.id)
                            let isSelected = selectedIds.contains(item.id)

                            Button(action: {
                                if isUnlocked {
                                    onItemSelected(item)
                                } else {
                                    onLockedItemClicked(item)
                                }
                            }) {
                                VStack(spacing: 4) {
                                    ZStack {
                                        RoundedRectangle(cornerRadius: 12)
                                            .fill(CandyColors.pastelPink.opacity(0.2))
                                            .frame(width: 52, height: 52)

                                        Circle()
                                            .fill(CandyColors.pink)
                                            .frame(width: 30, height: 30)

                                        if !isUnlocked {
                                            RoundedRectangle(cornerRadius: 12)
                                                .fill(Color.black.opacity(0.45))
                                                .frame(width: 52, height: 52)
                                                .overlay(
                                                    VStack(spacing: 2) {
                                                        Image(systemName: "lock.fill")
                                                            .font(.system(size: 12))
                                                            .foregroundColor(.white)
                                                        Text("\(item.candyPrice)🍬")
                                                            .font(.system(size: 9, weight: .bold))
                                                            .foregroundColor(.white)
                                                    }
                                                )
                                        }
                                    }

                                    Text(item.localizedName(language: language))
                                        .font(.system(size: 10, weight: isSelected ? .bold : .medium, design: .rounded))
                                        .foregroundColor(CandyColors.deepPlum)
                                        .lineLimit(2)
                                        .multilineTextAlignment(.center)
                                        .frame(width: 68)
                                }
                                .padding(6)
                                .background(isSelected ? CandyColors.yellow.opacity(0.9) : (isUnlocked ? Color.white : Color.gray.opacity(0.12)))
                                .cornerRadius(16)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 16)
                                        .stroke(isSelected ? CandyColors.pink : Color.gray.opacity(0.25), lineWidth: isSelected ? 2.5 : 1)
                                )
                            }
                        }
                    }
                    .padding(.horizontal, 4)
                }

                NavButton(
                    iconName: "xmark",
                    label: L10n.string("btn_close", language: language),
                    backgroundColor: CandyColors.pink,
                    fullWidth: true,
                    action: onDismiss
                )
            }
            .padding(20)
            .background(Color.white)
            .cornerRadius(24)
            .padding(.horizontal, 20)
            .frame(maxWidth: 440)
            .shadow(radius: 12)
        }
    }
}
