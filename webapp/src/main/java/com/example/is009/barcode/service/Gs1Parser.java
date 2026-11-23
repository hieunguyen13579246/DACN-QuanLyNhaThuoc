package com.example.is009.barcode.service;

import com.example.is009.barcode.dto.Gs1Result;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gs1Parser {

    public static final char FNC1 = '\u001D';
    private static final Pattern PAREN_AI_PATTERN = Pattern.compile("\\((01|10|11|17|21)\\)([^()]+)");

    public Gs1Result parse(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            return new Gs1Result(rawInput, null, null, null, null, null, null, false);
        }
        String clean = xoaTienToMayQuet(rawInput.trim());
        Map<String, String> aiMap = clean.contains("(") ? parseCoNgoac(clean) : parseKhongNgoac(clean);

        String gtin = aiMap.get("01");
        String ean13 = chuyenGtinSangEan13(gtin);
        LocalDate hanDung = parseNgayGs1(aiMap.get("17"));
        LocalDate nsx = parseNgayGs1(aiMap.get("11"));
        String soLo = aiMap.get("10");
        String serial = aiMap.get("21");
        boolean laGs1 = gtin != null || hanDung != null || soLo != null || serial != null;

        return new Gs1Result(rawInput, gtin, ean13, hanDung, nsx, soLo, serial, laGs1);
    }

    private String xoaTienToMayQuet(String s) {
        String res = s.replace("|", String.valueOf(FNC1)).replace("~", String.valueOf(FNC1));
        if (res.startsWith("]d2") || res.startsWith("]C1") || res.startsWith("]e0")) {
            res = res.substring(3);
        }
        while (!res.isEmpty() && res.charAt(0) == FNC1) {
            res = res.substring(1);
        }
        return res;
    }

    private Map<String, String> parseCoNgoac(String input) {
        Map<String, String> map = new HashMap<>();
        Matcher matcher = PAREN_AI_PATTERN.matcher(input);
        while (matcher.find()) {
            String ai = matcher.group(1);
            String val = matcher.group(2).replace(String.valueOf(FNC1), "").trim();
            map.put(ai, val);
        }
        return map;
    }

    private Map<String, String> parseKhongNgoac(String input) {
        Map<String, String> map = new HashMap<>();
        int idx = 0;
        int len = input.length();
        while (idx < len) {
            if (input.charAt(idx) == FNC1) {
                idx++;
                continue;
            }
            if (idx + 2 > len) {
                break;
            }
            String ai = input.substring(idx, idx + 2);
            idx += 2;
            if ("01".equals(ai) && idx + 14 <= len) {
                map.put("01", input.substring(idx, idx + 14));
                idx += 14;
            } else if (("17".equals(ai) || "11".equals(ai)) && idx + 6 <= len) {
                map.put(ai, input.substring(idx, idx + 6));
                idx += 6;
            } else if ("10".equals(ai) || "21".equals(ai)) {
                int fncPos = input.indexOf(FNC1, idx);
                int endPos = (fncPos >= 0) ? Math.min(fncPos, idx + 20) : Math.min(len, idx + 20);
                map.put(ai, input.substring(idx, endPos));
                idx = (fncPos >= 0) ? fncPos + 1 : endPos;
            } else {
                break;
            }
        }
        return map;
    }

    private String chuyenGtinSangEan13(String gtin) {
        if (gtin == null) {
            return null;
        }
        if (gtin.length() == 14 && gtin.startsWith("0")) {
            String candidate = gtin.substring(1);
            if (Ean13Validator.isValid(candidate)) {
                return candidate;
            }
        }
        if (gtin.length() == 13 && Ean13Validator.isValid(gtin)) {
            return gtin;
        }
        return null;
    }

    private LocalDate parseNgayGs1(String yymmdd) {
        if (yymmdd == null || yymmdd.length() != 6 || !yymmdd.matches("\\d{6}")) {
            return null;
        }
        int yy = Integer.parseInt(yymmdd.substring(0, 2));
        int mm = Integer.parseInt(yymmdd.substring(2, 4));
        int dd = Integer.parseInt(yymmdd.substring(4, 6));
        if (mm < 1 || mm > 12) {
            return null;
        }
        int year = 2000 + yy;
        YearMonth ym = YearMonth.of(year, mm);
        if (dd == 0) {
            return ym.atEndOfMonth();
        }
        if (dd < 1 || dd > ym.lengthOfMonth()) {
            return null;
        }
        return LocalDate.of(year, mm, dd);
    }
}
