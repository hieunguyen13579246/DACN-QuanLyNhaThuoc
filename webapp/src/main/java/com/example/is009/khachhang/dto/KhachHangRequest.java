package com.example.is009.khachhang.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class KhachHangRequest {

    @Size(max = 20, message = "Mã khách hàng tối đa 20 ký tự")
    private String ma;

    @NotBlank(message = "Họ tên khách hàng không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String hoTen;

    @NotBlank(message = "Số điện thoại khách hàng không được để trống")
    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String soDienThoai;

    @Size(max = 5, message = "Giới tính tối đa 5 ký tự")
    private String gioiTinh;

    @Min(value = 1900, message = "Năm sinh từ 1900 trở lên")
    @Max(value = 2100, message = "Năm sinh không hợp lệ")
    private Short namSinh;

    private Integer khuVucId;

    @Size(max = 250, message = "Địa chỉ tối đa 250 ký tự")
    private String diaChi;

    @Size(max = 500, message = "Ghi chú dị ứng tối đa 500 ký tự")
    private String ghiChuDiUng;

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public Short getNamSinh() {
        return namSinh;
    }

    public void setNamSinh(Short namSinh) {
        this.namSinh = namSinh;
    }

    public Integer getKhuVucId() {
        return khuVucId;
    }

    public void setKhuVucId(Integer khuVucId) {
        this.khuVucId = khuVucId;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getGhiChuDiUng() {
        return ghiChuDiUng;
    }

    public void setGhiChuDiUng(String ghiChuDiUng) {
        this.ghiChuDiUng = ghiChuDiUng;
    }
}
