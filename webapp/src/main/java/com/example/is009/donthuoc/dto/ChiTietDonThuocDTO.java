package com.example.is009.donthuoc.dto;

public record ChiTietDonThuocDTO(
        Integer id,
        Integer donThuocId,
        Integer thuocId,
        String maThuoc,
        String tenThuoc,
        String donViTinh,
        boolean canKeDon,
        int soLuongKe,
        int soLuongDaBan,
        int soLuongConLai,
        String lieuDung
) {
}
