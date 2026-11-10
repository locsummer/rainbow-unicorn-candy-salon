# ASSUMPTIONS: Rainbow Unicorn Candy Salon

Tài liệu này ghi nhận toàn bộ các giả định hợp lý được đưa ra trong quá trình phát triển khi thiếu thông tin cụ thể hoặc cần chuẩn hoá kỹ thuật giữa hai nền tảng:

## 1. Nền tảng & Hệ thống
- **ASM-001 (Hướng màn hình):** Toàn bộ trò chơi chạy ở chế độ **Landscape** (Màn hình ngang). Đây là định dạng công thái học lý tưởng cho trẻ em thao tác trên máy tính bảng và điện thoại khi chơi salon/dress-up, đồng thời phù hợp với cấu trúc trạm và thanh công cụ cuộn ngang bên dưới.
- **ASM-002 (Android SDK versions):**
  - `minSdk = 24` (Android 7.0 Nougat - hỗ trợ >95% thiết bị Android đang lưu hành).
  - `compileSdk = 35` (Android 15).
  - `targetSdk = 35` (Bản Android ổn định mới nhất theo khuyến nghị của Google Play).
- **ASM-003 (iOS Target & Swift):**
  - iOS Deployment Target: `iOS 15.6` (hỗ trợ các dòng iPhone từ iPhone 6s/SE 1st gen trở lên, iPad mini 4 trở lên).
  - Ngôn ngữ: `Swift 5.9+`.
- **ASM-004 (Môi trường kiểm thử iOS):** Do máy chủ host chạy Windows 11 không có macOS/Xcode toolchain, mã nguồn Swift được viết và tổ chức theo tiêu chuẩn Swift Package Manager (`Package.swift`) và Xcode Project (`.xcodeproj`), nhưng các test case yêu cầu Xcode runner được đánh dấu trung thực là `Blocked (No macOS/Xcode toolchain on Windows host)` theo đúng quy tắc kiểm thử của đề bài. Toàn bộ logic lõi được kiểm chứng chéo 100% bằng Golden Vectors trên Android JVM và Node.js test harness.

## 2. Luật chơi & Cơ chế Trạm
- **ASM-005 (Độ phân giải và tỷ lệ khung hình chuẩn):** Thiết kế dựa trên tỷ lệ chuẩn 16:9 / 19.5:9 với Safe Area tự động điều chỉnh. Bố cục nhân vật nằm ở trung tâm - trái (tùy trạm), bảng công cụ bên phải hoặc dưới đáy.
- **ASM-006 (Cơ chế tính điểm từng trạm - qualityScore):**
  - *Spa làm sạch:* Tỷ lệ % diện tích vết bẩn đã được chà sạch (0.0 đến 1.0, tương ứng 0% đến 100%).
  - *Trang điểm:* Tỷ lệ % nét vẽ/tô nằm trúng vùng cho phép (mask) và không bị lem (0.0 đến 1.0). Nét vẽ ngoài mask bị phạt trừ tỷ lệ.
  - *Bánh kẹo:* Đạt yêu cầu số tầng bánh và tối thiểu số lượng topping hợp lệ (tính theo tỷ lệ hoàn thành mục tiêu bánh).
  - *Các trạm khác (Tóc, Thay đồ, Thú cưng, Chụp ảnh):* Hoàn thành thao tác tương tác bắt buộc đạt điểm chất lượng 1.0.
- **ASM-007 (Cổng phụ huynh - Parental Gate):**
  - Yêu cầu người chơi nhấn và giữ nút xác nhận trong liên tục **3.0 giây** (có vòng tròn tiến trình hiển thị).
  - Sau khi giữ đủ 3 giây, hiển thị một câu hỏi toán học ngẫu nhiên: cộng 2 số từ 1 đến 9 (ví dụ: `4 + 8 = ?`), kèm 3 lựa chọn trắc nghiệm. Trả lời đúng mới mở khóa hành động nhạy cảm.

## 3. Kinh tế & Mở khoá
- **ASM-008 (Kinh tế kẹo):**
  - Người chơi khởi đầu với **50 kẹo**.
  - Mỗi sao mới đạt được lần đầu tiên (1 sao mới = +10 kẹo, tối đa 3 sao = +30 kẹo cho lần đầu đạt 3 sao). Chơi lại mà không tăng thêm sao nào thì nhận 0 kẹo.
  - Giá các món đồ khoá dao động từ 15 đến 50 kẹo.
  - Tỷ lệ đồ khoá trong mỗi danh mục được giới hạn nghiêm ngặt ở mức **tối đa 25%** (ít nhất 75% đồ là miễn phí ngay từ đầu).
  - Không có món đồ nào "chỉ bán bằng tiền thật"; mọi đồ khoá đều có thể mở bằng kẹo trong game.
- **ASM-009 (IAP Giả lập):**
  - Gói `premium_levels`: Mở khoá các level 11 đến 20 và vĩnh viễn tắt quảng cáo xen kẽ.
  - Gói `remove_ads`: Vĩnh viễn tắt quảng cáo xen kẽ.

## 4. Quảng cáo giả lập (AdPolicy)
- **ASM-010 (Điều kiện kích hoạt quảng cáo):**
  - Chỉ kích hoạt khi người chơi rời khỏi màn chơi và trở về màn chọn level (`LevelSelect`).
  - Phải cách lần xem quảng cáo trước đó ít nhất **180 giây** (cooldown).
  - Không bao giờ hiển thị ở Level 1 và Level 2.
  - Hiển thị tối đa 1 lần mỗi 2 level vượt qua.
  - Tuyệt đối không chèn quảng cáo giữa chừng khi đang ở trong trạm hoặc khi đang ở màn chụp ảnh tiệc.
  - Bị tắt hoàn toàn nếu người chơi sở hữu `premium_levels` hoặc `remove_ads`.

## 5. Lưu trữ & Thư viện
- **ASM-011 (Lưu trữ ảnh):**
  - Tối đa **50 ảnh** trong thư viện nội bộ của game.
  - Khi thư viện đạt 50 ảnh, hệ thống hiển thị thông báo rõ ràng "Thư viện đã đầy (50/50). Hãy xoá bớt ảnh cũ để chụp thêm!" và nút chụp bị khoá cho đến khi người chơi xoá bớt ảnh.
  - File lưu tiến độ game sử dụng tên `save_game.json` với file sao lưu `save_game.bak`. Khi file chính bị lỗi giải mã hoặc hỏng dữ liệu, hệ thống tự động phục hồi từ file `.bak`.
