package com.example.is009.banhang.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

public class GioHangRequest {

    private Integer nhaThuocId;
    private Integer khachHangId;
    private Integer donThuocId;

    @NotBlank(message = "Phải chọn phương thức thanh toán")
    private String phuongThucThanhToan = "TIEN_MAT";

    @DecimalMin(value = "0.0", message = "Giảm giá không được âm")
    private BigDecimal giamGia = BigDecimal.ZERO;

    @NotEmpty(message = "Giỏ hàng không được rỗng")
    @Valid
    private List<DongGioHang> chiTiet;

    public Integer getNhaThuocId() { return nhaThuocId; }
    public void setNhaThuocId(Integer nhaThuocId) { this.nhaThuocId = nhaThuocId; }

    public Integer getKhachHangId() { return khachHangId; }
    public void setKhachHangId(Integer khachHangId) { this.khachHangId = khachHangId; }

    public Integer getDonThuocId() { return donThuocId; }
    public void setDonThuocId(Integer donThuocId) { this.donThuocId = donThuocId; }

    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(String phuongThucThanhToan) { this.phuongThucThanhToan = phuongThucThanhToan; }

    public BigDecimal getGiamGia() { return giamGia; }
    public void setGiamGia(BigDecimal giamGia) { this.giamGia = giamGia; }

    public List<DongGioHang> getChiTiet() { return chiTiet; }
    public void setChiTiet(List<DongGioHang> chiTiet) { this.chiTiet = chiTiet; }
}
