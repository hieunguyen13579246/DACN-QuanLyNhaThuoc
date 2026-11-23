package com.example.is009.thongke.dto;

import java.math.BigDecimal;

public class DeXuatNhapHangDTO {

    private Integer nhaThuocId;
    private String tenNhaThuoc;
    private Integer thuocId;
    private String maThuoc;
    private String tenThuoc;
    private String donViTinh;
    private String tenNhom;
    private int tonKhaDung;
    private int tonToiThieu;
    private int tonToiDa;
    private BigDecimal banTrungBinhNgay;
    private int soNgayGiaoHang;
    private int soLuongDeXuatNhap;
    private BigDecimal giaNhapGanNhat;
    private BigDecimal thanhTienDuKien;

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

    public String getTenNhom() { return tenNhom; }
    public void setTenNhom(String tenNhom) { this.tenNhom = tenNhom; }

    public int getTonKhaDung() { return tonKhaDung; }
    public void setTonKhaDung(int tonKhaDung) { this.tonKhaDung = tonKhaDung; }

    public int getTonToiThieu() { return tonToiThieu; }
    public void setTonToiThieu(int tonToiThieu) { this.tonToiThieu = tonToiThieu; }

    public int getTonToiDa() { return tonToiDa; }
    public void setTonToiDa(int tonToiDa) { this.tonToiDa = tonToiDa; }

    public BigDecimal getBanTrungBinhNgay() { return banTrungBinhNgay; }
    public void setBanTrungBinhNgay(BigDecimal banTrungBinhNgay) { this.banTrungBinhNgay = banTrungBinhNgay; }

    public int getSoNgayGiaoHang() { return soNgayGiaoHang; }
    public void setSoNgayGiaoHang(int soNgayGiaoHang) { this.soNgayGiaoHang = soNgayGiaoHang; }

    public int getSoLuongDeXuatNhap() { return soLuongDeXuatNhap; }
    public void setSoLuongDeXuatNhap(int soLuongDeXuatNhap) { this.soLuongDeXuatNhap = soLuongDeXuatNhap; }

    public BigDecimal getGiaNhapGanNhat() { return giaNhapGanNhat; }
    public void setGiaNhapGanNhat(BigDecimal giaNhapGanNhat) { this.giaNhapGanNhat = giaNhapGanNhat; }

    public BigDecimal getThanhTienDuKien() { return thanhTienDuKien; }
    public void setThanhTienDuKien(BigDecimal thanhTienDuKien) { this.thanhTienDuKien = thanhTienDuKien; }

    public String getNhaThuoc() { return tenNhaThuoc; }
    public BigDecimal getBanTbNgay() { return banTrungBinhNgay; }
    public int getSoLuongDeXuat() { return soLuongDeXuatNhap; }
}
