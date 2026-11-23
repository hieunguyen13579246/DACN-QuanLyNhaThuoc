package com.example.is009.banhang.repository;

import com.example.is009.banhang.dto.ChiTietGiaoDichDTO;
import com.example.is009.banhang.dto.GiaoDichResponse;
import com.example.is009.banhang.dto.LoPhanBoDTO;
import com.example.is009.common.SoDienThoaiUtils;
import com.example.is009.common.ThoiGianVN;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SpBanHangCaller {

    private final JdbcTemplate jdbcTemplate;

    public SpBanHangCaller(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("unchecked")
    public List<LoPhanBoDTO> xemTruocFEFO(Integer nhaThuocId, String chiTietJson) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_XemTruocFEFO")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("nha_thuoc_id", Types.INTEGER),
                        new SqlParameter("chi_tiet_json", Types.NVARCHAR)
                )
                .returningResultSet("rows", (rs, rowNum) -> mapLoPhanBo(rs));

        Map<String, Object> out = call.execute(new MapSqlParameterSource()
                .addValue("nha_thuoc_id", nhaThuocId)
                .addValue("chi_tiet_json", chiTietJson));
        return (List<LoPhanBoDTO>) out.getOrDefault("rows", List.of());
    }

    public Integer banHang(Integer nhaThuocId, Integer nguoiBanId, Integer khachHangId,
                           Integer donThuocId, String phuongThuc, BigDecimal giamGia, String chiTietJson) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_BanHang")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("nha_thuoc_id", Types.INTEGER),
                        new SqlParameter("nguoi_ban_id", Types.INTEGER),
                        new SqlParameter("khach_hang_id", Types.INTEGER),
                        new SqlParameter("don_thuoc_id", Types.INTEGER),
                        new SqlParameter("phuong_thuc_thanh_toan", Types.VARCHAR),
                        new SqlParameter("giam_gia", Types.DECIMAL),
                        new SqlParameter("chi_tiet_json", Types.NVARCHAR),
                        new SqlOutParameter("giao_dich_id", Types.INTEGER)
                );

        Map<String, Object> out = call.execute(new MapSqlParameterSource()
                .addValue("nha_thuoc_id", nhaThuocId)
                .addValue("nguoi_ban_id", nguoiBanId)
                .addValue("khach_hang_id", khachHangId)
                .addValue("don_thuoc_id", donThuocId)
                .addValue("phuong_thuc_thanh_toan", phuongThuc)
                .addValue("giam_gia", giamGia != null ? giamGia : BigDecimal.ZERO)
                .addValue("chi_tiet_json", chiTietJson));
        Number id = (Number) out.get("giao_dich_id");
        return id != null ? id.intValue() : null;
    }

    public void huyGiaoDich(Integer giaoDichId, Integer nguoiHuyId, String lyDo) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_HuyGiaoDich")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("giao_dich_id", Types.INTEGER),
                        new SqlParameter("nguoi_huy_id", Types.INTEGER),
                        new SqlParameter("ly_do", Types.NVARCHAR)
                );

        call.execute(new MapSqlParameterSource()
                .addValue("giao_dich_id", giaoDichId)
                .addValue("nguoi_huy_id", nguoiHuyId)
                .addValue("ly_do", lyDo));
    }

    public List<GiaoDichResponse> timKiemGiaoDich(Integer phamViNhaThuocId, LocalDate tuNgay,
                                                  LocalDate denNgay, String trangThai, String tuKhoa) {
        StringBuilder sql = new StringBuilder("""
                SELECT TOP 200 gd.id, gd.ma_giao_dich, gd.nha_thuoc_id, nt.ten AS ten_nha_thuoc, nt.dia_chi,
                       gd.nguoi_ban_id, nb.ho_ten AS ten_nguoi_ban,
                       gd.khach_hang_id, kh.ho_ten AS ten_khach_hang, kh.so_dien_thoai AS sdt_khach_hang,
                       gd.don_thuoc_id, dt.ma_don AS ma_don_thuoc,
                       gd.thoi_gian, gd.tong_tien AS tong_tien_hang, gd.giam_gia, gd.thanh_tien AS thanh_toan,
                       gd.phuong_thuc_thanh_toan, gd.trang_thai,
                       nh.ho_ten AS ten_nguoi_huy, gd.thoi_gian_huy, gd.ly_do_huy
                FROM dbo.GiaoDich gd
                JOIN dbo.NhaThuoc nt ON nt.id = gd.nha_thuoc_id
                JOIN dbo.NguoiDung nb ON nb.id = gd.nguoi_ban_id
                LEFT JOIN dbo.KhachHang kh ON kh.id = gd.khach_hang_id
                LEFT JOIN dbo.DonThuoc dt ON dt.id = gd.don_thuoc_id
                LEFT JOIN dbo.NguoiDung nh ON nh.id = gd.nguoi_huy_id
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();
        if (phamViNhaThuocId != null) {
            sql.append(" AND gd.nha_thuoc_id = ?");
            params.add(phamViNhaThuocId);
        }
        if (tuNgay != null) {
            sql.append(" AND gd.thoi_gian >= ?");
            params.add(Timestamp.from(ThoiGianVN.sangUtc(tuNgay.atStartOfDay())));
        }
        if (denNgay != null) {
            sql.append(" AND gd.thoi_gian < ?");
            params.add(Timestamp.from(ThoiGianVN.sangUtc(denNgay.plusDays(1).atStartOfDay())));
        }
        if (trangThai != null && !trangThai.isBlank()) {
            sql.append(" AND gd.trang_thai = ?");
            params.add(trangThai.trim());
        }
        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append(" AND (gd.ma_giao_dich LIKE ? OR kh.ho_ten LIKE ? OR dt.ma_don LIKE ?)");
            String kw = "%" + tuKhoa.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        sql.append(" ORDER BY gd.thoi_gian DESC, gd.id DESC");
        return jdbcTemplate.query(sql.toString(), (rs, i) -> mapGiaoDichHeader(rs, true), params.toArray());
    }

    public Optional<GiaoDichResponse> layChiTietGiaoDich(Integer giaoDichId) {
        String sqlHeader = """
                SELECT gd.id, gd.ma_giao_dich, gd.nha_thuoc_id, nt.ten AS ten_nha_thuoc, nt.dia_chi,
                       gd.nguoi_ban_id, nb.ho_ten AS ten_nguoi_ban,
                       gd.khach_hang_id, kh.ho_ten AS ten_khach_hang, kh.so_dien_thoai AS sdt_khach_hang,
                       gd.don_thuoc_id, dt.ma_don AS ma_don_thuoc,
                       gd.thoi_gian, gd.tong_tien AS tong_tien_hang, gd.giam_gia, gd.thanh_tien AS thanh_toan,
                       gd.phuong_thuc_thanh_toan, gd.trang_thai,
                       nh.ho_ten AS ten_nguoi_huy, gd.thoi_gian_huy, gd.ly_do_huy
                FROM dbo.GiaoDich gd
                JOIN dbo.NhaThuoc nt ON nt.id = gd.nha_thuoc_id
                JOIN dbo.NguoiDung nb ON nb.id = gd.nguoi_ban_id
                LEFT JOIN dbo.KhachHang kh ON kh.id = gd.khach_hang_id
                LEFT JOIN dbo.DonThuoc dt ON dt.id = gd.don_thuoc_id
                LEFT JOIN dbo.NguoiDung nh ON nh.id = gd.nguoi_huy_id
                WHERE gd.id = ?
                """;
        List<GiaoDichResponse> list = jdbcTemplate.query(sqlHeader, (rs, i) -> mapGiaoDichHeader(rs, false), giaoDichId);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        GiaoDichResponse resp = list.get(0);
        String sqlLines = """
                SELECT ct.id, ct.thuoc_id, t.ma AS ma_thuoc, t.ten AS ten_thuoc, t.don_vi_tinh,
                       ct.lo_thuoc_id, lt.so_lo, lt.han_dung,
                       ct.so_luong, ct.don_gia AS gia_ban, ct.thanh_tien
                FROM dbo.ChiTietGiaoDich ct
                JOIN dbo.Thuoc t ON t.id = ct.thuoc_id
                JOIN dbo.LoThuoc lt ON lt.id = ct.lo_thuoc_id
                WHERE ct.giao_dich_id = ?
                ORDER BY ct.id
                """;
        List<ChiTietGiaoDichDTO> lines = jdbcTemplate.query(sqlLines, (rs, i) -> mapChiTietLine(rs), giaoDichId);
        resp.setChiTiet(lines);
        return Optional.of(resp);
    }

    private LoPhanBoDTO mapLoPhanBo(ResultSet rs) throws SQLException {
        LoPhanBoDTO dto = new LoPhanBoDTO();
        dto.setThuocId(rs.getInt("thuoc_id"));
        dto.setLoThuocId(rs.getInt("lo_thuoc_id"));
        dto.setSoLo(rs.getString("so_lo"));
        Date hd = rs.getDate("han_dung");
        if (hd != null) {
            LocalDate hanDung = hd.toLocalDate();
            dto.setHanDung(hanDung);
            dto.setSoNgayConLai(ChronoUnit.DAYS.between(ThoiGianVN.homNay(), hanDung));
        }
        dto.setSoLuong(rs.getInt("so_luong"));
        dto.setDonGia(rs.getBigDecimal("don_gia"));
        return dto;
    }

    private GiaoDichResponse mapGiaoDichHeader(ResultSet rs, boolean cheSdt) throws SQLException {
        GiaoDichResponse r = new GiaoDichResponse();
        r.setId(rs.getInt("id"));
        r.setMaGiaoDich(rs.getString("ma_giao_dich"));
        r.setNhaThuocId(rs.getInt("nha_thuoc_id"));
        r.setTenNhaThuoc(rs.getString("ten_nha_thuoc"));
        r.setDiaChiNhaThuoc(rs.getString("dia_chi"));
        r.setNguoiBanId(rs.getInt("nguoi_ban_id"));
        r.setTenNguoiBan(rs.getString("ten_nguoi_ban"));
        int khId = rs.getInt("khach_hang_id");
        r.setKhachHangId(rs.wasNull() ? null : khId);
        r.setTenKhachHang(rs.getString("ten_khach_hang"));
        String sdt = rs.getString("sdt_khach_hang");
        r.setSdtKhachHang(cheSdt ? SoDienThoaiUtils.cheSoDienThoai(sdt) : sdt);
        int dtId = rs.getInt("don_thuoc_id");
        r.setDonThuocId(rs.wasNull() ? null : dtId);
        r.setMaDonThuoc(rs.getString("ma_don_thuoc"));
        Timestamp tg = rs.getTimestamp("thoi_gian");
        r.setThoiGian(tg != null ? ThoiGianVN.dinhDangNgayGio(ThoiGianVN.tuTimestampUtc(tg)) : "");
        r.setTongTienHang(rs.getBigDecimal("tong_tien_hang"));
        r.setGiamGia(rs.getBigDecimal("giam_gia"));
        r.setThanhToan(rs.getBigDecimal("thanh_toan"));
        r.setPhuongThucThanhToan(rs.getString("phuong_thuc_thanh_toan"));
        r.setTrangThai(rs.getString("trang_thai"));
        r.setTenNguoiHuy(rs.getString("ten_nguoi_huy"));
        Timestamp tgHuy = rs.getTimestamp("thoi_gian_huy");
        r.setThoiGianHuy(tgHuy != null ? ThoiGianVN.dinhDangNgayGio(ThoiGianVN.tuTimestampUtc(tgHuy)) : null);
        r.setLyDoHuy(rs.getString("ly_do_huy"));
        return r;
    }

    private ChiTietGiaoDichDTO mapChiTietLine(ResultSet rs) throws SQLException {
        ChiTietGiaoDichDTO c = new ChiTietGiaoDichDTO();
        c.setId(rs.getInt("id"));
        c.setThuocId(rs.getInt("thuoc_id"));
        c.setMaThuoc(rs.getString("ma_thuoc"));
        c.setTenThuoc(rs.getString("ten_thuoc"));
        c.setDonViTinh(rs.getString("don_vi_tinh"));
        c.setLoThuocId(rs.getInt("lo_thuoc_id"));
        c.setSoLo(rs.getString("so_lo"));
        Date hd = rs.getDate("han_dung");
        c.setHanDung(hd != null ? hd.toLocalDate() : null);
        c.setSoLuong(rs.getInt("so_luong"));
        c.setGiaBan(rs.getBigDecimal("gia_ban"));
        c.setThanhTien(rs.getBigDecimal("thanh_tien"));
        return c;
    }
}
