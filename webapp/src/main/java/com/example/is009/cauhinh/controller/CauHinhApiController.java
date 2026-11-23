package com.example.is009.cauhinh.controller;

import com.example.is009.cauhinh.dto.AuditLogDTO;
import com.example.is009.cauhinh.dto.CauHinhDTO;
import com.example.is009.cauhinh.dto.CauHinhRequest;
import com.example.is009.cauhinh.service.CauHinhService;
import com.example.is009.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cauhinh")
@PreAuthorize("hasAuthority('QUANTRI')")
public class CauHinhApiController {

    private final CauHinhService cauHinhService;

    public CauHinhApiController(CauHinhService cauHinhService) {
        this.cauHinhService = cauHinhService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CauHinhDTO>>> layTatCa() {
        return ResponseEntity.ok(ApiResponse.thanhCong(cauHinhService.layTatCa()));
    }

    @PutMapping("/{khoa}")
    public ResponseEntity<ApiResponse<CauHinhDTO>> capNhat(
            @PathVariable("khoa") String khoa,
            @Valid @RequestBody CauHinhRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã cập nhật cấu hình", cauHinhService.capNhat(khoa, req)));
    }

    @GetMapping("/audit-log")
    public ResponseEntity<ApiResponse<List<AuditLogDTO>>> layAuditLog(
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return ResponseEntity.ok(ApiResponse.thanhCong(cauHinhService.layNhatKyGanDay(limit)));
    }
}
