package com.example.is009.barcode.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Ean13ValidatorTest {

    @Test
    void ean13HopLe_traVeTrue() {
        assertTrue(Ean13Validator.isValid("8930000000019"));
        assertTrue(Ean13Validator.isValid("8934563123451"));
    }

    @Test
    void ean13SaiCheckDigit_traVeFalse() {
        assertFalse(Ean13Validator.isValid("8930000000010"));
        assertFalse(Ean13Validator.isValid("8930000000018"));
    }

    @Test
    void ean13SaiDoDaiHoacKyTu_traVeFalse() {
        assertFalse(Ean13Validator.isValid(null));
        assertFalse(Ean13Validator.isValid(""));
        assertFalse(Ean13Validator.isValid("893000000001"));
        assertFalse(Ean13Validator.isValid("89300000000199"));
        assertFalse(Ean13Validator.isValid("893000000001A"));
    }

    @Test
    void tinhCheckDigit_chinhXac() {
        assertEquals('9', Ean13Validator.tinhCheckDigit("893000000001"));
        assertThrows(IllegalArgumentException.class, () -> Ean13Validator.tinhCheckDigit("123"));
    }
}
