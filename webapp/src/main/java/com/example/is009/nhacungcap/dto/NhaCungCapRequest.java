package com.example.is009.nhacungcap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class NhaCungCapRequest {

    @NotBlank(message = "Mã nhà cung cấp không được để trống")
    @Size(max = 20, message = "Mã NCC tối đa 20 ký tự")
    private String ma;

    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    @Size(max = 200, message = "Tên NCC tối đa 200 ký tự")
    private String ten;

    @Size(max = 250, message = "Địa chỉ tối đa 250 ký tự")
    private String diaChi;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String soDienThoai;

    @Size(max = 20, message = "Mã số thuế tối đa 20 ký tự")
    private String maSoThue;

    @Pattern(regexp = "HOAT_DONG|NGUNG", message = "Trạng thái phải là HOAT_DONG hoặc NGUNG")
    private String trangThai = "HOAT_DONG";

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

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getMaSoThue() {
        return maSoThue;
    }

    public void setMaSoThue(String maSoThue) {
        this.maSoThue = maSoThue;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
