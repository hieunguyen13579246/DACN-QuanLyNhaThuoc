package com.example.is009.khachhang.service;

import com.example.is009.exception.NghiepVuException;
import com.example.is009.khachhang.dto.KhachHangDTO;
import com.example.is009.khachhang.dto.KhachHangRequest;
import com.example.is009.khachhang.entity.KhachHang;
import com.example.is009.khachhang.repository.KhachHangRepository;
import com.example.is009.khuvuc.dto.KhuVucDTO;
import com.example.is009.khuvuc.service.KhuVucService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KhachHangService {

    private final KhachHangRepository khachHangRepo;
    private final KhuVucService khuVucService;

    public KhachHangService(KhachHangRepository khachHangRepo, KhuVucService khuVucService) {
        this.khachHangRepo = khachHangRepo;
        this.khuVucService = khuVucService;
    }

    public List<KhachHangDTO> timKiem(String tuKhoa) {
        Map<Integer, String> mapKv = khuVucService.layTatCa().stream()
                .collect(Collectors.toMap(KhuVucDTO::id, KhuVucDTO::ten));
        List<KhachHang> ds = (tuKhoa != null && !tuKhoa.isBlank())
                ? khachHangRepo.findTop50ByHoTenContainingIgnoreCaseOrSoDienThoaiContainingOrMaContainingIgnoreCaseOrderByIdDesc(
                tuKhoa.trim(), tuKhoa.trim(), tuKhoa.trim())
                : khachHangRepo.findTop100ByOrderByIdDesc();
        return ds.stream()
                .map(k -> sangDto(k, mapKv.get(k.getKhuVucId())))
                .toList();
    }

    public List<KhachHangDTO> timTheoSoDienThoai(String sdt) {
        if (sdt == null || sdt.isBlank()) {
            return List.of();
        }
        return khachHangRepo.findTop10BySoDienThoaiContainingOrderByIdDesc(sdt.trim()).stream()
                .map(k -> sangDto(k, null))
                .toList();
    }

    public KhachHangDTO layTheoId(int id) {
        KhachHang k = khachHangRepo.findById(id)
                .orElseThrow(() -> new NghiepVuException("50018", "Khách hàng không tồn tại trong hệ thống."));
        return sangDto(k, null);
    }

    @Transactional
    public KhachHangDTO taoMoi(KhachHangRequest req) {
        KhachHang kh = new KhachHang();
        String ma = (req.getMa() != null && !req.getMa().isBlank())
                ? req.getMa().trim().toUpperCase()
                : "KH" + (System.currentTimeMillis() % 1000000);
        kh.setMa(ma);
        capNhatTuRequest(kh, req);
        kh.setCreatedAt(Instant.now());
        khachHangRepo.save(kh);
        return sangDto(kh, null);
    }

    @Transactional
    public KhachHangDTO capNhat(int id, KhachHangRequest req) {
        KhachHang kh = khachHangRepo.findById(id)
                .orElseThrow(() -> new NghiepVuException("50018", "Khách hàng không tồn tại."));
        capNhatTuRequest(kh, req);
        khachHangRepo.save(kh);
        return sangDto(kh, null);
    }

    private void capNhatTuRequest(KhachHang kh, KhachHangRequest req) {
        kh.setHoTen(req.getHoTen().trim());
        kh.setSoDienThoai(req.getSoDienThoai().trim());
        kh.setGioiTinh(req.getGioiTinh() != null ? req.getGioiTinh().trim() : null);
        kh.setNamSinh(req.getNamSinh());
        kh.setKhuVucId(req.getKhuVucId());
        kh.setDiaChi(req.getDiaChi() != null ? req.getDiaChi().trim() : null);
        kh.setGhiChuDiUng(req.getGhiChuDiUng() != null ? req.getGhiChuDiUng().trim() : null);
    }

    private KhachHangDTO sangDto(KhachHang k, String tenKhuVuc) {
        return new KhachHangDTO(
                k.getId(),
                k.getMa(),
                k.getHoTen(),
                k.getSoDienThoai(),
                k.anSoDienThoai(),
                k.getGioiTinh(),
                k.getNamSinh(),
                k.getKhuVucId(),
                tenKhuVuc,
                k.getDiaChi(),
                k.getGhiChuDiUng()
        );
    }
}
