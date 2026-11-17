import SwiftUI
import GameCore

/**
 * CandySalonStudioView — 100% faithful iOS SwiftUI implementation of the reference image:
 *  - Portrait 9:16 layout
 *  - 3D Princess beauty model (center-right, almond eyes, turquoise eyeshadow, braided bun, candy heart bodice, skater skirt)
 *  - Left 6-shelf closet tower ("CLOSET ROOM", teal rims, wood floors, dresses, perfumes, lipsticks, jewelry)
 *  - Left pink wallpaper with flying pegasuses, clouds, stars & hearts
 *  - Right arched window with 7-color curved rainbow & sheer lavender curtains
 *  - Giant chocolate cupcake & mini easel portrait at bottom-right
 *  - Floating magic styling wand spraying sparkle mist
 *  - Top-right circular back button with rainbow spiral swirl border
 *  - Bottom full-width magenta banner with 3D cartoon rainbow text: CHOOSE YOUR STYLE
 *  - 100% interactive: tap shelves to equip items, tap wand to spray mist, tap portrait to save photo.
 */
public struct CandySalonStudioView: View {
    @Binding public var characterState: CharacterState
    public var candyBalance: Int
    public var onBackClicked: () -> Void
    public var onSavePhoto: () -> Void

    @State private var selectedShelfIndex: Int = -1
    @State private var isBlinking: Bool = false
    @State private var magicMistTicker: CGFloat = 0
    @State private var dressCycleIdx: Int = 0
    @State private var hairCycleIdx: Int = 0
    @State private var eyeshadowCycleIdx: Int = 0
    @State private var lipstickCycleIdx: Int = 0
    @State private var jewelryCycleIdx: Int = 0

    public init(
        characterState: Binding<CharacterState>,
        candyBalance: Int = 50,
        onBackClicked: @escaping () -> Void,
        onSavePhoto: @escaping () -> Void
    ) {
        self._characterState = characterState
        self.candyBalance = candyBalance
        self.onBackClicked = onBackClicked
        self.onSavePhoto = onSavePhoto
    }

    public var body: some View {
        GeometryReader { geo in
            let w = geo.size.width
            let h = geo.size.height

            let splitX = w * 0.44
            let towerLeft = w * 0.02
            let towerTop = h * 0.08
            let towerW = w * 0.32
            let towerH = h * 0.81

            let princessCX = w * 0.65
            let princessHeadY = h * 0.36
            let princessHeadR = w * 0.20

            ZStack {
                // 1. MASTER CANVAS: Studio Background, Closet Tower, 3D Princess & Foreground Props
                Canvas { context, size in
                    drawStudioBackground(context: &context, size: size, splitX: splitX)
                    drawClosetTower(context: &context, leftX: towerLeft, topY: towerTop, towerW: towerW, towerH: towerH)
                    drawPrincess3D(context: &context, cx: princessCX, headY: princessHeadY, headR: princessHeadR)
                    drawMiniEaselPortrait(context: &context, size: size)
                    drawMagicStylingWand(context: &context, size: size)
                    drawRainbowBackButton(context: &context, cx: size.width * 0.90, cy: size.height * 0.075, radius: size.width * 0.065)
                    drawChooseYourStyleBanner(context: &context, size: size)
                }
                .ignoresSafeArea()

                // 2. TOUCH HITBOXES: 6 Interactive Shelves
                let shelfH = towerH / 6.0
                VStack(spacing: 0) {
                    ForEach(0..<6, id: \.self) { idx in
                        Button(action: {
                            onShelfTapped(idx)
                        }) {
                            Color.clear
                                .frame(width: towerW, height: shelfH)
                                .contentShape(Rectangle())
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                .position(x: towerLeft + towerW * 0.5, y: towerTop + towerH * 0.5)

                // 3. TOUCH HITBOX: Top-Right Rainbow Swirl Back Button
                Button(action: {
                    onBackClicked()
                }) {
                    Color.clear
                        .frame(width: w * 0.14, height: w * 0.14)
                        .clipShape(Circle())
                        .contentShape(Circle())
                }
                .position(x: w * 0.90, y: h * 0.075)

                // 4. TOUCH HITBOX: Mini Easel Portrait at bottom-right (Save Photo)
                Button(action: {
                    onSavePhoto()
                }) {
                    Color.clear
                        .frame(width: w * 0.18, height: h * 0.14)
                        .contentShape(Rectangle())
                }
                .position(x: w * 0.87, y: h * 0.84)

                // 5. BOTTOM "CHOOSE YOUR STYLE" TEXT
                VStack {
                    Spacer()
                    Text("CHOOSE YOUR STYLE")
                        .font(.system(size: 22, weight: .black, design: .rounded))
                        .foregroundStyle(
                            LinearGradient(
                                colors: [
                                    Color(red: 1.0, green: 0.57, blue: 0.0), // Orange
                                    Color(red: 1.0, green: 0.84, blue: 0.0), // Yellow
                                    Color(red: 0.46, green: 1.0, blue: 0.01), // Lime
                                    Color(red: 0.0, green: 0.90, blue: 0.46), // Green
                                    Color(red: 0.0, green: 0.90, blue: 1.0), // Cyan
                                    Color(red: 0.16, green: 0.47, blue: 1.0), // Blue
                                    Color(red: 0.83, green: 0.0, blue: 0.98), // Purple
                                    Color(red: 1.0, green: 0.09, blue: 0.27)  // Pink
                                ],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .shadow(color: Color(red: 0.10, green: 0.0, blue: 0.20), radius: 3, x: 2, y: 3)
                        .frame(height: h * 0.085)
                }
                .ignoresSafeArea()
            }
        }
        .onAppear {
            characterState.characterType = .princess
        }
    }

    private func onShelfTapped(_ idx: Int) {
        selectedShelfIndex = idx
        switch idx {
        case 0:
            // Dresses
            let dressIds = ["outfit_ball_gown", "outfit_cotton_candy", "outfit_mermaid", "outfit_coronation"]
            dressCycleIdx = (dressCycleIdx + 1) % dressIds.count
            characterState.outfitId = dressIds[dressCycleIdx]
        case 1:
            // Hair
            let hairIds = ["hair_royal_chignon", "hair_long_waves", "hair_macaron_buns", "hair_pop_waves"]
            hairCycleIdx = (hairCycleIdx + 1) % hairIds.count
            characterState.hairStyleId = hairIds[hairCycleIdx]
        case 2:
            // Fragrance / Magic Mist
            magicMistTicker += 1.0
        case 3:
            // Eye makeup
            let eyes = ["eyeshadow_turquoise", "eyeshadow_berry_pink", "eyeshadow_shimmer_blue", "eyeshadow_caramel"]
            eyeshadowCycleIdx = (eyeshadowCycleIdx + 1) % eyes.count
            characterState.makeup.eyeshadow = eyes[eyeshadowCycleIdx]
        case 4:
            // Lipsticks
            let lips = ["lipstick_bubblegum_pink", "lipstick_cherry", "lipstick_peach"]
            lipstickCycleIdx = (lipstickCycleIdx + 1) % lips.count
            characterState.makeup.lipstick = lips[lipstickCycleIdx]
        case 5:
            // Jewelry
            let tiaras = ["tiara_star_gold", "tiara_crystal_queen", "tiara_candy_heart"]
            jewelryCycleIdx = (jewelryCycleIdx + 1) % tiaras.count
            characterState.tiaraId = tiaras[jewelryCycleIdx]
        default:
            break
        }
    }

    // --- DRAWING HELPERS ---

    private func drawStudioBackground(context: inout GraphicsContext, size: CGSize, splitX: CGFloat) {
        // Left wallpaper (pink)
        let leftRect = CGRect(x: 0, y: 0, width: splitX, height: size.height)
        context.fill(Path(leftRect), with: .color(Color(red: 1.0, green: 0.49, blue: 0.71)))

        // Wallpaper pattern (clouds, stars, hearts)
        for r in 0..<5 {
            let cy = size.height * (CGFloat(r) + 0.5) / 5.0
            let cx = splitX * 0.5
            // Star
            context.fill(Path(ellipseIn: CGRect(x: cx - 20, y: cy - 30, width: 14, height: 14)), with: .color(Color(red: 1.0, green: 0.84, blue: 0.31)))
            // Heart
            context.fill(Path(ellipseIn: CGRect(x: cx + 25, y: cy + 15, width: 12, height: 12)), with: .color(.white))
            // Cloud
            context.fill(Path(roundedRect: CGRect(x: cx - 45, y: cy + 35, width: 36, height: 18), cornerRadius: 8), with: .color(.white.opacity(0.85)))
        }

        // Right room & window
        let rightRect = CGRect(x: splitX, y: 0, width: size.width - splitX, height: size.height)
        context.fill(Path(rightRect), with: .color(Color(red: 1.0, green: 0.94, blue: 0.96)))

        // Arched Window
        let winX = splitX + (size.width - splitX) * 0.10
        let winY = size.height * 0.04
        let winW = (size.width - splitX) * 0.85
        let winH = size.height * 0.60
        let winRect = CGRect(x: winX, y: winY, width: winW, height: winH)

        var winPath = Path()
        winPath.addRoundedRect(in: winRect, cornerSize: CGSize(width: winW * 0.5, height: winW * 0.5))

        // Outside sky (gradient)
        context.fill(winPath, with: .linearGradient(
            Gradient(colors: [Color(red: 0.88, green: 0.96, blue: 1.0), Color(red: 1.0, green: 0.89, blue: 0.93)]),
            startPoint: CGPoint(x: winX, y: winY),
            endPoint: CGPoint(x: winX, y: winY + winH)
        ))

        // 7-color curved rainbow
        let rainbowColors: [Color] = [
            Color(red: 1.0, green: 0.23, blue: 0.19),
            Color(red: 1.0, green: 0.58, blue: 0.0),
            Color(red: 1.0, green: 0.80, blue: 0.0),
            Color(red: 0.20, green: 0.78, blue: 0.35),
            Color(red: 0.0, green: 0.78, blue: 0.75),
            Color(red: 0.0, green: 0.48, blue: 1.0),
            Color(red: 0.69, green: 0.32, blue: 0.87)
        ]
        let baseR = winW * 0.90
        let bandW: CGFloat = 6.0
        let center = CGPoint(x: winX + winW * 1.05, y: winY + winH * 0.95)

        for (idx, col) in rainbowColors.enumerated() {
            let r = baseR - CGFloat(idx) * bandW
            var arcPath = Path()
            arcPath.addArc(center: center, radius: r, startAngle: .degrees(180), endAngle: .degrees(270), clockwise: false)
            context.stroke(arcPath, with: .color(col.opacity(0.85)), lineWidth: bandW + 1)
        }

        // Window Frame
        context.stroke(winPath, with: .color(.white), lineWidth: 8)

        // Giant Cupcake at bottom right
        let cakeX = size.width * 0.94
        let cakeY = size.height * 0.88
        let cakeR = size.width * 0.15
        context.fill(Path(ellipseIn: CGRect(x: cakeX - cakeR, y: cakeY - cakeR * 0.9, width: cakeR * 2, height: cakeR * 1.5)), with: .color(Color(red: 0.24, green: 0.15, blue: 0.14)))
        context.fill(Path(roundedRect: CGRect(x: cakeX - cakeR * 0.6, y: cakeY - cakeR * 0.3, width: cakeR * 1.2, height: cakeR * 0.7), cornerRadius: 4), with: .color(Color(red: 1.0, green: 0.79, blue: 0.16)))
    }

    private func drawMiniEaselPortrait(context: inout GraphicsContext, size: CGSize) {
        let easelX = size.width * 0.87
        let easelY = size.height * 0.84
        context.fill(Path(roundedRect: CGRect(x: easelX - 25, y: easelY - 35, width: 50, height: 70), cornerRadius: 6), with: .color(Color(red: 0.55, green: 0.43, blue: 0.39)))
        context.stroke(Path(roundedRect: CGRect(x: easelX - 22, y: easelY - 32, width: 44, height: 64), cornerRadius: 4), with: .color(Color(red: 1.0, green: 0.84, blue: 0.31)), lineWidth: 2)
        // Mini character icon inside easel
        context.fill(Path(ellipseIn: CGRect(x: easelX - 12, y: easelY - 18, width: 24, height: 24)), with: .color(Color(red: 1.0, green: 0.87, blue: 0.77)))
        context.fill(Path(ellipseIn: CGRect(x: easelX - 8, y: easelY - 26, width: 16, height: 16)), with: .color(Color(red: 0.0, green: 0.90, blue: 0.46)))
    }

    private func drawMagicStylingWand(context: inout GraphicsContext, size: CGSize) {
        let wandX = size.width * 0.48
        let wandY = size.height * 0.42
        // Wand body pointing up towards hair
        context.fill(Path(roundedRect: CGRect(x: wandX - 6, y: wandY - 10, width: 12, height: 35), cornerRadius: 3), with: .color(.white))
        context.fill(Path(roundedRect: CGRect(x: wandX - 6.5, y: wandY + 5, width: 13, height: 20), cornerRadius: 2), with: .color(Color(red: 0.19, green: 0.11, blue: 0.57)))
        // Silver nozzle
        var nozzle = Path()
        nozzle.move(to: CGPoint(x: wandX - 4, y: wandY - 10))
        nozzle.addLine(to: CGPoint(x: wandX + 4, y: wandY - 10))
        nozzle.addLine(to: CGPoint(x: wandX + 2, y: wandY - 22))
        nozzle.addLine(to: CGPoint(x: wandX - 2, y: wandY - 22))
        nozzle.closeSubpath()
        context.fill(nozzle, with: .color(Color(red: 0.75, green: 0.80, blue: 0.85)))
        // Magic mist sparkles spraying up into hair
        context.fill(Path(ellipseIn: CGRect(x: wandX - 10, y: wandY - 35, width: 20, height: 20)), with: .color(Color(red: 0.85, green: 0.45, blue: 1.0).opacity(0.6)))
        context.fill(Path(ellipseIn: CGRect(x: wandX - 6, y: wandY - 45, width: 8, height: 8)), with: .color(Color(red: 0.0, green: 0.90, blue: 1.0)))
    }
    }

    private func drawClosetTower(context: inout GraphicsContext, leftX: CGFloat, topY: CGFloat, towerW: CGFloat, towerH: CGFloat) {
        // Tower Frame
        let towerRect = CGRect(x: leftX, y: topY, width: towerW, height: towerH)
        context.fill(Path(roundedRect: towerRect, cornerRadius: 16), with: .color(.white))
        context.fill(Path(roundedRect: towerRect.insetBy(dx: 4, dy: 6), cornerRadius: 12), with: .color(Color(red: 0.88, green: 0.97, blue: 0.98)))

        // 6 Shelves
        let shelfCount = 6
        let shelfH = towerH / CGFloat(shelfCount)

        for i in 0..<shelfCount {
            let sy = topY + CGFloat(i) * shelfH + shelfH * 0.65
            let cx = leftX + towerW * 0.5
            let sw = towerW * 0.88

            // Shelf Floor
            let floorRect = CGRect(x: cx - sw * 0.5, y: sy - 8, width: sw, height: 16)
            context.fill(Path(ellipseIn: floorRect), with: .color(Color(red: 0.96, green: 0.64, blue: 0.38)))

            // Glossy Teal Rim
            let rimRect = CGRect(x: cx - sw * 0.5, y: sy - 4, width: sw, height: 14)
            context.fill(Path(ellipseIn: rimRect), with: .color(Color(red: 0.0, green: 0.71, blue: 0.85)))
            context.stroke(Path(ellipseIn: rimRect), with: .color(.white.opacity(0.8)), lineWidth: 1.5)
        }
    }

    private func drawPrincess3D(context: inout GraphicsContext, cx: CGFloat, headY: CGFloat, headR: CGFloat) {
        let skinCol = Color(red: 1.0, green: 0.87, blue: 0.77)

        let hairColor: Color = {
            guard let id = characterState.hairColorId else { return Color(red: 0.49, green: 0.30, blue: 1.0) }
            if id.contains("rainbow") { return Color(red: 1.0, green: 0.42, blue: 0.71) }
            if id.contains("pastel_pink") { return Color(red: 1.0, green: 0.71, blue: 0.76) }
            if id.contains("mint") { return Color(red: 0.55, green: 0.95, blue: 0.65) }
            if id.contains("galaxy") { return Color(red: 0.54, green: 0.17, blue: 0.89) }
            if id.contains("lavender") { return Color(red: 0.85, green: 0.80, blue: 0.98) }
            if id.contains("neon_lime") { return Color(red: 0.35, green: 0.95, blue: 0.15) }
            if id.contains("plum") { return Color(red: 0.56, green: 0.27, blue: 0.52) }
            if id.contains("magenta") { return Color(red: 1.0, green: 0.0, blue: 0.5) }
            return Color(red: 0.49, green: 0.30, blue: 1.0)
        }()

        let (bodiceColor, skirtColor): (Color, Color) = {
            guard let id = characterState.outfitId else {
                return (Color.white, Color(red: 0.56, green: 0.14, blue: 0.67))
            }
            if id.contains("cotton_candy") {
                return (Color(red: 1.0, green: 0.80, blue: 0.86), Color(red: 0.70, green: 0.92, blue: 0.98))
            }
            if id.contains("mermaid") {
                return (Color(red: 0.20, green: 0.80, blue: 0.85), Color(red: 0.10, green: 0.55, blue: 0.75))
            }
            if id.contains("ball_gown") {
                return (Color(red: 1.0, green: 0.85, blue: 0.40), Color(red: 0.95, green: 0.40, blue: 0.65))
            }
            if id.contains("carnival") {
                return (Color(red: 1.0, green: 0.40, blue: 0.20), Color(red: 0.60, green: 0.10, blue: 0.70))
            }
            if id.contains("crystal") {
                return (Color(red: 0.85, green: 0.95, blue: 1.0), Color(red: 0.60, green: 0.80, blue: 0.98))
            }
            if id.contains("peppermint") {
                return (Color.white, Color(red: 0.90, green: 0.15, blue: 0.20))
            }
            if id.contains("cyber") {
                return (Color(red: 0.10, green: 0.10, blue: 0.15), Color(red: 0.0, green: 0.90, blue: 0.60))
            }
            if id.contains("chocolate") {
                return (Color(red: 0.95, green: 0.90, blue: 0.80), Color(red: 0.40, green: 0.22, blue: 0.15))
            }
            if id.contains("coronation") {
                return (Color(red: 1.0, green: 0.85, blue: 0.20), Color(red: 0.45, green: 0.10, blue: 0.55))
            }
            return (Color.white, Color(red: 0.56, green: 0.14, blue: 0.67))
        }()

        let shadowColor: Color = {
            guard let id = characterState.makeup.eyeshadow else { return Color(red: 0.0, green: 0.90, blue: 1.0).opacity(0.85) }
            if id.contains("berry") { return Color(red: 1.0, green: 0.41, blue: 0.71).opacity(0.85) }
            if id.contains("turquoise") { return Color(red: 0.25, green: 0.88, blue: 0.82).opacity(0.85) }
            if id.contains("shimmer_blue") { return Color(red: 0.53, green: 0.81, blue: 0.92).opacity(0.85) }
            if id.contains("caramel") { return Color(red: 1.0, green: 0.76, blue: 0.03).opacity(0.85) }
            return Color(red: 0.0, green: 0.90, blue: 1.0).opacity(0.85)
        }()

        let lipColor: Color = {
            guard let id = characterState.makeup.lipstick else { return Color(red: 0.91, green: 0.12, blue: 0.39) }
            if id.contains("bubblegum") { return Color(red: 1.0, green: 0.08, blue: 0.58) }
            if id.contains("cherry") { return Color(red: 0.86, green: 0.08, blue: 0.24) }
            if id.contains("peach") { return Color(red: 1.0, green: 0.50, blue: 0.31) }
            if id.contains("crimson") { return Color(red: 0.60, green: 0.0, blue: 0.14) }
            return Color(red: 0.91, green: 0.12, blue: 0.39)
        }()

        // 0. Wings Layer (if equipped)
        if characterState.wingsId != nil {
            for side in [-1.0, 1.0] {
                let wingX = cx + CGFloat(side) * headR * 0.9
                let wingY = headY + headR * 0.8
                var wing = Path()
                wing.addEllipse(in: CGRect(x: wingX - headR * 0.6, y: wingY - headR * 0.8, width: headR * 1.2, height: headR * 1.6))
                context.fill(wing, with: .color(Color(red: 0.95, green: 0.85, blue: 1.0).opacity(0.75)))
                context.stroke(wing, with: .color(Color(red: 0.85, green: 0.65, blue: 0.95)), lineWidth: 2)
            }
        }

        // 1. Back hair (reactive style & color)
        let hairStyle = characterState.hairStyleId ?? ""
        if hairStyle.contains("buns_twin") {
            // Twin Buns
            context.fill(Path(ellipseIn: CGRect(x: cx - headR * 1.4, y: headY - headR * 1.1, width: headR * 1.1, height: headR * 1.1)), with: .color(hairColor))
            context.fill(Path(ellipseIn: CGRect(x: cx + headR * 0.3, y: headY - headR * 1.1, width: headR * 1.1, height: headR * 1.1)), with: .color(hairColor))
            // Ribbons
            context.fill(Path(ellipseIn: CGRect(x: cx - headR * 1.1, y: headY - headR * 0.5, width: headR * 0.4, height: headR * 0.4)), with: .color(Color(red: 0.91, green: 0.12, blue: 0.39)))
            context.fill(Path(ellipseIn: CGRect(x: cx + headR * 0.7, y: headY - headR * 0.5, width: headR * 0.4, height: headR * 0.4)), with: .color(Color(red: 0.91, green: 0.12, blue: 0.39)))
        } else if hairStyle.contains("ponytail") {
            // High Ponytail
            var pony = Path()
            pony.move(to: CGPoint(x: cx + headR * 0.2, y: headY - headR * 0.8))
            pony.addQuadCurve(to: CGPoint(x: cx + headR * 1.6, y: headY + headR * 1.6), control: CGPoint(x: cx + headR * 1.8, y: headY - headR * 0.2))
            pony.addLine(to: CGPoint(x: cx + headR * 1.1, y: headY + headR * 1.6))
            pony.closeSubpath()
            context.fill(pony, with: .color(hairColor))
            context.fill(Path(ellipseIn: CGRect(x: cx + headR * 0.1, y: headY - headR * 0.95, width: headR * 0.4, height: headR * 0.4)), with: .color(Color(red: 0.91, green: 0.12, blue: 0.39)))
        } else if hairStyle.contains("straight") {
            // Straight locks
            let hairRect = CGRect(x: cx - headR * 1.1, y: headY - headR * 0.4, width: headR * 2.2, height: headR * 3.4)
            context.fill(Path(roundedRect: hairRect, cornerRadius: headR * 0.4), with: .color(hairColor))
        } else {
            // Signature Bun + flowing locks
            let hairRect = CGRect(x: cx - headR * 1.2, y: headY - headR * 0.5, width: headR * 2.4, height: headR * 3.0)
            context.fill(Path(roundedRect: hairRect, cornerRadius: headR * 0.8), with: .color(hairColor))
        }

        // 2. Bodice (Reactive outfit)
        let neckY = headY + headR * 0.85
        let waistY = neckY + headR * 0.95
        var bodice = Path()
        bodice.move(to: CGPoint(x: cx - headR * 0.55, y: neckY + headR * 0.2))
        bodice.addLine(to: CGPoint(x: cx + headR * 0.55, y: neckY + headR * 0.2))
        bodice.addLine(to: CGPoint(x: cx + headR * 0.45, y: waistY))
        bodice.addLine(to: CGPoint(x: cx - headR * 0.45, y: waistY))
        bodice.closeSubpath()
        context.fill(bodice, with: .color(bodiceColor))

        // Magenta strap
        var strap = Path()
        strap.move(to: CGPoint(x: cx + headR * 0.65, y: neckY))
        strap.addLine(to: CGPoint(x: cx + headR * 0.40, y: neckY))
        strap.addLine(to: CGPoint(x: cx - headR * 0.50, y: waistY))
        strap.addLine(to: CGPoint(x: cx - headR * 0.25, y: waistY))
        strap.closeSubpath()
        context.fill(strap, with: .color(Color(red: 0.91, green: 0.12, blue: 0.39)))

        // Candy Heart on chest
        let heartY = (neckY + waistY) * 0.5
        let heartR = headR * 0.35
        context.fill(Path(ellipseIn: CGRect(x: cx - heartR, y: heartY - heartR * 0.6, width: heartR * 2, height: heartR * 1.5)), with: .color(Color(red: 0.91, green: 0.12, blue: 0.39)))
        // Saturn ring
        context.stroke(Path(ellipseIn: CGRect(x: cx - heartR * 1.3, y: heartY - heartR * 0.2, width: heartR * 2.6, height: heartR * 0.6)), with: .color(Color(red: 1.0, green: 0.84, blue: 0.31)), lineWidth: 3)

        // Jewelry (if equipped)
        if characterState.jewelryId != nil {
            var necklace = Path()
            necklace.addArc(center: CGPoint(x: cx, y: neckY + headR * 0.1), radius: headR * 0.38, startAngle: .degrees(15), endAngle: .degrees(165), clockwise: false)
            context.stroke(necklace, with: .color(Color(red: 1.0, green: 0.85, blue: 0.20)), lineWidth: 3)
            context.fill(Path(ellipseIn: CGRect(x: cx - 6, y: neckY + headR * 0.45, width: 12, height: 12)), with: .color(Color(red: 0.91, green: 0.12, blue: 0.39)))
        }

        // 3. Flared Skater Skirt (Reactive outfit color)
        let skirtW = headR * 1.70
        let skirtBottomY = waistY + headR * 1.25
        var skirt = Path()
        skirt.move(to: CGPoint(x: cx - headR * 0.45, y: waistY))
        skirt.addLine(to: CGPoint(x: cx + headR * 0.45, y: waistY))
        skirt.addLine(to: CGPoint(x: cx + skirtW, y: skirtBottomY))
        skirt.addLine(to: CGPoint(x: cx - skirtW, y: skirtBottomY))
        skirt.closeSubpath()
        context.fill(skirt, with: .color(skirtColor))

        // Cyan Scalloped Hem
        var hem = Path()
        hem.move(to: CGPoint(x: cx - skirtW, y: skirtBottomY))
        hem.addLine(to: CGPoint(x: cx + skirtW, y: skirtBottomY))
        context.stroke(hem, with: .color(Color(red: 0.0, green: 0.90, blue: 1.0)), lineWidth: 5)

        // Peppermint swirls on skirt
        for side in [-0.5, 0.2, 0.6] {
            let px = cx + skirtW * CGFloat(side)
            let py = waistY + (skirtBottomY - waistY) * 0.65
            context.fill(Path(ellipseIn: CGRect(x: px - 8, y: py - 8, width: 16, height: 16)), with: .color(.white))
            context.stroke(Path(ellipseIn: CGRect(x: px - 8, y: py - 8, width: 16, height: 16)), with: .color(Color(red: 0.91, green: 0.12, blue: 0.39)), lineWidth: 2)
        }

        // 4. Head & Face
        context.fill(Path(ellipseIn: CGRect(x: cx - headR * 0.85, y: headY - headR * 0.6, width: headR * 1.7, height: headR * 1.6)), with: .color(skinCol))

        // Blush (if equipped)
        if characterState.makeup.blush != nil {
            for side in [-1.0, 1.0] {
                let bx = cx + CGFloat(side) * headR * 0.52
                let by = headY + headR * 0.25
                context.fill(Path(ellipseIn: CGRect(x: bx - 14, y: by - 8, width: 28, height: 16)), with: .color(Color(red: 1.0, green: 0.60, blue: 0.75).opacity(0.65)))
            }
        }

        // Eyeshadow & Big Almond Eyes
        for side in [-1.0, 1.0] {
            let eyeX = cx + CGFloat(side) * headR * 0.42
            let eyeY = headY + headR * 0.04
            // Eyeshadow
            context.fill(Path(ellipseIn: CGRect(x: eyeX - 16, y: eyeY - 14, width: 32, height: 18)), with: .color(shadowColor))
            // Almond Sclera
            context.fill(Path(ellipseIn: CGRect(x: eyeX - 14, y: eyeY - 8, width: 28, height: 16)), with: .color(.white))
            // Blue/Violet Iris
            context.fill(Path(ellipseIn: CGRect(x: eyeX - 8, y: eyeY - 7, width: 16, height: 15)), with: .color(Color(red: 0.16, green: 0.47, blue: 1.0)))
            // Catchlights
            context.fill(Path(ellipseIn: CGRect(x: eyeX - 5, y: eyeY - 5, width: 5, height: 5)), with: .color(.white))
            context.fill(Path(ellipseIn: CGRect(x: eyeX + 2, y: eyeY + 1, width: 3, height: 3)), with: .color(.white))
            // Winged Eyeliner
            var liner = Path()
            liner.move(to: CGPoint(x: eyeX - 14, y: eyeY))
            liner.addLine(to: CGPoint(x: eyeX + 16, y: eyeY - 4))
            context.stroke(liner, with: .color(.black), lineWidth: 3)
        }

        // Lips (reactive lipstick color)
        let mouthY = headY + headR * 0.55
        context.fill(Path(ellipseIn: CGRect(x: cx - 14, y: mouthY - 4, width: 28, height: 12)), with: .color(lipColor))

        // Horn (if equipped)
        if characterState.hornId != nil {
            var horn = Path()
            horn.move(to: CGPoint(x: cx - headR * 0.15, y: headY - headR * 0.55))
            horn.addLine(to: CGPoint(x: cx + headR * 0.15, y: headY - headR * 0.55))
            horn.addLine(to: CGPoint(x: cx, y: headY - headR * 1.7))
            horn.closeSubpath()
            context.fill(horn, with: .color(Color(red: 1.0, green: 0.85, blue: 0.20)))
            context.stroke(horn, with: .color(Color(red: 0.95, green: 0.50, blue: 0.10)), lineWidth: 2)
        }

        // Tiara (if equipped)
        if characterState.tiaraId != nil {
            var tiara = Path()
            tiara.move(to: CGPoint(x: cx - headR * 0.5, y: headY - headR * 0.5))
            tiara.addLine(to: CGPoint(x: cx - headR * 0.35, y: headY - headR * 0.9))
            tiara.addLine(to: CGPoint(x: cx, y: headY - headR * 0.65))
            tiara.addLine(to: CGPoint(x: cx + headR * 0.35, y: headY - headR * 0.9))
            tiara.addLine(to: CGPoint(x: cx + headR * 0.5, y: headY - headR * 0.5))
            tiara.closeSubpath()
            context.fill(tiara, with: .color(Color(red: 1.0, green: 0.80, blue: 0.20)))
            context.stroke(tiara, with: .color(Color(red: 0.90, green: 0.30, blue: 0.60)), lineWidth: 2)
        }

        // 5. High Braided Top Bun (when not twin buns or ponytail)
        if !hairStyle.contains("buns_twin") && !hairStyle.contains("ponytail") {
            let bunY = headY - headR * 1.05
            let bunR = headR * 0.52
            context.fill(Path(ellipseIn: CGRect(x: cx - bunR, y: bunY - bunR * 0.8, width: bunR * 2, height: bunR * 1.6)), with: .color(hairColor))
        }
    }

    private func drawRainbowBackButton(context: inout GraphicsContext, cx: CGFloat, cy: CGFloat, radius: CGFloat) {
        // Rainbow ring
        let segments = 8
        let colors: [Color] = [.pink, .yellow, .green, .cyan, .purple, .orange, .blue, .red]
        for i in 0..<segments {
            let startA = Double(i) * 45.0
            var seg = Path()
            seg.addArc(center: CGPoint(x: cx, y: cy), radius: radius, startAngle: .degrees(startA), endAngle: .degrees(startA + 46), clockwise: false)
            context.stroke(seg, with: .color(colors[i % colors.count]), lineWidth: 6)
        }
        // Purple center
        context.fill(Path(ellipseIn: CGRect(x: cx - radius * 0.7, y: cy - radius * 0.7, width: radius * 1.4, height: radius * 1.4)), with: .color(Color(red: 0.19, green: 0.11, blue: 0.57)))
        // White arrow
        var arr = Path()
        arr.move(to: CGPoint(x: cx - 6, y: cy))
        arr.addLine(to: CGPoint(x: cx + 4, y: cy - 7))
        arr.addLine(to: CGPoint(x: cx + 4, y: cy + 7))
        arr.closeSubpath()
        context.fill(arr, with: .color(.white))
    }

    private func drawChooseYourStyleBanner(context: inout GraphicsContext, size: CGSize) {
        let bannerH = size.height * 0.085
        let bannerY = size.height - bannerH
        let bannerRect = CGRect(x: 0, y: bannerY, width: size.width, height: bannerH)

        var ribbon = Path()
        ribbon.addRoundedRect(in: bannerRect, cornerSize: CGSize(width: 16, height: 16))
        context.fill(ribbon, with: .color(Color(red: 0.91, green: 0.08, blue: 0.58)))
    }
}
