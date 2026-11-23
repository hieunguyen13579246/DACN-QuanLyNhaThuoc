package com.example.is009.khachhang.dto;

public record KhachHangDTO(
        Integer id,
        String ma,
        String hoTen,
        String soDienThoai,
        String soDienThoaiChe,
        String gioiTinh,
        Short namSinh,
        Integer khuVucId,
        String tenKhuVuc,
        String diaChi,
        String ghiChuDiUng
) {
}
