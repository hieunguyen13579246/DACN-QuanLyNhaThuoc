-- =====================================================================
-- 03b_procedures_kho_donthuoc.sql  –  IS009
-- SP quản lý kho: nhập kho, điều chuyển, xuất hủy, duyệt, hủy phiếu
-- SP đối soát tồn kho
-- =====================================================================
USE IS009;
GO
SET QUOTED_IDENTIFIER ON;
GO

-- =====================================================================
-- Hàm hỗ trợ sinh mã phiếu kho
-- =====================================================================
CREATE OR ALTER FUNCTION dbo.fn_SinhMaPhieu(@loai NVARCHAR(5))
RETURNS NVARCHAR(30)
AS
BEGIN
    DECLARE @prefix NVARCHAR(5) =
        CASE @loai
            WHEN N'NHAP'         THEN N'NK'
            WHEN N'XUAT_HUY'     THEN N'XH'
            WHEN N'DIEU_CHUYEN'  THEN N'DC'
            ELSE N'PK'
        END;
    DECLARE @ngay NVARCHAR(8) = FORMAT(CAST(SWITCHOFFSET(SYSUTCDATETIME(), N'+07:00') AS DATE), N'yyyyMMdd');
    DECLARE @seq INT;
    SELECT @seq = ISNULL(MAX(CAST(RIGHT(ma_phieu, 5) AS INT)), 0) + 1
    FROM PhieuKho
    WHERE ma_phieu LIKE @prefix + N'-' + @ngay + N'-%';
    RETURN @prefix + N'-' + @ngay + N'-' + RIGHT(N'00000' + CAST(@seq AS NVARCHAR(5)), 5);
END;
GO

-- =====================================================================
-- sp_NhapKho  (BR07)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_NhapKho
    @nha_thuoc_id   INT,
    @nguoi_lap_id   INT,
    @nha_cung_cap_id INT,
    @ghi_chu        NVARCHAR(500) = NULL,
    @chi_tiet_json  NVARCHAR(MAX),
    @phieu_kho_id   INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
        IF ISJSON(@chi_tiet_json) = 0
            THROW 50016, N'JSON chi tiết phiếu nhập không hợp lệ.', 1;

        -- Cho phép THUKHO hoặc QUANLY của nhà thuốc đó, hoặc QUANTRI toàn chuỗi lập phiếu đề xuất nhập kho
        DECLARE @vt NVARCHAR(10), @nt_u INT;
        SELECT @vt = vai_tro, @nt_u = nha_thuoc_id
        FROM NguoiDung WHERE id = @nguoi_lap_id AND kich_hoat = 1;
        IF NOT (
            @vt = N'QUANTRI'
            OR (@vt IN (N'THUKHO', N'QUANLY') AND @nt_u = @nha_thuoc_id)
        )
            THROW 50011, N'Chỉ Thủ kho, Quản lý của nhà thuốc này hoặc Quản trị chuỗi mới được lập phiếu nhập kho.', 1;

        -- Parse JSON
        DECLARE @items TABLE (
            thuoc_id        INT            NOT NULL,
            so_lo           NVARCHAR(50)   NOT NULL,
            ngay_san_xuat   DATE           NULL,
            han_dung        DATE           NOT NULL,
            so_luong        INT            NOT NULL,
            gia_nhap        DECIMAL(18,0)  NOT NULL
        );
        INSERT INTO @items (thuoc_id, so_lo, ngay_san_xuat, han_dung, so_luong, gia_nhap)
        SELECT
            CAST(JSON_VALUE(j.value, N'$.thuoc_id')       AS INT),
            JSON_VALUE(j.value, N'$.so_lo'),
            TRY_CAST(JSON_VALUE(j.value, N'$.ngay_san_xuat') AS DATE),
            CAST(JSON_VALUE(j.value, N'$.han_dung')       AS DATE),
            CAST(JSON_VALUE(j.value, N'$.so_luong')       AS INT),
            CAST(JSON_VALUE(j.value, N'$.gia_nhap')       AS DECIMAL(18,0))
        FROM OPENJSON(@chi_tiet_json) j
        WHERE CAST(JSON_VALUE(j.value, N'$.so_luong') AS INT) > 0;

        IF NOT EXISTS (SELECT 1 FROM @items)
            THROW 50016, N'Không có dòng hợp lệ trong JSON nhập kho.', 1;

        -- BR07: Validate hạn dùng
        IF EXISTS (SELECT 1 FROM @items WHERE han_dung <= dbo.fn_HomNayVN())
            THROW 50008, N'Hạn dùng phải sau ngày nhập kho. Kiểm tra và sửa hạn dùng.', 1;
        IF EXISTS (SELECT 1 FROM @items WHERE ngay_san_xuat IS NOT NULL AND han_dung <= ngay_san_xuat)
            THROW 50008, N'Hạn dùng phải sau ngày sản xuất.', 1;

        BEGIN TRANSACTION;

        -- Sinh mã phiếu ở trạng thái CHO_DUYET (chưa cộng vào TonKho cho đến khi được duyệt)
        DECLARE @ma_phieu NVARCHAR(30) = dbo.fn_SinhMaPhieu(N'NHAP');
        DECLARE @tong_gt DECIMAL(18,0) =
            (SELECT SUM(so_luong * gia_nhap) FROM @items);

        INSERT INTO PhieuKho (
            ma_phieu, loai_phieu, nha_thuoc_id, nha_cung_cap_id,
            trang_thai, nguoi_lap_id, tong_gia_tri, ghi_chu
        ) VALUES (
            @ma_phieu, N'NHAP', @nha_thuoc_id, @nha_cung_cap_id,
            N'CHO_DUYET', @nguoi_lap_id, @tong_gt, @ghi_chu
        );
        SET @phieu_kho_id = SCOPE_IDENTITY();

        -- Xử lý từng dòng (chỉ tạo/tìm LoThuoc và lưu ChiTietPhieuKho, chưa cộng TonKho)
        DECLARE @i_thuoc INT, @i_lo NVARCHAR(50), @i_nsx DATE, @i_han DATE, @i_sl INT, @i_gi DECIMAL(18,0);
        DECLARE cur_nhap CURSOR FAST_FORWARD LOCAL FOR
            SELECT thuoc_id, so_lo, ngay_san_xuat, han_dung, so_luong, gia_nhap FROM @items;
        OPEN cur_nhap; FETCH NEXT FROM cur_nhap INTO @i_thuoc, @i_lo, @i_nsx, @i_han, @i_sl, @i_gi;
        WHILE @@FETCH_STATUS = 0
        BEGIN
            DECLARE @lo_id INT = NULL;
            SELECT @lo_id = id FROM LoThuoc WHERE thuoc_id = @i_thuoc AND so_lo = @i_lo;
            IF @lo_id IS NULL
            BEGIN
                INSERT INTO LoThuoc (thuoc_id, so_lo, ngay_san_xuat, han_dung, nha_cung_cap_id, gia_nhap)
                VALUES (@i_thuoc, @i_lo, @i_nsx, @i_han, @nha_cung_cap_id, @i_gi);
                SET @lo_id = SCOPE_IDENTITY();
            END

            INSERT INTO ChiTietPhieuKho (phieu_kho_id, lo_thuoc_id, so_luong, don_gia)
            VALUES (@phieu_kho_id, @lo_id, @i_sl, @i_gi);

            FETCH NEXT FROM cur_nhap INTO @i_thuoc, @i_lo, @i_nsx, @i_han, @i_sl, @i_gi;
        END
        CLOSE cur_nhap; DEALLOCATE cur_nhap;

        INSERT INTO AuditLog (nguoi_dung_id, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_moi)
        VALUES (@nguoi_lap_id, N'TAO_PHIEU_NHAP_KHO', N'PhieuKho', @phieu_kho_id,
                N'{"ma_phieu":"' + @ma_phieu + N'","trang_thai":"CHO_DUYET","tong_gia_tri":' + CAST(@tong_gt AS NVARCHAR(20)) + N'}');

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK;
        THROW;
    END CATCH;
END;
GO

-- =====================================================================
-- sp_TaoPhieuDieuChuyen  (CHO_DUYET, chưa trừ tồn)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_TaoPhieuDieuChuyen
    @nha_thuoc_nguon_id INT,
    @nha_thuoc_dich_id  INT,
    @nguoi_lap_id       INT,
    @ghi_chu            NVARCHAR(500) = NULL,
    @chi_tiet_json      NVARCHAR(MAX),
    @phieu_kho_id       INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
        IF ISJSON(@chi_tiet_json) = 0
            THROW 50016, N'JSON chi tiết điều chuyển không hợp lệ.', 1;
        IF @nha_thuoc_nguon_id = @nha_thuoc_dich_id
            THROW 50012, N'Nhà thuốc nguồn và đích phải khác nhau.', 1;

        -- Chỉ THUKHO của nhà thuốc nguồn
        DECLARE @vt NVARCHAR(10), @nt_u INT;
        SELECT @vt = vai_tro, @nt_u = nha_thuoc_id
        FROM NguoiDung WHERE id = @nguoi_lap_id AND kich_hoat = 1;
        IF @vt <> N'THUKHO' OR @nt_u <> @nha_thuoc_nguon_id
            THROW 50011, N'Chỉ THUKHO của nhà thuốc nguồn mới được tạo phiếu điều chuyển.', 1;

        -- Parse JSON [{lo_thuoc_id, so_luong}]
        DECLARE @items TABLE (lo_thuoc_id INT NOT NULL, so_luong INT NOT NULL);
        INSERT INTO @items (lo_thuoc_id, so_luong)
        SELECT
            CAST(JSON_VALUE(j.value, N'$.lo_thuoc_id') AS INT),
            CAST(JSON_VALUE(j.value, N'$.so_luong')    AS INT)
        FROM OPENJSON(@chi_tiet_json) j
        WHERE CAST(JSON_VALUE(j.value, N'$.so_luong') AS INT) > 0;

        -- Gộp dòng trùng lô
        DECLARE @items2 TABLE (lo_thuoc_id INT NOT NULL, so_luong INT NOT NULL);
        INSERT INTO @items2 SELECT lo_thuoc_id, SUM(so_luong) FROM @items GROUP BY lo_thuoc_id;

        IF NOT EXISTS (SELECT 1 FROM @items2)
            THROW 50016, N'Không có dòng hợp lệ trong JSON điều chuyển.', 1;

        -- Kiểm tra sơ bộ tồn
        IF EXISTS (
            SELECT 1 FROM @items2 i
            LEFT JOIN TonKho tk ON tk.lo_thuoc_id = i.lo_thuoc_id AND tk.nha_thuoc_id = @nha_thuoc_nguon_id
            WHERE ISNULL(tk.so_luong, 0) < i.so_luong
        )
            THROW 50002, N'Không đủ tồn kho cho một hoặc nhiều lô trong phiếu điều chuyển.', 1;

        BEGIN TRANSACTION;

        DECLARE @ma_phieu NVARCHAR(30) = dbo.fn_SinhMaPhieu(N'DIEU_CHUYEN');
        DECLARE @tong_gt DECIMAL(18,0) =
            (SELECT SUM(i.so_luong * lt.gia_nhap)
             FROM @items2 i JOIN LoThuoc lt ON lt.id = i.lo_thuoc_id);

        INSERT INTO PhieuKho (
            ma_phieu, loai_phieu, nha_thuoc_id, nha_thuoc_den_id,
            trang_thai, nguoi_lap_id, tong_gia_tri, ghi_chu
        ) VALUES (
            @ma_phieu, N'DIEU_CHUYEN', @nha_thuoc_nguon_id, @nha_thuoc_dich_id,
            N'CHO_DUYET', @nguoi_lap_id, @tong_gt, @ghi_chu
        );
        SET @phieu_kho_id = SCOPE_IDENTITY();

        INSERT INTO ChiTietPhieuKho (phieu_kho_id, lo_thuoc_id, so_luong, don_gia)
        SELECT @phieu_kho_id, i.lo_thuoc_id, i.so_luong, lt.gia_nhap
        FROM @items2 i JOIN LoThuoc lt ON lt.id = i.lo_thuoc_id;

        INSERT INTO AuditLog (nguoi_dung_id, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_moi)
        VALUES (@nguoi_lap_id, N'TAO_PHIEU_DIEU_CHUYEN', N'PhieuKho', @phieu_kho_id,
                N'{"ma_phieu":"' + @ma_phieu + N'"}');

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK;
        THROW;
    END CATCH;
END;
GO

-- =====================================================================
-- sp_TaoPhieuXuatHuy  (CHO_DUYET, chưa trừ tồn)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_TaoPhieuXuatHuy
    @nha_thuoc_id   INT,
    @nguoi_lap_id   INT,
    @ly_do          NVARCHAR(20),
    @ghi_chu        NVARCHAR(500) = NULL,
    @chi_tiet_json  NVARCHAR(MAX),
    @phieu_kho_id   INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
        IF ISJSON(@chi_tiet_json) = 0
            THROW 50016, N'JSON chi tiết xuất hủy không hợp lệ.', 1;
        IF @ly_do IS NULL OR @ly_do NOT IN (N'HET_HAN', N'HU_HONG', N'THU_HOI', N'KHAC')
            THROW 50010, N'Lý do xuất hủy không hợp lệ. Chọn: HET_HAN, HU_HONG, THU_HOI, KHAC.', 1;

        DECLARE @vt NVARCHAR(10), @nt_u INT;
        SELECT @vt = vai_tro, @nt_u = nha_thuoc_id
        FROM NguoiDung WHERE id = @nguoi_lap_id AND kich_hoat = 1;
        IF @vt <> N'THUKHO' OR @nt_u <> @nha_thuoc_id
            THROW 50011, N'Chỉ THUKHO của nhà thuốc này mới được tạo phiếu xuất hủy.', 1;

        DECLARE @items TABLE (lo_thuoc_id INT NOT NULL, so_luong INT NOT NULL);
        INSERT INTO @items (lo_thuoc_id, so_luong)
        SELECT
            CAST(JSON_VALUE(j.value, N'$.lo_thuoc_id') AS INT),
            CAST(JSON_VALUE(j.value, N'$.so_luong')    AS INT)
        FROM OPENJSON(@chi_tiet_json) j
        WHERE CAST(JSON_VALUE(j.value, N'$.so_luong') AS INT) > 0;

        DECLARE @items2 TABLE (lo_thuoc_id INT NOT NULL, so_luong INT NOT NULL);
        INSERT INTO @items2 SELECT lo_thuoc_id, SUM(so_luong) FROM @items GROUP BY lo_thuoc_id;

        IF NOT EXISTS (SELECT 1 FROM @items2)
            THROW 50016, N'Không có dòng hợp lệ trong JSON xuất hủy.', 1;

        -- Kiểm tra sơ bộ tồn
        IF EXISTS (
            SELECT 1 FROM @items2 i
            LEFT JOIN TonKho tk ON tk.lo_thuoc_id = i.lo_thuoc_id AND tk.nha_thuoc_id = @nha_thuoc_id
            WHERE ISNULL(tk.so_luong, 0) < i.so_luong
        )
            THROW 50002, N'Không đủ tồn kho cho một hoặc nhiều lô trong phiếu xuất hủy.', 1;

        BEGIN TRANSACTION;

        DECLARE @ma_phieu NVARCHAR(30) = dbo.fn_SinhMaPhieu(N'XUAT_HUY');
        DECLARE @tong_gt DECIMAL(18,0) =
            (SELECT SUM(i.so_luong * lt.gia_nhap)
             FROM @items2 i JOIN LoThuoc lt ON lt.id = i.lo_thuoc_id);

        INSERT INTO PhieuKho (
            ma_phieu, loai_phieu, nha_thuoc_id, ly_do,
            trang_thai, nguoi_lap_id, tong_gia_tri, ghi_chu
        ) VALUES (
            @ma_phieu, N'XUAT_HUY', @nha_thuoc_id, @ly_do,
            N'CHO_DUYET', @nguoi_lap_id, @tong_gt, @ghi_chu
        );
        SET @phieu_kho_id = SCOPE_IDENTITY();

        INSERT INTO ChiTietPhieuKho (phieu_kho_id, lo_thuoc_id, so_luong, don_gia)
        SELECT @phieu_kho_id, i.lo_thuoc_id, i.so_luong, lt.gia_nhap
        FROM @items2 i JOIN LoThuoc lt ON lt.id = i.lo_thuoc_id;

        INSERT INTO AuditLog (nguoi_dung_id, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_moi)
        VALUES (@nguoi_lap_id, N'TAO_PHIEU_XUAT_HUY', N'PhieuKho', @phieu_kho_id,
                N'{"ma_phieu":"' + @ma_phieu + N'","ly_do":"' + @ly_do + N'"}');

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK;
        THROW;
    END CATCH;
END;
GO

-- =====================================================================
-- sp_DuyetPhieuKho  (BR08, BR09, BR12, BR14)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_DuyetPhieuKho
    @phieu_kho_id  INT,
    @nguoi_duyet_id INT,
    @ket_qua       NVARCHAR(10),   -- 'DUYET' | 'TU_CHOI'
    @ly_do         NVARCHAR(500) = NULL
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
        IF @ket_qua NOT IN (N'DUYET', N'TU_CHOI')
            THROW 50009, N'@ket_qua phải là DUYET hoặc TU_CHOI.', 1;
        IF @ket_qua = N'TU_CHOI' AND (@ly_do IS NULL OR LEN(LTRIM(@ly_do)) = 0)
            THROW 50010, N'Từ chối phiếu phải có lý do.', 1;

        -- Thông tin phiếu
        DECLARE @loai NVARCHAR(15), @nt_nguon INT, @nt_dich INT, @tt NVARCHAR(15), @nguoi_lap INT;
        SELECT @loai = loai_phieu, @nt_nguon = nha_thuoc_id,
               @nt_dich = nha_thuoc_den_id, @tt = trang_thai, @nguoi_lap = nguoi_lap_id
        FROM PhieuKho WHERE id = @phieu_kho_id;
        IF @tt IS NULL THROW 50014, N'Phiếu kho không tồn tại.', 1;
        IF @tt <> N'CHO_DUYET' THROW 50009, N'Phiếu không ở trạng thái CHO_DUYET.', 1;

        -- Kiểm tra quyền: QUANLY nhà thuốc nguồn hoặc QUANTRI
        DECLARE @vt NVARCHAR(10), @nt_du INT;
        SELECT @vt = vai_tro, @nt_du = nha_thuoc_id
        FROM NguoiDung WHERE id = @nguoi_duyet_id AND kich_hoat = 1;
        IF NOT (@vt = N'QUANTRI' OR (@vt = N'QUANLY' AND @nt_du = @nt_nguon))
            THROW 50011, N'Chỉ QUANLY nhà thuốc nguồn hoặc QUANTRI mới được duyệt phiếu.', 1;

        IF @ket_qua = N'TU_CHOI'
        BEGIN
            UPDATE PhieuKho
            SET trang_thai = N'TU_CHOI',
                nguoi_duyet_id = @nguoi_duyet_id,
                thoi_gian_hoan_thanh = SYSUTCDATETIME(),
                ghi_chu = ISNULL(ghi_chu + N' | ', N'') + N'TỪ CHỐI: ' + @ly_do
            WHERE id = @phieu_kho_id;

            INSERT INTO AuditLog (nguoi_dung_id, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_moi)
            VALUES (@nguoi_duyet_id, N'TU_CHOI_PHIEU', N'PhieuKho', @phieu_kho_id,
                    N'{"ly_do":"' + REPLACE(@ly_do, N'"', N'\"') + N'"}');
            RETURN;
        END

        -- DUYET: khóa TonKho theo (nha_thuoc_id, lo_thuoc_id) tăng dần
        BEGIN TRANSACTION;

        -- Lấy danh sách lô cần khóa
        DECLARE @locks TABLE (nt INT, lo INT, sl INT, PRIMARY KEY (nt, lo));
        IF @loai = N'DIEU_CHUYEN'
        BEGIN
            INSERT INTO @locks (nt, lo, sl)
            SELECT @nt_nguon, ct.lo_thuoc_id, ct.so_luong
            FROM ChiTietPhieuKho ct WHERE ct.phieu_kho_id = @phieu_kho_id;
        END
        ELSE IF @loai = N'XUAT_HUY'
        BEGIN
            INSERT INTO @locks (nt, lo, sl)
            SELECT @nt_nguon, ct.lo_thuoc_id, ct.so_luong
            FROM ChiTietPhieuKho ct WHERE ct.phieu_kho_id = @phieu_kho_id;
        END

        -- Khóa TonKho theo thứ tự (nt, lo) tăng dần
        DECLARE @lk_nt INT, @lk_lo INT;
        DECLARE cur_lock CURSOR FAST_FORWARD LOCAL FOR
            SELECT nt, lo FROM @locks ORDER BY nt ASC, lo ASC;
        OPEN cur_lock; FETCH NEXT FROM cur_lock INTO @lk_nt, @lk_lo;
        WHILE @@FETCH_STATUS = 0
        BEGIN
            SELECT so_luong FROM TonKho WITH (UPDLOCK, HOLDLOCK)
            WHERE nha_thuoc_id = @lk_nt AND lo_thuoc_id = @lk_lo;
            FETCH NEXT FROM cur_lock INTO @lk_nt, @lk_lo;
        END
        CLOSE cur_lock; DEALLOCATE cur_lock;

        -- Kiểm tra lại tồn sau khi đã khóa
        IF EXISTS (
            SELECT 1 FROM @locks lk
            LEFT JOIN TonKho tk ON tk.nha_thuoc_id = lk.nt AND tk.lo_thuoc_id = lk.lo
            WHERE ISNULL(tk.so_luong, 0) < lk.sl
        )
        BEGIN ROLLBACK; THROW 50002, N'Không đủ tồn kho tại thời điểm duyệt phiếu.', 1; END

        -- Thực thi theo loại phiếu
        IF @loai = N'NHAP'
        BEGIN
            DECLARE @nk_lo INT, @nk_sl INT;
            DECLARE cur_nk CURSOR FAST_FORWARD LOCAL FOR
                SELECT lo_thuoc_id, so_luong FROM ChiTietPhieuKho WHERE phieu_kho_id = @phieu_kho_id
                ORDER BY lo_thuoc_id;
            OPEN cur_nk; FETCH NEXT FROM cur_nk INTO @nk_lo, @nk_sl;
            WHILE @@FETCH_STATUS = 0
            BEGIN
                MERGE TonKho AS tgt
                USING (SELECT @nt_nguon AS nt, @nk_lo AS lo) AS src
                ON tgt.nha_thuoc_id = src.nt AND tgt.lo_thuoc_id = src.lo
                WHEN MATCHED THEN UPDATE SET so_luong = so_luong + @nk_sl
                WHEN NOT MATCHED THEN INSERT (nha_thuoc_id, lo_thuoc_id, so_luong) VALUES (src.nt, src.lo, @nk_sl);

                DECLARE @sd_nk INT;
                SELECT @sd_nk = so_luong FROM TonKho WHERE nha_thuoc_id = @nt_nguon AND lo_thuoc_id = @nk_lo;

                INSERT INTO BienDongKho (nha_thuoc_id, lo_thuoc_id, loai_bien_dong, so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id)
                VALUES (@nt_nguon, @nk_lo, N'NHAP', @nk_sl, @sd_nk, N'PHIEU_KHO', @phieu_kho_id, @nguoi_duyet_id);

                FETCH NEXT FROM cur_nk INTO @nk_lo, @nk_sl;
            END
            CLOSE cur_nk; DEALLOCATE cur_nk;
        END
        ELSE IF @loai = N'DIEU_CHUYEN'
        BEGIN
            DECLARE @dc_lo INT, @dc_sl INT;
            DECLARE cur_dc CURSOR FAST_FORWARD LOCAL FOR
                SELECT lo_thuoc_id, so_luong FROM ChiTietPhieuKho WHERE phieu_kho_id = @phieu_kho_id
                ORDER BY lo_thuoc_id;
            OPEN cur_dc; FETCH NEXT FROM cur_dc INTO @dc_lo, @dc_sl;
            WHILE @@FETCH_STATUS = 0
            BEGIN
                -- Trừ nguồn
                UPDATE TonKho SET so_luong = so_luong - @dc_sl
                WHERE nha_thuoc_id = @nt_nguon AND lo_thuoc_id = @dc_lo;
                DECLARE @sd_xuat INT;
                SELECT @sd_xuat = so_luong FROM TonKho WHERE nha_thuoc_id = @nt_nguon AND lo_thuoc_id = @dc_lo;
                INSERT INTO BienDongKho (nha_thuoc_id, lo_thuoc_id, loai_bien_dong, so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id)
                VALUES (@nt_nguon, @dc_lo, N'DIEU_CHUYEN_XUAT', -@dc_sl, @sd_xuat, N'PHIEU_KHO', @phieu_kho_id, @nguoi_duyet_id);

                -- Cộng đích
                MERGE TonKho AS tgt
                USING (SELECT @nt_dich AS nt, @dc_lo AS lo) AS src
                ON tgt.nha_thuoc_id = src.nt AND tgt.lo_thuoc_id = src.lo
                WHEN MATCHED THEN UPDATE SET so_luong = so_luong + @dc_sl
                WHEN NOT MATCHED THEN INSERT (nha_thuoc_id, lo_thuoc_id, so_luong) VALUES (src.nt, src.lo, @dc_sl);
                DECLARE @sd_nhap_dc INT;
                SELECT @sd_nhap_dc = so_luong FROM TonKho WHERE nha_thuoc_id = @nt_dich AND lo_thuoc_id = @dc_lo;
                INSERT INTO BienDongKho (nha_thuoc_id, lo_thuoc_id, loai_bien_dong, so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id)
                VALUES (@nt_dich, @dc_lo, N'DIEU_CHUYEN_NHAP', @dc_sl, @sd_nhap_dc, N'PHIEU_KHO', @phieu_kho_id, @nguoi_duyet_id);

                FETCH NEXT FROM cur_dc INTO @dc_lo, @dc_sl;
            END
            CLOSE cur_dc; DEALLOCATE cur_dc;
        END
        ELSE IF @loai = N'XUAT_HUY'
        BEGIN
            DECLARE @xh_lo INT, @xh_sl INT;
            DECLARE cur_xh CURSOR FAST_FORWARD LOCAL FOR
                SELECT lo_thuoc_id, so_luong FROM ChiTietPhieuKho WHERE phieu_kho_id = @phieu_kho_id
                ORDER BY lo_thuoc_id;
            OPEN cur_xh; FETCH NEXT FROM cur_xh INTO @xh_lo, @xh_sl;
            WHILE @@FETCH_STATUS = 0
            BEGIN
                UPDATE TonKho SET so_luong = so_luong - @xh_sl
                WHERE nha_thuoc_id = @nt_nguon AND lo_thuoc_id = @xh_lo;
                DECLARE @sd_xh INT;
                SELECT @sd_xh = so_luong FROM TonKho WHERE nha_thuoc_id = @nt_nguon AND lo_thuoc_id = @xh_lo;
                INSERT INTO BienDongKho (nha_thuoc_id, lo_thuoc_id, loai_bien_dong, so_luong_thay_doi, so_du_sau, chung_tu_loai, chung_tu_id, nguoi_thuc_hien_id)
                VALUES (@nt_nguon, @xh_lo, N'XUAT_HUY', -@xh_sl, @sd_xh, N'PHIEU_KHO', @phieu_kho_id, @nguoi_duyet_id);
                FETCH NEXT FROM cur_xh INTO @xh_lo, @xh_sl;
            END
            CLOSE cur_xh; DEALLOCATE cur_xh;
        END

        UPDATE PhieuKho
        SET trang_thai = N'HOAN_THANH',
            nguoi_duyet_id = @nguoi_duyet_id,
            thoi_gian_hoan_thanh = SYSUTCDATETIME()
        WHERE id = @phieu_kho_id;

        INSERT INTO AuditLog (nguoi_dung_id, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_moi)
        VALUES (@nguoi_duyet_id, N'DUYET_PHIEU', N'PhieuKho', @phieu_kho_id,
                N'{"loai":"' + @loai + N'","trang_thai":"HOAN_THANH"}');

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK;
        THROW;
    END CATCH;
END;
GO

-- =====================================================================
-- sp_HuyPhieuKho  (người lập, CHO_DUYET → DA_HUY)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_HuyPhieuKho
    @phieu_kho_id INT,
    @nguoi_id     INT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
        DECLARE @tt NVARCHAR(15), @nguoi_lap INT, @ma_phieu NVARCHAR(30);
        SELECT @tt = trang_thai, @nguoi_lap = nguoi_lap_id, @ma_phieu = ma_phieu
        FROM PhieuKho WHERE id = @phieu_kho_id;
        IF @tt IS NULL THROW 50014, N'Phiếu kho không tồn tại.', 1;
        IF @tt <> N'CHO_DUYET' THROW 50009, N'Chỉ hủy được phiếu đang CHO_DUYET.', 1;
        IF @nguoi_lap <> @nguoi_id THROW 50011, N'Chỉ người lập phiếu mới được hủy.', 1;

        UPDATE PhieuKho SET trang_thai = N'DA_HUY' WHERE id = @phieu_kho_id;

        INSERT INTO AuditLog (nguoi_dung_id, hanh_dong, doi_tuong, doi_tuong_id, du_lieu_moi)
        VALUES (@nguoi_id, N'HUY_PHIEU', N'PhieuKho', @phieu_kho_id,
                N'{"ma_phieu":"' + @ma_phieu + N'","trang_thai_moi":"DA_HUY"}');
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK;
        THROW;
    END CATCH;
END;
GO

-- =====================================================================
-- sp_DoiSoatTonKho  (BR12)
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_DoiSoatTonKho
    @nha_thuoc_id INT = NULL
AS
BEGIN
    SET NOCOUNT ON;
    -- Trả các dòng (nha_thuoc_id, lo_thuoc_id) mà TonKho.so_luong ≠ tổng BienDongKho
    SELECT
        tk.nha_thuoc_id,
        nt.ten              AS ten_nha_thuoc,
        tk.lo_thuoc_id,
        lt.so_lo,
        t.ten               AS ten_thuoc,
        tk.so_luong         AS ton_kho_hien_tai,
        ISNULL(bdk.tong_bd, 0) AS tong_bien_dong,
        tk.so_luong - ISNULL(bdk.tong_bd, 0) AS chenh_lech
    FROM TonKho tk
    JOIN LoThuoc lt ON lt.id = tk.lo_thuoc_id
    JOIN Thuoc t    ON t.id  = lt.thuoc_id
    JOIN NhaThuoc nt ON nt.id = tk.nha_thuoc_id
    LEFT JOIN (
        SELECT nha_thuoc_id, lo_thuoc_id, SUM(so_luong_thay_doi) AS tong_bd
        FROM BienDongKho
        GROUP BY nha_thuoc_id, lo_thuoc_id
    ) bdk ON bdk.nha_thuoc_id = tk.nha_thuoc_id AND bdk.lo_thuoc_id = tk.lo_thuoc_id
    WHERE (@nha_thuoc_id IS NULL OR tk.nha_thuoc_id = @nha_thuoc_id)
      AND tk.so_luong <> ISNULL(bdk.tong_bd, 0)
    ORDER BY tk.nha_thuoc_id, tk.lo_thuoc_id;
END;
GO
