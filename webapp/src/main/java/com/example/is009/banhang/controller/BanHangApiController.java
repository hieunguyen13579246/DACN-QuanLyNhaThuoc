package com.example.is009.banhang.controller;

import com.example.is009.banhang.dto.GiaoDichResponse;
import com.example.is009.banhang.dto.GioHangRequest;
import com.example.is009.banhang.dto.HuyGiaoDichRequest;
import com.example.is009.banhang.dto.LoPhanBoDTO;
import com.example.is009.banhang.dto.XemTruocFEFORequest;
import com.example.is009.banhang.service.BanHangService;
import com.example.is009.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/banhang")
public class BanHangApiController {

    private final BanHangService banHangService;

    public BanHangApiController(BanHangService banHangService) {
        this.banHangService = banHangService;
    }

    @PostMapping("/xem-truoc-fefo")
    @PreAuthorize("hasAnyRole('DUOCSI', 'QUANLY')")
    public ApiResponse<List<LoPhanBoDTO>> xemTruocFEFO(@Valid @RequestBody XemTruocFEFORequest req) {
        return ApiResponse.thanhCong(banHangService.xemTruocFEFO(req));
    }

    @PostMapping("/thanh-toan")
    @PreAuthorize("hasAnyRole('DUOCSI', 'QUANLY')")
    public ApiResponse<GiaoDichResponse> thanhToan(@Valid @RequestBody GioHangRequest req) {
        GiaoDichResponse gd = banHangService.thanhToan(req);
        return ApiResponse.thanhCong("Thanh toán thành công giao dịch " + gd.getMaGiaoDich(), gd);
    }

    @PostMapping("/huy/{id}")
    @PreAuthorize("hasRole('QUANLY')")
    public ApiResponse<Void> huyGiaoDich(@PathVariable Integer id, @Valid @RequestBody HuyGiaoDichRequest req) {
        banHangService.huyGiaoDich(id, req);
        return ApiResponse.thanhCong("Đã hủy giao dịch và hoàn trả tồn kho theo đúng lô xuất", null);
    }

    @GetMapping("/lich-su")
    @PreAuthorize("hasAnyRole('DUOCSI', 'QUANLY', 'QUANTRI')")
    public ApiResponse<List<GiaoDichResponse>> lichSu(
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String tuKhoa) {
        return ApiResponse.thanhCong(banHangService.timKiemGiaoDich(nhaThuocId, tuNgay, denNgay, trangThai, tuKhoa));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DUOCSI', 'QUANLY', 'QUANTRI')")
    public ApiResponse<GiaoDichResponse> chiTiet(@PathVariable Integer id) {
        return ApiResponse.thanhCong(banHangService.layChiTietGiaoDich(id));
    }
}
