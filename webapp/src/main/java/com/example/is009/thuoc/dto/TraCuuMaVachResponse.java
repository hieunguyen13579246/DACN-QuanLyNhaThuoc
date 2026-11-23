package com.example.is009.thuoc.dto;

import java.util.List;

public record TraCuuMaVachResponse(
        boolean timThay,
        String maVachTraCuu,
        Integer nhaThuocId,
        ThuocDTO thuoc,
        int tongTonKhaDung,
        List<LoTheoMaVachDTO> danhSachLo
) {
    public TraCuuMaVachResponse(
            String maVachTraCuu,
            Integer nhaThuocId,
            ThuocDTO thuoc,
            int tongTonKhaDung,
            List<LoTheoMaVachDTO> danhSachLo
    ) {
        this(true, maVachTraCuu, nhaThuocId, thuoc, tongTonKhaDung, danhSachLo);
    }
}
