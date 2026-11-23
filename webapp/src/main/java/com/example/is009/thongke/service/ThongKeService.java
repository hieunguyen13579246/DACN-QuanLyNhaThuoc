package com.example.is009.thongke.service;

import com.example.is009.common.ThoiGianVN;
import com.example.is009.exception.ErrorCodeMapper;
import com.example.is009.security.PhamViService;
import com.example.is009.thongke.dto.*;
import com.example.is009.thongke.repository.ThongKeRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ThongKeService {

    private final ThongKeRepository thongKeRepository;
    private final PhamViService phamViService;

    public ThongKeService(ThongKeRepository thongKeRepository, PhamViService phamViService) {
        this.thongKeRepository = thongKeRepository;
        this.phamViService = phamViService;
    }

    public List<NhuCauKhuVucDTO> layNhuCauKhuVuc(Integer nhaThuocIdLoc, Integer khuVucId,
                                                 Integer nhomThuocId, LocalDate tuNgay, LocalDate denNgay) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        LocalDate[] range = chuanHoaKhoangNgay(tuNgay, denNgay);
        try {
            return thongKeRepository.thongKeNhuCauKhuVuc(phamViId, khuVucId, nhomThuocId, range[0], range[1]);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public List<XuHuongThangDTO> layXuHuongThang(Integer nhaThuocIdLoc, Integer khuVucId,
                                                 Integer nhomThuocId, LocalDate tuNgay, LocalDate denNgay) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        LocalDate[] range = chuanHoaKhoangNgay(tuNgay, denNgay);
        try {
            return thongKeRepository.thongKeXuHuong(phamViId, khuVucId, nhomThuocId, range[0], range[1]);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public List<TopThuocDTO> layTopThuoc(Integer nhaThuocIdLoc, Integer khuVucId,
                                         Integer nhomThuocId, LocalDate tuNgay, LocalDate denNgay, int topN) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        LocalDate[] range = chuanHoaKhoangNgay(tuNgay, denNgay);
        int limit = (topN <= 0 || topN > 50) ? 10 : topN;
        try {
            return thongKeRepository.thongKeTopThuoc(phamViId, khuVucId, nhomThuocId, range[0], range[1], limit);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public List<HieuSuatNhaThuocDTO> layHieuSuatNhaThuoc(Integer nhaThuocIdLoc, Integer khuVucId,
                                                         LocalDate tuNgay, LocalDate denNgay) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        LocalDate[] range = chuanHoaKhoangNgay(tuNgay, denNgay);
        try {
            return thongKeRepository.thongKeHieuSuatNhaThuoc(phamViId, khuVucId, range[0], range[1]);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public List<DeXuatNhapHangDTO> layDeXuatNhapHang(Integer nhaThuocIdLoc, Integer nhomThuocId) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        try {
            return thongKeRepository.deXuatNhapHang(phamViId, nhomThuocId);
        } catch (DataAccessException ex) {
            throw ErrorCodeMapper.tuDataAccessException(ex);
        }
    }

    public DashboardNhaThuocDTO layKpiNhaThuoc(Integer nhaThuocIdLoc) {
        Integer phamViId = phamViService.xacDinhPhamViNhaThuoc(nhaThuocIdLoc);
        return thongKeRepository.truyVanKpiNhaThuoc(phamViId);
    }

    public List<Map<String, Object>> layAuditLog() {
        return thongKeRepository.truyVanAuditLog();
    }

    public LocalDate[] chuanHoaKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
        LocalDate den = denNgay != null ? denNgay : ThoiGianVN.homNay();
        LocalDate tu = tuNgay != null ? tuNgay : den.minusMonths(6);
        if (tu.isAfter(den)) {
            LocalDate tmp = tu;
            tu = den;
            den = tmp;
        }
        return new LocalDate[]{tu, den};
    }
}
