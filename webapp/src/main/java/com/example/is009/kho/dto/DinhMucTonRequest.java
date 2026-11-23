package com.example.is009.kho.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DinhMucTonRequest {

    private Integer nhaThuocId;

    @NotNull(message = "Phải chọn thuốc")
    private Integer thuocId;

    @NotNull(message = "Phải nhập tồn tối thiểu")
    @Min(value = 0, message = "Tồn tối thiểu không được âm")
    private Integer tonToiThieu;

    @NotNull(message = "Phải nhập tồn tối đa")
    @Min(value = 1, message = "Tồn tối đa phải lớn hơn 0")
    private Integer tonToiDa;

    @NotNull(message = "Phải nhập số ngày giao hàng")
    @Min(value = 1, message = "Số ngày giao hàng phải lớn hơn 0")
    private Integer soNgayGiaoHang = 3;

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public Integer getThuocId() { return thuocId; }
    public void setThuocId(Integer thuocId) { this.thuocId = thuocId; }

    public Integer getTonToiThieu() { return tonToiThieu; }
    public void setTonToiThieu(Integer tonToiThieu) { this.tonToiThieu = tonToiThieu; }

    public Integer getTonToiDa() { return tonToiDa; }
    public void setTonToiDa(Integer tonToiDa) { this.tonToiDa = tonToiDa; }

    public Integer getSoNgayGiaoHang() { return soNgayGiaoHang; }
    public void setSoNgayGiaoHang(Integer soNgayGiaoHang) { this.soNgayGiaoHang = soNgayGiaoHang; }
}
