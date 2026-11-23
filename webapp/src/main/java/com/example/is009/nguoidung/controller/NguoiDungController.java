package com.example.is009.nguoidung.controller;

import com.example.is009.nguoidung.service.NguoiDungService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasAuthority('QUANTRI')")
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    public NguoiDungController(NguoiDungService nguoiDungService) {
        this.nguoiDungService = nguoiDungService;
    }

    @GetMapping({"/nguoidung", "/quantri/nguoi-dung"})
    public String trangNguoiDung(Model model) {
        model.addAttribute("danhSachNguoiDung", nguoiDungService.layTatCa());
        return "quantri/nguoi-dung";
    }
}
