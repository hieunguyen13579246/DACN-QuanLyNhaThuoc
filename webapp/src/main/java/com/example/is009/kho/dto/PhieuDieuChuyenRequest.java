package com.example.is009.kho.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PhieuDieuChuyenRequest {

    private Integer nhaThuocNguonId;

    @NotNull(message = "Phải chọn nhà thuốc đích")
    private Integer nhaThuocDichId;

    private String ghiChu;

    @NotEmpty(message = "Phiếu điều chuyển phải có ít nhất 1 lô thuốc")
    @Valid
    private List<DongPhieuLoRequest> chiTiet;

    public Integer getNhaThuocNguonId() { return nhaThuocNguonId; }
    public void setNhaThuocNguonId(Integer nhaThuocNguonId) { this.nhaThuocNguonId = nhaThuocNguonId; }

    public Integer getNhaThuocDichId() { return nhaThuocDichId; }
    public void setNhaThuocDichId(Integer nhaThuocDichId) { this.nhaThuocDichId = nhaThuocDichId; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public List<DongPhieuLoRequest> getChiTiet() { return chiTiet; }
    public void setChiTiet(List<DongPhieuLoRequest> chiTiet) { this.chiTiet = chiTiet; }
}
