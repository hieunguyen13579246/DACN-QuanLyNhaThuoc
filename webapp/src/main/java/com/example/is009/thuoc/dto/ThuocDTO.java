package com.example.is009.thuoc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ThuocDTO(
        Integer id,
        String ma,
        String ten,
        String hoatChat,
        String hamLuong,
        String dangBaoChe,
        String donViTinh,
        Integer nhomThuocId,
        String tenNhomThuoc,
        String nhaSanXuat,
        boolean canKeDon,
        BigDecimal giaBanLe,
        String trangThai,
        String moTa,
        String maVachChinh
) {
    @JsonProperty("maThuoc")
    public String getMaThuoc() {
        return ma;
    }

    @JsonProperty("tenThuoc")
    public String getTenThuoc() {
        return ten;
    }

    @JsonProperty("thuocKeDon")
    public boolean isThuocKeDon() {
        return canKeDon;
    }
}
