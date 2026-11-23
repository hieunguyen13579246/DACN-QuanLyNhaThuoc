package com.example.is009.thongke.repository;

import com.example.is009.common.ThoiGianVN;
import com.example.is009.thongke.dto.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class ThongKeRepository {

    private final JdbcTemplate jdbcTemplate;

    public ThongKeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("unchecked")
    public List<NhuCauKhuVucDTO> thongKeNhuCauKhuVuc(Integer phamViNhaThuocId, Integer khuVucId,
                                                     Integer nhomThuocId, LocalDate tuNgay, LocalDate denNgay) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_ThongKeNhuCauKhuVuc")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("tu", Types.DATE),
                        new SqlParameter("den", Types.DATE),
                        new SqlParameter("khu_vuc_id", Types.INTEGER),
                        new SqlParameter("nhom_thuoc_id", Types.INTEGER),
                        new SqlParameter("pham_vi_nha_thuoc_id", Types.INTEGER)
                )
                .returningResultSet("rows", (rs, i) -> mapNhuCauKhuVuc(rs));

        Map<String, Object> out = call.execute(new MapSqlParameterSource()
                .addValue("tu", Date.valueOf(tuNgay))
                .addValue("den", Date.valueOf(denNgay))
                .addValue("khu_vuc_id", khuVucId)
                .addValue("nhom_thuoc_id", nhomThuocId)
                .addValue("pham_vi_nha_thuoc_id", phamViNhaThuocId));
        return (List<NhuCauKhuVucDTO>) out.getOrDefault("rows", List.of());
    }

    @SuppressWarnings("unchecked")
    public List<XuHuongThangDTO> thongKeXuHuong(Integer phamViNhaThuocId, Integer khuVucId,
                                                Integer nhomThuocId, LocalDate tuNgay, LocalDate denNgay) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_ThongKeXuHuong")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("tu", Types.DATE),
                        new SqlParameter("den", Types.DATE),
                        new SqlParameter("khu_vuc_id", Types.INTEGER),
                        new SqlParameter("nhom_thuoc_id", Types.INTEGER),
                        new SqlParameter("pham_vi_nha_thuoc_id", Types.INTEGER)
                )
                .returningResultSet("rows", (rs, i) -> mapXuHuong(rs));

        Map<String, Object> out = call.execute(new MapSqlParameterSource()
                .addValue("tu", Date.valueOf(tuNgay))
                .addValue("den", Date.valueOf(denNgay))
                .addValue("khu_vuc_id", khuVucId)
                .addValue("nhom_thuoc_id", nhomThuocId)
                .addValue("pham_vi_nha_thuoc_id", phamViNhaThuocId));
        return (List<XuHuongThangDTO>) out.getOrDefault("rows", List.of());
    }

    @SuppressWarnings("unchecked")
    public List<TopThuocDTO> thongKeTopThuoc(Integer phamViNhaThuocId, Integer khuVucId,
                                             Integer nhomThuocId, LocalDate tuNgay, LocalDate denNgay, int topN) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_ThongKeTopThuoc")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("tu", Types.DATE),
                        new SqlParameter("den", Types.DATE),
                        new SqlParameter("khu_vuc_id", Types.INTEGER),
                        new SqlParameter("nhom_thuoc_id", Types.INTEGER),
                        new SqlParameter("pham_vi_nha_thuoc_id", Types.INTEGER),
                        new SqlParameter("top", Types.INTEGER)
                )
                .returningResultSet("rows", (rs, i) -> mapTopThuoc(rs));

        Map<String, Object> out = call.execute(new MapSqlParameterSource()
                .addValue("tu", Date.valueOf(tuNgay))
                .addValue("den", Date.valueOf(denNgay))
                .addValue("khu_vuc_id", khuVucId)
                .addValue("nhom_thuoc_id", nhomThuocId)
                .addValue("pham_vi_nha_thuoc_id", phamViNhaThuocId)
                .addValue("top", topN));
        return (List<TopThuocDTO>) out.getOrDefault("rows", List.of());
    }

    @SuppressWarnings("unchecked")
    public List<HieuSuatNhaThuocDTO> thongKeHieuSuatNhaThuoc(Integer phamViNhaThuocId, Integer khuVucId,
                                                             LocalDate tuNgay, LocalDate denNgay) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_ThongKeHieuSuatNhaThuoc")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("tu", Types.DATE),
                        new SqlParameter("den", Types.DATE),
                        new SqlParameter("khu_vuc_id", Types.INTEGER),
                        new SqlParameter("nhom_thuoc_id", Types.INTEGER),
                        new SqlParameter("pham_vi_nha_thuoc_id", Types.INTEGER)
                )
                .returningResultSet("rows", (rs, i) -> mapHieuSuat(rs));

        Map<String, Object> out = call.execute(new MapSqlParameterSource()
                .addValue("tu", Date.valueOf(tuNgay))
                .addValue("den", Date.valueOf(denNgay))
                .addValue("khu_vuc_id", khuVucId)
                .addValue("nhom_thuoc_id", null)
                .addValue("pham_vi_nha_thuoc_id", phamViNhaThuocId));
        return (List<HieuSuatNhaThuocDTO>) out.getOrDefault("rows", List.of());
    }

    @SuppressWarnings("unchecked")
    public List<DeXuatNhapHangDTO> deXuatNhapHang(Integer phamViNhaThuocId, Integer nhomThuocId) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_DeXuatNhapHang")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("tu", Types.DATE),
                        new SqlParameter("den", Types.DATE),
                        new SqlParameter("khu_vuc_id", Types.INTEGER),
                        new SqlParameter("nhom_thuoc_id", Types.INTEGER),
                        new SqlParameter("pham_vi_nha_thuoc_id", Types.INTEGER)
                )
                .returningResultSet("rows", (rs, i) -> mapDeXuat(rs));

        Map<String, Object> out = call.execute(new MapSqlParameterSource()
                .addValue("tu", null)
                .addValue("den", null)
                .addValue("khu_vuc_id", null)
                .addValue("nhom_thuoc_id", nhomThuocId)
                .addValue("pham_vi_nha_thuoc_id", phamViNhaThuocId));
        return (List<DeXuatNhapHangDTO>) out.getOrDefault("rows", List.of());
    }

    public DashboardNhaThuocDTO truyVanKpiNhaThuoc(Integer phamViNhaThuocId) {
        LocalDate homNay = ThoiGianVN.homNay();
        Timestamp dauNgayUtc = Timestamp.from(ThoiGianVN.sangUtc(homNay.atStartOfDay()));
        Timestamp dau30NgayUtc = Timestamp.from(ThoiGianVN.sangUtc(homNay.minusDays(29).atStartOfDay()));

        String sql = """
                SELECT
                    ISNULL((SELECT SUM(thanh_tien) FROM dbo.GiaoDich
                            WHERE trang_thai = N'HOAN_THANH' AND thoi_gian >= ?
                              AND (? IS NULL OR nha_thuoc_id = ?)), 0) AS doanh_thu_hom_nay,
                    ISNULL((SELECT COUNT(1) FROM dbo.GiaoDich
                            WHERE trang_thai = N'HOAN_THANH' AND thoi_gian >= ?
                              AND (? IS NULL OR nha_thuoc_id = ?)), 0) AS so_gd_hom_nay,
                    ISNULL((SELECT SUM(thanh_tien) FROM dbo.GiaoDich
                            WHERE trang_thai = N'HOAN_THANH' AND thoi_gian >= ?
                              AND (? IS NULL OR nha_thuoc_id = ?)), 0) AS doanh_thu_30_ngay,
                    ISNULL((SELECT COUNT(1) FROM dbo.GiaoDich
                            WHERE trang_thai = N'HOAN_THANH' AND thoi_gian >= ?
                              AND (? IS NULL OR nha_thuoc_id = ?)), 0) AS so_gd_30_ngay,
                    ISNULL((SELECT SUM(CAST(tk.so_luong AS DECIMAL(18,2)) * lt.gia_nhap)
                            FROM dbo.TonKho tk JOIN dbo.LoThuoc lt ON lt.id = tk.lo_thuoc_id
                            WHERE (? IS NULL OR tk.nha_thuoc_id = ?)), 0) AS gia_tri_ton,
                    ISNULL((SELECT COUNT(1) FROM dbo.PhieuKho
                            WHERE trang_thai = N'CHO_DUYET'
                              AND (? IS NULL OR nha_thuoc_id = ?)), 0) AS phieu_cho_duyet
                """;
        return jdbcTemplate.queryForObject(sql, (rs, i) -> {
            DashboardNhaThuocDTO d = new DashboardNhaThuocDTO();
            d.setDoanhThuHomNay(rs.getBigDecimal("doanh_thu_hom_nay"));
            d.setSoGiaoDichHomNay(rs.getInt("so_gd_hom_nay"));
            d.setDoanhThu30Ngay(rs.getBigDecimal("doanh_thu_30_ngay"));
            d.setSoGiaoDich30Ngay(rs.getInt("so_gd_30_ngay"));
            d.setTongGiaTriTonKho(rs.getBigDecimal("gia_tri_ton"));
            d.setSoPhieuChoDuyet(rs.getInt("phieu_cho_duyet"));
            return d;
        }, dauNgayUtc, phamViNhaThuocId, phamViNhaThuocId,
           dauNgayUtc, phamViNhaThuocId, phamViNhaThuocId,
           dau30NgayUtc, phamViNhaThuocId, phamViNhaThuocId,
           dau30NgayUtc, phamViNhaThuocId, phamViNhaThuocId,
           phamViNhaThuocId, phamViNhaThuocId,
           phamViNhaThuocId, phamViNhaThuocId);
    }

    public List<Map<String, Object>> truyVanAuditLog() {
        String sql = """
                SELECT TOP 150 al.id, al.nguoi_dung_id, al.hanh_dong, al.doi_tuong, al.doi_tuong_id,
                       al.du_lieu_cu, al.du_lieu_moi, al.created_at
                FROM dbo.AuditLog al
                ORDER BY al.created_at DESC, al.id DESC
                """;
        return jdbcTemplate.query(sql, (rs, i) -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", rs.getLong("id"));
            int nd = rs.getInt("nguoi_dung_id");
            m.put("nguoiDungId", rs.wasNull() ? null : nd);
            m.put("hanhDong", rs.getString("hanh_dong"));
            m.put("doiTuong", rs.getString("doi_tuong"));
            int dt = rs.getInt("doi_tuong_id");
            m.put("doiTuongId", rs.wasNull() ? null : dt);
            m.put("duLieuCu", rs.getString("du_lieu_cu"));
            m.put("duLieuMoi", rs.getString("du_lieu_moi"));
            Timestamp tg = rs.getTimestamp("created_at");
            m.put("thoiGianVN", tg != null ? ThoiGianVN.dinhDangNgayGio(ThoiGianVN.tuTimestampUtc(tg)) : "");
            return m;
        });
    }

    private NhuCauKhuVucDTO mapNhuCauKhuVuc(ResultSet rs) throws SQLException {
        NhuCauKhuVucDTO d = new NhuCauKhuVucDTO();
        d.setKhuVucId(rs.getInt("khu_vuc_id"));
        d.setMaKhuVuc(rs.getString("cap"));
        d.setTenKhuVuc(rs.getString("khu_vuc"));
        d.setDanSo(rs.getInt("dan_so"));
        d.setNhomThuocId(rs.getInt("nhom_thuoc_id"));
        d.setMaNhom(String.valueOf(rs.getInt("nhom_thuoc_id")));
        d.setTenNhom(rs.getString("nhom_thuoc"));
        d.setTongSoLuong(rs.getInt("tong_so_luong"));
        d.setTongDoanhThu(rs.getBigDecimal("tong_doanh_thu"));
        d.setSoGiaoDich(rs.getInt("so_khach"));
        BigDecimal sl1000 = rs.getBigDecimal("sl_1000_dan");
        d.setSoLuongTren1000Dan(sl1000 != null ? sl1000 : BigDecimal.ZERO);
        return d;
    }

    private XuHuongThangDTO mapXuHuong(ResultSet rs) throws SQLException {
        XuHuongThangDTO d = new XuHuongThangDTO();
        d.setNam(rs.getInt("nam"));
        d.setThang(rs.getInt("thang"));
        d.setKyThang(rs.getString("thang_str"));
        d.setSoGiaoDich(rs.getInt("tong_so_luong"));
        d.setTongSoLuong(rs.getInt("tong_so_luong"));
        d.setDoanhThu(rs.getBigDecimal("doanh_so"));
        d.setDoanhThuThangTruoc(rs.getBigDecimal("doanh_so_thang_truoc"));
        d.setTangTruongPhanTram(rs.getBigDecimal("tang_truong_pct"));
        return d;
    }

    private TopThuocDTO mapTopThuoc(ResultSet rs) throws SQLException {
        TopThuocDTO d = new TopThuocDTO();
        d.setThuocId(rs.getInt("thuoc_id"));
        d.setMaThuoc("TH-" + rs.getInt("thuoc_id"));
        d.setTenThuoc(rs.getString("ten_thuoc"));
        d.setHoatChat(rs.getString("hoat_chat"));
        d.setDonViTinh("");
        d.setTenNhom(rs.getString("khu_vuc"));
        d.setTongSoLuong(rs.getInt("tong_so_luong"));
        d.setTongDoanhThu(rs.getBigDecimal("tong_doanh_thu"));
        d.setSoGiaoDich(rs.getInt("xep_hang"));
        d.setKhuVuc(rs.getString("khu_vuc"));
        d.setXepHang(rs.getInt("xep_hang"));
        return d;
    }

    private HieuSuatNhaThuocDTO mapHieuSuat(ResultSet rs) throws SQLException {
        HieuSuatNhaThuocDTO d = new HieuSuatNhaThuocDTO();
        d.setNhaThuocId(rs.getInt("nha_thuoc_id"));
        d.setMaNhaThuoc("NT" + String.format("%03d", rs.getInt("nha_thuoc_id")));
        d.setTenNhaThuoc(rs.getString("nha_thuoc"));
        d.setTenKhuVuc(rs.getString("khu_vuc"));
        d.setSoGiaoDich(rs.getInt("so_giao_dich"));
        d.setTongDoanhThu(rs.getBigDecimal("doanh_thu"));
        BigDecimal loiNhuanPct = rs.getBigDecimal("bien_loi_nhuan_gop_pct");
        BigDecimal dt = rs.getBigDecimal("doanh_thu");
        if (dt != null && loiNhuanPct != null) {
            BigDecimal ln = dt.multiply(loiNhuanPct).divide(new BigDecimal("100"), 0, java.math.RoundingMode.HALF_UP);
            d.setLoiNhuanGop(ln);
            d.setTongGiaVon(dt.subtract(ln));
        } else {
            d.setLoiNhuanGop(BigDecimal.ZERO);
            d.setTongGiaVon(BigDecimal.ZERO);
        }
        d.setTySuatLoiNhuanPct(loiNhuanPct != null ? loiNhuanPct : BigDecimal.ZERO);
        d.setGiaTriTonKhoHienTai(rs.getBigDecimal("gia_tri_gio_tb"));
        d.setGiaTriGioTb(rs.getBigDecimal("gia_tri_gio_tb"));
        d.setTyLeBanTheoDonPct(rs.getBigDecimal("ty_le_ban_theo_don_pct"));
        d.setVongQuayTon(rs.getBigDecimal("vong_quay_ton"));
        return d;
    }

    private DeXuatNhapHangDTO mapDeXuat(ResultSet rs) throws SQLException {
        DeXuatNhapHangDTO d = new DeXuatNhapHangDTO();
        d.setNhaThuocId(rs.getInt("nha_thuoc_id"));
        d.setTenNhaThuoc(rs.getString("nha_thuoc"));
        d.setThuocId(rs.getInt("thuoc_id"));
        d.setMaThuoc("TH-" + String.format("%03d", rs.getInt("thuoc_id")));
        d.setTenThuoc(rs.getString("ten_thuoc"));
        d.setDonViTinh(rs.getString("don_vi_tinh"));
        d.setTenNhom("");
        int tonKd = rs.getInt("ton_kha_dung");
        int deXuat = rs.getInt("so_luong_de_xuat");
        d.setTonKhaDung(tonKd);
        d.setTonToiThieu(rs.getInt("so_ngay_giao_hang"));
        d.setTonToiDa(tonKd + deXuat);
        BigDecimal tbNgay = rs.getBigDecimal("ban_tb_ngay");
        d.setBanTrungBinhNgay(tbNgay != null ? tbNgay : BigDecimal.ZERO);
        d.setSoNgayGiaoHang(rs.getInt("so_ngay_giao_hang"));
        d.setSoLuongDeXuatNhap(deXuat);
        d.setGiaNhapGanNhat(new BigDecimal("15000"));
        d.setThanhTienDuKien(new BigDecimal("15000").multiply(BigDecimal.valueOf(deXuat)));
        return d;
    }
}
