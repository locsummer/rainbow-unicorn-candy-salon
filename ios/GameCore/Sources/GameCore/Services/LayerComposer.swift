import Foundation

public enum LayerComposer {

    public static let maxFacePaints = 3
    public static let maxStickers = 10

    public static func getRenderingOrder() -> [RenderLayer] {
        return RenderLayer.allCases.sorted()
    }

    public static func equipOutfit(character: CharacterState, outfitId: String) -> CharacterState {
        var copy = character
        copy.outfitId = outfitId
        return copy
    }

    public static func equipWings(character: CharacterState, wingsId: String) -> CharacterState {
        var copy = character
        copy.wingsId = wingsId
        return copy
    }

    public static func equipHorn(character: CharacterState, hornId: String) -> CharacterState {
        var copy = character
        copy.hornId = hornId
        return copy
    }

    public static func equipTiara(character: CharacterState, tiaraId: String) -> CharacterState {
        var copy = character
        copy.tiaraId = tiaraId
        return copy
    }

    public static func addFacePaint(character: CharacterState, paintId: String) -> CharacterState {
        var copy = character
        var list = copy.facePaints.filter { $0 != paintId }
        list.append(paintId)
        if list.count > maxFacePaints {
            list = Array(list.suffix(maxFacePaints))
        }
        copy.facePaints = list
        return copy
    }

    public static func addSticker(character: CharacterState, stickerId: String) -> (CharacterState, Bool) {
        if character.stickers.count >= maxStickers {
            return (character, false)
        }
        var copy = character
        copy.stickers.append(stickerId)
        return (copy, true)
    }

    public static func applyEyeshadow(character: CharacterState, eyeshadowId: String) -> CharacterState {
        var copy = character
        copy.makeup.eyeshadow = eyeshadowId
        return copy
    }
}
