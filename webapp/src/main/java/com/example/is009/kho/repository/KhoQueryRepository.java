package com.example.is009.kho.repository;

import com.example.is009.common.ThoiGianVN;
import com.example.is009.kho.dto.BienDongKhoDTO;
import com.example.is009.kho.dto.ChiTietPhieuKhoDTO;
import com.example.is009.kho.dto.DinhMucTonDTO;
import com.example.is009.kho.dto.PhieuKhoDTO;
import com.example.is009.kho.dto.TonKhoLoDTO;
import com.example.is009.kho.dto.TonKhoThuocDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class KhoQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public KhoQueryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TonKhoThuocDTO> truyVanTonTheoThuoc(Integer phamViNhaThuocId, Integer nhomThuocId, String tuKhoa) {
        StringBuilder sql = new StringBuilder("""
                SELECT vt.nha_thuoc_id,
                       nt.ten AS ten_nha_thuoc,
                       vt.thuoc_id,
                       t.ma AS ma_thuoc,
                       t.ten AS ten_thuoc,
                       t.don_vi_tinh,
                       t.nhom_thuoc_id,
                       nh.ten AS ten_nhom,
                       vt.tong_ton,
                       0 AS tong_giu_cho,
                       vt.tong_ton AS tong_kha_dung,
                       (SELECT MIN(lt2.han_dung)
                        FROM dbo.TonKho tk2
                        JOIN dbo.LoThuoc lt2 ON lt2.id = tk2.lo_thuoc_id
                        WHERE tk2.nha_thuoc_id = vt.nha_thuoc_id
                          AND lt2.thuoc_id = vt.thuoc_id
                          AND tk2.so_luong > 0) AS han_dung_gan_nhat,
                       (SELECT COUNT(1)
                        FROM dbo.TonKho tk3
                        JOIN dbo.LoThuoc lt3 ON lt3.id = tk3.lo_thuoc_id
                        WHERE tk3.nha_thuoc_id = vt.nha_thuoc_id
                          AND lt3.thuoc_id = vt.thuoc_id
                          AND tk3.so_luong > 0) AS so_lo_con_ton
                FROM dbo.vw_TonKhoTheoThuoc vt
                JOIN dbo.NhaThuoc nt ON nt.id = vt.nha_thuoc_id
                JOIN dbo.Thuoc t ON t.id = vt.thuoc_id
                JOIN dbo.NhomThuoc nh ON nh.id = t.nhom_thuoc_id
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();
        if (phamViNhaThuocId != null) {
            sql.append(" AND vt.nha_thuoc_id = ?");
            params.add(phamViNhaThuocId);
        }
        if (nhomThuocId != null) {
            sql.append(" AND t.nhom_thuoc_id = ?");
            params.add(nhomThuocId);
        }
        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append(" AND (t.ma LIKE ? OR t.ten LIKE ?)");
            String kw = "%" + tuKhoa.trim() + "%";
            params.add(kw);
            params.add(kw);
        }
        sql.append(" ORDER BY nt.ten, t.ten");
        return jdbcTemplate.query(sql.toString(), (rs, i) -> mapTonKhoThuoc(rs), params.toArray());
    }

    public List<TonKhoLoDTO> truyVanTonTheoLo(Integer phamViNhaThuocId, Integer thuocId, String tuKhoa, boolean chiLayConTon) {
        StringBuilder sql = new StringBuilder("""
                SELECT tk.nha_thuoc_id, nt.ten AS ten_nha_thuoc,
                       lt.id AS lo_thuoc_id, lt.so_lo, lt.so_lo AS ma_vach_lo,
                       t.id AS thuoc_id, t.ma AS ma_thuoc, t.ten AS ten_thuoc, t.don_vi_tinh,
                       (SELECT TOP 1 mv.ma_vach FROM dbo.MaVachThuoc mv WHERE mv.thuoc_id = t.id ORDER BY mv.la_ma_chinh DESC) AS ma_vach_chinh,
                       lt.ngay_san_xuat, lt.han_dung, lt.gia_nhap, t.gia_ban_le,
                       tk.so_luong AS so_luong_ton, 0 AS so_luong_giu_cho, tk.so_luong AS so_luong_kha_dung
                FROM dbo.TonKho tk
                JOIN dbo.NhaThuoc nt ON nt.id = tk.nha_thuoc_id
                JOIN dbo.LoThuoc lt ON lt.id = tk.lo_thuoc_id
                JOIN dbo.Thuoc t ON t.id = lt.thuoc_id
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();
        if (phamViNhaThuocId != null) {
            sql.append(" AND tk.nha_thuoc_id = ?");
            params.add(phamViNhaThuocId);
        }
        if (thuocId != null) {
            sql.append(" AND t.id = ?");
            params.add(thuocId);
        }
        if (chiLayConTon) {
            sql.append(" AND tk.so_luong > 0");
        }
        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append(" AND (lt.so_lo LIKE ? OR t.ma LIKE ? OR t.ten LIKE ?)");
            String kw = "%" + tuKhoa.trim() + "%";
            params.add(kw);
            params.add(kw);
            params.add(kw);
        }
        sql.append(" ORDER BY lt.han_dung ASC, lt.id ASC");
        return jdbcTemplate.query(sql.toString(), (rs, i) -> mapTonKhoLo(rs), params.toArray());
    }

    public List<PhieuKhoDTO> truyVanDanhSachPhieu(Integer phamViNhaThuocId, String loaiPhieu, String trangThai) {
        StringBuilder sql = new StringBuilder("""
                SELECT TOP 200 pk.id, pk.ma_phieu, pk.loai_phieu,
                       pk.nha_thuoc_id, nt1.ten AS ten_nha_thuoc,
                       pk.nha_thuoc_den_id AS nha_thuoc_dich_id, nt2.ten AS ten_nha_thuoc_dich,
                       pk.nha_cung_cap_id, ncc.ten AS ten_nha_cung_cap,
                       pk.trang_thai, pk.ly_do AS ly_do_huy, pk.ghi_chu AS ly_do_tu_choi, pk.ghi_chu,
                       pk.nguoi_lap_id, nl.ho_ten AS ten_nguoi_lap, pk.thoi_gian_lap,
                       pk.nguoi_duyet_id, nd.ho_ten AS ten_nguoi_duyet, pk.thoi_gian_hoan_thanh AS thoi_gian_duyet,
                       pk.tong_gia_tri
                FROM dbo.PhieuKho pk
                JOIN dbo.NhaThuoc nt1 ON nt1.id = pk.nha_thuoc_id
                LEFT JOIN dbo.NhaThuoc nt2 ON nt2.id = pk.nha_thuoc_den_id
                LEFT JOIN dbo.NhaCungCap ncc ON ncc.id = pk.nha_cung_cap_id
                JOIN dbo.NguoiDung nl ON nl.id = pk.nguoi_lap_id
                LEFT JOIN dbo.NguoiDung nd ON nd.id = pk.nguoi_duyet_id
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();
        if (phamViNhaThuocId != null) {
            sql.append(" AND (pk.nha_thuoc_id = ? OR pk.nha_thuoc_den_id = ?)");
            params.add(phamViNhaThuocId);
            params.add(phamViNhaThuocId);
        }
        if (loaiPhieu != null && !loaiPhieu.isBlank()) {
            String lp = loaiPhieu.trim();
            if ("NHAP".equalsIgnoreCase(lp) || "NHAP_KHO".equalsIgnoreCase(lp)) {
                sql.append(" AND pk.loai_phieu IN (N'NHAP', N'NHAP_KHO')");
            } else {
                sql.append(" AND pk.loai_phieu = ?");
                params.add(lp);
            }
        }
        if (trangThai != null && !trangThai.isBlank()) {
            String tt = trangThai.trim();
            if ("DA_DUYET".equalsIgnoreCase(tt) || "HOAN_THANH".equalsIgnoreCase(tt)) {
                sql.append(" AND pk.trang_thai IN (N'HOAN_THANH', N'DA_DUYET')");
            } else {
                sql.append(" AND pk.trang_thai = ?");
                params.add(tt);
            }
        }
        sql.append(" ORDER BY pk.thoi_gian_lap DESC, pk.id DESC");
        return jdbcTemplate.query(sql.toString(), (rs, i) -> mapPhieuKhoHeader(rs), params.toArray());
    }

    public Optional<PhieuKhoDTO> truyVanPhieuTheoId(Integer id) {
        String sqlHeader = """
                SELECT pk.id, pk.ma_phieu, pk.loai_phieu,
                       pk.nha_thuoc_id, nt1.ten AS ten_nha_thuoc,
                       pk.nha_thuoc_den_id AS nha_thuoc_dich_id, nt2.ten AS ten_nha_thuoc_dich,
                       pk.nha_cung_cap_id, ncc.ten AS ten_nha_cung_cap,
                       pk.trang_thai, pk.ly_do AS ly_do_huy, pk.ghi_chu AS ly_do_tu_choi, pk.ghi_chu,
                       pk.nguoi_lap_id, nl.ho_ten AS ten_nguoi_lap, pk.thoi_gian_lap,
                       pk.nguoi_duyet_id, nd.ho_ten AS ten_nguoi_duyet, pk.thoi_gian_hoan_thanh AS thoi_gian_duyet,
                       pk.tong_gia_tri
                FROM dbo.PhieuKho pk
                JOIN dbo.NhaThuoc nt1 ON nt1.id = pk.nha_thuoc_id
                LEFT JOIN dbo.NhaThuoc nt2 ON nt2.id = pk.nha_thuoc_den_id
                LEFT JOIN dbo.NhaCungCap ncc ON ncc.id = pk.nha_cung_cap_id
                JOIN dbo.NguoiDung nl ON nl.id = pk.nguoi_lap_id
                LEFT JOIN dbo.NguoiDung nd ON nd.id = pk.nguoi_duyet_id
                WHERE pk.id = ?
                """;
        List<PhieuKhoDTO> list = jdbcTemplate.query(sqlHeader, (rs, i) -> mapPhieuKhoHeader(rs), id);
        if (list.isEmpty()) return Optional.empty();
        PhieuKhoDTO dto = list.get(0);
        String sqlLines = """
                SELECT ct.id, lt.thuoc_id, t.ma AS ma_thuoc, t.ten AS ten_thuoc, t.don_vi_tinh,
                       ct.lo_thuoc_id, lt.so_lo, lt.ngay_san_xuat, lt.han_dung,
                       ct.so_luong, ISNULL(ct.don_gia, lt.gia_nhap) AS don_gia,
                       (CAST(ct.so_luong AS DECIMAL(18,0)) * ISNULL(ct.don_gia, lt.gia_nhap)) AS thanh_tien
                FROM dbo.ChiTietPhieuKho ct
                JOIN dbo.LoThuoc lt ON lt.id = ct.lo_thuoc_id
                JOIN dbo.Thuoc t ON t.id = lt.thuoc_id
                WHERE ct.phieu_kho_id = ?
                ORDER BY ct.id
                """;
        dto.setChiTiet(jdbcTemplate.query(sqlLines, (rs, i) -> mapChiTietPhieu(rs), id));
        return Optional.of(dto);
    }

    public List<DinhMucTonDTO> truyVanDinhMucTon(Integer phamViNhaThuocId) {
        StringBuilder sql = new StringBuilder("""
                SELECT dm.id, dm.nha_thuoc_id, nt.ten AS ten_nha_thuoc,
                       dm.thuoc_id, t.ma AS ma_thuoc, t.ten AS ten_thuoc, t.don_vi_tinh,
                       dm.ton_toi_thieu, ISNULL(dm.ton_toi_da, dm.ton_toi_thieu * 3) AS ton_toi_da, dm.so_ngay_giao_hang,
                       ISNULL(vw.tong_ton, 0) AS ton_kha_dung_hien_tai
                FROM dbo.DinhMucTon dm
                JOIN dbo.NhaThuoc nt ON nt.id = dm.nha_thuoc_id
                JOIN dbo.Thuoc t ON t.id = dm.thuoc_id
                LEFT JOIN dbo.vw_TonKhoTheoThuoc vw ON vw.nha_thuoc_id = dm.nha_thuoc_id AND vw.thuoc_id = dm.thuoc_id
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();
        if (phamViNhaThuocId != null) {
            sql.append(" AND dm.nha_thuoc_id = ?");
            params.add(phamViNhaThuocId);
        }
        sql.append(" ORDER BY nt.ten, t.ten");
        return jdbcTemplate.query(sql.toString(), (rs, i) -> {
            DinhMucTonDTO d = new DinhMucTonDTO();
            d.setId(rs.getInt("id"));
            d.setNhaThuocId(rs.getInt("nha_thuoc_id"));
            d.setTenNhaThuoc(rs.getString("ten_nha_thuoc"));
            d.setThuocId(rs.getInt("thuoc_id"));
            d.setMaThuoc(rs.getString("ma_thuoc"));
            d.setTenThuoc(rs.getString("ten_thuoc"));
            d.setDonViTinh(rs.getString("don_vi_tinh"));
            d.setTonToiThieu(rs.getInt("ton_toi_thieu"));
            d.setTonToiDa(rs.getInt("ton_toi_da"));
            d.setSoNgayGiaoHang(rs.getInt("so_ngay_giao_hang"));
            d.setTonKhaDungHienTai(rs.getInt("ton_kha_dung_hien_tai"));
            return d;
        }, params.toArray());
    }

    public List<BienDongKhoDTO> truyVanBienDongKho(Integer phamViNhaThuocId) {
        StringBuilder sql = new StringBuilder("""
                SELECT TOP 150 bd.id, bd.nha_thuoc_id, nt.ten AS ten_nha_thuoc,
                       bd.lo_thuoc_id, lt.so_lo, t.ten AS ten_thuoc,
                       bd.loai_bien_dong, bd.so_luong_thay_doi AS so_luong, bd.so_du_sau AS ton_sau,
                       bd.chung_tu_loai AS tham_chieu_loai, bd.chung_tu_id AS tham_chieu_id, bd.thoi_gian,
                       ISNULL(nd.ho_ten, N'Hệ thống') AS ten_nguoi_thuc_hien,
                       CAST(N'' AS NVARCHAR(100)) AS ghi_chu
                FROM dbo.BienDongKho bd
                JOIN dbo.NhaThuoc nt ON nt.id = bd.nha_thuoc_id
                JOIN dbo.LoThuoc lt ON lt.id = bd.lo_thuoc_id
                JOIN dbo.Thuoc t ON t.id = lt.thuoc_id
                LEFT JOIN dbo.NguoiDung nd ON nd.id = bd.nguoi_thuc_hien_id
                WHERE 1=1
                """);
        List<Object> params = new ArrayList<>();
        if (phamViNhaThuocId != null) {
            sql.append(" AND bd.nha_thuoc_id = ?");
            params.add(phamViNhaThuocId);
        }
        sql.append(" ORDER BY bd.thoi_gian DESC, bd.id DESC");
        return jdbcTemplate.query(sql.toString(), (rs, i) -> {
            BienDongKhoDTO b = new BienDongKhoDTO();
            b.setId((int) rs.getLong("id"));
            b.setNhaThuocId(rs.getInt("nha_thuoc_id"));
            b.setTenNhaThuoc(rs.getString("ten_nha_thuoc"));
            b.setLoThuocId(rs.getInt("lo_thuoc_id"));
            b.setSoLo(rs.getString("so_lo"));
            b.setTenThuoc(rs.getString("ten_thuoc"));
            b.setLoaiBienDong(rs.getString("loai_bien_dong"));
            b.setSoLuong(rs.getInt("so_luong"));
            b.setTonSau(rs.getInt("ton_sau"));
            b.setThamChieuLoai(rs.getString("tham_chieu_loai"));
            b.setThamChieuId(rs.getInt("tham_chieu_id"));
            Timestamp tg = rs.getTimestamp("thoi_gian");
            b.setThoiGian(tg != null ? ThoiGianVN.dinhDangNgayGio(ThoiGianVN.tuTimestampUtc(tg)) : "");
            b.setTenNguoiThucHien(rs.getString("ten_nguoi_thuc_hien"));
            b.setGhiChu(rs.getString("ghi_chu"));
            return b;
        }, params.toArray());
    }

    private TonKhoThuocDTO mapTonKhoThuoc(ResultSet rs) throws SQLException {
        TonKhoThuocDTO d = new TonKhoThuocDTO();
        d.setNhaThuocId(rs.getInt("nha_thuoc_id"));
        d.setTenNhaThuoc(rs.getString("ten_nha_thuoc"));
        d.setThuocId(rs.getInt("thuoc_id"));
        d.setMaThuoc(rs.getString("ma_thuoc"));
        d.setTenThuoc(rs.getString("ten_thuoc"));
        d.setDonViTinh(rs.getString("don_vi_tinh"));
        d.setNhomThuocId(rs.getInt("nhom_thuoc_id"));
        d.setTenNhom(rs.getString("ten_nhom"));
        d.setTongTon(rs.getInt("tong_ton"));
        d.setTongGiuCho(rs.getInt("tong_giu_cho"));
        d.setTongKhaDung(rs.getInt("tong_kha_dung"));
        Date hd = rs.getDate("han_dung_gan_nhat");
        d.setHanDungGanNhat(hd != null ? hd.toLocalDate() : null);
        d.setSoLoConTon(rs.getInt("so_lo_con_ton"));
        return d;
    }

    private TonKhoLoDTO mapTonKhoLo(ResultSet rs) throws SQLException {
        TonKhoLoDTO d = new TonKhoLoDTO();
        d.setNhaThuocId(rs.getInt("nha_thuoc_id"));
        d.setTenNhaThuoc(rs.getString("ten_nha_thuoc"));
        d.setLoThuocId(rs.getInt("lo_thuoc_id"));
        d.setSoLo(rs.getString("so_lo"));
        d.setMaVachLo(rs.getString("ma_vach_lo"));
        d.setThuocId(rs.getInt("thuoc_id"));
        d.setMaThuoc(rs.getString("ma_thuoc"));
        d.setTenThuoc(rs.getString("ten_thuoc"));
        d.setDonViTinh(rs.getString("don_vi_tinh"));
        d.setMaVachChinh(rs.getString("ma_vach_chinh"));
        Date nsx = rs.getDate("ngay_san_xuat");
        d.setNgaySanXuat(nsx != null ? nsx.toLocalDate() : null);
        Date hd = rs.getDate("han_dung");
        if (hd != null) {
            LocalDate hanDung = hd.toLocalDate();
            d.setHanDung(hanDung);
            d.setSoNgayConLai(ChronoUnit.DAYS.between(ThoiGianVN.homNay(), hanDung));
        }
        d.setGiaNhap(rs.getBigDecimal("gia_nhap"));
        d.setGiaBanLe(rs.getBigDecimal("gia_ban_le"));
        d.setSoLuongTon(rs.getInt("so_luong_ton"));
        d.setSoLuongGiuCho(rs.getInt("so_luong_giu_cho"));
        d.setSoLuongKhaDung(rs.getInt("so_luong_kha_dung"));
        return d;
    }

    private PhieuKhoDTO mapPhieuKhoHeader(ResultSet rs) throws SQLException {
        PhieuKhoDTO p = new PhieuKhoDTO();
        p.setId(rs.getInt("id"));
        p.setMaPhieu(rs.getString("ma_phieu"));
        p.setLoaiPhieu(rs.getString("loai_phieu"));
        p.setNhaThuocId(rs.getInt("nha_thuoc_id"));
        p.setTenNhaThuoc(rs.getString("ten_nha_thuoc"));
        int ntDich = rs.getInt("nha_thuoc_dich_id");
        p.setNhaThuocDichId(rs.wasNull() ? null : ntDich);
        p.setTenNhaThuocDich(rs.getString("ten_nha_thuoc_dich"));
        int nccId = rs.getInt("nha_cung_cap_id");
        p.setNhaCungCapId(rs.wasNull() ? null : nccId);
        p.setTenNhaCungCap(rs.getString("ten_nha_cung_cap"));
        p.setTrangThai(rs.getString("trang_thai"));
        p.setLyDoHuy(rs.getString("ly_do_huy"));
        p.setLyDoTuChoi(rs.getString("ly_do_tu_choi"));
        p.setGhiChu(rs.getString("ghi_chu"));
        p.setNguoiLapId(rs.getInt("nguoi_lap_id"));
        p.setTenNguoiLap(rs.getString("ten_nguoi_lap"));
        Timestamp tLap = rs.getTimestamp("thoi_gian_lap");
        p.setThoiGianLap(tLap != null ? ThoiGianVN.dinhDangNgayGio(ThoiGianVN.tuTimestampUtc(tLap)) : "");
        int ndId = rs.getInt("nguoi_duyet_id");
        p.setNguoiDuyetId(rs.wasNull() ? null : ndId);
        p.setTenNguoiDuyet(rs.getString("ten_nguoi_duyet"));
        Timestamp tDuyet = rs.getTimestamp("thoi_gian_duyet");
        p.setThoiGianDuyet(tDuyet != null ? ThoiGianVN.dinhDangNgayGio(ThoiGianVN.tuTimestampUtc(tDuyet)) : null);
        BigDecimal tgt = rs.getBigDecimal("tong_gia_tri");
        p.setTongGiaTri(tgt != null ? tgt : BigDecimal.ZERO);
        return p;
    }

    private ChiTietPhieuKhoDTO mapChiTietPhieu(ResultSet rs) throws SQLException {
        ChiTietPhieuKhoDTO c = new ChiTietPhieuKhoDTO();
        c.setId(rs.getInt("id"));
        c.setThuocId(rs.getInt("thuoc_id"));
        c.setMaThuoc(rs.getString("ma_thuoc"));
        c.setTenThuoc(rs.getString("ten_thuoc"));
        c.setDonViTinh(rs.getString("don_vi_tinh"));
        c.setLoThuocId(rs.getInt("lo_thuoc_id"));
        c.setSoLo(rs.getString("so_lo"));
        Date nsx = rs.getDate("ngay_san_xuat");
        c.setNgaySanXuat(nsx != null ? nsx.toLocalDate() : null);
        Date hd = rs.getDate("han_dung");
        c.setHanDung(hd != null ? hd.toLocalDate() : null);
        c.setSoLuong(rs.getInt("so_luong"));
        c.setDonGia(rs.getBigDecimal("don_gia"));
        c.setThanhTien(rs.getBigDecimal("thanh_tien"));
        return c;
    }
}
