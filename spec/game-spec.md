# GAME SPECIFICATION: Rainbow Unicorn Candy Salon
*Single Source of Truth (SSOT) cho cả hai nền tảng iOS & Android*

---

## 1. Nguyên Tắc Cốt Lõi & An Toàn Trẻ Em

- **RULE-001 (Kid Safety - No Analytics/Trackers):** Trò chơi tuyệt đối không thu thập hoặc gửi bất kỳ thông tin nhận dạng thiết bị nào (Không IDFA trên iOS, Không GAID/AAID trên Android). Không tích hợp SDK theo dõi hay analytics bên thứ ba.
- **RULE-002 (Kid Safety - Fully Offline Playable):** Toàn bộ trò chơi có thể chơi ngoại tuyến 100% không cần kết nối Internet. Mọi tài nguyên, logic màn chơi, quy tắc mở khoá và dữ liệu kinh tế đều nằm hoàn toàn trên thiết bị.
- **RULE-003 (Kid Safety - Parental Gate Requirement):** Cổng phụ huynh là bắt buộc trước mọi hành động dẫn ra ngoài hoặc nhạy cảm:
  - Mua hàng / Khôi phục giao dịch (Store / Restore purchases).
  - Đánh giá ứng dụng (Rate app).
  - Mở liên kết chính sách bảo mật hoặc điều khoản.
- **RULE-004 (Kid Safety - Parental Gate Mechanism):** Cơ chế cổng phụ huynh gồm 2 bước liên hoàn:
  1. Giữ nút ấn liên tục trong đúng **3.0 giây** (hiển thị vòng tiến trình). Nếu thả tay trước 3 giây, tiến trình bị hủy và reset về 0.
  2. Trả lời đúng câu hỏi toán số học đơn giản (phép cộng ngẫu nhiên 2 số nguyên từ 1 đến 9, ví dụ: $a + b = ?$). Nếu trả lời sai, câu hỏi mới được tạo ra và không thực hiện thao tác nhạy cảm.
- **RULE-005 (Kid Safety - Content Appropriateness):** Không có tính năng chat, không có tương tác người dùng mở, không có nội dung bạo lực, kinh dị hay người lớn.

---

## 2. Các Chế Độ Chơi (Game Modes)

- **RULE-010 (Mode - Party Mode):**
  - Chế độ tự do sáng tạo không giới hạn.
  - Không tính điểm, không có điều kiện thua hay áp lực thời gian.
  - Người chơi chọn hoặc tạo nhân vật (Kỳ lân Unicorn hoặc Công chúa Princess).
  - Tự do thay đổi trang phục, kiểu tóc, trang điểm, phụ kiện, làm bánh và chơi với thú cưng.
  - Có thể vào trạm Chụp ảnh tiệc, tạo dáng, nhảy múa, chụp ảnh và lưu vào Thư viện ảnh.
- **RULE-011 (Mode - Story Mode Progression):**
  - Gồm chính xác **20 level** tuần tự được định nghĩa trong `spec/levels.json`.
  - **Level 1 đến 10:** Miễn phí trải nghiệm cho toàn bộ người dùng.
  - **Level 11 đến 20:** Yêu cầu quyền sở hữu gói `premium_levels`.
- **RULE-012 (Mode - Story Level Unlock):**
  - Người chơi hoàn thành thắng level $n$ (đạt ít nhất 1 sao) sẽ tự động mở khoá level $n+1$.
  - Nếu người chơi chưa có quyền `premium_levels`, khi bấm vào Level 11 (hoặc cao hơn), hệ thống hiển thị biểu tượng khoá và mở hộp thoại mua hàng qua Cổng phụ huynh.

---

## 3. Hệ Thống Trạm Chơi (Stations)

Mỗi level bao gồm từ 3 đến 6 trạm được chỉ định trong file `spec/levels.json`. Có tổng cộng 7 loại trạm chuẩn:
1. **Spa làm sạch (Cleaning Spa)**
2. **Làm tóc (Hair Salon)**
3. **Trang điểm (Makeup)**
4. **Thay đồ & Phụ kiện (Dress Up)**
5. **Làm bánh kẹo (Candy Cake Maker)**
6. **Chăm sóc thú cưng (Pet Care)**
7. **Chụp ảnh & Tiệc tùng (Photo & Party)**

- **RULE-020 (Station - Lifecycle State Machine):**
  - Mỗi trạm trong màn chơi hoạt động theo máy trạng thái 3 bước nghiêm ngặt:
    $$\text{NotStarted} \longrightarrow \text{InProgress} \longrightarrow \text{Completed}$$
  - Trạm chỉ được đánh dấu là `Completed` khi người dùng nhấn nút "Hoàn thành" của trạm đó và đạt điều kiện hoàn thành tối thiểu của trạm.
- **RULE-021 (Station - Spa Cleaning):**
  - Spa có từ 3 đến 6 vết bẩn trên nhân vật (vết bùn, vết kẹo dính, vệt nước ngọt).
  - Người chơi dùng bọt biển/vòi sen chà sạch.
  - Tỷ lệ sạch tính từ 0.0 (0%) đến 1.0 (100%).
  - Trạm chỉ cho phép hoàn thành khi tỷ lệ sạch đạt $\ge 0.80$ (80%).
- **RULE-022 (Station - Hair Salon):**
  - Chọn kiểu tóc (slot độc quyền).
  - Chọn màu tóc đơn sắc hoặc dải màu cầu vồng (Rainbow gradient).
  - Sử dụng dụng cụ: lược chải (hiệu ứng bóng tóc), máy sấy (hiệu ứng bồng bềnh), uốn tóc.
- **RULE-023 (Station - Makeup & Face Paint):**
  - Các lớp trang điểm: Phấn mắt (Eyeshadow), Son môi (Lipstick), Phấn má hồng (Blush), Vẽ mặt nghệ thuật (Face Paint).
  - Phấn mắt và màu má phải được giới hạn chặt chẽ bởi Mask tương ứng và nằm dưới lông mi. Nét vẽ/tô ngoài mask bị cắt bỏ tự động.
  - Có nút Tẩy trang (Remover) để xóa sạch trang điểm và thử lại.
- **RULE-024 (Station - Dress Up):**
  - Trang phục (Trang phục nguyên bộ hoặc áo + váy), Cánh (Wings), Sừng kỳ lân (Horn), Vương miện (Tiara/Crown), Trang sức (Dây chuyền, khuyên tai), Giày, Sticker lấp lánh.
  - Hỗ trợ đổi bảng màu sắc cho trang phục.
- **RULE-025 (Station - Candy Cake Maker):**
  - Xếp tầng bánh (từ 1 đến 4 tầng bánh kẹo).
  - Phủ kem cầu vồng, dâu tây, chocolate, marshmallow.
  - Rắc kẹo topping (kẹo sao, kẹo tim, kẹo bi cầu vồng).
- **RULE-026 (Station - Pet Care):**
  - Chăm sóc thú cưng kẹo ngọt (Bé rồng con hoặc Bé cún bông cầu vồng).
  - Cho ăn kẹo ngọt, tắm bọt xà phòng thơm và chải lông mượt.
- **RULE-027 (Station - Photo & Party):**
  - Nhân vật tạo dáng nhảy múa ăn mừng tiệc kẹo ngọt.
  - Chọn phông nền tiệc (Disco kẹo, Lâu đài mây hồng, Rừng cầu vồng).
  - Nút chụp ảnh: phát âm thanh tiếng màn trập camera, nháy sáng màn hình và lưu ảnh vào thư viện.

---

## 4. Công Thức Tính Điểm, Sao & Điều Kiện Thắng/Chưa Đạt

- **RULE-030 (Customer Request Card):**
  - Mỗi level trong Story Mode có một "Thẻ yêu cầu khách hàng" gồm từ 3 đến 6 tiêu chí (criteria).
  - Ví dụ:
    1. Tóc: Màu cầu vồng (`hair_color: "rainbow"`)
    2. Trang điểm: Phấn mắt hồng dâu (`eyeshadow: "pink"`)
    3. Phụ kiện: Vương miện hình ngôi sao (`tiara: "star_crown"`)
    4. Bánh kẹo: Bánh tối thiểu 2 tầng (`cake_layers >= 2`)
- **RULE-031 (Match Score Formula):**
  $$\text{matchScore} = \frac{\text{Số tiêu chí đạt}}{\text{Tổng số tiêu chí}}$$
  Giá trị nằm trong khoảng $[0.0, 1.0]$.
- **RULE-032 (Quality Score Formula):**
  $$\text{qualityScore} = \frac{\sum_{i=1}^{M} q_i}{M}$$
  Trong đó $M$ là số trạm bắt buộc trong level, và $q_i \in [0.0, 1.0]$ là độ hoàn thiện của từng trạm:
  - Spa: Tỷ lệ vết bẩn được làm sạch.
  - Makeup: Tỷ lệ nét vẽ nằm trúng vùng mask quy định.
  - Cake: Tỷ lệ đáp ứng số tầng và số lượng topping hợp lệ.
  - Các trạm khác: Mặc định $1.0$ khi hoàn thành thao tác.
- **RULE-033 (Total Score Formula):**
  $$\text{totalScore} = \text{round}\left(100 \times \left(0.6 \times \text{matchScore} + 0.4 \times \text{qualityScore}\right)\right)$$
  Kết quả là số nguyên trong đoạn $[0, 100]$.
- **RULE-034 (Star Rating Thresholds):**
  $$\text{stars} = \begin{cases} 
  3 & \text{khi } \text{totalScore} \ge 90 \\
  2 & \text{khi } 75 \le \text{totalScore} < 90 \\
  1 & \text{khi } 50 \le \text{totalScore} < 75 \\
  0 & \text{khi } \text{totalScore} < 50 
  \end{cases}$$
- **RULE-035 (Win / Try Again Condition):**
  - **Thắng (Victory):** Hoàn thành tất cả các trạm bắt buộc VÀ $\text{totalScore} \ge 50$ (đạt ít nhất 1 sao).
  - **Chưa đạt (Try Again):** $\text{totalScore} < 50$ hoặc chưa hoàn thành đủ các trạm bắt buộc.
  - Tuyệt đối không có Game Over, không có đồng hồ đếm ngược gây căng thẳng. Người chơi được tự do bấm "Chỉnh sửa tiếp" (Edit look) để đạt thêm tiêu chí hoặc chọn "Chơi lại" (Replay).
- **RULE-036 (Monotonic Best Stars):**
  - Số sao tốt nhất của mỗi level (`bestStars`) là đơn điệu tăng: chỉ ghi nhận giá trị cao nhất từng đạt, không bao giờ bị giảm khi chơi lại đạt điểm thấp hơn:
    $$\text{bestStars}_{\text{mới}} = \max\left(\text{bestStars}_{\text{cũ}}, \text{stars}\right)$$
- **RULE-037 (First-Time Star Candy Reward):**
  - Thưởng **10 kẹo** cho mỗi ngôi sao tăng thêm ở lần đầu tiên đạt được:
    $$\Delta\text{stars} = \max\left(0, \text{stars} - \text{bestStars}_{\text{cũ}}\right)$$
    $$\text{candyEarned} = \Delta\text{stars} \times 10$$
  - Chơi lại một level đã đạt 3 sao từ trước sẽ nhận $\text{candyEarned} = 0$, ngăn chặn triệt để hành vi farm kẹo vô tận bằng cách cày đi cày lại level cũ.

---

## 5. Kinh Tế Trong Game, Mở Khoá & Cửa Hàng (Economy & Shop)

- **RULE-040 (Non-negative Candy Balance):**
  - Số kẹo người chơi sở hữu (`candyBalance`) luôn là số nguyên $\ge 0$.
  - Mọi thao tác trừ kẹo khi số dư nhỏ hơn giá tiền của món đồ đều bị từ chối (`InsufficientCandyException`).
- **RULE-041 (Item Pricing & Catalog Constraints):**
  - Mọi món đồ được khai báo trong `spec/items.json` với trường `unlockedByDefault` và `candyPrice`.
  - Giá kẹo: $0$ đối với đồ mặc định, từ $15$ đến $50$ đối với đồ mở khoá.
  - **Giới hạn tỷ lệ đồ khoá:** Trong mỗi danh mục (category), số lượng đồ bị khoá không được vượt quá **25%** tổng số đồ của danh mục đó:
    $$\frac{N_{\text{locked}}}{N_{\text{total}}} \le 0.25$$
- **RULE-042 (No Real-Money Exclusive Items):**
  - Không có bất kỳ món trang phục hay phụ kiện nào chỉ bán độc quyền bằng tiền thật. Mọi món đồ trong cửa hàng đều có thể mở khóa bằng kẹo tích lũy trong game hoặc tùy chọn xem quảng cáo thưởng (Rewarded Ad).
- **RULE-043 (Idempotent Purchases):**
  - Một món đồ đã mở khóa thì vĩnh viễn thuộc về người chơi.
  - Thao tác mua lại một món đồ đã sở hữu phải bị chặn ngay lập tức và tuyệt đối không trừ kẹo lần thứ hai.
- **RULE-044 (Rewarded Ad Unlock):**
  - Người chơi có thể tuỳ chọn xem 1 video quảng cáo thưởng để mở khoá tạm thời hoặc vĩnh viễn 1 món đồ khoá mà không tốn kẹo.

---

## 6. Mô Hình Nhân Vật, Thứ Tự 11 Lớp Đồ Hoạ & Undo/Redo

- **RULE-050 (Character Attributes):**
  - Nhân vật gồm: Chủng tộc (`unicorn` hoặc `princess`), Màu da (`skinTone`), Màu mắt (`eyeColor`), Kiểu tóc (`hairStyleId`), Màu tóc (`hairColorId`), Lớp trang điểm (`makeupState`), Trang phục (`outfitId`), Cánh (`wingsId`), Sừng kỳ lân (`hornId`), Vương miện (`tiaraId`), Trang sức (`jewelryId`), Vẽ mặt (`facePaints`), Sticker dán (`stickers`).
- **RULE-051 (Strict 11-Layer Rendering Order):**
  Trình kết xuất đồ hoạ (`LayerComposer`) phải xếp chồng các lớp từ sau ra trước (Back-to-Front) theo đúng thứ tự 11 tầng sau:
  1. `Layer 1: Background` (Phông nền cảnh/phòng)
  2. `Layer 2: Back Wings` (Đôi cánh phía sau lưng)
  3. `Layer 3: Back Hair` (Phần tóc đuôi/bờm phía sau gáy)
  4. `Layer 4: Body & Skin` (Thân hình, khuôn mặt, làn da)
  5. `Layer 5: Makeup (Clipped by Mask)` (Phấn mắt, phấn má, son môi - cắt theo mask hình học)
  6. `Layer 6: Eyes & Eyelashes` (Mắt, con ngươi và hàng lông mi - luôn đè lên phấn mắt)
  7. `Layer 7: Outfit` (Trang phục chính: váy, áo, quần)
  8. `Layer 8: Front Hair` (Tóc mái, tóc trước trán)
  9. `Layer 9: Accessories & Jewelry` (Khuyên tai, vòng cổ, sừng kỳ lân)
  10. `Layer 10: Tiara & Crown` (Vương miện trên đầu)
  11. `Layer 11: Stickers & Sparkles` (Sticker trang trí, vẽ mặt nghệ thuật, hạt lấp lánh trên cùng)
- **RULE-052 (Eyeshadow Mask Clipping):**
  - Lớp phấn mắt (Layer 5) bắt buộc phải nằm dưới lông mi (Layer 6) và bị giới hạn tuyệt đối trong vùng mí mắt trên (Eyelid Mask). Bất kỳ nét vẽ nào tràn ra ngoài vùng mask đều bị cắt bỏ, ngăn ngừa triệt để lỗi lem màu qua mi mắt.
- **RULE-053 (Exclusive Slots vs Multi-item Slots):**
  - **Slot độc quyền (Exclusive):** Kiểu tóc, Trang phục, Đôi cánh, Vương miện, Sừng, Giày. Khi người dùng chọn một món mới trong slot này, món cũ bị thay thế hoàn toàn.
  - **Slot nhiều món (Multi-item):**
    - Vẽ mặt (`facePaints`): Tối đa **3** họa tiết vẽ mặt cùng lúc. Chọn món thứ 4 sẽ thay thế họa tiết cũ nhất (FIFO).
    - Sticker trang trí (`stickers`): Tối đa **10** sticker trên nhân vật. Chọn sticker thứ 11 sẽ từ chối thêm và rung nhẹ cảnh báo.
- **RULE-054 (Undo / Redo 20 Steps):**
  - Ngăn xếp Hoàn tác / Làm lại (UndoStack) ghi nhận tối đa **20 bước** thay đổi thuộc tính ngoại hình trong phiên chơi hiện tại.
  - Khi thực hiện hành động mới, toàn bộ nhánh Redo phía sau bị xoá bỏ.
- **RULE-055 (Station Reset Scope):**
  - Nút "Reset" đưa nhân vật về trạng thái mặc định của trạm hiện tại (ví dụ: xoá sạch lớp trang điểm trong trạm Makeup; xoá toàn bộ topping bánh trong trạm Cake).
  - Trước khi reset, bắt buộc phải hiển thị Hộp thoại xác nhận (Confirmation Dialog): "Bạn có chắc chắn muốn làm lại trạm này không?".
  - **Tuyệt đối không ảnh hưởng đến:** Số kẹo sở hữu, số sao đã đạt của các màn chơi, tiến độ Story Mode và các quyền mua hàng IAP đã sở hữu.

---

## 7. Chính Sách Quảng Cáo & Giao Dịch Mua Hàng (AdPolicy & In-App Purchase)

- **RULE-060 (Ad Eligibility Flag):**
  $$\text{adsEnabled} = \neg\left(\text{hasRemoveAds} \lor \text{hasPremiumLevels}\right)$$
  Nếu người chơi đã mua `remove_ads` hoặc `premium_levels`, toàn bộ quảng cáo xen kẽ (Interstitial Ads) đều bị tắt vĩnh viễn.
- **RULE-061 (Interstitial Ad Placement & Cooldown):**
  - Quảng cáo xen kẽ **CHỈ ĐƯỢC PHÉP XUẤT HIỆN** khi người chơi hoàn thành màn chơi hoặc thoát và quay trở về màn hình chọn level (`LevelSelectScreen`).
  - **Thời gian giãn cách (Cooldown):** Ít nhất **180 giây** kể từ lần hiển thị quảng cáo gần nhất.
  - **Tần suất level:** Tối đa 1 lần sau mỗi 2 level vượt qua.
  - **Cấm hoàn toàn:**
    - Tuyệt đối không hiển thị sau khi chơi Level 1 và Level 2.
    - Tuyệt đối không hiển thị khi người chơi đang ở trong bất kỳ trạm nào.
    - Tuyệt đối không hiển thị ở màn hình Chụp ảnh / Tiệc tùng.
- **RULE-062 (Parental Gate for All Purchases):**
  - Bất kỳ yêu cầu mua hàng nào đối với `premium_levels` hoặc `remove_ads` đều phải vượt qua Cổng phụ huynh (RULE-003, RULE-004) trước khi gọi API thanh toán.
- **RULE-063 (Transaction Failure & Cancellation Safety):**
  - Nếu giao dịch thanh toán bị người dùng hủy bỏ, thẻ thanh toán từ chối hoặc mạng gặp sự cố, hệ thống giữ nguyên trạng thái cũ, không cấp quyền nhầm và không hiển thị thông báo lỗi khó hiểu cho trẻ.
- **RULE-064 (Purchase Restoration):**
  - Nút "Khôi phục giao dịch" (Restore Purchases) nằm trong màn hình Cài đặt (sau Cổng phụ huynh).
  - Khi bấm, hệ thống đồng bộ lại biên lai từ StoreKit 2 / Google Play Billing và khôi phục đầy đủ quyền `premium_levels` hoặc `remove_ads` nếu người dùng đã từng mua trước đó.

---

## 8. Điều Hướng & Trải Nghiệm Người Dùng (UX & Navigation)

- **RULE-070 (Consistent Back/Exit Button with Icon + Label):**
  - Nút Quay lại / Thoát (`Back / Exit`) luôn có biểu tượng (icon mũi tên hoặc biểu tượng ngôi nhà) kèm nhãn chữ rõ ràng (`"Quay lại"` / `"Thoát"`) ở vị trí cố định (góc trên bên trái màn hình).
  - Tránh tuyệt đối lỗi chỉ dùng một biểu tượng không nhãn gây nhầm lẫn cho trẻ em.
- **RULE-071 (Mid-Level Exit Confirmation & Auto-Save):**
  - Khi người chơi bấm nút Thoát khi đang chơi dở một level trong Story Mode, hệ thống hiển thị hộp thoại xác nhận: "Bạn có muốn tạm dừng và quay về menu không?".
  - Nếu xác nhận thoát, hệ thống tự động lưu lại trạng thái các trạm đã hoàn thành trong level đó để người chơi có thể tiếp tục sau.
- **RULE-072 (WYSIWYG Item Preview):**
  - Mỗi ô chọn món đồ trong khay thanh đồ phải hiển thị hình ảnh xem trước (Thumbnail preview) giống hệt với món đồ khi được khoác lên nhân vật (màu sắc, họa tiết, hình dáng chính xác).
- **RULE-073 (Bottom Tray Scroll Retention & Non-overlapping):**
  - Khay chứa đồ phía dưới cuộn ngang mượt mà.
  - Khi chuyển đổi giữa các thẻ danh mục (ví dụ từ Tóc sang Váy), vị trí cuộn của từng thẻ được lưu lại.
  - Khay đồ có chiều cao cố định và không được che khuất nhân vật chính ở khu vực sân khấu trung tâm.
- **RULE-074 (Minimum Touch Target Sizes):**
  - Mọi nút bấm tương tác và ô chọn đồ phải có kích thước vùng chạm tối thiểu đạt chuẩn tiếp cận:
    - iOS: Tối thiểu **44 x 44 pt**.
    - Android: Tối thiểu **48 x 48 dp**.

---

## 9. Âm Thanh, Hoạt Ảnh & Hiệu Năng

- **RULE-080 (BGM & SFX Settings Persistence):**
  - Cung cấp nút bật/tắt Nhạc nền (`bgmEnabled`) và Âm thanh tương tác (`sfxEnabled`).
  - Trạng thái cài đặt được lưu trữ bền vững (persistent) qua các phiên chơi.
- **RULE-081 (Audio Interruption Handling):**
  - Tự động tạm dừng nhạc nền khi có cuộc gọi đến, phát chuông báo thức hoặc khi ứng dụng chuyển vào chế độ nền (`background`).
  - Tự động tiếp tục phát khi ứng dụng quay trở lại tiền cảnh (`foreground`), nếu `bgmEnabled == true`.
- **RULE-082 (Particle System Limits & Performance):**
  - Tổng số hạt hiệu ứng lấp lánh (sparkles, bubbles, confetti) hiển thị đồng thời trên màn hình không được vượt quá **300 hạt**.
  - Tốc độ khung hình duy trì ổn định $\ge 55$ FPS trên thiết bị tầm trung.
- **RULE-083 (Reduce Motion Compliance):**
  - Tôn trọng thiết lập "Reduce Motion" của hệ điều hành iOS và hệ số thời lượng hoạt ảnh (Animator duration scale) của Android.
  - Khi chế độ giảm chuyển động được kích hoạt, các hiệu ứng bay lắc mạnh, rung lắc màn hình và hạt phân tán được thay bằng hiệu ứng mờ dần (fade-in / fade-out) nhẹ nhàng.
- **RULE-084 (Non-blocking Animation Cancellation):**
  - Hoạt ảnh chuyển cảnh hoặc ăn mừng chiến thắng không được chặn luồng thao tác của người dùng sau khi kết thúc hoặc khi người dùng chạm bỏ qua.

---

## 10. Lưu Trữ Dữ Liệu & Quản Lý Thư Viện Ảnh (Persistence & Gallery)

- **RULE-090 (Auto-Save Triggers):**
  - Dữ liệu game tự động lưu vào đĩa trong 3 trường hợp:
    1. Khi hoàn thành xong bất kỳ trạm chơi nào.
    2. Khi ứng dụng bị chuyển vào chế độ nền (`onPause` / `sceneDidEnterBackground`).
    3. Khi đóng hoặc thoát ứng dụng.
- **RULE-091 (Schema Versioning & Migration):**
  - File lưu dữ liệu có trường `schemaVersion` (bắt đầu từ phiên bản 1).
  - Khi nâng cấp phiên bản game với cấu trúc mới, hệ thống tự động chạy trình di chuyển dữ liệu (`SaveDataMigrator`) mà không làm mất số kẹo, số sao hay các màn đã mở.
- **RULE-092 (Corrupt Save Recovery):**
  - Khi lưu dữ liệu, hệ thống tạo bản sao dự phòng `save_game.bak`.
  - Nếu file chính `save_game.json` bị lỗi cú pháp JSON hoặc lỗi đọc ghi, hệ thống tự động khôi phục từ file `.bak` mà không bao giờ bị crash ứng dụng.
- **RULE-093 (Photo Gallery Capacity & Clear Warnings):**
  - Thư viện ảnh trong game chứa tối đa **50 ảnh/bức hình tạo dáng**.
  - Khi thư viện đạt 50 ảnh, hệ thống hiển thị thông báo rõ ràng "Thư viện đã đầy (50/50). Hãy xóa bớt ảnh cũ để chụp thêm!" và nút chụp bị khóa cho đến khi có chỗ trống. Tuyệt đối không tự ý xóa ngầm ảnh cũ của trẻ nhỏ.
- **RULE-094 (Platform Native Photo Export):**
  - Khi người dùng xuất ảnh ra thư viện hệ thống thiết bị:
    - **iOS:** Yêu cầu quyền "Chỉ thêm ảnh" (`NSPhotoLibraryAddUsageDescription`) qua `PHPhotoLibrary.shared().performChanges`.
    - **Android:** Android 10+ (API 29+) lưu thông qua `MediaStore.Images.Media` mà không cần xin bất kỳ quyền nguy hiểm nào; quyền `WRITE_EXTERNAL_STORAGE` chỉ khai báo với `android:maxSdkVersion="28"`.
