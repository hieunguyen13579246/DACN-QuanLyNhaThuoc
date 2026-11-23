package com.example.is009.banhang.service;

import com.example.is009.banhang.dto.DongGioHang;
import com.example.is009.banhang.dto.GiaoDichResponse;
import com.example.is009.banhang.dto.GioHangRequest;
import com.example.is009.banhang.dto.HuyGiaoDichRequest;
import com.example.is009.banhang.dto.LoPhanBoDTO;
import com.example.is009.banhang.dto.XemTruocFEFORequest;
import com.example.is009.banhang.repository.SpBanHangCaller;
import com.example.is009.security.PhamViService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BanHangServiceTest {

    @Mock
    private SpBanHangCaller spBanHangCaller;

    @Mock
    private PhamViService phamViService;

    private BanHangService banHangService;

    @BeforeEach
    void setUp() {
        banHangService = new BanHangService(spBanHangCaller, phamViService, new ObjectMapper());
    }

    @Test
    void xemTruocFEFO_packsJsonAndCallsSp() {
        when(phamViService.batBuocNhaThuocCuaMinh(null)).thenReturn(1);
        LoPhanBoDTO lo = new LoPhanBoDTO();
        lo.setThuocId(11);
        lo.setLoThuocId(101);
        lo.setSoLo("LO-A01");
        lo.setSoLuong(2);
        when(spBanHangCaller.xemTruocFEFO(eq(1), anyString())).thenReturn(List.of(lo));

        XemTruocFEFORequest req = new XemTruocFEFORequest();
        req.setChiTiet(List.of(new DongGioHang(11, 2, null)));

        List<LoPhanBoDTO> res = banHangService.xemTruocFEFO(req);
        assertEquals(1, res.size());
        assertEquals("LO-A01", res.get(0).getSoLo());

        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(spBanHangCaller).xemTruocFEFO(eq(1), jsonCaptor.capture());
        String json = jsonCaptor.getValue();
        assertTrue(json.contains("\"thuoc_id\":11"));
        assertTrue(json.contains("\"so_luong\":2"));
    }

    @Test
    void thanhToan_callsSpBanHangAndReturnsDetail() {
        when(phamViService.batBuocNhaThuocCuaMinh(null)).thenReturn(2);
        when(phamViService.layNguoiDungId()).thenReturn(9);
        when(spBanHangCaller.banHang(eq(2), eq(9), eq(5), isNull(), eq("TIEN_MAT"), eq(BigDecimal.ZERO), anyString()))
                .thenReturn(77);

        GiaoDichResponse mockResp = new GiaoDichResponse();
        mockResp.setId(77);
        mockResp.setMaGiaoDich("GD-2026-0077");
        mockResp.setNhaThuocId(2);
        when(spBanHangCaller.layChiTietGiaoDich(77)).thenReturn(Optional.of(mockResp));

        GioHangRequest req = new GioHangRequest();
        req.setKhachHangId(5);
        req.setPhuongThucThanhToan("TIEN_MAT");
        req.setGiamGia(BigDecimal.ZERO);
        req.setChiTiet(List.of(new DongGioHang(12, 3, null)));

        GiaoDichResponse result = banHangService.thanhToan(req);
        assertEquals(77, result.getId());
        assertEquals("GD-2026-0077", result.getMaGiaoDich());
        verify(phamViService).kiemTraQuyenNhaThuoc(2);
    }

    @Test
    void huyGiaoDich_verifiesScopeAndCallsSp() {
        GiaoDichResponse mockResp = new GiaoDichResponse();
        mockResp.setId(88);
        mockResp.setNhaThuocId(3);
        when(spBanHangCaller.layChiTietGiaoDich(88)).thenReturn(Optional.of(mockResp));
        when(phamViService.layNguoiDungId()).thenReturn(4);

        HuyGiaoDichRequest req = new HuyGiaoDichRequest();
        req.setLyDo("Khách đổi ý trả lại ngay tại quầy");

        banHangService.huyGiaoDich(88, req);
        verify(phamViService, times(2)).kiemTraQuyenNhaThuoc(3);
        verify(spBanHangCaller).huyGiaoDich(88, 4, "Khách đổi ý trả lại ngay tại quầy");
    }
}
