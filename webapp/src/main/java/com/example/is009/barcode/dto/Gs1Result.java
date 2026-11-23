package com.example.is009.barcode.dto;

import java.time.LocalDate;

public class Gs1Result {

    private final String rawBarcode;
    private final String gtin;
    private final String ean13;
    private final LocalDate hanDung;
    private final LocalDate ngaySanXuat;
    private final String soLo;
    private final String soSerial;
    private final boolean laGs1;

    public Gs1Result(String rawBarcode, String gtin, String ean13, LocalDate hanDung,
                     LocalDate ngaySanXuat, String soLo, String soSerial, boolean laGs1) {
        this.rawBarcode = rawBarcode;
        this.gtin = gtin;
        this.ean13 = ean13;
        this.hanDung = hanDung;
        this.ngaySanXuat = ngaySanXuat;
        this.soLo = soLo;
        this.soSerial = soSerial;
        this.laGs1 = laGs1;
    }

    public String getRawBarcode() {
        return rawBarcode;
    }

    public String getGtin() {
        return gtin;
    }

    public String getEan13() {
        return ean13;
    }

    public LocalDate getHanDung() {
        return hanDung;
    }

    public LocalDate getNgaySanXuat() {
        return ngaySanXuat;
    }

    public String getSoLo() {
        return soLo;
    }

    public String getSoSerial() {
        return soSerial;
    }

    public boolean isLaGs1() {
        return laGs1;
    }

    public String getMaTraCuuUuTien() {
        if (ean13 != null && !ean13.isBlank()) {
            return ean13;
        }
        return gtin;
    }
}
