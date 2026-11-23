package com.example.is009.nhacungcap.controller;

import com.example.is009.common.ApiResponse;
import com.example.is009.nhacungcap.dto.NhaCungCapDTO;
import com.example.is009.nhacungcap.dto.NhaCungCapRequest;
import com.example.is009.nhacungcap.service.NhaCungCapService;
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
@RequestMapping("/api/nhacungcap")
public class NhaCungCapApiController {

    private final NhaCungCapService nccService;

    public NhaCungCapApiController(NhaCungCapService nccService) {
        this.nccService = nccService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NhaCungCapDTO>>> layDanhSach() {
        return ResponseEntity.ok(ApiResponse.thanhCong(nccService.layTatCa()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('QUANTRI')")
    public ResponseEntity<ApiResponse<NhaCungCapDTO>> taoMoi(@Valid @RequestBody NhaCungCapRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã thêm nhà cung cấp", nccService.taoMoi(req)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('QUANTRI')")
    public ResponseEntity<ApiResponse<NhaCungCapDTO>> capNhat(
            @PathVariable("id") int id,
            @Valid @RequestBody NhaCungCapRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã cập nhật nhà cung cấp", nccService.capNhat(id, req)));
    }
}
