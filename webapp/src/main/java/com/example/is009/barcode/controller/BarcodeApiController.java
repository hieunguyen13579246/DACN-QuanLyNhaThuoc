package com.example.is009.barcode.controller;

import com.example.is009.barcode.dto.Gs1Result;
import com.example.is009.barcode.service.BarcodeService;
import com.example.is009.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/barcode")
public class BarcodeApiController {

    private final BarcodeService barcodeService;

    public BarcodeApiController(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    @GetMapping("/validate-ean13")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateEan13(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "ma", required = false) String ma) {
        String rawVal = (code != null && !code.isBlank()) ? code : ma;
        String clean = rawVal != null ? rawVal.trim() : "";
        boolean hopLe = barcodeService.kiemTraEan13(clean);
        String goiYCheckDigit = "";
        String goiYMaDung = "";
        if (!hopLe && clean.matches("\\d{12,13}")) {
            char check = barcodeService.tinhCheckDigit(clean.substring(0, 12));
            goiYCheckDigit = String.valueOf(check);
            goiYMaDung = clean.substring(0, 12) + check;
        }
        Map<String, Object> data = Map.of(
                "ma", clean,
                "hopLe", hopLe,
                "goiYCheckDigit", goiYCheckDigit,
                "goiYMaDung", goiYMaDung
        );
        return ResponseEntity.ok(ApiResponse.thanhCong(data));
    }

    @GetMapping("/parse-gs1")
    public ResponseEntity<ApiResponse<Gs1Result>> parseGs1(@RequestParam("raw") String raw) {
        Gs1Result res = barcodeService.phanTichGs1(raw);
        return ResponseEntity.ok(ApiResponse.thanhCong(res));
    }
}
