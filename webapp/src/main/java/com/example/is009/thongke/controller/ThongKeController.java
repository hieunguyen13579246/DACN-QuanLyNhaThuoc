package com.example.is009.thongke.controller;

import com.example.is009.canhbao.service.CanhBaoService;
import com.example.is009.khuvuc.service.KhuVucService;
import com.example.is009.nhathuoc.service.NhaThuocService;
import com.example.is009.security.PhamViService;
import com.example.is009.thongke.service.ThongKeService;
import com.example.is009.thuoc.service.ThuocService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/thongke")
public class ThongKeController {

    private final ThongKeService thongKeService;
    private final CanhBaoService canhBaoService;
    private final NhaThuocService nhaThuocService;
    private final KhuVucService khuVucService;
    private final ThuocService thuocService;
    private final PhamViService phamViService;

    public ThongKeController(ThongKeService thongKeService,
                             CanhBaoService canhBaoService,
                             NhaThuocService nhaThuocService,
                             KhuVucService khuVucService,
                             ThuocService thuocService,
                             PhamViService phamViService) {
        this.thongKeService = thongKeService;
        this.canhBaoService = canhBaoService;
        this.nhaThuocService = nhaThuocService;
        this.khuVucService = khuVucService;
        this.thuocService = thuocService;
        this.phamViService = phamViService;
    }

    @GetMapping("/de-xuat-nhap")
    @PreAuthorize("hasAnyAuthority('THUKHO','QUANLY','QUANTRI')")
    public String deXuatNhapHang(@RequestParam(required = false) Integer nhaThuocId,
                                 @RequestParam(required = false) Integer nhomThuocId,
                                 Model model) {
        model.addAttribute("danhSachDeXuat", thongKeService.layDeXuatNhapHang(nhaThuocId, nhomThuocId));
        model.addAttribute("danhSachNhom", thuocService.layTatCaNhomThuoc());
        model.addAttribute("nhomThuocId", nhomThuocId);
        model.addAttribute("nhaThuocId", nhaThuocId);
        if (phamViService.laQuanTri()) {
            model.addAttribute("danhSachNhaThuoc", nhaThuocService.layTatCa());
        }
        return "thongke/de-xuat-nhap";
    }

    @GetMapping("/dashboard-nha-thuoc")
    @PreAuthorize("hasAnyAuthority('DUOCSI','THUKHO','QUANLY','QUANTRI')")
    public String dashboardNhaThuoc(@RequestParam(required = false) Integer nhaThuocId,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
                                    Model model) {
        LocalDate[] range = thongKeService.chuanHoaKhoangNgay(tuNgay, denNgay);
        model.addAttribute("kpi", thongKeService.layKpiNhaThuoc(nhaThuocId));
        model.addAttribute("canhBao", canhBaoService.layTongHopCanhBao(nhaThuocId));
        model.addAttribute("xuHuong", thongKeService.layXuHuongThang(nhaThuocId, null, null, range[0], range[1]));
        model.addAttribute("topThuoc", thongKeService.layTopThuoc(nhaThuocId, null, null, range[0], range[1], 10));
        model.addAttribute("hieuSuat", thongKeService.layHieuSuatNhaThuoc(nhaThuocId, null, range[0], range[1]));
        model.addAttribute("deXuat", thongKeService.layDeXuatNhapHang(nhaThuocId, null));
        model.addAttribute("nhaThuocId", nhaThuocId);
        model.addAttribute("tuNgay", range[0]);
        model.addAttribute("denNgay", range[1]);
        if (phamViService.laQuanTri()) {
            model.addAttribute("danhSachNhaThuoc", nhaThuocService.layTatCa());
        }
        return "thongke/dashboard-nha-thuoc";
    }

    @GetMapping("/dashboard-chuoi")
    @PreAuthorize("hasAnyAuthority('QUANLY','QUANTRI')")
    public String dashboardChuoi(@RequestParam(required = false) Integer khuVucId,
                                 @RequestParam(required = false) Integer nhomThuocId,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
                                 Model model) {
        LocalDate[] range = thongKeService.chuanHoaKhoangNgay(tuNgay, denNgay);
        model.addAttribute("kpi", thongKeService.layKpiNhaThuoc(null));
        model.addAttribute("nhuCauKhuVuc", thongKeService.layNhuCauKhuVuc(null, khuVucId, nhomThuocId, range[0], range[1]));
        model.addAttribute("xuHuong", thongKeService.layXuHuongThang(null, khuVucId, nhomThuocId, range[0], range[1]));
        model.addAttribute("topThuoc", thongKeService.layTopThuoc(null, khuVucId, nhomThuocId, range[0], range[1], 10));
        model.addAttribute("hieuSuat", thongKeService.layHieuSuatNhaThuoc(null, khuVucId, range[0], range[1]));
        model.addAttribute("danhSachKhuVuc", khuVucService.layTatCa());
        model.addAttribute("danhSachNhom", thuocService.layTatCaNhomThuoc());
        model.addAttribute("khuVucId", khuVucId);
        model.addAttribute("nhomThuocId", nhomThuocId);
        model.addAttribute("tuNgay", range[0]);
        model.addAttribute("denNgay", range[1]);
        return "thongke/dashboard-chuoi";
    }
}
