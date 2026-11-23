package com.example.is009.kho.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class PhieuXuatHuyRequest {

    private Integer nhaThuocId;

    @NotBlank(message = "Phải chọn lý do xuất hủy (HET_HAN, HU_HONG, THU_HOI, KHAC)")
    private String lyDo;

    private String ghiChu;

    @NotEmpty(message = "Phiếu xuất hủy phải có ít nhất 1 lô thuốc")
    @Valid
    private List<DongPhieuLoRequest> chiTiet;

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public List<DongPhieuLoRequest> getChiTiet() { return chiTiet; }
    public void setChiTiet(List<DongPhieuLoRequest> chiTiet) { this.chiTiet = chiTiet; }
}
