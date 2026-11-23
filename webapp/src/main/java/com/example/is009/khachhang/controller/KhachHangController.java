package com.example.is009.khachhang.controller;

import com.example.is009.khachhang.service.KhachHangService;
import com.example.is009.khuvuc.service.KhuVucService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/khachhang")
public class KhachHangController {

    private final KhachHangService khachHangService;
    private final KhuVucService khuVucService;

    public KhachHangController(KhachHangService khachHangService, KhuVucService khuVucService) {
        this.khachHangService = khachHangService;
        this.khuVucService = khuVucService;
    }

    @GetMapping
    public String trangKhachHang(
            @RequestParam(value = "tuKhoa", required = false) String tuKhoa,
            Model model) {
        model.addAttribute("danhSachKhachHang", khachHangService.timKiem(tuKhoa));
        model.addAttribute("danhSachKhuVuc", khuVucService.layTheoCap("QUAN"));
        model.addAttribute("tuKhoa", tuKhoa);
        return "khachhang/danh-sach";
    }
}
