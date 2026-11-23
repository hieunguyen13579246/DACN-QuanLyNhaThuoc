package com.example.is009.khachhang.controller;

import com.example.is009.common.ApiResponse;
import com.example.is009.khachhang.dto.KhachHangDTO;
import com.example.is009.khachhang.dto.KhachHangRequest;
import com.example.is009.khachhang.service.KhachHangService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/khachhang")
public class KhachHangApiController {

    private final KhachHangService khachHangService;

    public KhachHangApiController(KhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KhachHangDTO>>> timKiem(
            @RequestParam(value = "q", required = false) String q) {
        return ResponseEntity.ok(ApiResponse.thanhCong(khachHangService.timKiem(q)));
    }

    @GetMapping("/tim-sdt")
    public ResponseEntity<ApiResponse<List<KhachHangDTO>>> timTheoSdt(
            @RequestParam("sdt") String sdt) {
        return ResponseEntity.ok(ApiResponse.thanhCong(khachHangService.timTheoSoDienThoai(sdt)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<KhachHangDTO>> taoMoi(@Valid @RequestBody KhachHangRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã thêm khách hàng mới", khachHangService.taoMoi(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KhachHangDTO>> capNhat(
            @PathVariable("id") int id,
            @Valid @RequestBody KhachHangRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã cập nhật thông tin khách hàng", khachHangService.capNhat(id, req)));
    }
}
