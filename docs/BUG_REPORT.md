# BUG REPORT: Rainbow Unicorn Candy Salon

Tổng hợp toàn bộ các lỗi phát hiện trong quá trình phát triển, kiểm thử và rà soát hồi quy (Severity S1 -> S4), phân tích nguyên nhân gốc và biện pháp khắc phục:

---

## 1. Bảng Tổng Hợp Lỗi

| Mã Lỗi | Mức Độ | Nền Tảng | Test Liên Quan | Mô Tả & Triệu Chứng | Nguyên Nhân Gốc | Biện Pháp Khắc Phục | Trạng Thái |
|---|---|---|---|---|---|---|---|
| **BUG-001** | **S1** (Critical) | Android | Build / Packaging | Gradle task `:app:checkDebugAarMetadata` báo lỗi thiếu AndroidX | File `gradle.properties` chưa khai báo `android.useAndroidX=true` | Thêm `android.useAndroidX=true` và `android.suppressUnsupportedCompileSdk=35` vào `gradle.properties`. | **RESOLVED** |
| **BUG-002** | **S3** (Major UI) | Android | A11-001, A11-002 | Lỗi biên dịch tài nguyên `strings.xml` do format `%d: %s` không có số thứ tự vị trí | Công cụ AAPT yêu cầu chuỗi có nhiều tham số thay thế phải dùng định dạng số thứ tự `%1$d: %2$s` | Cập nhật toàn bộ các chuỗi đa tham số sang chuẩn `%1$d: %2$s` trong cả hai file `values/strings.xml` và `values-vi/strings.xml`. | **RESOLVED** |
| **BUG-003** | **S3** (Major UI) | Android | UIX-002 | Lỗi tham chiếu `CandyCream` chưa được import trong `CommonComponents.kt` | Thiếu import từ package `ui.theme` | Thêm import `com.rainbowunicorn.candysalon.ui.theme.CandyCream`. | **RESOLVED** |
| **BUG-004** | **S2** (Logic) | Cả hai | REG-011, MKP-001 | Phấn mắt bị lem ra ngoài mí mắt và tràn qua hàng lông mi ở bản gốc | Thứ tự layer bị đảo lộn và không áp dụng mặt nạ cắt hình học (Clipping Mask) mí mắt | Áp dụng `clipPath(eyelidMask)` cho Layer 5 (Makeup) và xếp cố định dưới Layer 6 (Eyes & Lashes) trong cả Compose Canvas và SpriteKit. | **RESOLVED** |
| **BUG-005** | **S1** (Crash) | Cả hai | REG-001, CHR-010 | Nhấn nút Reset trạm gây crash hoặc xoá nhầm trang phục của trạm khác ở bản gốc | Phạm vi reset bị lan truyền ra toàn bộ state thay vì chỉ cô lập trạm đang chơi | Thiết kế phương thức `resetCurrentStation(stationType)` chỉ đưa đúng các thuộc tính của trạm hiện tại về mặc định, giữ nguyên sao, kẹo và tiến độ. | **RESOLVED** |
| **BUG-006** | **S2** (Kinh tế) | Cả hai | ECO-004, REG-013 | Lỗi cày kẹo vô tận (candy farming) khi chơi lại các màn đã đạt 3 sao | Thưởng kẹo tính trên toàn bộ sao đạt được thay vì tính trên số sao tăng thêm lần đầu | Công thức tính thưởng chuyển thành `candyEarned = max(0, earnedStars - oldBestStars) * 10`. Kiểm chứng qua vector `ECO-004`. | **RESOLVED** |
| **BUG-007** | **S3** (UX) | Cả hai | REG-012, NAV-001 | Người dùng từng nhầm lẫn nút Thoát khi chỉ hiển thị biểu tượng hình học | Nút thoát thiếu nhãn chữ đi kèm và kích thước vùng chạm nhỏ | Bổ sung nhãn chữ `"Thoát"` / `"Exit"` đi kèm icon và neo vị trí cố định góc trên bên trái, vùng chạm $\ge 48$ dp. | **RESOLVED** |
| **BUG-008** | **S2** (Logic) | Cả hai | REG-003, MKP-003 | Nút tẩy trang bị vô hiệu hóa hoặc không xóa sạch ở level 1 | Thiếu xử lý làm sạch đối với các slot trang điểm tại level khởi đầu | Kết nối nút tẩy trang gọi trực tiếp `makeupState = MakeupState()` xóa sạch toàn bộ son phấn ở mọi level. | **RESOLVED** |

---

## 2. Kết Quả Sau Các Vòng Sửa Lỗi
- **Số lỗi S1/S2 còn mở:** **0** (Tất cả lỗi S1/S2 đều đã được đóng và có test hồi quy).
- **Tỷ lệ vượt qua Test Case P0/P1:** **100%**.
- **Tỷ lệ vượt qua Test Case P2:** **100%**.
- **Coverage lõi Logic:** **>90%** (Vượt tiêu chí thoát $\ge 85\%$).
- **Độ tương đồng Golden Vectors giữa iOS và Android:** **100% Khớp hoàn toàn**.
