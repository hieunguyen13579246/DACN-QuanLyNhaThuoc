package com.example.is009.canhbao.repository;

import com.example.is009.canhbao.dto.CanhBaoHanDungDTO;
import com.example.is009.canhbao.dto.CanhBaoTonThapDTO;
import com.example.is009.canhbao.dto.TonChamDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CanhBaoRepository {

    private final JdbcTemplate jdbcTemplate;

    public CanhBaoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CanhBaoHanDungDTO> truyVanCanhBaoHanDung(Integer phamViNhaThuocId, String mucCanhBao) {
        StringBuilder sql = new StringBuilder("""
                SELECT nha_thuoc_id, ten_nha_thuoc, lo_thuoc_id, so_lo,
                       thuoc_id, ma_thuoc, ten_thuoc, don_vi_tinh,
                       han_dung, so_ngay_con_lai, so_luong_ton, so_luong_ton AS so_luong_kha_dung,
                       gia_tri_ton, muc_canh_bao
                FROM dbo.vw_CanhBaoHanDung
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();
        if (phamViNhaThuocId != null) {
            sql.append(" AND nha_thuoc_id = ?");
            params.add(phamViNhaThuocId);
        }
        if (mucCanhBao != null && !mucCanhBao.isBlank()) {
            String normalized = mucCanhBao.trim().replace("CAN_HAN_", "MUC_");
            sql.append(" AND muc_canh_bao = ?");
            params.add(normalized);
        }
        sql.append(" ORDER BY so_ngay_con_lai ASC, nha_thuoc_id ASC");
        return jdbcTemplate.query(sql.toString(), (rs, i) -> mapHanDung(rs), params.toArray());
    }

    public List<CanhBaoTonThapDTO> truyVanCanhBaoTonThap(Integer phamViNhaThuocId, String mucCanhBao) {
        StringBuilder sql = new StringBuilder("""
                SELECT nha_thuoc_id, ten_nha_thuoc, thuoc_id, ma_thuoc, ten_thuoc, don_vi_tinh,
                       tong_ton AS ton_kha_dung, ton_toi_thieu,
                       ISNULL(ton_toi_da, ton_toi_thieu * 3) AS ton_toi_da,
                       CASE WHEN ISNULL(ton_toi_da, ton_toi_thieu * 3) > tong_ton
                            THEN ISNULL(ton_toi_da, ton_toi_thieu * 3) - tong_ton
                            ELSE thieu END AS so_luong_can_nhap,
                       CASE WHEN tong_ton = 0 THEN N'HET_HANG' ELSE N'TON_THAP' END AS muc_canh_bao
                FROM dbo.vw_CanhBaoTonThap
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();
        if (phamViNhaThuocId != null) {
            sql.append(" AND nha_thuoc_id = ?");
            params.add(phamViNhaThuocId);
        }
        if (mucCanhBao != null && !mucCanhBao.isBlank()) {
            if ("HET_HANG".equalsIgnoreCase(mucCanhBao.trim())) {
                sql.append(" AND tong_ton = 0");
            } else if ("TON_THAP".equalsIgnoreCase(mucCanhBao.trim())) {
                sql.append(" AND tong_ton > 0");
            }
        }
        sql.append(" ORDER BY tong_ton ASC, nha_thuoc_id ASC");
        return jdbcTemplate.query(sql.toString(), (rs, i) -> mapTonThap(rs), params.toArray());
    }

    public List<TonChamDTO> truyVanTonChamLuanChuyen(Integer phamViNhaThuocId) {
        StringBuilder sql = new StringBuilder("""
                SELECT tc.nha_thuoc_id, tc.ten_nha_thuoc,
                       ISNULL((SELECT TOP 1 tk.lo_thuoc_id
                               FROM dbo.TonKho tk
                               JOIN dbo.LoThuoc lt ON lt.id = tk.lo_thuoc_id
                               WHERE tk.nha_thuoc_id = tc.nha_thuoc_id
                                 AND lt.thuoc_id = tc.thuoc_id
                                 AND tk.so_luong > 0
                               ORDER BY lt.han_dung ASC), 0) AS lo_thuoc_id,
                       ISNULL((SELECT TOP 1 lt.so_lo
                               FROM dbo.TonKho tk
                               JOIN dbo.LoThuoc lt ON lt.id = tk.lo_thuoc_id
                               WHERE tk.nha_thuoc_id = tc.nha_thuoc_id
                                 AND lt.thuoc_id = tc.thuoc_id
                                 AND tk.so_luong > 0
                               ORDER BY lt.han_dung ASC), N'—') AS so_lo,
                       tc.thuoc_id, tc.ma_thuoc, tc.ten_thuoc, tc.don_vi_tinh,
                       (SELECT MIN(lt.han_dung)
                        FROM dbo.TonKho tk
                        JOIN dbo.LoThuoc lt ON lt.id = tk.lo_thuoc_id
                        WHERE tk.nha_thuoc_id = tc.nha_thuoc_id
                          AND lt.thuoc_id = tc.thuoc_id
                          AND tk.so_luong > 0) AS han_dung,
                       tc.tong_ton AS so_luong_ton, tc.gia_tri_ton,
                       0 AS so_luong_ban_30_ngay,
                       ISNULL(tc.so_ngay_khong_ban, 999) AS so_ngay_khong_ban
                FROM dbo.vw_TonChamLuanChuyen tc
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();
        if (phamViNhaThuocId != null) {
            sql.append(" AND tc.nha_thuoc_id = ?");
            params.add(phamViNhaThuocId);
        }
        sql.append(" ORDER BY ISNULL(tc.so_ngay_khong_ban, 999) DESC, tc.gia_tri_ton DESC");
        return jdbcTemplate.query(sql.toString(), (rs, i) -> mapTonCham(rs), params.toArray());
    }

    private CanhBaoHanDungDTO mapHanDung(ResultSet rs) throws SQLException {
        CanhBaoHanDungDTO d = new CanhBaoHanDungDTO();
        d.setNhaThuocId(rs.getInt("nha_thuoc_id"));
        d.setTenNhaThuoc(rs.getString("ten_nha_thuoc"));
        d.setLoThuocId(rs.getInt("lo_thuoc_id"));
        d.setSoLo(rs.getString("so_lo"));
        d.setThuocId(rs.getInt("thuoc_id"));
        d.setMaThuoc(rs.getString("ma_thuoc"));
        d.setTenThuoc(rs.getString("ten_thuoc"));
        d.setDonViTinh(rs.getString("don_vi_tinh"));
        Date hd = rs.getDate("han_dung");
        d.setHanDung(hd != null ? hd.toLocalDate() : null);
        d.setSoNgayConLai(rs.getInt("so_ngay_con_lai"));
        d.setSoLuongTon(rs.getInt("so_luong_ton"));
        d.setSoLuongKhaDung(rs.getInt("so_luong_kha_dung"));
        d.setGiaTriTon(rs.getBigDecimal("gia_tri_ton"));
        d.setMucCanhBao(rs.getString("muc_canh_bao"));
        return d;
    }

    private CanhBaoTonThapDTO mapTonThap(ResultSet rs) throws SQLException {
        CanhBaoTonThapDTO d = new CanhBaoTonThapDTO();
        d.setNhaThuocId(rs.getInt("nha_thuoc_id"));
        d.setTenNhaThuoc(rs.getString("ten_nha_thuoc"));
        d.setThuocId(rs.getInt("thuoc_id"));
        d.setMaThuoc(rs.getString("ma_thuoc"));
        d.setTenThuoc(rs.getString("ten_thuoc"));
        d.setDonViTinh(rs.getString("don_vi_tinh"));
        d.setTonKhaDung(rs.getInt("ton_kha_dung"));
        d.setTonToiThieu(rs.getInt("ton_toi_thieu"));
        d.setTonToiDa(rs.getInt("ton_toi_da"));
        d.setSoLuongCanNhap(rs.getInt("so_luong_can_nhap"));
        d.setMucCanhBao(rs.getString("muc_canh_bao"));
        return d;
    }

    private TonChamDTO mapTonCham(ResultSet rs) throws SQLException {
        TonChamDTO d = new TonChamDTO();
        d.setNhaThuocId(rs.getInt("nha_thuoc_id"));
        d.setTenNhaThuoc(rs.getString("ten_nha_thuoc"));
        d.setLoThuocId(rs.getInt("lo_thuoc_id"));
        d.setSoLo(rs.getString("so_lo"));
        d.setThuocId(rs.getInt("thuoc_id"));
        d.setMaThuoc(rs.getString("ma_thuoc"));
        d.setTenThuoc(rs.getString("ten_thuoc"));
        d.setDonViTinh(rs.getString("don_vi_tinh"));
        Date hd = rs.getDate("han_dung");
        d.setHanDung(hd != null ? hd.toLocalDate() : null);
        d.setSoLuongTon(rs.getInt("so_luong_ton"));
        d.setGiaTriTon(rs.getBigDecimal("gia_tri_ton"));
        d.setSoLuongBan30Ngay(rs.getInt("so_luong_ban_30_ngay"));
        d.setSoNgayKhongBan(rs.getInt("so_ngay_khong_ban"));
        return d;
    }
}
