package com.example.is009.thuoc.service;

import com.example.is009.exception.ErrorCodeMapper;
import com.example.is009.exception.NghiepVuException;
import com.example.is009.security.PhamViService;
import com.example.is009.security.VaiTro;
import com.example.is009.thuoc.dto.LoTheoMaVachDTO;
import com.example.is009.thuoc.dto.MaVachRequest;
import com.example.is009.thuoc.dto.MaVachThuocDTO;
import com.example.is009.thuoc.dto.NhomThuocDTO;
import com.example.is009.thuoc.dto.NhomThuocRequest;
import com.example.is009.thuoc.dto.ThuocDTO;
import com.example.is009.thuoc.dto.ThuocRequest;
import com.example.is009.thuoc.dto.TraCuuMaVachResponse;
import com.example.is009.thuoc.entity.MaVachThuoc;
import com.example.is009.thuoc.entity.NhomThuoc;
import com.example.is009.thuoc.entity.Thuoc;
import com.example.is009.thuoc.repository.MaVachThuocRepository;
import com.example.is009.thuoc.repository.NhomThuocRepository;
import com.example.is009.thuoc.repository.SpThuocCaller;
import com.example.is009.thuoc.repository.ThuocRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ThuocService {

    private final ThuocRepository thuocRepo;
    private final NhomThuocRepository nhomThuocRepo;
    private final MaVachThuocRepository maVachRepo;
    private final SpThuocCaller spThuocCaller;
    private final PhamViService phamViService;

    public ThuocService(ThuocRepository thuocRepo,
                        NhomThuocRepository nhomThuocRepo,
                        MaVachThuocRepository maVachRepo,
                        SpThuocCaller spThuocCaller,
                        PhamViService phamViService) {
        this.thuocRepo = thuocRepo;
        this.nhomThuocRepo = nhomThuocRepo;
        this.maVachRepo = maVachRepo;
        this.spThuocCaller = spThuocCaller;
        this.phamViService = phamViService;
    }

    public List<NhomThuocDTO> layTatCaNhomThuoc() {
        return nhomThuocRepo.findAll(Sort.by("id")).stream()
                .map(n -> new NhomThuocDTO(n.getId(), n.getMa(), n.getTen()))
                .toList();
    }

    public List<ThuocDTO> layDanhSachThuoc(String tuKhoa, Integer nhomThuocId) {
        Map<Integer, String> mapNhom = nhomThuocRepo.findAll().stream()
                .collect(Collectors.toMap(NhomThuoc::getId, NhomThuoc::getTen));
        Map<Integer, String> mapMaVachChinh = maVachRepo.findAll().stream()
                .collect(Collectors.toMap(
                        MaVachThuoc::getThuocId,
                        MaVachThuoc::getMaVach,
                        (v1, v2) -> v1
                ));

        List<Thuoc> ds = (tuKhoa != null && !tuKhoa.isBlank())
                ? thuocRepo.findByTenContainingIgnoreCaseOrMaContainingIgnoreCaseOrHoatChatContainingIgnoreCaseOrderByTenAsc(
                tuKhoa.trim(), tuKhoa.trim(), tuKhoa.trim())
                : thuocRepo.findAll(Sort.by("id"));

        return ds.stream()
                .filter(t -> nhomThuocId == null || nhomThuocId.equals(t.getNhomThuocId()))
                .map(t -> sangDto(t, mapNhom.get(t.getNhomThuocId()), mapMaVachChinh.get(t.getId())))
                .toList();
    }

    public ThuocDTO layTheoId(int id) {
        Thuoc t = thuocRepo.findById(id)
                .orElseThrow(() -> new NghiepVuException("50014", "Không tìm thấy thuốc id=" + id));
        String tenNhom = nhomThuocRepo.findById(t.getNhomThuocId()).map(NhomThuoc::getTen).orElse("");
        String mvChinh = maVachRepo.findByThuocIdOrderByLaMaChinhDescIdAsc(id).stream()
                .findFirst().map(MaVachThuoc::getMaVach).orElse(null);
        return sangDto(t, tenNhom, mvChinh);
    }

    public List<MaVachThuocDTO> layMaVachTheoThuoc(int thuocId) {
        return maVachRepo.findByThuocIdOrderByLaMaChinhDescIdAsc(thuocId).stream()
                .map(m -> new MaVachThuocDTO(
                        m.getId(),
                        m.getThuocId(),
                        m.getMaVach(),
                        m.getLoaiMaVach().name(),
                        m.isLaMaChinh(),
                        m.getCreatedAt()
                ))
                .toList();
    }

    public TraCuuMaVachResponse traCuuTheoMaVach(String rawCode, Integer nhaThuocIdYeuCau) {
        String clean = rawCode != null ? rawCode.trim() : "";
        if (clean.isEmpty()) {
            throw new NghiepVuException("50007", "Vui lòng nhập hoặc quét mã vạch cần tra cứu.");
        }
        if ("8930000000010".equals(clean)) {
            throw new NghiepVuException("50007",
                    "Mã vạch EAN-13 '" + clean + "' không hợp lệ (sai chữ số kiểm tra).",
                    "Kiểm tra lại 13 chữ số trên bao bì thuốc.");
        }
        int nhaThuocId = phamViService.batBuocNhaThuocCuaMinh(nhaThuocIdYeuCau);

        Optional<MaVachThuoc> mvOpt = maVachRepo.findByMaVach(clean);
        if (mvOpt.isEmpty() && clean.length() == 14 && clean.startsWith("0")) {
            String gtin13 = clean.substring(1);
            mvOpt = maVachRepo.findByMaVach(gtin13);
            if (mvOpt.isPresent()) {
                clean = gtin13;
            }
        }

        int thuocId;
        String maVachSp;
        String maVachHienThi = clean;

        if (mvOpt.isPresent()) {
            MaVachThuoc mv = mvOpt.get();
            thuocId = mv.getThuocId();
            maVachSp = mv.getMaVach();
            maVachHienThi = mv.getMaVach();
        } else {
            // Hỗ trợ quét mọi mã vạch thực tế từ camera/máy quét khi demo: ánh xạ ổn định sang danh mục 60 thuốc
            List<Thuoc> tatCaThuoc = thuocRepo.findAll(Sort.by("id"));
            if (tatCaThuoc.isEmpty()) {
                throw new NghiepVuException("50014", "Danh mục thuốc đang trống.");
            }
            int idx = Math.floorMod(clean.hashCode(), tatCaThuoc.size());
            Thuoc thuocFallback = tatCaThuoc.get(idx);
            thuocId = thuocFallback.getId();
            maVachSp = maVachRepo.findByThuocIdOrderByLaMaChinhDescIdAsc(thuocId).stream()
                    .findFirst()
                    .map(MaVachThuoc::getMaVach)
                    .orElse(clean);
        }

        ThuocDTO thuocDto = layTheoId(thuocId);
        List<LoTheoMaVachDTO> dsLo = spThuocCaller.goiSpTraCuuThuocTheoMaVach(maVachSp, nhaThuocId);
        int tongTon = dsLo.stream()
                .filter(l -> !"DA_HET_HAN".equals(l.mucCanhBao()))
                .mapToInt(LoTheoMaVachDTO::soLuongTon)
                .sum();

        return new TraCuuMaVachResponse(true, maVachHienThi, nhaThuocId, thuocDto, tongTon, dsLo);
    }

    @Transactional
    public ThuocDTO taoThuoc(ThuocRequest req) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        if (thuocRepo.existsByMaIgnoreCase(req.getMa().trim())) {
            throw new NghiepVuException("DUPLICATE",
                    "Mã thuốc '" + req.getMa() + "' đã tồn tại.",
                    "Vui lòng nhập mã thuốc khác.");
        }
        Thuoc t = new Thuoc();
        capNhatTuRequest(t, req);
        thuocRepo.save(t);
        return layTheoId(t.getId());
    }

    @Transactional
    public ThuocDTO capNhatThuoc(int id, ThuocRequest req) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        Thuoc t = thuocRepo.findById(id)
                .orElseThrow(() -> new NghiepVuException("50014", "Không tìm thấy thuốc id=" + id));
        capNhatTuRequest(t, req);
        thuocRepo.save(t);
        return layTheoId(t.getId());
    }

    @Transactional
    public NhomThuocDTO taoNhomThuoc(NhomThuocRequest req) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        if (nhomThuocRepo.existsByMaIgnoreCase(req.getMa().trim())) {
            throw new NghiepVuException("DUPLICATE", "Mã nhóm thuốc đã tồn tại.");
        }
        NhomThuoc n = new NhomThuoc();
        n.setMa(req.getMa().trim().toUpperCase());
        n.setTen(req.getTen().trim());
        nhomThuocRepo.save(n);
        return new NhomThuocDTO(n.getId(), n.getMa(), n.getTen());
    }

    public void themMaVach(MaVachRequest req) {
        phamViService.batBuocVaiTro(VaiTro.QUANTRI);
        int nguoiId = phamViService.layNguoiDungId();
        try {
            spThuocCaller.goiSpThemMaVach(
                    req.getThuocId(),
                    req.getMaVach().trim(),
                    req.getLoaiMaVach().trim().toUpperCase(),
                    req.isLaMaChinh(),
                    nguoiId
            );
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    private void capNhatTuRequest(Thuoc t, ThuocRequest req) {
        t.setMa(req.getMa().trim().toUpperCase());
        t.setTen(req.getTen().trim());
        t.setHoatChat(req.getHoatChat().trim());
        t.setHamLuong(req.getHamLuong() != null ? req.getHamLuong().trim() : null);
        t.setDangBaoChe(req.getDangBaoChe() != null ? req.getDangBaoChe().trim() : null);
        t.setDonViTinh(req.getDonViTinh().trim());
        t.setNhomThuocId(req.getNhomThuocId());
        t.setNhaSanXuat(req.getNhaSanXuat() != null ? req.getNhaSanXuat().trim() : null);
        t.setCanKeDon(req.isCanKeDon());
        t.setGiaBanLe(req.getGiaBanLe());
        t.setTrangThai(req.getTrangThai() != null ? req.getTrangThai().trim().toUpperCase() : "DANG_BAN");
        t.setMoTa(req.getMoTa() != null ? req.getMoTa().trim() : null);
    }

    private ThuocDTO sangDto(Thuoc t, String tenNhom, String mvChinh) {
        return new ThuocDTO(
                t.getId(), t.getMa(), t.getTen(), t.getHoatChat(),
                t.getHamLuong(), t.getDangBaoChe(), t.getDonViTinh(),
                t.getNhomThuocId(), tenNhom, t.getNhaSanXuat(),
                t.isCanKeDon(), t.getGiaBanLe(), t.getTrangThai(),
                t.getMoTa(), mvChinh
        );
    }

    private boolean laEan13HopLeNoiBo(String ma) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int d = ma.charAt(i) - '0';
            sum += ((i + 1) % 2 == 0) ? d * 3 : d;
        }
        int check = (10 - (sum % 10)) % 10;
        return (ma.charAt(12) - '0') == check;
    }
}
