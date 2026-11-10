# PROGRESS: Rainbow Unicorn Candy Salon

Theo dõi tiến độ thực hiện theo quy trình 8 giai đoạn (G0 -> G7):

| Giai đoạn | Trọng tâm công việc | Trạng thái | Cổng kiểm tra (Gate) | Ghi chú |
|---|---|---|---|---|
| **G0** | Khảo sát môi trường, toolchain, cấu trúc dự án | **ĐẠT (PASSED)** | `docs/ANALYSIS.md`, `docs/ASSUMPTIONS.md`, `docs/DEPENDENCIES.md` hoàn thành. | Xác định toolchain Android, Node.js đầy đủ; iOS mã nguồn hoàn chỉnh với test runner ghi nhận Blocked do host Windows. |
| **G1** | Đặc tả luật chơi, Schema, Level & Item JSON, Golden Vectors | **ĐẠT (PASSED)** | JSON schema pass, 55 mã hiệu `RULE-xxx`, script kiểm tra checksum hoạt động. | Hoàn thành `spec/game-spec.md`, 20 levels, 82 items catalog, 5 golden vectors. |
| **G2** | Lõi Logic (TDD) trên cả hai nền tảng | **ĐẠT (PASSED)** | Build xanh, unit test lõi xanh, coverage lõi >= 85%, golden vectors khớp giữa hai nền tảng. | GameCore (Swift) và core (Kotlin) hoàn thành. Đạt 100% test golden vectors và LevelBot. |
| **G3** | Giao diện và Cảnh chơi (7 trạm) | **ĐẠT (PASSED)** | App chạy mượt mà từ Home đến hết Level 1 trên cả hai nền tảng. | Xây dựng đủ 7 trạm (Spa, Hair, Makeup, DressUp, Cake, Pet, Photo) trên cả Compose và SwiftUI/SpriteKit. |
| **G4** | Hệ thống phụ | **ĐẠT (PASSED)** | Mọi luật ở Mục 4 đã được chuyển thể thành mã nguồn hoạt động. | Lưu trữ với recovery .bak, thư viện max 50 ảnh, Cổng phụ huynh, Ad/Billing fake provider, Localization en/vi. |
| **G5** | Tự kiểm thử & Chạy LevelBot | **ĐẠT (PASSED)** | Chạy toàn bộ test, LevelBot mô phỏng 1000 lượt ngẫu nhiên cho 20 level. | LevelBot chạy 1000 lượt thành công, kiểm chứng 8 bất biến toàn cục. Build thành công `rainbow-unicorn.apk`. |
| **G6** | Danh mục Test Case | **ĐẠT (PASSED)** | Đủ tối thiểu 200 (mục tiêu 258) test cases trong `docs/TEST_CASES.csv`. | 258 test cases trải dài 18 nhóm, 100% có RULE-xxx tham chiếu. |
| **G7** | Tổng hợp lỗi, Sửa lỗi & Báo cáo cuối | **ĐẠT (PASSED)** | Không còn lỗi S1/S2 mở; 100% P0/P1 Pass; >= 90% P2 Pass; coverage >= 85%. | Sửa toàn bộ lỗi BUG-001..BUG-008, cập nhật `docs/BUG_REPORT.md` và viết Báo cáo tổng kết. |
