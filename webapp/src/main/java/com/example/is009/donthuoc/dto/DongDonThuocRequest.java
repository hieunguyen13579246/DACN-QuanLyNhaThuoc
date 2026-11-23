package com.example.is009.donthuoc.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DongDonThuocRequest {

    @NotNull(message = "Thuốc bắt buộc chọn")
    @JsonProperty("thuoc_id")
    @JsonAlias({"thuocId", "thuoc_id"})
    private Integer thuocId;

    @NotNull(message = "Số lượng kê không được để trống")
    @Min(value = 1, message = "Số lượng kê phải lớn hơn 0")
    @JsonProperty("so_luong_ke")
    @JsonAlias({"soLuongKe", "so_luong_ke"})
    private Integer soLuongKe;

    @Size(max = 200, message = "Liều dùng tối đa 200 ký tự")
    @JsonProperty("lieu_dung")
    @JsonAlias({"lieuDung", "lieu_dung"})
    private String lieuDung;

    public Integer getThuocId() {
        return thuocId;
    }

    public void setThuocId(Integer thuocId) {
        this.thuocId = thuocId;
    }

    public Integer getSoLuongKe() {
        return soLuongKe;
    }

    public void setSoLuongKe(Integer soLuongKe) {
        this.soLuongKe = soLuongKe;
    }

    public String getLieuDung() {
        return lieuDung;
    }

    public void setLieuDung(String lieuDung) {
        this.lieuDung = lieuDung;
    }
}
