-- =============================================================
-- 07_security.sql  –  IS009
-- SQL Login is009_app + DB User + quyền tối thiểu
-- =============================================================
USE master;
GO

-- Tạo SQL Login (bỏ qua nếu đã tồn tại)
IF NOT EXISTS (SELECT 1 FROM sys.server_principals WHERE name = N'is009_app')
BEGIN
    CREATE LOGIN is009_app
        WITH PASSWORD = N'Is009@AppPass2026!',
             DEFAULT_DATABASE = IS009,
             CHECK_EXPIRATION = OFF,
             CHECK_POLICY = ON;
    PRINT N'Login is009_app đã tạo.';
END
ELSE PRINT N'Login is009_app đã tồn tại, bỏ qua.';
GO

USE IS009;
GO

-- Tạo DB User
IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = N'is009_app')
BEGIN
    CREATE USER is009_app FOR LOGIN is009_app;
    PRINT N'User is009_app đã tạo.';
END
GO

-- Quyền: SELECT, INSERT, UPDATE trên các bảng nghiệp vụ
-- Không có DELETE (append-only logs), không ALTER
GRANT SELECT, INSERT, UPDATE ON dbo.KhuVuc        TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.NhaThuoc      TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.NguoiDung     TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.NhomThuoc     TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.NhaCungCap    TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.Thuoc         TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.MaVachThuoc   TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.LoThuoc       TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.TonKho        TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.DinhMucTon    TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.PhieuKho      TO is009_app;
GRANT SELECT, INSERT ON dbo.ChiTietPhieuKho       TO is009_app;
GRANT SELECT, INSERT ON dbo.BienDongKho           TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.KhachHang     TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.DonThuoc      TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.ChiTietDonThuoc TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.GiaoDich      TO is009_app;
GRANT SELECT, INSERT ON dbo.ChiTietGiaoDich       TO is009_app;
GRANT SELECT, INSERT, UPDATE ON dbo.CauHinh       TO is009_app;
GRANT SELECT, INSERT ON dbo.AuditLog              TO is009_app;

-- Quyền EXECUTE trên tất cả stored procedures
GRANT EXECUTE ON dbo.sp_ThemMaVach                TO is009_app;
GRANT EXECUTE ON dbo.sp_TraCuuThuocTheoMaVach     TO is009_app;
GRANT EXECUTE ON dbo.sp_XemTruocFEFO              TO is009_app;
GRANT EXECUTE ON dbo.sp_BanHang                   TO is009_app;
GRANT EXECUTE ON dbo.sp_HuyGiaoDich               TO is009_app;
GRANT EXECUTE ON dbo.sp_TaoDonThuoc               TO is009_app;
GRANT EXECUTE ON dbo.sp_HuyDonThuoc               TO is009_app;
GRANT EXECUTE ON dbo.sp_CapNhatDonHetHieuLuc      TO is009_app;
GRANT EXECUTE ON dbo.sp_NhapKho                   TO is009_app;
GRANT EXECUTE ON dbo.sp_TaoPhieuDieuChuyen        TO is009_app;
GRANT EXECUTE ON dbo.sp_TaoPhieuXuatHuy           TO is009_app;
GRANT EXECUTE ON dbo.sp_DuyetPhieuKho             TO is009_app;
GRANT EXECUTE ON dbo.sp_HuyPhieuKho               TO is009_app;
GRANT EXECUTE ON dbo.sp_DoiSoatTonKho             TO is009_app;
GRANT EXECUTE ON dbo.sp_ThongKeNhuCauKhuVuc       TO is009_app;
GRANT EXECUTE ON dbo.sp_ThongKeXuHuong            TO is009_app;
GRANT EXECUTE ON dbo.sp_ThongKeTopThuoc           TO is009_app;
GRANT EXECUTE ON dbo.sp_ThongKeHieuSuatNhaThuoc   TO is009_app;
GRANT EXECUTE ON dbo.sp_DeXuatNhapHang            TO is009_app;
GRANT EXECUTE ON dbo.sp_KiemTraThamSoThoiGian     TO is009_app;

-- Quyền SELECT trên views
GRANT SELECT ON dbo.vw_TonKhoTheoThuoc       TO is009_app;
GRANT SELECT ON dbo.vw_CanhBaoHanDung        TO is009_app;
GRANT SELECT ON dbo.vw_CanhBaoTonThap        TO is009_app;
GRANT SELECT ON dbo.vw_TonChamLuanChuyen     TO is009_app;
GRANT SELECT ON dbo.vw_BanHangChiTiet        TO is009_app;

PRINT N'07_security.sql hoàn thành.';
GO
