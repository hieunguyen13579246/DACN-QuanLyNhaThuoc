-- =====================================================================
-- 06_seed_p2.sql – Dữ liệu nghiệp vụ giả lập IS009
-- Bao gồm: LoThuoc, PhieuKho, ChiTietPhieuKho, KhachHang, DinhMucTon,
--          DonThuoc, ChiTietDonThuoc, GiaoDich (3000 bán lẻ + 300 kê đơn),
--          ChiTietGiaoDich, TonKho, BienDongKho, AuditLog
-- =====================================================================
USE IS009;
SET QUOTED_IDENTIFIER ON;
SET NOCOUNT ON;
GO

-- Tắt trigger append-only trong lúc nạp seed
DISABLE TRIGGER trg_BienDongKho_ChiThem_Upd ON BienDongKho;
DISABLE TRIGGER trg_BienDongKho_ChiThem_Del ON BienDongKho;
DISABLE TRIGGER trg_AuditLog_ChiThem_Upd    ON AuditLog;
DISABLE TRIGGER trg_AuditLog_ChiThem_Del    ON AuditLog;
GO

-- Làm sạch dữ liệu phần 2 nếu chạy lại độc lập
DELETE FROM AuditLog;
DELETE FROM BienDongKho;
DELETE FROM ChiTietGiaoDich;
DELETE FROM GiaoDich;
DELETE FROM ChiTietDonThuoc;
DELETE FROM DonThuoc;
DELETE FROM ChiTietPhieuKho;
DELETE FROM PhieuKho;
DELETE FROM DinhMucTon;
DELETE FROM TonKho;
DELETE FROM LoThuoc;
DELETE FROM KhachHang;

DBCC CHECKIDENT ('AuditLog', RESEED, 0);
DBCC CHECKIDENT ('BienDongKho', RESEED, 0);
DBCC CHECKIDENT ('ChiTietGiaoDich', RESEED, 0);
DBCC CHECKIDENT ('ChiTietDonThuoc', RESEED, 0);
DBCC CHECKIDENT ('ChiTietPhieuKho', RESEED, 0);
GO

-- === TEMP TABLES ===
IF OBJECT_ID('tempdb..#ton')     IS NOT NULL DROP TABLE #ton;
IF OBJECT_ID('tempdb..#lo_gia')  IS NOT NULL DROP TABLE #lo_gia;
CREATE TABLE #ton    (nt_id INT, lo_id INT, so_luong INT, PRIMARY KEY(nt_id, lo_id));
CREATE TABLE #lo_gia (lo_id INT PRIMARY KEY, thuoc_id INT, han_dung DATE, gia_nhap DECIMAL(18,0));
GO

-- =====================================================================
-- 1. LOTHUOC: 180 lô (60 thuốc × 3 lô/thuốc)
--    Đủ 4 mức cảnh báo (DA_HET_HAN, MUC_30, MUC_60, MUC_90) + hạn dài (>90 ngày)
-- =====================================================================
SET IDENTITY_INSERT LoThuoc ON;
DECLARE @today DATE = CAST(SWITCHOFFSET(SYSUTCDATETIME(), N'+07:00') AS DATE);
DECLARE @li INT = 1, @tid INT, @lt INT, @han DATE, @nsx DATE, @gi DECIMAL(18,0);

WHILE @li <= 180
BEGIN
    SET @tid = ((@li - 1) / 3) + 1;
    SET @lt  = (@li - 1) % 3;

    -- Phân bổ hạn dùng:
    -- @lt = 0: Lô cũ (thuốc 1-20: đã hết hạn; thuốc 21-40: <=30 ngày; thuốc 41-60: 31-60 ngày)
    -- @lt = 1: Lô cận hạn (61-90 ngày hoặc 31-60 ngày)
    -- @lt = 2: Lô hạn dài (8-18 tháng) phục vụ bán FEFO bình thường
    SET @han = CASE
        WHEN @lt = 0 AND @tid <= 20 THEN DATEADD(DAY, -15 - (@tid % 15), @today)  -- DA_HET_HAN
        WHEN @lt = 0 AND @tid <= 40 THEN DATEADD(DAY,  10 + (@tid % 18), @today)  -- MUC_30
        WHEN @lt = 0                THEN DATEADD(DAY,  35 + (@tid % 20), @today)  -- MUC_60
        WHEN @lt = 1 AND @tid <= 30 THEN DATEADD(DAY,  65 + (@tid % 22), @today)  -- MUC_90
        WHEN @lt = 1                THEN DATEADD(DAY,  40 + (@tid % 45), @today)  -- MUC_60 / MUC_90
        ELSE                             DATEADD(MONTH, 8 + (@li % 10),  @today)  -- > 90 ngày
    END;

    SET @nsx = DATEADD(MONTH, -24, @han);
    SELECT @gi = CAST(ROUND(gia_ban_le * 0.65, 0) AS DECIMAL(18,0)) FROM Thuoc WHERE id = @tid;
    IF @gi <= 0 SET @gi = 500;

    INSERT INTO LoThuoc(id, thuoc_id, so_lo, ngay_san_xuat, han_dung, nha_cung_cap_id, gia_nhap, created_at)
    VALUES(
        @li,
        @tid,
        N'LO-' + RIGHT(N'00' + CAST(@tid AS NVARCHAR), 2) + N'-' + RIGHT(N'000' + CAST(@li AS NVARCHAR), 3),
        @nsx,
        @han,
        (@li % 6) + 1,
        @gi,
        DATEADD(MONTH, -11, SYSUTCDATETIME())
    );

    INSERT INTO #lo_gia VALUES(@li, @tid, @han, @gi);
    SET @li = @li + 1;
END
SET IDENTITY_INSERT LoThuoc OFF;
PRINT N'LoThuoc: 180 lô đã tạo.';
GO

-- =====================================================================
-- 2. NHẬP KHO KHỞI TẠO (9 phiếu nhập cho 9 nhà thuốc cách đây 11 tháng)
-- =====================================================================
SET IDENTITY_INSERT PhieuKho ON;
DECLARE @nt INT = 1;
WHILE @nt <= 9
BEGIN
    INSERT INTO PhieuKho(
        id, ma_phieu, loai_phieu, nha_thuoc_id, nha_cung_cap_id,
        trang_thai, nguoi_lap_id, tong_gia_tri, ghi_chu, thoi_gian_lap, thoi_gian_hoan_thanh
    )
    SELECT
        @nt,
        N'NK-INIT-' + RIGHT(N'00' + CAST(@nt AS NVARCHAR), 2),
        N'NHAP',
        @nt,
        (@nt % 6) + 1,
        N'HOAN_THANH',
        ISNULL((SELECT TOP 1 id FROM NguoiDung WHERE nha_thuoc_id = @nt AND vai_tro = N'THUKHO'),
               (SELECT TOP 1 id FROM NguoiDung WHERE nha_thuoc_id = @nt AND vai_tro = N'QUANLY')),
        0,
        N'Nhập kho khởi tạo đầu kỳ',
        DATEADD(MONTH, -11, SYSUTCDATETIME()),
        DATEADD(MONTH, -11, SYSUTCDATETIME());
    SET @nt = @nt + 1;
END
SET IDENTITY_INSERT PhieuKho OFF;
GO

-- Nạp ChiTietPhieuKho + #ton + BienDongKho (không bật nhiều IDENTITY_INSERT cùng lúc)
DECLARE @nt_loop INT = 1;
DECLARE @lo_loop INT, @lt_mod INT, @sl INT, @sd INT, @nguoi_tk INT;
DECLARE @thoi_gian_nhap DATETIME2(0) = DATEADD(MONTH, -11, SYSUTCDATETIME());

WHILE @nt_loop <= 9
BEGIN
    SET @nguoi_tk = ISNULL(
        (SELECT TOP 1 id FROM NguoiDung WHERE nha_thuoc_id = @nt_loop AND vai_tro = N'THUKHO'),
        (SELECT TOP 1 id FROM NguoiDung WHERE nha_thuoc_id = @nt_loop AND vai_tro = N'QUANLY')
    );

    SET @lo_loop = 1;
    WHILE @lo_loop <= 180
    BEGIN
        SET @lt_mod = (@lo_loop - 1) % 3;

        -- Tất cả nhà thuốc đều có lô hạn dài (@lt_mod = 2) cho mọi thuốc để bán hàng;
        -- Các lô hết hạn / cận hạn (@lt_mod IN (0,1)) phân bổ đều cho cả 9 nhà thuốc để cảnh báo
        IF @lt_mod = 2
           OR (((@lo_loop - 1) / 3 + @nt_loop + @lt_mod) % 2 = 0)
           OR (@lo_loop IN (1, 2, 3, 4, 5, 6) AND @nt_loop IN (1, 2, 9))
        BEGIN
            SET @sl = CASE
                WHEN @lt_mod = 2 THEN 250 + ((@lo_loop * 7 + @nt_loop * 13) % 200) -- 250..449
                WHEN @lt_mod = 1 THEN 40  + ((@lo_loop * 5 + @nt_loop * 11) % 60)  -- 40..99
                ELSE                  20  + ((@lo_loop * 3 + @nt_loop * 7)  % 35)  -- 20..54 (lô cũ/hết hạn)
            END;

            INSERT INTO ChiTietPhieuKho(phieu_kho_id, lo_thuoc_id, so_luong, don_gia)
            SELECT @nt_loop, @lo_loop, @sl, lg.gia_nhap
            FROM #lo_gia lg WHERE lg.lo_id = @lo_loop;

            IF EXISTS (SELECT 1 FROM #ton WHERE nt_id = @nt_loop AND lo_id = @lo_loop)
                UPDATE #ton SET so_luong = so_luong + @sl WHERE nt_id = @nt_loop AND lo_id = @lo_loop;
            ELSE
                INSERT INTO #ton VALUES(@nt_loop, @lo_loop, @sl);

            SELECT @sd = so_luong FROM #ton WHERE nt_id = @nt_loop AND lo_id = @lo_loop;

            INSERT INTO BienDongKho(
                nha_thuoc_id, lo_thuoc_id, loai_bien_dong,
                so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id,
                nguoi_thuc_hien_id, thoi_gian
            )
            VALUES(@nt_loop, @lo_loop, N'NHAP', @sl, @sd, N'PHIEU_KHO', @nt_loop, @nguoi_tk, @thoi_gian_nhap);
        END

        SET @lo_loop = @lo_loop + 1;
    END
    SET @nt_loop = @nt_loop + 1;
END

UPDATE pk
SET tong_gia_tri = (
    SELECT ISNULL(SUM(ct.so_luong * ct.don_gia), 0)
    FROM ChiTietPhieuKho ct WHERE ct.phieu_kho_id = pk.id
)
FROM PhieuKho pk WHERE pk.loai_phieu = N'NHAP';

PRINT N'Nhập kho khởi tạo hoàn tất.';
GO

-- =====================================================================
-- 3. KHACH HANG (300 khách hàng)
-- =====================================================================
SET IDENTITY_INSERT KhachHang ON;
DECLARE @kid INT = 1;
DECLARE @ho_tbl TABLE (idx INT PRIMARY KEY, ho NVARCHAR(20));
INSERT INTO @ho_tbl VALUES
(1, N'Nguyễn'), (2, N'Trần'), (3, N'Lê'), (4, N'Phạm'), (5, N'Hoàng'),
(6, N'Phan'), (7, N'Vũ'), (8, N'Bùi'), (9, N'Đỗ'), (10, N'Hồ');

DECLARE @ten_tbl TABLE (idx INT PRIMARY KEY, ten NVARCHAR(30));
INSERT INTO @ten_tbl VALUES
(1, N'Văn An'), (2, N'Thị Bình'), (3, N'Minh Cường'), (4, N'Thu Dung'), (5, N'Quốc Đạt'),
(6, N'Thanh Phương'), (7, N'Đức Giang'), (8, N'Ngọc Hoa'), (9, N'Tuấn Kiệt'), (10, N'Thị Mai');

DECLARE @duong_tbl TABLE (idx INT PRIMARY KEY, ten_duong NVARCHAR(60));
INSERT INTO @duong_tbl VALUES
(1, N'Nguyễn Huệ'), (2, N'Lê Lợi'), (3, N'Trần Hưng Đạo'), (4, N'Điện Biên Phủ'),
(5, N'Hai Bà Trưng'), (6, N'Lý Thường Kiệt'), (7, N'Phan Đình Phùng'), (8, N'Quang Trung');

DECLARE @ho_count INT = 10, @ten_count INT = 10;
DECLARE @kv_ids TABLE (rid INT IDENTITY(1,1), kv_id INT);
INSERT INTO @kv_ids SELECT id FROM KhuVuc WHERE cap = N'QUAN' ORDER BY id;
DECLARE @kv_cnt INT = (SELECT COUNT(*) FROM @kv_ids);

WHILE @kid <= 300
BEGIN
    DECLARE @hidx INT = ((@kid - 1) % @ho_count) + 1;
    DECLARE @tidx INT = (((@kid * 3) + ((@kid - 1) / @ho_count)) % @ten_count) + 1;
    DECLARE @ho_nm  NVARCHAR(20) = (SELECT ho FROM @ho_tbl WHERE idx = @hidx);
    DECLARE @ten_nm NVARCHAR(30) = (SELECT ten FROM @ten_tbl WHERE idx = @tidx);
    DECLARE @duong_nm NVARCHAR(60) = (SELECT ten_duong FROM @duong_tbl WHERE idx = ((@kid - 1) % 8) + 1);
    DECLARE @kv_kh  INT          = (SELECT kv_id FROM @kv_ids WHERE rid = (@kid % @kv_cnt) + 1);
    DECLARE @sdt_kh NVARCHAR(20) = N'09' + RIGHT(N'000000000' + CAST(100000000 + @kid * 97 AS NVARCHAR), 9);

    INSERT INTO KhachHang(id, ma, ho_ten, so_dien_thoai, gioi_tinh, nam_sinh, khu_vuc_id, dia_chi, ghi_chu_di_ung)
    VALUES(
        @kid,
        N'KH' + RIGHT(N'0000' + CAST(@kid AS NVARCHAR), 4),
        ISNULL(@ho_nm, N'Nguyễn') + N' ' + ISNULL(@ten_nm, N'Văn An'),
        @sdt_kh,
        CASE WHEN @tidx IN (2, 4, 6, 8, 10) THEN N'Nữ' ELSE N'Nam' END,
        1965 + (@kid % 40),
        @kv_kh,
        N'Số ' + CAST((@kid % 150) + 1 AS NVARCHAR) + N' Đường ' + @duong_nm,
        CASE
            WHEN @kid % 15 = 0 THEN N'Dị ứng nhẹ với nhóm Penicillin'
            WHEN @kid % 23 = 0 THEN N'Tiền sử kích ứng NSAID liều cao'
            ELSE NULL
        END
    );
    SET @kid = @kid + 1;
END
SET IDENTITY_INSERT KhachHang OFF;
PRINT N'KhachHang: 300 khách hàng.';
GO

-- =====================================================================
-- 4. DINH MUC TON (9 nhà thuốc × các thuốc không kê đơn)
--    Đặt ngưỡng cao cho một số thuốc để vw_CanhBaoTonThap có dữ liệu demo
-- =====================================================================
SET IDENTITY_INSERT DinhMucTon ON;
DECLARE @dm_id INT = 1;
DECLARE @thuoc_dm TABLE(rid INT IDENTITY(1,1), tid INT);
INSERT INTO @thuoc_dm SELECT id FROM Thuoc WHERE can_ke_don = 0 ORDER BY id;
DECLARE @nt_dm INT = 1, @thuoc_cnt INT = (SELECT COUNT(*) FROM @thuoc_dm);

WHILE @nt_dm <= 9
BEGIN
    DECLARE @tdm INT = 1;
    WHILE @tdm <= @thuoc_cnt
    BEGIN
        DECLARE @t_id INT = (SELECT tid FROM @thuoc_dm WHERE rid = @tdm);
        -- Thuốc đầu tiên mỗi nhà thuốc (rid <= 4) đặt tồn tối thiểu cao (500) để kích hoạt cảnh báo tồn thấp
        DECLARE @min_t INT = CASE WHEN @tdm <= 4 THEN 500 ELSE 30 + (@tdm * 3 % 40) END;
        DECLARE @max_t INT = @min_t + 250;

        INSERT INTO DinhMucTon(id, nha_thuoc_id, thuoc_id, ton_toi_thieu, ton_toi_da, so_ngay_giao_hang)
        VALUES(@dm_id, @nt_dm, @t_id, @min_t, @max_t, 2 + (@nt_dm % 4));

        SET @dm_id = @dm_id + 1;
        SET @tdm   = @tdm + 1;
    END
    SET @nt_dm = @nt_dm + 1;
END
SET IDENTITY_INSERT DinhMucTon OFF;
PRINT N'DinhMucTon: ' + CAST(@dm_id - 1 AS NVARCHAR) + N' dòng.';
GO

-- =====================================================================
-- 5. DON THUOC (150 đơn thuốc đủ 5 trạng thái) + CHI TIET DON THUOC
-- =====================================================================
DECLARE @ked_thuocs TABLE(rid INT IDENTITY(1,1), tid INT, gia DECIMAL(18,0));
INSERT INTO @ked_thuocs(tid, gia)
SELECT id, gia_ban_le FROM Thuoc WHERE can_ke_don = 1 ORDER BY id;
DECLARE @ked_cnt INT = (SELECT COUNT(*) FROM @ked_thuocs);
DECLARE @today3 DATE = CAST(SWITCHOFFSET(SYSUTCDATETIME(), N'+07:00') AS DATE);

SET IDENTITY_INSERT DonThuoc ON;
DECLARE @dt_id INT = 1;
WHILE @dt_id <= 150
BEGIN
    DECLARE @don_nt INT = ((@dt_id - 1) % 9) + 1;
    DECLARE @tt_don NVARCHAR(15) = CASE
        WHEN @dt_id <= 30  THEN N'DA_BAN_HET'
        WHEN @dt_id <= 80  THEN N'BAN_MOT_PHAN'
        WHEN @dt_id <= 120 THEN N'CHUA_BAN'
        WHEN @dt_id <= 140 THEN N'HET_HIEU_LUC'
        ELSE                    N'HUY'
    END;

    -- Đơn CHUA_BAN có ngay_ke_don trong 1-3 ngày gần đây để còn hiệu lực bán demo!
    DECLARE @ngay_ke DATE = CASE
        WHEN @tt_don = N'CHUA_BAN'     THEN DATEADD(DAY, -(@dt_id % 3), @today3)
        WHEN @tt_don = N'HET_HIEU_LUC' THEN DATEADD(DAY, -(15 + (@dt_id % 60)), @today3)
        ELSE                                DATEADD(DAY, -(5 + (@dt_id * 2) % 300), @today3)
    END;

    DECLARE @kh_don INT = (@dt_id % 300) + 1;
    DECLARE @tiep_nhan INT = ISNULL(
        (SELECT TOP 1 id FROM NguoiDung WHERE nha_thuoc_id = @don_nt AND vai_tro IN (N'DUOCSI', N'QUANLY') ORDER BY vai_tro),
        3
    );
    DECLARE @ma_don NVARCHAR(30) = N'DT-' + FORMAT(@ngay_ke, N'yyyyMMdd') + N'-' + RIGHT(N'00000' + CAST(@dt_id AS NVARCHAR), 5);
    DECLARE @bac_si NVARCHAR(100) = CASE (@dt_id % 6)
        WHEN 0 THEN N'BS. Nguyễn Minh Tâm'
        WHEN 1 THEN N'BS. Trần Quốc Bảo'
        WHEN 2 THEN N'BS. Lê Thu Hà'
        WHEN 3 THEN N'BS. Phạm Hoàng Nam'
        WHEN 4 THEN N'BS. Vũ Thanh Thảo'
        ELSE        N'BS. Đặng Quang Huy'
    END;
    DECLARE @bv NVARCHAR(200) = CASE (@dt_id % 5)
        WHEN 0 THEN N'Bệnh viện Đa khoa Khu vực'
        WHEN 1 THEN N'Bệnh viện Chợ Rẫy'
        WHEN 2 THEN N'Bệnh viện Bạch Mai'
        WHEN 3 THEN N'Bệnh viện Đại học Y Dược'
        ELSE        N'Bệnh viện Đa khoa Trung ương'
    END;
    DECLARE @chan_doan NVARCHAR(200) = CASE (@dt_id % 5)
        WHEN 0 THEN N'Nhiễm khuẩn đường hô hấp trên cấp tính'
        WHEN 1 THEN N'Viêm dạ dày - trào ngược thực quản'
        WHEN 2 THEN N'Tăng huyết áp nguyên phát độ 1'
        WHEN 3 THEN N'Viêm phế quản cấp / dị ứng thời tiết'
        ELSE        N'Đái tháo đường tuýp 2 điều trị ngoại trú'
    END;

    INSERT INTO DonThuoc(
        id, ma_don, khach_hang_id, nha_thuoc_id, ten_bac_si, co_so_kham,
        chan_doan, ngay_ke_don, so_ngay_hieu_luc, trang_thai, nguoi_tiep_nhan_id, created_at
    )
    VALUES(
        @dt_id, @ma_don, @kh_don, @don_nt,
        @bac_si, @bv,
        @chan_doan,
        @ngay_ke, 5, @tt_don, @tiep_nhan,
        CAST(@ngay_ke AS DATETIME2(0))
    );

    SET @dt_id = @dt_id + 1;
END
SET IDENTITY_INSERT DonThuoc OFF;

-- Chèn ChiTietDonThuoc (mỗi đơn 2 thuốc kê đơn khác nhau)
DECLARE @dt_loop INT = 1;
WHILE @dt_loop <= 150
BEGIN
    DECLARE @tt_d NVARCHAR(15) = (SELECT trang_thai FROM DonThuoc WHERE id = @dt_loop);
    DECLARE @p1 INT = ((@dt_loop - 1) % @ked_cnt) + 1;
    DECLARE @p2 INT = ((@dt_loop + 3) % @ked_cnt) + 1;
    IF @p2 = @p1 SET @p2 = (@p1 % @ked_cnt) + 1;

    DECLARE @tid1 INT = (SELECT tid FROM @ked_thuocs WHERE rid = @p1);
    DECLARE @tid2 INT = (SELECT tid FROM @ked_thuocs WHERE rid = @p2);
    DECLARE @sl_ke1 INT = 10 + (@dt_loop % 10);
    DECLARE @sl_ke2 INT = 6  + (@dt_loop % 8);

    DECLARE @sl_ban1 INT = CASE @tt_d
        WHEN N'DA_BAN_HET'   THEN @sl_ke1
        WHEN N'BAN_MOT_PHAN' THEN @sl_ke1 / 2
        ELSE 0
    END;
    DECLARE @sl_ban2 INT = CASE @tt_d
        WHEN N'DA_BAN_HET'   THEN @sl_ke2
        WHEN N'BAN_MOT_PHAN' THEN @sl_ke2 / 2
        ELSE 0
    END;

    INSERT INTO ChiTietDonThuoc(don_thuoc_id, thuoc_id, so_luong_ke, so_luong_da_ban, lieu_dung)
    VALUES
        (@dt_loop, @tid1, @sl_ke1, @sl_ban1, N'Uống 1 viên × 2 lần/ngày sau ăn'),
        (@dt_loop, @tid2, @sl_ke2, @sl_ban2, N'Uống 1 viên/ngày buổi sáng');

    SET @dt_loop = @dt_loop + 1;
END
PRINT N'DonThuoc: 150 đơn, ChiTietDonThuoc: 300 dòng.';
GO

-- =====================================================================
-- 6. GIAO DỊCH BÁN HÀNG (3.000 bán lẻ không kê đơn + 160 giao dịch bán theo đơn)
-- =====================================================================
SET IDENTITY_INSERT GiaoDich ON;

DECLARE @today_gd DATE = CAST(SWITCHOFFSET(SYSUTCDATETIME(), N'+07:00') AS DATE);
DECLARE @gd_id INT = 1;

-- 6A. Tạo các GiaoDich bán theo đơn cho các đơn DA_BAN_HET và BAN_MOT_PHAN (đơn 1..80)
-- Mỗi đơn có 2 lần mua hoặc 1 lần mua -> tạo 160 giao dịch có don_thuoc_id
DECLARE @dt_gd INT = 1;
WHILE @dt_gd <= 80
BEGIN
    DECLARE @nt_dt INT, @kh_dt INT, @ngay_ke_dt DATE, @nguoi_ban_dt INT;
    SELECT @nt_dt = nha_thuoc_id, @kh_dt = khach_hang_id, @ngay_ke_dt = ngay_ke_don, @nguoi_ban_dt = nguoi_tiep_nhan_id
    FROM DonThuoc WHERE id = @dt_gd;

    -- Mỗi dòng ChiTietDonThuoc có so_luong_da_ban > 0 tạo 1 giao dịch bán theo đơn
    DECLARE @ctdt_thuoc INT, @ctdt_sl INT;
    DECLARE cur_ctdt CURSOR FAST_FORWARD LOCAL FOR
        SELECT thuoc_id, so_luong_da_ban
        FROM ChiTietDonThuoc
        WHERE don_thuoc_id = @dt_gd AND so_luong_da_ban > 0;
    OPEN cur_ctdt;
    FETCH NEXT FROM cur_ctdt INTO @ctdt_thuoc, @ctdt_sl;
    WHILE @@FETCH_STATUS = 0
    BEGIN
        DECLARE @lo_ked INT = NULL, @gv_ked DECIMAL(18,0), @gia_ked DECIMAL(18,0);
        SELECT @gia_ked = gia_ban_le FROM Thuoc WHERE id = @ctdt_thuoc;

        SELECT TOP 1 @lo_ked = t.lo_id, @gv_ked = lg.gia_nhap
        FROM #ton t
        JOIN #lo_gia lg ON lg.lo_id = t.lo_id
        WHERE t.nt_id = @nt_dt AND lg.thuoc_id = @ctdt_thuoc
          AND t.so_luong >= @ctdt_sl AND lg.han_dung >= @today_gd
        ORDER BY lg.han_dung ASC, t.lo_id ASC;

        IF @lo_ked IS NOT NULL
        BEGIN
            DECLARE @tg_dt DATETIME2(0) = DATEADD(HOUR, 9 + (@gd_id % 8), CAST(@ngay_ke_dt AS DATETIME2(0)));
            DECLARE @tong_dt DECIMAL(18,0) = @ctdt_sl * @gia_ked;
            DECLARE @ma_gd_dt NVARCHAR(30) = N'GD-' + FORMAT(@ngay_ke_dt, N'yyyyMMdd') + N'-' + RIGHT(N'00000' + CAST(@gd_id AS NVARCHAR), 5);
            DECLARE @pttt_dt NVARCHAR(15) = CASE @gd_id % 3
                WHEN 0 THEN N'CHUYEN_KHOAN'
                WHEN 1 THEN N'TIEN_MAT'
                ELSE        N'THE'
            END;

            INSERT INTO GiaoDich(
                id, ma_giao_dich, nha_thuoc_id, nguoi_ban_id, khach_hang_id, don_thuoc_id,
                thoi_gian, tong_tien, giam_gia, thanh_tien, phuong_thuc_thanh_toan, trang_thai
            )
            VALUES(
                @gd_id, @ma_gd_dt, @nt_dt, @nguoi_ban_dt, @kh_dt, @dt_gd,
                @tg_dt, @tong_dt, 0, @tong_dt, @pttt_dt, N'HOAN_THANH'
            );

            INSERT INTO ChiTietGiaoDich(giao_dich_id, thuoc_id, lo_thuoc_id, so_luong, don_gia, gia_von, thanh_tien)
            VALUES(@gd_id, @ctdt_thuoc, @lo_ked, @ctdt_sl, @gia_ked, @gv_ked, @tong_dt);

            UPDATE #ton SET so_luong = so_luong - @ctdt_sl WHERE nt_id = @nt_dt AND lo_id = @lo_ked;
            DECLARE @sd_dt INT = (SELECT so_luong FROM #ton WHERE nt_id = @nt_dt AND lo_id = @lo_ked);

            INSERT INTO BienDongKho(
                nha_thuoc_id, lo_thuoc_id, loai_bien_dong,
                so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id, thoi_gian
            )
            VALUES(@nt_dt, @lo_ked, N'BAN', -@ctdt_sl, @sd_dt, N'GIAO_DICH', @gd_id, @nguoi_ban_dt, @tg_dt);

            SET @gd_id = @gd_id + 1;
        END
        FETCH NEXT FROM cur_ctdt INTO @ctdt_thuoc, @ctdt_sl;
    END
    CLOSE cur_ctdt;
    DEALLOCATE cur_ctdt;

    SET @dt_gd = @dt_gd + 1;
END

-- 6B. 3.000 Giao dịch bán lẻ không kê đơn (trải đều 12 tháng)
-- Chừa lại thuốc id IN (58, 59) không bán trong 90 ngày gần đây để có dữ liệu vw_TonChamLuanChuyen
DECLARE @free_thuocs TABLE(rid INT IDENTITY(1,1), tid INT, gia DECIMAL(18,0), nhom INT);
INSERT INTO @free_thuocs
SELECT id, gia_ban_le, nhom_thuoc_id
FROM Thuoc
WHERE can_ke_don = 0 AND id NOT IN (58, 59)
ORDER BY id;
DECLARE @ft_cnt INT = (SELECT COUNT(*) FROM @free_thuocs);

DECLARE @hh_thuocs TABLE(rid INT IDENTITY(1,1), tid INT, gia DECIMAL(18,0));
INSERT INTO @hh_thuocs
SELECT id, gia_ban_le
FROM Thuoc
WHERE can_ke_don = 0 AND nhom_thuoc_id = 5
ORDER BY id;
DECLARE @hh_cnt INT = (SELECT COUNT(*) FROM @hh_thuocs);

DECLARE @iter INT = 1;
WHILE @iter <= 3375
BEGIN
    DECLARE @nt_gd INT = ((@iter - 1) % 9) + 1;
    DECLARE @banid INT = ISNULL(
        (SELECT TOP 1 id FROM NguoiDung WHERE nha_thuoc_id = @nt_gd AND vai_tro IN (N'DUOCSI', N'QUANLY') ORDER BY vai_tro),
        3
    );
    DECLARE @qlid INT = ISNULL(
        (SELECT TOP 1 id FROM NguoiDung WHERE nha_thuoc_id = @nt_gd AND vai_tro = N'QUANLY'),
        3
    );

    -- Ngày giao dịch trải đều 360 ngày qua
    DECLARE @days_ago INT = (@iter * 360) / 3375;
    DECLARE @ngay_gd DATETIME2(0) = DATEADD(MINUTE, (@iter * 17) % 600,
        DATEADD(HOUR, 8, CAST(DATEADD(DAY, -@days_ago, @today_gd) AS DATETIME2(0))));

    DECLARE @m_gd INT = MONTH(CAST(@ngay_gd AS DATE));
    DECLARE @ft_tid INT, @ft_gia DECIMAL(18,0);

    -- Quy luật mùa vụ: Hà Nội (NT 4,5,6) vào mùa lạnh (tháng 11,12,1,2) tăng mạnh nhóm Hô hấp
    IF @nt_gd IN (4, 5, 6) AND @m_gd IN (11, 12, 1, 2) AND (@iter % 3 <> 0)
    BEGIN
        SELECT @ft_tid = tid, @ft_gia = gia FROM @hh_thuocs WHERE rid = (@iter % @hh_cnt) + 1;
    END
    ELSE
    BEGIN
        SELECT @ft_tid = tid, @ft_gia = gia FROM @free_thuocs WHERE rid = (@iter % @ft_cnt) + 1;
    END

    -- FEFO: chọn lô còn hạn >= today_gd có đủ tồn
    DECLARE @lo_pick INT = NULL, @lo_gv DECIMAL(18,0), @ton_avail INT = 0;
    SELECT TOP 1 @lo_pick = t.lo_id, @lo_gv = lg.gia_nhap, @ton_avail = t.so_luong
    FROM #ton t
    JOIN #lo_gia lg ON lg.lo_id = t.lo_id
    WHERE t.nt_id = @nt_gd AND lg.thuoc_id = @ft_tid
      AND t.so_luong >= 2 AND lg.han_dung >= @today_gd
    ORDER BY lg.han_dung ASC, t.lo_id ASC;

    -- Nếu lô hiện tại cạn thì tự động nhập bổ sung lô hạn dài cho nhà thuốc đó trước khi bán
    IF @lo_pick IS NULL
    BEGIN
        SELECT TOP 1 @lo_pick = lg.lo_id, @lo_gv = lg.gia_nhap
        FROM #lo_gia lg
        WHERE lg.thuoc_id = @ft_tid AND lg.han_dung >= DATEADD(MONTH, 3, @today_gd)
        ORDER BY lg.han_dung ASC;

        IF EXISTS (SELECT 1 FROM #ton WHERE nt_id = @nt_gd AND lo_id = @lo_pick)
            UPDATE #ton SET so_luong = so_luong + 200 WHERE nt_id = @nt_gd AND lo_id = @lo_pick;
        ELSE
            INSERT INTO #ton VALUES(@nt_gd, @lo_pick, 200);

        DECLARE @sd_restock INT = (SELECT so_luong FROM #ton WHERE nt_id = @nt_gd AND lo_id = @lo_pick);
        INSERT INTO ChiTietPhieuKho(phieu_kho_id, lo_thuoc_id, so_luong, don_gia)
        VALUES(@nt_gd, @lo_pick, 200, @lo_gv);

        INSERT INTO BienDongKho(
            nha_thuoc_id, lo_thuoc_id, loai_bien_dong,
            so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id, thoi_gian
        )
        VALUES(@nt_gd, @lo_pick, N'NHAP', 200, @sd_restock, N'PHIEU_KHO', @nt_gd, @banid, DATEADD(DAY, -1, @ngay_gd));
    END

    DECLARE @sl_gd INT = 2 + (@iter % 8); -- 2..9 đơn vị
    IF @sl_gd > (SELECT so_luong FROM #ton WHERE nt_id = @nt_gd AND lo_id = @lo_pick)
        SET @sl_gd = (SELECT so_luong FROM #ton WHERE nt_id = @nt_gd AND lo_id = @lo_pick);

    DECLARE @tt_gd NVARCHAR(10) = CASE WHEN @iter % 50 = 0 THEN N'DA_HUY' ELSE N'HOAN_THANH' END;
    DECLARE @tong DECIMAL(18,0) = @sl_gd * @ft_gia;
    DECLARE @giam DECIMAL(18,0) = CASE WHEN @iter % 20 = 0 AND @tong >= 10000 THEN 1000 ELSE 0 END;
    DECLARE @thanh DECIMAL(18,0) = @tong - @giam;
    DECLARE @pttt NVARCHAR(15) = CASE @iter % 4
        WHEN 0 THEN N'CHUYEN_KHOAN'
        WHEN 1 THEN N'THE'
        ELSE        N'TIEN_MAT'
    END;
    DECLARE @ma_gd NVARCHAR(30) = N'GD-' + FORMAT(CAST(@ngay_gd AS DATE), N'yyyyMMdd') + N'-' + RIGHT(N'00000' + CAST(@gd_id AS NVARCHAR), 5);
    DECLARE @ly_huy NVARCHAR(500) = CASE WHEN @tt_gd = N'DA_HUY' THEN N'Khách hàng đổi ý trong ngày' ELSE NULL END;
    DECLARE @ng_huy INT           = CASE WHEN @tt_gd = N'DA_HUY' THEN @qlid ELSE NULL END;
    DECLARE @tg_huy DATETIME2(0)  = CASE WHEN @tt_gd = N'DA_HUY' THEN DATEADD(HOUR, 1, @ngay_gd) ELSE NULL END;

    INSERT INTO GiaoDich(
        id, ma_giao_dich, nha_thuoc_id, nguoi_ban_id, khach_hang_id,
        thoi_gian, tong_tien, giam_gia, thanh_tien, phuong_thuc_thanh_toan,
        trang_thai, ly_do_huy, nguoi_huy_id, thoi_gian_huy
    )
    VALUES(
        @gd_id, @ma_gd, @nt_gd, @banid, (@iter % 300) + 1,
        @ngay_gd, @tong, @giam, @thanh, @pttt,
        @tt_gd, @ly_huy, @ng_huy, @tg_huy
    );

    INSERT INTO ChiTietGiaoDich(giao_dich_id, thuoc_id, lo_thuoc_id, so_luong, don_gia, gia_von, thanh_tien)
    VALUES(@gd_id, @ft_tid, @lo_pick, @sl_gd, @ft_gia, @lo_gv, @tong);

    -- Ghi BienDongKho: nếu HOAN_THANH thì trừ tồn; nếu DA_HUY thì ghi cả BAN và HOAN_KHO
    UPDATE #ton SET so_luong = so_luong - @sl_gd WHERE nt_id = @nt_gd AND lo_id = @lo_pick;
    DECLARE @sd_ban INT = (SELECT so_luong FROM #ton WHERE nt_id = @nt_gd AND lo_id = @lo_pick);

    INSERT INTO BienDongKho(
        nha_thuoc_id, lo_thuoc_id, loai_bien_dong,
        so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id, thoi_gian
    )
    VALUES(@nt_gd, @lo_pick, N'BAN', -@sl_gd, @sd_ban, N'GIAO_DICH', @gd_id, @banid, @ngay_gd);

    IF @tt_gd = N'DA_HUY'
    BEGIN
        UPDATE #ton SET so_luong = so_luong + @sl_gd WHERE nt_id = @nt_gd AND lo_id = @lo_pick;
        DECLARE @sd_hoan INT = (SELECT so_luong FROM #ton WHERE nt_id = @nt_gd AND lo_id = @lo_pick);

        INSERT INTO BienDongKho(
            nha_thuoc_id, lo_thuoc_id, loai_bien_dong,
            so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id, thoi_gian
        )
        VALUES(@nt_gd, @lo_pick, N'HOAN_KHO', @sl_gd, @sd_hoan, N'GIAO_DICH', @gd_id, @qlid, @tg_huy);
    END

    SET @gd_id = @gd_id + 1;
    SET @iter  = @iter + 1;
END

SET IDENTITY_INSERT GiaoDich OFF;

-- Cập nhật lại tong_gia_tri các phiếu nhập kho nếu có dòng nhập bổ sung
UPDATE pk
SET tong_gia_tri = (
    SELECT ISNULL(SUM(ct.so_luong * ct.don_gia), 0)
    FROM ChiTietPhieuKho ct WHERE ct.phieu_kho_id = pk.id
)
FROM PhieuKho pk WHERE pk.loai_phieu = N'NHAP';

PRINT N'GiaoDich: ' + CAST(@gd_id - 1 AS NVARCHAR) + N' giao dịch đã tạo.';
GO

-- =====================================================================
-- 7. PHIẾU ĐIỀU CHUYỂN & XUẤT HỦY (đủ các trạng thái HOAN_THANH, CHO_DUYET, TU_CHOI, DA_HUY)
-- =====================================================================
SET IDENTITY_INSERT PhieuKho ON;

-- Phieu 10: Điều chuyển HOAN_THANH từ NT1 -> NT2 (lô 3 = TH001 hạn dài)
INSERT INTO PhieuKho(id, ma_phieu, loai_phieu, nha_thuoc_id, nha_thuoc_den_id, trang_thai, nguoi_lap_id, nguoi_duyet_id, tong_gia_tri, ghi_chu, thoi_gian_lap, thoi_gian_hoan_thanh)
VALUES(10, N'DC-INIT-00001', N'DIEU_CHUYEN', 1, 2, N'HOAN_THANH', 16, 3, 30 * 1625, N'Điều chuyển bổ sung tồn kho Q.Bình Thạnh', DATEADD(MONTH, -3, SYSUTCDATETIME()), DATEADD(MONTH, -3, SYSUTCDATETIME()));

-- Phieu 11: Điều chuyển CHO_DUYET từ NT3 -> NT4 (để demo M11 Duyệt phiếu)
INSERT INTO PhieuKho(id, ma_phieu, loai_phieu, nha_thuoc_id, nha_thuoc_den_id, trang_thai, nguoi_lap_id, tong_gia_tri, ghi_chu, thoi_gian_lap)
VALUES(11, N'DC-PEND-00002', N'DIEU_CHUYEN', 3, 4, N'CHO_DUYET', 18, 20 * 1950, N'Đề xuất điều chuyển sang Hoàn Kiếm', DATEADD(HOUR, -6, SYSUTCDATETIME()));

-- Phieu 12: Điều chuyển TU_CHOI từ NT5 -> NT6
INSERT INTO PhieuKho(id, ma_phieu, loai_phieu, nha_thuoc_id, nha_thuoc_den_id, trang_thai, nguoi_lap_id, nguoi_duyet_id, tong_gia_tri, ghi_chu, thoi_gian_lap, thoi_gian_hoan_thanh)
VALUES(12, N'DC-REJ-00003', N'DIEU_CHUYEN', 5, 6, N'TU_CHOI', 7, 7, 15 * 2275, N'TỪ CHỐI: Nhà thuốc nguồn cần giữ tồn dự phòng', DATEADD(MONTH, -2, SYSUTCDATETIME()), DATEADD(MONTH, -2, SYSUTCDATETIME()));

-- Phieu 13: Xuất hủy HOAN_THANH tại NT1 (xuất hủy 10 đơn vị lô 1 đã hết hạn, phần còn lại để cảnh báo)
INSERT INTO PhieuKho(id, ma_phieu, loai_phieu, nha_thuoc_id, ly_do, trang_thai, nguoi_lap_id, nguoi_duyet_id, tong_gia_tri, ghi_chu, thoi_gian_lap, thoi_gian_hoan_thanh)
VALUES(13, N'XH-INIT-00001', N'XUAT_HUY', 1, N'HET_HAN', N'HOAN_THANH', 16, 3, 10 * 1625, N'Xuất hủy một phần lô hết hạn đợt 1', DATEADD(DAY, -10, SYSUTCDATETIME()), DATEADD(DAY, -10, SYSUTCDATETIME()));

-- Phieu 14: Xuất hủy CHO_DUYET tại NT2 (để demo M11 Duyệt phiếu)
INSERT INTO PhieuKho(id, ma_phieu, loai_phieu, nha_thuoc_id, ly_do, trang_thai, nguoi_lap_id, tong_gia_tri, ghi_chu, thoi_gian_lap)
VALUES(14, N'XH-PEND-00002', N'XUAT_HUY', 2, N'HET_HAN', N'CHO_DUYET', 17, 10 * 1625, N'Chờ quản lý duyệt hủy lô hết hạn', DATEADD(HOUR, -4, SYSUTCDATETIME()));

-- Phieu 15: Xuất hủy DA_HUY tại NT2
INSERT INTO PhieuKho(id, ma_phieu, loai_phieu, nha_thuoc_id, ly_do, trang_thai, nguoi_lap_id, tong_gia_tri, ghi_chu, thoi_gian_lap)
VALUES(15, N'XH-CANC-00003', N'XUAT_HUY', 2, N'HU_HONG', N'DA_HUY', 17, 5 * 1625, N'Thủ kho hủy phiếu do nhập nhầm số lô', DATEADD(DAY, -5, SYSUTCDATETIME()));

SET IDENTITY_INSERT PhieuKho OFF;

-- Chi tiết và biến động kho cho các phiếu 10..15
-- Phiếu 10 (HOAN_THANH): Điều chuyển 30 viên lô 3 từ NT1 -> NT2
IF EXISTS (SELECT 1 FROM #ton WHERE nt_id = 1 AND lo_id = 3 AND so_luong >= 30)
BEGIN
    INSERT INTO ChiTietPhieuKho(phieu_kho_id, lo_thuoc_id, so_luong, don_gia) VALUES(10, 3, 30, 1625);
    UPDATE #ton SET so_luong = so_luong - 30 WHERE nt_id = 1 AND lo_id = 3;
    DECLARE @sd9a INT = (SELECT so_luong FROM #ton WHERE nt_id = 1 AND lo_id = 3);
    INSERT INTO BienDongKho(nha_thuoc_id, lo_thuoc_id, loai_bien_dong, so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id, thoi_gian)
    VALUES(1, 3, N'DIEU_CHUYEN_XUAT', -30, @sd9a, N'PHIEU_KHO', 10, 3, DATEADD(MONTH, -3, SYSUTCDATETIME()));

    IF EXISTS (SELECT 1 FROM #ton WHERE nt_id = 2 AND lo_id = 3)
        UPDATE #ton SET so_luong = so_luong + 30 WHERE nt_id = 2 AND lo_id = 3;
    ELSE
        INSERT INTO #ton VALUES(2, 3, 30);

    DECLARE @sd9b INT = (SELECT so_luong FROM #ton WHERE nt_id = 2 AND lo_id = 3);
    INSERT INTO BienDongKho(nha_thuoc_id, lo_thuoc_id, loai_bien_dong, so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id, thoi_gian)
    VALUES(2, 3, N'DIEU_CHUYEN_NHAP', 30, @sd9b, N'PHIEU_KHO', 10, 3, DATEADD(MONTH, -3, SYSUTCDATETIME()));
END

-- Phiếu 11 (CHO_DUYET): Chi tiết phiếu (chưa trừ tồn)
INSERT INTO ChiTietPhieuKho(phieu_kho_id, lo_thuoc_id, so_luong, don_gia) VALUES(11, 6, 20, 1950);

-- Phiếu 12 (TU_CHOI): Chi tiết phiếu (không trừ tồn)
INSERT INTO ChiTietPhieuKho(phieu_kho_id, lo_thuoc_id, so_luong, don_gia) VALUES(12, 9, 15, 2275);

-- Phiếu 13 (HOAN_THANH): Xuất hủy 10 viên lô 1 tại NT1 (vẫn còn tồn lô 1 để hiển thị DA_HET_HAN trên vw_CanhBaoHanDung)
IF EXISTS (SELECT 1 FROM #ton WHERE nt_id = 1 AND lo_id = 1 AND so_luong > 10)
BEGIN
    INSERT INTO ChiTietPhieuKho(phieu_kho_id, lo_thuoc_id, so_luong, don_gia) VALUES(13, 1, 10, 1625);
    UPDATE #ton SET so_luong = so_luong - 10 WHERE nt_id = 1 AND lo_id = 1;
    DECLARE @sd12 INT = (SELECT so_luong FROM #ton WHERE nt_id = 1 AND lo_id = 1);
    INSERT INTO BienDongKho(nha_thuoc_id, lo_thuoc_id, loai_bien_dong, so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id, thoi_gian)
    VALUES(1, 1, N'XUAT_HUY', -10, @sd12, N'PHIEU_KHO', 13, 3, DATEADD(DAY, -10, SYSUTCDATETIME()));
END

-- Phiếu 14 (CHO_DUYET) & Phiếu 15 (DA_HUY)
INSERT INTO ChiTietPhieuKho(phieu_kho_id, lo_thuoc_id, so_luong, don_gia) VALUES(14, 4, 10, 1625);
INSERT INTO ChiTietPhieuKho(phieu_kho_id, lo_thuoc_id, so_luong, don_gia) VALUES(15, 4, 5, 1625);

PRINT N'Phiếu điều chuyển & xuất hủy đã tạo.';
GO

-- =====================================================================
-- 8. ĐỒNG BỘ TONKHO VÀ RUNNING SUM BIENDONGKHO
-- =====================================================================
INSERT INTO TonKho(nha_thuoc_id, lo_thuoc_id, so_luong)
SELECT nt_id, lo_id, so_luong
FROM #ton
WHERE so_luong >= 0;

PRINT N'TonKho: ' + CAST(@@ROWCOUNT AS NVARCHAR) + N' dòng.';

-- Đảm bảo so_du_sau trong BienDongKho khớp tuyệt đối theo thứ tự id
;WITH Running AS (
    SELECT
        id,
        SUM(so_luong_thay_doi) OVER (
            PARTITION BY nha_thuoc_id, lo_thuoc_id
            ORDER BY id ASC
            ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
        ) AS rs
    FROM BienDongKho
)
UPDATE bdk
SET so_du_sau = r.rs
FROM BienDongKho bdk
JOIN Running r ON r.id = bdk.id;

DROP TABLE IF EXISTS #ton;
DROP TABLE IF EXISTS #lo_gia;
GO

-- =====================================================================
-- 9. AUDIT LOG MẪU (phục vụ màn hình M23 Nhật ký kiểm toán)
-- =====================================================================
INSERT INTO AuditLog(nguoi_dung_id, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_cu, du_lieu_moi, created_at)
VALUES
(1,  N'THEM_MA_VACH',          N'MaVachThuoc', 1,   NULL, N'{"ma_vach":"8930000000019","loai":"EAN13"}', DATEADD(MONTH, -11, SYSUTCDATETIME())),
(16, N'NHAP_KHO',              N'PhieuKho',    1,   NULL, N'{"ma_phieu":"NK-INIT-01","trang_thai":"HOAN_THANH"}', DATEADD(MONTH, -11, SYSUTCDATETIME())),
(17, N'NHAP_KHO',              N'PhieuKho',    2,   NULL, N'{"ma_phieu":"NK-INIT-02","trang_thai":"HOAN_THANH"}', DATEADD(MONTH, -11, SYSUTCDATETIME())),
(16, N'TAO_PHIEU_DIEU_CHUYEN', N'PhieuKho',    9,   NULL, N'{"ma_phieu":"DC-INIT-00001","nha_thuoc_den_id":2}', DATEADD(MONTH, -3, SYSUTCDATETIME())),
(3,  N'DUYET_PHIEU',           N'PhieuKho',    9,   N'{"trang_thai":"CHO_DUYET"}', N'{"loai":"DIEU_CHUYEN","trang_thai":"HOAN_THANH"}', DATEADD(MONTH, -3, SYSUTCDATETIME())),
(7,  N'TU_CHOI_PHIEU',         N'PhieuKho',    11,  N'{"trang_thai":"CHO_DUYET"}', N'{"ly_do":"Nhà thuốc nguồn cần giữ tồn dự phòng"}', DATEADD(MONTH, -2, SYSUTCDATETIME())),
(16, N'TAO_PHIEU_XUAT_HUY',    N'PhieuKho',    12,  NULL, N'{"ma_phieu":"XH-INIT-00001","ly_do":"HET_HAN"}', DATEADD(DAY, -10, SYSUTCDATETIME())),
(3,  N'DUYET_PHIEU',           N'PhieuKho',    12,  N'{"trang_thai":"CHO_DUYET"}', N'{"loai":"XUAT_HUY","trang_thai":"HOAN_THANH"}', DATEADD(DAY, -10, SYSUTCDATETIME())),
(11, N'TAO_DON_THUOC',         N'DonThuoc',    1,   NULL, N'{"ma_don":"DT-00001","trang_thai":"CHUA_BAN"}', DATEADD(DAY, -7, SYSUTCDATETIME())),
(11, N'BAN_HANG',              N'GiaoDich',    1,   NULL, N'{"don_thuoc_id":1,"trang_thai":"HOAN_THANH"}', DATEADD(DAY, -7, SYSUTCDATETIME())),
(3,  N'HUY_GIAO_DICH',         N'GiaoDich',    210, N'{"trang_thai":"HOAN_THANH"}', N'{"trang_thai":"DA_HUY","ly_do":"Khách hàng đổi ý trong ngày"}', DATEADD(DAY, -5, SYSUTCDATETIME())),
(1,  N'CAP_NHAT_CAU_HINH',     N'CauHinh',     NULL, N'{"canh_bao_han_muc3_ngay":"30"}', N'{"canh_bao_han_muc3_ngay":"30"}', DATEADD(DAY, -1, SYSUTCDATETIME()));

PRINT N'AuditLog: ' + CAST(@@ROWCOUNT AS NVARCHAR) + N' dòng.';
GO

-- =====================================================================
-- 10. BẬT LẠI TRIGGER & ĐỐI SOÁT TỒN KHO
-- =====================================================================
ENABLE TRIGGER trg_BienDongKho_ChiThem_Upd ON BienDongKho;
ENABLE TRIGGER trg_BienDongKho_ChiThem_Del ON BienDongKho;
ENABLE TRIGGER trg_AuditLog_ChiThem_Upd    ON AuditLog;
ENABLE TRIGGER trg_AuditLog_ChiThem_Del    ON AuditLog;
PRINT N'Triggers append-only đã bật lại.';
GO

PRINT N'-- Kiểm tra sp_DoiSoatTonKho (0 dòng chênh lệch = ĐẠT):';
EXEC dbo.sp_DoiSoatTonKho;
GO
PRINT N'06_seed_p2.sql hoàn thành thành công.';
GO
