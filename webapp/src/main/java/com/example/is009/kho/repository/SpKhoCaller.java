package com.example.is009.kho.repository;

import com.example.is009.kho.dto.DoiSoatTonKhoDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class SpKhoCaller {

    private final JdbcTemplate jdbcTemplate;

    public SpKhoCaller(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer nhapKho(Integer nhaThuocId, Integer nguoiLapId, Integer nhaCungCapId,
                           String ghiChu, String chiTietJson) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_NhapKho")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("nha_thuoc_id", Types.INTEGER),
                        new SqlParameter("nguoi_lap_id", Types.INTEGER),
                        new SqlParameter("nha_cung_cap_id", Types.INTEGER),
                        new SqlParameter("ghi_chu", Types.NVARCHAR),
                        new SqlParameter("chi_tiet_json", Types.NVARCHAR),
                        new SqlOutParameter("phieu_kho_id", Types.INTEGER)
                );

        Map<String, Object> out = call.execute(new MapSqlParameterSource()
                .addValue("nha_thuoc_id", nhaThuocId)
                .addValue("nguoi_lap_id", nguoiLapId)
                .addValue("nha_cung_cap_id", nhaCungCapId)
                .addValue("ghi_chu", ghiChu)
                .addValue("chi_tiet_json", chiTietJson));
        Number id = (Number) out.get("phieu_kho_id");
        return id != null ? id.intValue() : null;
    }

    public Integer taoPhieuDieuChuyen(Integer nhaThuocNguonId, Integer nhaThuocDichId,
                                      Integer nguoiLapId, String ghiChu, String chiTietJson) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_TaoPhieuDieuChuyen")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("nha_thuoc_nguon_id", Types.INTEGER),
                        new SqlParameter("nha_thuoc_dich_id", Types.INTEGER),
                        new SqlParameter("nguoi_lap_id", Types.INTEGER),
                        new SqlParameter("ghi_chu", Types.NVARCHAR),
                        new SqlParameter("chi_tiet_json", Types.NVARCHAR),
                        new SqlOutParameter("phieu_kho_id", Types.INTEGER)
                );

        Map<String, Object> out = call.execute(new MapSqlParameterSource()
                .addValue("nha_thuoc_nguon_id", nhaThuocNguonId)
                .addValue("nha_thuoc_dich_id", nhaThuocDichId)
                .addValue("nguoi_lap_id", nguoiLapId)
                .addValue("ghi_chu", ghiChu)
                .addValue("chi_tiet_json", chiTietJson));
        Number id = (Number) out.get("phieu_kho_id");
        return id != null ? id.intValue() : null;
    }

    public Integer taoPhieuXuatHuy(Integer nhaThuocId, Integer nguoiLapId, String lyDo,
                                   String ghiChu, String chiTietJson) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_TaoPhieuXuatHuy")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("nha_thuoc_id", Types.INTEGER),
                        new SqlParameter("nguoi_lap_id", Types.INTEGER),
                        new SqlParameter("ly_do", Types.VARCHAR),
                        new SqlParameter("ghi_chu", Types.NVARCHAR),
                        new SqlParameter("chi_tiet_json", Types.NVARCHAR),
                        new SqlOutParameter("phieu_kho_id", Types.INTEGER)
                );

        Map<String, Object> out = call.execute(new MapSqlParameterSource()
                .addValue("nha_thuoc_id", nhaThuocId)
                .addValue("nguoi_lap_id", nguoiLapId)
                .addValue("ly_do", lyDo)
                .addValue("ghi_chu", ghiChu)
                .addValue("chi_tiet_json", chiTietJson));
        Number id = (Number) out.get("phieu_kho_id");
        return id != null ? id.intValue() : null;
    }

    public void duyetPhieuKho(Integer phieuKhoId, Integer nguoiDuyetId, String ketQua, String lyDo) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_DuyetPhieuKho")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("phieu_kho_id", Types.INTEGER),
                        new SqlParameter("nguoi_duyet_id", Types.INTEGER),
                        new SqlParameter("ket_qua", Types.VARCHAR),
                        new SqlParameter("ly_do", Types.NVARCHAR)
                );

        call.execute(new MapSqlParameterSource()
                .addValue("phieu_kho_id", phieuKhoId)
                .addValue("nguoi_duyet_id", nguoiDuyetId)
                .addValue("ket_qua", ketQua)
                .addValue("ly_do", lyDo));
    }

    public void huyPhieuKho(Integer phieuKhoId, Integer nguoiId) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_HuyPhieuKho")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("phieu_kho_id", Types.INTEGER),
                        new SqlParameter("nguoi_id", Types.INTEGER)
                );

        call.execute(new MapSqlParameterSource()
                .addValue("phieu_kho_id", phieuKhoId)
                .addValue("nguoi_id", nguoiId));
    }

    @SuppressWarnings("unchecked")
    public List<DoiSoatTonKhoDTO> doiSoatTonKho(Integer nhaThuocId) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_DoiSoatTonKho")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("nha_thuoc_id", Types.INTEGER))
                .returningResultSet("rows", (rs, rowNum) -> mapDoiSoat(rs));

        Map<String, Object> out = call.execute(new MapSqlParameterSource()
                .addValue("nha_thuoc_id", nhaThuocId));
        return (List<DoiSoatTonKhoDTO>) out.getOrDefault("rows", List.of());
    }

    private DoiSoatTonKhoDTO mapDoiSoat(ResultSet rs) throws SQLException {
        DoiSoatTonKhoDTO dto = new DoiSoatTonKhoDTO();
        dto.setNhaThuocId(rs.getInt("nha_thuoc_id"));
        dto.setTenNhaThuoc(rs.getString("ten_nha_thuoc"));
        dto.setLoThuocId(rs.getInt("lo_thuoc_id"));
        dto.setSoLo(rs.getString("so_lo"));
        dto.setTenThuoc(rs.getString("ten_thuoc"));
        dto.setTonHienTai(rs.getInt("ton_kho_hien_tai"));
        dto.setTongBienDong(rs.getInt("tong_bien_dong"));
        dto.setChenhLech(rs.getInt("chenh_lech"));
        return dto;
    }
}
