package com.example.is009.kho.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DongPhieuNhapRequest {

    @NotNull(message = "Phải chọn thuốc")
    @JsonProperty("thuoc_id")
    @JsonAlias({"thuocId", "thuoc_id"})
    private Integer thuocId;

    @NotBlank(message = "Số lô không được trống")
    @JsonProperty("so_lo")
    @JsonAlias({"soLo", "so_lo"})
    private String soLo;

    @NotNull(message = "Ngày sản xuất không được trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("ngay_san_xuat")
    @JsonAlias({"ngaySanXuat", "ngay_san_xuat"})
    private LocalDate ngaySanXuat;

    @NotNull(message = "Hạn dùng không được trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("han_dung")
    @JsonAlias({"hanDung", "han_dung"})
    private LocalDate hanDung;

    @NotNull(message = "Số lượng phải lớn hơn 0")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    @JsonProperty("so_luong")
    @JsonAlias({"soLuong", "so_luong"})
    private Integer soLuong;

    @NotNull(message = "Giá nhập phải lớn hơn 0")
    @DecimalMin(value = "0.01", message = "Giá nhập phải lớn hơn 0")
    @JsonProperty("gia_nhap")
    @JsonAlias({"giaNhap", "gia_nhap"})
    private BigDecimal giaNhap;

    public Integer getThuocId() { return thuocId; }
    public void setThuocId(Integer thuocId) { this.thuocId = thuocId; }

    public String getSoLo() { return soLo; }
    public void setSoLo(String soLo) { this.soLo = soLo; }

    public LocalDate getNgaySanXuat() { return ngaySanXuat; }
    public void setNgaySanXuat(LocalDate ngaySanXuat) { this.ngaySanXuat = ngaySanXuat; }

    public LocalDate getHanDung() { return hanDung; }
    public void setHanDung(LocalDate hanDung) { this.hanDung = hanDung; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public BigDecimal getGiaNhap() { return giaNhap; }
    public void setGiaNhap(BigDecimal giaNhap) { this.giaNhap = giaNhap; }
}
