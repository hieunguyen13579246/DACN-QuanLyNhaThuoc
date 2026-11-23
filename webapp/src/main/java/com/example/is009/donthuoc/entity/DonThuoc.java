package com.example.is009.donthuoc.entity;

import com.example.is009.common.ThoiGianVN;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "DonThuoc")
public class DonThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_don", nullable = false, length = 30)
    private String maDon;

    @Column(name = "khach_hang_id", nullable = false)
    private Integer khachHangId;

    @Column(name = "nha_thuoc_id", nullable = false)
    private Integer nhaThuocId;

    @Column(name = "ten_bac_si", nullable = false, length = 100)
    private String tenBacSi;

    @Column(name = "co_so_kham", nullable = false, length = 200)
    private String coSoKham;

    @Column(name = "chan_doan", length = 200)
    private String chanDoan;

    @Column(name = "ngay_ke_don", nullable = false)
    private LocalDate ngayKeDon;

    @Column(name = "so_ngay_hieu_luc", nullable = false)
    private Integer soNgayHieuLuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 15)
    private TrangThaiDon trangThai = TrangThaiDon.CHUA_BAN;

    @Column(name = "nguoi_tiep_nhan_id", nullable = false)
    private Integer nguoiTiepNhanId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public LocalDate ngayHetHieuLuc() {
        if (ngayKeDon == null || soNgayHieuLuc == null) {
            return null;
        }
        return ngayKeDon.plusDays(soNgayHieuLuc);
    }

    public boolean conHieuLuc() {
        LocalDate hetHan = ngayHetHieuLuc();
        if (hetHan == null) {
            return false;
        }
        return !hetHan.isBefore(ThoiGianVN.homNay())
                && (trangThai == TrangThaiDon.CHUA_BAN || trangThai == TrangThaiDon.BAN_MOT_PHAN);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaDon() {
        return maDon;
    }

    public void setMaDon(String maDon) {
        this.maDon = maDon;
    }

    public Integer getKhachHangId() {
        return khachHangId;
    }

    public void setKhachHangId(Integer khachHangId) {
        this.khachHangId = khachHangId;
    }

    public Integer getNhaThuocId() {
        return nhaThuocId;
    }

    public void setNhaThuocId(Integer nhaThuocId) {
        this.nhaThuocId = nhaThuocId;
    }

    public String getTenBacSi() {
        return tenBacSi;
    }

    public void setTenBacSi(String tenBacSi) {
        this.tenBacSi = tenBacSi;
    }

    public String getCoSoKham() {
        return coSoKham;
    }

    public void setCoSoKham(String coSoKham) {
        this.coSoKham = coSoKham;
    }

    public String getChanDoan() {
        return chanDoan;
    }

    public void setChanDoan(String chanDoan) {
        this.chanDoan = chanDoan;
    }

    public LocalDate getNgayKeDon() {
        return ngayKeDon;
    }

    public void setNgayKeDon(LocalDate ngayKeDon) {
        this.ngayKeDon = ngayKeDon;
    }

    public Integer getSoNgayHieuLuc() {
        return soNgayHieuLuc;
    }

    public void setSoNgayHieuLuc(Integer soNgayHieuLuc) {
        this.soNgayHieuLuc = soNgayHieuLuc;
    }

    public TrangThaiDon getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiDon trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getNguoiTiepNhanId() {
        return nguoiTiepNhanId;
    }

    public void setNguoiTiepNhanId(Integer nguoiTiepNhanId) {
        this.nguoiTiepNhanId = nguoiTiepNhanId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
