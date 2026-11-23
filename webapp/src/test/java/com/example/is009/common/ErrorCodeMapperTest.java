package com.example.is009.common;

import com.example.is009.exception.ErrorCodeMapper;
import com.example.is009.exception.NghiepVuException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorCodeMapperTest {

    @Test
    void mapDayDuMaLoi50001Den50018() {
        for (int code = 50001; code <= 50018; code++) {
            String key = String.valueOf(code);
            ErrorCodeMapper.ThongTinLoi info = ErrorCodeMapper.traCuu(key);
            assertNotNull(info.thongBaoMacDinh());
            assertNotNull(info.huongDanSua());
        }
    }

    @Test
    void mapExceptionSangResponseEntity_traVeHuongDanSua() {
        NghiepVuException ex = new NghiepVuException("50004", "Thuốc kê đơn cần đơn thuốc hợp lệ.");
        ResponseEntity<ApiResponse<Void>> res = ErrorCodeMapper.mapException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertNotNull(res.getBody());
        assertFalse(res.getBody().isThanhCong());
        assertEquals("50004", res.getBody().getMaLoi());
        assertNotNull(res.getBody().getHuongDanSua());
    }
}
