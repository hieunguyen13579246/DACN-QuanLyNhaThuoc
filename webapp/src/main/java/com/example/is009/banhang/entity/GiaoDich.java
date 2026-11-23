package com.example.is009.banhang.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "GiaoDich")
public class GiaoDich {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_giao_dich", nullable = false, unique = true, length = 30)
    private String maGiaoDich;

    @Column(name = "nha_thuoc_id", nullable = false)
    private Integer nhaThuocId;

    @Column(name = "nguoi_ban_id", nullable = false)
    private Integer nguoiBanId;

    @Column(name = "khach_hang_id")
    private Integer khachHangId;

    @Column(name = "don_thuoc_id")
    private Integer donThuocId;

    @Column(name = "thoi_gian", nullable = false)
    private LocalDateTime thoiGian;

    @Column(name = "tong_tien_hang", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongTienHang;

    @Column(name = "giam_gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal giamGia;

    @Column(name = "thanh_toan", nullable = false, precision = 18, scale = 2)
    private BigDecimal thanhToan;

    @Column(name = "phuong_thuc_thanh_toan", nullable = false, length = 20)
    private String phuongThucThanhToan;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 15)
    private TrangThaiGD trangThai;

    @Column(name = "nguoi_huy_id")
    private Integer nguoiHuyId;

    @Column(name = "thoi_gian_huy")
    private LocalDateTime thoiGianHuy;

    @Column(name = "ly_do_huy", length = 300)
    private String lyDoHuy;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public Integer getNguoiBanId() { return nguoiBanId; }
    public void setNguoiBanId(Integer nguoiBanId) { this.nguoiBanId = nguoiBanId; }

    public Integer getKhachHangId() { return khachHangId; }
    public void setKhachHangId(Integer khachHangId) { this.khachHangId = khachHangId; }

    public Integer getDonThuocId() { return donThuocId; }
    public void setDonThuocId(Integer donThuocId) { this.donThuocId = donThuocId; }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }

    public BigDecimal getTongTienHang() { return tongTienHang; }
    public void setTongTienHang(BigDecimal tongTienHang) { this.tongTienHang = tongTienHang; }

    public BigDecimal getGiamGia() { return giamGia; }
    public void setGiamGia(BigDecimal giamGia) { this.giamGia = giamGia; }

    public BigDecimal getThanhToan() { return thanhToan; }
    public void setThanhToan(BigDecimal thanhToan) { this.thanhToan = thanhToan; }

    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }

    public TrangThaiGD getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiGD trangThai) { this.trangThai = trangThai; }

    public Integer getNguoiHuyId() { return nguoiHuyId; }
    public void setNguoiHuyId(Integer nguoiHuyId) { this.nguoiHuyId = nguoiHuyId; }

    public LocalDateTime getThoiGianHuy() { return thoiGianHuy; }
    public void setThoiGianHuy(LocalDateTime thoiGianHuy) { this.thoiGianHuy = thoiGianHuy; }

    public String getLyDoHuy() { return lyDoHuy; }
    public void setLyDoHuy(String lyDoHuy) { this.lyDoHuy = lyDoHuy; }
}
