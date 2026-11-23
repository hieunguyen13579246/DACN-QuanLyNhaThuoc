# 08 – Kiến trúc, Phân quyền và Giao diện

## 1. Kiến trúc 3 Lớp

```
┌─────────────────────────────────────────────────────────────────┐
│  TẦNG TRÌNH BÀY (Presentation)                                  │
│  • Trình duyệt web (Chrome/Edge): Thymeleaf templates           │
│  • Máy quét mã vạch: USB HID (giả lập bàn phím) / Camera        │
│  • Giao tiếp: HTTP/HTTPS → Spring MVC Controller                 │
└──────────────────────────┬──────────────────────────────────────┘
                           │ HTTP/HTTPS
┌──────────────────────────▼──────────────────────────────────────┐
│  TẦNG LOGIC NGHIỆP VỤ (Business Logic)                         │
│  • Java 17 + Spring Boot 3.x                                    │
│  • Spring MVC (Controller) + Spring Security (JWT/Session)       │
│  • Service Layer (BanHangService, KhoService, ThongKeService)   │
│  • Thymeleaf View Engine (Server-side rendering)                │
│  • Spring Scheduled (batch cảnh báo hạn dùng)                  │
└──────────────────────────┬──────────────────────────────────────┘
                           │ JDBC / Spring Data
┌──────────────────────────▼──────────────────────────────────────┐
│  TẦNG DỮ LIỆU (Data)                                           │
│  • SQL Server 2019+                                             │
│  • Stored Procedures: sp_ThongKeNhuCauKhuVuc, sp_DeXuatNhapHang│
│  • Views: vw_TonKhoTheoThuoc, vw_CanhBaoHanDung, ...           │
│  • Transaction + ACID (UPDLOCK, ROWLOCK, deadlock retry)        │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Luồng Quét Mã Vạch

### 2.1 USB HID (giả lập bàn phím)
```
Dược sĩ nhấn vào ô nhập liệu POS
→ Quét mã vạch bằng đầu đọc USB
→ Thiết bị gửi chuỗi ký tự + Enter vào input field
→ JavaScript onKeyDown bắt Enter → gọi AJAX /ban/quet-ma-vach
→ Server tra MaVachThuoc → trả ThuocDTO + LoFEFODTO
→ Giao diện hiển thị tên thuốc, lô, hạn, giá
```

### 2.2 Camera (web)
```
Nhấn nút "Quét camera"
→ JavaScript dùng BarcodeDetector API hoặc ZXing.js
→ Đọc mã từ camera → gửi chuỗi lên server tương tự USB
```

### 2.3 In tem Code128 / QR nội bộ
```
Thủ kho vào màn hình M10 "In tem"
→ Chọn lô thuốc, số lượng tem
→ Server tạo PDF (iText/JasperReports) với Code128 (so_lo + thuoc_id + han_dung)
→ Tải PDF về; in trên máy in nhãn
```

### 2.4 Đọc GS1/Data Matrix (bonus nhập kho)
```
Thủ kho quét mã GS1 DataMatrix trên thùng hàng
→ Chuỗi GS1: (01)12345678901234(17)270101(10)LOTABC
→ Gs1Parser.parse() → {gtin:"12345678901234", hanDung:2027-01-01, soLo:"LOTABC"}
→ Tra MaVachThuoc bằng GTIN → thuoc_id
→ Tự điền form nhập kho
```

---

## 3. Ma trận RBAC

| Chức năng | DUOCSI | THUKHO | QUANLY | QUANTRI | Phạm vi |
|-----------|--------|--------|--------|---------|---------|
| Bán thuốc POS | ✓ | — | ✓ | — | Nhà thuốc mình |
| Tiếp nhận đơn thuốc | ✓ | — | ✓ | — | Nhà thuốc mình |
| Quản lý khách hàng | ✓ | — | ✓ | — | Toàn chuỗi (khách) |
| Tra cứu mã vạch | ✓ | ✓ | ✓ | ✓ | — |
| Hủy giao dịch | — | — | ✓ | — | GD của nhà thuốc mình |
| Tạo phiếu nhập kho | — | ✓ | — | — | Nhà thuốc mình |
| Tạo phiếu điều chuyển | — | ✓ | — | — | Nhà thuốc mình |
| Tạo phiếu xuất hủy | — | ✓ | — | — | Nhà thuốc mình |
| Duyệt phiếu | — | — | ✓ | — | Nhà thuốc mình |
| Xem tồn kho | ✓ | ✓ | ✓ | ✓ | NT mình / Toàn chuỗi |
| In tem mã vạch | — | ✓ | ✓ | — | — |
| Cảnh báo hạn/tồn thấp | ✓ | ✓ | ✓ | ✓ | NT mình / Toàn chuỗi |
| Định mức tồn | — | — | ✓ | ✓ | NT mình / Toàn chuỗi |
| Dashboard nhà thuốc | ✓ | ✓ | ✓ | — | Nhà thuốc mình |
| Dashboard chuỗi | — | — | — | ✓ | Toàn chuỗi |
| Phân tích khu vực | — | — | — | ✓ | Toàn chuỗi |
| Đề xuất nhập hàng | — | — | ✓ | ✓ | NT mình / Toàn chuỗi |
| Quản lý thuốc, mã vạch | — | — | — | ✓ | Toàn chuỗi |
| Quản lý NCC, khu vực | — | — | — | ✓ | Toàn chuỗi |
| Quản lý người dùng | — | — | — | ✓ | Toàn chuỗi |
| Cấu hình hệ thống | — | — | — | ✓ | Toàn chuỗi |
| Nhật ký kiểm toán | — | — | — | ✓ | Toàn chuỗi |
| Đối soát tồn kho | — | — | — | ✓ | Toàn chuỗi |

---

## 4. Danh mục Màn hình

| Mã | Màn hình | Vai trò truy cập | Mô tả ngắn |
|----|----------|-----------------|------------|
| M01 | Đăng nhập | Tất cả | Form email + mật khẩu |
| M02 | Dashboard nhà thuốc | DS, TK, QL | Tổng hợp doanh thu, cảnh báo, tồn nhanh |
| M03 | POS Bán hàng | DS, QL | Quét mã vạch, giỏ hàng, thanh toán |
| M04 | Tiếp nhận đơn thuốc | DS, QL | Nhập thông tin đơn + danh sách thuốc |
| M05 | Tìm kiếm khách hàng | DS, QL | Tìm theo tên/SĐT; thêm khách mới |
| M06 | Tra cứu mã vạch | Tất cả | Quét hoặc nhập mã → thông tin thuốc |
| M07 | Nhập kho | TK | Tạo phiếu nhập, quét GS1, danh sách chi tiết |
| M08 | Điều chuyển kho | TK | Tạo phiếu điều chuyển, chọn nhà thuốc đích |
| M09 | Xuất hủy | TK | Tạo phiếu xuất hủy, chọn lý do |
| M10 | In tem mã vạch | TK, QL | Chọn lô, số lượng, tải PDF |
| M11 | Duyệt phiếu kho | QL | Danh sách phiếu CHO_DUYET; duyệt/từ chối |
| M12 | Tồn kho theo lô | DS, TK, QL, QTRI | Bảng tồn, lọc theo thuốc/lô/hạn |
| M13 | Cảnh báo hạn dùng | DS, TK, QL, QTRI | Danh sách lô theo mức cảnh báo |
| M14 | Cảnh báo tồn thấp/chậm | QL, QTRI | Danh sách thuốc dưới định mức |
| M15 | Định mức tồn | QL, QTRI | Thiết lập ton_toi_thieu, ton_toi_da |
| M16 | Hủy giao dịch | QL | Tìm GD, nhập lý do hủy |
| M17 | Dashboard chuỗi | QTRI | Tổng hợp toàn chuỗi, biểu đồ khu vực |
| M18 | Phân tích nhu cầu | QTRI | Bộ lọc khu vực/nhóm/thời gian; biểu đồ |
| M19 | Đề xuất nhập hàng | QL, QTRI | Bảng đề xuất K5 |
| M20 | Quản lý thuốc | QTRI | CRUD danh mục thuốc + mã vạch |
| M21 | Quản lý NCC & khu vực | QTRI | CRUD NCC, khu vực, nhà thuốc |
| M22 | Quản lý người dùng | QTRI | CRUD NguoiDung; phân vai trò |
| M23 | Nhật ký và cấu hình | QTRI | AuditLog, CauHinh, đối soát |

---

## 5. Wireframe (bố cục bảng)

### M01 – Đăng nhập
```
┌────────────────────────────────────────┐
│           [Logo chuỗi nhà thuốc]       │
│                                        │
│  Email:    [________________________]  │
│  Mật khẩu:[________________________]  │
│                                        │
│           [   ĐĂNG NHẬP   ]           │
│                                        │
│  Phiên bản hệ thống: IS009 v1.0        │
└────────────────────────────────────────┘
```

### M03 – POS Bán hàng
```
┌─────────────────────────────────────────────────────────────────────┐
│ [Logo]  POS – Nhà thuốc NT001   Dược sĩ: Nguyễn A  [Đăng xuất]    │
├────────────────────────┬────────────────────────────────────────────┤
│ QUÉT MÃ VẠCH          │ GIỎ HÀNG                                  │
│ [🔍 Nhập mã / tên ]   │ STT | Thuốc | Lô | Hạn | SL | Đơn giá | TT │
│ [📷 Quét camera]      │  1  | ...   |    |     |    |          |    │
│                        │  2  | ...   |    |     |    |          |    │
│ KHU VỰC THÔNG TIN     │                                            │
│ Thuốc: Amoxicillin    │ ────────────────────────────────────────── │
│ Lô: LO2025A           │ Tổng tiền:              150.000 đ          │
│ Hạn: 2027-05-01       │ Giảm giá:                    0 đ          │
│ Tồn: 45 viên          │ Thanh tiền:             150.000 đ          │
│ ⚠ Lô còn 28 ngày      │ [TIỀN MẶT] [CHUYỂN KHOẢN] [THẺ]          │
│                        │ [      XÁC NHẬN THANH TOÁN     ]          │
└────────────────────────┴────────────────────────────────────────────┘
```

### M07 – Nhập kho
```
┌─────────────────────────────────────────────────────────────────┐
│ Phiếu nhập kho   Nhà cung cấp: [Chọn NCC ▼]   Ngày: 28/10/2026 │
├─────────────────────────────────────────────────────────────────┤
│ Quét mã vạch: [____________________] [📷] [Thêm dòng thủ công] │
├──────┬─────────────────┬───────────┬────────────┬──────┬────────┤
│ #    │ Thuốc           │ Số lô     │ Hạn dùng   │ SL   │ Giá N  │
├──────┼─────────────────┼───────────┼────────────┼──────┼────────┤
│  1   │ Amoxicillin 500 │ LO2025A   │ 2027-05-01 │ 100  │ 12.000 │
│  2   │ Paracetamol 500 │ PA2025B   │ 2027-03-15 │  50  │  5.000 │
├──────┴─────────────────┴───────────┴────────────┴──────┴────────┤
│                                     Tổng giá trị: 1.450.000 đ   │
│                         [LƯU NHÁ P]    [HOÀN THÀNH NHẬP KHO]   │
└─────────────────────────────────────────────────────────────────┘
```

### M11 – Duyệt phiếu kho
```
┌───────────────────────────────────────────────────────────────────┐
│ DANH SÁCH PHIẾU CHỜ DUYỆT                     [Lọc loại: Tất cả▼]│
├───────────┬───────────────┬──────────────┬────────────┬───────────┤
│ Mã phiếu  │ Loại          │ Người lập    │ Thời gian  │ Hành động │
├───────────┼───────────────┼──────────────┼────────────┼───────────┤
│ DC001     │ Điều chuyển   │ Thủ kho A    │ 28/10 8:00 │[Xem][Duyệt][Từ chối] │
│ XH002     │ Xuất hủy      │ Thủ kho B    │ 28/10 9:30 │[Xem][Duyệt][Từ chối] │
└───────────┴───────────────┴──────────────┴────────────┴───────────┘
```

### M13 – Cảnh báo hạn dùng
```
┌──────────────────────────────────────────────────────────────────┐
│ CẢNH BÁO HẠN DÙNG       [Lọc nhà thuốc ▼]   [Lọc mức ▼] [Xuất]│
├──────────────┬──────────┬───────────┬──────────┬────────────────┤
│ Thuốc        │ Lô       │ Hạn dùng  │ Tồn (lô) │ Mức cảnh báo  │
├──────────────┼──────────┼───────────┼──────────┼────────────────┤
│ Amox 500     │ LO2024A  │ 2026-11-01│     20   │ 🔴 Đã hết hạn  │
│ Parace 500   │ PA2025A  │ 2026-11-25│     15   │ 🟠 ≤30 ngày   │
│ Ibuprofen    │ IB2025B  │ 2026-12-15│      8   │ 🟡 ≤60 ngày   │
├──────────────┴──────────┴───────────┴──────────┴────────────────┤
│ [Tạo phiếu xuất hủy cho các lô đã hết hạn]                      │
└──────────────────────────────────────────────────────────────────┘
```

### M06 – Tra cứu mã vạch
```
┌────────────────────────────────────────────┐
│ TRA CỨU THUỐC THEO MÃ VẠCH                │
│ Nhập hoặc quét: [__________________] [🔍] │
│                                            │
│ KẾT QUẢ:                                  │
│ Tên thuốc:  Amoxicillin 500mg              │
│ Hoạt chất:  Amoxicillin                   │
│ Dạng BC:    Viên nén                      │
│ Đơn vị:     Viên                          │
│ Giá bán lẻ: 1.500 đ/viên                  │
│ Cần đơn:    Không                         │
│                                            │
│ TỒN KHO HIỆN TẠI (nhà thuốc hiện tại):    │
│ Lô PA2025A | Hạn: 2027-05-01 | Tồn: 45   │
│ Lô PA2025B | Hạn: 2027-08-01 | Tồn: 10   │
│                                            │
│ [Tạo phiếu nhập]  [POS Bán]               │
└────────────────────────────────────────────┘
```

### M02 – Dashboard nhà thuốc
```
┌──────────────────────────────────────────────────────────────────┐
│ DASHBOARD – NT001 Quận Bình Thạnh          28/10/2026           │
├──────────────────────┬──────────────────┬────────────────────────┤
│ 💰 Doanh thu hôm nay │ 📦 Tổng giao dịch│ ⚠ Cảnh báo tồn kho   │
│   2.450.000 đ        │     18 GD        │   3 thuốc tồn thấp    │
├──────────────────────┴──────────────────┴────────────────────────┤
│ CẢNH BÁO ĐỎ (lô hết hạn / ≤30 ngày):                          │
│ • Amox 500 – LO2024A – Hết hạn → [Tạo phiếu xuất hủy]          │
│ • Parace 500 – PA2025A – 28 ngày còn lại                        │
├─────────────────────────────────────────────────────────────────┤
│ TOP THUỐC BÁN HÔM NAY       │ ĐƠN THUỐC ĐANG TIẾP NHẬN        │
│ 1. Paracetamol 500 – 45 viên│ • DON001 – ChuaB – Nguyen Thi B  │
│ 2. Vitamin C – 30 viên      │ • DON002 – BanMotPhan – Le V A    │
│ 3. Amoxicillin – 20 viên    │                                  │
└─────────────────────────────────────────────────────────────────┘
```

### M17 – Dashboard chuỗi
```
┌──────────────────────────────────────────────────────────────────┐
│ DASHBOARD TOÀN CHUỖI               Tháng 10/2026    QUANTRI     │
├─────────────┬──────────────┬────────────────┬────────────────────┤
│ Doanh thu   │ Tổng GD      │ Nhà thuốc hàng │ KV dẫn đầu        │
│ 125.000.000 │  1.240 GD    │ NT005 – Q.1    │ TP.HCM – Q1        │
├─────────────┴──────────────┴────────────────┴────────────────────┤
│ BIỂU ĐỒ DOANH THU THEO THÁNG (12 tháng gần nhất)               │
│ [Bar chart placeholder – nhập tại plantuml.com hoặc Chart.js]    │
├─────────────────────────────────────────────────────────────────┤
│ PHÂN TÍCH NHU CẦU THEO KHU VỰC              [Xem phân tích chi tiết →]│
│ Nhóm thuốc     │ Q.Bình Thạnh │ Q.Tân Bình  │ Q.1               │
│ Kháng sinh     │  120 đv/1KDân│  98 đv/1KDân│ 145 đv/1KDân      │
│ Vitamin        │   80         │  110         │  95               │
└─────────────────────────────────────────────────────────────────┘
```

### M19 – Đề xuất nhập hàng
```
┌───────────────────────────────────────────────────────────────────┐
│ ĐỀ XUẤT NHẬP HÀNG – NT001   [Làm mới]   [Xuất Excel]            │
│ Công thức K5: Đề xuất = BanTBNgay × (SoNgayGiaoHang + SoNgayAT) − TonKhaDung │
├─────────────────┬──────┬────────┬───────┬───────────┬────────────┤
│ Thuốc           │ Bán TB│ Giao   │ Tồn KD│ Đề xuất  │ Hành động  │
│                 │ /ngày │ hàng   │(cuối) │          │            │
├─────────────────┼──────┼────────┼───────┼───────────┼────────────┤
│ Paracetamol 500 │  15  │  3 ngày│  20   │   25 viên │[Đặt hàng]  │
│ Amoxicillin 500 │   8  │  5 ngày│   5   │   59 viên │[Đặt hàng]  │
│ Vitamin C 1000mg│   5  │  3 ngày│  40   │    0      │ Đủ hàng │
└─────────────────┴──────┴────────┴───────┴───────────┴────────────┘
```
