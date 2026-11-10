import Foundation

public enum CharacterType: String, Codable, CaseIterable {
    case unicorn = "unicorn"
    case princess = "princess"

    public static func fromValue(_ val: String) -> CharacterType {
        return CharacterType(rawValue: val.lowercased()) ?? .unicorn
    }
}

public enum StationType: String, Codable, CaseIterable {
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

public struct Criterion: Codable, Equatable {
    public let id: String
    public let station: String
    public let type: String
    public let targetDescription: String
    public let description: String

    public init(id: String, station: String, type: String, targetDescription: String, description: String) {
        self.id = id
        self.station = station
        self.type = type
        self.targetDescription = targetDescription
        self.description = description
    }
}

public struct Level: Codable, Equatable {
    public let id: Int
    public let name: String
    public let theme: String
    public let characterType: CharacterType
    public let requiresPremium: BooleanLiteralType
    public let stations: [StationType]
    public let criteria: [Criterion]
    public let baseCandyReward: Int

    public init(
        id: Int,
        name: String,
        theme: String,
        characterType: CharacterType,
        requiresPremium: Bool,
        stations: [StationType],
        criteria: [Criterion],
        baseCandyReward: Int = 10
    ) {
        self.id = id
        self.name = name
        self.theme = theme
        self.characterType = characterType
        self.requiresPremium = requiresPremium
        self.stations = stations
        self.criteria = criteria
        self.baseCandyReward = baseCandyReward
    }
}

public struct Item: Codable, Equatable {
    public let id: String
    public let name: String
    public let category: String
    public let unlockedByDefault: Bool
    public let candyPrice: Int
    public let assetId: String
    public let color: String?
    public let layer: Int

    public init(
        id: String,
        name: String,
        category: String,
        unlockedByDefault: Bool,
        candyPrice: Int,
        assetId: String,
        color: String? = nil,
        layer: Int = 0
    ) {
        self.id = id
        self.name = name
        self.category = category
        self.unlockedByDefault = unlockedByDefault
        self.candyPrice = candyPrice
        self.assetId = assetId
        self.color = color
        self.layer = layer
    }
}

public struct ItemCategory: Codable, Equatable {
    public let id: String
    public let name: String
    public let slotType: SlotType
    public let maxSelected: Int

    public init(id: String, name: String, slotType: SlotType, maxSelected: Int) {
        self.id = id
        self.name = name
        self.slotType = slotType
        self.maxSelected = maxSelected
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

    public init(
        schemaVersion: Int = 1,
        candyBalance: Int = 50,
        bestStars: [String: Int] = [:],
        unlockedLevels: [Int] = [1],
        unlockedItems: [String] = [],
        purchasedEntitlements: [String] = [],
        galleryCount: Int = 0,
        bgmEnabled: Bool = true,
        sfxEnabled: Bool = true
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
    }
}
