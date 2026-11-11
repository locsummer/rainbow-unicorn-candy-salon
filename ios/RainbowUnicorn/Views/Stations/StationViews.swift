import SwiftUI
import GameCore

public struct SpaStationView: View {
    public let characterState: CharacterState
    public let language: String
    public let resetTrigger: Int
    public let onCleanPercentChanged: (Double) -> Void

    private struct DirtSpot: Identifiable {
        let id: Int
        let nx: CGFloat
        let ny: CGFloat
        var isCleaned: Bool
    }

    @State private var spots: [DirtSpot] = SpaStationView.defaultSpots()
    @State private var spongeOffset: CGPoint = CGPoint(x: 150, y: 150)

    public init(
        characterState: CharacterState,
        language: String,
        resetTrigger: Int,
        onCleanPercentChanged: @escaping (Double) -> Void
    ) {
        self.characterState = characterState
        self.language = language
        self.resetTrigger = resetTrigger
        self.onCleanPercentChanged = onCleanPercentChanged
    }

    private static func defaultSpots() -> [DirtSpot] {
        return [
            DirtSpot(id: 0, nx: 0.42, ny: 0.36, isCleaned: false),
            DirtSpot(id: 1, nx: 0.58, ny: 0.36, isCleaned: false),
            DirtSpot(id: 2, nx: 0.50, ny: 0.44, isCleaned: false),
            DirtSpot(id: 3, nx: 0.38, ny: 0.48, isCleaned: false),
            DirtSpot(id: 4, nx: 0.62, ny: 0.48, isCleaned: false),
            DirtSpot(id: 5, nx: 0.44, ny: 0.62, isCleaned: false),
            DirtSpot(id: 6, nx: 0.56, ny: 0.62, isCleaned: false),
            DirtSpot(id: 7, nx: 0.50, ny: 0.72, isCleaned: false)
        ]
    }

    public var body: some View {
        let cleaned = spots.filter { $0.isCleaned }.count
        let pct = Double(cleaned) / Double(spots.count)

        VStack(spacing: 10) {
            VStack(spacing: 4) {
                Text(L10n.format("spa_clean_percent", language: language, Int((pct * 100).rounded())))
                    .font(.system(size: 16, weight: .bold, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum)
                ProgressView(value: pct)
                    .tint(pct >= 0.8 ? CandyColors.mint : CandyColors.pink)
                    .padding(.horizontal, 24)
            }
            .padding(10)
            .background(Color.white.opacity(0.9))
            .cornerRadius(16)
            .padding(.horizontal, 16)

            GeometryReader { geo in
                ZStack {
                    CharacterSpriteView(characterState: characterState)
                        .frame(width: geo.size.width, height: geo.size.height)

                    ForEach(spots.filter { !$0.isCleaned }) { spot in
                        Circle()
                            .fill(Color.brown.opacity(0.62))
                            .frame(width: 44, height: 44)
                            .position(x: spot.nx * geo.size.width, y: spot.ny * geo.size.height)
                    }

                    ZStack {
                        RoundedRectangle(cornerRadius: 12)
                            .fill(CandyColors.yellow)
                            .frame(width: 60, height: 38)
                        Text("🧽")
                            .font(.system(size: 20))
                    }
                    .position(spongeOffset)
                }
                .contentShape(Rectangle())
                .gesture(
                    DragGesture(minimumDistance: 0)
                        .onChanged { value in
                            spongeOffset = value.location
                            var newlyCleaned = false
                            for i in spots.indices where !spots[i].isCleaned {
                                let sx = spots[i].nx * geo.size.width
                                let sy = spots[i].ny * geo.size.height
                                let dist = hypot(value.location.x - sx, value.location.y - sy)
                                if dist < 44 {
                                    spots[i].isCleaned = true
                                    newlyCleaned = true
                                }
                            }
                            if newlyCleaned {
                                let newPct = Double(spots.filter { $0.isCleaned }.count) / Double(spots.count)
                                onCleanPercentChanged(newPct)
                            }
                        }
                )
            }
        }
        .onChange(of: resetTrigger) { _ in
            spots = Self.defaultSpots()
            onCleanPercentChanged(0.0)
        }
    }
}

public struct CakeStationView: View {
    public let layers: Int
    public let flavor: String?
    public let toppings: [String]
    public let language: String
    public let onLayersChanged: (Int) -> Void
    public let onClearToppings: () -> Void

    public init(
        layers: Int,
        flavor: String?,
        toppings: [String],
        language: String,
        onLayersChanged: @escaping (Int) -> Void,
        onClearToppings: @escaping () -> Void
    ) {
        self.layers = layers
        self.flavor = flavor
        self.toppings = toppings
        self.language = language
        self.onLayersChanged = onLayersChanged
        self.onClearToppings = onClearToppings
    }

    public var body: some View {
        let clampedLayers = min(max(layers, 1), 4)
        let icingColor: Color = {
            guard let f = flavor else { return Color(red: 1.0, green: 0.96, blue: 0.88) }
            if f.contains("strawberry") { return CandyColors.pastelPink }
            if f.contains("blue_cotton") { return Color(red: 0.70, green: 0.92, blue: 0.95) }
            if f.contains("chocolate") { return Color(red: 0.43, green: 0.30, blue: 0.25) }
            return Color(red: 1.0, green: 0.96, blue: 0.88)
        }()

        VStack(spacing: 12) {
            VStack(spacing: 6) {
                Text(L10n.string("cake_layers_label", language: language))
                    .font(.system(size: 13, weight: .bold, design: .rounded))
                    .foregroundColor(CandyColors.deepPlum)

                HStack(spacing: 8) {
                    ForEach(1...4, id: \.self) { t in
                        Button(action: { onLayersChanged(t) }) {
                            Text("\(t)")
                                .font(.system(size: 15, weight: .bold, design: .rounded))
                                .frame(minWidth: 44, minHeight: 44)
                                .background(clampedLayers == t ? CandyColors.pink : CandyColors.pastelPink.opacity(0.4))
                                .foregroundColor(clampedLayers == t ? .white : CandyColors.deepPlum)
                                .cornerRadius(14)
                        }
                    }

                    if !toppings.isEmpty {
                        Button(action: onClearToppings) {
                            HStack(spacing: 4) {
                                Image(systemName: "trash")
                                Text(L10n.string("btn_clear", language: language))
                            }
                            .font(.system(size: 12, weight: .bold, design: .rounded))
                            .padding(.horizontal, 10)
                            .frame(minHeight: 44)
                            .background(CandyColors.yellow)
                            .foregroundColor(CandyColors.deepPlum)
                            .cornerRadius(14)
                        }
                    }
                }
            }
            .padding(12)
            .background(Color.white.opacity(0.95))
            .cornerRadius(18)

            Spacer()

            VStack(spacing: -4) {
                ForEach((0..<clampedLayers).reversed(), id: \.self) { i in
                    let w = CGFloat(200 - i * 32)
                    ZStack(alignment: .top) {
                        RoundedRectangle(cornerRadius: 14)
                            .fill(icingColor)
                            .frame(width: w, height: 42)
                            .overlay(RoundedRectangle(cornerRadius: 14).stroke(CandyColors.deepPlum.opacity(0.2), lineWidth: 1.5))

                        if i == clampedLayers - 1 && !toppings.isEmpty {
                            HStack(spacing: 6) {
                                ForEach(Array(toppings.prefix(5).enumerated()), id: \.offset) { _, _ in
                                    Circle()
                                        .fill(CandyColors.pink)
                                        .frame(width: 12, height: 12)
                                }
                            }
                            .offset(y: -6)
                        }
                    }
                }
                RoundedRectangle(cornerRadius: 6)
                    .fill(CandyColors.deepPlum.opacity(0.18))
                    .frame(width: 240, height: 14)
            }

            Spacer()
        }
        .padding(.horizontal, 16)
    }
}

public struct PetStationView: View {
    public let petFed: Bool
    public let petClean: Double
    public let petGroomed: Bool
    public let language: String
    public let onFeedPet: () -> Void
    public let onWashPetStep: () -> Void
    public let onGroomPet: () -> Void

    public init(
        petFed: Bool,
        petClean: Double,
        petGroomed: Bool,
        language: String,
        onFeedPet: @escaping () -> Void,
        onWashPetStep: @escaping () -> Void,
        onGroomPet: @escaping () -> Void
    ) {
        self.petFed = petFed
        self.petClean = petClean
        self.petGroomed = petGroomed
        self.language = language
        self.onFeedPet = onFeedPet
        self.onWashPetStep = onWashPetStep
        self.onGroomPet = onGroomPet
    }

    public var body: some View {
        VStack(spacing: 14) {
            Spacer()

            ZStack {
                Circle()
                    .fill(CandyColors.mint.opacity(0.28))
                    .frame(width: 190, height: 190)

                VStack(spacing: 8) {
                    Text(petFed && petGroomed ? "🐶✨" : "🐶")
                        .font(.system(size: 72))
                    Text(L10n.format("pet_clean_format", language: language, Int((petClean * 100).rounded())))
                        .font(.system(size: 14, weight: .bold, design: .rounded))
                        .foregroundColor(CandyColors.deepPlum)
                }
            }

            Spacer()

            VStack(spacing: 8) {
                NavButton(
                    iconName: petFed ? "checkmark.circle.fill" : "heart.fill",
                    label: L10n.string("pet_feed", language: language),
                    backgroundColor: petFed ? CandyColors.mint : CandyColors.pink,
                    foregroundColor: petFed ? CandyColors.deepPlum : .white,
                    fullWidth: true,
                    action: onFeedPet
                )

                NavButton(
                    iconName: petClean >= 0.99 ? "checkmark.circle.fill" : "drop.fill",
                    label: L10n.string("pet_bath", language: language),
                    backgroundColor: petClean >= 0.99 ? CandyColors.mint : CandyColors.yellow,
                    foregroundColor: CandyColors.deepPlum,
                    fullWidth: true,
                    action: onWashPetStep
                )

                NavButton(
                    iconName: petGroomed ? "checkmark.circle.fill" : "sparkles",
                    label: L10n.string("pet_groom", language: language),
                    backgroundColor: petGroomed ? CandyColors.mint : CandyColors.purple,
                    foregroundColor: petGroomed ? CandyColors.deepPlum : .white,
                    fullWidth: true,
                    action: onGroomPet
                )
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 12)
        }
    }
}
