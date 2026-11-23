package com.example.is009.kho.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TonKho")
public class TonKho {

    @EmbeddedId
    private TonKhoId id;

    @Column(name = "so_luong_ton", nullable = false)
    private Integer soLuongTon;

    @Column(name = "so_luong_giu_cho", nullable = false)
    private Integer soLuongGiuCho;

    @Column(name = "so_luong_kha_dung", insertable = false, updatable = false)
    private Integer soLuongKhaDung;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public TonKhoId getId() { return id; }
    public void setId(TonKhoId id) { this.id = id; }

    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer soLuongTon) { this.soLuongTon = soLuongTon; }

    public Integer getSoLuongGiuCho() { return soLuongGiuCho; }
    public void setSoLuongGiuCho(Integer soLuongGiuCho) { this.soLuongGiuCho = soLuongGiuCho; }

    public Integer getSoLuongKhaDung() { return soLuongKhaDung; }
    public void setSoLuongKhaDung(Integer soLuongKhaDung) { this.soLuongKhaDung = soLuongKhaDung; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
