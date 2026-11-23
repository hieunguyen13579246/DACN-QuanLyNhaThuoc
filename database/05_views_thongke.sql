-- =====================================================================
-- 05_views_thongke.sql  –  IS009
-- Views báo cáo + Stored Procedures thống kê K1–K5
-- =====================================================================
USE IS009;
GO
SET QUOTED_IDENTIFIER ON;
GO

-- =====================================================================
-- VIEW: vw_TonKhoTheoThuoc
-- (nha_thuoc_id, thuoc_id, tong_ton, gia_tri_ton)
-- =====================================================================
CREATE OR ALTER VIEW dbo.vw_TonKhoTheoThuoc AS
SELECT
    tk.nha_thuoc_id,
    lt.thuoc_id,
    SUM(tk.so_luong)                       AS tong_ton,
    SUM(tk.so_luong * lt.gia_nhap)         AS gia_tri_ton
FROM TonKho tk
JOIN LoThuoc lt ON lt.id = tk.lo_thuoc_id
GROUP BY tk.nha_thuoc_id, lt.thuoc_id;
GO

-- =====================================================================
-- VIEW: vw_CanhBaoHanDung
-- Ngưỡng đọc từ CauHinh động
-- =====================================================================
CREATE OR ALTER VIEW dbo.vw_CanhBaoHanDung AS
SELECT
    tk.nha_thuoc_id,
    nt.ten                                 AS ten_nha_thuoc,
    nt.khu_vuc_id,
    lt.thuoc_id,
    t.ma                                   AS ma_thuoc,
    t.ten                                  AS ten_thuoc,
    t.don_vi_tinh,
    tk.lo_thuoc_id,
    lt.so_lo,
    lt.han_dung,
    DATEDIFF(DAY,
        CAST(SWITCHOFFSET(SYSUTCDATETIME(), N'+07:00') AS DATE),
        lt.han_dung)                       AS so_ngay_con_lai,
    tk.so_luong                            AS so_luong_ton,
    tk.so_luong * lt.gia_nhap             AS gia_tri_ton,
    CASE
        WHEN lt.han_dung < CAST(SWITCHOFFSET(SYSUTCDATETIME(), N'+07:00') AS DATE)
            THEN N'DA_HET_HAN'
        WHEN DATEDIFF(DAY, CAST(SWITCHOFFSET(SYSUTCDATETIME(), N'+07:00') AS DATE), lt.han_dung)
             <= (SELECT CAST(gia_tri AS INT) FROM CauHinh WHERE khoa = N'canh_bao_han_muc3_ngay')
            THEN N'MUC_30'
        WHEN DATEDIFF(DAY, CAST(SWITCHOFFSET(SYSUTCDATETIME(), N'+07:00') AS DATE), lt.han_dung)
             <= (SELECT CAST(gia_tri AS INT) FROM CauHinh WHERE khoa = N'canh_bao_han_muc2_ngay')
            THEN N'MUC_60'
        WHEN DATEDIFF(DAY, CAST(SWITCHOFFSET(SYSUTCDATETIME(), N'+07:00') AS DATE), lt.han_dung)
             <= (SELECT CAST(gia_tri AS INT) FROM CauHinh WHERE khoa = N'canh_bao_han_muc1_ngay')
            THEN N'MUC_90'
        ELSE NULL
    END                                    AS muc_canh_bao
FROM TonKho tk
JOIN LoThuoc lt  ON lt.id = tk.lo_thuoc_id
JOIN Thuoc t     ON t.id  = lt.thuoc_id
JOIN NhaThuoc nt ON nt.id = tk.nha_thuoc_id
WHERE tk.so_luong > 0
  AND DATEDIFF(DAY,
        CAST(SWITCHOFFSET(SYSUTCDATETIME(), N'+07:00') AS DATE),
        lt.han_dung)
      <= (SELECT CAST(gia_tri AS INT) FROM CauHinh WHERE khoa = N'canh_bao_han_muc1_ngay');
GO

-- =====================================================================
-- VIEW: vw_CanhBaoTonThap
-- (nha_thuoc_id, thuoc_id, tong_ton, ton_toi_thieu, thieu)
-- =====================================================================
CREATE OR ALTER VIEW dbo.vw_CanhBaoTonThap AS
SELECT
    dm.nha_thuoc_id,
    nt.ten                  AS ten_nha_thuoc,
    dm.thuoc_id,
    t.ma                    AS ma_thuoc,
    t.ten                   AS ten_thuoc,
    t.don_vi_tinh,
    ISNULL(vt.tong_ton, 0)  AS tong_ton,
    dm.ton_toi_thieu,
    dm.ton_toi_da,
    dm.ton_toi_thieu - ISNULL(vt.tong_ton, 0) AS thieu
FROM DinhMucTon dm
JOIN NhaThuoc nt ON nt.id = dm.nha_thuoc_id
JOIN Thuoc t     ON t.id  = dm.thuoc_id
LEFT JOIN vw_TonKhoTheoThuoc vt
  ON vt.nha_thuoc_id = dm.nha_thuoc_id AND vt.thuoc_id = dm.thuoc_id
WHERE ISNULL(vt.tong_ton, 0) < dm.ton_toi_thieu;
GO

-- =====================================================================
-- VIEW: vw_TonChamLuanChuyen
-- (nha_thuoc_id, thuoc_id, tong_ton, ngay_ban_cuoi, so_ngay_khong_ban)
-- Thuốc có tồn nhưng không bán trong so_ngay_ton_cham ngày
-- =====================================================================
CREATE OR ALTER VIEW dbo.vw_TonChamLuanChuyen AS
WITH BanGanDay AS (
    SELECT
        gd.nha_thuoc_id,
        ctgd.thuoc_id,
        MAX(CAST(SWITCHOFFSET(gd.thoi_gian, N'+07:00') AS DATE)) AS ngay_ban_cuoi
    FROM GiaoDich gd
    JOIN ChiTietGiaoDich ctgd ON gd.id = ctgd.giao_dich_id
    WHERE gd.trang_thai = N'HOAN_THANH'
    GROUP BY gd.nha_thuoc_id, ctgd.thuoc_id
)
SELECT
    vt.nha_thuoc_id,
    nt.ten       AS ten_nha_thuoc,
    vt.thuoc_id,
    t.ma         AS ma_thuoc,
    t.ten        AS ten_thuoc,
    t.don_vi_tinh,
    vt.tong_ton,
    vt.gia_tri_ton,
    bgd.ngay_ban_cuoi,
    DATEDIFF(DAY, bgd.ngay_ban_cuoi,
        CAST(SWITCHOFFSET(SYSUTCDATETIME(), N'+07:00') AS DATE)) AS so_ngay_khong_ban
FROM vw_TonKhoTheoThuoc vt
JOIN NhaThuoc nt ON nt.id = vt.nha_thuoc_id
JOIN Thuoc t     ON t.id  = vt.thuoc_id
LEFT JOIN BanGanDay bgd
  ON bgd.nha_thuoc_id = vt.nha_thuoc_id AND bgd.thuoc_id = vt.thuoc_id
WHERE vt.tong_ton > 0
  AND (
    bgd.ngay_ban_cuoi IS NULL
    OR DATEDIFF(DAY, bgd.ngay_ban_cuoi,
           CAST(SWITCHOFFSET(SYSUTCDATETIME(), N'+07:00') AS DATE))
       >= (SELECT CAST(gia_tri AS INT) FROM CauHinh WHERE khoa = N'so_ngay_ton_cham')
  );
GO

-- =====================================================================
-- VIEW: vw_BanHangChiTiet  (chỉ HOAN_THANH)
-- =====================================================================
CREATE OR ALTER VIEW dbo.vw_BanHangChiTiet AS
SELECT
    gd.id                                                       AS giao_dich_id,
    CAST(SWITCHOFFSET(gd.thoi_gian, N'+07:00') AS DATE)        AS ngay,
    MONTH(SWITCHOFFSET(gd.thoi_gian, N'+07:00'))               AS thang,
    YEAR(SWITCHOFFSET(gd.thoi_gian, N'+07:00'))                AS nam,
    gd.nha_thuoc_id,
    nt.khu_vuc_id,
    kv.khu_vuc_cha_id,
    ctgd.thuoc_id,
    t.nhom_thuoc_id,
    ctgd.lo_thuoc_id,
    ctgd.so_luong,
    ctgd.thanh_tien                                            AS doanh_thu,
    ctgd.gia_von * ctgd.so_luong                               AS gia_von,
    gd.khach_hang_id,
    kh.khu_vuc_id                                              AS khu_vuc_khach_id,
    CASE WHEN gd.don_thuoc_id IS NOT NULL THEN 1 ELSE 0 END    AS co_don_thuoc
FROM GiaoDich gd
JOIN ChiTietGiaoDich ctgd ON gd.id    = ctgd.giao_dich_id
JOIN NhaThuoc nt            ON nt.id  = gd.nha_thuoc_id
JOIN KhuVuc kv              ON kv.id  = nt.khu_vuc_id
JOIN Thuoc t                ON t.id   = ctgd.thuoc_id
LEFT JOIN KhachHang kh      ON kh.id  = gd.khach_hang_id
WHERE gd.trang_thai = N'HOAN_THANH';
GO

PRINT N'Views: OK';
GO

-- =====================================================================
-- STORED PROCEDURES THỐNG KÊ
-- =====================================================================

-- Hàm kiểm tra tham số thời gian (dùng nội bộ)
CREATE OR ALTER PROCEDURE dbo.sp_KiemTraThamSoThoiGian
    @tu DATE, @den DATE
AS
BEGIN
    IF @tu IS NULL OR @den IS NULL OR @den < @tu
        OR DATEDIFF(DAY, @tu, @den) > 731
        THROW 50017, N'Khoảng thời gian không hợp lệ hoặc vượt quá 731 ngày.', 1;
END;
GO

-- =====================================================================
-- sp_ThongKeNhuCauKhuVuc  (K1)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_ThongKeNhuCauKhuVuc
    @tu                    DATE,
    @den                   DATE,
    @khu_vuc_id            INT  = NULL,
    @nhom_thuoc_id         INT  = NULL,
    @pham_vi_nha_thuoc_id  INT  = NULL
AS
BEGIN
    SET NOCOUNT ON;
    EXEC sp_KiemTraThamSoThoiGian @tu, @den;

    -- dan_so cấp TINH = tổng quận con
    ;WITH DanSoKhuVuc AS (
        SELECT id, dan_so FROM KhuVuc WHERE cap = N'QUAN'
        UNION ALL
        SELECT kv_tinh.id,
               (SELECT ISNULL(SUM(q.dan_so), 0)
                FROM KhuVuc q WHERE q.khu_vuc_cha_id = kv_tinh.id AND q.cap = N'QUAN')
        FROM KhuVuc kv_tinh WHERE kv_tinh.cap = N'TINH'
    ),
    -- Xác định tập khu vực cần lọc (bao gồm khu vực con)
    KhuVucLoc AS (
        SELECT id FROM KhuVuc
        WHERE @khu_vuc_id IS NULL
           OR id = @khu_vuc_id
           OR khu_vuc_cha_id = @khu_vuc_id
    )
    SELECT
        kv.id                           AS khu_vuc_id,
        kv.ten                          AS khu_vuc,
        kv.cap,
        nt2.id                          AS nhom_thuoc_id,
        nt2.ten                         AS nhom_thuoc,
        SUM(b.so_luong)                 AS tong_so_luong,
        SUM(b.doanh_thu)                AS tong_doanh_thu,
        COUNT(DISTINCT b.khach_hang_id) AS so_khach,
        dsv.dan_so,
        CASE WHEN ISNULL(dsv.dan_so, 0) > 0
             THEN CAST(SUM(b.so_luong) * 1000.0 / dsv.dan_so AS DECIMAL(10,2))
             ELSE NULL END              AS sl_1000_dan
    FROM vw_BanHangChiTiet b
    JOIN NhaThuoc nt  ON nt.id  = b.nha_thuoc_id
    JOIN KhuVuc kv    ON kv.id  = nt.khu_vuc_id
    JOIN KhuVucLoc kl ON kl.id  = kv.id
    JOIN NhomThuoc nt2 ON nt2.id = b.nhom_thuoc_id
    LEFT JOIN DanSoKhuVuc dsv ON dsv.id = kv.id
    WHERE b.ngay BETWEEN @tu AND @den
      AND (@nhom_thuoc_id   IS NULL OR b.nhom_thuoc_id   = @nhom_thuoc_id)
      AND (@pham_vi_nha_thuoc_id IS NULL OR b.nha_thuoc_id = @pham_vi_nha_thuoc_id)
    GROUP BY kv.id, kv.ten, kv.cap, nt2.id, nt2.ten, dsv.dan_so
    ORDER BY sl_1000_dan DESC;
END;
GO

-- =====================================================================
-- sp_ThongKeXuHuong  (K2)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_ThongKeXuHuong
    @tu                    DATE,
    @den                   DATE,
    @khu_vuc_id            INT  = NULL,
    @nhom_thuoc_id         INT  = NULL,
    @pham_vi_nha_thuoc_id  INT  = NULL
AS
BEGIN
    SET NOCOUNT ON;
    EXEC sp_KiemTraThamSoThoiGian @tu, @den;

    WITH KhuVucLoc AS (
        SELECT id FROM KhuVuc
        WHERE @khu_vuc_id IS NULL OR id = @khu_vuc_id OR khu_vuc_cha_id = @khu_vuc_id
    ),
    MonthlyData AS (
        SELECT
            b.nam,
            b.thang,
            SUM(b.so_luong)   AS tong_so_luong,
            SUM(b.doanh_thu)  AS doanh_so
        FROM vw_BanHangChiTiet b
        JOIN NhaThuoc nt ON nt.id = b.nha_thuoc_id
        JOIN KhuVucLoc kl ON kl.id = nt.khu_vuc_id
        WHERE b.ngay BETWEEN @tu AND @den
          AND (@nhom_thuoc_id        IS NULL OR b.nhom_thuoc_id   = @nhom_thuoc_id)
          AND (@pham_vi_nha_thuoc_id IS NULL OR b.nha_thuoc_id    = @pham_vi_nha_thuoc_id)
        GROUP BY b.nam, b.thang
    )
    SELECT
        nam,
        thang,
        FORMAT(DATEFROMPARTS(nam, thang, 1), N'yyyy-MM') AS thang_str,
        tong_so_luong,
        doanh_so,
        LAG(doanh_so) OVER (ORDER BY nam, thang) AS doanh_so_thang_truoc,
        CASE WHEN LAG(doanh_so) OVER (ORDER BY nam, thang) > 0
             THEN CAST((doanh_so - LAG(doanh_so) OVER (ORDER BY nam, thang)) * 100.0
                       / LAG(doanh_so) OVER (ORDER BY nam, thang) AS DECIMAL(5,2))
             ELSE NULL END AS tang_truong_pct
    FROM MonthlyData
    ORDER BY nam, thang;
END;
GO

-- =====================================================================
-- sp_ThongKeTopThuoc  (K3)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_ThongKeTopThuoc
    @tu                    DATE,
    @den                   DATE,
    @khu_vuc_id            INT  = NULL,
    @nhom_thuoc_id         INT  = NULL,
    @pham_vi_nha_thuoc_id  INT  = NULL,
    @top                   INT  = 10
AS
BEGIN
    SET NOCOUNT ON;
    EXEC sp_KiemTraThamSoThoiGian @tu, @den;

    WITH KhuVucLoc AS (
        SELECT id FROM KhuVuc
        WHERE @khu_vuc_id IS NULL OR id = @khu_vuc_id OR khu_vuc_cha_id = @khu_vuc_id
    ),
    Ranked AS (
        SELECT
            kv.id                   AS khu_vuc_id,
            kv.ten                  AS khu_vuc,
            b.thuoc_id,
            t.ten                   AS ten_thuoc,
            t.hoat_chat,
            SUM(b.so_luong)         AS tong_so_luong,
            SUM(b.doanh_thu)        AS tong_doanh_thu,
            RANK() OVER (
                PARTITION BY kv.id
                ORDER BY SUM(b.so_luong) DESC
            )                       AS xep_hang
        FROM vw_BanHangChiTiet b
        JOIN NhaThuoc nt ON nt.id = b.nha_thuoc_id
        JOIN KhuVuc kv   ON kv.id = nt.khu_vuc_id
        JOIN KhuVucLoc kl ON kl.id = kv.id
        JOIN Thuoc t      ON t.id  = b.thuoc_id
        WHERE b.ngay BETWEEN @tu AND @den
          AND (@nhom_thuoc_id        IS NULL OR b.nhom_thuoc_id = @nhom_thuoc_id)
          AND (@pham_vi_nha_thuoc_id IS NULL OR b.nha_thuoc_id  = @pham_vi_nha_thuoc_id)
        GROUP BY kv.id, kv.ten, b.thuoc_id, t.ten, t.hoat_chat
    )
    SELECT * FROM Ranked WHERE xep_hang <= @top
    ORDER BY khu_vuc_id, xep_hang;
END;
GO

-- =====================================================================
-- sp_ThongKeHieuSuatNhaThuoc  (K4)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_ThongKeHieuSuatNhaThuoc
    @tu                    DATE,
    @den                   DATE,
    @khu_vuc_id            INT  = NULL,
    @nhom_thuoc_id         INT  = NULL,
    @pham_vi_nha_thuoc_id  INT  = NULL
AS
BEGIN
    SET NOCOUNT ON;
    EXEC sp_KiemTraThamSoThoiGian @tu, @den;

    WITH KhuVucLoc AS (
        SELECT id FROM KhuVuc
        WHERE @khu_vuc_id IS NULL OR id = @khu_vuc_id OR khu_vuc_cha_id = @khu_vuc_id
    ),
    GDData AS (
        SELECT
            gd.nha_thuoc_id,
            gd.id           AS gd_id,
            gd.thanh_tien,
            gd.don_thuoc_id,
            SUM(ctgd.so_luong)                AS tong_so_luong_ban,
            SUM(ctgd.so_luong * ctgd.gia_von) AS tong_gia_von,
            SUM(ctgd.thanh_tien)              AS tong_doanh_thu
        FROM GiaoDich gd
        JOIN ChiTietGiaoDich ctgd ON gd.id = ctgd.giao_dich_id
        JOIN Thuoc t              ON t.id  = ctgd.thuoc_id
        JOIN NhaThuoc nt          ON nt.id  = gd.nha_thuoc_id
        JOIN KhuVucLoc kl         ON kl.id  = nt.khu_vuc_id
        WHERE gd.trang_thai = N'HOAN_THANH'
          AND CAST(SWITCHOFFSET(gd.thoi_gian, N'+07:00') AS DATE) BETWEEN @tu AND @den
          AND (@nhom_thuoc_id        IS NULL OR t.nhom_thuoc_id = @nhom_thuoc_id)
          AND (@pham_vi_nha_thuoc_id IS NULL OR gd.nha_thuoc_id = @pham_vi_nha_thuoc_id)
        GROUP BY gd.nha_thuoc_id, gd.id, gd.thanh_tien, gd.don_thuoc_id
    ),
    TonTrungBinh AS (
        SELECT vt.nha_thuoc_id, SUM(vt.tong_ton) AS tong_ton_ht
        FROM vw_TonKhoTheoThuoc vt
        JOIN Thuoc t ON t.id = vt.thuoc_id
        WHERE (@nhom_thuoc_id IS NULL OR t.nhom_thuoc_id = @nhom_thuoc_id)
        GROUP BY vt.nha_thuoc_id
    )
    SELECT
        nt.id                               AS nha_thuoc_id,
        nt.ten                              AS nha_thuoc,
        kv.ten                              AS khu_vuc,
        SUM(g.tong_doanh_thu)              AS doanh_thu,
        COUNT(DISTINCT g.gd_id)            AS so_giao_dich,
        CAST(SUM(g.tong_doanh_thu) * 1.0
             / NULLIF(COUNT(DISTINCT g.gd_id), 0)
             AS DECIMAL(18,0))             AS gia_tri_gio_tb,
        CAST(COUNT(CASE WHEN g.don_thuoc_id IS NOT NULL THEN 1 END) * 100.0
             / NULLIF(COUNT(DISTINCT g.gd_id), 0)
             AS DECIMAL(5,2))              AS ty_le_ban_theo_don_pct,
        CAST((SUM(g.tong_doanh_thu) - SUM(g.tong_gia_von)) * 100.0
             / NULLIF(SUM(g.tong_doanh_thu), 0)
             AS DECIMAL(5,2))             AS bien_loi_nhuan_gop_pct,
        CAST(SUM(g.tong_so_luong_ban) * 1.0
             / NULLIF(ttb.tong_ton_ht, 0)
             AS DECIMAL(10,2))             AS vong_quay_ton
    FROM GDData g
    JOIN NhaThuoc nt ON nt.id = g.nha_thuoc_id
    JOIN KhuVuc kv   ON kv.id = nt.khu_vuc_id
    LEFT JOIN TonTrungBinh ttb ON ttb.nha_thuoc_id = nt.id
    GROUP BY nt.id, nt.ten, kv.ten, ttb.tong_ton_ht
    ORDER BY doanh_thu DESC;
END;
GO

-- =====================================================================
-- sp_DeXuatNhapHang  (K5)
-- Công thức: de_xuat = MAX(0, ban_tb_ngay × (so_ngay_giao_hang + so_ngay_an_toan) − ton_kha_dung)
-- ton_kha_dung: loại lô còn < 30 ngày
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_DeXuatNhapHang
    @tu                    DATE       = NULL,
    @den                   DATE       = NULL,
    @khu_vuc_id            INT        = NULL,
    @nhom_thuoc_id         INT        = NULL,
    @pham_vi_nha_thuoc_id  INT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @so_ngay_tb   INT = dbo.fn_CauHinhInt(N'so_ngay_tinh_ban_trung_binh', 30);
    DECLARE @so_ngay_at   INT = dbo.fn_CauHinhInt(N'so_ngay_an_toan', 3);
    DECLARE @hom_nay      DATE = dbo.fn_HomNayVN();
    DECLARE @ngay_tu      DATE = DATEADD(DAY, -@so_ngay_tb, @hom_nay);
    DECLARE @ngay_den_calc DATE = @hom_nay;

    ;WITH BanTrungBinh AS (
        SELECT
            gd.nha_thuoc_id,
            ctgd.thuoc_id,
            CAST(SUM(ctgd.so_luong) * 1.0 / @so_ngay_tb AS DECIMAL(10,4)) AS ban_tb_ngay
        FROM GiaoDich gd
        JOIN ChiTietGiaoDich ctgd ON gd.id = ctgd.giao_dich_id
        WHERE gd.trang_thai = N'HOAN_THANH'
          AND CAST(SWITCHOFFSET(gd.thoi_gian, N'+07:00') AS DATE)
              BETWEEN @ngay_tu AND @ngay_den_calc
          AND (@pham_vi_nha_thuoc_id IS NULL OR gd.nha_thuoc_id = @pham_vi_nha_thuoc_id)
        GROUP BY gd.nha_thuoc_id, ctgd.thuoc_id
    ),
    -- Tồn khả dụng: loại lô còn < 30 ngày
    TonKhaDung AS (
        SELECT
            tk.nha_thuoc_id,
            lt.thuoc_id,
            SUM(tk.so_luong) AS ton_kha_dung
        FROM TonKho tk
        JOIN LoThuoc lt ON lt.id = tk.lo_thuoc_id
        WHERE lt.han_dung >= DATEADD(DAY, 30, @hom_nay)
          AND (@pham_vi_nha_thuoc_id IS NULL OR tk.nha_thuoc_id = @pham_vi_nha_thuoc_id)
        GROUP BY tk.nha_thuoc_id, lt.thuoc_id
    )
    SELECT
        b.nha_thuoc_id,
        nt.ten                                  AS nha_thuoc,
        b.thuoc_id,
        t.ten                                   AS ten_thuoc,
        t.don_vi_tinh,
        ISNULL(d.so_ngay_giao_hang, 3)          AS so_ngay_giao_hang,
        b.ban_tb_ngay,
        ISNULL(tkd.ton_kha_dung, 0)             AS ton_kha_dung,
        CAST(CEILING(CASE
            WHEN b.ban_tb_ngay * (ISNULL(d.so_ngay_giao_hang, 3) + @so_ngay_at)
                 - ISNULL(tkd.ton_kha_dung, 0) > 0
            THEN b.ban_tb_ngay * (ISNULL(d.so_ngay_giao_hang, 3) + @so_ngay_at)
                 - ISNULL(tkd.ton_kha_dung, 0)
            ELSE 0
        END) AS INT)                            AS so_luong_de_xuat
    FROM BanTrungBinh b
    LEFT JOIN TonKhaDung tkd
      ON tkd.nha_thuoc_id = b.nha_thuoc_id AND tkd.thuoc_id = b.thuoc_id
    JOIN NhaThuoc nt ON nt.id = b.nha_thuoc_id
    JOIN Thuoc t     ON t.id  = b.thuoc_id
    LEFT JOIN DinhMucTon d
      ON d.nha_thuoc_id = b.nha_thuoc_id AND d.thuoc_id = b.thuoc_id
    WHERE (@nhom_thuoc_id IS NULL OR t.nhom_thuoc_id = @nhom_thuoc_id)
    ORDER BY so_luong_de_xuat DESC;
END;
GO

