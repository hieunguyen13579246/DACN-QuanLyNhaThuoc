package com.example.is009.banhang.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoPhanBoDTO {

    private Integer thuocId;
    private Integer loThuocId;
    private String soLo;
    private LocalDate hanDung;
    private Integer soLuong;
    private BigDecimal donGia;
    private Long soNgayConLai;

    public Integer getThuocId() { return thuocId; }
    public void setThuocId(Integer thuocId) { this.thuocId = thuocId; }

    public Integer getLoThuocId() { return loThuocId; }
    public void setLoThuocId(Integer loThuocId) { this.loThuocId = loThuocId; }

    public String getSoLo() { return soLo; }
    public void setSoLo(String soLo) { this.soLo = soLo; }

    public LocalDate getHanDung() { return hanDung; }
    public void setHanDung(LocalDate hanDung) { this.hanDung = hanDung; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }

    public Long getSoNgayConLai() { return soNgayConLai; }
    public void setSoNgayConLai(Long soNgayConLai) { this.soNgayConLai = soNgayConLai; }
}
