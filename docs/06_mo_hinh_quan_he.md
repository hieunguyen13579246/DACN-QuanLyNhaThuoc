# 06 – Mô hình Quan hệ

## 1. Quy tắc chuyển ERD → Quan hệ

| Trường hợp | Quy tắc |
|------------|---------|
| Thực thể mạnh | Mỗi thực thể → 1 bảng; PK giữ nguyên |
| Quan hệ 1-nhiều | FK thêm vào bảng "nhiều" |
| Quan hệ nhiều-nhiều | Tạo bảng liên kết (ChiTietPhieuKho, ChiTietDonThuoc, ChiTietGiaoDich, TonKho, DinhMucTon) |
| Thuộc tính đa trị | Bảng riêng (MaVachThuoc) |
| Kế thừa / tự tham chiếu | Thêm FK cùng bảng (KhuVuc.khu_vuc_cha_id) |

---

## 2. Lược đồ Quan hệ (tên bảng và PK)

```
KhuVuc(id*, ma, ten, cap, khu_vuc_cha_id→KhuVuc, dan_so)
NhaThuoc(id*, ma, ten, dia_chi, khu_vuc_id→KhuVuc, so_dien_thoai, trang_thai, ngay_khai_truong)
NguoiDung(id*, ho_ten, email, password_hash, vai_tro, nha_thuoc_id→NhaThuoc, so_chung_chi_hanh_nghe, kich_hoat, created_at)
NhomThuoc(id*, ma, ten)
Thuoc(id*, ma, ten, hoat_chat, ham_luong, dang_bao_che, don_vi_tinh, nhom_thuoc_id→NhomThuoc, nha_san_xuat, can_ke_don, gia_ban_le, trang_thai, mo_ta)
MaVachThuoc(id*, thuoc_id→Thuoc, ma_vach, loai_ma_vach, la_ma_chinh, created_at)
NhaCungCap(id*, ma, ten, dia_chi, so_dien_thoai, ma_so_thue, trang_thai)
LoThuoc(id*, thuoc_id→Thuoc, so_lo, ngay_san_xuat, han_dung, nha_cung_cap_id→NhaCungCap, gia_nhap, created_at)
TonKho(nha_thuoc_id→NhaThuoc*, lo_thuoc_id→LoThuoc*, so_luong, cap_nhat_luc)
DinhMucTon(id*, nha_thuoc_id→NhaThuoc, thuoc_id→Thuoc, ton_toi_thieu, ton_toi_da, so_ngay_giao_hang)
PhieuKho(id*, ma_phieu, loai_phieu, nha_thuoc_id→NhaThuoc, nha_thuoc_den_id→NhaThuoc, nha_cung_cap_id→NhaCungCap, ly_do, ghi_chu, trang_thai, nguoi_lap_id→NguoiDung, nguoi_duyet_id→NguoiDung, thoi_gian_lap, thoi_gian_hoan_thanh, tong_gia_tri)
ChiTietPhieuKho(id*, phieu_kho_id→PhieuKho, lo_thuoc_id→LoThuoc, so_luong, don_gia)
BienDongKho(id BIGINT*, nha_thuoc_id→NhaThuoc, lo_thuoc_id→LoThuoc, loai_bien_dong, so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id→NguoiDung, thoi_gian)
KhachHang(id*, ma, ho_ten, so_dien_thoai, gioi_tinh, nam_sinh, khu_vuc_id→KhuVuc, dia_chi, ghi_chu_di_ung, created_at)
DonThuoc(id*, ma_don, khach_hang_id→KhachHang, nha_thuoc_id→NhaThuoc, ten_bac_si, co_so_kham, chan_doan, ngay_ke_don, so_ngay_hieu_luc, trang_thai, nguoi_tiep_nhan_id→NguoiDung, created_at)
ChiTietDonThuoc(id*, don_thuoc_id→DonThuoc, thuoc_id→Thuoc, so_luong_ke, so_luong_da_ban, lieu_dung)
GiaoDich(id*, ma_giao_dich, nha_thuoc_id→NhaThuoc, nguoi_ban_id→NguoiDung, khach_hang_id→KhachHang, don_thuoc_id→DonThuoc, thoi_gian, tong_tien, giam_gia, thanh_tien, phuong_thuc_thanh_toan, trang_thai, ly_do_huy, nguoi_huy_id→NguoiDung, thoi_gian_huy, ghi_chu)
ChiTietGiaoDich(id*, giao_dich_id→GiaoDich, thuoc_id→Thuoc, lo_thuoc_id→LoThuoc, so_luong, don_gia, gia_von, thanh_tien)
CauHinh(khoa*, gia_tri, mo_ta)
AuditLog(id BIGINT*, nguoi_dung_id→NguoiDung, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_cu, du_lieu_moi, created_at)
```
*PK gạch dưới

---

## 3. Danh sách Khóa Ngoại

| Bảng | Cột FK | Tham chiếu |
|------|--------|------------|
| KhuVuc | khu_vuc_cha_id | KhuVuc(id) |
| NhaThuoc | khu_vuc_id | KhuVuc(id) |
| NguoiDung | nha_thuoc_id | NhaThuoc(id) |
| Thuoc | nhom_thuoc_id | NhomThuoc(id) |
| MaVachThuoc | thuoc_id | Thuoc(id) |
| LoThuoc | thuoc_id | Thuoc(id) |
| LoThuoc | nha_cung_cap_id | NhaCungCap(id) |
| TonKho | nha_thuoc_id | NhaThuoc(id) |
| TonKho | lo_thuoc_id | LoThuoc(id) |
| DinhMucTon | nha_thuoc_id | NhaThuoc(id) |
| DinhMucTon | thuoc_id | Thuoc(id) |
| PhieuKho | nha_thuoc_id | NhaThuoc(id) |
| PhieuKho | nha_thuoc_den_id | NhaThuoc(id) NULL |
| PhieuKho | nha_cung_cap_id | NhaCungCap(id) NULL |
| PhieuKho | nguoi_lap_id | NguoiDung(id) |
| PhieuKho | nguoi_duyet_id | NguoiDung(id) NULL |
| ChiTietPhieuKho | phieu_kho_id | PhieuKho(id) |
| ChiTietPhieuKho | lo_thuoc_id | LoThuoc(id) |
| BienDongKho | nha_thuoc_id | NhaThuoc(id) |
| BienDongKho | lo_thuoc_id | LoThuoc(id) |
| BienDongKho | nguoi_thuc_hien_id | NguoiDung(id) NULL |
| KhachHang | khu_vuc_id | KhuVuc(id) NULL |
| DonThuoc | khach_hang_id | KhachHang(id) |
| DonThuoc | nha_thuoc_id | NhaThuoc(id) |
| DonThuoc | nguoi_tiep_nhan_id | NguoiDung(id) |
| ChiTietDonThuoc | don_thuoc_id | DonThuoc(id) |
| ChiTietDonThuoc | thuoc_id | Thuoc(id) |
| GiaoDich | nha_thuoc_id | NhaThuoc(id) |
| GiaoDich | nguoi_ban_id | NguoiDung(id) |
| GiaoDich | khach_hang_id | KhachHang(id) NULL |
| GiaoDich | don_thuoc_id | DonThuoc(id) NULL |
| GiaoDich | nguoi_huy_id | NguoiDung(id) NULL |
| ChiTietGiaoDich | giao_dich_id | GiaoDich(id) |
| ChiTietGiaoDich | thuoc_id | Thuoc(id) |
| ChiTietGiaoDich | lo_thuoc_id | LoThuoc(id) |
| AuditLog | nguoi_dung_id | NguoiDung(id) NULL |

---

## 4. Ràng buộc CHECK / UNIQUE

```sql
-- Unique
ALTER TABLE KhuVuc           ADD CONSTRAINT UQ_KhuVuc_Ma             UNIQUE (ma);
ALTER TABLE NhaThuoc         ADD CONSTRAINT UQ_NhaThuoc_Ma           UNIQUE (ma);
ALTER TABLE NguoiDung        ADD CONSTRAINT UQ_NguoiDung_Email       UNIQUE (email);
ALTER TABLE NhomThuoc        ADD CONSTRAINT UQ_NhomThuoc_Ma          UNIQUE (ma);
ALTER TABLE Thuoc            ADD CONSTRAINT UQ_Thuoc_Ma              UNIQUE (ma);
ALTER TABLE MaVachThuoc      ADD CONSTRAINT UQ_MaVach                UNIQUE (ma_vach);
ALTER TABLE NhaCungCap       ADD CONSTRAINT UQ_NCC_Ma                UNIQUE (ma);
ALTER TABLE LoThuoc          ADD CONSTRAINT UQ_Lo_ThuocSoLo          UNIQUE (thuoc_id, so_lo);
ALTER TABLE DinhMucTon       ADD CONSTRAINT UQ_DinhMuc_NT_Thuoc      UNIQUE (nha_thuoc_id, thuoc_id);
ALTER TABLE PhieuKho         ADD CONSTRAINT UQ_PhieuKho_Ma           UNIQUE (ma_phieu);
ALTER TABLE KhachHang        ADD CONSTRAINT UQ_KhachHang_Ma          UNIQUE (ma);
ALTER TABLE DonThuoc         ADD CONSTRAINT UQ_DonThuoc_Ma           UNIQUE (ma_don);
ALTER TABLE GiaoDich         ADD CONSTRAINT UQ_GiaoDich_Ma           UNIQUE (ma_giao_dich);
ALTER TABLE ChiTietDonThuoc  ADD CONSTRAINT UQ_CTDT_DonThuoc_Thuoc   UNIQUE (don_thuoc_id, thuoc_id);

-- CHECK
ALTER TABLE TonKho           ADD CONSTRAINT CK_TonKho_SoLuong        CHECK (so_luong >= 0);
ALTER TABLE Thuoc            ADD CONSTRAINT CK_Thuoc_GiaBanLe        CHECK (gia_ban_le > 0);
ALTER TABLE LoThuoc          ADD CONSTRAINT CK_LoThuoc_HanDung       CHECK (han_dung > CONVERT(date, GETDATE()));
ALTER TABLE ChiTietDonThuoc  ADD CONSTRAINT CK_CTDT_SoLuong         CHECK (so_luong_da_ban >= 0 AND so_luong_da_ban <= so_luong_ke);
ALTER TABLE DinhMucTon       ADD CONSTRAINT CK_DinhMuc_TonMin        CHECK (ton_toi_thieu >= 0);
ALTER TABLE PhieuKho         ADD CONSTRAINT CK_PhieuKho_DieuChuyen   CHECK (loai_phieu <> 'DIEU_CHUYEN' OR nha_thuoc_den_id <> nha_thuoc_id);
ALTER TABLE KhuVuc           ADD CONSTRAINT CK_KhuVuc_Cap            CHECK (cap IN ('TINH','QUAN'));
ALTER TABLE NguoiDung        ADD CONSTRAINT CK_NguoiDung_VaiTro      CHECK (vai_tro IN ('DUOCSI','THUKHO','QUANLY','QUANTRI'));
```

---

## 5. Chuẩn hóa 1NF – 3NF

| Bảng | Mức chuẩn | Ghi chú |
|------|-----------|---------|
| Tất cả bảng chính | 3NF | PK đơn giản; không có phụ thuộc bắc cầu |
| ChiTietGiaoDich | Phi chuẩn hóa có chủ ý | **thuoc_id**: FK có vẻ thừa (đã có lo_thuoc_id→thuoc_id) nhưng lưu để query nhanh; **gia_von** và **thanh_tien**: tính toán lại theo lo_thuoc_id.gia_nhap và don_gia*so_luong nhưng lưu để snapshot tại thời điểm bán (giá thay đổi không ảnh hưởng lịch sử) |
| BienDongKho | Phi chuẩn hóa có chủ ý | **so_du_sau**: có thể tính lại từ SUM(so_luong_thay_doi) nhưng lưu để truy vết nhanh không cần tổng hợp toàn bộ log |
| TonKho | Phi chuẩn hóa có chủ ý | Là aggregate của BienDongKho; lưu riêng để truy vấn O(1) thay vì SUM(BienDongKho) mỗi lần |

---

## 6. Từ điển Dữ liệu

### KhuVuc
| Tên cột | Kiểu SQL Server | Ràng buộc | Ý nghĩa | Miền giá trị |
|---------|----------------|-----------|---------|--------------|
| id | INT IDENTITY | PK | Khóa chính | Tự tăng |
| ma | NVARCHAR(20) | NOT NULL, UQ | Mã khu vực | VD: 'HN', 'HCM.Q1' |
| ten | NVARCHAR(200) | NOT NULL | Tên khu vực | — |
| cap | VARCHAR(5) | NOT NULL, CHECK IN('TINH','QUAN') | Cấp hành chính | TINH hoặc QUAN |
| khu_vuc_cha_id | INT | FK→KhuVuc, NULL | Tỉnh cha (NULL nếu là tỉnh) | — |
| dan_so | INT | NULL | Dân số (nhập ở cấp QUAN) | > 0 |

### NhaThuoc
| Tên cột | Kiểu SQL Server | Ràng buộc | Ý nghĩa | Miền giá trị |
|---------|----------------|-----------|---------|--------------|
| id | INT IDENTITY | PK | — | — |
| ma | NVARCHAR(20) | NOT NULL, UQ | Mã nhà thuốc | VD: 'NT001' |
| ten | NVARCHAR(200) | NOT NULL | Tên nhà thuốc | — |
| dia_chi | NVARCHAR(500) | NOT NULL | Địa chỉ | — |
| khu_vuc_id | INT | FK→KhuVuc, NOT NULL | Khu vực thuộc | — |
| so_dien_thoai | VARCHAR(20) | NULL | Số điện thoại | — |
| trang_thai | VARCHAR(20) | NOT NULL DEFAULT 'HOAT_DONG' | Trạng thái | HOAT_DONG, TAM_NGUNG |
| ngay_khai_truong | DATE | NULL | Ngày khai trương | ≤ hôm nay |

### NguoiDung
| Tên cột | Kiểu SQL Server | Ràng buộc | Ý nghĩa | Miền giá trị |
|---------|----------------|-----------|---------|--------------|
| id | INT IDENTITY | PK | — | — |
| ho_ten | NVARCHAR(200) | NOT NULL | Họ tên | — |
| email | NVARCHAR(200) | NOT NULL, UQ | Email đăng nhập | — |
| password_hash | VARCHAR(256) | NOT NULL | Mật khẩu đã hash | bcrypt |
| vai_tro | VARCHAR(10) | NOT NULL | Vai trò | DUOCSI, THUKHO, QUANLY, QUANTRI |
| nha_thuoc_id | INT | FK→NhaThuoc, NULL | Nhà thuốc (NULL nếu QUANTRI) | — |
| so_chung_chi_hanh_nghe | NVARCHAR(50) | NULL | Số chứng chỉ | — |
| kich_hoat | BIT | NOT NULL DEFAULT 1 | Tài khoản hoạt động | 0/1 |
| created_at | DATETIME2(0) | NOT NULL DEFAULT GETUTCDATE() | Thời gian tạo | UTC |

### Thuoc
| Tên cột | Kiểu SQL Server | Ràng buộc | Ý nghĩa | Miền giá trị |
|---------|----------------|-----------|---------|--------------|
| id | INT IDENTITY | PK | — | — |
| ma | NVARCHAR(20) | NOT NULL, UQ | Mã thuốc nội bộ | — |
| ten | NVARCHAR(300) | NOT NULL | Tên biệt dược | — |
| hoat_chat | NVARCHAR(300) | NOT NULL | Hoạt chất | — |
| ham_luong | NVARCHAR(100) | NULL | Hàm lượng | VD: '500mg' |
| dang_bao_che | NVARCHAR(100) | NULL | Dạng bào chế | Viên, chai, ống... |
| don_vi_tinh | NVARCHAR(50) | NOT NULL | Đơn vị tính | Viên, hộp, chai |
| nhom_thuoc_id | INT | FK→NhomThuoc, NOT NULL | Nhóm | — |
| nha_san_xuat | NVARCHAR(200) | NULL | Nhà sản xuất | — |
| can_ke_don | BIT | NOT NULL DEFAULT 0 | Bắt buộc kê đơn | 0=không, 1=có |
| gia_ban_le | DECIMAL(18,0) | NOT NULL, CHECK >0 | Giá bán lẻ (VND) | > 0 |
| trang_thai | VARCHAR(10) | NOT NULL DEFAULT 'DANG_BAN' | Trạng thái | DANG_BAN, NGUNG_BAN |
| mo_ta | NVARCHAR(MAX) | NULL | Mô tả thêm | — |

### MaVachThuoc
| Tên cột | Kiểu SQL Server | Ràng buộc | Ý nghĩa | Miền giá trị |
|---------|----------------|-----------|---------|--------------|
| id | INT IDENTITY | PK | — | — |
| thuoc_id | INT | FK→Thuoc, NOT NULL | Thuốc | — |
| ma_vach | NVARCHAR(50) | NOT NULL, UQ | Mã vạch | EAN13: 13 chữ số |
| loai_ma_vach | VARCHAR(10) | NOT NULL | Loại | EAN13, GTIN14, CODE128, NOI_BO |
| la_ma_chinh | BIT | NOT NULL DEFAULT 0 | Mã chính | 0/1 |
| created_at | DATETIME2(0) | NOT NULL DEFAULT GETUTCDATE() | Thời gian tạo | UTC |

### LoThuoc
| Tên cột | Kiểu SQL Server | Ràng buộc | Ý nghĩa | Miền giá trị |
|---------|----------------|-----------|---------|--------------|
| id | INT IDENTITY | PK | — | — |
| thuoc_id | INT | FK→Thuoc, NOT NULL | Thuốc | — |
| so_lo | NVARCHAR(100) | NOT NULL | Số lô | — |
| ngay_san_xuat | DATE | NULL | Ngày sản xuất | ≤ han_dung |
| han_dung | DATE | NOT NULL | Hạn dùng | > ngày nhập (BR07) |
| nha_cung_cap_id | INT | FK→NhaCungCap, NULL | Nhà cung cấp | — |
| gia_nhap | DECIMAL(18,0) | NOT NULL | Giá nhập (VND) | > 0 |
| created_at | DATETIME2(0) | NOT NULL DEFAULT GETUTCDATE() | Thời gian tạo | UTC |

### TonKho
| Tên cột | Kiểu SQL Server | Ràng buộc | Ý nghĩa | Miền giá trị |
|---------|----------------|-----------|---------|--------------|
| nha_thuoc_id | INT | PK, FK→NhaThuoc | Nhà thuốc | — |
| lo_thuoc_id | INT | PK, FK→LoThuoc | Lô thuốc | — |
| so_luong | INT | NOT NULL, CHECK ≥0 | Số lượng tồn | ≥ 0 (BR04) |
| cap_nhat_luc | DATETIME2(0) | NOT NULL DEFAULT GETUTCDATE() | Cập nhật lần cuối | UTC |

### PhieuKho
| Tên cột | Kiểu SQL Server | Ràng buộc | Ý nghĩa | Miền giá trị |
|---------|----------------|-----------|---------|--------------|
| id | INT IDENTITY | PK | — | — |
| ma_phieu | NVARCHAR(30) | NOT NULL, UQ | Mã phiếu | VD: 'NK20250101-001' |
| loai_phieu | VARCHAR(15) | NOT NULL | Loại | NHAP, XUAT_HUY, DIEU_CHUYEN |
| nha_thuoc_id | INT | FK→NhaThuoc | Nhà thuốc nguồn | — |
| nha_thuoc_den_id | INT | FK→NhaThuoc, NULL | Đích (điều chuyển) | ≠ nguồn |
| nha_cung_cap_id | INT | FK→NhaCungCap, NULL | NCC (nhập kho) | — |
| ly_do | VARCHAR(10) | NULL | Lý do xuất hủy | HET_HAN, HU_HONG, THU_HOI, KHAC |
| ghi_chu | NVARCHAR(500) | NULL | Ghi chú / lý do từ chối | — |
| trang_thai | VARCHAR(15) | NOT NULL | Trạng thái | CHO_DUYET, HOAN_THANH, TU_CHOI, DA_HUY |
| nguoi_lap_id | INT | FK→NguoiDung | Người lập phiếu | — |
| nguoi_duyet_id | INT | FK→NguoiDung, NULL | Người duyệt | — |
| thoi_gian_lap | DATETIME2(0) | NOT NULL DEFAULT GETUTCDATE() | Thời gian lập | UTC |
| thoi_gian_hoan_thanh | DATETIME2(0) | NULL | Thời gian hoàn thành | UTC |
| tong_gia_tri | DECIMAL(18,0) | NOT NULL DEFAULT 0 | Tổng giá trị (VND) | ≥ 0 |

### GiaoDich
| Tên cột | Kiểu SQL Server | Ràng buộc | Ý nghĩa | Miền giá trị |
|---------|----------------|-----------|---------|--------------|
| id | INT IDENTITY | PK | — | — |
| ma_giao_dich | NVARCHAR(30) | NOT NULL, UQ | Mã giao dịch | — |
| nha_thuoc_id | INT | FK→NhaThuoc, NOT NULL | Nhà thuốc | — |
| nguoi_ban_id | INT | FK→NguoiDung, NOT NULL | Người bán | — |
| khach_hang_id | INT | FK→KhachHang, NULL | Khách hàng | — |
| don_thuoc_id | INT | FK→DonThuoc, NULL | Đơn thuốc | — |
| thoi_gian | DATETIME2(0) | NOT NULL DEFAULT GETUTCDATE() | Thời gian bán | UTC |
| tong_tien | DECIMAL(18,0) | NOT NULL | Trước giảm giá | ≥ 0 |
| giam_gia | DECIMAL(18,0) | NOT NULL DEFAULT 0 | Giảm giá | ≥ 0 |
| thanh_tien | DECIMAL(18,0) | NOT NULL | Thực thu | ≥ 0, = tong_tien - giam_gia |
| phuong_thuc_thanh_toan | VARCHAR(15) | NOT NULL | Phương thức | TIEN_MAT, CHUYEN_KHOAN, THE |
| trang_thai | VARCHAR(10) | NOT NULL DEFAULT 'HOAN_THANH' | Trạng thái | HOAN_THANH, DA_HUY |
| ly_do_huy | NVARCHAR(500) | NULL | Lý do hủy | — |
| nguoi_huy_id | INT | FK→NguoiDung, NULL | Người hủy | — |
| thoi_gian_huy | DATETIME2(0) | NULL | Thời gian hủy | UTC |
| ghi_chu | NVARCHAR(500) | NULL | Ghi chú | — |

### ChiTietGiaoDich
| Tên cột | Kiểu SQL Server | Ràng buộc | Ý nghĩa | Miền giá trị |
|---------|----------------|-----------|---------|--------------|
| id | INT IDENTITY | PK | — | — |
| giao_dich_id | INT | FK→GiaoDich, NOT NULL | Giao dịch | — |
| thuoc_id | INT | FK→Thuoc, NOT NULL | Thuốc (phi chuẩn hóa) | — |
| lo_thuoc_id | INT | FK→LoThuoc, NOT NULL | Lô | — |
| so_luong | INT | NOT NULL, CHECK >0 | Số lượng | > 0 |
| don_gia | DECIMAL(18,0) | NOT NULL | Đơn giá tại thời điểm bán | > 0 |
| gia_von | DECIMAL(18,0) | NOT NULL | Giá vốn tại thời điểm bán | ≥ 0 |
| thanh_tien | DECIMAL(18,0) | NOT NULL | = don_gia * so_luong | ≥ 0 |

### BienDongKho
| Tên cột | Kiểu SQL Server | Ràng buộc | Ý nghĩa | Miền giá trị |
|---------|----------------|-----------|---------|--------------|
| id | BIGINT IDENTITY | PK | — | — |
| nha_thuoc_id | INT | FK→NhaThuoc, NOT NULL | Nhà thuốc | — |
| lo_thuoc_id | INT | FK→LoThuoc, NOT NULL | Lô | — |
| loai_bien_dong | VARCHAR(25) | NOT NULL | Loại | NHAP, BAN, HOAN_KHO, DIEU_CHUYEN_XUAT, DIEU_CHUYEN_NHAP, XUAT_HUY, DIEU_CHINH |
| so_luong_thay_doi | INT | NOT NULL | Thay đổi (+/-) | Dương=tăng, Âm=giảm |
| so_du_sau | INT | NOT NULL | Tồn sau biến động | ≥ 0 |
| chung_tu_loai | VARCHAR(15) | NOT NULL | Loại chứng từ | PHIEU_KHO, GIAO_DICH |
| chung_tu_id | INT | NOT NULL | ID chứng từ (không FK) | — |
| nguoi_thuc_hien_id | INT | FK→NguoiDung, NULL | Người thực hiện | — |
| thoi_gian | DATETIME2(0) | NOT NULL DEFAULT GETUTCDATE() | Thời gian | UTC |

### CauHinh
| Tên cột | Kiểu SQL Server | Ràng buộc | Ý nghĩa | Giá trị mặc định |
|---------|----------------|-----------|---------|-----------------|
| khoa | NVARCHAR(100) | PK | Khóa cấu hình | — |
| gia_tri | NVARCHAR(500) | NOT NULL | Giá trị | — |
| mo_ta | NVARCHAR(500) | NULL | Mô tả | — |

**Dữ liệu mặc định CauHinh:**

| khoa | gia_tri | mo_ta |
|------|---------|-------|
| canh_bao_han_muc1_ngay | 90 | Mức cảnh báo 1 (xanh) |
| canh_bao_han_muc2_ngay | 60 | Mức cảnh báo 2 (vàng) |
| canh_bao_han_muc3_ngay | 30 | Mức cảnh báo 3 (cam) |
| so_ngay_hieu_luc_don_mac_dinh | 5 | Hiệu lực đơn thuốc (ngày) |
| gio_cho_phep_huy_giao_dich | 24 | Giờ được phép hủy GD |
| so_ngay_ton_cham | 90 | Ngưỡng tồn chậm (ngày) |
| so_ngay_tinh_ban_trung_binh | 30 | Tính bán TB ngày (K5) |
| so_ngay_an_toan | 3 | Số ngày an toàn (K5) |

---

## 7. Index

```sql
-- Tìm kiếm theo mã vạch (hot path POS)
CREATE INDEX IX_MaVachThuoc_MaVach ON MaVachThuoc (ma_vach) INCLUDE (thuoc_id);

-- Tồn kho theo nhà thuốc
CREATE INDEX IX_TonKho_NhaThuoc ON TonKho (nha_thuoc_id) INCLUDE (lo_thuoc_id, so_luong);

-- FEFO: lô theo thuốc, sắp xếp hạn dùng
CREATE INDEX IX_LoThuoc_Thuoc_HanDung ON LoThuoc (thuoc_id, han_dung) INCLUDE (so_lo);

-- Cảnh báo hạn dùng
CREATE INDEX IX_TonKho_LoHanDung ON TonKho (nha_thuoc_id, lo_thuoc_id)
  INCLUDE (so_luong); -- join với LoThuoc.han_dung

-- Giao dịch theo thời gian (báo cáo)
CREATE INDEX IX_GiaoDich_NhaThuoc_Thoi ON GiaoDich (nha_thuoc_id, thoi_gian)
  WHERE trang_thai = 'HOAN_THANH';

-- BienDongKho (truy vết)
CREATE INDEX IX_BienDongKho_NhaThuoc_Lo ON BienDongKho (nha_thuoc_id, lo_thuoc_id, thoi_gian);

-- DonThuoc chờ bán
CREATE INDEX IX_DonThuoc_NhaThuoc_TrangThai ON DonThuoc (nha_thuoc_id, trang_thai)
  WHERE trang_thai IN ('CHUA_BAN','BAN_MOT_PHAN');
```

---

## 8. Xử lý Đồng thời

**Vấn đề:** Hai dược sĩ bán lô cuối cùng cùng lúc → tồn âm.

**Giải pháp:**
```sql
-- Khóa hàng khi đọc tồn (trong transaction bán)
SELECT so_luong FROM TonKho WITH (UPDLOCK, ROWLOCK)
WHERE nha_thuoc_id = @nha_thuoc AND lo_thuoc_id = @lo;

-- Khóa theo thứ tự lo_thuoc_id tăng dần (tránh deadlock khi điều chuyển nhiều lô)
-- Retry deadlock: catch SqlException (Number=1205), delay 50ms, retry tối đa 3 lần
```

| Tình huống | Cơ chế |
|------------|--------|
| Bán lô cuối | UPDLOCK + ROWLOCK trên TonKho; người sau bị block và thấy tồn=0 |
| Điều chuyển nhiều lô | Khóa theo thứ tự lo_thuoc_id tăng dần để tránh deadlock |
| Deadlock | Retry tối đa 3 lần với delay 50ms |

---

## 9. Định dạng JSON Truyền Giỏ hàng

### 9.1 Giỏ hàng bán (POST /ban/xac-nhan)
```json
{
  "nha_thuoc_id": 1,
  "nguoi_ban_id": 5,
  "khach_hang_id": null,
  "don_thuoc_id": null,
  "phuong_thuc_thanh_toan": "TIEN_MAT",
  "giam_gia": 0,
  "ghi_chu": null,
  "items": [
    { "thuoc_id": 10, "lo_thuoc_id": 25, "so_luong": 2, "don_gia": 15000 },
    { "thuoc_id": 11, "lo_thuoc_id": 30, "so_luong": 1, "don_gia": 50000 }
  ]
}
```

### 9.2 Phiếu nhập kho (POST /kho/phieu-nhap/hoan-thanh)
```json
{
  "phieu_kho_id": 3,
  "items": [
    {
      "thuoc_id": 10,
      "so_lo": "LO2025A",
      "ngay_san_xuat": "2025-01-01",
      "han_dung": "2027-01-01",
      "nha_cung_cap_id": 2,
      "so_luong": 100,
      "don_gia": 12000
    }
  ]
}
```

### 9.3 Phiếu điều chuyển / xuất hủy (POST /kho/dieu-chuyen/tao)
```json
{
  "nha_thuoc_id": 1,
  "nha_thuoc_den_id": 3,
  "loai_phieu": "DIEU_CHUYEN",
  "ghi_chu": "Điều chuyển vì nhu cầu cao Q.Bình Thạnh",
  "items": [
    { "lo_thuoc_id": 25, "so_luong": 20 },
    { "lo_thuoc_id": 26, "so_luong": 10 }
  ]
}
```
