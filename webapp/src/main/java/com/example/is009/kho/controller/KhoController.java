package com.example.is009.kho.controller;

import com.example.is009.kho.service.KhoService;
import com.example.is009.nhacungcap.service.NhaCungCapService;
import com.example.is009.nhathuoc.service.NhaThuocService;
import com.example.is009.security.PhamViService;
import com.example.is009.thuoc.service.ThuocService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/kho")
public class KhoController {

    private final KhoService khoService;
    private final ThuocService thuocService;
    private final NhaCungCapService nhaCungCapService;
    private final NhaThuocService nhaThuocService;
    private final PhamViService phamViService;

    public KhoController(KhoService khoService,
                         ThuocService thuocService,
                         NhaCungCapService nhaCungCapService,
                         NhaThuocService nhaThuocService,
                         PhamViService phamViService) {
        this.khoService = khoService;
        this.thuocService = thuocService;
        this.nhaCungCapService = nhaCungCapService;
        this.nhaThuocService = nhaThuocService;
        this.phamViService = phamViService;
    }

    @GetMapping("/nhap-kho")
    @PreAuthorize("hasAnyAuthority('THUKHO', 'QUANLY', 'QUANTRI')")
    public String manHinhNhapKho(@RequestParam(required = false) Integer thuocId,
                                 @RequestParam(required = false) Integer soLuong,
                                 Model model) {
        model.addAttribute("danhSachNcc", nhaCungCapService.layTatCa());
        model.addAttribute("danhSachThuoc", thuocService.layDanhSachThuoc(null, null));
        model.addAttribute("danhSachPhieuNhap", khoService.layDanhSachPhieu(null, "NHAP", null));
        if (phamViService.laQuanTri()) {
            model.addAttribute("danhSachNhaThuoc", nhaThuocService.layTatCa());
        }
        model.addAttribute("prefillThuocId", thuocId);
        model.addAttribute("prefillSoLuong", soLuong);
        return "kho/nhap-kho";
    }

    @GetMapping("/ton-kho")
    public String manHinhTonKho(@RequestParam(required = false) Integer nhaThuocId,
                                @RequestParam(required = false) Integer nhomThuocId,
                                @RequestParam(required = false) String tuKhoa,
                                @RequestParam(defaultValue = "thuoc") String cheDo,
                                Model model) {
        model.addAttribute("tonTheoThuoc", khoService.layTonTheoThuoc(nhaThuocId, nhomThuocId, tuKhoa));
        model.addAttribute("tonTheoLo", khoService.layTonTheoLo(nhaThuocId, null, tuKhoa, false));
        model.addAttribute("danhSachNhom", thuocService.layTatCaNhomThuoc());
        if (phamViService.laQuanTri()) {
            model.addAttribute("danhSachNhaThuoc", nhaThuocService.layTatCa());
        }
        model.addAttribute("nhaThuocId", nhaThuocId);
        model.addAttribute("nhomThuocId", nhomThuocId);
        model.addAttribute("tuKhoa", tuKhoa);
        model.addAttribute("cheDo", cheDo);
        return "kho/ton-kho";
    }

    @GetMapping("/phieu-kho")
    public String manHinhPhieuKho(@RequestParam(required = false) Integer nhaThuocId,
                                  @RequestParam(required = false) String loaiPhieu,
                                  @RequestParam(required = false) String trangThai,
                                  @RequestParam(required = false) String moForm,
                                  @RequestParam(required = false) Integer prefillLoThuocId,
                                  @RequestParam(required = false) Integer prefillSoLuong,
                                  @RequestParam(required = false) String prefillLyDo,
                                  Model model) {
        model.addAttribute("danhSachPhieu", khoService.layDanhSachPhieu(nhaThuocId, loaiPhieu, trangThai));
        model.addAttribute("danhSachNhaThuoc", nhaThuocService.layTatCa());
        model.addAttribute("danhSachLoConTon", khoService.layTonTheoLo(nhaThuocId, null, null, true));
        model.addAttribute("nhaThuocHienTaiId", phamViService.layNhaThuocIdHienTai());
        model.addAttribute("nhaThuocId", nhaThuocId);
        model.addAttribute("loaiPhieu", loaiPhieu);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("moForm", moForm);
        model.addAttribute("prefillLoThuocId", prefillLoThuocId);
        model.addAttribute("prefillSoLuong", prefillSoLuong);
        model.addAttribute("prefillLyDo", prefillLyDo);
        return "kho/phieu-kho";
    }

    @GetMapping("/duyet-phieu")
    @PreAuthorize("hasAnyAuthority('QUANLY', 'QUANTRI')")
    public String manHinhDuyetPhieu(@RequestParam(required = false) Integer nhaThuocId,
                                    @RequestParam(defaultValue = "CHO_DUYET") String trangThai,
                                    Model model) {
        model.addAttribute("danhSachPhieu", khoService.layDanhSachPhieu(nhaThuocId, null, trangThai));
        if (phamViService.laQuanTri()) {
            model.addAttribute("danhSachNhaThuoc", nhaThuocService.layTatCa());
        }
        model.addAttribute("nhaThuocId", nhaThuocId);
        model.addAttribute("trangThai", trangThai);
        return "kho/duyet-phieu";
    }

    @GetMapping("/in-tem")
    @PreAuthorize("hasAnyAuthority('THUKHO', 'QUANLY', 'QUANTRI')")
    public String manHinhInTem(@RequestParam(required = false) Integer nhaThuocId, Model model) {
        model.addAttribute("danhSachLo", khoService.layTonTheoLo(nhaThuocId, null, null, true));
        model.addAttribute("danhSachThuoc", thuocService.layDanhSachThuoc(null, null));
        return "kho/in-tem";
    }

    @GetMapping("/dinh-muc")
    @PreAuthorize("hasAnyAuthority('QUANLY', 'QUANTRI')")
    public String manHinhDinhMuc(@RequestParam(required = false) Integer nhaThuocId, Model model) {
        model.addAttribute("danhSachDinhMuc", khoService.layDinhMucTon(nhaThuocId));
        model.addAttribute("danhSachThuoc", thuocService.layDanhSachThuoc(null, null));
        if (phamViService.laQuanTri()) {
            model.addAttribute("danhSachNhaThuoc", nhaThuocService.layTatCa());
        }
        model.addAttribute("nhaThuocId", nhaThuocId);
        return "kho/dinh-muc";
    }
}
