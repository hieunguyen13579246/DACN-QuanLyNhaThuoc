package com.example.is009.canhbao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TonChamDTO {

    private Integer nhaThuocId;
    private String tenNhaThuoc;
    private Integer loThuocId;
    private String soLo;
    private Integer thuocId;
    private String maThuoc;
    private String tenThuoc;
    private String donViTinh;
    private LocalDate hanDung;
    private Integer soLuongTon;
    private BigDecimal giaTriTon;
    private Integer soLuongBan30Ngay;
    private Integer soNgayKhongBan;

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public String getTenNhaThuoc() { return tenNhaThuoc; }
    public void setTenNhaThuoc(String tenNhaThuoc) { this.tenNhaThuoc = tenNhaThuoc; }

    public Integer getLoThuocId() { return loThuocId; }
    public void setLoThuocId(Integer loThuocId) { this.loThuocId = loThuocId; }

    public String getSoLo() { return soLo; }
    public void setSoLo(String soLo) { this.soLo = soLo; }

    public Integer getThuocId() { return thuocId; }
    public void setThuocId(Integer thuocId) { this.thuocId = thuocId; }

    public String getMaThuoc() { return maThuoc; }
    public void setMaThuoc(String maThuoc) { this.maThuoc = maThuoc; }

    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public LocalDate getHanDung() { return hanDung; }
    public void setHanDung(LocalDate hanDung) { this.hanDung = hanDung; }

    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer soLuongTon) { this.soLuongTon = soLuongTon; }

    public BigDecimal getGiaTriTon() { return giaTriTon; }
    public void setGiaTriTon(BigDecimal giaTriTon) { this.giaTriTon = giaTriTon; }

    public Integer getSoLuongBan30Ngay() { return soLuongBan30Ngay; }
    public void setSoLuongBan30Ngay(Integer soLuongBan30Ngay) { this.soLuongBan30Ngay = soLuongBan30Ngay; }

    public Integer getSoNgayKhongBan() { return soNgayKhongBan; }
    public void setSoNgayKhongBan(Integer soNgayKhongBan) { this.soNgayKhongBan = soNgayKhongBan; }
}
