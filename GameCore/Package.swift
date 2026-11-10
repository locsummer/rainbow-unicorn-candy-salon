// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "GameCore",
    platforms: [
        .iOS(.v15),
        .macOS(.v12)
    ],
    products: [
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
            path: "Sources/GameCore"
        ),
        .testTarget(
            name: "GameCoreTests",
            dependencies: ["GameCore"],
            path: "Tests/GameCoreTests",
            resources: [
                .process("Golden")
            ]
        )
    ]
)
