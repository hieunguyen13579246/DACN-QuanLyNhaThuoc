package com.example.is009.exception;

public class NghiepVuException extends RuntimeException {

    private final String errorCode;
    private final String huongDanSua;

    public NghiepVuException(String errorCode, String message, String huongDanSua) {
        super(message);
        this.errorCode = errorCode;
        this.huongDanSua = huongDanSua;
    }

    public NghiepVuException(String errorCode, String message) {
        this(errorCode, message, null);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getHuongDanSua() {
        return huongDanSua;
    }
}
