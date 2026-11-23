package com.example.is009.donthuoc.service;

import com.example.is009.donthuoc.dto.DonThuocDTO;
import com.example.is009.donthuoc.dto.TaoDonThuocRequest;
import com.example.is009.donthuoc.repository.SpDonThuocCaller;
import com.example.is009.exception.ErrorCodeMapper;
import com.example.is009.exception.NghiepVuException;
import com.example.is009.security.PhamViService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonThuocService {

    private final SpDonThuocCaller spCaller;
    private final PhamViService phamViService;
    private final ObjectMapper objectMapper;

    public DonThuocService(SpDonThuocCaller spCaller,
                           PhamViService phamViService,
                           ObjectMapper objectMapper) {
        this.spCaller = spCaller;
        this.phamViService = phamViService;
        this.objectMapper = objectMapper;
    }

    public List<DonThuocDTO> layDanhSachDon(Integer nhaThuocIdYeuCau, String tuKhoa, String trangThai) {
        Integer phamViNtId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdYeuCau);
        spCaller.goiSpCapNhatDonHetHieuLuc(phamViNtId);
        return spCaller.truyVanDanhSachDon(phamViNtId, tuKhoa, trangThai);
    }

    public List<DonThuocDTO> layDonKhaDungChoPos(Integer nhaThuocIdYeuCau, String tuKhoa) {
        int ntId = phamViService.batBuocNhaThuocCuaMinh(nhaThuocIdYeuCau);
        spCaller.goiSpCapNhatDonHetHieuLuc(ntId);
        return spCaller.truyVanDanhSachDon(ntId, tuKhoa, null).stream()
                .filter(d -> "CHUA_BAN".equals(d.trangThai()) || "BAN_MOT_PHAN".equals(d.trangThai()))
                .toList();
    }

    public DonThuocDTO layTheoId(int id) {
        DonThuocDTO dto = spCaller.truyVanDonTheoId(id)
                .orElseThrow(() -> new NghiepVuException("50014", "Đơn thuốc không tồn tại."));
        phamViService.kiemTraQuyenNhaThuoc(dto.nhaThuocId());
        return dto;
    }

    public DonThuocDTO taoDonThuoc(TaoDonThuocRequest req) {
        int ntId = phamViService.batBuocNhaThuocCuaMinh(req.getNhaThuocId());
        int nguoiId = phamViService.layNguoiDungId();
        String json;
        try {
            json = objectMapper.writeValueAsString(req.getItems());
        } catch (JsonProcessingException e) {
            throw new NghiepVuException("50016", "Không thể đóng gói JSON chi tiết đơn thuốc.");
        }

        try {
            int donId = spCaller.goiSpTaoDonThuoc(
                    req.getKhachHangId(),
                    ntId,
                    req.getTenBacSi().trim(),
                    req.getCoSoKham().trim(),
                    req.getChanDoan() != null ? req.getChanDoan().trim() : null,
                    req.getNgayKeDon(),
                    req.getSoNgayHieuLuc(),
                    nguoiId,
                    json
            );
            return layTheoId(donId);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public void huyDonThuoc(int donThuocId) {
        DonThuocDTO dto = layTheoId(donThuocId);
        phamViService.kiemTraQuyenNhaThuoc(dto.nhaThuocId());
        int nguoiId = phamViService.layNguoiDungId();
        try {
            spCaller.goiSpHuyDonThuoc(donThuocId, nguoiId);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }
}
