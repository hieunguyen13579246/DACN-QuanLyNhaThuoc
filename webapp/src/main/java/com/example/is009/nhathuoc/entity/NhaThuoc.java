package com.example.is009.nhathuoc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "NhaThuoc")
public class NhaThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma", nullable = false, length = 20)
    private String ma;

    @Column(name = "ten", nullable = false, length = 150)
    private String ten;

    @Column(name = "dia_chi", nullable = false, length = 250)
    private String diaChi;

    @Column(name = "khu_vuc_id", nullable = false)
    private Integer khuVucId;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "trang_thai", nullable = false, length = 15)
    private String trangThai = "HOAT_DONG";

    @Column(name = "ngay_khai_truong")
    private LocalDate ngayKhaiTruong;

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

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public Integer getKhuVucId() {
        return khuVucId;
    }

    public void setKhuVucId(Integer khuVucId) {
        this.khuVucId = khuVucId;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDate getNgayKhaiTruong() {
        return ngayKhaiTruong;
    }

    public void setNgayKhaiTruong(LocalDate ngayKhaiTruong) {
        this.ngayKhaiTruong = ngayKhaiTruong;
    }
}
