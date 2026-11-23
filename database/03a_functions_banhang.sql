-- =====================================================================
-- 03a_functions_banhang.sql  –  IS009
-- Hàm tiện ích + SP bán hàng và đơn thuốc
-- =====================================================================
USE IS009;
GO
SET QUOTED_IDENTIFIER ON;
GO

-- =====================================================================
-- HÀM TIỆN ÍCH
-- =====================================================================

-- fn_HomNayVN(): ngày hôm nay theo giờ Việt Nam (UTC+7)
CREATE OR ALTER FUNCTION dbo.fn_HomNayVN()
RETURNS DATE
AS
BEGIN
    RETURN CAST(
        SWITCHOFFSET(SYSUTCDATETIME(), N'+07:00') AS DATE
    );
END;
GO

-- fn_CauHinhInt(@khoa, @mac_dinh): đọc CauHinh trả INT
CREATE OR ALTER FUNCTION dbo.fn_CauHinhInt(@khoa NVARCHAR(50), @mac_dinh INT)
RETURNS INT
AS
BEGIN
    DECLARE @v NVARCHAR(200);
    SELECT @v = gia_tri FROM CauHinh WHERE khoa = @khoa;
    IF @v IS NULL OR ISNUMERIC(@v) = 0 RETURN @mac_dinh;
    RETURN CAST(@v AS INT);
END;
GO

-- fn_KiemTraEAN13(@ma): trả 1 nếu EAN13 hợp lệ (13 chữ số, checkdigit đúng)
CREATE OR ALTER FUNCTION dbo.fn_KiemTraEAN13(@ma NVARCHAR(50))
RETURNS BIT
AS
BEGIN
    IF LEN(@ma) <> 13 OR @ma NOT LIKE N'[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'
        RETURN 0;
    DECLARE @sum INT = 0, @i INT = 1;
    WHILE @i <= 12
    BEGIN
        DECLARE @digit INT = CAST(SUBSTRING(@ma, @i, 1) AS INT);
        IF @i % 2 = 0 SET @sum = @sum + @digit * 3
        ELSE           SET @sum = @sum + @digit;
        SET @i = @i + 1;
    END;
    DECLARE @check INT = (10 - (@sum % 10)) % 10;
    IF @check = CAST(SUBSTRING(@ma, 13, 1) AS INT) RETURN 1;
    RETURN 0;
END;
GO

-- fn_CoQuyenNhaThuoc(@nguoi_id, @nha_thuoc_id): 1 nếu QUANTRI hoặc thuộc nhà thuốc đó
CREATE OR ALTER FUNCTION dbo.fn_CoQuyenNhaThuoc(@nguoi_id INT, @nha_thuoc_id INT)
RETURNS BIT
AS
BEGIN
    DECLARE @vai_tro NVARCHAR(10), @nt INT;
    SELECT @vai_tro = vai_tro, @nt = nha_thuoc_id
    FROM NguoiDung WHERE id = @nguoi_id AND kich_hoat = 1;
    IF @vai_tro IS NULL RETURN 0;
    IF @vai_tro = N'QUANTRI' RETURN 1;
    IF @nt = @nha_thuoc_id RETURN 1;
    RETURN 0;
END;
GO

-- =====================================================================
-- sp_ThemMaVach
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_ThemMaVach
    @thuoc_id      INT,
    @ma_vach       NVARCHAR(50),
    @loai_ma_vach  NVARCHAR(10),
    @la_ma_chinh   BIT = 0,
    @nguoi_id      INT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
        -- Chỉ QUANTRI
        DECLARE @vt NVARCHAR(10);
        SELECT @vt = vai_tro FROM NguoiDung WHERE id = @nguoi_id AND kich_hoat = 1;
        IF @vt <> N'QUANTRI'
            THROW 50011, N'Chỉ QUANTRI mới được thêm mã vạch. Kiểm tra vai trò tài khoản.', 1;

        -- EAN13 phải hợp lệ
        IF @loai_ma_vach = N'EAN13' AND dbo.fn_KiemTraEAN13(@ma_vach) = 0
            THROW 50007, N'Mã EAN13 không hợp lệ: cần đúng 13 chữ số và chữ số kiểm đúng.', 1;

        -- Mã đã tồn tại
        IF EXISTS (SELECT 1 FROM MaVachThuoc WHERE ma_vach = @ma_vach)
            THROW 50007, N'Mã vạch đã tồn tại trong hệ thống. Dùng mã khác.', 1;

        INSERT INTO MaVachThuoc (thuoc_id, ma_vach, loai_ma_vach, la_ma_chinh)
        VALUES (@thuoc_id, @ma_vach, @loai_ma_vach, @la_ma_chinh);

        INSERT INTO AuditLog (nguoi_dung_id, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_moi)
        VALUES (@nguoi_id, N'THEM_MA_VACH', N'MaVachThuoc', SCOPE_IDENTITY(),
                N'{"ma_vach":"' + @ma_vach + N'","loai":"' + @loai_ma_vach + N'"}');
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK;
        THROW;
    END CATCH;
END;
GO

-- =====================================================================
-- sp_TraCuuThuocTheoMaVach
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_TraCuuThuocTheoMaVach
    @ma_vach      NVARCHAR(50),
    @nha_thuoc_id INT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        t.id              AS thuoc_id,
        t.ma              AS ma_thuoc,
        t.ten             AS ten_thuoc,
        t.hoat_chat,
        t.ham_luong,
        t.dang_bao_che,
        t.don_vi_tinh,
        t.can_ke_don,
        t.gia_ban_le,
        t.trang_thai,
        lt.id             AS lo_thuoc_id,
        lt.so_lo,
        lt.han_dung,
        DATEDIFF(DAY, dbo.fn_HomNayVN(), lt.han_dung) AS so_ngay_con_lai,
        CASE
            WHEN lt.han_dung < dbo.fn_HomNayVN()
                 THEN N'DA_HET_HAN'
            WHEN DATEDIFF(DAY, dbo.fn_HomNayVN(), lt.han_dung)
                 <= dbo.fn_CauHinhInt(N'canh_bao_han_muc3_ngay', 30)
                 THEN N'MUC_30'
            WHEN DATEDIFF(DAY, dbo.fn_HomNayVN(), lt.han_dung)
                 <= dbo.fn_CauHinhInt(N'canh_bao_han_muc2_ngay', 60)
                 THEN N'MUC_60'
            WHEN DATEDIFF(DAY, dbo.fn_HomNayVN(), lt.han_dung)
                 <= dbo.fn_CauHinhInt(N'canh_bao_han_muc1_ngay', 90)
                 THEN N'MUC_90'
            ELSE NULL
        END               AS muc_canh_bao,
        tk.so_luong       AS so_luong_ton
    FROM MaVachThuoc mv
    JOIN Thuoc   t  ON mv.thuoc_id    = t.id
    JOIN LoThuoc lt ON lt.thuoc_id    = t.id
    JOIN TonKho  tk ON tk.lo_thuoc_id = lt.id
                   AND tk.nha_thuoc_id = @nha_thuoc_id
    WHERE mv.ma_vach = @ma_vach
      AND tk.so_luong > 0
    ORDER BY lt.han_dung ASC, lt.id ASC;
END;
GO

-- =====================================================================
-- sp_XemTruocFEFO  (chỉ đọc, không giữ chỗ)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_XemTruocFEFO
    @nha_thuoc_id   INT,
    @chi_tiet_json  NVARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        IF ISJSON(@chi_tiet_json) = 0
            THROW 50016, N'JSON chi tiết không hợp lệ. Kiểm tra cú pháp JSON.', 1;

        -- Parse JSON input
        DECLARE @items TABLE (thuoc_id INT, so_luong INT, lo_thuoc_id INT);
        INSERT INTO @items (thuoc_id, so_luong, lo_thuoc_id)
        SELECT
            CAST(JSON_VALUE(j.value, N'$.thuoc_id')    AS INT),
            CAST(JSON_VALUE(j.value, N'$.so_luong')    AS INT),
            CAST(JSON_VALUE(j.value, N'$.lo_thuoc_id') AS INT)
        FROM OPENJSON(@chi_tiet_json) j;

        -- Gộp dòng trùng (cùng thuoc + lô)
        ;WITH GopLai AS (
            SELECT thuoc_id,
                   lo_thuoc_id,
                   SUM(so_luong) AS so_luong
            FROM @items
            GROUP BY thuoc_id, lo_thuoc_id
        ),
        -- Phân lô FEFO cho từng thuốc
        LoFEFO AS (
            SELECT
                g.thuoc_id,
                lt.id       AS lo_thuoc_id,
                lt.so_lo,
                lt.han_dung,
                tk.so_luong AS ton_hien_tai,
                g.so_luong  AS so_luong_can,
                ROW_NUMBER() OVER (PARTITION BY g.thuoc_id ORDER BY lt.han_dung ASC, lt.id ASC) AS rn,
                SUM(tk.so_luong) OVER (
                    PARTITION BY g.thuoc_id
                    ORDER BY lt.han_dung ASC, lt.id ASC
                    ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
                ) AS tich_luy
            FROM GopLai g
            JOIN LoThuoc lt ON lt.thuoc_id = g.thuoc_id
            JOIN TonKho  tk ON tk.lo_thuoc_id = lt.id
                            AND tk.nha_thuoc_id = @nha_thuoc_id
            -- lo_thuoc_id tường minh nếu người dùng chỉ định
            WHERE (g.lo_thuoc_id IS NULL OR lt.id = g.lo_thuoc_id)
              AND lt.han_dung >= dbo.fn_HomNayVN()   -- bỏ lô hết hạn
              AND tk.so_luong > 0
        )
        SELECT
            f.thuoc_id,
            f.lo_thuoc_id,
            f.so_lo,
            f.han_dung,
            CASE
                WHEN f.tich_luy - f.ton_hien_tai >= f.so_luong_can
                     THEN 0
                WHEN f.tich_luy >= f.so_luong_can
                     THEN f.so_luong_can - (f.tich_luy - f.ton_hien_tai)
                ELSE f.ton_hien_tai
            END                AS so_luong,
            t.gia_ban_le       AS don_gia
        FROM LoFEFO f
        JOIN Thuoc t ON t.id = f.thuoc_id
        WHERE f.tich_luy - f.ton_hien_tai < f.so_luong_can;
    END TRY
    BEGIN CATCH THROW; END CATCH;
END;
GO

-- =====================================================================
-- sp_BanHang  (transaction chính BR01–BR04,BR12,BR15)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_BanHang
    @nha_thuoc_id           INT,
    @nguoi_ban_id           INT,
    @khach_hang_id          INT        = NULL,
    @don_thuoc_id           INT        = NULL,
    @phuong_thuc_thanh_toan NVARCHAR(15),
    @giam_gia               DECIMAL(18,0) = 0,
    @chi_tiet_json          NVARCHAR(MAX),
    @giao_dich_id           INT        OUTPUT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
        -- Validate JSON
        IF ISJSON(@chi_tiet_json) = 0
            THROW 50016, N'JSON chi tiết không hợp lệ.', 1;

        -- Kiểm tra người bán (DUOCSI hoặc QUANLY, nhà thuốc đó, còn kích hoạt)
        DECLARE @vai_tro_ban NVARCHAR(10), @nt_ban INT;
        SELECT @vai_tro_ban = vai_tro, @nt_ban = nha_thuoc_id
        FROM NguoiDung WHERE id = @nguoi_ban_id AND kich_hoat = 1;
        IF @vai_tro_ban IS NULL
            THROW 50013, N'Người dùng không tồn tại hoặc bị khóa.', 1;
        IF @vai_tro_ban NOT IN (N'DUOCSI', N'QUANLY') OR @nt_ban <> @nha_thuoc_id
            THROW 50011, N'Không có quyền bán tại nhà thuốc này.', 1;

        -- Kiểm tra nhà thuốc đang hoạt động
        IF NOT EXISTS (SELECT 1 FROM NhaThuoc WHERE id = @nha_thuoc_id AND trang_thai = N'HOAT_DONG')
            THROW 50012, N'Nhà thuốc không hoạt động hoặc không tồn tại.', 1;

        -- Parse JSON chi tiết
        DECLARE @items TABLE (
            thuoc_id    INT       NOT NULL,
            so_luong    INT       NOT NULL,
            lo_thuoc_id INT       NULL
        );
        INSERT INTO @items (thuoc_id, so_luong, lo_thuoc_id)
        SELECT
            CAST(JSON_VALUE(j.value, N'$.thuoc_id')    AS INT),
            CAST(JSON_VALUE(j.value, N'$.so_luong')    AS INT),
            CAST(JSON_VALUE(j.value, N'$.lo_thuoc_id') AS INT)
        FROM OPENJSON(@chi_tiet_json) j
        WHERE CAST(JSON_VALUE(j.value, N'$.so_luong') AS INT) > 0;

        IF NOT EXISTS (SELECT 1 FROM @items)
            THROW 50016, N'Chi tiết JSON không có dòng hợp lệ (so_luong > 0).', 1;

        -- Gộp dòng trùng
        DECLARE @items2 TABLE (
            thuoc_id    INT  NOT NULL,
            so_luong    INT  NOT NULL,
            lo_thuoc_id INT  NULL,
            don_gia     DECIMAL(18,0) NULL,
            can_ke_don  BIT NULL
        );
        INSERT INTO @items2 (thuoc_id, so_luong, lo_thuoc_id)
        SELECT thuoc_id, SUM(so_luong), lo_thuoc_id
        FROM @items GROUP BY thuoc_id, lo_thuoc_id;

        -- Kiểm tra thuốc tồn tại và không ngừng bán
        UPDATE i SET don_gia = t.gia_ban_le, can_ke_don = t.can_ke_don
        FROM @items2 i JOIN Thuoc t ON t.id = i.thuoc_id AND t.trang_thai = N'DANG_BAN';
        IF EXISTS (SELECT 1 FROM @items2 WHERE don_gia IS NULL)
            THROW 50001, N'Có thuốc không tồn tại hoặc đã ngừng bán. Kiểm tra danh sách.', 1;

        -- BR01: kiểm tra thuốc kê đơn
        IF EXISTS (SELECT 1 FROM @items2 WHERE can_ke_don = 1)
        BEGIN
            IF @don_thuoc_id IS NULL
                THROW 50004, N'Thuốc kê đơn cần đơn thuốc hợp lệ. Vui lòng tiếp nhận đơn trước.', 1;

            -- Cập nhật đơn hết hiệu lực trước
            EXEC dbo.sp_CapNhatDonHetHieuLuc @nha_thuoc_id = @nha_thuoc_id;

            DECLARE @dt_nt INT, @dt_tt NVARCHAR(15);
            SELECT @dt_nt = nha_thuoc_id, @dt_tt = trang_thai
            FROM DonThuoc WHERE id = @don_thuoc_id;

            IF @dt_tt = N'HET_HIEU_LUC'
                THROW 50005, N'Đơn thuốc đã hết hiệu lực. Yêu cầu đơn mới từ bác sĩ.', 1;
            IF @dt_tt NOT IN (N'CHUA_BAN', N'BAN_MOT_PHAN')
                THROW 50005, N'Đơn thuốc không còn hiệu lực để bán.', 1;
            IF @dt_nt <> @nha_thuoc_id
                THROW 50004, N'Đơn thuốc không thuộc nhà thuốc này. Chỉ bán tại nhà thuốc tiếp nhận.', 1;

            -- Kiểm tra từng thuốc kê đơn: có trong đơn, không vượt số lượng còn lại
            DECLARE @t_id INT, @sl INT;
            DECLARE cur_ke CURSOR FAST_FORWARD LOCAL FOR
                SELECT thuoc_id, so_luong FROM @items2 WHERE can_ke_don = 1;
            OPEN cur_ke; FETCH NEXT FROM cur_ke INTO @t_id, @sl;
            WHILE @@FETCH_STATUS = 0
            BEGIN
                DECLARE @sl_ke INT, @sl_da_ban INT;
                SELECT @sl_ke = so_luong_ke, @sl_da_ban = so_luong_da_ban
                FROM ChiTietDonThuoc
                WHERE don_thuoc_id = @don_thuoc_id AND thuoc_id = @t_id;
                IF @sl_ke IS NULL
                    THROW 50004, N'Thuốc kê đơn không có trong đơn thuốc này.', 1;
                IF (@sl_da_ban + @sl) > @sl_ke
                    THROW 50006, N'Số lượng bán vượt số lượng còn được phép trong đơn.', 1;
                FETCH NEXT FROM cur_ke INTO @t_id, @sl;
            END
            CLOSE cur_ke; DEALLOCATE cur_ke;
        END

        -- BR02+BR03: Phân lô FEFO (với UPDLOCK theo thứ tự lo_thuoc_id tăng dần – BR04)
        DECLARE @lo_phan TABLE (
            thuoc_id    INT           NOT NULL,
            lo_thuoc_id INT           NOT NULL,
            so_luong    INT           NOT NULL,
            don_gia     DECIMAL(18,0) NOT NULL,
            gia_von     DECIMAL(18,0) NOT NULL
        );

        -- Xây dựng danh sách lô ưu tiên (FEFO) cho từng thuốc
        ;WITH LoCandidates AS (
            SELECT
                i.thuoc_id,
                i.so_luong        AS can_mua,
                lt.id             AS lo_thuoc_id,
                lt.han_dung,
                i.don_gia,
                lt.gia_nhap       AS gia_von,
                i.lo_thuoc_id     AS lo_chi_dinh,
                ROW_NUMBER() OVER (
                    PARTITION BY i.thuoc_id
                    ORDER BY lt.han_dung ASC, lt.id ASC
                ) AS rn,
                SUM(tk.so_luong) OVER (
                    PARTITION BY i.thuoc_id
                    ORDER BY lt.han_dung ASC, lt.id ASC
                    ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
                ) AS cum_ton,
                SUM(tk.so_luong) OVER (
                    PARTITION BY i.thuoc_id
                    ORDER BY lt.han_dung ASC, lt.id ASC
                    ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING
                ) AS prev_cum
            FROM @items2 i
            JOIN LoThuoc lt ON lt.thuoc_id = i.thuoc_id
            JOIN TonKho  tk ON tk.lo_thuoc_id = lt.id
                            AND tk.nha_thuoc_id = @nha_thuoc_id
            WHERE lt.han_dung >= dbo.fn_HomNayVN()   -- BR03
              AND tk.so_luong > 0
              AND (i.lo_thuoc_id IS NULL OR lt.id = i.lo_thuoc_id)
        )
        INSERT INTO @lo_phan (thuoc_id, lo_thuoc_id, so_luong, don_gia, gia_von)
        SELECT
            thuoc_id,
            lo_thuoc_id,
            CASE
                WHEN ISNULL(prev_cum, 0) >= can_mua THEN 0
                WHEN cum_ton >= can_mua
                     THEN can_mua - ISNULL(prev_cum, 0)
                ELSE
                     cum_ton - ISNULL(prev_cum, 0)
            END AS so_luong,
            don_gia,
            gia_von
        FROM LoCandidates
        WHERE ISNULL(prev_cum, 0) < can_mua;

        -- Xóa dòng so_luong = 0
        DELETE FROM @lo_phan WHERE so_luong = 0;

        -- Kiểm tra tổng phân lô đủ không
        DECLARE @thieuTon BIT = 0;
        SELECT @thieuTon = 1
        FROM @items2 i
        WHERE (SELECT ISNULL(SUM(p.so_luong),0)
               FROM @lo_phan p WHERE p.thuoc_id = i.thuoc_id) < i.so_luong;
        IF @thieuTon = 1
            THROW 50002, N'Không đủ tồn kho cho một hoặc nhiều mặt hàng trong giỏ.', 1;

        -- ===== BEGIN TRANSACTION =====
        BEGIN TRANSACTION;

        -- BR04: Khóa TonKho theo thứ tự lo_thuoc_id tăng dần
        DECLARE @lo_ids TABLE (lo_id INT PRIMARY KEY);
        INSERT INTO @lo_ids SELECT DISTINCT lo_thuoc_id FROM @lo_phan ORDER BY lo_thuoc_id;

        DECLARE @lo_id INT;
        DECLARE cur_lock CURSOR FAST_FORWARD LOCAL FOR
            SELECT lo_id FROM @lo_ids ORDER BY lo_id;
        OPEN cur_lock; FETCH NEXT FROM cur_lock INTO @lo_id;
        WHILE @@FETCH_STATUS = 0
        BEGIN
            SELECT so_luong FROM TonKho WITH (UPDLOCK, HOLDLOCK)
            WHERE nha_thuoc_id = @nha_thuoc_id AND lo_thuoc_id = @lo_id;
            FETCH NEXT FROM cur_lock INTO @lo_id;
        END
        CLOSE cur_lock; DEALLOCATE cur_lock;

        -- Kiểm tra lại tồn sau khi đã khóa
        DECLARE @thieuSauLock BIT = 0;
        SELECT @thieuSauLock = 1
        FROM @lo_phan p
        JOIN TonKho tk ON tk.lo_thuoc_id = p.lo_thuoc_id
                       AND tk.nha_thuoc_id = @nha_thuoc_id
        WHERE tk.so_luong < p.so_luong;
        IF @thieuSauLock = 1
        BEGIN ROLLBACK; THROW 50002, N'Không đủ tồn kho (tồn thay đổi từ khi kiểm tra).', 1; END

        -- Sinh mã giao dịch
        DECLARE @ma_gd NVARCHAR(30);
        DECLARE @seq INT;
        SELECT @seq = ISNULL(MAX(CAST(RIGHT(ma_giao_dich, 5) AS INT)), 0) + 1
        FROM GiaoDich
        WHERE ma_giao_dich LIKE N'GD-' + FORMAT(dbo.fn_HomNayVN(), N'yyyyMMdd') + N'-%';
        SET @ma_gd = N'GD-' + FORMAT(dbo.fn_HomNayVN(), N'yyyyMMdd') + N'-' + RIGHT(N'00000' + CAST(@seq AS NVARCHAR(5)), 5);

        -- Tính tổng tiền
        DECLARE @tong_tien DECIMAL(18,0) =
            (SELECT SUM(p.so_luong * p.don_gia) FROM @lo_phan p);
        DECLARE @thanh_tien DECIMAL(18,0) = @tong_tien - ISNULL(@giam_gia, 0);

        -- Tạo GiaoDich
        INSERT INTO GiaoDich (
            ma_giao_dich, nha_thuoc_id, nguoi_ban_id, khach_hang_id, don_thuoc_id,
            thoi_gian, tong_tien, giam_gia, thanh_tien, phuong_thuc_thanh_toan, trang_thai
        ) VALUES (
            @ma_gd, @nha_thuoc_id, @nguoi_ban_id, @khach_hang_id, @don_thuoc_id,
            SYSUTCDATETIME(), @tong_tien, ISNULL(@giam_gia,0), @thanh_tien,
            @phuong_thuc_thanh_toan, N'HOAN_THANH'
        );
        SET @giao_dich_id = SCOPE_IDENTITY();

        -- Ghi ChiTietGiaoDich + trừ TonKho + ghi BienDongKho
        DECLARE @p_thuoc INT, @p_lo INT, @p_sl INT, @p_dg DECIMAL(18,0), @p_gv DECIMAL(18,0);
        DECLARE cur_ban CURSOR FAST_FORWARD LOCAL FOR
            SELECT thuoc_id, lo_thuoc_id, so_luong, don_gia, gia_von
            FROM @lo_phan ORDER BY lo_thuoc_id;
        OPEN cur_ban; FETCH NEXT FROM cur_ban INTO @p_thuoc, @p_lo, @p_sl, @p_dg, @p_gv;
        WHILE @@FETCH_STATUS = 0
        BEGIN
            INSERT INTO ChiTietGiaoDich (giao_dich_id, thuoc_id, lo_thuoc_id, so_luong, don_gia, gia_von, thanh_tien)
            VALUES (@giao_dich_id, @p_thuoc, @p_lo, @p_sl, @p_dg, @p_gv, @p_sl * @p_dg);

            UPDATE TonKho
            SET so_luong = so_luong - @p_sl
            WHERE nha_thuoc_id = @nha_thuoc_id AND lo_thuoc_id = @p_lo;

            DECLARE @so_du INT;
            SELECT @so_du = so_luong FROM TonKho
            WHERE nha_thuoc_id = @nha_thuoc_id AND lo_thuoc_id = @p_lo;

            INSERT INTO BienDongKho (
                nha_thuoc_id, lo_thuoc_id, loai_bien_dong,
                so_luong_thay_doi, so_du_sau,
                chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id
            ) VALUES (
                @nha_thuoc_id, @p_lo, N'BAN',
                -@p_sl, @so_du,
                N'GIAO_DICH', @giao_dich_id, @nguoi_ban_id
            );

            FETCH NEXT FROM cur_ban INTO @p_thuoc, @p_lo, @p_sl, @p_dg, @p_gv;
        END
        CLOSE cur_ban; DEALLOCATE cur_ban;

        -- BR01: Cập nhật so_luong_da_ban và trạng thái DonThuoc
        IF @don_thuoc_id IS NOT NULL
        BEGIN
            DECLARE @item_thuoc INT, @item_sl INT;
            DECLARE cur_don CURSOR FAST_FORWARD LOCAL FOR
                SELECT thuoc_id, SUM(so_luong)
                FROM @lo_phan GROUP BY thuoc_id;
            OPEN cur_don; FETCH NEXT FROM cur_don INTO @item_thuoc, @item_sl;
            WHILE @@FETCH_STATUS = 0
            BEGIN
                UPDATE ChiTietDonThuoc
                SET so_luong_da_ban = so_luong_da_ban + @item_sl
                WHERE don_thuoc_id = @don_thuoc_id AND thuoc_id = @item_thuoc;
                FETCH NEXT FROM cur_don INTO @item_thuoc, @item_sl;
            END
            CLOSE cur_don; DEALLOCATE cur_don;

            -- Cập nhật trạng thái đơn
            DECLARE @tong_ke INT, @tong_da_ban INT;
            SELECT @tong_ke = SUM(so_luong_ke), @tong_da_ban = SUM(so_luong_da_ban)
            FROM ChiTietDonThuoc WHERE don_thuoc_id = @don_thuoc_id;
            UPDATE DonThuoc
            SET trang_thai = CASE
                WHEN @tong_da_ban >= @tong_ke THEN N'DA_BAN_HET'
                WHEN @tong_da_ban > 0         THEN N'BAN_MOT_PHAN'
                ELSE trang_thai
            END
            WHERE id = @don_thuoc_id;
        END

        -- AuditLog
        INSERT INTO AuditLog (nguoi_dung_id, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_moi)
        VALUES (@nguoi_ban_id, N'BAN_HANG', N'GiaoDich', @giao_dich_id,
                N'{"ma_giao_dich":"' + @ma_gd + N'","thanh_tien":' + CAST(@thanh_tien AS NVARCHAR(30)) + N'}');

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK;
        THROW;
    END CATCH;
END;
GO

-- =====================================================================
-- sp_HuyGiaoDich  (BR10)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_HuyGiaoDich
    @giao_dich_id INT,
    @nguoi_huy_id INT,
    @ly_do        NVARCHAR(500)
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
        IF @ly_do IS NULL OR LEN(LTRIM(@ly_do)) = 0
            THROW 50010, N'Bắt buộc nhập lý do hủy giao dịch.', 1;

        -- Kiểm tra vai trò QUANLY, nhà thuốc đúng
        DECLARE @vt NVARCHAR(10), @nt_huy INT;
        SELECT @vt = vai_tro, @nt_huy = nha_thuoc_id
        FROM NguoiDung WHERE id = @nguoi_huy_id AND kich_hoat = 1;
        IF @vt <> N'QUANLY'
            THROW 50011, N'Chỉ QUANLY mới được hủy giao dịch.', 1;

        DECLARE @gd_nt INT, @gd_tt NVARCHAR(10), @gd_time DATETIME2(0), @gd_dt INT;
        SELECT @gd_nt = nha_thuoc_id, @gd_tt = trang_thai,
               @gd_time = thoi_gian, @gd_dt = don_thuoc_id
        FROM GiaoDich WHERE id = @giao_dich_id;
        IF @gd_tt IS NULL
            THROW 50014, N'Giao dịch không tồn tại.', 1;
        IF @gd_tt <> N'HOAN_THANH'
            THROW 50009, N'Giao dịch không ở trạng thái HOAN_THANH. Không thể hủy.', 1;
        IF @nt_huy <> @gd_nt
            THROW 50011, N'Không có quyền hủy giao dịch của nhà thuốc khác.', 1;

        -- Kiểm tra trong hạn (BR10)
        DECLARE @gio_cho_phep INT = dbo.fn_CauHinhInt(N'gio_cho_phep_huy_giao_dich', 24);
        IF DATEDIFF(HOUR, @gd_time, SYSUTCDATETIME()) > @gio_cho_phep
            THROW 50015, N'Đã quá thời hạn cho phép hủy giao dịch. Liên hệ QUANTRI để xử lý.', 1;

        BEGIN TRANSACTION;

        -- Hoàn tồn đúng lô
        DECLARE @ct_lo INT, @ct_sl INT, @ct_thuoc INT;
        DECLARE cur_huy CURSOR FAST_FORWARD LOCAL FOR
            SELECT lo_thuoc_id, so_luong, thuoc_id, giao_dich_id
            FROM ChiTietGiaoDich WHERE giao_dich_id = @giao_dich_id
            ORDER BY lo_thuoc_id;
        OPEN cur_huy; FETCH NEXT FROM cur_huy INTO @ct_lo, @ct_sl, @ct_thuoc, @giao_dich_id;
        WHILE @@FETCH_STATUS = 0
        BEGIN
            -- Merge để tránh thiếu dòng TonKho (lô đã từng bị điều chuyển đi hết)
            MERGE TonKho AS target
            USING (SELECT @gd_nt AS nha_thuoc_id, @ct_lo AS lo_thuoc_id) AS src
            ON target.nha_thuoc_id = src.nha_thuoc_id AND target.lo_thuoc_id = src.lo_thuoc_id
            WHEN MATCHED THEN UPDATE SET so_luong = so_luong + @ct_sl
            WHEN NOT MATCHED THEN INSERT (nha_thuoc_id, lo_thuoc_id, so_luong) VALUES (src.nha_thuoc_id, src.lo_thuoc_id, @ct_sl);

            DECLARE @so_du_h INT;
            SELECT @so_du_h = so_luong FROM TonKho
            WHERE nha_thuoc_id = @gd_nt AND lo_thuoc_id = @ct_lo;

            INSERT INTO BienDongKho (
                nha_thuoc_id, lo_thuoc_id, loai_bien_dong,
                so_luong_thay_doi, so_du_sau,
                chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id
            ) VALUES (
                @gd_nt, @ct_lo, N'HOAN_KHO',
                @ct_sl, @so_du_h,
                N'GIAO_DICH', @giao_dich_id, @nguoi_huy_id
            );

            FETCH NEXT FROM cur_huy INTO @ct_lo, @ct_sl, @ct_thuoc, @giao_dich_id;
        END
        CLOSE cur_huy; DEALLOCATE cur_huy;

        -- Hoàn so_luong_da_ban nếu bán theo đơn
        IF @gd_dt IS NOT NULL
        BEGIN
            DECLARE @rct_thuoc INT, @rct_sl INT;
            DECLARE cur_hd CURSOR FAST_FORWARD LOCAL FOR
                SELECT thuoc_id, SUM(so_luong)
                FROM ChiTietGiaoDich WHERE giao_dich_id = @giao_dich_id
                GROUP BY thuoc_id;
            OPEN cur_hd; FETCH NEXT FROM cur_hd INTO @rct_thuoc, @rct_sl;
            WHILE @@FETCH_STATUS = 0
            BEGIN
                UPDATE ChiTietDonThuoc
                SET so_luong_da_ban = CASE WHEN so_luong_da_ban - @rct_sl < 0 THEN 0 ELSE so_luong_da_ban - @rct_sl END
                WHERE don_thuoc_id = @gd_dt AND thuoc_id = @rct_thuoc;
                FETCH NEXT FROM cur_hd INTO @rct_thuoc, @rct_sl;
            END
            CLOSE cur_hd; DEALLOCATE cur_hd;

            -- Tính lại trạng thái đơn
            DECLARE @tong_ke2 INT, @tong_ban2 INT;
            SELECT @tong_ke2 = SUM(so_luong_ke), @tong_ban2 = SUM(so_luong_da_ban)
            FROM ChiTietDonThuoc WHERE don_thuoc_id = @gd_dt;
            UPDATE DonThuoc SET trang_thai =
                CASE WHEN @tong_ban2 = 0 THEN N'CHUA_BAN'
                     WHEN @tong_ban2 < @tong_ke2 THEN N'BAN_MOT_PHAN'
                     ELSE trang_thai END
            WHERE id = @gd_dt;
        END

        -- Cập nhật GiaoDich
        UPDATE GiaoDich
        SET trang_thai   = N'DA_HUY',
            ly_do_huy    = @ly_do,
            nguoi_huy_id = @nguoi_huy_id,
            thoi_gian_huy = SYSUTCDATETIME()
        WHERE id = @giao_dich_id;

        INSERT INTO AuditLog (nguoi_dung_id, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_moi)
        VALUES (@nguoi_huy_id, N'HUY_GIAO_DICH', N'GiaoDich', @giao_dich_id,
                N'{"ly_do":"' + REPLACE(@ly_do, N'"', N'\"') + N'"}');

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK;
        THROW;
    END CATCH;
END;
GO

-- =====================================================================
-- sp_TaoDonThuoc
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_TaoDonThuoc
    @khach_hang_id       INT,
    @nha_thuoc_id        INT,
    @ten_bac_si          NVARCHAR(100),
    @co_so_kham          NVARCHAR(200),
    @chan_doan           NVARCHAR(200) = NULL,
    @ngay_ke_don         DATE,
    @so_ngay_hieu_luc    INT           = NULL,
    @nguoi_tiep_nhan_id  INT,
    @chi_tiet_json       NVARCHAR(MAX),
    @don_thuoc_id        INT           OUTPUT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
        IF ISJSON(@chi_tiet_json) = 0
            THROW 50016, N'JSON chi tiết đơn thuốc không hợp lệ.', 1;

        -- Kiểm tra khách hàng
        IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE id = @khach_hang_id)
            THROW 50018, N'Khách hàng không tồn tại trong hệ thống.', 1;

        -- Ngày kê đơn không ở tương lai
        IF @ngay_ke_don > dbo.fn_HomNayVN()
            THROW 50008, N'Ngày kê đơn không được ở tương lai.', 1;

        -- Người tiếp nhận phải thuộc nhà thuốc
        IF dbo.fn_CoQuyenNhaThuoc(@nguoi_tiep_nhan_id, @nha_thuoc_id) = 0
            THROW 50011, N'Người tiếp nhận không thuộc nhà thuốc này.', 1;

        -- Mặc định so_ngay_hieu_luc từ CauHinh
        IF @so_ngay_hieu_luc IS NULL OR @so_ngay_hieu_luc <= 0
            SET @so_ngay_hieu_luc = dbo.fn_CauHinhInt(N'so_ngay_hieu_luc_don_mac_dinh', 5);

        -- Sinh mã đơn
        DECLARE @ma_don NVARCHAR(30);
        DECLARE @seq_d INT;
        SELECT @seq_d = ISNULL(MAX(CAST(RIGHT(ma_don, 5) AS INT)), 0) + 1
        FROM DonThuoc WHERE ma_don LIKE N'DT-' + FORMAT(@ngay_ke_don, N'yyyyMMdd') + N'-%';
        SET @ma_don = N'DT-' + FORMAT(@ngay_ke_don, N'yyyyMMdd') + N'-' + RIGHT(N'00000' + CAST(@seq_d AS NVARCHAR(5)), 5);

        INSERT INTO DonThuoc (
            ma_don, khach_hang_id, nha_thuoc_id, ten_bac_si, co_so_kham,
            chan_doan, ngay_ke_don, so_ngay_hieu_luc, trang_thai, nguoi_tiep_nhan_id
        ) VALUES (
            @ma_don, @khach_hang_id, @nha_thuoc_id, @ten_bac_si, @co_so_kham,
            @chan_doan, @ngay_ke_don, @so_ngay_hieu_luc, N'CHUA_BAN', @nguoi_tiep_nhan_id
        );
        SET @don_thuoc_id = SCOPE_IDENTITY();

        -- Chi tiết đơn
        INSERT INTO ChiTietDonThuoc (don_thuoc_id, thuoc_id, so_luong_ke, so_luong_da_ban, lieu_dung)
        SELECT
            @don_thuoc_id,
            CAST(JSON_VALUE(j.value, N'$.thuoc_id')    AS INT),
            CAST(JSON_VALUE(j.value, N'$.so_luong_ke') AS INT),
            0,
            JSON_VALUE(j.value, N'$.lieu_dung')
        FROM OPENJSON(@chi_tiet_json) j
        WHERE CAST(JSON_VALUE(j.value, N'$.so_luong_ke') AS INT) > 0;

        INSERT INTO AuditLog (nguoi_dung_id, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_moi)
        VALUES (@nguoi_tiep_nhan_id, N'TAO_DON_THUOC', N'DonThuoc', @don_thuoc_id,
                N'{"ma_don":"' + @ma_don + N'"}');
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK;
        THROW;
    END CATCH;
END;
GO

-- =====================================================================
-- sp_HuyDonThuoc
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_HuyDonThuoc
    @don_thuoc_id INT,
    @nguoi_id     INT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
        DECLARE @dt_tt NVARCHAR(15), @dt_nt INT, @dt_ma NVARCHAR(30);
        SELECT @dt_tt = trang_thai, @dt_nt = nha_thuoc_id, @dt_ma = ma_don
        FROM DonThuoc WHERE id = @don_thuoc_id;
        IF @dt_tt IS NULL THROW 50014, N'Đơn thuốc không tồn tại.', 1;
        IF @dt_tt <> N'CHUA_BAN' THROW 50009, N'Chỉ hủy được đơn chưa bán. Đơn đã có giao dịch không thể hủy.', 1;
        IF dbo.fn_CoQuyenNhaThuoc(@nguoi_id, @dt_nt) = 0
            THROW 50011, N'Không có quyền hủy đơn thuốc của nhà thuốc này.', 1;

        UPDATE DonThuoc SET trang_thai = N'HUY' WHERE id = @don_thuoc_id;

        INSERT INTO AuditLog (nguoi_dung_id, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_cu, du_lieu_moi)
        VALUES (@nguoi_id, N'HUY_DON_THUOC', N'DonThuoc', @don_thuoc_id,
                N'{"trang_thai":"CHUA_BAN"}', N'{"trang_thai":"HUY"}');
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK;
        THROW;
    END CATCH;
END;
GO

-- =====================================================================
-- sp_CapNhatDonHetHieuLuc  (set-based, idempotent – BR05)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_CapNhatDonHetHieuLuc
    @nha_thuoc_id INT = NULL
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE DonThuoc
    SET trang_thai = N'HET_HIEU_LUC'
    WHERE trang_thai IN (N'CHUA_BAN', N'BAN_MOT_PHAN')
      AND DATEADD(DAY, so_ngay_hieu_luc, ngay_ke_don) < dbo.fn_HomNayVN()
      AND (@nha_thuoc_id IS NULL OR nha_thuoc_id = @nha_thuoc_id);
END;
GO
