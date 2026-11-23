package com.example.is009.kho.service;

import com.example.is009.exception.NghiepVuException;
import com.example.is009.kho.dto.DongPhieuNhapRequest;
import com.example.is009.kho.dto.DuyetPhieuRequest;
import com.example.is009.kho.dto.PhieuKhoDTO;
import com.example.is009.kho.dto.PhieuNhapRequest;
import com.example.is009.kho.repository.DinhMucTonRepository;
import com.example.is009.kho.repository.KhoQueryRepository;
import com.example.is009.kho.repository.SpKhoCaller;
import com.example.is009.security.PhamViService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KhoServiceTest {

    @Mock
    private SpKhoCaller spKhoCaller;

    @Mock
    private KhoQueryRepository khoQueryRepository;

    @Mock
    private DinhMucTonRepository dinhMucTonRepository;

    @Mock
    private PhamViService phamViService;

    private KhoService khoService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        khoService = new KhoService(spKhoCaller, khoQueryRepository, dinhMucTonRepository, phamViService, objectMapper);
    }

    @Test
    @DisplayName("Nhập kho thành công: đóng gói JSON đúng định dạng và gọi sp_NhapKho")
    void nhapKho_ThanhCong() {
        DongPhieuNhapRequest dong = new DongPhieuNhapRequest();
        dong.setThuocId(10);
        dong.setSoLo("LO-2026-01");
        dong.setNgaySanXuat(LocalDate.of(2026, 1, 1));
        dong.setHanDung(LocalDate.of(2028, 1, 1));
        dong.setSoLuong(200);
        dong.setGiaNhap(new BigDecimal("45000"));

        PhieuNhapRequest req = new PhieuNhapRequest();
        req.setNhaThuocId(1);
        req.setNhaCungCapId(2);
        req.setGhiChu("Nhập lô đầu tháng");
        req.setChiTiet(List.of(dong));

        when(phamViService.batBuocNhaThuocCuaMinh(1)).thenReturn(1);
        when(phamViService.layNguoiDungId()).thenReturn(3);
        when(phamViService.laQuanTri()).thenReturn(true);
        when(spKhoCaller.nhapKho(eq(1), eq(3), eq(2), eq("Nhập lô đầu tháng"), anyString())).thenReturn(55);

        PhieuKhoDTO dtoMock = new PhieuKhoDTO();
        dtoMock.setId(55);
        dtoMock.setMaPhieu("PN-00055");
        dtoMock.setNhaThuocId(1);
        when(khoQueryRepository.truyVanPhieuTheoId(55)).thenReturn(Optional.of(dtoMock));

        PhieuKhoDTO res = khoService.nhapKho(req);
        assertNotNull(res);
        assertEquals(55, res.getId());
        assertEquals("PN-00055", res.getMaPhieu());

        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(spKhoCaller).nhapKho(eq(1), eq(3), eq(2), eq("Nhập lô đầu tháng"), jsonCaptor.capture());
        String json = jsonCaptor.getValue();
        assertTrue(json.contains("\"thuoc_id\":10"));
        assertTrue(json.contains("\"so_lo\":\"LO-2026-01\""));
        assertTrue(json.contains("\"so_luong\":200"));
    }

    @Test
    @DisplayName("Nhập kho thất bại khi hạn dùng ngắn hơn cấu hình tối thiểu (mã lỗi 50004)")
    void nhapKho_Loi50004_HanDungNgan() {
        DongPhieuNhapRequest dong = new DongPhieuNhapRequest();
        dong.setThuocId(10);
        dong.setSoLo("LO-NGAN");
        dong.setHanDung(LocalDate.now().plusDays(15));
        dong.setSoLuong(50);
        dong.setGiaNhap(new BigDecimal("20000"));

        PhieuNhapRequest req = new PhieuNhapRequest();
        req.setNhaThuocId(1);
        req.setNhaCungCapId(2);
        req.setChiTiet(List.of(dong));

        when(phamViService.batBuocNhaThuocCuaMinh(1)).thenReturn(1);
        when(phamViService.layNguoiDungId()).thenReturn(3);

        SQLException sqlEx = new SQLException("50004: Hạn dùng lô nhập quá ngắn", "S0001", 50004);
        when(spKhoCaller.nhapKho(anyInt(), anyInt(), anyInt(), any(), anyString()))
                .thenThrow(new DataIntegrityViolationException("SP Error", sqlEx));

        NghiepVuException ex = assertThrows(NghiepVuException.class, () -> khoService.nhapKho(req));
        assertEquals("50004", ex.getErrorCode());
    }

    @Test
    @DisplayName("Duyệt phiếu kho kiểm tra phạm vi nhà thuốc nguồn trước khi gọi sp_DuyetPhieuKho")
    void duyetPhieuKho_ThanhCong() {
        PhieuKhoDTO phieu = new PhieuKhoDTO();
        phieu.setId(88);
        phieu.setNhaThuocId(1);
        phieu.setTrangThai("CHO_DUYET");

        when(khoQueryRepository.truyVanPhieuTheoId(88)).thenReturn(Optional.of(phieu));
        when(phamViService.laQuanTri()).thenReturn(true);
        when(phamViService.layNguoiDungId()).thenReturn(2);

        DuyetPhieuRequest req = new DuyetPhieuRequest();
        req.setKetQua("DUYET");
        req.setLyDo("Đồng ý điều chuyển");

        khoService.duyetPhieuKho(88, req);

        verify(phamViService).kiemTraQuyenNhaThuoc(1);
        verify(spKhoCaller).duyetPhieuKho(88, 2, "DUYET", "Đồng ý điều chuyển");
    }
}
