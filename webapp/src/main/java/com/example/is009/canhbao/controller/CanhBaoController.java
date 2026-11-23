package com.example.is009.canhbao.controller;

import com.example.is009.canhbao.service.CanhBaoService;
import com.example.is009.nhathuoc.service.NhaThuocService;
import com.example.is009.security.PhamViService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/canhbao")
public class CanhBaoController {

    private final CanhBaoService canhBaoService;
    private final NhaThuocService nhaThuocService;
    private final PhamViService phamViService;

    public CanhBaoController(CanhBaoService canhBaoService,
                             NhaThuocService nhaThuocService,
                             PhamViService phamViService) {
        this.canhBaoService = canhBaoService;
        this.nhaThuocService = nhaThuocService;
        this.phamViService = phamViService;
    }

    @GetMapping
    public String danhSachCanhBao(@RequestParam(required = false) Integer nhaThuocId,
                                  @RequestParam(required = false) String mucHanDung,
                                  @RequestParam(required = false) String mucTonThap,
                                  Model model) {
        model.addAttribute("tongHop", canhBaoService.layTongHopCanhBao(nhaThuocId));
        model.addAttribute("dsHanDung", canhBaoService.layCanhBaoHanDung(nhaThuocId, mucHanDung));
        model.addAttribute("dsTonThap", canhBaoService.layCanhBaoTonThap(nhaThuocId, mucTonThap));
        model.addAttribute("dsTonCham", canhBaoService.layTonChamLuanChuyen(nhaThuocId));
        if (phamViService.laQuanTri()) {
            model.addAttribute("danhSachNhaThuoc", nhaThuocService.layTatCa());
        }
        model.addAttribute("nhaThuocId", nhaThuocId);
        model.addAttribute("mucHanDung", mucHanDung);
        model.addAttribute("mucTonThap", mucTonThap);
        return "canhbao/danh-sach";
    }
}
