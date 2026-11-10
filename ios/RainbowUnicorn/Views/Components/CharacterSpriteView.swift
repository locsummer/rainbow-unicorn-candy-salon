import SwiftUI
import SpriteKit
import GameCore

public struct CharacterSpriteView: View {
    public let characterState: CharacterState
    public var isBlinking: Bool = false

    public init(characterState: CharacterState, isBlinking: Bool = false) {
        self.characterState = characterState
        self.isBlinking = isBlinking
    }

    public var body: some View {
        SpriteView(scene: makeScene(), isPaused: false)
    }

    private func makeScene() -> SKScene {
        let scene = SKScene(size: CGSize(width: 600, height: 600))
        scene.backgroundColor = .clear

        let cx = 300.0
        let cy = 280.0
        let bodyRadius = 140.0

        // 11 Layers (RULE-051)

        // Layer 2: Back Wings
        if characterState.wingsId != nil {
            let leftWing = SKShapeNode(ellipseOf: CGSize(width: 140, height: 260))
            leftWing.fillColor = UIColor(red: 1.0, green: 0.9, blue: 0.5, alpha: 0.8)
            leftWing.strokeColor = .clear
            leftWing.position = CGPoint(x: cx - 120, y: cy + 40)
            leftWing.zPosition = CGFloat(RenderLayer.backWings.rawValue)
            scene.addChild(leftWing)

            let rightWing = SKShapeNode(ellipseOf: CGSize(width: 140, height: 260))
            rightWing.fillColor = UIColor(red: 1.0, green: 0.9, blue: 0.5, alpha: 0.8)
            rightWing.strokeColor = .clear
            rightWing.position = CGPoint(x: cx + 120, y: cy + 40)
            rightWing.zPosition = CGFloat(RenderLayer.backWings.rawValue)
            scene.addChild(rightWing)
        }

        // Layer 3: Back Hair
        let backHair = SKShapeNode(rectOf: CGSize(width: 280, height: 320), cornerRadius: 50)
        backHair.fillColor = UIColor(red: 0.78, green: 0.57, blue: 0.92, alpha: 1.0)
        backHair.strokeColor = .clear
        backHair.position = CGPoint(x: cx, y: cy + 20)
        backHair.zPosition = CGFloat(RenderLayer.backHair.rawValue)
        scene.addChild(backHair)

        // Layer 4: Body & Skin
        let body = SKShapeNode(circleOfRadius: CGFloat(bodyRadius))
        body.fillColor = characterState.characterType == .unicorn ? UIColor(red: 1.0, green: 0.98, blue: 0.99, alpha: 1.0) : UIColor(red: 1.0, green: 0.88, blue: 0.77, alpha: 1.0)
        body.strokeColor = UIColor(red: 0.29, green: 0.08, blue: 0.29, alpha: 0.2)
        body.lineWidth = 3
        body.position = CGPoint(x: cx, y: cy)
        body.zPosition = CGFloat(RenderLayer.bodySkin.rawValue)
        scene.addChild(body)

        // Layer 5: Makeup (Clipped by mask RULE-052)
        if characterState.makeup.eyeshadow != nil {
            let leftShadow = SKShapeNode(circleOfRadius: 28)
            leftShadow.fillColor = UIColor(red: 1.0, green: 0.41, blue: 0.71, alpha: 0.6)
            leftShadow.strokeColor = .clear
            leftShadow.position = CGPoint(x: cx - 60, y: cy + 10)
            leftShadow.zPosition = CGFloat(RenderLayer.makeupMasked.rawValue)
            scene.addChild(leftShadow)

            let rightShadow = SKShapeNode(circleOfRadius: 28)
            rightShadow.fillColor = UIColor(red: 1.0, green: 0.41, blue: 0.71, alpha: 0.6)
            rightShadow.strokeColor = .clear
            rightShadow.position = CGPoint(x: cx + 60, y: cy + 10)
            rightShadow.zPosition = CGFloat(RenderLayer.makeupMasked.rawValue)
            scene.addChild(rightShadow)
        }

        // Layer 6: Eyes & Lashes (Always over makeup RULE-051)
        let leftEye = SKShapeNode(circleOfRadius: 22)
        leftEye.fillColor = UIColor(red: 0.29, green: 0.08, blue: 0.29, alpha: 1.0)
        leftEye.strokeColor = .clear
        leftEye.position = CGPoint(x: cx - 60, y: cy)
        leftEye.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue)
        scene.addChild(leftEye)

        let rightEye = SKShapeNode(circleOfRadius: 22)
        rightEye.fillColor = UIColor(red: 0.29, green: 0.08, blue: 0.29, alpha: 1.0)
        rightEye.strokeColor = .clear
        rightEye.position = CGPoint(x: cx + 60, y: cy)
        rightEye.zPosition = CGFloat(RenderLayer.eyesLashes.rawValue)
        scene.addChild(rightEye)

        // Layer 7: Outfit
        if characterState.outfitId != nil {
            let outfit = SKShapeNode(rectOf: CGSize(width: 220, height: 160), cornerRadius: 24)
            outfit.fillColor = UIColor(red: 1.0, green: 0.71, blue: 0.76, alpha: 1.0)
            outfit.strokeColor = .clear
            outfit.position = CGPoint(x: cx, y: cy - 100)
            outfit.zPosition = CGFloat(RenderLayer.outfit.rawValue)
            scene.addChild(outfit)
        }

        // Layer 8: Front Hair / Bangs
        let bangs = SKShapeNode(rectOf: CGSize(width: 220, height: 70), cornerRadius: 20)
        bangs.fillColor = UIColor(red: 0.78, green: 0.57, blue: 0.92, alpha: 1.0)
        bangs.strokeColor = .clear
        bangs.position = CGPoint(x: cx, y: cy + 110)
        bangs.zPosition = CGFloat(RenderLayer.frontHair.rawValue)
        scene.addChild(bangs)

        // Layer 9: Horn / Accessories
        if characterState.hornId != nil {
            let path = CGMutablePath()
            path.move(to: CGPoint(x: cx, y: cy + 220))
            path.addLine(to: CGPoint(x: cx + 25, y: cy + 120))
            path.addLine(to: CGPoint(x: cx - 25, y: cy + 120))
            path.closeSubpath()

            let horn = SKShapeNode(path: path)
            horn.fillColor = UIColor(red: 1.0, green: 0.9, blue: 0.5, alpha: 1.0)
            horn.strokeColor = .clear
            horn.zPosition = CGFloat(RenderLayer.accessories.rawValue)
            scene.addChild(horn)
        }

        // Layer 10: Tiara
        if characterState.tiaraId != nil {
            let tiara = SKShapeNode(rectOf: CGSize(width: 120, height: 40), cornerRadius: 10)
            tiara.fillColor = UIColor(red: 1.0, green: 0.84, blue: 0.0, alpha: 1.0)
            tiara.strokeColor = .clear
            tiara.position = CGPoint(x: cx, y: cy + 130)
            tiara.zPosition = CGFloat(RenderLayer.tiara.rawValue)
            scene.addChild(tiara)
        }

        // Layer 11: Stickers & Sparkles
        for (idx, _) in characterState.stickers.enumerated() {
            let star = SKShapeNode(circleOfRadius: 10)
            star.fillColor = UIColor(red: 1.0, green: 0.9, blue: 0.2, alpha: 1.0)
            star.strokeColor = .clear
            let angle = Double(idx) * (2.0 * .pi / 10.0)
            star.position = CGPoint(x: cx + cos(angle) * 160.0, y: cy + sin(angle) * 160.0)
            star.zPosition = CGFloat(RenderLayer.stickersSparkles.rawValue)
            scene.addChild(star)
        }

        return scene
    }
}
