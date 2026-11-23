package com.example.is009.kho.dto;

import java.time.LocalDate;

public class TonKhoThuocDTO {

    private Integer nhaThuocId;
    private String tenNhaThuoc;
    private Integer thuocId;
    private String maThuoc;
    private String tenThuoc;
    private String donViTinh;
    private Integer nhomThuocId;
    private String tenNhom;
    private Integer tongTon;
    private Integer tongGiuCho;
    private Integer tongKhaDung;
    private LocalDate hanDungGanNhat;
    private Integer soLoConTon;

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public String getTenNhaThuoc() { return tenNhaThuoc; }
    public void setTenNhaThuoc(String tenNhaThuoc) { this.tenNhaThuoc = tenNhaThuoc; }

    public Integer getThuocId() { return thuocId; }
    public void setThuocId(Integer thuocId) { this.thuocId = thuocId; }

    public String getMaThuoc() { return maThuoc; }
    public void setMaThuoc(String maThuoc) { this.maThuoc = maThuoc; }

    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public Integer getNhomThuocId() { return nhomThuocId; }
    public void setNhomThuocId(Integer nhomThuocId) { this.nhomThuocId = nhomThuocId; }

    public String getTenNhom() { return tenNhom; }
    public void setTenNhom(String tenNhom) { this.tenNhom = tenNhom; }

    public Integer getTongTon() { return tongTon; }
    public void setTongTon(Integer tongTon) { this.tongTon = tongTon; }

    public Integer getTongGiuCho() { return tongGiuCho; }
    public void setTongGiuCho(Integer tongGiuCho) { this.tongGiuCho = tongGiuCho; }

    public Integer getTongKhaDung() { return tongKhaDung; }
    public void setTongKhaDung(Integer tongKhaDung) { this.tongKhaDung = tongKhaDung; }

    public LocalDate getHanDungGanNhat() { return hanDungGanNhat; }
    public void setHanDungGanNhat(LocalDate hanDungGanNhat) { this.hanDungGanNhat = hanDungGanNhat; }

    public Integer getSoLoConTon() { return soLoConTon; }
    public void setSoLoConTon(Integer soLoConTon) { this.soLoConTon = soLoConTon; }
}
