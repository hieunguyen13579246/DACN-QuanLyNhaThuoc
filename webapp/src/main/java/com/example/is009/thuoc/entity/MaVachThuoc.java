package com.example.is009.thuoc.entity;

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
@Table(name = "MaVachThuoc")
public class MaVachThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "thuoc_id", nullable = false)
    private Integer thuocId;

    @Column(name = "ma_vach", nullable = false, length = 50)
    private String maVach;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_ma_vach", nullable = false, length = 10)
    private LoaiMaVach loaiMaVach;

    @Column(name = "la_ma_chinh", nullable = false)
    private boolean laMaChinh;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getThuocId() {
        return thuocId;
    }

    public void setThuocId(Integer thuocId) {
        this.thuocId = thuocId;
    }

    public String getMaVach() {
        return maVach;
    }

    public void setMaVach(String maVach) {
        this.maVach = maVach;
    }

    public LoaiMaVach getLoaiMaVach() {
        return loaiMaVach;
    }

    public void setLoaiMaVach(LoaiMaVach loaiMaVach) {
        this.loaiMaVach = loaiMaVach;
    }

    public boolean isLaMaChinh() {
        return laMaChinh;
    }

    public void setLaMaChinh(boolean laMaChinh) {
        this.laMaChinh = laMaChinh;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
