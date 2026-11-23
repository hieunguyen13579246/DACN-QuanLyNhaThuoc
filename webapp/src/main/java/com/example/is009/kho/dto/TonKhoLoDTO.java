package com.example.is009.kho.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TonKhoLoDTO {

    private Integer nhaThuocId;
    private String tenNhaThuoc;
    private Integer loThuocId;
    private String soLo;
    private String maVachLo;
    private Integer thuocId;
    private String maThuoc;
    private String tenThuoc;
    private String donViTinh;
    private String maVachChinh;
    private LocalDate ngaySanXuat;
    private LocalDate hanDung;
    private Long soNgayConLai;
    private BigDecimal giaNhap;
    private BigDecimal giaBanLe;
    private Integer soLuongTon;
    private Integer soLuongGiuCho;
    private Integer soLuongKhaDung;

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public String getTenNhaThuoc() { return tenNhaThuoc; }
    public void setTenNhaThuoc(String tenNhaThuoc) { this.tenNhaThuoc = tenNhaThuoc; }

    public Integer getLoThuocId() { return loThuocId; }
    public void setLoThuocId(Integer loThuocId) { this.loThuocId = loThuocId; }

    public String getSoLo() { return soLo; }
    public void setSoLo(String soLo) { this.soLo = soLo; }

    public String getMaVachLo() { return maVachLo; }
    public void setMaVachLo(String maVachLo) { this.maVachLo = maVachLo; }

    public Integer getThuocId() { return thuocId; }
    public void setThuocId(Integer thuocId) { this.thuocId = thuocId; }

    public String getMaThuoc() { return maThuoc; }
    public void setMaThuoc(String maThuoc) { this.maThuoc = maThuoc; }

    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public String getMaVachChinh() { return maVachChinh; }
    public void setMaVachChinh(String maVachChinh) { this.maVachChinh = maVachChinh; }

    public LocalDate getNgaySanXuat() { return ngaySanXuat; }
    public void setNgaySanXuat(LocalDate ngaySanXuat) { this.ngaySanXuat = ngaySanXuat; }

    public LocalDate getHanDung() { return hanDung; }
    public void setHanDung(LocalDate hanDung) { this.hanDung = hanDung; }

    public Long getSoNgayConLai() { return soNgayConLai; }
    public void setSoNgayConLai(Long soNgayConLai) { this.soNgayConLai = soNgayConLai; }

    public BigDecimal getGiaNhap() { return giaNhap; }
    public void setGiaNhap(BigDecimal giaNhap) { this.giaNhap = giaNhap; }

    public BigDecimal getGiaBanLe() { return giaBanLe; }
    public void setGiaBanLe(BigDecimal giaBanLe) { this.giaBanLe = giaBanLe; }

    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer soLuongTon) { this.soLuongTon = soLuongTon; }

    public Integer getSoLuongGiuCho() { return soLuongGiuCho; }
    public void setSoLuongGiuCho(Integer soLuongGiuCho) { this.soLuongGiuCho = soLuongGiuCho; }

    public Integer getSoLuongKhaDung() { return soLuongKhaDung; }
    public void setSoLuongKhaDung(Integer soLuongKhaDung) { this.soLuongKhaDung = soLuongKhaDung; }
}
