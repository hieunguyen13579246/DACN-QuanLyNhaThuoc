package com.example.is009.cauhinh.controller;

import com.example.is009.cauhinh.service.CauHinhService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasAuthority('QUANTRI')")
public class CauHinhController {

    private final CauHinhService cauHinhService;

    public CauHinhController(CauHinhService cauHinhService) {
        this.cauHinhService = cauHinhService;
    }

    @GetMapping({"/cauhinh", "/quantri/cau-hinh"})
    public String trangCauHinh(Model model) {
        model.addAttribute("danhSachCauHinh", cauHinhService.layTatCa());
        return "quantri/cau-hinh";
    }
}
