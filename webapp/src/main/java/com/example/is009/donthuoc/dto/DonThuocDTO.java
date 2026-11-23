package com.example.is009.donthuoc.dto;

import java.time.LocalDate;
import java.util.List;

public record DonThuocDTO(
        Integer id,
        String maDon,
        Integer khachHangId,
        String tenKhachHang,
        String soDienThoaiKhach,
        Integer nhaThuocId,
        String tenNhaThuoc,
        String tenBacSi,
        String coSoKham,
        String chanDoan,
        LocalDate ngayKeDon,
        int soNgayHieuLuc,
        LocalDate ngayHetHieuLuc,
        String trangThai,
        boolean coTheHuy,
        List<ChiTietDonThuocDTO> chiTiet
) {
}
