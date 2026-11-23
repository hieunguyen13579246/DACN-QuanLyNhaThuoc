package com.example.is009.kho.dto;

import java.math.BigDecimal;
import java.util.List;

public class PhieuKhoDTO {

    private Integer id;
    private String maPhieu;
    private String loaiPhieu;
    private Integer nhaThuocId;
    private String tenNhaThuoc;
    private Integer nhaThuocDichId;
    private String tenNhaThuocDich;
    private Integer nhaCungCapId;
    private String tenNhaCungCap;
    private String trangThai;
    private String lyDoHuy;
    private String lyDoTuChoi;
    private String ghiChu;
    private Integer nguoiLapId;
    private String tenNguoiLap;
    private String thoiGianLap;
    private Integer nguoiDuyetId;
    private String tenNguoiDuyet;
    private String thoiGianDuyet;
    private BigDecimal tongGiaTri;
    private List<ChiTietPhieuKhoDTO> chiTiet;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getMaPhieu() { return maPhieu; }
    public void setMaPhieu(String maPhieu) { this.maPhieu = maPhieu; }

    public String getLoaiPhieu() { return loaiPhieu; }
    public void setLoaiPhieu(String loaiPhieu) { this.loaiPhieu = loaiPhieu; }

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public String getTenNhaThuoc() { return tenNhaThuoc; }
    public void setTenNhaThuoc(String tenNhaThuoc) { this.tenNhaThuoc = tenNhaThuoc; }

    public Integer getNhaThuocDichId() { return nhaThuocDichId; }
    public void setNhaThuocDichId(Integer nhaThuocDichId) { this.nhaThuocDichId = nhaThuocDichId; }

    public String getTenNhaThuocDich() { return tenNhaThuocDich; }
    public void setTenNhaThuocDich(String tenNhaThuocDich) { this.tenNhaThuocDich = tenNhaThuocDich; }

    public Integer getNhaCungCapId() { return nhaCungCapId; }
    public void setNhaCungCapId(Integer nhaCungCapId) { this.nhaCungCapId = nhaCungCapId; }

    public String getTenNhaCungCap() { return tenNhaCungCap; }
    public void setTenNhaCungCap(String tenNhaCungCap) { this.tenNhaCungCap = tenNhaCungCap; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getLyDoHuy() { return lyDoHuy; }
    public void setLyDoHuy(String lyDoHuy) { this.lyDoHuy = lyDoHuy; }

    public String getLyDoTuChoi() { return lyDoTuChoi; }
    public void setLyDoTuChoi(String lyDoTuChoi) { this.lyDoTuChoi = lyDoTuChoi; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public Integer getNguoiLapId() { return nguoiLapId; }
    public void setNguoiLapId(Integer nguoiLapId) { this.nguoiLapId = nguoiLapId; }

    public String getTenNguoiLap() { return tenNguoiLap; }
    public void setTenNguoiLap(String tenNguoiLap) { this.tenNguoiLap = tenNguoiLap; }

    public String getThoiGianLap() { return thoiGianLap; }
    public void setThoiGianLap(String thoiGianLap) { this.thoiGianLap = thoiGianLap; }

    public Integer getNguoiDuyetId() { return nguoiDuyetId; }
    public void setNguoiDuyetId(Integer nguoiDuyetId) { this.nguoiDuyetId = nguoiDuyetId; }

    public String getTenNguoiDuyet() { return tenNguoiDuyet; }
    public void setTenNguoiDuyet(String tenNguoiDuyet) { this.tenNguoiDuyet = tenNguoiDuyet; }

    public String getThoiGianDuyet() { return thoiGianDuyet; }
    public void setThoiGianDuyet(String thoiGianDuyet) { this.thoiGianDuyet = thoiGianDuyet; }

    public BigDecimal getTongGiaTri() { return tongGiaTri; }
    public void setTongGiaTri(BigDecimal tongGiaTri) { this.tongGiaTri = tongGiaTri; }

    public List<ChiTietPhieuKhoDTO> getChiTiet() { return chiTiet; }
    public void setChiTiet(List<ChiTietPhieuKhoDTO> chiTiet) { this.chiTiet = chiTiet; }
}
