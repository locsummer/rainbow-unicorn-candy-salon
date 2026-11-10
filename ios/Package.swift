// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "RainbowUnicorn",
    platforms: [
        .iOS(.v15),
        .macOS(.v12)
    ],
    products: [
        .executable(
            name: "RainbowUnicorn",
            targets: ["RainbowUnicorn"]
        ),
        .library(
            name: "GameCore",
            targets: ["GameCore"]
        )
    ],
    dependencies: [],
    targets: [
        .target(
            name: "GameCore",
            dependencies: [],
            path: "GameCore/Sources/GameCore"
        ),
        .testTarget(
            name: "GameCoreTests",
            dependencies: ["GameCore"],
            path: "GameCore/Tests/GameCoreTests",
            resources: [
                .process("Golden")
            ]
        ),
        .executableTarget(
            name: "RainbowUnicorn",
            dependencies: ["GameCore"],
            path: "RainbowUnicorn",
            resources: [
                .process("Resources")
            ]
        )
    ]
)
