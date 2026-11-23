package com.example.is009.common;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class ThoiGianVN {

    public static final ZoneId MUI_GIO_VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DINH_DANG_NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DINH_DANG_ISO_NGAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ThoiGianVN() {
    }

    public static LocalDate homNay() {
        return LocalDate.now(MUI_GIO_VN);
    }

    public static LocalDateTime bayGio() {
        return LocalDateTime.now(MUI_GIO_VN);
    }

    public static Instant sangUtc(LocalDateTime gioVn) {
        if (gioVn == null) {
            return null;
        }
        return gioVn.atZone(MUI_GIO_VN).toInstant();
    }

    public static LocalDateTime sangGioVN(Instant utc) {
        if (utc == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(utc, MUI_GIO_VN).toLocalDateTime();
    }

    public static LocalDate sangNgayVN(Instant utc) {
        if (utc == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(utc, MUI_GIO_VN).toLocalDate();
    }

    public static Instant tuTimestampUtc(Timestamp ts) {
        if (ts == null) {
            return null;
        }
        return ts.toInstant();
    }

    public static String dinhDangNgay(LocalDate ngay) {
        if (ngay == null) {
            return "";
        }
        return ngay.format(DINH_DANG_NGAY);
    }

    public static String dinhDangIso(LocalDate ngay) {
        if (ngay == null) {
            return "";
        }
        return ngay.format(DINH_DANG_ISO_NGAY);
    }

    public static String dinhDangNgayGio(Instant utc) {
        if (utc == null) {
            return "";
        }
        return sangGioVN(utc).format(DINH_DANG_NGAY_GIO);
    }
}
