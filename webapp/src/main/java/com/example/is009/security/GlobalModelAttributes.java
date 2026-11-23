package com.example.is009.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void themThongTinPhien(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails user) {
            model.addAttribute("nguoiDungHienTai", user);
            model.addAttribute("hoTenHienTai", user.getHoTen());
            model.addAttribute("tenNhaThuocHienTai", user.getTenNhaThuoc());
            model.addAttribute("maNhaThuocHienTai", user.getMaNhaThuoc());
            model.addAttribute("nhaThuocIdHienTai", user.getNhaThuocId());
            model.addAttribute("vaiTroHienTai", user.getVaiTro().name());
            model.addAttribute("tenVaiTroHienTai", user.getVaiTro().getTenHienThi());
            model.addAttribute("laQuanTri", user.getVaiTro() == VaiTro.QUANTRI);
            model.addAttribute("laQuanLy", user.getVaiTro() == VaiTro.QUANLY);
            model.addAttribute("laThuKho", user.getVaiTro() == VaiTro.THUKHO);
            model.addAttribute("laDuocSi", user.getVaiTro() == VaiTro.DUOCSI);
        }
    }
}
