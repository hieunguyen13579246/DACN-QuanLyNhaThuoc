package com.example.is009.nhacungcap.service;

import com.example.is009.exception.NghiepVuException;
import com.example.is009.nhacungcap.dto.NhaCungCapDTO;
import com.example.is009.nhacungcap.dto.NhaCungCapRequest;
import com.example.is009.nhacungcap.entity.NhaCungCap;
import com.example.is009.nhacungcap.repository.NhaCungCapRepository;
import com.example.is009.security.PhamViService;
import com.example.is009.security.VaiTro;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NhaCungCapService {

    private final NhaCungCapRepository nccRepo;
    private final PhamViService phamViService;

    public NhaCungCapService(NhaCungCapRepository nccRepo, PhamViService phamViService) {
        this.nccRepo = nccRepo;
        this.phamViService = phamViService;
    }

    public List<NhaCungCapDTO> layTatCa() {
        return nccRepo.findAll(Sort.by("id")).stream()
                .map(this::sangDto)
                .toList();
    }

    public List<NhaCungCapDTO> layDangHoatDong() {
        return nccRepo.findByTrangThaiOrderByTenAsc("HOAT_DONG").stream()
                .map(this::sangDto)
                .toList();
    }

    @Transactional
    public NhaCungCapDTO taoMoi(NhaCungCapRequest req) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        if (nccRepo.existsByMaIgnoreCase(req.getMa().trim())) {
            throw new NghiepVuException("DUPLICATE",
                    "Mã nhà cung cấp '" + req.getMa() + "' đã tồn tại.",
                    "Vui lòng nhập mã NCC khác.");
        }
        NhaCungCap ncc = new NhaCungCap();
        capNhatTuRequest(ncc, req);
        nccRepo.save(ncc);
        return sangDto(ncc);
    }

    @Transactional
    public NhaCungCapDTO capNhat(int id, NhaCungCapRequest req) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        NhaCungCap ncc = nccRepo.findById(id)
                .orElseThrow(() -> new NghiepVuException("50014", "Không tìm thấy nhà cung cấp id=" + id));
        capNhatTuRequest(ncc, req);
        nccRepo.save(ncc);
        return sangDto(ncc);
    }

    private void capNhatTuRequest(NhaCungCap ncc, NhaCungCapRequest req) {
        ncc.setMa(req.getMa().trim().toUpperCase());
        ncc.setTen(req.getTen().trim());
        ncc.setDiaChi(req.getDiaChi() != null ? req.getDiaChi().trim() : null);
        ncc.setSoDienThoai(req.getSoDienThoai() != null ? req.getSoDienThoai().trim() : null);
        ncc.setMaSoThue(req.getMaSoThue() != null ? req.getMaSoThue().trim() : null);
        ncc.setTrangThai(req.getTrangThai() != null ? req.getTrangThai().trim().toUpperCase() : "HOAT_DONG");
    }

    private NhaCungCapDTO sangDto(NhaCungCap ncc) {
        return new NhaCungCapDTO(
                ncc.getId(),
                ncc.getMa(),
                ncc.getTen(),
                ncc.getDiaChi(),
                ncc.getSoDienThoai(),
                ncc.getMaSoThue(),
                ncc.getTrangThai()
        );
    }
}
