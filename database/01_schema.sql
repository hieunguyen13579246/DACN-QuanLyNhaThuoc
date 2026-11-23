-- =====================================================================
-- 01_schema.sql  –  IS009 Quản lý Chuỗi Nhà thuốc
-- CẢNH BÁO: Script này XÓA và TẠO LẠI toàn bộ bảng trong DB IS009.
--           Không chạy trên môi trường sản xuất có dữ liệu thật.
-- SQL Server 2022 | Collation: Vietnamese_CI_AS
-- =====================================================================
USE master;
GO

-- Tạo database nếu chưa có
IF DB_ID(N'IS009') IS NULL
BEGIN
    CREATE DATABASE IS009
        COLLATE Vietnamese_CI_AS;
    PRINT N'Database IS009 đã được tạo.';
END
GO

USE IS009;
GO

-- Hỗ trợ chạy lại: tắt FK rồi DROP từng bảng theo thứ tự phụ thuộc
EXEC sp_MSforeachtable N'ALTER TABLE ? NOCHECK CONSTRAINT ALL';
GO

-- Xóa theo thứ tự phụ thuộc (lá trước)
DECLARE @tbls TABLE (nam NVARCHAR(128), ord INT);
INSERT @tbls VALUES
 (N'AuditLog',20),(N'BienDongKho',19),(N'ChiTietGiaoDich',18),
 (N'GiaoDich',17),(N'ChiTietDonThuoc',16),(N'DonThuoc',15),
 (N'KhachHang',14),(N'ChiTietPhieuKho',13),(N'PhieuKho',12),
 (N'DinhMucTon',11),(N'TonKho',10),(N'LoThuoc',9),
 (N'MaVachThuoc',8),(N'Thuoc',7),(N'NhomThuoc',6),
 (N'NhaCungCap',5),(N'NguoiDung',4),(N'NhaThuoc',3),
 (N'KhuVuc',2),(N'CauHinh',1);

DECLARE @sql NVARCHAR(500), @t NVARCHAR(128);
DECLARE cur CURSOR FAST_FORWARD FOR
    SELECT nam FROM @tbls ORDER BY ord DESC;
OPEN cur; FETCH NEXT FROM cur INTO @t;
WHILE @@FETCH_STATUS = 0
BEGIN
    IF OBJECT_ID(@t, N'U') IS NOT NULL
    BEGIN
        SET @sql = N'DROP TABLE ' + QUOTENAME(@t);
        EXEC sp_executesql @sql;
        PRINT N'Đã xóa bảng: ' + @t;
    END
    FETCH NEXT FROM cur INTO @t;
END
CLOSE cur; DEALLOCATE cur;
GO

-- =====================================================================
-- 1. CauHinh
-- =====================================================================
CREATE TABLE CauHinh (
    khoa    NVARCHAR(50)  NOT NULL,
    gia_tri NVARCHAR(200) NOT NULL,
    mo_ta   NVARCHAR(300) NULL,
    CONSTRAINT PK_CauHinh PRIMARY KEY (khoa)
);
GO

-- =====================================================================
-- 2. KhuVuc
-- =====================================================================
CREATE TABLE KhuVuc (
    id              INT IDENTITY(1,1) NOT NULL,
    ma              NVARCHAR(20)  NOT NULL,
    ten             NVARCHAR(100) NOT NULL,
    cap             NVARCHAR(5)   NOT NULL,
    khu_vuc_cha_id  INT           NULL,
    dan_so          INT           NULL,
    CONSTRAINT PK_KhuVuc   PRIMARY KEY (id),
    CONSTRAINT CK_KhuVuc_Cap  CHECK (cap IN (N'TINH', N'QUAN')),
    CONSTRAINT CK_KhuVuc_DanSo CHECK (dan_so IS NULL OR dan_so >= 0)
);
GO

-- =====================================================================
-- 3. NhaThuoc
-- =====================================================================
CREATE TABLE NhaThuoc (
    id              INT IDENTITY(1,1) NOT NULL,
    ma              NVARCHAR(20)  NOT NULL,
    ten             NVARCHAR(150) NOT NULL,
    dia_chi         NVARCHAR(250) NOT NULL,
    khu_vuc_id      INT           NOT NULL,
    so_dien_thoai   NVARCHAR(20)  NULL,
    trang_thai      NVARCHAR(15)  NOT NULL CONSTRAINT DF_NhaThuoc_TrangThai DEFAULT N'HOAT_DONG',
    ngay_khai_truong DATE         NULL,
    CONSTRAINT PK_NhaThuoc           PRIMARY KEY (id),
    CONSTRAINT FK_NhaThuoc_KhuVuc    FOREIGN KEY (khu_vuc_id) REFERENCES KhuVuc(id),
    CONSTRAINT CK_NhaThuoc_TrangThai CHECK (trang_thai IN (N'HOAT_DONG', N'TAM_NGUNG'))
);
GO

-- =====================================================================
-- 4. NguoiDung
-- =====================================================================
CREATE TABLE NguoiDung (
    id                      INT IDENTITY(1,1) NOT NULL,
    ho_ten                  NVARCHAR(100) NOT NULL,
    email                   NVARCHAR(150) NOT NULL,
    password_hash           NVARCHAR(100) NOT NULL,
    vai_tro                 NVARCHAR(10)  NOT NULL,
    nha_thuoc_id            INT           NULL,
    so_chung_chi_hanh_nghe  NVARCHAR(30)  NULL,
    kich_hoat               BIT           NOT NULL CONSTRAINT DF_NguoiDung_KichHoat DEFAULT 1,
    created_at              DATETIME2(0)  NOT NULL CONSTRAINT DF_NguoiDung_CreatedAt DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_NguoiDung          PRIMARY KEY (id),
    CONSTRAINT FK_NguoiDung_NhaThuoc FOREIGN KEY (nha_thuoc_id) REFERENCES NhaThuoc(id),
    CONSTRAINT CK_NguoiDung_VaiTro   CHECK (vai_tro IN (N'DUOCSI', N'THUKHO', N'QUANLY', N'QUANTRI')),
    CONSTRAINT CK_NguoiDung_NhaThuoc CHECK (
        (vai_tro = N'QUANTRI' AND nha_thuoc_id IS NULL)
        OR (vai_tro <> N'QUANTRI' AND nha_thuoc_id IS NOT NULL)
    )
);
GO

-- =====================================================================
-- 5. NhomThuoc
-- =====================================================================
CREATE TABLE NhomThuoc (
    id  INT IDENTITY(1,1) NOT NULL,
    ma  NVARCHAR(20)  NOT NULL,
    ten NVARCHAR(100) NOT NULL,
    CONSTRAINT PK_NhomThuoc PRIMARY KEY (id)
);
GO

-- =====================================================================
-- 6. NhaCungCap
-- =====================================================================
CREATE TABLE NhaCungCap (
    id            INT IDENTITY(1,1) NOT NULL,
    ma            NVARCHAR(20)  NOT NULL,
    ten           NVARCHAR(200) NOT NULL,
    dia_chi       NVARCHAR(250) NULL,
    so_dien_thoai NVARCHAR(20)  NULL,
    ma_so_thue    NVARCHAR(20)  NULL,
    trang_thai    NVARCHAR(10)  NOT NULL CONSTRAINT DF_NCC_TrangThai DEFAULT N'HOAT_DONG',
    CONSTRAINT PK_NhaCungCap          PRIMARY KEY (id),
    CONSTRAINT CK_NhaCungCap_TrangThai CHECK (trang_thai IN (N'HOAT_DONG', N'NGUNG'))
);
GO

-- =====================================================================
-- 7. Thuoc
-- =====================================================================
CREATE TABLE Thuoc (
    id            INT IDENTITY(1,1) NOT NULL,
    ma            NVARCHAR(30)  NOT NULL,
    ten           NVARCHAR(200) NOT NULL,
    hoat_chat     NVARCHAR(200) NOT NULL,
    ham_luong     NVARCHAR(50)  NULL,
    dang_bao_che  NVARCHAR(50)  NULL,
    don_vi_tinh   NVARCHAR(20)  NOT NULL,
    nhom_thuoc_id INT           NOT NULL,
    nha_san_xuat  NVARCHAR(150) NULL,
    can_ke_don    BIT           NOT NULL CONSTRAINT DF_Thuoc_CanKeDon DEFAULT 0,
    gia_ban_le    DECIMAL(18,0) NOT NULL,
    trang_thai    NVARCHAR(10)  NOT NULL CONSTRAINT DF_Thuoc_TrangThai DEFAULT N'DANG_BAN',
    mo_ta         NVARCHAR(500) NULL,
    CONSTRAINT PK_Thuoc             PRIMARY KEY (id),
    CONSTRAINT FK_Thuoc_NhomThuoc   FOREIGN KEY (nhom_thuoc_id) REFERENCES NhomThuoc(id),
    CONSTRAINT CK_Thuoc_GiaBanLe    CHECK (gia_ban_le > 0),
    CONSTRAINT CK_Thuoc_TrangThai   CHECK (trang_thai IN (N'DANG_BAN', N'NGUNG_BAN'))
);
GO

-- =====================================================================
-- 8. MaVachThuoc
-- =====================================================================
CREATE TABLE MaVachThuoc (
    id            INT IDENTITY(1,1) NOT NULL,
    thuoc_id      INT          NOT NULL,
    ma_vach       NVARCHAR(50) NOT NULL,
    loai_ma_vach  NVARCHAR(10) NOT NULL,
    la_ma_chinh   BIT          NOT NULL CONSTRAINT DF_MaVach_LaMaChinh DEFAULT 0,
    created_at    DATETIME2(0) NOT NULL CONSTRAINT DF_MaVach_CreatedAt DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_MaVachThuoc            PRIMARY KEY (id),
    CONSTRAINT FK_MaVachThuoc_Thuoc      FOREIGN KEY (thuoc_id) REFERENCES Thuoc(id),
    CONSTRAINT CK_MaVachThuoc_LoaiMaVach CHECK (loai_ma_vach IN (N'EAN13', N'GTIN14', N'CODE128', N'NOI_BO'))
    -- CHECK EAN13 đúng 13 chữ số được thực hiện trong SP sp_ThemMaVach (fn_KiemTraEAN13)
    -- vì SQL Server CHECK không thể gọi UDF có side-effect; validate ở tầng SP
);
GO

-- =====================================================================
-- 9. LoThuoc  (dùng chung toàn chuỗi)
-- =====================================================================
CREATE TABLE LoThuoc (
    id              INT IDENTITY(1,1) NOT NULL,
    thuoc_id        INT           NOT NULL,
    so_lo           NVARCHAR(50)  NOT NULL,
    ngay_san_xuat   DATE          NULL,
    han_dung        DATE          NOT NULL,
    nha_cung_cap_id INT           NULL,
    gia_nhap        DECIMAL(18,0) NOT NULL CONSTRAINT DF_LoThuoc_GiaNhap DEFAULT 0,
    created_at      DATETIME2(0)  NOT NULL CONSTRAINT DF_LoThuoc_CreatedAt DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_LoThuoc            PRIMARY KEY (id),
    CONSTRAINT FK_LoThuoc_Thuoc      FOREIGN KEY (thuoc_id) REFERENCES Thuoc(id),
    CONSTRAINT FK_LoThuoc_NCC        FOREIGN KEY (nha_cung_cap_id) REFERENCES NhaCungCap(id),
    CONSTRAINT CK_LoThuoc_GiaNhap    CHECK (gia_nhap >= 0),
    CONSTRAINT CK_LoThuoc_HanDung    CHECK (ngay_san_xuat IS NULL OR han_dung > ngay_san_xuat)
);
GO

-- =====================================================================
-- 10. TonKho  (PK kép)
-- =====================================================================
CREATE TABLE TonKho (
    nha_thuoc_id INT          NOT NULL,
    lo_thuoc_id  INT          NOT NULL,
    so_luong     INT          NOT NULL CONSTRAINT DF_TonKho_SoLuong DEFAULT 0,
    cap_nhat_luc DATETIME2(0) NOT NULL CONSTRAINT DF_TonKho_CapNhatLuc DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_TonKho          PRIMARY KEY (nha_thuoc_id, lo_thuoc_id),
    CONSTRAINT FK_TonKho_NhaThuoc FOREIGN KEY (nha_thuoc_id) REFERENCES NhaThuoc(id),
    CONSTRAINT FK_TonKho_LoThuoc  FOREIGN KEY (lo_thuoc_id)  REFERENCES LoThuoc(id),
    CONSTRAINT CK_TonKho_SoLuong  CHECK (so_luong >= 0)
);
GO

-- =====================================================================
-- 11. DinhMucTon
-- =====================================================================
CREATE TABLE DinhMucTon (
    id                INT IDENTITY(1,1) NOT NULL,
    nha_thuoc_id      INT NOT NULL,
    thuoc_id          INT NOT NULL,
    ton_toi_thieu     INT NOT NULL CONSTRAINT DF_DinhMuc_TonToiThieu DEFAULT 0,
    ton_toi_da        INT NULL,
    so_ngay_giao_hang INT NOT NULL CONSTRAINT DF_DinhMuc_NguayGiaoHang DEFAULT 3,
    CONSTRAINT PK_DinhMucTon           PRIMARY KEY (id),
    CONSTRAINT FK_DinhMuc_NhaThuoc     FOREIGN KEY (nha_thuoc_id) REFERENCES NhaThuoc(id),
    CONSTRAINT FK_DinhMuc_Thuoc        FOREIGN KEY (thuoc_id)     REFERENCES Thuoc(id),
    CONSTRAINT CK_DinhMuc_TonToiThieu  CHECK (ton_toi_thieu >= 0),
    CONSTRAINT CK_DinhMuc_TonToiDa     CHECK (ton_toi_da IS NULL OR ton_toi_da >= ton_toi_thieu),
    CONSTRAINT CK_DinhMuc_SoNgay       CHECK (so_ngay_giao_hang > 0)
);
GO

-- =====================================================================
-- 12. PhieuKho
-- =====================================================================
CREATE TABLE PhieuKho (
    id                  INT IDENTITY(1,1) NOT NULL,
    ma_phieu            NVARCHAR(30)  NOT NULL,
    loai_phieu          NVARCHAR(15)  NOT NULL,
    nha_thuoc_id        INT           NOT NULL,
    nha_thuoc_den_id    INT           NULL,
    nha_cung_cap_id     INT           NULL,
    ly_do               NVARCHAR(20)  NULL,
    ghi_chu             NVARCHAR(500) NULL,
    trang_thai          NVARCHAR(15)  NOT NULL CONSTRAINT DF_PhieuKho_TrangThai DEFAULT N'CHO_DUYET',
    nguoi_lap_id        INT           NOT NULL,
    nguoi_duyet_id      INT           NULL,
    thoi_gian_lap       DATETIME2(0)  NOT NULL CONSTRAINT DF_PhieuKho_ThoiGianLap DEFAULT SYSUTCDATETIME(),
    thoi_gian_hoan_thanh DATETIME2(0) NULL,
    tong_gia_tri        DECIMAL(18,0) NOT NULL CONSTRAINT DF_PhieuKho_TongGiaTri DEFAULT 0,
    CONSTRAINT PK_PhieuKho               PRIMARY KEY (id),
    CONSTRAINT FK_PhieuKho_NhaThuoc      FOREIGN KEY (nha_thuoc_id)     REFERENCES NhaThuoc(id),
    CONSTRAINT FK_PhieuKho_NhaThuocDen   FOREIGN KEY (nha_thuoc_den_id) REFERENCES NhaThuoc(id),
    CONSTRAINT FK_PhieuKho_NCC           FOREIGN KEY (nha_cung_cap_id)  REFERENCES NhaCungCap(id),
    CONSTRAINT FK_PhieuKho_NguoiLap      FOREIGN KEY (nguoi_lap_id)     REFERENCES NguoiDung(id),
    CONSTRAINT FK_PhieuKho_NguoiDuyet    FOREIGN KEY (nguoi_duyet_id)   REFERENCES NguoiDung(id),
    CONSTRAINT CK_PhieuKho_LoaiPhieu     CHECK (loai_phieu IN (N'NHAP', N'XUAT_HUY', N'DIEU_CHUYEN')),
    CONSTRAINT CK_PhieuKho_TrangThai     CHECK (trang_thai IN (N'CHO_DUYET', N'HOAN_THANH', N'TU_CHOI', N'DA_HUY')),
    CONSTRAINT CK_PhieuKho_NHAP         CHECK (
        loai_phieu <> N'NHAP' OR (nha_cung_cap_id IS NOT NULL AND nha_thuoc_den_id IS NULL)
    ),
    CONSTRAINT CK_PhieuKho_DIEU_CHUYEN  CHECK (
        loai_phieu <> N'DIEU_CHUYEN' OR (nha_thuoc_den_id IS NOT NULL AND nha_thuoc_den_id <> nha_thuoc_id)
    ),
    CONSTRAINT CK_PhieuKho_XUAT_HUY     CHECK (
        loai_phieu <> N'XUAT_HUY' OR ly_do IN (N'HET_HAN', N'HU_HONG', N'THU_HOI', N'KHAC')
    )
);
GO

-- =====================================================================
-- 13. ChiTietPhieuKho
-- =====================================================================
CREATE TABLE ChiTietPhieuKho (
    id           INT IDENTITY(1,1) NOT NULL,
    phieu_kho_id INT           NOT NULL,
    lo_thuoc_id  INT           NOT NULL,
    so_luong     INT           NOT NULL,
    don_gia      DECIMAL(18,0) NULL,
    CONSTRAINT PK_ChiTietPhieuKho         PRIMARY KEY (id),
    CONSTRAINT FK_CTPK_PhieuKho           FOREIGN KEY (phieu_kho_id) REFERENCES PhieuKho(id),
    CONSTRAINT FK_CTPK_LoThuoc            FOREIGN KEY (lo_thuoc_id)  REFERENCES LoThuoc(id),
    CONSTRAINT CK_CTPK_SoLuong            CHECK (so_luong > 0)
);
GO

-- =====================================================================
-- 14. BienDongKho  (append-only, BIGINT id)
-- =====================================================================
CREATE TABLE BienDongKho (
    id                   BIGINT IDENTITY(1,1) NOT NULL,
    nha_thuoc_id         INT          NOT NULL,
    lo_thuoc_id          INT          NOT NULL,
    loai_bien_dong       NVARCHAR(25) NOT NULL,
    so_luong_thay_doi    INT          NOT NULL,
    so_du_sau            INT          NOT NULL,
    chung_tu_loai        NVARCHAR(15) NOT NULL,
    chung_tu_id          INT          NOT NULL,
    nguoi_thuc_hien_id   INT          NULL,
    thoi_gian            DATETIME2(0) NOT NULL CONSTRAINT DF_BDK_ThoiGian DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_BienDongKho            PRIMARY KEY (id),
    CONSTRAINT FK_BDK_NhaThuoc           FOREIGN KEY (nha_thuoc_id)       REFERENCES NhaThuoc(id),
    CONSTRAINT FK_BDK_LoThuoc            FOREIGN KEY (lo_thuoc_id)         REFERENCES LoThuoc(id),
    CONSTRAINT FK_BDK_NguoiThucHien      FOREIGN KEY (nguoi_thuc_hien_id)  REFERENCES NguoiDung(id),
    CONSTRAINT CK_BDK_LoaiBienDong       CHECK (loai_bien_dong IN (
        N'NHAP', N'BAN', N'HOAN_KHO', N'DIEU_CHUYEN_XUAT',
        N'DIEU_CHUYEN_NHAP', N'XUAT_HUY', N'DIEU_CHINH'
    )),
    CONSTRAINT CK_BDK_SoLuongThayDoi    CHECK (so_luong_thay_doi <> 0),
    CONSTRAINT CK_BDK_SoDuSau           CHECK (so_du_sau >= 0),
    CONSTRAINT CK_BDK_ChungTuLoai       CHECK (chung_tu_loai IN (N'PHIEU_KHO', N'GIAO_DICH'))
);
GO

-- =====================================================================
-- 15. KhachHang
-- =====================================================================
CREATE TABLE KhachHang (
    id              INT IDENTITY(1,1) NOT NULL,
    ma              NVARCHAR(20)  NOT NULL,
    ho_ten          NVARCHAR(100) NOT NULL,
    so_dien_thoai   NVARCHAR(20)  NULL,
    gioi_tinh       NVARCHAR(5)   NULL,
    nam_sinh        SMALLINT      NULL,
    khu_vuc_id      INT           NULL,
    dia_chi         NVARCHAR(250) NULL,
    ghi_chu_di_ung  NVARCHAR(500) NULL,
    created_at      DATETIME2(0)  NOT NULL CONSTRAINT DF_KhachHang_CreatedAt DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_KhachHang          PRIMARY KEY (id),
    CONSTRAINT FK_KhachHang_KhuVuc   FOREIGN KEY (khu_vuc_id) REFERENCES KhuVuc(id),
    CONSTRAINT CK_KhachHang_NamSinh  CHECK (nam_sinh IS NULL OR (nam_sinh >= 1900 AND nam_sinh <= 2100))
);
GO

-- =====================================================================
-- 16. DonThuoc
-- =====================================================================
CREATE TABLE DonThuoc (
    id                  INT IDENTITY(1,1) NOT NULL,
    ma_don              NVARCHAR(30)  NOT NULL,
    khach_hang_id       INT           NOT NULL,
    nha_thuoc_id        INT           NOT NULL,
    ten_bac_si          NVARCHAR(100) NOT NULL,
    co_so_kham          NVARCHAR(200) NOT NULL,
    chan_doan           NVARCHAR(200) NULL,
    ngay_ke_don         DATE          NOT NULL,
    so_ngay_hieu_luc    INT           NOT NULL,
    trang_thai          NVARCHAR(15)  NOT NULL CONSTRAINT DF_DonThuoc_TrangThai DEFAULT N'CHUA_BAN',
    nguoi_tiep_nhan_id  INT           NOT NULL,
    created_at          DATETIME2(0)  NOT NULL CONSTRAINT DF_DonThuoc_CreatedAt DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_DonThuoc               PRIMARY KEY (id),
    CONSTRAINT FK_DonThuoc_KhachHang     FOREIGN KEY (khach_hang_id)      REFERENCES KhachHang(id),
    CONSTRAINT FK_DonThuoc_NhaThuoc      FOREIGN KEY (nha_thuoc_id)       REFERENCES NhaThuoc(id),
    CONSTRAINT FK_DonThuoc_NguoiTiepNhan FOREIGN KEY (nguoi_tiep_nhan_id) REFERENCES NguoiDung(id),
    CONSTRAINT CK_DonThuoc_TrangThai     CHECK (trang_thai IN (
        N'CHUA_BAN', N'BAN_MOT_PHAN', N'DA_BAN_HET', N'HET_HIEU_LUC', N'HUY'
    )),
    CONSTRAINT CK_DonThuoc_HieuLuc      CHECK (so_ngay_hieu_luc > 0)
);
GO

-- =====================================================================
-- 17. ChiTietDonThuoc
-- =====================================================================
CREATE TABLE ChiTietDonThuoc (
    id               INT IDENTITY(1,1) NOT NULL,
    don_thuoc_id     INT           NOT NULL,
    thuoc_id         INT           NOT NULL,
    so_luong_ke      INT           NOT NULL,
    so_luong_da_ban  INT           NOT NULL CONSTRAINT DF_CTDT_SoLuongDaBan DEFAULT 0,
    lieu_dung        NVARCHAR(200) NULL,
    CONSTRAINT PK_ChiTietDonThuoc        PRIMARY KEY (id),
    CONSTRAINT FK_CTDT_DonThuoc          FOREIGN KEY (don_thuoc_id) REFERENCES DonThuoc(id),
    CONSTRAINT FK_CTDT_Thuoc             FOREIGN KEY (thuoc_id)     REFERENCES Thuoc(id),
    CONSTRAINT CK_CTDT_SoLuongKe        CHECK (so_luong_ke > 0),
    CONSTRAINT CK_CTDT_SoLuongDaBan     CHECK (so_luong_da_ban >= 0 AND so_luong_da_ban <= so_luong_ke)
);
GO

-- =====================================================================
-- 18. GiaoDich
-- =====================================================================
CREATE TABLE GiaoDich (
    id                    INT IDENTITY(1,1) NOT NULL,
    ma_giao_dich          NVARCHAR(30)  NOT NULL,
    nha_thuoc_id          INT           NOT NULL,
    nguoi_ban_id          INT           NOT NULL,
    khach_hang_id         INT           NULL,
    don_thuoc_id          INT           NULL,
    thoi_gian             DATETIME2(0)  NOT NULL CONSTRAINT DF_GD_ThoiGian DEFAULT SYSUTCDATETIME(),
    tong_tien             DECIMAL(18,0) NOT NULL,
    giam_gia              DECIMAL(18,0) NOT NULL CONSTRAINT DF_GD_GiamGia DEFAULT 0,
    thanh_tien            DECIMAL(18,0) NOT NULL,
    phuong_thuc_thanh_toan NVARCHAR(15) NOT NULL,
    trang_thai            NVARCHAR(10)  NOT NULL CONSTRAINT DF_GD_TrangThai DEFAULT N'HOAN_THANH',
    ly_do_huy             NVARCHAR(500) NULL,
    nguoi_huy_id          INT           NULL,
    thoi_gian_huy         DATETIME2(0)  NULL,
    ghi_chu               NVARCHAR(500) NULL,
    CONSTRAINT PK_GiaoDich               PRIMARY KEY (id),
    CONSTRAINT FK_GD_NhaThuoc            FOREIGN KEY (nha_thuoc_id)  REFERENCES NhaThuoc(id),
    CONSTRAINT FK_GD_NguoiBan            FOREIGN KEY (nguoi_ban_id)  REFERENCES NguoiDung(id),
    CONSTRAINT FK_GD_KhachHang           FOREIGN KEY (khach_hang_id) REFERENCES KhachHang(id),
    CONSTRAINT FK_GD_DonThuoc            FOREIGN KEY (don_thuoc_id)  REFERENCES DonThuoc(id),
    CONSTRAINT FK_GD_NguoiHuy            FOREIGN KEY (nguoi_huy_id)  REFERENCES NguoiDung(id),
    CONSTRAINT CK_GD_PTTT               CHECK (phuong_thuc_thanh_toan IN (N'TIEN_MAT', N'CHUYEN_KHOAN', N'THE')),
    CONSTRAINT CK_GD_TrangThai          CHECK (trang_thai IN (N'HOAN_THANH', N'DA_HUY')),
    CONSTRAINT CK_GD_ThanhTien          CHECK (thanh_tien = tong_tien - giam_gia),
    CONSTRAINT CK_GD_TongTien           CHECK (tong_tien >= 0),
    CONSTRAINT CK_GD_GiamGia            CHECK (giam_gia >= 0),
    CONSTRAINT CK_GD_LyDoHuy           CHECK (trang_thai <> N'DA_HUY' OR (ly_do_huy IS NOT NULL AND LEN(LTRIM(ly_do_huy)) > 0))
);
GO

-- =====================================================================
-- 19. ChiTietGiaoDich
-- =====================================================================
CREATE TABLE ChiTietGiaoDich (
    id           INT IDENTITY(1,1) NOT NULL,
    giao_dich_id INT           NOT NULL,
    thuoc_id     INT           NOT NULL,
    lo_thuoc_id  INT           NOT NULL,
    so_luong     INT           NOT NULL,
    don_gia      DECIMAL(18,0) NOT NULL,
    gia_von      DECIMAL(18,0) NOT NULL,
    thanh_tien   DECIMAL(18,0) NOT NULL,
    CONSTRAINT PK_ChiTietGiaoDich        PRIMARY KEY (id),
    CONSTRAINT FK_CTGD_GiaoDich          FOREIGN KEY (giao_dich_id) REFERENCES GiaoDich(id),
    CONSTRAINT FK_CTGD_Thuoc             FOREIGN KEY (thuoc_id)     REFERENCES Thuoc(id),
    CONSTRAINT FK_CTGD_LoThuoc           FOREIGN KEY (lo_thuoc_id)  REFERENCES LoThuoc(id),
    CONSTRAINT CK_CTGD_SoLuong          CHECK (so_luong > 0),
    CONSTRAINT CK_CTGD_DonGia           CHECK (don_gia > 0),
    CONSTRAINT CK_CTGD_GiaVon           CHECK (gia_von >= 0),
    CONSTRAINT CK_CTGD_ThanhTien        CHECK (thanh_tien = so_luong * don_gia)
);
GO

-- =====================================================================
-- 20. AuditLog  (append-only, BIGINT id)
-- =====================================================================
CREATE TABLE AuditLog (
    id           BIGINT IDENTITY(1,1) NOT NULL,
    nguoi_dung_id INT           NULL,
    hanh_dong    NVARCHAR(100) NOT NULL,
    doi_tuong    NVARCHAR(50)  NOT NULL,
    doi_tuong_id INT           NULL,
    du_lieu_cu   NVARCHAR(MAX) NULL,
    du_lieu_moi  NVARCHAR(MAX) NULL,
    created_at   DATETIME2(0)  NOT NULL CONSTRAINT DF_AuditLog_CreatedAt DEFAULT SYSUTCDATETIME(),
    CONSTRAINT PK_AuditLog            PRIMARY KEY (id),
    CONSTRAINT FK_AuditLog_NguoiDung  FOREIGN KEY (nguoi_dung_id) REFERENCES NguoiDung(id),
    CONSTRAINT CK_AuditLog_DuLieuCu  CHECK (du_lieu_cu  IS NULL OR ISJSON(du_lieu_cu)  = 1),
    CONSTRAINT CK_AuditLog_DuLieuMoi CHECK (du_lieu_moi IS NULL OR ISJSON(du_lieu_moi) = 1)
);
GO

