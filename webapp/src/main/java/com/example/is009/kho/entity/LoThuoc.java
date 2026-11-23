package com.example.is009.kho.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LoThuoc")
public class LoThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "thuoc_id", nullable = false)
    private Integer thuocId;

    @Column(name = "nha_cung_cap_id")
    private Integer nhaCungCapId;

    @Column(name = "so_lo", nullable = false, length = 50)
    private String soLo;

    @Column(name = "ma_vach_lo", length = 100)
    private String maVachLo;

    @Column(name = "ngay_san_xuat", nullable = false)
    private LocalDate ngaySanXuat;

    @Column(name = "han_dung", nullable = false)
    private LocalDate hanDung;

    @Column(name = "gia_nhap", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaNhap;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getThuocId() { return thuocId; }
    public void setThuocId(Integer thuocId) { this.thuocId = thuocId; }

    public Integer getNhaCungCapId() { return nhaCungCapId; }
    public void setNhaCungCapId(Integer nhaCungCapId) { this.nhaCungCapId = nhaCungCapId; }

    public String getSoLo() { return soLo; }
    public void setSoLo(String soLo) { this.soLo = soLo; }

    public String getMaVachLo() { return maVachLo; }
    public void setMaVachLo(String maVachLo) { this.maVachLo = maVachLo; }

    public LocalDate getNgaySanXuat() { return ngaySanXuat; }
    public void setNgaySanXuat(LocalDate ngaySanXuat) { this.ngaySanXuat = ngaySanXuat; }

    public LocalDate getHanDung() { return hanDung; }
    public void setHanDung(LocalDate hanDung) { this.hanDung = hanDung; }

    public BigDecimal getGiaNhap() { return giaNhap; }
    public void setGiaNhap(BigDecimal giaNhap) { this.giaNhap = giaNhap; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
