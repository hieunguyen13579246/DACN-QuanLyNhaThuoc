package com.example.is009.thuoc.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ThuocRequest {

    @NotBlank(message = "Mã thuốc không được để trống")
    @Size(max = 30, message = "Mã thuốc tối đa 30 ký tự")
    private String ma;

    @NotBlank(message = "Tên thuốc không được để trống")
    @Size(max = 200, message = "Tên thuốc tối đa 200 ký tự")
    private String ten;

    @NotBlank(message = "Hoạt chất không được để trống")
    @Size(max = 200, message = "Hoạt chất tối đa 200 ký tự")
    private String hoatChat;

    @Size(max = 50, message = "Hàm lượng tối đa 50 ký tự")
    private String hamLuong;

    @Size(max = 50, message = "Dạng bào chế tối đa 50 ký tự")
    private String dangBaoChe;

    @NotBlank(message = "Đơn vị tính không được để trống")
    @Size(max = 20, message = "Đơn vị tính tối đa 20 ký tự")
    private String donViTinh;

    @NotNull(message = "Nhóm thuốc bắt buộc chọn")
    private Integer nhomThuocId;

    @Size(max = 150, message = "Nhà sản xuất tối đa 150 ký tự")
    private String nhaSanXuat;

    private boolean canKeDon;

    @NotNull(message = "Giá bán lẻ không được để trống")
    @DecimalMin(value = "1", message = "Giá bán lẻ phải lớn hơn 0")
    private BigDecimal giaBanLe;

    @Pattern(regexp = "DANG_BAN|NGUNG_BAN", message = "Trạng thái phải là DANG_BAN hoặc NGUNG_BAN")
    private String trangThai = "DANG_BAN";

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String moTa;

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

    public String getHoatChat() {
        return hoatChat;
    }

    public void setHoatChat(String hoatChat) {
        this.hoatChat = hoatChat;
    }

    public String getHamLuong() {
        return hamLuong;
    }

    public void setHamLuong(String hamLuong) {
        this.hamLuong = hamLuong;
    }

    public String getDangBaoChe() {
        return dangBaoChe;
    }

    public void setDangBaoChe(String dangBaoChe) {
        this.dangBaoChe = dangBaoChe;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public Integer getNhomThuocId() {
        return nhomThuocId;
    }

    public void setNhomThuocId(Integer nhomThuocId) {
        this.nhomThuocId = nhomThuocId;
    }

    public String getNhaSanXuat() {
        return nhaSanXuat;
    }

    public void setNhaSanXuat(String nhaSanXuat) {
        this.nhaSanXuat = nhaSanXuat;
    }

    public boolean isCanKeDon() {
        return canKeDon;
    }

    public void setCanKeDon(boolean canKeDon) {
        this.canKeDon = canKeDon;
    }

    public BigDecimal getGiaBanLe() {
        return giaBanLe;
    }

    public void setGiaBanLe(BigDecimal giaBanLe) {
        this.giaBanLe = giaBanLe;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
