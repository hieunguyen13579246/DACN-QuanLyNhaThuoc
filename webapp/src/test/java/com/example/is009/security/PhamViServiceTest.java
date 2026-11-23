package com.example.is009.security;

import com.example.is009.exception.NghiepVuException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhamViServiceTest {

    private final PhamViService phamViService = new PhamViService();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void quanTri_xacDinhPhamViNhaThuoc_choPhepNullHoacChiDinh() {
        CustomUserDetails qt = new CustomUserDetails(
                1, "Nguyen Quan Tri", "qt01@nhathuoc.vn", "hash",
                VaiTro.QUANTRI, null, null, null, true
        );
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(qt, null, qt.getAuthorities())
        );

        assertNull(phamViService.xacDinhPhamViNhaThuoc(null));
        assertEquals(3, phamViService.xacDinhPhamViNhaThuoc(3));
    }

    @Test
    void duocSi_truyCapNhaThuocKhac_biChanMaLoi50011() {
        CustomUserDetails ds = new CustomUserDetails(
                11, "Tran Duoc Si", "ds01@nhathuoc.vn", "hash",
                VaiTro.DUOCSI, 1, "NT001", "NT An Khang Q1", true
        );
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(ds, null, ds.getAuthorities())
        );

        assertEquals(1, phamViService.xacDinhPhamViNhaThuoc(null));
        assertEquals(1, phamViService.xacDinhPhamViNhaThuoc(1));

        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> phamViService.xacDinhPhamViNhaThuoc(2));
        assertEquals("50011", ex.getErrorCode());
    }
}
