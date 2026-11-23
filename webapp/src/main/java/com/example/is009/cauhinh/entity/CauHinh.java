package com.example.is009.cauhinh.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CauHinh")
public class CauHinh {

    @Id
    @Column(name = "khoa", nullable = false, length = 50)
    private String khoa;

    @Column(name = "gia_tri", nullable = false, length = 200)
    private String giaTri;

    @Column(name = "mo_ta", length = 300)
    private String moTa;

    public int toInt() {
        try {
            return Integer.parseInt(giaTri != null ? giaTri.trim() : "0");
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getKhoa() {
        return khoa;
    }

    public void setKhoa(String khoa) {
        this.khoa = khoa;
    }

    public String getGiaTri() {
        return giaTri;
    }

    public void setGiaTri(String giaTri) {
        this.giaTri = giaTri;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
