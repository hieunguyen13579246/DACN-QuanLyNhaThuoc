package com.example.is009.kho.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietPhieuKho")
public class ChiTietPhieuKho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phieu_kho_id", nullable = false)
    private Integer phieuKhoId;

    @Column(name = "lo_thuoc_id", nullable = false)
    private Integer loThuocId;

    @Column(name = "thuoc_id", nullable = false)
    private Integer thuocId;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "thanh_tien", insertable = false, updatable = false, precision = 18, scale = 2)
    private BigDecimal thanhTien;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPhieuKhoId() { return phieuKhoId; }
    public void setPhieuKhoId(Integer phieuKhoId) { this.phieuKhoId = phieuKhoId; }

    public Integer getLoThuocId() { return loThuocId; }
    public void setLoThuocId(Integer loThuocId) { this.loThuocId = loThuocId; }

    public Integer getThuocId() { return thuocId; }
    public void setThuocId(Integer thuocId) { this.thuocId = thuocId; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }

    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
}
