package com.example.is009.kho.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TonKhoId implements Serializable {

    @Column(name = "nha_thuoc_id")
    private Integer nhaThuocId;

    @Column(name = "lo_thuoc_id")
    private Integer loThuocId;

    public TonKhoId() {
    }

    public TonKhoId(Integer nhaThuocId, Integer loThuocId) {
        this.nhaThuocId = nhaThuocId;
        this.loThuocId = loThuocId;
    }

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public Integer getLoThuocId() { return loThuocId; }
    public void setLoThuocId(Integer loThuocId) { this.loThuocId = loThuocId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TonKhoId that)) return false;
        return Objects.equals(nhaThuocId, that.nhaThuocId) && Objects.equals(loThuocId, that.loThuocId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nhaThuocId, loThuocId);
    }
}
