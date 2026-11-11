# DEPENDENCIES: Rainbow Unicorn Candy Salon

Theo ràng buộc bắt buộc của dự án:
- Ưu tiên tối đa **không dùng thư viện bên thứ ba** (Zero unnecessary third-party dependencies).
- Không tích hợp bất kỳ SDK quảng cáo, SDK analytics, tracker hay IDFA/GAID nào.
- Toàn bộ cơ chế đồ họa, hạt hiệu ứng (particle systems), âm thanh, lưu trữ, toán học và logic cổng phụ huynh đều tự xây dựng (vanilla first-party frameworks).

---

## 1. Nền tảng iOS (Apple Swift)

| Thư viện / Framework | Nguồn gốc | License | Mục đích & Lý do sử dụng |
|---|---|---|---|
| **SwiftUI** | Apple First-Party | Proprietary Apple SDK | Xây dựng giao diện menu, danh sách trạm, cửa hàng, thanh chọn đồ và hộp thoại. |
| **SpriteKit** | Apple First-Party | Proprietary Apple SDK | Dựng cảnh chơi tương tác đồ họa 2D, quản lý nút chạm, renderer nhân vật và hệ thống hạt hiệu ứng lấp lánh (SKEmitterNode). |
| **AVFoundation** | Apple First-Party | Proprietary Apple SDK | Phát nhạc nền vòng lặp (BGM) và hiệu ứng âm thanh (SFX) đa kênh. |
| **StoreKit 2** | Apple First-Party | Proprietary Apple SDK | Triển khai giao diện thanh toán In-App Purchase chuẩn sau BillingProvider. |
| **Photos** | Apple First-Party | Proprietary Apple SDK | Lưu ảnh Party Mode vào Photos Album người dùng với quyền chỉ-thêm (`NSPhotoLibraryAddUsageDescription`). |
| **XCTest** | Apple First-Party | Proprietary Apple SDK | Bộ khung kiểm thử Unit Test và UI Test chuẩn cho iOS. |

*Tổng số dependency bên thứ ba trên iOS: **0**.*

---

## 2. Nền tảng Android (Google Kotlin)

| Thư viện / Dependency | Phiên bản | Nguồn gốc / License | Mục đích & Lý do sử dụng |
|---|---|---|---|
| **Kotlin Standard Library & JVM (`:core`)** | 2.0.0 | Apache 2.0 | Module `:core` thuần JVM, hoàn toàn không phụ thuộc thư viện bên thứ ba hay Android SDK. |
| **AndroidX Core KTX** | 1.13.1 | Apache 2.0 | Tiện ích mở rộng Kotlin cho Android runtime & WindowCompat edge-to-edge. |
| **AndroidX Lifecycle Runtime KTX** | 2.8.4 | Apache 2.0 | Quản lý vòng đời Activity (`LifecycleEventObserver` tạm dừng/tiếp tục âm thanh BGM và tự lưu state). |
| **Jetpack Compose BOM (UI, Graphics, Material 3)** | 2024.06.00 | Apache 2.0 | Bộ công cụ UI khai báo chính thức của Android, dùng Canvas vẽ nhân vật 11 lớp và hạt hiệu ứng. |
| **AndroidX Material Icons Extended** | Compose BOM | Apache 2.0 | Bộ icon chuẩn Material đi kèm nhãn chữ trên các nút điều hướng an toàn cho trẻ em. |
| **AndroidX Activity Compose** | 1.9.1 | Apache 2.0 | `ComponentActivity`, `setContent`, `ActivityResultRegistryOwner` (xin quyền API <= 28). |
| **Kotlin Test (`kotlin("test")` + JUnit Platform)** | 2.0.0 | Apache 2.0 / EPL 2.0 | Khung kiểm thử đơn vị thuần JVM trong `:core:test` (Golden Vectors, LevelBot, SaveSchema). |

*Tổng số dependency bên thứ ba ngoài hệ sinh thái AndroidX/Jetpack/Kotlin chính thống: **0** (không có SDK bên ngoài).*

---

## 3. Công cụ Hỗ trợ & Đồng bộ Dữ liệu

| Công cụ | Phiên bản | License | Mục đích |
|---|---|---|---|
| **Node.js** | 22.23.2 | MIT | Thực thi script kiểm tra tính hợp lệ JSON schema, tính mã băm SHA-256 và đồng bộ specs giữa hai nền tảng. |
| **Git** | 2.55.0 | GPL v2 | Quản lý phiên bản mã nguồn. |
