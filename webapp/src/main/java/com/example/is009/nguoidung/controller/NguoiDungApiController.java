package com.example.is009.nguoidung.controller;

import com.example.is009.common.ApiResponse;
import com.example.is009.nguoidung.dto.NguoiDungDTO;
import com.example.is009.nguoidung.dto.NguoiDungRequest;
import com.example.is009.nguoidung.service.NguoiDungService;
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
@RequestMapping("/api/nguoidung")
@PreAuthorize("hasAuthority('QUANTRI')")
public class NguoiDungApiController {

    private final NguoiDungService nguoiDungService;

    public NguoiDungApiController(NguoiDungService nguoiDungService) {
        this.nguoiDungService = nguoiDungService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NguoiDungDTO>>> layDanhSach() {
        return ResponseEntity.ok(ApiResponse.thanhCong(nguoiDungService.layTatCa()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NguoiDungDTO>> taoMoi(@Valid @RequestBody NguoiDungRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã tạo người dùng mới", nguoiDungService.taoMoi(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NguoiDungDTO>> capNhat(
            @PathVariable("id") int id,
            @Valid @RequestBody NguoiDungRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã cập nhật người dùng", nguoiDungService.capNhat(id, req)));
    }
}
