package com.example.is009.banhang.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class XemTruocFEFORequest {

    private Integer nhaThuocId;

    @NotEmpty(message = "Giỏ hàng không được rỗng")
    @Valid
    private List<DongGioHang> chiTiet;

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public List<DongGioHang> getChiTiet() { return chiTiet; }
    public void setChiTiet(List<DongGioHang> chiTiet) { this.chiTiet = chiTiet; }
}
