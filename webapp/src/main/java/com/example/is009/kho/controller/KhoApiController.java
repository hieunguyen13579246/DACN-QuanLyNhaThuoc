package com.example.is009.kho.controller;

import com.example.is009.common.ApiResponse;
import com.example.is009.kho.dto.*;
import com.example.is009.kho.service.KhoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kho")
public class KhoApiController {

    private final KhoService khoService;

    public KhoApiController(KhoService khoService) {
        this.khoService = khoService;
    }

    @GetMapping("/ton-thuoc")
    public ApiResponse<List<TonKhoThuocDTO>> layTonTheoThuoc(
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) Integer nhomThuocId,
            @RequestParam(required = false) String tuKhoa) {
        return ApiResponse.thanhCong(khoService.layTonTheoThuoc(nhaThuocId, nhomThuocId, tuKhoa));
    }

    @GetMapping("/ton-lo")
    public ApiResponse<List<TonKhoLoDTO>> layTonTheoLo(
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) Integer thuocId,
            @RequestParam(required = false) String tuKhoa,
            @RequestParam(defaultValue = "true") boolean chiLayConTon) {
        return ApiResponse.thanhCong(khoService.layTonTheoLo(nhaThuocId, thuocId, tuKhoa, chiLayConTon));
    }

    @PostMapping("/nhap-kho")
    @PreAuthorize("hasAnyAuthority('THUKHO', 'QUANLY', 'QUANTRI')")
    public ApiResponse<PhieuKhoDTO> nhapKho(@Valid @RequestBody PhieuNhapRequest req) {
        PhieuKhoDTO dto = khoService.nhapKho(req);
        return ApiResponse.thanhCong("Đã tạo phiếu nhập kho " + dto.getMaPhieu() + " (Chờ Quản lý / Quản trị duyệt)", dto);
    }

    @PostMapping("/dieu-chuyen")
    @PreAuthorize("hasAnyAuthority('THUKHO', 'QUANLY', 'QUANTRI')")
    public ApiResponse<PhieuKhoDTO> taoPhieuDieuChuyen(@Valid @RequestBody PhieuDieuChuyenRequest req) {
        PhieuKhoDTO dto = khoService.taoPhieuDieuChuyen(req);
        return ApiResponse.thanhCong("Đã tạo phiếu điều chuyển " + dto.getMaPhieu() + " (Chờ duyệt)", dto);
    }

    @PostMapping("/xuat-huy")
    @PreAuthorize("hasAnyAuthority('THUKHO', 'QUANLY', 'QUANTRI')")
    public ApiResponse<PhieuKhoDTO> taoPhieuXuatHuy(@Valid @RequestBody PhieuXuatHuyRequest req) {
        PhieuKhoDTO dto = khoService.taoPhieuXuatHuy(req);
        return ApiResponse.thanhCong("Đã tạo phiếu xuất hủy " + dto.getMaPhieu() + " (Chờ duyệt)", dto);
    }

    @GetMapping("/phieu/{id}")
    public ApiResponse<PhieuKhoDTO> layChiTietPhieu(@PathVariable Integer id) {
        return ApiResponse.thanhCong(khoService.layPhieuTheoId(id));
    }

    @PostMapping("/phieu/{id}/duyet")
    @PreAuthorize("hasAnyAuthority('QUANLY', 'QUANTRI')")
    public ApiResponse<Void> duyetPhieu(@PathVariable Integer id, @Valid @RequestBody DuyetPhieuRequest req) {
        khoService.duyetPhieuKho(id, req);
        String msg = "DUYET".equalsIgnoreCase(req.getKetQua())
                ? "Đã duyệt phiếu kho và cập nhật tồn kho thành công!"
                : "Đã từ chối phiếu kho!";
        return ApiResponse.thanhCong(msg, null);
    }

    @PostMapping("/phieu/{id}/huy")
    @PreAuthorize("hasAnyAuthority('THUKHO', 'QUANLY', 'QUANTRI')")
    public ApiResponse<Void> huyPhieu(@PathVariable Integer id) {
        khoService.huyPhieuKho(id);
        return ApiResponse.thanhCong("Đã hủy phiếu kho đang chờ duyệt", null);
    }

    @PostMapping("/dinh-muc")
    @PreAuthorize("hasAnyAuthority('QUANLY', 'QUANTRI')")
    public ApiResponse<Void> luuDinhMuc(@Valid @RequestBody DinhMucTonRequest req) {
        khoService.luuDinhMucTon(req);
        return ApiResponse.thanhCong("Đã cập nhật định mức tồn kho", null);
    }

    @GetMapping("/doi-soat")
    @PreAuthorize("hasAuthority('QUANTRI')")
    public ApiResponse<List<DoiSoatTonKhoDTO>> doiSoat(@RequestParam(required = false) Integer nhaThuocId) {
        return ApiResponse.thanhCong(khoService.doiSoatTonKho(nhaThuocId));
    }
}
