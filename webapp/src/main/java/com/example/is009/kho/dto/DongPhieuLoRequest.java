package com.example.is009.kho.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DongPhieuLoRequest {

    @NotNull(message = "Phải chọn lô thuốc")
    @JsonProperty("lo_thuoc_id")
    @JsonAlias({"loThuocId", "lo_thuoc_id"})
    private Integer loThuocId;

    @NotNull(message = "Số lượng phải lớn hơn 0")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    @JsonProperty("so_luong")
    @JsonAlias({"soLuong", "so_luong"})
    private Integer soLuong;

    public Integer getLoThuocId() { return loThuocId; }
    public void setLoThuocId(Integer loThuocId) { this.loThuocId = loThuocId; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
}
