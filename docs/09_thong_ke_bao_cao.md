# 09 – Thống kê và Báo cáo

## Tham số chung cho mọi SP/View phân tích

```sql
@tu         DATE,
@den        DATE,
@khu_vuc_id INT = NULL,           -- NULL = toàn chuỗi
@nhom_thuoc_id INT = NULL,        -- NULL = tất cả nhóm
@pham_vi_nha_thuoc_id INT = NULL  -- NULL = toàn chuỗi
```

**Phân quyền tầng ứng dụng (BR13):**
- QUANTRI: truyền `@pham_vi_nha_thuoc_id = NULL`
- QUANLY: ứng dụng buộc `@pham_vi_nha_thuoc_id = nha_thuoc_id_cua_ho`

---

## K1 – Nhu cầu dược phẩm theo khu vực và nhóm thuốc

| Mục | Chi tiết |
|-----|----------|
| Mô tả | Số lượng thuốc bán ra (quy đổi/1.000 dân) theo khu vực cấp QUAN và nhóm thuốc |
| Công thức | `so_luong_ban_1000dan = SUM(ctgd.so_luong) / (kv.dan_so / 1000.0)` |
| Bảng/cột nguồn | GiaoDich(thoi_gian, nha_thuoc_id), ChiTietGiaoDich(so_luong, thuoc_id), NhaThuoc(khu_vuc_id), KhuVuc(dan_so), Thuoc(nhom_thuoc_id) |
| Bộ lọc | `gd.trang_thai = 'HOAN_THANH'`, khoảng thời gian, nhóm thuốc, khu vực |
| Stored Procedure | `sp_ThongKeNhuCauKhuVuc(@tu, @den, @khu_vuc_id, @nhom_thuoc_id, @pham_vi...)` |
| Biểu đồ | Cột nhóm (grouped bar): trục X = khu vực, trục Y = SL/1.000 dân, màu = nhóm thuốc |
| Người xem | QUANTRI |
| Quyết định | Phân bổ nhập hàng theo địa bàn; mở rộng danh mục tại khu vực nhu cầu cao |

```sql
CREATE OR ALTER PROCEDURE sp_ThongKeNhuCauKhuVuc
  @tu DATE, @den DATE,
  @khu_vuc_id INT = NULL,
  @nhom_thuoc_id INT = NULL,
  @pham_vi_nha_thuoc_id INT = NULL
AS
BEGIN
  SELECT
    kv.id           AS khu_vuc_id,
    kv.ten          AS khu_vuc,
    nt2.id          AS nhom_thuoc_id,
    nt2.ten         AS nhom_thuoc,
    SUM(ctgd.so_luong) AS tong_so_luong,
    kv.dan_so,
    CAST(SUM(ctgd.so_luong) * 1000.0 / NULLIF(kv.dan_so,0) AS DECIMAL(10,2)) AS sl_1000_dan
  FROM GiaoDich gd
  JOIN ChiTietGiaoDich ctgd ON gd.id = ctgd.giao_dich_id
  JOIN Thuoc t ON ctgd.thuoc_id = t.id
  JOIN NhomThuoc nt2 ON t.nhom_thuoc_id = nt2.id
  JOIN NhaThuoc nt ON gd.nha_thuoc_id = nt.id
  JOIN KhuVuc kv ON nt.khu_vuc_id = kv.id
  WHERE gd.trang_thai = 'HOAN_THANH'
    AND CAST(gd.thoi_gian AS DATE) BETWEEN @tu AND @den
    AND (@khu_vuc_id IS NULL OR kv.id = @khu_vuc_id OR kv.khu_vuc_cha_id = @khu_vuc_id)
    AND (@nhom_thuoc_id IS NULL OR t.nhom_thuoc_id = @nhom_thuoc_id)
    AND (@pham_vi_nha_thuoc_id IS NULL OR gd.nha_thuoc_id = @pham_vi_nha_thuoc_id)
    AND kv.cap = 'QUAN'
  GROUP BY kv.id, kv.ten, nt2.id, nt2.ten, kv.dan_so
  ORDER BY sl_1000_dan DESC;
END
```

---

## K2 – Xu hướng theo tháng + tăng trưởng

| Mục | Chi tiết |
|-----|----------|
| Mô tả | Doanh số theo tháng; tăng trưởng so tháng trước (%) |
| Công thức | `tang_truong_% = (doanh_so_thang_nay - thang_truoc) / thang_truoc * 100` |
| Bảng/cột nguồn | GiaoDich(thoi_gian, thanh_tien, nha_thuoc_id) |
| Bộ lọc | 12-24 tháng gần nhất; nhà thuốc tùy chọn |
| SP | `sp_ThongKeXuHuong(@tu, @den, @khu_vuc_id, @nhom_thuoc_id, @pham_vi...)` |
| Biểu đồ | Line chart tháng; cột cho tăng trưởng |
| Người xem | QUANTRI |
| Quyết định | Nhận diện mùa vụ; lập kế hoạch nhập hàng theo mùa |

```sql
CREATE OR ALTER PROCEDURE sp_ThongKeXuHuong
  @tu DATE, @den DATE,
  @khu_vuc_id INT = NULL,
  @nhom_thuoc_id INT = NULL,
  @pham_vi_nha_thuoc_id INT = NULL
AS
BEGIN
  WITH MonthlyData AS (
    SELECT
      FORMAT(gd.thoi_gian, 'yyyy-MM') AS thang,
      SUM(ctgd.so_luong)              AS tong_so_luong,
      SUM(gd.thanh_tien)              AS doanh_so
    FROM GiaoDich gd
    JOIN ChiTietGiaoDich ctgd ON gd.id = ctgd.giao_dich_id
    JOIN Thuoc t ON ctgd.thuoc_id = t.id
    JOIN NhaThuoc nt ON gd.nha_thuoc_id = nt.id
    JOIN KhuVuc kv ON nt.khu_vuc_id = kv.id
    WHERE gd.trang_thai = 'HOAN_THANH'
      AND CAST(gd.thoi_gian AS DATE) BETWEEN @tu AND @den
      AND (@nhom_thuoc_id IS NULL OR t.nhom_thuoc_id = @nhom_thuoc_id)
      AND (@pham_vi_nha_thuoc_id IS NULL OR gd.nha_thuoc_id = @pham_vi_nha_thuoc_id)
    GROUP BY FORMAT(gd.thoi_gian, 'yyyy-MM')
  )
  SELECT
    thang,
    doanh_so,
    tong_so_luong,
    LAG(doanh_so) OVER (ORDER BY thang) AS doanh_so_thang_truoc,
    CASE WHEN LAG(doanh_so) OVER (ORDER BY thang) > 0
         THEN CAST((doanh_so - LAG(doanh_so) OVER (ORDER BY thang))
                   * 100.0
                   / LAG(doanh_so) OVER (ORDER BY thang) AS DECIMAL(5,2))
         ELSE NULL END AS tang_truong_pct
  FROM MonthlyData
  ORDER BY thang;
END
```

---

## K3 – Top thuốc theo khu vực

| Mục | Chi tiết |
|-----|----------|
| Mô tả | Top N thuốc bán chạy tại từng khu vực |
| Công thức | `RANK() OVER (PARTITION BY khu_vuc_id ORDER BY SUM(so_luong) DESC)` |
| SP | `sp_ThongKeTopThuoc(@tu, @den, @khu_vuc_id, @nhom_thuoc_id, @top_n)` |
| Biểu đồ | Bar chart ngang; lọc theo khu vực/nhóm |
| Người xem | QUANTRI |
| Quyết định | Ưu tiên tồn kho mặt hàng chủ lực theo địa bàn |

---

## K4 – Hiệu suất nhà thuốc

| Chỉ số | Công thức | Bảng nguồn |
|--------|-----------|------------|
| Doanh thu | `SUM(GiaoDich.thanh_tien)` | GiaoDich |
| Số GD | `COUNT(GiaoDich.id)` | GiaoDich |
| Giá trị giỏ TB | `doanh_thu / so_gd` | GiaoDich |
| Tỷ lệ bán theo đơn | `COUNT(don_thuoc_id IS NOT NULL) / COUNT(*) * 100` | GiaoDich |
| Biên lợi nhuận gộp | `SUM(don_gia - gia_von) * so_luong / SUM(thanh_tien) * 100` | ChiTietGiaoDich |
| Vòng quay tồn | `so_luong_ban_ky / ton_trung_binh_ky` | BienDongKho, TonKho |

```sql
CREATE OR ALTER PROCEDURE sp_ThongKeHieuSuatNhaThuoc
  @tu DATE, @den DATE,
  @khu_vuc_id INT = NULL,
  @nhom_thuoc_id INT = NULL,
  @pham_vi_nha_thuoc_id INT = NULL
AS
BEGIN
  SELECT
    nt.id                           AS nha_thuoc_id,
    nt.ten                          AS nha_thuoc,
    kv.ten                          AS khu_vuc,
    SUM(gd.thanh_tien)              AS doanh_thu,
    COUNT(DISTINCT gd.id)           AS so_giao_dich,
    CAST(SUM(gd.thanh_tien) * 1.0 / NULLIF(COUNT(DISTINCT gd.id),0) AS DECIMAL(18,0)) AS gia_tri_gio_tb,
    CAST(COUNT(CASE WHEN gd.don_thuoc_id IS NOT NULL THEN 1 END) * 100.0
         / NULLIF(COUNT(DISTINCT gd.id),0) AS DECIMAL(5,2))            AS ty_le_ban_theo_don_pct,
    CAST(SUM((ctgd.don_gia - ctgd.gia_von) * ctgd.so_luong) * 100.0
         / NULLIF(SUM(gd.thanh_tien),0) AS DECIMAL(5,2))               AS bien_loi_nhuan_gop_pct
  FROM GiaoDich gd
  JOIN ChiTietGiaoDich ctgd ON gd.id = ctgd.giao_dich_id
  JOIN NhaThuoc nt ON gd.nha_thuoc_id = nt.id
  JOIN KhuVuc kv ON nt.khu_vuc_id = kv.id
  WHERE gd.trang_thai = 'HOAN_THANH'
    AND CAST(gd.thoi_gian AS DATE) BETWEEN @tu AND @den
    AND (@pham_vi_nha_thuoc_id IS NULL OR gd.nha_thuoc_id = @pham_vi_nha_thuoc_id)
  GROUP BY nt.id, nt.ten, kv.ten
  ORDER BY doanh_thu DESC;
END
```

---

## K5 – Đề xuất nhập hàng

| Mục | Chi tiết |
|-----|----------|
| Mô tả | Tính lượng cần nhập cho từng thuốc tại mỗi nhà thuốc |
| Công thức | `de_xuat = MAX(0, ban_tb_ngay × (so_ngay_giao_hang + so_ngay_an_toan) − ton_kha_dung)` |
| Tồn khả dụng | `SUM(TonKho.so_luong)` với `LoThuoc.han_dung > TODAY + 30` (loại lô ≤30 ngày) |
| Bán TB ngày | `SUM(ctgd.so_luong) / so_ngay_tinh_ban_trung_binh` trong `so_ngay_tinh_ban_trung_binh` ngày gần nhất |
| Cấu hình | `CauHinh`: so_ngay_an_toan=3, so_ngay_tinh_ban_trung_binh=30 |
| SP | `sp_DeXuatNhapHang(@pham_vi_nha_thuoc_id)` |
| Biểu đồ | Bảng M19 (xem wireframe 08) |
| Người xem | QUANLY (nhà thuốc mình), QUANTRI (toàn chuỗi) |
| Quyết định | Lập đơn đặt hàng NCC hoặc đề xuất điều chuyển |

```sql
CREATE OR ALTER PROCEDURE sp_DeXuatNhapHang
  @tu DATE = NULL, @den DATE = NULL,
  @khu_vuc_id INT = NULL,
  @nhom_thuoc_id INT = NULL,
  @pham_vi_nha_thuoc_id INT = NULL
AS
BEGIN
  DECLARE @so_ngay_tb    INT = (SELECT CAST(gia_tri AS INT) FROM CauHinh WHERE khoa='so_ngay_tinh_ban_trung_binh');
  DECLARE @so_ngay_an_toan INT = (SELECT CAST(gia_tri AS INT) FROM CauHinh WHERE khoa='so_ngay_an_toan');
  DECLARE @ngay_den DATE = CAST(GETDATE() AS DATE);
  DECLARE @ngay_tu  DATE = DATEADD(DAY, -@so_ngay_tb, @ngay_den);

  WITH BanTrungBinh AS (
    SELECT
      gd.nha_thuoc_id,
      ctgd.thuoc_id,
      CAST(SUM(ctgd.so_luong) * 1.0 / @so_ngay_tb AS DECIMAL(10,4)) AS ban_tb_ngay
    FROM GiaoDich gd
    JOIN ChiTietGiaoDich ctgd ON gd.id = ctgd.giao_dich_id
    WHERE gd.trang_thai = 'HOAN_THANH'
      AND CAST(gd.thoi_gian AS DATE) BETWEEN @ngay_tu AND @ngay_den
      AND (@pham_vi_nha_thuoc_id IS NULL OR gd.nha_thuoc_id = @pham_vi_nha_thuoc_id)
    GROUP BY gd.nha_thuoc_id, ctgd.thuoc_id
  ),
  TonKhaDung AS (
    SELECT
      tk.nha_thuoc_id,
      lt.thuoc_id,
      SUM(tk.so_luong) AS ton_kha_dung
    FROM TonKho tk
    JOIN LoThuoc lt ON tk.lo_thuoc_id = lt.id
    WHERE lt.han_dung > DATEADD(DAY, 30, GETDATE())
      AND (@pham_vi_nha_thuoc_id IS NULL OR tk.nha_thuoc_id = @pham_vi_nha_thuoc_id)
    GROUP BY tk.nha_thuoc_id, lt.thuoc_id
  )
  SELECT
    b.nha_thuoc_id,
    nt.ten                           AS nha_thuoc,
    b.thuoc_id,
    t.ten                            AS thuoc,
    d.so_ngay_giao_hang              AS ngay_giao_hang,
    b.ban_tb_ngay,
    ISNULL(tkd.ton_kha_dung, 0)      AS ton_kha_dung,
    CAST(CEILING(
      GREATEST(0,
        b.ban_tb_ngay * (d.so_ngay_giao_hang + @so_ngay_an_toan)
        - ISNULL(tkd.ton_kha_dung, 0)
      )
    ) AS INT)                        AS so_luong_de_xuat
  FROM BanTrungBinh b
  LEFT JOIN TonKhaDung tkd ON b.nha_thuoc_id = tkd.nha_thuoc_id AND b.thuoc_id = tkd.thuoc_id
  JOIN NhaThuoc nt ON b.nha_thuoc_id = nt.id
  JOIN Thuoc t ON b.thuoc_id = t.id
  LEFT JOIN DinhMucTon d ON d.nha_thuoc_id = b.nha_thuoc_id AND d.thuoc_id = b.thuoc_id
  WHERE CAST(CEILING(
      GREATEST(0,
        b.ban_tb_ngay * (ISNULL(d.so_ngay_giao_hang,3) + @so_ngay_an_toan)
        - ISNULL(tkd.ton_kha_dung, 0)
      )
    ) AS INT) > 0
  ORDER BY so_luong_de_xuat DESC;
END
```

---

## K6 – Cảnh báo hạn dùng

| Mục | Chi tiết |
|-----|----------|
| Mô tả | Danh sách lô theo mức cảnh báo (BR11) |
| View | `vw_CanhBaoHanDung` |
| Bộ lọc | Nhà thuốc (BR13); mức cảnh báo; nhóm thuốc |
| Biểu đồ | Bảng phân màu (đỏ/cam/vàng/xanh); có thể xuất Excel |
| Người xem | DUOCSI (màn POS), QUANLY (M13), QUANTRI (toàn chuỗi) |
| Quyết định | Tạo phiếu xuất hủy; ưu tiên bán lô gần hạn |

```sql
CREATE OR ALTER VIEW vw_CanhBaoHanDung AS
SELECT
  tk.nha_thuoc_id,
  nt.ten                            AS nha_thuoc,
  lt.id                             AS lo_thuoc_id,
  lt.so_lo,
  lt.han_dung,
  DATEDIFF(DAY, CAST(GETDATE() AS DATE), lt.han_dung) AS so_ngay_con_lai,
  t.id                              AS thuoc_id,
  t.ten                             AS ten_thuoc,
  tk.so_luong,
  CASE
    WHEN lt.han_dung < CAST(GETDATE() AS DATE)                                                THEN 'DA_HET_HAN'
    WHEN DATEDIFF(DAY, CAST(GETDATE() AS DATE), lt.han_dung) <= 30                           THEN 'MUC3_30_NGAY'
    WHEN DATEDIFF(DAY, CAST(GETDATE() AS DATE), lt.han_dung) <= 60                           THEN 'MUC2_60_NGAY'
    WHEN DATEDIFF(DAY, CAST(GETDATE() AS DATE), lt.han_dung) <= 90                           THEN 'MUC1_90_NGAY'
    ELSE NULL
  END AS muc_canh_bao
FROM TonKho tk
JOIN LoThuoc lt ON tk.lo_thuoc_id = lt.id
JOIN Thuoc t ON lt.thuoc_id = t.id
JOIN NhaThuoc nt ON tk.nha_thuoc_id = nt.id
WHERE tk.so_luong > 0
  AND DATEDIFF(DAY, CAST(GETDATE() AS DATE), lt.han_dung) <= 90;
```

---

## K7 – Tồn thấp và Tồn chậm

### K7a – Cảnh báo tồn thấp

| Mục | Chi tiết |
|-----|----------|
| Công thức | `SUM(TonKho.so_luong) < DinhMucTon.ton_toi_thieu` (theo thuốc tại nhà thuốc) |
| View | `vw_CanhBaoTonThap` |
| Người xem | QUANLY, QUANTRI |
| Quyết định | Link sang K5 Đề xuất nhập hàng |

```sql
CREATE OR ALTER VIEW vw_CanhBaoTonThap AS
SELECT
  dm.nha_thuoc_id,
  nt.ten          AS nha_thuoc,
  dm.thuoc_id,
  t.ten           AS ten_thuoc,
  dm.ton_toi_thieu,
  ISNULL(SUM(tk.so_luong), 0) AS ton_hien_tai,
  dm.ton_toi_thieu - ISNULL(SUM(tk.so_luong), 0) AS thieu
FROM DinhMucTon dm
JOIN NhaThuoc nt ON dm.nha_thuoc_id = nt.id
JOIN Thuoc t ON dm.thuoc_id = t.id
LEFT JOIN TonKho tk ON tk.nha_thuoc_id = dm.nha_thuoc_id
  LEFT JOIN LoThuoc lt ON tk.lo_thuoc_id = lt.id AND lt.thuoc_id = dm.thuoc_id
WHERE ISNULL(SUM(tk.so_luong), 0) < dm.ton_toi_thieu
GROUP BY dm.nha_thuoc_id, nt.ten, dm.thuoc_id, t.ten, dm.ton_toi_thieu;
```

### K7b – Tồn chậm luân chuyển

| Mục | Chi tiết |
|-----|----------|
| Công thức | Có tồn > 0 nhưng không có GD.HOAN_THANH nào trong `so_ngay_ton_cham` ngày gần nhất |
| View | `vw_TonChamLuanChuyen` |
| Người xem | QUANLY, QUANTRI |
| Quyết định | Xem xét điều chuyển sang nhà thuốc có nhu cầu hoặc giảm giá |

```sql
CREATE OR ALTER VIEW vw_TonChamLuanChuyen AS
WITH ThuocBan AS (
  SELECT DISTINCT gd.nha_thuoc_id, ctgd.thuoc_id
  FROM GiaoDich gd
  JOIN ChiTietGiaoDich ctgd ON gd.id = ctgd.giao_dich_id
  WHERE gd.trang_thai = 'HOAN_THANH'
    AND gd.thoi_gian >= DATEADD(DAY,
        -(SELECT CAST(gia_tri AS INT) FROM CauHinh WHERE khoa='so_ngay_ton_cham'),
        GETUTCDATE())
)
SELECT
  vt.nha_thuoc_id,
  nt.ten AS nha_thuoc,
  t.id   AS thuoc_id,
  t.ten  AS ten_thuoc,
  SUM(tk.so_luong) AS ton_hien_tai
FROM TonKho tk
JOIN LoThuoc lt ON tk.lo_thuoc_id = lt.id
JOIN Thuoc t ON lt.thuoc_id = t.id
JOIN NhaThuoc nt ON tk.nha_thuoc_id = nt.id
LEFT JOIN ThuocBan vt ON vt.nha_thuoc_id = tk.nha_thuoc_id AND vt.thuoc_id = t.id
WHERE tk.so_luong > 0
  AND lt.han_dung > CAST(GETDATE() AS DATE)
  AND vt.thuoc_id IS NULL  -- không có giao dịch trong kỳ
GROUP BY tk.nha_thuoc_id, nt.ten, t.id, t.ten
HAVING SUM(tk.so_luong) > 0;
```

---

## View hỗ trợ: vw_TonKhoTheoThuoc và vw_BanHangChiTiet

```sql
-- Tổng tồn theo (nhà thuốc, thuốc) – dùng cho K4 vòng quay tồn, K7a
CREATE OR ALTER VIEW vw_TonKhoTheoThuoc AS
SELECT tk.nha_thuoc_id, lt.thuoc_id, SUM(tk.so_luong) AS tong_ton
FROM TonKho tk JOIN LoThuoc lt ON tk.lo_thuoc_id = lt.id
GROUP BY tk.nha_thuoc_id, lt.thuoc_id;

-- Chi tiết bán hàng phẳng – nền tảng cho K1, K2, K3, K4
CREATE OR ALTER VIEW vw_BanHangChiTiet AS
SELECT
  gd.id AS giao_dich_id, gd.nha_thuoc_id, gd.thoi_gian,
  nt.khu_vuc_id, kv.ten AS khu_vuc,
  ctgd.thuoc_id, t.ten AS ten_thuoc, t.nhom_thuoc_id,
  ctgd.so_luong, ctgd.don_gia, ctgd.gia_von, ctgd.thanh_tien,
  gd.khach_hang_id, kh.khu_vuc_id AS kh_khu_vuc_id
FROM GiaoDich gd
JOIN ChiTietGiaoDich ctgd ON gd.id = ctgd.giao_dich_id
JOIN NhaThuoc nt ON gd.nha_thuoc_id = nt.id
JOIN KhuVuc kv ON nt.khu_vuc_id = kv.id
JOIN Thuoc t ON ctgd.thuoc_id = t.id
LEFT JOIN KhachHang kh ON gd.khach_hang_id = kh.id
WHERE gd.trang_thai = 'HOAN_THANH';
```

---

## Tổng hợp Chỉ số – Bảng tham chiếu

| Mã | Tên chỉ số | SP/View | Người xem | Biểu đồ |
|----|-----------|---------|-----------|---------|
| K1 | Nhu cầu theo khu vực/nhóm | sp_ThongKeNhuCauKhuVuc | QUANTRI | Bar nhóm |
| K2 | Xu hướng theo tháng | sp_ThongKeXuHuong | QUANTRI | Line + tăng trưởng |
| K3 | Top thuốc theo khu vực | sp_ThongKeTopThuoc | QUANTRI | Bar ngang |
| K4 | Hiệu suất nhà thuốc | sp_ThongKeHieuSuatNhaThuoc | QUANTRI, QUANLY | Bảng + KPI card |
| K5 | Đề xuất nhập hàng | sp_DeXuatNhapHang | QUANTRI, QUANLY | Bảng hành động |
| K6 | Cảnh báo hạn dùng | vw_CanhBaoHanDung | DS, QL, QTRI | Bảng màu |
| K7a | Cảnh báo tồn thấp | vw_CanhBaoTonThap | QUANLY, QTRI | Bảng + nút đặt hàng |
| K7b | Tồn chậm luân chuyển | vw_TonChamLuanChuyen | QUANLY, QTRI | Bảng |

> **Giả định:**
> 1. KhuVuc.dan_so được nhập thủ công ở cấp QUAN; QUANTRI chịu trách nhiệm cập nhật.
> 2. sp_ThongKeTopThuoc nhận thêm tham số @top_n INT = 10 (mặc định top 10).
> 3. Hàm GREATEST() có sẵn từ SQL Server 2022; nếu dùng SQL Server 2019 thì thay bằng CASE WHEN ... THEN 0 ELSE ... END.
> 4. vw_CanhBaoHanDung chạy real-time; cần index IX_TonKho_LoHanDung.
