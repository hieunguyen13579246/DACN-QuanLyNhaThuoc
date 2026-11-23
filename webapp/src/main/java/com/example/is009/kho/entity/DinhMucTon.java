package com.example.is009.kho.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DinhMucTon")
public class DinhMucTon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nha_thuoc_id", nullable = false)
    private Integer nhaThuocId;

    @Column(name = "thuoc_id", nullable = false)
    private Integer thuocId;

    @Column(name = "ton_toi_thieu", nullable = false)
    private Integer tonToiThieu;

    @Column(name = "ton_toi_da", nullable = false)
    private Integer tonToiDa;

    @Column(name = "so_ngay_giao_hang", nullable = false)
    private Integer soNgayGiaoHang;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public Integer getThuocId() { return thuocId; }
    public void setThuocId(Integer thuocId) { this.thuocId = thuocId; }

    public Integer getTonToiThieu() { return tonToiThieu; }
    public void setTonToiThieu(Integer tonToiThieu) { this.tonToiThieu = tonToiThieu; }

    public Integer getTonToiDa() { return tonToiDa; }
    public void setTonToiDa(Integer tonToiDa) { this.tonToiDa = tonToiDa; }

    public Integer getSoNgayGiaoHang() { return soNgayGiaoHang; }
    public void setSoNgayGiaoHang(Integer soNgayGiaoHang) { this.soNgayGiaoHang = soNgayGiaoHang; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
