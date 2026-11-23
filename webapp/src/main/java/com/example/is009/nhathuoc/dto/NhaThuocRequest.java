package com.example.is009.nhathuoc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class NhaThuocRequest {

    @NotBlank(message = "Mã nhà thuốc không được để trống")
    @Size(max = 20, message = "Mã nhà thuốc tối đa 20 ký tự")
    private String ma;

    @NotBlank(message = "Tên nhà thuốc không được để trống")
    @Size(max = 150, message = "Tên nhà thuốc tối đa 150 ký tự")
    private String ten;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 250, message = "Địa chỉ tối đa 250 ký tự")
    private String diaChi;

    @NotNull(message = "Khu vực bắt buộc chọn")
    private Integer khuVucId;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String soDienThoai;

    @Pattern(regexp = "HOAT_DONG|TAM_NGUNG", message = "Trạng thái phải là HOAT_DONG hoặc TAM_NGUNG")
    private String trangThai = "HOAT_DONG";

    private LocalDate ngayKhaiTruong;

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

    public Integer getKhuVucId() {
        return khuVucId;
    }

    public void setKhuVucId(Integer khuVucId) {
        this.khuVucId = khuVucId;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDate getNgayKhaiTruong() {
        return ngayKhaiTruong;
    }

    public void setNgayKhaiTruong(LocalDate ngayKhaiTruong) {
        this.ngayKhaiTruong = ngayKhaiTruong;
    }
}
