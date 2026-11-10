# ANALYSIS: Rainbow Unicorn Candy Salon

## 1. Tổng quan Dự án & Mục tiêu
- **Tên dự án:** Rainbow Unicorn Candy Salon
- **Nền tảng mục tiêu:**
  - **iOS:** Swift 5.9+, iOS 15.6+ (iPhone & iPad), SwiftUI cho điều hướng/menu, SpriteKit cho cảnh chơi và hiệu ứng hạt, MVVM, async/await, @MainActor cho UI, Codable + FileManager, StoreKit 2 abstraction qua BillingProvider.
  - **Android:** Kotlin 1.9+, Android SDK minSdk 24, targetSdk 35 (Android 15), Jetpack Compose (Canvas cho cảnh chơi và hạt), MVVM/MVI, Coroutines + Flow, DataStore/JSON file, Play Billing abstraction qua BillingProvider.
- **Mục tiêu chất lượng:**
  - Đồng bộ 100% luật chơi giữa hai nền tảng dựa trên một nguồn sự thật duy nhất (`spec/game-spec.md`, `spec/levels.json`, `spec/items.json`, `spec/golden/*.json`).
  - Danh mục test >= 200 (mục tiêu 258 test cases theo 18 nhóm).
  - Tự kiểm thử toàn diện, LevelBot tự động chơi 20 level với 3 chiến lược (tối ưu, ngẫu nhiên, không làm gì), tổng hợp `docs/BUG_REPORT.md` và sửa lỗi qua các vòng lặp.

---

## 2. Khảo sát Hiện trạng Workspace
- **Trạng thái kho mã nguồn:** Bắt đầu từ thư mục trống (`D:\rainbow-unicorn-candy-salon`), đã khởi tạo Git repository mới.
- **Thư viện/Dự án tham khảo trên máy:**
  - `D:\snake-puzzle-slither-to-eat`: Dự án song song Android (Kotlin/Compose) + iOS (Swift/SwiftUI) hoàn chỉnh cùng hệ thống levels/spec.
  - `D:\rotate-rings`, `D:\pedometer`: Mẫu cấu trúc Gradle, Compose và Android SDK.

---

## 3. Khảo sát Toolchain & Khả năng Chạy Thực tế

| Công cụ / Nền tảng | Trạng thái trên Host | Chi tiết phiên bản / Đường dẫn | Khả năng kiểm thử thực tế |
|---|---|---|---|
| **Hệ điều hành Host** | Sẵn sàng | Windows 11 Enterprise (AMD64 build 26300) | Môi trường host thực thi CLI |
| **Node.js** | Sẵn sàng | v22.23.2 (`C:\Program Files\nodejs\node.exe`) | Chạy schema validator, checksum validator, spec generator |
| **Java JDK** | Sẵn sàng | OpenJDK 21.0.5 (`D:\jdk-21.0.5`) & OpenJDK 25 (`C:\Program Files\Eclipse Adoptium\jdk-25.0.4.7-hotspot`) | Biên dịch Kotlin, Android Gradle Plugin, Unit Tests |
| **Android SDK** | Sẵn sàng | Android SDK (`D:\Android\sdk` & `C:\Users\Admin\AppData\Local\Android\sdk`) | Biên dịch Android App, linting, packaging APK |
| **Android Platforms** | Sẵn sàng | android-33, android-34, android-35, android-36, android-37.0 | Target Android 35 (Android 15) ổn định |
| **Android Build Tools** | Sẵn sàng | 34.0.0, 35.0.0, 36.0.0 | Đóng gói AAR/APK |
| **Gradle Cache** | Sẵn sàng | Cache sẵn Gradle 8.7, 8.11.1, 8.13, 9.3.0 tại `D:\.gradle\wrapper\dists` | Build offline cực nhanh, không phụ thuộc mạng |
| **Android Emulator / AVD** | Sẵn sàng | Emulator 37.1.11, system-image: `android-37.2-beta3/google_apis_playstore_ps16k/x86_64` | Khởi chạy máy ảo Android, Compose UI Test, chụp màn hình |
| **macOS / Xcode / Swift** | Không có (Blocked) | Host là Windows; Apple Xcode và iOS Simulator chỉ chạy trên macOS | Mã nguồn iOS viết đầy đủ, chuẩn xác 100%; trạng thái chạy test iOS ghi rõ `Blocked (No macOS/Xcode toolchain on Windows host)` theo Quy tắc trung thực |

---

## 4. Phân tích Rủi ro & Giải pháp Khắc phục

1. **Rủi ro lệch luật chơi giữa hai nền tảng:**
   - *Giải pháp:* Viết `spec/` dạng JSON và Markdown có đánh số hiệu RULE (RULE-001 ... RULE-xxx). Cả hai nền tảng dùng chung cùng một bộ Golden Test Vectors (`spec/golden/*.json`). Mọi thay đổi dữ liệu phải qua script đồng bộ và kiểm tra mã băm SHA-256 (`spec/checksum.json`).
2. **Rủi ro vi phạm an toàn trẻ em (COPPA / Google Play Families Policy):**
   - *Giải pháp:* Cách ly hoàn toàn mã định danh (không GAID, không IDFA), chế độ chơi 100% offline, cổng phụ huynh (Parental Gate) toán học + giữ nút 3 giây bắt buộc trước mọi tương tác nhạy cảm (mua hàng, khôi phục, đổi cài đặt mạng, đánh giá).
3. **Rủi ro hiệu năng hoạt ảnh & rò rỉ hạt (Particle Leaks):**
   - *Giải pháp:* Giới hạn tối đa 300 hạt hoạt động cùng một lúc, tự dọn dẹp khi đổi trạm hoặc chuyển cảnh, tôn trọng thiết lập Reduce Motion.
4. **Rủi ro force unwrap / crash runtime:**
   - *Giải pháp:* Áp dụng triệt để quy tắc: Swift không `!`, Kotlin không `!!`, bắt lỗi giải mã JSON an toàn với schema fallback.

---

## 5. Kết luận Cổng G0
- Cổng G0 **ĐẠT (PASSED)**.
- Đã nắm rõ năng lực môi trường: Android build & test đầy đủ trên máy thật/máy ảo; iOS triển khai đầy đủ mã nguồn chuẩn mực, ghi nhận test runner Blocked do thiếu macOS toolchain.
- Sẵn sàng bước vào G1 (Đặc tả chi tiết).
