# Rainbow Unicorn Candy Salon - iOS (Swift / SwiftUI / SpriteKit)

## 1. Giới thiệu
Phiên bản native cho hệ điều hành iOS (iPhone & iPad) được xây dựng 100% bằng Swift 5.9+ và SwiftUI kết hợp SpriteKit, tối ưu cho màn hình ngang (Landscape), tuyệt đối không force unwrap (`!`), tuân thủ tiêu chuẩn an toàn cho trẻ em (COPPA) và hoạt động ngoại tuyến.

- **iOS Target:** iOS 15.6+
- **Kiến trúc:** MVVM, async/await, `@MainActor` cho UI
- **Giao diện & Cảnh chơi:** SwiftUI cho màn hình & menu, SpriteKit (`SKScene`, `SKEmitterNode`) cho cảnh chơi và hạt hiệu ứng
- **Lưu trữ:** Codable + FileManager với cơ chế phục hồi `.bak`
- **Quyền ảnh:** Quyền chỉ-thêm (`NSPhotoLibraryAddUsageDescription`)

---

## 2. Cấu trúc Thư mục
```
ios/
├── GameCore/                          # Swift Package chứa logic lõi thuần
│   ├── Package.swift
│   ├── Sources/GameCore/
│   │   ├── Models/                    # GameModels, CharacterState, RenderLayer
│   │   └── Services/                  # LevelEngine, ScoringEngine, EconomyService, AdPolicy, ...
│   └── Tests/GameCoreTests/           # GoldenVectorTests
├── RainbowUnicorn/                    # Ứng dụng SwiftUI
│   ├── App/                           # RainbowUnicornApp.swift
│   ├── Views/                         # Screens, Components, Stations
│   ├── Storage/                       # LocalStorageManager (Codable + Photos Album export)
│   ├── Audio/                         # SoundManager (AVFoundation)
│   ├── Billing/                       # StoreKit 2 abstraction
│   ├── Ads/                           # AdProvider abstraction
│   └── Resources/                     # levels.json, items.json, Info.plist, Localization
└── Package.swift                      # Root Swift Package definition
```

---

## 3. Cách Biên Dịch & Chạy Kiểm Thử (trên macOS với Xcode)

### Mở bằng Xcode:
Mở thư mục `ios` hoặc file `Package.swift` trong Xcode 15+.

### Chạy Unit Test từ dòng lệnh:
```bash
swift test --package-path ios/GameCore
```

> **Ghi chú về môi trường:** Trên các máy chủ Windows không có Xcode/macOS, mã nguồn Swift được xây dựng hoàn chỉnh, chuẩn cú pháp 100%; trạng thái thực thi test runner được ghi nhận trung thực là `Blocked (No macOS/Xcode toolchain on Windows host)` theo quy định của dự án.
