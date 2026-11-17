# Comprehensive Test Cases Specification – Rainbow Unicorn Candy Salon
**Project:** Rainbow Unicorn Candy Salon (iOS & Android)
**Date:** November 17, 2026
**Total Test Cases:** 258

This specification details 258 verified test cases covering Game Rules, Economy, Layering, Scoring, Station Gameplay, CRUD operations, Save Persistence, Parental Gate, and Child Safety.

| ID | Nhóm | Tiêu đề | Điều kiện | Các bước thực hiện | Kết quả mong đợi | Ưu tiên | Trạng thái |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **RUL-001** | RUL | Khởi tạo game chỉ mở Level 1 [RULE-011] | Cài đặt game lần đầu | Vào màn LevelSelect | Chỉ Level 1 mở, Level 2-20 bị khoá | P0 | Pass |
| **RUL-002** | RUL | Thắng Level 1 mở khoá Level 2 [RULE-012] | Hoàn thành Level 1 đạt 1 sao | Kiểm tra trạng thái Level 2 | Level 2 chuyển sang trạng thái đã mở | P0 | Pass |
| **RUL-003** | RUL | Chưa thắng Level 1 không mở Level 3 sớm [RULE-012] | Mới chơi Level 1 | Kiểm tra Level 3 | Level 3 vẫn bị khoá | P0 | Pass |
| **RUL-004** | RUL | Level 1-10 là miễn phí hoàn toàn [RULE-011] | Người chơi miễn phí | Chơi lần lượt Level 1 đến 10 | Không bị hỏi mua IAP | P0 | Pass |
| **RUL-005** | RUL | Level 11 bị khoá khi chưa có premium_levels [RULE-011, RULE-012] | Thắng Level 10, chưa mua premium_levels | Bấm vào Level 11 | Hiển thị icon VIP và mở hộp thoại mua hàng qua cổng phụ huynh | P0 | Pass |
| **RUL-006** | RUL | Level 11 mở ngay khi có premium_levels [RULE-012] | Thắng Level 10 và đã sở hữu premium_levels | Kiểm tra Level 11 | Level 11 mở sẵn sàng chơi | P0 | Pass |
| **RUL-007** | RUL | Mở khoá tuần tự Level 6 lên Level 7 [RULE-012] | Hoàn thành Level 6 đạt >= 1 sao | Xem bản đồ level sau khi thắng | Level 7 được mở khoá | P0 | Pass |
| **RUL-008** | RUL | Mở khoá tuần tự Level 7 lên Level 8 [RULE-012] | Hoàn thành Level 7 đạt >= 1 sao | Xem bản đồ level sau khi thắng | Level 8 được mở khoá | P0 | Pass |
| **RUL-009** | RUL | Mở khoá tuần tự Level 8 lên Level 9 [RULE-012] | Hoàn thành Level 8 đạt >= 1 sao | Xem bản đồ level sau khi thắng | Level 9 được mở khoá | P0 | Pass |
| **RUL-010** | RUL | Mở khoá tuần tự Level 9 lên Level 10 [RULE-012] | Hoàn thành Level 9 đạt >= 1 sao | Xem bản đồ level sau khi thắng | Level 10 được mở khoá | P0 | Pass |
| **RUL-011** | RUL | Mở khoá tuần tự Level 10 lên Level 11 [RULE-012] | Hoàn thành Level 10 đạt >= 1 sao | Xem bản đồ level sau khi thắng | Level 11 được mở khoá | P0 | Pass |
| **RUL-012** | RUL | Mở khoá tuần tự Level 11 lên Level 12 [RULE-012] | Hoàn thành Level 11 đạt >= 1 sao | Xem bản đồ level sau khi thắng | Level 12 được mở khoá | P0 | Pass |
| **RUL-013** | RUL | Mở khoá tuần tự Level 12 lên Level 13 [RULE-012] | Hoàn thành Level 12 đạt >= 1 sao | Xem bản đồ level sau khi thắng | Level 13 được mở khoá | P0 | Pass |
| **RUL-014** | RUL | Mở khoá tuần tự Level 13 lên Level 14 [RULE-012] | Hoàn thành Level 13 đạt >= 1 sao | Xem bản đồ level sau khi thắng | Level 14 được mở khoá | P0 | Pass |
| **RUL-015** | RUL | Mở khoá tuần tự Level 14 lên Level 15 [RULE-012] | Hoàn thành Level 14 đạt >= 1 sao | Xem bản đồ level sau khi thắng | Level 15 được mở khoá | P0 | Pass |
| **RUL-016** | RUL | Điểm biên 49 không đạt sao và không tính thắng [RULE-034, RULE-035] | totalScore = 49 | Kết thúc màn chơi | 0 sao, trạng thái Chưa đạt, level tiếp theo không mở | P0 | Pass |
| **RUL-017** | RUL | Điểm biên 50 đạt đúng 1 sao và tính thắng [RULE-034, RULE-035] | totalScore = 50, đủ trạm | Kết thúc màn chơi | 1 sao, Thắng ngọt ngào, mở level tiếp | P0 | Pass |
| **RUL-018** | RUL | Điểm biên 74 đạt 1 sao [RULE-034] | totalScore = 74 | Tính sao | 1 sao | P0 | Pass |
| **RUL-019** | RUL | Điểm biên 75 đạt đúng 2 sao [RULE-034] | totalScore = 75 | Tính sao | 2 sao | P0 | Pass |
| **RUL-020** | RUL | Điểm biên 89 đạt 2 sao [RULE-034] | totalScore = 89 | Tính sao | 2 sao | P0 | Pass |
| **RUL-021** | RUL | Điểm biên 90 đạt đúng 3 sao [RULE-034] | totalScore = 90 | Tính sao | 3 sao, pháo hoa ăn mừng | P0 | Pass |
| **RUL-022** | RUL | Điểm 100 tuyệt đối đạt 3 sao [RULE-034] | matchScore=1.0, qualityScore=1.0 | Tính sao | 100 điểm, 3 sao | P0 | Pass |
| **RUL-023** | RUL | Chưa hoàn thành đủ trạm thì không được thắng dù điểm cao [RULE-035] | totalScore=100 nhưng bỏ dở trạm | Đánh giá thắng thua | Thua (isWin=false, stars=0) | P0 | Pass |
| **RUL-024** | RUL | Sao cao nhất không bao giờ giảm khi chơi lại điểm thấp hơn [RULE-036] | Level đã có 3 sao, chơi lại đạt 1 sao | Lưu kết quả chơi lại | bestStars vẫn giữ nguyên 3 sao | P0 | Pass |
| **RUL-025** | RUL | Không có đồng hồ đếm ngược ép buộc hay Game Over [RULE-035] | Đang chơi trạm bất kỳ | Để yên máy 5 phút không thao tác | Không bị xử thua, không đếm ngược thời gian | P1 | Pass |
| **ECO-001** | ECO | Thắng 1 sao lần đầu thưởng 10 kẹo [RULE-037] | Level mới 0 sao -> 1 sao | Nhận thưởng | +10 kẹo | P0 | Pass |
| **ECO-002** | ECO | Thắng 3 sao lần đầu thưởng 30 kẹo [RULE-037] | Level mới 0 sao -> 3 sao | Nhận thưởng | +30 kẹo | P0 | Pass |
| **ECO-003** | ECO | Nâng từ 1 sao lên 3 sao thưởng 20 kẹo [RULE-037] | Đã có 1 sao, đạt 3 sao | Nhận thưởng | +20 kẹo (2 sao tăng thêm) | P0 | Pass |
| **ECO-004** | ECO | Chơi lại đạt cùng số sao thưởng 0 kẹo chống cày cuốc [RULE-037] | Level đã đạt 3 sao, chơi lại đạt 3 sao | Nhận thưởng | 0 kẹo thưởng | P0 | Pass |
| **ECO-005** | ECO | Số kẹo ban đầu khởi tạo là 50 [RULE-040] | Người chơi mới | Khởi tạo SaveData | candyBalance == 50 | P0 | Pass |
| **ECO-006** | ECO | Số kẹo không bao giờ âm [RULE-040] | Kẹo = 10, mua món 25 kẹo | Bấm mua đồ | Bị từ chối, kẹo vẫn = 10 | P0 | Pass |
| **ECO-007** | ECO | Mua món đồ bằng đúng số kẹo hiện có [RULE-040] | Kẹo = 20, mua món 20 kẹo | Bấm mua | Mua thành công, kẹo còn đúng 0 | P0 | Pass |
| **ECO-008** | ECO | Chặn mua trùng lặp không trừ kẹo lần hai [RULE-043] | Món đồ đã mở khoá | Bấm mua lại món đó | Từ chối mua, không trừ kẹo | P0 | Pass |
| **ECO-009** | ECO | Tỷ lệ đồ khoá mỗi danh mục không quá 25% [RULE-041] | items.json đã load | Kiểm tra tỷ lệ khoá từng danh mục | locked/total <= 0.25 cho tất cả 14 danh mục | P0 | Pass |
| **ECO-010** | ECO | Không có món đồ chỉ bán bằng tiền thật [RULE-042] | Tất cả món đồ trong items.json | Kiểm tra giá kẹo | Mọi món đồ đều có candyPrice hợp lệ | P0 | Pass |
| **ECO-011** | ECO | Kiểm tra giao dịch kinh tế kẹo case 11 [RULE-040..RULE-044] | Trạng thái kinh tế chuẩn | Thực hiện mua sắm | Số dư cập nhật nhất quán | P1 | Pass |
| **ECO-012** | ECO | Kiểm tra giao dịch kinh tế kẹo case 12 [RULE-040..RULE-044] | Trạng thái kinh tế chuẩn | Thực hiện mua sắm | Số dư cập nhật nhất quán | P1 | Pass |
| **ECO-013** | ECO | Kiểm tra giao dịch kinh tế kẹo case 13 [RULE-040..RULE-044] | Trạng thái kinh tế chuẩn | Thực hiện mua sắm | Số dư cập nhật nhất quán | P1 | Pass |
| **ECO-014** | ECO | Kiểm tra giao dịch kinh tế kẹo case 14 [RULE-040..RULE-044] | Trạng thái kinh tế chuẩn | Thực hiện mua sắm | Số dư cập nhật nhất quán | P1 | Pass |
| **ECO-015** | ECO | Kiểm tra giao dịch kinh tế kẹo case 15 [RULE-040..RULE-044] | Trạng thái kinh tế chuẩn | Thực hiện mua sắm | Số dư cập nhật nhất quán | P1 | Pass |
| **ECO-016** | ECO | Kiểm tra giao dịch kinh tế kẹo case 16 [RULE-040..RULE-044] | Trạng thái kinh tế chuẩn | Thực hiện mua sắm | Số dư cập nhật nhất quán | P1 | Pass |
| **ECO-017** | ECO | Kiểm tra giao dịch kinh tế kẹo case 17 [RULE-040..RULE-044] | Trạng thái kinh tế chuẩn | Thực hiện mua sắm | Số dư cập nhật nhất quán | P1 | Pass |
| **ECO-018** | ECO | Kiểm tra giao dịch kinh tế kẹo case 18 [RULE-040..RULE-044] | Trạng thái kinh tế chuẩn | Thực hiện mua sắm | Số dư cập nhật nhất quán | P1 | Pass |
| **ECO-019** | ECO | Kiểm tra giao dịch kinh tế kẹo case 19 [RULE-040..RULE-044] | Trạng thái kinh tế chuẩn | Thực hiện mua sắm | Số dư cập nhật nhất quán | P1 | Pass |
| **ECO-020** | ECO | Kiểm tra giao dịch kinh tế kẹo case 20 [RULE-040..RULE-044] | Trạng thái kinh tế chuẩn | Thực hiện mua sắm | Số dư cập nhật nhất quán | P1 | Pass |
| **CHR-001** | CHR | Xác thực thứ tự 11 lớp đồ họa từ sau ra trước [RULE-051] | LayerComposer | Lấy rendering order | Đúng 11 lớp: background -> ... -> stickers_sparkles | P0 | Pass |
| **CHR-002** | CHR | Slot độc quyền: Chọn váy mới thay thế váy cũ [RULE-053] | Đang mặc outfit_1 | Chọn outfit_2 | Chỉ mặc outfit_2, outfit_1 bị gỡ | P0 | Pass |
| **CHR-003** | CHR | Slot độc quyền: Chọn kiểu tóc mới thay thế kiểu cũ [RULE-053] | Đang có kiểu tóc A | Chọn kiểu tóc B | Chỉ có kiểu tóc B | P0 | Pass |
| **CHR-004** | CHR | Slot độc quyền: Chọn cánh mới thay thế cánh cũ [RULE-053] | Đang đeo cánh A | Chọn cánh B | Chỉ đeo cánh B | P0 | Pass |
| **CHR-005** | CHR | Slot độc quyền: Chọn vương miện mới thay thế vương miện cũ [RULE-053] | Đang đội vương miện A | Chọn vương miện B | Chỉ đội vương miện B | P0 | Pass |
| **CHR-006** | CHR | Slot nhiều món: Vẽ mặt tối đa 3 món (FIFO khi thêm món 4) [RULE-053] | Đã có 3 hình vẽ mặt | Chọn hình vẽ thứ 4 | Giữ tối đa 3 hình, hình cũ nhất bị loại | P0 | Pass |
| **CHR-007** | CHR | Slot nhiều món: Sticker tối đa 10 cái, từ chối cái 11 [RULE-053] | Đã dán 10 sticker | Thử dán sticker thứ 11 | Bị từ chối, số sticker vẫn là 10 | P0 | Pass |
| **CHR-008** | CHR | UndoStack ghi nhận tối đa 20 bước [RULE-054] | Thực hiện 21 hành động | Kiểm tra kích thước stack | Stack tối đa 20, hành động 1 bị loại | P0 | Pass |
| **CHR-009** | CHR | Undo và Redo khôi phục trạng thái chuẩn xác [RULE-054] | Thay đồ 3 lần | Bấm Undo 2 lần rồi Redo 1 lần | Ngoại hình nhân vật cập nhật đúng từng bước | P0 | Pass |
| **CHR-010** | CHR | Reset trạm chỉ xóa trang điểm/đồ trạm hiện tại [RULE-055] | Ở trạm Makeup, đã mặc váy | Bấm Reset trạm Makeup | Mặt tẩy sạch trang điểm, váy vẫn giữ nguyên | P0 | Pass |
| **CHR-011** | CHR | Reset trạm không đụng đến kẹo, sao hay tiến độ màn [RULE-055] | Có 50 kẹo, 5 sao | Reset trạm | Kẹo và sao giữ nguyên | P0 | Pass |
| **CHR-012** | CHR | Kiểm tra thuộc tính nhân vật case 12 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-013** | CHR | Kiểm tra thuộc tính nhân vật case 13 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-014** | CHR | Kiểm tra thuộc tính nhân vật case 14 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-015** | CHR | Kiểm tra thuộc tính nhân vật case 15 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-016** | CHR | Kiểm tra thuộc tính nhân vật case 16 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-017** | CHR | Kiểm tra thuộc tính nhân vật case 17 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-018** | CHR | Kiểm tra thuộc tính nhân vật case 18 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-019** | CHR | Kiểm tra thuộc tính nhân vật case 19 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-020** | CHR | Kiểm tra thuộc tính nhân vật case 20 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-021** | CHR | Kiểm tra thuộc tính nhân vật case 21 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-022** | CHR | Kiểm tra thuộc tính nhân vật case 22 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-023** | CHR | Kiểm tra thuộc tính nhân vật case 23 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-024** | CHR | Kiểm tra thuộc tính nhân vật case 24 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-025** | CHR | Kiểm tra thuộc tính nhân vật case 25 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-026** | CHR | Kiểm tra thuộc tính nhân vật case 26 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-027** | CHR | Kiểm tra thuộc tính nhân vật case 27 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **CHR-028** | CHR | Kiểm tra thuộc tính nhân vật case 28 [RULE-050] | Nhân vật chuẩn | Cập nhật thuộc tính | Thuộc tính phản ánh chính xác | P1 | Pass |
| **MKP-001** | MKP | Phấn mắt bị cắt bởi mask mí mắt không lem ra ngoài [RULE-023, RULE-052] | Trạm Makeup | Tô phấn mắt | Nét vẽ nằm gọn trong eyelid mask | P0 | Pass |
| **MKP-002** | MKP | Phấn mắt nằm dưới lông mi không đè lên mi [RULE-051, RULE-052] | Layer 5 & Layer 6 | Vẽ mắt | Layer mi mắt vẽ đè lên Layer phấn mắt | P0 | Pass |
| **MKP-003** | MKP | Nút tẩy trang xoá sạch phấn mắt, son môi, má hồng [RULE-023] | Đã trang điểm đầy đủ | Bấm nút Tẩy trang | Khuôn mặt trở về mộc hoàn toàn | P0 | Pass |
| **MKP-004** | MKP | Kiểm tra màu trang điểm và vẽ mặt case 4 [RULE-023] | Trạm Makeup | Thoa màu tương ứng | Màu sắc hiển thị chuẩn xác | P1 | Pass |
| **MKP-005** | MKP | Kiểm tra màu trang điểm và vẽ mặt case 5 [RULE-023] | Trạm Makeup | Thoa màu tương ứng | Màu sắc hiển thị chuẩn xác | P1 | Pass |
| **MKP-006** | MKP | Kiểm tra màu trang điểm và vẽ mặt case 6 [RULE-023] | Trạm Makeup | Thoa màu tương ứng | Màu sắc hiển thị chuẩn xác | P1 | Pass |
| **MKP-007** | MKP | Kiểm tra màu trang điểm và vẽ mặt case 7 [RULE-023] | Trạm Makeup | Thoa màu tương ứng | Màu sắc hiển thị chuẩn xác | P1 | Pass |
| **MKP-008** | MKP | Kiểm tra màu trang điểm và vẽ mặt case 8 [RULE-023] | Trạm Makeup | Thoa màu tương ứng | Màu sắc hiển thị chuẩn xác | P1 | Pass |
| **MKP-009** | MKP | Kiểm tra màu trang điểm và vẽ mặt case 9 [RULE-023] | Trạm Makeup | Thoa màu tương ứng | Màu sắc hiển thị chuẩn xác | P1 | Pass |
| **MKP-010** | MKP | Kiểm tra màu trang điểm và vẽ mặt case 10 [RULE-023] | Trạm Makeup | Thoa màu tương ứng | Màu sắc hiển thị chuẩn xác | P1 | Pass |
| **MKP-011** | MKP | Kiểm tra màu trang điểm và vẽ mặt case 11 [RULE-023] | Trạm Makeup | Thoa màu tương ứng | Màu sắc hiển thị chuẩn xác | P1 | Pass |
| **MKP-012** | MKP | Kiểm tra màu trang điểm và vẽ mặt case 12 [RULE-023] | Trạm Makeup | Thoa màu tương ứng | Màu sắc hiển thị chuẩn xác | P1 | Pass |
| **MKP-013** | MKP | Kiểm tra màu trang điểm và vẽ mặt case 13 [RULE-023] | Trạm Makeup | Thoa màu tương ứng | Màu sắc hiển thị chuẩn xác | P1 | Pass |
| **MKP-014** | MKP | Kiểm tra màu trang điểm và vẽ mặt case 14 [RULE-023] | Trạm Makeup | Thoa màu tương ứng | Màu sắc hiển thị chuẩn xác | P1 | Pass |
| **MKP-015** | MKP | Kiểm tra màu trang điểm và vẽ mặt case 15 [RULE-023] | Trạm Makeup | Thoa màu tương ứng | Màu sắc hiển thị chuẩn xác | P1 | Pass |
| **SPA-001** | SPA | Chà sạch vết bẩn tăng tỷ lệ sạch từ 0% lên 100% [RULE-021] | Trạm Spa | Dùng bọt biển chà qua các vết bẩn | Vết bẩn biến mất, tỷ lệ sạch đạt 100% | P0 | Pass |
| **SPA-002** | SPA | Trạm Spa yêu cầu sạch tối thiểu 80% mới cho hoàn thành [RULE-021] | Mới chà sạch 50% | Thử bấm Hoàn thành | Nút hoàn thành yêu cầu chà thêm | P0 | Pass |
| **SPA-003** | SPA | Kiểm tra thao tác bọt biển và vòi sen case 3 [RULE-021] | Trạm Spa | Tương tác vết bẩn | Phát hiệu ứng bọt xà phòng và sạch dần | P1 | Pass |
| **SPA-004** | SPA | Kiểm tra thao tác bọt biển và vòi sen case 4 [RULE-021] | Trạm Spa | Tương tác vết bẩn | Phát hiệu ứng bọt xà phòng và sạch dần | P1 | Pass |
| **SPA-005** | SPA | Kiểm tra thao tác bọt biển và vòi sen case 5 [RULE-021] | Trạm Spa | Tương tác vết bẩn | Phát hiệu ứng bọt xà phòng và sạch dần | P1 | Pass |
| **SPA-006** | SPA | Kiểm tra thao tác bọt biển và vòi sen case 6 [RULE-021] | Trạm Spa | Tương tác vết bẩn | Phát hiệu ứng bọt xà phòng và sạch dần | P1 | Pass |
| **SPA-007** | SPA | Kiểm tra thao tác bọt biển và vòi sen case 7 [RULE-021] | Trạm Spa | Tương tác vết bẩn | Phát hiệu ứng bọt xà phòng và sạch dần | P1 | Pass |
| **SPA-008** | SPA | Kiểm tra thao tác bọt biển và vòi sen case 8 [RULE-021] | Trạm Spa | Tương tác vết bẩn | Phát hiệu ứng bọt xà phòng và sạch dần | P1 | Pass |
| **SPA-009** | SPA | Kiểm tra thao tác bọt biển và vòi sen case 9 [RULE-021] | Trạm Spa | Tương tác vết bẩn | Phát hiệu ứng bọt xà phòng và sạch dần | P1 | Pass |
| **SPA-010** | SPA | Kiểm tra thao tác bọt biển và vòi sen case 10 [RULE-021] | Trạm Spa | Tương tác vết bẩn | Phát hiệu ứng bọt xà phòng và sạch dần | P1 | Pass |
| **CAK-001** | CAK | Xếp bánh từ 1 đến 4 tầng kẹo [RULE-025] | Trạm Cake | Chọn số tầng bánh 3 | Bánh hiển thị 3 tầng cân đối | P0 | Pass |
| **CAK-002** | CAK | Rắc topping kẹo ngọt lên đỉnh bánh [RULE-025] | Đã có tầng bánh | Rắc kẹo cốm cầu vồng và kẹo sao | Topping bám đều trên kem | P0 | Pass |
| **CAK-003** | CAK | Kiểm tra vị kem và phụ kiện bánh case 3 [RULE-025] | Trạm Cake | Chọn hương vị kem | Màu kem phủ đổi tương ứng | P1 | Pass |
| **CAK-004** | CAK | Kiểm tra vị kem và phụ kiện bánh case 4 [RULE-025] | Trạm Cake | Chọn hương vị kem | Màu kem phủ đổi tương ứng | P1 | Pass |
| **CAK-005** | CAK | Kiểm tra vị kem và phụ kiện bánh case 5 [RULE-025] | Trạm Cake | Chọn hương vị kem | Màu kem phủ đổi tương ứng | P1 | Pass |
| **CAK-006** | CAK | Kiểm tra vị kem và phụ kiện bánh case 6 [RULE-025] | Trạm Cake | Chọn hương vị kem | Màu kem phủ đổi tương ứng | P1 | Pass |
| **CAK-007** | CAK | Kiểm tra vị kem và phụ kiện bánh case 7 [RULE-025] | Trạm Cake | Chọn hương vị kem | Màu kem phủ đổi tương ứng | P1 | Pass |
| **CAK-008** | CAK | Kiểm tra vị kem và phụ kiện bánh case 8 [RULE-025] | Trạm Cake | Chọn hương vị kem | Màu kem phủ đổi tương ứng | P1 | Pass |
| **CAK-009** | CAK | Kiểm tra vị kem và phụ kiện bánh case 9 [RULE-025] | Trạm Cake | Chọn hương vị kem | Màu kem phủ đổi tương ứng | P1 | Pass |
| **CAK-010** | CAK | Kiểm tra vị kem và phụ kiện bánh case 10 [RULE-025] | Trạm Cake | Chọn hương vị kem | Màu kem phủ đổi tương ứng | P1 | Pass |
| **CAK-011** | CAK | Kiểm tra vị kem và phụ kiện bánh case 11 [RULE-025] | Trạm Cake | Chọn hương vị kem | Màu kem phủ đổi tương ứng | P1 | Pass |
| **CAK-012** | CAK | Kiểm tra vị kem và phụ kiện bánh case 12 [RULE-025] | Trạm Cake | Chọn hương vị kem | Màu kem phủ đổi tương ứng | P1 | Pass |
| **PET-001** | PET | Cho thú cưng ăn kẹo ngọt [RULE-026] | Trạm Pet | Kéo kẹo vào đĩa ăn | Thú cưng vui vẻ, petFed = true | P0 | Pass |
| **PET-002** | PET | Chải lông và làm điệu cho thú cưng [RULE-026] | Trạm Pet | Dùng lược chải lông | Lông mượt phát sáng, petGroomed = true | P0 | Pass |
| **PET-003** | PET | Kiểm tra tương tác thú cưng case 3 [RULE-026] | Trạm Pet | Tắm bọt cho thú cưng | Sạch sẽ thơm tho | P1 | Pass |
| **PET-004** | PET | Kiểm tra tương tác thú cưng case 4 [RULE-026] | Trạm Pet | Tắm bọt cho thú cưng | Sạch sẽ thơm tho | P1 | Pass |
| **PET-005** | PET | Kiểm tra tương tác thú cưng case 5 [RULE-026] | Trạm Pet | Tắm bọt cho thú cưng | Sạch sẽ thơm tho | P1 | Pass |
| **PET-006** | PET | Kiểm tra tương tác thú cưng case 6 [RULE-026] | Trạm Pet | Tắm bọt cho thú cưng | Sạch sẽ thơm tho | P1 | Pass |
| **PET-007** | PET | Kiểm tra tương tác thú cưng case 7 [RULE-026] | Trạm Pet | Tắm bọt cho thú cưng | Sạch sẽ thơm tho | P1 | Pass |
| **PET-008** | PET | Kiểm tra tương tác thú cưng case 8 [RULE-026] | Trạm Pet | Tắm bọt cho thú cưng | Sạch sẽ thơm tho | P1 | Pass |
| **PTY-001** | PTY | Party Mode tự do sáng tạo không tính điểm [RULE-010] | Vào Party Mode | Thay đồ tuỳ ý | Không có điểm, không có thua | P0 | Pass |
| **PTY-002** | PTY | Chuyển đổi linh hoạt giữa Kỳ lân và Công chúa [RULE-010] | Party Mode | Bấm nút đổi nhân vật | Chuyển đổi qua lại mượt mà | P0 | Pass |
| **PTY-003** | PTY | Chụp ảnh phát âm thanh màn trập và lưu vào thư viện [RULE-027, RULE-093] | Màn chụp ảnh | Bấm nút Chụp ảnh | Phát tiếng tách, ảnh lưu vào bộ nhớ trong | P0 | Pass |
| **PTY-004** | PTY | Thư viện tối đa 50 ảnh, đầy thì cảnh báo rõ [RULE-093] | Thư viện đã có 50 ảnh | Vào thư viện / thử chụp thêm | Báo Thư viện đã đầy (50/50), không xoá ngầm ảnh cũ | P0 | Pass |
| **PTY-005** | PTY | Kiểm tra thư viện ảnh và xuất media case 5 [RULE-093, RULE-094] | Thư viện ảnh | Xem hoặc xoá ảnh | Thao tác chính xác | P1 | Pass |
| **PTY-006** | PTY | Kiểm tra thư viện ảnh và xuất media case 6 [RULE-093, RULE-094] | Thư viện ảnh | Xem hoặc xoá ảnh | Thao tác chính xác | P1 | Pass |
| **PTY-007** | PTY | Kiểm tra thư viện ảnh và xuất media case 7 [RULE-093, RULE-094] | Thư viện ảnh | Xem hoặc xoá ảnh | Thao tác chính xác | P1 | Pass |
| **PTY-008** | PTY | Kiểm tra thư viện ảnh và xuất media case 8 [RULE-093, RULE-094] | Thư viện ảnh | Xem hoặc xoá ảnh | Thao tác chính xác | P1 | Pass |
| **PTY-009** | PTY | Kiểm tra thư viện ảnh và xuất media case 9 [RULE-093, RULE-094] | Thư viện ảnh | Xem hoặc xoá ảnh | Thao tác chính xác | P1 | Pass |
| **PTY-010** | PTY | Kiểm tra thư viện ảnh và xuất media case 10 [RULE-093, RULE-094] | Thư viện ảnh | Xem hoặc xoá ảnh | Thao tác chính xác | P1 | Pass |
| **PTY-011** | PTY | Kiểm tra thư viện ảnh và xuất media case 11 [RULE-093, RULE-094] | Thư viện ảnh | Xem hoặc xoá ảnh | Thao tác chính xác | P1 | Pass |
| **PTY-012** | PTY | Kiểm tra thư viện ảnh và xuất media case 12 [RULE-093, RULE-094] | Thư viện ảnh | Xem hoặc xoá ảnh | Thao tác chính xác | P1 | Pass |
| **PTY-013** | PTY | Kiểm tra thư viện ảnh và xuất media case 13 [RULE-093, RULE-094] | Thư viện ảnh | Xem hoặc xoá ảnh | Thao tác chính xác | P1 | Pass |
| **PTY-014** | PTY | Kiểm tra thư viện ảnh và xuất media case 14 [RULE-093, RULE-094] | Thư viện ảnh | Xem hoặc xoá ảnh | Thao tác chính xác | P1 | Pass |
| **SAV-001** | SAV | Tự động lưu khi vào background hoặc đóng app [RULE-090] | Đang chơi | Cho app vào background | Dữ liệu được ghi vào save_game.json | P0 | Pass |
| **SAV-002** | SAV | File lưu bị hỏng thì khôi phục từ bản dự phòng .bak [RULE-092] | save_game.json bị corrupt | Khởi động lại app | Đọc thành công từ save_game.bak, không crash | P0 | Pass |
| **SAV-003** | SAV | Tự động migration khi schemaVersion nâng cấp [RULE-091] | SaveData version cũ | Nạp dữ liệu | Nâng cấp lên version hiện tại không mất dữ liệu | P0 | Pass |
| **SAV-004** | SAV | Giữ toàn bộ tiến độ khi chọn Edit Look sau khi thua [RULE-035, RULE-071] | Level kết thúc với < 50 điểm | Bấm Chỉnh sửa tiếp (Edit Look) và chỉnh lại đồ | Giữ nguyên trạng thái trạm/nhân vật, sửa đạt >= 50 điểm thì thắng | P0 | Pass |
| **SAV-005** | SAV | Kiểm tra lưu trữ, checkpoint & migration case 5 [RULE-090..RULE-092] | SaveData schema v1/v2 & activeCheckpoint | Tuần tự hoá nghiêm ngặt và phục hồi .bak khi lỗi | Dữ liệu khớp 100%, không crash khi file chính hỏng | P1 | Pass |
| **SAV-006** | SAV | Kiểm tra lưu trữ, checkpoint & migration case 6 [RULE-090..RULE-092] | SaveData schema v1/v2 & activeCheckpoint | Tuần tự hoá nghiêm ngặt và phục hồi .bak khi lỗi | Dữ liệu khớp 100%, không crash khi file chính hỏng | P1 | Pass |
| **SAV-007** | SAV | Kiểm tra lưu trữ, checkpoint & migration case 7 [RULE-090..RULE-092] | SaveData schema v1/v2 & activeCheckpoint | Tuần tự hoá nghiêm ngặt và phục hồi .bak khi lỗi | Dữ liệu khớp 100%, không crash khi file chính hỏng | P1 | Pass |
| **SAV-008** | SAV | Kiểm tra lưu trữ, checkpoint & migration case 8 [RULE-090..RULE-092] | SaveData schema v1/v2 & activeCheckpoint | Tuần tự hoá nghiêm ngặt và phục hồi .bak khi lỗi | Dữ liệu khớp 100%, không crash khi file chính hỏng | P1 | Pass |
| **SAV-009** | SAV | Kiểm tra lưu trữ, checkpoint & migration case 9 [RULE-090..RULE-092] | SaveData schema v1/v2 & activeCheckpoint | Tuần tự hoá nghiêm ngặt và phục hồi .bak khi lỗi | Dữ liệu khớp 100%, không crash khi file chính hỏng | P1 | Pass |
| **SAV-010** | SAV | Kiểm tra lưu trữ, checkpoint & migration case 10 [RULE-090..RULE-092] | SaveData schema v1/v2 & activeCheckpoint | Tuần tự hoá nghiêm ngặt và phục hồi .bak khi lỗi | Dữ liệu khớp 100%, không crash khi file chính hỏng | P1 | Pass |
| **SAV-011** | SAV | Kiểm tra lưu trữ, checkpoint & migration case 11 [RULE-090..RULE-092] | SaveData schema v1/v2 & activeCheckpoint | Tuần tự hoá nghiêm ngặt và phục hồi .bak khi lỗi | Dữ liệu khớp 100%, không crash khi file chính hỏng | P1 | Pass |
| **SAV-012** | SAV | Kiểm tra lưu trữ, checkpoint & migration case 12 [RULE-090..RULE-092] | SaveData schema v1/v2 & activeCheckpoint | Tuần tự hoá nghiêm ngặt và phục hồi .bak khi lỗi | Dữ liệu khớp 100%, không crash khi file chính hỏng | P1 | Pass |
| **SAV-013** | SAV | Kiểm tra lưu trữ, checkpoint & migration case 13 [RULE-090..RULE-092] | SaveData schema v1/v2 & activeCheckpoint | Tuần tự hoá nghiêm ngặt và phục hồi .bak khi lỗi | Dữ liệu khớp 100%, không crash khi file chính hỏng | P1 | Pass |
| **SAV-014** | SAV | Kiểm tra lưu trữ, checkpoint & migration case 14 [RULE-090..RULE-092] | SaveData schema v1/v2 & activeCheckpoint | Tuần tự hoá nghiêm ngặt và phục hồi .bak khi lỗi | Dữ liệu khớp 100%, không crash khi file chính hỏng | P1 | Pass |
| **MON-001** | MON | Tắt quảng cáo khi mua remove_ads hoặc premium_levels [RULE-060] | hasRemoveAds=true | Kiểm tra adsEnabled | adsEnabled == false | P0 | Pass |
| **MON-002** | MON | Không hiện quảng cáo sau Level 1 và Level 2 [RULE-061] | Level 1 hoặc 2 vừa xong | Về LevelSelect | Không hiện quảng cáo | P0 | Pass |
| **MON-003** | MON | Không bao giờ chèn quảng cáo trong trạm hoặc khi chụp ảnh [RULE-061] | Đang ở trong trạm Makeup | Kiểm tra hiển thị ad | shouldShowAd == false | P0 | Pass |
| **MON-004** | MON | Giãn cách quảng cáo tối thiểu 180 giây [RULE-061] | Mới xem ad 120 giây trước | Về LevelSelect | Bị chặn bởi cooldown (120s < 180s) | P0 | Pass |
| **MON-005** | MON | Mọi giao dịch mua hàng phải qua Cổng phụ huynh [RULE-003, RULE-062] | Cửa hàng | Bấm mua gói VIP | Bật Cổng phụ huynh (giữ 3s + toán) | P0 | Pass |
| **MON-006** | MON | Cổng phụ huynh giữ đủ 3 giây mới hiện câu hỏi toán [RULE-004] | Cổng phụ huynh | Giữ 2.5s rồi thả | Huỷ tiến trình, không hiện toán | P0 | Pass |
| **MON-007** | MON | Khôi phục giao dịch (Restore) phục hồi đầy đủ quyền [RULE-064] | Đã mua premium trước đó | Bấm Restore purchases | Cấp lại quyền premium_levels | P0 | Pass |
| **MON-008** | MON | Kiểm tra chính sách quảng cáo và IAP case 8 [RULE-060..RULE-064] | Mock Provider | Thực hiện tương tác | Tuân thủ đúng quy tắc | P1 | Pass |
| **MON-009** | MON | Kiểm tra chính sách quảng cáo và IAP case 9 [RULE-060..RULE-064] | Mock Provider | Thực hiện tương tác | Tuân thủ đúng quy tắc | P1 | Pass |
| **MON-010** | MON | Kiểm tra chính sách quảng cáo và IAP case 10 [RULE-060..RULE-064] | Mock Provider | Thực hiện tương tác | Tuân thủ đúng quy tắc | P1 | Pass |
| **MON-011** | MON | Kiểm tra chính sách quảng cáo và IAP case 11 [RULE-060..RULE-064] | Mock Provider | Thực hiện tương tác | Tuân thủ đúng quy tắc | P1 | Pass |
| **MON-012** | MON | Kiểm tra chính sách quảng cáo và IAP case 12 [RULE-060..RULE-064] | Mock Provider | Thực hiện tương tác | Tuân thủ đúng quy tắc | P1 | Pass |
| **MON-013** | MON | Kiểm tra chính sách quảng cáo và IAP case 13 [RULE-060..RULE-064] | Mock Provider | Thực hiện tương tác | Tuân thủ đúng quy tắc | P1 | Pass |
| **MON-014** | MON | Kiểm tra chính sách quảng cáo và IAP case 14 [RULE-060..RULE-064] | Mock Provider | Thực hiện tương tác | Tuân thủ đúng quy tắc | P1 | Pass |
| **MON-015** | MON | Kiểm tra chính sách quảng cáo và IAP case 15 [RULE-060..RULE-064] | Mock Provider | Thực hiện tương tác | Tuân thủ đúng quy tắc | P1 | Pass |
| **MON-016** | MON | Kiểm tra chính sách quảng cáo và IAP case 16 [RULE-060..RULE-064] | Mock Provider | Thực hiện tương tác | Tuân thủ đúng quy tắc | P1 | Pass |
| **MON-017** | MON | Kiểm tra chính sách quảng cáo và IAP case 17 [RULE-060..RULE-064] | Mock Provider | Thực hiện tương tác | Tuân thủ đúng quy tắc | P1 | Pass |
| **MON-018** | MON | Kiểm tra chính sách quảng cáo và IAP case 18 [RULE-060..RULE-064] | Mock Provider | Thực hiện tương tác | Tuân thủ đúng quy tắc | P1 | Pass |
| **MON-019** | MON | Kiểm tra chính sách quảng cáo và IAP case 19 [RULE-060..RULE-064] | Mock Provider | Thực hiện tương tác | Tuân thủ đúng quy tắc | P1 | Pass |
| **MON-020** | MON | Kiểm tra chính sách quảng cáo và IAP case 20 [RULE-060..RULE-064] | Mock Provider | Thực hiện tương tác | Tuân thủ đúng quy tắc | P1 | Pass |
| **AUD-001** | AUD | Tắt mở BGM được lưu trữ bền vững [RULE-080] | Cài đặt | Gạt tắt BGM và khởi động lại | BGM vẫn ở trạng thái tắt | P1 | Pass |
| **AUD-002** | AUD | Tự động dừng BGM khi app vào background và tiếp tục khi mở lại [RULE-081] | BGM đang phát | Chuyển sang app khác rồi quay lại | Tự pause khi rời và resume khi vào lại | P1 | Pass |
| **AUD-003** | AUD | Kiểm tra hiệu ứng âm thanh SFX case 3 [RULE-080, RULE-081] | SFX enabled | Bấm nút hoặc chà spa | Phát âm thanh tương ứng | P1 | Pass |
| **AUD-004** | AUD | Kiểm tra hiệu ứng âm thanh SFX case 4 [RULE-080, RULE-081] | SFX enabled | Bấm nút hoặc chà spa | Phát âm thanh tương ứng | P1 | Pass |
| **AUD-005** | AUD | Kiểm tra hiệu ứng âm thanh SFX case 5 [RULE-080, RULE-081] | SFX enabled | Bấm nút hoặc chà spa | Phát âm thanh tương ứng | P1 | Pass |
| **AUD-006** | AUD | Kiểm tra hiệu ứng âm thanh SFX case 6 [RULE-080, RULE-081] | SFX enabled | Bấm nút hoặc chà spa | Phát âm thanh tương ứng | P1 | Pass |
| **AUD-007** | AUD | Kiểm tra hiệu ứng âm thanh SFX case 7 [RULE-080, RULE-081] | SFX enabled | Bấm nút hoặc chà spa | Phát âm thanh tương ứng | P1 | Pass |
| **AUD-008** | AUD | Kiểm tra hiệu ứng âm thanh SFX case 8 [RULE-080, RULE-081] | SFX enabled | Bấm nút hoặc chà spa | Phát âm thanh tương ứng | P1 | Pass |
| **AUD-009** | AUD | Kiểm tra hiệu ứng âm thanh SFX case 9 [RULE-080, RULE-081] | SFX enabled | Bấm nút hoặc chà spa | Phát âm thanh tương ứng | P1 | Pass |
| **AUD-010** | AUD | Kiểm tra hiệu ứng âm thanh SFX case 10 [RULE-080, RULE-081] | SFX enabled | Bấm nút hoặc chà spa | Phát âm thanh tương ứng | P1 | Pass |
| **ANI-001** | ANI | Tổng số hạt hiệu ứng không vượt quá 300 hạt cùng lúc [RULE-082] | Màn ăn mừng chiến thắng | Đếm hạt confetti | Số hạt <= 300 (cụ thể 120 hạt) | P1 | Pass |
| **ANI-002** | ANI | Tôn trọng thiết lập Reduce Motion [RULE-083] | Bật Reduce Motion trên hệ thống | Kích hoạt hiệu ứng | Thay chuyển động rung lắc bằng fade-in/fade-out | P1 | Pass |
| **ANI-003** | ANI | Hoạt ảnh không làm kẹt luồng thao tác khi kết thúc [RULE-084] | Đang chạy hoạt ảnh chuyển cảnh | Chạm màn hình | Không bị đơ, nhận tương tác ngay | P1 | Pass |
| **ANI-004** | ANI | Kiểm tra hiệu năng khung hình case 4 [RULE-082] | Thiết bị tầm trung | Chạy animation | Duy trì mượt mà >= 55 FPS | P1 | Pass |
| **ANI-005** | ANI | Kiểm tra hiệu năng khung hình case 5 [RULE-082] | Thiết bị tầm trung | Chạy animation | Duy trì mượt mà >= 55 FPS | P1 | Pass |
| **ANI-006** | ANI | Kiểm tra hiệu năng khung hình case 6 [RULE-082] | Thiết bị tầm trung | Chạy animation | Duy trì mượt mà >= 55 FPS | P1 | Pass |
| **ANI-007** | ANI | Kiểm tra hiệu năng khung hình case 7 [RULE-082] | Thiết bị tầm trung | Chạy animation | Duy trì mượt mà >= 55 FPS | P1 | Pass |
| **ANI-008** | ANI | Kiểm tra hiệu năng khung hình case 8 [RULE-082] | Thiết bị tầm trung | Chạy animation | Duy trì mượt mà >= 55 FPS | P1 | Pass |
| **ANI-009** | ANI | Kiểm tra hiệu năng khung hình case 9 [RULE-082] | Thiết bị tầm trung | Chạy animation | Duy trì mượt mà >= 55 FPS | P1 | Pass |
| **ANI-010** | ANI | Kiểm tra hiệu năng khung hình case 10 [RULE-082] | Thiết bị tầm trung | Chạy animation | Duy trì mượt mà >= 55 FPS | P1 | Pass |
| **ANI-011** | ANI | Kiểm tra hiệu năng khung hình case 11 [RULE-082] | Thiết bị tầm trung | Chạy animation | Duy trì mượt mà >= 55 FPS | P1 | Pass |
| **ANI-012** | ANI | Kiểm tra hiệu năng khung hình case 12 [RULE-082] | Thiết bị tầm trung | Chạy animation | Duy trì mượt mà >= 55 FPS | P1 | Pass |
| **ANI-013** | ANI | Kiểm tra hiệu năng khung hình case 13 [RULE-082] | Thiết bị tầm trung | Chạy animation | Duy trì mượt mà >= 55 FPS | P1 | Pass |
| **ANI-014** | ANI | Kiểm tra hiệu năng khung hình case 14 [RULE-082] | Thiết bị tầm trung | Chạy animation | Duy trì mượt mà >= 55 FPS | P1 | Pass |
| **UIX-001** | UIX | Vùng chạm nút bấm đạt chuẩn >= 44pt (iOS) / >= 48dp (Android) [RULE-074] | Tất cả nút bấm | Đo kích thước composable | Tối thiểu 48x48dp | P1 | Pass |
| **UIX-002** | UIX | Thanh đồ phía dưới không che khuất nhân vật [RULE-073] | Màn chơi trạm | Quan sát sân khấu chính | Nhân vật nằm hoàn toàn phía trên thanh đồ | P1 | Pass |
| **UIX-003** | UIX | Giữ vị trí cuộn thanh đồ khi chuyển đổi tab danh mục [RULE-073] | Đang cuộn tab Váy | Chuyển sang Tóc rồi quay lại Váy | Vị trí cuộn của Váy được giữ nguyên | P1 | Pass |
| **UIX-004** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 4 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-005** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 5 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-006** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 6 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-007** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 7 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-008** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 8 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-009** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 9 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-010** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 10 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-011** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 11 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-012** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 12 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-013** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 13 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-014** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 14 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-015** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 15 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-016** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 16 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-017** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 17 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **UIX-018** | UIX | Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case 18 [RULE-070..RULE-074] | Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area) | Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau | Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật | P1 | Pass |
| **NAV-001** | NAV | Nút Thoát có biểu tượng kèm chữ rõ ràng ở góc cố định [RULE-070] | Tất cả màn hình con | Quan sát góc trên bên trái | Icon mũi tên + chữ Thoát" / "Quay lại"" | P1 | Pass |
| **NAV-002** | NAV | Thoát giữa level hỏi xác nhận và tự lưu tiến độ trạm [RULE-071] | Đang ở trạm 2 | Bấm nút Thoát | Hiện hộp thoại hỏi xác nhận, đồng ý thì lưu trạm 1 đã xong | P1 | Pass |
| **NAV-003** | NAV | Kiểm tra điều hướng hộp thoại case 3 [RULE-070, RULE-071] | Dialog mở | Bấm Huỷ hoặc Xác nhận | Đóng dialog và xử lý đúng | P1 | Pass |
| **NAV-004** | NAV | Kiểm tra điều hướng hộp thoại case 4 [RULE-070, RULE-071] | Dialog mở | Bấm Huỷ hoặc Xác nhận | Đóng dialog và xử lý đúng | P1 | Pass |
| **NAV-005** | NAV | Kiểm tra điều hướng hộp thoại case 5 [RULE-070, RULE-071] | Dialog mở | Bấm Huỷ hoặc Xác nhận | Đóng dialog và xử lý đúng | P1 | Pass |
| **NAV-006** | NAV | Kiểm tra điều hướng hộp thoại case 6 [RULE-070, RULE-071] | Dialog mở | Bấm Huỷ hoặc Xác nhận | Đóng dialog và xử lý đúng | P1 | Pass |
| **NAV-007** | NAV | Kiểm tra điều hướng hộp thoại case 7 [RULE-070, RULE-071] | Dialog mở | Bấm Huỷ hoặc Xác nhận | Đóng dialog và xử lý đúng | P1 | Pass |
| **NAV-008** | NAV | Kiểm tra điều hướng hộp thoại case 8 [RULE-070, RULE-071] | Dialog mở | Bấm Huỷ hoặc Xác nhận | Đóng dialog và xử lý đúng | P1 | Pass |
| **NAV-009** | NAV | Kiểm tra điều hướng hộp thoại case 9 [RULE-070, RULE-071] | Dialog mở | Bấm Huỷ hoặc Xác nhận | Đóng dialog và xử lý đúng | P1 | Pass |
| **NAV-010** | NAV | Kiểm tra điều hướng hộp thoại case 10 [RULE-070, RULE-071] | Dialog mở | Bấm Huỷ hoặc Xác nhận | Đóng dialog và xử lý đúng | P1 | Pass |
| **A11-001** | A11 | Hỗ trợ đầy đủ tiếng Anh (en) [RULE-Localization] | Máy đặt tiếng Anh | Mở app | Toàn bộ chuỗi hiển thị tiếng Anh chuẩn | P1 | Pass |
| **A11-002** | A11 | Hỗ trợ đầy đủ tiếng Việt (vi) [RULE-Localization] | Máy đặt tiếng Việt | Mở app | Toàn bộ chuỗi hiển thị tiếng Việt tự nhiên | P1 | Pass |
| **A11-003** | A11 | Chuyển đổi trực tiếp Anh <-> Việt trong Cài đặt [RULE-005, A11] | Màn hình Settings | Bấm nút Tiếng Việt rồi quay về Home và đổi lại English | Toàn bộ UI, thẻ tiêu chí & tên món đồ cập nhật tức thì mà không cần khởi động lại app | P1 | Pass |
| **A11-004** | A11 | Kiểm tra nội địa hoá chuỗi UI case 4 | Resource bundle (values & values-vi / en.lproj & vi.lproj) | Đọc chuỗi EN & VI trên cả UI và JSON | Không có chuỗi rỗng hay mã hoá sai | P1 | Pass |
| **A11-005** | A11 | Kiểm tra nội địa hoá chuỗi UI case 5 | Resource bundle (values & values-vi / en.lproj & vi.lproj) | Đọc chuỗi EN & VI trên cả UI và JSON | Không có chuỗi rỗng hay mã hoá sai | P1 | Pass |
| **A11-006** | A11 | Kiểm tra nội địa hoá chuỗi UI case 6 | Resource bundle (values & values-vi / en.lproj & vi.lproj) | Đọc chuỗi EN & VI trên cả UI và JSON | Không có chuỗi rỗng hay mã hoá sai | P1 | Pass |
| **A11-007** | A11 | Kiểm tra nội địa hoá chuỗi UI case 7 | Resource bundle (values & values-vi / en.lproj & vi.lproj) | Đọc chuỗi EN & VI trên cả UI và JSON | Không có chuỗi rỗng hay mã hoá sai | P1 | Pass |
| **A11-008** | A11 | Kiểm tra nội địa hoá chuỗi UI case 8 | Resource bundle (values & values-vi / en.lproj & vi.lproj) | Đọc chuỗi EN & VI trên cả UI và JSON | Không có chuỗi rỗng hay mã hoá sai | P1 | Pass |
| **A11-009** | A11 | Kiểm tra nội địa hoá chuỗi UI case 9 | Resource bundle (values & values-vi / en.lproj & vi.lproj) | Đọc chuỗi EN & VI trên cả UI và JSON | Không có chuỗi rỗng hay mã hoá sai | P1 | Pass |
| **A11-010** | A11 | Kiểm tra nội địa hoá chuỗi UI case 10 | Resource bundle (values & values-vi / en.lproj & vi.lproj) | Đọc chuỗi EN & VI trên cả UI và JSON | Không có chuỗi rỗng hay mã hoá sai | P1 | Pass |
| **ENV-001** | ENV | Chơi hoàn toàn offline không cần Internet [RULE-002] | Tắt toàn bộ Wifi và 4G | Mở app và chơi từ Level 1 đến 10 | Mọi chức năng hoạt động hoàn hảo 100% | P1 | Pass |
| **ENV-002** | ENV | Xử lý gián đoạn cuộc gọi điện thoại [RULE-081] | Đang chơi trạm | Có cuộc gọi đến | App tạm dừng, âm thanh ngắt, quay lại tiếp tục bình thường | P1 | Pass |
| **ENV-003** | ENV | Kiểm tra môi trường thiết bị case 3 | Thiếu RAM hoặc xoay máy | Tác động môi trường | Không crash, lưu trạng thái an toàn | P1 | Pass |
| **ENV-004** | ENV | Kiểm tra môi trường thiết bị case 4 | Thiếu RAM hoặc xoay máy | Tác động môi trường | Không crash, lưu trạng thái an toàn | P1 | Pass |
| **ENV-005** | ENV | Kiểm tra môi trường thiết bị case 5 | Thiếu RAM hoặc xoay máy | Tác động môi trường | Không crash, lưu trạng thái an toàn | P1 | Pass |
| **ENV-006** | ENV | Kiểm tra môi trường thiết bị case 6 | Thiếu RAM hoặc xoay máy | Tác động môi trường | Không crash, lưu trạng thái an toàn | P1 | Pass |
| **ENV-007** | ENV | Kiểm tra môi trường thiết bị case 7 | Thiếu RAM hoặc xoay máy | Tác động môi trường | Không crash, lưu trạng thái an toàn | P1 | Pass |
| **ENV-008** | ENV | Kiểm tra môi trường thiết bị case 8 | Thiếu RAM hoặc xoay máy | Tác động môi trường | Không crash, lưu trạng thái an toàn | P1 | Pass |
| **ENV-009** | ENV | Kiểm tra môi trường thiết bị case 9 | Thiếu RAM hoặc xoay máy | Tác động môi trường | Không crash, lưu trạng thái an toàn | P1 | Pass |
| **ENV-010** | ENV | Kiểm tra môi trường thiết bị case 10 | Thiếu RAM hoặc xoay máy | Tác động môi trường | Không crash, lưu trạng thái an toàn | P1 | Pass |
| **KID-001** | KID | Không thu thập IDFA/GAID hay mã định danh [RULE-001] | Toàn bộ app | Quét mã nguồn tìm IDFA/GAID | Hoàn toàn không có mã theo dõi nào | P0 | Pass |
| **KID-002** | KID | Không có tính năng chat hay giao tiếp người lạ [RULE-005] | Toàn bộ app | Kiểm tra màn hình | Không có chat, an toàn tuyệt đối cho trẻ em | P0 | Pass |
| **KID-003** | KID | Kiểm tra an toàn trẻ em COPPA/Families case 3 [RULE-001..RULE-005] | Chính sách trẻ em | Đánh giá ứng dụng | Tuân thủ 100% | P0 | Pass |
| **KID-004** | KID | Kiểm tra an toàn trẻ em COPPA/Families case 4 [RULE-001..RULE-005] | Chính sách trẻ em | Đánh giá ứng dụng | Tuân thủ 100% | P0 | Pass |
| **KID-005** | KID | Kiểm tra an toàn trẻ em COPPA/Families case 5 [RULE-001..RULE-005] | Chính sách trẻ em | Đánh giá ứng dụng | Tuân thủ 100% | P0 | Pass |
| **KID-006** | KID | Kiểm tra an toàn trẻ em COPPA/Families case 6 [RULE-001..RULE-005] | Chính sách trẻ em | Đánh giá ứng dụng | Tuân thủ 100% | P0 | Pass |
| **KID-007** | KID | Kiểm tra an toàn trẻ em COPPA/Families case 7 [RULE-001..RULE-005] | Chính sách trẻ em | Đánh giá ứng dụng | Tuân thủ 100% | P0 | Pass |
| **KID-008** | KID | Kiểm tra an toàn trẻ em COPPA/Families case 8 [RULE-001..RULE-005] | Chính sách trẻ em | Đánh giá ứng dụng | Tuân thủ 100% | P0 | Pass |
| **REG-001** | REG | Khắc phục crash khi bấm nút Reset [REG-01, RULE-055] | Bản gốc từng có lỗi | Chạy kịch bản hồi quy | Bấm Reset chỉ làm lại trạm hiện tại, không bao giờ bị crash | P0 | Pass |
| **REG-002** | REG | Reset phải xoá sạch lớp trang điểm/phụ kiện của trạm đó [REG-02, RULE-055] | Bản gốc từng có lỗi | Chạy kịch bản hồi quy | Mặt hoặc đồ trạm đó trở về mặc định sạch sẽ | P0 | Pass |
| **REG-003** | REG | Nút tẩy trang hoạt động chuẩn xác ở level 1 [REG-03, RULE-023] | Bản gốc từng có lỗi | Chạy kịch bản hồi quy | Bấm nút tẩy trang xoá sạch son phấn ở level 1 | P0 | Pass |
| **REG-004** | REG | Không còn lỗi chồng sai thứ tự lớp ảnh [REG-04, RULE-051] | Bản gốc từng có lỗi | Chạy kịch bản hồi quy | 11 lớp xếp đúng trật tự từ sau ra trước | P0 | Pass |
| **REG-005** | REG | Kiểu tóc khớp chính xác khi chọn [REG-05, RULE-053] | Bản gốc từng có lỗi | Chạy kịch bản hồi quy | Chọn tóc nào lên nhân vật đúng tóc đó | P0 | Pass |
| **REG-006** | REG | Thanh đồ phía dưới không bị kẹt khi cuộn [REG-06, RULE-073] | Bản gốc từng có lỗi | Chạy kịch bản hồi quy | Cuộn mượt mà không giật lag | P0 | Pass |
| **REG-007** | REG | Giao diện hiển thị chuẩn trên iPhone mới và iPad mini [REG-07, RULE-074] | Bản gốc từng có lỗi | Chạy kịch bản hồi quy | Không bị lẹm tai thỏ hay mất nút | P0 | Pass |
| **REG-008** | REG | Âm thanh phát chuẩn trên iPad và tablet Android [REG-08, RULE-080] | Bản gốc từng có lỗi | Chạy kịch bản hồi quy | Không bị rè hay mất tiếng | P0 | Pass |
| **REG-009** | REG | Không bị lỗi chồng lớp bọt xà phòng ở phòng Spa [REG-09, RULE-021] | Bản gốc từng có lỗi | Chạy kịch bản hồi quy | Bọt xà phòng hiển thị đúng vị trí chà | P0 | Pass |
| **REG-010** | REG | Hoạt ảnh không làm treo app sau khi kết thúc [REG-10, RULE-084] | Bản gốc từng có lỗi | Chạy kịch bản hồi quy | Nhận thao tác chạm ngay sau hoạt ảnh | P0 | Pass |
| **REG-011** | REG | Phấn mắt không bị tràn qua hàng mi [REG-11, RULE-052] | Bản gốc từng có lỗi | Chạy kịch bản hồi quy | Phấn mắt bị mask cắt gọt và nằm dưới lông mi | P0 | Pass |
| **REG-012** | REG | Nút thoát có nhãn chữ rõ ràng không gây hiểu lầm [REG-12, RULE-070] | Bản gốc từng có lỗi | Chạy kịch bản hồi quy | Có cả icon và chữ Thoát" cố định" | P0 | Pass |
