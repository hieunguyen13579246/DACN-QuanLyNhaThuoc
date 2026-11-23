package com.example.is009.donthuoc.controller;

import com.example.is009.common.ApiResponse;
import com.example.is009.donthuoc.dto.DonThuocDTO;
import com.example.is009.donthuoc.dto.TaoDonThuocRequest;
import com.example.is009.donthuoc.service.DonThuocService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/donthuoc")
public class DonThuocApiController {

    private final DonThuocService donThuocService;

    public DonThuocApiController(DonThuocService donThuocService) {
        this.donThuocService = donThuocService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DonThuocDTO>>> layDanhSach(
            @RequestParam(value = "nhaThuocId", required = false) Integer nhaThuocId,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "trangThai", required = false) String trangThai) {
        return ResponseEntity.ok(ApiResponse.thanhCong(donThuocService.layDanhSachDon(nhaThuocId, q, trangThai)));
    }

    @GetMapping("/kha-dung")
    public ResponseEntity<ApiResponse<List<DonThuocDTO>>> layDonKhaDungPos(
            @RequestParam(value = "nhaThuocId", required = false) Integer nhaThuocId,
            @RequestParam(value = "q", required = false) String q) {
        return ResponseEntity.ok(ApiResponse.thanhCong(donThuocService.layDonKhaDungChoPos(nhaThuocId, q)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DonThuocDTO>> layChiTiet(@PathVariable("id") int id) {
        return ResponseEntity.ok(ApiResponse.thanhCong(donThuocService.layTheoId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DonThuocDTO>> taoDon(@Valid @RequestBody TaoDonThuocRequest req) {
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã tiếp nhận đơn thuốc thành công", donThuocService.taoDonThuoc(req)));
    }

    @PostMapping("/{id}/huy")
    public ResponseEntity<ApiResponse<Void>> huyDon(@PathVariable("id") int id) {
        donThuocService.huyDonThuoc(id);
        return ResponseEntity.ok(ApiResponse.thanhCong("Đã hủy đơn thuốc thành công", null));
    }
}
