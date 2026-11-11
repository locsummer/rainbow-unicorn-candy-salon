import Foundation

public enum LevelRepository {

    private struct LevelsFile: Codable {
        let version: Int
        let levels: [Level]
    }

    private struct ItemsFile: Codable {
        let version: Int
        let categories: [ItemCategory]
        let items: [Item]
    }

    public static func loadLevels(from data: Data) throws -> [Level] {
        let wrapper = try JSONDecoder().decode(LevelsFile.self, from: data)
        return wrapper.levels
    }

    public static func loadLevels(from jsonString: String) throws -> [Level] {
        guard let data = jsonString.data(using: .utf8) else {
            return []
        }
        return try loadLevels(from: data)
    }

    public static func loadItems(from data: Data) throws -> (categories: [ItemCategory], items: [Item]) {
        let wrapper = try JSONDecoder().decode(ItemsFile.self, from: data)
        return (wrapper.categories, wrapper.items)
    }

    public static func loadItems(from jsonString: String) throws -> (categories: [ItemCategory], items: [Item]) {
        guard let data = jsonString.data(using: .utf8) else {
            return ([], [])
        }
        return try loadItems(from: data)
    }
}
