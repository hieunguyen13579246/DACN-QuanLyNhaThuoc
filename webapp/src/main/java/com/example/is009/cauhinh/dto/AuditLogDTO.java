package com.example.is009.cauhinh.dto;

import java.time.Instant;

public record AuditLogDTO(
        Long id,
        Integer nguoiDungId,
        String hanhDong,
        String doiTuong,
        Integer doiTuongId,
        String duLieuCu,
        String duLieuMoi,
        Instant createdAt,
        String thoiGianVN
) {
}
