package com.example.is009.banhang.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietGiaoDich")
public class ChiTietGiaoDich {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "giao_dich_id", nullable = false)
    private Integer giaoDichId;

    @Column(name = "lo_thuoc_id", nullable = false)
    private Integer loThuocId;

    @Column(name = "thuoc_id", nullable = false)
    private Integer thuocId;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "gia_ban", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBan;

    @Column(name = "gia_von", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaVon;

    @Column(name = "thanh_tien", insertable = false, updatable = false, precision = 18, scale = 2)
    private BigDecimal thanhTien;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getGiaoDichId() { return giaoDichId; }
    public void setGiaoDichId(Integer giaoDichId) { this.giaoDichId = giaoDichId; }

    public Integer getLoThuocId() { return loThuocId; }
    public void setLoThuocId(Integer loThuocId) { this.loThuocId = loThuocId; }

    public Integer getThuocId() { return thuocId; }
    public void setThuocId(Integer thuocId) { this.thuocId = thuocId; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public BigDecimal getGiaBan() { return giaBan; }
    public void setGiaBan(BigDecimal giaBan) { this.giaBan = giaBan; }

    public BigDecimal getGiaVon() { return giaVon; }
    public void setGiaVon(BigDecimal giaVon) { this.giaVon = giaVon; }

    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
}
