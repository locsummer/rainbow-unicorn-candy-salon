import SwiftUI
import UIKit
import GameCore

public enum StageBackdrop: String, CaseIterable {
    case pastel
    case disco
    case forest
}

public enum CharacterPose: String, CaseIterable {
    case idle
    case happy
    case star
    case wave
}

public enum SpritePalette {
    public static func uiColor(hex: String?, fallback: UIColor = UIColor(red: 1.0, green: 0.35, blue: 0.59, alpha: 1.0)) -> UIColor {
        guard let hex = hex, !hex.isEmpty else { return fallback }
        let cleaned = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var intVal: UInt64 = 0
        guard Scanner(string: cleaned).scanHexInt64(&intVal), cleaned.count == 6 else { return fallback }
        let r = CGFloat((intVal >> 16) & 0xFF) / 255.0
        let g = CGFloat((intVal >> 8) & 0xFF) / 255.0
        let b = CGFloat(intVal & 0xFF) / 255.0
        return UIColor(red: r, green: g, blue: b, alpha: 1.0)
    }

    public static func color(hex: String?, fallback: Color = CandyColors.pink) -> Color {
        Color(uiColor(hex: hex, fallback: UIColor(fallback)))
    }

    public static func hairColor(_ id: String?) -> Color {
        guard let id = id else { return CandyColors.purple }
        if id.contains("rainbow") { return Color(red: 1.0, green: 0.42, blue: 0.71) }
        if id.contains("pastel_pink") { return Color(red: 1.0, green: 0.72, blue: 0.80) }
        if id.contains("mint_green") { return Color(red: 0.45, green: 0.92, blue: 0.78) }
        if id.contains("galaxy_purple") { return Color(red: 0.55, green: 0.20, blue: 0.90) }
        if id.contains("lavender") { return Color(red: 0.82, green: 0.75, blue: 0.98) }
        if id.contains("neon_lime") { return Color(red: 0.38, green: 0.94, blue: 0.25) }
        if id.contains("plum") { return Color(red: 0.56, green: 0.25, blue: 0.50) }
        if id.contains("magenta") { return Color(red: 0.95, green: 0.05, blue: 0.55) }
        return CandyColors.purple
    }

    public static func hairUIColor(_ id: String?) -> UIColor {
        UIColor(hairColor(id))
    }

    public static func eyeshadowColor(_ id: String?) -> Color {
        guard let id = id else { return .clear }
        if id.contains("berry_pink") { return Color(red: 1.0, green: 0.40, blue: 0.70) }
        if id.contains("turquoise") { return Color(red: 0.15, green: 0.85, blue: 0.82) }
        if id.contains("shimmer_blue") { return Color(red: 0.45, green: 0.80, blue: 0.95) }
        if id.contains("caramel") { return Color(red: 1.0, green: 0.72, blue: 0.15) }
        return CandyColors.pink
    }

    public static func lipstickColor(_ id: String?) -> Color {
        guard let id = id else { return Color(red: 1.0, green: 0.45, blue: 0.62) }
        if id.contains("bubblegum") { return Color(red: 1.0, green: 0.25, blue: 0.65) }
        if id.contains("cherry") { return Color(red: 0.88, green: 0.08, blue: 0.22) }
        if id.contains("peach") { return Color(red: 1.0, green: 0.52, blue: 0.38) }
        if id.contains("crimson") { return Color(red: 0.65, green: 0.05, blue: 0.18) }
        return Color(red: 1.0, green: 0.45, blue: 0.62)
    }

    public static func outfitColor(_ id: String?) -> Color {
        guard let id = id else { return CandyColors.pink }
        if id.contains("cotton_candy") { return Color(red: 1.0, green: 0.70, blue: 0.82) }
        if id.contains("pastry_chef") { return Color(red: 0.98, green: 0.98, blue: 1.0) }
        if id.contains("mermaid") { return Color(red: 0.25, green: 0.85, blue: 0.88) }
        if id.contains("ball_gown") { return Color(red: 0.96, green: 0.18, blue: 0.54) }
        if id.contains("carnival") { return Color(red: 0.68, green: 0.25, blue: 0.80) }
        if id.contains("crystal") { return Color(red: 0.65, green: 0.90, blue: 0.98) }
        if id.contains("peppermint") { return Color(red: 0.95, green: 0.25, blue: 0.28) }
        if id.contains("cyber") { return Color(red: 0.0, green: 0.92, blue: 0.50) }
        if id.contains("chocolate") { return Color(red: 0.45, green: 0.28, blue: 0.22) }
        if id.contains("coronation") { return Color(red: 1.0, green: 0.82, blue: 0.20) }
        return CandyColors.purple
    }

    public static func outfitUIColor(_ id: String?) -> UIColor {
        UIColor(outfitColor(id))
    }
}

// MARK: - Master Vector Character View (Pure SwiftUI Canvas, Zero Letterboxing)

public struct CharacterSpriteView: View {
    public let characterState: CharacterState
    public var backdrop: StageBackdrop = .pastel
    public var pose: CharacterPose = .idle
    public var drawBackdrop: Bool = false

    public init(
        characterState: CharacterState,
        backdrop: StageBackdrop = .pastel,
        pose: CharacterPose = .idle,
        drawBackdrop: Bool = false
    ) {
        self.characterState = characterState
        self.backdrop = backdrop
        self.pose = pose
        self.drawBackdrop = drawBackdrop
    }

    public var body: some View {
        GeometryReader { geo in
            let w = geo.size.width
            let h = geo.size.height

            Canvas { context, size in
                drawCharacterVector(context: &context, size: size)
            }
            .frame(width: w, height: h)
        }
    }

    // MARK: - Snapshot Renderer for Photo Shoot
    public static func renderSnapshot(
        characterState: CharacterState,
        size: CGSize = CGSize(width: 720, height: 960),
        backdrop: StageBackdrop = .pastel,
        pose: CharacterPose = .happy
    ) -> UIImage {
        let renderer = UIGraphicsImageRenderer(size: size)
        return renderer.image { ctx in
            let cg = ctx.cgContext
            drawSnapshotToCG(cg: cg, size: size, state: characterState, backdrop: backdrop, pose: pose)
        }
    }

    private static func drawSnapshotToCG(
        cg: CGContext,
        size: CGSize,
        state: CharacterState,
        backdrop: StageBackdrop,
        pose: CharacterPose
    ) {
        // Background
        let bgCol: UIColor
        switch backdrop {
        case .pastel: bgCol = UIColor(red: 1.0, green: 0.92, blue: 0.96, alpha: 1.0)
        case .disco: bgCol = UIColor(red: 0.18, green: 0.08, blue: 0.26, alpha: 1.0)
        case .forest: bgCol = UIColor(red: 0.90, green: 0.97, blue: 0.92, alpha: 1.0)
        }
        cg.setFillColor(bgCol.cgColor)
        cg.fill(CGRect(origin: .zero, size: size))

        // Pedestal glow
        cg.setFillColor(UIColor(white: 1.0, alpha: 0.7).cgColor)
        cg.fillEllipse(in: CGRect(x: size.width * 0.15, y: size.height * 0.72, width: size.width * 0.70, height: size.height * 0.16))

        // Render vector layers via CoreGraphics
        let cx = size.width * 0.5
        let headY = size.height * 0.36
        let headR = min(size.width * 0.26, size.height * 0.20)
        let hairCol = SpritePalette.hairUIColor(state.hairColorId)

        // Back hair
        cg.setFillColor(hairCol.cgColor)
        cg.fillEllipse(in: CGRect(x: cx - headR * 1.3, y: headY - headR * 0.5, width: headR * 2.6, height: headR * 3.0))

        // Body
        let skinCol = state.characterType == .unicorn
            ? UIColor(red: 1.0, green: 0.97, blue: 0.98, alpha: 1.0)
            : UIColor(red: 1.0, green: 0.90, blue: 0.82, alpha: 1.0)
        cg.setFillColor(skinCol.cgColor)
        cg.fill(CGRect(x: cx - headR * 0.45, y: headY + headR * 0.6, width: headR * 0.9, height: headR * 1.6))

        // Head
        cg.fillEllipse(in: CGRect(x: cx - headR, y: headY - headR * 0.85, width: headR * 2, height: headR * 1.9))

        // Eyes
        cg.setFillColor(UIColor.white.cgColor)
        cg.fillEllipse(in: CGRect(x: cx - headR * 0.55, y: headY - headR * 0.1, width: headR * 0.42, height: headR * 0.46))
        cg.fillEllipse(in: CGRect(x: cx + headR * 0.13, y: headY - headR * 0.1, width: headR * 0.42, height: headR * 0.46))

        cg.setFillColor(UIColor(red: 0.4, green: 0.15, blue: 0.65, alpha: 1.0).cgColor)
        cg.fillEllipse(in: CGRect(x: cx - headR * 0.48, y: headY - headR * 0.05, width: headR * 0.30, height: headR * 0.36))
        cg.fillEllipse(in: CGRect(x: cx + headR * 0.18, y: headY - headR * 0.05, width: headR * 0.30, height: headR * 0.36))

        // Outfit
        let outCol = SpritePalette.outfitUIColor(state.outfitId)
        cg.setFillColor(outCol.cgColor)
        let skirt = CGMutablePath()
        skirt.move(to: CGPoint(x: cx - headR * 0.55, y: headY + headR * 1.4))
        skirt.addLine(to: CGPoint(x: cx + headR * 0.55, y: headY + headR * 1.4))
        skirt.addLine(to: CGPoint(x: cx + headR * 1.45, y: headY + headR * 2.7))
        skirt.addLine(to: CGPoint(x: cx - headR * 1.45, y: headY + headR * 2.7))
        skirt.closeSubpath()
        cg.addPath(skirt)
        cg.fillPath()
    }

    // MARK: - Master Canvas Vector Rendering Engine
    private func drawCharacterVector(context: inout GraphicsContext, size: CGSize) {
        let w = size.width
        let h = size.height

        // Anchors & Proportions: Large, Centered, Majestic
        let cx = w * 0.5
        let headY = h * 0.34
        let headR = min(w * 0.26, h * 0.20)
        let neckY = headY + headR * 0.85
        let waistY = neckY + headR * 0.95
        let skirtBottomY = min(h * 0.92, waistY + headR * 1.6)

        // Palette resolution
        let hairColor = SpritePalette.hairColor(characterState.hairColorId)
        let skinColor: Color = characterState.characterType == .unicorn
            ? Color(red: 1.0, green: 0.97, blue: 0.98)
            : Color(red: 1.0, green: 0.90, blue: 0.82)
        let skinShadow = Color(red: 0.92, green: 0.78, blue: 0.72)
        let outfitColor = SpritePalette.outfitColor(characterState.outfitId)

        // STAGE 0: Backdrop & Pedestal Glow
        if drawBackdrop {
            let bgRect = CGRect(origin: .zero, size: size)
            let grad: Gradient
            switch backdrop {
            case .pastel:
                grad = Gradient(colors: [Color(red: 1.0, green: 0.93, blue: 0.96), Color(red: 0.94, green: 0.88, blue: 0.98), Color.white])
            case .disco:
                grad = Gradient(colors: [Color(red: 0.25, green: 0.08, blue: 0.35), Color(red: 0.12, green: 0.04, blue: 0.18)])
            case .forest:
                grad = Gradient(colors: [Color(red: 0.88, green: 0.98, blue: 0.90), Color(red: 0.78, green: 0.94, blue: 0.85)])
            }
            context.fill(Path(bgRect), with: .linearGradient(grad, startPoint: .top, endPoint: .bottom))
        }

        // Soft floor pedestal shadow
        let shadowRect = CGRect(x: cx - headR * 1.6, y: skirtBottomY - headR * 0.25, width: headR * 3.2, height: headR * 0.5)
        context.fill(Path(ellipseIn: shadowRect), with: .color(Color(red: 0.75, green: 0.55, blue: 0.70).opacity(0.28)))

        // STAGE 1: Back Wings
        if let wId = characterState.wingsId {
            drawBackWings(context: &context, cx: cx, neckY: neckY, headR: headR, wingId: wId)
        }

        // STAGE 2: Unicorn Tail (if unicorn)
        if characterState.characterType == .unicorn {
            drawUnicornTail(context: &context, cx: cx, waistY: waistY, headR: headR, hairColor: hairColor)
        }

        // STAGE 3: Back Hair
        drawBackHair(context: &context, cx: cx, headY: headY, headR: headR, hairStyleId: characterState.hairStyleId, hairColor: hairColor)

        // STAGE 4: Unicorn Pointy Ears (if unicorn)
        if characterState.characterType == .unicorn {
            drawUnicornEars(context: &context, cx: cx, headY: headY, headR: headR, skinColor: skinColor)
        }

        // STAGE 5: Torso & Neck & Arms
        drawBody(context: &context, cx: cx, headY: headY, neckY: neckY, waistY: waistY, headR: headR, skinColor: skinColor, skinShadow: skinShadow)

        // STAGE 6: Head & Facial Features
        drawHeadAndFace(context: &context, cx: cx, headY: headY, headR: headR, skinColor: skinColor, skinShadow: skinShadow)

        // STAGE 7: Makeup (Eyeshadow, Blush, Lipstick, Facepaint)
        drawMakeup(context: &context, cx: cx, headY: headY, headR: headR)

        // STAGE 8: Outfit (Bodice & Skirt hugging body curves)
        if characterState.outfitId != nil {
            drawOutfit(context: &context, cx: cx, neckY: neckY, waistY: waistY, skirtBottomY: skirtBottomY, headR: headR, outfitColor: outfitColor)
        }

        // STAGE 9: Front Hair / Bangs (Distinct geometry per hairStyleId)
        drawFrontHair(context: &context, cx: cx, headY: headY, headR: headR, hairStyleId: characterState.hairStyleId, hairColor: hairColor)

        // STAGE 10: Horn (Top center of head)
        if let hornId = characterState.hornId {
            drawHorn(context: &context, cx: cx, headY: headY, headR: headR, hornId: hornId)
        }

        // STAGE 11: Tiara (Over forehead & bangs)
        if let tiaraId = characterState.tiaraId {
            drawTiara(context: &context, cx: cx, headY: headY, headR: headR, tiaraId: tiaraId)
        }

        // STAGE 12: Jewelry (Necklace & Earrings)
        if let jewelId = characterState.jewelryId {
            drawJewelry(context: &context, cx: cx, headY: headY, neckY: neckY, headR: headR, jewelId: jewelId)
        }

        // STAGE 13: Stickers
        if !characterState.stickers.isEmpty {
            drawStickers(context: &context, cx: cx, headY: headY, waistY: waistY, headR: headR, stickers: characterState.stickers)
        }
    }

    // MARK: - Layer Implementations

    private func drawBackWings(context: inout GraphicsContext, cx: CGFloat, neckY: CGFloat, headR: CGFloat, wingId: String) {
        let isRainbow = wingId.contains("rainbow")
        let wingSpan = headR * 1.8
        let wingH = headR * 2.6

        for side: CGFloat in [-1.0, 1.0] {
            var wing = Path()
            let rootX = cx + side * headR * 0.45
            let rootY = neckY + headR * 0.1
            wing.move(to: CGPoint(x: rootX, y: rootY))
            wing.addQuadCurve(to: CGPoint(x: rootX + side * wingSpan, y: rootY - wingH * 0.7), control: CGPoint(x: rootX + side * wingSpan * 0.5, y: rootY - wingH * 0.95))
            wing.addQuadCurve(to: CGPoint(x: rootX + side * wingSpan * 0.75, y: rootY + wingH * 0.2), control: CGPoint(x: rootX + side * wingSpan * 1.1, y: rootY - wingH * 0.1))
            wing.addQuadCurve(to: CGPoint(x: rootX, y: rootY + headR * 0.5), control: CGPoint(x: rootX + side * wingSpan * 0.3, y: rootY + wingH * 0.3))
            wing.closeSubpath()

            let wingGrad = isRainbow
                ? Gradient(colors: [Color(red: 1.0, green: 0.6, blue: 0.8), Color(red: 0.9, green: 0.7, blue: 1.0), Color(red: 0.5, green: 0.85, blue: 1.0)])
                : Gradient(colors: [Color(red: 1.0, green: 0.92, blue: 0.60), Color(red: 1.0, green: 0.70, blue: 0.85)])

            context.fill(wing, with: .linearGradient(wingGrad, startPoint: CGPoint(x: rootX, y: rootY - wingH), endPoint: CGPoint(x: rootX + side * wingSpan, y: rootY + wingH)))
            context.stroke(wing, with: .color(Color.white.opacity(0.9)), lineWidth: 2.5)
        }
    }

    private func drawUnicornTail(context: inout GraphicsContext, cx: CGFloat, waistY: CGFloat, headR: CGFloat, hairColor: Color) {
        var tail = Path()
        let root = CGPoint(x: cx + headR * 0.5, y: waistY + headR * 0.4)
        tail.move(to: root)
        tail.addQuadCurve(to: CGPoint(x: cx + headR * 1.7, y: waistY + headR * 1.3), control: CGPoint(x: cx + headR * 1.6, y: waistY + headR * 0.5))
        tail.addQuadCurve(to: CGPoint(x: cx + headR * 1.2, y: waistY + headR * 1.8), control: CGPoint(x: cx + headR * 1.8, y: waistY + headR * 1.7))
        tail.addQuadCurve(to: root, control: CGPoint(x: cx + headR * 0.8, y: waistY + headR * 1.2))
        tail.closeSubpath()

        context.fill(tail, with: .color(hairColor))
        context.stroke(tail, with: .color(Color.white.opacity(0.6)), lineWidth: 1.5)
    }

    private func drawBackHair(context: inout GraphicsContext, cx: CGFloat, headY: CGFloat, headR: CGFloat, hairStyleId: String?, hairColor: Color) {
        let style = hairStyleId ?? "hair_long_waves"

        if style.contains("buns") {
            // Twin Puffy Buns on Top Left & Top Right
            for side: CGFloat in [-1.0, 1.0] {
                let bx = cx + side * headR * 0.95
                let by = headY - headR * 0.65
                let bunRect = CGRect(x: bx - headR * 0.52, y: by - headR * 0.52, width: headR * 1.04, height: headR * 1.04)
                context.fill(Path(ellipseIn: bunRect), with: .color(hairColor))
                context.stroke(Path(ellipseIn: bunRect), with: .color(Color.white.opacity(0.5)), lineWidth: 2)

                // Candy ribbon wrap at bun base
                let ribRect = CGRect(x: bx - headR * 0.35, y: by + headR * 0.28, width: headR * 0.70, height: headR * 0.22)
                context.fill(Path(roundedRect: ribRect, cornerRadius: 4), with: .color(CandyColors.pink))
            }
        } else if style.contains("ponytail") {
            // High Side-swept Ponytail on the Right
            var pony = Path()
            let pRoot = CGPoint(x: cx + headR * 0.45, y: headY - headR * 0.45)
            pony.move(to: pRoot)
            pony.addQuadCurve(to: CGPoint(x: cx + headR * 1.65, y: headY + headR * 0.5), control: CGPoint(x: cx + headR * 1.5, y: headY - headR * 0.6))
            pony.addQuadCurve(to: CGPoint(x: cx + headR * 1.25, y: headY + headR * 1.4), control: CGPoint(x: cx + headR * 1.85, y: headY + headR * 1.0))
            pony.addQuadCurve(to: pRoot, control: CGPoint(x: cx + headR * 0.75, y: headY + headR * 0.2))
            pony.closeSubpath()

            context.fill(pony, with: .color(hairColor))
            context.stroke(pony, with: .color(Color.white.opacity(0.5)), lineWidth: 2)

            // Star scrunchie
            context.fill(Path(ellipseIn: CGRect(x: pRoot.x - headR * 0.18, y: pRoot.y - headR * 0.18, width: headR * 0.36, height: headR * 0.36)), with: .color(CandyColors.yellow))
        } else if style.contains("straight") {
            // Long Sleek Cascading Locks behind shoulders
            var locks = Path()
            locks.addRoundedRect(in: CGRect(x: cx - headR * 1.25, y: headY - headR * 0.3, width: headR * 2.50, height: headR * 2.8), cornerSize: CGSize(width: headR * 0.6, height: headR * 0.6))
            context.fill(locks, with: .color(hairColor))
        } else {
            // Voluminous Waves / Pop Waves
            var waves = Path()
            waves.addRoundedRect(in: CGRect(x: cx - headR * 1.35, y: headY - headR * 0.35, width: headR * 2.70, height: headR * 2.9), cornerSize: CGSize(width: headR * 0.9, height: headR * 0.9))
            context.fill(waves, with: .color(hairColor))
        }
    }

    private func drawUnicornEars(context: inout GraphicsContext, cx: CGFloat, headY: CGFloat, headR: CGFloat, skinColor: Color) {
        for side: CGFloat in [-1.0, 1.0] {
            var ear = Path()
            let bx = cx + side * headR * 0.55
            let by = headY - headR * 0.50
            ear.move(to: CGPoint(x: bx - headR * 0.18, y: by))
            ear.addLine(to: CGPoint(x: bx + side * headR * 0.32, y: by - headR * 0.85))
            ear.addLine(to: CGPoint(x: bx + headR * 0.20, y: by + headR * 0.10))
            ear.closeSubpath()

            context.fill(ear, with: .color(skinColor))
            context.stroke(ear, with: .color(Color(red: 0.85, green: 0.70, blue: 0.78)), lineWidth: 1.5)

            // Inner ear pink shading
            var inEar = Path()
            inEar.move(to: CGPoint(x: bx - headR * 0.08, y: by - headR * 0.1))
            inEar.addLine(to: CGPoint(x: bx + side * headR * 0.22, y: by - headR * 0.65))
            inEar.addLine(to: CGPoint(x: bx + headR * 0.10, y: by))
            inEar.closeSubpath()
            context.fill(inEar, with: .color(Color(red: 1.0, green: 0.75, blue: 0.85).opacity(0.8)))
        }
    }

    private func drawBody(
        context: inout GraphicsContext,
        cx: CGFloat,
        headY: CGFloat,
        neckY: CGFloat,
        waistY: CGFloat,
        headR: CGFloat,
        skinColor: Color,
        skinShadow: Color
    ) {
        // Neck
        let neckW = headR * 0.40
        let neckH = headR * 0.45
        let neckRect = CGRect(x: cx - neckW * 0.5, y: neckY - neckH * 0.3, width: neckW, height: neckH)
        context.fill(Path(neckRect), with: .color(skinColor))

        // Torso / Shoulders
        var torso = Path()
        let shoulderW = headR * 1.55
        let waistW = headR * 1.05
        torso.move(to: CGPoint(x: cx - shoulderW * 0.5, y: neckY + headR * 0.25))
        torso.addLine(to: CGPoint(x: cx + shoulderW * 0.5, y: neckY + headR * 0.25))
        torso.addQuadCurve(to: CGPoint(x: cx + waistW * 0.5, y: waistY), control: CGPoint(x: cx + shoulderW * 0.48, y: waistY - headR * 0.3))
        torso.addLine(to: CGPoint(x: cx - waistW * 0.5, y: waistY))
        torso.addQuadCurve(to: CGPoint(x: cx - shoulderW * 0.5, y: neckY + headR * 0.25), control: CGPoint(x: cx - shoulderW * 0.48, y: waistY - headR * 0.3))
        torso.closeSubpath()

        context.fill(torso, with: .color(skinColor))

        // Arms posed gently at sides
        for side: CGFloat in [-1.0, 1.0] {
            var arm = Path()
            let ax = cx + side * (shoulderW * 0.48)
            let ay = neckY + headR * 0.28
            arm.move(to: CGPoint(x: ax, y: ay))
            arm.addQuadCurve(to: CGPoint(x: cx + side * (waistW * 0.65), y: waistY + headR * 0.35), control: CGPoint(x: ax + side * headR * 0.15, y: ay + headR * 0.6))
            arm.addLine(to: CGPoint(x: cx + side * (waistW * 0.48), y: waistY + headR * 0.35))
            arm.addQuadCurve(to: CGPoint(x: ax - side * headR * 0.12, y: ay + headR * 0.1), control: CGPoint(x: ax + side * headR * 0.02, y: ay + headR * 0.5))
            arm.closeSubpath()

            context.fill(arm, with: .color(skinColor))
        }
    }

    private func drawHeadAndFace(
        context: inout GraphicsContext,
        cx: CGFloat,
        headY: CGFloat,
        headR: CGFloat,
        skinColor: Color,
        skinShadow: Color
    ) {
        // Head Contour (Graceful slightly tapered chin)
        var head = Path()
        head.addEllipse(in: CGRect(x: cx - headR, y: headY - headR * 0.88, width: headR * 2.0, height: headR * 1.85))
        context.fill(head, with: .color(skinColor))
        context.stroke(head, with: .color(skinShadow.opacity(0.35)), lineWidth: 1.5)

        // Eyebrows
        for side: CGFloat in [-1.0, 1.0] {
            var brow = Path()
            let bx = cx + side * headR * 0.38
            let by = headY - headR * 0.30
            brow.move(to: CGPoint(x: bx - side * headR * 0.20, y: by + headR * 0.04))
            brow.addQuadCurve(to: CGPoint(x: bx + side * headR * 0.20, y: by), control: CGPoint(x: bx, y: by - headR * 0.08))
            context.stroke(brow, with: .color(Color(red: 0.40, green: 0.22, blue: 0.35)), lineWidth: 2.2)
        }

        // Eyes: Big, Glossy, Expressive Anime Eyes
        let eyeW = headR * 0.46
        let eyeH = headR * 0.50
        for side: CGFloat in [-1.0, 1.0] {
            let ex = cx + side * headR * 0.38
            let ey = headY - headR * 0.02

            // Sclera
            let eyeRect = CGRect(x: ex - eyeW * 0.5, y: ey - eyeH * 0.5, width: eyeW, height: eyeH)
            context.fill(Path(ellipseIn: eyeRect), with: .color(.white))

            // Iris: Rich Deep Violet Gradient
            let irisW = eyeW * 0.74
            let irisH = eyeH * 0.86
            let irisRect = CGRect(x: ex - irisW * 0.5, y: ey - irisH * 0.5, width: irisW, height: irisH)
            let irisGrad = Gradient(colors: [Color(red: 0.35, green: 0.12, blue: 0.65), Color(red: 0.65, green: 0.28, blue: 0.90), Color(red: 0.25, green: 0.65, blue: 0.95)])
            context.fill(Path(ellipseIn: irisRect), with: .radialGradient(irisGrad, center: CGPoint(x: ex, y: ey), startRadius: 0, endRadius: irisW * 0.6))

            // Pupil: Dark center
            let pupRect = CGRect(x: ex - irisW * 0.26, y: ey - irisH * 0.30, width: irisW * 0.52, height: irisH * 0.60)
            context.fill(Path(ellipseIn: pupRect), with: .color(Color(red: 0.12, green: 0.05, blue: 0.22)))

            // Highlights: Two Sparkling Stars/Circles
            let hl1 = CGRect(x: ex - irisW * 0.28, y: ey - irisH * 0.35, width: irisW * 0.32, height: irisH * 0.32)
            context.fill(Path(ellipseIn: hl1), with: .color(.white))

            let hl2 = CGRect(x: ex + irisW * 0.08, y: ey + irisH * 0.10, width: irisW * 0.16, height: irisH * 0.16)
            context.fill(Path(ellipseIn: hl2), with: .color(.white.opacity(0.85)))

            // Winged Upper Eyelash line
            var lash = Path()
            lash.move(to: CGPoint(x: ex - side * eyeW * 0.55, y: ey - eyeH * 0.15))
            lash.addQuadCurve(to: CGPoint(x: ex + side * eyeW * 0.65, y: ey - eyeH * 0.42), control: CGPoint(x: ex, y: ey - eyeH * 0.60))
            context.stroke(lash, with: .color(Color(red: 0.22, green: 0.08, blue: 0.25)), lineWidth: 3.2)
        }

        // Cute Button Nose
        var nose = Path()
        nose.addArc(center: CGPoint(x: cx, y: headY + headR * 0.28), radius: headR * 0.045, startAngle: .degrees(30), endAngle: .degrees(150), clockwise: false)
        context.stroke(nose, with: .color(skinShadow), lineWidth: 1.8)
    }

    private func drawMakeup(context: inout GraphicsContext, cx: CGFloat, headY: CGFloat, headR: CGFloat) {
        // 1. Eyeshadow
        if let esId = characterState.makeup.eyeshadow {
            let esColor = SpritePalette.eyeshadowColor(esId)
            let eyeW = headR * 0.46
            let eyeH = headR * 0.50
            for side: CGFloat in [-1.0, 1.0] {
                let ex = cx + side * headR * 0.38
                let ey = headY - headR * 0.02
                var es = Path()
                es.move(to: CGPoint(x: ex - side * eyeW * 0.5, y: ey - eyeH * 0.2))
                es.addQuadCurve(to: CGPoint(x: ex + side * eyeW * 0.6, y: ey - eyeH * 0.45), control: CGPoint(x: ex, y: ey - eyeH * 0.72))
                es.closeSubpath()
                context.fill(es, with: .color(esColor.opacity(0.65)))
            }
        }

        // 2. Blush
        let blushColor: Color
        if let bId = characterState.makeup.blush {
            blushColor = SpritePalette.color(hex: bId, fallback: Color(red: 1.0, green: 0.45, blue: 0.65))
        } else {
            blushColor = Color(red: 1.0, green: 0.65, blue: 0.75).opacity(0.45)
        }
        for side: CGFloat in [-1.0, 1.0] {
            let chX = cx + side * headR * 0.58
            let chY = headY + headR * 0.30
            let bRect = CGRect(x: chX - headR * 0.22, y: chY - headR * 0.12, width: headR * 0.44, height: headR * 0.24)
            context.fill(Path(ellipseIn: bRect), with: .color(blushColor.opacity(0.55)))
        }

        // 3. Lipstick (Glossy 3D Lips with Cupid's Bow)
        let lipColor = SpritePalette.lipstickColor(characterState.makeup.lipstick)
        let lipY = headY + headR * 0.52
        let lipW = headR * 0.42
        let lipH = headR * 0.22

        var lip = Path()
        lip.move(to: CGPoint(x: cx - lipW * 0.5, y: lipY))
        lip.addQuadCurve(to: CGPoint(x: cx, y: lipY - lipH * 0.35), control: CGPoint(x: cx - lipW * 0.25, y: lipY - lipH * 0.5))
        lip.addQuadCurve(to: CGPoint(x: cx + lipW * 0.5, y: lipY), control: CGPoint(x: cx + lipW * 0.25, y: lipY - lipH * 0.5))
        lip.addQuadCurve(to: CGPoint(x: cx - lipW * 0.5, y: lipY), control: CGPoint(x: cx, y: lipY + lipH * 0.75))
        lip.closeSubpath()

        context.fill(lip, with: .color(lipColor))

        // Lip gloss highlight
        let glossRect = CGRect(x: cx - lipW * 0.18, y: lipY + lipH * 0.08, width: lipW * 0.36, height: lipH * 0.28)
        context.fill(Path(ellipseIn: glossRect), with: .color(Color.white.opacity(0.55)))

        // 4. Facepaint
        for fp in characterState.facePaints {
            if fp.contains("star") {
                let stX = cx + headR * 0.58
                let stY = headY + headR * 0.22
                context.fill(Path(ellipseIn: CGRect(x: stX - 5, y: stY - 5, width: 10, height: 10)), with: .color(CandyColors.yellow))
            } else if fp.contains("rainbow") {
                var rb = Path()
                rb.move(to: CGPoint(x: cx - headR * 0.75, y: headY + headR * 0.28))
                rb.addLine(to: CGPoint(x: cx - headR * 0.45, y: headY + headR * 0.28))
                context.stroke(rb, with: .color(CandyColors.pink), lineWidth: 3)
            }
        }
    }

    private func drawOutfit(
        context: inout GraphicsContext,
        cx: CGFloat,
        neckY: CGFloat,
        waistY: CGFloat,
        skirtBottomY: CGFloat,
        headR: CGFloat,
        outfitColor: Color
    ) {
        let shoulderW = headR * 1.55
        let waistW = headR * 1.05
        let skirtW = headR * 2.80

        // Bodice
        var bodice = Path()
        bodice.move(to: CGPoint(x: cx - shoulderW * 0.48, y: neckY + headR * 0.32))
        bodice.addLine(to: CGPoint(x: cx + shoulderW * 0.48, y: neckY + headR * 0.32))
        bodice.addQuadCurve(to: CGPoint(x: cx + waistW * 0.5, y: waistY), control: CGPoint(x: cx + shoulderW * 0.45, y: waistY - headR * 0.3))
        bodice.addLine(to: CGPoint(x: cx - waistW * 0.5, y: waistY))
        bodice.addQuadCurve(to: CGPoint(x: cx - shoulderW * 0.48, y: neckY + headR * 0.32), control: CGPoint(x: cx - shoulderW * 0.45, y: waistY - headR * 0.3))
        bodice.closeSubpath()

        context.fill(bodice, with: .color(outfitColor))
        context.stroke(bodice, with: .color(Color.white.opacity(0.8)), lineWidth: 2)

        // Sweetheart center neckline brooch
        let broochRect = CGRect(x: cx - headR * 0.16, y: neckY + headR * 0.30, width: headR * 0.32, height: headR * 0.28)
        context.fill(Path(ellipseIn: broochRect), with: .color(CandyColors.yellow))

        // Flared Skirt (A-Line Princess Skirt)
        var skirt = Path()
        skirt.move(to: CGPoint(x: cx - waistW * 0.52, y: waistY))
        skirt.addLine(to: CGPoint(x: cx + waistW * 0.52, y: waistY))
        skirt.addQuadCurve(to: CGPoint(x: cx + skirtW * 0.5, y: skirtBottomY), control: CGPoint(x: cx + skirtW * 0.42, y: waistY + headR * 0.8))
        skirt.addQuadCurve(to: CGPoint(x: cx - skirtW * 0.5, y: skirtBottomY), control: CGPoint(x: cx, y: skirtBottomY + headR * 0.18))
        skirt.addQuadCurve(to: CGPoint(x: cx - waistW * 0.52, y: waistY), control: CGPoint(x: cx - skirtW * 0.42, y: waistY + headR * 0.8))
        skirt.closeSubpath()

        let skirtGrad = Gradient(colors: [outfitColor, outfitColor.opacity(0.85), Color.white.opacity(0.35)])
        context.fill(skirt, with: .linearGradient(skirtGrad, startPoint: CGPoint(x: cx, y: waistY), endPoint: CGPoint(x: cx, y: skirtBottomY)))
        context.stroke(skirt, with: .color(Color.white.opacity(0.7)), lineWidth: 2)

        // Gold Waist Belt / Ribbon
        let beltRect = CGRect(x: cx - waistW * 0.54, y: waistY - headR * 0.08, width: waistW * 1.08, height: headR * 0.16)
        context.fill(Path(roundedRect: beltRect, cornerRadius: 4), with: .color(CandyColors.yellow))
    }

    private func drawFrontHair(
        context: inout GraphicsContext,
        cx: CGFloat,
        headY: CGFloat,
        headR: CGFloat,
        hairStyleId: String?,
        hairColor: Color
    ) {
        let style = hairStyleId ?? "hair_long_waves"

        // Face-framing side tendrils
        for side: CGFloat in [-1.0, 1.0] {
            var sideLock = Path()
            let sx = cx + side * headR * 0.88
            let sy = headY - headR * 0.45
            sideLock.move(to: CGPoint(x: sx, y: sy))
            sideLock.addQuadCurve(to: CGPoint(x: cx + side * headR * 0.96, y: headY + headR * 0.75), control: CGPoint(x: cx + side * headR * 1.15, y: headY + headR * 0.2))
            sideLock.addQuadCurve(to: CGPoint(x: cx + side * headR * 0.72, y: headY + headR * 0.45), control: CGPoint(x: cx + side * headR * 0.85, y: headY + headR * 0.65))
            sideLock.closeSubpath()

            context.fill(sideLock, with: .color(hairColor))
        }

        // Layered Bangs across Forehead
        var bangs = Path()
        bangs.move(to: CGPoint(x: cx - headR * 0.90, y: headY - headR * 0.55))
        bangs.addQuadCurve(to: CGPoint(x: cx + headR * 0.90, y: headY - headR * 0.55), control: CGPoint(x: cx, y: headY - headR * 0.95))

        if style.contains("ponytail") {
            // Chic Swept Bangs
            bangs.addQuadCurve(to: CGPoint(x: cx + headR * 0.20, y: headY - headR * 0.15), control: CGPoint(x: cx + headR * 0.60, y: headY - headR * 0.25))
            bangs.addQuadCurve(to: CGPoint(x: cx - headR * 0.90, y: headY - headR * 0.55), control: CGPoint(x: cx - headR * 0.35, y: headY - headR * 0.20))
        } else {
            // Soft Curved Curtain Bangs
            bangs.addQuadCurve(to: CGPoint(x: cx, y: headY - headR * 0.28), control: CGPoint(x: cx + headR * 0.50, y: headY - headR * 0.25))
            bangs.addQuadCurve(to: CGPoint(x: cx - headR * 0.90, y: headY - headR * 0.55), control: CGPoint(x: cx - headR * 0.50, y: headY - headR * 0.25))
        }
        bangs.closeSubpath()

        context.fill(bangs, with: .color(hairColor))
        context.stroke(bangs, with: .color(Color.white.opacity(0.4)), lineWidth: 1.5)
    }

    private func drawHorn(context: inout GraphicsContext, cx: CGFloat, headY: CGFloat, headR: CGFloat, hornId: String) {
        let hornH = headR * 1.50
        let hornW = headR * 0.38
        let hornBaseY = headY - headR * 0.75

        var horn = Path()
        horn.move(to: CGPoint(x: cx - hornW * 0.5, y: hornBaseY))
        horn.addLine(to: CGPoint(x: cx, y: hornBaseY - hornH))
        horn.addLine(to: CGPoint(x: cx + hornW * 0.5, y: hornBaseY))
        horn.closeSubpath()

        let hornGrad = hornId.contains("candy")
            ? Gradient(colors: [Color(red: 1.0, green: 0.45, blue: 0.65), Color.white, Color(red: 1.0, green: 0.45, blue: 0.65)])
            : Gradient(colors: [Color(red: 1.0, green: 0.88, blue: 0.20), Color(red: 1.0, green: 0.65, blue: 0.05)])

        context.fill(horn, with: .linearGradient(hornGrad, startPoint: CGPoint(x: cx, y: hornBaseY), endPoint: CGPoint(x: cx, y: hornBaseY - hornH)))
        context.stroke(horn, with: .color(Color.white), lineWidth: 2)

        // Diagonal spiral ridges
        for i in 1...4 {
            let ry = hornBaseY - CGFloat(i) * (hornH * 0.22)
            let rw = hornW * (1.0 - CGFloat(i) * 0.20)
            var ridge = Path()
            ridge.move(to: CGPoint(x: cx - rw * 0.5, y: ry + 4))
            ridge.addLine(to: CGPoint(x: cx + rw * 0.5, y: ry - 4))
            context.stroke(ridge, with: .color(Color.white.opacity(0.85)), lineWidth: 1.8)
        }

        // Sparkle star at tip
        let tip = CGPoint(x: cx, y: hornBaseY - hornH)
        context.fill(Path(ellipseIn: CGRect(x: tip.x - 4, y: tip.y - 4, width: 8, height: 8)), with: .color(CandyColors.yellow))
    }

    private func drawTiara(context: inout GraphicsContext, cx: CGFloat, headY: CGFloat, headR: CGFloat, tiaraId: String) {
        let tiaraW = headR * 1.40
        let tiaraBaseY = headY - headR * 0.60

        var tiara = Path()
        tiara.move(to: CGPoint(x: cx - tiaraW * 0.5, y: tiaraBaseY))
        // 5 filigree peaks
        tiara.addLine(to: CGPoint(x: cx - tiaraW * 0.35, y: tiaraBaseY - headR * 0.35))
        tiara.addLine(to: CGPoint(x: cx - tiaraW * 0.20, y: tiaraBaseY - headR * 0.15))
        tiara.addLine(to: CGPoint(x: cx, y: tiaraBaseY - headR * 0.55)) // Tall center peak
        tiara.addLine(to: CGPoint(x: cx + tiaraW * 0.20, y: tiaraBaseY - headR * 0.15))
        tiara.addLine(to: CGPoint(x: cx + tiaraW * 0.35, y: tiaraBaseY - headR * 0.35))
        tiara.addLine(to: CGPoint(x: cx + tiaraW * 0.5, y: tiaraBaseY))
        tiara.addQuadCurve(to: CGPoint(x: cx - tiaraW * 0.5, y: tiaraBaseY), control: CGPoint(x: cx, y: tiaraBaseY + headR * 0.08))
        tiara.closeSubpath()

        let tiaraGrad = Gradient(colors: [Color(red: 1.0, green: 0.90, blue: 0.30), Color(red: 1.0, green: 0.68, blue: 0.08)])
        context.fill(tiara, with: .linearGradient(tiaraGrad, startPoint: CGPoint(x: cx, y: tiaraBaseY), endPoint: CGPoint(x: cx, y: tiaraBaseY - headR * 0.55)))
        context.stroke(tiara, with: .color(Color.white), lineWidth: 2)

        // Center jewel gem (Pink/Ruby Heart)
        let gemRect = CGRect(x: cx - headR * 0.10, y: tiaraBaseY - headR * 0.28, width: headR * 0.20, height: headR * 0.20)
        context.fill(Path(ellipseIn: gemRect), with: .color(CandyColors.pink))
    }

    private func drawJewelry(context: inout GraphicsContext, cx: CGFloat, headY: CGFloat, neckY: CGFloat, headR: CGFloat, jewelId: String) {
        if jewelId.contains("necklace") || jewelId.contains("pearl") {
            // Elegant contoured pearl necklace
            let nPoints = 9
            let radius = headR * 0.52
            let arcCenter = CGPoint(x: cx, y: neckY - headR * 0.15)
            for i in 0..<nPoints {
                let angle = Double.pi * 0.25 + Double(i) * (Double.pi * 0.50 / Double(nPoints - 1))
                let px = arcCenter.x + CGFloat(cos(angle)) * radius
                let py = arcCenter.y + CGFloat(sin(angle)) * radius
                let pearlRect = CGRect(x: px - 5, y: py - 5, width: 10, height: 10)
                context.fill(Path(ellipseIn: pearlRect), with: .color(Color(red: 0.98, green: 0.98, blue: 1.0)))
                context.stroke(Path(ellipseIn: pearlRect), with: .color(CandyColors.pink.opacity(0.5)), lineWidth: 1)
            }
        } else if jewelId.contains("choker") {
            // Satin choker with golden heart
            let chRect = CGRect(x: cx - headR * 0.24, y: neckY + headR * 0.05, width: headR * 0.48, height: 6)
            context.fill(Path(roundedRect: chRect, cornerRadius: 2), with: .color(Color(red: 0.85, green: 0.12, blue: 0.35)))
            context.fill(Path(ellipseIn: CGRect(x: cx - 4, y: neckY + headR * 0.05, width: 8, height: 8)), with: .color(CandyColors.yellow))
        }

        if jewelId.contains("earring") {
            // Drop earrings hanging from both ear lobes
            for side: CGFloat in [-1.0, 1.0] {
                let ex = cx + side * headR * 0.92
                let ey = headY + headR * 0.20
                context.fill(Path(ellipseIn: CGRect(x: ex - 4, y: ey, width: 8, height: 8)), with: .color(CandyColors.yellow))
                context.fill(Path(ellipseIn: CGRect(x: ex - 6, y: ey + 8, width: 12, height: 12)), with: .color(CandyColors.pink))
            }
        }
    }

    private func drawStickers(context: inout GraphicsContext, cx: CGFloat, headY: CGFloat, waistY: CGFloat, headR: CGFloat, stickers: [String]) {
        let stickerOffsets: [CGPoint] = [
            CGPoint(x: cx + headR * 0.65, y: headY + headR * 0.10), // Right cheek
            CGPoint(x: cx - headR * 0.65, y: headY + headR * 0.10), // Left cheek
            CGPoint(x: cx, y: waistY + headR * 0.65),               // Dress center
            CGPoint(x: cx + headR * 0.85, y: waistY + headR * 0.90), // Dress right
            CGPoint(x: cx - headR * 0.85, y: waistY + headR * 0.90), // Dress left
            CGPoint(x: cx + headR * 0.50, y: headY - headR * 0.50), // Hair accessory
        ]

        for (idx, stId) in stickers.prefix(stickerOffsets.count).enumerated() {
            let pt = stickerOffsets[idx]
            let sRect = CGRect(x: pt.x - 12, y: pt.y - 12, width: 24, height: 24)

            if stId.contains("star") {
                context.fill(Path(ellipseIn: sRect), with: .color(CandyColors.yellow))
                context.stroke(Path(ellipseIn: sRect), with: .color(.white), lineWidth: 2)
            } else if stId.contains("donut") {
                context.fill(Path(ellipseIn: sRect), with: .color(CandyColors.pink))
                context.fill(Path(ellipseIn: sRect.insetBy(dx: 6, dy: 6)), with: .color(.white))
            } else if stId.contains("lollipop") {
                context.fill(Path(ellipseIn: sRect), with: .color(Color(red: 1.0, green: 0.35, blue: 0.55)))
            } else {
                context.fill(Path(ellipseIn: sRect), with: .color(CandyColors.mint))
            }
        }
    }
}

// MARK: - Dedicated Vector Thumbnail Generator (Zero Placeholders, Distinct Artwork)

public struct ItemThumbnailView: View {
    public let item: Item

    public init(item: Item) {
        self.item = item
    }

    public var body: some View {
        let swatch = SpritePalette.color(hex: item.color)

        ZStack {
            switch item.category {
            case "hair_colors":
                if item.id.contains("rainbow") {
                    Circle()
                        .fill(AngularGradient(colors: [.pink, .orange, .yellow, .mint, .blue, .purple, .pink], center: .center))
                        .overlay(Circle().stroke(Color.white, lineWidth: 2))
                } else {
                    Circle()
                        .fill(SpritePalette.hairColor(item.id))
                        .overlay(Circle().stroke(Color.white, lineWidth: 2))
                }
                Image(systemName: "sparkles")
                    .font(.system(size: 13, weight: .bold))
                    .foregroundColor(.white.opacity(0.9))

            case "hair_styles":
                // Full Miniature Vector Preview of the Hairstyle!
                let hairCol = Color(red: 0.85, green: 0.35, blue: 0.65)
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(CandyColors.pastelPink.opacity(0.28))

                    // Miniature head outline
                    Circle()
                        .fill(Color(red: 1.0, green: 0.92, blue: 0.86))
                        .frame(width: 26, height: 26)

                    if item.id.contains("buns") {
                        // Twin Buns with ribbon
                        HStack(spacing: 16) {
                            Circle().fill(hairCol).frame(width: 15, height: 15)
                            Circle().fill(hairCol).frame(width: 15, height: 15)
                        }
                        .offset(y: -10)
                        Capsule().fill(hairCol).frame(width: 22, height: 10).offset(y: -8)
                    } else if item.id.contains("ponytail") {
                        // High Side Ponytail
                        Capsule().fill(hairCol).frame(width: 22, height: 10).offset(y: -8)
                        Capsule().fill(hairCol).frame(width: 12, height: 26).rotationEffect(.degrees(32)).offset(x: 14, y: 0)
                        Circle().fill(CandyColors.yellow).frame(width: 7, height: 7).offset(x: 10, y: -9)
                    } else if item.id.contains("straight") {
                        // Long Silky Straight Locks
                        HStack(spacing: 18) {
                            Capsule().fill(hairCol).frame(width: 9, height: 32)
                            Capsule().fill(hairCol).frame(width: 9, height: 32)
                        }
                        .offset(y: 4)
                        Capsule().fill(hairCol).frame(width: 24, height: 12).offset(y: -8)
                    } else {
                        // Waves
                        HStack(spacing: 16) {
                            RoundedRectangle(cornerRadius: 6).fill(hairCol).frame(width: 11, height: 30)
                            RoundedRectangle(cornerRadius: 6).fill(hairCol).frame(width: 11, height: 30)
                        }
                        .offset(y: 4)
                        Capsule().fill(hairCol).frame(width: 26, height: 12).offset(y: -8)
                    }
                }

            case "outfits":
                let outCol = SpritePalette.outfitColor(item.id)
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(outCol.opacity(0.18))
                    VStack(spacing: 1) {
                        // Miniature Bodice
                        RoundedRectangle(cornerRadius: 3)
                            .fill(outCol)
                            .frame(width: 22, height: 14)
                        // Miniature Skirt
                        Path { p in
                            p.move(to: CGPoint(x: 7, y: 0))
                            p.addLine(to: CGPoint(x: 27, y: 0))
                            p.addLine(to: CGPoint(x: 34, y: 20))
                            p.addLine(to: CGPoint(x: 0, y: 20))
                            p.closeSubpath()
                        }
                        .fill(outCol)
                        .frame(width: 34, height: 20)
                    }
                    Circle()
                        .fill(CandyColors.yellow)
                        .frame(width: 7, height: 7)
                        .offset(y: -4)
                }

            case "wings":
                let isRb = item.id.contains("rainbow")
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(CandyColors.purple.opacity(0.16))
                    HStack(spacing: 2) {
                        Capsule()
                            .fill(isRb ? Color(red: 1.0, green: 0.55, blue: 0.8) : CandyColors.yellow)
                            .frame(width: 14, height: 30)
                            .rotationEffect(.degrees(-32))
                        Capsule()
                            .fill(isRb ? Color(red: 0.45, green: 0.85, blue: 1.0) : CandyColors.yellow)
                            .frame(width: 14, height: 30)
                            .rotationEffect(.degrees(32))
                    }
                }

            case "horns":
                let hornGrad = item.id.contains("candy")
                    ? Gradient(colors: [CandyColors.pink, .white])
                    : Gradient(colors: [CandyColors.yellow, Color(red: 1.0, green: 0.65, blue: 0.0)])
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(CandyColors.yellow.opacity(0.20))
                    Path { p in
                        p.move(to: CGPoint(x: 10, y: 36))
                        p.addLine(to: CGPoint(x: 22, y: 4))
                        p.addLine(to: CGPoint(x: 34, y: 36))
                        p.closeSubpath()
                    }
                    .fill(LinearGradient(hornGrad, startPoint: .bottom, endPoint: .top))
                    .frame(width: 44, height: 40)
                }

            case "tiaras":
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(CandyColors.yellow.opacity(0.18))
                    VStack(spacing: 2) {
                        HStack(spacing: 3) {
                            Capsule().fill(CandyColors.yellow).frame(width: 5, height: 16)
                            Capsule().fill(CandyColors.yellow).frame(width: 6, height: 22)
                            Capsule().fill(CandyColors.yellow).frame(width: 7, height: 28) // Center peak
                            Capsule().fill(CandyColors.yellow).frame(width: 6, height: 22)
                            Capsule().fill(CandyColors.yellow).frame(width: 5, height: 16)
                        }
                        Capsule().fill(CandyColors.yellow).frame(width: 38, height: 5)
                    }
                    Circle().fill(CandyColors.pink).frame(width: 6, height: 6).offset(y: -4)
                }

            case "jewelry":
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(CandyColors.mint.opacity(0.18))
                    if item.id.contains("earring") {
                        HStack(spacing: 12) {
                            VStack(spacing: 2) {
                                Circle().fill(CandyColors.yellow).frame(width: 5, height: 5)
                                Circle().fill(CandyColors.pink).frame(width: 10, height: 10)
                            }
                            VStack(spacing: 2) {
                                Circle().fill(CandyColors.yellow).frame(width: 5, height: 5)
                                Circle().fill(CandyColors.pink).frame(width: 10, height: 10)
                            }
                        }
                    } else {
                        // Pearl Necklace
                        Circle()
                            .stroke(Color.white, lineWidth: 3)
                            .frame(width: 28, height: 28)
                            .overlay(Circle().fill(CandyColors.yellow).frame(width: 7, height: 7).offset(y: 14))
                    }
                }

            case "eyeshadows":
                ZStack {
                    RoundedRectangle(cornerRadius: 10)
                        .fill(Color(white: 0.22))
                        .frame(width: 42, height: 42)
                    HStack(spacing: 3) {
                        RoundedRectangle(cornerRadius: 4)
                            .fill(swatch)
                            .frame(width: 16, height: 32)
                        RoundedRectangle(cornerRadius: 4)
                            .fill(CandyColors.yellow.opacity(0.85))
                            .frame(width: 16, height: 32)
                    }
                }

            case "blushes":
                ZStack {
                    Circle()
                        .fill(Color(white: 0.25))
                        .frame(width: 42, height: 42)
                    Circle()
                        .fill(swatch)
                        .frame(width: 32, height: 32)
                    Image(systemName: "heart.fill")
                        .font(.system(size: 12))
                        .foregroundColor(.white.opacity(0.7))
                }

            case "lipsticks":
                VStack(spacing: 1) {
                    Capsule()
                        .fill(swatch)
                        .frame(width: 12, height: 18)
                    Rectangle()
                        .fill(CandyColors.yellow)
                        .frame(width: 14, height: 6)
                    RoundedRectangle(cornerRadius: 3)
                        .fill(Color(white: 0.2))
                        .frame(width: 16, height: 16)
                }

            case "cake_flavors":
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(swatch.opacity(0.35))
                    Circle()
                        .fill(swatch)
                        .frame(width: 28, height: 28)
                    Image(systemName: "sparkles")
                        .font(.system(size: 14))
                        .foregroundColor(.white)
                }

            case "cake_toppings":
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(CandyColors.pastelPink.opacity(0.28))
                    if item.id.contains("cherries") {
                        HStack(spacing: -3) {
                            Circle().fill(Color(red: 0.88, green: 0.08, blue: 0.20)).frame(width: 16, height: 16)
                            Circle().fill(Color(red: 0.88, green: 0.08, blue: 0.20)).frame(width: 16, height: 16)
                        }
                    } else if item.id.contains("macaron") {
                        VStack(spacing: 1) {
                            Capsule().fill(CandyColors.purple).frame(width: 22, height: 7)
                            Capsule().fill(Color.white).frame(width: 20, height: 3)
                            Capsule().fill(CandyColors.purple).frame(width: 22, height: 7)
                        }
                    } else if item.id.contains("wafer") {
                        Capsule().fill(CandyColors.yellow).frame(width: 8, height: 28).rotationEffect(.degrees(22))
                    } else if item.id.contains("chocochip") || item.id.contains("chocolate") {
                        Circle().fill(Color(red: 0.38, green: 0.22, blue: 0.15)).frame(width: 18, height: 18)
                    } else {
                        Image(systemName: "star.fill")
                            .font(.system(size: 20))
                            .foregroundColor(CandyColors.yellow)
                    }
                }

            default:
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(swatch.opacity(0.28))
                    Circle()
                        .fill(swatch)
                        .frame(width: 22, height: 22)
                }
            }
        }
    }
}
