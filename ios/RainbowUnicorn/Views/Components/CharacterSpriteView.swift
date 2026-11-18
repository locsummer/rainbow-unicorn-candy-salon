import SwiftUI
import SpriteKit
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

    public static func hairUIColor(_ id: String?) -> UIColor {
        guard let id = id else { return UIColor(red: 0.78, green: 0.57, blue: 0.92, alpha: 1.0) }
        if id.contains("rainbow") { return UIColor(red: 1.0, green: 0.42, blue: 0.71, alpha: 1.0) }
        if id.contains("pastel_pink") { return UIColor(red: 1.0, green: 0.71, blue: 0.76, alpha: 1.0) }
        if id.contains("mint_green") { return UIColor(red: 0.55, green: 0.95, blue: 0.65, alpha: 1.0) }
        if id.contains("galaxy_purple") { return UIColor(red: 0.54, green: 0.17, blue: 0.89, alpha: 1.0) }
        if id.contains("lavender") { return UIColor(red: 0.85, green: 0.80, blue: 0.98, alpha: 1.0) }
        if id.contains("neon_lime") { return UIColor(red: 0.35, green: 0.95, blue: 0.15, alpha: 1.0) }
        if id.contains("plum") { return UIColor(red: 0.56, green: 0.27, blue: 0.52, alpha: 1.0) }
        if id.contains("magenta") { return UIColor(red: 1.0, green: 0.0, blue: 0.5, alpha: 1.0) }
        return UIColor(red: 0.78, green: 0.57, blue: 0.92, alpha: 1.0)
    }

    public static func eyeshadowUIColor(_ id: String?) -> UIColor {
        guard let id = id else { return .clear }
        if id.contains("berry_pink") { return UIColor(red: 1.0, green: 0.41, blue: 0.71, alpha: 0.85) }
        if id.contains("turquoise") { return UIColor(red: 0.25, green: 0.88, blue: 0.82, alpha: 0.85) }
        if id.contains("shimmer_blue") { return UIColor(red: 0.53, green: 0.81, blue: 0.92, alpha: 0.85) }
        if id.contains("caramel") { return UIColor(red: 1.0, green: 0.76, blue: 0.03, alpha: 0.85) }
        return UIColor.systemPink.withAlphaComponent(0.75)
    }

    public static func lipstickUIColor(_ id: String?) -> UIColor {
        guard let id = id else { return .clear }
        if id.contains("bubblegum") { return UIColor(red: 1.0, green: 0.08, blue: 0.58, alpha: 1.0) }
        if id.contains("cherry") { return UIColor(red: 0.86, green: 0.08, blue: 0.24, alpha: 1.0) }
        if id.contains("peach") { return UIColor(red: 1.0, green: 0.50, blue: 0.31, alpha: 1.0) }
        if id.contains("crimson") { return UIColor(red: 0.60, green: 0.0, blue: 0.14, alpha: 1.0) }
        return UIColor.systemPink
    }

    public static func outfitUIColor(_ id: String?) -> UIColor {
        guard let id = id else { return .clear }
        if id.contains("cotton_candy") { return UIColor(red: 1.0, green: 0.71, blue: 0.76, alpha: 1.0) }
        if id.contains("pastry_chef") { return UIColor.white }
        if id.contains("mermaid") { return UIColor(red: 0.30, green: 0.82, blue: 0.88, alpha: 1.0) }
        if id.contains("ball_gown") { return UIColor(red: 1.0, green: 0.35, blue: 0.59, alpha: 1.0) }
        if id.contains("carnival") { return UIColor(red: 0.67, green: 0.28, blue: 0.74, alpha: 1.0) }
        if id.contains("crystal") { return UIColor(red: 0.70, green: 0.90, blue: 0.99, alpha: 1.0) }
        if id.contains("peppermint") { return UIColor(red: 0.94, green: 0.33, blue: 0.31, alpha: 1.0) }
        if id.contains("cyber") { return UIColor(red: 0.0, green: 0.90, blue: 0.46, alpha: 1.0) }
        if id.contains("chocolate") { return UIColor(red: 0.43, green: 0.30, blue: 0.25, alpha: 1.0) }
        if id.contains("coronation") { return UIColor(red: 1.0, green: 0.84, blue: 0.31, alpha: 1.0) }
        return UIColor(red: 0.78, green: 0.57, blue: 0.92, alpha: 1.0)
    }
}

public struct CharacterSpriteView: View {
    public let characterState: CharacterState
    public var backdrop: StageBackdrop = .pastel
    public var pose: CharacterPose = .idle
    public var drawBackdrop: Bool = false

    @Environment(\.accessibilityReduceMotion) private var reduceMotion

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
        SpriteView(
            scene: Self.makeScene(
                characterState: characterState,
                backdrop: backdrop,
                pose: pose,
                drawBackdrop: drawBackdrop,
                reduceMotion: reduceMotion
            ),
            options: [.allowsTransparency]
        )
    }

    public static func renderSnapshot(
        characterState: CharacterState,
        size: CGSize = CGSize(width: 720, height: 960),
        backdrop: StageBackdrop = .pastel,
        pose: CharacterPose = .happy
    ) -> UIImage {
        let scene = makeScene(
            characterState: characterState,
            size: size,
            backdrop: backdrop,
            pose: pose,
            drawBackdrop: true,
            reduceMotion: true
        )
        let skView = SKView(frame: CGRect(origin: .zero, size: size))
        if let texture = skView.texture(from: scene) {
            return UIImage(cgImage: texture.cgImage())
        }
        let renderer = UIGraphicsImageRenderer(size: size)
        return renderer.image { ctx in
            let bgColor: UIColor
            switch backdrop {
            case .pastel: bgColor = UIColor(red: 1.0, green: 0.92, blue: 0.96, alpha: 1.0)
            case .disco: bgColor = UIColor(red: 0.19, green: 0.07, blue: 0.28, alpha: 1.0)
            case .forest: bgColor = UIColor(red: 0.90, green: 0.97, blue: 0.91, alpha: 1.0)
            }
            bgColor.setFill()
            ctx.fill(CGRect(origin: .zero, size: size))
            let skinColor = characterState.characterType == .unicorn
                ? UIColor(red: 1.0, green: 0.96, blue: 0.98, alpha: 1.0)
                : UIColor(red: 1.0, green: 0.88, blue: 0.77, alpha: 1.0)
            skinColor.setFill()
            let headR = min(size.width, size.height) * 0.22
            ctx.cgContext.fillEllipse(in: CGRect(x: size.width * 0.5 - headR, y: size.height * 0.26, width: headR * 2, height: headR * 2))
        }
    }

    public static func makeScene(
        characterState: CharacterState,
        size: CGSize = CGSize(width: 540, height: 720),
        backdrop: StageBackdrop = .pastel,
        pose: CharacterPose = .idle,
        drawBackdrop: Bool = false,
        reduceMotion: Bool = false
    ) -> SKScene {
        let scene = SKScene(size: size)
        scene.scaleMode = .aspectFit
        scene.backgroundColor = .clear

        let cx = size.width * 0.5
        let headY = size.height * (pose == .star ? 0.62 : 0.58)
        let headR = min(size.width, size.height) * 0.21
        let neckY = headY - headR * 0.85
        let bodyBottomY = neckY - headR * 1.45

        // LAYER 1: Background
        if drawBackdrop {
            let bg = SKShapeNode(rectOf: size)
            bg.position = CGPoint(x: cx, y: size.height * 0.5)
            bg.strokeColor = .clear
            bg.zPosition = CGFloat(RenderLayer.background.rawValue)
            switch backdrop {
            case .pastel: bg.fillColor = UIColor(red: 1.0, green: 0.92, blue: 0.96, alpha: 1.0)
            case .disco: bg.fillColor = UIColor(red: 0.19, green: 0.07, blue: 0.28, alpha: 1.0)
            case .forest: bg.fillColor = UIColor(red: 0.90, green: 0.97, blue: 0.91, alpha: 1.0)
            }
            scene.addChild(bg)
        }

        // LAYER 2: Back Wings
        if let wId = characterState.wingsId {
            let wColor: UIColor = wId.contains("rainbow")
                ? UIColor(red: 1.0, green: 0.50, blue: 0.67, alpha: 0.88)
                : (wId.contains("starlight") ? UIColor.systemIndigo.withAlphaComponent(0.8) : UIColor(red: 1.0, green: 0.90, blue: 0.50, alpha: 0.88))
            for side in [-1.0, 1.0] {
                let wing = SKShapeNode(ellipseOf: CGSize(width: headR * 1.25, height: headR * 2.1))
                wing.fillColor = wColor
                wing.strokeColor = .white
                wing.lineWidth = 2
                wing.position = CGPoint(x: cx + side * headR * 0.95, y: neckY - headR * 0.4)
                wing.zRotation = side * -0.25
                wing.zPosition = CGFloat(RenderLayer.backWings.rawValue)
                scene.addChild(wing)
            }
        }

        // LAYER 3: Back Hair & Unicorn Tail (animated tail wag respecting Reduce Motion RULE-083)
        let hairCol = SpritePalette.hairUIColor(characterState.hairColorId)
        if characterState.characterType == .unicorn {
            let tail = SKShapeNode(ellipseOf: CGSize(width: headR * 1.1, height: headR * 0.6))
            tail.fillColor = hairCol
            tail.strokeColor = .clear
            tail.position = CGPoint(x: cx + headR * 0.85, y: bodyBottomY + headR * 0.25)
            tail.zRotation = -0.3
            tail.zPosition = CGFloat(RenderLayer.backHair.rawValue)
            if !reduceMotion {
                let wag = SKAction.sequence([
                    SKAction.rotate(toAngle: -0.15, duration: 0.6),
                    SKAction.rotate(toAngle: -0.45, duration: 0.6)
                ])
                tail.run(SKAction.repeatForever(wag))
            }
            scene.addChild(tail)
        }

        let backHair = SKShapeNode(rectOf: CGSize(width: headR * 2.25, height: headR * 2.45), cornerRadius: headR * 0.9)
        backHair.fillColor = hairCol
        backHair.strokeColor = .clear
        backHair.position = CGPoint(x: cx, y: headY - headR * 0.25)
        backHair.zPosition = CGFloat(RenderLayer.backHair.rawValue)
        scene.addChild(backHair)

        // LAYER 4: Body, Ears & Head Skin
        let skinCol = characterState.characterType == .unicorn
            ? UIColor(red: 1.0, green: 0.96, blue: 0.98, alpha: 1.0)
            : UIColor(red: 1.0, green: 0.88, blue: 0.77, alpha: 1.0)
        let outline = UIColor(red: 0.29, green: 0.08, blue: 0.29, alpha: 0.30)

        let torso = SKShapeNode(rectOf: CGSize(width: headR * 1.1, height: headR * 1.45), cornerRadius: headR * 0.35)
        torso.fillColor = skinCol
        torso.strokeColor = outline
        torso.lineWidth = 2
        torso.position = CGPoint(x: cx, y: (neckY + bodyBottomY) * 0.5)
        torso.zPosition = CGFloat(RenderLayer.bodySkin.rawValue)
        scene.addChild(torso)

        if characterState.characterType == .unicorn {
            for side in [-1.0, 1.0] {
                let earPath = CGMutablePath()
                earPath.move(to: CGPoint(x: cx + side * headR * 0.55, y: headY + headR * 0.62))
                earPath.addLine(to: CGPoint(x: cx + side * headR * 0.90, y: headY + headR * 1.32))
                earPath.addLine(to: CGPoint(x: cx + side * headR * 0.24, y: headY + headR * 0.86))
                earPath.closeSubpath()
                let ear = SKShapeNode(path: earPath)
                ear.fillColor = skinCol
                ear.strokeColor = outline
                ear.lineWidth = 2
                ear.zPosition = CGFloat(RenderLayer.bodySkin.rawValue)
                scene.addChild(ear)
            }
        }

        let head = SKShapeNode(circleOfRadius: headR)
        head.fillColor = skinCol
        head.strokeColor = outline
        head.lineWidth = 2
        head.position = CGPoint(x: cx, y: headY)
        head.zPosition = CGFloat(RenderLayer.bodySkin.rawValue)
        scene.addChild(head)

        // LAYER 5: Makeup Clipped Inside Eyelid Mask (RULE-052) + Blush
        let eyeR = headR * 0.21
        let eyePositions = [CGPoint(x: cx - headR * 0.39, y: headY + headR * 0.04), CGPoint(x: cx + headR * 0.39, y: headY + headR * 0.04)]
        if let esId = characterState.makeup.eyeshadow {
            let esColor = SpritePalette.eyeshadowUIColor(esId)
            for ep in eyePositions {
                let maskShape = SKShapeNode(ellipseOf: CGSize(width: eyeR * 2.3, height: eyeR * 1.4))
                maskShape.fillColor = .white
                maskShape.position = CGPoint(x: ep.x, y: ep.y + eyeR * 0.55)

                let cropNode = SKCropNode()
                cropNode.maskNode = maskShape
                cropNode.zPosition = CGFloat(RenderLayer.makeupMasked.rawValue)

                let shadowNode = SKShapeNode(circleOfRadius: eyeR * 1.25)
                shadowNode.fillColor = esColor
                shadowNode.strokeColor = .clear
                shadowNode.position = CGPoint(x: ep.x, y: ep.y + eyeR * 0.55)
                cropNode.addChild(shadowNode)
                scene.addChild(cropNode)
            }
        }

        if characterState.makeup.blush != nil {
            for side in [-1.0, 1.0] {
                let cheek = SKShapeNode(ellipseOf: CGSize(width: headR * 0.34, height: headR * 0.18))
                cheek.fillColor = UIColor(red: 1.0, green: 0.56, blue: 0.67, alpha: 0.65)
                cheek.strokeColor = .clear
                cheek.position = CGPoint(x: cx + side * headR * 0.52, y: headY - headR * 0.22)
                cheek.zPosition = CGFloat(RenderLayer.makeupMasked.rawValue)
                scene.addChild(cheek)
            }
        }

        // LAYER 6: Eyes + Upper Eyelashes (STRICTLY ABOVE Eyeshadow RULE-051) + Lipstick
        for ep in eyePositions {
            let sclera = SKShapeNode(circleOfRadius: eyeR)
            sclera.fillColor = .white
            sclera.strokeColor = UIColor(red: 0.17, green: 0.06, blue: 0.21, alpha: 1.0)
            sclera.lineWidth = 2
            sclera.position = ep
            sclera.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue)
            scene.addChild(sclera)

            let iris = SKShapeNode(circleOfRadius: eyeR * 0.66)
            iris.fillColor = UIColor(red: 0.37, green: 0.17, blue: 0.59, alpha: 1.0)
            iris.strokeColor = .clear
            iris.position = ep
            iris.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue) + 0.1
            scene.addChild(iris)

            let lash = SKShapeNode(rectOf: CGSize(width: eyeR * 1.9, height: 4), cornerRadius: 2)
            lash.fillColor = UIColor(red: 0.17, green: 0.06, blue: 0.21, alpha: 1.0)
            lash.strokeColor = .clear
            lash.position = CGPoint(x: ep.x, y: ep.y + eyeR * 0.92)
            lash.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue) + 0.2
            scene.addChild(lash)

            if !reduceMotion {
                let blink = SKAction.sequence([
                    SKAction.wait(forDuration: 2.8),
                    SKAction.scaleY(to: 0.12, duration: 0.08),
                    SKAction.scaleY(to: 1.0, duration: 0.08)
                ])
                sclera.run(SKAction.repeatForever(blink))
                iris.run(SKAction.repeatForever(blink))
            }
        }

        let mouthY = headY - headR * 0.50
        let mouth = SKShapeNode(ellipseOf: CGSize(width: headR * 0.34, height: headR * 0.14))
        mouth.fillColor = characterState.makeup.lipstick != nil
            ? SpritePalette.lipstickUIColor(characterState.makeup.lipstick)
            : UIColor(red: 0.29, green: 0.08, blue: 0.29, alpha: 0.7)
        mouth.strokeColor = .clear
        mouth.position = CGPoint(x: cx, y: mouthY)
        mouth.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue)
        scene.addChild(mouth)

        // LAYER 7: Outfit
        if let outId = characterState.outfitId {
            let dressPath = CGMutablePath()
            dressPath.move(to: CGPoint(x: cx - headR * 0.45, y: neckY))
            dressPath.addLine(to: CGPoint(x: cx + headR * 0.45, y: neckY))
            dressPath.addLine(to: CGPoint(x: cx + headR * 1.15, y: bodyBottomY))
            dressPath.addLine(to: CGPoint(x: cx - headR * 1.15, y: bodyBottomY))
            dressPath.closeSubpath()
            let outfit = SKShapeNode(path: dressPath)
            outfit.fillColor = SpritePalette.outfitUIColor(outId)
            outfit.strokeColor = outline
            outfit.lineWidth = 2
            outfit.zPosition = CGFloat(RenderLayer.outfit.rawValue)
            scene.addChild(outfit)
        }

        // LAYER 8: Front Hair / Bangs
        let bangs = SKShapeNode(ellipseOf: CGSize(width: headR * 1.95, height: headR * 0.78))
        bangs.fillColor = hairCol
        bangs.strokeColor = .clear
        bangs.position = CGPoint(x: cx, y: headY + headR * 0.74)
        bangs.zPosition = CGFloat(RenderLayer.frontHair.rawValue)
        scene.addChild(bangs)

        // LAYER 9: Accessories (Horn & Jewelry)
        if characterState.hornId != nil {
            let hornPath = CGMutablePath()
            hornPath.move(to: CGPoint(x: cx, y: headY + headR * 1.6))
            hornPath.addLine(to: CGPoint(x: cx + headR * 0.16, y: headY + headR * 0.85))
            hornPath.addLine(to: CGPoint(x: cx - headR * 0.16, y: headY + headR * 0.85))
            hornPath.closeSubpath()
            let horn = SKShapeNode(path: hornPath)
            horn.fillColor = UIColor(red: 1.0, green: 0.86, blue: 0.25, alpha: 1.0)
            horn.strokeColor = outline
            horn.lineWidth = 1.5
            horn.zPosition = CGFloat(RenderLayer.accessories.rawValue)
            scene.addChild(horn)
        }

        if characterState.jewelryId != nil {
            let jewel = SKShapeNode(circleOfRadius: headR * 0.12)
            jewel.fillColor = UIColor(red: 0.0, green: 0.90, blue: 1.0, alpha: 1.0)
            jewel.strokeColor = .white
            jewel.lineWidth = 2
            jewel.position = CGPoint(x: cx, y: neckY - headR * 0.15)
            jewel.zPosition = CGFloat(RenderLayer.accessories.rawValue)
            scene.addChild(jewel)
        }

        // LAYER 10: Tiara
        if characterState.tiaraId != nil {
            let tiaraPath = CGMutablePath()
            tiaraPath.move(to: CGPoint(x: cx - headR * 0.48, y: headY + headR * 0.86))
            tiaraPath.addLine(to: CGPoint(x: cx - headR * 0.25, y: headY + headR * 1.22))
            tiaraPath.addLine(to: CGPoint(x: cx, y: headY + headR * 1.34))
            tiaraPath.addLine(to: CGPoint(x: cx + headR * 0.25, y: headY + headR * 1.22))
            tiaraPath.addLine(to: CGPoint(x: cx + headR * 0.48, y: headY + headR * 0.86))
            tiaraPath.closeSubpath()
            let tiara = SKShapeNode(path: tiaraPath)
            tiara.fillColor = UIColor(red: 1.0, green: 0.84, blue: 0.0, alpha: 1.0)
            tiara.strokeColor = outline
            tiara.lineWidth = 2
            tiara.zPosition = CGFloat(RenderLayer.tiara.rawValue)
            scene.addChild(tiara)
        }

        // LAYER 11: Face Paints & Stickers
        for (idx, _) in characterState.facePaints.enumerated() {
            let dot = SKShapeNode(circleOfRadius: headR * 0.10)
            dot.fillColor = UIColor.systemYellow
            dot.strokeColor = .clear
            let side = idx % 2 == 0 ? 0.56 : -0.56
            dot.position = CGPoint(x: cx + side * headR, y: headY - Double(idx) * 8)
            dot.zPosition = CGFloat(RenderLayer.stickersSparkles.rawValue)
            scene.addChild(dot)
        }

        for (idx, _) in characterState.stickers.prefix(10).enumerated() {
            let star = SKShapeNode(circleOfRadius: headR * 0.12)
            star.fillColor = idx % 2 == 0 ? UIColor.systemPink : UIColor.systemYellow
            star.strokeColor = .white
            star.lineWidth = 2
            let angle = Double(idx) * (2.0 * .pi / 10.0)
            star.position = CGPoint(
                x: cx + cos(angle) * headR * 1.45,
                y: (headY + bodyBottomY) * 0.5 + sin(angle) * headR * 1.6
            )
            star.zPosition = CGFloat(RenderLayer.stickersSparkles.rawValue)
            scene.addChild(star)
        }

        return scene
    }
}

public struct ItemThumbnailView: View {
    public let item: Item

    public init(item: Item) {
        self.item = item
    }

    public var body: some View {
        let swatch = Color(SpritePalette.uiColor(hex: item.color))
        ZStack {
            switch item.category {
            case "hair_colors":
                if item.id.contains("rainbow") {
                    Circle().fill(
                        AngularGradient(colors: [.pink, .orange, .yellow, .mint, .blue, .purple, .pink], center: .center)
                    )
                    .overlay(Circle().stroke(Color.white, lineWidth: 2))
                } else {
                    Circle().fill(Color(SpritePalette.hairUIColor(item.id)))
                        .overlay(Circle().stroke(Color.white, lineWidth: 2))
                }
                Image(systemName: "sparkles")
                    .font(.system(size: 14))
                    .foregroundColor(.white.opacity(0.85))

            case "hair_styles":
                Circle().fill(CandyColors.purple.opacity(0.85))
                Image(systemName: "comb.fill")
                    .font(.system(size: 18))
                    .foregroundColor(.white)

            case "outfits":
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color(SpritePalette.outfitUIColor(item.id)).opacity(0.2))
                    Image(systemName: "tshirt.fill")
                        .font(.system(size: 26))
                        .foregroundColor(Color(SpritePalette.outfitUIColor(item.id)))
                    Image(systemName: "heart.fill")
                        .font(.system(size: 10))
                        .foregroundColor(.white)
                        .offset(y: 2)
                }

            case "wings":
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(CandyColors.pastelPink.opacity(0.3))
                    Image(systemName: "wind")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(CandyColors.purple)
                    Image(systemName: "sparkle")
                        .font(.system(size: 10))
                        .foregroundColor(CandyColors.yellow)
                        .offset(x: 10, y: -10)
                }

            case "horns":
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(CandyColors.yellow.opacity(0.25))
                    Image(systemName: "sparkles")
                        .font(.system(size: 22, weight: .bold))
                        .foregroundColor(CandyColors.yellow)
                }

            case "tiaras":
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(CandyColors.yellow.opacity(0.25))
                    Image(systemName: "crown.fill")
                        .font(.system(size: 24))
                        .foregroundColor(CandyColors.yellow)
                    Circle()
                        .fill(CandyColors.pink)
                        .frame(width: 6, height: 6)
                        .offset(y: -4)
                }

            case "jewelry":
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(CandyColors.pastelPink.opacity(0.25))
                    if item.id.contains("earring") {
                        HStack(spacing: 12) {
                            VStack(spacing: 2) {
                                Circle().fill(CandyColors.yellow).frame(width: 4, height: 4)
                                Image(systemName: "star.fill").font(.system(size: 10)).foregroundColor(CandyColors.yellow)
                            }
                            VStack(spacing: 2) {
                                Circle().fill(CandyColors.yellow).frame(width: 4, height: 4)
                                Image(systemName: "star.fill").font(.system(size: 10)).foregroundColor(CandyColors.yellow)
                            }
                        }
                    } else if item.id.contains("choker") {
                        VStack(spacing: 1) {
                            Capsule().fill(CandyColors.pink).frame(width: 32, height: 6)
                            Image(systemName: "heart.fill").font(.system(size: 10)).foregroundColor(CandyColors.yellow)
                        }
                    } else {
                        // Pearl or Diamond necklace
                        VStack(spacing: 2) {
                            Image(systemName: "circle.grid.cross.fill")
                                .font(.system(size: 14))
                                .foregroundColor(Color(red: 0.5, green: 0.88, blue: 0.95))
                            Image(systemName: "suit.diamond.fill")
                                .font(.system(size: 12))
                                .foregroundColor(CandyColors.pink)
                        }
                    }
                }

            case "stickers":
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(CandyColors.cream)
                    if item.id.contains("lollipop") {
                        Image(systemName: "circle.circle.fill")
                            .font(.system(size: 22))
                            .foregroundColor(CandyColors.pink)
                    } else if item.id.contains("donut") {
                        Image(systemName: "circle.dotted")
                            .font(.system(size: 22, weight: .bold))
                            .foregroundColor(Color(red: 0.6, green: 0.35, blue: 0.2))
                    } else if item.id.contains("wand") {
                        Image(systemName: "wand.and.stars")
                            .font(.system(size: 20))
                            .foregroundColor(CandyColors.yellow)
                    } else {
                        Image(systemName: "star.fill")
                            .font(.system(size: 22))
                            .foregroundColor(CandyColors.yellow)
                    }
                }

            case "cake_toppings":
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(CandyColors.cream)
                    if item.id.contains("cherry") {
                        Image(systemName: "heart.circle.fill")
                            .font(.system(size: 22))
                            .foregroundColor(Color(red: 0.9, green: 0.1, blue: 0.2))
                    } else if item.id.contains("wafer") {
                        Capsule()
                            .fill(CandyColors.yellow)
                            .frame(width: 8, height: 28)
                            .rotationEffect(.degrees(25))
                    } else if item.id.contains("macaron") {
                        Capsule()
                            .fill(CandyColors.pink)
                            .frame(width: 26, height: 16)
                    } else {
                        Image(systemName: "sparkles")
                            .font(.system(size: 22))
                            .foregroundColor(CandyColors.pink)
                    }
                }

            case "cake_frostings":
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(swatch)
                    Image(systemName: "drop.fill")
                        .font(.system(size: 16))
                        .foregroundColor(.white.opacity(0.8))
                }

            case "lipsticks", "eyeshadows", "blushes":
                Circle().fill(swatch)
                    .overlay(Circle().stroke(Color.white, lineWidth: 2))
                    .shadow(color: swatch.opacity(0.5), radius: 3)

            default:
                RoundedRectangle(cornerRadius: 12)
                    .fill(swatch)
                    .overlay(
                        Image(systemName: "sparkle")
                            .font(.system(size: 14))
                            .foregroundColor(.white.opacity(0.8))
                    )
            }
        }
        .frame(width: 52, height: 52)
    }
}
