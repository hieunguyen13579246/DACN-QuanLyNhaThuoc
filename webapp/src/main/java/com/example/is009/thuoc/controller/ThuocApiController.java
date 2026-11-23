package com.example.is009.thuoc.controller;

import com.example.is009.common.ApiResponse;
import com.example.is009.thuoc.dto.MaVachRequest;
import com.example.is009.thuoc.dto.MaVachThuocDTO;
import com.example.is009.thuoc.dto.NhomThuocDTO;
import com.example.is009.thuoc.dto.NhomThuocRequest;
import com.example.is009.thuoc.dto.ThuocDTO;
import com.example.is009.thuoc.dto.ThuocRequest;
import com.example.is009.thuoc.dto.TraCuuMaVachResponse;
import com.example.is009.thuoc.service.ThuocService;
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
@RequestMapping("/api/thuoc")
public class ThuocApiController {

    private final ThuocService thuocService;

    public ThuocApiController(ThuocService thuocService) {
        this.thuocService = thuocService;
    }

    @GetMapping("/ma-vach/{code}")
    public ResponseEntity<ApiResponse<TraCuuMaVachResponse>> traCuuMaVach(
            @PathVariable("code") String code,
            @RequestParam(value = "nhaThuocId", required = false) Integer nhaThuocId) {
        return ResponseEntity.ok(ApiResponse.thanhCong(thuocService.traCuuTheoMaVach(code, nhaThuocId)));
    }

    @GetMapping("/tim-kiem")
    public ResponseEntity<ApiResponse<List<ThuocDTO>>> timKiem(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "nhomThuocId", required = false) Integer nhomThuocId) {
        return ResponseEntity.ok(ApiResponse.thanhCong(thuocService.layDanhSachThuoc(q, nhomThuocId)));
    }

    @GetMapping("/nhom")
    public ResponseEntity<ApiResponse<List<NhomThuocDTO>>> layNhomThuoc() {
        return ResponseEntity.ok(ApiResponse.thanhCong(thuocService.layTatCaNhomThuoc()));
    }

    @GetMapping("/{id}/ma-vach")
    public ResponseEntity<ApiResponse<List<MaVachThuocDTO>>> layMaVachTheoThuoc(@PathVariable("id") int id) {
        return ResponseEntity.ok(ApiResponse.thanhCong(thuocService.layMaVachTheoThuoc(id)));
    }

    @PostMapping("/quan-ly")
    @PreAuthorize("hasAuthority('QUANTRI')")
    public ResponseEntity<ApiResponse<ThuocDTO>> taoThuoc(@Valid @RequestBody ThuocRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã thêm thuốc mới", thuocService.taoThuoc(req)));
    }

    @PutMapping("/quan-ly/{id}")
    @PreAuthorize("hasAuthority('QUANTRI')")
    public ResponseEntity<ApiResponse<ThuocDTO>> capNhatThuoc(
            @PathVariable("id") int id,
            @Valid @RequestBody ThuocRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã cập nhật thuốc", thuocService.capNhatThuoc(id, req)));
    }

    @PostMapping("/quan-ly/nhom")
    @PreAuthorize("hasAuthority('QUANTRI')")
    public ResponseEntity<ApiResponse<NhomThuocDTO>> taoNhom(@Valid @RequestBody NhomThuocRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã thêm nhóm thuốc mới", thuocService.taoNhomThuoc(req)));
    }

    @PostMapping("/quan-ly/ma-vach")
    @PreAuthorize("hasAuthority('QUANTRI')")
    public ResponseEntity<ApiResponse<Void>> themMaVach(@Valid @RequestBody MaVachRequest req) {
        thuocService.themMaVach(req);
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã gán mã vạch cho thuốc thành công", null));
    }
}
