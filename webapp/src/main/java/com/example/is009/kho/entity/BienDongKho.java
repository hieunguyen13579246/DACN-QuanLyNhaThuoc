package com.example.is009.kho.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BienDongKho")
public class BienDongKho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nha_thuoc_id", nullable = false)
    private Integer nhaThuocId;

    @Column(name = "lo_thuoc_id", nullable = false)
    private Integer loThuocId;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_bien_dong", nullable = false, length = 20)
    private LoaiBienDong loaiBienDong;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "ton_sau", nullable = false)
    private Integer tonSau;

    @Column(name = "tham_chieu_loai", nullable = false, length = 20)
    private String thamChieuLoai;

    @Column(name = "tham_chieu_id", nullable = false)
    private Integer thamChieuId;

    @Column(name = "thoi_gian", nullable = false)
    private LocalDateTime thoiGian;

    @Column(name = "nguoi_thuc_hien_id", nullable = false)
    private Integer nguoiThucHienId;

    @Column(name = "ghi_chu", length = 200)
    private String ghiChu;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public Integer getLoThuocId() { return loThuocId; }
    public void setLoThuocId(Integer loThuocId) { this.loThuocId = loThuocId; }

    public LoaiBienDong getLoaiBienDong() { return loaiBienDong; }
    public void setLoaiBienDong(LoaiBienDong loaiBienDong) { this.loaiBienDong = loaiBienDong; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public Integer getTonSau() { return tonSau; }
    public void setTonSau(Integer tonSau) { this.tonSau = tonSau; }

    public String getThamChieuLoai() { return thamChieuLoai; }
    public void setThamChieuLoai(String thamChieuLoai) { this.thamChieuLoai = thamChieuLoai; }

    public Integer getThamChieuId() { return thamChieuId; }
    public void setThamChieuId(Integer thamChieuId) { this.thamChieuId = thamChieuId; }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }

    public Integer getNguoiThucHienId() { return nguoiThucHienId; }
    public void setNguoiThucHienId(Integer nguoiThucHienId) { this.nguoiThucHienId = nguoiThucHienId; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
