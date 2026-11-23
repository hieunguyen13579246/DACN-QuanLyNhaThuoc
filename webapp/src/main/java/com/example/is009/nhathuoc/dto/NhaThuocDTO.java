package com.example.is009.nhathuoc.dto;

import java.time.LocalDate;

public record NhaThuocDTO(
        Integer id,
        String ma,
        String ten,
        String diaChi,
        Integer khuVucId,
        String soDienThoai,
        String trangThai,
        LocalDate ngayKhaiTruong
) {
}
