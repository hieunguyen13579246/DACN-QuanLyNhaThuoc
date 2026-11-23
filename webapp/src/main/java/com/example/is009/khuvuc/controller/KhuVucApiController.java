package com.example.is009.khuvuc.controller;

import com.example.is009.common.ApiResponse;
import com.example.is009.khuvuc.dto.KhuVucDTO;
import com.example.is009.khuvuc.dto.KhuVucRequest;
import com.example.is009.khuvuc.service.KhuVucService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/khuvuc")
public class KhuVucApiController {

    private final KhuVucService khuVucService;

    public KhuVucApiController(KhuVucService khuVucService) {
        this.khuVucService = khuVucService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KhuVucDTO>>> layDanhSach(
            @RequestParam(value = "cap", required = false) String cap) {
        return ResponseEntity.ok(ApiResponse.thanhCong(khuVucService.layTheoCap(cap)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('QUANTRI')")
    public ResponseEntity<ApiResponse<KhuVucDTO>> taoMoi(@Valid @RequestBody KhuVucRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã tạo khu vực mới", khuVucService.taoMoi(req)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('QUANTRI')")
    public ResponseEntity<ApiResponse<KhuVucDTO>> capNhat(
            @PathVariable("id") int id,
            @Valid @RequestBody KhuVucRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã cập nhật khu vực", khuVucService.capNhat(id, req)));
    }
}
