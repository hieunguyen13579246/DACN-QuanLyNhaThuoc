package com.example.is009.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class TienVND {

    private TienVND() {
    }

    public static String dinhDang(BigDecimal soTien) {
        if (soTien == null) {
            return "0 đ";
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(soTien.setScale(0, RoundingMode.HALF_UP)) + " đ";
    }

    public static String dinhDangSo(BigDecimal soTien) {
        if (soTien == null) {
            return "0";
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);
        return df.format(soTien.setScale(0, RoundingMode.HALF_UP));
    }

    public static String dinhDang(long soTien) {
        return dinhDang(BigDecimal.valueOf(soTien));
    }
}
