package com.example.is009.nguoidung.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class NguoiDungRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String hoTen;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Định dạng email không hợp lệ")
    @Size(max = 150, message = "Email tối đa 150 ký tự")
    private String email;

    @Size(max = 50, message = "Mật khẩu tối đa 50 ký tự")
    private String matKhau;

    @NotBlank(message = "Vai trò không được để trống")
    @Pattern(regexp = "DUOCSI|THUKHO|QUANLY|QUANTRI", message = "Vai trò phải là DUOCSI, THUKHO, QUANLY hoặc QUANTRI")
    private String vaiTro;

    private Integer nhaThuocId;

    @Size(max = 30, message = "Số chứng chỉ hành nghề tối đa 30 ký tự")
    private String soChungChiHanhNghe;

    private boolean kichHoat = true;

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public Integer getNhaThuocId() {
        return nhaThuocId;
    }

    public void setNhaThuocId(Integer nhaThuocId) {
        this.nhaThuocId = nhaThuocId;
    }

    public String getSoChungChiHanhNghe() {
        return soChungChiHanhNghe;
    }

    public void setSoChungChiHanhNghe(String soChungChiHanhNghe) {
        this.soChungChiHanhNghe = soChungChiHanhNghe;
    }

    public boolean isKichHoat() {
        return kichHoat;
    }

    public void setKichHoat(boolean kichHoat) {
        this.kichHoat = kichHoat;
    }
}
