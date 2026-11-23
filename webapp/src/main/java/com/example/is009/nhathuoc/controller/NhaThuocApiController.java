package com.example.is009.nhathuoc.controller;

import com.example.is009.common.ApiResponse;
import com.example.is009.nhathuoc.dto.NhaThuocDTO;
import com.example.is009.nhathuoc.dto.NhaThuocRequest;
import com.example.is009.nhathuoc.service.NhaThuocService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/nhathuoc")
public class NhaThuocApiController {

    private final NhaThuocService nhaThuocService;

    public NhaThuocApiController(NhaThuocService nhaThuocService) {
        this.nhaThuocService = nhaThuocService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NhaThuocDTO>>> layDanhSach() {
        return ResponseEntity.ok(ApiResponse.thanhCong(nhaThuocService.layTatCa()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('QUANTRI')")
    public ResponseEntity<ApiResponse<NhaThuocDTO>> taoMoi(@Valid @RequestBody NhaThuocRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã tạo nhà thuốc mới", nhaThuocService.taoMoi(req)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('QUANTRI')")
    public ResponseEntity<ApiResponse<NhaThuocDTO>> capNhat(
            @PathVariable("id") int id,
            @Valid @RequestBody NhaThuocRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã cập nhật nhà thuốc", nhaThuocService.capNhat(id, req)));
    }
}
