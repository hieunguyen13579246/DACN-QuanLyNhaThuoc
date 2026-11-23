-- =====================================================================
-- 04_triggers.sql  –  IS009
-- Triggers bảo vệ append-only và cập nhật timestamp
-- =====================================================================
USE IS009;
GO
SET QUOTED_IDENTIFIER ON;
GO

-- =====================================================================
-- BienDongKho: chỉ thêm – cấm UPDATE và DELETE
-- =====================================================================
CREATE OR ALTER TRIGGER trg_BienDongKho_ChiThem_Upd
ON BienDongKho
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    ROLLBACK TRANSACTION;
    THROW 50099, N'BienDongKho là bảng append-only. Không được phép UPDATE.', 1;
END;
GO

CREATE OR ALTER TRIGGER trg_BienDongKho_ChiThem_Del
ON BienDongKho
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    ROLLBACK TRANSACTION;
    THROW 50099, N'BienDongKho là bảng append-only. Không được phép DELETE.', 1;
END;
GO

-- =====================================================================
-- AuditLog: chỉ thêm – cấm UPDATE và DELETE
-- =====================================================================
CREATE OR ALTER TRIGGER trg_AuditLog_ChiThem_Upd
ON AuditLog
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    ROLLBACK TRANSACTION;
    THROW 50099, N'AuditLog là bảng append-only. Không được phép UPDATE.', 1;
END;
GO

CREATE OR ALTER TRIGGER trg_AuditLog_ChiThem_Del
ON AuditLog
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;
    ROLLBACK TRANSACTION;
    THROW 50099, N'AuditLog là bảng append-only. Không được phép DELETE.', 1;
END;
GO

-- =====================================================================
-- TonKho: cập nhật cap_nhat_luc khi INSERT hoặc UPDATE
-- =====================================================================
CREATE OR ALTER TRIGGER trg_TonKho_CapNhatLuc
ON TonKho
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF TRIGGER_NESTLEVEL() > 1 RETURN;
    -- Chỉ cập nhật đúng các cặp (nha_thuoc_id, lo_thuoc_id) vừa thay đổi
    UPDATE tk
    SET cap_nhat_luc = SYSUTCDATETIME()
    FROM TonKho tk
    INNER JOIN inserted i
        ON tk.nha_thuoc_id = i.nha_thuoc_id
       AND tk.lo_thuoc_id  = i.lo_thuoc_id;
END;
GO
