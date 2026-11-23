package com.example.is009.donthuoc.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class TaoDonThuocRequest {

    @NotNull(message = "Khách hàng bắt buộc chọn")
    private Integer khachHangId;

    private Integer nhaThuocId;

    @NotBlank(message = "Tên bác sĩ kê đơn không được để trống")
    @Size(max = 100, message = "Tên bác sĩ tối đa 100 ký tự")
    private String tenBacSi;

    @NotBlank(message = "Cơ sở khám không được để trống")
    @Size(max = 200, message = "Cơ sở khám tối đa 200 ký tự")
    private String coSoKham;

    @Size(max = 200, message = "Chẩn đoán tối đa 200 ký tự")
    private String chanDoan;

    @NotNull(message = "Ngày kê đơn không được để trống")
    private LocalDate ngayKeDon;

    private Integer soNgayHieuLuc;

    @NotEmpty(message = "Đơn thuốc phải có ít nhất một dòng thuốc kê đơn")
    @Valid
    private List<DongDonThuocRequest> items;

    public Integer getKhachHangId() {
        return khachHangId;
    }

    public void setKhachHangId(Integer khachHangId) {
        this.khachHangId = khachHangId;
    }

    public Integer getNhaThuocId() {
        return nhaThuocId;
    }

    public void setNhaThuocId(Integer nhaThuocId) {
        this.nhaThuocId = nhaThuocId;
    }

    public String getTenBacSi() {
        return tenBacSi;
    }

    public void setTenBacSi(String tenBacSi) {
        this.tenBacSi = tenBacSi;
    }

    public String getCoSoKham() {
        return coSoKham;
    }

    public void setCoSoKham(String coSoKham) {
        this.coSoKham = coSoKham;
    }

    public String getChanDoan() {
        return chanDoan;
    }

    public void setChanDoan(String chanDoan) {
        this.chanDoan = chanDoan;
    }

    public LocalDate getNgayKeDon() {
        return ngayKeDon;
    }

    public void setNgayKeDon(LocalDate ngayKeDon) {
        this.ngayKeDon = ngayKeDon;
    }

    public Integer getSoNgayHieuLuc() {
        return soNgayHieuLuc;
    }

    public void setSoNgayHieuLuc(Integer soNgayHieuLuc) {
        this.soNgayHieuLuc = soNgayHieuLuc;
    }

    public List<DongDonThuocRequest> getItems() {
        return items;
    }

    public void setItems(List<DongDonThuocRequest> items) {
        this.items = items;
    }
}
