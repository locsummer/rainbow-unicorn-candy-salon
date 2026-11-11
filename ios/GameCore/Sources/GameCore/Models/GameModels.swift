import Foundation

public enum CharacterType: String, Codable, CaseIterable, Hashable {
    case unicorn = "unicorn"
    case princess = "princess"

    public static func fromValue(_ val: String) -> CharacterType {
        return CharacterType(rawValue: val.lowercased()) ?? .unicorn
    }
}

public enum StationType: String, Codable, CaseIterable, Hashable {
    case spa = "spa"
    case hair = "hair"
    case makeup = "makeup"
    case dressup = "dressup"
    case cake = "cake"
    case pet = "pet"
    case photo = "photo"

    public static func fromValue(_ val: String) -> StationType {
        return StationType(rawValue: val.lowercased()) ?? .spa
    }
}

public enum StationStatus: String, Codable {
    case notStarted = "not_started"
    case inProgress = "in_progress"
    case completed = "completed"
}

public enum SlotType: String, Codable {
    case exclusive = "exclusive"
    case multi = "multi"
}

public struct Criterion: Codable, Equatable, Identifiable {
    public let id: String
    public let station: String
    public let type: String
    public let targetDescription: String
    public let description: String
    public let descriptionEn: String

    enum CodingKeys: String, CodingKey {
        case id, station, type, target, targetDescription, description, descriptionEn
    }

    public init(
        id: String,
        station: String,
        type: String,
        targetDescription: String,
        description: String,
        descriptionEn: String? = nil
    ) {
        self.id = id
        self.station = station
        self.type = type
        self.targetDescription = targetDescription
        self.description = description
        self.descriptionEn = descriptionEn ?? description
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        id = try container.decode(String.self, forKey: .id)
        station = try container.decode(String.self, forKey: .station)
        type = try container.decode(String.self, forKey: .type)
        description = try container.decode(String.self, forKey: .description)
        descriptionEn = (try container.decodeIfPresent(String.self, forKey: .descriptionEn)) ?? description

        if let strTarget = try? container.decode(String.self, forKey: .target) {
            targetDescription = strTarget
        } else if let intTarget = try? container.decode(Int.self, forKey: .target) {
            targetDescription = String(intTarget)
        } else if let dblTarget = try? container.decode(Double.self, forKey: .target) {
            targetDescription = String(dblTarget)
        } else if let boolTarget = try? container.decode(Bool.self, forKey: .target) {
            targetDescription = boolTarget ? "true" : "false"
        } else {
            targetDescription = (try container.decodeIfPresent(String.self, forKey: .targetDescription)) ?? ""
        }
    }

    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(id, forKey: .id)
        try container.encode(station, forKey: .station)
        try container.encode(type, forKey: .type)
        try container.encode(targetDescription, forKey: .targetDescription)
        try container.encode(description, forKey: .description)
        try container.encode(descriptionEn, forKey: .descriptionEn)
    }

    public func localizedDescription(language: String) -> String {
        if language.lowercased() == "vi" {
            return description
        }
        return descriptionEn.isEmpty ? description : descriptionEn
    }
}

public struct Level: Codable, Equatable, Identifiable {
    public let id: Int
    public let name: String
    public let nameVi: String
    public let theme: String
    public let characterType: CharacterType
    public let requiresPremium: Bool
    public let stations: [StationType]
    public let criteria: [Criterion]
    public let baseCandyReward: Int

    enum CodingKeys: String, CodingKey {
        case id, name, nameVi, theme, characterType, requiresPremium, stations, criteria, baseCandyReward
    }

    public init(
        id: Int,
        name: String,
        nameVi: String? = nil,
        theme: String,
        characterType: CharacterType,
        requiresPremium: Bool,
        stations: [StationType],
        criteria: [Criterion],
        baseCandyReward: Int = 10
    ) {
        self.id = id
        self.name = name
        self.nameVi = nameVi ?? name
        self.theme = theme
        self.characterType = characterType
        self.requiresPremium = requiresPremium
        self.stations = stations
        self.criteria = criteria
        self.baseCandyReward = baseCandyReward
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        id = try container.decode(Int.self, forKey: .id)
        name = try container.decode(String.self, forKey: .name)
        nameVi = (try container.decodeIfPresent(String.self, forKey: .nameVi)) ?? name
        theme = try container.decode(String.self, forKey: .theme)
        characterType = try container.decode(CharacterType.self, forKey: .characterType)
        requiresPremium = try container.decode(Bool.self, forKey: .requiresPremium)
        stations = try container.decode([StationType].self, forKey: .stations)
        criteria = try container.decode([Criterion].self, forKey: .criteria)
        baseCandyReward = (try container.decodeIfPresent(Int.self, forKey: .baseCandyReward)) ?? 10
    }

    public func localizedName(language: String) -> String {
        if language.lowercased() == "vi" {
            return nameVi.isEmpty ? name : nameVi
        }
        return name
    }
}

public struct Item: Codable, Equatable, Identifiable {
    public let id: String
    public let name: String
    public let nameEn: String
    public let category: String
    public let unlockedByDefault: Bool
    public let candyPrice: Int
    public let assetId: String
    public let color: String?
    public let layer: Int

    enum CodingKeys: String, CodingKey {
        case id, name, nameEn, category, unlockedByDefault, candyPrice, assetId, color, layer
    }

    public init(
        id: String,
        name: String,
        nameEn: String? = nil,
        category: String,
        unlockedByDefault: Bool,
        candyPrice: Int,
        assetId: String,
        color: String? = nil,
        layer: Int = 0
    ) {
        self.id = id
        self.name = name
        self.nameEn = nameEn ?? name
        self.category = category
        self.unlockedByDefault = unlockedByDefault
        self.candyPrice = candyPrice
        self.assetId = assetId
        self.color = color
        self.layer = layer
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        id = try container.decode(String.self, forKey: .id)
        name = try container.decode(String.self, forKey: .name)
        nameEn = (try container.decodeIfPresent(String.self, forKey: .nameEn)) ?? name
        category = try container.decode(String.self, forKey: .category)
        unlockedByDefault = try container.decode(Bool.self, forKey: .unlockedByDefault)
        candyPrice = try container.decode(Int.self, forKey: .candyPrice)
        assetId = try container.decode(String.self, forKey: .assetId)
        color = try container.decodeIfPresent(String.self, forKey: .color)
        layer = (try container.decodeIfPresent(Int.self, forKey: .layer)) ?? 0
    }

    public func localizedName(language: String) -> String {
        if language.lowercased() == "vi" {
            return name
        }
        return nameEn.isEmpty ? name : nameEn
    }
}

public struct ItemCategory: Codable, Equatable, Identifiable {
    public let id: String
    public let name: String
    public let nameEn: String
    public let slotType: SlotType
    public let maxSelected: Int

    enum CodingKeys: String, CodingKey {
        case id, name, nameEn, slotType, maxSelected
    }

    public init(
        id: String,
        name: String,
        nameEn: String? = nil,
        slotType: SlotType,
        maxSelected: Int
    ) {
        self.id = id
        self.name = name
        self.nameEn = nameEn ?? name
        self.slotType = slotType
        self.maxSelected = maxSelected
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        id = try container.decode(String.self, forKey: .id)
        name = try container.decode(String.self, forKey: .name)
        nameEn = (try container.decodeIfPresent(String.self, forKey: .nameEn)) ?? name
        slotType = try container.decode(SlotType.self, forKey: .slotType)
        maxSelected = try container.decode(Int.self, forKey: .maxSelected)
    }

    public func localizedName(language: String) -> String {
        if language.lowercased() == "vi" {
            return name
        }
        return nameEn.isEmpty ? name : nameEn
    }
}

public struct MakeupState: Codable, Equatable {
    public var eyeshadow: String?
    public var lipstick: String?
    public var blush: String?

    public init(eyeshadow: String? = nil, lipstick: String? = nil, blush: String? = nil) {
        self.eyeshadow = eyeshadow
        self.lipstick = lipstick
        self.blush = blush
    }
}

public struct CharacterState: Codable, Equatable {
    public var characterType: CharacterType
    public var skinTone: String
    public var eyeColor: String
    public var hairStyleId: String?
    public var hairColorId: String?
    public var makeup: MakeupState
    public var outfitId: String?
    public var wingsId: String?
    public var hornId: String?
    public var tiaraId: String?
    public var jewelryId: String?
    public var facePaints: [String]
    public var stickers: [String]

    public init(
        characterType: CharacterType = .unicorn,
        skinTone: String = "default",
        eyeColor: String = "default",
        hairStyleId: String? = nil,
        hairColorId: String? = nil,
        makeup: MakeupState = MakeupState(),
        outfitId: String? = nil,
        wingsId: String? = nil,
        hornId: String? = nil,
        tiaraId: String? = nil,
        jewelryId: String? = nil,
        facePaints: [String] = [],
        stickers: [String] = []
    ) {
        self.characterType = characterType
        self.skinTone = skinTone
        self.eyeColor = eyeColor
        self.hairStyleId = hairStyleId
        self.hairColorId = hairColorId
        self.makeup = makeup
        self.outfitId = outfitId
        self.wingsId = wingsId
        self.hornId = hornId
        self.tiaraId = tiaraId
        self.jewelryId = jewelryId
        self.facePaints = facePaints
        self.stickers = stickers
    }

    public func resetCurrentStation(_ station: StationType) -> CharacterState {
        var copy = self
        switch station {
        case .spa:
            return copy
        case .hair:
            copy.hairStyleId = nil
            copy.hairColorId = nil
        case .makeup:
            copy.makeup = MakeupState()
            copy.facePaints = []
        case .dressup:
            copy.outfitId = nil
            copy.wingsId = nil
            copy.hornId = nil
            copy.tiaraId = nil
            copy.jewelryId = nil
            copy.stickers = []
        case .cake, .pet, .photo:
            return copy
        }
        return copy
    }
}

public struct StationOutput: Equatable {
    public let stationType: StationType
    public var isCompleted: Bool
    public var qualityScore: Double
    public var cakeLayers: Int
    public var cakeFlavor: String?
    public var cakeToppings: [String]
    public var petFed: Bool
    public var petClean: Double
    public var petGroomed: Bool
    public var photoTaken: Bool

    public init(
        stationType: StationType,
        isCompleted: Bool = false,
        qualityScore: Double = 1.0,
        cakeLayers: Int = 0,
        cakeFlavor: String? = nil,
        cakeToppings: [String] = [],
        petFed: Bool = false,
        petClean: Double = 1.0,
        petGroomed: Bool = false,
        photoTaken: Bool = false
    ) {
        self.stationType = stationType
        self.isCompleted = isCompleted
        self.qualityScore = qualityScore
        self.cakeLayers = cakeLayers
        self.cakeFlavor = cakeFlavor
        self.cakeToppings = cakeToppings
        self.petFed = petFed
        self.petClean = petClean
        self.petGroomed = petGroomed
        self.photoTaken = photoTaken
    }
}

public enum RenderLayer: Int, CaseIterable, Comparable {
    case background = 1
    case backWings = 2
    case backHair = 3
    case bodySkin = 4
    case makeupMasked = 5
    case eyesLashes = 6
    case outfit = 7
    case frontHair = 8
    case accessories = 9
    case tiara = 10
    case stickersSparkles = 11

    public var layerName: String {
        switch self {
        case .background: return "background"
        case .backWings: return "back_wings"
        case .backHair: return "back_hair"
        case .bodySkin: return "body_skin"
        case .makeupMasked: return "makeup_masked"
        case .eyesLashes: return "eyes_lashes"
        case .outfit: return "outfit"
        case .frontHair: return "front_hair"
        case .accessories: return "accessories_jewelry_horn"
        case .tiara: return "tiara_crown"
        case .stickersSparkles: return "stickers_sparkles"
        }
    }

    public static func < (lhs: RenderLayer, rhs: RenderLayer) -> Bool {
        return lhs.rawValue < rhs.rawValue
    }
}

public struct SavedLevelCheckpoint: Codable, Equatable {
    public var levelId: Int
    public var currentStationIndex: Int
    public var completedStations: [StationType]
    public var characterState: CharacterState
    public var spaCleanPercent: Double
    public var cakeLayers: Int
    public var cakeFlavor: String?
    public var cakeToppings: [String]
    public var petFed: Bool
    public var petClean: Double
    public var petGroomed: Bool
    public var photoTaken: Bool

    public init(
        levelId: Int,
        currentStationIndex: Int = 0,
        completedStations: [StationType] = [],
        characterState: CharacterState = CharacterState(),
        spaCleanPercent: Double = 0.0,
        cakeLayers: Int = 2,
        cakeFlavor: String? = "cake_flavor_vanilla_rainbow",
        cakeToppings: [String] = [],
        petFed: Bool = false,
        petClean: Double = 0.0,
        petGroomed: Bool = false,
        photoTaken: Bool = false
    ) {
        self.levelId = levelId
        self.currentStationIndex = currentStationIndex
        self.completedStations = completedStations
        self.characterState = characterState
        self.spaCleanPercent = spaCleanPercent
        self.cakeLayers = cakeLayers
        self.cakeFlavor = cakeFlavor
        self.cakeToppings = cakeToppings
        self.petFed = petFed
        self.petClean = petClean
        self.petGroomed = petGroomed
        self.photoTaken = photoTaken
    }
}

public struct SaveData: Codable, Equatable {
    public var schemaVersion: Int
    public var candyBalance: Int
    public var bestStars: [String: Int]
    public var unlockedLevels: [Int]
    public var unlockedItems: [String]
    public var purchasedEntitlements: [String]
    public var galleryCount: Int
    public var bgmEnabled: Bool
    public var sfxEnabled: Bool
    public var language: String
    public var activeCheckpoint: SavedLevelCheckpoint?

    enum CodingKeys: String, CodingKey {
        case schemaVersion, candyBalance, bestStars, unlockedLevels, unlockedItems
        case purchasedEntitlements, galleryCount, bgmEnabled, sfxEnabled, language, activeCheckpoint
    }

    public init(
        schemaVersion: Int = 2,
        candyBalance: Int = 50,
        bestStars: [String: Int] = [:],
        unlockedLevels: [Int] = [1],
        unlockedItems: [String] = [],
        purchasedEntitlements: [String] = [],
        galleryCount: Int = 0,
        bgmEnabled: Bool = true,
        sfxEnabled: Bool = true,
        language: String = "en",
        activeCheckpoint: SavedLevelCheckpoint? = nil
    ) {
        self.schemaVersion = schemaVersion
        self.candyBalance = candyBalance
        self.bestStars = bestStars
        self.unlockedLevels = unlockedLevels
        self.unlockedItems = unlockedItems
        self.purchasedEntitlements = purchasedEntitlements
        self.galleryCount = galleryCount
        self.bgmEnabled = bgmEnabled
        self.sfxEnabled = sfxEnabled
        self.language = language
        self.activeCheckpoint = activeCheckpoint
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        schemaVersion = (try container.decodeIfPresent(Int.self, forKey: .schemaVersion)) ?? 1
        candyBalance = max(0, (try container.decodeIfPresent(Int.self, forKey: .candyBalance)) ?? 50)
        bestStars = (try container.decodeIfPresent([String: Int].self, forKey: .bestStars)) ?? [:]
        unlockedLevels = (try container.decodeIfPresent([Int].self, forKey: .unlockedLevels)) ?? [1]
        unlockedItems = (try container.decodeIfPresent([String].self, forKey: .unlockedItems)) ?? []
        purchasedEntitlements = (try container.decodeIfPresent([String].self, forKey: .purchasedEntitlements)) ?? []
        galleryCount = max(0, (try container.decodeIfPresent(Int.self, forKey: .galleryCount)) ?? 0)
        bgmEnabled = (try container.decodeIfPresent(Bool.self, forKey: .bgmEnabled)) ?? true
        sfxEnabled = (try container.decodeIfPresent(Bool.self, forKey: .sfxEnabled)) ?? true
        language = (try container.decodeIfPresent(String.self, forKey: .language)) ?? "en"
        activeCheckpoint = try container.decodeIfPresent(SavedLevelCheckpoint.self, forKey: .activeCheckpoint)
    }
}
