package com.example.is009.thuoc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "Thuoc")
public class Thuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma", nullable = false, length = 30)
    private String ma;

    @Column(name = "ten", nullable = false, length = 200)
    private String ten;

    @Column(name = "hoat_chat", nullable = false, length = 200)
    private String hoatChat;

    @Column(name = "ham_luong", length = 50)
    private String hamLuong;

    @Column(name = "dang_bao_che", length = 50)
    private String dangBaoChe;

    @Column(name = "don_vi_tinh", nullable = false, length = 20)
    private String donViTinh;

    @Column(name = "nhom_thuoc_id", nullable = false)
    private Integer nhomThuocId;

    @Column(name = "nha_san_xuat", length = 150)
    private String nhaSanXuat;

    @Column(name = "can_ke_don", nullable = false)
    private boolean canKeDon;

    @Column(name = "gia_ban_le", nullable = false, precision = 18, scale = 0)
    private BigDecimal giaBanLe;

    @Column(name = "trang_thai", nullable = false, length = 10)
    private String trangThai = "DANG_BAN";

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    public boolean dangBan() {
        return "DANG_BAN".equalsIgnoreCase(trangThai);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getHoatChat() {
        return hoatChat;
    }

    public void setHoatChat(String hoatChat) {
        this.hoatChat = hoatChat;
    }

    public String getHamLuong() {
        return hamLuong;
    }

    public void setHamLuong(String hamLuong) {
        this.hamLuong = hamLuong;
    }

    public String getDangBaoChe() {
        return dangBaoChe;
    }

    public void setDangBaoChe(String dangBaoChe) {
        this.dangBaoChe = dangBaoChe;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public Integer getNhomThuocId() {
        return nhomThuocId;
    }

    public void setNhomThuocId(Integer nhomThuocId) {
        this.nhomThuocId = nhomThuocId;
    }

    public String getNhaSanXuat() {
        return nhaSanXuat;
    }

    public void setNhaSanXuat(String nhaSanXuat) {
        this.nhaSanXuat = nhaSanXuat;
    }

    public boolean isCanKeDon() {
        return canKeDon;
    }

    public void setCanKeDon(boolean canKeDon) {
        this.canKeDon = canKeDon;
    }

    public BigDecimal getGiaBanLe() {
        return giaBanLe;
    }

    public void setGiaBanLe(BigDecimal giaBanLe) {
        this.giaBanLe = giaBanLe;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
