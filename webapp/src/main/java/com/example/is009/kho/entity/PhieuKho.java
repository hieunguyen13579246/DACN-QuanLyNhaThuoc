package com.example.is009.kho.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PhieuKho")
public class PhieuKho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_phieu", nullable = false, unique = true, length = 30)
    private String maPhieu;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_phieu", nullable = false, length = 20)
    private LoaiPhieu loaiPhieu;

    @Column(name = "nha_thuoc_id", nullable = false)
    private Integer nhaThuocId;

    @Column(name = "nha_thuoc_dich_id")
    private Integer nhaThuocDichId;

    @Column(name = "nha_cung_cap_id")
    private Integer nhaCungCapId;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    private TrangThaiPhieu trangThai;

    @Column(name = "ly_do_huy", length = 20)
    private String lyDoHuy;

    @Column(name = "ly_do_tu_choi", length = 300)
    private String lyDoTuChoi;

    @Column(name = "ghi_chu", length = 300)
    private String ghiChu;

    @Column(name = "nguoi_lap_id", nullable = false)
    private Integer nguoiLapId;

    @Column(name = "thoi_gian_lap", nullable = false)
    private LocalDateTime thoiGianLap;

    @Column(name = "nguoi_duyet_id")
    private Integer nguoiDuyetId;

    @Column(name = "thoi_gian_duyet")
    private LocalDateTime thoiGianDuyet;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getMaPhieu() { return maPhieu; }
    public void setMaPhieu(String maPhieu) { this.maPhieu = maPhieu; }

    public LoaiPhieu getLoaiPhieu() { return loaiPhieu; }
    public void setLoaiPhieu(LoaiPhieu loaiPhieu) { this.loaiPhieu = loaiPhieu; }

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public Integer getNhaThuocDichId() { return nhaThuocDichId; }
    public void setNhaThuocDichId(Integer nhaThuocDichId) { this.nhaThuocDichId = nhaThuocDichId; }

    public Integer getNhaCungCapId() { return nhaCungCapId; }
    public void setNhaCungCapId(Integer nhaCungCapId) { this.nhaCungCapId = nhaCungCapId; }

    public TrangThaiPhieu getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiPhieu trangThai) { this.trangThai = trangThai; }

    public String getLyDoHuy() { return lyDoHuy; }
    public void setLyDoHuy(String lyDoHuy) { this.lyDoHuy = lyDoHuy; }

    public String getLyDoTuChoi() { return lyDoTuChoi; }
    public void setLyDoTuChoi(String lyDoTuChoi) { this.lyDoTuChoi = lyDoTuChoi; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public Integer getNguoiLapId() { return nguoiLapId; }
    public void setNguoiLapId(Integer nguoiLapId) { this.nguoiLapId = nguoiLapId; }

    public LocalDateTime getThoiGianLap() { return thoiGianLap; }
    public void setThoiGianLap(LocalDateTime thoiGianLap) { this.thoiGianLap = thoiGianLap; }

    public Integer getNguoiDuyetId() { return nguoiDuyetId; }
    public void setNguoiDuyetId(Integer nguoiDuyetId) { this.nguoiDuyetId = nguoiDuyetId; }

    public LocalDateTime getThoiGianDuyet() { return thoiGianDuyet; }
    public void setThoiGianDuyet(LocalDateTime thoiGianDuyet) { this.thoiGianDuyet = thoiGianDuyet; }
}
