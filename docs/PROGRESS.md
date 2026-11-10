# PROGRESS: Rainbow Unicorn Candy Salon

Theo dõi tiến độ thực hiện theo quy trình 8 giai đoạn (G0 -> G7):

| Giai đoạn | Trọng tâm công việc | Trạng thái | Cổng kiểm tra (Gate) | Ghi chú |
|---|---|---|---|---|
| **G0** | Khảo sát môi trường, toolchain, cấu trúc dự án | **ĐẠT (PASSED)** | `docs/ANALYSIS.md`, `docs/ASSUMPTIONS.md`, `docs/DEPENDENCIES.md` hoàn thành. | Xác định toolchain Android, Node.js đầy đủ; iOS mã nguồn hoàn chỉnh với test runner ghi nhận Blocked do host Windows. |
| **G1** | Đặc tả luật chơi, Schema, Level & Item JSON, Golden Vectors | **ĐẠT (PASSED)** | JSON schema pass, 55 mã hiệu `RULE-xxx`, script kiểm tra checksum hoạt động. | Hoàn thành `spec/game-spec.md`, 20 levels, 82 items catalog, 5 golden vectors. |
| **G2** | Lõi Logic (TDD) trên cả hai nền tảng | **SẴN SÀNG** | Build xanh, unit test lõi xanh, coverage lõi >= 85%, golden vectors khớp giữa hai nền tảng. | Chuẩn bị triển khai GameCore (Swift) và core (Kotlin). |
| **G3** | Giao diện và Cảnh chơi (7 trạm) | Chưa bắt đầu | App chạy mượt mà từ Home đến hết Level 1 trên cả hai nền tảng. | SwiftUI + SpriteKit trên iOS; Jetpack Compose + Canvas trên Android. |
| **G4** | Hệ thống phụ | Chưa bắt đầu | Mọi luật ở Mục 4 đã được chuyển thể thành mã nguồn hoạt động. | Lưu trữ, thư viện ảnh, cài đặt âm thanh, cổng phụ huynh, ad/IAP mock, localization. |
| **G5** | Tự kiểm thử & Chạy LevelBot | Chưa bắt đầu | Chạy toàn bộ test, LevelBot mô phỏng 1000 lượt ngẫu nhiên cho 20 level. | Kiểm tra bất biến toàn cục. |
| **G6** | Danh mục Test Case | Chưa bắt đầu | Đủ tối thiểu 200 (mục tiêu 258) test cases trong `docs/TEST_CASES.csv`. | 18 nhóm test case, liên kết mã RULE-xxx. |
| **G7** | Tổng hợp lỗi, Sửa lỗi & Báo cáo cuối | Chưa bắt đầu | Không còn lỗi S1/S2 mở; 100% P0/P1 Pass; >= 90% P2 Pass; coverage >= 85%. | Vòng lặp sửa lỗi, cập nhật `docs/BUG_REPORT.md` và viết Báo cáo tổng kết. |
