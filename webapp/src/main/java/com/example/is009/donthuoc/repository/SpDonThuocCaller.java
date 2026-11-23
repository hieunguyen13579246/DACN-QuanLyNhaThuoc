package com.example.is009.donthuoc.repository;

import com.example.is009.donthuoc.dto.ChiTietDonThuocDTO;
import com.example.is009.donthuoc.dto.DonThuocDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SpDonThuocCaller {

    private final JdbcTemplate jdbcTemplate;

    public SpDonThuocCaller(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void goiSpCapNhatDonHetHieuLuc(Integer nhaThuocId) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_CapNhatDonHetHieuLuc")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("nha_thuoc_id", Types.INTEGER));

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nha_thuoc_id", nhaThuocId);
        call.execute(params);
    }

    public int goiSpTaoDonThuoc(int khachHangId, int nhaThuocId, String tenBacSi, String coSoKham,
                                String chanDoan, LocalDate ngayKeDon, Integer soNgayHieuLuc,
                                int nguoiTiepNhanId, String chiTietJson) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_TaoDonThuoc")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("khach_hang_id", Types.INTEGER),
                        new SqlParameter("nha_thuoc_id", Types.INTEGER),
                        new SqlParameter("ten_bac_si", Types.NVARCHAR),
                        new SqlParameter("co_so_kham", Types.NVARCHAR),
                        new SqlParameter("chan_doan", Types.NVARCHAR),
                        new SqlParameter("ngay_ke_don", Types.DATE),
                        new SqlParameter("so_ngay_hieu_luc", Types.INTEGER),
                        new SqlParameter("nguoi_tiep_nhan_id", Types.INTEGER),
                        new SqlParameter("chi_tiet_json", Types.NVARCHAR),
                        new SqlOutParameter("don_thuoc_id", Types.INTEGER)
                );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("khach_hang_id", khachHangId)
                .addValue("nha_thuoc_id", nhaThuocId)
                .addValue("ten_bac_si", tenBacSi)
                .addValue("co_so_kham", coSoKham)
                .addValue("chan_doan", chanDoan)
                .addValue("ngay_ke_don", Date.valueOf(ngayKeDon))
                .addValue("so_ngay_hieu_luc", soNgayHieuLuc)
                .addValue("nguoi_tiep_nhan_id", nguoiTiepNhanId)
                .addValue("chi_tiet_json", chiTietJson);

        Map<String, Object> out = call.execute(params);
        Number outId = (Number) out.get("don_thuoc_id");
        return outId != null ? outId.intValue() : 0;
    }

    public void goiSpHuyDonThuoc(int donThuocId, int nguoiId) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_HuyDonThuoc")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("don_thuoc_id", Types.INTEGER),
                        new SqlParameter("nguoi_id", Types.INTEGER)
                );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("don_thuoc_id", donThuocId)
                .addValue("nguoi_id", nguoiId);
        call.execute(params);
    }

    public List<DonThuocDTO> truyVanDanhSachDon(Integer phamViNhaThuocId, String tuKhoa, String trangThai) {
        StringBuilder sql = new StringBuilder("""
                SELECT TOP 100
                    dt.id, dt.ma_don, dt.khach_hang_id, kh.ho_ten AS ten_khach_hang,
                    kh.so_dien_thoai AS sdt_khach, dt.nha_thuoc_id, nt.ten AS ten_nha_thuoc,
                    dt.ten_bac_si, dt.co_so_kham, dt.chan_doan, dt.ngay_ke_don,
                    dt.so_ngay_hieu_luc, dt.trang_thai
                FROM DonThuoc dt
                JOIN KhachHang kh ON kh.id = dt.khach_hang_id
                JOIN NhaThuoc nt ON nt.id = dt.nha_thuoc_id
                WHERE 1=1
                """);
        List<Object> args = new ArrayList<>();
        if (phamViNhaThuocId != null) {
            sql.append(" AND dt.nha_thuoc_id = ?");
            args.add(phamViNhaThuocId);
        }
        if (trangThai != null && !trangThai.isBlank()) {
            sql.append(" AND dt.trang_thai = ?");
            args.add(trangThai.trim());
        }
        if (tuKhoa != null && !tuKhoa.isBlank()) {
            String kw = "%" + tuKhoa.trim() + "%";
            sql.append(" AND (dt.ma_don LIKE ? OR kh.ho_ten LIKE ? OR kh.so_dien_thoai LIKE ? OR dt.ten_bac_si LIKE ?)");
            args.add(kw);
            args.add(kw);
            args.add(kw);
            args.add(kw);
        }
        sql.append(" ORDER BY dt.id DESC");

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            int id = rs.getInt("id");
            LocalDate ngayKe = rs.getDate("ngay_ke_don").toLocalDate();
            int soNgay = rs.getInt("so_ngay_hieu_luc");
            String tt = rs.getString("trang_thai");
            return new DonThuocDTO(
                    id,
                    rs.getString("ma_don"),
                    rs.getInt("khach_hang_id"),
                    rs.getString("ten_khach_hang"),
                    rs.getString("sdt_khach"),
                    rs.getInt("nha_thuoc_id"),
                    rs.getString("ten_nha_thuoc"),
                    rs.getString("ten_bac_si"),
                    rs.getString("co_so_kham"),
                    rs.getString("chan_doan"),
                    ngayKe,
                    soNgay,
                    ngayKe.plusDays(soNgay),
                    tt,
                    "CHUA_BAN".equals(tt),
                    truyVanChiTietDon(id)
            );
        }, args.toArray());
    }

    public Optional<DonThuocDTO> truyVanDonTheoId(int donThuocId) {
        String sql = """
                SELECT dt.id, dt.ma_don, dt.khach_hang_id, kh.ho_ten AS ten_khach_hang,
                       kh.so_dien_thoai AS sdt_khach, dt.nha_thuoc_id, nt.ten AS ten_nha_thuoc,
                       dt.ten_bac_si, dt.co_so_kham, dt.chan_doan, dt.ngay_ke_don,
                       dt.so_ngay_hieu_luc, dt.trang_thai
                FROM DonThuoc dt
                JOIN KhachHang kh ON kh.id = dt.khach_hang_id
                JOIN NhaThuoc nt ON nt.id = dt.nha_thuoc_id
                WHERE dt.id = ?
                """;
        List<DonThuocDTO> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            int id = rs.getInt("id");
            LocalDate ngayKe = rs.getDate("ngay_ke_don").toLocalDate();
            int soNgay = rs.getInt("so_ngay_hieu_luc");
            String tt = rs.getString("trang_thai");
            return new DonThuocDTO(
                    id,
                    rs.getString("ma_don"),
                    rs.getInt("khach_hang_id"),
                    rs.getString("ten_khach_hang"),
                    rs.getString("sdt_khach"),
                    rs.getInt("nha_thuoc_id"),
                    rs.getString("ten_nha_thuoc"),
                    rs.getString("ten_bac_si"),
                    rs.getString("co_so_kham"),
                    rs.getString("chan_doan"),
                    ngayKe,
                    soNgay,
                    ngayKe.plusDays(soNgay),
                    tt,
                    "CHUA_BAN".equals(tt),
                    truyVanChiTietDon(id)
            );
        }, donThuocId);
        return list.stream().findFirst();
    }

    public List<ChiTietDonThuocDTO> truyVanChiTietDon(int donThuocId) {
        String sql = """
                SELECT ct.id, ct.don_thuoc_id, ct.thuoc_id, t.ma AS ma_thuoc,
                       t.ten AS ten_thuoc, t.don_vi_tinh, t.can_ke_don,
                       ct.so_luong_ke, ct.so_luong_da_ban, ct.lieu_dung
                FROM ChiTietDonThuoc ct
                JOIN Thuoc t ON t.id = ct.thuoc_id
                WHERE ct.don_thuoc_id = ?
                ORDER BY ct.id ASC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            int ke = rs.getInt("so_luong_ke");
            int daBan = rs.getInt("so_luong_da_ban");
            return new ChiTietDonThuocDTO(
                    rs.getInt("id"),
                    rs.getInt("don_thuoc_id"),
                    rs.getInt("thuoc_id"),
                    rs.getString("ma_thuoc"),
                    rs.getString("ten_thuoc"),
                    rs.getString("don_vi_tinh"),
                    rs.getBoolean("can_ke_don"),
                    ke,
                    daBan,
                    Math.max(0, ke - daBan),
                    rs.getString("lieu_dung")
            );
        }, donThuocId);
    }
}
