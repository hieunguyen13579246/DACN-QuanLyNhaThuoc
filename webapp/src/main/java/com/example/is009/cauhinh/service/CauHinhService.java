package com.example.is009.cauhinh.service;

import com.example.is009.cauhinh.dto.AuditLogDTO;
import com.example.is009.cauhinh.dto.CauHinhDTO;
import com.example.is009.cauhinh.dto.CauHinhRequest;
import com.example.is009.cauhinh.entity.AuditLog;
import com.example.is009.cauhinh.entity.CauHinh;
import com.example.is009.cauhinh.repository.AuditLogRepository;
import com.example.is009.cauhinh.repository.CauHinhRepository;
import com.example.is009.common.ThoiGianVN;
import com.example.is009.exception.NghiepVuException;
import com.example.is009.security.PhamViService;
import com.example.is009.security.VaiTro;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class CauHinhService {

    private final CauHinhRepository cauHinhRepo;
    private final AuditLogRepository auditLogRepo;
    private final PhamViService phamViService;

    public CauHinhService(CauHinhRepository cauHinhRepo,
                          AuditLogRepository auditLogRepo,
                          PhamViService phamViService) {
        this.cauHinhRepo = cauHinhRepo;
        this.auditLogRepo = auditLogRepo;
        this.phamViService = phamViService;
    }

    public List<CauHinhDTO> layTatCa() {
        return cauHinhRepo.findAll(Sort.by("khoa")).stream()
                .map(c -> new CauHinhDTO(c.getKhoa(), c.getGiaTri(), c.getMoTa()))
                .toList();
    }

    public int layGiaTriInt(String khoa, int macDinh) {
        return cauHinhRepo.findById(khoa)
                .map(CauHinh::toInt)
                .filter(v -> v > 0)
                .orElse(macDinh);
    }

    @Transactional
    public CauHinhDTO capNhat(String khoa, CauHinhRequest req) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        CauHinh ch = cauHinhRepo.findById(khoa)
                .orElseThrow(() -> new NghiepVuException("50014", "Không tìm thấy khóa cấu hình: " + khoa));
        String cu = ch.getGiaTri();
        ch.setGiaTri(req.getGiaTri().trim());
        if (req.getMoTa() != null) {
            ch.setMoTa(req.getMoTa().trim());
        }
        cauHinhRepo.save(ch);

        AuditLog log = new AuditLog();
        log.setNguoiDungId(phamViService.layNguoiDungId());
        log.setHanhDong("CAP_NHAT_CAU_HINH");
        log.setDoiTuong("CauHinh");
        log.setDuLieuCu("{\"khoa\":\"" + khoa + "\",\"gia_tri\":\"" + cu + "\"}");
        log.setDuLieuMoi("{\"khoa\":\"" + khoa + "\",\"gia_tri\":\"" + ch.getGiaTri() + "\"}");
        log.setCreatedAt(Instant.now());
        auditLogRepo.save(log);

        return new CauHinhDTO(ch.getKhoa(), ch.getGiaTri(), ch.getMoTa());
    }

    public List<AuditLogDTO> layNhatKyGanDay(int gioiHan) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        int limit = Math.min(200, Math.max(10, gioiHan));
        return auditLogRepo.findAllByOrderByIdDesc(PageRequest.of(0, limit)).stream()
                .map(a -> new AuditLogDTO(
                        a.getId(),
                        a.getNguoiDungId(),
                        a.getHanhDong(),
                        a.getDoiTuong(),
                        a.getDoiTuongId(),
                        a.getDuLieuCu(),
                        a.getDuLieuMoi(),
                        a.getCreatedAt(),
                        ThoiGianVN.dinhDangNgayGio(a.getCreatedAt())
                ))
                .toList();
    }
}
