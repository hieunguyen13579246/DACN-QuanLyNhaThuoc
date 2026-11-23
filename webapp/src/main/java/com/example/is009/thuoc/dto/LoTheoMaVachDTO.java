package com.example.is009.thuoc.dto;

import java.time.LocalDate;

public record LoTheoMaVachDTO(
        Integer loThuocId,
        String soLo,
        LocalDate hanDung,
        int soNgayConLai,
        String mucCanhBao,
        int soLuongTon
) {
}
