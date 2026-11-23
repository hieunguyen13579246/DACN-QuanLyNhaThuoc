package com.example.is009.security;

public enum VaiTro {
    DUOCSI("Dược sĩ"),
    THUKHO("Thủ kho"),
    QUANLY("Quản lý nhà thuốc"),
    QUANTRI("Quản trị chuỗi");

    private final String tenHienThi;

    VaiTro(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
