package com.example.is009.nhathuoc.service;

import com.example.is009.exception.NghiepVuException;
import com.example.is009.nhathuoc.dto.NhaThuocDTO;
import com.example.is009.nhathuoc.dto.NhaThuocRequest;
import com.example.is009.nhathuoc.entity.NhaThuoc;
import com.example.is009.nhathuoc.repository.NhaThuocRepository;
import com.example.is009.security.PhamViService;
import com.example.is009.security.VaiTro;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NhaThuocService {

    private final NhaThuocRepository nhaThuocRepo;
    private final PhamViService phamViService;

    public NhaThuocService(NhaThuocRepository nhaThuocRepo, PhamViService phamViService) {
        this.nhaThuocRepo = nhaThuocRepo;
        this.phamViService = phamViService;
    }

    public List<NhaThuocDTO> layTatCa() {
        return nhaThuocRepo.findAll(Sort.by("id")).stream()
                .map(this::sangDto)
                .toList();
    }

    public List<NhaThuocDTO> layDangHoatDong() {
        return nhaThuocRepo.findByTrangThaiOrderByIdAsc("HOAT_DONG").stream()
                .map(this::sangDto)
                .toList();
    }

    public NhaThuocDTO layTheoId(int id) {
        NhaThuoc nt = nhaThuocRepo.findById(id)
                .orElseThrow(() -> new NghiepVuException("50014", "Không tìm thấy nhà thuốc id=" + id));
        return sangDto(nt);
    }

    @Transactional
    public NhaThuocDTO taoMoi(NhaThuocRequest req) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        if (nhaThuocRepo.existsByMaIgnoreCase(req.getMa().trim())) {
            throw new NghiepVuException("DUPLICATE",
                    "Mã nhà thuốc '" + req.getMa() + "' đã tồn tại.",
                    "Vui lòng nhập mã nhà thuốc khác.");
        }
        NhaThuoc nt = new NhaThuoc();
        capNhatTuRequest(nt, req);
        nhaThuocRepo.save(nt);
        return sangDto(nt);
    }

    @Transactional
    public NhaThuocDTO capNhat(int id, NhaThuocRequest req) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        NhaThuoc nt = nhaThuocRepo.findById(id)
                .orElseThrow(() -> new NghiepVuException("50014", "Không tìm thấy nhà thuốc id=" + id));
        capNhatTuRequest(nt, req);
        nhaThuocRepo.save(nt);
        return sangDto(nt);
    }

    private void capNhatTuRequest(NhaThuoc nt, NhaThuocRequest req) {
        nt.setMa(req.getMa().trim().toUpperCase());
        nt.setTen(req.getTen().trim());
        nt.setDiaChi(req.getDiaChi().trim());
        nt.setKhuVucId(req.getKhuVucId());
        nt.setSoDienThoai(req.getSoDienThoai() != null ? req.getSoDienThoai().trim() : null);
        nt.setTrangThai(req.getTrangThai() != null ? req.getTrangThai().trim().toUpperCase() : "HOAT_DONG");
        nt.setNgayKhaiTruong(req.getNgayKhaiTruong());
    }

    private NhaThuocDTO sangDto(NhaThuoc nt) {
        return new NhaThuocDTO(
                nt.getId(),
                nt.getMa(),
                nt.getTen(),
                nt.getDiaChi(),
                nt.getKhuVucId(),
                nt.getSoDienThoai(),
                nt.getTrangThai(),
                nt.getNgayKhaiTruong()
        );
    }
}
