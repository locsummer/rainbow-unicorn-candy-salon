# 🦄 Rainbow Unicorn Candy Salon (iOS & Android)

[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge)](android/)
[![Android APK](https://img.shields.io/badge/Download-APK-blue?style=for-the-badge&logo=android)](rainbow-unicorn.apk)
[![Platform - Android](https://img.shields.io/badge/Platform-Android%20Kotlin-brightgreen?style=for-the-badge&logo=kotlin)](android/)
[![Platform - iOS](https://img.shields.io/badge/Platform-iOS%20Swift-orange?style=for-the-badge&logo=swift)](ios/)
[![Tests - 258 Verified](https://img.shields.io/badge/Tests-258%20Cases%20(18%20Groups)-purple?style=for-the-badge)](docs/TEST_CASES.csv)

Game thời trang & thẩm mỹ viện kẹo ngọt **Rainbow Unicorn Candy Salon** được phát triển song song chuẩn Native cho cả hai nền tảng:
- 🤖 **Android:** 100% Kotlin & Jetpack Compose (minSdk 24, targetSdk 35). File cài đặt [rainbow-unicorn.apk](file:///d:/rainbow-unicorn-candy-salon/rainbow-unicorn.apk) đã được biên dịch sẵn ngay tại thư mục gốc!
- 🍏 **iOS:** 100% Swift 5.9+, SwiftUI & SpriteKit (iOS 15.6+).

---

## 🌟 Điểm Nổi Bật Của Trò Chơi

1. **Đồng bộ Luật chơi Tuyệt đối (Single Source of Truth):**
   - 20 level tuần tự hoàn chỉnh định nghĩa tại [spec/levels.json](file:///d:/rainbow-unicorn-candy-salon/spec/levels.json).
   - Danh mục 82 món đồ phân bổ trên 14 danh mục tại [spec/items.json](file:///d:/rainbow-unicorn-candy-salon/spec/items.json), đảm bảo tỷ lệ đồ khoá $\le 25\%$ mỗi danh mục.
   - Bộ 5 tập Golden Test Vectors ([spec/golden/](file:///d:/rainbow-unicorn-candy-salon/spec/golden)) bảo đảm hai nền tảng đưa ra cùng kết quả đến từng điểm số.
2. **7 Trạm Chơi Sáng Tạo:**
   - 🧼 **Spa làm sạch:** Chà vết bẩn với hiệu ứng bọt xà phòng.
   - ✂️ **Làm tóc:** Bờm tóc 7 sắc cầu vồng, uốn sấy bồng bềnh.
   - 💄 **Trang điểm:** Phấn mắt cắt theo mask mí mắt (không tràn ra ngoài và nằm dưới hàng mi), son kẹo ngọt, vẽ mặt nghệ thuật.
   - 👗 **Thay đồ & Phụ kiện:** Váy dạ hội kẹo bông, cánh thiên thần, sừng xoắn hoàng kim, vương miện, sticker lấp lánh.
   - 🎂 **Làm bánh kẹo:** Xếp tầng bánh từ 1 đến 4 tầng, phủ kem, rắc kẹo cốm.
   - 🐾 **Chăm sóc thú cưng:** Tắm rửa, chải lông và cho rồng con / cún bông ăn kẹo.
   - 📸 **Chụp ảnh tiệc:** Nhảy múa ăn mừng, hiệu ứng flash máy ảnh, lưu vào thư viện.
3. **An Toàn Cho Trẻ Em (COPPA & Families Compliant):**
   - Hoạt động **100% ngoại tuyến (Offline)**, không cần Internet.
   - Không thu thập IDFA, không GAID, không SDK theo dõi định danh.
   - **Cổng phụ huynh 2 lớp:** Giữ nút liên tục trong 3.0 giây kèm câu hỏi cộng số học đơn giản trước mọi thao tác nhạy cảm.
4. **Hệ Thống Kiểm Thử Tự Động Toàn Diện:**
   - Đủ **258 test cases** phân loại theo 18 nhóm tiêu chuẩn tại [docs/TEST_CASES.csv](file:///d:/rainbow-unicorn-candy-salon/docs/TEST_CASES.csv).
   - **LevelBot** tự động mô phỏng chơi 20 level với 3 chiến lược: Tối ưu (100% thắng 3 sao), Không làm gì (thử nghiệm luồng Chưa đạt), và **1000 lượt ngẫu nhiên** kiểm chứng 8 bất biến toàn cục.

---

## 📁 Cấu Trúc Repository
```
rainbow-unicorn-candy-salon/
├── android/                           # Dự án Android Native (Kotlin + Compose)
├── ios/                               # Dự án iOS Native (Swift + SwiftUI + SpriteKit)
├── spec/                              # Nguồn sự thật duy nhất (Single Source of Truth)
│   ├── game-spec.md                   # Đặc tả chi tiết mã hiệu RULE-001..RULE-094
│   ├── levels.json                    # Cấu hình 20 màn chơi
│   ├── items.json                     # Danh mục 82 món đồ
│   ├── checksum.json                  # Mã băm SHA-256 bảo vệ tính toàn vẹn
│   └── golden/                        # 5 bộ test vectors dùng chung
├── docs/                              # Tài liệu & Bằng chứng kiểm thử
│   ├── ANALYSIS.md                    # Khảo sát môi trường và phân tích khoảng trống
│   ├── ASSUMPTIONS.md                 # Danh sách các giả định hợp lý (ASM-001..ASM-011)
│   ├── DEPENDENCIES.md                # Danh mục phụ thuộc (Zero third-party SDK)
│   ├── PROGRESS.md                    # Nhật ký tiến độ từng giai đoạn G0-G7
│   ├── TEST_CASES.csv                 # Danh mục 258 test cases
│   ├── BUG_REPORT.md                  # Tổng hợp lỗi và nhật ký sửa lỗi
│   └── evidence/                      # Nhật ký thực thi kiểm thử thực tế
├── scripts/                           # Script xác thực schema và đồng bộ spec
├── CREDITS.md                         # Ghi nhận bản quyền đồ hoạ và âm thanh CC0
└── rainbow-unicorn.apk                # File cài đặt Android APK sẵn có
```
