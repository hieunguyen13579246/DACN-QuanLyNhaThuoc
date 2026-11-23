package com.example.is009.thongke.dto;

import java.math.BigDecimal;

public class NhuCauKhuVucDTO {

    private Integer khuVucId;
    private String maKhuVuc;
    private String tenKhuVuc;
    private Integer danSo;
    private Integer nhomThuocId;
    private String maNhom;
    private String tenNhom;
    private int tongSoLuong;
    private BigDecimal tongDoanhThu;
    private int soGiaoDich;
    private BigDecimal soLuongTren1000Dan;

    public Integer getKhuVucId() { return khuVucId; }
    public void setKhuVucId(Integer khuVucId) { this.khuVucId = khuVucId; }

    public String getMaKhuVuc() { return maKhuVuc; }
    public void setMaKhuVuc(String maKhuVuc) { this.maKhuVuc = maKhuVuc; }

    public String getTenKhuVuc() { return tenKhuVuc; }
    public void setTenKhuVuc(String tenKhuVuc) { this.tenKhuVuc = tenKhuVuc; }

    public Integer getDanSo() { return danSo; }
    public void setDanSo(Integer danSo) { this.danSo = danSo; }

    public Integer getNhomThuocId() { return nhomThuocId; }
    public void setNhomThuocId(Integer nhomThuocId) { this.nhomThuocId = nhomThuocId; }

    public String getMaNhom() { return maNhom; }
    public void setMaNhom(String maNhom) { this.maNhom = maNhom; }

    public String getTenNhom() { return tenNhom; }
    public void setTenNhom(String tenNhom) { this.tenNhom = tenNhom; }

    public int getTongSoLuong() { return tongSoLuong; }
    public void setTongSoLuong(int tongSoLuong) { this.tongSoLuong = tongSoLuong; }

    public BigDecimal getTongDoanhThu() { return tongDoanhThu; }
    public void setTongDoanhThu(BigDecimal tongDoanhThu) { this.tongDoanhThu = tongDoanhThu; }

    public int getSoGiaoDich() { return soGiaoDich; }
    public void setSoGiaoDich(int soGiaoDich) { this.soGiaoDich = soGiaoDich; }

    public BigDecimal getSoLuongTren1000Dan() { return soLuongTren1000Dan; }
    public void setSoLuongTren1000Dan(BigDecimal soLuongTren1000Dan) { this.soLuongTren1000Dan = soLuongTren1000Dan; }

    public String getKhuVuc() { return tenKhuVuc; }
    public String getCap() { return getTenCapHanhChinh(); }
    public String getNhomThuoc() { return tenNhom; }
    public int getSoKhach() { return soGiaoDich; }
    public BigDecimal getSl1000Dan() { return soLuongTren1000Dan; }

    public String getTenCapHanhChinh() {
        if ("TINH".equalsIgnoreCase(maKhuVuc)) {
            String t = tenKhuVuc != null ? tenKhuVuc.toLowerCase() : "";
            if (t.contains("hồ chí minh") || t.contains("hà nội") || t.contains("đà nẵng")
                    || t.contains("hải phòng") || t.contains("cần thơ") || t.contains("huế")
                    || t.startsWith("tp") || t.startsWith("thành phố")) {
                return "Thành phố";
            }
            return "Tỉnh";
        }
        if ("QUAN".equalsIgnoreCase(maKhuVuc)) {
            String t = tenKhuVuc != null ? tenKhuVuc.toLowerCase() : "";
            if (t.startsWith("huyện")) return "Huyện";
            if (t.startsWith("thị xã")) return "Thị xã";
            return "Quận";
        }
        return maKhuVuc;
    }
}
