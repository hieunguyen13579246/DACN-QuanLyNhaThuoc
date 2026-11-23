package com.example.is009.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SoDienThoaiUtilsTest {

    @Test
    void cheSoDienThoai_10SoChuan() {
        assertEquals("090****567", SoDienThoaiUtils.cheSoDienThoai("0901234567"));
    }

    @Test
    void cheSoDienThoai_rongHoacNull() {
        assertEquals("—", SoDienThoaiUtils.cheSoDienThoai(null));
        assertEquals("—", SoDienThoaiUtils.cheSoDienThoai("   "));
    }
}
