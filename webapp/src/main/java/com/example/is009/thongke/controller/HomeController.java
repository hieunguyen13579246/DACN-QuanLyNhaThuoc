package com.example.is009.thongke.controller;

import com.example.is009.canhbao.service.CanhBaoService;
import com.example.is009.kho.service.KhoService;
import com.example.is009.khuvuc.service.KhuVucService;
import com.example.is009.nhacungcap.service.NhaCungCapService;
import com.example.is009.nhathuoc.service.NhaThuocService;
import com.example.is009.security.PhamViService;
import com.example.is009.thongke.service.ThongKeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final ThongKeService thongKeService;
    private final CanhBaoService canhBaoService;
    private final KhoService khoService;
    private final NhaThuocService nhaThuocService;
    private final KhuVucService khuVucService;
    private final NhaCungCapService nhaCungCapService;
    private final PhamViService phamViService;

    public HomeController(ThongKeService thongKeService,
                          CanhBaoService canhBaoService,
                          KhoService khoService,
                          NhaThuocService nhaThuocService,
                          KhuVucService khuVucService,
                          NhaCungCapService nhaCungCapService,
                          PhamViService phamViService) {
        this.thongKeService = thongKeService;
        this.canhBaoService = canhBaoService;
        this.khoService = khoService;
        this.nhaThuocService = nhaThuocService;
        this.khuVucService = khuVucService;
        this.nhaCungCapService = nhaCungCapService;
        this.phamViService = phamViService;
    }

    @GetMapping("/")
    public String trangChu(@RequestParam(required = false) Integer nhaThuocId, Model model) {
        model.addAttribute("kpi", thongKeService.layKpiNhaThuoc(nhaThuocId));
        model.addAttribute("canhBao", canhBaoService.layTongHopCanhBao(nhaThuocId));
        model.addAttribute("nhaThuocId", nhaThuocId);
        if (phamViService.laQuanTri()) {
            model.addAttribute("danhSachNhaThuoc", nhaThuocService.layTatCa());
        }
        return "home/index";
    }

    @GetMapping("/quantri/danh-muc-chuoi")
    @PreAuthorize("hasAuthority('QUANTRI')")
    public String danhMucChuoi(Model model) {
        model.addAttribute("danhSachKhuVuc", khuVucService.layTatCa());
        model.addAttribute("danhSachNhaThuoc", nhaThuocService.layTatCa());
        model.addAttribute("danhSachNcc", nhaCungCapService.layTatCa());
        return "quantri/danh-muc-chuoi";
    }

    @GetMapping("/quantri/nhat-ky-doi-soat")
    @PreAuthorize("hasAuthority('QUANTRI')")
    public String nhatKyDoiSoat(@RequestParam(required = false) Integer nhaThuocId, Model model) {
        model.addAttribute("ketQuaDoiSoat", khoService.doiSoatTonKho(nhaThuocId));
        model.addAttribute("danhSachBienDong", khoService.layBienDongKho(nhaThuocId));
        model.addAttribute("danhSachNhatKy", thongKeService.layAuditLog());
        model.addAttribute("danhSachNhaThuoc", nhaThuocService.layTatCa());
        model.addAttribute("nhaThuocId", nhaThuocId);
        return "quantri/nhat-ky-doi-soat";
    }
}
