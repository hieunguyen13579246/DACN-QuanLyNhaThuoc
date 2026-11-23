package com.example.is009.thongke.dto;

import java.math.BigDecimal;

public class HieuSuatNhaThuocDTO {

    private Integer nhaThuocId;
    private String maNhaThuoc;
    private String tenNhaThuoc;
    private String tenKhuVuc;
    private int soGiaoDich;
    private BigDecimal tongDoanhThu;
    private BigDecimal tongGiaVon;
    private BigDecimal loiNhuanGop;
    private BigDecimal tySuatLoiNhuanPct;
    private BigDecimal giaTriTonKhoHienTai;
    private BigDecimal giaTriGioTb;
    private BigDecimal tyLeBanTheoDonPct;
    private BigDecimal vongQuayTon;

    public BigDecimal getGiaTriGioTb() { return giaTriGioTb; }
    public void setGiaTriGioTb(BigDecimal giaTriGioTb) { this.giaTriGioTb = giaTriGioTb; }

    public BigDecimal getTyLeBanTheoDonPct() { return tyLeBanTheoDonPct; }
    public void setTyLeBanTheoDonPct(BigDecimal tyLeBanTheoDonPct) { this.tyLeBanTheoDonPct = tyLeBanTheoDonPct; }

    public BigDecimal getVongQuayTon() { return vongQuayTon; }
    public void setVongQuayTon(BigDecimal vongQuayTon) { this.vongQuayTon = vongQuayTon; }
    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public String getMaNhaThuoc() { return maNhaThuoc; }
    public void setMaNhaThuoc(String maNhaThuoc) { this.maNhaThuoc = maNhaThuoc; }

    public String getTenNhaThuoc() { return tenNhaThuoc; }
    public void setTenNhaThuoc(String tenNhaThuoc) { this.tenNhaThuoc = tenNhaThuoc; }

    public String getTenKhuVuc() { return tenKhuVuc; }
    public void setTenKhuVuc(String tenKhuVuc) { this.tenKhuVuc = tenKhuVuc; }

    public int getSoGiaoDich() { return soGiaoDich; }
    public void setSoGiaoDich(int soGiaoDich) { this.soGiaoDich = soGiaoDich; }

    public BigDecimal getTongDoanhThu() { return tongDoanhThu; }
    public void setTongDoanhThu(BigDecimal tongDoanhThu) { this.tongDoanhThu = tongDoanhThu; }

    public BigDecimal getTongGiaVon() { return tongGiaVon; }
    public void setTongGiaVon(BigDecimal tongGiaVon) { this.tongGiaVon = tongGiaVon; }

    public BigDecimal getLoiNhuanGop() { return loiNhuanGop; }
    public void setLoiNhuanGop(BigDecimal loiNhuanGop) { this.loiNhuanGop = loiNhuanGop; }

    public BigDecimal getTySuatLoiNhuanPct() { return tySuatLoiNhuanPct; }
    public void setTySuatLoiNhuanPct(BigDecimal tySuatLoiNhuanPct) { this.tySuatLoiNhuanPct = tySuatLoiNhuanPct; }

    public BigDecimal getGiaTriTonKhoHienTai() { return giaTriTonKhoHienTai; }
    public void setGiaTriTonKhoHienTai(BigDecimal giaTriTonKhoHienTai) { this.giaTriTonKhoHienTai = giaTriTonKhoHienTai; }

    public String getNhaThuoc() { return tenNhaThuoc; }
    public String getKhuVuc() { return tenKhuVuc; }
    public BigDecimal getDoanhThu() { return tongDoanhThu; }
    public BigDecimal getBienLoiNhuanGopPct() { return tySuatLoiNhuanPct; }
}
