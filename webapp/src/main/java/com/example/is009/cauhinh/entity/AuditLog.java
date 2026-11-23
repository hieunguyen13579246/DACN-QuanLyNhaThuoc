package com.example.is009.cauhinh.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "AuditLog")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nguoi_dung_id")
    private Integer nguoiDungId;

    @Column(name = "hanh_dong", nullable = false, length = 100)
    private String hanhDong;

    @Column(name = "doi_tuong", nullable = false, length = 50)
    private String doiTuong;

    @Column(name = "doi_tuong_id")
    private Integer doiTuongId;

    @Column(name = "du_lieu_cu")
    private String duLieuCu;

    @Column(name = "du_lieu_moi")
    private String duLieuMoi;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(Integer nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    public String getHanhDong() {
        return hanhDong;
    }

    public void setHanhDong(String hanhDong) {
        this.hanhDong = hanhDong;
    }

    public String getDoiTuong() {
        return doiTuong;
    }

    public void setDoiTuong(String doiTuong) {
        this.doiTuong = doiTuong;
    }

    public Integer getDoiTuongId() {
        return doiTuongId;
    }

    public void setDoiTuongId(Integer doiTuongId) {
        this.doiTuongId = doiTuongId;
    }

    public String getDuLieuCu() {
        return duLieuCu;
    }

    public void setDuLieuCu(String duLieuCu) {
        this.duLieuCu = duLieuCu;
    }

    public String getDuLieuMoi() {
        return duLieuMoi;
    }

    public void setDuLieuMoi(String duLieuMoi) {
        this.duLieuMoi = duLieuMoi;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
