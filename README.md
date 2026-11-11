# Rainbow Unicorn Candy Salon - Android (Kotlin / Jetpack Compose)

## 1. Giới thiệu
Phiên bản native cho hệ điều hành Android được xây dựng 100% bằng Kotlin và Jetpack Compose, tối ưu cho màn hình ngang (Landscape), tuân thủ tiêu chuẩn an toàn cho trẻ em (COPPA / Google Play Families Policy) và hoạt động hoàn toàn offline.

- **minSdk:** 24 (Android 7.0+)
- **targetSdk / compileSdk:** 35 (Android 15)
- **Kiến trúc:** MVVM / MVI với Kotlin Coroutines & StateFlow
- **Giao diện & Cảnh chơi:** Jetpack Compose Material 3 & Compose Canvas
- **Lưu trữ:** JSON mã hoá UTF-8 với cơ chế phục hồi bản sao `.bak`
- **Thanh toán & Quảng cáo:** Abstraction qua `BillingProvider` và `AdProvider`

---

## 2. Cấu trúc Thư mục
```
android/
├── app/                               # Module ứng dụng chính
│   ├── src/main/
│   │   ├── java/com/rainbowunicorn/candysalon/
│   │   │   ├── MainActivity.kt        # Entry point & Navigation điều hướng
│   │   │   ├── ui/                    # Giao diện Compose (Screens, Components, Stations, Theme)
│   │   │   ├── audio/                 # SoundManager tổng hợp âm thanh BGM/SFX
│   │   │   ├── storage/               # LocalStorageManager & MediaStore Export
│   │   │   ├── billing/               # BillingProvider interface & Fake implementation
│   │   │   └── ads/                   # AdProvider interface & Fake implementation
│   │   ├── assets/                    # levels.json, items.json
│   │   └── res/                       # values (en), values-vi (vi)
│   └── build.gradle.kts
├── core/                              # Module logic thuần JVM (Zero UI dependency)
│   ├── src/main/kotlin/com/rainbowunicorn/core/
│   │   ├── models/                    # GameModels, CharacterState, RenderLayer
│   │   ├── services/                  # LevelEngine, ScoringEngine, EconomyService, AdPolicy, ...
│   │   └── bot/                       # LevelBot (Tự động chơi 1000+ lượt ngẫu nhiên)
│   ├── src/test/kotlin/               # Unit tests, GoldenVectorTests, Invariant checks
│   └── build.gradle.kts
├── gradle/                            # Gradle wrapper & libs.versions.toml
├── build.gradle.kts                   # Root build script
├── settings.gradle.kts                # Project settings
└── gradle.properties                  # AndroidX & Compiler flags
```

---

## 3. Cách Biên Dịch & Chạy Kiểm Thử

> **Yêu cầu JDK:** Gradle 8.7 hỗ trợ **JDK 17 hoặc JDK 21** (không chạy trực tiếp trên JDK 22–25). Script `gradlew.bat` / `gradlew` đã hỗ trợ tự động tìm JDK 17/21 tương thích nếu môi trường chưa cấu hình `JAVA_HOME`.

### Biên dịch ứng dụng (Debug APK):
- **Windows (PowerShell / CMD):**
  ```powershell
  cd android
  .\gradlew.bat :app:assembleDebug
  ```
- **Git Bash / macOS / Linux:**
  ```bash
  cd android
  ./gradlew :app:assembleDebug
  ```
File APK xuất ra tại: `app/build/outputs/apk/debug/app-debug.apk` (và bản cài đặt kiểm chứng sẵn tại thư mục gốc `rainbow-unicorn.apk`).

### Biên dịch Release APK (cần cấu hình keystore để ký khi phát hành):
```powershell
.\gradlew.bat :app:assembleRelease
```

### Chạy toàn bộ Unit Tests & Golden Vectors:
```powershell
.\gradlew.bat :core:test --info
```

Báo cáo kết quả kiểm thử HTML tại: `core/build/reports/tests/test/index.html`.
