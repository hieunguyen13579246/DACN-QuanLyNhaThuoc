package com.example.is009.thongke.controller;

import com.example.is009.thongke.dto.*;
import com.example.is009.thongke.service.CsvExportService;
import com.example.is009.thongke.service.ThongKeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/thongke")
public class ThongKeApiController {

    private static final String XLSX_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final ThongKeService thongKeService;
    private final CsvExportService csvExportService;

    public ThongKeApiController(ThongKeService thongKeService, CsvExportService csvExportService) {
        this.thongKeService = thongKeService;
        this.csvExportService = csvExportService;
    }

    @GetMapping("/nhu-cau-khu-vuc")
    @PreAuthorize("hasAnyAuthority('QUANLY','QUANTRI')")
    public ResponseEntity<List<NhuCauKhuVucDTO>> apiNhuCauKhuVuc(
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) Integer khuVucId,
            @RequestParam(required = false) Integer nhomThuocId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        return ResponseEntity.ok(thongKeService.layNhuCauKhuVuc(nhaThuocId, khuVucId, nhomThuocId, tuNgay, denNgay));
    }

    @GetMapping("/xu-huong-thang")
    @PreAuthorize("hasAnyAuthority('DUOCSI','THUKHO','QUANLY','QUANTRI')")
    public ResponseEntity<List<XuHuongThangDTO>> apiXuHuongThang(
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) Integer khuVucId,
            @RequestParam(required = false) Integer nhomThuocId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        return ResponseEntity.ok(thongKeService.layXuHuongThang(nhaThuocId, khuVucId, nhomThuocId, tuNgay, denNgay));
    }

    @GetMapping("/top-thuoc")
    @PreAuthorize("hasAnyAuthority('DUOCSI','THUKHO','QUANLY','QUANTRI')")
    public ResponseEntity<List<TopThuocDTO>> apiTopThuoc(
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) Integer khuVucId,
            @RequestParam(required = false) Integer nhomThuocId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(defaultValue = "10") int topN) {
        return ResponseEntity.ok(thongKeService.layTopThuoc(nhaThuocId, khuVucId, nhomThuocId, tuNgay, denNgay, topN));
    }

    @GetMapping("/hieu-suat")
    @PreAuthorize("hasAnyAuthority('QUANLY','QUANTRI')")
    public ResponseEntity<List<HieuSuatNhaThuocDTO>> apiHieuSuat(
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) Integer khuVucId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        return ResponseEntity.ok(thongKeService.layHieuSuatNhaThuoc(nhaThuocId, khuVucId, tuNgay, denNgay));
    }

    @GetMapping({"/csv/nhu-cau-khu-vuc", "/excel/nhu-cau-khu-vuc"})
    @PreAuthorize("hasAnyAuthority('QUANLY','QUANTRI')")
    public ResponseEntity<byte[]> xuatExcelNhuCauKhuVuc(
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) Integer khuVucId,
            @RequestParam(required = false) Integer nhomThuocId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        LocalDate[] range = thongKeService.chuanHoaKhoangNgay(tuNgay, denNgay);
        List<NhuCauKhuVucDTO> rows = thongKeService.layNhuCauKhuVuc(nhaThuocId, khuVucId, nhomThuocId, range[0], range[1]);
        byte[] bytes = csvExportService.xuatExcelNhuCauKhuVuc(rows, range[0], range[1]);
        return fileResponse(bytes, "Bao_Cao_Nhu_Cau_Duoc_Pham_Khu_Vuc_IS009.xlsx");
    }

    @GetMapping({"/csv/hieu-suat", "/excel/hieu-suat"})
    @PreAuthorize("hasAnyAuthority('QUANLY','QUANTRI')")
    public ResponseEntity<byte[]> xuatExcelHieuSuat(
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) Integer khuVucId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        LocalDate[] range = thongKeService.chuanHoaKhoangNgay(tuNgay, denNgay);
        List<HieuSuatNhaThuocDTO> rows = thongKeService.layHieuSuatNhaThuoc(nhaThuocId, khuVucId, range[0], range[1]);
        byte[] bytes = csvExportService.xuatExcelHieuSuatNhaThuoc(rows, range[0], range[1]);
        return fileResponse(bytes, "Bao_Cao_Hieu_Suat_Chuoi_Nha_Thuoc_IS009.xlsx");
    }

    @GetMapping({"/csv/top-thuoc", "/excel/top-thuoc"})
    @PreAuthorize("hasAnyAuthority('DUOCSI','THUKHO','QUANLY','QUANTRI')")
    public ResponseEntity<byte[]> xuatExcelTopThuoc(
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) Integer khuVucId,
            @RequestParam(required = false) Integer nhomThuocId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        LocalDate[] range = thongKeService.chuanHoaKhoangNgay(tuNgay, denNgay);
        List<TopThuocDTO> rows = thongKeService.layTopThuoc(nhaThuocId, khuVucId, nhomThuocId, range[0], range[1], 50);
        byte[] bytes = csvExportService.xuatExcelTopThuoc(rows, range[0], range[1]);
        return fileResponse(bytes, "Bao_Cao_Top_Thuoc_Ban_Chay_IS009.xlsx");
    }

    @GetMapping({"/csv/de-xuat-nhap", "/excel/de-xuat-nhap", "/xuat-csv"})
    @PreAuthorize("hasAnyAuthority('THUKHO','QUANLY','QUANTRI')")
    public ResponseEntity<byte[]> xuatExcelDeXuatNhap(
            @RequestParam(required = false) String loai,
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) Integer nhomThuocId) {
        List<DeXuatNhapHangDTO> rows = thongKeService.layDeXuatNhapHang(nhaThuocId, nhomThuocId);
        byte[] bytes = csvExportService.xuatExcelDeXuatNhapHang(rows);
        return fileResponse(bytes, "Bang_De_Xuat_Nhap_Hang_GPP_IS009.xlsx");
    }

    @GetMapping("/chuoi/xuat-csv")
    @PreAuthorize("hasAnyAuthority('QUANLY','QUANTRI')")
    public ResponseEntity<byte[]> xuatExcelDashboardChuoi(
            @RequestParam(defaultValue = "nhu-cau") String loai,
            @RequestParam(required = false) Integer nhaThuocId,
            @RequestParam(required = false) Integer khuVucId,
            @RequestParam(required = false) Integer nhomThuocId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        if ("hieu-suat".equalsIgnoreCase(loai)) {
            return xuatExcelHieuSuat(nhaThuocId, khuVucId, tuNgay, denNgay);
        }
        if ("top-thuoc".equalsIgnoreCase(loai)) {
            return xuatExcelTopThuoc(nhaThuocId, khuVucId, nhomThuocId, tuNgay, denNgay);
        }
        return xuatExcelNhuCauKhuVuc(nhaThuocId, khuVucId, nhomThuocId, tuNgay, denNgay);
    }

    private ResponseEntity<byte[]> fileResponse(byte[] bytes, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(XLSX_CONTENT_TYPE))
                .contentLength(bytes.length)
                .body(bytes);
    }
}
