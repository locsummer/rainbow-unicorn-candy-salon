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
| **Kotlin Standard Library & Coroutines** | 1.9.22+ / 1.8.0+ | Apache 2.0 | Xử lý ngôn ngữ, lập trình bất đồng bộ (Coroutines + Flow). |
| **AndroidX Core KTX** | 1.13.1+ | Apache 2.0 | Tiện ích mở rộng Kotlin cho Android runtime chuẩn. |
| **AndroidX Lifecycle & ViewModel Compose** | 2.8.6+ | Apache 2.0 | Kiến trúc MVVM, quản lý vòng đời màn hình và StateFlow. |
| **Jetpack Compose BOM & UI Tooling** | 2024.09.00+ | Apache 2.0 | Bộ công cụ UI khai báo chính thức của Android, dùng Canvas vẽ nhân vật và hạt hiệu ứng. |
| **AndroidX Activity Compose** | 1.9.2+ | Apache 2.0 | Tích hợp Activity vòng đời với Compose UI. |
| **AndroidX Navigation Compose** | 2.8.0+ | Apache 2.0 | Điều hướng phân cấp giữa các màn hình trong ứng dụng. |
| **JUnit 4** (Test) | 4.13.2 | EPL 2.0 | Bộ khung kiểm thử đơn vị tiêu chuẩn trên JVM. |
| **Kotlinx Coroutines Test** (Test) | 1.8.0+ | Apache 2.0 | Kiểm thử bất đồng bộ với `runTest` và `StandardTestDispatcher`. |
| **Turbine** (Test) | 1.1.0 | Apache 2.0 | Thư viện kiểm thử phản ứng luồng StateFlow chuyên dụng cho Kotlin. |
| **AndroidX Compose UI Test** (Test) | 1.7.0+ | Apache 2.0 | Kiểm thử tự động giao diện Compose. |

*Tổng số dependency bên thứ ba ngoài hệ sinh thái AndroidX/Jetpack chính thống: **0** (không có SDK bên ngoài).*

---

## 3. Công cụ Hỗ trợ & Đồng bộ Dữ liệu

| Công cụ | Phiên bản | License | Mục đích |
|---|---|---|---|
| **Node.js** | 22.23.2 | MIT | Thực thi script kiểm tra tính hợp lệ JSON schema, tính mã băm SHA-256 và đồng bộ specs giữa hai nền tảng. |
| **Git** | 2.55.0 | GPL v2 | Quản lý phiên bản mã nguồn. |
