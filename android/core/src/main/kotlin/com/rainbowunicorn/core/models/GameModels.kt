package com.rainbowunicorn.core.models

enum class CharacterType(val value: String) {
    UNICORN("unicorn"),
    PRINCESS("princess");

    companion object {
        fun fromValue(value: String): CharacterType =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: UNICORN
    }
}

enum class StationType(val value: String) {
    SPA("spa"),
    HAIR("hair"),
    MAKEUP("makeup"),
    DRESSUP("dressup"),
    CAKE("cake"),
    PET("pet"),
    PHOTO("photo");

    companion object {
        fun fromValue(value: String): StationType =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: SPA
    }
}

enum class StationStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}

enum class SlotType(val value: String) {
    EXCLUSIVE("exclusive"),
    MULTI("multi");

    companion object {
        fun fromValue(value: String): SlotType =
            entries.find { it.value.equals(value, ignoreCase = true) } ?: EXCLUSIVE
    }
}

data class Criterion(
    val id: String,
    val station: String,
    val type: String,
    val target: Any?,
    val description: String
)

data class Level(
    val id: Int,
    val name: String,
    val theme: String,
    val characterType: CharacterType,
    val requiresPremium: Boolean,
    val stations: List<StationType>,
    val criteria: List<Criterion>,
    val baseCandyReward: Int = 10
)

data class Item(
    val id: String,
    val name: String,
    val category: String,
    val unlockedByDefault: Boolean,
    val candyPrice: Int,
    val assetId: String,
    val color: String? = null,
    val layer: Int = 0
)

data class ItemCategory(
    val id: String,
    val name: String,
    val slotType: SlotType,
    val maxSelected: Int
)

data class MakeupState(
    val eyeshadow: String? = null,
    val lipstick: String? = null,
    val blush: String? = null
)

data class CharacterState(
    val characterType: CharacterType = CharacterType.UNICORN,
    val skinTone: String = "default",
    val eyeColor: String = "default",
    val hairStyleId: String? = null,
    val hairColorId: String? = null,
    val makeup: MakeupState = MakeupState(),
    val outfitId: String? = null,
    val wingsId: String? = null,
    val hornId: String? = null,
    val tiaraId: String? = null,
    val jewelryId: String? = null,
    val facePaints: List<String> = emptyList(),
    val stickers: List<String> = emptyList()
) {
    fun resetCurrentStation(stationType: StationType): CharacterState {
        return when (stationType) {
            StationType.SPA -> this // Spa reset does not wipe clothes
            StationType.HAIR -> copy(hairStyleId = null, hairColorId = null)
            StationType.MAKEUP -> copy(makeup = MakeupState(), facePaints = emptyList())
            StationType.DRESSUP -> copy(
                outfitId = null,
                wingsId = null,
                hornId = null,
                tiaraId = null,
                jewelryId = null,
                stickers = emptyList()
            )
            StationType.CAKE, StationType.PET, StationType.PHOTO -> this
        }
    }
}

data class StationOutput(
    val stationType: StationType,
    val isCompleted: Boolean = false,
    val qualityScore: Double = 1.0,
    val cakeLayers: Int = 0,
    val cakeFlavor: String? = null,
    val cakeToppings: List<String> = emptyList(),
    val petFed: Boolean = false,
    val petClean: Double = 1.0,
    val petGroomed: Boolean = false,
    val photoTaken: Boolean = false
)

enum class RenderLayer(val order: Int, val layerName: String) {
    BACKGROUND(1, "background"),
    BACK_WINGS(2, "back_wings"),
    BACK_HAIR(3, "back_hair"),
    BODY_SKIN(4, "body_skin"),
    MAKEUP_MASKED(5, "makeup_masked"),
    EYES_LASHES(6, "eyes_lashes"),
    OUTFIT(7, "outfit"),
    FRONT_HAIR(8, "front_hair"),
    ACCESSORIES(9, "accessories_jewelry_horn"),
    TIARA(10, "tiara_crown"),
    STICKERS_SPARKLES(11, "stickers_sparkles");

    companion object {
        fun ordered(): List<RenderLayer> = entries.sortedBy { it.order }
    }
}

data class SaveData(
    val schemaVersion: Int = 1,
    val candyBalance: Int = 50,
    val bestStars: Map<Int, Int> = emptyMap(),
    val unlockedLevels: Set<Int> = setOf(1),
    val unlockedItems: Set<String> = emptySet(),
    val purchasedEntitlements: Set<String> = emptySet(),
    val galleryCount: Int = 0,
    val bgmEnabled: Boolean = true,
    val sfxEnabled: Boolean = true
)
