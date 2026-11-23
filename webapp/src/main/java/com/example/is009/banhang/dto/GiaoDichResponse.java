package com.example.is009.banhang.dto;

import java.math.BigDecimal;
import java.util.List;

public class GiaoDichResponse {

    private Integer id;
    private String maGiaoDich;
    private Integer nhaThuocId;
    private String tenNhaThuoc;
    private String diaChiNhaThuoc;
    private Integer nguoiBanId;
    private String tenNguoiBan;
    private Integer khachHangId;
    private String tenKhachHang;
    private String sdtKhachHang;
    private Integer donThuocId;
    private String maDonThuoc;
    private String thoiGian;
    private BigDecimal tongTienHang;
    private BigDecimal giamGia;
    private BigDecimal thanhToan;
    private String phuongThucThanhToan;
    private String trangThai;
    private String tenNguoiHuy;
    private String thoiGianHuy;
    private String lyDoHuy;
    private List<ChiTietGiaoDichDTO> chiTiet;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public String getTenNhaThuoc() { return tenNhaThuoc; }
    public void setTenNhaThuoc(String tenNhaThuoc) { this.tenNhaThuoc = tenNhaThuoc; }

    public String getDiaChiNhaThuoc() { return diaChiNhaThuoc; }
    public void setDiaChiNhaThuoc(String diaChiNhaThuoc) { this.diaChiNhaThuoc = diaChiNhaThuoc; }

    public Integer getNguoiBanId() { return nguoiBanId; }
    public void setNguoiBanId(Integer nguoiBanId) { this.nguoiBanId = nguoiBanId; }

    public String getTenNguoiBan() { return tenNguoiBan; }
    public void setTenNguoiBan(String tenNguoiBan) { this.tenNguoiBan = tenNguoiBan; }

    public Integer getKhachHangId() { return khachHangId; }
    public void setKhachHangId(Integer khachHangId) { this.khachHangId = khachHangId; }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public String getSdtKhachHang() { return sdtKhachHang; }
    public void setSdtKhachHang(String sdtKhachHang) { this.sdtKhachHang = sdtKhachHang; }

    public Integer getDonThuocId() { return donThuocId; }
    public void setDonThuocId(Integer donThuocId) { this.donThuocId = donThuocId; }

    public String getMaDonThuoc() { return maDonThuoc; }
    public void setMaDonThuoc(String maDonThuoc) { this.maDonThuoc = maDonThuoc; }

    public String getThoiGian() { return thoiGian; }
    public void setThoiGian(String thoiGian) { this.thoiGian = thoiGian; }

    public BigDecimal getTongTienHang() { return tongTienHang; }
    public void setTongTienHang(BigDecimal tongTienHang) { this.tongTienHang = tongTienHang; }

    public BigDecimal getGiamGia() { return giamGia; }
    public void setGiamGia(BigDecimal giamGia) { this.giamGia = giamGia; }

    public BigDecimal getThanhToan() { return thanhToan; }
    public void setThanhToan(BigDecimal thanhToan) { this.thanhToan = thanhToan; }

    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getTenNguoiHuy() { return tenNguoiHuy; }
    public void setTenNguoiHuy(String tenNguoiHuy) { this.tenNguoiHuy = tenNguoiHuy; }

    public String getThoiGianHuy() { return thoiGianHuy; }
    public void setThoiGianHuy(String thoiGianHuy) { this.thoiGianHuy = thoiGianHuy; }

    public String getLyDoHuy() { return lyDoHuy; }
    public void setLyDoHuy(String lyDoHuy) { this.lyDoHuy = lyDoHuy; }

    public List<ChiTietGiaoDichDTO> getChiTiet() { return chiTiet; }
    public void setChiTiet(List<ChiTietGiaoDichDTO> chiTiet) { this.chiTiet = chiTiet; }
}
