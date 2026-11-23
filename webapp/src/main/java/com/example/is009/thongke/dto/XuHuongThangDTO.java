package com.example.is009.thongke.dto;

import java.math.BigDecimal;

public class XuHuongThangDTO {

    private int nam;
    private int thang;
    private String kyThang;
    private int soGiaoDich;
    private int tongSoLuong;
    private BigDecimal doanhThu;
    private BigDecimal doanhThuThangTruoc;
    private BigDecimal tangTruongPhanTram;

    public int getNam() { return nam; }
    public void setNam(int nam) { this.nam = nam; }

    public int getThang() { return thang; }
    public void setThang(int thang) { this.thang = thang; }

    public String getKyThang() { return kyThang; }
    public void setKyThang(String kyThang) { this.kyThang = kyThang; }

    public int getSoGiaoDich() { return soGiaoDich; }
    public void setSoGiaoDich(int soGiaoDich) { this.soGiaoDich = soGiaoDich; }

    public int getTongSoLuong() { return tongSoLuong; }
    public void setTongSoLuong(int tongSoLuong) { this.tongSoLuong = tongSoLuong; }

    public BigDecimal getDoanhThu() { return doanhThu; }
    public void setDoanhThu(BigDecimal doanhThu) { this.doanhThu = doanhThu; }

    public BigDecimal getDoanhThuThangTruoc() { return doanhThuThangTruoc; }
    public void setDoanhThuThangTruoc(BigDecimal doanhThuThangTruoc) { this.doanhThuThangTruoc = doanhThuThangTruoc; }

    public BigDecimal getTangTruongPhanTram() { return tangTruongPhanTram; }
    public void setTangTruongPhanTram(BigDecimal tangTruongPhanTram) { this.tangTruongPhanTram = tangTruongPhanTram; }
}
