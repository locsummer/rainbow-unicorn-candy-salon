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
    public var onRemoveTopping: (String) -> Void = { _ in }
    public var onOpenAddModal: () -> Void = {}
    public var onOpenFlavorModal: () -> Void = {}

    public init(
        layers: Int,
        flavor: String?,
        toppings: [String],
        language: String,
        onLayersChanged: @escaping (Int) -> Void,
        onClearToppings: @escaping () -> Void,
        onRemoveTopping: @escaping (String) -> Void = { _ in },
        onOpenAddModal: @escaping () -> Void = {},
        onOpenFlavorModal: @escaping () -> Void = {}
    ) {
        self.layers = layers
        self.flavor = flavor
        self.toppings = toppings
        self.language = language
        self.onLayersChanged = onLayersChanged
        self.onClearToppings = onClearToppings
        self.onRemoveTopping = onRemoveTopping
        self.onOpenAddModal = onOpenAddModal
        self.onOpenFlavorModal = onOpenFlavorModal
    }

    @ViewBuilder
    private func toppingView(for topId: String) -> some View {
        if topId.contains("cherries") || topId.contains("cherry") {
            // Dual Glossy Cherries with green stem
            ZStack {
                Path { p in
                    p.move(to: CGPoint(x: 14, y: 0))
                    p.addQuadCurve(to: CGPoint(x: 8, y: 16), control: CGPoint(x: 6, y: 6))
                    p.move(to: CGPoint(x: 14, y: 0))
                    p.addQuadCurve(to: CGPoint(x: 20, y: 16), control: CGPoint(x: 22, y: 6))
                }
                .stroke(Color.green, lineWidth: 2)

                HStack(spacing: 2) {
                    Circle().fill(Color(red: 0.90, green: 0.08, blue: 0.22)).frame(width: 16, height: 16)
                        .overlay(Circle().fill(Color.white.opacity(0.6)).frame(width: 4, height: 4).offset(x: -3, y: -3))
                    Circle().fill(Color(red: 0.82, green: 0.05, blue: 0.18)).frame(width: 16, height: 16)
                        .overlay(Circle().fill(Color.white.opacity(0.6)).frame(width: 4, height: 4).offset(x: -3, y: -3))
                }
                .offset(y: 8)
            }
            .frame(width: 32, height: 32)
        } else if topId.contains("macaron") {
            VStack(spacing: 1) {
                Capsule().fill(CandyColors.purple).frame(width: 26, height: 8)
                Capsule().fill(Color.white).frame(width: 24, height: 3)
                Capsule().fill(CandyColors.purple).frame(width: 26, height: 8)
            }
        } else if topId.contains("wafer") {
            Capsule()
                .fill(LinearGradient(colors: [CandyColors.yellow, Color(red: 0.95, green: 0.65, blue: 0.2)], startPoint: .topLeading, endPoint: .bottomTrailing))
                .frame(width: 8, height: 32)
                .rotationEffect(.degrees(24))
        } else if topId.contains("chocolate") || topId.contains("chocochip") || topId.contains("kiss") {
            ZStack {
                Path { p in
                    p.move(to: CGPoint(x: 2, y: 20))
                    p.addQuadCurve(to: CGPoint(x: 10, y: 2), control: CGPoint(x: 4, y: 8))
                    p.addQuadCurve(to: CGPoint(x: 18, y: 20), control: CGPoint(x: 16, y: 8))
                    p.closeSubpath()
                }
                .fill(Color(red: 0.38, green: 0.20, blue: 0.12))
                Circle().fill(Color.white.opacity(0.4)).frame(width: 4, height: 4).offset(x: -2, y: 2)
            }
            .frame(width: 20, height: 22)
        } else if topId.contains("sprinkles") {
            HStack(spacing: 3) {
                Capsule().fill(CandyColors.pink).frame(width: 4, height: 12).rotationEffect(.degrees(30))
                Capsule().fill(CandyColors.mint).frame(width: 4, height: 12).rotationEffect(.degrees(-20))
                Capsule().fill(CandyColors.yellow).frame(width: 4, height: 12).rotationEffect(.degrees(45))
                Capsule().fill(CandyColors.purple).frame(width: 4, height: 12).rotationEffect(.degrees(-35))
            }
        } else if topId.contains("crystal") {
            Image(systemName: "suit.diamond.fill")
                .font(.system(size: 22))
                .foregroundColor(Color(red: 0.2, green: 0.90, blue: 1.0))
                .shadow(color: .white, radius: 2)
        } else {
            Image(systemName: "star.fill")
                .font(.system(size: 20))
                .foregroundColor(CandyColors.yellow)
                .shadow(color: CandyColors.pink.opacity(0.5), radius: 2)
        }
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

        VStack(spacing: 8) {
            // Interactive Cake Action Buttons Bar (CRUD Operations: Add, Edit, Delete)
            HStack(spacing: 8) {
                Button(action: onOpenAddModal) {
                    HStack(spacing: 4) {
                        Image(systemName: "plus.circle.fill")
                        Text(L10n.string("btn_add_topping", language: language))
                    }
                    .font(.system(size: 12, weight: .bold, design: .rounded))
                    .frame(maxWidth: .infinity, minHeight: 44)
                    .background(CandyColors.pink)
                    .foregroundColor(.white)
                    .cornerRadius(16)
                }

                Button(action: onOpenFlavorModal) {
                    HStack(spacing: 4) {
                        Image(systemName: "paintpalette.fill")
                        Text(L10n.string("btn_change_flavor", language: language))
                    }
                    .font(.system(size: 12, weight: .bold, design: .rounded))
                    .frame(maxWidth: .infinity, minHeight: 44)
                    .background(CandyColors.purple)
                    .foregroundColor(.white)
                    .cornerRadius(16)
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
                        .cornerRadius(16)
                    }
                }
            }

            // Removable Topping Chips Row (Delete specific toppings with ✕)
            if !toppings.isEmpty {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 6) {
                        ForEach(Array(toppings.enumerated()), id: \.offset) { _, topId in
                            HStack(spacing: 4) {
                                Text(topId.replacingOccurrences(of: "topping_", with: "").replacingOccurrences(of: "cake_top_", with: "").replacingOccurrences(of: "_", with: " ").capitalized)
                                    .font(.system(size: 11, weight: .medium, design: .rounded))
                                    .foregroundColor(CandyColors.deepPlum)

                                Button(action: { onRemoveTopping(topId) }) {
                                    Image(systemName: "xmark.circle.fill")
                                        .font(.system(size: 13))
                                        .foregroundColor(CandyColors.pink)
                                }
                            }
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(CandyColors.pastelPink.opacity(0.5))
                            .cornerRadius(12)
                        }
                    }
                    .padding(.vertical, 2)
                }
            }

            // Cake Layers Selector
            VStack(spacing: 4) {
                Text(L10n.string("cake_layers_label", language: language))
                    .font(.system(size: 12, weight: .bold, design: .rounded))
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
                }
            }
            .padding(10)
            .background(Color.white.opacity(0.95))
            .cornerRadius(16)

            Spacer()

            VStack(spacing: -4) {
                ForEach((0..<clampedLayers).reversed(), id: \.self) { i in
                    let w = CGFloat(210 - i * 34)
                    let isTopTier = (i == clampedLayers - 1)
                    let tierToppings: [String] = {
                        if isTopTier {
                            return Array(toppings.prefix(4))
                        } else {
                            let skipped = 4 + (clampedLayers - 2 - i) * 2
                            if skipped < toppings.count {
                                return Array(toppings.dropFirst(skipped).prefix(2))
                            }
                            return []
                        }
                    }()

                    ZStack(alignment: .top) {
                        // Cake sponge & icing body
                        RoundedRectangle(cornerRadius: 14)
                            .fill(
                                LinearGradient(
                                    colors: [icingColor, icingColor.opacity(0.85)],
                                    startPoint: .top,
                                    endPoint: .bottom
                                )
                            )
                            .frame(width: w, height: 54)
                            .overlay(
                                VStack {
                                    // Scalloped white drip frosting glaze at top of tier
                                    HStack(spacing: 2) {
                                        ForEach(0..<max(Int(w / 14), 1), id: \.self) { _ in
                                            Circle()
                                                .fill(Color.white.opacity(0.85))
                                                .frame(width: 14, height: 14)
                                        }
                                    }
                                    .offset(y: -4)
                                    Spacer()
                                }
                            )
                            .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.white.opacity(0.6), lineWidth: 2))
                            .shadow(color: .black.opacity(0.08), radius: 4, x: 0, y: 2)

                        // Tier Toppings Display
                        if !tierToppings.isEmpty {
                            HStack(spacing: 8) {
                                ForEach(Array(tierToppings.enumerated()), id: \.offset) { _, topId in
                                    toppingView(for: topId)
                                }
                            }
                            .offset(y: -18)
                        }
                    }
                }
                // Elegant Cake Pedestal
                VStack(spacing: 2) {
                    RoundedRectangle(cornerRadius: 10)
                        .fill(
                            LinearGradient(
                                colors: [Color(white: 0.98), Color(white: 0.88), Color(white: 0.95)],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .frame(width: 260, height: 18)
                        .shadow(color: .black.opacity(0.12), radius: 3, x: 0, y: 2)
                    RoundedRectangle(cornerRadius: 6)
                        .fill(Color(white: 0.85))
                        .frame(width: 80, height: 14)
                    RoundedRectangle(cornerRadius: 8)
                        .fill(Color(white: 0.92))
                        .frame(width: 140, height: 10)
                }
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
                    .frame(width: 210, height: 210)

                VStack(spacing: 8) {
                    ZStack {
                        // Fluffy cartoon puppy face with unicorn horn and sparkling eyes
                        Canvas { context, size in
                            let cx = size.width * 0.5
                            let cy = size.height * 0.55
                            let r = size.width * 0.38
                            // Ears
                            for side: CGFloat in [-1.0, 1.0] {
                                var ear = Path()
                                ear.move(to: CGPoint(x: cx + side * r * 0.5, y: cy - r * 0.5))
                                ear.addQuadCurve(to: CGPoint(x: cx + side * r * 1.1, y: cy + r * 0.3), control: CGPoint(x: cx + side * r * 1.3, y: cy - r * 0.3))
                                ear.addQuadCurve(to: CGPoint(x: cx + side * r * 0.7, y: cy + r * 0.1), control: CGPoint(x: cx + side * r * 0.8, y: cy + r * 0.2))
                                ear.closeSubpath()
                                context.fill(ear, with: .color(Color(red: 0.95, green: 0.85, blue: 0.75)))
                            }
                            // Head
                            context.fill(Path(ellipseIn: CGRect(x: cx - r, y: cy - r * 0.9, width: r * 2, height: r * 1.8)), with: .color(Color(red: 1.0, green: 0.96, blue: 0.92)))
                            // Unicorn Horn on Pet
                            var horn = Path()
                            horn.move(to: CGPoint(x: cx - 6, y: cy - r * 0.85))
                            horn.addLine(to: CGPoint(x: cx, y: cy - r * 1.35))
                            horn.addLine(to: CGPoint(x: cx + 6, y: cy - r * 0.85))
                            horn.closeSubpath()
                            context.fill(horn, with: .color(CandyColors.yellow))
                            // Eyes
                            for side: CGFloat in [-1.0, 1.0] {
                                let ex = cx + side * r * 0.38
                                let ey = cy - r * 0.05
                                context.fill(Path(ellipseIn: CGRect(x: ex - 7, y: ey - 9, width: 14, height: 18)), with: .color(Color(red: 0.15, green: 0.08, blue: 0.20)))
                                context.fill(Path(ellipseIn: CGRect(x: ex - 4, y: ey - 7, width: 6, height: 6)), with: .color(.white))
                            }
                            // Snout & Nose
                            context.fill(Path(ellipseIn: CGRect(x: cx - 18, y: cy + r * 0.15, width: 36, height: 26)), with: .color(.white))
                            context.fill(Path(ellipseIn: CGRect(x: cx - 6, y: cy + r * 0.20, width: 12, height: 8)), with: .color(Color(red: 0.25, green: 0.12, blue: 0.20)))
                            // Smile
                            var smile = Path()
                            smile.addArc(center: CGPoint(x: cx, y: cy + r * 0.32), radius: 6, startAngle: .degrees(0), endAngle: .degrees(180), clockwise: false)
                            context.stroke(smile, with: .color(Color(red: 0.25, green: 0.12, blue: 0.20)), lineWidth: 1.8)
                            // Cheeks
                            context.fill(Path(ellipseIn: CGRect(x: cx - r * 0.65, y: cy + r * 0.15, width: 16, height: 10)), with: .color(CandyColors.pink.opacity(0.6)))
                            context.fill(Path(ellipseIn: CGRect(x: cx + r * 0.65 - 16, y: cy + r * 0.15, width: 16, height: 10)), with: .color(CandyColors.pink.opacity(0.6)))
                        }
                        .frame(width: 140, height: 140)

                        if petFed && petGroomed {
                            Image(systemName: "sparkles")
                                .font(.system(size: 26))
                                .foregroundColor(CandyColors.yellow)
                                .offset(x: 50, y: -45)
                        }
                    }

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
