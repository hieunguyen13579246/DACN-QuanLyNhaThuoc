package com.example.is009.khuvuc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "KhuVuc")
public class KhuVuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma", nullable = false, length = 20)
    private String ma;

    @Column(name = "ten", nullable = false, length = 100)
    private String ten;

    @Column(name = "cap", nullable = false, length = 5)
    private String cap;

    @Column(name = "khu_vuc_cha_id")
    private Integer khuVucChaId;

    @Column(name = "dan_so")
    private Integer danSo;

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

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public Integer getKhuVucChaId() {
        return khuVucChaId;
    }

    public void setKhuVucChaId(Integer khuVucChaId) {
        this.khuVucChaId = khuVucChaId;
    }

    public Integer getDanSo() {
        return danSo;
    }

    public void setDanSo(Integer danSo) {
        this.danSo = danSo;
    }
}
