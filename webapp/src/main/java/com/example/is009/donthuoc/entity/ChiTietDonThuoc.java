package com.example.is009.donthuoc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ChiTietDonThuoc")
public class ChiTietDonThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "don_thuoc_id", nullable = false)
    private Integer donThuocId;

    @Column(name = "thuoc_id", nullable = false)
    private Integer thuocId;

    @Column(name = "so_luong_ke", nullable = false)
    private Integer soLuongKe;

    @Column(name = "so_luong_da_ban", nullable = false)
    private Integer soLuongDaBan = 0;

    @Column(name = "lieu_dung", length = 200)
    private String lieuDung;

    public int soLuongConDuocBan() {
        int ke = soLuongKe != null ? soLuongKe : 0;
        int ban = soLuongDaBan != null ? soLuongDaBan : 0;
        return Math.max(0, ke - ban);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDonThuocId() {
        return donThuocId;
    }

    public void setDonThuocId(Integer donThuocId) {
        this.donThuocId = donThuocId;
    }

    public Integer getThuocId() {
        return thuocId;
    }

    public void setThuocId(Integer thuocId) {
        this.thuocId = thuocId;
    }

    public Integer getSoLuongKe() {
        return soLuongKe;
    }

    public void setSoLuongKe(Integer soLuongKe) {
        this.soLuongKe = soLuongKe;
    }

    public Integer getSoLuongDaBan() {
        return soLuongDaBan;
    }

    public void setSoLuongDaBan(Integer soLuongDaBan) {
        this.soLuongDaBan = soLuongDaBan;
    }

    public String getLieuDung() {
        return lieuDung;
    }

    public void setLieuDung(String lieuDung) {
        this.lieuDung = lieuDung;
    }
}
