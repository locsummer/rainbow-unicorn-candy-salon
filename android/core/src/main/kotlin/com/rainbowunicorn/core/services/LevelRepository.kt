package com.rainbowunicorn.core.services

import com.rainbowunicorn.core.models.CharacterType
import com.rainbowunicorn.core.models.Criterion
import com.rainbowunicorn.core.models.Item
import com.rainbowunicorn.core.models.ItemCategory
import com.rainbowunicorn.core.models.Level
import com.rainbowunicorn.core.models.SlotType
import com.rainbowunicorn.core.models.StationType

object LevelRepository {

    fun loadLevels(jsonStr: String): List<Level> {
        val root = JsonParser.parse(jsonStr) as? JsonElement.JsonObject ?: return emptyList()
        val levelsArray = root.getArray("levels") ?: return emptyList()

        return levelsArray.mapNotNull { lvlElement ->
            val obj = lvlElement as? JsonElement.JsonObject ?: return@mapNotNull null
            val id = obj.getInt("id") ?: return@mapNotNull null
            val name = obj.getString("name") ?: ""
            val nameVi = obj.getString("nameVi") ?: name
            val theme = obj.getString("theme") ?: ""
            val charTypeStr = obj.getString("characterType") ?: "unicorn"
            val charType = CharacterType.fromValue(charTypeStr)
            val requiresPremium = obj.getBoolean("requiresPremium") ?: false

            val stations = obj.getArray("stations")?.mapNotNull { stEl ->
                (stEl as? JsonElement.JsonString)?.value?.let { StationType.fromValue(it) }
            } ?: emptyList()

            val criteria = obj.getArray("criteria")?.mapNotNull { crEl ->
                val crObj = crEl as? JsonElement.JsonObject ?: return@mapNotNull null
                val crId = crObj.getString("id") ?: ""
                val st = crObj.getString("station") ?: ""
                val type = crObj.getString("type") ?: ""
                val desc = crObj.getString("description") ?: ""
                val descEn = crObj.getString("descriptionEn") ?: desc
                val targetEl = crObj.fields["target"]
                val target: Any? = when (targetEl) {
                    is JsonElement.JsonString -> targetEl.value
                    is JsonElement.JsonNumber -> targetEl.value
                    is JsonElement.JsonBoolean -> targetEl.value
                    else -> null
                }
                Criterion(id = crId, station = st, type = type, target = target, description = desc, descriptionEn = descEn)
            } ?: emptyList()

            Level(
                id = id,
                name = name,
                nameVi = nameVi,
                theme = theme,
                characterType = charType,
                requiresPremium = requiresPremium,
                stations = stations,
                criteria = criteria
            )
        }
    }

    fun loadItems(jsonStr: String): Pair<List<ItemCategory>, List<Item>> {
        val root = JsonParser.parse(jsonStr) as? JsonElement.JsonObject ?: return Pair(emptyList(), emptyList())

        val categories = root.getArray("categories")?.mapNotNull { el ->
            val obj = el as? JsonElement.JsonObject ?: return@mapNotNull null
            val id = obj.getString("id") ?: ""
            val name = obj.getString("name") ?: ""
            val nameEn = obj.getString("nameEn") ?: name
            val slotTypeStr = obj.getString("slotType") ?: "exclusive"
            val slotType = SlotType.fromValue(slotTypeStr)
            val maxSelected = obj.getInt("maxSelected") ?: 1
            ItemCategory(id = id, name = name, nameEn = nameEn, slotType = slotType, maxSelected = maxSelected)
        } ?: emptyList()

        val items = root.getArray("items")?.mapNotNull { el ->
            val obj = el as? JsonElement.JsonObject ?: return@mapNotNull null
            val id = obj.getString("id") ?: ""
            val name = obj.getString("name") ?: ""
            val nameEn = obj.getString("nameEn") ?: name
            val category = obj.getString("category") ?: ""
            val unlocked = obj.getBoolean("unlockedByDefault") ?: true
            val price = obj.getInt("candyPrice") ?: 0
            val assetId = obj.getString("assetId") ?: ""
            val color = obj.getString("color")
            val layer = obj.getInt("layer") ?: 0

            Item(
                id = id,
                name = name,
                nameEn = nameEn,
                category = category,
                unlockedByDefault = unlocked,
                candyPrice = price,
                assetId = assetId,
                color = color,
                layer = layer
            )
        } ?: emptyList()

        return Pair(categories, items)
    }
}
