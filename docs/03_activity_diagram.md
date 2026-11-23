# 03 – Activity Diagram

## Mô tả chung
Mỗi sơ đồ trình bày dạng swimlane PlantUML với start (●), stop (◉), quyết định (<>), fork/join khi cần.  
Kèm "Các bước thực hiện" đánh số để đối chiếu khi đọc sơ đồ.

---

## P3 – Bán thuốc

### 3.1 P3 AS-IS – Bán thuốc thủ công

```plantuml
@startuml P3_AS_IS
|Khách hàng|
start
:Trình đơn / yêu cầu thuốc;

|Dược sĩ|
:Tra Excel tồn kho;
if (Còn hàng?) then (Không)
  :Báo hết;
  stop
else (Có)
  :Lấy thuốc theo cảm tính\n(không kiểm FEFO, không kiểm hạn);
  if (Thuốc kê đơn?) then (Có)
    :Hỏi khách có đơn không\n(không đối chiếu);
  endif
  :Ghi tay hóa đơn / nhập Excel;
  :Trừ tồn Excel thủ công;
  :Khách nhận thuốc & thanh toán;
end
endif
stop
@enduml
```

**Các bước P3 AS-IS:**
1. Khách trình đơn hoặc yêu cầu mua thuốc
2. Dược sĩ tra Excel tồn kho
3. Nếu hết hàng: báo khách, kết thúc
4. Lấy thuốc theo cảm tính (không đảm bảo FEFO, không kiểm hạn dùng)
5. Nếu thuốc kê đơn: hỏi miệng khách, không kiểm tra đơn thật
6. Ghi tay hóa đơn hoặc nhập Excel
7. Trừ tồn Excel thủ công (dễ sai)
8. Khách nhận thuốc, thanh toán

---

### 3.2 P3 TO-BE – Bán thuốc POS (quét mã vạch + FEFO)

```plantuml
@startuml P3_TOBE_POS
|Dược sĩ|
start
:Mở phiên bán hàng (chọn nhà thuốc);
:Quét mã vạch hoặc tìm tên thuốc;

|Hệ thống|
:Tìm thuốc theo mã vạch (MaVachThuoc);
if (Tìm thấy?) then (Không)
  :Thông báo "Không tìm thấy mã";
  |Dược sĩ|
  :Tìm thủ công hoặc kết thúc;
  stop
else (Có)
  if (can_ke_don = 1?) then (Có)
    |Dược sĩ|
    :Chọn đơn thuốc\nWHERE nha_thuoc_id = ? AND hiệu lực;
    |Hệ thống|
    :Kiểm tra BR01:\n– Đơn còn hiệu lực (BR05)\n– Thuốc có trong đơn\n– SL ≤ cho phép;
    if (Hợp lệ?) then (Không)
      :Từ chối, hiển thị lý do;
      stop
    endif
  endif
  :Áp dụng FEFO: chọn lô\nhan_dung gần nhất còn tồn (BR02);
  if (Lô ≤30 ngày?) then (Có)
    :Hiển thị cảnh báo cam\n(vẫn cho bán - BR03);
  endif
  :Kiểm tra tồn lô (BR04)\nvới UPDLOCK;
  if (Đủ tồn?) then (Không)
    :Thông báo "Hết hàng";
    stop
  endif
  |Dược sĩ|
  :Nhập số lượng; xác nhận;
  :Chọn phương thức thanh toán;
  |Hệ thống|
  :Lưu GiaoDich + ChiTietGiaoDich\n(lưu don_gia, gia_von – BR15);
  fork
    :Cập nhật TonKho (trừ theo lô);
  fork again
    :Ghi BienDongKho loại BAN (BR12);
  fork again
    if (Bán theo đơn?) then (Có)
      :Cập nhật ChiTietDonThuoc.so_luong_da_ban\nCập nhật trang_thai DonThuoc;
    endif
  end fork
  :In / hiển thị hóa đơn;
  stop
endif
@enduml
```

**Các bước P3 TO-BE:**
1. Dược sĩ mở phiên bán hàng
2. Quét mã vạch hoặc tìm tên thuốc
3. Hệ thống tra bảng MaVachThuoc → Thuoc
4. Nếu thuốc kê đơn: yêu cầu chọn đơn thuốc, kiểm tra BR01 + BR05
5. Áp dụng FEFO: hệ thống tự chọn lô gần hạn nhất (BR02)
6. Hiển thị cảnh báo nếu lô ≤30 ngày (BR03)
7. Kiểm tra tồn đủ theo lô với UPDLOCK (BR04)
8. Dược sĩ nhập số lượng, xác nhận, chọn thanh toán
9. Hệ thống lưu GiaoDich + ChiTietGiaoDich (BR15)
10. Song song: trừ TonKho, ghi BienDongKho (BR12), cập nhật đơn thuốc nếu có

---

## P2 – Nhập kho

### 3.3 P2 AS-IS

```plantuml
@startuml P2_AS_IS
|Nhà cung cấp|
start
:Giao hàng kèm phiếu giao;

|Nhân viên kho|
:Kiểm đếm thực tế;
:Ghi sổ / Excel:\ntên thuốc, số lô, hạn, SL, giá;
:Cập nhật Excel tồn kho\n(cộng thủ công);
stop
@enduml
```

**Các bước P2 AS-IS:** NCC giao hàng → kiểm đếm thực tế → ghi sổ/Excel (số lô, hạn, NCC không đầy đủ) → cập nhật tồn thủ công.

---

### 3.4 P2 TO-BE – Nhập kho (có đọc GS1)

```plantuml
@startuml P2_TOBE
|Thủ kho|
start
:Tạo phiếu nhập kho mới\n(chọn NCC);
:Quét mã vạch sản phẩm;

|Hệ thống|
if (Mã GS1/DataMatrix?) then (Có)
  :Parse GS1:\nGTIN(01)→thuoc_id\nHạn dùng(17)→han_dung\nSố lô(10)→so_lo\nTự điền các trường;
else (Không)
  :Tìm thuốc theo EAN13/CODE128;
endif

|Thủ kho|
:Xem / chỉnh sửa: số lô, hạn dùng,\nngày sx, số lượng, giá nhập;

|Hệ thống|
:Kiểm tra BR07:\n– han_dung > ngày nhập, > ngay_sx\n– UQ(thuoc_id, so_lo) cho lô mới;
if (Hợp lệ?) then (Không)
  :Báo lỗi chi tiết;
  |Thủ kho|
  :Sửa lại;
  note right: quay về bước kiểm tra
else (Có)
  if (Lô đã tồn tại?) then (Có)
    :Cộng dồn số lượng;
  else (Lô mới)
    :Tạo LoThuoc mới;
  endif
  fork
    :Cập nhật TonKho (cộng theo lô);
  fork again
    :Ghi BienDongKho loại NHAP (BR12);
  end fork
  :PhieuKho.trang_thai = HOAN_THANH;
  |Thủ kho|
  :In tem mã vạch nội bộ (nếu cần);
  stop
endif
@enduml
```

**Các bước P2 TO-BE:**
1. Thủ kho tạo phiếu nhập, chọn NCC
2. Quét mã vạch từng sản phẩm
3. Nếu GS1/DataMatrix: hệ thống parse tự động GTIN, hạn, lô
4. Thủ kho xem lại, chỉnh sửa nếu cần
5. Hệ thống kiểm tra BR07; báo lỗi nếu vi phạm
6. Nếu lô đã có: cộng dồn; lô mới: tạo LoThuoc
7. Cập nhật TonKho + ghi BienDongKho song song (BR12)
8. PhieuKho → HOAN_THANH; thủ kho in tem nếu cần

---

## P4 – Điều chuyển và Xuất hủy

### 3.5 P4 TO-BE – Điều chuyển (swimlane 3 vai trò)

```plantuml
@startuml P4_DIEUCHIYEN
|Thủ kho (xuất)|
start
:Tạo PhieuKho DIEU_CHUYEN\n(chọn nhà thuốc đích ≠ nguồn);
:Thêm từng dòng:\nthuốc, lô, số lượng;

|Hệ thống|
:Kiểm tra tồn đủ theo lô (BR08);
if (Đủ tồn?) then (Không)
  :Báo lỗi;
  |Thủ kho (xuất)|
  :Điều chỉnh số lượng;
  note right: quay lại kiểm tra
else (Có)
  :Lưu PhieuKho CHO_DUYET;
  
  |Quản lý (xuất)|
  :Nhận thông báo phiếu chờ duyệt;
  :Xem chi tiết phiếu;
  if (Duyệt?) then (Từ chối)
    |Hệ thống|
    :PhieuKho TU_CHOI\nGhi lý do vào ghi_chu;
    |Hệ thống|
    :Ghi AuditLog (BR14);
    stop
  else (Duyệt)
    |Hệ thống|
    :BEGIN TRANSACTION\nTrừ TonKho[nha_thuoc_xuat][lo]\nCộng TonKho[nha_thuoc_nhan][lo]\nGhi BienDongKho DIEU_CHUYEN_XUAT\nGhi BienDongKho DIEU_CHUYEN_NHAP\nPHIEU_KHO HOAN_THANH\nCOMMIT (BR08, BR12);
    :Ghi AuditLog (BR14);
    stop
  endif
endif
@enduml
```

### 3.6 P4 TO-BE – Xuất hủy

```plantuml
@startuml P4_XUATHUY
|Thủ kho|
start
:Tạo PhieuKho XUAT_HUY;
:Chọn thuốc/lô cần hủy;
:Chọn lý do (BR09):\nHET_HAN | HU_HONG | THU_HOI | KHAC;
:Lưu PhieuKho CHO_DUYET;

|Quản lý|
:Xem phiếu xuất hủy chờ duyệt;
if (Duyệt?) then (Từ chối)
  |Hệ thống|
  :PhieuKho TU_CHOI;
  :Ghi AuditLog (BR14);
  stop
else (Duyệt)
  |Hệ thống|
  :Trừ TonKho theo lô;
  :Ghi BienDongKho loại XUAT_HUY;
  :PhieuKho HOAN_THANH;
  :Ghi AuditLog (BR14);
  stop
endif
@enduml
```

---

## P5 – Xử lý cảnh báo hạn dùng

### 3.7 P5 TO-BE

```plantuml
@startuml P5_CANHBAO
|Hệ thống (batch/realtime)|
start
:Truy vấn vw_CanhBaoHanDung\nvw_CanhBaoTonThap\nvw_TonChamLuanChuyen;

fork
  :CÁC LÔ ĐÃ HẾT HẠN\n→ Mark đỏ; gửi Alert;
fork again
  :CÁC LÔ ≤30 NGÀY\n→ Mark cam;
fork again
  :CÁC LÔ ≤60 NGÀY\n→ Mark vàng;
fork again
  :TỒN THẤP\n(tổng < ton_toi_thieu);
fork again
  :TỒN CHẬM\n(không bán trong X ngày);
end fork

|Quản lý nhà thuốc|
:Xem dashboard cảnh báo;

if (Lô hết hạn/hỏng?) then (Có)
  :Tạo phiếu xuất hủy (UC16);
else if (Tồn thấp?) then (Có)
  :Đặt hàng / xin điều chuyển;
else (Tồn chậm)
  :Xem xét điều chuyển sang\nnhà thuốc có nhu cầu;
endif
stop
@enduml
```

---

## Bảng So sánh AS-IS / TO-BE

| Quy trình | AS-IS (vấn đề) | TO-BE (cải tiến) |
|-----------|----------------|------------------|
| P3 – Bán | Không FEFO, không kiểm hạn, không kiểm đơn kê toa | FEFO tự động, kiểm hạn, bắt buộc đơn với can_ke_don=1 |
| P2 – Nhập | Ghi sổ tay, thiếu số lô/hạn dùng | Quét mã vạch (GS1 tự điền), kiểm tra BR07, ghi lô đầy đủ |
| P4 – Điều chuyển | Nhắn tin, không có xác nhận điện tử | Phiếu điều chuyển có duyệt, transaction 2 chiều, AuditLog |
| P4 – Xuất hủy | Ghi sổ đơn giản | Phiếu xuất hủy có lý do, duyệt, BienDongKho |
| P5 – Cảnh báo | Kiểm tra thủ công hàng tháng | View tự động, dashboard real-time 5 loại cảnh báo |
