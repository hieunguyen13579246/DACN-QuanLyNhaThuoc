package com.example.is009.banhang.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ChiTietGiaoDichDTO {

    private Integer id;
    private Integer thuocId;
    private String maThuoc;
    private String tenThuoc;
    private String donViTinh;
    private Integer loThuocId;
    private String soLo;
    private LocalDate hanDung;
    private Integer soLuong;
    private BigDecimal giaBan;
    private BigDecimal thanhTien;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getThuocId() { return thuocId; }
    public void setThuocId(Integer thuocId) { this.thuocId = thuocId; }

    public String getMaThuoc() { return maThuoc; }
    public void setMaThuoc(String maThuoc) { this.maThuoc = maThuoc; }

    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public Integer getLoThuocId() { return loThuocId; }
    public void setLoThuocId(Integer loThuocId) { this.loThuocId = loThuocId; }

    public String getSoLo() { return soLo; }
    public void setSoLo(String soLo) { this.soLo = soLo; }

    public LocalDate getHanDung() { return hanDung; }
    public void setHanDung(LocalDate hanDung) { this.hanDung = hanDung; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public BigDecimal getGiaBan() { return giaBan; }
    public void setGiaBan(BigDecimal giaBan) { this.giaBan = giaBan; }

    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
}
