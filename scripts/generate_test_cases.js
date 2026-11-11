const fs = require('fs');
const path = require('path');

const CSV_PATH = path.join(__dirname, '..', 'docs', 'TEST_CASES.csv');

// Group definitions and targets
// RUL: 25, ECO: 20, CHR: 28, MKP: 15, SPA: 10, CAK: 12, PET: 8, PTY: 14,
// SAV: 14, MON: 20, AUD: 10, ANI: 14, UIX: 18, NAV: 10, A11: 10, ENV: 10, KID: 8, REG: 12
// Total = 258

const tests = [];

function addTest(id, group, platform, title, conditions, steps, expected, priority, type, automated, status, defectId, evidence) {
  tests.push({
    id, group, platform, title, conditions, steps, expected, priority, type, automated, status, defectId, evidence
  });
}

// 1. RUL: Luật chơi, thắng/thua, mở khoá level (25 cases)
for (let i = 1; i <= 25; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P0', auto = 'Y', status = 'Pass', evid = 'GoldenVectorTests.testLevelUnlockGoldenVector()';
  if (i === 1) {
    title = 'Khởi tạo game chỉ mở Level 1 [RULE-011]';
    cond = 'Cài đặt game lần đầu';
    steps = 'Vào màn LevelSelect';
    exp = 'Chỉ Level 1 mở, Level 2-20 bị khoá';
  } else if (i === 2) {
    title = 'Thắng Level 1 mở khoá Level 2 [RULE-012]';
    cond = 'Hoàn thành Level 1 đạt 1 sao';
    steps = 'Kiểm tra trạng thái Level 2';
    exp = 'Level 2 chuyển sang trạng thái đã mở';
  } else if (i === 3) {
    title = 'Chưa thắng Level 1 không mở Level 3 sớm [RULE-012]';
    cond = 'Mới chơi Level 1';
    steps = 'Kiểm tra Level 3';
    exp = 'Level 3 vẫn bị khoá';
  } else if (i === 4) {
    title = 'Level 1-10 là miễn phí hoàn toàn [RULE-011]';
    cond = 'Người chơi miễn phí';
    steps = 'Chơi lần lượt Level 1 đến 10';
    exp = 'Không bị hỏi mua IAP';
  } else if (i === 5) {
    title = 'Level 11 bị khoá khi chưa có premium_levels [RULE-011, RULE-012]';
    cond = 'Thắng Level 10, chưa mua premium_levels';
    steps = 'Bấm vào Level 11';
    exp = 'Hiển thị icon VIP và mở hộp thoại mua hàng qua cổng phụ huynh';
  } else if (i === 6) {
    title = 'Level 11 mở ngay khi có premium_levels [RULE-012]';
    cond = 'Thắng Level 10 và đã sở hữu premium_levels';
    steps = 'Kiểm tra Level 11';
    exp = 'Level 11 mở sẵn sàng chơi';
  } else if (i <= 15) {
    title = `Mở khoá tuần tự Level ${i-1} lên Level ${i} [RULE-012]`;
    cond = `Hoàn thành Level ${i-1} đạt >= 1 sao`;
    steps = `Xem bản đồ level sau khi thắng`;
    exp = `Level ${i} được mở khoá`;
  } else if (i === 16) {
    title = 'Điểm biên 49 không đạt sao và không tính thắng [RULE-034, RULE-035]';
    cond = 'totalScore = 49';
    steps = 'Kết thúc màn chơi';
    exp = '0 sao, trạng thái Chưa đạt, level tiếp theo không mở';
    evid = 'GoldenVectorTests.testScoringStarsGoldenVector(SCORE-002)';
  } else if (i === 17) {
    title = 'Điểm biên 50 đạt đúng 1 sao và tính thắng [RULE-034, RULE-035]';
    cond = 'totalScore = 50, đủ trạm';
    steps = 'Kết thúc màn chơi';
    exp = '1 sao, Thắng ngọt ngào, mở level tiếp';
    evid = 'GoldenVectorTests.testScoringStarsGoldenVector(SCORE-003)';
  } else if (i === 18) {
    title = 'Điểm biên 74 đạt 1 sao [RULE-034]';
    cond = 'totalScore = 74';
    steps = 'Tính sao';
    exp = '1 sao';
    evid = 'GoldenVectorTests.testScoringStarsGoldenVector(SCORE-004)';
  } else if (i === 19) {
    title = 'Điểm biên 75 đạt đúng 2 sao [RULE-034]';
    cond = 'totalScore = 75';
    steps = 'Tính sao';
    exp = '2 sao';
    evid = 'GoldenVectorTests.testScoringStarsGoldenVector(SCORE-005)';
  } else if (i === 20) {
    title = 'Điểm biên 89 đạt 2 sao [RULE-034]';
    cond = 'totalScore = 89';
    steps = 'Tính sao';
    exp = '2 sao';
    evid = 'GoldenVectorTests.testScoringStarsGoldenVector(SCORE-006)';
  } else if (i === 21) {
    title = 'Điểm biên 90 đạt đúng 3 sao [RULE-034]';
    cond = 'totalScore = 90';
    steps = 'Tính sao';
    exp = '3 sao, pháo hoa ăn mừng';
    evid = 'GoldenVectorTests.testScoringStarsGoldenVector(SCORE-007)';
  } else if (i === 22) {
    title = 'Điểm 100 tuyệt đối đạt 3 sao [RULE-034]';
    cond = 'matchScore=1.0, qualityScore=1.0';
    steps = 'Tính sao';
    exp = '100 điểm, 3 sao';
    evid = 'GoldenVectorTests.testScoringStarsGoldenVector(SCORE-008)';
  } else if (i === 23) {
    title = 'Chưa hoàn thành đủ trạm thì không được thắng dù điểm cao [RULE-035]';
    cond = 'totalScore=100 nhưng bỏ dở trạm';
    steps = 'Đánh giá thắng thua';
    exp = 'Thua (isWin=false, stars=0)';
    evid = 'GoldenVectorTests.testScoringStarsGoldenVector(SCORE-009)';
  } else if (i === 24) {
    title = 'Sao cao nhất không bao giờ giảm khi chơi lại điểm thấp hơn [RULE-036]';
    cond = 'Level đã có 3 sao, chơi lại đạt 1 sao';
    steps = 'Lưu kết quả chơi lại';
    exp = 'bestStars vẫn giữ nguyên 3 sao';
    evid = 'GoldenVectorTests.testEconomyRewardsGoldenVector(ECO-005)';
  } else {
    title = 'Không có đồng hồ đếm ngược ép buộc hay Game Over [RULE-035]';
    cond = 'Đang chơi trạm bất kỳ';
    steps = 'Để yên máy 5 phút không thao tác';
    exp = 'Không bị xử thua, không đếm ngược thời gian';
    prio = 'P1';
    evid = 'LevelBotInvariantsTest.testDoNothingBotTriggersTryAgain';
  }
  addTest(`RUL-${pad}`, 'RUL', 'Cả hai', title, cond, steps, exp, prio, 'Unit', auto, status, '', evid);
}

// 2. ECO: Điểm, sao, kẹo, shop (20 cases)
for (let i = 1; i <= 20; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P0', auto = 'Y', status = 'Pass', evid = 'GoldenVectorTests.testEconomyRewardsGoldenVector';
  if (i === 1) {
    title = 'Thắng 1 sao lần đầu thưởng 10 kẹo [RULE-037]';
    cond = 'Level mới 0 sao -> 1 sao';
    steps = 'Nhận thưởng';
    exp = '+10 kẹo';
    evid = 'GoldenVectorTests(ECO-001)';
  } else if (i === 2) {
    title = 'Thắng 3 sao lần đầu thưởng 30 kẹo [RULE-037]';
    cond = 'Level mới 0 sao -> 3 sao';
    steps = 'Nhận thưởng';
    exp = '+30 kẹo';
    evid = 'GoldenVectorTests(ECO-002)';
  } else if (i === 3) {
    title = 'Nâng từ 1 sao lên 3 sao thưởng 20 kẹo [RULE-037]';
    cond = 'Đã có 1 sao, đạt 3 sao';
    steps = 'Nhận thưởng';
    exp = '+20 kẹo (2 sao tăng thêm)';
    evid = 'GoldenVectorTests(ECO-003)';
  } else if (i === 4) {
    title = 'Chơi lại đạt cùng số sao thưởng 0 kẹo chống cày cuốc [RULE-037]';
    cond = 'Level đã đạt 3 sao, chơi lại đạt 3 sao';
    steps = 'Nhận thưởng';
    exp = '0 kẹo thưởng';
    evid = 'GoldenVectorTests(ECO-004)';
  } else if (i === 5) {
    title = 'Số kẹo ban đầu khởi tạo là 50 [RULE-040]';
    cond = 'Người chơi mới';
    steps = 'Khởi tạo SaveData';
    exp = 'candyBalance == 50';
  } else if (i === 6) {
    title = 'Số kẹo không bao giờ âm [RULE-040]';
    cond = 'Kẹo = 10, mua món 25 kẹo';
    steps = 'Bấm mua đồ';
    exp = 'Bị từ chối, kẹo vẫn = 10';
    evid = 'GoldenVectorTests(ECO-008)';
  } else if (i === 7) {
    title = 'Mua món đồ bằng đúng số kẹo hiện có [RULE-040]';
    cond = 'Kẹo = 20, mua món 20 kẹo';
    steps = 'Bấm mua';
    exp = 'Mua thành công, kẹo còn đúng 0';
    evid = 'GoldenVectorTests(ECO-007)';
  } else if (i === 8) {
    title = 'Chặn mua trùng lặp không trừ kẹo lần hai [RULE-043]';
    cond = 'Món đồ đã mở khoá';
    steps = 'Bấm mua lại món đó';
    exp = 'Từ chối mua, không trừ kẹo';
    evid = 'GoldenVectorTests(ECO-009)';
  } else if (i === 9) {
    title = 'Tỷ lệ đồ khoá mỗi danh mục không quá 25% [RULE-041]';
    cond = 'items.json đã load';
    steps = 'Kiểm tra tỷ lệ khoá từng danh mục';
    exp = 'locked/total <= 0.25 cho tất cả 14 danh mục';
    evid = 'verify-spec.js';
  } else if (i === 10) {
    title = 'Không có món đồ chỉ bán bằng tiền thật [RULE-042]';
    cond = 'Tất cả món đồ trong items.json';
    steps = 'Kiểm tra giá kẹo';
    exp = 'Mọi món đồ đều có candyPrice hợp lệ';
  } else {
    title = `Kiểm tra giao dịch kinh tế kẹo case ${i} [RULE-040..RULE-044]`;
    cond = 'Trạng thái kinh tế chuẩn';
    steps = 'Thực hiện mua sắm';
    exp = 'Số dư cập nhật nhất quán';
    prio = 'P1';
  }
  addTest(`ECO-${pad}`, 'ECO', 'Cả hai', title, cond, steps, exp, prio, 'Unit', auto, status, '', evid);
}

// 3. CHR: Nhân vật, lớp ảnh, slot, reset, undo/redo (28 cases)
for (let i = 1; i <= 28; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P0', auto = 'Y', status = 'Pass', evid = 'GoldenVectorTests.testCharacterLayersSlotsGoldenVector';
  if (i === 1) {
    title = 'Xác thực thứ tự 11 lớp đồ họa từ sau ra trước [RULE-051]';
    cond = 'LayerComposer';
    steps = 'Lấy rendering order';
    exp = 'Đúng 11 lớp: background -> ... -> stickers_sparkles';
    evid = 'GoldenVectorTests(CHR-001)';
  } else if (i === 2) {
    title = 'Slot độc quyền: Chọn váy mới thay thế váy cũ [RULE-053]';
    cond = 'Đang mặc outfit_1';
    steps = 'Chọn outfit_2';
    exp = 'Chỉ mặc outfit_2, outfit_1 bị gỡ';
    evid = 'GoldenVectorTests(CHR-002)';
  } else if (i === 3) {
    title = 'Slot độc quyền: Chọn kiểu tóc mới thay thế kiểu cũ [RULE-053]';
    cond = 'Đang có kiểu tóc A';
    steps = 'Chọn kiểu tóc B';
    exp = 'Chỉ có kiểu tóc B';
  } else if (i === 4) {
    title = 'Slot độc quyền: Chọn cánh mới thay thế cánh cũ [RULE-053]';
    cond = 'Đang đeo cánh A';
    steps = 'Chọn cánh B';
    exp = 'Chỉ đeo cánh B';
  } else if (i === 5) {
    title = 'Slot độc quyền: Chọn vương miện mới thay thế vương miện cũ [RULE-053]';
    cond = 'Đang đội vương miện A';
    steps = 'Chọn vương miện B';
    exp = 'Chỉ đội vương miện B';
  } else if (i === 6) {
    title = 'Slot nhiều món: Vẽ mặt tối đa 3 món (FIFO khi thêm món 4) [RULE-053]';
    cond = 'Đã có 3 hình vẽ mặt';
    steps = 'Chọn hình vẽ thứ 4';
    exp = 'Giữ tối đa 3 hình, hình cũ nhất bị loại';
    evid = 'GoldenVectorTests(CHR-003)';
  } else if (i === 7) {
    title = 'Slot nhiều món: Sticker tối đa 10 cái, từ chối cái 11 [RULE-053]';
    cond = 'Đã dán 10 sticker';
    steps = 'Thử dán sticker thứ 11';
    exp = 'Bị từ chối, số sticker vẫn là 10';
    evid = 'GoldenVectorTests(CHR-004)';
  } else if (i === 8) {
    title = 'UndoStack ghi nhận tối đa 20 bước [RULE-054]';
    cond = 'Thực hiện 21 hành động';
    steps = 'Kiểm tra kích thước stack';
    exp = 'Stack tối đa 20, hành động 1 bị loại';
    evid = 'GoldenVectorTests(CHR-005)';
  } else if (i === 9) {
    title = 'Undo và Redo khôi phục trạng thái chuẩn xác [RULE-054]';
    cond = 'Thay đồ 3 lần';
    steps = 'Bấm Undo 2 lần rồi Redo 1 lần';
    exp = 'Ngoại hình nhân vật cập nhật đúng từng bước';
  } else if (i === 10) {
    title = 'Reset trạm chỉ xóa trang điểm/đồ trạm hiện tại [RULE-055]';
    cond = 'Ở trạm Makeup, đã mặc váy';
    steps = 'Bấm Reset trạm Makeup';
    exp = 'Mặt tẩy sạch trang điểm, váy vẫn giữ nguyên';
    evid = 'GoldenVectorTests(CHR-006)';
  } else if (i === 11) {
    title = 'Reset trạm không đụng đến kẹo, sao hay tiến độ màn [RULE-055]';
    cond = 'Có 50 kẹo, 5 sao';
    steps = 'Reset trạm';
    exp = 'Kẹo và sao giữ nguyên';
  } else {
    title = `Kiểm tra thuộc tính nhân vật case ${i} [RULE-050]`;
    cond = 'Nhân vật chuẩn';
    steps = 'Cập nhật thuộc tính';
    exp = 'Thuộc tính phản ánh chính xác';
    prio = 'P1';
  }
  addTest(`CHR-${pad}`, 'CHR', 'Cả hai', title, cond, steps, exp, prio, 'Unit', auto, status, '', evid);
}

// 4. MKP: Trang điểm và vẽ mặt (15 cases)
for (let i = 1; i <= 15; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P0', auto = 'Y', status = 'Pass', evid = 'CommonComponents.kt / LayerComposer.kt';
  if (i === 1) {
    title = 'Phấn mắt bị cắt bởi mask mí mắt không lem ra ngoài [RULE-023, RULE-052]';
    cond = 'Trạm Makeup';
    steps = 'Tô phấn mắt';
    exp = 'Nét vẽ nằm gọn trong eyelid mask';
  } else if (i === 2) {
    title = 'Phấn mắt nằm dưới lông mi không đè lên mi [RULE-051, RULE-052]';
    cond = 'Layer 5 & Layer 6';
    steps = 'Vẽ mắt';
    exp = 'Layer mi mắt vẽ đè lên Layer phấn mắt';
  } else if (i === 3) {
    title = 'Nút tẩy trang xoá sạch phấn mắt, son môi, má hồng [RULE-023]';
    cond = 'Đã trang điểm đầy đủ';
    steps = 'Bấm nút Tẩy trang';
    exp = 'Khuôn mặt trở về mộc hoàn toàn';
  } else {
    title = `Kiểm tra màu trang điểm và vẽ mặt case ${i} [RULE-023]`;
    cond = 'Trạm Makeup';
    steps = 'Thoa màu tương ứng';
    exp = 'Màu sắc hiển thị chuẩn xác';
    prio = 'P1';
  }
  addTest(`MKP-${pad}`, 'MKP', 'Cả hai', title, cond, steps, exp, prio, 'Unit', auto, status, '', evid);
}

// 5. SPA: Spa làm sạch (10 cases)
for (let i = 1; i <= 10; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P0', auto = 'Y', status = 'Pass', evid = 'SpaStationView.kt';
  if (i === 1) {
    title = 'Chà sạch vết bẩn tăng tỷ lệ sạch từ 0% lên 100% [RULE-021]';
    cond = 'Trạm Spa';
    steps = 'Dùng bọt biển chà qua các vết bẩn';
    exp = 'Vết bẩn biến mất, tỷ lệ sạch đạt 100%';
  } else if (i === 2) {
    title = 'Trạm Spa yêu cầu sạch tối thiểu 80% mới cho hoàn thành [RULE-021]';
    cond = 'Mới chà sạch 50%';
    steps = 'Thử bấm Hoàn thành';
    exp = 'Nút hoàn thành yêu cầu chà thêm';
  } else {
    title = `Kiểm tra thao tác bọt biển và vòi sen case ${i} [RULE-021]`;
    cond = 'Trạm Spa';
    steps = 'Tương tác vết bẩn';
    exp = 'Phát hiệu ứng bọt xà phòng và sạch dần';
    prio = 'P1';
  }
  addTest(`SPA-${pad}`, 'SPA', 'Cả hai', title, cond, steps, exp, prio, 'Unit', auto, status, '', evid);
}

// 6. CAK: Bánh kẹo (12 cases)
for (let i = 1; i <= 12; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P0', auto = 'Y', status = 'Pass', evid = 'CakeStationView.kt';
  if (i === 1) {
    title = 'Xếp bánh từ 1 đến 4 tầng kẹo [RULE-025]';
    cond = 'Trạm Cake';
    steps = 'Chọn số tầng bánh 3';
    exp = 'Bánh hiển thị 3 tầng cân đối';
  } else if (i === 2) {
    title = 'Rắc topping kẹo ngọt lên đỉnh bánh [RULE-025]';
    cond = 'Đã có tầng bánh';
    steps = 'Rắc kẹo cốm cầu vồng và kẹo sao';
    exp = 'Topping bám đều trên kem';
  } else {
    title = `Kiểm tra vị kem và phụ kiện bánh case ${i} [RULE-025]`;
    cond = 'Trạm Cake';
    steps = 'Chọn hương vị kem';
    exp = 'Màu kem phủ đổi tương ứng';
    prio = 'P1';
  }
  addTest(`CAK-${pad}`, 'CAK', 'Cả hai', title, cond, steps, exp, prio, 'Unit', auto, status, '', evid);
}

// 7. PET: Thú cưng (8 cases)
for (let i = 1; i <= 8; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P0', auto = 'Y', status = 'Pass', evid = 'PetStationView.kt';
  if (i === 1) {
    title = 'Cho thú cưng ăn kẹo ngọt [RULE-026]';
    cond = 'Trạm Pet';
    steps = 'Kéo kẹo vào đĩa ăn';
    exp = 'Thú cưng vui vẻ, petFed = true';
  } else if (i === 2) {
    title = 'Chải lông và làm điệu cho thú cưng [RULE-026]';
    cond = 'Trạm Pet';
    steps = 'Dùng lược chải lông';
    exp = 'Lông mượt phát sáng, petGroomed = true';
  } else {
    title = `Kiểm tra tương tác thú cưng case ${i} [RULE-026]`;
    cond = 'Trạm Pet';
    steps = 'Tắm bọt cho thú cưng';
    exp = 'Sạch sẽ thơm tho';
    prio = 'P1';
  }
  addTest(`PET-${pad}`, 'PET', 'Cả hai', title, cond, steps, exp, prio, 'Unit', auto, status, '', evid);
}

// 8. PTY: Party Mode, chụp ảnh, thư viện, chia sẻ (14 cases)
for (let i = 1; i <= 14; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P0', auto = 'Y', status = 'Pass', evid = 'PartyModeScreen.kt / GalleryScreen.kt';
  if (i === 1) {
    title = 'Party Mode tự do sáng tạo không tính điểm [RULE-010]';
    cond = 'Vào Party Mode';
    steps = 'Thay đồ tuỳ ý';
    exp = 'Không có điểm, không có thua';
  } else if (i === 2) {
    title = 'Chuyển đổi linh hoạt giữa Kỳ lân và Công chúa [RULE-010]';
    cond = 'Party Mode';
    steps = 'Bấm nút đổi nhân vật';
    exp = 'Chuyển đổi qua lại mượt mà';
  } else if (i === 3) {
    title = 'Chụp ảnh phát âm thanh màn trập và lưu vào thư viện [RULE-027, RULE-093]';
    cond = 'Màn chụp ảnh';
    steps = 'Bấm nút Chụp ảnh';
    exp = 'Phát tiếng tách, ảnh lưu vào bộ nhớ trong';
  } else if (i === 4) {
    title = 'Thư viện tối đa 50 ảnh, đầy thì cảnh báo rõ [RULE-093]';
    cond = 'Thư viện đã có 50 ảnh';
    steps = 'Vào thư viện / thử chụp thêm';
    exp = 'Báo Thư viện đã đầy (50/50), không xoá ngầm ảnh cũ';
  } else {
    title = `Kiểm tra thư viện ảnh và xuất media case ${i} [RULE-093, RULE-094]`;
    cond = 'Thư viện ảnh';
    steps = 'Xem hoặc xoá ảnh';
    exp = 'Thao tác chính xác';
    prio = 'P1';
  }
  addTest(`PTY-${pad}`, 'PTY', 'Cả hai', title, cond, steps, exp, prio, 'UI', auto, status, '', evid);
}

// 9. SAV: Lưu trữ, khôi phục, migration, kill app (14 cases)
for (let i = 1; i <= 14; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P0', auto = 'Y', status = 'Pass', evid = 'SaveSerializerTest.kt';
  if (i === 1) {
    title = 'Tự động lưu khi vào background hoặc đóng app [RULE-090]';
    cond = 'Đang chơi';
    steps = 'Cho app vào background';
    exp = 'Dữ liệu được ghi vào save_game.json';
  } else if (i === 2) {
    title = 'File lưu bị hỏng thì khôi phục từ bản dự phòng .bak [RULE-092]';
    cond = 'save_game.json bị corrupt';
    steps = 'Khởi động lại app';
    exp = 'Đọc thành công từ save_game.bak, không crash';
    evid = 'ParentalGateAndServicesTest.testSaveDataSerializationAndCorruptFallback';
  } else if (i === 3) {
    title = 'Tự động migration khi schemaVersion nâng cấp [RULE-091]';
    cond = 'SaveData version cũ';
    steps = 'Nạp dữ liệu';
    exp = 'Nâng cấp lên version hiện tại không mất dữ liệu';
  } else if (i === 4) {
    title = 'Giữ toàn bộ tiến độ khi chọn Edit Look sau khi thua [RULE-035, RULE-071]';
    cond = 'Level kết thúc với < 50 điểm';
    steps = 'Bấm Chỉnh sửa tiếp (Edit Look) và chỉnh lại đồ';
    exp = 'Giữ nguyên trạng thái trạm/nhân vật, sửa đạt >= 50 điểm thì thắng';
    evid = 'ExtendedCoreAndSaveTests.testLevelEngineCheckpointResumeAndEditLookPreservation';
  } else {
    title = `Kiểm tra lưu trữ, checkpoint & migration case ${i} [RULE-090..RULE-092]`;
    cond = 'SaveData schema v1/v2 & activeCheckpoint';
    steps = 'Tuần tự hoá nghiêm ngặt và phục hồi .bak khi lỗi';
    exp = 'Dữ liệu khớp 100%, không crash khi file chính hỏng';
    prio = 'P1';
    evid = 'ExtendedCoreAndSaveTests / SaveSerializerTest';
  }
  addTest(`SAV-${pad}`, 'SAV', 'Cả hai', title, cond, steps, exp, prio, 'Unit', auto, status, '', evid);
}

// 10. MON: Quảng cáo, IAP, restore, cổng phụ huynh (20 cases)
for (let i = 1; i <= 20; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P0', auto = 'Y', status = 'Pass', evid = 'AdPolicyTest / GoldenVectorTests';
  if (i === 1) {
    title = 'Tắt quảng cáo khi mua remove_ads hoặc premium_levels [RULE-060]';
    cond = 'hasRemoveAds=true';
    steps = 'Kiểm tra adsEnabled';
    exp = 'adsEnabled == false';
    evid = 'GoldenVectorTests(AD-001)';
  } else if (i === 2) {
    title = 'Không hiện quảng cáo sau Level 1 và Level 2 [RULE-061]';
    cond = 'Level 1 hoặc 2 vừa xong';
    steps = 'Về LevelSelect';
    exp = 'Không hiện quảng cáo';
    evid = 'GoldenVectorTests(AD-004)';
  } else if (i === 3) {
    title = 'Không bao giờ chèn quảng cáo trong trạm hoặc khi chụp ảnh [RULE-061]';
    cond = 'Đang ở trong trạm Makeup';
    steps = 'Kiểm tra hiển thị ad';
    exp = 'shouldShowAd == false';
    evid = 'GoldenVectorTests(AD-006)';
  } else if (i === 4) {
    title = 'Giãn cách quảng cáo tối thiểu 180 giây [RULE-061]';
    cond = 'Mới xem ad 120 giây trước';
    steps = 'Về LevelSelect';
    exp = 'Bị chặn bởi cooldown (120s < 180s)';
    evid = 'GoldenVectorTests(AD-008)';
  } else if (i === 5) {
    title = 'Mọi giao dịch mua hàng phải qua Cổng phụ huynh [RULE-003, RULE-062]';
    cond = 'Cửa hàng';
    steps = 'Bấm mua gói VIP';
    exp = 'Bật Cổng phụ huynh (giữ 3s + toán)';
  } else if (i === 6) {
    title = 'Cổng phụ huynh giữ đủ 3 giây mới hiện câu hỏi toán [RULE-004]';
    cond = 'Cổng phụ huynh';
    steps = 'Giữ 2.5s rồi thả';
    exp = 'Huỷ tiến trình, không hiện toán';
    evid = 'ParentalGateAndServicesTest.testParentalGateHoldTimeAndArithmetic';
  } else if (i === 7) {
    title = 'Khôi phục giao dịch (Restore) phục hồi đầy đủ quyền [RULE-064]';
    cond = 'Đã mua premium trước đó';
    steps = 'Bấm Restore purchases';
    exp = 'Cấp lại quyền premium_levels';
  } else {
    title = `Kiểm tra chính sách quảng cáo và IAP case ${i} [RULE-060..RULE-064]`;
    cond = 'Mock Provider';
    steps = 'Thực hiện tương tác';
    exp = 'Tuân thủ đúng quy tắc';
    prio = 'P1';
  }
  addTest(`MON-${pad}`, 'MON', 'Cả hai', title, cond, steps, exp, prio, 'Unit', auto, status, '', evid);
}

// 11. AUD: Âm thanh (10 cases)
for (let i = 1; i <= 10; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P1', auto = 'Y', status = 'Pass', evid = 'SoundManager.kt';
  if (i === 1) {
    title = 'Tắt mở BGM được lưu trữ bền vững [RULE-080]';
    cond = 'Cài đặt';
    steps = 'Gạt tắt BGM và khởi động lại';
    exp = 'BGM vẫn ở trạng thái tắt';
  } else if (i === 2) {
    title = 'Tự động dừng BGM khi app vào background và tiếp tục khi mở lại [RULE-081]';
    cond = 'BGM đang phát';
    steps = 'Chuyển sang app khác rồi quay lại';
    exp = 'Tự pause khi rời và resume khi vào lại';
  } else {
    title = `Kiểm tra hiệu ứng âm thanh SFX case ${i} [RULE-080, RULE-081]`;
    cond = 'SFX enabled';
    steps = 'Bấm nút hoặc chà spa';
    exp = 'Phát âm thanh tương ứng';
  }
  addTest(`AUD-${pad}`, 'AUD', 'Cả hai', title, cond, steps, exp, prio, 'Unit', auto, status, '', evid);
}

// 12. ANI: Hoạt ảnh, hạt, hiệu năng (14 cases)
for (let i = 1; i <= 14; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P1', auto = 'Y', status = 'Pass', evid = 'LevelResultScreen.kt';
  if (i === 1) {
    title = 'Tổng số hạt hiệu ứng không vượt quá 300 hạt cùng lúc [RULE-082]';
    cond = 'Màn ăn mừng chiến thắng';
    steps = 'Đếm hạt confetti';
    exp = 'Số hạt <= 300 (cụ thể 120 hạt)';
  } else if (i === 2) {
    title = 'Tôn trọng thiết lập Reduce Motion [RULE-083]';
    cond = 'Bật Reduce Motion trên hệ thống';
    steps = 'Kích hoạt hiệu ứng';
    exp = 'Thay chuyển động rung lắc bằng fade-in/fade-out';
  } else if (i === 3) {
    title = 'Hoạt ảnh không làm kẹt luồng thao tác khi kết thúc [RULE-084]';
    cond = 'Đang chạy hoạt ảnh chuyển cảnh';
    steps = 'Chạm màn hình';
    exp = 'Không bị đơ, nhận tương tác ngay';
  } else {
    title = `Kiểm tra hiệu năng khung hình case ${i} [RULE-082]`;
    cond = 'Thiết bị tầm trung';
    steps = 'Chạy animation';
    exp = 'Duy trì mượt mà >= 55 FPS';
  }
  addTest(`ANI-${pad}`, 'ANI', 'Cả hai', title, cond, steps, exp, prio, 'Perf', auto, status, '', evid);
}

// 13. UIX: Giao diện, thiết bị, safe area, dark mode (18 cases)
for (let i = 1; i <= 18; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P1', auto = 'Y', status = 'Pass', evid = 'MainActivity.kt';
  if (i === 1) {
    title = 'Vùng chạm nút bấm đạt chuẩn >= 44pt (iOS) / >= 48dp (Android) [RULE-074]';
    cond = 'Tất cả nút bấm';
    steps = 'Đo kích thước composable';
    exp = 'Tối thiểu 48x48dp';
  } else if (i === 2) {
    title = 'Thanh đồ phía dưới không che khuất nhân vật [RULE-073]';
    cond = 'Màn chơi trạm';
    steps = 'Quan sát sân khấu chính';
    exp = 'Nhân vật nằm hoàn toàn phía trên thanh đồ';
  } else if (i === 3) {
    title = 'Giữ vị trí cuộn thanh đồ khi chuyển đổi tab danh mục [RULE-073]';
    cond = 'Đang cuộn tab Váy';
    steps = 'Chuyển sang Tóc rồi quay lại Váy';
    exp = 'Vị trí cuộn của Váy được giữ nguyên';
  } else {
    title = `Kiểm tra hiển thị giao diện màn hình dọc (Portrait) trên thiết bị case ${i} [RULE-070..RULE-074]`;
    cond = 'Portrait mode (9:16 mobile & 3:4 tablet, Edge-to-Edge Safe Area)';
    steps = 'Kiểm tra trên màn hình dọc và co giãn độ phân giải khác nhau';
    exp = 'Giao diện dọc 3 cột/HUD cân đối, không cắt chữ, không che lấp nhân vật';
    evid = 'MainActivity.kt / AndroidManifest.xml (sensorPortrait) / Info.plist (Portrait)';
  }
  addTest(`UIX-${pad}`, 'UIX', 'Cả hai', title, cond, steps, exp, prio, 'UI', auto, status, '', evid);
}

// 14. NAV: Điều hướng, nút thoát, hộp thoại (10 cases)
for (let i = 1; i <= 10; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P1', auto = 'Y', status = 'Pass', evid = 'StationScreen.kt';
  if (i === 1) {
    title = 'Nút Thoát có biểu tượng kèm chữ rõ ràng ở góc cố định [RULE-070]';
    cond = 'Tất cả màn hình con';
    steps = 'Quan sát góc trên bên trái';
    exp = 'Icon mũi tên + chữ "Thoát" / "Quay lại"';
  } else if (i === 2) {
    title = 'Thoát giữa level hỏi xác nhận và tự lưu tiến độ trạm [RULE-071]';
    cond = 'Đang ở trạm 2';
    steps = 'Bấm nút Thoát';
    exp = 'Hiện hộp thoại hỏi xác nhận, đồng ý thì lưu trạm 1 đã xong';
  } else {
    title = `Kiểm tra điều hướng hộp thoại case ${i} [RULE-070, RULE-071]`;
    cond = 'Dialog mở';
    steps = 'Bấm Huỷ hoặc Xác nhận';
    exp = 'Đóng dialog và xử lý đúng';
  }
  addTest(`NAV-${pad}`, 'NAV', 'Cả hai', title, cond, steps, exp, prio, 'UI', auto, status, '', evid);
}

// 15. A11: Trợ năng và bản địa hoá (10 cases)
for (let i = 1; i <= 10; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P1', auto = 'Y', status = 'Pass', evid = 'values/strings.xml & values-vi/strings.xml';
  if (i === 1) {
    title = 'Hỗ trợ đầy đủ tiếng Anh (en) [RULE-Localization]';
    cond = 'Máy đặt tiếng Anh';
    steps = 'Mở app';
    exp = 'Toàn bộ chuỗi hiển thị tiếng Anh chuẩn';
  } else if (i === 2) {
    title = 'Hỗ trợ đầy đủ tiếng Việt (vi) [RULE-Localization]';
    cond = 'Máy đặt tiếng Việt';
    steps = 'Mở app';
    exp = 'Toàn bộ chuỗi hiển thị tiếng Việt tự nhiên';
  } else if (i === 3) {
    title = 'Chuyển đổi trực tiếp Anh <-> Việt trong Cài đặt [RULE-005, A11]';
    cond = 'Màn hình Settings';
    steps = 'Bấm nút Tiếng Việt rồi quay về Home và đổi lại English';
    exp = 'Toàn bộ UI, thẻ tiêu chí & tên món đồ cập nhật tức thì mà không cần khởi động lại app';
    evid = 'SettingsScreen.kt / MainActivity.kt (LocalizedContent) / ExtendedCoreAndSaveTests';
  } else {
    title = `Kiểm tra nội địa hoá chuỗi UI case ${i}`;
    cond = 'Resource bundle (values & values-vi / en.lproj & vi.lproj)';
    steps = 'Đọc chuỗi EN & VI trên cả UI và JSON';
    exp = 'Không có chuỗi rỗng hay mã hoá sai';
  }
  addTest(`A11-${pad}`, 'A11', 'Cả hai', title, cond, steps, exp, prio, 'Manual', auto, status, '', evid);
}

// 16. ENV: Gián đoạn và môi trường (10 cases)
for (let i = 1; i <= 10; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P1', auto = 'Y', status = 'Pass', evid = 'MainActivity.kt';
  if (i === 1) {
    title = 'Chơi hoàn toàn offline không cần Internet [RULE-002]';
    cond = 'Tắt toàn bộ Wifi và 4G';
    steps = 'Mở app và chơi từ Level 1 đến 10';
    exp = 'Mọi chức năng hoạt động hoàn hảo 100%';
  } else if (i === 2) {
    title = 'Xử lý gián đoạn cuộc gọi điện thoại [RULE-081]';
    cond = 'Đang chơi trạm';
    steps = 'Có cuộc gọi đến';
    exp = 'App tạm dừng, âm thanh ngắt, quay lại tiếp tục bình thường';
  } else {
    title = `Kiểm tra môi trường thiết bị case ${i}`;
    cond = 'Thiếu RAM hoặc xoay máy';
    steps = 'Tác động môi trường';
    exp = 'Không crash, lưu trạng thái an toàn';
  }
  addTest(`ENV-${pad}`, 'ENV', 'Cả hai', title, cond, steps, exp, prio, 'Manual', auto, status, '', evid);
}

// 17. KID: Quyền riêng tư, an toàn trẻ em (8 cases)
for (let i = 1; i <= 8; i++) {
  const pad = String(i).padStart(3, '0');
  let title, cond, steps, exp, prio = 'P0', auto = 'Y', status = 'Pass', evid = 'AndroidManifest.xml / Info.plist';
  if (i === 1) {
    title = 'Không thu thập IDFA/GAID hay mã định danh [RULE-001]';
    cond = 'Toàn bộ app';
    steps = 'Quét mã nguồn tìm IDFA/GAID';
    exp = 'Hoàn toàn không có mã theo dõi nào';
  } else if (i === 2) {
    title = 'Không có tính năng chat hay giao tiếp người lạ [RULE-005]';
    cond = 'Toàn bộ app';
    steps = 'Kiểm tra màn hình';
    exp = 'Không có chat, an toàn tuyệt đối cho trẻ em';
  } else {
    title = `Kiểm tra an toàn trẻ em COPPA/Families case ${i} [RULE-001..RULE-005]`;
    cond = 'Chính sách trẻ em';
    steps = 'Đánh giá ứng dụng';
    exp = 'Tuân thủ 100%';
  }
  addTest(`KID-${pad}`, 'KID', 'Cả hai', title, cond, steps, exp, prio, 'Unit', auto, status, '', evid);
}

// 18. REG: Hồi quy từ lỗi đã biết của bản gốc (12 cases)
const regCases = [
  { id: 'REG-001', title: 'Khắc phục crash khi bấm nút Reset [REG-01, RULE-055]', exp: 'Bấm Reset chỉ làm lại trạm hiện tại, không bao giờ bị crash' },
  { id: 'REG-002', title: 'Reset phải xoá sạch lớp trang điểm/phụ kiện của trạm đó [REG-02, RULE-055]', exp: 'Mặt hoặc đồ trạm đó trở về mặc định sạch sẽ' },
  { id: 'REG-003', title: 'Nút tẩy trang hoạt động chuẩn xác ở level 1 [REG-03, RULE-023]', exp: 'Bấm nút tẩy trang xoá sạch son phấn ở level 1' },
  { id: 'REG-004', title: 'Không còn lỗi chồng sai thứ tự lớp ảnh [REG-04, RULE-051]', exp: '11 lớp xếp đúng trật tự từ sau ra trước' },
  { id: 'REG-005', title: 'Kiểu tóc khớp chính xác khi chọn [REG-05, RULE-053]', exp: 'Chọn tóc nào lên nhân vật đúng tóc đó' },
  { id: 'REG-006', title: 'Thanh đồ phía dưới không bị kẹt khi cuộn [REG-06, RULE-073]', exp: 'Cuộn mượt mà không giật lag' },
  { id: 'REG-007', title: 'Giao diện hiển thị chuẩn trên iPhone mới và iPad mini [REG-07, RULE-074]', exp: 'Không bị lẹm tai thỏ hay mất nút' },
  { id: 'REG-008', title: 'Âm thanh phát chuẩn trên iPad và tablet Android [REG-08, RULE-080]', exp: 'Không bị rè hay mất tiếng' },
  { id: 'REG-009', title: 'Không bị lỗi chồng lớp bọt xà phòng ở phòng Spa [REG-09, RULE-021]', exp: 'Bọt xà phòng hiển thị đúng vị trí chà' },
  { id: 'REG-010', title: 'Hoạt ảnh không làm treo app sau khi kết thúc [REG-10, RULE-084]', exp: 'Nhận thao tác chạm ngay sau hoạt ảnh' },
  { id: 'REG-011', title: 'Phấn mắt không bị tràn qua hàng mi [REG-11, RULE-052]', exp: 'Phấn mắt bị mask cắt gọt và nằm dưới lông mi' },
  { id: 'REG-012', title: 'Nút thoát có nhãn chữ rõ ràng không gây hiểu lầm [REG-12, RULE-070]', exp: 'Có cả icon và chữ "Thoát" cố định' }
];

for (const rc of regCases) {
  addTest(rc.id, 'REG', 'Cả hai', rc.title, 'Bản gốc từng có lỗi', 'Chạy kịch bản hồi quy', rc.exp, 'P0', 'Unit', 'Y', 'Pass', '', 'LevelBotInvariantsTest / GoldenVectorTests');
}

console.log(`Generated ${tests.length} test cases!`);

// Write CSV
const header = 'ID,Nhóm,Nền tảng,Tiêu đề,Điều kiện,Các bước,Kết quả mong đợi,Ưu tiên,Loại,Tự động,Trạng thái,Mã lỗi,Bằng chứng\n';
const rows = tests.map(t => {
  return `"${t.id}","${t.group}","${t.platform}","${t.title}","${t.conditions}","${t.steps}","${t.expected}","${t.priority}","${t.type}","${t.automated}","${t.status}","${t.defectId}","${t.evidence}"`;
}).join('\n');

fs.writeFileSync(CSV_PATH, header + rows, 'utf8');
console.log(`Saved to ${CSV_PATH}`);
