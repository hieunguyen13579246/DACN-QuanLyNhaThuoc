package com.example.is009.thuoc.controller;

import com.example.is009.thuoc.service.ThuocService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/thuoc")
public class ThuocController {

    private final ThuocService thuocService;

    public ThuocController(ThuocService thuocService) {
        this.thuocService = thuocService;
    }

    @GetMapping({"/tra-cuu", "/tra-cuu-ma-vach"})
    public String trangTraCuuMaVach(
            @RequestParam(value = "ma", required = false) String ma,
            Model model) {
        model.addAttribute("maBanDau", ma);
        return "thuoc/tra-cuu-ma-vach";
    }

    @GetMapping("/quan-ly")
    @PreAuthorize("hasAuthority('QUANTRI')")
    public String trangQuanLyThuoc(
            @RequestParam(value = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(value = "nhomThuocId", required = false) Integer nhomThuocId,
            Model model) {
        model.addAttribute("danhSachNhom", thuocService.layTatCaNhomThuoc());
        model.addAttribute("danhSachThuoc", thuocService.layDanhSachThuoc(tuKhoa, nhomThuocId));
        model.addAttribute("tuKhoa", tuKhoa);
        model.addAttribute("nhomThuocId", nhomThuocId);
        return "thuoc/quan-ly";
    }
}
