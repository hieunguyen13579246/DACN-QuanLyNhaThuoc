package com.example.is009.banhang.controller;

import com.example.is009.banhang.dto.GiaoDichResponse;
import com.example.is009.banhang.service.BanHangService;
import com.example.is009.donthuoc.service.DonThuocService;
import com.example.is009.nhathuoc.service.NhaThuocService;
import com.example.is009.security.PhamViService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/banhang")
public class BanHangController {

    private final BanHangService banHangService;
    private final DonThuocService donThuocService;
    private final NhaThuocService nhaThuocService;
    private final PhamViService phamViService;

    public BanHangController(BanHangService banHangService,
                             DonThuocService donThuocService,
                             NhaThuocService nhaThuocService,
                             PhamViService phamViService) {
        this.banHangService = banHangService;
        this.donThuocService = donThuocService;
        this.nhaThuocService = nhaThuocService;
        this.phamViService = phamViService;
    }

    @GetMapping("/pos")
    @PreAuthorize("hasAnyRole('DUOCSI', 'QUANLY')")
    public String manHinhPos(@RequestParam(required = false) Integer donThuocId, Model model) {
        model.addAttribute("donThuocChonSan", donThuocId != null ? donThuocService.layTheoId(donThuocId) : null);
        model.addAttribute("danhSachDonHopLe", donThuocService.layDanhSachDon(null, null, "CHUA_BAN"));
        model.addAttribute("danhSachDonBanMotPhan", donThuocService.layDanhSachDon(null, null, "BAN_MOT_PHAN"));
        return "banhang/pos";
    }

    @GetMapping("/lich-su")
    @PreAuthorize("hasAnyRole('DUOCSI', 'QUANLY', 'QUANTRI')")
    public String lichSuGiaoDich(@RequestParam(required = false) Integer nhaThuocId,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
                                 @RequestParam(required = false) String trangThai,
                                 @RequestParam(required = false) String tuKhoa,
                                 Model model) {
        model.addAttribute("danhSach", banHangService.timKiemGiaoDich(nhaThuocId, tuNgay, denNgay, trangThai, tuKhoa));
        if (phamViService.laQuanTri()) {
            model.addAttribute("danhSachNhaThuoc", nhaThuocService.layTatCa());
        }
        model.addAttribute("nhaThuocId", nhaThuocId);
        model.addAttribute("tuNgay", tuNgay);
        model.addAttribute("denNgay", denNgay);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("tuKhoa", tuKhoa);
        return "banhang/lich-su";
    }

    @GetMapping({"/huy", "/huy-giao-dich"})
    @PreAuthorize("hasRole('QUANLY')")
    public String manHinhHuyGiaoDich(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
                                     @RequestParam(required = false) String tuKhoa,
                                     Model model) {
        model.addAttribute("danhSach", banHangService.timKiemGiaoDich(null, tuNgay, denNgay, "HOAN_THANH", tuKhoa));
        model.addAttribute("tuNgay", tuNgay);
        model.addAttribute("denNgay", denNgay);
        model.addAttribute("tuKhoa", tuKhoa);
        return "banhang/huy-giao-dich";
    }

    @GetMapping({"/hoa-don/{id}", "/hoa-don/{id}/in"})
    @PreAuthorize("hasAnyRole('DUOCSI', 'QUANLY', 'QUANTRI')")
    public String inHoaDon(@PathVariable Integer id,
                           @RequestParam(required = false) java.math.BigDecimal tienKhachDua,
                           @RequestParam(required = false) java.math.BigDecimal tienThoi,
                           Model model) {
        GiaoDichResponse gd = banHangService.layChiTietGiaoDich(id);
        model.addAttribute("gd", gd);
        model.addAttribute("tienKhachDua", tienKhachDua != null ? tienKhachDua : gd.getThanhToan());
        model.addAttribute("tienThoi", tienThoi != null ? tienThoi : java.math.BigDecimal.ZERO);
        return "banhang/hoa-don-in";
    }
}
