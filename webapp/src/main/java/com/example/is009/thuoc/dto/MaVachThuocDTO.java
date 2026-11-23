package com.example.is009.thuoc.dto;

import java.time.Instant;

public record MaVachThuocDTO(
        Integer id,
        Integer thuocId,
        String maVach,
        String loaiMaVach,
        boolean laMaChinh,
        Instant createdAt
) {
}
