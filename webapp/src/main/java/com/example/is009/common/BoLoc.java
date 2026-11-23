package com.example.is009.common;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class BoLoc {

    private String tuKhoa;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate tuNgay;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate denNgay;
    private Integer nhaThuocId;
    private Integer khuVucId;
    private Integer nhomThuocId;
    private String trangThai;
    private String loai;
    private int trang = 1;
    private int kichThuoc = 20;

    public String getTuKhoa() {
        return tuKhoa;
    }

    public void setTuKhoa(String tuKhoa) {
        this.tuKhoa = tuKhoa;
    }

    public LocalDate getTuNgay() {
        return tuNgay;
    }

    public void setTuNgay(LocalDate tuNgay) {
        this.tuNgay = tuNgay;
    }

    public LocalDate getDenNgay() {
        return denNgay;
    }

    public void setDenNgay(LocalDate denNgay) {
        this.denNgay = denNgay;
    }

    public Integer getNhaThuocId() {
        return nhaThuocId;
    }

    public void setNhaThuocId(Integer nhaThuocId) {
        this.nhaThuocId = nhaThuocId;
    }

    public Integer getKhuVucId() {
        return khuVucId;
    }

    public void setKhuVucId(Integer khuVucId) {
        this.khuVucId = khuVucId;
    }

    public Integer getNhomThuocId() {
        return nhomThuocId;
    }

    public void setNhomThuocId(Integer nhomThuocId) {
        this.nhomThuocId = nhomThuocId;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public int getTrang() {
        return Math.max(1, trang);
    }

    public void setTrang(int trang) {
        this.trang = Math.max(1, trang);
    }

    public int getKichThuoc() {
        return Math.min(100, Math.max(5, kichThuoc));
    }

    public void setKichThuoc(int kichThuoc) {
        this.kichThuoc = Math.min(100, Math.max(5, kichThuoc));
    }
}
