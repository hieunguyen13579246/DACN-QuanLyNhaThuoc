package com.example.is009.cauhinh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CauHinhRequest {

    @NotBlank(message = "Giá trị cấu hình không được để trống")
    @Size(max = 200, message = "Giá trị cấu hình tối đa 200 ký tự")
    private String giaTri;

    @Size(max = 300, message = "Mô tả tối đa 300 ký tự")
    private String moTa;

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
