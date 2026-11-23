package com.example.is009.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final int id;
    private final String hoTen;
    private final String email;
    private final String passwordHash;
    private final VaiTro vaiTro;
    private final Integer nhaThuocId;
    private final String maNhaThuoc;
    private final String tenNhaThuoc;
    private final boolean kichHoat;

    public CustomUserDetails(int id, String hoTen, String email, String passwordHash,
                             VaiTro vaiTro, Integer nhaThuocId, String maNhaThuoc,
                             String tenNhaThuoc, boolean kichHoat) {
        this.id = id;
        this.hoTen = hoTen;
        this.email = email;
        this.passwordHash = passwordHash;
        this.vaiTro = vaiTro;
        this.nhaThuocId = nhaThuocId;
        this.maNhaThuoc = maNhaThuoc;
        this.tenNhaThuoc = tenNhaThuoc;
        this.kichHoat = kichHoat;
    }

    public int getId() {
        return id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getEmail() {
        return email;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public Integer getNhaThuocId() {
        return nhaThuocId;
    }

    public String getMaNhaThuoc() {
        return maNhaThuoc;
    }

    public String getTenNhaThuoc() {
        return tenNhaThuoc != null ? tenNhaThuoc : "Toàn chuỗi IS009";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + vaiTro.name()),
                new SimpleGrantedAuthority(vaiTro.name())
        );
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return kichHoat;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return kichHoat;
    }
}
