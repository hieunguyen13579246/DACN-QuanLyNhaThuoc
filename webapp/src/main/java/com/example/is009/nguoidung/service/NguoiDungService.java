package com.example.is009.nguoidung.service;

import com.example.is009.exception.NghiepVuException;
import com.example.is009.nguoidung.dto.NguoiDungDTO;
import com.example.is009.nguoidung.dto.NguoiDungRequest;
import com.example.is009.nguoidung.entity.NguoiDung;
import com.example.is009.nguoidung.repository.NguoiDungRepository;
import com.example.is009.security.PhamViService;
import com.example.is009.security.VaiTro;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class NguoiDungService {

    private final NguoiDungRepository nguoiDungRepo;
    private final PasswordEncoder passwordEncoder;
    private final PhamViService phamViService;

    public NguoiDungService(NguoiDungRepository nguoiDungRepo,
                            PasswordEncoder passwordEncoder,
                            PhamViService phamViService) {
        this.nguoiDungRepo = nguoiDungRepo;
        this.passwordEncoder = passwordEncoder;
        this.phamViService = phamViService;
    }

    public List<NguoiDungDTO> layTatCa() {
        return nguoiDungRepo.findAll(Sort.by("id")).stream()
                .map(this::sangDto)
                .toList();
    }

    @Transactional
    public NguoiDungDTO taoMoi(NguoiDungRequest req) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        if (nguoiDungRepo.existsByEmailIgnoreCase(req.getEmail().trim())) {
            throw new NghiepVuException("DUPLICATE",
                    "Email '" + req.getEmail() + "' đã tồn tại.",
                    "Vui lòng nhập địa chỉ email khác.");
        }
        VaiTro vt = VaiTro.valueOf(req.getVaiTro().trim().toUpperCase());
        kiemTraVaiTroVaNhaThuoc(vt, req.getNhaThuocId());

        NguoiDung nd = new NguoiDung();
        nd.setHoTen(req.getHoTen().trim());
        nd.setEmail(req.getEmail().trim().toLowerCase());
        String rawPass = (req.getMatKhau() != null && !req.getMatKhau().isBlank()) ? req.getMatKhau().trim() : "123456";
        nd.setPasswordHash(passwordEncoder.encode(rawPass));
        nd.setVaiTro(vt);
        nd.setNhaThuocId(vt == VaiTro.QUANTRI ? null : req.getNhaThuocId());
        nd.setSoChungChiHanhNghe(req.getSoChungChiHanhNghe() != null ? req.getSoChungChiHanhNghe().trim() : null);
        nd.setKichHoat(req.isKichHoat());
        nd.setCreatedAt(Instant.now());
        nguoiDungRepo.save(nd);
        return sangDto(nd);
    }

    @Transactional
    public NguoiDungDTO capNhat(int id, NguoiDungRequest req) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        NguoiDung nd = nguoiDungRepo.findById(id)
                .orElseThrow(() -> new NghiepVuException("50013", "Không tìm thấy người dùng id=" + id));
        VaiTro vt = VaiTro.valueOf(req.getVaiTro().trim().toUpperCase());
        kiemTraVaiTroVaNhaThuoc(vt, req.getNhaThuocId());

        nd.setHoTen(req.getHoTen().trim());
        nd.setEmail(req.getEmail().trim().toLowerCase());
        if (req.getMatKhau() != null && !req.getMatKhau().isBlank()) {
            nd.setPasswordHash(passwordEncoder.encode(req.getMatKhau().trim()));
        }
        nd.setVaiTro(vt);
        nd.setNhaThuocId(vt == VaiTro.QUANTRI ? null : req.getNhaThuocId());
        nd.setSoChungChiHanhNghe(req.getSoChungChiHanhNghe() != null ? req.getSoChungChiHanhNghe().trim() : null);
        nd.setKichHoat(req.isKichHoat());
        nguoiDungRepo.save(nd);
        return sangDto(nd);
    }

    private void kiemTraVaiTroVaNhaThuoc(VaiTro vt, Integer nhaThuocId) {
        if (vt != VaiTro.QUANTRI && nhaThuocId == null) {
            throw new NghiepVuException("50011",
                    "Vai trò " + vt.getTenHienThi() + " bắt buộc phải gán vào một nhà thuốc cụ thể.",
                    "Vui lòng chọn nhà thuốc làm việc cho nhân viên.");
        }
    }

    private NguoiDungDTO sangDto(NguoiDung nd) {
        return new NguoiDungDTO(
                nd.getId(),
                nd.getHoTen(),
                nd.getEmail(),
                nd.getVaiTro().name(),
                nd.getVaiTro().getTenHienThi(),
                nd.getNhaThuocId(),
                nd.getSoChungChiHanhNghe(),
                nd.isKichHoat(),
                nd.getCreatedAt()
        );
    }
}
