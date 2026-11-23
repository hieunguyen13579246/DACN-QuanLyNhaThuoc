package com.example.is009.khuvuc.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class KhuVucRequest {

    @NotBlank(message = "Mã khu vực không được để trống")
    @Size(max = 20, message = "Mã khu vực tối đa 20 ký tự")
    private String ma;

    @NotBlank(message = "Tên khu vực không được để trống")
    @Size(max = 100, message = "Tên khu vực tối đa 100 ký tự")
    private String ten;

    @NotBlank(message = "Cấp khu vực bắt buộc chọn")
    @Pattern(regexp = "TINH|QUAN", message = "Cấp hành chính phải là TINH hoặc QUAN")
    private String cap;

    private Integer khuVucChaId;

    @Min(value = 0, message = "Dân số phải lớn hơn hoặc bằng 0")
    private Integer danSo;

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
