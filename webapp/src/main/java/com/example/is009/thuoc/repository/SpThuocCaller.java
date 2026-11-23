package com.example.is009.thuoc.repository;

import com.example.is009.thuoc.dto.LoTheoMaVachDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class SpThuocCaller {

    private final JdbcTemplate jdbcTemplate;

    public SpThuocCaller(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void goiSpThemMaVach(int thuocId, String maVach, String loaiMaVach, boolean laMaChinh, int nguoiId) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_ThemMaVach")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("thuoc_id", Types.INTEGER),
                        new SqlParameter("ma_vach", Types.NVARCHAR),
                        new SqlParameter("loai_ma_vach", Types.NVARCHAR),
                        new SqlParameter("la_ma_chinh", Types.BIT),
                        new SqlParameter("nguoi_id", Types.INTEGER)
                );

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("thuoc_id", thuocId)
                .addValue("ma_vach", maVach)
                .addValue("loai_ma_vach", loaiMaVach)
                .addValue("la_ma_chinh", laMaChinh)
                .addValue("nguoi_id", nguoiId);

        call.execute(params);
    }

    @SuppressWarnings("unchecked")
    public List<LoTheoMaVachDTO> goiSpTraCuuThuocTheoMaVach(String maVach, int nhaThuocId) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withSchemaName("dbo")
                .withProcedureName("sp_TraCuuThuocTheoMaVach")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("ma_vach", Types.NVARCHAR),
                        new SqlParameter("nha_thuoc_id", Types.INTEGER)
                )
                .returningResultSet("items", (rs, rowNum) -> new LoTheoMaVachDTO(
                        rs.getInt("lo_thuoc_id"),
                        rs.getString("so_lo"),
                        rs.getDate("han_dung").toLocalDate(),
                        rs.getInt("so_ngay_con_lai"),
                        rs.getString("muc_canh_bao"),
                        rs.getInt("so_luong_ton")
                ));

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ma_vach", maVach)
                .addValue("nha_thuoc_id", nhaThuocId);

        Map<String, Object> out = call.execute(params);
        List<LoTheoMaVachDTO> result = (List<LoTheoMaVachDTO>) out.get("items");
        return result != null ? result : Collections.emptyList();
    }
}
