package com.example.is009.banhang.service;

import com.example.is009.banhang.dto.GiaoDichResponse;
import com.example.is009.banhang.dto.GioHangRequest;
import com.example.is009.banhang.dto.HuyGiaoDichRequest;
import com.example.is009.banhang.dto.LoPhanBoDTO;
import com.example.is009.banhang.dto.XemTruocFEFORequest;
import com.example.is009.banhang.repository.SpBanHangCaller;
import com.example.is009.exception.ErrorCodeMapper;
import com.example.is009.exception.NghiepVuException;
import com.example.is009.security.PhamViService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BanHangService {

    private final SpBanHangCaller spBanHangCaller;
    private final PhamViService phamViService;
    private final ObjectMapper objectMapper;

    public BanHangService(SpBanHangCaller spBanHangCaller,
                          PhamViService phamViService,
                          ObjectMapper objectMapper) {
        this.spBanHangCaller = spBanHangCaller;
        this.phamViService = phamViService;
        this.objectMapper = objectMapper;
    }

    public List<LoPhanBoDTO> xemTruocFEFO(XemTruocFEFORequest req) {
        int nhaThuocId = phamViService.batBuocNhaThuocCuaMinh(req.getNhaThuocId());
        String json = toJson(req.getChiTiet());
        try {
            return spBanHangCaller.xemTruocFEFO(nhaThuocId, json);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public GiaoDichResponse thanhToan(GioHangRequest req) {
        int nhaThuocId = phamViService.batBuocNhaThuocCuaMinh(req.getNhaThuocId());
        int nguoiBanId = phamViService.layNguoiDungId();
        String json = toJson(req.getChiTiet());
        BigDecimal giamGia = req.getGiamGia() != null ? req.getGiamGia() : BigDecimal.ZERO;

        try {
            Integer giaoDichId = spBanHangCaller.banHang(
                    nhaThuocId,
                    nguoiBanId,
                    req.getKhachHangId(),
                    req.getDonThuocId(),
                    req.getPhuongThucThanhToan(),
                    giamGia,
                    json
            );
            return layChiTietGiaoDich(giaoDichId);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public void huyGiaoDich(Integer giaoDichId, HuyGiaoDichRequest req) {
        GiaoDichResponse gd = layChiTietGiaoDich(giaoDichId);
        phamViService.kiemTraQuyenNhaThuoc(gd.getNhaThuocId());
        int nguoiHuyId = phamViService.layNguoiDungId();
        try {
            spBanHangCaller.huyGiaoDich(giaoDichId, nguoiHuyId, req.getLyDo().trim());
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public List<GiaoDichResponse> timKiemGiaoDich(Integer nhaThuocIdLoc, LocalDate tuNgay,
                                                  LocalDate denNgay, String trangThai, String tuKhoa) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        return spBanHangCaller.timKiemGiaoDich(phamViId, tuNgay, denNgay, trangThai, tuKhoa);
    }

    public GiaoDichResponse layChiTietGiaoDich(Integer giaoDichId) {
        GiaoDichResponse gd = spBanHangCaller.layChiTietGiaoDich(giaoDichId)
                .orElseThrow(() -> new NghiepVuException("50011", "Không tìm thấy giao dịch #" + giaoDichId));
        phamViService.kiemTraQuyenNhaThuoc(gd.getNhaThuocId());
        return gd;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new NghiepVuException("50016", "Không thể đóng gói JSON giỏ hàng: " + e.getMessage());
        }
    }
}
