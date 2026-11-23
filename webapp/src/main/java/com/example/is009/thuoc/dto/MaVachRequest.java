package com.example.is009.thuoc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class MaVachRequest {

    @NotNull(message = "Thuốc bắt buộc chọn")
    private Integer thuocId;

    @NotBlank(message = "Mã vạch không được để trống")
    @Size(max = 50, message = "Mã vạch tối đa 50 ký tự")
    private String maVach;

    @NotBlank(message = "Loại mã vạch bắt buộc chọn")
    @Pattern(regexp = "EAN13|GTIN14|CODE128|NOI_BO", message = "Loại mã vạch phải là EAN13, GTIN14, CODE128 hoặc NOI_BO")
    private String loaiMaVach = "EAN13";

    private boolean laMaChinh;

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

    public String getLoaiMaVach() {
        return loaiMaVach;
    }

    public void setLoaiMaVach(String loaiMaVach) {
        this.loaiMaVach = loaiMaVach;
    }

    public boolean isLaMaChinh() {
        return laMaChinh;
    }

    public void setLaMaChinh(boolean laMaChinh) {
        this.laMaChinh = laMaChinh;
    }
}
