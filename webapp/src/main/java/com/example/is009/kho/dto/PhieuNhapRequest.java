package com.example.is009.kho.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PhieuNhapRequest {

    private Integer nhaThuocId;

    @NotNull(message = "Phải chọn nhà cung cấp")
    private Integer nhaCungCapId;

    private String ghiChu;

    @NotEmpty(message = "Phiếu nhập phải có ít nhất 1 lô thuốc")
    @Valid
    private List<DongPhieuNhapRequest> chiTiet;

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public Integer getNhaCungCapId() { return nhaCungCapId; }
    public void setNhaCungCapId(Integer nhaCungCapId) { this.nhaCungCapId = nhaCungCapId; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public List<DongPhieuNhapRequest> getChiTiet() { return chiTiet; }
    public void setChiTiet(List<DongPhieuNhapRequest> chiTiet) { this.chiTiet = chiTiet; }
}
