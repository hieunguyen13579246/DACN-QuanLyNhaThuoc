package com.example.is009.barcode.service;

import com.example.is009.barcode.dto.Gs1Result;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Gs1ParserTest {

    private final Gs1Parser parser = new Gs1Parser();

    @Test
    void parseChuoiGs1CoDauNgoac_dayDuAI01_17_10_21() {
        String raw = "(01)08930000000019(17)270515(10)LO2025A(21)SER9988";
        Gs1Result res = parser.parse(raw);

        assertTrue(res.isLaGs1());
        assertEquals("08930000000019", res.getGtin());
        assertEquals("8930000000019", res.getEan13());
        assertEquals(LocalDate.of(2027, 5, 15), res.getHanDung());
        assertEquals("LO2025A", res.getSoLo());
        assertEquals("SER9988", res.getSoSerial());
    }

    @Test
    void parseChuoiGs1KhongNgoac_coKyTuPhanCachFNC1() {
        String raw = "]d201089300000000191727020010LOTFNC1\u001D21SN12345";
        Gs1Result res = parser.parse(raw);

        assertTrue(res.isLaGs1());
        assertEquals("08930000000019", res.getGtin());
        assertEquals("8930000000019", res.getEan13());
        // Ngày 00 trong GS1 hiểu là ngày cuối tháng (2027-02-28)
        assertEquals(LocalDate.of(2027, 2, 28), res.getHanDung());
        assertEquals("LOTFNC1", res.getSoLo());
        assertEquals("SN12345", res.getSoSerial());
    }

    @Test
    void parseChuoiRong_traVeFalse() {
        Gs1Result res = parser.parse("   ");
        assertFalse(res.isLaGs1());
        assertNull(res.getGtin());
    }
}
