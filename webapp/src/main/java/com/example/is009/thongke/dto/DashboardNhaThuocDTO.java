package com.example.is009.thongke.dto;

import java.math.BigDecimal;

public class DashboardNhaThuocDTO {

    private BigDecimal doanhThuHomNay = BigDecimal.ZERO;
    private int soGiaoDichHomNay;
    private BigDecimal doanhThu30Ngay = BigDecimal.ZERO;
    private int soGiaoDich30Ngay;
    private BigDecimal tongGiaTriTonKho = BigDecimal.ZERO;
    private int soPhieuChoDuyet;

    public BigDecimal getDoanhThuHomNay() { return doanhThuHomNay; }
    public void setDoanhThuHomNay(BigDecimal doanhThuHomNay) { this.doanhThuHomNay = doanhThuHomNay; }

    public int getSoGiaoDichHomNay() { return soGiaoDichHomNay; }
    public void setSoGiaoDichHomNay(int soGiaoDichHomNay) { this.soGiaoDichHomNay = soGiaoDichHomNay; }

    public BigDecimal getDoanhThu30Ngay() { return doanhThu30Ngay; }
    public void setDoanhThu30Ngay(BigDecimal doanhThu30Ngay) { this.doanhThu30Ngay = doanhThu30Ngay; }

    public int getSoGiaoDich30Ngay() { return soGiaoDich30Ngay; }
    public void setSoGiaoDich30Ngay(int soGiaoDich30Ngay) { this.soGiaoDich30Ngay = soGiaoDich30Ngay; }

    public BigDecimal getTongGiaTriTonKho() { return tongGiaTriTonKho; }
    public void setTongGiaTriTonKho(BigDecimal tongGiaTriTonKho) { this.tongGiaTriTonKho = tongGiaTriTonKho; }

    public int getSoPhieuChoDuyet() { return soPhieuChoDuyet; }
    public void setSoPhieuChoDuyet(int soPhieuChoDuyet) { this.soPhieuChoDuyet = soPhieuChoDuyet; }
}
