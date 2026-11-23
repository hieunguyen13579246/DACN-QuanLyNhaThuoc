package com.example.is009.khuvuc.service;

import com.example.is009.exception.NghiepVuException;
import com.example.is009.khuvuc.dto.KhuVucDTO;
import com.example.is009.khuvuc.dto.KhuVucRequest;
import com.example.is009.khuvuc.entity.KhuVuc;
import com.example.is009.khuvuc.repository.KhuVucRepository;
import com.example.is009.security.PhamViService;
import com.example.is009.security.VaiTro;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KhuVucService {

    private final KhuVucRepository khuVucRepo;
    private final PhamViService phamViService;

    public KhuVucService(KhuVucRepository khuVucRepo, PhamViService phamViService) {
        this.khuVucRepo = khuVucRepo;
        this.phamViService = phamViService;
    }

    public List<KhuVucDTO> layTatCa() {
        List<KhuVuc> all = khuVucRepo.findAll(Sort.by("id"));
        Map<Integer, String> mapTen = all.stream()
                .collect(Collectors.toMap(KhuVuc::getId, KhuVuc::getTen));
        Map<Integer, Integer> mapDanSoTinh = all.stream()
                .filter(k -> "QUAN".equals(k.getCap()) && k.getKhuVucChaId() != null)
                .collect(Collectors.groupingBy(
                        KhuVuc::getKhuVucChaId,
                        Collectors.summingInt(k -> k.getDanSo() != null ? k.getDanSo() : 0)
                ));

        return all.stream()
                .map(k -> new KhuVucDTO(
                        k.getId(),
                        k.getMa(),
                        k.getTen(),
                        k.getCap(),
                        k.getKhuVucChaId(),
                        k.getKhuVucChaId() != null ? mapTen.get(k.getKhuVucChaId()) : null,
                        "TINH".equals(k.getCap()) ? mapDanSoTinh.getOrDefault(k.getId(), k.getDanSo()) : k.getDanSo()
                ))
                .toList();
    }

    public List<KhuVucDTO> layTheoCap(String cap) {
        return layTatCa().stream()
                .filter(k -> cap == null || cap.isBlank() || cap.equalsIgnoreCase(k.cap()))
                .toList();
    }

    @Transactional
    public KhuVucDTO taoMoi(KhuVucRequest req) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        if (khuVucRepo.existsByMaIgnoreCase(req.getMa().trim())) {
            throw new NghiepVuException("DUPLICATE",
                    "Mã khu vực '" + req.getMa() + "' đã tồn tại.",
                    "Vui lòng nhập mã khu vực khác.");
        }
        KhuVuc kv = new KhuVuc();
        capNhatTuRequest(kv, req);
        khuVucRepo.save(kv);
        return sangDtoDon(kv);
    }

    @Transactional
    public KhuVucDTO capNhat(int id, KhuVucRequest req) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        KhuVuc kv = khuVucRepo.findById(id)
                .orElseThrow(() -> new NghiepVuException("50014", "Không tìm thấy khu vực id=" + id));
        capNhatTuRequest(kv, req);
        khuVucRepo.save(kv);
        return sangDtoDon(kv);
    }

    private void capNhatTuRequest(KhuVuc kv, KhuVucRequest req) {
        kv.setMa(req.getMa().trim().toUpperCase());
        kv.setTen(req.getTen().trim());
        kv.setCap(req.getCap().trim().toUpperCase());
        kv.setKhuVucChaId("QUAN".equalsIgnoreCase(req.getCap()) ? req.getKhuVucChaId() : null);
        kv.setDanSo("QUAN".equalsIgnoreCase(req.getCap()) ? req.getDanSo() : null);
    }

    private KhuVucDTO sangDtoDon(KhuVuc k) {
        String tenCha = null;
        if (k.getKhuVucChaId() != null) {
            tenCha = khuVucRepo.findById(k.getKhuVucChaId()).map(KhuVuc::getTen).orElse(null);
        }
        return new KhuVucDTO(k.getId(), k.getMa(), k.getTen(), k.getCap(), k.getKhuVucChaId(), tenCha, k.getDanSo());
    }
}
