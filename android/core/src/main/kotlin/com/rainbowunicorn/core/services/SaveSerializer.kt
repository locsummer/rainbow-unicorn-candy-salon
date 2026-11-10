package com.rainbowunicorn.core.services

import com.rainbowunicorn.core.models.SaveData

object SaveSerializer {

    const val CURRENT_SCHEMA_VERSION = 1

    fun toJson(saveData: SaveData): String {
        val starsEntries = saveData.bestStars.entries.joinToString(",") { "\"${it.key}\":${it.value}" }
        val unlockedLevels = saveData.unlockedLevels.joinToString(",") { it.toString() }
        val unlockedItems = saveData.unlockedItems.joinToString(",") { "\"$it\"" }
        val entitlements = saveData.purchasedEntitlements.joinToString(",") { "\"$it\"" }

        return """
        {
          "schemaVersion": ${saveData.schemaVersion},
          "candyBalance": ${saveData.candyBalance},
          "bestStars": { $starsEntries },
          "unlockedLevels": [ $unlockedLevels ],
          "unlockedItems": [ $unlockedItems ],
          "purchasedEntitlements": [ $entitlements ],
          "galleryCount": ${saveData.galleryCount},
          "bgmEnabled": ${saveData.bgmEnabled},
          "sfxEnabled": ${saveData.sfxEnabled}
        }
        """.trimIndent()
    }

    fun fromJson(jsonStr: String): SaveData {
        val root = try {
            JsonParser.parse(jsonStr) as? JsonElement.JsonObject ?: return SaveData()
        } catch (_: Exception) {
            return SaveData() // Fallback to safe default on corruption
        }

        val version = root.getInt("schemaVersion") ?: CURRENT_SCHEMA_VERSION
        val candy = root.getInt("candyBalance") ?: 50

        val bestStars = mutableMapOf<Int, Int>()
        val starsObj = root.getObject("bestStars")
        starsObj?.fields?.forEach { (k, v) ->
            val lvl = k.toIntOrNull()
            val st = (v as? JsonElement.JsonNumber)?.value?.toInt()
            if (lvl != null && st != null) {
                bestStars[lvl] = st
            }
        }

        val unlockedLevels = mutableSetOf<Int>()
        root.getArray("unlockedLevels")?.forEach { el ->
            (el as? JsonElement.JsonNumber)?.value?.toInt()?.let { unlockedLevels.add(it) }
        }
        if (unlockedLevels.isEmpty()) unlockedLevels.add(1)

        val unlockedItems = mutableSetOf<String>()
        root.getArray("unlockedItems")?.forEach { el ->
            (el as? JsonElement.JsonString)?.value?.let { unlockedItems.add(it) }
        }

        val entitlements = mutableSetOf<String>()
        root.getArray("purchasedEntitlements")?.forEach { el ->
            (el as? JsonElement.JsonString)?.value?.let { entitlements.add(it) }
        }

        val galleryCount = root.getInt("galleryCount") ?: 0
        val bgm = root.getBoolean("bgmEnabled") ?: true
        val sfx = root.getBoolean("sfxEnabled") ?: true

        val parsed = SaveData(
            schemaVersion = version,
            candyBalance = candy,
            bestStars = bestStars,
            unlockedLevels = unlockedLevels,
            unlockedItems = unlockedItems,
            purchasedEntitlements = entitlements,
            galleryCount = galleryCount,
            bgmEnabled = bgm,
            sfxEnabled = sfx
        )

        return migrateIfNeeded(parsed)
    }

    private fun migrateIfNeeded(data: SaveData): SaveData {
        if (data.schemaVersion < CURRENT_SCHEMA_VERSION) {
            // Apply future migrations step by step
            return data.copy(schemaVersion = CURRENT_SCHEMA_VERSION)
        }
        return data
    }
}
