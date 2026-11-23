package com.example.is009.nguoidung.dto;

import java.time.Instant;

public record NguoiDungDTO(
        Integer id,
        String hoTen,
        String email,
        String vaiTro,
        String tenVaiTro,
        Integer nhaThuocId,
        String soChungChiHanhNghe,
        boolean kichHoat,
        Instant createdAt
) {
}
