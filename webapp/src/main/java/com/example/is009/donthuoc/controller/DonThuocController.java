package com.example.is009.donthuoc.controller;

import com.example.is009.cauhinh.service.CauHinhService;
import com.example.is009.common.ThoiGianVN;
import com.example.is009.donthuoc.service.DonThuocService;
import com.example.is009.nhathuoc.service.NhaThuocService;
import com.example.is009.thuoc.service.ThuocService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/donthuoc")
public class DonThuocController {

    private final DonThuocService donThuocService;
    private final ThuocService thuocService;
    private final NhaThuocService nhaThuocService;
    private final CauHinhService cauHinhService;

    public DonThuocController(DonThuocService donThuocService,
                              ThuocService thuocService,
                              NhaThuocService nhaThuocService,
                              CauHinhService cauHinhService) {
        this.donThuocService = donThuocService;
        this.thuocService = thuocService;
        this.nhaThuocService = nhaThuocService;
        this.cauHinhService = cauHinhService;
    }

    @GetMapping
    public String trangDanhSachDon(
            @RequestParam(value = "nhaThuocId", required = false) Integer nhaThuocId,
            @RequestParam(value = "tuKhoa", required = false) String tuKhoa,
            @RequestParam(value = "trangThai", required = false) String trangThai,
            Model model) {
        model.addAttribute("danhSachDon", donThuocService.layDanhSachDon(nhaThuocId, tuKhoa, trangThai));
        model.addAttribute("danhSachNhaThuoc", nhaThuocService.layTatCa());
        model.addAttribute("danhSachThuoc", thuocService.layDanhSachThuoc(null, null));
        model.addAttribute("ngayHomNay", ThoiGianVN.homNay());
        model.addAttribute("soNgayHieuLucMacDinh", cauHinhService.layGiaTriInt("so_ngay_hieu_luc_don_mac_dinh", 5));
        model.addAttribute("nhaThuocIdLoc", nhaThuocId);
        model.addAttribute("tuKhoa", tuKhoa);
        model.addAttribute("trangThaiLoc", trangThai);
        return "donthuoc/danh-sach";
    }
}
