package com.example.is009.thongke.dto;

import java.math.BigDecimal;

public class TopThuocDTO {

    private Integer thuocId;
    private String maThuoc;
    private String tenThuoc;
    private String hoatChat;
    private String donViTinh;
    private String tenNhom;
    private int tongSoLuong;
    private BigDecimal tongDoanhThu;
    private int soGiaoDich;
    private String khuVuc;
    private int xepHang;

    public String getKhuVuc() { return khuVuc; }
    public void setKhuVuc(String khuVuc) { this.khuVuc = khuVuc; }

    public int getXepHang() { return xepHang; }
    public void setXepHang(int xepHang) { this.xepHang = xepHang; }
    public Integer getThuocId() { return thuocId; }
    public void setThuocId(Integer thuocId) { this.thuocId = thuocId; }

    public String getMaThuoc() { return maThuoc; }
    public void setMaThuoc(String maThuoc) { this.maThuoc = maThuoc; }

    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }

    public String getHoatChat() { return hoatChat; }
    public void setHoatChat(String hoatChat) { this.hoatChat = hoatChat; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public String getTenNhom() { return tenNhom; }
    public void setTenNhom(String tenNhom) { this.tenNhom = tenNhom; }

    public int getTongSoLuong() { return tongSoLuong; }
    public void setTongSoLuong(int tongSoLuong) { this.tongSoLuong = tongSoLuong; }

    public BigDecimal getTongDoanhThu() { return tongDoanhThu; }
    public void setTongDoanhThu(BigDecimal tongDoanhThu) { this.tongDoanhThu = tongDoanhThu; }

    public int getSoGiaoDich() { return soGiaoDich; }
    public void setSoGiaoDich(int soGiaoDich) { this.soGiaoDich = soGiaoDich; }
}
