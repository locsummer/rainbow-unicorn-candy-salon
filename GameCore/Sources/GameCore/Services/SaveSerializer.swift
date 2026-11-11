import Foundation

public enum SaveSerializer {

    public static let currentSchemaVersion = 2

    public static func toJSON(_ saveData: SaveData) -> String {
        let encoder = JSONEncoder()
        encoder.outputFormatting = [.prettyPrinted, .sortedKeys]
        guard let data = try? encoder.encode(saveData),
              let str = String(data: data, encoding: .utf8) else {
            return "{}"
        }
        return str
    }

    public static func fromJSONStrict(_ jsonString: String) throws -> SaveData {
        guard let data = jsonString.data(using: .utf8) else {
            throw CocoaError(.fileReadCorruptFile)
        }
        let parsed = try JSONDecoder().decode(SaveData.self, from: data)
        return migrateIfNeeded(parsed)
    }

    public static func fromJSON(_ jsonString: String) -> SaveData {
        return (try? fromJSONStrict(jsonString)) ?? SaveData()
    }

    public static func migrateIfNeeded(_ data: SaveData) -> SaveData {
        guard data.schemaVersion < currentSchemaVersion else {
            return data
        }
        var migrated = data
        migrated.schemaVersion = currentSchemaVersion
        if !migrated.unlockedLevels.contains(1) {
            migrated.unlockedLevels.append(1)
            migrated.unlockedLevels.sort()
        }
        if migrated.language.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            migrated.language = "en"
        }
        return migrated
    }
}
