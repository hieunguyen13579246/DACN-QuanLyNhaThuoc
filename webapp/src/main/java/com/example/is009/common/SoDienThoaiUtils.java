package com.example.is009.common;

public final class SoDienThoaiUtils {

    private SoDienThoaiUtils() {
    }

    /**
     * Che bớt số điện thoại khách hàng khi hiển thị ở danh sách (BR16).
     * Ví dụ: "0901234567" -> "090****567"
     */
    public static String cheSoDienThoai(String sdt) {
        if (sdt == null || sdt.isBlank()) {
            return "—";
        }
        String clean = sdt.trim();
        if (clean.length() <= 6) {
            return "***" + clean.substring(Math.max(0, clean.length() - 2));
        }
        String dau = clean.substring(0, 3);
        String cuoi = clean.substring(clean.length() - 3);
        int soSao = Math.max(3, clean.length() - 6);
        return dau + "*".repeat(soSao) + cuoi;
    }
}
