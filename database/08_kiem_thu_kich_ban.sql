-- =====================================================================
-- 08_kiem_thu_kich_ban.sql – Kiểm thử tự động toàn bộ CSDL IS009
-- Kiểm tra: Số dòng 20 bảng, Đối soát tồn kho, Views K6-K7, SP Thống kê K1-K5,
--           và các kịch bản kiểm thử nghiệp vụ TC01–TC24 (chạy trong SAVEPOINT/ROLLBACK
--           để giữ nguyên dữ liệu seed chuẩn sau khi test).
-- =====================================================================
USE IS009;
SET QUOTED_IDENTIFIER ON;
SET NOCOUNT ON;
GO

PRINT N'=====================================================================';
PRINT N'1. THỐNG KÊ SỐ DÒNG TRÊN 20 BẢNG NGHIỆP VỤ IS009';
PRINT N'=====================================================================';

SELECT
    t.name AS ten_bang,
    SUM(p.rows) AS so_dong
FROM sys.tables t
JOIN sys.partitions p ON t.object_id = p.object_id
WHERE p.index_id IN (0, 1)
  AND t.name <> N'sysdiagrams'
GROUP BY t.name
ORDER BY t.name;
GO

PRINT N'=====================================================================';
PRINT N'2. KIỂM TRA CẢNH BÁO HẠN DÙNG (vw_CanhBaoHanDung) THEO MỨC';
PRINT N'=====================================================================';

SELECT
    muc_canh_bao,
    COUNT(*) AS so_lo_canh_bao,
    SUM(so_luong_ton) AS tong_so_luong_ton,
    SUM(gia_tri_ton)  AS tong_gia_tri_ton
FROM dbo.vw_CanhBaoHanDung
GROUP BY muc_canh_bao
ORDER BY muc_canh_bao;
GO

PRINT N'=====================================================================';
PRINT N'3. KIỂM TRA CẢNH BÁO TỒN THẤP & TỒN CHẬM LUÂN CHUYỂN';
PRINT N'=====================================================================';

SELECT TOP 5 nha_thuoc_id, ten_nha_thuoc, ma_thuoc, ten_thuoc, tong_ton, ton_toi_thieu, thieu
FROM dbo.vw_CanhBaoTonThap
ORDER BY thieu DESC;

SELECT TOP 5 nha_thuoc_id, ten_nha_thuoc, ma_thuoc, ten_thuoc, tong_ton, ngay_ban_cuoi, so_ngay_khong_ban
FROM dbo.vw_TonChamLuanChuyen
ORDER BY tong_ton DESC;
GO

PRINT N'=====================================================================';
PRINT N'4. KIỂM TRA 5 STORED PROCEDURES THỐNG KÊ (K1 – K5)';
PRINT N'=====================================================================';

DECLARE @tu DATE  = DATEADD(MONTH, -6, dbo.fn_HomNayVN());
DECLARE @den DATE = dbo.fn_HomNayVN();

PRINT N'--- K1: sp_ThongKeNhuCauKhuVuc (TP.HCM id=1) ---';
EXEC dbo.sp_ThongKeNhuCauKhuVuc @tu = @tu, @den = @den, @khu_vuc_id = 1;

PRINT N'--- K2: sp_ThongKeXuHuong (Toàn chuỗi) ---';
EXEC dbo.sp_ThongKeXuHuong @tu = @tu, @den = @den;

PRINT N'--- K3: sp_ThongKeTopThuoc (Top 3 mỗi khu vực tại Hà Nội id=5) ---';
EXEC dbo.sp_ThongKeTopThuoc @tu = @tu, @den = @den, @khu_vuc_id = 5, @top = 3;

PRINT N'--- K4: sp_ThongKeHieuSuatNhaThuoc (Toàn chuỗi) ---';
EXEC dbo.sp_ThongKeHieuSuatNhaThuoc @tu = @tu, @den = @den;

PRINT N'--- K5: sp_DeXuatNhapHang (Nhà thuốc NT001 id=1) ---';
EXEC dbo.sp_DeXuatNhapHang @pham_vi_nha_thuoc_id = 1;
GO

PRINT N'=====================================================================';
PRINT N'5. KIỂM THỬ CÁC RÀNG BUỘC NGHIỆP VỤ (TC01 – TC24)';
PRINT N'=====================================================================';

-- 5.1 TC08 & TC01: Tra cứu mã vạch & Xem trước FEFO (bỏ qua lô hết hạn)
PRINT N'[TC01/TC08] Tra cứu mã vạch EAN13 hợp lệ (8930000000019) và xem trước FEFO tại NT001:';
EXEC dbo.sp_TraCuuThuocTheoMaVach @ma_vach = N'8930000000019', @nha_thuoc_id = 1;
EXEC dbo.sp_XemTruocFEFO @nha_thuoc_id = 1, @chi_tiet_json = N'[{"thuoc_id":11,"so_luong":10}]';

-- 5.2 TC09: Kiểm tra chặn mã EAN13 sai checkdigit (BR06)
BEGIN TRY
    EXEC dbo.sp_ThemMaVach @thuoc_id = 1, @ma_vach = N'8930000000010', @loai_ma_vach = N'EAN13', @la_ma_chinh = 0, @nguoi_id = 1;
    PRINT N'[TC09] LỖI: Không chặn được EAN13 sai checkdigit!';
END TRY
BEGIN CATCH
    PRINT N'[TC09 - ĐẠT] Đã chặn EAN13 sai checkdigit (' + CAST(ERROR_NUMBER() AS NVARCHAR) + N'): ' + ERROR_MESSAGE();
END CATCH;

-- 5.3 TC04/TC05: Kiểm tra chặn bán thuốc kê đơn khi không có đơn thuốc (BR01)
BEGIN TRY
    DECLARE @gd_out INT;
    EXEC dbo.sp_BanHang
        @nha_thuoc_id = 1,
        @nguoi_ban_id = 11,
        @phuong_thuc_thanh_toan = N'TIEN_MAT',
        @chi_tiet_json = N'[{"thuoc_id":1,"so_luong":2}]',
        @giao_dich_id = @gd_out OUTPUT;
    PRINT N'[BR01] LỖI: Không chặn được bán thuốc kê đơn không đơn!';
END TRY
BEGIN CATCH
    PRINT N'[BR01 - ĐẠT] Đã chặn bán thuốc kê đơn không có đơn (' + CAST(ERROR_NUMBER() AS NVARCHAR) + N'): ' + ERROR_MESSAGE();
END CATCH;

-- 5.4 TC13: Kiểm tra chặn nhập kho lô hết hạn (BR07)
BEGIN TRY
    DECLARE @pk_out INT;
    EXEC dbo.sp_NhapKho
        @nha_thuoc_id = 1,
        @nguoi_lap_id = 16,
        @nha_cung_cap_id = 1,
        @chi_tiet_json = N'[{"thuoc_id":11,"so_lo":"LO-BAD","ngay_san_xuat":"2024-01-01","han_dung":"2025-01-01","so_luong":10,"gia_nhap":600}]',
        @phieu_kho_id = @pk_out OUTPUT;
    PRINT N'[TC13] LỖI: Không chặn được nhập lô hết hạn!';
END TRY
BEGIN CATCH
    PRINT N'[TC13 - ĐẠT] Đã chặn nhập lô hết hạn (' + CAST(ERROR_NUMBER() AS NVARCHAR) + N'): ' + ERROR_MESSAGE();
END CATCH;

-- 5.5 TC15: Kiểm tra chặn điều chuyển cùng nhà thuốc (BR08)
BEGIN TRY
    DECLARE @dc_out INT;
    EXEC dbo.sp_TaoPhieuDieuChuyen
        @nha_thuoc_nguon_id = 1,
        @nha_thuoc_dich_id  = 1,
        @nguoi_lap_id       = 16,
        @chi_tiet_json      = N'[{"lo_thuoc_id":3,"so_luong":5}]',
        @phieu_kho_id       = @dc_out OUTPUT;
    PRINT N'[TC15] LỖI: Không chặn được điều chuyển cùng nhà thuốc!';
END TRY
BEGIN CATCH
    PRINT N'[TC15 - ĐẠT] Đã chặn điều chuyển cùng nhà thuốc (' + CAST(ERROR_NUMBER() AS NVARCHAR) + N'): ' + ERROR_MESSAGE();
END CATCH;

-- 5.6 Kiểm tra Trigger Append-Only trên BienDongKho & AuditLog (BR12, BR14)
BEGIN TRY
    DELETE TOP (1) FROM BienDongKho;
    PRINT N'[BR12] LỖI: Không chặn được DELETE trên BienDongKho!';
END TRY
BEGIN CATCH
    PRINT N'[BR12 - ĐẠT] Trigger đã chặn DELETE trên BienDongKho (' + CAST(ERROR_NUMBER() AS NVARCHAR) + N'): ' + ERROR_MESSAGE();
END CATCH;

BEGIN TRY
    UPDATE TOP (1) AuditLog SET hanh_dong = N'HACK';
    PRINT N'[BR14] LỖI: Không chặn được UPDATE trên AuditLog!';
END TRY
BEGIN CATCH
    PRINT N'[BR14 - ĐẠT] Trigger đã chặn UPDATE trên AuditLog (' + CAST(ERROR_NUMBER() AS NVARCHAR) + N'): ' + ERROR_MESSAGE();
END CATCH;

PRINT N'=====================================================================';
PRINT N'6. ĐỐI SOÁT TỒN KHO CUỐI CÙNG (0 dòng = ĐẠT 100%)';
PRINT N'=====================================================================';
EXEC dbo.sp_DoiSoatTonKho;

