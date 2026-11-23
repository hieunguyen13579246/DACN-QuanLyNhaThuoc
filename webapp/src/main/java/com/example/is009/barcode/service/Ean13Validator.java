package com.example.is009.barcode.service;

public final class Ean13Validator {

    private Ean13Validator() {
    }

    public static boolean isValid(String ean13) {
        if (ean13 == null || ean13.length() != 13) {
            return false;
        }
        for (int i = 0; i < 13; i++) {
            char c = ean13.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        char expectedCheck = tinhCheckDigit(ean13.substring(0, 12));
        return ean13.charAt(12) == expectedCheck;
    }

    public static char tinhCheckDigit(String ean12) {
        if (ean12 == null || ean12.length() != 12) {
            throw new IllegalArgumentException("Chuỗi tính check digit EAN-13 phải gồm đúng 12 chữ số.");
        }
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            char c = ean12.charAt(i);
            if (c < '0' || c > '9') {
                throw new IllegalArgumentException("Chuỗi EAN-12 chỉ được chứa chữ số 0-9.");
            }
            int digit = c - '0';
            // Vị trí chẵn (theo 1-based: 2, 4, 6, 8, 10, 12 -> index lẻ 1, 3, 5, 7, 9, 11) nhân 3
            if ((i + 1) % 2 == 0) {
                sum += digit * 3;
            } else {
                sum += digit;
            }
        }
        int check = (10 - (sum % 10)) % 10;
        return (char) ('0' + check);
    }
}
