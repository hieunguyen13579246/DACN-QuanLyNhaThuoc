package com.example.is009.nguoidung.entity;

import com.example.is009.security.VaiTro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "NguoiDung")
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false, length = 10)
    private VaiTro vaiTro;

    @Column(name = "nha_thuoc_id")
    private Integer nhaThuocId;

    @Column(name = "so_chung_chi_hanh_nghe", length = 30)
    private String soChungChiHanhNghe;

    @Column(name = "kich_hoat", nullable = false)
    private boolean kichHoat = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public boolean coQuyenDuyetPhieu() {
        return vaiTro == VaiTro.QUANLY || vaiTro == VaiTro.QUANTRI;
    }

    public boolean thuocNhaThuoc(int targetNhaThuocId) {
        if (vaiTro == VaiTro.QUANTRI) {
            return true;
        }
        return nhaThuocId != null && nhaThuocId == targetNhaThuocId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro = vaiTro;
    }

    public Integer getNhaThuocId() {
        return nhaThuocId;
    }

    public void setNhaThuocId(Integer nhaThuocId) {
        this.nhaThuocId = nhaThuocId;
    }

    public String getSoChungChiHanhNghe() {
        return soChungChiHanhNghe;
    }

    public void setSoChungChiHanhNghe(String soChungChiHanhNghe) {
        this.soChungChiHanhNghe = soChungChiHanhNghe;
    }

    public boolean isKichHoat() {
        return kichHoat;
    }

    public void setKichHoat(boolean kichHoat) {
        this.kichHoat = kichHoat;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
