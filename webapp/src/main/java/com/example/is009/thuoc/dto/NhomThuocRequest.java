package com.example.is009.thuoc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NhomThuocRequest {

    @NotBlank(message = "Mã nhóm thuốc không được để trống")
    @Size(max = 20, message = "Mã nhóm thuốc tối đa 20 ký tự")
    private String ma;

    @NotBlank(message = "Tên nhóm thuốc không được để trống")
    @Size(max = 100, message = "Tên nhóm thuốc tối đa 100 ký tự")
    private String ten;

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
}
