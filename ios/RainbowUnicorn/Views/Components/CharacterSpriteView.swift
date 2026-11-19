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

        // LAYER 6: Eyes + Upper Eyelashes (STRICTLY ABOVE Eyeshadow RULE-051) + Snout + Lipstick
        if characterState.characterType == .unicorn {
            // Cute 3D Pink Snout with Nostrils and Smile
            let snoutY = headY - headR * 0.44
            let snout = SKShapeNode(ellipseOf: CGSize(width: headR * 0.65, height: headR * 0.40))
            snout.fillColor = UIColor(red: 1.0, green: 0.90, blue: 0.94, alpha: 1.0)
            snout.strokeColor = UIColor(red: 1.0, green: 0.70, blue: 0.82, alpha: 0.5)
            snout.lineWidth = 1.5
            snout.position = CGPoint(x: cx, y: snoutY)
            snout.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue) - 0.05
            scene.addChild(snout)

            // Nostrils
            for side in [-1.0, 1.0] {
                let nostril = SKShapeNode(ellipseOf: CGSize(width: headR * 0.06, height: headR * 0.04))
                nostril.fillColor = UIColor(red: 0.95, green: 0.50, blue: 0.65, alpha: 0.8)
                nostril.strokeColor = .clear
                nostril.position = CGPoint(x: cx + side * headR * 0.12, y: snoutY + headR * 0.04)
                nostril.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue)
                scene.addChild(nostril)
            }
        }

        for (i, ep) in eyePositions.enumerated() {
            let sideSign = (i == 0) ? -1.0 : 1.0
            let sclera = SKShapeNode(circleOfRadius: eyeR)
            sclera.fillColor = .white
            sclera.strokeColor = UIColor(red: 0.17, green: 0.06, blue: 0.21, alpha: 1.0)
            sclera.lineWidth = 2
            sclera.position = ep
            sclera.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue)
            scene.addChild(sclera)

            // Outer Iris (Deep royal violet)
            let iris = SKShapeNode(circleOfRadius: eyeR * 0.72)
            iris.fillColor = UIColor(red: 0.29, green: 0.08, blue: 0.55, alpha: 1.0)
            iris.strokeColor = .clear
            iris.position = ep
            iris.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue) + 0.1
            scene.addChild(iris)

            // Inner Iris Highlight (Aquamarine / Orchid shimmer)
            let irisShimmer = SKShapeNode(circleOfRadius: eyeR * 0.52)
            irisShimmer.fillColor = UIColor(red: 0.0, green: 0.88, blue: 0.95, alpha: 0.9)
            irisShimmer.strokeColor = .clear
            irisShimmer.position = CGPoint(x: ep.x, y: ep.y - eyeR * 0.10)
            irisShimmer.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue) + 0.12
            scene.addChild(irisShimmer)

            // Dark Midnight Pupil
            let pupil = SKShapeNode(circleOfRadius: eyeR * 0.40)
            pupil.fillColor = UIColor(red: 0.09, green: 0.01, blue: 0.15, alpha: 1.0)
            pupil.strokeColor = .clear
            pupil.position = ep
            pupil.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue) + 0.15
            scene.addChild(pupil)

            // Sparkle catchlights (Large highlight + bottom crescent + glint)
            let catch1 = SKShapeNode(circleOfRadius: eyeR * 0.26)
            catch1.fillColor = .white
            catch1.strokeColor = .clear
            catch1.position = CGPoint(x: ep.x - eyeR * 0.22, y: ep.y + eyeR * 0.22)
            catch1.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue) + 0.18
            scene.addChild(catch1)

            let catch2 = SKShapeNode(circleOfRadius: eyeR * 0.12)
            catch2.fillColor = .white
            catch2.strokeColor = .clear
            catch2.position = CGPoint(x: ep.x + eyeR * 0.24, y: ep.y - eyeR * 0.20)
            catch2.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue) + 0.18
            scene.addChild(catch2)

            // Winged upper lash with flutter flicks
            let lashPath = CGMutablePath()
            lashPath.move(to: CGPoint(x: ep.x - sideSign * eyeR * 1.1, y: ep.y + eyeR * 0.2))
            lashPath.addQuadCurve(to: CGPoint(x: ep.x + sideSign * eyeR * 1.25, y: ep.y + eyeR * 0.6), control: CGPoint(x: ep.x, y: ep.y + eyeR * 1.15))
            lashPath.addLine(to: CGPoint(x: ep.x + sideSign * eyeR * 1.5, y: ep.y + eyeR * 0.9))

            let lash = SKShapeNode(path: lashPath)
            lash.strokeColor = UIColor(red: 0.17, green: 0.06, blue: 0.21, alpha: 1.0)
            lash.lineWidth = 3.5
            lash.lineCap = .round
            lash.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue) + 0.22
            scene.addChild(lash)

            // Double eyelid crease
            let creasePath = CGMutablePath()
            creasePath.move(to: CGPoint(x: ep.x - sideSign * eyeR * 0.85, y: ep.y + eyeR * 1.25))
            creasePath.addQuadCurve(to: CGPoint(x: ep.x + sideSign * eyeR * 0.85, y: ep.y + eyeR * 1.35), control: CGPoint(x: ep.x, y: ep.y + eyeR * 1.55))
            let crease = SKShapeNode(path: creasePath)
            crease.strokeColor = UIColor(red: 0.55, green: 0.25, blue: 0.45, alpha: 0.4)
            crease.lineWidth = 1.5
            crease.lineCap = .round
            crease.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue) + 0.2
            scene.addChild(crease)

            if !reduceMotion {
                let blink = SKAction.sequence([
                    SKAction.wait(forDuration: 2.8),
                    SKAction.scaleY(to: 0.12, duration: 0.08),
                    SKAction.scaleY(to: 1.0, duration: 0.08)
                ])
                sclera.run(SKAction.repeatForever(blink))
                iris.run(SKAction.repeatForever(blink))
                irisShimmer.run(SKAction.repeatForever(blink))
                pupil.run(SKAction.repeatForever(blink))
                catch1.run(SKAction.repeatForever(blink))
                catch2.run(SKAction.repeatForever(blink))
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

        // LAYER 7: Outfit (Bodice & Skirt that snugly hugs shoulders, bust, waist & hips)
        if let outId = characterState.outfitId {
            let outColor = SpritePalette.outfitUIColor(outId)
            let waistY = neckY - headR * 0.58
            let waistW = headR * 0.48
            let shoulderW = headR * 0.88
            let skirtW: CGFloat = outId.contains("ball_gown") || outId.contains("coronation") ? headR * 1.55 : headR * 1.35

            // 1. Bodice wrapping shoulders, bust, and torso
            let bodicePath = CGMutablePath()
            bodicePath.move(to: CGPoint(x: cx - shoulderW, y: neckY - headR * 0.18))
            bodicePath.addQuadCurve(to: CGPoint(x: cx, y: neckY - headR * 0.26), control: CGPoint(x: cx - headR * 0.35, y: neckY - headR * 0.08))
            bodicePath.addQuadCurve(to: CGPoint(x: cx + shoulderW, y: neckY - headR * 0.18), control: CGPoint(x: cx + headR * 0.35, y: neckY - headR * 0.08))
            bodicePath.addQuadCurve(to: CGPoint(x: cx + waistW, y: waistY), control: CGPoint(x: cx + shoulderW * 0.85, y: neckY - headR * 0.40))
            bodicePath.addLine(to: CGPoint(x: cx - waistW, y: waistY))
            bodicePath.addQuadCurve(to: CGPoint(x: cx - shoulderW, y: neckY - headR * 0.18), control: CGPoint(x: cx - shoulderW * 0.85, y: neckY - headR * 0.40))
            bodicePath.closeSubpath()

            let bodice = SKShapeNode(path: bodicePath)
            bodice.fillColor = outColor
            bodice.strokeColor = outline
            bodice.lineWidth = 2
            bodice.zPosition = CGFloat(RenderLayer.outfit.rawValue)
            scene.addChild(bodice)

            // Cap sleeve ruffles on both shoulders
            for side in [-1.0, 1.0] {
                let sleeve = SKShapeNode(ellipseOf: CGSize(width: headR * 0.42, height: headR * 0.28))
                sleeve.fillColor = UIColor.white.withAlphaComponent(0.65)
                sleeve.strokeColor = UIColor.white
                sleeve.lineWidth = 1.5
                sleeve.position = CGPoint(x: cx + side * shoulderW * 0.95, y: neckY - headR * 0.14)
                sleeve.zRotation = side * 0.35
                sleeve.zPosition = CGFloat(RenderLayer.outfit.rawValue) + 0.05
                scene.addChild(sleeve)
            }

            // Candy brooch on bust
            let brooch = SKShapeNode(circleOfRadius: headR * 0.10)
            brooch.fillColor = UIColor(red: 1.0, green: 0.25, blue: 0.50, alpha: 1.0)
            brooch.strokeColor = .white
            brooch.lineWidth = 1.5
            brooch.position = CGPoint(x: cx, y: neckY - headR * 0.32)
            brooch.zPosition = CGFloat(RenderLayer.outfit.rawValue) + 0.1
            scene.addChild(brooch)

            // 2. Skirt flaring out from waist
            let skirtPath = CGMutablePath()
            skirtPath.move(to: CGPoint(x: cx - waistW, y: waistY))
            skirtPath.addLine(to: CGPoint(x: cx + waistW, y: waistY))
            skirtPath.addQuadCurve(to: CGPoint(x: cx + skirtW, y: bodyBottomY), control: CGPoint(x: cx + skirtW * 0.85, y: waistY - headR * 0.45))
            skirtPath.addQuadCurve(to: CGPoint(x: cx - skirtW, y: bodyBottomY), control: CGPoint(x: cx, y: bodyBottomY - headR * 0.18))
            skirtPath.addQuadCurve(to: CGPoint(x: cx - waistW, y: waistY), control: CGPoint(x: cx - skirtW * 0.85, y: waistY - headR * 0.45))
            skirtPath.closeSubpath()

            let skirt = SKShapeNode(path: skirtPath)
            skirt.fillColor = outColor
            skirt.strokeColor = outline
            skirt.lineWidth = 2
            skirt.zPosition = CGFloat(RenderLayer.outfit.rawValue)
            scene.addChild(skirt)

            // Cyan scalloped lace hem peeking underneath
            let hem = SKShapeNode(ellipseOf: CGSize(width: skirtW * 2.05, height: headR * 0.22))
            hem.fillColor = UIColor(red: 0.0, green: 0.90, blue: 1.0, alpha: 0.9)
            hem.strokeColor = .white
            hem.lineWidth = 1.5
            hem.position = CGPoint(x: cx, y: bodyBottomY)
            hem.zPosition = CGFloat(RenderLayer.outfit.rawValue) - 0.02
            scene.addChild(hem)

            // 3. Waist sash
            let sash = SKShapeNode(rectOf: CGSize(width: waistW * 2.1, height: headR * 0.12), cornerRadius: 4)
            sash.fillColor = UIColor(red: 1.0, green: 0.25, blue: 0.50, alpha: 1.0)
            sash.strokeColor = .white
            sash.lineWidth = 1.2
            sash.position = CGPoint(x: cx, y: waistY)
            sash.zPosition = CGFloat(RenderLayer.outfit.rawValue) + 0.12
            scene.addChild(sash)

            let buckle = SKShapeNode(circleOfRadius: headR * 0.08)
            buckle.fillColor = UIColor(red: 1.0, green: 0.84, blue: 0.0, alpha: 1.0)
            buckle.strokeColor = .white
            buckle.lineWidth = 1.2
            buckle.position = CGPoint(x: cx, y: waistY)
            buckle.zPosition = CGFloat(RenderLayer.outfit.rawValue) + 0.15
            scene.addChild(buckle)
        }

        // LAYER 8: Front Hair / Bangs (Face-framing side tendrils hugging cheeks + soft layered bangs)
        for side in [-1.0, 1.0] {
            let cheekLockPath = CGMutablePath()
            cheekLockPath.move(to: CGPoint(x: cx + side * headR * 0.65, y: headY + headR * 0.35))
            cheekLockPath.addQuadCurve(to: CGPoint(x: cx + side * headR * 0.85, y: headY - headR * 0.25), control: CGPoint(x: cx + side * headR * 1.05, y: headY + headR * 0.05))
            cheekLockPath.addQuadCurve(to: CGPoint(x: cx + side * headR * 0.55, y: headY + headR * 0.10), control: CGPoint(x: cx + side * headR * 0.75, y: headY - headR * 0.10))
            cheekLockPath.closeSubpath()

            let cheekLock = SKShapeNode(path: cheekLockPath)
            cheekLock.fillColor = hairCol
            cheekLock.strokeColor = .clear
            cheekLock.zPosition = CGFloat(RenderLayer.frontHair.rawValue)
            scene.addChild(cheekLock)
        }

        let bangsPath = CGMutablePath()
        bangsPath.move(to: CGPoint(x: cx - headR * 0.95, y: headY + headR * 0.30))
        bangsPath.addQuadCurve(to: CGPoint(x: cx + headR * 0.95, y: headY + headR * 0.30), control: CGPoint(x: cx, y: headY + headR * 0.90))
        bangsPath.addQuadCurve(to: CGPoint(x: cx, y: headY + headR * 0.35), control: CGPoint(x: cx + headR * 0.45, y: headY + headR * 0.22))
        bangsPath.addQuadCurve(to: CGPoint(x: cx - headR * 0.95, y: headY + headR * 0.30), control: CGPoint(x: cx - headR * 0.45, y: headY + headR * 0.22))
        bangsPath.closeSubpath()

        let bangs = SKShapeNode(path: bangsPath)
        bangs.fillColor = hairCol
        bangs.strokeColor = .clear
        bangs.zPosition = CGFloat(RenderLayer.frontHair.rawValue)
        scene.addChild(bangs)

        // LAYER 9: Accessories (Horn & Jewelry)
        if characterState.hornId != nil {
            let hornForeheadY = headY + headR * 0.52
            let hornTipY = headY + headR * 1.35
            let hornHalfW = headR * 0.16

            // Horn base circlet hugging forehead
            let baseBand = SKShapeNode(rectOf: CGSize(width: hornHalfW * 2.8, height: headR * 0.10), cornerRadius: 4)
            baseBand.fillColor = UIColor(red: 1.0, green: 0.84, blue: 0.0, alpha: 1.0)
            baseBand.strokeColor = .white
            baseBand.lineWidth = 1.2
            baseBand.position = CGPoint(x: cx, y: hornForeheadY)
            baseBand.zPosition = CGFloat(RenderLayer.accessories.rawValue) + 0.05
            scene.addChild(baseBand)

            let hornPath = CGMutablePath()
            hornPath.move(to: CGPoint(x: cx, y: hornTipY))
            hornPath.addLine(to: CGPoint(x: cx + hornHalfW, y: hornForeheadY))
            hornPath.addLine(to: CGPoint(x: cx - hornHalfW, y: hornForeheadY))
            hornPath.closeSubpath()

            let horn = SKShapeNode(path: hornPath)
            horn.fillColor = UIColor(red: 1.0, green: 0.86, blue: 0.25, alpha: 1.0)
            horn.strokeColor = outline
            horn.lineWidth = 1.5
            horn.zPosition = CGFloat(RenderLayer.accessories.rawValue)
            scene.addChild(horn)

            // Star crest at horn tip
            let starTip = SKShapeNode(circleOfRadius: headR * 0.08)
            starTip.fillColor = .white
            starTip.strokeColor = UIColor(red: 1.0, green: 0.84, blue: 0.0, alpha: 1.0)
            starTip.lineWidth = 2
            starTip.position = CGPoint(x: cx, y: hornTipY)
            starTip.zPosition = CGFloat(RenderLayer.accessories.rawValue) + 0.1
            scene.addChild(starTip)
        }

        if let jewId = characterState.jewelryId {
            if jewId.contains("earrings") {
                // Anchored to earlobes / cheek roots
                for side in [-1.0, 1.0] {
                    let ex = cx + side * headR * 0.74
                    let ey = headY - headR * 0.10

                    let stud = SKShapeNode(circleOfRadius: headR * 0.065)
                    stud.fillColor = UIColor(red: 1.0, green: 0.84, blue: 0.0, alpha: 1.0)
                    stud.strokeColor = .white
                    stud.lineWidth = 1.2
                    stud.position = CGPoint(x: ex, y: ey)
                    stud.zPosition = CGFloat(RenderLayer.accessories.rawValue) + 0.1
                    scene.addChild(stud)

                    let drop = SKShapeNode(circleOfRadius: headR * 0.08)
                    drop.fillColor = UIColor(red: 1.0, green: 0.25, blue: 0.50, alpha: 1.0)
                    drop.strokeColor = .white
                    drop.lineWidth = 1.5
                    drop.position = CGPoint(x: ex, y: ey - headR * 0.18)
                    drop.zPosition = CGFloat(RenderLayer.accessories.rawValue) + 0.1
                    scene.addChild(drop)
                }
            } else if jewId.contains("choker") {
                // Curved choker hugging the neck band
                let chokerPath = CGMutablePath()
                chokerPath.move(to: CGPoint(x: cx - headR * 0.28, y: neckY - headR * 0.05))
                chokerPath.addQuadCurve(to: CGPoint(x: cx + headR * 0.28, y: neckY - headR * 0.05), control: CGPoint(x: cx, y: neckY - headR * 0.14))
                let choker = SKShapeNode(path: chokerPath)
                choker.strokeColor = UIColor(red: 0.91, green: 0.12, blue: 0.39, alpha: 1.0)
                choker.lineWidth = 6
                choker.lineCap = .round
                choker.zPosition = CGFloat(RenderLayer.accessories.rawValue)
                scene.addChild(choker)

                let locket = SKShapeNode(circleOfRadius: headR * 0.08)
                locket.fillColor = UIColor(red: 1.0, green: 0.84, blue: 0.0, alpha: 1.0)
                locket.strokeColor = .white
                locket.lineWidth = 1.5
                locket.position = CGPoint(x: cx, y: neckY - headR * 0.12)
                locket.zPosition = CGFloat(RenderLayer.accessories.rawValue) + 0.05
                scene.addChild(locket)
            } else {
                // Pearl necklace draped along clavicle
                let pearlPath = CGMutablePath()
                pearlPath.move(to: CGPoint(x: cx - headR * 0.34, y: neckY - headR * 0.08))
                pearlPath.addQuadCurve(to: CGPoint(x: cx + headR * 0.34, y: neckY - headR * 0.08), control: CGPoint(x: cx, y: neckY - headR * 0.28))
                let necklace = SKShapeNode(path: pearlPath)
                necklace.strokeColor = UIColor(red: 1.0, green: 0.98, blue: 0.88, alpha: 1.0)
                necklace.lineWidth = 5
                necklace.lineCap = .round
                necklace.zPosition = CGFloat(RenderLayer.accessories.rawValue)
                scene.addChild(necklace)

                let jewel = SKShapeNode(circleOfRadius: headR * 0.09)
                jewel.fillColor = UIColor(red: 0.0, green: 0.90, blue: 1.0, alpha: 1.0)
                jewel.strokeColor = .white
                jewel.lineWidth = 1.5
                jewel.position = CGPoint(x: cx, y: neckY - headR * 0.24)
                jewel.zPosition = CGFloat(RenderLayer.accessories.rawValue) + 0.05
                scene.addChild(jewel)
            }
        }

        // LAYER 10: Tiara (Anchored to forehead hairline with 5 jeweled peaks & golden circlet)
        if characterState.tiaraId != nil {
            let tiaraBaseY = headY + headR * 0.50
            let tiaraW = headR * 0.60

            let tiaraPath = CGMutablePath()
            tiaraPath.move(to: CGPoint(x: cx - tiaraW, y: tiaraBaseY))
            tiaraPath.addLine(to: CGPoint(x: cx - headR * 0.44, y: tiaraBaseY + headR * 0.26))
            tiaraPath.addLine(to: CGPoint(x: cx - headR * 0.26, y: tiaraBaseY + headR * 0.08))
            tiaraPath.addLine(to: CGPoint(x: cx, y: tiaraBaseY + headR * 0.40))
            tiaraPath.addLine(to: CGPoint(x: cx + headR * 0.26, y: tiaraBaseY + headR * 0.08))
            tiaraPath.addLine(to: CGPoint(x: cx + headR * 0.44, y: tiaraBaseY + headR * 0.26))
            tiaraPath.addLine(to: CGPoint(x: cx + tiaraW, y: tiaraBaseY))
            tiaraPath.addQuadCurve(to: CGPoint(x: cx - tiaraW, y: tiaraBaseY), control: CGPoint(x: cx, y: tiaraBaseY + headR * 0.06))
            tiaraPath.closeSubpath()

            let tiara = SKShapeNode(path: tiaraPath)
            tiara.fillColor = UIColor(red: 1.0, green: 0.84, blue: 0.0, alpha: 1.0)
            tiara.strokeColor = outline
            tiara.lineWidth = 2
            tiara.zPosition = CGFloat(RenderLayer.tiara.rawValue)
            scene.addChild(tiara)

            // Golden circlet band with end clips
            let bandPath = CGMutablePath()
            bandPath.move(to: CGPoint(x: cx - tiaraW * 1.05, y: tiaraBaseY))
            bandPath.addQuadCurve(to: CGPoint(x: cx + tiaraW * 1.05, y: tiaraBaseY), control: CGPoint(x: cx, y: tiaraBaseY + headR * 0.06))
            let circlet = SKShapeNode(path: bandPath)
            circlet.strokeColor = UIColor(red: 1.0, green: 0.75, blue: 0.0, alpha: 1.0)
            circlet.lineWidth = 4
            circlet.zPosition = CGFloat(RenderLayer.tiara.rawValue) + 0.05
            scene.addChild(circlet)

            // Central ruby gem
            let gem = SKShapeNode(circleOfRadius: headR * 0.09)
            gem.fillColor = UIColor(red: 1.0, green: 0.20, blue: 0.45, alpha: 1.0)
            gem.strokeColor = .white
            gem.lineWidth = 1.5
            gem.position = CGPoint(x: cx, y: tiaraBaseY + headR * 0.25)
            gem.zPosition = CGFloat(RenderLayer.tiara.rawValue) + 0.1
            scene.addChild(gem)
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
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(CandyColors.pastelPink.opacity(0.35))
                    if item.id.contains("buns") {
                        HStack(spacing: 14) {
                            Circle().fill(CandyColors.purple).frame(width: 16, height: 16)
                            Circle().fill(CandyColors.purple).frame(width: 16, height: 16)
                        }
                    } else if item.id.contains("ponytail") {
                        VStack(spacing: 2) {
                            Circle().fill(CandyColors.purple).frame(width: 18, height: 18)
                            Capsule().fill(CandyColors.purple).frame(width: 8, height: 20).rotationEffect(.degrees(25))
                        }
                    } else if item.id.contains("waves") {
                        Image(systemName: "wind")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(CandyColors.purple)
                    } else {
                        Image(systemName: "comb.fill")
                            .font(.system(size: 22))
                            .foregroundColor(CandyColors.purple)
                    }
                }

            case "eyeshadows":
                ZStack {
                    RoundedRectangle(cornerRadius: 8)
                        .fill(LinearGradient(colors: [CandyColors.yellow, Color(red: 1.0, green: 0.7, blue: 0.0)], startPoint: .top, endPoint: .bottom))
                        .frame(width: 48, height: 44)
                    RoundedRectangle(cornerRadius: 6)
                        .fill(Color(white: 0.15))
                        .frame(width: 44, height: 40)
                    VStack(spacing: 3) {
                        RoundedRectangle(cornerRadius: 3)
                            .fill(LinearGradient(colors: [Color(white: 0.95), Color(white: 0.8)], startPoint: .top, endPoint: .bottom))
                            .frame(width: 38, height: 10)
                        HStack(spacing: 4) {
                            RoundedRectangle(cornerRadius: 3)
                                .fill(swatch)
                                .frame(width: 17, height: 16)
                                .overlay(Image(systemName: "sparkle").font(.system(size: 8)).foregroundColor(.white.opacity(0.8)))
                            RoundedRectangle(cornerRadius: 3)
                                .fill(item.id.contains("blue") || item.id.contains("turquoise") ? Color(red: 0.5, green: 0.88, blue: 0.95) : CandyColors.yellow.opacity(0.85))
                                .frame(width: 17, height: 16)
                        }
                        Capsule()
                            .fill(Color(white: 0.35))
                            .frame(width: 36, height: 3)
                    }
                }

            case "blushes":
                ZStack {
                    Circle()
                        .fill(LinearGradient(colors: [Color(red: 1.0, green: 0.85, blue: 0.7), Color(red: 0.9, green: 0.6, blue: 0.4)], startPoint: .topLeading, endPoint: .bottomTrailing))
                        .frame(width: 46, height: 46)
                    Circle()
                        .fill(swatch)
                        .frame(width: 38, height: 38)
                    Image(systemName: "heart.fill")
                        .font(.system(size: 14))
                        .foregroundColor(.white.opacity(0.45))
                    HStack(spacing: 1) {
                        Capsule().fill(Color(white: 0.15)).frame(width: 14, height: 4)
                        Capsule().fill(CandyColors.yellow).frame(width: 5, height: 5)
                        Circle().fill(CandyColors.pink).frame(width: 9, height: 9)
                    }
                    .rotationEffect(.degrees(-35))
                    .offset(x: 10, y: 10)
                }

            case "lipsticks":
                VStack(spacing: 0) {
                    Capsule()
                        .fill(swatch)
                        .frame(width: 14, height: 20)
                        .overlay(
                            Capsule()
                                .stroke(Color.white.opacity(0.6), lineWidth: 1)
                                .offset(x: -2)
                        )
                    Rectangle()
                        .fill(LinearGradient(colors: [CandyColors.yellow, Color(red: 1.0, green: 0.75, blue: 0.0)], startPoint: .leading, endPoint: .trailing))
                        .frame(width: 16, height: 7)
                    RoundedRectangle(cornerRadius: 4)
                        .fill(LinearGradient(colors: [Color(white: 0.25), Color(white: 0.1)], startPoint: .leading, endPoint: .trailing))
                        .frame(width: 20, height: 20)
                }

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
