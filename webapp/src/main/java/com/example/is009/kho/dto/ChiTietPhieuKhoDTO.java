package com.example.is009.kho.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ChiTietPhieuKhoDTO {

    private Integer id;
    private Integer thuocId;
    private String maThuoc;
    private String tenThuoc;
    private String donViTinh;
    private Integer loThuocId;
    private String soLo;
    private LocalDate ngaySanXuat;
    private LocalDate hanDung;
    private Integer soLuong;
    private BigDecimal donGia;
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

    public LocalDate getNgaySanXuat() { return ngaySanXuat; }
    public void setNgaySanXuat(LocalDate ngaySanXuat) { this.ngaySanXuat = ngaySanXuat; }

    public LocalDate getHanDung() { return hanDung; }
    public void setHanDung(LocalDate hanDung) { this.hanDung = hanDung; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }

    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
}
