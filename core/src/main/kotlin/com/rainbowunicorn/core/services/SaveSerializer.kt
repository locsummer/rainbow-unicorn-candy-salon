package com.rainbowunicorn.core.services

import com.rainbowunicorn.core.models.CharacterState
import com.rainbowunicorn.core.models.CharacterType
import com.rainbowunicorn.core.models.MakeupState
import com.rainbowunicorn.core.models.SaveData
import com.rainbowunicorn.core.models.SavedLevelCheckpoint
import com.rainbowunicorn.core.models.StationType

object SaveSerializer {

    const val CURRENT_SCHEMA_VERSION = 2

    private fun escape(str: String): String =
        str.replace("\\", "\\\\").replace("\"", "\\\"")

    private fun stringListJson(list: Collection<String>): String =
        list.joinToString(",") { "\"${escape(it)}\"" }

    private fun nullableStringJson(value: String?): String =
        if (value == null) "null" else "\"${escape(value)}\""

    private fun checkpointToJson(cp: SavedLevelCheckpoint?): String {
        if (cp == null) return "null"
        val cs = cp.characterState
        return """
        {
          "levelId": ${cp.levelId},
          "currentStationIndex": ${cp.currentStationIndex},
          "completedStations": [${cp.completedStations.joinToString(",") { "\"${it.value}\"" }}],
          "characterState": {
            "characterType": "${cs.characterType.value}",
            "skinTone": "${escape(cs.skinTone)}",
            "eyeColor": "${escape(cs.eyeColor)}",
            "hairStyleId": ${nullableStringJson(cs.hairStyleId)},
            "hairColorId": ${nullableStringJson(cs.hairColorId)},
            "eyeshadow": ${nullableStringJson(cs.makeup.eyeshadow)},
            "lipstick": ${nullableStringJson(cs.makeup.lipstick)},
            "blush": ${nullableStringJson(cs.makeup.blush)},
            "outfitId": ${nullableStringJson(cs.outfitId)},
            "wingsId": ${nullableStringJson(cs.wingsId)},
            "hornId": ${nullableStringJson(cs.hornId)},
            "tiaraId": ${nullableStringJson(cs.tiaraId)},
            "jewelryId": ${nullableStringJson(cs.jewelryId)},
            "facePaints": [${stringListJson(cs.facePaints)}],
            "stickers": [${stringListJson(cs.stickers)}]
          },
          "spaCleanPercent": ${cp.spaCleanPercent},
          "cakeLayers": ${cp.cakeLayers},
          "cakeFlavor": ${nullableStringJson(cp.cakeFlavor)},
          "cakeToppings": [${stringListJson(cp.cakeToppings)}],
          "petFed": ${cp.petFed},
          "petClean": ${cp.petClean},
          "petGroomed": ${cp.petGroomed},
          "photoTaken": ${cp.photoTaken}
        }
        """.trimIndent()
    }

    fun toJson(saveData: SaveData): String {
        val starsEntries = saveData.bestStars.entries.joinToString(",") { "\"${it.key}\":${it.value}" }
        val unlockedLevels = saveData.unlockedLevels.sorted().joinToString(",") { it.toString() }
        val unlockedItems = stringListJson(saveData.unlockedItems)
        val entitlements = stringListJson(saveData.purchasedEntitlements)

        return """
        {
          "schemaVersion": $CURRENT_SCHEMA_VERSION,
          "candyBalance": ${saveData.candyBalance},
          "bestStars": { $starsEntries },
          "unlockedLevels": [ $unlockedLevels ],
          "unlockedItems": [ $unlockedItems ],
          "purchasedEntitlements": [ $entitlements ],
          "galleryCount": ${saveData.galleryCount},
          "bgmEnabled": ${saveData.bgmEnabled},
          "sfxEnabled": ${saveData.sfxEnabled},
          "language": "${escape(saveData.language)}",
          "activeCheckpoint": ${checkpointToJson(saveData.activeCheckpoint)}
        }
        """.trimIndent()
    }

    private fun parseCheckpoint(obj: JsonElement.JsonObject?): SavedLevelCheckpoint? {
        if (obj == null) return null
        val levelId = obj.getInt("levelId") ?: return null
        val stationIdx = obj.getInt("currentStationIndex") ?: 0
        val completed = obj.getArray("completedStations")?.mapNotNull { el ->
            (el as? JsonElement.JsonString)?.value?.let { StationType.fromValue(it) }
        }?.toSet() ?: emptySet()

        val csObj = obj.getObject("characterState")
        val cs = if (csObj != null) {
            CharacterState(
                characterType = CharacterType.fromValue(csObj.getString("characterType") ?: "unicorn"),
                skinTone = csObj.getString("skinTone") ?: "default",
                eyeColor = csObj.getString("eyeColor") ?: "default",
                hairStyleId = csObj.getString("hairStyleId"),
                hairColorId = csObj.getString("hairColorId"),
                makeup = MakeupState(
                    eyeshadow = csObj.getString("eyeshadow"),
                    lipstick = csObj.getString("lipstick"),
                    blush = csObj.getString("blush")
                ),
                outfitId = csObj.getString("outfitId"),
                wingsId = csObj.getString("wingsId"),
                hornId = csObj.getString("hornId"),
                tiaraId = csObj.getString("tiaraId"),
                jewelryId = csObj.getString("jewelryId"),
                facePaints = csObj.getArray("facePaints")?.mapNotNull { (it as? JsonElement.JsonString)?.value } ?: emptyList(),
                stickers = csObj.getArray("stickers")?.mapNotNull { (it as? JsonElement.JsonString)?.value } ?: emptyList()
            )
        } else CharacterState()

        return SavedLevelCheckpoint(
            levelId = levelId,
            currentStationIndex = stationIdx,
            completedStations = completed,
            characterState = cs,
            spaCleanPercent = obj.getDouble("spaCleanPercent") ?: 0.0,
            cakeLayers = obj.getInt("cakeLayers") ?: 2,
            cakeFlavor = obj.getString("cakeFlavor"),
            cakeToppings = obj.getArray("cakeToppings")?.mapNotNull { (it as? JsonElement.JsonString)?.value } ?: emptyList(),
            petFed = obj.getBoolean("petFed") ?: false,
            petClean = obj.getDouble("petClean") ?: 0.0,
            petGroomed = obj.getBoolean("petGroomed") ?: false,
            photoTaken = obj.getBoolean("photoTaken") ?: false
        )
    }

    fun fromJsonStrict(jsonStr: String): SaveData {
        require(jsonStr.isNotBlank()) { "Blank save JSON" }
        val parsedRoot = JsonParser.parse(jsonStr)
        val root = parsedRoot as? JsonElement.JsonObject
            ?: throw IllegalArgumentException("Save JSON root is not an object")

        val version = root.getInt("schemaVersion") ?: 1
        val candy = (root.getInt("candyBalance") ?: 50).coerceAtLeast(0)

        val bestStars = mutableMapOf<Int, Int>()
        val starsObj = root.getObject("bestStars")
        starsObj?.fields?.forEach { (k, v) ->
            val lvl = k.toIntOrNull()
            val st = (v as? JsonElement.JsonNumber)?.value?.toInt()
            if (lvl != null && st != null) {
                bestStars[lvl] = st.coerceIn(0, 3)
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

        val galleryCount = (root.getInt("galleryCount") ?: 0).coerceAtLeast(0)
        val bgm = root.getBoolean("bgmEnabled") ?: true
        val sfx = root.getBoolean("sfxEnabled") ?: true
        val language = root.getString("language") ?: "en"
        val checkpoint = parseCheckpoint(root.getObject("activeCheckpoint"))

        val parsed = SaveData(
            schemaVersion = version,
            candyBalance = candy,
            bestStars = bestStars,
            unlockedLevels = unlockedLevels,
            unlockedItems = unlockedItems,
            purchasedEntitlements = entitlements,
            galleryCount = galleryCount,
            bgmEnabled = bgm,
            sfxEnabled = sfx,
            language = language,
            activeCheckpoint = checkpoint
        )

        return migrateIfNeeded(parsed)
    }

    fun fromJson(jsonStr: String): SaveData {
        return try {
            fromJsonStrict(jsonStr)
        } catch (_: Exception) {
            SaveData()
        }
    }

    private fun migrateIfNeeded(data: SaveData): SaveData {
        if (data.schemaVersion < CURRENT_SCHEMA_VERSION) {
            val normalizedUnlocked = data.unlockedLevels + 1
            return data.copy(
                schemaVersion = CURRENT_SCHEMA_VERSION,
                unlockedLevels = normalizedUnlocked,
                language = data.language.ifBlank { "en" }
            )
        }
        return data
    }
}
