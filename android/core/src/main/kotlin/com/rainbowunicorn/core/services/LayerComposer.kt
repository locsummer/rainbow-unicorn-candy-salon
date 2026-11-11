package com.rainbowunicorn.core.services

import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.RenderLayer

object LayerComposer {

    const val MAX_FACE_PAINTS = 3
    const val MAX_STICKERS = 10

    fun getRenderingOrder(): List<RenderLayer> = RenderLayer.ordered()

    fun equipOutfit(character: CharacterState, outfitId: String): CharacterState {
        return character.copy(outfitId = outfitId)
    }

    fun equipWings(character: CharacterState, wingsId: String): CharacterState {
        return character.copy(wingsId = wingsId)
    }

    fun equipHorn(character: CharacterState, hornId: String): CharacterState {
        return character.copy(hornId = hornId)
    }

    fun equipTiara(character: CharacterState, tiaraId: String): CharacterState {
        return character.copy(tiaraId = tiaraId)
    }

    fun addFacePaint(character: CharacterState, paintId: String): CharacterState {
        val current = character.facePaints.toMutableList()
        current.remove(paintId) // Remove duplicate if present to move to end
        current.add(paintId)
        // FIFO if exceeds 3 items
        val trimmed = if (current.size > MAX_FACE_PAINTS) {
            current.subList(current.size - MAX_FACE_PAINTS, current.size)
        } else {
            current
        }
        return character.copy(facePaints = trimmed)
    }

    fun addSticker(character: CharacterState, stickerId: String): Pair<CharacterState, Boolean> {
        if (character.stickers.size >= MAX_STICKERS) {
            return Pair(character, false) // Rejected: reached max 10
        }
        val updated = character.stickers + stickerId
        return Pair(character.copy(stickers = updated), true)
    }

    fun applyEyeshadow(character: CharacterState, eyeshadowId: String): CharacterState {
        // Eyeshadow is placed in makeup state, clipped to eyelid mask during rendering
        return character.copy(
            makeup = character.makeup.copy(eyeshadow = eyeshadowId)
        )
    }

    fun toggleFacePaint(character: CharacterState, paintId: String): CharacterState {
        return if (character.facePaints.contains(paintId)) {
            character.copy(facePaints = character.facePaints - paintId)
        } else {
            addFacePaint(character, paintId)
        }
    }

    fun removeLastSticker(character: CharacterState): CharacterState {
        if (character.stickers.isEmpty()) return character
        return character.copy(stickers = character.stickers.dropLast(1))
    }

    fun clearMakeup(character: CharacterState): CharacterState {
        return character.copy(makeup = com.rainbowunicorn.core.models.MakeupState(), facePaints = emptyList())
    }
}
