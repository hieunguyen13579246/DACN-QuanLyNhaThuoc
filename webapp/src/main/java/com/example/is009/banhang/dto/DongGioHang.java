package com.example.is009.banhang.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DongGioHang {

    @NotNull(message = "Phải chọn thuốc")
    @JsonProperty("thuoc_id")
    @JsonAlias({"thuocId", "thuoc_id"})
    private Integer thuocId;

    @NotNull(message = "Phải nhập số lượng")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    @JsonProperty("so_luong")
    @JsonAlias({"soLuong", "so_luong"})
    private Integer soLuong;

    @JsonProperty("lo_thuoc_id")
    @JsonAlias({"loThuocId", "lo_thuoc_id"})
    private Integer loThuocId;

    public DongGioHang() {
    }

    public DongGioHang(Integer thuocId, Integer soLuong, Integer loThuocId) {
        this.thuocId = thuocId;
        this.soLuong = soLuong;
        this.loThuocId = loThuocId;
    }

    public Integer getThuocId() { return thuocId; }
    public void setThuocId(Integer thuocId) { this.thuocId = thuocId; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public Integer getLoThuocId() { return loThuocId; }
    public void setLoThuocId(Integer loThuocId) { this.loThuocId = loThuocId; }
}
