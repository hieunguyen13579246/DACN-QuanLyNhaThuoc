-- =====================================================================
-- 02_constraints_indexes.sql  –  IS009
-- Unique constraints, indexes, và dữ liệu khởi tạo CauHinh
-- Chạy lại không lỗi (kiểm tra IF NOT EXISTS trước khi tạo)
-- =====================================================================
USE IS009;
GO

-- =====================================================================
-- UNIQUE CONSTRAINTS
-- =====================================================================
IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_KhuVuc_Ma' AND parent_object_id = OBJECT_ID(N'KhuVuc'))
    ALTER TABLE KhuVuc ADD CONSTRAINT UQ_KhuVuc_Ma UNIQUE (ma);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_NhaThuoc_Ma' AND parent_object_id = OBJECT_ID(N'NhaThuoc'))
    ALTER TABLE NhaThuoc ADD CONSTRAINT UQ_NhaThuoc_Ma UNIQUE (ma);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_NguoiDung_Email' AND parent_object_id = OBJECT_ID(N'NguoiDung'))
    ALTER TABLE NguoiDung ADD CONSTRAINT UQ_NguoiDung_Email UNIQUE (email);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_NhomThuoc_Ma' AND parent_object_id = OBJECT_ID(N'NhomThuoc'))
    ALTER TABLE NhomThuoc ADD CONSTRAINT UQ_NhomThuoc_Ma UNIQUE (ma);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_Thuoc_Ma' AND parent_object_id = OBJECT_ID(N'Thuoc'))
    ALTER TABLE Thuoc ADD CONSTRAINT UQ_Thuoc_Ma UNIQUE (ma);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_MaVachThuoc_MaVach' AND parent_object_id = OBJECT_ID(N'MaVachThuoc'))
    ALTER TABLE MaVachThuoc ADD CONSTRAINT UQ_MaVachThuoc_MaVach UNIQUE (ma_vach);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_NhaCungCap_Ma' AND parent_object_id = OBJECT_ID(N'NhaCungCap'))
    ALTER TABLE NhaCungCap ADD CONSTRAINT UQ_NhaCungCap_Ma UNIQUE (ma);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_LoThuoc_ThuocSoLo' AND parent_object_id = OBJECT_ID(N'LoThuoc'))
    ALTER TABLE LoThuoc ADD CONSTRAINT UQ_LoThuoc_ThuocSoLo UNIQUE (thuoc_id, so_lo);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_DinhMucTon_NTThuoc' AND parent_object_id = OBJECT_ID(N'DinhMucTon'))
    ALTER TABLE DinhMucTon ADD CONSTRAINT UQ_DinhMucTon_NTThuoc UNIQUE (nha_thuoc_id, thuoc_id);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_PhieuKho_MaPhieu' AND parent_object_id = OBJECT_ID(N'PhieuKho'))
    ALTER TABLE PhieuKho ADD CONSTRAINT UQ_PhieuKho_MaPhieu UNIQUE (ma_phieu);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_KhachHang_Ma' AND parent_object_id = OBJECT_ID(N'KhachHang'))
    ALTER TABLE KhachHang ADD CONSTRAINT UQ_KhachHang_Ma UNIQUE (ma);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_DonThuoc_MaDon' AND parent_object_id = OBJECT_ID(N'DonThuoc'))
    ALTER TABLE DonThuoc ADD CONSTRAINT UQ_DonThuoc_MaDon UNIQUE (ma_don);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_CTDT_DonThuocThuoc' AND parent_object_id = OBJECT_ID(N'ChiTietDonThuoc'))
    ALTER TABLE ChiTietDonThuoc ADD CONSTRAINT UQ_CTDT_DonThuocThuoc UNIQUE (don_thuoc_id, thuoc_id);

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_GiaoDich_MaGiaoDich' AND parent_object_id = OBJECT_ID(N'GiaoDich'))
    ALTER TABLE GiaoDich ADD CONSTRAINT UQ_GiaoDich_MaGiaoDich UNIQUE (ma_giao_dich);

-- FK tự tham chiếu KhuVuc (thêm sau khi bảng đã tồn tại)
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = N'FK_KhuVuc_KhuVucCha')
    ALTER TABLE KhuVuc ADD CONSTRAINT FK_KhuVuc_KhuVucCha
        FOREIGN KEY (khu_vuc_cha_id) REFERENCES KhuVuc(id);

PRINT N'Unique constraints: OK';
GO

-- =====================================================================
-- INDEXES  (filtered indexes yêu cầu SET QUOTED_IDENTIFIER ON)
-- =====================================================================
SET QUOTED_IDENTIFIER ON;
GO

-- MaVachThuoc: unique có điều kiện (mỗi thuốc chỉ 1 mã chính)
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'UX_MaVachThuoc_MaChinh' AND object_id = OBJECT_ID(N'MaVachThuoc'))
    CREATE UNIQUE INDEX UX_MaVachThuoc_MaChinh
        ON MaVachThuoc (thuoc_id)
        WHERE la_ma_chinh = 1;

-- MaVachThuoc: tra cứu theo thuoc_id (tìm mã vạch của thuốc)
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_MaVachThuoc_ThuocId' AND object_id = OBJECT_ID(N'MaVachThuoc'))
    CREATE INDEX IX_MaVachThuoc_ThuocId
        ON MaVachThuoc (thuoc_id)
        INCLUDE (ma_vach, loai_ma_vach, la_ma_chinh);

-- LoThuoc: FEFO – sắp xếp theo thuoc_id và han_dung
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_LoThuoc_ThuocHanDung' AND object_id = OBJECT_ID(N'LoThuoc'))
    CREATE INDEX IX_LoThuoc_ThuocHanDung
        ON LoThuoc (thuoc_id, han_dung, id)
        INCLUDE (so_lo, gia_nhap);

-- TonKho: tra cứu theo lo_thuoc_id
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_TonKho_LoThuocId' AND object_id = OBJECT_ID(N'TonKho'))
    CREATE INDEX IX_TonKho_LoThuocId
        ON TonKho (lo_thuoc_id)
        INCLUDE (nha_thuoc_id, so_luong);

-- GiaoDich: báo cáo theo nhà thuốc + thời gian
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_GiaoDich_NhaThuocThoiGian' AND object_id = OBJECT_ID(N'GiaoDich'))
    CREATE INDEX IX_GiaoDich_NhaThuocThoiGian
        ON GiaoDich (nha_thuoc_id, thoi_gian)
        INCLUDE (trang_thai, thanh_tien, tong_tien, giam_gia, don_thuoc_id, nguoi_ban_id);

-- ChiTietGiaoDich: tra theo giao_dich_id
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_CTGD_GiaoDichId' AND object_id = OBJECT_ID(N'ChiTietGiaoDich'))
    CREATE INDEX IX_CTGD_GiaoDichId
        ON ChiTietGiaoDich (giao_dich_id)
        INCLUDE (thuoc_id, lo_thuoc_id, so_luong, don_gia, gia_von, thanh_tien);

-- ChiTietGiaoDich: tra theo thuoc + lo
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_CTGD_ThuocLo' AND object_id = OBJECT_ID(N'ChiTietGiaoDich'))
    CREATE INDEX IX_CTGD_ThuocLo
        ON ChiTietGiaoDich (thuoc_id, lo_thuoc_id)
        INCLUDE (so_luong, giao_dich_id);

-- BienDongKho: đối soát tồn kho
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_BDK_NhaThuocLoThoiGian' AND object_id = OBJECT_ID(N'BienDongKho'))
    CREATE INDEX IX_BDK_NhaThuocLoThoiGian
        ON BienDongKho (nha_thuoc_id, lo_thuoc_id, thoi_gian)
        INCLUDE (loai_bien_dong, so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id);

-- DonThuoc: tra theo khach_hang_id
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_DonThuoc_KhachHang' AND object_id = OBJECT_ID(N'DonThuoc'))
    CREATE INDEX IX_DonThuoc_KhachHang
        ON DonThuoc (khach_hang_id)
        INCLUDE (ma_don, trang_thai, ngay_ke_don);

-- DonThuoc: tra theo nha_thuoc + trang_thai
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_DonThuoc_NhaThuocTrangThai' AND object_id = OBJECT_ID(N'DonThuoc'))
    CREATE INDEX IX_DonThuoc_NhaThuocTrangThai
        ON DonThuoc (nha_thuoc_id, trang_thai)
        INCLUDE (ma_don, khach_hang_id, ngay_ke_don, so_ngay_hieu_luc);

-- PhieuKho: tra theo nha_thuoc + trang_thai
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_PhieuKho_NhaThuocTrangThai' AND object_id = OBJECT_ID(N'PhieuKho'))
    CREATE INDEX IX_PhieuKho_NhaThuocTrangThai
        ON PhieuKho (nha_thuoc_id, trang_thai)
        INCLUDE (loai_phieu, ma_phieu, nguoi_lap_id, thoi_gian_lap);

-- KhachHang: tra theo so_dien_thoai
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_KhachHang_SoDienThoai' AND object_id = OBJECT_ID(N'KhachHang'))
    CREATE INDEX IX_KhachHang_SoDienThoai
        ON KhachHang (so_dien_thoai)
        WHERE so_dien_thoai IS NOT NULL;

-- NguoiDung: tra theo nha_thuoc_id
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_NguoiDung_NhaThuoc' AND object_id = OBJECT_ID(N'NguoiDung'))
    CREATE INDEX IX_NguoiDung_NhaThuoc
        ON NguoiDung (nha_thuoc_id)
        WHERE nha_thuoc_id IS NOT NULL;

-- AuditLog: tra theo doi_tuong + created_at
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_AuditLog_DoiTuong' AND object_id = OBJECT_ID(N'AuditLog'))
    CREATE INDEX IX_AuditLog_DoiTuong
        ON AuditLog (doi_tuong, doi_tuong_id, created_at);

PRINT N'Indexes: OK';
GO

-- =====================================================================
-- DỮ LIỆU KHỞI TẠO CauHinh
-- =====================================================================
MERGE CauHinh AS target
USING (VALUES
    (N'canh_bao_han_muc1_ngay',          N'90',   N'Ngưỡng cảnh báo hạn dùng mức 1 (MUC_90)'),
    (N'canh_bao_han_muc2_ngay',          N'60',   N'Ngưỡng cảnh báo hạn dùng mức 2 (MUC_60)'),
    (N'canh_bao_han_muc3_ngay',          N'30',   N'Ngưỡng cảnh báo hạn dùng mức 3 (MUC_30)'),
    (N'so_ngay_hieu_luc_don_mac_dinh',   N'5',    N'Số ngày hiệu lực đơn thuốc mặc định (TT 52/2017)'),
    (N'gio_cho_phep_huy_giao_dich',      N'24',   N'Số giờ cho phép hủy giao dịch bán'),
    (N'so_ngay_ton_cham',                N'90',   N'Số ngày không bán coi là tồn chậm'),
    (N'so_ngay_tinh_ban_trung_binh',     N'30',   N'Số ngày tính bán trung bình/ngày (K5)'),
    (N'so_ngay_an_toan',                 N'3',    N'Số ngày an toàn trong công thức đề xuất nhập (K5)')
) AS source (khoa, gia_tri, mo_ta)
ON target.khoa = source.khoa
WHEN MATCHED THEN UPDATE SET gia_tri = source.gia_tri, mo_ta = source.mo_ta
WHEN NOT MATCHED THEN INSERT (khoa, gia_tri, mo_ta) VALUES (source.khoa, source.gia_tri, source.mo_ta);

PRINT N'CauHinh: ' + CAST(@@ROWCOUNT AS NVARCHAR(10)) + N' dòng upsert.';
GO