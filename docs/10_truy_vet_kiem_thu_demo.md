# 10 – Truy vết, Kiểm thử và Demo

## 1. Ma trận Truy vết FR ↔ UC ↔ Bảng ↔ Màn hình ↔ BR

| FR | Tên | UC | Bảng chính | Màn hình | BR |
|----|-----|----|------------|----------|----|
| FR01 | Quản lý danh mục thuốc | UC01 | Thuoc, NhomThuoc | M20 | — |
| FR02 | Quản lý mã vạch | UC02, UC03 | MaVachThuoc | M20, M06 | BR06 |
| FR03 | Quản lý NCC, khu vực, nhà thuốc | UC05 | NhaCungCap, KhuVuc, NhaThuoc | M21 | — |
| FR04 | Quản lý người dùng & RBAC | UC26 | NguoiDung | M22 | BR13 |
| FR05 | Nhập kho | UC12, UC13 | PhieuKho, ChiTietPhieuKho, LoThuoc, TonKho, BienDongKho | M07 | BR07, BR12 |
| FR06 | Bán hàng POS + quét mã vạch | UC06, UC07 | GiaoDich, ChiTietGiaoDich, TonKho, MaVachThuoc | M03 | BR02, BR03, BR04, BR12, BR15 |
| FR07 | Bán theo đơn | UC07 | DonThuoc, ChiTietDonThuoc | M03, M04 | BR01, BR05 |
| FR08 | Quản lý khách hàng & đơn thuốc | UC09, UC10 | KhachHang, DonThuoc, ChiTietDonThuoc | M04, M05 | BR05 |
| FR09 | Điều chuyển kho | UC14, UC15 | PhieuKho, ChiTietPhieuKho, TonKho, BienDongKho | M08, M11 | BR08, BR12, BR14 |
| FR10 | Xuất hủy | UC16, UC17 | PhieuKho, ChiTietPhieuKho, TonKho, BienDongKho | M09, M11 | BR09, BR12, BR14 |
| FR11 | Hủy giao dịch | UC08 | GiaoDich, TonKho, BienDongKho, ChiTietDonThuoc | M16 | BR10, BR12, BR14 |
| FR12 | Cảnh báo hạn dùng/tồn thấp/chậm | UC19, UC20 | vw_CanhBaoHanDung, vw_CanhBaoTonThap, vw_TonChamLuanChuyen | M13, M14 | BR11, BR13 |
| FR13 | Định mức tồn | — | DinhMucTon | M15 | BR11 |
| FR14 | Dashboard nhà thuốc | UC21 | GiaoDich, TonKho, DinhMucTon | M02 | BR13 |
| FR15 | Dashboard chuỗi | UC22 | Mọi bảng chính | M17 | BR13 |
| FR16 | Phân tích nhu cầu | UC23 | vw_BanHangChiTiet, KhuVuc | M18 | BR13, BR16 |
| FR17 | Đề xuất nhập hàng | UC24 | sp_DeXuatNhapHang, DinhMucTon, CauHinh | M19 | — |
| FR18 | Nhật ký kiểm toán | UC28 | AuditLog | M23 | BR14 |
| FR19 | Đối soát tồn kho | UC25, UC29 | BienDongKho, TonKho | M23 | BR12 |
| FR20 | Cấu hình hệ thống | UC27 | CauHinh | M23 | BR05, BR10, BR11 |

---

## 2. Rủi ro và Giảm thiểu

| # | Rủi ro | Mức độ | Giảm thiểu |
|---|--------|--------|------------|
| R01 | Deadlock khi bán lô cuối cùng đồng thời | Cao | UPDLOCK + ROWLOCK; khóa theo thứ tự lo_thuoc_id; retry 3 lần |
| R02 | Tồn TonKho không khớp BienDongKho | Cao | Đối soát định kỳ (FR19); không cho sửa BienDongKho |
| R03 | Mã vạch EAN13 sai checkdigit được nhập | Trung | Server validate bằng Ean13Validator; UI báo lỗi ngay |
| R04 | Bán vượt số lượng đơn (BR01) | Cao | Validate chặt tại Service; test case TC04, TC05 |
| R05 | Hủy giao dịch sau giờ cho phép | Trung | Kiểm tra thời gian UTC tại Service (BR10); test TC14 |
| R06 | Parse GS1 sai cú pháp | Thấp | Gs1Parser có unit test; fallback về thủ công |
| R07 | Dữ liệu khách hàng thật bị lộ | Cao | Toàn bộ dùng dữ liệu giả lập; che SĐT (BR16) |
| R08 | Điều chuyển thiếu tồn do race condition | Trung | Kiểm tra tồn lại trong transaction duyệt (SELECT WITH UPDLOCK) |

---

## 3. Điểm khác biệt so với đề tài tương tự

| Hệ thống | IS009 (đề này) | Khác biệt |
|----------|---------------|-----------|
| IS112 – WMS quản lý kho | Quản lý kho chung | IS009 chuyên dược phẩm: FEFO bắt buộc, hạn dùng, đơn thuốc; WMS không có nghiệp vụ y tế |
| IS126 – Dược phẩm blockchain | Cũng dược phẩm | IS009 là HTTT nghiệp vụ chuỗi nhà thuốc thực tiễn; IS126 tập trung chuỗi cung ứng + truy xuất nguồn gốc blockchain |
| IS083 – Kiểm kê SKU | Quản lý tồn kho | IS083 là kiểm kê định kỳ; IS009 theo dõi real-time theo lô + GS1 barcode |
| Hệ thống của giảng viên gợi ý (6 bảng) | Mở rộng từ 6 bảng | IS009 thêm: MaVachThuoc, BienDongKho, AuditLog, CauHinh, KhuVuc (phân tích địa lý), KhachHang.khu_vuc_id |

---

## 4. Kế hoạch Kiểm thử – 24 Kịch bản

| # | Nhóm | Mô tả kịch bản |
|---|------|---------------|
| TC01 | Bán FEFO | Bán thuốc → hệ thống chọn lô gần hạn nhất |
| TC02 | Bán FEFO | Bán khi tồn = 0 → báo lỗi |
| TC03 | Bán FEFO | Hai kho, chọn đúng lô tại nhà thuốc mình |
| TC04 | Bán kê đơn | Bán đúng theo đơn còn hiệu lực |
| TC05 | Bán kê đơn | Bán vượt số lượng cho phép → từ chối |
| TC06 | Bán kê đơn | Đơn hết hiệu lực (quá so_ngay_hieu_luc) → từ chối |
| TC07 | Bán kê đơn | Bán tại nhà thuốc khác nơi tiếp nhận → từ chối |
| TC08 | Mã vạch | Quét EAN13 hợp lệ → hiển thị thuốc |
| TC09 | Mã vạch | Quét EAN13 sai checkdigit → báo lỗi |
| TC10 | Mã vạch | Quét GS1 DataMatrix → tự điền lô và hạn dùng |
| TC11 | Nhập kho | Nhập lô mới hợp lệ → TonKho tăng, BienDongKho ghi |
| TC12 | Nhập kho | Nhập lô đã có (cùng thuoc_id, so_lo) → cộng dồn |
| TC13 | Nhập kho | Hạn dùng ≤ ngày nhập → từ chối (BR07) |
| TC14 | Điều chuyển | Tạo + duyệt → TonKho 2 nhà thuốc cập nhật đúng |
| TC15 | Điều chuyển | Tạo điều chuyển từ nhà thuốc đến chính nhà thuốc → từ chối (BR08) |
| TC16 | Điều chuyển | Duyệt khi tồn không đủ (do bán sau khi tạo phiếu) → xử lý |
| TC17 | Hủy giao dịch | Hủy trong 24h → hoàn kho đúng lô |
| TC18 | Hủy giao dịch | Hủy sau 24h → từ chối (BR10) |
| TC19 | Hủy giao dịch | Hủy GD có đơn thuốc → hoàn so_luong_da_ban |
| TC20 | Đồng thời | Hai dược sĩ bán lô cuối → chỉ 1 thành công |
| TC21 | Phạm vi vai trò | Dược sĩ xem dashboard nhà thuốc khác → bị từ chối (BR13) |
| TC22 | Phạm vi vai trò | QUANTRI xem tất cả nhà thuốc → thành công |
| TC23 | Cảnh báo | Lô ≤30 ngày hiển thị cảnh báo cam trên POS |
| TC24 | Cảnh báo | Lô hết hạn không bán được (BR03) |

---

## 5. Test Case Chi tiết (10 TC mẫu)

### TC01 – Bán FEFO: hệ thống chọn lô gần hạn nhất

| Mục | Nội dung |
|-----|---------|
| Mã | TC01 |
| Mục tiêu | Xác nhận hệ thống tự áp dụng FEFO khi bán (BR02) |
| Điều kiện tiên quyết | Nhà thuốc NT001 có 2 lô thuốc A: LO_OLD (hạn 2027-01-01, tồn 10) và LO_NEW (hạn 2027-06-01, tồn 5). Dược sĩ đã đăng nhập. |
| Dữ liệu đầu vào | Quét mã vạch thuốc A; số lượng = 2 |
| Các bước | 1. Dược sĩ vào M03 POS. 2. Quét mã vạch thuốc A. 3. Hệ thống trả về thông tin. 4. Xem lô hiển thị. |
| Kết quả mong đợi | Lô hiển thị là LO_OLD (hạn 2027-01-01); không phải LO_NEW |
| Kết quả thực tế | |
| Trạng thái | |

---

### TC04 – Bán kê đơn hợp lệ

| Mục | Nội dung |
|-----|---------|
| Mã | TC04 |
| Mục tiêu | Bán thuốc kê đơn khi đơn còn hiệu lực, nhà thuốc đúng, số lượng hợp lệ (BR01) |
| Điều kiện tiên quyết | DonThuoc D001 tiếp nhận tại NT001; ngay_ke_don = hôm nay; so_ngay_hieu_luc=5; thuốc TH001 kê 10 viên, bán được 0. |
| Dữ liệu đầu vào | don_thuoc_id=D001; thuoc_id=TH001; so_luong=5 |
| Các bước | 1. Dược sĩ vào M03, chọn tab "Theo đơn". 2. Nhập mã đơn D001. 3. Hệ thống hiển thị đơn. 4. Thêm TH001 số lượng 5. 5. Xác nhận thanh toán. |
| Kết quả mong đợi | GiaoDich HOAN_THANH; ChiTietDonThuoc.so_luong_da_ban = 5; TonKho giảm 5; BienDongKho ghi BAN |
| Kết quả thực tế | |
| Trạng thái | |

---

### TC05 – Bán vượt số lượng kê đơn

| Mục | Nội dung |
|-----|---------|
| Mã | TC05 |
| Mục tiêu | Hệ thống từ chối bán vượt so_luong_ke (BR01) |
| Điều kiện tiên quyết | Đơn D001; TH001 kê 10 viên; đã bán 8 viên → còn 2 |
| Dữ liệu đầu vào | don_thuoc_id=D001; thuoc_id=TH001; so_luong=5 |
| Các bước | 1. Chọn đơn D001. 2. Thêm TH001 số lượng 5. 3. Xác nhận. |
| Kết quả mong đợi | Thông báo lỗi: "Số lượng bán (5) vượt số lượng cho phép (2)"; chặn giao dịch |
| Kết quả thực tế | |
| Trạng thái | |

---

### TC08 – Quét EAN13 hợp lệ

| Mục | Nội dung |
|-----|---------|
| Mã | TC08 |
| Mục tiêu | Quét mã EAN13 đúng → tra được thuốc (FR02, BR06) |
| Điều kiện tiên quyết | MaVachThuoc có bản ghi: ma_vach='6938654001234', thuoc_id=10 |
| Dữ liệu đầu vào | raw_barcode = '6938654001234' |
| Các bước | 1. Vào M06 Tra cứu mã vạch. 2. Nhập hoặc quét '6938654001234'. 3. Nhấn Tìm. |
| Kết quả mong đợi | Hiển thị thông tin thuốc id=10; tồn kho theo lô |
| Kết quả thực tế | |
| Trạng thái | |

---

### TC09 – Quét EAN13 sai checkdigit

| Mục | Nội dung |
|-----|---------|
| Mã | TC09 |
| Mục tiêu | Server từ chối EAN13 sai checkdigit (BR06) |
| Điều kiện tiên quyết | — |
| Dữ liệu đầu vào | raw_barcode = '6938654001235' (sai chữ số kiểm) |
| Các bước | 1. Nhập mã '6938654001235'. 2. Nhấn Tìm. |
| Kết quả mong đợi | Thông báo "Mã vạch EAN13 không hợp lệ (sai checkdigit)" |
| Kết quả thực tế | |
| Trạng thái | |

---

### TC13 – Nhập lô có hạn dùng ≤ ngày nhập

| Mục | Nội dung |
|-----|---------|
| Mã | TC13 |
| Mục tiêu | BR07: không cho nhập lô khi hạn dùng ≤ ngày nhập |
| Điều kiện tiên quyết | Ngày hôm nay 2026-10-28 |
| Dữ liệu đầu vào | thuoc_id=10; so_lo='BADLOT'; han_dung='2026-10-28'; so_luong=50 |
| Các bước | 1. Tạo phiếu nhập, thêm dòng với han_dung = hôm nay. 2. Nhấn Xác nhận. |
| Kết quả mong đợi | Thông báo lỗi: "Hạn dùng phải sau ngày nhập kho"; dòng không được thêm |
| Kết quả thực tế | |
| Trạng thái | |

---

### TC17 – Hủy giao dịch trong 24h

| Mục | Nội dung |
|-----|---------|
| Mã | TC17 |
| Mục tiêu | Hủy GD hợp lệ → hoàn kho đúng lô (BR10, BR12) |
| Điều kiện tiên quyết | GD001 HOAN_THANH tại NT001; bán LO_A 5 viên; tạo 2 giờ trước |
| Dữ liệu đầu vào | giao_dich_id=GD001; ly_do="Khách đổi ý" |
| Các bước | 1. Quản lý vào M16, tìm GD001. 2. Nhập lý do. 3. Xác nhận hủy. |
| Kết quả mong đợi | GiaoDich.trang_thai=DA_HUY; TonKho[NT001][LO_A] tăng 5; BienDongKho ghi HOAN_KHO; AuditLog ghi |
| Kết quả thực tế | |
| Trạng thái | |

---

### TC18 – Hủy giao dịch sau 24h

| Mục | Nội dung |
|-----|---------|
| Mã | TC18 |
| Mục tiêu | Từ chối hủy GD ngoài thời gian (BR10) |
| Điều kiện tiên quyết | GD002 tạo 25 giờ trước; CauHinh gio_cho_phep_huy_giao_dich=24 |
| Dữ liệu đầu vào | giao_dich_id=GD002; ly_do="Test" |
| Các bước | 1. Quản lý vào M16, tìm GD002. 2. Nhấn Hủy. |
| Kết quả mong đợi | Thông báo: "Đã quá thời gian cho phép hủy (24 giờ)"; GD không thay đổi |
| Kết quả thực tế | |
| Trạng thái | |

---

### TC20 – Đồng thời bán lô cuối

| Mục | Nội dung |
|-----|---------|
| Mã | TC20 |
| Mục tiêu | Chỉ một giao dịch thành công khi hai DS bán cùng lúc (BR04) |
| Điều kiện tiên quyết | TonKho[NT001][LO_X] = 1; hai tab trình duyệt của DS_A và DS_B |
| Dữ liệu đầu vào | Cả hai đều cố bán 1 viên từ LO_X |
| Các bước | 1. DS_A và DS_B cùng quét mã, nhập SL=1. 2. Cả hai nhấn "Xác nhận" gần như cùng lúc. |
| Kết quả mong đợi | Đúng 1 GD HOAN_THANH; 1 GD nhận lỗi "Hết hàng"; TonKho[LO_X]=0 |
| Kết quả thực tế | |
| Trạng thái | |

---

### TC21 – Phạm vi vai trò: Dược sĩ xem nhà thuốc khác

| Mục | Nội dung |
|-----|---------|
| Mã | TC21 |
| Mục tiêu | BR13: DUOCSI không thấy dữ liệu nhà thuốc khác |
| Điều kiện tiên quyết | DS_A thuộc NT001; NT002 tồn tại trong hệ thống |
| Dữ liệu đầu vào | GET /api/ton-kho?nha_thuoc_id=2 (NT002) |
| Các bước | 1. DS_A đăng nhập. 2. Gọi API với nha_thuoc_id=2. |
| Kết quả mong đợi | HTTP 403 Forbidden; không trả dữ liệu NT002 |
| Kết quả thực tế | |
| Trạng thái | |

---

## 6. Checklist Mã vạch

| # | Kiểm tra | Kết quả |
|---|----------|---------|
| CB01 | EAN13 đúng 13 ký tự số và hợp lệ checkdigit → tra được thuốc | |
| CB02 | EAN13 sai checkdigit → báo lỗi rõ ràng | |
| CB03 | EAN13 < 13 ký tự → báo lỗi | |
| CB04 | Code128 nội bộ tìm đúng thuốc → hiển thị | |
| CB05 | Mã không tồn tại trong MaVachThuoc → "Không tìm thấy" | |
| CB06 | GS1 DataMatrix (01)+(17)+(10) parse đúng GTIN, hạn, lô | |
| CB07 | GS1 thiếu AI → cảnh báo, yêu cầu nhập thủ công | |
| CB08 | Quét camera ra mã → xử lý như USB | |
| CB09 | Quét lô hết hạn tại POS → từ chối bán, hiển thị lý do | |
| CB10 | In tem Code128 cho lô → PDF đọc được bằng đầu đọc | |

---

## 7. Kịch bản Demo (8 bước)

| Bước | Hành động | Vai trò | Màn hình | Điểm nhấn |
|------|-----------|---------|----------|-----------|
| 1 | Đăng nhập lần lượt 4 vai trò | Tất cả | M01 | Giao diện thay đổi theo vai trò |
| 2 | Hệ thống cảnh báo lô sắp hết hạn | QUANLY | M13 | Màu đỏ/cam; nút tạo phiếu xuất hủy |
| 3 | Nhập kho: quét GS1 DataMatrix, tự điền lô + hạn | THUKHO | M07 | Tự động hóa nhập liệu |
| 4 | Bán thuốc: quét EAN13 → FEFO tự chọn lô, cảnh báo ≤30 ngày | DUOCSI | M03 | Real-time, không nhập tay |
| 5 | Bán theo đơn: tiếp nhận đơn → kiểm tra hiệu lực → bán | DUOCSI | M04, M03 | BR01 + BR05 enforcement |
| 6 | Tạo phiếu điều chuyển → duyệt → xác nhận TonKho 2 nhà thuốc | THUKHO → QUANLY | M08, M11 | Luồng duyệt, transaction 2 chiều |
| 7 | QUANTRI xem dashboard chuỗi: phân tích nhu cầu theo khu vực | QUANTRI | M17, M18 | Biểu đồ K1; sl/1.000 dân |
| 8 | Xem đề xuất nhập hàng K5 và AuditLog | QUANTRI | M19, M23 | Quy tắc nghiệp vụ thay AI |
