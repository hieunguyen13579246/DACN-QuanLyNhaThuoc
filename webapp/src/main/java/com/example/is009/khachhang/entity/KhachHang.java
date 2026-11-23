package com.example.is009.khachhang.entity;

import com.example.is009.common.SoDienThoaiUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "KhachHang")
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma", nullable = false, length = 20)
    private String ma;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "gioi_tinh", length = 5)
    private String gioiTinh;

    @Column(name = "nam_sinh")
    private Short namSinh;

    @Column(name = "khu_vuc_id")
    private Integer khuVucId;

    @Column(name = "dia_chi", length = 250)
    private String diaChi;

    @Column(name = "ghi_chu_di_ung", length = 500)
    private String ghiChuDiUng;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public String anSoDienThoai() {
        return SoDienThoaiUtils.cheSoDienThoai(soDienThoai);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public Short getNamSinh() {
        return namSinh;
    }

    public void setNamSinh(Short namSinh) {
        this.namSinh = namSinh;
    }

    public Integer getKhuVucId() {
        return khuVucId;
    }

    public void setKhuVucId(Integer khuVucId) {
        this.khuVucId = khuVucId;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getGhiChuDiUng() {
        return ghiChuDiUng;
    }

    public void setGhiChuDiUng(String ghiChuDiUng) {
        this.ghiChuDiUng = ghiChuDiUng;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
