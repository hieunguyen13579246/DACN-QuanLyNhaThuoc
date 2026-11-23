package com.example.is009.khuvuc.dto;

public record KhuVucDTO(
        Integer id,
        String ma,
        String ten,
        String cap,
        Integer khuVucChaId,
        String tenKhuVucCha,
        Integer danSo
) {
    public String getTenCapHanhChinh() {
        if ("TINH".equalsIgnoreCase(cap)) {
            String t = ten != null ? ten.toLowerCase() : "";
            if (t.contains("hồ chí minh") || t.contains("hà nội") || t.contains("đà nẵng")
                    || t.contains("hải phòng") || t.contains("cần thơ") || t.contains("huế")
                    || t.startsWith("tp") || t.startsWith("thành phố")) {
                return "Thành phố";
            }
            return "Tỉnh";
        }
        if ("QUAN".equalsIgnoreCase(cap)) {
            String t = ten != null ? ten.toLowerCase() : "";
            if (t.startsWith("huyện")) {
                return "Huyện";
            }
            if (t.startsWith("thị xã")) {
                return "Thị xã";
            }
            return "Quận";
        }
        return cap;
    }
}
