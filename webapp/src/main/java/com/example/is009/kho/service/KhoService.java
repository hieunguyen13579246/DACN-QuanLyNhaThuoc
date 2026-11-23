package com.example.is009.kho.service;

import com.example.is009.common.ThoiGianVN;
import com.example.is009.exception.ErrorCodeMapper;
import com.example.is009.exception.NghiepVuException;
import com.example.is009.kho.dto.*;
import com.example.is009.kho.entity.DinhMucTon;
import com.example.is009.kho.repository.DinhMucTonRepository;
import com.example.is009.kho.repository.KhoQueryRepository;
import com.example.is009.kho.repository.SpKhoCaller;
import com.example.is009.security.PhamViService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class KhoService {

    private final SpKhoCaller spKhoCaller;
    private final KhoQueryRepository khoQueryRepository;
    private final DinhMucTonRepository dinhMucTonRepository;
    private final PhamViService phamViService;
    private final ObjectMapper objectMapper;

    public KhoService(SpKhoCaller spKhoCaller,
                      KhoQueryRepository khoQueryRepository,
                      DinhMucTonRepository dinhMucTonRepository,
                      PhamViService phamViService,
                      ObjectMapper objectMapper) {
        this.spKhoCaller = spKhoCaller;
        this.khoQueryRepository = khoQueryRepository;
        this.dinhMucTonRepository = dinhMucTonRepository;
        this.phamViService = phamViService;
        this.objectMapper = objectMapper;
    }

    public List<TonKhoThuocDTO> layTonTheoThuoc(Integer nhaThuocIdLoc, Integer nhomThuocId, String tuKhoa) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        return khoQueryRepository.truyVanTonTheoThuoc(phamViId, nhomThuocId, tuKhoa);
    }

    public List<TonKhoLoDTO> layTonTheoLo(Integer nhaThuocIdLoc, Integer thuocId, String tuKhoa, boolean chiLayConTon) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        return khoQueryRepository.truyVanTonTheoLo(phamViId, thuocId, tuKhoa, chiLayConTon);
    }

    public PhieuKhoDTO nhapKho(PhieuNhapRequest req) {
        int nhaThuocId = phamViService.batBuocNhaThuocCuaMinh(req.getNhaThuocId());
        int nguoiLapId = phamViService.layNguoiDungId();
        String json = toJson(req.getChiTiet());
        try {
            Integer phieuId = spKhoCaller.nhapKho(nhaThuocId, nguoiLapId, req.getNhaCungCapId(), req.getGhiChu(), json);
            return layPhieuTheoId(phieuId);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public PhieuKhoDTO taoPhieuDieuChuyen(PhieuDieuChuyenRequest req) {
        int nhaThuocNguonId = phamViService.batBuocNhaThuocCuaMinh(req.getNhaThuocNguonId());
        int nguoiLapId = phamViService.layNguoiDungId();
        String json = toJson(req.getChiTiet());
        try {
            Integer phieuId = spKhoCaller.taoPhieuDieuChuyen(
                    nhaThuocNguonId, req.getNhaThuocDichId(), nguoiLapId, req.getGhiChu(), json);
            return layPhieuTheoId(phieuId);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public PhieuKhoDTO taoPhieuXuatHuy(PhieuXuatHuyRequest req) {
        int nhaThuocId = phamViService.batBuocNhaThuocCuaMinh(req.getNhaThuocId());
        int nguoiLapId = phamViService.layNguoiDungId();
        String json = toJson(req.getChiTiet());
        try {
            Integer phieuId = spKhoCaller.taoPhieuXuatHuy(
                    nhaThuocId, nguoiLapId, req.getLyDo().trim(), req.getGhiChu(), json);
            return layPhieuTheoId(phieuId);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public void duyetPhieuKho(Integer phieuKhoId, DuyetPhieuRequest req) {
        PhieuKhoDTO phieu = layPhieuTheoId(phieuKhoId);
        phamViService.kiemTraQuyenNhaThuoc(phieu.getNhaThuocId());
        int nguoiDuyetId = phamViService.layNguoiDungId();
        String ketQua = req.getKetQua().trim().toUpperCase();
        String lyDo = req.getLyDo() != null ? req.getLyDo().trim() : null;
        try {
            spKhoCaller.duyetPhieuKho(phieuKhoId, nguoiDuyetId, ketQua, lyDo);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public void huyPhieuKho(Integer phieuKhoId) {
        PhieuKhoDTO phieu = layPhieuTheoId(phieuKhoId);
        phamViService.kiemTraQuyenNhaThuoc(phieu.getNhaThuocId());
        int nguoiId = phamViService.layNguoiDungId();
        try {
            spKhoCaller.huyPhieuKho(phieuKhoId, nguoiId);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public List<PhieuKhoDTO> layDanhSachPhieu(Integer nhaThuocIdLoc, String loaiPhieu, String trangThai) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        return khoQueryRepository.truyVanDanhSachPhieu(phamViId, loaiPhieu, trangThai);
    }

    public PhieuKhoDTO layPhieuTheoId(Integer id) {
        PhieuKhoDTO dto = khoQueryRepository.truyVanPhieuTheoId(id)
                .orElseThrow(() -> new NghiepVuException("50011", "Không tìm thấy phiếu kho #" + id));
        if (!phamViService.laQuanTri()) {
            Integer myNtId = phamViService.layNhaThuocIdHienTai();
            boolean hopLe = myNtId != null && (myNtId.equals(dto.getNhaThuocId()) || myNtId.equals(dto.getNhaThuocDichId()));
            if (!hopLe) {
                phamViService.kiemTraQuyenNhaThuoc(dto.getNhaThuocId());
            }
        }
        return dto;
    }

    public List<DinhMucTonDTO> layDinhMucTon(Integer nhaThuocIdLoc) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        return khoQueryRepository.truyVanDinhMucTon(phamViId);
    }

    @Transactional
    public void luuDinhMucTon(DinhMucTonRequest req) {
        int nhaThuocId = phamViService.batBuocNhaThuocCuaMinh(req.getNhaThuocId());
        if (req.getTonToiDa() < req.getTonToiThieu()) {
            throw new NghiepVuException("50011",
                    "Tồn tối đa phải lớn hơn hoặc bằng tồn tối thiểu.",
                    "Vui lòng nhập tồn tối đa >= " + req.getTonToiThieu());
        }
        DinhMucTon dm = dinhMucTonRepository.findByNhaThuocIdAndThuocId(nhaThuocId, req.getThuocId())
                .orElseGet(() -> {
                    DinhMucTon m = new DinhMucTon();
                    m.setNhaThuocId(nhaThuocId);
                    m.setThuocId(req.getThuocId());
                    return m;
                });
        dm.setTonToiThieu(req.getTonToiThieu());
        dm.setTonToiDa(req.getTonToiDa());
        dm.setSoNgayGiaoHang(req.getSoNgayGiaoHang());
        dm.setUpdatedAt(LocalDateTime.ofInstant(ThoiGianVN.sangUtc(ThoiGianVN.bayGio()), ZoneOffset.UTC));
        dinhMucTonRepository.save(dm);
    }

    public List<DoiSoatTonKhoDTO> doiSoatTonKho(Integer nhaThuocIdLoc) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        return spKhoCaller.doiSoatTonKho(phamViId);
    }

    public List<BienDongKhoDTO> layBienDongKho(Integer nhaThuocIdLoc) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        return khoQueryRepository.truyVanBienDongKho(phamViId);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new NghiepVuException("50016", "Không thể đóng gói JSON chi tiết phiếu kho: " + e.getMessage());
        }
    }
}
