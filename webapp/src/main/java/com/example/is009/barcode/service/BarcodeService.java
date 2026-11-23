package com.example.is009.barcode.service;

import com.example.is009.barcode.dto.Gs1Result;
import com.example.is009.exception.NghiepVuException;
import org.springframework.stereotype.Service;

@Service
public class BarcodeService {

    private final Gs1Parser gs1Parser = new Gs1Parser();

    public boolean kiemTraEan13(String maVach) {
        return Ean13Validator.isValid(maVach);
    }

    public void batBuocEan13HopLe(String maVach) {
        if (!Ean13Validator.isValid(maVach)) {
            throw new NghiepVuException("50007",
                    "Mã vạch EAN-13 '" + maVach + "' không hợp lệ (cần đúng 13 chữ số và đúng chữ số kiểm).",
                    "Kiểm tra lại 13 chữ số trên bao bì hoặc dùng nút gợi ý chữ số kiểm.");
        }
    }

    public char tinhCheckDigit(String ean12) {
        return Ean13Validator.tinhCheckDigit(ean12);
    }

    public Gs1Result phanTichGs1(String rawBarcode) {
        return gs1Parser.parse(rawBarcode);
    }

    public String taoMaTemNoiBo(int thuocId, String soLo) {
        String cleanLo = soLo != null ? soLo.replaceAll("[^A-Za-z0-9-]", "") : "LO";
        return String.format("NB-%03d-%s", thuocId, cleanLo);
    }
}
